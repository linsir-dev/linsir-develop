package com.linsir.abc.core.jvm.memory.methodarea;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * 方法区(元空间)内存溢出
 * 
 * <p>方法区用于存放Class的相关信息，如类名、访问修饰符、常量池、字段描述、方法描述等。
 * 借助CGLib字节码技术动态生成大量的类，可以填满方法区，触发OOM。</p>
 * 
 * <p><strong>JDK 7及之前VM参数:</strong> -XX:PermSize=10m -XX:MaxPermSize=10m</p>
 * <p><strong>JDK 8+ VM参数:</strong> -XX:MetaspaceSize=10m -XX:MaxMetaspaceSize=10m</p>
 * 
 * <p><strong>JDK 7预期异常:</strong> java.lang.OutOfMemoryError: PermGen space</p>
 * <p><strong>JDK 8+预期异常:</strong> java.lang.OutOfMemoryError: Metaspace</p>
 * 
 * <p><strong>依赖:</strong> 需要cglib库 (cglib:cglib:3.3.0)</p>
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class MethodAreaOutOfMemory {

    /**
     * 用于动态生成子类的目标类
     */
    public static class TargetClass {
        
        /**
         * 示例方法
         */
        public void doSomething() {
            System.out.println("Doing something...");
        }
    }

    /**
     * 类生成计数器
     */
    private int classCount = 0;

    /**
     * 通过CGLib动态生成大量类来触发方法区OOM
     * 
     * <p>使用CGLib的Enhancer不断创建动态代理类，每个类都会加载到方法区(元空间)。
     * 当方法区空间耗尽时，抛出OOM。</p>
     */
    public void generateClasses() {
        while (true) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(TargetClass.class);
            // 禁用缓存，确保每次都生成新类
            enhancer.setUseCache(false);
            // 设置方法拦截器
            enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) 
                -> proxy.invokeSuper(obj, args));
            
            // 创建代理类实例
            enhancer.create();
            
            classCount++;
            if (classCount % 100 == 0) {
                System.out.println("已生成 " + classCount + " 个动态类");
            }
        }
    }

    /**
     * 程序入口
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        MethodAreaOutOfMemory demo = new MethodAreaOutOfMemory();
        
        System.out.println("开始生成动态类，准备触发方法区内存溢出...");
        System.out.println("JDK 7 VM参数: -XX:PermSize=10m -XX:MaxPermSize=10m");
        System.out.println("JDK 8+ VM参数: -XX:MetaspaceSize=10m -XX:MaxMetaspaceSize=10m");
        
        try {
            demo.generateClasses();
        } catch (OutOfMemoryError e) {
            System.err.println("捕获到OutOfMemoryError: " + e.getMessage());
            System.err.println("已生成类数量: " + demo.classCount);
            throw e;
        }
    }
}
