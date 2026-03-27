package com.linsir.abc.core.jvm.memory.heap;

import java.util.ArrayList;
import java.util.List;

/**
 * Java堆内存溢出
 * 
 * <p>Java堆用于存储对象实例，只要不断地创建对象，并且保证GC Roots到对象之间有可达路径
 * 来避免垃圾回收，那么在对象数量到达最大堆的容量限制后就会产生内存溢出异常。</p>
 * 
 * <p><strong>VM参数:</strong> -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError</p>
 * 
 * <p><strong>预期异常:</strong> java.lang.OutOfMemoryError: Java heap space</p>
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class HeapOutOfMemory {

    /**
     * 用于占用堆内存的对象
     * 每个对象占用约1MB内存
     */
    static class MemoryObject {
        
        /**
         * 对象数据，用于占用内存
         * 1MB = 1024 * 1024 bytes
         */
        private final byte[] data = new byte[1024 * 1024];
    }

    /**
     * 程序入口
     * 
     * <p>通过不断创建对象并保持引用，导致堆内存溢出。</p>
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 用于保持对象引用，防止被GC回收
        List<MemoryObject> objectList = new ArrayList<>();
        
        System.out.println("开始创建对象，准备触发堆内存溢出...");
        System.out.println("VM参数: -Xms20m -Xmx20m");
        
        try {
            // 无限循环创建对象
            while (true) {
                objectList.add(new MemoryObject());
            }
        } catch (OutOfMemoryError e) {
            System.err.println("捕获到OutOfMemoryError: " + e.getMessage());
            System.err.println("已创建对象数量: " + objectList.size());
            throw e;
        }
    }
}
