package com.linsir.abc.core.base.net.url;

import java.io.*;
import java.net.*;
import java.nio.charset.*;

/**
 * URL资源获取器
 * 演示URL和URLConnection的使用，获取网络资源
 *
 * 设计要点：
 * 1. URL统一资源定位符
 * 2. URLConnection提供与资源的连接
 * 3. 支持设置请求头、超时等
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class UrlResourceFetcher {

    /**
     * 获取URL内容
     */
    public String fetchContent(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        // 设置请求头
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        return content.toString();
    }

    /**
     * 获取URL信息
     */
    public void printUrlInfo(String urlString) throws IOException {
        URL url = new URL(urlString);

        System.out.println("URL信息:");
        System.out.println("  Protocol: " + url.getProtocol());
        System.out.println("  Host: " + url.getHost());
        System.out.println("  Port: " + url.getPort());
        System.out.println("  Default Port: " + url.getDefaultPort());
        System.out.println("  Path: " + url.getPath());
        System.out.println("  Query: " + url.getQuery());
        System.out.println("  File: " + url.getFile());
        System.out.println("  Ref: " + url.getRef());
    }

    /**
     * 下载文件
     */
    public void downloadFile(String urlString, String savePath) throws IOException {
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(savePath)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytes = 0;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }

            System.out.println("下载完成: " + totalBytes + " bytes");
        }
    }

    /**
     * 演示URL操作
     */
    public static void demonstrate() {
        System.out.println("=== URL资源获取演示 ===\n");

        UrlResourceFetcher fetcher = new UrlResourceFetcher();

        try {
            // 解析URL
            String testUrl = "https://www.example.com:8080/path/to/resource?key=value#section";
            fetcher.printUrlInfo(testUrl);

            // 获取网页内容（使用示例URL）
            System.out.println("\n尝试获取 http://example.com ...");
            try {
                String content = fetcher.fetchContent("http://example.com");
                System.out.println("内容长度: " + content.length() + " characters");
                System.out.println("前200字符:\n" + content.substring(0, Math.min(200, content.length())));
            } catch (IOException e) {
                System.out.println("获取失败: " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
