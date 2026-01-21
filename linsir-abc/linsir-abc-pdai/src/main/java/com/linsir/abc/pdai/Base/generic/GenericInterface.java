package com.linsir.abc.pdai.base.generic;

/**
 * 泛型接口示例
 * T 是类型参数
 */
public interface GenericInterface<T> {
    T getValue();
    void setValue(T value);
}
