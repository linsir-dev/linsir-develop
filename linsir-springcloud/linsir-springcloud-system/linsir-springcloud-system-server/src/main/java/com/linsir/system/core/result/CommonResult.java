package com.linsir.system.core.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Objects;

/**
 * 通用返回结果
 *
 * @param <T> 数据类型
 * @author linsir
 * @version 1.0.0
 */
@Data
public class CommonResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功状态码
     */
    public static final Integer CODE_SUCCESS = 0;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 错误提示
     */
    private String msg;

    /**
     * 构造方法
     */
    public CommonResult() {
    }

    public CommonResult(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    /**
     * 判断是否为成功响应
     *
     * @return 是否成功
     */
    @JsonIgnore
    public boolean isSuccess() {
        return Objects.equals(CODE_SUCCESS, code);
    }

    /**
     * 判断是否为失败响应
     *
     * @return 是否失败
     */
    @JsonIgnore
    public boolean isError() {
        return !isSuccess();
    }

    // ==================== 成功响应 ====================

    /**
     * 成功响应（无数据）
     *
     * @return 通用结果
     */
    public static <T> CommonResult<T> success() {
        return new CommonResult<>(CODE_SUCCESS, null, "success");
    }

    /**
     * 成功响应（有数据）
     *
     * @param data 数据
     * @return 通用结果
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(CODE_SUCCESS, data, "success");
    }

    /**
     * 成功响应（自定义消息）
     *
     * @param data 数据
     * @param msg  消息
     * @return 通用结果
     */
    public static <T> CommonResult<T> success(T data, String msg) {
        return new CommonResult<>(CODE_SUCCESS, data, msg);
    }

    // ==================== 失败响应 ====================

    /**
     * 失败响应（自定义错误码和消息）
     *
     * @param code 错误码
     * @param msg  错误消息
     * @return 通用结果
     */
    public static <T> CommonResult<T> error(Integer code, String msg) {
        Assert.isTrue(!CODE_SUCCESS.equals(code), "code 必须是错误的！");
        return new CommonResult<>(code, null, msg);
    }

    /**
     * 失败响应（自定义消息，默认错误码）
     *
     * @param msg 错误消息
     * @return 通用结果
     */
    public static <T> CommonResult<T> error(String msg) {
        return new CommonResult<>(500, null, msg);
    }

    /**
     * 失败响应（基于错误码枚举）
     *
     * @param errorCode 错误码枚举
     * @return 通用结果
     */
    public static <T> CommonResult<T> error(ErrorCode errorCode) {
        return new CommonResult<>(errorCode.getCode(), null, errorCode.getMsg());
    }

    /**
     * 失败响应（基于错误码枚举，带参数）
     *
     * @param errorCode 错误码枚举
     * @param params    参数
     * @return 通用结果
     */
    public static <T> CommonResult<T> error(ErrorCode errorCode, Object... params) {
        return new CommonResult<>(errorCode.getCode(), null, String.format(errorCode.getMsg(), params));
    }
}
