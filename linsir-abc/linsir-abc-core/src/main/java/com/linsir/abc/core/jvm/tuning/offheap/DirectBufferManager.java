package com.linsir.abc.core.jvm.tuning.offheap;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * 堆外内存管理器
 * 用于管理DirectByteBuffer的分配和释放，防止堆外内存泄漏
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class DirectBufferManager {

    private static final Logger LOGGER = Logger.getLogger(DirectBufferManager.class.getName());

    /**
     * 默认最大堆外内存（字节）
     */
    private static final long DEFAULT_MAX_DIRECT_MEMORY = 1024 * 1024 * 1024; // 1GB

    /**
     * 缓冲区分配计数器
     */
    private final AtomicLong allocatedSize;

    /**
     * 已分配的缓冲区映射
     */
    private final ConcurrentMap<String, ByteBuffer> allocatedBuffers;

    /**
     * 最大堆外内存限制
     */
    private final long maxDirectMemory;

    /**
     * 是否启用自动清理
     */
    private final boolean autoCleanup;

    public DirectBufferManager() {
        this(DEFAULT_MAX_DIRECT_MEMORY, true);
    }

    public DirectBufferManager(long maxDirectMemory, boolean autoCleanup) {
        this.maxDirectMemory = maxDirectMemory;
        this.autoCleanup = autoCleanup;
        this.allocatedSize = new AtomicLong(0);
        this.allocatedBuffers = new ConcurrentHashMap<>();
    }

    /**
     * 分配直接缓冲区
     *
     * @param size 缓冲区大小
     * @return ByteBuffer实例
     * @throws OutOfMemoryError 当超过最大堆外内存限制时
     */
    public ByteBuffer allocate(int size) {
        return allocate(generateBufferId(), size);
    }

    /**
     * 分配直接缓冲区（带ID）
     *
     * @param bufferId 缓冲区ID
     * @param size     缓冲区大小
     * @return ByteBuffer实例
     * @throws OutOfMemoryError 当超过最大堆外内存限制时
     */
    public ByteBuffer allocate(String bufferId, int size) {
        // 检查内存限制
        long currentSize = allocatedSize.get();
        if (currentSize + size > maxDirectMemory) {
            throw new OutOfMemoryError(
                    "Direct buffer memory limit exceeded: " + currentSize + " + " + size +
                            " > " + maxDirectMemory);
        }

        ByteBuffer buffer = ByteBuffer.allocateDirect(size);
        allocatedBuffers.put(bufferId, buffer);
        allocatedSize.addAndGet(size);

        LOGGER.fine("Allocated direct buffer: id=" + bufferId + ", size=" + size +
                ", totalAllocated=" + allocatedSize.get());

        return buffer;
    }

    /**
     * 释放直接缓冲区
     *
     * @param bufferId 缓冲区ID
     * @return 是否成功释放
     */
    public boolean release(String bufferId) {
        ByteBuffer buffer = allocatedBuffers.remove(bufferId);
        if (buffer != null) {
            int size = buffer.capacity();
            cleanDirectBuffer(buffer);
            allocatedSize.addAndGet(-size);
            LOGGER.fine("Released direct buffer: id=" + bufferId + ", size=" + size);
            return true;
        }
        return false;
    }

    /**
     * 释放直接缓冲区
     *
     * @param buffer ByteBuffer实例
     * @return 是否成功释放
     */
    public boolean release(ByteBuffer buffer) {
        if (buffer == null) {
            return false;
        }

        // 从映射中移除
        String keyToRemove = null;
        for (ConcurrentMap.Entry<String, ByteBuffer> entry : allocatedBuffers.entrySet()) {
            if (entry.getValue() == buffer) {
                keyToRemove = entry.getKey();
                break;
            }
        }

        if (keyToRemove != null) {
            allocatedBuffers.remove(keyToRemove);
        }

        int size = buffer.capacity();
        cleanDirectBuffer(buffer);
        allocatedSize.addAndGet(-size);
        LOGGER.fine("Released direct buffer: size=" + size);
        return true;
    }

    /**
     * 清理直接缓冲区
     * 使用反射调用Cleaner.clean()方法释放堆外内存
     *
     * @param buffer ByteBuffer实例
     */
    private void cleanDirectBuffer(ByteBuffer buffer) {
        if (buffer == null || !buffer.isDirect()) {
            return;
        }

        try {
            // 方式1：尝试使用JDK 9+的Unsafe.invokeCleaner
            cleanWithUnsafe(buffer);
        } catch (Exception e) {
            LOGGER.warning("Failed to clean direct buffer: " + e.getMessage());
        }
    }

    /**
     * 使用Unsafe清理直接缓冲区
     * 兼容JDK 9+
     *
     * @param buffer ByteBuffer实例
     */
    private void cleanWithUnsafe(ByteBuffer buffer) {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Object unsafe = unsafeField.get(null);

            // JDK 9+使用invokeCleaner方法
            try {
                Method invokeCleaner = unsafeClass.getMethod("invokeCleaner", ByteBuffer.class);
                invokeCleaner.invoke(unsafe, buffer);
            } catch (NoSuchMethodException e) {
                // JDK 8使用cleaner
                cleanWithCleaner(buffer);
            }
        } catch (Exception e) {
            LOGGER.fine("Failed to clean with Unsafe: " + e.getMessage());
        }
    }

    /**
     * 使用Cleaner清理（JDK 8兼容）
     *
     * @param buffer ByteBuffer实例
     */
    private void cleanWithCleaner(ByteBuffer buffer) {
        try {
            // 获取DirectByteBuffer的cleaner字段
            Field cleanerField = buffer.getClass().getDeclaredField("cleaner");
            cleanerField.setAccessible(true);
            Object cleaner = cleanerField.get(buffer);

            if (cleaner != null) {
                Method cleanMethod = cleaner.getClass().getMethod("clean");
                cleanMethod.invoke(cleaner);
            }
        } catch (Exception e) {
            LOGGER.fine("Failed to clean with Cleaner: " + e.getMessage());
        }
    }

    /**
     * 获取当前已分配的堆外内存大小
     *
     * @return 已分配内存大小（字节）
     */
    public long getAllocatedSize() {
        return allocatedSize.get();
    }

    /**
     * 获取已分配的缓冲区数量
     *
     * @return 缓冲区数量
     */
    public int getBufferCount() {
        return allocatedBuffers.size();
    }

    /**
     * 获取最大堆外内存限制
     *
     * @return 最大内存限制（字节）
     */
    public long getMaxDirectMemory() {
        return maxDirectMemory;
    }

    /**
     * 获取剩余可用堆外内存
     *
     * @return 剩余内存（字节）
     */
    public long getRemainingMemory() {
        return maxDirectMemory - allocatedSize.get();
    }

    /**
     * 生成缓冲区ID
     *
     * @return 唯一ID
     */
    private String generateBufferId() {
        return "buffer-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();
    }

    /**
     * 关闭管理器，释放所有缓冲区
     */
    public void shutdown() {
        LOGGER.info("Shutting down DirectBufferManager, releasing all buffers...");
        for (String bufferId : allocatedBuffers.keySet()) {
            release(bufferId);
        }
        allocatedBuffers.clear();
        allocatedSize.set(0);
    }

    /**
     * 获取统计信息
     *
     * @return 统计信息
     */
    public String getStatistics() {
        return String.format(
                "DirectBufferManager[allocated=%d bytes, count=%d, max=%d bytes, remaining=%d bytes]",
                getAllocatedSize(),
                getBufferCount(),
                maxDirectMemory,
                getRemainingMemory()
        );
    }
}
