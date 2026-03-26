package com.linsir.abc.core.grammar.annotation;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 自定义注解示例
 *
 * 本类演示如何创建和使用自定义注解
 * 对应 JDK: 元注解和自定义注解
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class CustomAnnotations {

    /**
     * 演示自定义注解的使用
     */
    public void demonstrateCustomAnnotations() {
        System.out.println("=== 自定义注解使用 ===");

        // 创建并测试用户服务
        UserService userService = new UserService();

        System.out.println("调用方法:");
        userService.createUser("张三", "zhangsan@example.com");
        userService.deleteUser(123);
        userService.getUser(456);
    }

    /**
     * 演示注解处理器
     */
    public void demonstrateAnnotationProcessor() {
        System.out.println("\n=== 注解处理器 ===");

        try {
            Class<?> clazz = UserService.class;

            // 处理类级别的注解
            if (clazz.isAnnotationPresent(Service.class)) {
                Service service = clazz.getAnnotation(Service.class);
                System.out.println("服务信息:");
                System.out.println("  名称: " + service.name());
                System.out.println("  描述: " + service.description());
                System.out.println("  是否单例: " + service.singleton());
            }

            // 处理方法级别的注解
            System.out.println("\n方法信息:");
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Transaction.class)) {
                    Transaction tx = method.getAnnotation(Transaction.class);
                    System.out.println("  方法: " + method.getName());
                    System.out.println("    需要事务: 是");
                    System.out.println("    只读: " + tx.readOnly());
                    System.out.println("    超时: " + tx.timeout() + "秒");
                }

                if (method.isAnnotationPresent(Deprecated.class)) {
                    Deprecated dep = method.getAnnotation(Deprecated.class);
                    System.out.println("    已过时: 是");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 演示重复注解
     */
    public void demonstrateRepeatableAnnotation() {
        System.out.println("\n=== 重复注解 ===");

        try {
            Class<?> clazz = UserService.class;

            // 获取所有权限注解
            Permission[] permissions = clazz.getAnnotationsByType(Permission.class);
            System.out.println("权限列表:");
            for (Permission p : permissions) {
                System.out.println("  - " + p.value() + " (" + p.description() + ")");
            }

            // 通过容器获取
            Permissions container = clazz.getAnnotation(Permissions.class);
            if (container != null) {
                System.out.println("\n通过容器获取:");
                Arrays.stream(container.value()).forEach(p -> 
                    System.out.println("  - " + p.value())
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============ 自定义注解定义 ============

    /**
     * 服务注解
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    public @interface Service {
        String name();
        String description() default "";
        boolean singleton() default false;
    }

    /**
     * 事务注解
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Transaction {
        boolean readOnly() default false;
        int timeout() default -1;
        Class<? extends Exception>[] rollbackFor() default {};
    }

    /**
     * 权限注解（可重复）
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Repeatable(Permissions.class)
    public @interface Permission {
        String value();
        String description() default "";
    }

    /**
     * 权限容器注解
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    public @interface Permissions {
        Permission[] value();
    }

    /**
     * 日志注解
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Log {
        String value() default "";
        LogLevel level() default LogLevel.INFO;
    }

    /**
     * 日志级别枚举
     */
    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }

    // ============ 使用注解的类 ============

    /**
     * 用户服务类
     */
    @Service(
        name = "UserService",
        description = "用户管理服务",
        singleton = true
    )
    @Permission(value = "user:read", description = "读取用户")
    @Permission(value = "user:write", description = "写入用户")
    @Permission(value = "user:delete", description = "删除用户")
    static class UserService {

        @Transaction(readOnly = false, timeout = 30)
        @Log("创建用户")
        public void createUser(String name, String email) {
            System.out.println("  创建用户: " + name);
        }

        @Transaction(readOnly = false)
        @Log(value = "删除用户", level = LogLevel.WARN)
        public void deleteUser(int userId) {
            System.out.println("  删除用户 ID: " + userId);
        }

        @Transaction(readOnly = true)
        @Log("查询用户")
        public void getUser(int userId) {
            System.out.println("  查询用户 ID: " + userId);
        }

        @Deprecated(since = "2.0", forRemoval = true)
        public void oldMethod() {
            System.out.println("  旧方法");
        }
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 自定义注解演示                        ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        CustomAnnotations demo = new CustomAnnotations();
        demo.demonstrateCustomAnnotations();
        demo.demonstrateAnnotationProcessor();
        demo.demonstrateRepeatableAnnotation();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
        System.out.println("\n元注解说明:");
        System.out.println("• @Retention - 注解保留策略");
        System.out.println("• @Target - 注解应用目标");
        System.out.println("• @Documented - 包含在 Javadoc 中");
        System.out.println("• @Inherited - 子类继承注解");
        System.out.println("• @Repeatable - 可重复注解");
    }
}
