package com.linsir.abc.pdai.tools.apacheCommons;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;

/**
 * Apache Commons IO 示例代码
 * commons-io 提供了丰富的IO操作工具类
 */
public class CommonsIODemo {

    private static final String TEST_FILE_PATH = "test-file.txt";
    private static final String TEST_DIR_PATH = "test-dir";

    /**
     * 演示 FileUtils 工具类的使用
     */
    public static void demonstrateFileUtils() {
        System.out.println("=== FileUtils 示例 ===");
        
        try {
            // 创建测试文件
            File testFile = new File(TEST_FILE_PATH);
            FileUtils.writeStringToFile(testFile, "Hello, Apache Commons IO!\nThis is a test file.", StandardCharsets.UTF_8);
            System.out.println("FileUtils.writeStringToFile: 文件创建成功");
            
            // 读取文件内容
            String content = FileUtils.readFileToString(testFile, StandardCharsets.UTF_8);
            System.out.println("FileUtils.readFileToString: " + content);
            
            // 复制文件
            File destFile = new File("test-file-copy.txt");
            FileUtils.copyFile(testFile, destFile);
            System.out.println("FileUtils.copyFile: 文件复制成功");
            
            // 创建目录
            File testDir = new File(TEST_DIR_PATH);
            FileUtils.forceMkdir(testDir);
            System.out.println("FileUtils.forceMkdir: 目录创建成功");
            
            // 复制文件到目录
            FileUtils.copyFileToDirectory(testFile, testDir);
            System.out.println("FileUtils.copyFileToDirectory: 文件复制到目录成功");
            
            // 列出目录中的文件
            Collection<File> files = FileUtils.listFiles(testDir, FileFilterUtils.fileFileFilter(), null);
            System.out.println("FileUtils.listFiles: " + files);
            
            // 删除文件和目录
            FileUtils.deleteQuietly(testFile);
            FileUtils.deleteQuietly(destFile);
            FileUtils.deleteDirectory(testDir);
            System.out.println("FileUtils.deleteQuietly: 文件和目录删除成功");
            
        } catch (IOException e) {
            System.err.println("FileUtils 操作异常: " + e.getMessage());
        }
    }

    /**
     * 演示 IOUtils 工具类的使用
     */
    public static void demonstrateIOUtils() {
        System.out.println("\n=== IOUtils 示例 ===");
        
        try {
            // 字符串转输入流
            String content = "Hello, IOUtils!";
            InputStream inputStream = IOUtils.toInputStream(content, StandardCharsets.UTF_8);
            
            // 输入流转字符串
            String readContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            System.out.println("IOUtils.toString: " + readContent);
            
            // 输入流复制到输出流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, outputStream);
            
            // 关闭流
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            System.out.println("IOUtils.closeQuietly: 流关闭成功");
            
        } catch (IOException e) {
            System.err.println("IOUtils 操作异常: " + e.getMessage());
        }
    }

    /**
     * 演示 LineIterator 工具类的使用
     */
    public static void demonstrateLineIterator() {
        System.out.println("\n=== LineIterator 示例 ===");
        
        try {
            // 创建测试文件
            File testFile = new File(TEST_FILE_PATH);
            FileUtils.writeStringToFile(testFile, "Line 1\nLine 2\nLine 3\nLine 4\nLine 5", StandardCharsets.UTF_8);
            
            // 使用 LineIterator 逐行读取
            LineIterator iterator = FileUtils.lineIterator(testFile, StandardCharsets.UTF_8.name());
            int lineCount = 0;
            
            while (iterator.hasNext()) {
                String line = iterator.nextLine();
                lineCount++;
                System.out.println("Line " + lineCount + ": " + line);
            }
            
            // 关闭迭代器
            LineIterator.closeQuietly(iterator);
            
            // 删除测试文件
            FileUtils.deleteQuietly(testFile);
            
        } catch (IOException e) {
            System.err.println("LineIterator 操作异常: " + e.getMessage());
        }
    }

    /**
     * 演示 FilenameUtils 工具类的使用
     */
    public static void demonstrateFilenameUtils() {
        System.out.println("\n=== FilenameUtils 示例 ===");
        
        String filePath = "C:\\Users\\linsir\\Documents\\test-file.txt";
        
        // 文件名操作
        System.out.println("FilenameUtils.getName(filePath): " + FilenameUtils.getName(filePath));
        System.out.println("FilenameUtils.getBaseName(filePath): " + FilenameUtils.getBaseName(filePath));
        System.out.println("FilenameUtils.getExtension(filePath): " + FilenameUtils.getExtension(filePath));
        System.out.println("FilenameUtils.getPath(filePath): " + FilenameUtils.getPath(filePath));
        System.out.println("FilenameUtils.getFullPath(filePath): " + FilenameUtils.getFullPath(filePath));
        
        // 路径操作
        String path1 = "C:\\Users\\linsir\\Documents";
        String path2 = "..\\Downloads\\file.txt";
        String normalizedPath = FilenameUtils.normalize(FilenameUtils.concat(path1, path2));
        System.out.println("FilenameUtils.normalize: " + normalizedPath);
        
        // 路径比较
        String path3 = "C:\\Users\\linsir\\Documents\\file.txt";
        String path4 = "c:/users/linsir/documents/file.txt";
        System.out.println("FilenameUtils.equals(path3, path4): " + FilenameUtils.equals(path3, path4));
        System.out.println("FilenameUtils.equalsNormalized(path3, path4): " + FilenameUtils.equalsNormalized(path3, path4));
    }

    /**
     * 演示 FileFilterUtils 工具类的使用
     */
    public static void demonstrateFileFilterUtils() {
        System.out.println("\n=== FileFilterUtils 示例 ===");
        
        try {
            // 创建测试目录结构
            File testDir = new File(TEST_DIR_PATH);
            FileUtils.forceMkdir(testDir);
            
            // 创建测试文件
            FileUtils.writeStringToFile(new File(testDir, "file1.txt"), "content1", StandardCharsets.UTF_8);
            FileUtils.writeStringToFile(new File(testDir, "file2.java"), "content2", StandardCharsets.UTF_8);
            FileUtils.writeStringToFile(new File(testDir, "file3.txt"), "content3", StandardCharsets.UTF_8);
            
            // 创建子目录
            File subDir = new File(testDir, "subdir");
            FileUtils.forceMkdir(subDir);
            FileUtils.writeStringToFile(new File(subDir, "file4.txt"), "content4", StandardCharsets.UTF_8);
            
            // 使用过滤器列出 .txt 文件
            IOFileFilter txtFileFilter = FileFilterUtils.suffixFileFilter(".txt");
            Collection<File> txtFiles = FileUtils.listFiles(testDir, txtFileFilter, FileFilterUtils.directoryFileFilter());
            System.out.println("FileFilterUtils.suffixFileFilter('.txt'): " + txtFiles);
            
            // 使用逻辑过滤器
            IOFileFilter javaFileFilter = FileFilterUtils.suffixFileFilter(".java");
            IOFileFilter combinedFilter = FileFilterUtils.or(txtFileFilter, javaFileFilter);
            Collection<File> combinedFiles = FileUtils.listFiles(testDir, combinedFilter, FileFilterUtils.directoryFileFilter());
            System.out.println("FileFilterUtils.or(txtFileFilter, javaFileFilter): " + combinedFiles);
            
            // 清理
            FileUtils.deleteDirectory(testDir);
            System.out.println("清理完成");
            
        } catch (IOException e) {
            System.err.println("FileFilterUtils 操作异常: " + e.getMessage());
        }
    }
}
