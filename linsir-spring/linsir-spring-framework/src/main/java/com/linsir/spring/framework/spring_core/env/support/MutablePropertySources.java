package com.linsir.spring.framework.spring_core.env.support;

import com.linsir.spring.framework.spring_core.env.source.PropertySource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 可变的属性源集合
 *
 * 管理多个 PropertySource，支持按优先级排序。
 * 属性源按照添加顺序形成优先级，先添加的优先级更高。
 *
 * @author linsir
 * @since 1.0.0
 */
public class MutablePropertySources implements Iterable<PropertySource<?>> {

    /**
     * 属性源列表
     */
    private final List<PropertySource<?>> propertySourceList;

    /**
     * 创建一个新的 MutablePropertySources
     */
    public MutablePropertySources() {
        this.propertySourceList = new CopyOnWriteArrayList<>();
    }

    /**
     * 从另一个 MutablePropertySources 创建
     *
     * @param propertySources 源属性源集合
     */
    public MutablePropertySources(MutablePropertySources propertySources) {
        this();
        if (propertySources != null) {
            for (PropertySource<?> propertySource : propertySources) {
                this.addLast(propertySource);
            }
        }
    }

    /**
     * 将属性源添加到列表开头（最高优先级）
     *
     * @param propertySource 属性源
     */
    public void addFirst(PropertySource<?> propertySource) {
        if (propertySource == null) {
            throw new IllegalArgumentException("PropertySource must not be null");
        }
        removeIfPresent(propertySource.getName());
        this.propertySourceList.add(0, propertySource);
    }

    /**
     * 将属性源添加到列表末尾（最低优先级）
     *
     * @param propertySource 属性源
     */
    public void addLast(PropertySource<?> propertySource) {
        if (propertySource == null) {
            throw new IllegalArgumentException("PropertySource must not be null");
        }
        removeIfPresent(propertySource.getName());
        this.propertySourceList.add(propertySource);
    }

    /**
     * 在指定属性源之前添加
     *
     * @param relativePropertySourceName 相对属性源名称
     * @param propertySource 要添加的属性源
     */
    public void addBefore(String relativePropertySourceName, PropertySource<?> propertySource) {
        if (propertySource == null) {
            throw new IllegalArgumentException("PropertySource must not be null");
        }
        int index = indexOf(relativePropertySourceName);
        if (index == -1) {
            throw new IllegalArgumentException("PropertySource '" + relativePropertySourceName + "' not found");
        }
        removeIfPresent(propertySource.getName());
        this.propertySourceList.add(index, propertySource);
    }

    /**
     * 在指定属性源之后添加
     *
     * @param relativePropertySourceName 相对属性源名称
     * @param propertySource 要添加的属性源
     */
    public void addAfter(String relativePropertySourceName, PropertySource<?> propertySource) {
        if (propertySource == null) {
            throw new IllegalArgumentException("PropertySource must not be null");
        }
        int index = indexOf(relativePropertySourceName);
        if (index == -1) {
            throw new IllegalArgumentException("PropertySource '" + relativePropertySourceName + "' not found");
        }
        removeIfPresent(propertySource.getName());
        this.propertySourceList.add(index + 1, propertySource);
    }

    /**
     * 获取指定位置的属性源
     *
     * @param index 索引
     * @return 属性源
     */
    public PropertySource<?> get(int index) {
        return this.propertySourceList.get(index);
    }

    /**
     * 获取指定名称的属性源
     *
     * @param name 属性源名称
     * @return 属性源，如果不存在则返回 null
     */
    public PropertySource<?> get(String name) {
        for (PropertySource<?> propertySource : this.propertySourceList) {
            if (propertySource.getName().equals(name)) {
                return propertySource;
            }
        }
        return null;
    }

    /**
     * 获取属性源数量
     *
     * @return 属性源数量
     */
    public int size() {
        return this.propertySourceList.size();
    }

    /**
     * 判断是否为空
     *
     * @return 如果为空则返回 true
     */
    public boolean isEmpty() {
        return this.propertySourceList.isEmpty();
    }

    /**
     * 判断是否包含指定名称的属性源
     *
     * @param name 属性源名称
     * @return 如果包含则返回 true
     */
    public boolean contains(String name) {
        return get(name) != null;
    }

    /**
     * 移除指定名称的属性源
     *
     * @param name 属性源名称
     * @return 被移除的属性源，如果不存在则返回 null
     */
    public PropertySource<?> remove(String name) {
        PropertySource<?> propertySource = get(name);
        if (propertySource != null) {
            this.propertySourceList.remove(propertySource);
        }
        return propertySource;
    }

    /**
     * 替换指定名称的属性源
     *
     * @param name 属性源名称
     * @param propertySource 新的属性源
     * @return 被替换的属性源，如果不存在则返回 null
     */
    public PropertySource<?> replace(String name, PropertySource<?> propertySource) {
        if (propertySource == null) {
            throw new IllegalArgumentException("PropertySource must not be null");
        }
        int index = indexOf(name);
        if (index == -1) {
            return null;
        }
        PropertySource<?> old = this.propertySourceList.get(index);
        this.propertySourceList.set(index, propertySource);
        return old;
    }

    /**
     * 获取属性源名称列表
     *
     * @return 属性源名称列表
     */
    public List<String> getPropertySourceNames() {
        List<String> names = new ArrayList<>();
        for (PropertySource<?> propertySource : this.propertySourceList) {
            names.add(propertySource.getName());
        }
        return Collections.unmodifiableList(names);
    }

    @Override
    public Iterator<PropertySource<?>> iterator() {
        return this.propertySourceList.iterator();
    }

    /**
     * 如果存在则移除
     *
     * @param name 属性源名称
     */
    private void removeIfPresent(String name) {
        remove(name);
    }

    /**
     * 获取指定名称的属性源索引
     *
     * @param name 属性源名称
     * @return 索引，如果不存在则返回 -1
     */
    private int indexOf(String name) {
        for (int i = 0; i < this.propertySourceList.size(); i++) {
            if (this.propertySourceList.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return this.propertySourceList.toString();
    }
}
