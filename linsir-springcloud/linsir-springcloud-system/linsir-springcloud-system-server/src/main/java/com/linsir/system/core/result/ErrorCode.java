package com.linsir.system.core.result;

import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author linsir
 * @version 1.0.0
 */
@Getter
public enum ErrorCode {

    // ==================== 系统级别错误码 ====================
    SUCCESS(0, "成功"),
    SYSTEM_ERROR(500, "系统繁忙，请稍后再试"),
    SYSTEM_UNAVAILABLE(503, "系统服务不可用"),

    // ==================== 参数错误 ====================
    BAD_REQUEST(400, "请求参数不正确"),
    PARAM_ERROR(400100, "请求参数错误：%s"),
    PARAM_MISSING(400101, "请求参数缺失：%s"),
    PARAM_TYPE_ERROR(400102, "请求参数类型错误：%s"),

    // ==================== 认证授权错误 ====================
    UNAUTHORIZED(401, "账号未登录"),
    FORBIDDEN(403, "没有该操作权限"),
    TOKEN_EXPIRED(401100, "Token 已过期"),
    TOKEN_INVALID(401101, "Token 无效"),

    // ==================== 资源错误 ====================
    NOT_FOUND(404, "请求未找到"),
    METHOD_NOT_ALLOWED(405, "请求方法不正确"),

    // ==================== 业务错误 ====================
    BUSINESS_ERROR(500100, "业务异常：%s"),
    RECORD_NOT_FOUND(500101, "记录不存在"),
    RECORD_ALREADY_EXISTS(500102, "记录已存在"),

    // ==================== 限流降级 ====================
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),
    DEGRADED(500200, "服务降级中，请稍后再试");

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误提示
     */
    private final String msg;

    ErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
