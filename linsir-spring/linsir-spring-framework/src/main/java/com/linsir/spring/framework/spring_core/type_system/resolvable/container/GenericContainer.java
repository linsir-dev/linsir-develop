package com.linsir.spring.framework.spring_core.type_system.resolvable.container;

/**
 * 泛型容器类
 * 用于演示字段级别的泛型类型解析
 *
 * @param <T> 容器中存储的元素类型
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
public class GenericContainer<T> {

    /**
     * 存储的元素
     */
    private T element;

    /**
     * 元素名称
     */
    private String name;

    public GenericContainer() {
    }

    public GenericContainer(String name, T element) {
        this.name = name;
        this.element = element;
    }

    /**
     * 获取元素
     *
     * @return 元素
     */
    public T getElement() {
        return element;
    }

    /**
     * 设置元素
     *
     * @param element 元素
     */
    public void setElement(T element) {
        this.element = element;
    }

    /**
     * 获取名称
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取元素类型
     *
     * @return 元素类型
     */
    public Class<?> getElementType() {
        return element != null ? element.getClass() : null;
    }

    @Override
    public String toString() {
        return "GenericContainer{" +
                "name='" + name + '\'' +
                ", element=" + element +
                '}';
    }
}
