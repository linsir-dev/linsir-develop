package com.linsir.abc.pdai.structure.dataStructure;

/**
 * 二叉树示例代码
 * 
 * 说明：
 * 1. 二叉树是一种树状数据结构，每个节点最多有两个子节点（左子节点和右子节点）
 * 2. 二叉树的特点：
 *    - 每个节点最多有两个子节点
 *    - 左子树和右子树是有顺序的，不能颠倒
 *    - 即使某个节点只有一个子节点，也要区分是左子节点还是右子节点
 * 3. 二叉树的常见类型：
 *    - 满二叉树：除了叶子节点外，每个节点都有两个子节点
 *    - 完全二叉树：除了最后一层，其他层的节点数都达到最大值，且最后一层的节点都靠左排列
 *    - 二叉搜索树：左子树上所有节点的值均小于它的根节点的值，右子树上所有节点的值均大于它的根节点的值
 *    - 平衡二叉树：左右子树的高度差不超过1
 */
public class BinaryTreeDemo {

    /**
     * 二叉树节点类
     */
    private static class TreeNode {
        int data;
        TreeNode left;
        TreeNode right;

        TreeNode(int data) {
            this.data = data;
            this.left = null;
            this.right = null;
        }
    }

    /**
     * 二叉树类
     */
    private static class BinaryTree {
        private TreeNode root;

        BinaryTree() {
            this.root = null;
        }

        /**
         * 插入节点（二叉搜索树方式）
         */
        public void insert(int data) {
            root = insertRec(root, data);
        }

        /**
         * 递归插入节点
         */
        private TreeNode insertRec(TreeNode root, int data) {
            if (root == null) {
                root = new TreeNode(data);
                return root;
            }

            if (data < root.data) {
                root.left = insertRec(root.left, data);
            } else if (data > root.data) {
                root.right = insertRec(root.right, data);
            }

            return root;
        }

        /**
         * 中序遍历（左 -> 根 -> 右）
         */
        public void inorderTraversal() {
            System.out.print("中序遍历: ");
            inorderRec(root);
            System.out.println();
        }

        /**
         * 递归中序遍历
         */
        private void inorderRec(TreeNode root) {
            if (root != null) {
                inorderRec(root.left);
                System.out.print(root.data + " ");
                inorderRec(root.right);
            }
        }

        /**
         * 前序遍历（根 -> 左 -> 右）
         */
        public void preorderTraversal() {
            System.out.print("前序遍历: ");
            preorderRec(root);
            System.out.println();
        }

        /**
         * 递归前序遍历
         */
        private void preorderRec(TreeNode root) {
            if (root != null) {
                System.out.print(root.data + " ");
                preorderRec(root.left);
                preorderRec(root.right);
            }
        }

        /**
         * 后序遍历（左 -> 右 -> 根）
         */
        public void postorderTraversal() {
            System.out.print("后序遍历: ");
            postorderRec(root);
            System.out.println();
        }

        /**
         * 递归后序遍历
         */
        private void postorderRec(TreeNode root) {
            if (root != null) {
                postorderRec(root.left);
                postorderRec(root.right);
                System.out.print(root.data + " ");
            }
        }

        /**
         * 层序遍历（广度优先遍历）
         */
        public void levelOrderTraversal() {
            System.out.print("层序遍历: ");
            if (root == null) {
                return;
            }

            java.util.Queue<TreeNode> queue = new java.util.LinkedList<>();
            queue.add(root);

            while (!queue.isEmpty()) {
                TreeNode current = queue.poll();
                System.out.print(current.data + " ");

                if (current.left != null) {
                    queue.add(current.left);
                }
                if (current.right != null) {
                    queue.add(current.right);
                }
            }
            System.out.println();
        }

        /**
         * 查找节点
         */
        public boolean search(int data) {
            return searchRec(root, data);
        }

        /**
         * 递归查找节点
         */
        private boolean searchRec(TreeNode root, int data) {
            if (root == null) {
                return false;
            }

            if (root.data == data) {
                return true;
            }

            if (data < root.data) {
                return searchRec(root.left, data);
            } else {
                return searchRec(root.right, data);
            }
        }

        /**
         * 删除节点
         */
        public void delete(int data) {
            root = deleteRec(root, data);
        }

        /**
         * 递归删除节点
         */
        private TreeNode deleteRec(TreeNode root, int data) {
            if (root == null) {
                return root;
            }

            if (data < root.data) {
                root.left = deleteRec(root.left, data);
            } else if (data > root.data) {
                root.right = deleteRec(root.right, data);
            } else {
                // 节点找到，删除它
                
                // 情况1：叶子节点
                if (root.left == null && root.right == null) {
                    return null;
                }
                // 情况2：只有一个子节点
                else if (root.left == null) {
                    return root.right;
                }
                else if (root.right == null) {
                    return root.left;
                }
                // 情况3：有两个子节点
                else {
                    // 找到右子树中的最小值
                    int minValue = findMin(root.right);
                    // 用最小值替换当前节点
                    root.data = minValue;
                    // 删除右子树中的最小值节点
                    root.right = deleteRec(root.right, minValue);
                }
            }

            return root;
        }

        /**
         * 查找树中的最小值
         */
        private int findMin(TreeNode root) {
            int minValue = root.data;
            while (root.left != null) {
                minValue = root.left.data;
                root = root.left;
            }
            return minValue;
        }

        /**
         * 计算树的高度
         */
        public int height() {
            return heightRec(root);
        }

        /**
         * 递归计算树的高度
         */
        private int heightRec(TreeNode root) {
            if (root == null) {
                return -1;
            }

            int leftHeight = heightRec(root.left);
            int rightHeight = heightRec(root.right);

            return Math.max(leftHeight, rightHeight) + 1;
        }

        /**
         * 检查树是否为二叉搜索树
         */
        public boolean isBST() {
            return isBSTRec(root, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }

        /**
         * 递归检查树是否为二叉搜索树
         */
        private boolean isBSTRec(TreeNode root, int min, int max) {
            if (root == null) {
                return true;
            }

            if (root.data < min || root.data > max) {
                return false;
            }

            return isBSTRec(root.left, min, root.data - 1) && 
                   isBSTRec(root.right, root.data + 1, max);
        }

        /**
         * 计算树的节点数
         */
        public int size() {
            return sizeRec(root);
        }

        /**
         * 递归计算树的节点数
         */
        private int sizeRec(TreeNode root) {
            if (root == null) {
                return 0;
            }

            return 1 + sizeRec(root.left) + sizeRec(root.right);
        }

        /**
         * 计算树的叶子节点数
         */
        public int leafCount() {
            return leafCountRec(root);
        }

        /**
         * 递归计算树的叶子节点数
         */
        private int leafCountRec(TreeNode root) {
            if (root == null) {
                return 0;
            }

            if (root.left == null && root.right == null) {
                return 1;
            }

            return leafCountRec(root.left) + leafCountRec(root.right);
        }

        /**
         * 打印树的结构（简单版）
         */
        public void printTree() {
            System.out.println("二叉树结构:");
            printTreeRec(root, 0);
        }

        /**
         * 递归打印树的结构
         */
        private void printTreeRec(TreeNode root, int level) {
            if (root != null) {
                printTreeRec(root.right, level + 1);
                for (int i = 0; i < level; i++) {
                    System.out.print("    ");
                }
                System.out.println(root.data);
                printTreeRec(root.left, level + 1);
            }
        }
    }

    /**
     * 测试二叉树
     */
    public void testBinaryTree() {
        System.out.println("=== 二叉树示例 ===");
        
        BinaryTree tree = new BinaryTree();
        
        // 插入节点
        tree.insert(50);
        tree.insert(30);
        tree.insert(20);
        tree.insert(40);
        tree.insert(70);
        tree.insert(60);
        tree.insert(80);
        
        // 打印树的结构
        tree.printTree();
        
        // 遍历树
        tree.inorderTraversal();   // 中序遍历：20 30 40 50 60 70 80
        tree.preorderTraversal();  // 前序遍历：50 30 20 40 70 60 80
        tree.postorderTraversal(); // 后序遍历：20 40 30 60 80 70 50
        tree.levelOrderTraversal(); // 层序遍历：50 30 70 20 40 60 80
        
        // 查找节点
        System.out.println("是否包含节点40: " + tree.search(40));
        System.out.println("是否包含节点90: " + tree.search(90));
        
        // 计算树的高度
        System.out.println("树的高度: " + tree.height());
        
        // 计算树的节点数
        System.out.println("树的节点数: " + tree.size());
        
        // 计算树的叶子节点数
        System.out.println("树的叶子节点数: " + tree.leafCount());
        
        // 检查树是否为二叉搜索树
        System.out.println("是否为二叉搜索树: " + tree.isBST());
        
        // 删除节点
        System.out.println("\n删除节点20:");
        tree.delete(20);
        tree.printTree();
        tree.inorderTraversal();
        
        System.out.println("\n删除节点30:");
        tree.delete(30);
        tree.printTree();
        tree.inorderTraversal();
        
        System.out.println("\n删除节点50:");
        tree.delete(50);
        tree.printTree();
        tree.inorderTraversal();
    }

    /**
     * 二叉树应用示例
     */
    public void binaryTreeApplications() {
        System.out.println("\n=== 二叉树应用示例 ===");
        
        // 示例1：二叉搜索树用于快速查找
        System.out.println("示例1：二叉搜索树用于快速查找");
        BinaryTree bst = new BinaryTree();
        
        // 插入大量数据
        int[] data = {50, 30, 20, 40, 70, 60, 80, 10, 25, 35, 45, 65, 75, 85, 5};
        for (int num : data) {
            bst.insert(num);
        }
        
        // 快速查找
        long startTime = System.nanoTime();
        boolean found = bst.search(65);
        long endTime = System.nanoTime();
        System.out.println("查找节点65: " + found + "，耗时: " + (endTime - startTime) + "ns");
        
        // 示例2：二叉树用于排序
        System.out.println("\n示例2：二叉树用于排序");
        System.out.println("中序遍历结果（有序）:");
        bst.inorderTraversal();
        
        // 示例3：二叉树用于表达式解析
        System.out.println("\n示例3：二叉树用于表达式解析");
        // 构建表达式树：(3 + 4) * (5 - 2)
        TreeNode expressionTree = new TreeNode('*');
        expressionTree.left = new TreeNode('+');
        expressionTree.left.left = new TreeNode(3);
        expressionTree.left.right = new TreeNode(4);
        expressionTree.right = new TreeNode('-');
        expressionTree.right.left = new TreeNode(5);
        expressionTree.right.right = new TreeNode(2);
        
        System.out.println("表达式树的中序遍历（显示表达式）:");
        printExpressionTree(expressionTree);
        System.out.println();
        
        // 计算表达式值
        int result = evaluateExpressionTree(expressionTree);
        System.out.println("表达式计算结果: " + result);
    }

    /**
     * 打印表达式树
     */
    private void printExpressionTree(TreeNode root) {
        if (root != null) {
            // 如果是操作符，需要加括号
            if (root.data == '+' || root.data == '-' || root.data == '*' || root.data == '/') {
                System.out.print("(");
            }
            
            printExpressionTree(root.left);
            
            // 打印节点值
            if (root.data == '+' || root.data == '-' || root.data == '*' || root.data == '/') {
                System.out.print((char) root.data + " ");
            } else {
                System.out.print(root.data + " ");
            }
            
            printExpressionTree(root.right);
            
            if (root.data == '+' || root.data == '-' || root.data == '*' || root.data == '/') {
                System.out.print(")");
            }
        }
    }

    /**
     * 计算表达式树的值
     */
    private int evaluateExpressionTree(TreeNode root) {
        if (root == null) {
            return 0;
        }

        // 如果是叶子节点，返回其值
        if (root.left == null && root.right == null) {
            return root.data;
        }

        // 递归计算左右子树
        int leftValue = evaluateExpressionTree(root.left);
        int rightValue = evaluateExpressionTree(root.right);

        // 根据操作符计算结果
        switch (root.data) {
            case '+':
                return leftValue + rightValue;
            case '-':
                return leftValue - rightValue;
            case '*':
                return leftValue * rightValue;
            case '/':
                return leftValue / rightValue;
            default:
                return 0;
        }
    }
}
