package com.linsir.abc.pdai.structure.dataStructure;

/**
 * 红黑树示例代码
 * 
 * 说明：
 * 1. 红黑树是一种自平衡的二叉搜索树，它通过在每个节点上添加一个颜色属性（红色或黑色）来维持树的平衡
 * 2. 红黑树的特点：
 *    - 每个节点要么是红色，要么是黑色
 *    - 根节点是黑色
 *    - 每个叶子节点（NIL节点，空节点）是黑色
 *    - 如果一个节点是红色，则其两个子节点都是黑色
 *    - 从任一节点到其每个叶子节点的所有路径都包含相同数目的黑色节点
 * 3. 红黑树的时间复杂度：
 *    - 查找、插入、删除的时间复杂度均为O(log n)
 * 4. 应用场景：
 *    - 需要频繁插入和删除操作的场景
 *    - 需要保持树的平衡以确保查询效率的场景
 *    - 作为关联容器的底层实现（如Java的TreeMap）
 */
public class RedBlackTreeDemo {

    /**
     * 红黑树节点颜色
     */
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    /**
     * 红黑树节点类
     */
    private static class Node {
        int key;
        Object value;
        Node left;
        Node right;
        Node parent;
        boolean color;

        Node(int key, Object value) {
            this.key = key;
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
        private Node root;
        private Node nil; // 哨兵节点，代表空节点

        RedBlackTree() {
            nil = new Node(0, null);
            nil.color = BLACK;
            root = nil;
        }

        /**
         * 左旋操作
         */
        private void leftRotate(Node x) {
            Node y = x.right;
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
         * 右旋操作
         */
        private void rightRotate(Node y) {
            Node x = y.left;
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
         * 插入操作
         */
        public void insert(int key, Object value) {
            Node z = new Node(key, value);
            Node y = nil;
            Node x = root;

            while (x != nil) {
                y = x;
                if (z.key < x.key) {
                    x = x.left;
                } else {
                    x = x.right;
                }
            }

            z.parent = y;

            if (y == nil) {
                root = z;
            } else if (z.key < y.key) {
                y.left = z;
            } else {
                y.right = z;
            }

            z.left = nil;
            z.right = nil;
            z.color = RED;

            insertFixup(z);
        }

        /**
         * 插入修复操作
         */
        private void insertFixup(Node z) {
            while (z.parent.color == RED) {
                if (z.parent == z.parent.parent.left) {
                    Node y = z.parent.parent.right;

                    if (y.color == RED) {
                        z.parent.color = BLACK;
                        y.color = BLACK;
                        z.parent.parent.color = RED;
                        z = z.parent.parent;
                    } else {
                        if (z == z.parent.right) {
                            z = z.parent;
                            leftRotate(z);
                        }
                        z.parent.color = BLACK;
                        z.parent.parent.color = RED;
                        rightRotate(z.parent.parent);
                    }
                } else {
                    Node y = z.parent.parent.left;

                    if (y.color == RED) {
                        z.parent.color = BLACK;
                        y.color = BLACK;
                        z.parent.parent.color = RED;
                        z = z.parent.parent;
                    } else {
                        if (z == z.parent.left) {
                            z = z.parent;
                            rightRotate(z);
                        }
                        z.parent.color = BLACK;
                        z.parent.parent.color = RED;
                        leftRotate(z.parent.parent);
                    }
                }
            }

            root.color = BLACK;
        }

        /**
         * 查找操作
         */
        public Object search(int key) {
            Node node = searchNode(key);
            return node == nil ? null : node.value;
        }

        /**
         * 查找节点
         */
        private Node searchNode(int key) {
            Node x = root;
            while (x != nil && key != x.key) {
                if (key < x.key) {
                    x = x.left;
                } else {
                    x = x.right;
                }
            }
            return x;
        }

        /**
         * 删除操作
         */
        public void delete(int key) {
            Node z = searchNode(key);
            if (z == nil) {
                return; // 节点不存在
            }

            Node y = z;
            Node x;
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
        private void transplant(Node u, Node v) {
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
         * 查找最小节点
         */
        private Node minimum(Node x) {
            while (x.left != nil) {
                x = x.left;
            }
            return x;
        }

        /**
         * 删除修复操作
         */
        private void deleteFixup(Node x) {
            while (x != root && x.color == BLACK) {
                if (x == x.parent.left) {
                    Node w = x.parent.right;

                    if (w.color == RED) {
                        w.color = BLACK;
                        x.parent.color = RED;
                        leftRotate(x.parent);
                        w = x.parent.right;
                    }

                    if (w.left.color == BLACK && w.right.color == BLACK) {
                        w.color = RED;
                        x = x.parent;
                    } else {
                        if (w.right.color == BLACK) {
                            w.left.color = BLACK;
                            w.color = RED;
                            rightRotate(w);
                            w = x.parent.right;
                        }
                        w.color = x.parent.color;
                        x.parent.color = BLACK;
                        w.right.color = BLACK;
                        leftRotate(x.parent);
                        x = root;
                    }
                } else {
                    Node w = x.parent.left;

                    if (w.color == RED) {
                        w.color = BLACK;
                        x.parent.color = RED;
                        rightRotate(x.parent);
                        w = x.parent.left;
                    }

                    if (w.right.color == BLACK && w.left.color == BLACK) {
                        w.color = RED;
                        x = x.parent;
                    } else {
                        if (w.left.color == BLACK) {
                            w.right.color = BLACK;
                            w.color = RED;
                            leftRotate(w);
                            w = x.parent.left;
                        }
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
        public void inorderTraversal() {
            System.out.print("中序遍历: ");
            inorderTraversal(root);
            System.out.println();
        }

        /**
         * 递归中序遍历
         */
        private void inorderTraversal(Node x) {
            if (x != nil) {
                inorderTraversal(x.left);
                System.out.print("[" + x.key + ":" + x.value + "] " + (x.color == RED ? "(R) " : "(B) "));
                inorderTraversal(x.right);
            }
        }

        /**
         * 打印树的结构
         */
        public void printTree() {
            System.out.println("红黑树结构:");
            printTree(root, 0);
        }

        /**
         * 递归打印树的结构
         */
        private void printTree(Node x, int level) {
            if (x != nil) {
                printTree(x.right, level + 1);
                for (int i = 0; i < level; i++) {
                    System.out.print("    ");
                }
                System.out.println(x.key + " " + (x.color == RED ? "(R)" : "(B)"));
                printTree(x.left, level + 1);
            }
        }

        /**
         * 获取树的高度
         */
        public int height() {
            return height(root);
        }

        /**
         * 递归获取树的高度
         */
        private int height(Node x) {
            if (x == nil) {
                return 0;
            }
            return Math.max(height(x.left), height(x.right)) + 1;
        }
    }

    /**
     * 测试红黑树
     */
    public void testRedBlackTree() {
        System.out.println("=== 红黑树示例 ===");
        
        RedBlackTree tree = new RedBlackTree();
        
        // 插入元素
        tree.insert(10, "Value10");
        tree.insert(20, "Value20");
        tree.insert(30, "Value30");
        tree.insert(15, "Value15");
        tree.insert(25, "Value25");
        tree.insert(5, "Value5");
        tree.insert(35, "Value35");
        tree.insert(1, "Value1");
        tree.insert(40, "Value40");
        
        // 打印树的结构
        tree.printTree();
        
        // 中序遍历
        tree.inorderTraversal();
        
        // 获取树的高度
        System.out.println("树的高度: " + tree.height());
        
        // 查找元素
        System.out.println("查找键15: " + tree.search(15));
        System.out.println("查找键22: " + tree.search(22));
        
        // 删除元素
        System.out.println("\n删除键15后:");
        tree.delete(15);
        tree.printTree();
        tree.inorderTraversal();
        
        System.out.println("\n删除键10后:");
        tree.delete(10);
        tree.printTree();
        tree.inorderTraversal();
        
        System.out.println("\n删除键20后:");
        tree.delete(20);
        tree.printTree();
        tree.inorderTraversal();
        
        // 获取树的高度
        System.out.println("树的高度: " + tree.height());
    }

    /**
     * 红黑树应用示例
     */
    public void redBlackTreeApplications() {
        System.out.println("\n=== 红黑树应用示例 ===");
        
        // 示例1：作为有序映射
        System.out.println("示例1：作为有序映射");
        RedBlackTree map = new RedBlackTree();
        
        // 插入键值对
        map.insert(30, "Alice");
        map.insert(10, "Bob");
        map.insert(20, "Charlie");
        map.insert(50, "David");
        map.insert(40, "Eve");
        map.insert(60, "Frank");
        
        System.out.println("有序映射内容:");
        map.inorderTraversal();
        
        // 查找值
        System.out.println("查找键20对应的值: " + map.search(20));
        System.out.println("查找键25对应的值: " + map.search(25));
        
        // 示例2：范围查询
        System.out.println("\n示例2：范围查询");
        // 红黑树的中序遍历天然支持范围查询
        // 可以通过修改代码实现更高效的范围查询
        System.out.println("所有键的有序排列:");
        map.inorderTraversal();
        
        // 示例3：性能对比
        System.out.println("\n示例3：性能对比");
        int operations = 1000;
        
        // 测试红黑树插入性能
        RedBlackTree rbt = new RedBlackTree();
        long startTime = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            rbt.insert((int) (Math.random() * 10000), "Value" + i);
        }
        long rbtInsertTime = System.nanoTime() - startTime;
        System.out.println("红黑树插入" + operations + "个元素的时间: " + rbtInsertTime + " ns");
        System.out.println("红黑树高度: " + rbt.height());
        
        // 测试Java TreeMap插入性能
        java.util.TreeMap<Integer, String> treeMap = new java.util.TreeMap<>();
        startTime = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            treeMap.put((int) (Math.random() * 10000), "Value" + i);
        }
        long treeMapInsertTime = System.nanoTime() - startTime;
        System.out.println("Java TreeMap插入" + operations + "个元素的时间: " + treeMapInsertTime + " ns");
        System.out.println("Java TreeMap大小: " + treeMap.size());
    }
}
