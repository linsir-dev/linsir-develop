package com.linsir.abc.core.base.util.concurrent;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * 写时复制列表实现
 * 演示CopyOnWriteArrayList的核心原理：读多写少、写时复制、读操作无锁
 *
 * <p>设计要点：</p>
 * <ul>
 *   <li>写时复制：写操作创建新数组，读操作读取旧数组</li>
 *   <li>读操作无锁：读操作不需要加锁，性能高</li>
 *   <li>写操作加锁：写操作需要加锁，保证线程安全</li>
 *   <li>最终一致性：读操作可能读到旧数据，但最终会一致</li>
 * </ul>
 *
 * <p>适用场景：</p>
 * <ul>
 *   <li>读多写少：读操作远多于写操作</li>
 *   <li>数据量小：写操作需要复制整个数组</li>
 *   <li>容忍旧数据：读操作可以容忍读到旧数据</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class CopyOnWriteArrayListImplementation<E> implements List<E> {

    /**
     * 锁
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 数组（volatile保证可见性）
     */
    private volatile Object[] array;

    /**
     * 默认构造器
     */
    public CopyOnWriteArrayListImplementation() {
        this.array = new Object[0];
    }

    /**
     * 带初始集合的构造器
     *
     * @param c 初始集合
     */
    public CopyOnWriteArrayListImplementation(Collection<? extends E> c) {
        this.array = c.toArray();
    }

    /**
     * 获取当前数组（读操作）
     *
     * @return 当前数组
     */
    private Object[] getArray() {
        return array;
    }

    /**
     * 设置数组（写操作）
     *
     * @param a 新数组
     */
    private void setArray(Object[] a) {
        array = a;
    }

    @Override
    public int size() {
        return getArray().length;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new CowIterator<>(getArray(), 0);
    }

    @Override
    public Object[] toArray() {
        Object[] elements = getArray();
        return Arrays.copyOf(elements, elements.length);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        Object[] elements = getArray();
        int len = elements.length;
        if (a.length < len) {
            return (T[]) Arrays.copyOf(elements, len, a.getClass());
        } else {
            System.arraycopy(elements, 0, a, 0, len);
            if (a.length > len) {
                a[len] = null;
            }
            return a;
        }
    }

    @Override
    public boolean add(E e) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            Object[] newElements = Arrays.copyOf(elements, len + 1);
            newElements[len] = e;
            setArray(newElements);
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(Object o) {
        Object[] elements = getArray();
        int index = indexOf(o, elements);
        if (index < 0) {
            return false;
        }
        remove(index);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c.isEmpty()) {
            return false;
        }
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            Object[] newElements = Arrays.copyOf(elements, len + c.size());
            int i = len;
            for (E e : c) {
                newElements[i++] = e;
            }
            setArray(newElements);
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (c.isEmpty()) {
            return false;
        }
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            if (index < 0 || index > len) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + len);
            }
            Object[] newElements = new Object[len + c.size()];
            System.arraycopy(elements, 0, newElements, 0, index);
            int i = index;
            for (E e : c) {
                newElements[i++] = e;
            }
            System.arraycopy(elements, index, newElements, i, len - index);
            setArray(newElements);
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.isEmpty()) {
            return false;
        }
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            if (len == 0) {
                return false;
            }

            // 创建临时数组存储不需要删除的元素
            Object[] temp = new Object[len];
            int newLen = 0;
            for (int i = 0; i < len; i++) {
                Object element = elements[i];
                if (!c.contains(element)) {
                    temp[newLen++] = element;
                }
            }

            if (newLen == len) {
                return false;
            }

            setArray(Arrays.copyOf(temp, newLen));
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            if (len == 0) {
                return false;
            }

            // 创建临时数组存储需要保留的元素
            Object[] temp = new Object[len];
            int newLen = 0;
            for (int i = 0; i < len; i++) {
                Object element = elements[i];
                if (c.contains(element)) {
                    temp[newLen++] = element;
                }
            }

            if (newLen == len) {
                return false;
            }

            setArray(Arrays.copyOf(temp, newLen));
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            setArray(new Object[0]);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        return (E) getArray()[index];
    }

    @Override
    public E set(int index, E element) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            @SuppressWarnings("unchecked")
            E oldValue = (E) elements[index];
            if (oldValue != element) {
                int len = elements.length;
                Object[] newElements = Arrays.copyOf(elements, len);
                newElements[index] = element;
                setArray(newElements);
            }
            return oldValue;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void add(int index, E element) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            if (index < 0 || index > len) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + len);
            }
            Object[] newElements = new Object[len + 1];
            System.arraycopy(elements, 0, newElements, 0, index);
            newElements[index] = element;
            System.arraycopy(elements, index, newElements, index + 1, len - index);
            setArray(newElements);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            if (index < 0 || index >= len) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + len);
            }
            E oldValue = (E) elements[index];
            Object[] newElements = new Object[len - 1];
            System.arraycopy(elements, 0, newElements, 0, index);
            System.arraycopy(elements, index + 1, newElements, index, len - index - 1);
            setArray(newElements);
            return oldValue;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int indexOf(Object o) {
        return indexOf(o, getArray());
    }

    /**
     * 在指定数组中查找元素索引
     *
     * @param o 元素
     * @param elements 数组
     * @return 索引，如果不存在返回-1
     */
    private int indexOf(Object o, Object[] elements) {
        if (o == null) {
            for (int i = 0; i < elements.length; i++) {
                if (elements[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < elements.length; i++) {
                if (o.equals(elements[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        Object[] elements = getArray();
        if (o == null) {
            for (int i = elements.length - 1; i >= 0; i--) {
                if (elements[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = elements.length - 1; i >= 0; i--) {
                if (o.equals(elements[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return new CowIterator<>(getArray(), 0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        Object[] elements = getArray();
        if (index < 0 || index > elements.length) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        return new CowIterator<>(elements, index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("subList not supported");
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            Object[] newElements = Arrays.copyOf(elements, len);
            for (int i = 0; i < len; i++) {
                @SuppressWarnings("unchecked")
                E e = (E) elements[i];
                newElements[i] = operator.apply(e);
            }
            setArray(newElements);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void sort(Comparator<? super E> c) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            @SuppressWarnings("unchecked")
            E[] es = (E[]) Arrays.copyOf(elements, elements.length);
            Arrays.sort(es, c);
            setArray(es);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(getArray(), Spliterator.IMMUTABLE | Spliterator.ORDERED);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            if (len == 0) {
                return false;
            }

            // 创建临时数组存储不需要删除的元素
            Object[] temp = new Object[len];
            int newLen = 0;
            for (int i = 0; i < len; i++) {
                @SuppressWarnings("unchecked")
                E e = (E) elements[i];
                if (!filter.test(e)) {
                    temp[newLen++] = e;
                }
            }

            if (newLen == len) {
                return false;
            }

            setArray(Arrays.copyOf(temp, newLen));
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        for (Object e : getArray()) {
            @SuppressWarnings("unchecked")
            E element = (E) e;
            action.accept(element);
        }
    }

    /**
     * 迭代器实现
     */
    private static class CowIterator<E> implements ListIterator<E> {

        /**
         * 快照数组
         */
        private final Object[] snapshot;

        /**
         * 当前索引
         */
        private int cursor;

        CowIterator(Object[] snapshot, int cursor) {
            this.snapshot = snapshot;
            this.cursor = cursor;
        }

        @Override
        public boolean hasNext() {
            return cursor < snapshot.length;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return (E) snapshot[cursor++];
        }

        @Override
        public boolean hasPrevious() {
            return cursor > 0;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            return (E) snapshot[--cursor];
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }

        @Override
        public void set(E e) {
            throw new UnsupportedOperationException("set not supported");
        }

        @Override
        public void add(E e) {
            throw new UnsupportedOperationException("add not supported");
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(getArray());
    }
}
