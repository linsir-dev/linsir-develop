package com.linsir.abc.pdai.data-structure;

/**
 * 二叉树示例代码
 * 
 * 说明：
 * 1. 二叉树是一种树形数据结构，每个节点最多有两个子节点
 * 2. 二叉树的特点：
 *    - 每个节点最多有两个子节点（左子节点和右子节点）
 *    - 左子树和右子树也是二叉树
 *    - 二叉树可以是空树
 * 3. 常见的二叉树类型：
 *    - 满二叉树：除了叶子节点，每个节点都有两个子节点
 *    - 完全二叉树：除了最后一层，其他层的节点数都达到最大值，且最后一层的节点都靠左排列
 *    - 平衡二叉树：左右子树的高度差不超过1
 *    - 二叉搜索树：左子树的所有节点值小于根节点，右子树的所有节点值大于根节点
 */
public class BinaryTreeDemo {

    /**
     * 二叉树节点类
     */
    private static class TreeNode {
        int value;
        TreeNode left;
        TreeNode right;

        TreeNode(int value) {
            this.value = value;
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
        public void insert(int value) {
            root = insertRec(root, value);
        }

        /**
         * 递归插入节点
         */
        private TreeNode insertRec(TreeNode root, int value) {
            if (root == null) {
                return new TreeNode(value);
            }

            if (value < root.value) {
                root.left = insertRec(root.left, value);
            } else if (value > root.value) {
                root.right = insertRec(root.right, value);
            }

            return root;
        }

        /**
         * 查找节点
         */
        public boolean search(int value) {
            return searchRec(root, value);
        }

        /**
         * 递归查找节点
         */
        private boolean searchRec(TreeNode root, int value) {
            if (root == null) {
                return false;
            }

            if (root.value == value) {
                return true;
            }

            if (value < root.value) {
                return searchRec(root.left, value);
            } else {
                return searchRec(root.right, value);
            }
        }

        /**
         * 删除节点
         */
        public void delete(int value) {
            root = deleteRec(root, value);
        }

        /**
         * 递归删除节点
         */
        private TreeNode deleteRec(TreeNode root, int value) {
            if (root == null) {
                return null;
            }

            if (value < root.value) {
                root.left = deleteRec(root.left, value);
            } else if (value > root.value) {
                root.right = deleteRec(root.right, value);
            } else {
                // 找到要删除的节点
                // 情况1：叶子节点
                if (root.left == null && root.right == null) {
                    return null;
                }
                // 情况2：只有一个子节点
                else if (root.left == null) {
                    return root.right;
                } else if (root.right == null) {
                    return root.left;
                }
                // 情况3：有两个子节点
                else {
                    // 找到右子树中的最小值
                    int minValue = findMin(root.right);
                    root.value = minValue;
                    // 删除右子树中的最小值节点
                    root.right = deleteRec(root.right, minValue);
                }
            }

            return root;
        }

        /**
         * 查找最小值
         */
        private int findMin(TreeNode root) {
            int minValue = root.value;
            while (root.left != null) {
                minValue = root.left.value;
                root = root.left;
            }
            return minValue;
        }

        /**
         * 前序遍历（根-左-右）
         */
        public void preOrder() {
            System.out.print("前序遍历: ");
            preOrderRec(root);
            System.out.println();
        }

        /**
         * 递归前序遍历
         */
        private void preOrderRec(TreeNode root) {
            if (root != null) {
                System.out.print(root.value + " ");
                preOrderRec(root.left);
                preOrderRec(root.right);
            }
        }

        /**
         * 中序遍历（左-根-右）
         */
        public void inOrder() {
            System.out.print("中序遍历: ");
            inOrderRec(root);
            System.out.println();
        }

        /**
         * 递归中序遍历
         */
        private void inOrderRec(TreeNode root) {
            if (root != null) {
                inOrderRec(root.left);
                System.out.print(root.value + " ");
                inOrderRec(root.right);
            }
        }

        /**
         * 后序遍历（左-右-根）
         */
        public void postOrder() {
            System.out.print("后序遍历: ");
            postOrderRec(root);
            System.out.println();
        }

        /**
         * 递归后序遍历
         */
        private void postOrderRec(TreeNode root) {
            if (root != null) {
                postOrderRec(root.left);
                postOrderRec(root.right);
                System.out.print(root.value + " ");
            }
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
                return 0;
            }

            int leftHeight = heightRec(root.left);
            int rightHeight = heightRec(root.right);

            return Math.max(leftHeight, rightHeight) + 1;
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

            return sizeRec(root.left) + sizeRec(root.right) + 1;
        }

        /**
         * 检查是否为平衡二叉树
         */
        public boolean isBalanced() {
            return isBalancedRec(root);
        }

        /**
         * 递归检查是否为平衡二叉树
         */
        private boolean isBalancedRec(TreeNode root) {
            if (root == null) {
                return true;
            }

            int leftHeight = heightRec(root.left);
            int rightHeight = heightRec(root.right);

            if (Math.abs(leftHeight - rightHeight) <= 1 && 
                isBalancedRec(root.left) && 
                isBalancedRec(root.right)) {
                return true;
            }

            return false;
        }

        /**
         * 打印树的结构（简单可视化）
         */
        public void print() {
            System.out.println("二叉树结构:");
            printRec(root, 0);
        }

        /**
         * 递归打印树的结构
         */
        private void printRec(TreeNode root, int level) {
            if (root != null) {
                printRec(root.right, level + 1);
                for (int i = 0; i < level; i++) {
                    System.out.print("    ");
                }
                System.out.println(root.value);
                printRec(root.left, level + 1);
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
        tree.insert(70);
        tree.insert(20);
        tree.insert(40);
        tree.insert(60);
        tree.insert(80);
        
        // 打印树结构
        tree.print();
        
        // 遍历
        tree.preOrder();
        tree.inOrder();
        tree.postOrder();
        
        // 查找节点
        System.out.println("查找节点40: " + tree.search(40));
        System.out.println("查找节点90: " + tree.search(90));
        
        // 计算树的高度和节点数
        System.out.println("树的高度: " + tree.height());
        System.out.println("树的节点数: " + tree.size());
        
        // 检查是否为平衡二叉树
        System.out.println("是否为平衡二叉树: " + tree.isBalanced());
        
        // 删除节点
        System.out.println("\n删除节点20:");
        tree.delete(20);
        tree.print();
        
        System.out.println("删除节点30:");
        tree.delete(30);
        tree.print();
        
        System.out.println("删除节点50:");
        tree.delete(50);
        tree.print();
        
        // 再次检查平衡状态
        System.out.println("是否为平衡二叉树: " + tree.isBalanced());
    }

    /**
     * 二叉搜索树的应用示例
     */
    public void binarySearchTreeApplications() {
        System.out.println("\n=== 二叉搜索树应用示例 ===");
        
        // 创建一个二叉搜索树
        BinaryTree bst = new BinaryTree();
        
        // 插入一些数据
        int[] data = {15, 10, 20, 8, 12, 16, 25};
        for (int num : data) {
            bst.insert(num);
        }
        
        System.out.println("原始二叉搜索树:");
        bst.print();
        
        // 示例1: 查找最小值和最大值
        System.out.println("\n示例1: 查找最小值和最大值");
        int min = findMinValue(bst.root);
        int max = findMaxValue(bst.root);
        System.out.println("最小值: " + min);
        System.out.println("最大值: " + max);
        
        // 示例2: 查找某个节点的父节点
        System.out.println("\n示例2: 查找父节点");
        TreeNode parent = findParent(bst.root, 12);
        if (parent != null) {
            System.out.println("节点12的父节点值: " + parent.value);
        } else {
            System.out.println("节点12没有父节点（可能是根节点或不存在）");
        }
        
        // 示例3: 查找某个节点的后继节点
        System.out.println("\n示例3: 查找后继节点");
        TreeNode node = findNode(bst.root, 15);
        if (node != null) {
            TreeNode successor = findSuccessor(bst.root, node);
            if (successor != null) {
                System.out.println("节点15的后继节点值: " + successor.value);
            } else {
                System.out.println("节点15没有后继节点");
            }
        }
    }

    /**
     * 查找二叉树中的最小值
     */
    private int findMinValue(TreeNode root) {
        if (root == null) {
            throw new IllegalArgumentException("树为空");
        }

        while (root.left != null) {
            root = root.left;
        }
        return root.value;
    }

    /**
     * 查找二叉树中的最大值
     */
    private int findMaxValue(TreeNode root) {
        if (root == null) {
            throw new IllegalArgumentException("树为空");
        }

        while (root.right != null) {
            root = root.right;
        }
        return root.value;
    }

    /**
     * 查找节点的父节点
     */
    private TreeNode findParent(TreeNode root, int value) {
        if (root == null || root.value == value) {
            return null;
        }

        if ((root.left != null && root.left.value == value) || 
            (root.right != null && root.right.value == value)) {
            return root;
        }

        if (value < root.value) {
            return findParent(root.left, value);
        } else {
            return findParent(root.right, value);
        }
    }

    /**
     * 查找指定值的节点
     */
    private TreeNode findNode(TreeNode root, int value) {
        if (root == null || root.value == value) {
            return root;
        }

        if (value < root.value) {
            return findNode(root.left, value);
        } else {
            return findNode(root.right, value);
        }
    }

    /**
     * 查找节点的后继节点
     */
    private TreeNode findSuccessor(TreeNode root, TreeNode node) {
        if (node.right != null) {
            // 情况1: 节点有右子树，后继是右子树的最小值
            return findMinNode(node.right);
        }

        // 情况2: 节点没有右子树，后继是其祖先中第一个左孩子是该节点的节点
        TreeNode successor = null;
        TreeNode current = root;

        while (current != null) {
            if (node.value < current.value) {
                successor = current;
                current = current.left;
            } else if (node.value > current.value) {
                current = current.right;
            } else {
                break;
            }
        }

        return successor;
    }

    /**
     * 查找以root为根的树中的最小值节点
     */
    private TreeNode findMinNode(TreeNode root) {
        while (root.left != null) {
            root = root.left;
        }
        return root;
    }

    /**
     * 测试平衡二叉树
     */
    public void testBalancedTree() {
        System.out.println("\n=== 平衡二叉树测试 ===");
        
        // 创建平衡二叉树
        BinaryTree balancedTree = new BinaryTree();
        balancedTree.insert(50);
        balancedTree.insert(30);
        balancedTree.insert(70);
        balancedTree.insert(20);
        balancedTree.insert(40);
        balancedTree.insert(60);
        balancedTree.insert(80);
        
        System.out.println("平衡二叉树:");
        balancedTree.print();
        System.out.println("是否为平衡二叉树: " + balancedTree.isBalanced());
        
        // 创建非平衡二叉树
        BinaryTree unbalancedTree = new BinaryTree();
        unbalancedTree.insert(10);
        unbalancedTree.insert(20);
        unbalancedTree.insert(30);
        unbalancedTree.insert(40);
        unbalancedTree.insert(50);
        
        System.out.println("\n非平衡二叉树:");
        unbalancedTree.print();
        System.out.println("是否为平衡二叉树: " + unbalancedTree.isBalanced());
    }
}
