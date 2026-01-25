package com.linsir.abc.pdai.tools.hutool;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Hutool HTTP 工具类示例
 * 演示 Hutool 提供的 HTTP 客户端工具，如发送 HTTP 请求、处理响应等
 */
public class HutoolHttpDemo {

    /**
     * 演示 GET 请求
     */
    public static void demonstrateGetRequest() {
        System.out.println("=== GET 请求示例 ===");
        
        // 发送简单的 GET 请求
        String url = "https://httpbin.org/get";
        System.out.println("发送 GET 请求到: " + url);
        
        String result = HttpUtil.get(url);
        System.out.println("响应结果:");
        System.out.println(result);
        
        // 带参数的 GET 请求
        System.out.println("\n发送带参数的 GET 请求:");
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Hutool");
        params.put("version", "5.8.26");
        
        result = HttpUtil.get(url, params);
        System.out.println("响应结果:");
        System.out.println(result);
    }

    /**
     * 演示 POST 请求
     */
    public static void demonstratePostRequest() {
        System.out.println("\n=== POST 请求示例 ===");
        
        // 发送简单的 POST 请求
        String url = "https://httpbin.org/post";
        System.out.println("发送 POST 请求到: " + url);
        
        // 表单参数
        Map<String, Object> formParams = new HashMap<>();
        formParams.put("username", "test");
        formParams.put("password", "123456");
        
        String result = HttpUtil.post(url, formParams);
        System.out.println("响应结果:");
        System.out.println(result);
        
        // JSON 参数
        System.out.println("\n发送 JSON 参数的 POST 请求:");
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("name", "Hutool");
        jsonParams.put("type", "HTTP Client");
        jsonParams.put("version", "5.8.26");
        
        result = HttpUtil.post(url, jsonParams.toString());
        System.out.println("响应结果:");
        System.out.println(result);
    }

    /**
     * 演示 HttpRequest 高级用法
     */
    public static void demonstrateHttpRequest() {
        System.out.println("\n=== HttpRequest 高级用法示例 ===");
        
        String url = "https://httpbin.org/put";
        System.out.println("发送 PUT 请求到: " + url);
        
        // 使用 HttpRequest 构建请求
        HttpResponse response = HttpRequest.put(url)
                .header("Content-Type", "application/json")
                .header("User-Agent", "Hutool HTTP Client")
                .body("{\"method\": \"PUT\", \"data\": \"test\"}")
                .timeout(5000) // 超时时间
                .execute();
        
        // 获取响应信息
        System.out.println("响应状态码: " + response.getStatus());
        System.out.println("响应头:");
        response.headers().forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });
        System.out.println("响应体:");
        System.out.println(response.body());
        
        // 关闭响应
        response.close();
    }

    /**
     * 演示文件下载
     */
    public static void demonstrateFileDownload() {
        System.out.println("\n=== 文件下载示例 ===");
        
        // 下载小文件
        String fileUrl = "https://www.apache.org/licenses/LICENSE-2.0.txt";
        String destFile = System.getProperty("user.dir") + "/target/LICENSE-2.0.txt";
        
        System.out.println("下载文件从: " + fileUrl);
        System.out.println("保存到: " + destFile);
        
        long size = HttpUtil.downloadFile(fileUrl, destFile);
        System.out.println("下载完成，文件大小: " + size + " 字节");
        
        // 读取下载的文件内容
        System.out.println("\n读取下载的文件前 500 个字符:");
        String content = cn.hutool.core.io.FileUtil.readString(destFile, java.nio.charset.StandardCharsets.UTF_8);
        if (content.length() > 500) {
            System.out.println(content.substring(0, 500) + "...");
        } else {
            System.out.println(content);
        }
        
        // 清理文件
        cn.hutool.core.io.FileUtil.del(destFile);
        System.out.println("\n临时文件已清理");
    }

    /**
     * 演示 JSON 工具
     */
    public static void demonstrateJsonUtil() {
        System.out.println("\n=== JSON 工具示例 ===");
        
        // JSON 字符串转对象
        String jsonStr = "{\"name\": \"Hutool\", \"version\": \"5.8.26\", \"features\": [\"HTTP\", \"IO\", \"Date\"]}";
        System.out.println("原始 JSON 字符串:");
        System.out.println(jsonStr);
        
        // 解析 JSON
        JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
        System.out.println("\n解析后的 JSON 对象:");
        System.out.println("name: " + jsonObject.getStr("name"));
        System.out.println("version: " + jsonObject.getStr("version"));
        System.out.println("features: " + jsonObject.getJSONArray("features"));
        
        // 对象转 JSON
        System.out.println("\n对象转 JSON:");
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Hutool");
        map.put("version", "5.8.26");
        map.put("active", true);
        
        String json = JSONUtil.toJsonStr(map);
        System.out.println(json);
        
        // 美化 JSON
        System.out.println("\n美化 JSON:");
        String prettyJson = JSONUtil.toJsonPrettyStr(map);
        System.out.println(prettyJson);
    }

    /**
     * 演示 HTTP 会话管理
     */
    public static void demonstrateHttpSession() {
        System.out.println("\n=== HTTP 会话管理示例 ===");
        
        // 创建会话
        System.out.println("创建 HTTP 会话");
        
        // 模拟登录
        String loginUrl = "https://httpbin.org/post";
        Map<String, Object> loginParams = new HashMap<>();
        loginParams.put("username", "test");
        loginParams.put("password", "123456");
        
        HttpResponse loginResponse = HttpRequest.post(loginUrl)
                .form(loginParams)
                .execute();
        
        // 获取 cookies
        System.out.println("\n登录响应 cookies:");
        loginResponse.getCookies().forEach(cookie -> {
            System.out.println(cookie.getName() + "=" + cookie.getValue());
        });
        
        // 使用相同的 cookies 发送后续请求
        System.out.println("\n使用相同的 cookies 发送后续请求:");
        HttpResponse nextResponse = HttpRequest.get("https://httpbin.org/get")
                .execute();
        
        System.out.println("响应结果:");
        System.out.println(nextResponse.body());
        
        // 关闭响应
        loginResponse.close();
        nextResponse.close();
    }
}
