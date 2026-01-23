package com.linsir.abc.pdai.structure.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 对称加密算法示例代码
 * 
 * 说明：
 * 1. 对称加密算法使用相同的密钥进行加密和解密
 * 2. 常见的对称加密算法包括AES、DES、3DES等
 * 3. 对称加密算法的特点是加密速度快，适合加密大量数据
 */
public class SymmetricEncryption {

    /**
     * 使用AES算法加密
     */
    public String encryptWithAES(String plaintext, String key) throws Exception {
        // 创建AES密钥
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        // 创建Cipher对象
        Cipher cipher = Cipher.getInstance("AES");
        // 初始化Cipher为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        // 加密数据
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
        // 将加密结果转换为Base64字符串
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 使用AES算法解密
     */
    public String decryptWithAES(String ciphertext, String key) throws Exception {
        // 创建AES密钥
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        // 创建Cipher对象
        Cipher cipher = Cipher.getInstance("AES");
        // 初始化为解密模式
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        // 解密数据
        byte[] encryptedBytes = Base64.getDecoder().decode(ciphertext);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        // 将解密结果转换为字符串
        return new String(decryptedBytes);
    }

    /**
     * 生成AES密钥
     */
    public String generateAESKey() throws Exception {
        // 创建KeyGenerator对象
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        // 初始化密钥长度为128位
        keyGenerator.init(128);
        // 生成密钥
        SecretKey secretKey = keyGenerator.generateKey();
        // 将密钥转换为Base64字符串
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 测试对称加密算法
     */
    public void test() throws Exception {
        System.out.println("=== 对称加密算法示例 ===");
        
        // 生成AES密钥
        String key = generateAESKey();
        System.out.println("生成的AES密钥: " + key);
        
        // 测试数据
        String plaintext = "Hello, Symmetric Encryption!";
        System.out.println("原始数据: " + plaintext);
        
        // 使用AES加密
        String encrypted = encryptWithAES(plaintext, key);
        System.out.println("AES加密后: " + encrypted);
        
        // 使用AES解密
        String decrypted = decryptWithAES(encrypted, key);
        System.out.println("AES解密后: " + decrypted);
        
        // 验证解密结果
        System.out.println("解密结果是否正确: " + plaintext.equals(decrypted));
    }
}
