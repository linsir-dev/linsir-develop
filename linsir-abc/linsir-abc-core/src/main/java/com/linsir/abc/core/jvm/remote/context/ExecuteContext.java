package com.linsir.abc.core.jvm.remote.context;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 代码执行上下文
 *
 * 功能：
 * 1. 提供执行时的输入输出流
 * 2. 传递上下文对象（如Spring容器、数据库连接等）
 * 3. 收集执行结果
 *
 * 使用场景：
 * 1. 远程代码执行时传递服务器端对象
 * 2. 捕获执行期间的输出
 * 3. 传递执行参数
 *
 * 使用示例：
 * <pre>
 * ExecuteContext context = new ExecuteContext();
 * context.addContextObject("service", myService);
 * context.setParameter("timeout", 5000);
 * // 执行代码...
 * String output = context.getOutput();
 * </pre>
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExecuteContext {

    /**
     * 标准输出捕获流
     */
    private final ByteArrayOutputStream outputStream;

    /**
     * 包装后的打印流
     */
    private final PrintStream printStream;

    /**
     * 原始的标准输出流（用于恢复）
     */
    private final PrintStream originalOut;

    /**
     * 原始的标准错误流（用于恢复）
     */
    private final PrintStream originalErr;

    /**
     * 上下文对象映射
     * key: 对象名称
     * value: 对象实例
     */
    private final Map<String, Object> contextObjects;

    /**
     * 执行参数映射
     * key: 参数名称
     * value: 参数值
     */
    private final Map<String, Object> parameters;

    /**
     * 构造执行上下文
     */
    public ExecuteContext() {
        this.outputStream = new ByteArrayOutputStream();
        this.printStream = new PrintStream(outputStream, true);
        this.originalOut = System.out;
        this.originalErr = System.err;
        this.contextObjects = new HashMap<>();
        this.parameters = new HashMap<>();
    }

    /**
     * 获取标准输出流
     *
     * 注意：此流会捕获所有输出内容，可通过getOutput()获取
     *
     * @return PrintStream对象
     */
    public PrintStream getOut() {
        return printStream;
    }

    /**
     * 获取捕获的输出内容
     *
     * @return 输出字符串
     */
    public String getOutput() {
        return outputStream.toString();
    }

    /**
     * 清空输出内容
     */
    public void clearOutput() {
        outputStream.reset();
    }

    /**
     * 重定向系统输出流
     *
     * 调用此方法后，System.out和System.err的输出将被捕获
     */
    public void redirectSystemOut() {
        System.setOut(printStream);
        System.setErr(printStream);
    }

    /**
     * 恢复系统输出流
     *
     * 调用此方法后，System.out和System.err恢复为原始流
     */
    public void restoreSystemOut() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    /**
     * 添加上下文对象
     *
     * @param name 对象名称
     * @param object 对象实例
     */
    public void addContextObject(String name, Object object) {
        contextObjects.put(name, object);
    }

    /**
     * 批量添加上下文对象
     *
     * @param objects 对象映射
     */
    public void addContextObjects(Map<String, Object> objects) {
        if (objects != null) {
            contextObjects.putAll(objects);
        }
    }

    /**
     * 获取上下文对象
     *
     * @param name 对象名称
     * @return 对象实例，如果不存在返回null
     */
    public Object getContextObject(String name) {
        return contextObjects.get(name);
    }

    /**
     * 获取上下文对象（带类型转换）
     *
     * @param name 对象名称
     * @param type 对象类型
     * @param <T> 类型参数
     * @return 对象实例，如果不存在或类型不匹配返回null
     */
    @SuppressWarnings("unchecked")
    public <T> T getContextObject(String name, Class<T> type) {
        Object obj = contextObjects.get(name);
        if (obj != null && type.isInstance(obj)) {
            return (T) obj;
        }
        return null;
    }

    /**
     * 移除上下文对象
     *
     * @param name 对象名称
     * @return 被移除的对象，如果不存在返回null
     */
    public Object removeContextObject(String name) {
        return contextObjects.remove(name);
    }

    /**
     * 检查是否包含指定名称的上下文对象
     *
     * @param name 对象名称
     * @return true表示存在
     */
    public boolean hasContextObject(String name) {
        return contextObjects.containsKey(name);
    }

    /**
     * 设置执行参数
     *
     * @param name 参数名称
     * @param value 参数值
     */
    public void setParameter(String name, Object value) {
        parameters.put(name, value);
    }

    /**
     * 获取执行参数
     *
     * @param name 参数名称
     * @return 参数值，如果不存在返回null
     */
    public Object getParameter(String name) {
        return parameters.get(name);
    }

    /**
     * 获取执行参数（带默认值）
     *
     * @param name 参数名称
     * @param defaultValue 默认值
     * @param <T> 类型参数
     * @return 参数值，如果不存在返回默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String name, T defaultValue) {
        Object value = parameters.get(name);
        if (value != null) {
            try {
                return (T) value;
            } catch (ClassCastException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * 获取所有上下文对象
     *
     * @return 上下文对象映射的副本
     */
    public Map<String, Object> getAllContextObjects() {
        return new HashMap<>(contextObjects);
    }

    /**
     * 获取所有参数
     *
     * @return 参数映射的副本
     */
    public Map<String, Object> getAllParameters() {
        return new HashMap<>(parameters);
    }

    /**
     * 清空上下文对象
     */
    public void clearContextObjects() {
        contextObjects.clear();
    }

    /**
     * 清空参数
     */
    public void clearParameters() {
        parameters.clear();
    }

    /**
     * 清空所有数据（上下文对象、参数、输出）
     */
    public void clearAll() {
        contextObjects.clear();
        parameters.clear();
        outputStream.reset();
    }
}
