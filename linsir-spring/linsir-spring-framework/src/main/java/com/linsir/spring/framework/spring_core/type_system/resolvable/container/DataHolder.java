package com.linsir.spring.framework.spring_core.type_system.resolvable.container;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据持有者类
 * 包含各种泛型字段，用于演示复杂泛型类型的解析
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
public class DataHolder {

    /**
     * 字符串列表 - 简单泛型
     */
    private List<String> stringList;

    /**
     * 整数集合 - 简单泛型
     */
    private Set<Integer> integerSet;

    /**
     * 字符串到对象的映射 - 双参数泛型
     */
    private Map<String, Object> stringObjectMap;

    /**
     * 嵌套泛型：列表的列表
     */
    private List<List<String>> nestedList;

    /**
     * 复杂泛型：字符串到列表的映射
     */
    private Map<String, List<Integer>> complexMap;

    /**
     * 泛型数组
     */
    private String[] stringArray;

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public Set<Integer> getIntegerSet() {
        return integerSet;
    }

    public void setIntegerSet(Set<Integer> integerSet) {
        this.integerSet = integerSet;
    }

    public Map<String, Object> getStringObjectMap() {
        return stringObjectMap;
    }

    public void setStringObjectMap(Map<String, Object> stringObjectMap) {
        this.stringObjectMap = stringObjectMap;
    }

    public List<List<String>> getNestedList() {
        return nestedList;
    }

    public void setNestedList(List<List<String>> nestedList) {
        this.nestedList = nestedList;
    }

    public Map<String, List<Integer>> getComplexMap() {
        return complexMap;
    }

    public void setComplexMap(Map<String, List<Integer>> complexMap) {
        this.complexMap = complexMap;
    }

    public String[] getStringArray() {
        return stringArray;
    }

    public void setStringArray(String[] stringArray) {
        this.stringArray = stringArray;
    }
}
