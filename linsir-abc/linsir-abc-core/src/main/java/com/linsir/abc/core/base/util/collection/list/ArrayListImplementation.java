package com.linsir.abc.core.base.util.collection.list;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 简化版ArrayList实现
 * 
 * 本类模拟JDK ArrayList的核心实现：
 * 1. 基于数组存储元素
 * 2. 动态扩容机制（默认1.5倍）
 * 3. 支持快速随机访问
 * 4. 非线程安全
 * 
 * 核心设计：
 * - elementData：存储元素的数组缓冲区
 * - size：实际元素数量
 * - DEFAULT_CAPACITY：默认初始容量
 * 
 * 扩容策略：
 * - 当容量不足时，扩容为原来的1.5倍
 * - 使用Arrays.copyOf进行高效数组拷贝
 * 
 * @param <E> 元素类型
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class ArrayListImplementation<E> implements Iterable<E> {
    
    /**
     * 默认初始容量
     */
    private static final int DEFAULT_CAPACITY = 10;
    
    /**
     * 空数组实例（用于空列表）
     */
    private static final Object[] EMPTY_ELEMENT_DATA = {};
    
    /**
     * 默认容量的空数组实例
     */
    private static final Object[] DEFAULT_CAPACITY_EMPTY_ELEMENT_DATA = {};
    
    /**
     * 存储元素的数组缓冲区
     * 容量 >= size，可能有多余空间
     */
    private Object[] elementData;
    
    /**
     * 列表中实际元素的数量
     */
    private int size;
    
    /**
     * 构造一个空列表，默认初始容量为10
     */
    public ArrayListImplementation() {
        this.elementData = DEFAULT_CAPACITY_EMPTY_ELEMENT_DATA;
    }
    
    /**
     * 构造一个指定初始容量的空列表
     * 
     * @param initialCapacity 初始容量
     * @throws IllegalArgumentException 如果初始容量为负数
     */
    public ArrayListImplementation(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENT_DATA;
        } else {
            throw new IllegalArgumentException("非法容量: " + initialCapacity);
        }
    }
    
    /**
     * 返回列表中的元素数量
     * 
     * @return 元素数量
     */
    public int size() {
        return size;
    }
    
    /**
     * 判断列表是否为空
     * 
     * @return 如果列表为空返回true，否则返回false
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * 添加元素到列表末尾
     * 
     * 步骤：
     * 1. 确保容量足够
     * 2. 在size位置添加元素
     * 3. size增加1
     * 
     * @param e 要添加的元素
     * @return 始终返回true
     */
    public boolean add(E e) {
        // 确保容量足够（size + 1表示添加一个元素后的最小容量）
        ensureCapacityInternal(size + 1);
        
        // 添加元素
        elementData[size++] = e;
        return true;
    }
    
    /**
     * 在指定位置插入元素
     * 
     * @param index 插入位置
     * @param element 要插入的元素
     * @throws IndexOutOfBoundsException 如果索引越界
     */
    public void add(int index, E element) {
        // 检查索引范围
        rangeCheckForAdd(index);
        
        // 确保容量足够
        ensureCapacityInternal(size + 1);
        
        // 将index及之后的元素后移一位
        System.arraycopy(elementData, index, elementData, index + 1, size - index);
        
        // 插入元素
        elementData[index] = element;
        size++;
    }
    
    /**
     * 获取指定位置的元素
     * 
     * ArrayList的优势：支持O(1)时间复杂度的随机访问
     * 
     * @param index 元素位置
     * @return 指定位置的元素
     * @throws IndexOutOfBoundsException 如果索引越界
     */
    @SuppressWarnings("unchecked")
    public E get(int index) {
        rangeCheck(index);
        return (E) elementData[index];
    }
    
    /**
     * 设置指定位置的元素
     * 
     * @param index 元素位置
     * @param element 新元素
     * @return 原来的元素
     * @throws IndexOutOfBoundsException 如果索引越界
     */
    @SuppressWarnings("unchecked")
    public E set(int index, E element) {
        rangeCheck(index);
        E oldValue = (E) elementData[index];
        elementData[index] = element;
        return oldValue;
    }
    
    /**
     * 移除指定位置的元素
     * 
     * @param index 元素位置
     * @return 被移除的元素
     * @throws IndexOutOfBoundsException 如果索引越界
     */
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        rangeCheck(index);
        
        E oldValue = (E) elementData[index];
        
        // 计算需要移动的元素数量
        int numMoved = size - index - 1;
        
        // 将index之后的元素前移一位
        if (numMoved > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);
        }
        
        // 清空最后一个元素（帮助GC）
        elementData[--size] = null;
        
        return oldValue;
    }
    
    /**
     * 移除第一个匹配的元素
     * 
     * @param o 要移除的元素
     * @return 如果找到并移除返回true，否则返回false
     */
    public boolean remove(Object o) {
        if (o == null) {
            // 移除null元素
            for (int i = 0; i < size; i++) {
                if (elementData[i] == null) {
                    fastRemove(i);
                    return true;
                }
            }
        } else {
            // 移除非null元素
            for (int i = 0; i < size; i++) {
                if (o.equals(elementData[i])) {
                    fastRemove(i);
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 快速移除（不返回被移除的元素）
     * 
     * @param index 元素位置
     */
    private void fastRemove(int index) {
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);
        }
        elementData[--size] = null;
    }
    
    /**
     * 检查是否包含指定元素
     * 
     * @param o 要检查的元素
     * @return 如果包含返回true，否则返回false
     */
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }
    
    /**
     * 查找元素的索引
     * 
     * @param o 要查找的元素
     * @return 元素索引，如果不存在返回-1
     */
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++) {
                if (elementData[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (o.equals(elementData[i])) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    /**
     * 清空列表
     */
    public void clear() {
        // 清空所有元素（帮助GC）
        for (int i = 0; i < size; i++) {
            elementData[i] = null;
        }
        size = 0;
    }
    
    /**
     * 确保内部容量足够
     * 
     * @param minCapacity 最小容量需求
     */
    private void ensureCapacityInternal(int minCapacity) {
        // 如果是默认空数组，使用默认容量和minCapacity的较大值
        if (elementData == DEFAULT_CAPACITY_EMPTY_ELEMENT_DATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        
        ensureExplicitCapacity(minCapacity);
    }
    
    /**
     * 确保显式容量足够
     * 
     * @param minCapacity 最小容量需求
     */
    private void ensureExplicitCapacity(int minCapacity) {
        // 如果当前容量不足，进行扩容
        if (minCapacity - elementData.length > 0) {
            grow(minCapacity);
        }
    }
    
    /**
     * 扩容方法
     * 
     * 扩容策略：
     * 1. 新容量 = 旧容量的1.5倍（oldCapacity + (oldCapacity >> 1)）
     * 2. 如果1.5倍还不够，使用所需的最小容量
     * 3. 使用Arrays.copyOf进行数组拷贝
     * 
     * @param minCapacity 最小容量需求
     */
    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        
        // 新容量 = 旧容量的1.5倍
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        
        // 如果1.5倍还不够，使用最小容量
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        
        // 扩容
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
    
    /**
     * 检查索引是否越界（用于get/set/remove）
     * 
     * @param index 索引
     */
    private void rangeCheck(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("索引: " + index + ", 大小: " + size);
        }
    }
    
    /**
     * 检查索引是否越界（用于add）
     * 
     * @param index 索引
     */
    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException("索引: " + index + ", 大小: " + size);
        }
    }
    
    /**
     * 返回迭代器
     * 
     * @return 迭代器
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int cursor = 0;
            
            @Override
            public boolean hasNext() {
                return cursor < size;
            }
            
            @Override
            @SuppressWarnings("unchecked")
            public E next() {
                if (cursor >= size) {
                    throw new NoSuchElementException();
                }
                return (E) elementData[cursor++];
            }
        };
    }
    
    /**
     * 转换为数组
     * 
     * @return 包含所有元素的数组
     */
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }
    
    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < size; i++) {
            sb.append(elementData[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append(']');
        return sb.toString();
    }
}
