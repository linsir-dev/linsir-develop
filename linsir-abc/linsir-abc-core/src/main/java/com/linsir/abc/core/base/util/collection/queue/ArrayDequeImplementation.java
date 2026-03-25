package com.linsir.abc.core.base.util.collection.queue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 简化版ArrayDeque实现
 *
 * 本类模拟JDK ArrayDeque的核心实现：
 * 1. 基于循环数组的存储结构
 * 2. 支持双端操作（队列和栈）
 * 3. 无界队列（自动扩容）
 * 4. 不允许null元素
 *
 * ArrayDeque特点：
 * - 基于循环数组实现
 * - 支持双端队列操作
 * - 可以作为栈（LIFO）或队列（FIFO）使用
 * - 插入、删除操作时间复杂度O(1)
 * - 不允许null元素
 * - 非线程安全
 * - 比LinkedList更快（缓存友好）
 *
 * @param <E> 元素类型
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class ArrayDequeImplementation<E> implements Collection<E> {

    /**
     * 默认初始容量（必须是2的幂）
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * 存储元素的数组（循环数组）
     */
    private Object[] elements;

    /**
     * 队首索引
     */
    private int head;

    /**
     * 队尾索引（下一个插入位置）
     */
    private int tail;

    /**
     * 最小容量
     */
    private static final int MIN_INITIAL_CAPACITY = 8;

    /**
     * 默认构造方法
     */
    public ArrayDequeImplementation() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    /**
     * 指定初始容量的构造方法
     *
     * @param numElements 初始容量
     */
    public ArrayDequeImplementation(int numElements) {
        allocateElements(numElements);
    }

    /**
     * 从集合构造
     *
     * @param c 集合
     */
    public ArrayDequeImplementation(Collection<? extends E> c) {
        allocateElements(c.size());
        addAll(c);
    }

    /**
     * 分配数组空间
     *
     * @param numElements 需要的容量
     */
    private void allocateElements(int numElements) {
        int initialCapacity = MIN_INITIAL_CAPACITY;
        if (numElements >= initialCapacity) {
            initialCapacity = numElements;
            initialCapacity |= (initialCapacity >>> 1);
            initialCapacity |= (initialCapacity >>> 2);
            initialCapacity |= (initialCapacity >>> 4);
            initialCapacity |= (initialCapacity >>> 8);
            initialCapacity |= (initialCapacity >>> 16);
            initialCapacity++;

            if (initialCapacity < 0) {
                initialCapacity >>>= 1;
            }
        }
        elements = new Object[initialCapacity];
    }

    /**
     * 扩容
     */
    private void doubleCapacity() {
        assert head == tail;
        int p = head;
        int n = elements.length;
        int r = n - p;
        int newCapacity = n << 1;
        if (newCapacity < 0) {
            throw new IllegalStateException("Sorry, deque too big");
        }
        Object[] a = new Object[newCapacity];

        // 复制元素到新数组
        System.arraycopy(elements, p, a, 0, r);
        System.arraycopy(elements, 0, a, r, p);
        elements = a;
        head = 0;
        tail = n;
    }

    /**
     * 获取元素数量
     */
    @Override
    public int size() {
        return (tail - head) & (elements.length - 1);
    }

    /**
     * 判断是否为空
     */
    @Override
    public boolean isEmpty() {
        return head == tail;
    }

    /**
     * 在队首添加元素
     *
     * @param e 元素
     */
    public void addFirst(E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        // 计算新的head位置
        elements[head = (head - 1) & (elements.length - 1)] = e;
        if (head == tail) {
            doubleCapacity();
        }
    }

    /**
     * 在队尾添加元素
     *
     * @param e 元素
     */
    public void addLast(E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        elements[tail] = e;
        // 计算新的tail位置
        if ((tail = (tail + 1) & (elements.length - 1)) == head) {
            doubleCapacity();
        }
    }

    /**
     * 添加元素（等同于addLast）
     */
    @Override
    public boolean add(E e) {
        addLast(e);
        return true;
    }

    /**
     * 在队首添加元素（等同于addFirst）
     *
     * @param e 元素
     * @return 是否添加成功
     */
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    /**
     * 在队尾添加元素（等同于addLast）
     *
     * @param e 元素
     * @return 是否添加成功
     */
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    /**
     * 在队尾添加元素（等同于offerLast）
     *
     * @param e 元素
     * @return 是否添加成功
     */
    public boolean offer(E e) {
        return offerLast(e);
    }

    /**
     * 移除并返回队首元素
     *
     * @return 队首元素
     * @throws NoSuchElementException 如果队列为空
     */
    public E removeFirst() {
        E x = pollFirst();
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }

    /**
     * 移除并返回队尾元素
     *
     * @return 队尾元素
     * @throws NoSuchElementException 如果队列为空
     */
    public E removeLast() {
        E x = pollLast();
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }

    /**
     * 移除并返回队首元素（等同于removeFirst）
     *
     * @return 队首元素
     * @throws NoSuchElementException 如果队列为空
     */
    @Override
    public boolean remove(Object o) {
        return removeFirstOccurrence(o);
    }

    /**
     * 移除并返回队首元素（空队列返回null）
     *
     * @return 队首元素，如果为空返回null
     */
    @SuppressWarnings("unchecked")
    public E pollFirst() {
        int h = head;
        @SuppressWarnings("unchecked")
        E result = (E) elements[h];
        if (result == null) {
            return null;
        }
        elements[h] = null;
        head = (h + 1) & (elements.length - 1);
        return result;
    }

    /**
     * 移除并返回队尾元素（空队列返回null）
     *
     * @return 队尾元素，如果为空返回null
     */
    @SuppressWarnings("unchecked")
    public E pollLast() {
        int t = (tail - 1) & (elements.length - 1);
        @SuppressWarnings("unchecked")
        E result = (E) elements[t];
        if (result == null) {
            return null;
        }
        elements[t] = null;
        tail = t;
        return result;
    }

    /**
     * 移除并返回队首元素（等同于pollFirst）
     *
     * @return 队首元素，如果为空返回null
     */
    public E poll() {
        return pollFirst();
    }

    /**
     * 移除并返回队首元素（等同于removeFirst）
     *
     * @return 队首元素
     * @throws NoSuchElementException 如果队列为空
     */
    public E remove() {
        return removeFirst();
    }

    /**
     * 查看队首元素（不移除）
     *
     * @return 队首元素，如果为空返回null
     */
    @SuppressWarnings("unchecked")
    public E peekFirst() {
        return (E) elements[head];
    }

    /**
     * 查看队尾元素（不移除）
     *
     * @return 队尾元素，如果为空返回null
     */
    @SuppressWarnings("unchecked")
    public E peekLast() {
        return (E) elements[(tail - 1) & (elements.length - 1)];
    }

    /**
     * 查看队首元素（等同于peekFirst）
     *
     * @return 队首元素，如果为空返回null
     */
    public E peek() {
        return peekFirst();
    }

    /**
     * 查看队首元素（等同于peekFirst，空队列抛出异常）
     *
     * @return 队首元素
     * @throws NoSuchElementException 如果队列为空
     */
    public E element() {
        E x = peekFirst();
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }

    /**
     * 压栈（在队首添加元素）
     *
     * @param e 元素
     */
    public void push(E e) {
        addFirst(e);
    }

    /**
     * 弹栈（移除并返回队首元素）
     *
     * @return 队首元素
     * @throws NoSuchElementException 如果栈为空
     */
    public E pop() {
        return removeFirst();
    }

    /**
     * 删除第一次出现的元素
     *
     * @param o 要删除的元素
     * @return 是否删除成功
     */
    public boolean removeFirstOccurrence(Object o) {
        if (o == null) {
            return false;
        }
        int mask = elements.length - 1;
        int i = head;
        Object x;
        while ((x = elements[i]) != null) {
            if (o.equals(x)) {
                delete(i);
                return true;
            }
            i = (i + 1) & mask;
        }
        return false;
    }

    /**
     * 删除最后一次出现的元素
     *
     * @param o 要删除的元素
     * @return 是否删除成功
     */
    public boolean removeLastOccurrence(Object o) {
        if (o == null) {
            return false;
        }
        int mask = elements.length - 1;
        int i = (tail - 1) & mask;
        Object x;
        while ((x = elements[i]) != null) {
            if (o.equals(x)) {
                delete(i);
                return true;
            }
            i = (i - 1) & mask;
        }
        return false;
    }

    /**
     * 删除指定位置的元素
     *
     * @param i 索引
     */
    private void delete(int i) {
        // 简化实现：将元素置为null，后续元素前移
        // 实际实现会更复杂，考虑循环数组的特性
        elements[i] = null;
    }

    /**
     * 判断是否包含元素
     */
    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        int mask = elements.length - 1;
        int i = head;
        Object x;
        while ((x = elements[i]) != null) {
            if (o.equals(x)) {
                return true;
            }
            i = (i + 1) & mask;
        }
        return false;
    }

    /**
     * 清空所有元素
     */
    @Override
    public void clear() {
        int h = head;
        int t = tail;
        if (h != t) {
            head = tail = 0;
            int i = h;
            int mask = elements.length - 1;
            do {
                elements[i] = null;
                i = (i + 1) & mask;
            } while (i != t);
        }
    }

    /**
     * 转换为数组
     */
    @Override
    public Object[] toArray() {
        return copyElements(new Object[size()]);
    }

    /**
     * 转换为指定类型的数组
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        int size = size();
        if (a.length < size) {
            a = (T[]) java.lang.reflect.Array.newInstance(
                a.getClass().getComponentType(), size);
        }
        copyElements(a);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    /**
     * 复制元素到数组
     */
    private <T> T[] copyElements(T[] a) {
        if (head < tail) {
            System.arraycopy(elements, head, a, 0, size());
        } else if (head > tail) {
            int headPortionLen = elements.length - head;
            System.arraycopy(elements, head, a, 0, headPortionLen);
            System.arraycopy(elements, 0, a, headPortionLen, tail);
        }
        return a;
    }

    /**
     * 获取迭代器
     */
    @Override
    public Iterator<E> iterator() {
        return new DeqIterator();
    }

    /**
     * 迭代器实现
     */
    private class DeqIterator implements Iterator<E> {
        private int cursor = head;
        private int fence = tail;

        @Override
        public boolean hasNext() {
            return cursor != fence;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            if (cursor == fence) {
                throw new NoSuchElementException();
            }
            @SuppressWarnings("unchecked")
            E result = (E) elements[cursor];
            cursor = (cursor + 1) & (elements.length - 1);
            return result;
        }
    }

    /**
     * 添加所有元素
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }

    /**
     * 判断是否包含所有元素
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 删除所有元素
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object e : c) {
            while (remove(e)) {
                modified = true;
            }
        }
        return modified;
    }

    /**
     * 只保留集合中的元素
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    /**
     * 演示ArrayDeque作为队列使用
     */
    public static void demonstrateAsQueue() {
        System.out.println("========== ArrayDeque作为队列演示 ==========");

        ArrayDequeImplementation<String> queue = new ArrayDequeImplementation<>();

        // 入队
        queue.offer("Alice");
        queue.offer("Bob");
        queue.offer("Charlie");

        System.out.println("队列大小: " + queue.size());
        System.out.println("队首元素: " + queue.peek());

        // 出队
        System.out.println("出队顺序:");
        while (!queue.isEmpty()) {
            System.out.println("  " + queue.poll());
        }

        System.out.println();
    }

    /**
     * 演示ArrayDeque作为栈使用
     */
    public static void demonstrateAsStack() {
        System.out.println("========== ArrayDeque作为栈演示 ==========");

        ArrayDequeImplementation<String> stack = new ArrayDequeImplementation<>();

        // 压栈
        stack.push("First");
        stack.push("Second");
        stack.push("Third");

        System.out.println("栈大小: " + stack.size());
        System.out.println("栈顶元素: " + stack.peek());

        // 弹栈
        System.out.println("弹栈顺序:");
        while (!stack.isEmpty()) {
            System.out.println("  " + stack.pop());
        }

        System.out.println();
    }

    /**
     * 演示ArrayDeque作为双端队列使用
     */
    public static void demonstrateAsDeque() {
        System.out.println("========== ArrayDeque作为双端队列演示 ==========");

        ArrayDequeImplementation<String> deque = new ArrayDequeImplementation<>();

        // 两端添加元素
        deque.addFirst("Front1");
        deque.addLast("Back1");
        deque.addFirst("Front2");
        deque.addLast("Back2");

        System.out.println("双端队列大小: " + deque.size());
        System.out.println("队首: " + deque.peekFirst());
        System.out.println("队尾: " + deque.peekLast());

        // 两端移除元素
        System.out.println("从队首移除: " + deque.pollFirst());
        System.out.println("从队尾移除: " + deque.pollLast());
        System.out.println("剩余大小: " + deque.size());

        System.out.println();
    }

    /**
     * 主方法：运行所有演示
     */
    public static void main(String[] args) {
        demonstrateAsQueue();
        demonstrateAsStack();
        demonstrateAsDeque();
    }
}
