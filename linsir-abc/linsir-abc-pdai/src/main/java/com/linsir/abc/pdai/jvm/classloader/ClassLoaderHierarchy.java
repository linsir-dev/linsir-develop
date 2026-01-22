package com.linsir.abc.pdai.jvm.classloader;

/**
 * 验证类加载器的层次关系
 */
public class ClassLoaderHierarchy {
    public static void main(String[] args) {
        // 获取当前类的类加载器
        ClassLoader classLoader = ClassLoaderHierarchy.class.getClassLoader();
        System.out.println("当前类的类加载器: " + classLoader);
        
        // 获取父加载器
        ClassLoader parentClassLoader = classLoader.getParent();
        System.out.println("父加载器: " + parentClassLoader);
        
        // 获取祖父加载器
        ClassLoader grandParentClassLoader = parentClassLoader.getParent();
        System.out.println("祖父加载器: " + grandParentClassLoader);
        
        // 获取核心类的类加载器
        ClassLoader bootstrapClassLoader = String.class.getClassLoader();
        System.out.println("String类的类加载器: " + bootstrapClassLoader);
        
        // 获取系统类加载器
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println("系统类加载器: " + systemClassLoader);
        
        // 验证系统类加载器与当前类加载器是否相同
        System.out.println("系统类加载器与当前类加载器是否相同: " + (systemClassLoader == classLoader));
    }
}
