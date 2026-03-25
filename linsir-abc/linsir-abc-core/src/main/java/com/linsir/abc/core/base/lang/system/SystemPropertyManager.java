package com.linsir.abc.core.base.lang.system;

import java.util.Properties;
import java.util.Set;

/**
 * 系统属性管理器
 * 
 * 本类封装了System类的常用功能：
 * 1. 系统属性的读取和设置
 * 2. 环境变量的访问
 * 3. 系统时间测量
 * 4. 数组高效拷贝
 * 
 * System类特点：
 * - 不能被实例化（私有构造方法）
 * - 所有方法都是静态的
 * - 提供与操作系统交互的能力
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class SystemPropertyManager {
    
    /**
     * 私有构造方法，防止实例化
     */
    private SystemPropertyManager() {
        throw new AssertionError("工具类不应被实例化");
    }
    
    /**
     * 获取系统属性
     * 
     * @param key 属性键
     * @return 属性值，如果不存在返回null
     */
    public static String getProperty(String key) {
        return System.getProperty(key);
    }
    
    /**
     * 获取系统属性，如果不存在返回默认值
     * 
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值或默认值
     */
    public static String getProperty(String key, String defaultValue) {
        return System.getProperty(key, defaultValue);
    }
    
    /**
     * 设置系统属性
     * 
     * @param key 属性键
     * @param value 属性值
     * @return 之前的属性值，如果不存在返回null
     */
    public static String setProperty(String key, String value) {
        return System.setProperty(key, value);
    }
    
    /**
     * 清除系统属性
     * 
     * @param key 属性键
     * @return 被清除的属性值，如果不存在返回null
     */
    public static String clearProperty(String key) {
        return System.clearProperty(key);
    }
    
    /**
     * 获取所有系统属性
     * 
     * @return 系统属性集合
     */
    public static Properties getAllProperties() {
        return System.getProperties();
    }
    
    /**
     * 打印所有系统属性
     */
    public static void printAllProperties() {
        Properties props = System.getProperties();
        Set<String> propertyNames = props.stringPropertyNames();
        
        System.out.println("=== 系统属性列表 ===");
        for (String name : propertyNames) {
            System.out.println(name + " = " + props.getProperty(name));
        }
    }
    
    /**
     * 获取常用系统属性
     * 
     * @return 常用系统属性信息
     */
    public static String getCommonProperties() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== 常用系统属性 ===\n");
        sb.append("Java版本: ").append(System.getProperty("java.version")).append("\n");
        sb.append("Java供应商: ").append(System.getProperty("java.vendor")).append("\n");
        sb.append("Java安装目录: ").append(System.getProperty("java.home")).append("\n");
        sb.append("操作系统名称: ").append(System.getProperty("os.name")).append("\n");
        sb.append("操作系统版本: ").append(System.getProperty("os.version")).append("\n");
        sb.append("操作系统架构: ").append(System.getProperty("os.arch")).append("\n");
        sb.append("用户目录: ").append(System.getProperty("user.dir")).append("\n");
        sb.append("用户主目录: ").append(System.getProperty("user.home")).append("\n");
        sb.append("用户名: ").append(System.getProperty("user.name")).append("\n");
        sb.append("文件分隔符: ").append(System.getProperty("file.separator")).append("\n");
        sb.append("路径分隔符: ").append(System.getProperty("path.separator")).append("\n");
        sb.append("行分隔符: ").append(System.getProperty("line.separator").replace("\n", "\\n").replace("\r", "\\r")).append("\n");
        
        return sb.toString();
    }
    
    /**
     * 获取环境变量
     * 
     * @param name 环境变量名
     * @return 环境变量值，如果不存在返回null
     */
    public static String getEnv(String name) {
        return System.getenv(name);
    }
    
    /**
     * 获取所有环境变量
     * 
     * @return 环境变量映射
     */
    public static java.util.Map<String, String> getAllEnv() {
        return System.getenv();
    }
    
    /**
     * 获取当前时间（毫秒）
     * 返回自1970年1月1日00:00:00 GMT以来的毫秒数
     * 
     * @return 当前时间的毫秒表示
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }
    
    /**
     * 获取当前时间（纳秒）
     * 返回最精确的可用系统计时器的当前值，以纳秒为单位
     * 
     * @return 当前时间的纳秒表示
     */
    public static long nanoTime() {
        return System.nanoTime();
    }
    
    /**
     * 建议JVM进行垃圾回收
     * 注意：这只是建议，JVM不一定会立即执行
     */
    public static void suggestGarbageCollection() {
        System.gc();
    }
    
    /**
     * 使用System.arraycopy进行数组拷贝
     * 
     * 特点：
     * - 是native方法，性能极高
     * - 可以进行数组元素的类型转换（如果兼容）
     * - 源数组和目标数组可以是同一个数组（支持重叠拷贝）
     * 
     * @param src 源数组
     * @param srcPos 源数组起始位置
     * @param dest 目标数组
     * @param destPos 目标数组起始位置
     * @param length 拷贝长度
     */
    public static void arrayCopy(Object src, int srcPos, Object dest, int destPos, int length) {
        System.arraycopy(src, srcPos, dest, destPos, length);
    }
    
    /**
     * 退出JVM
     * 
     * @param status 退出状态码，0表示正常退出，非0表示异常退出
     */
    public static void exit(int status) {
        System.exit(status);
    }
}
