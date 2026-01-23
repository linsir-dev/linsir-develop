package com.linsir.abc.pdai.structure.encryption;

import org.jasypt.util.text.BasicTextEncryptor;
import org.jasypt.util.text.StrongTextEncryptor;

/**
 * Jasypt加密库示例代码
 * 
 * 说明：
 * 1. Jasypt是一个简单的加密库，适用于配置文件加密等场景
 * 2. Jasypt提供了简单的API，如BasicTextEncryptor和StrongTextEncryptor
 * 3. Jasypt支持的算法包括：PBEWithMD5AndDES、PBEWithSHA1AndDESede等
 * 4. Jasypt的设计目标是简单易用，适合快速实现加密功能
 */
public class JasyptExample {

    private BasicTextEncryptor basicTextEncryptor;
    private StrongTextEncryptor strongTextEncryptor;

    /**
     * 初始化Jasypt加密器
     */
    public void initialize(String password) {
        // 初始化BasicTextEncryptor
        basicTextEncryptor = new BasicTextEncryptor();
        basicTextEncryptor.setPassword(password);
        
        // 初始化StrongTextEncryptor
        strongTextEncryptor = new StrongTextEncryptor();
        strongTextEncryptor.setPassword(password);
    }

    /**
     * 使用BasicTextEncryptor加密
     */
    public String encryptWithBasic(String plaintext) {
        return basicTextEncryptor.encrypt(plaintext);
    }

    /**
     * 使用BasicTextEncryptor解密
     */
    public String decryptWithBasic(String ciphertext) {
        return basicTextEncryptor.decrypt(ciphertext);
    }

    /**
     * 使用StrongTextEncryptor加密
     */
    public String encryptWithStrong(String plaintext) {
        return strongTextEncryptor.encrypt(plaintext);
    }

    /**
     * 使用StrongTextEncryptor解密
     */
    public String decryptWithStrong(String ciphertext) {
        return strongTextEncryptor.decrypt(ciphertext);
    }

    /**
     * 测试Jasypt加密库
     */
    public void test() {
        System.out.println("=== Jasypt加密库示例 ===");
        
        // 初始化Jasypt加密器
        String password = "my-secret-password";
        initialize(password);
        System.out.println("使用的密码: " + password);
        
        // 测试数据
        String plaintext = "Hello, Jasypt!";
        System.out.println("原始数据: " + plaintext);
        
        // 使用BasicTextEncryptor加密
        String basicEncrypted = encryptWithBasic(plaintext);
        System.out.println("BasicTextEncryptor加密后: " + basicEncrypted);
        
        // 使用BasicTextEncryptor解密
        String basicDecrypted = decryptWithBasic(basicEncrypted);
        System.out.println("BasicTextEncryptor解密后: " + basicDecrypted);
        
        // 验证解密结果
        System.out.println("BasicTextEncryptor解密结果是否正确: " + plaintext.equals(basicDecrypted));
        
        // 使用StrongTextEncryptor加密
        String strongEncrypted = encryptWithStrong(plaintext);
        System.out.println("StrongTextEncryptor加密后: " + strongEncrypted);
        
        // 使用StrongTextEncryptor解密
        String strongDecrypted = decryptWithStrong(strongEncrypted);
        System.out.println("StrongTextEncryptor解密后: " + strongDecrypted);
        
        // 验证解密结果
        System.out.println("StrongTextEncryptor解密结果是否正确: " + plaintext.equals(strongDecrypted));
    }
}
