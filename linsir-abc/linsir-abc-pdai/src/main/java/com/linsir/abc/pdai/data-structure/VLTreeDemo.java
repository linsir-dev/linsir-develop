package com.linsir.abc.pdai.data-structure;

/**
 * VL树（Van Emde Boas树）示例代码
 * 
 * 说明：
 * 1. Van Emde Boas树是一种用于高效查找、插入、删除和范围查询的树形数据结构
 * 2. 它主要用于存储整数键，支持以下操作在O(log log M)时间复杂度内完成：
 *    - 查找（search）
 *    - 插入（insert）
 *    - 删除（delete）
 *    - 前驱（predecessor）
 *    - 后继（successor）
 *    - 最小值（minimum）
 *    - 最大值（maximum）
 * 3. 适用范围：
 *    - 键是有界的整数（通常是0到M-1之间的整数）
 *    - M是2的幂次
 * 4. 空间复杂度：O(M)
 * 
 * 注意：本示例实现了一个简化版的Van Emde Boas树，仅用于演示基本概念
 */
public class VLTreeDemo {

    /**
     * Van Emde Boas树节点类
     */
    private static class VEBNode {
        int u; //  universe size
        int min; // 最小值
        int max; // 最大值
        VEBNode summary; // 摘要节点
        VEBNode[] cluster; // 簇节点

        VEBNode(int u) {
            this.u = u;
            this.min = -1;
            this.max = -1;
            
            if (u > 2) {
                int upper = (int) Math.ceil(Math.sqrt(u));
                this.summary = new VEBNode(upper);
                this.cluster = new VEBNode[upper];
                for (int i = 0; i < upper; i++) {
                    this.cluster[i] = new VEBNode((int) Math.floor(Math.sqrt(u)));
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
    private static class VanEmdeBoasTree {
        private VEBNode root;
        private int universeSize;

        /**
         * 构造函数
         * @param universeSize  universe大小，必须是2的幂次
         */
        VanEmdeBoasTree(int universeSize) {
            // 确保universeSize是2的幂次
            if (!isPowerOfTwo(universeSize)) {
                throw new IllegalArgumentException("universeSize必须是2的幂次");
            }
            this.universeSize = universeSize;
            this.root = new VEBNode(universeSize);
        }

        /**
         * 检查一个数是否是2的幂次
         */
        private boolean isPowerOfTwo(int n) {
            return n > 0 && (n & (n - 1)) == 0;
        }

        /**
         * 计算高位
         */
        private int high(int x, int u) {
            return (int) Math.floor(x / Math.sqrt(u));
        }

        /**
         * 计算低位
         */
        private int low(int x, int u) {
            return (int) (x % Math.sqrt(u));
        }

        /**
         * 合并高位和低位
         */
        private int index(int high, int low, int u) {
            return (int) (high * Math.sqrt(u) + low);
        }

        /**
         * 插入元素
         */
        public void insert(int x) {
            if (x < 0 || x >= universeSize) {
                throw new IllegalArgumentException("x必须在[0, " + (universeSize - 1) + "]范围内");
            }
            insert(root, x);
        }

        /**
         * 递归插入元素
         */
        private void insert(VEBNode node, int x) {
            if (node.min == -1) {
                node.min = node.max = x;
                return;
            }

            if (x < node.min) {
                int temp = node.min;
                node.min = x;
                x = temp;
            }

            if (node.u > 2) {
                int high = high(x, node.u);
                int low = low(x, node.u);
                
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
                throw new IllegalArgumentException("x必须在[0, " + (universeSize - 1) + "]范围内");
            }
            delete(root, x);
        }

        /**
         * 递归删除元素
         */
        private void delete(VEBNode node, int x) {
            if (node.min == node.max) {
                node.min = node.max = -1;
                return;
            }

            if (node.u == 2) {
                if (x == 0) {
                    node.min = 1;
                } else {
                    node.min = 0;
                }
                node.max = node.min;
                return;
            }

            if (x == node.min) {
                int firstCluster = minimum(node.summary);
                x = index(firstCluster, minimum(node.cluster[firstCluster]), node.u);
                node.min = x;
            }

            int high = high(x, node.u);
            int low = low(x, node.u);
            delete(node.cluster[high], low);

            if (node.cluster[high].min == -1) {
                delete(node.summary, high);
                if (x == node.max) {
                    int summaryMax = maximum(node.summary);
                    if (summaryMax == -1) {
                        node.max = node.min;
                    } else {
                        node.max = index(summaryMax, maximum(node.cluster[summaryMax]), node.u);
                    }
                }
            } else if (x == node.max) {
                node.max = index(high, maximum(node.cluster[high]), node.u);
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
            if (node.min == -1 || x < node.min || x > node.max) {
                return false;
            }

            if (node.min == x || node.max == x) {
                return true;
            }

            if (node.u == 2) {
                return false;
            }

            int high = high(x, node.u);
            int low = low(x, node.u);
            return search(node.cluster[high], low);
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
                throw new IllegalArgumentException("x必须在[0, " + (universeSize - 1) + "]范围内");
            }
            return predecessor(root, x);
        }

        /**
         * 递归查找前驱
         */
        private int predecessor(VEBNode node, int x) {
            if (node.min == -1) {
                return -1;
            }

            if (x > node.max) {
                return node.max;
            }

            if (node.u == 2) {
                if (x == 1 && node.min == 0) {
                    return 0;
                } else {
                    return -1;
                }
            }

            int high = high(x, node.u);
            int low = low(x, node.u);
            int clusterMin = node.cluster[high].min;

            if (clusterMin != -1 && low > clusterMin) {
                int predLow = predecessor(node.cluster[high], low);
                if (predLow != -1) {
                    return index(high, predLow, node.u);
                }
            }

            int predCluster = predecessor(node.summary, high);
            if (predCluster != -1) {
                int predLow = maximum(node.cluster[predCluster]);
                return index(predCluster, predLow, node.u);
            }

            if (node.min < x) {
                return node.min;
            } else {
                return -1;
            }
        }

        /**
         * 查找后继
         */
        public int successor(int x) {
            if (x < 0 || x >= universeSize) {
                throw new IllegalArgumentException("x必须在[0, " + (universeSize - 1) + "]范围内");
            }
            return successor(root, x);
        }

        /**
         * 递归查找后继
         */
        private int successor(VEBNode node, int x) {
            if (node.min == -1) {
                return -1;
            }

            if (x < node.min) {
                return node.min;
            }

            if (node.u == 2) {
                if (x == 0 && node.max == 1) {
                    return 1;
                } else {
                    return -1;
                }
            }

            int high = high(x, node.u);
            int low = low(x, node.u);
            int clusterMax = node.cluster[high].max;

            if (clusterMax != -1 && low < clusterMax) {
                int succLow = successor(node.cluster[high], low);
                if (succLow != -1) {
                    return index(high, succLow, node.u);
                }
            }

            int succCluster = successor(node.summary, high);
            if (succCluster != -1) {
                int succLow = minimum(node.cluster[succCluster]);
                return index(succCluster, succLow, node.u);
            }

            return -1;
        }

        /**
         * 打印树的内容
         */
        public void print() {
            System.out.println("Van Emde Boas树内容:");
            System.out.println("Universe大小: " + universeSize);
            System.out.println("最小值: " + minimum());
            System.out.println("最大值: " + maximum());
            System.out.println("包含的元素:");
            printElements();
        }

        /**
         * 打印所有元素
         */
        private void printElements() {
            for (int i = 0; i < universeSize; i++) {
                if (search(i)) {
                    System.out.print(i + " ");
                }
            }
            System.out.println();
        }
    }

    /**
     * 测试Van Emde Boas树
     */
    public void testVanEmdeBoasTree() {
        System.out.println("=== Van Emde Boas树示例 ===");
        
        // 创建一个universe大小为16的Van Emde Boas树
        VanEmdeBoasTree vebTree = new VanEmdeBoasTree(16);
        
        // 插入元素
        vebTree.insert(2);
        vebTree.insert(5);
        vebTree.insert(8);
        vebTree.insert(12);
        vebTree.insert(15);
        
        // 打印树的内容
        vebTree.print();
        
        // 测试查找
        System.out.println("查找元素5: " + vebTree.search(5));
        System.out.println("查找元素6: " + vebTree.search(6));
        
        // 测试最小值和最大值
        System.out.println("最小值: " + vebTree.minimum());
        System.out.println("最大值: " + vebTree.maximum());
        
        // 测试前驱和后继
        System.out.println("元素7的前驱: " + vebTree.predecessor(7));
        System.out.println("元素7的后继: " + vebTree.successor(7));
        System.out.println("元素2的前驱: " + vebTree.predecessor(2));
        System.out.println("元素15的后继: " + vebTree.successor(15));
        
        // 测试删除
        System.out.println("\n删除元素8:");
        vebTree.delete(8);
        vebTree.print();
        
        System.out.println("删除元素2:");
        vebTree.delete(2);
        vebTree.print();
        
        // 测试删除后的前驱和后继
        System.out.println("元素5的前驱: " + vebTree.predecessor(5));
        System.out.println("元素5的后继: " + vebTree.successor(5));
    }

    /**
     * 性能比较（与普通数组相比）
     */
    public void performanceComparison() {
        System.out.println("\n=== 性能比较 ===");
        
        int universeSize = 1024; // 2^10
        
        // 创建Van Emde Boas树
        VanEmdeBoasTree vebTree = new VanEmdeBoasTree(universeSize);
        
        // 创建普通布尔数组
        boolean[] array = new boolean[universeSize];
        
        // 测试插入性能
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < universeSize / 2; i++) {
            vebTree.insert(i * 2); // 插入偶数
        }
        long vebInsertTime = System.currentTimeMillis() - startTime;
        
        startTime = System.currentTimeMillis();
        for (int i = 0; i < universeSize / 2; i++) {
            array[i * 2] = true; // 标记偶数为存在
        }
        long arrayInsertTime = System.currentTimeMillis() - startTime;
        
        System.out.println("插入" + (universeSize / 2) + "个元素:");
        System.out.println("Van Emde Boas树: " + vebInsertTime + "ms");
        System.out.println("普通数组: " + arrayInsertTime + "ms");
        
        // 测试查找性能
        startTime = System.currentTimeMillis();
        for (int i = 0; i < universeSize; i++) {
            vebTree.search(i);
        }
        long vebSearchTime = System.currentTimeMillis() - startTime;
        
        startTime = System.currentTimeMillis();
        for (int i = 0; i < universeSize; i++) {
            boolean exists = array[i];
        }
        long arraySearchTime = System.currentTimeMillis() - startTime;
        
        System.out.println("\n查找" + universeSize + "个元素:");
        System.out.println("Van Emde Boas树: " + vebSearchTime + "ms");
        System.out.println("普通数组: " + arraySearchTime + "ms");
        
        // 测试前驱后继性能
        startTime = System.currentTimeMillis();
        for (int i = 0; i < universeSize; i++) {
            vebTree.predecessor(i);
            vebTree.successor(i);
        }
        long vebPredSuccTime = System.currentTimeMillis() - startTime;
        
        startTime = System.currentTimeMillis();
        for (int i = 0; i < universeSize; i++) {
            // 数组实现前驱查找
            int pred = -1;
            for (int j = i - 1; j >= 0; j--) {
                if (array[j]) {
                    pred = j;
                    break;
                }
            }
            
            // 数组实现后继查找
            int succ = -1;
            for (int j = i + 1; j < universeSize; j++) {
                if (array[j]) {
                    succ = j;
                    break;
                }
            }
        }
        long arrayPredSuccTime = System.currentTimeMillis() - startTime;
        
        System.out.println("\n查找" + universeSize + "个元素的前驱和后继:");
        System.out.println("Van Emde Boas树: " + vebPredSuccTime + "ms");
        System.out.println("普通数组: " + arrayPredSuccTime + "ms");
        
        System.out.println("\n结论: Van Emde Boas树在查找前驱和后继操作上表现优异，");
        System.out.println("但在简单的插入和查找操作上，普通数组可能更快。");
        System.out.println("Van Emde Boas树的优势在于处理大范围整数的高效操作。");
    }

    /**
     * Van Emde Boas树的应用示例
     */
    public void applications() {
        System.out.println("\n=== Van Emde Boas树应用示例 ===");
        
        // 示例1: 高效的区间查询
        System.out.println("示例1: 高效的区间查询");
        VanEmdeBoasTree vebTree = new VanEmdeBoasTree(1000);
        
        // 插入一些数据
        int[] data = {100, 200, 300, 400, 500, 600, 700, 800, 900};
        for (int num : data) {
            vebTree.insert(num);
        }
        
        // 查询[250, 750]范围内的所有元素
        System.out.println("查询[250, 750]范围内的所有元素:");
        int current = vebTree.successor(250 - 1);
        while (current != -1 && current <= 750) {
            System.out.print(current + " ");
            current = vebTree.successor(current);
        }
        System.out.println();
        
        // 示例2: 高效的事件调度
        System.out.println("\n示例2: 高效的事件调度");
        // 事件时间点
        int[] eventTimes = {10, 20, 30, 40, 50};
        VanEmdeBoasTree eventTree = new VanEmdeBoasTree(100);
        for (int time : eventTimes) {
            eventTree.insert(time);
        }
        
        // 查找下一个要处理的事件
        int currentTime = 25;
        int nextEvent = eventTree.successor(currentTime - 1);
        System.out.println("当前时间: " + currentTime);
        System.out.println("下一个事件时间: " + nextEvent);
    }
}
