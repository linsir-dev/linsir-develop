package com.linsir.abc.core.base.io.reader;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

/**
 * 编码转换器
 * 演示不同字符编码之间的转换
 *
 * 设计要点：
 * 1. Charset类提供编码解码功能
 * 2. CharsetEncoder/CharsetDecoder提供细粒度控制
 * 3. 处理编码错误（替换、忽略、报告）
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class EncodingConverter {

    /**
     * 转换文件编码
     *
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @param sourceEncoding 源编码
     * @param targetEncoding 目标编码
     * @throws IOException 当IO操作失败时
     */
    public void convertEncoding(String sourcePath, String destPath, String sourceEncoding, String targetEncoding) throws IOException {
        // 使用NIO的Charset进行编码转换
        Charset sourceCharset = Charset.forName(sourceEncoding);
        Charset targetCharset = Charset.forName(targetEncoding);

        try (FileInputStream fis = new FileInputStream(sourcePath);
             FileOutputStream fos = new FileOutputStream(destPath)) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                // 解码：字节 -> 字符
                ByteBuffer inputBuffer = ByteBuffer.wrap(buffer, 0, bytesRead);
                CharBuffer charBuffer = sourceCharset.decode(inputBuffer);

                // 编码：字符 -> 字节
                ByteBuffer outputBuffer = targetCharset.encode(charBuffer);
                fos.write(outputBuffer.array(), 0, outputBuffer.limit());
            }
        }
    }

    /**
     * 转换字符串编码
     *
     * @param text 原始字符串
     * @param sourceEncoding 源编码
     * @param targetEncoding 目标编码
     * @return 转换后的字符串
     */
    public String convertStringEncoding(String text, String sourceEncoding, String targetEncoding) {
        try {
            byte[] bytes = text.getBytes(sourceEncoding);
            return new String(bytes, targetEncoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("不支持的编码: " + e.getMessage(), e);
        }
    }

    /**
     * 检测文件编码（简单检测，非100%准确）
     *
     * @param filePath 文件路径
     * @return 检测到的编码
     * @throws IOException 当IO操作失败时
     */
    public String detectEncoding(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            byte[] header = new byte[4];
            int bytesRead = bis.read(header);

            if (bytesRead >= 3) {
                // UTF-8 BOM: EF BB BF
                if ((header[0] & 0xFF) == 0xEF && (header[1] & 0xFF) == 0xBB && (header[2] & 0xFF) == 0xBF) {
                    return "UTF-8";
                }
                // UTF-16 BE BOM: FE FF
                if ((header[0] & 0xFF) == 0xFE && (header[1] & 0xFF) == 0xFF) {
                    return "UTF-16BE";
                }
                // UTF-16 LE BOM: FF FE
                if ((header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xFE) {
                    return "UTF-16LE";
                }
            }

            // 尝试UTF-8解码
            bis.reset();
            byte[] content = bis.readAllBytes();
            if (isValidUtf8(content)) {
                return "UTF-8";
            }

            return "GBK"; // 默认返回GBK
        }
    }

    /**
     * 检查是否为有效的UTF-8编码
     */
    private boolean isValidUtf8(byte[] data) {
        int i = 0;
        while (i < data.length) {
            int b = data[i] & 0xFF;

            if (b < 0x80) {
                i++;
            } else if ((b & 0xE0) == 0xC0) {
                // 2字节序列
                if (i + 1 >= data.length || (data[i + 1] & 0xC0) != 0x80) return false;
                i += 2;
            } else if ((b & 0xF0) == 0xE0) {
                // 3字节序列
                if (i + 2 >= data.length ||
                    (data[i + 1] & 0xC0) != 0x80 ||
                    (data[i + 2] & 0xC0) != 0x80) return false;
                i += 3;
            } else if ((b & 0xF8) == 0xF0) {
                // 4字节序列
                if (i + 3 >= data.length ||
                    (data[i + 1] & 0xC0) != 0x80 ||
                    (data[i + 2] & 0xC0) != 0x80 ||
                    (data[i + 3] & 0xC0) != 0x80) return false;
                i += 4;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 使用指定的错误处理策略进行编码转换
     *
     * @param text 原始字符串
     * @param targetEncoding 目标编码
     * @param errorAction 错误处理策略
     * @return 转换后的字节数组
     */
    public byte[] encodeWithErrorHandling(String text, String targetEncoding, CodingErrorAction errorAction) {
        Charset charset = Charset.forName(targetEncoding);
        CharsetEncoder encoder = charset.newEncoder()
                .onMalformedInput(errorAction)
                .onUnmappableCharacter(errorAction);

        try {
            ByteBuffer buffer = encoder.encode(CharBuffer.wrap(text));
            byte[] result = new byte[buffer.remaining()];
            buffer.get(result);
            return result;
        } catch (CharacterCodingException e) {
            throw new RuntimeException("编码失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取系统支持的编码列表
     */
    public void listAvailableCharsets() {
        System.out.println("系统支持的编码:");
        java.util.SortedMap<String, Charset> charsets = Charset.availableCharsets();
        int count = 0;
        for (String name : charsets.keySet()) {
            System.out.printf("  %-20s", name);
            count++;
            if (count % 4 == 0) {
                System.out.println();
            }
        }
        if (count % 4 != 0) {
            System.out.println();
        }
        System.out.println("\n总计: " + charsets.size() + " 种编码");
    }

    /**
     * 演示编码转换
     */
    public static void demonstrate() {
        EncodingConverter converter = new EncodingConverter();

        try {
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "encoding_demo");
            tempDir.mkdirs();

            // 显示支持的编码
            converter.listAvailableCharsets();

            System.out.println("\n" + "=".repeat(50));

            // 创建测试文件
            String utf8File = new File(tempDir, "utf8.txt").getAbsolutePath();
            String gbkFile = new File(tempDir, "gbk.txt").getAbsolutePath();
            String backToUtf8File = new File(tempDir, "back_utf8.txt").getAbsolutePath();

            // 写入UTF-8文件
            String content = "Hello, World!\n你好，世界！\n日本語テキスト\n한국어 텍스트";
            try (FileOutputStream fos = new FileOutputStream(utf8File);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8")) {
                osw.write(content);
            }
            System.out.println("\n创建UTF-8文件: " + utf8File);

            // UTF-8 -> GBK
            converter.convertEncoding(utf8File, gbkFile, "UTF-8", "GBK");
            System.out.println("转换为GBK: " + gbkFile);

            // GBK -> UTF-8
            converter.convertEncoding(gbkFile, backToUtf8File, "GBK", "UTF-8");
            System.out.println("转回UTF-8: " + backToUtf8File);

            // 验证内容
            String originalContent = readFile(utf8File, "UTF-8");
            String convertedContent = readFile(backToUtf8File, "UTF-8");
            System.out.println("\n原始内容:\n" + originalContent);
            System.out.println("\n转换后内容:\n" + convertedContent);
            System.out.println("内容一致: " + originalContent.equals(convertedContent));

            // 字符串编码转换
            System.out.println("\n字符串编码转换测试:");
            String text = "Hello 世界";
            byte[] utf8Bytes = text.getBytes("UTF-8");
            byte[] gbkBytes = text.getBytes("GBK");
            System.out.println("  UTF-8字节数: " + utf8Bytes.length);
            System.out.println("  GBK字节数: " + gbkBytes.length);

            // 错误处理测试
            System.out.println("\n错误处理测试:");
            String mixedText = "Hello \uD800World"; // 包含非法的代理字符
            try {
                byte[] bytes = converter.encodeWithErrorHandling(mixedText, "UTF-8", CodingErrorAction.REPORT);
                System.out.println("  REPORT: 成功");
            } catch (Exception e) {
                System.out.println("  REPORT: 失败 - " + e.getMessage());
            }

            byte[] replaceBytes = converter.encodeWithErrorHandling(mixedText, "UTF-8", CodingErrorAction.REPLACE);
            System.out.println("  REPLACE: 替换后长度 = " + replaceBytes.length);

            // 清理
            new File(utf8File).delete();
            new File(gbkFile).delete();
            new File(backToUtf8File).delete();
            tempDir.delete();

        } catch (IOException e) {
            System.err.println("IO错误: " + e.getMessage());
        }
    }

    private static String readFile(String filePath, String encoding) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(filePath);
             InputStreamReader isr = new InputStreamReader(fis, encoding);
             BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
