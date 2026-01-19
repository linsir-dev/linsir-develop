package com.linsir.designpattern.iterator;


// 定义一个可迭代的集合接口
public interface IterableCollection<T> {
    Iterator<T> createIterator();
}
