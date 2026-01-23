package com.linsir.abc.pdai.structure.encryption;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import javax.crypto.Cipher;
import java.util.Base64;

/**
 * 非对称加密算法示例代码
 * 
 * 说明：
 * 1. 非对称加密算法使用一对密钥（公钥和私钥）进行加密和解密
 * 2. 公钥用于加密，私钥用于解密
 * 3. 常见的非对称加密算法包括RSA、ECC等
 * 4. 非对称加密算法的特点是安全性高，适合密钥交换和数字签名
 */
public class AsymmetricEncryption {

    private KeyPair keyPair;

    /**
     * 生成RSA密钥对
     */
    public void generateRSAKeyPair() throws Exception {
        // 创建KeyPairGenerator对象
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥长度为2048位
        keyPairGenerator.initialize(2048);
        // 生成密钥对
        keyPair = keyPairGenerator.generateKeyPair();
    }

    /**
     * 使用RSA公钥加密
     */
    public String encryptWithRSA(String plaintext, PublicKey publicKey) throws Exception {
        // 创建Cipher对象
        Cipher cipher = Cipher.getInstance("RSA");
        // 初始化为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // 加密数据
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
        // 将加密结果转换为Base64字符串
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 使用RSA私钥解密
     */
    public String decryptWithRSA(String ciphertext, PrivateKey privateKey) throws Exception {
        // 创建Cipher对象
        Cipher cipher = Cipher.getInstance("RSA");
        // 初始化为解密模式
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        // 解密数据
        byte[] encryptedBytes = Base64.getDecoder().decode(ciphertext);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        // 将解密结果转换为字符串
        return new String(decryptedBytes);
    }

    /**
     * 使用RSA私钥签名
     */
    public String signWithRSA(String data, PrivateKey privateKey) throws Exception {
        // 创建Signature对象
        Signature signature = Signature.getInstance("SHA256withRSA");
        // 初始化为签名模式
        signature.initSign(privateKey);
        // 更新数据
        signature.update(data.getBytes());
        // 生成签名
        byte[] signatureBytes = signature.sign();
        // 将签名转换为Base64字符串
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    /**
     * 使用RSA公钥验证签名
     */
    public boolean verifyWithRSA(String data, String signature, PublicKey publicKey) throws Exception {
        // 创建Signature对象
        Signature sig = Signature.getInstance("SHA256withRSA");
        // 初始化为验证模式
        sig.initVerify(publicKey);
        // 更新数据
        sig.update(data.getBytes());
        // 验证签名
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        return sig.verify(signatureBytes);
    }

    /**
     * 获取公钥
     */
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    /**
     * 获取私钥
     */
    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    /**
     * 获取公钥的Base64字符串
     */
    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    /**
     * 获取私钥的Base64字符串
     */
    public String getPrivateKeyBase64() {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    /**
     * 测试非对称加密算法
     */
    public void test() throws Exception {
        System.out.println("=== 非对称加密算法示例 ===");
        
        // 生成RSA密钥对
        generateRSAKeyPair();
        System.out.println("生成的RSA公钥: " + getPublicKeyBase64());
        System.out.println("生成的RSA私钥: " + getPrivateKeyBase64());
        
        // 测试数据
        String plaintext = "Hello, Asymmetric Encryption!";
        System.out.println("原始数据: " + plaintext);
        
        // 使用RSA公钥加密
        String encrypted = encryptWithRSA(plaintext, getPublicKey());
        System.out.println("RSA加密后: " + encrypted);
        
        // 使用RSA私钥解密
        String decrypted = decryptWithRSA(encrypted, getPrivateKey());
        System.out.println("RSA解密后: " + decrypted);
        
        // 验证解密结果
        System.out.println("解密结果是否正确: " + plaintext.equals(decrypted));
        
        // 使用RSA私钥签名
        String signature = signWithRSA(plaintext, getPrivateKey());
        System.out.println("RSA签名: " + signature);
        
        // 使用RSA公钥验证签名
        boolean verified = verifyWithRSA(plaintext, signature, getPublicKey());
        System.out.println("RSA签名验证结果: " + verified);
    }
}
