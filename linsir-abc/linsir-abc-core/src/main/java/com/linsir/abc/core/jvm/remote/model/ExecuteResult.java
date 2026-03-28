package com.linsir.abc.core.jvm.remote.model;

import java.io.Serializable;

/**
 * 执行结果
 *
 * 功能：封装服务器执行代码后的返回结果
 *
 * 包含信息：
 * 1. 执行是否成功
 * 2. 标准输出内容
 * 3. 返回值
 * 4. 异常信息（如果执行失败）
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExecuteResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 执行是否成功
     */
    private boolean success;

    /**
     * 标准输出内容
     */
    private String output;

    /**
     * 返回值
     */
    private Object result;

    /**
     * 异常信息（如果执行失败）
     */
    private Exception exception;

    /**
     * 构造执行结果
     *
     * @param success 执行是否成功
     * @param output 标准输出内容
     * @param result 返回值
     * @param exception 异常信息
     */
    public ExecuteResult(boolean success, String output, Object result, Exception exception) {
        this.success = success;
        this.output = output;
        this.result = result;
        this.exception = exception;
    }

    /**
     * 获取执行是否成功
     *
     * @return true表示成功，false表示失败
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 获取标准输出内容
     *
     * @return 输出内容
     */
    public String getOutput() {
        return output;
    }

    /**
     * 获取返回值
     *
     * @return 返回值
     */
    public Object getResult() {
        return result;
    }

    /**
     * 获取异常信息
     *
     * @return 异常信息，如果执行成功则返回null
     */
    public Exception getException() {
        return exception;
    }

    @Override
    public String toString() {
        return "ExecuteResult{" +
                "success=" + success +
                ", output='" + output + '\'' +
                ", result=" + result +
                ", exception=" + exception +
                '}';
    }
}
