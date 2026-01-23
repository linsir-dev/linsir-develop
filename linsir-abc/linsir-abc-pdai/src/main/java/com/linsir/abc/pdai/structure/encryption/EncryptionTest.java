package com.linsir.abc.pdai.structure.encryption;

/**
 * 加密算法测试类
 * 
 * 说明：
 * 1. 测试所有加密算法的正确性
 * 2. 包括对称加密、非对称加密、哈希函数、Bouncy Castle、Google Tink、Jasypt等
 */
public class EncryptionTest {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("加密算法测试");
        System.out.println("========================================");
        
        try {
            // 测试对称加密算法
            SymmetricEncryption symmetricEncryption = new SymmetricEncryption();
            symmetricEncryption.test();
            System.out.println();
            
            // 测试非对称加密算法
            AsymmetricEncryption asymmetricEncryption = new AsymmetricEncryption();
            asymmetricEncryption.test();
            System.out.println();
            
            // 测试哈希函数
            HashFunction hashFunction = new HashFunction();
            hashFunction.test();
            System.out.println();
            
            // 测试Bouncy Castle加密库
            BouncyCastleExample bouncyCastleExample = new BouncyCastleExample();
            bouncyCastleExample.test();
            System.out.println();
            
            // 测试Google Tink加密库
            GoogleTinkExample googleTinkExample = new GoogleTinkExample();
            googleTinkExample.test();
            System.out.println();
            
            // 测试Jasypt加密库
            JasyptExample jasyptExample = new JasyptExample();
            jasyptExample.test();
            System.out.println();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("========================================");
        System.out.println("所有加密算法测试完成");
        System.out.println("========================================");
    }
}
