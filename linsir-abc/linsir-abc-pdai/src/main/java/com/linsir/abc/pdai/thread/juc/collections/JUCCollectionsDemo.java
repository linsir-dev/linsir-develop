package com.linsir.abc.pdai.thread.juc.collections;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author linsir
 * @version 1.0
 * @description: 演示JUC框架下的集合类的特征
 * @date 2026/1/22 4:26
 */
public class JUCCollectionsDemo {
    // 1. ConcurrentHashMap示例
    static class ConcurrentHashMapDemo {
        public static void test() {
            System.out.println("1. ConcurrentHashMap示例:");
            ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
            
            // 基本操作
            map.put("key1", 1);
            map.put("key2", 2);
            map.put("key3", 3);
            System.out.println("初始map: " + map);
            
            // 并发操作
            Runnable task = () -> {
                for (int i = 4; i < 10; i++) {
                    map.put("key" + i, i);
                }
            };
            
            Thread t1 = new Thread(task);
            Thread t2 = new Thread(task);
            t1.start();
            t2.start();
            
            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.println("并发操作后map: " + map);
            System.out.println("map大小: " + map.size());
            System.out.println("获取key1: " + map.get("key1"));
            System.out.println("是否包含key5: " + map.containsKey("key5"));
            System.out.println("删除key1: " + map.remove("key1"));
            System.out.println("删除后map: " + map);
            System.out.println();
        }
    }
    
    // 2. ConcurrentLinkedQueue示例
    static class ConcurrentLinkedQueueDemo {
        public static void test() {
            System.out.println("2. ConcurrentLinkedQueue示例:");
            ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
            
            // 基本操作
            queue.offer("元素1");
            queue.offer("元素2");
            queue.offer("元素3");
            System.out.println("初始队列: " + queue);
            System.out.println("队列大小: " + queue.size());
            System.out.println("队首元素: " + queue.peek());
            System.out.println("出队元素: " + queue.poll());
            System.out.println("出队后队列: " + queue);
            
            // 并发操作
            Runnable producer = () -> {
                for (int i = 4; i < 10; i++) {
                    queue.offer("元素" + i);
                }
            };
            
            Runnable consumer = () -> {
                for (int i = 0; i < 5; i++) {
                    System.out.println("消费元素: " + queue.poll());
                }
            };
            
            Thread t1 = new Thread(producer);
            Thread t2 = new Thread(consumer);
            t1.start();
            t2.start();
            
            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.println("最终队列: " + queue);
            System.out.println();
        }
    }
    
    // 3. ArrayBlockingQueue示例
    static class ArrayBlockingQueueDemo {
        public static void test() {
            System.out.println("3. ArrayBlockingQueue示例:");
            // 容量为5的阻塞队列
            ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(5);
            
            // 基本操作
            try {
                queue.put("元素1");
                queue.put("元素2");
                queue.put("元素3");
                queue.put("元素4");
                queue.put("元素5");
                System.out.println("初始队列: " + queue);
                
                // 尝试插入第六个元素（会阻塞）
                Runnable producer = () -> {
                    try {
                        System.out.println("尝试插入元素6...");
                        queue.put("元素6");
                        System.out.println("插入元素6成功");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };
                
                Thread t1 = new Thread(producer);
                t1.start();
                
                // 消费一个元素，让生产者能够插入
                Thread.sleep(1000);
                System.out.println("消费元素: " + queue.take());
                
                t1.join();
                
                System.out.println("最终队列: " + queue);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
    
    // 4. LinkedBlockingQueue示例
    static class LinkedBlockingQueueDemo {
        public static void test() {
            System.out.println("4. LinkedBlockingQueue示例:");
            // 无界阻塞队列（默认容量为Integer.MAX_VALUE）
            LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
            
            // 基本操作
            try {
                queue.put("元素1");
                queue.put("元素2");
                queue.put("元素3");
                System.out.println("初始队列: " + queue);
                System.out.println("队列大小: " + queue.size());
                
                // 并发操作
                Runnable producer = () -> {
                    try {
                        for (int i = 4; i < 10; i++) {
                            queue.put("元素" + i);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };
                
                Runnable consumer = () -> {
                    try {
                        for (int i = 0; i < 5; i++) {
                            System.out.println("消费元素: " + queue.take());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };
                
                Thread t1 = new Thread(producer);
                Thread t2 = new Thread(consumer);
                t1.start();
                t2.start();
                
                t1.join();
                t2.join();
                
                System.out.println("最终队列: " + queue);
                System.out.println("队列大小: " + queue.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
    
    // 5. PriorityBlockingQueue示例
    static class PriorityBlockingQueueDemo {
        static class Task implements Comparable<Task> {
            private int priority;
            private String name;
            
            public Task(int priority, String name) {
                this.priority = priority;
                this.name = name;
            }
            
            @Override
            public int compareTo(Task other) {
                // 优先级高的先执行（数字小的优先级高）
                return Integer.compare(this.priority, other.priority);
            }
            
            @Override
            public String toString() {
                return "Task{" +
                        "priority=" + priority +
                        ", name='" + name + '\'' +
                        '}';
            }
        }
        
        public static void test() {
            System.out.println("5. PriorityBlockingQueue示例:");
            PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<>();
            
            // 添加任务
            queue.put(new Task(3, "任务3"));
            queue.put(new Task(1, "任务1"));
            queue.put(new Task(5, "任务5"));
            queue.put(new Task(2, "任务2"));
            queue.put(new Task(4, "任务4"));
            
            System.out.println("初始队列: " + queue);
            
            // 取出任务（按优先级顺序）
            System.out.println("按优先级取出任务:");
            while (!queue.isEmpty()) {
                try {
                    System.out.println("取出: " + queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println();
        }
    }
    
    // 6. CopyOnWriteArrayList示例
    static class CopyOnWriteArrayListDemo {
        public static void test() {
            System.out.println("6. CopyOnWriteArrayList示例:");
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
            
            // 基本操作
            list.add("元素1");
            list.add("元素2");
            list.add("元素3");
            System.out.println("初始列表: " + list);
            System.out.println("列表大小: " + list.size());
            System.out.println("获取索引1的元素: " + list.get(1));
            System.out.println("是否包含元素2: " + list.contains("元素2"));
            System.out.println("删除元素2: " + list.remove("元素2"));
            System.out.println("删除后列表: " + list);
            
            // 并发操作
            Runnable task = () -> {
                for (int i = 4; i < 10; i++) {
                    list.add("元素" + i);
                }
            };
            
            Thread t1 = new Thread(task);
            Thread t2 = new Thread(task);
            t1.start();
            t2.start();
            
            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.println("并发操作后列表: " + list);
            System.out.println("列表大小: " + list.size());
            System.out.println();
        }
    }
    
    // 7. CopyOnWriteArraySet示例
    static class CopyOnWriteArraySetDemo {
        public static void test() {
            System.out.println("7. CopyOnWriteArraySet示例:");
            CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<>();
            
            // 基本操作
            set.add("元素1");
            set.add("元素2");
            set.add("元素3");
            set.add("元素2"); // 重复元素，不会被添加
            System.out.println("初始集合: " + set);
            System.out.println("集合大小: " + set.size());
            System.out.println("是否包含元素2: " + set.contains("元素2"));
            System.out.println("删除元素2: " + set.remove("元素2"));
            System.out.println("删除后集合: " + set);
            
            // 并发操作
            Runnable task = () -> {
                for (int i = 4; i < 10; i++) {
                    set.add("元素" + i);
                }
            };
            
            Thread t1 = new Thread(task);
            Thread t2 = new Thread(task);
            t1.start();
            t2.start();
            
            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.println("并发操作后集合: " + set);
            System.out.println("集合大小: " + set.size());
            System.out.println();
        }
    }
    
    // 8. SynchronousQueue示例
    static class SynchronousQueueDemo {
        public static void test() {
            System.out.println("8. SynchronousQueue示例:");
            SynchronousQueue<String> queue = new SynchronousQueue<>();
            
            // 生产者线程
            Thread producer = new Thread(() -> {
                try {
                    System.out.println("生产者: 尝试放入元素1");
                    queue.put("元素1");
                    System.out.println("生产者: 放入元素1成功");
                    
                    System.out.println("生产者: 尝试放入元素2");
                    queue.put("元素2");
                    System.out.println("生产者: 放入元素2成功");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            
            // 消费者线程
            Thread consumer = new Thread(() -> {
                try {
                    Thread.sleep(1000); // 等待生产者放入元素
                    System.out.println("消费者: 取出元素: " + queue.take());
                    
                    Thread.sleep(1000);
                    System.out.println("消费者: 取出元素: " + queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            
            producer.start();
            consumer.start();
            
            try {
                producer.join();
                consumer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
    
    // 9. LinkedTransferQueue示例
    static class LinkedTransferQueueDemo {
        public static void test() {
            System.out.println("9. LinkedTransferQueue示例:");
            LinkedTransferQueue<String> queue = new LinkedTransferQueue<>();
            
            // 生产者线程
            Thread producer = new Thread(() -> {
                try {
                    System.out.println("生产者: 尝试transfer元素1");
                    queue.transfer("元素1"); // 阻塞直到消费者接收
                    System.out.println("生产者: transfer元素1成功");
                    
                    System.out.println("生产者: offer元素2");
                    queue.offer("元素2"); // 非阻塞
                    System.out.println("生产者: offer元素2成功");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            
            // 消费者线程
            Thread consumer = new Thread(() -> {
                try {
                    Thread.sleep(1000); // 等待生产者
                    System.out.println("消费者: take元素: " + queue.take());
                    
                    Thread.sleep(1000);
                    System.out.println("消费者: take元素: " + queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            
            producer.start();
            consumer.start();
            
            try {
                producer.join();
                consumer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
    
    // 10. 并发集合性能测试
    static class CollectionPerformanceTest {
        public static void test() {
            System.out.println("10. 并发集合性能测试:");
            final int THREAD_COUNT = 10;
            final int OPERATIONS_PER_THREAD = 10000;
            
            // 测试ConcurrentHashMap
            ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
            long startTime = System.currentTimeMillis();
            
            Thread[] threads = new Thread[THREAD_COUNT];
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        int key = threadId * OPERATIONS_PER_THREAD + j;
                        concurrentHashMap.put(key, key);
                        concurrentHashMap.get(key);
                    }
                });
                threads[i].start();
            }
            
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            long concurrentHashMapTime = System.currentTimeMillis() - startTime;
            System.out.println("ConcurrentHashMap操作时间: " + concurrentHashMapTime + "ms");
            System.out.println("ConcurrentHashMap大小: " + concurrentHashMap.size());
            
            // 测试ConcurrentLinkedQueue
            ConcurrentLinkedQueue<Integer> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
            startTime = System.currentTimeMillis();
            
            for (int i = 0; i < THREAD_COUNT; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        concurrentLinkedQueue.offer(j);
                        concurrentLinkedQueue.poll();
                    }
                });
                threads[i].start();
            }
            
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            long concurrentLinkedQueueTime = System.currentTimeMillis() - startTime;
            System.out.println("ConcurrentLinkedQueue操作时间: " + concurrentLinkedQueueTime + "ms");
            System.out.println("ConcurrentLinkedQueue大小: " + concurrentLinkedQueue.size());
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        ConcurrentHashMapDemo.test();
        ConcurrentLinkedQueueDemo.test();
        ArrayBlockingQueueDemo.test();
        LinkedBlockingQueueDemo.test();
        PriorityBlockingQueueDemo.test();
        CopyOnWriteArrayListDemo.test();
        CopyOnWriteArraySetDemo.test();
        SynchronousQueueDemo.test();
        LinkedTransferQueueDemo.test();
        CollectionPerformanceTest.test();
    }
}
