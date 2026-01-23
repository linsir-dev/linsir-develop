package com.linsir.abc.pdai.structure.encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Base64;

/**
 * Bouncy Castle加密库示例代码
 * 
 * 说明：
 * 1. Bouncy Castle是一个广泛使用的加密库，提供了Java标准库中没有的许多加密算法
 * 2. Bouncy Castle支持的算法包括：AES、DES、3DES、Blowfish、Twofish、RSA、ECC等
 * 3. Bouncy Castle还提供了一些特殊的算法，如Camellia、Serpent等
 */
public class BouncyCastleExample {

    static {
        // 添加Bouncy Castle作为安全提供者
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 使用Blowfish算法加密
     */
    public String encryptWithBlowfish(String plaintext, String key) throws Exception {
        // 创建Blowfish密钥
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "Blowfish");
        // 创建Cipher对象，指定使用Bouncy Castle提供者
        Cipher cipher = Cipher.getInstance("Blowfish", "BC");
        // 初始化为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        // 加密数据
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
        // 将加密结果转换为Base64字符串
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 使用Blowfish算法解密
     */
    public String decryptWithBlowfish(String ciphertext, String key) throws Exception {
        // 创建Blowfish密钥
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "Blowfish");
        // 创建Cipher对象，指定使用Bouncy Castle提供者
        Cipher cipher = Cipher.getInstance("Blowfish", "BC");
        // 初始化为解密模式
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        // 解密数据
        byte[] encryptedBytes = Base64.getDecoder().decode(ciphertext);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        // 将解密结果转换为字符串
        return new String(decryptedBytes);
    }

    /**
     * 生成Blowfish密钥
     */
    public String generateBlowfishKey() throws Exception {
        // 创建KeyGenerator对象，指定使用Bouncy Castle提供者
        KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish", "BC");
        // 初始化密钥长度为128位
        keyGenerator.init(128);
        // 生成密钥
        SecretKey secretKey = keyGenerator.generateKey();
        // 将密钥转换为Base64字符串
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 使用Twofish算法加密
     */
    public String encryptWithTwofish(String plaintext, String key) throws Exception {
        // 创建Twofish密钥
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "Twofish");
        // 创建Cipher对象，指定使用Bouncy Castle提供者
        Cipher cipher = Cipher.getInstance("Twofish", "BC");
        // 初始化为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        // 加密数据
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
        // 将加密结果转换为Base64字符串
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 使用Twofish算法解密
     */
    public String decryptWithTwofish(String ciphertext, String key) throws Exception {
        // 创建Twofish密钥
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "Twofish");
        // 创建Cipher对象，指定使用Bouncy Castle提供者
        Cipher cipher = Cipher.getInstance("Twofish", "BC");
        // 初始化为解密模式
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        // 解密数据
        byte[] encryptedBytes = Base64.getDecoder().decode(ciphertext);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        // 将解密结果转换为字符串
        return new String(decryptedBytes);
    }

    /**
     * 测试Bouncy Castle加密库
     */
    public void test() throws Exception {
        System.out.println("=== Bouncy Castle加密库示例 ===");
        
        // 生成Blowfish密钥
        String blowfishKey = generateBlowfishKey();
        System.out.println("生成的Blowfish密钥: " + blowfishKey);
        
        // 测试数据
        String plaintext = "Hello, Bouncy Castle!";
        System.out.println("原始数据: " + plaintext);
        
        // 使用Blowfish加密
        String blowfishEncrypted = encryptWithBlowfish(plaintext, blowfishKey);
        System.out.println("Blowfish加密后: " + blowfishEncrypted);
        
        // 使用Blowfish解密
        String blowfishDecrypted = decryptWithBlowfish(blowfishEncrypted, blowfishKey);
        System.out.println("Blowfish解密后: " + blowfishDecrypted);
        
        // 验证解密结果
        System.out.println("Blowfish解密结果是否正确: " + plaintext.equals(blowfishDecrypted));
        
        // 使用Twofish加密
        String twofishEncrypted = encryptWithTwofish(plaintext, blowfishKey);
        System.out.println("Twofish加密后: " + twofishEncrypted);
        
        // 使用Twofish解密
        String twofishDecrypted = decryptWithTwofish(twofishEncrypted, blowfishKey);
        System.out.println("Twofish解密后: " + twofishDecrypted);
        
        // 验证解密结果
        System.out.println("Twofish解密结果是否正确: " + plaintext.equals(twofishDecrypted));
    }
}
