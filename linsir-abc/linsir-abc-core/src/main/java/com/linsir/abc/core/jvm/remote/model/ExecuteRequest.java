package com.linsir.abc.core.jvm.remote.model;

import java.io.Serializable;
import java.util.Map;

/**
 * 执行请求
 *
 * 功能：封装客户端发送到服务器的执行请求数据
 *
 * 包含信息：
 * 1. 类名（全限定名）
 * 2. Java源代码
 * 3. 上下文对象（用于传递服务器端对象到执行代码中）
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExecuteRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类名（全限定名）
     */
    private String className;

    /**
     * Java源代码
     */
    private String sourceCode;

    /**
     * 上下文对象映射
     * key: 对象名称
     * value: 对象实例
     */
    private Map<String, Object> contextObjects;

    /**
     * 获取类名
     *
     * @return 类名
     */
    public String getClassName() {
        return className;
    }

    /**
     * 设置类名
     *
     * @param className 类名
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * 获取源代码
     *
     * @return Java源代码
     */
    public String getSourceCode() {
        return sourceCode;
    }

    /**
     * 设置源代码
     *
     * @param sourceCode Java源代码
     */
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    /**
     * 获取上下文对象
     *
     * @return 上下文对象映射
     */
    public Map<String, Object> getContextObjects() {
        return contextObjects;
    }

    /**
     * 设置上下文对象
     *
     * @param contextObjects 上下文对象映射
     */
    public void setContextObjects(Map<String, Object> contextObjects) {
        this.contextObjects = contextObjects;
    }

    @Override
    public String toString() {
        return "ExecuteRequest{" +
                "className='" + className + '\'' +
                ", sourceCodeLength=" + (sourceCode != null ? sourceCode.length() : 0) +
                ", contextObjects=" + contextObjects +
                '}';
    }
}
