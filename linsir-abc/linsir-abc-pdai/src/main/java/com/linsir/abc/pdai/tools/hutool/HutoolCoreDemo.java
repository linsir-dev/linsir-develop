package com.linsir.abc.pdai.tools.hutool;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.lang.Validator;

import java.util.Date;

/**
 * Hutool 核心工具类示例
 * 演示 Hutool 提供的核心工具类，如字符串、日期、数字等
 */
public class HutoolCoreDemo {

    /**
     * 演示字符串工具类
     */
    public static void demonstrateStringUtil() {
        System.out.println("=== 字符串工具类示例 ===");
        
        // 字符串判空
        String str1 = null;
        String str2 = "";
        String str3 = "  ";
        String str4 = "Hutool";
        
        System.out.println("StrUtil.isEmpty(null): " + StrUtil.isEmpty(str1));
        System.out.println("StrUtil.isEmpty(''): " + StrUtil.isEmpty(str2));
        System.out.println("StrUtil.isEmpty('  '): " + StrUtil.isEmpty(str3));
        System.out.println("StrUtil.isEmpty('Hutool'): " + StrUtil.isEmpty(str4));
        
        System.out.println("StrUtil.isBlank(null): " + StrUtil.isBlank(str1));
        System.out.println("StrUtil.isBlank(''): " + StrUtil.isBlank(str2));
        System.out.println("StrUtil.isBlank('  '): " + StrUtil.isBlank(str3));
        System.out.println("StrUtil.isBlank('Hutool'): " + StrUtil.isBlank(str4));
        
        // 字符串操作
        String str = "Hello, Hutool!";
        System.out.println("原始字符串: " + str);
        System.out.println("StrUtil.sub(str, 0, 5): " + StrUtil.sub(str, 0, 5));
        System.out.println("StrUtil.removePrefix(str, \"Hello\"): " + StrUtil.removePrefix(str, "Hello"));
        System.out.println("StrUtil.removeSuffix(str, \"!\"): " + StrUtil.removeSuffix(str, "!"));
        System.out.println("StrUtil.trim(str): " + StrUtil.trim(str));
        System.out.println("StrUtil.upperFirst(str): " + StrUtil.upperFirst(str));
        System.out.println("StrUtil.lowerFirst(str): " + StrUtil.lowerFirst(str));
        
        // 字符串格式化
        String template = "Hello, {}! Today is {}.";
        String formatted = StrUtil.format(template, "World", "Monday");
        System.out.println("StrUtil.format: " + formatted);
        
        // 字符串分割
        String strs = "a,b,c,d";
        java.util.List<String> splitList = StrUtil.split(strs, ",");
        System.out.println("StrUtil.split: " + splitList);
    }

    /**
     * 演示日期工具类
     */
    public static void demonstrateDateUtil() {
        System.out.println("\n=== 日期工具类示例 ===");
        
        // 获取当前时间
        Date now = DateUtil.date();
        System.out.println("当前时间: " + now);
        System.out.println("当前时间(yyyy-MM-dd HH:mm:ss): " + DateUtil.formatDateTime(now));
        System.out.println("当前日期(yyyy-MM-dd): " + DateUtil.formatDate(now));
        System.out.println("当前时间(HH:mm:ss): " + DateUtil.formatTime(now));
        
        // 字符串转日期
        String dateStr = "2024-01-01 12:00:00";
        Date date = DateUtil.parse(dateStr);
        System.out.println("字符串转日期: " + date);
        
        // 日期计算
        Date tomorrow = DateUtil.tomorrow();
        System.out.println("明天: " + DateUtil.formatDate(tomorrow));
        
        Date nextWeek = DateUtil.offsetWeek(now, 1);
        System.out.println("下周今天: " + DateUtil.formatDate(nextWeek));
        
        Date lastMonth = DateUtil.offsetMonth(now, -1);
        System.out.println("上月今天: " + DateUtil.formatDate(lastMonth));
        
        // 日期差值
        long betweenDay = DateUtil.between(date, now, cn.hutool.core.date.DateUnit.DAY);
        System.out.println("日期差值(天): " + betweenDay);
        
        // 计时器
        TimeInterval timer = DateUtil.timer();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("计时器(毫秒): " + timer.interval());
        System.out.println("计时器(秒): " + timer.intervalSecond());
    }

    /**
     * 演示数字工具类
     */
    public static void demonstrateNumberUtil() {
        System.out.println("\n=== 数字工具类示例 ===");
        
        // 随机数
        int randomInt = RandomUtil.randomInt(1, 100);
        System.out.println("随机整数(1-100): " + randomInt);
        
        double randomDouble = RandomUtil.randomDouble(0, 1);
        System.out.println("随机小数(0-1): " + randomDouble);
        
        String randomStr = RandomUtil.randomString(10);
        System.out.println("随机字符串(10位): " + randomStr);
        
        String randomNumbers = RandomUtil.randomNumbers(6);
        System.out.println("随机数字字符串(6位): " + randomNumbers);
        
        // ID 生成
        String uuid = IdUtil.randomUUID();
        System.out.println("UUID: " + uuid);
        
        String simpleUUID = IdUtil.simpleUUID();
        System.out.println("简化UUID: " + simpleUUID);
        
        long snowflakeId = IdUtil.getSnowflakeNextId();
        System.out.println("雪花ID: " + snowflakeId);
    }

    /**
     * 演示验证工具类
     */
    public static void demonstrateValidateUtil() {
        System.out.println("\n=== 验证工具类示例 ===");
        
        // 验证是否为数字
        System.out.println("Validator.isNumber('123'): " + Validator.isNumber("123"));
        System.out.println("Validator.isNumber('abc'): " + Validator.isNumber("abc"));
        
        // 验证是否为邮箱
        System.out.println("Validator.isEmail('test@example.com'): " + Validator.isEmail("test@example.com"));
        System.out.println("Validator.isEmail('test'): " + Validator.isEmail("test"));
        
        // 验证是否为手机号
        System.out.println("Validator.isMobile('13800138000'): " + Validator.isMobile("13800138000"));
        System.out.println("Validator.isMobile('1234567890'): " + Validator.isMobile("1234567890"));
    }

    /**
     * 演示其他核心工具类
     */
    public static void demonstrateOtherUtil() {
        System.out.println("\n=== 其他核心工具类示例 ===");
        
        // Console 工具
        System.out.println("=== Console 工具 ===");
        String[] array = {"a", "b", "c"};
        Console.log("数组内容: {}", array);
        Console.log("Hello, {}", "Hutool");
        
        // 系统工具
        System.out.println("\n=== 系统工具 ===");
        System.out.println("操作系统: " + cn.hutool.system.SystemUtil.getOsInfo().getName());
        System.out.println("Java版本: " + cn.hutool.system.SystemUtil.getJavaInfo().getVersion());
        System.out.println("用户主目录: " + cn.hutool.system.SystemUtil.getUserInfo().getHomeDir());
        System.out.println("用户名称: " + cn.hutool.system.SystemUtil.getUserInfo().getName());
    }
}
