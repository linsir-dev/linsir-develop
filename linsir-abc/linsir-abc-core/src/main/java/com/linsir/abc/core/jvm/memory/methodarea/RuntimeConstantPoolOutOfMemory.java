package com.linsir.abc.core.jvm.memory.methodarea;

import java.util.ArrayList;
import java.util.List;

/**
 * 运行时常量池内存溢出
 * 
 * <p>运行时常量池是方法区的一部分。String.intern()是一个Native方法，
 * 如果字符串常量池中已经包含一个等于此String对象的字符串，则返回代表池中这个字符串的String对象；
 * 否则，将此String对象包含的字符串添加到常量池中，并返回此String对象的引用。</p>
 * 
 * <p><strong>JDK 6 VM参数:</strong> -XX:PermSize=10m -XX:MaxPermSize=10m</p>
 * <p><strong>JDK 7+ VM参数:</strong> -Xms20m -Xmx20m (字符串常量池移到了堆中)</p>
 * 
 * <p><strong>JDK 6预期异常:</strong> java.lang.OutOfMemoryError: PermGen space</p>
 * <p><strong>JDK 7+预期异常:</strong> java.lang.OutOfMemoryError: Java heap space</p>
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class RuntimeConstantPoolOutOfMemory {

    /**
     * 字符串计数器
     */
    private int stringCount = 0;

    /**
     * 程序入口
     * 
     * <p>使用List保持引用，避免Full GC回收常量池中的字符串。
     * 不断调用intern()方法将字符串添加到常量池。</p>
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        RuntimeConstantPoolOutOfMemory demo = new RuntimeConstantPoolOutOfMemory();
        
        System.out.println("开始添加字符串到常量池，准备触发OOM...");
        System.out.println("JDK 6 VM参数: -XX:PermSize=10m -XX:MaxPermSize=10m");
        System.out.println("JDK 7+ VM参数: -Xms20m -Xmx20m");
        
        // 使用List保持引用，避免Full GC回收
        List<String> stringList = new ArrayList<>();
        
        try {
            int i = 0;
            while (true) {
                // 将字符串添加到常量池
                String str = String.valueOf(i++).intern();
                stringList.add(str);
                
                demo.stringCount++;
                if (demo.stringCount % 10000 == 0) {
                    System.out.println("已添加 " + demo.stringCount + " 个字符串到常量池");
                }
            }
        } catch (OutOfMemoryError e) {
            System.err.println("捕获到OutOfMemoryError: " + e.getMessage());
            System.err.println("已添加字符串数量: " + demo.stringCount);
            throw e;
        }
    }
}
