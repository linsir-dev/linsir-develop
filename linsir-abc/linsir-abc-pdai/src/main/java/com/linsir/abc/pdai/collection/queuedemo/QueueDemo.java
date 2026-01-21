package com.linsir.abc.pdai.collection.queuedemo;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Queue 队列示例
 * 展示 LinkedList、PriorityQueue、ArrayDeque 的使用方法
 */
public class QueueDemo {

    public static void main(String[] args) {
        System.out.println("=== Queue 队列示例 ===\n");

        // 1. LinkedList 作为队列示例
        System.out.println("1. LinkedList 作为队列示例:");
        System.out.println("   特点: 有序、可重复、基于双向链表实现、FIFO（先进先出）");
        Queue<String> linkedListQueue = new LinkedList<>();
        linkedListQueue.offer("Apple"); // 入队
        linkedListQueue.offer("Banana");
        linkedListQueue.offer("Orange");
        linkedListQueue.offer("Pear");
        System.out.println("   队列元素: " + linkedListQueue);
        System.out.println("   队列大小: " + linkedListQueue.size());
        System.out.println("   队首元素: " + linkedListQueue.peek()); // 查看队首
        System.out.println("   出队元素: " + linkedListQueue.poll()); // 出队
        System.out.println("   出队后队列: " + linkedListQueue);
        System.out.println("   队首元素: " + linkedListQueue.peek());

        // 2. PriorityQueue 示例
        System.out.println("\n2. PriorityQueue 示例:");
        System.out.println("   特点: 基于优先级堆实现、元素按优先级排序、默认小顶堆");
        Queue<Integer> priorityQueue = new PriorityQueue<>();
        priorityQueue.offer(5);
        priorityQueue.offer(1);
        priorityQueue.offer(3);
        priorityQueue.offer(8);
        priorityQueue.offer(2);
        System.out.println("   队列元素: " + priorityQueue);
        System.out.println("   队列大小: " + priorityQueue.size());
        System.out.println("   队首元素: " + priorityQueue.peek()); // 查看队首（最小元素）
        System.out.println("   出队元素: " + priorityQueue.poll()); // 出队（最小元素）
        System.out.println("   出队后队列: " + priorityQueue);
        System.out.println("   队首元素: " + priorityQueue.peek());

        // 3. ArrayDeque 作为双端队列示例
        System.out.println("\n3. ArrayDeque 作为双端队列示例:");
        System.out.println("   特点: 基于可变数组实现、双端队列、两端都可操作、效率高");
        ArrayDeque<String> arrayDeque = new ArrayDeque<>();
        // 从队尾添加元素
        arrayDeque.offerLast("Apple");
        arrayDeque.offerLast("Banana");
        arrayDeque.offerLast("Orange");
        System.out.println("   队列元素: " + arrayDeque);
        System.out.println("   队列大小: " + arrayDeque.size());
        System.out.println("   队首元素: " + arrayDeque.peekFirst());
        System.out.println("   队尾元素: " + arrayDeque.peekLast());
        
        // 从队首添加元素
        arrayDeque.offerFirst("Pear");
        System.out.println("   队首添加 Pear 后: " + arrayDeque);
        
        // 从队首出队
        System.out.println("   队首出队: " + arrayDeque.pollFirst());
        System.out.println("   队首出队后: " + arrayDeque);
        
        // 从队尾出队
        System.out.println("   队尾出队: " + arrayDeque.pollLast());
        System.out.println("   队尾出队后: " + arrayDeque);

        // 4. 队列遍历
        System.out.println("\n4. 队列遍历示例:");
        System.out.println("   使用 for-each 循环:");
        for (String fruit : linkedListQueue) {
            System.out.println("   - " + fruit);
        }

        // 5. 清空队列
        System.out.println("\n5. 清空队列示例:");
        System.out.println("   清空前大小: " + linkedListQueue.size());
        while (!linkedListQueue.isEmpty()) {
            System.out.println("   出队: " + linkedListQueue.poll());
        }
        System.out.println("   清空后大小: " + linkedListQueue.size());
        System.out.println("   是否为空: " + linkedListQueue.isEmpty());

        // 6. 优先队列的字符串排序
        System.out.println("\n6. 优先队列的字符串排序示例:");
        Queue<String> stringPriorityQueue = new PriorityQueue<>();
        stringPriorityQueue.offer("Banana");
        stringPriorityQueue.offer("Apple");
        stringPriorityQueue.offer("Orange");
        stringPriorityQueue.offer("Pear");
        System.out.println("   队列元素: " + stringPriorityQueue);
        System.out.println("   出队顺序:");
        while (!stringPriorityQueue.isEmpty()) {
            System.out.println("   - " + stringPriorityQueue.poll());
        }

        System.out.println("\n=== Queue 队列总结 ===");
        System.out.println("1. LinkedList: 适合作为一般队列使用，支持 FIFO");
        System.out.println("2. PriorityQueue: 适合需要优先级排序的场景");
        System.out.println("3. ArrayDeque: 适合作为双端队列使用，两端操作效率高");
        System.out.println("4. 队列的主要操作:");
        System.out.println("   - offer(): 入队，失败返回 false");
        System.out.println("   - poll(): 出队，队列为空返回 null");
        System.out.println("   - peek(): 查看队首，队列为空返回 null");
        System.out.println("   - size(): 获取队列大小");
        System.out.println("   - isEmpty(): 检查队列是否为空");
    }
}
