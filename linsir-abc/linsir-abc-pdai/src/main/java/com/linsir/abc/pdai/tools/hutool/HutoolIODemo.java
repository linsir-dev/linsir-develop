package com.linsir.abc.pdai.tools.hutool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.CharsetUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Hutool IO 工具类示例
 * 演示 Hutool 提供的 IO 操作工具，如文件、流等
 */
public class HutoolIODemo {

    /**
     * 演示文件工具类
     */
    public static void demonstrateFileUtil() {
        System.out.println("=== 文件工具类示例 ===");
        
        // 文件路径
        String basePath = System.getProperty("user.dir") + "/target";
        String testDir = basePath + "/test_hutool";
        String testFile = testDir + "/test.txt";
        String copyFile = testDir + "/test_copy.txt";
        
        // 创建目录
        System.out.println("创建目录: " + testDir);
        FileUtil.mkdir(testDir);
        
        // 写入文件
        System.out.println("写入文件: " + testFile);
        FileUtil.writeString("Hello, Hutool IO!\n这是一个测试文件。", testFile, StandardCharsets.UTF_8);
        
        // 读取文件
        System.out.println("读取文件内容:");
        String content = FileUtil.readString(testFile, StandardCharsets.UTF_8);
        System.out.println(content);
        
        // 复制文件
        System.out.println("复制文件到: " + copyFile);
        FileUtil.copy(testFile, copyFile, true);
        
        // 读取复制后的文件
        System.out.println("读取复制后的文件内容:");
        String copyContent = FileUtil.readString(copyFile, StandardCharsets.UTF_8);
        System.out.println(copyContent);
        
        // 文件信息
        System.out.println("\n文件信息:");
        System.out.println("文件是否存在: " + FileUtil.exist(testFile));
        System.out.println("文件大小: " + FileUtil.size(new File(testFile)) + " 字节");
        System.out.println("文件绝对路径: " + FileUtil.getAbsolutePath(testFile));
        System.out.println("文件名: " + FileUtil.getName(testFile));
        System.out.println("文件扩展名: " + FileUtil.getSuffix(testFile));
        
        // 列出目录下的文件
        System.out.println("\n目录下的文件:");
        File[] files = FileUtil.ls(testDir);
        if (files != null) {
            for (File file : files) {
                System.out.println(file.getName() + " (" + FileUtil.size(file) + " 字节)");
            }
        }
        
        // 删除文件
        System.out.println("\n删除文件:");
        FileUtil.del(testFile);
        FileUtil.del(copyFile);
        FileUtil.del(testDir);
        System.out.println("文件删除后是否存在: " + FileUtil.exist(testFile));
    }

    /**
     * 演示流工具类
     */
    public static void demonstrateIoUtil() {
        System.out.println("\n=== 流工具类示例 ===");
        
        // 字节数组转输入流
        byte[] bytes = "Hello, IoUtil!".getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = IoUtil.toStream(bytes);
        
        // 输入流转字节数组
        byte[] readBytes = IoUtil.readBytes(inputStream);
        System.out.println("输入流转字节数组: " + new String(readBytes, StandardCharsets.UTF_8));
        
        // 输入流转字符串
        inputStream = IoUtil.toStream(bytes);
        String str = IoUtil.read(inputStream, StandardCharsets.UTF_8);
        System.out.println("输入流转字符串: " + str);
        
        // 关闭流
        IoUtil.close(inputStream);
        
        // 演示 try-with-resources 风格
        System.out.println("\n演示 try-with-resources 风格:");
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write("Hello, Try-With-Resources!".getBytes(StandardCharsets.UTF_8));
            System.out.println("输出流内容: " + outputStream.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 演示资源工具类
     */
    public static void demonstrateResourceUtil() {
        System.out.println("\n=== 资源工具类示例 ===");
        
        // 读取 ClassPath 下的资源
        System.out.println("读取 ClassPath 下的资源:");
        try {
            String resourceContent = ResourceUtil.readUtf8Str("logback.xml");
            if (resourceContent != null) {
                System.out.println("logback.xml 资源内容长度: " + resourceContent.length() + " 字符");
                // 打印前 500 个字符
                if (resourceContent.length() > 500) {
                    System.out.println("前 500 个字符:");
                    System.out.println(resourceContent.substring(0, 500) + "...");
                } else {
                    System.out.println(resourceContent);
                }
            } else {
                System.out.println("未找到 logback.xml 资源");
            }
        } catch (Exception e) {
            System.out.println("未找到 logback.xml 资源: " + e.getMessage());
        }
        
        // 检查资源是否存在
        System.out.println("\n检查资源是否存在:");
        System.out.println("logback.xml 是否存在: " + (ResourceUtil.getResource("logback.xml") != null));
        System.out.println("不存在的资源是否存在: " + (ResourceUtil.getResource("non_existent.txt") != null));
    }

    /**
     * 演示文件监视器
     */
    public static void demonstrateFileWatcher() {
        System.out.println("\n=== 文件监视器示例 ===");
        
        // 创建临时目录和文件
        String tempDir = System.getProperty("user.dir") + "/target/temp_watcher";
        String tempFile = tempDir + "/watch.txt";
        
        FileUtil.mkdir(tempDir);
        FileUtil.writeString("初始内容", tempFile, StandardCharsets.UTF_8);
        
        System.out.println("创建文件监视器，监控目录: " + tempDir);
        System.out.println("修改文件内容以触发监视器...");
        
        // 创建文件监视器
        cn.hutool.core.io.watch.WatchMonitor watchMonitor = cn.hutool.core.io.watch.WatchMonitor.createAll(tempDir, new cn.hutool.core.io.watch.Watcher() {
            @Override
            public void onCreate(java.nio.file.WatchEvent<?> event, java.nio.file.Path currentPath) {
                System.out.println("文件创建事件: " + currentPath);
            }
            
            @Override
            public void onModify(java.nio.file.WatchEvent<?> event, java.nio.file.Path currentPath) {
                System.out.println("文件修改事件: " + currentPath);
                String content = FileUtil.readString(currentPath.toFile(), StandardCharsets.UTF_8);
                System.out.println("修改后的内容: " + content);
            }
            
            @Override
            public void onDelete(java.nio.file.WatchEvent<?> event, java.nio.file.Path currentPath) {
                System.out.println("文件删除事件: " + currentPath);
            }
            
            @Override
            public void onOverflow(java.nio.file.WatchEvent<?> event, java.nio.file.Path currentPath) {
                System.out.println("文件事件溢出: " + currentPath);
            }
        });
        
        // 启动监视器
        watchMonitor.setDaemon(true);
        watchMonitor.start();
        
        // 模拟修改文件
        try {
            Thread.sleep(1000);
            System.out.println("\n模拟修改文件...");
            FileUtil.writeString("修改后的内容", tempFile, StandardCharsets.UTF_8);
            Thread.sleep(1000);
            
            // 模拟创建新文件
            System.out.println("\n模拟创建新文件...");
            String newFile = tempDir + "/new_file.txt";
            FileUtil.writeString("新文件内容", newFile, StandardCharsets.UTF_8);
            Thread.sleep(1000);
            
            // 模拟删除文件
            System.out.println("\n模拟删除文件...");
            FileUtil.del(newFile);
            Thread.sleep(1000);
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                // 停止监视器
                watchMonitor.close();
                // 清理临时文件
                Thread.sleep(500); // 等待监视器完全停止
                FileUtil.del(tempFile);
                FileUtil.del(tempDir);
                System.out.println("\n文件监视器已停止，临时文件已清理");
            } catch (Exception e) {
                System.out.println("清理临时文件时出现错误: " + e.getMessage());
            }
        }
    }
}
