package com.linsir.abc.pdai.Base.generic;

/**
 * 泛型类示例
 * T 是类型参数，可以是任何引用类型
 */
public class GenericClass<T> {
    private T value;

    public GenericClass(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void showType() {
        System.out.println("类型参数T的实际类型是: " + value.getClass().getName());
    }
}
