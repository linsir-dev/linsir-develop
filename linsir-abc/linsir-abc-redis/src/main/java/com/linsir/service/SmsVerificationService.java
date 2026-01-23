package com.linsir.service;

/**
 * 手机验证码服务接口
 * 用于生成、发送和验证手机验证码，使用Redis存储验证码并设置过期时间
 */
public interface SmsVerificationService {

    /**
     * 生成并发送手机验证码
     * @param phoneNumber 手机号码
     * @param expireSeconds 验证码过期时间（秒）
     * @param maxAttempts 最大尝试次数
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String phoneNumber, long expireSeconds, int maxAttempts);

    /**
     * 验证手机验证码
     * @param phoneNumber 手机号码
     * @param code 验证码
     * @return 是否验证成功
     */
    boolean verifyCode(String phoneNumber, String code);

    /**
     * 检查是否可以发送验证码（防止频繁发送）
     * @param phoneNumber 手机号码
     * @param coolDownSeconds 冷却时间（秒）
     * @return 是否可以发送
     */
    boolean canSendCode(String phoneNumber, long coolDownSeconds);

    /**
     * 清除手机验证码
     * @param phoneNumber 手机号码
     * @return 是否清除成功
     */
    boolean clearCode(String phoneNumber);

    /**
     * 获取验证码剩余过期时间
     * @param phoneNumber 手机号码
     * @return 剩余过期时间（秒），-1表示不存在
     */
    long getCodeTtl(String phoneNumber);
}
