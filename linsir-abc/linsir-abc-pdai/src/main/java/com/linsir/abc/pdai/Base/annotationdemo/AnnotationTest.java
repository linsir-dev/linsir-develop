package com.linsir.abc.pdai.base.annotationdemo;

/**
 * 注解测试类
 * 演示Java自定义注解的使用和处理
 */
public class AnnotationTest {
    public static void main(String[] args) {
        System.out.println("=== Java自定义注解示例 ===\n");

        // 创建AnnotationDemo实例
        AnnotationDemo demo = new AnnotationDemo(1, "张三", 25);
        System.out.println("1. 显示对象信息:");
        demo.showInfo();

        System.out.println("\n2. 处理注解信息:");
        // 处理AnnotationDemo类的所有注解
        AnnotationProcessor.processAllAnnotations(AnnotationDemo.class);

        System.out.println("\n3. 测试注解的实际应用:");
        // 模拟注解的实际应用场景
        System.out.println("注解可以用于:");
        System.out.println("- 配置类和方法的行为");
        System.out.println("- 生成文档");
        System.out.println("- 运行时反射处理");
        System.out.println("- 代码检查和验证");
        System.out.println("- 框架配置（如Spring中的@Component等）");
    }
}
