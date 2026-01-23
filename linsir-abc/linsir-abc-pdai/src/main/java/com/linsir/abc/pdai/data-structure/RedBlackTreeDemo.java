package com.linsir.abc.pdai.data-structure;

/**
 * 红黑树示例代码
 * 
 * 说明：
 * 1. 红黑树是一种自平衡的二叉搜索树，每个节点都有一个颜色属性（红色或黑色）
 * 2. 红黑树的性质：
 *    - 每个节点要么是红色，要么是黑色
 *    - 根节点是黑色
 *    - 每个叶子节点（NIL节点）是黑色
 *    - 如果一个节点是红色，则其两个子节点都是黑色（没有连续的红色节点）
 *    - 从任一节点到其每个叶子节点的所有路径都包含相同数目的黑色节点
 * 3. 红黑树的优势：
 *    - 插入、删除、查找操作的时间复杂度均为O(log n)
 *    - 相比于AVL树，红黑树的旋转操作更少，插入和删除操作更高效
 *    - 适用于需要频繁插入和删除操作的场景
 */
public class RedBlackTreeDemo {

    /**
     * 红黑树节点颜色
     */
    private static final boolean RED = false;
    private static final boolean BLACK = true;

    /**
     * 红黑树节点类
     */
    private static class RedBlackNode {
        int value;
        RedBlackNode left;
        RedBlackNode right;
        RedBlackNode parent;
        boolean color;

        RedBlackNode(int value) {
            this.value = value;
            this.left = null;
            this.right = null;
            this.parent = null;
            this.color = RED; // 新节点默认为红色
        }
    }

    /**
     * 红黑树类
     */
    private static class RedBlackTree {
        private RedBlackNode root;
        private RedBlackNode nil; // 哨兵节点，代表空节点

        RedBlackTree() {
            // 创建哨兵节点
            nil = new RedBlackNode(0);
            nil.color = BLACK;
            nil.left = nil;
            nil.right = nil;
            nil.parent = nil;
            
            root = nil;
        }

        /**
         * 左旋转
         */
        private void leftRotate(RedBlackNode x) {
            RedBlackNode y = x.right;
            x.right = y.left;
            
            if (y.left != nil) {
                y.left.parent = x;
            }
            
            y.parent = x.parent;
            
            if (x.parent == nil) {
                root = y;
            } else if (x == x.parent.left) {
                x.parent.left = y;
            } else {
                x.parent.right = y;
            }
            
            y.left = x;
            x.parent = y;
        }

        /**
         * 右旋转
         */
        private void rightRotate(RedBlackNode y) {
            RedBlackNode x = y.left;
            y.left = x.right;
            
            if (x.right != nil) {
                x.right.parent = y;
            }
            
            x.parent = y.parent;
            
            if (y.parent == nil) {
                root = x;
            } else if (y == y.parent.left) {
                y.parent.left = x;
            } else {
                y.parent.right = x;
            }
            
            x.right = y;
            y.parent = x;
        }

        /**
         * 插入节点
         */
        public void insert(int value) {
            RedBlackNode z = new RedBlackNode(value);
            z.left = nil;
            z.right = nil;
            
            RedBlackNode y = nil;
            RedBlackNode x = root;
            
            while (x != nil) {
                y = x;
                if (z.value < x.value) {
                    x = x.left;
                } else {
                    x = x.right;
                }
            }
            
            z.parent = y;
            
            if (y == nil) {
                root = z;
            } else if (z.value < y.value) {
                y.left = z;
            } else {
                y.right = z;
            }
            
            // 插入后修复红黑树性质
            insertFixup(z);
        }

        /**
         * 插入后修复红黑树性质
         */
        private void insertFixup(RedBlackNode z) {
            while (z.parent.color == RED) {
                if (z.parent == z.parent.parent.left) {
                    RedBlackNode y = z.parent.parent.right;
                    
                    // 情况1：叔叔节点是红色
                    if (y.color == RED) {
                        z.parent.color = BLACK;
                        y.color = BLACK;
                        z.parent.parent.color = RED;
                        z = z.parent.parent;
                    } else {
                        // 情况2：叔叔节点是黑色，且z是右孩子
                        if (z == z.parent.right) {
                            z = z.parent;
                            leftRotate(z);
                        }
                        
                        // 情况3：叔叔节点是黑色，且z是左孩子
                        z.parent.color = BLACK;
                        z.parent.parent.color = RED;
                        rightRotate(z.parent.parent);
                    }
                } else {
                    RedBlackNode y = z.parent.parent.left;
                    
                    // 情况1：叔叔节点是红色
                    if (y.color == RED) {
                        z.parent.color = BLACK;
                        y.color = BLACK;
                        z.parent.parent.color = RED;
                        z = z.parent.parent;
                    } else {
                        // 情况2：叔叔节点是黑色，且z是左孩子
                        if (z == z.parent.left) {
                            z = z.parent;
                            rightRotate(z);
                        }
                        
                        // 情况3：叔叔节点是黑色，且z是右孩子
                        z.parent.color = BLACK;
                        z.parent.parent.color = RED;
                        leftRotate(z.parent.parent);
                    }
                }
            }
            
            // 确保根节点是黑色
            root.color = BLACK;
        }

        /**
         * 查找节点
         */
        public RedBlackNode search(int value) {
            return searchRec(root, value);
        }

        /**
         * 递归查找节点
         */
        private RedBlackNode searchRec(RedBlackNode node, int value) {
            if (node == nil || node.value == value) {
                return node;
            }
            
            if (value < node.value) {
                return searchRec(node.left, value);
            } else {
                return searchRec(node.right, value);
            }
        }

        /**
         * 查找最小值节点
         */
        private RedBlackNode minimum(RedBlackNode node) {
            while (node.left != nil) {
                node = node.left;
            }
            return node;
        }

        /**
         * 查找最大值节点
         */
        private RedBlackNode maximum(RedBlackNode node) {
            while (node.right != nil) {
                node = node.right;
            }
            return node;
        }

        /**
         * 查找节点的后继节点
         */
        private RedBlackNode successor(RedBlackNode node) {
            if (node.right != nil) {
                return minimum(node.right);
            }
            
            RedBlackNode y = node.parent;
            while (y != nil && node == y.right) {
                node = y;
                y = y.parent;
            }
            return y;
        }

        /**
         * 删除节点
         */
        public void delete(int value) {
            RedBlackNode z = search(value);
            if (z == nil) {
                return; // 节点不存在
            }
            
            RedBlackNode y = z;
            RedBlackNode x;
            boolean yOriginalColor = y.color;
            
            if (z.left == nil) {
                x = z.right;
                transplant(z, z.right);
            } else if (z.right == nil) {
                x = z.left;
                transplant(z, z.left);
            } else {
                y = minimum(z.right);
                yOriginalColor = y.color;
                x = y.right;
                
                if (y.parent == z) {
                    x.parent = y;
                } else {
                    transplant(y, y.right);
                    y.right = z.right;
                    y.right.parent = y;
                }
                
                transplant(z, y);
                y.left = z.left;
                y.left.parent = y;
                y.color = z.color;
            }
            
            if (yOriginalColor == BLACK) {
                deleteFixup(x);
            }
        }

        /**
         * 替换子树
         */
        private void transplant(RedBlackNode u, RedBlackNode v) {
            if (u.parent == nil) {
                root = v;
            } else if (u == u.parent.left) {
                u.parent.left = v;
            } else {
                u.parent.right = v;
            }
            v.parent = u.parent;
        }

        /**
         * 删除后修复红黑树性质
         */
        private void deleteFixup(RedBlackNode x) {
            while (x != root && x.color == BLACK) {
                if (x == x.parent.left) {
                    RedBlackNode w = x.parent.right;
                    
                    // 情况1：兄弟节点是红色
                    if (w.color == RED) {
                        w.color = BLACK;
                        x.parent.color = RED;
                        leftRotate(x.parent);
                        w = x.parent.right;
                    }
                    
                    // 情况2：兄弟节点是黑色，且兄弟的两个子节点都是黑色
                    if (w.left.color == BLACK && w.right.color == BLACK) {
                        w.color = RED;
                        x = x.parent;
                    } else {
                        // 情况3：兄弟节点是黑色，且兄弟的左子节点是红色，右子节点是黑色
                        if (w.right.color == BLACK) {
                            w.left.color = BLACK;
                            w.color = RED;
                            rightRotate(w);
                            w = x.parent.right;
                        }
                        
                        // 情况4：兄弟节点是黑色，且兄弟的右子节点是红色
                        w.color = x.parent.color;
                        x.parent.color = BLACK;
                        w.right.color = BLACK;
                        leftRotate(x.parent);
                        x = root;
                    }
                } else {
                    RedBlackNode w = x.parent.left;
                    
                    // 情况1：兄弟节点是红色
                    if (w.color == RED) {
                        w.color = BLACK;
                        x.parent.color = RED;
                        rightRotate(x.parent);
                        w = x.parent.left;
                    }
                    
                    // 情况2：兄弟节点是黑色，且兄弟的两个子节点都是黑色
                    if (w.right.color == BLACK && w.left.color == BLACK) {
                        w.color = RED;
                        x = x.parent;
                    } else {
                        // 情况3：兄弟节点是黑色，且兄弟的右子节点是红色，左子节点是黑色
                        if (w.left.color == BLACK) {
                            w.right.color = BLACK;
                            w.color = RED;
                            leftRotate(w);
                            w = x.parent.left;
                        }
                        
                        // 情况4：兄弟节点是黑色，且兄弟的左子节点是红色
                        w.color = x.parent.color;
                        x.parent.color = BLACK;
                        w.left.color = BLACK;
                        rightRotate(x.parent);
                        x = root;
                    }
                }
            }
            
            x.color = BLACK;
        }

        /**
         * 中序遍历
         */
        public void inOrderTraversal() {
            System.out.print("中序遍历: ");
            inOrderTraversalRec(root);
            System.out.println();
        }

        /**
         * 递归中序遍历
         */
        private void inOrderTraversalRec(RedBlackNode node) {
            if (node != nil) {
                inOrderTraversalRec(node.left);
                System.out.print(node.value + "(" + (node.color == RED ? "R" : "B") + ") ");
                inOrderTraversalRec(node.right);
            }
        }

        /**
         * 打印树的结构（简单可视化）
         */
        public void print() {
            System.out.println("红黑树结构:");
            printRec(root, 0);
        }

        /**
         * 递归打印树的结构
         */
        private void printRec(RedBlackNode node, int level) {
            if (node != nil) {
                printRec(node.right, level + 1);
                for (int i = 0; i < level; i++) {
                    System.out.print("    ");
                }
                System.out.println(node.value + "(" + (node.color == RED ? "R" : "B") + ")");
                printRec(node.left, level + 1);
            }
        }

        /**
         * 获取树的高度
         */
        public int height() {
            return heightRec(root);
        }

        /**
         * 递归获取树的高度
         */
        private int heightRec(RedBlackNode node) {
            if (node == nil) {
                return 0;
            }
            
            int leftHeight = heightRec(node.left);
            int rightHeight = heightRec(node.right);
            
            return Math.max(leftHeight, rightHeight) + 1;
        }
    }

    /**
     * 测试红黑树
     */
    public void testRedBlackTree() {
        System.out.println("=== 红黑树示例 ===");
        
        RedBlackTree tree = new RedBlackTree();
        
        // 插入节点
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(15);
        tree.insert(5);
        tree.insert(25);
        tree.insert(35);
        tree.insert(40);
        
        // 打印树结构
        tree.print();
        
        // 中序遍历
        tree.inOrderTraversal();
        
        // 测试查找
        System.out.println("查找节点25: " + (tree.search(25) != tree.nil));
        System.out.println("查找节点100: " + (tree.search(100) != tree.nil));
        
        // 测试删除
        System.out.println("\n删除节点20:");
        tree.delete(20);
        tree.print();
        tree.inOrderTraversal();
        
        System.out.println("\n删除节点10:");
        tree.delete(10);
        tree.print();
        tree.inOrderTraversal();
        
        // 测试树的高度
        System.out.println("树的高度: " + tree.height());
    }

    /**
     * 红黑树性能测试
     */
    public void performanceTest() {
        System.out.println("\n=== 红黑树性能测试 ===");
        
        RedBlackTree tree = new RedBlackTree();
        int size = 10000;
        
        // 测试插入性能
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            tree.insert(i);
        }
        long insertTime = System.currentTimeMillis() - startTime;
        System.out.println("插入" + size + "个元素耗时: " + insertTime + "ms");
        
        // 测试查找性能
        startTime = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            tree.search(i);
        }
        long searchTime = System.currentTimeMillis() - startTime;
        System.out.println("查找" + size + "个元素耗时: " + searchTime + "ms");
        
        // 测试删除性能
        startTime = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            tree.delete(i);
        }
        long deleteTime = System.currentTimeMillis() - startTime;
        System.out.println("删除" + size + "个元素耗时: " + deleteTime + "ms");
    }

    /**
     * 红黑树的应用示例
     */
    public void applications() {
        System.out.println("\n=== 红黑树应用示例 ===");
        
        // 示例1: 实现有序映射
        System.out.println("示例1: 实现有序映射");
        // 这里可以使用Java内置的TreeMap，它基于红黑树实现
        java.util.TreeMap<Integer, String> treeMap = new java.util.TreeMap<>();
        
        // 添加键值对
        treeMap.put(10, "Alice");
        treeMap.put(20, "Bob");
        treeMap.put(15, "Charlie");
        treeMap.put(5, "David");
        treeMap.put(25, "Eve");
        
        // 遍历（自动按键排序）
        System.out.println("遍历TreeMap（按键排序）:");
        for (java.util.Map.Entry<Integer, String> entry : treeMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        // 查找范围
        System.out.println("\n查找键大于10的所有元素:");
        for (java.util.Map.Entry<Integer, String> entry : treeMap.tailMap(10, false).entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        // 示例2: 实现优先级队列
        System.out.println("\n示例2: 实现优先级队列");
        // 这里可以使用Java内置的PriorityQueue，它基于堆实现
        // 但如果需要有序遍历，红黑树更合适
        
        // 示例3: Linux内核中的CFS调度器
        System.out.println("\n示例3: Linux内核中的CFS调度器");
        System.out.println("Linux内核的完全公平调度器(CFS)使用红黑树来管理进程的虚拟运行时间，");
        System.out.println("确保每个进程都能获得公平的CPU时间片。");
    }
}
