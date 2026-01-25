package com.linsir.abc.pdai.tools.spring;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Spring ResourceLoader 工具类示例
 * 演示 ResourceLoader 的常用方法
 */
public class SpringResourceLoaderDemo {

    /**
     * 演示 ResourceLoader 的常用方法
     */
    public static void demonstrateResourceLoader() {
        // 1. 创建资源加载器
        System.out.println("=== 创建资源加载器 ===");
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        System.out.println("创建 ResourceLoader: " + resourceLoader.getClass().getName());
        
        // 2. 加载类路径资源
        System.out.println("\n=== 加载类路径资源 ===");
        try {
            // 尝试加载 classpath 资源
            Resource classPathResource = resourceLoader.getResource("classpath:application.properties");
            System.out.println("加载类路径资源: classpath:application.properties");
            System.out.println("资源存在: " + classPathResource.exists());
            
            if (classPathResource.exists()) {
                System.out.println("资源 URL: " + classPathResource.getURL());
                System.out.println("资源文件: " + classPathResource.getFile());
                
                // 读取资源内容
                try (InputStream is = classPathResource.getInputStream()) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        baos.write(buffer, 0, length);
                    }
                    byte[] bytes = baos.toByteArray();
                    String content = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("资源内容:");
                    System.out.println(content);
                }
            } else {
                System.out.println("注意: 资源不存在，这是正常的，因为我们没有创建 application.properties 文件");
            }
        } catch (IOException e) {
            System.err.println("加载类路径资源时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 3. 加载文件系统资源
        System.out.println("\n=== 加载文件系统资源 ===");
        try {
            // 创建临时文件路径
            String tempDir = System.getProperty("java.io.tmpdir");
            String testFile = tempDir + "/test.txt";
            
            // 创建测试文件
            java.io.File file = new java.io.File(testFile);
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                fos.write("Hello from file system resource!".getBytes(StandardCharsets.UTF_8));
            }
            System.out.println("创建测试文件: " + testFile);
            
            // 加载文件系统资源
            Resource fileResource = resourceLoader.getResource("file:" + testFile);
            System.out.println("加载文件系统资源: file:" + testFile);
            System.out.println("资源存在: " + fileResource.exists());
            
            if (fileResource.exists()) {
                System.out.println("资源 URL: " + fileResource.getURL());
                System.out.println("资源文件: " + fileResource.getFile());
                
                // 读取资源内容
                try (InputStream is = fileResource.getInputStream()) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        baos.write(buffer, 0, length);
                    }
                    byte[] bytes = baos.toByteArray();
                    String content = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("资源内容: " + content);
                }
            }
            
            // 删除测试文件
            file.delete();
            System.out.println("删除测试文件: " + testFile);
            
        } catch (IOException e) {
            System.err.println("加载文件系统资源时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 4. 加载 URL 资源
        System.out.println("\n=== 加载 URL 资源 ===");
        try {
            // 加载 URL 资源
            Resource urlResource = resourceLoader.getResource("https://www.springframework.org");
            System.out.println("加载 URL 资源: https://www.springframework.org");
            System.out.println("资源存在: " + urlResource.exists());
            
            if (urlResource.exists()) {
                System.out.println("资源 URL: " + urlResource.getURL());
                
                // 读取资源内容（仅读取前 500 个字符）
                try (InputStream is = urlResource.getInputStream()) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    int totalRead = 0;
                    while ((length = is.read(buffer)) > 0 && totalRead < 500) {
                        int bytesToWrite = Math.min(length, 500 - totalRead);
                        baos.write(buffer, 0, bytesToWrite);
                        totalRead += bytesToWrite;
                    }
                    byte[] bytes = baos.toByteArray();
                    String content = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("资源内容（前 500 个字符）:");
                    System.out.println(content + "...");
                }
            }
        } catch (IOException e) {
            System.err.println("加载 URL 资源时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 5. 加载相对路径资源
        System.out.println("\n=== 加载相对路径资源 ===");
        try {
            // 加载相对路径资源
            Resource relativeResource = resourceLoader.getResource("./");
            System.out.println("加载相对路径资源: ./");
            System.out.println("资源存在: " + relativeResource.exists());
            
            if (relativeResource.exists()) {
                System.out.println("资源 URL: " + relativeResource.getURL());
                System.out.println("资源文件: " + relativeResource.getFile());
            }
        } catch (IOException e) {
            System.err.println("加载相对路径资源时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 6. 资源类型判断
        System.out.println("\n=== 资源类型判断 ===");
        Resource classPathRes = resourceLoader.getResource("classpath:");
        Resource fileRes = resourceLoader.getResource("file:");
        Resource urlRes = resourceLoader.getResource("https://example.com");
        
        System.out.println("classpath: 资源类型: " + classPathRes.getClass().getName());
        System.out.println("file: 资源类型: " + fileRes.getClass().getName());
        System.out.println("https: 资源类型: " + urlRes.getClass().getName());
    }
}
