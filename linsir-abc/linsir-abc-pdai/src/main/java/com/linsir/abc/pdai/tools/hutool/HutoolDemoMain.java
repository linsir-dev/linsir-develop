package com.linsir.abc.pdai.tools.hutool;

/**
 * Hutool 示例代码演示主类
 * 用于运行和验证所有 Hutool 库的示例代码
 */
public class HutoolDemoMain {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("Hutool 库示例代码验证");
        System.out.println("===========================================");
        
        try {
            // 运行 Hutool 核心工具类示例
            System.out.println("\n1. 运行 Hutool 核心工具类示例:");
            System.out.println("-------------------------------------------");
            HutoolCoreDemo.demonstrateStringUtil();
            HutoolCoreDemo.demonstrateDateUtil();
            HutoolCoreDemo.demonstrateNumberUtil();
            HutoolCoreDemo.demonstrateValidateUtil();
            HutoolCoreDemo.demonstrateOtherUtil();
            
            // 运行 Hutool IO 工具类示例
            System.out.println("\n2. 运行 Hutool IO 工具类示例:");
            System.out.println("-------------------------------------------");
            HutoolIODemo.demonstrateFileUtil();
            HutoolIODemo.demonstrateIoUtil();
            HutoolIODemo.demonstrateResourceUtil();
            HutoolIODemo.demonstrateFileWatcher();
            
            // 运行 Hutool HTTP 工具类示例
            System.out.println("\n3. 运行 Hutool HTTP 工具类示例:");
            System.out.println("-------------------------------------------");
            HutoolHttpDemo.demonstrateGetRequest();
            HutoolHttpDemo.demonstratePostRequest();
            HutoolHttpDemo.demonstrateHttpRequest();
            HutoolHttpDemo.demonstrateFileDownload();
            HutoolHttpDemo.demonstrateJsonUtil();
            HutoolHttpDemo.demonstrateHttpSession();
            
            // 运行 Hutool 加密工具类示例
            System.out.println("\n4. 运行 Hutool 加密工具类示例:");
            System.out.println("-------------------------------------------");
            HutoolCryptoDemo.demonstrateDigest();
            HutoolCryptoDemo.demonstrateSymmetric();
            HutoolCryptoDemo.demonstrateAsymmetric();
            HutoolCryptoDemo.demonstrateSecureUtil();
            HutoolCryptoDemo.demonstrateBase64();
            
            System.out.println("\n===========================================");
            System.out.println("Hutool 库示例代码验证完成！");
            System.out.println("===========================================");
            
        } catch (Exception e) {
            System.err.println("运行示例时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
