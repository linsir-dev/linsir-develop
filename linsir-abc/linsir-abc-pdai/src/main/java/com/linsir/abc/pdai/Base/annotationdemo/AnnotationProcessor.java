package com.linsir.abc.pdai.base.annotationdemo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 注解处理器
 * 演示如何通过反射获取和处理注解信息
 */
public class AnnotationProcessor {

    /**
     * 处理类级别的注解
     * @param clazz 类对象
     */
    public static void processClassAnnotation(Class<?> clazz) {
        System.out.println("=== 处理类级别的注解 ===");
        
        // 检查类是否有LinAnnotation注解
        if (clazz.isAnnotationPresent(LinAnnotation.class)) {
            LinAnnotation annotation = clazz.getAnnotation(LinAnnotation.class);
            System.out.println("类注解信息:");
            System.out.println("value: " + annotation.value());
            System.out.println("name: " + annotation.name());
            System.out.println("version: " + annotation.version());
        }
    }

    /**
     * 处理字段级别的注解
     * @param clazz 类对象
     */
    public static void processFieldAnnotations(Class<?> clazz) {
        System.out.println("\n=== 处理字段级别的注解 ===");
        
        // 获取所有字段
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 检查字段是否有LinFieldAnnotation注解
            if (field.isAnnotationPresent(LinFieldAnnotation.class)) {
                LinFieldAnnotation annotation = field.getAnnotation(LinFieldAnnotation.class);
                System.out.println("字段: " + field.getName());
                System.out.println("  value: " + annotation.value());
                System.out.println("  required: " + annotation.required());
                System.out.println("  description: " + annotation.description());
            }
        }
    }

    /**
     * 处理方法级别的注解
     * @param clazz 类对象
     */
    public static void processMethodAnnotations(Class<?> clazz) {
        System.out.println("\n=== 处理方法级别的注解 ===");
        
        // 获取所有方法
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            // 检查方法是否有LinMethodAnnotation注解
            if (method.isAnnotationPresent(LinMethodAnnotation.class)) {
                LinMethodAnnotation annotation = method.getAnnotation(LinMethodAnnotation.class);
                System.out.println("方法: " + method.getName());
                System.out.println("  value: " + annotation.value());
                System.out.println("  author: " + annotation.author());
                System.out.println("  date: " + annotation.date());
                System.out.print("  tags: ");
                String[] tags = annotation.tags();
                for (int i = 0; i < tags.length; i++) {
                    System.out.print(tags[i]);
                    if (i < tags.length - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            }
        }
    }

    /**
     * 处理所有注解
     * @param clazz 类对象
     */
    public static void processAllAnnotations(Class<?> clazz) {
        processClassAnnotation(clazz);
        processFieldAnnotations(clazz);
        processMethodAnnotations(clazz);
    }
}
