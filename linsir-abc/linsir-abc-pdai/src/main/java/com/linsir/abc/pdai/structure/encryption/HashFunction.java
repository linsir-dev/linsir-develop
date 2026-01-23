package com.linsir.abc.pdai.structure.encryption;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * 哈希函数示例代码
 * 
 * 说明：
 * 1. 哈希函数将任意长度的数据映射为固定长度的哈希值
 * 2. 哈希函数的特点是单向性，即从哈希值无法推导出原始数据
 * 3. 常见的哈希函数包括SHA-256、MD5等
 * 4. 哈希函数适合数据完整性校验和密码存储
 */
public class HashFunction {

    /**
     * 使用SHA-256计算哈希值
     */
    public String calculateSHA256(String data) throws Exception {
        // 创建MessageDigest对象
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        // 更新数据
        messageDigest.update(data.getBytes());
        // 计算哈希值
        byte[] hashBytes = messageDigest.digest();
        // 将哈希值转换为Base64字符串
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    /**
     * 使用SHA-512计算哈希值
     */
    public String calculateSHA512(String data) throws Exception {
        // 创建MessageDigest对象
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        // 更新数据
        messageDigest.update(data.getBytes());
        // 计算哈希值
        byte[] hashBytes = messageDigest.digest();
        // 将哈希值转换为Base64字符串
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    /**
     * 使用MD5计算哈希值
     */
    public String calculateMD5(String data) throws Exception {
        // 创建MessageDigest对象
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        // 更新数据
        messageDigest.update(data.getBytes());
        // 计算哈希值
        byte[] hashBytes = messageDigest.digest();
        // 将哈希值转换为Base64字符串
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    /**
     * 计算文件的哈希值（模拟）
     */
    public String calculateFileHash(String fileName, String algorithm) throws Exception {
        // 模拟文件内容
        String fileContent = "This is a simulated file content for " + fileName;
        // 根据算法计算哈希值
        if ("SHA-256".equals(algorithm)) {
            return calculateSHA256(fileContent);
        } else if ("SHA-512".equals(algorithm)) {
            return calculateSHA512(fileContent);
        } else if ("MD5".equals(algorithm)) {
            return calculateMD5(fileContent);
        } else {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
    }

    /**
     * 测试哈希函数
     */
    public void test() throws Exception {
        System.out.println("=== 哈希函数示例 ===");
        
        // 测试数据
        String testData = "Hello, Hash Function!";
        System.out.println("原始数据: " + testData);
        
        // 使用SHA-256计算哈希值
        String sha256Hash = calculateSHA256(testData);
        System.out.println("SHA-256哈希值: " + sha256Hash);
        
        // 使用SHA-512计算哈希值
        String sha512Hash = calculateSHA512(testData);
        System.out.println("SHA-512哈希值: " + sha512Hash);
        
        // 使用MD5计算哈希值
        String md5Hash = calculateMD5(testData);
        System.out.println("MD5哈希值: " + md5Hash);
        
        // 测试文件哈希
        String fileName = "test.txt";
        String fileHash = calculateFileHash(fileName, "SHA-256");
        System.out.println("文件 " + fileName + " 的SHA-256哈希值: " + fileHash);
        
        // 测试哈希函数的单向性
        System.out.println("\n哈希函数的单向性测试:");
        System.out.println("相同数据的哈希值是否相同: " + calculateSHA256(testData).equals(calculateSHA256(testData)));
        
        // 测试不同数据的哈希值
        String differentData = "Hello, Different Data!";
        System.out.println("不同数据的哈希值是否不同: " + !calculateSHA256(testData).equals(calculateSHA256(differentData)));
    }
}
