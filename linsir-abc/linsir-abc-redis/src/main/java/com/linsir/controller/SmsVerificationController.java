package com.linsir.controller;

import com.linsir.service.SmsVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 手机验证码控制器
 * 提供手机验证码相关的API接口
 */
@RestController
@RequestMapping("/redis/sms")
public class SmsVerificationController {

    @Autowired
    private SmsVerificationService smsVerificationService;

    /**
     * 发送验证码
     * @param phoneNumber 手机号码
     * @param expireSeconds 过期时间
     * @param maxAttempts 最大尝试次数
     * @return 操作结果
     */
    @PostMapping("/send-code")
    public String sendVerificationCode(
            @RequestParam String phoneNumber,
            @RequestParam(required = false, defaultValue = "300") long expireSeconds,
            @RequestParam(required = false, defaultValue = "3") int maxAttempts) {
        boolean result = smsVerificationService.sendVerificationCode(phoneNumber, expireSeconds, maxAttempts);
        return result ? "发送验证码成功" : "发送验证码失败，请稍后重试";
    }

    /**
     * 验证验证码
     * @param phoneNumber 手机号码
     * @param code 验证码
     * @return 验证结果
     */
    @PostMapping("/verify-code")
    public String verifyCode(
            @RequestParam String phoneNumber,
            @RequestParam String code) {
        boolean result = smsVerificationService.verifyCode(phoneNumber, code);
        return result ? "验证码验证成功" : "验证码验证失败";
    }

    /**
     * 检查是否可以发送验证码
     * @param phoneNumber 手机号码
     * @param coolDownSeconds 冷却时间
     * @return 是否可以发送
     */
    @GetMapping("/can-send")
    public boolean canSendCode(
            @RequestParam String phoneNumber,
            @RequestParam(required = false, defaultValue = "60") long coolDownSeconds) {
        return smsVerificationService.canSendCode(phoneNumber, coolDownSeconds);
    }

    /**
     * 清除验证码
     * @param phoneNumber 手机号码
     * @return 操作结果
     */
    @PostMapping("/clear-code")
    public String clearCode(@RequestParam String phoneNumber) {
        boolean result = smsVerificationService.clearCode(phoneNumber);
        return result ? "清除验证码成功" : "清除验证码失败";
    }

    /**
     * 获取验证码剩余过期时间
     * @param phoneNumber 手机号码
     * @return 剩余过期时间
     */
    @GetMapping("/code-ttl")
    public long getCodeTtl(@RequestParam String phoneNumber) {
        return smsVerificationService.getCodeTtl(phoneNumber);
    }
}
