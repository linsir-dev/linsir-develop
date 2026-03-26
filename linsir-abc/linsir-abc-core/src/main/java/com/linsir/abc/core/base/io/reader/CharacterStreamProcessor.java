package com.linsir.abc.core.base.io.reader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 字符流处理器
 * 演示Reader/Writer的使用，包括字符读写、行处理等
 *
 * 设计要点：
 * 1. Reader/Writer处理字符数据，自动处理编码
 * 2. BufferedReader/BufferedWriter提供缓冲和行处理
 * 3. 适合处理文本文件
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class CharacterStreamProcessor {

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * 读取文本文件全部内容
     *
     * @param filePath 文件路径
     * @param encoding 字符编码
     * @return 文件内容字符串
     * @throws IOException 当IO操作失败时
     */
    public String readText(String filePath, String encoding) throws IOException {
        StringBuilder content = new StringBuilder();

        try (FileInputStream fis = new FileInputStream(filePath);
             InputStreamReader isr = new InputStreamReader(fis, encoding);
             BufferedReader br = new BufferedReader(isr)) {

            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            int charsRead;

            while ((charsRead = br.read(buffer)) != -1) {
                content.append(buffer, 0, charsRead);
            }
        }

        return content.toString();
    }

    /**
     * 读取文本文件全部内容（使用默认编码）
     *
     * @param filePath 文件路径
     * @return 文件内容字符串
     * @throws IOException 当IO操作失败时
     */
    public String readText(String filePath) throws IOException {
        return readText(filePath, java.nio.charset.Charset.defaultCharset().name());
    }

    /**
     * 按行读取文本文件
     *
     * @param filePath 文件路径
     * @param encoding 字符编码
     * @return 行列表
     * @throws IOException 当IO操作失败时
     */
    public List<String> readLines(String filePath, String encoding) throws IOException {
        List<String> lines = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             InputStreamReader isr = new InputStreamReader(fis, encoding);
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }

    /**
     * 按行读取文本文件（使用默认编码）
     *
     * @param filePath 文件路径
     * @return 行列表
     * @throws IOException 当IO操作失败时
     */
    public List<String> readLines(String filePath) throws IOException {
        return readLines(filePath, java.nio.charset.Charset.defaultCharset().name());
    }

    /**
     * 写入文本到文件
     *
     * @param filePath 文件路径
     * @param content 文本内容
     * @param encoding 字符编码
     * @param append 是否追加
     * @throws IOException 当IO操作失败时
     */
    public void writeText(String filePath, String content, String encoding, boolean append) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath, append);
             OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
             BufferedWriter bw = new BufferedWriter(osw)) {

            bw.write(content);
            bw.flush();
        }
    }

    /**
     * 写入文本到文件（使用默认编码）
     *
     * @param filePath 文件路径
     * @param content 文本内容
     * @param append 是否追加
     * @throws IOException 当IO操作失败时
     */
    public void writeText(String filePath, String content, boolean append) throws IOException {
        writeText(filePath, content, java.nio.charset.Charset.defaultCharset().name(), append);
    }

    /**
     * 写入多行文本
     *
     * @param filePath 文件路径
     * @param lines 行列表
     * @param encoding 字符编码
     * @param append 是否追加
     * @throws IOException 当IO操作失败时
     */
    public void writeLines(String filePath, List<String> lines, String encoding, boolean append) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath, append);
             OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
             BufferedWriter bw = new BufferedWriter(osw)) {

            for (int i = 0; i < lines.size(); i++) {
                bw.write(lines.get(i));
                if (i < lines.size() - 1) {
                    bw.newLine();
                }
            }
            bw.flush();
        }
    }

    /**
     * 复制文本文件
     *
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @param sourceEncoding 源文件编码
     * @param destEncoding 目标文件编码
     * @throws IOException 当IO操作失败时
     */
    public void copyTextFile(String sourcePath, String destPath, String sourceEncoding, String destEncoding) throws IOException {
        try (FileInputStream fis = new FileInputStream(sourcePath);
             InputStreamReader isr = new InputStreamReader(fis, sourceEncoding);
             BufferedReader br = new BufferedReader(isr);
             FileOutputStream fos = new FileOutputStream(destPath);
             OutputStreamWriter osw = new OutputStreamWriter(fos, destEncoding);
             BufferedWriter bw = new BufferedWriter(osw)) {

            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            int charsRead;

            while ((charsRead = br.read(buffer)) != -1) {
                bw.write(buffer, 0, charsRead);
            }

            bw.flush();
        }
    }

    /**
     * 统计文本文件信息
     *
     * @param filePath 文件路径
     * @param encoding 字符编码
     * @return 文件统计信息
     * @throws IOException 当IO操作失败时
     */
    public TextStatistics analyzeText(String filePath, String encoding) throws IOException {
        TextStatistics stats = new TextStatistics();

        try (FileInputStream fis = new FileInputStream(filePath);
             InputStreamReader isr = new InputStreamReader(fis, encoding);
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            int totalChars = 0;
            int totalWords = 0;
            int lineCount = 0;

            while ((line = br.readLine()) != null) {
                lineCount++;
                totalChars += line.length();

                // 简单单词统计（按空白字符分割）
                String[] words = line.trim().split("\\s+");
                if (words.length > 0 && !words[0].isEmpty()) {
                    totalWords += words.length;
                }
            }

            stats.setLineCount(lineCount);
            stats.setCharCount(totalChars);
            stats.setWordCount(totalWords);
        }

        return stats;
    }

    /**
     * 文本统计信息类
     */
    public static class TextStatistics {
        private int lineCount;
        private int charCount;
        private int wordCount;

        public int getLineCount() { return lineCount; }
        public void setLineCount(int lineCount) { this.lineCount = lineCount; }
        public int getCharCount() { return charCount; }
        public void setCharCount(int charCount) { this.charCount = charCount; }
        public int getWordCount() { return wordCount; }
        public void setWordCount(int wordCount) { this.wordCount = wordCount; }

        @Override
        public String toString() {
            return "TextStatistics{" +
                    "lineCount=" + lineCount +
                    ", charCount=" + charCount +
                    ", wordCount=" + wordCount +
                    '}';
        }
    }

    /**
     * 演示字符流的使用
     */
    public static void demonstrate() {
        CharacterStreamProcessor processor = new CharacterStreamProcessor();

        try {
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "char_stream_demo");
            tempDir.mkdirs();

            String textFile = new File(tempDir, "sample.txt").getAbsolutePath();
            String utf8File = new File(tempDir, "utf8.txt").getAbsolutePath();
            String gbkFile = new File(tempDir, "gbk.txt").getAbsolutePath();

            // 写入文本
            String content = "Hello, World!\n这是一行中文。\nThis is line 3.\n最后一行。";
            processor.writeText(textFile, content, false);
            System.out.println("写入文件: " + textFile);

            // 读取文本
            String readContent = processor.readText(textFile);
            System.out.println("读取内容:\n" + readContent);

            // 按行读取
            System.out.println("\n按行读取:");
            List<String> lines = processor.readLines(textFile);
            for (int i = 0; i < lines.size(); i++) {
                System.out.println("  行 " + (i + 1) + ": " + lines.get(i));
            }

            // 统计信息
            TextStatistics stats = processor.analyzeText(textFile, "UTF-8");
            System.out.println("\n文件统计: " + stats);

            // 编码转换
            processor.writeText(utf8File, "中文测试 UTF-8", "UTF-8", false);
            processor.copyTextFile(utf8File, gbkFile, "UTF-8", "GBK");
            System.out.println("\n编码转换: UTF-8 -> GBK");

            // 追加写入
            processor.writeText(textFile, "\n这是追加的内容。", true);
            System.out.println("\n追加后内容:\n" + processor.readText(textFile));

            // 清理
            new File(textFile).delete();
            new File(utf8File).delete();
            new File(gbkFile).delete();
            tempDir.delete();

        } catch (IOException e) {
            System.err.println("IO错误: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
