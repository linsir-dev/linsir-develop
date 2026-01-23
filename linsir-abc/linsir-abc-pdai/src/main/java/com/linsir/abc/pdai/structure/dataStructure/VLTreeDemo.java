package com.linsir.abc.pdai.structure.dataStructure;

/**
 * Van Emde Boas树（VL树）示例代码
 * 
 * 说明：
 * 1. Van Emde Boas树（简称VL树或vEB树）是一种用于高效存储和查询整数的树形数据结构
 * 2. VL树的特点：
 *    - 支持快速查找、插入、删除、前驱和后继操作
 *    - 时间复杂度为O(log log M)，其中M是可能的键值范围
 *    - 适用于存储无符号整数，且键值范围是固定的
 *    - 空间复杂度较高，为O(M)
 * 3. 应用场景：
 *    - 需要高效处理整数键的场景
 *    - 频繁进行前驱和后继查询的场景
 *    - 键值范围不是特别大的场景
 */
public class VLTreeDemo {

    /**
     * Van Emde Boas树节点类
     */
    private static class VEBNode {
        int u;          // 节点的大小（范围）
        int min;        // 最小值
        int max;        // 最大值
        VEBNode summary; // 摘要节点
        VEBNode[] cluster; // 子集群

        VEBNode(int u) {
            this.u = u;
            this.min = -1;
            this.max = -1;
            
            if (u > 2) {
                int sqrtU = (int) Math.sqrt(u);
                this.summary = new VEBNode(sqrtU);
                this.cluster = new VEBNode[sqrtU];
                for (int i = 0; i < sqrtU; i++) {
                    this.cluster[i] = new VEBNode(sqrtU);
                }
            } else {
                this.summary = null;
                this.cluster = null;
            }
        }
    }

    /**
     * Van Emde Boas树类
     */
    private static class VEBTree {
        private VEBNode root;
        private int universeSize;

        VEBTree(int universeSize) {
            // 确保universeSize是2的幂
            this.universeSize = 1;
            while (this.universeSize < universeSize) {
                this.universeSize <<= 1;
            }
            this.root = new VEBNode(this.universeSize);
        }

        /**
         * 计算高位
         */
        private int high(int x) {
            int sqrtU = (int) Math.sqrt(root.u);
            return x / sqrtU;
        }

        /**
         * 计算低位
         */
        private int low(int x) {
            int sqrtU = (int) Math.sqrt(root.u);
            return x % sqrtU;
        }

        /**
         * 合并高位和低位
         */
        private int index(int high, int low) {
            int sqrtU = (int) Math.sqrt(root.u);
            return high * sqrtU + low;
        }

        /**
         * 插入元素
         */
        public void insert(int x) {
            if (x < 0 || x >= universeSize) {
                throw new IllegalArgumentException("x must be in [0, " + (universeSize - 1) + "]");
            }
            insert(root, x);
        }

        /**
         * 递归插入元素
         */
        private void insert(VEBNode node, int x) {
            if (node.min == -1) {
                node.min = x;
                node.max = x;
                return;
            }

            if (x < node.min) {
                int temp = node.min;
                node.min = x;
                x = temp;
            }

            if (node.u > 2) {
                int high = high(x);
                int low = low(x);
                
                if (node.cluster[high].min == -1) {
                    insert(node.summary, high);
                }
                insert(node.cluster[high], low);
            }

            if (x > node.max) {
                node.max = x;
            }
        }

        /**
         * 删除元素
         */
        public void delete(int x) {
            if (x < 0 || x >= universeSize) {
                throw new IllegalArgumentException("x must be in [0, " + (universeSize - 1) + "]");
            }
            delete(root, x);
        }

        /**
         * 递归删除元素
         */
        private void delete(VEBNode node, int x) {
            if (node.min == node.max) {
                node.min = -1;
                node.max = -1;
                return;
            }

            if (node.min == x) {
                int firstCluster = minimum(node.summary);
                x = index(firstCluster, minimum(node.cluster[firstCluster]));
                node.min = x;
            }

            if (node.u > 2) {
                int high = high(x);
                int low = low(x);
                delete(node.cluster[high], low);
                
                if (node.cluster[high].min == -1) {
                    delete(node.summary, high);
                    if (x == node.max) {
                        int summaryMax = maximum(node.summary);
                        if (summaryMax == -1) {
                            node.max = node.min;
                        } else {
                            node.max = index(summaryMax, maximum(node.cluster[summaryMax]));
                        }
                    }
                } else if (x == node.max) {
                    node.max = index(high, maximum(node.cluster[high]));
                }
            } else {
                if (x == 1) {
                    node.max = 0;
                } else {
                    node.max = 1;
                }
            }
        }

        /**
         * 查找元素
         */
        public boolean search(int x) {
            if (x < 0 || x >= universeSize) {
                return false;
            }
            return search(root, x);
        }

        /**
         * 递归查找元素
         */
        private boolean search(VEBNode node, int x) {
            if (node.min == x || node.max == x) {
                return true;
            }

            if (node.u == 2) {
                return false;
            }

            return search(node.cluster[high(x)], low(x));
        }

        /**
         * 查找最小值
         */
        public int minimum() {
            return minimum(root);
        }

        /**
         * 递归查找最小值
         */
        private int minimum(VEBNode node) {
            return node.min;
        }

        /**
         * 查找最大值
         */
        public int maximum() {
            return maximum(root);
        }

        /**
         * 递归查找最大值
         */
        private int maximum(VEBNode node) {
            return node.max;
        }

        /**
         * 查找前驱
         */
        public int predecessor(int x) {
            if (x < 0 || x >= universeSize) {
                throw new IllegalArgumentException("x must be in [0, " + (universeSize - 1) + "]");
            }
            return predecessor(root, x);
        }

        /**
         * 递归查找前驱
         */
        private int predecessor(VEBNode node, int x) {
            if (node.u == 2) {
                if (x == 1 && node.min == 0) {
                    return 0;
                } else {
                    return -1;
                }
            }

            if (node.max != -1 && x > node.max) {
                return node.max;
            }

            int high = high(x);
            int low = low(x);
            int minLow = node.cluster[high].min;

            if (minLow != -1 && low > minLow) {
                int offset = predecessor(node.cluster[high], low);
                return offset != -1 ? index(high, offset) : -1;
            } else {
                int predCluster = predecessor(node.summary, high);
                if (predCluster == -1) {
                    if (node.min != -1 && x > node.min) {
                        return node.min;
                    } else {
                        return -1;
                    }
                } else {
                    int offset = maximum(node.cluster[predCluster]);
                    return index(predCluster, offset);
                }
            }
        }

        /**
         * 查找后继
         */
        public int successor(int x) {
            if (x < 0 || x >= universeSize) {
                throw new IllegalArgumentException("x must be in [0, " + (universeSize - 1) + "]");
            }
            return successor(root, x);
        }

        /**
         * 递归查找后继
         */
        private int successor(VEBNode node, int x) {
            if (node.u == 2) {
                if (x == 0 && node.max == 1) {
                    return 1;
                } else {
                    return -1;
                }
            }

            if (node.min != -1 && x < node.min) {
                return node.min;
            }

            int high = high(x);
            int low = low(x);
            int maxLow = node.cluster[high].max;

            if (maxLow != -1 && low < maxLow) {
                int offset = successor(node.cluster[high], low);
                return offset != -1 ? index(high, offset) : -1;
            } else {
                int succCluster = successor(node.summary, high);
                if (succCluster == -1) {
                    return -1;
                } else {
                    int offset = minimum(node.cluster[succCluster]);
                    return index(succCluster, offset);
                }
            }
        }

        /**
         * 打印树中的所有元素
         */
        public void printElements() {
            System.out.print("VL树中的元素: ");
            printElements(root, 0);
            System.out.println();
        }

        /**
         * 递归打印树中的所有元素
         */
        private void printElements(VEBNode node, int base) {
            if (node.min == -1) {
                return;
            }

            System.out.print((base + node.min) + " ");

            if (node.u == 2) {
                if (node.max == 1 && node.min == 0) {
                    System.out.print((base + 1) + " ");
                }
                return;
            }

            for (int i = 0; i < (int) Math.sqrt(node.u); i++) {
                printElements(node.cluster[i], base + i * (int) Math.sqrt(node.u));
            }
        }
    }

    /**
     * 测试VL树
     */
    public void testVLTree() {
        System.out.println("=== Van Emde Boas树（VL树）示例 ===");
        
        // 创建一个universeSize为16的VL树（存储0-15的整数）
        VEBTree vebTree = new VEBTree(16);
        
        // 插入元素
        vebTree.insert(2);
        vebTree.insert(5);
        vebTree.insert(8);
        vebTree.insert(12);
        vebTree.insert(3);
        vebTree.insert(10);
        
        System.out.println("插入元素后:");
        vebTree.printElements();
        
        // 查找元素
        System.out.println("查找元素5: " + vebTree.search(5));
        System.out.println("查找元素6: " + vebTree.search(6));
        
        // 查找最小值和最大值
        System.out.println("最小值: " + vebTree.minimum());
        System.out.println("最大值: " + vebTree.maximum());
        
        // 查找前驱和后继
        System.out.println("元素7的前驱: " + vebTree.predecessor(7));
        System.out.println("元素7的后继: " + vebTree.successor(7));
        System.out.println("元素2的前驱: " + vebTree.predecessor(2));
        System.out.println("元素12的后继: " + vebTree.successor(12));
        
        // 删除元素
        System.out.println("\n删除元素5后:");
        vebTree.delete(5);
        vebTree.printElements();
        
        System.out.println("删除元素3后:");
        vebTree.delete(3);
        vebTree.printElements();
        
        // 再次查找
        System.out.println("查找元素5: " + vebTree.search(5));
        System.out.println("元素4的前驱: " + vebTree.predecessor(4));
        System.out.println("元素4的后继: " + vebTree.successor(4));
    }

    /**
     * VL树应用示例
     */
    public void vebTreeApplications() {
        System.out.println("\n=== VL树应用示例 ===");
        
        // 示例1：高效的整数集合操作
        System.out.println("示例1：高效的整数集合操作");
        VEBTree intSet = new VEBTree(1024); // 存储0-1023的整数
        
        // 插入一些整数
        int[] numbers = {100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
        for (int num : numbers) {
            intSet.insert(num);
        }
        
        System.out.println("插入的整数:");
        intSet.printElements();
        
        // 快速查找
        System.out.println("查找500: " + intSet.search(500));
        System.out.println("查找550: " + intSet.search(550));
        
        // 快速查找前驱和后继
        System.out.println("550的前驱: " + intSet.predecessor(550));
        System.out.println("550的后继: " + intSet.successor(550));
        
        // 示例2：时间区间管理
        System.out.println("\n示例2：时间区间管理");
        // 假设我们需要管理时间槽（0-1000），每个时间槽表示一个时间单位
        VEBTree timeSlots = new VEBTree(1001);
        
        // 标记已占用的时间槽
        timeSlots.insert(100);
        timeSlots.insert(101);
        timeSlots.insert(102);
        timeSlots.insert(200);
        timeSlots.insert(201);
        timeSlots.insert(300);
        
        // 查找第一个可用的时间槽
        int availableSlot = -1;
        for (int i = 0; i <= 1000; i++) {
            if (!timeSlots.search(i)) {
                availableSlot = i;
                break;
            }
        }
        System.out.println("第一个可用的时间槽: " + availableSlot);
        
        // 查找给定时间槽之后的第一个可用时间槽
        int start = 105;
        int nextAvailable = -1;
        for (int i = start; i <= 1000; i++) {
            if (!timeSlots.search(i)) {
                nextAvailable = i;
                break;
            }
        }
        System.out.println(start + "之后的第一个可用时间槽: " + nextAvailable);
    }

    /**
     * 性能对比示例
     */
    public void performanceComparison() {
        System.out.println("\n=== 性能对比示例 ===");
        
        int universeSize = 10000;
        int operations = 10000;
        
        // 创建VL树
        VEBTree vebTree = new VEBTree(universeSize);
        
        // 创建HashMap作为对比
        java.util.HashMap<Integer, Boolean> hashMap = new java.util.HashMap<>();
        
        // 测试插入性能
        long startTime = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            int num = (int) (Math.random() * universeSize);
            vebTree.insert(num);
        }
        long vebInsertTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            int num = (int) (Math.random() * universeSize);
            hashMap.put(num, true);
        }
        long hashMapInsertTime = System.nanoTime() - startTime;
        
        System.out.println("插入" + operations + "个元素的时间:");
        System.out.println("VL树: " + vebInsertTime + " ns");
        System.out.println("HashMap: " + hashMapInsertTime + " ns");
        
        // 测试查找性能
        startTime = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            int num = (int) (Math.random() * universeSize);
            vebTree.search(num);
        }
        long vebSearchTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            int num = (int) (Math.random() * universeSize);
            hashMap.containsKey(num);
        }
        long hashMapSearchTime = System.nanoTime() - startTime;
        
        System.out.println("\n查找" + operations + "个元素的时间:");
        System.out.println("VL树: " + vebSearchTime + " ns");
        System.out.println("HashMap: " + hashMapSearchTime + " ns");
        
        // 测试前驱后继性能
        startTime = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            int num = (int) (Math.random() * universeSize);
            vebTree.predecessor(num);
            vebTree.successor(num);
        }
        long vebPredecessorSuccessorTime = System.nanoTime() - startTime;
        
        System.out.println("\n查找" + operations + "个元素的前驱和后继的时间:");
        System.out.println("VL树: " + vebPredecessorSuccessorTime + " ns");
        System.out.println("HashMap: 不支持直接的前驱后继操作");
    }
}
