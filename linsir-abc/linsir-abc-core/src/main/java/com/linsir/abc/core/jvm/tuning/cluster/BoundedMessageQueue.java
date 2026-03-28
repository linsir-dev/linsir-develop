package com.linsir.abc.core.jvm.tuning.cluster;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * 有界消息队列
 * 用于限制接收队列大小，防止内存溢出
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class BoundedMessageQueue {

    private static final Logger LOGGER = Logger.getLogger(BoundedMessageQueue.class.getName());

    /**
     * 默认队列最大容量
     */
    private static final int DEFAULT_MAX_CAPACITY = 10000;

    /**
     * 默认消息处理超时时间（毫秒）
     */
    private static final long DEFAULT_TIMEOUT_MILLIS = 30000;

    /**
     * 消息队列
     */
    private final BlockingQueue<ClusterMessage> messageQueue;

    /**
     * 队列最大容量
     */
    private final int maxCapacity;

    /**
     * 丢弃消息计数器
     */
    private final AtomicLong droppedMessageCount;

    /**
     * 接收消息计数器
     */
    private final AtomicLong receivedMessageCount;

    /**
     * 队列名称
     */
    private final String queueName;

    public BoundedMessageQueue(String queueName) {
        this(queueName, DEFAULT_MAX_CAPACITY);
    }

    public BoundedMessageQueue(String queueName, int maxCapacity) {
        this.queueName = queueName;
        this.maxCapacity = maxCapacity;
        this.messageQueue = new ArrayBlockingQueue<>(maxCapacity);
        this.droppedMessageCount = new AtomicLong(0);
        this.receivedMessageCount = new AtomicLong(0);
    }

    /**
     * 接收消息
     * 当队列满时，丢弃消息并记录日志
     *
     * @param message 集群消息
     * @return 是否成功接收
     */
    public boolean receive(ClusterMessage message) {
        if (message == null) {
            return false;
        }

        // 检查消息是否已超时
        if (message.isExpired()) {
            LOGGER.warning("Message expired, dropping: " + message.getMessageId());
            droppedMessageCount.incrementAndGet();
            return false;
        }

        // 检查队列是否已满
        if (messageQueue.size() >= maxCapacity) {
            LOGGER.warning("Message queue full (" + maxCapacity + "), dropping message: " + message.getMessageId());
            droppedMessageCount.incrementAndGet();
            return false;
        }

        boolean offered = messageQueue.offer(message);
        if (offered) {
            receivedMessageCount.incrementAndGet();
            LOGGER.fine("Message received: " + message.getMessageId());
        } else {
            droppedMessageCount.incrementAndGet();
            LOGGER.warning("Failed to offer message to queue: " + message.getMessageId());
        }

        return offered;
    }

    /**
     * 接收消息（带超时）
     *
     * @param message 集群消息
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return 是否成功接收
     */
    public boolean receive(ClusterMessage message, long timeout, TimeUnit unit) {
        if (message == null) {
            return false;
        }

        try {
            if (messageQueue.size() >= maxCapacity) {
                LOGGER.warning("Message queue full, dropping message: " + message.getMessageId());
                droppedMessageCount.incrementAndGet();
                return false;
            }

            boolean offered = messageQueue.offer(message, timeout, unit);
            if (offered) {
                receivedMessageCount.incrementAndGet();
            } else {
                droppedMessageCount.incrementAndGet();
                LOGGER.warning("Message offer timeout, dropping: " + message.getMessageId());
            }
            return offered;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            droppedMessageCount.incrementAndGet();
            LOGGER.warning("Message receive interrupted: " + message.getMessageId());
            return false;
        }
    }

    /**
     * 取出消息进行处理
     *
     * @return 消息，如果队列为空则返回null
     */
    public ClusterMessage poll() {
        return messageQueue.poll();
    }

    /**
     * 取出消息（阻塞）
     *
     * @return 消息
     * @throws InterruptedException 中断异常
     */
    public ClusterMessage take() throws InterruptedException {
        return messageQueue.take();
    }

    /**
     * 获取当前队列大小
     *
     * @return 队列大小
     */
    public int size() {
        return messageQueue.size();
    }

    /**
     * 获取队列剩余容量
     *
     * @return 剩余容量
     */
    public int remainingCapacity() {
        return messageQueue.remainingCapacity();
    }

    /**
     * 获取丢弃消息数量
     *
     * @return 丢弃消息数
     */
    public long getDroppedMessageCount() {
        return droppedMessageCount.get();
    }

    /**
     * 获取接收消息数量
     *
     * @return 接收消息数
     */
    public long getReceivedMessageCount() {
        return receivedMessageCount.get();
    }

    /**
     * 清空队列
     */
    public void clear() {
        messageQueue.clear();
        LOGGER.info("Message queue cleared");
    }

    /**
     * 获取队列统计信息
     *
     * @return 统计信息
     */
    public String getStatistics() {
        return String.format(
                "Queue[%s]: size=%d, maxCapacity=%d, received=%d, dropped=%d",
                queueName,
                size(),
                maxCapacity,
                receivedMessageCount.get(),
                droppedMessageCount.get()
        );
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public String getQueueName() {
        return queueName;
    }
}
