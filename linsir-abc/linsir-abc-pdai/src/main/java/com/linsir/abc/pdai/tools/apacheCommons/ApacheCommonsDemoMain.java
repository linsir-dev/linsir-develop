package com.linsir.abc.pdai.tools.apacheCommons;

/**
 * Apache Commons 示例代码验证主类
 * 用于运行和验证所有 Apache Commons 库的示例代码
 */
public class ApacheCommonsDemoMain {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("Apache Commons 库示例代码验证");
        System.out.println("==========================================");
        
        // 运行 Commons Lang3 示例
        System.out.println("\n1. 运行 Apache Commons Lang3 示例:");
        System.out.println("------------------------------------------");
        CommonsLang3Demo.demonstrateStringUtils();
        CommonsLang3Demo.demonstrateNumberUtils();
        CommonsLang3Demo.demonstrateDateUtils();
        CommonsLang3Demo.demonstrateRandomStringUtils();
        CommonsLang3Demo.demonstrateObjectUtils();
        
        // 运行 Commons Collections4 示例
        System.out.println("\n2. 运行 Apache Commons Collections4 示例:");
        System.out.println("------------------------------------------");
        CommonsCollections4Demo.demonstrateCollectionUtils();
        CommonsCollections4Demo.demonstrateListUtils();
        CommonsCollections4Demo.demonstrateMapUtils();
        CommonsCollections4Demo.demonstrateBidiMap();
        CommonsCollections4Demo.demonstrateLRUMap();
        CommonsCollections4Demo.demonstrateHashedMap();
        
        // 运行 Commons IO 示例
        System.out.println("\n3. 运行 Apache Commons IO 示例:");
        System.out.println("------------------------------------------");
        CommonsIODemo.demonstrateFileUtils();
        CommonsIODemo.demonstrateIOUtils();
        CommonsIODemo.demonstrateLineIterator();
        CommonsIODemo.demonstrateFilenameUtils();
        CommonsIODemo.demonstrateFileFilterUtils();
        
        System.out.println("\n==========================================");
        System.out.println("Apache Commons 库示例代码验证完成！");
        System.out.println("==========================================");
    }
}
