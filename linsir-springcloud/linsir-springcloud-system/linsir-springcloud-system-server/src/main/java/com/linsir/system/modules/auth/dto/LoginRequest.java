package com.linsir.system.modules.auth.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求 DTO
 *
 * @author linsir
 * @version 1.0.0
 */
@Data
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 登录方式
     * password: 用户名密码登录
     * sms_code: 手机验证码登录
     */
    private String loginType = "password";

    /**
     * 用户名（密码登录时使用）
     */
    private String username;

    /**
     * 密码（密码登录时使用）
     */
    private String password;

    /**
     * 手机号（短信登录时使用）
     */
    private String phone;

    /**
     * 验证码（短信登录时使用）
     */
    private String code;

    /**
     * 设备ID（用于多设备管理）
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;
}
