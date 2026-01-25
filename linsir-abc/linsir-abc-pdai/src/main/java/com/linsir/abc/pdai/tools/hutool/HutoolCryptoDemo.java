package com.linsir.abc.pdai.tools.hutool;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.DES;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

import java.nio.charset.StandardCharsets;

/**
 * Hutool 加密工具类示例
 * 演示 Hutool 提供的加密工具，如 MD5、SHA、AES、RSA 等
 */
public class HutoolCryptoDemo {

    /**
     * 演示摘要加密（MD5、SHA等）
     */
    public static void demonstrateDigest() {
        System.out.println("=== 摘要加密示例 ===");
        
        String content = "Hello, Hutool Crypto!";
        System.out.println("原始内容: " + content);
        
        // MD5 加密
        String md5Hex = DigestUtil.md5Hex(content);
        System.out.println("MD5 加密结果: " + md5Hex);
        
        // SHA-1 加密
        String sha1Hex = DigestUtil.sha1Hex(content);
        System.out.println("SHA-1 加密结果: " + sha1Hex);
        
        // SHA-256 加密
        String sha256Hex = DigestUtil.sha256Hex(content);
        System.out.println("SHA-256 加密结果: " + sha256Hex);
        
        // SHA-512 加密
        String sha512Hex = DigestUtil.sha512Hex(content);
        System.out.println("SHA-512 加密结果: " + sha512Hex);
        
        // 带盐值的 MD5
        String salt = "hutool";
        String md5WithSalt = DigestUtil.md5Hex(content + salt);
        System.out.println("带盐值 MD5 加密结果: " + md5WithSalt);
    }

    /**
     * 演示对称加密（AES、DES等）
     */
    public static void demonstrateSymmetric() {
        System.out.println("\n=== 对称加密示例 ===");
        
        String content = "Hello, Symmetric Crypto!";
        System.out.println("原始内容: " + content);
        
        // AES 加密
        System.out.println("\nAES 加密:");
        SymmetricCrypto aes = new AES();
        String aesEncrypted = aes.encryptHex(content);
        System.out.println("加密结果: " + aesEncrypted);
        String aesDecrypted = aes.decryptStr(aesEncrypted, StandardCharsets.UTF_8);
        System.out.println("解密结果: " + aesDecrypted);
        System.out.println("解密结果与原始内容是否一致: " + content.equals(aesDecrypted));
        
        // 使用自定义密钥的 AES 加密
        System.out.println("\n使用自定义密钥的 AES 加密:");
        String key = "1234567890123456";
        SymmetricCrypto aesWithKey = new AES(key.getBytes());
        String aesWithKeyEncrypted = aesWithKey.encryptHex(content);
        System.out.println("加密结果: " + aesWithKeyEncrypted);
        String aesWithKeyDecrypted = aesWithKey.decryptStr(aesWithKeyEncrypted, StandardCharsets.UTF_8);
        System.out.println("解密结果: " + aesWithKeyDecrypted);
        
        // DES 加密
        System.out.println("\nDES 加密:");
        SymmetricCrypto des = new DES();
        String desEncrypted = des.encryptHex(content);
        System.out.println("加密结果: " + desEncrypted);
        String desDecrypted = des.decryptStr(desEncrypted, StandardCharsets.UTF_8);
        System.out.println("解密结果: " + desDecrypted);
    }

    /**
     * 演示非对称加密（RSA）
     */
    public static void demonstrateAsymmetric() {
        System.out.println("\n=== 非对称加密示例 ===");
        
        String content = "Hello, Asymmetric Crypto!";
        System.out.println("原始内容: " + content);
        
        // 生成 RSA 密钥对
        System.out.println("\n生成 RSA 密钥对:");
        RSA rsa = new RSA();
        
        // 获取公钥和私钥
        String publicKeyBase64 = rsa.getPublicKeyBase64();
        String privateKeyBase64 = rsa.getPrivateKeyBase64();
        
        System.out.println("公钥:");
        System.out.println(publicKeyBase64);
        System.out.println("\n私钥:");
        System.out.println(privateKeyBase64);
        
        // 使用公钥加密
        System.out.println("\n使用公钥加密:");
        String encrypted = rsa.encryptHex(content, KeyType.PublicKey);
        System.out.println("加密结果: " + encrypted);
        
        // 使用私钥解密
        System.out.println("使用私钥解密:");
        String decrypted = rsa.decryptStr(encrypted, KeyType.PrivateKey);
        System.out.println("解密结果: " + decrypted);
        System.out.println("解密结果与原始内容是否一致: " + content.equals(decrypted));
        
        // 使用私钥加密，公钥解密（签名和验证）
        System.out.println("\n使用私钥加密，公钥解密（签名和验证）:");
        String signed = rsa.encryptHex(content, KeyType.PrivateKey);
        System.out.println("签名结果: " + signed);
        String verified = rsa.decryptStr(signed, KeyType.PublicKey);
        System.out.println("验证结果: " + verified);
        System.out.println("验证结果与原始内容是否一致: " + content.equals(verified));
    }

    /**
     * 演示 SecureUtil 工具类
     */
    public static void demonstrateSecureUtil() {
        System.out.println("\n=== SecureUtil 工具类示例 ===");
        
        String content = "Hello, SecureUtil!";
        System.out.println("原始内容: " + content);
        
        // MD5 加密
        String md5 = SecureUtil.md5(content);
        System.out.println("SecureUtil.md5: " + md5);
        
        // SHA-1 加密
        String sha1 = SecureUtil.sha1(content);
        System.out.println("SecureUtil.sha1: " + sha1);
        
        // 生成随机密码
        System.out.println("\n生成随机密码:");
        String randomPwd = cn.hutool.core.util.RandomUtil.randomString(12);
        System.out.println("12位随机密码: " + randomPwd);
        
        // 生成强随机密码（包含大小写字母、数字和特殊字符）
        String strongPwd = cn.hutool.core.util.RandomUtil.randomString(16);
        System.out.println("16位强随机密码: " + strongPwd);
    }

    /**
     * 演示 Base64 编码解码
     */
    public static void demonstrateBase64() {
        System.out.println("\n=== Base64 编码解码示例 ===");
        
        String content = "Hello, Base64!";
        System.out.println("原始内容: " + content);
        
        // Base64 编码
        String base64Encoded = cn.hutool.core.codec.Base64.encode(content);
        System.out.println("Base64 编码结果: " + base64Encoded);
        
        // Base64 解码
        String base64Decoded = cn.hutool.core.codec.Base64.decodeStr(base64Encoded);
        System.out.println("Base64 解码结果: " + base64Decoded);
        System.out.println("解码结果与原始内容是否一致: " + content.equals(base64Decoded));
    }
}
