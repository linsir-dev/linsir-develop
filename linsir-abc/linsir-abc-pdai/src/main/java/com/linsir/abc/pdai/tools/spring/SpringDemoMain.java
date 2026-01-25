package com.linsir.abc.pdai.tools.spring;

/**
 * Spring 工具类示例主类
 * 用于运行所有 Spring 工具类的示例代码
 */
public class SpringDemoMain {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("Spring 工具类示例代码验证");
        System.out.println("===========================================");
        
        try {
            // 运行 StringUtils 示例
            System.out.println("\n1. 运行 StringUtils 示例:");
            System.out.println("-------------------------------------------");
            SpringStringUtilsDemo.demonstrateStringUtils();
            
            // 运行 CollectionUtils 示例
            System.out.println("\n2. 运行 CollectionUtils 示例:");
            System.out.println("-------------------------------------------");
            SpringCollectionUtilsDemo.demonstrateCollectionUtils();
            
            // 运行 ObjectUtils 示例
            System.out.println("\n3. 运行 ObjectUtils 示例:");
            System.out.println("-------------------------------------------");
            SpringObjectUtilsDemo.demonstrateObjectUtils();
            
            // 运行 ReflectionUtils 示例
            System.out.println("\n4. 运行 ReflectionUtils 示例:");
            System.out.println("-------------------------------------------");
            SpringReflectionUtilsDemo.demonstrateReflectionUtils();
            
            // 运行 Assert 示例
            System.out.println("\n5. 运行 Assert 示例:");
            System.out.println("-------------------------------------------");
            SpringAssertDemo.demonstrateAssert();
            
            // 运行 ClassUtils 示例
            System.out.println("\n6. 运行 ClassUtils 示例:");
            System.out.println("-------------------------------------------");
            SpringClassUtilsDemo.demonstrateClassUtils();
            
            // 运行 FileCopyUtils 示例
            System.out.println("\n7. 运行 FileCopyUtils 示例:");
            System.out.println("-------------------------------------------");
            SpringFileCopyUtilsDemo.demonstrateFileCopyUtils();
            
            // 运行 ResourceLoader 示例
            System.out.println("\n8. 运行 ResourceLoader 示例:");
            System.out.println("-------------------------------------------");
            SpringResourceLoaderDemo.demonstrateResourceLoader();
            
            System.out.println("\n===========================================");
            System.out.println("Spring 工具类示例代码验证完成！");
            System.out.println("===========================================");
            
        } catch (Exception e) {
            System.err.println("运行示例时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
