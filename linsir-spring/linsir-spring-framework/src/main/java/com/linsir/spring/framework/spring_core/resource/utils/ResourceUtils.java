package com.linsir.spring.framework.spring_core.resource.utils;

import com.linsir.spring.framework.spring_core.resource.core.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源工具类
 * 提供资源操作的便捷方法
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public class ResourceUtils {

    /**
     * 读取资源内容为字符串
     *
     * @param resource 资源对象
     * @return 内容字符串
     * @throws IOException 当读取失败时抛出
     */
    public static String readAsString(Resource resource) throws IOException {
        if (!resource.exists()) {
            throw new IOException("资源不存在: " + resource.getDescription());
        }

        try (InputStream is = resource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * 读取资源内容为字符串列表（按行）
     *
     * @param resource 资源对象
     * @return 字符串列表
     * @throws IOException 当读取失败时抛出
     */
    public static List<String> readAsLines(Resource resource) throws IOException {
        if (!resource.exists()) {
            throw new IOException("资源不存在: " + resource.getDescription());
        }

        List<String> lines = new ArrayList<>();

        try (InputStream is = resource.getInputStream();
             BufferedReader reader = new BufferedReader(
                 new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }

    /**
     * 读取资源内容为字节数组
     *
     * @param resource 资源对象
     * @return 字节数组
     * @throws IOException 当读取失败时抛出
     */
    public static byte[] readAsBytes(Resource resource) throws IOException {
        if (!resource.exists()) {
            throw new IOException("资源不存在: " + resource.getDescription());
        }

        try (InputStream is = resource.getInputStream()) {
            return is.readAllBytes();
        }
    }

    /**
     * 获取资源文件扩展名
     *
     * @param resource 资源对象
     * @return 扩展名（不含点），如果没有则返回空字符串
     */
    public static String getFileExtension(Resource resource) {
        String filename = resource.getFilename();
        if (filename == null) {
            return "";
        }

        int dotIndex = filename.lastIndexOf('.');
        return dotIndex != -1 ? filename.substring(dotIndex + 1) : "";
    }

    /**
     * 判断资源是否为文本文件
     *
     * @param resource 资源对象
     * @return true 如果是文本文件
     */
    public static boolean isTextFile(Resource resource) {
        String extension = getFileExtension(resource).toLowerCase();
        return extension.equals("txt") ||
               extension.equals("properties") ||
               extension.equals("xml") ||
               extension.equals("json") ||
               extension.equals("yaml") ||
               extension.equals("yml") ||
               extension.equals("html") ||
               extension.equals("htm") ||
               extension.equals("css") ||
               extension.equals("js");
    }

    /**
     * 判断资源是否为图片文件
     *
     * @param resource 资源对象
     * @return true 如果是图片文件
     */
    public static boolean isImageFile(Resource resource) {
        String extension = getFileExtension(resource).toLowerCase();
        return extension.equals("jpg") ||
               extension.equals("jpeg") ||
               extension.equals("png") ||
               extension.equals("gif") ||
               extension.equals("bmp") ||
               extension.equals("svg");
    }

    /**
     * 安全关闭资源输入流
     *
     * @param resource 资源对象
     */
    public static void closeQuietly(Resource resource) {
        if (resource == null) {
            return;
        }

        try {
            if (resource.isOpen()) {
                resource.getInputStream().close();
            }
        } catch (IOException e) {
            // 忽略关闭异常
        }
    }

    /**
     * 获取资源的 MIME 类型（简化实现）
     *
     * @param resource 资源对象
     * @return MIME 类型字符串
     */
    public static String getMimeType(Resource resource) {
        String extension = getFileExtension(resource).toLowerCase();

        switch (extension) {
            case "txt":
                return "text/plain";
            case "html":
            case "htm":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "json":
                return "application/json";
            case "xml":
                return "application/xml";
            case "properties":
                return "text/plain";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "svg":
                return "image/svg+xml";
            case "pdf":
                return "application/pdf";
            default:
                return "application/octet-stream";
        }
    }
}
