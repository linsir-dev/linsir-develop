package com.linsir.abc.core.base.net.url;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

/**
 * HTTP连接管理器
 * 演示HttpURLConnection的使用，发送HTTP请求
 *
 * 设计要点：
 * 1. HttpURLConnection支持HTTP协议操作
 * 2. 支持GET、POST等方法
 * 3. 可以设置请求头、读取响应头
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class HttpConnectionManager {

    /**
     * 发送GET请求
     */
    public HttpResponse sendGet(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("Accept", "text/html");

        return executeRequest(connection);
    }

    /**
     * 发送POST请求
     */
    public HttpResponse sendPost(String urlString, String body) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // 写入请求体
        try (OutputStream os = connection.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        return executeRequest(connection);
    }

    /**
     * 执行请求并获取响应
     */
    private HttpResponse executeRequest(HttpURLConnection connection) throws IOException {
        HttpResponse response = new HttpResponse();
        response.setStatusCode(connection.getResponseCode());
        response.setStatusMessage(connection.getResponseMessage());

        // 读取响应头
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        response.setHeaders(headerFields);

        // 读取响应体
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        response.setBody(content.toString());

        connection.disconnect();
        return response;
    }

    /**
     * HTTP响应类
     */
    public static class HttpResponse {
        private int statusCode;
        private String statusMessage;
        private Map<String, List<String>> headers;
        private String body;

        public int getStatusCode() { return statusCode; }
        public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
        public String getStatusMessage() { return statusMessage; }
        public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
        public Map<String, List<String>> getHeaders() { return headers; }
        public void setHeaders(Map<String, List<String>> headers) { this.headers = headers; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }

        @Override
        public String toString() {
            return "HTTP " + statusCode + " " + statusMessage + ", Body length: " + (body != null ? body.length() : 0);
        }
    }

    /**
     * 演示HTTP请求
     */
    public static void demonstrate() {
        System.out.println("=== HTTP连接管理演示 ===\n");

        HttpConnectionManager manager = new HttpConnectionManager();

        try {
            // GET请求
            System.out.println("发送GET请求到 http://httpbin.org/get ...");
            try {
                HttpResponse response = manager.sendGet("http://httpbin.org/get");
                System.out.println("响应: " + response);
                System.out.println("响应体前300字符:\n" + 
                    response.getBody().substring(0, Math.min(300, response.getBody().length())));
            } catch (IOException e) {
                System.out.println("GET请求失败: " + e.getMessage());
            }

            System.out.println();

            // POST请求
            System.out.println("发送POST请求到 http://httpbin.org/post ...");
            try {
                HttpResponse response = manager.sendPost("http://httpbin.org/post", "name=test&value=123");
                System.out.println("响应: " + response);
            } catch (IOException e) {
                System.out.println("POST请求失败: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
