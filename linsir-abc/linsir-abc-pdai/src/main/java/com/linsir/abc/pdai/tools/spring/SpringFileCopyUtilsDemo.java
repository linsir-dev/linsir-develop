package com.linsir.abc.pdai.tools.spring;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Spring FileCopyUtils 工具类示例
 * 演示文件复制操作的常用方法
 */
public class SpringFileCopyUtilsDemo {

    /**
     * 演示文件复制操作的常用方法
     */
    public static void demonstrateFileCopyUtils() {
        // 创建临时文件路径
        String tempDir = System.getProperty("java.io.tmpdir");
        String sourceFile = tempDir + File.separator + "source.txt";
        String targetFile = tempDir + File.separator + "target.txt";
        String outputFile = tempDir + File.separator + "output.txt";
        
        try {
            // 1. 准备测试文件
            System.out.println("=== 准备测试文件 ===");
            String content = "Hello, Spring File Operations!\nThis is a test file.\nTesting file copy operations.";
            
            // 写入测试文件
            try (FileOutputStream fos = new FileOutputStream(sourceFile)) {
                fos.write(content.getBytes(StandardCharsets.UTF_8));
            }
            System.out.println("创建测试文件: " + sourceFile);
            System.out.println("文件内容:");
            System.out.println(content);
            
            // 2. 字节数组复制到文件
            System.out.println("\n=== 字节数组复制到文件 ===");
            byte[] bytes = "Hello from byte array!".getBytes(StandardCharsets.UTF_8);
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(bytes);
            }
            System.out.println("字节数组复制到文件: " + outputFile);
            
            // 读取文件内容验证
            String fileContent = new String(readFileToByteArray(new File(outputFile)), StandardCharsets.UTF_8);
            System.out.println("文件内容: " + fileContent);
            
            // 3. 文件复制到文件
            System.out.println("\n=== 文件复制到文件 ===");
            try (FileInputStream fis = new FileInputStream(sourceFile);
                 FileOutputStream fos = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            }
            System.out.println("文件复制: " + sourceFile + " -> " + targetFile);
            
            // 读取目标文件内容验证
            String targetContent = new String(readFileToByteArray(new File(targetFile)), StandardCharsets.UTF_8);
            System.out.println("目标文件内容:");
            System.out.println(targetContent);
            
            // 4. 输入流复制到输出流
            System.out.println("\n=== 输入流复制到输出流 ===");
            String streamOutputFile = tempDir + File.separator + "stream_output.txt";
            
            try (FileInputStream fis = new FileInputStream(sourceFile);
                 FileOutputStream fos = new FileOutputStream(streamOutputFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            }
            System.out.println("输入流复制到输出流: " + sourceFile + " -> " + streamOutputFile);
            
            // 读取流输出文件内容验证
            String streamContent = new String(readFileToByteArray(new File(streamOutputFile)), StandardCharsets.UTF_8);
            System.out.println("流输出文件内容:");
            System.out.println(streamContent);
            
            // 5. 字符串复制到 Writer
            System.out.println("\n=== 字符串复制到 Writer ===");
            String writerOutputFile = tempDir + File.separator + "writer_output.txt";
            
            try (FileWriter fw = new FileWriter(writerOutputFile)) {
                fw.write("Hello from String to Writer!");
            }
            System.out.println("字符串复制到 Writer: " + writerOutputFile);
            
            // 读取 Writer 输出文件内容验证
            String writerContent = new String(readFileToByteArray(new File(writerOutputFile)), StandardCharsets.UTF_8);
            System.out.println("Writer 输出文件内容: " + writerContent);
            
            // 6. 文件复制到字节数组
            System.out.println("\n=== 文件复制到字节数组 ===");
            byte[] fileBytes = readFileToByteArray(new File(sourceFile));
            System.out.println("文件复制到字节数组成功");
            System.out.println("字节数组长度: " + fileBytes.length);
            System.out.println("字节数组内容:");
            System.out.println(new String(fileBytes, StandardCharsets.UTF_8));
            
            // 7. 输入流复制到字节数组
            System.out.println("\n=== 输入流复制到字节数组 ===");
            try (FileInputStream fis = new FileInputStream(sourceFile)) {
                byte[] inputStreamBytes = readInputStreamToByteArray(fis);
                System.out.println("输入流复制到字节数组成功");
                System.out.println("字节数组长度: " + inputStreamBytes.length);
                System.out.println("字节数组内容:");
                System.out.println(new String(inputStreamBytes, StandardCharsets.UTF_8));
            }
            
            // 8. Reader 复制到字符串
            System.out.println("\n=== Reader 复制到字符串 ===");
            try (FileReader fr = new FileReader(sourceFile)) {
                String readerContent = readReaderToString(fr);
                System.out.println("Reader 复制到字符串成功");
                System.out.println("字符串内容:");
                System.out.println(readerContent);
            }
            
        } catch (IOException e) {
            System.err.println("文件操作时发生异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 清理临时文件
            System.out.println("\n=== 清理临时文件 ===");
            deleteFile(sourceFile);
            deleteFile(targetFile);
            deleteFile(outputFile);
            deleteFile(tempDir + File.separator + "stream_output.txt");
            deleteFile(tempDir + File.separator + "writer_output.txt");
        }
    }

    /**
     * 读取文件到字节数组
     * @param file 文件
     * @return 字节数组
     * @throws IOException IO异常
     */
    private static byte[] readFileToByteArray(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return readInputStreamToByteArray(fis);
        }
    }

    /**
     * 读取输入流到字节数组
     * @param is 输入流
     * @return 字节数组
     * @throws IOException IO异常
     */
    private static byte[] readInputStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            baos.write(buffer, 0, length);
        }
        return baos.toByteArray();
    }

    /**
     * 读取Reader到字符串
     * @param reader Reader
     * @return 字符串
     * @throws IOException IO异常
     */
    private static String readReaderToString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1024];
        int length;
        while ((length = reader.read(buffer)) > 0) {
            sb.append(buffer, 0, length);
        }
        return sb.toString();
    }

    /**
     * 删除文件
     * @param filePath 文件路径
     */
    private static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.delete()) {
            System.out.println("删除临时文件: " + filePath);
        }
    }
}
