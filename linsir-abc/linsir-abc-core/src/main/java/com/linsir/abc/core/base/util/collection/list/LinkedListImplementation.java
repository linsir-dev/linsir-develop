package com.linsir.abc.core.base.util.collection.list;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 简化版LinkedList实现
 * 
 * 本类模拟JDK LinkedList的核心实现：
 * 1. 基于双向链表存储元素
 * 2. 支持在头部和尾部高效添加/删除
 * 3. 实现List和Deque的基本操作
 * 
 * LinkedList vs ArrayList：
 * - LinkedList：插入删除O(1)，随机访问O(n)，内存开销大
 * - ArrayList：插入删除O(n)，随机访问O(1)，内存开销小
 * 
 * 适用场景：
 * - 频繁在头部/尾部添加删除元素
 * - 不需要频繁随机访问
 * - 实现队列或双端队列
 * 
 * @param <E> 元素类型
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class LinkedListImplementation<E> implements Iterable<E> {
    
    /**
     * 链表节点类
     */
    private static class Node<E> {
        E item;           // 节点存储的元素
        Node<E> next;     // 下一个节点
        Node<E> prev;     // 上一个节点
        
        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
    
    // 链表大小
    private int size = 0;
    
    // 首节点
    private Node<E> first;
    
    // 尾节点
    private Node<E> last;
    
    // 修改计数（用于快速失败迭代器）
    private int modCount = 0;
    
    /**
     * 构造一个空链表
     */
    public LinkedListImplementation() {
    }
    
    /**
     * 在链表尾部添加元素
     * 
     * @param e 要添加的元素
     * @return true（按约定）
     */
    public boolean add(E e) {
        linkLast(e);
        return true;
    }
    
    /**
     * 在链表头部添加元素
     * 
     * @param e 要添加的元素
     */
    public void addFirst(E e) {
        linkFirst(e);
    }
    
    /**
     * 在链表尾部添加元素
     * 
     * @param e 要添加的元素
     */
    public void addLast(E e) {
        linkLast(e);
    }
    
    /**
     * 在指定位置插入元素
     * 
     * @param index 插入位置
     * @param element 要插入的元素
     * @throws IndexOutOfBoundsException 如果索引越界
     */
    public void add(int index, E element) {
        checkPositionIndex(index);
        
        if (index == size) {
            linkLast(element);
        } else {
            linkBefore(element, node(index));
        }
    }
    
    /**
     * 获取指定位置的元素
     * 
     * @param index 元素索引
     * @return 指定位置的元素
     * @throws IndexOutOfBoundsException 如果索引越界
     */
    public E get(int index) {
        checkElementIndex(index);
        return node(index).item;
    }
    
    /**
     * 获取第一个元素
     * 
     * @return 第一个元素
     * @throws NoSuchElementException 如果链表为空
     */
    public E getFirst() {
        final Node<E> f = first;
        if (f == null) {
            throw new NoSuchElementException();
        }
        return f.item;
    }
    
    /**
     * 获取最后一个元素
     * 
     * @return 最后一个元素
     * @throws NoSuchElementException 如果链表为空
     */
    public E getLast() {
        final Node<E> l = last;
        if (l == null) {
            throw new NoSuchElementException();
        }
        return l.item;
    }
    
    /**
     * 移除并返回第一个元素
     * 
     * @return 第一个元素
     * @throws NoSuchElementException 如果链表为空
     */
    public E removeFirst() {
        final Node<E> f = first;
        if (f == null) {
            throw new NoSuchElementException();
        }
        return unlinkFirst(f);
    }
    
    /**
     * 移除并返回最后一个元素
     * 
     * @return 最后一个元素
     * @throws NoSuchElementException 如果链表为空
     */
    public E removeLast() {
        final Node<E> l = last;
        if (l == null) {
            throw new NoSuchElementException();
        }
        return unlinkLast(l);
    }
    
    /**
     * 移除指定位置的元素
     * 
     * @param index 要移除的元素索引
     * @return 被移除的元素
     * @throws IndexOutOfBoundsException 如果索引越界
     */
    public E remove(int index) {
        checkElementIndex(index);
        return unlink(node(index));
    }
    
    /**
     * 移除指定元素（第一个匹配项）
     * 
     * @param o 要移除的元素
     * @return true如果找到并移除元素
     */
    public boolean remove(Object o) {
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 在头部插入元素（队列操作）
     * 
     * @param e 要插入的元素
     * @return true（按约定）
     */
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }
    
    /**
     * 在尾部插入元素（队列操作）
     * 
     * @param e 要插入的元素
     * @return true（按约定）
     */
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }
    
    /**
     * 移除并返回头部元素（队列操作）
     * 
     * @return 头部元素，如果为空返回null
     */
    public E pollFirst() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }
    
    /**
     * 移除并返回尾部元素（队列操作）
     * 
     * @return 尾部元素，如果为空返回null
     */
    public E pollLast() {
        final Node<E> l = last;
        return (l == null) ? null : unlinkLast(l);
    }
    
    /**
     * 查看头部元素（队列操作）
     * 
     * @return 头部元素，如果为空返回null
     */
    public E peekFirst() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }
    
    /**
     * 查看尾部元素（队列操作）
     * 
     * @return 尾部元素，如果为空返回null
     */
    public E peekLast() {
        final Node<E> l = last;
        return (l == null) ? null : l.item;
    }
    
    /**
     * 返回链表大小
     * 
     * @return 元素数量
     */
    public int size() {
        return size;
    }
    
    /**
     * 检查链表是否为空
     * 
     * @return true如果链表为空
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * 清空链表
     */
    public void clear() {
        // 清空所有节点引用，帮助GC
        for (Node<E> x = first; x != null; ) {
            Node<E> next = x.next;
            x.item = null;
            x.next = null;
            x.prev = null;
            x = next;
        }
        first = last = null;
        size = 0;
        modCount++;
    }
    
    /**
     * 在头部链接节点
     * 
     * @param e 要链接的元素
     */
    private void linkFirst(E e) {
        final Node<E> f = first;
        final Node<E> newNode = new Node<>(null, e, f);
        first = newNode;
        if (f == null) {
            last = newNode;
        } else {
            f.prev = newNode;
        }
        size++;
        modCount++;
    }
    
    /**
     * 在尾部链接节点
     * 
     * @param e 要链接的元素
     */
    private void linkLast(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
        size++;
        modCount++;
    }
    
    /**
     * 在指定节点前插入元素
     * 
     * @param e 要插入的元素
     * @param succ 后继节点
     */
    private void linkBefore(E e, Node<E> succ) {
        final Node<E> pred = succ.prev;
        final Node<E> newNode = new Node<>(pred, e, succ);
        succ.prev = newNode;
        if (pred == null) {
            first = newNode;
        } else {
            pred.next = newNode;
        }
        size++;
        modCount++;
    }
    
    /**
     * 取消链接首节点
     * 
     * @param f 首节点
     * @return 被移除的元素
     */
    private E unlinkFirst(Node<E> f) {
        final E element = f.item;
        final Node<E> next = f.next;
        f.item = null;
        f.next = null; // 帮助GC
        first = next;
        if (next == null) {
            last = null;
        } else {
            next.prev = null;
        }
        size--;
        modCount++;
        return element;
    }
    
    /**
     * 取消链接尾节点
     * 
     * @param l 尾节点
     * @return 被移除的元素
     */
    private E unlinkLast(Node<E> l) {
        final E element = l.item;
        final Node<E> prev = l.prev;
        l.item = null;
        l.prev = null; // 帮助GC
        last = prev;
        if (prev == null) {
            first = null;
        } else {
            prev.next = null;
        }
        size--;
        modCount++;
        return element;
    }
    
    /**
     * 取消链接指定节点
     * 
     * @param x 要取消链接的节点
     * @return 被移除的元素
     */
    private E unlink(Node<E> x) {
        final E element = x.item;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;
        
        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }
        
        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }
        
        x.item = null;
        size--;
        modCount++;
        return element;
    }
    
    /**
     * 获取指定索引的节点
     * 优化：根据索引位置决定从头部还是尾部开始遍历
     * 
     * @param index 节点索引
     * @return 指定索引的节点
     */
    private Node<E> node(int index) {
        // 如果索引在前半部分，从头部开始遍历
        if (index < (size >> 1)) {
            Node<E> x = first;
            for (int i = 0; i < index; i++) {
                x = x.next;
            }
            return x;
        } else {
            // 如果索引在后半部分，从尾部开始遍历
            Node<E> x = last;
            for (int i = size - 1; i > index; i--) {
                x = x.prev;
            }
            return x;
        }
    }
    
    /**
     * 检查元素索引是否有效
     * 
     * @param index 要检查的索引
     */
    private void checkElementIndex(int index) {
        if (!isElementIndex(index)) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }
    
    /**
     * 检查位置索引是否有效
     * 
     * @param index 要检查的索引
     */
    private void checkPositionIndex(int index) {
        if (!isPositionIndex(index)) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }
    
    /**
     * 判断是否为有效的元素索引
     * 
     * @param index 索引
     * @return true如果是有效的元素索引
     */
    private boolean isElementIndex(int index) {
        return index >= 0 && index < size;
    }
    
    /**
     * 判断是否为有效的位置索引
     * 
     * @param index 索引
     * @return true如果是有效的位置索引
     */
    private boolean isPositionIndex(int index) {
        return index >= 0 && index <= size;
    }
    
    /**
     * 生成越界错误消息
     * 
     * @param index 越界的索引
     * @return 错误消息
     */
    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + size;
    }
    
    /**
     * 返回迭代器
     * 
     * @return 迭代器
     */
    @Override
    public Iterator<E> iterator() {
        return new ListItr(0);
    }
    
    /**
     * 返回降序迭代器
     * 
     * @return 降序迭代器
     */
    public Iterator<E> descendingIterator() {
        return new DescendingItr();
    }
    
    /**
     * 列表迭代器实现
     */
    private class ListItr implements Iterator<E> {
        private Node<E> lastReturned;
        private Node<E> next;
        private int nextIndex;
        private int expectedModCount = modCount;
        
        ListItr(int index) {
            next = (index == size) ? null : node(index);
            nextIndex = index;
        }
        
        @Override
        public boolean hasNext() {
            return nextIndex < size;
        }
        
        @Override
        public E next() {
            checkForComodification();
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.item;
        }
        
        @Override
        public void remove() {
            checkForComodification();
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            
            Node<E> lastNext = lastReturned.next;
            unlink(lastReturned);
            if (next == lastReturned) {
                next = lastNext;
            } else {
                nextIndex--;
            }
            lastReturned = null;
            expectedModCount = modCount;
        }
        
        final void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new java.util.ConcurrentModificationException();
            }
        }
    }
    
    /**
     * 降序迭代器实现
     */
    private class DescendingItr implements Iterator<E> {
        private final ExtendedListItr itr = new ExtendedListItr(size());
        
        @Override
        public boolean hasNext() {
            return itr.hasPrevious();
        }
        
        @Override
        public E next() {
            return itr.previous();
        }
        
        @Override
        public void remove() {
            itr.remove();
        }
    }
    
    /**
     * 扩展的列表迭代器支持双向遍历
     */
    private class ExtendedListItr extends ListItr {
        ExtendedListItr(int index) {
            super(index);
        }
        
        public boolean hasPrevious() {
            return super.nextIndex > 0;
        }
        
        public E previous() {
            checkForComodification();
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            
            super.lastReturned = super.next = (super.next == null) ? last : super.next.prev;
            super.nextIndex--;
            return super.lastReturned.item;
        }
        
        public int nextIndex() {
            return super.nextIndex;
        }
        
        public int previousIndex() {
            return super.nextIndex - 1;
        }
    }
}
