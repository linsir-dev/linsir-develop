package com.linsir.abc.core.grammar.variable;

/**
 * 变量作用域示例
 * 
 * 本类演示 Java 中不同作用域的变量：局部变量、成员变量和类变量
 * 对应 JDK: 变量声明和作用域规则
 * 
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class VariableScopes {
    
    /** 
     * 成员变量（实例变量）
     * 属于对象，对象创建时初始化，对象销毁时销毁
     * 存储在堆内存中
     */
    private int instanceVar = 10;
    
    /**
     * 类变量（静态变量）
     * 属于类，类加载时初始化，类卸载时销毁
     * 被所有实例共享，存储在方法区
     */
    private static int staticVar = 20;
    
    /**
     * 演示各种变量作用域
     * 展示局部变量、代码块变量、循环变量的作用范围
     */
    public void demonstrateScopes() {
        System.out.println("=== 变量作用域演示 ===");
        
        // 局部变量：在方法内声明，方法执行期间存在
        // 存储在栈内存中，必须显式初始化
        int localVar = 30;
        
        System.out.println("成员变量 (instanceVar): " + instanceVar);
        System.out.println("类变量 (staticVar): " + staticVar);
        System.out.println("局部变量 (localVar): " + localVar);
        
        // 代码块作用域
        {
            // blockVar 只在代码块内可见
            int blockVar = 40;
            System.out.println("\n代码块内:");
            System.out.println("  代码块变量 (blockVar): " + blockVar);
            // 可以访问外部变量
            System.out.println("  访问局部变量: " + localVar);
        }
        // blockVar 在这里不可见，编译错误
        // System.out.println(blockVar);  // Error!
        
        // 循环变量作用域
        System.out.println("\n循环变量作用域:");
        for (int i = 0; i < 3; i++) {
            // i 只在循环体内可见
            System.out.println("  循环内 i = " + i);
        }
        // i 在这里不可见
        // System.out.println(i);  // Error!
        
        // 条件语句中的变量
        if (localVar > 0) {
            int ifVar = 50;
            System.out.println("\nif 语句块内:");
            System.out.println("  if变量 (ifVar): " + ifVar);
        }
        // ifVar 在这里不可见
        // System.out.println(ifVar);  // Error!
        
        // 可以在不同代码块中使用相同名称的变量
        {
            int blockVar = 60;  // 新的 blockVar，与之前的无关
            System.out.println("\n新代码块内的 blockVar: " + blockVar);
        }
    }
    
    /**
     * 演示变量隐藏（Shadowing）
     * 当局部变量与成员变量同名时，局部变量会隐藏成员变量
     * 
     * @param instanceVar 参数，会隐藏同名的成员变量
     */
    public void demonstrateShadowing(int instanceVar) {
        System.out.println("\n=== 变量隐藏（Shadowing）===");
        
        // 参数 instanceVar 隐藏了成员变量 instanceVar
        System.out.println("参数 instanceVar: " + instanceVar);
        
        // 使用 this 关键字访问被隐藏的成员变量
        System.out.println("成员变量 this.instanceVar: " + this.instanceVar);
        
        // 方法内的局部变量也会隐藏成员变量
        int localInstanceVar = 100;
        System.out.println("局部变量 localInstanceVar: " + localInstanceVar);
    }
    
    /**
     * 演示静态变量的共享特性
     * 静态变量被所有实例共享
     */
    public void demonstrateStaticSharing() {
        System.out.println("\n=== 静态变量共享 ===");
        
        System.out.println("修改前 staticVar = " + staticVar);
        staticVar++;
        System.out.println("修改后 staticVar = " + staticVar);
    }
    
    /**
     * 获取静态变量的值
     * @return 静态变量的当前值
     */
    public static int getStaticVar() {
        return staticVar;
    }
    
    /**
     * 主方法，运行所有演示
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 变量作用域演示                        ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");
        
        // 创建第一个对象
        VariableScopes obj1 = new VariableScopes();
        System.out.println("【对象 1 的演示】");
        obj1.demonstrateScopes();
        obj1.demonstrateShadowing(100);
        
        System.out.println("\n" + "─".repeat(50));
        
        // 创建第二个对象，演示静态变量共享
        VariableScopes obj2 = new VariableScopes();
        System.out.println("\n【对象 2 的演示 - 静态变量共享】");
        System.out.println("对象 1 修改静态变量:");
        obj1.demonstrateStaticSharing();
        
        System.out.println("\n对象 2 看到相同的静态变量值:");
        obj2.demonstrateStaticSharing();
        
        System.out.println("\n通过类名访问静态变量: VariableScopes.getStaticVar() = " 
                          + VariableScopes.getStaticVar());
        
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
        System.out.println("\n总结:");
        System.out.println("• 局部变量：方法/代码块内，栈内存，必须初始化");
        System.out.println("• 成员变量：对象级别，堆内存，自动初始化");
        System.out.println("• 类变量：类级别，方法区，被所有实例共享");
    }
}
