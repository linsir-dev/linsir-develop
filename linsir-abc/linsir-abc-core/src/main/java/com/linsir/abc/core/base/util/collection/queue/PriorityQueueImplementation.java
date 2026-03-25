package com.linsir.abc.core.base.util.collection.queue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * 简化版PriorityQueue实现
 *
 * 本类模拟JDK PriorityQueue的核心实现：
 * 1. 基于二叉堆（最小堆）的存储结构
 * 2. 自动排序（按元素的自然顺序或自定义比较器）
 * 3. 支持高效的插入和删除最小元素
 *
 * PriorityQueue特点：
 * - 基于二叉堆实现
 * - 元素自动排序
 * - 不允许null元素
 * - 插入时间复杂度O(log n)，查看最小元素O(1)
 * - 非线程安全
 * - 无界队列（自动扩容）
 *
 * @param <E> 元素类型
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class PriorityQueueImplementation<E> {

    /**
     * 默认初始容量
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 11;

    /**
     * 存储元素的数组（二叉堆）
     */
    private Object[] queue;

    /**
     * 元素数量
     */
    private int size = 0;

    /**
     * 比较器
     */
    private final Comparator<? super E> comparator;

    /**
     * 修改次数（用于快速失败）
     */
    private int modCount = 0;

    /**
     * 最大数组大小
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * 默认构造方法（使用元素的自然顺序）
     */
    public PriorityQueueImplementation() {
        this(DEFAULT_INITIAL_CAPACITY, null);
    }

    /**
     * 指定初始容量的构造方法
     *
     * @param initialCapacity 初始容量
     */
    public PriorityQueueImplementation(int initialCapacity) {
        this(initialCapacity, null);
    }

    /**
     * 指定比较器的构造方法
     *
     * @param comparator 比较器
     */
    public PriorityQueueImplementation(Comparator<? super E> comparator) {
        this(DEFAULT_INITIAL_CAPACITY, comparator);
    }

    /**
     * 完整构造方法
     *
     * @param initialCapacity 初始容量
     * @param comparator 比较器
     */
    public PriorityQueueImplementation(int initialCapacity, Comparator<? super E> comparator) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException();
        }
        this.queue = new Object[initialCapacity];
        this.comparator = comparator;
    }

    /**
     * 获取元素数量
     *
     * @return 元素数量
     */
    public int size() {
        return size;
    }

    /**
     * 判断是否为空
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 添加元素
     *
     * @param e 元素
     * @return 是否添加成功
     */
    public boolean add(E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        modCount++;
        int i = size;
        if (i >= queue.length) {
            grow(i + 1);
        }
        size = i + 1;
        if (i == 0) {
            queue[0] = e;
        } else {
            siftUp(i, e);
        }
        return true;
    }

    /**
     * 插入元素（与add相同）
     *
     * @param e 元素
     * @return 是否添加成功
     */
    public boolean offer(E e) {
        return add(e);
    }

    /**
     * 查看队首元素（不移除）
     *
     * @return 队首元素，如果为空返回null
     */
    @SuppressWarnings("unchecked")
    public E peek() {
        return (size == 0) ? null : (E) queue[0];
    }

    /**
     * 获取并移除队首元素
     *
     * @return 队首元素，如果为空返回null
     */
    @SuppressWarnings("unchecked")
    public E poll() {
        if (size == 0) {
            return null;
        }
        int s = --size;
        modCount++;
        E result = (E) queue[0];
        E x = (E) queue[s];
        queue[s] = null;
        if (s != 0) {
            siftDown(0, x);
        }
        return result;
    }

    /**
     * 获取并移除队首元素（空队列抛出异常）
     *
     * @return 队首元素
     * @throws NoSuchElementException 如果队列为空
     */
    public E remove() {
        E x = poll();
        if (x != null) {
            return x;
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * 查看队首元素（空队列抛出异常）
     *
     * @return 队首元素
     * @throws NoSuchElementException 如果队列为空
     */
    public E element() {
        E x = peek();
        if (x != null) {
            return x;
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * 向上调整（插入时使用）
     *
     * @param k 插入位置
     * @param x 插入的元素
     */
    private void siftUp(int k, E x) {
        if (comparator != null) {
            siftUpUsingComparator(k, x);
        } else {
            siftUpComparable(k, x);
        }
    }

    /**
     * 使用Comparable向上调整
     */
    @SuppressWarnings("unchecked")
    private void siftUpComparable(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>) x;
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            if (key.compareTo((E) e) >= 0) {
                break;
            }
            queue[k] = e;
            k = parent;
        }
        queue[k] = key;
    }

    /**
     * 使用Comparator向上调整
     */
    @SuppressWarnings("unchecked")
    private void siftUpUsingComparator(int k, E x) {
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            if (comparator.compare(x, (E) e) >= 0) {
                break;
            }
            queue[k] = e;
            k = parent;
        }
        queue[k] = x;
    }

    /**
     * 向下调整（删除时使用）
     *
     * @param k 起始位置
     * @param x 要放置的元素
     */
    private void siftDown(int k, E x) {
        if (comparator != null) {
            siftDownUsingComparator(k, x);
        } else {
            siftDownComparable(k, x);
        }
    }

    /**
     * 使用Comparable向下调整
     */
    @SuppressWarnings("unchecked")
    private void siftDownComparable(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>) x;
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = queue[child];
            int right = child + 1;
            if (right < size &&
                ((Comparable<? super E>) c).compareTo((E) queue[right]) > 0) {
                c = queue[child = right];
            }
            if (key.compareTo((E) c) <= 0) {
                break;
            }
            queue[k] = c;
            k = child;
        }
        queue[k] = key;
    }

    /**
     * 使用Comparator向下调整
     */
    @SuppressWarnings("unchecked")
    private void siftDownUsingComparator(int k, E x) {
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = queue[child];
            int right = child + 1;
            if (right < size &&
                comparator.compare((E) c, (E) queue[right]) > 0) {
                c = queue[child = right];
            }
            if (comparator.compare(x, (E) c) <= 0) {
                break;
            }
            queue[k] = c;
            k = child;
        }
        queue[k] = x;
    }

    /**
     * 扩容
     *
     * @param minCapacity 最小容量
     */
    private void grow(int minCapacity) {
        int oldCapacity = queue.length;
        // 扩容为原来的1.5倍或根据需要
        int newCapacity = oldCapacity + ((oldCapacity < 64) ?
                                         (oldCapacity + 2) :
                                         (oldCapacity >> 1));
        // 溢出检查
        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            newCapacity = hugeCapacity(minCapacity);
        }
        queue = Arrays.copyOf(queue, newCapacity);
    }

    /**
     * 处理超大容量
     */
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) {
            throw new OutOfMemoryError();
        }
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }

    /**
     * 清空队列
     */
    public void clear() {
        modCount++;
        for (int i = 0; i < size; i++) {
            queue[i] = null;
        }
        size = 0;
    }

    /**
     * 判断是否包含元素
     *
     * @param o 元素
     * @return 是否包含
     */
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    /**
     * 查找元素索引
     *
     * @param o 元素
     * @return 索引，不存在返回-1
     */
    private int indexOf(Object o) {
        if (o != null) {
            for (int i = 0; i < size; i++) {
                if (o.equals(queue[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 转换为数组
     *
     * @return 数组
     */
    public Object[] toArray() {
        return Arrays.copyOf(queue, size);
    }

    /**
     * 演示PriorityQueue的使用
     */
    public static void demonstrateUsage() {
        System.out.println("========== PriorityQueue演示 ==========");

        PriorityQueueImplementation<Integer> queue = new PriorityQueueImplementation<>();

        // 添加元素（乱序）
        queue.add(30);
        queue.add(10);
        queue.add(50);
        queue.add(20);
        queue.add(40);

        System.out.println("添加元素后大小: " + queue.size());
        System.out.println("队首元素: " + queue.peek());

        // 按优先级取出元素
        System.out.println("按优先级取出元素:");
        while (!queue.isEmpty()) {
            System.out.println("  " + queue.poll());
        }

        System.out.println();
    }

    /**
     * 演示自定义比较器（最大堆）
     */
    public static void demonstrateMaxHeap() {
        System.out.println("========== 最大堆演示 ==========");

        // 使用自定义比较器实现最大堆
        PriorityQueueImplementation<Integer> maxHeap = new PriorityQueueImplementation<>(
            (a, b) -> b - a
        );

        maxHeap.add(30);
        maxHeap.add(10);
        maxHeap.add(50);
        maxHeap.add(20);
        maxHeap.add(40);

        System.out.println("最大堆队首元素: " + maxHeap.peek());

        System.out.println("按优先级取出元素:");
        while (!maxHeap.isEmpty()) {
            System.out.println("  " + maxHeap.poll());
        }

        System.out.println();
    }

    /**
     * 演示任务调度场景
     */
    public static void demonstrateTaskScheduling() {
        System.out.println("========== 任务调度演示 ==========");

        // 任务类
        class Task implements Comparable<Task> {
            String name;
            int priority;

            Task(String name, int priority) {
                this.name = name;
                this.priority = priority;
            }

            @Override
            public int compareTo(Task other) {
                return this.priority - other.priority;
            }

            @Override
            public String toString() {
                return name + "(优先级:" + priority + ")";
            }
        }

        PriorityQueueImplementation<Task> taskQueue = new PriorityQueueImplementation<>();

        // 添加任务
        taskQueue.add(new Task("普通任务1", 5));
        taskQueue.add(new Task("紧急任务", 1));
        taskQueue.add(new Task("普通任务2", 5));
        taskQueue.add(new Task("重要任务", 2));
        taskQueue.add(new Task("低优先级任务", 10));

        System.out.println("按优先级处理任务:");
        while (!taskQueue.isEmpty()) {
            System.out.println("  处理: " + taskQueue.poll());
        }

        System.out.println();
    }

    /**
     * 主方法：运行所有演示
     */
    public static void main(String[] args) {
        demonstrateUsage();
        demonstrateMaxHeap();
        demonstrateTaskScheduling();
    }
}
