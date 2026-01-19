package com.linsir.designpattern.iterator;

public interface Iterator<T> {
    boolean hasNext();

    T next();
}
