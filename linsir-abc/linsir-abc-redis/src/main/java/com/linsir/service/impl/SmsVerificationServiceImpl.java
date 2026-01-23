package com.linsir.service.impl;

import com.linsir.service.SmsVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

/**
 * 手机验证码服务实现类
 * 使用Redis存储验证码信息，设置过期时间，实现验证码的生成、发送和验证
 */
@Service
public class SmsVerificationServiceImpl implements SmsVerificationService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 验证码前缀
    private static final String VERIFICATION_CODE_PREFIX = "sms:code:";
    // 发送记录前缀
    private static final String SEND_RECORD_PREFIX = "sms:send:";
    // 尝试次数前缀
    private static final String ATTEMPT_PREFIX = "sms:attempt:";

    // 随机数生成器
    private final Random random = new Random();

    @Override
    public boolean sendVerificationCode(String phoneNumber, long expireSeconds, int maxAttempts) {
        // 检查是否可以发送
        if (!canSendCode(phoneNumber, 60)) { // 默认60秒冷却时间
            return false;
        }

        // 生成6位随机验证码
        String code = generateCode(6);
        String codeKey = VERIFICATION_CODE_PREFIX + phoneNumber;
        String sendKey = SEND_RECORD_PREFIX + phoneNumber;
        String attemptKey = ATTEMPT_PREFIX + phoneNumber;

        // 存储验证码到Redis
        redisTemplate.opsForValue().set(codeKey, code, Duration.ofSeconds(expireSeconds));
        // 记录发送时间
        redisTemplate.opsForValue().set(sendKey, System.currentTimeMillis(), Duration.ofSeconds(60));
        // 初始化尝试次数
        redisTemplate.opsForValue().set(attemptKey, 0, Duration.ofSeconds(expireSeconds));

        // 模拟发送短信（实际项目中应该调用短信发送API）
        System.out.println("向手机号 " + phoneNumber + " 发送验证码：" + code);

        return true;
    }

    @Override
    public boolean verifyCode(String phoneNumber, String code) {
        String codeKey = VERIFICATION_CODE_PREFIX + phoneNumber;
        String attemptKey = ATTEMPT_PREFIX + phoneNumber;

        // 检查验证码是否存在
        if (!redisTemplate.hasKey(codeKey)) {
            return false;
        }

        // 获取存储的验证码
        String storedCode = (String) redisTemplate.opsForValue().get(codeKey);

        // 获取并增加尝试次数
        int attempts = redisTemplate.opsForValue().get(attemptKey) != null ? 
                (int) redisTemplate.opsForValue().get(attemptKey) : 0;
        attempts++;
        redisTemplate.opsForValue().set(attemptKey, attempts, Duration.ofSeconds(getCodeTtl(phoneNumber)));

        // 检查尝试次数是否超过限制
        if (attempts > 3) { // 默认最多3次尝试
            clearCode(phoneNumber);
            return false;
        }

        // 验证验证码
        boolean isValid = code.equals(storedCode);
        
        // 验证成功后清除验证码
        if (isValid) {
            clearCode(phoneNumber);
        }

        return isValid;
    }

    @Override
    public boolean canSendCode(String phoneNumber, long coolDownSeconds) {
        String sendKey = SEND_RECORD_PREFIX + phoneNumber;
        
        // 检查是否存在发送记录
        if (!redisTemplate.hasKey(sendKey)) {
            return true;
        }

        // 检查是否在冷却时间内
        long lastSendTime = (long) redisTemplate.opsForValue().get(sendKey);
        long currentTime = System.currentTimeMillis();
        
        return (currentTime - lastSendTime) / 1000 > coolDownSeconds;
    }

    @Override
    public boolean clearCode(String phoneNumber) {
        String codeKey = VERIFICATION_CODE_PREFIX + phoneNumber;
        String attemptKey = ATTEMPT_PREFIX + phoneNumber;

        redisTemplate.delete(codeKey);
        redisTemplate.delete(attemptKey);

        return true;
    }

    @Override
    public long getCodeTtl(String phoneNumber) {
        String codeKey = VERIFICATION_CODE_PREFIX + phoneNumber;
        
        Object result = redisTemplate.getExpire(codeKey);
        if (result instanceof java.time.Duration) {
            java.time.Duration duration = (java.time.Duration) result;
            return duration.getSeconds();
        } else if (result instanceof Long) {
            return (Long) result;
        }
        return -1;
    }

    /**
     * 生成指定长度的随机验证码
     * @param length 验证码长度
     * @return 随机验证码
     */
    private String generateCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
