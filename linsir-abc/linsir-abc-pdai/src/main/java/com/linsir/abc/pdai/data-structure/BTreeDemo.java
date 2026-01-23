package com.linsir.abc.pdai.data-structure;

import java.util.ArrayList;
import java.util.List;

/**
 * B树示例代码
 * 
 * 说明：
 * 1. B树是一种自平衡的多路搜索树，特别适合于磁盘等外存储设备
 * 2. B树的特点：
 *    - 每个节点可以有多个键和多个子节点
 *    - 所有叶子节点都在同一层
 *    - 每个节点的键按升序排列
 *    - 对于一个m阶B树：
 *      - 根节点至少有2个子节点（除非它是叶子节点）
 *      - 非根节点至少有⌈m/2⌉个子节点
 *      - 每个节点最多有m个子节点
 *      - 每个节点的键数比子节点数少1
 * 3. B树的优势：
 *    - 减少了磁盘I/O操作次数
 *    - 支持高效的查找、插入和删除操作
 *    - 适合存储大量数据
 * 4. 应用场景：
 *    - 数据库索引
 *    - 文件系统
 *    - 其他需要处理大量数据的场景
 */
public class BTreeDemo {

    /**
     * B树节点类
     */
    private static class BTreeNode {
        List<Integer> keys; // 键列表
        List<BTreeNode> children; // 子节点列表
        boolean isLeaf; // 是否为叶子节点

        BTreeNode(boolean isLeaf) {
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
            this.isLeaf = isLeaf;
        }
    }

    /**
     * B树类
     */
    private static class BTree {
        private BTreeNode root;
        private int order; // B树的阶

        /**
         * 构造函数
         * @param order B树的阶
         */
        BTree(int order) {
            this.order = order;
            this.root = new BTreeNode(true); // 初始根节点是叶子节点
        }

        /**
         * 查找键
         */
        public boolean search(int key) {
            return search(root, key);
        }

        /**
         * 递归查找键
         */
        private boolean search(BTreeNode node, int key) {
            int i = 0;
            // 找到第一个大于等于key的位置
            while (i < node.keys.size() && key > node.keys.get(i)) {
                i++;
            }

            // 如果找到key，返回true
            if (i < node.keys.size() && key == node.keys.get(i)) {
                return true;
            }

            // 如果是叶子节点，返回false
            if (node.isLeaf) {
                return false;
            }

            // 否则在相应的子节点中查找
            return search(node.children.get(i), key);
        }

        /**
         * 插入键
         */
        public void insert(int key) {
            BTreeNode r = root;

            // 如果根节点已满，需要分裂
            if (r.keys.size() == order - 1) {
                BTreeNode s = new BTreeNode(false);
                root = s;
                s.children.add(r);
                splitChild(s, 0);
                insertNonFull(s, key);
            } else {
                insertNonFull(r, key);
            }
        }

        /**
         * 分裂子节点
         */
        private void splitChild(BTreeNode parent, int index) {
            BTreeNode child = parent.children.get(index);
            BTreeNode newNode = new BTreeNode(child.isLeaf);

            // 将child的后半部分键和子节点移动到newNode
            int mid = (order - 1) / 2;
            int medianKey = child.keys.get(mid);

            // 移动键
            for (int i = mid + 1; i < child.keys.size(); i++) {
                newNode.keys.add(child.keys.get(i));
            }
            child.keys.subList(mid, child.keys.size()).clear();

            // 如果不是叶子节点，移动子节点
            if (!child.isLeaf) {
                for (int i = mid + 1; i < child.children.size(); i++) {
                    newNode.children.add(child.children.get(i));
                }
                child.children.subList(mid + 1, child.children.size()).clear();
            }

            // 将中位数键插入到父节点
            parent.keys.add(index, medianKey);
            // 将newNode作为父节点的子节点
            parent.children.add(index + 1, newNode);
        }

        /**
         * 向非满节点插入键
         */
        private void insertNonFull(BTreeNode node, int key) {
            int i = node.keys.size() - 1;

            if (node.isLeaf) {
                // 如果是叶子节点，直接插入到正确位置
                while (i >= 0 && key < node.keys.get(i)) {
                    i--;
                }
                node.keys.add(i + 1, key);
            } else {
                // 如果不是叶子节点，找到对应的子节点
                while (i >= 0 && key < node.keys.get(i)) {
                    i--;
                }
                i++;

                // 如果子节点已满，先分裂
                if (node.children.get(i).keys.size() == order - 1) {
                    splitChild(node, i);
                    if (key > node.keys.get(i)) {
                        i++;
                    }
                }
                insertNonFull(node.children.get(i), key);
            }
        }

        /**
         * 删除键
         */
        public void delete(int key) {
            delete(root, key);
        }

        /**
         * 递归删除键
         */
        private void delete(BTreeNode node, int key) {
            int i = 0;
            while (i < node.keys.size() && key > node.keys.get(i)) {
                i++;
            }

            if (i < node.keys.size() && key == node.keys.get(i)) {
                // 情况1：键在当前节点中
                if (node.isLeaf) {
                    // 情况1a：当前节点是叶子节点，直接删除
                    node.keys.remove(i);
                } else {
                    // 情况1b：当前节点不是叶子节点
                    // 用前驱或后继键替换当前键
                    if (node.children.get(i).keys.size() >= (order + 1) / 2) {
                        // 前驱键在左子节点中
                        int predecessor = getPredecessor(node.children.get(i));
                        node.keys.set(i, predecessor);
                        delete(node.children.get(i), predecessor);
                    } else if (node.children.get(i + 1).keys.size() >= (order + 1) / 2) {
                        // 后继键在右子节点中
                        int successor = getSuccessor(node.children.get(i + 1));
                        node.keys.set(i, successor);
                        delete(node.children.get(i + 1), successor);
                    } else {
                        // 左右子节点都不够，合并
                        merge(node, i);
                        delete(node.children.get(i), key);
                    }
                }
            } else {
                // 情况2：键不在当前节点中
                if (node.isLeaf) {
                    // 情况2a：当前节点是叶子节点，键不存在
                    return;
                }

                // 情况2b：当前节点不是叶子节点
                boolean isLastChild = (i == node.keys.size());

                // 如果子节点的键数不足，先调整
                if (node.children.get(i).keys.size() < (order + 1) / 2) {
                    if (i > 0 && node.children.get(i - 1).keys.size() >= (order + 1) / 2) {
                        // 从左兄弟借一个键
                        borrowFromLeft(node, i);
                    } else if (i < node.keys.size() && node.children.get(i + 1).keys.size() >= (order + 1) / 2) {
                        // 从右兄弟借一个键
                        borrowFromRight(node, i);
                    } else {
                        // 合并子节点
                        if (i < node.keys.size()) {
                            merge(node, i);
                        } else {
                            merge(node, i - 1);
                            i--;
                        }
                    }
                }

                delete(node.children.get(i), key);
            }

            // 如果根节点的键数为0，更新根节点
            if (node == root && node.keys.isEmpty() && !node.isLeaf) {
                root = node.children.get(0);
            }
        }

        /**
         * 获取前驱键
         */
        private int getPredecessor(BTreeNode node) {
            while (!node.isLeaf) {
                node = node.children.get(node.children.size() - 1);
            }
            return node.keys.get(node.keys.size() - 1);
        }

        /**
         * 获取后继键
         */
        private int getSuccessor(BTreeNode node) {
            while (!node.isLeaf) {
                node = node.children.get(0);
            }
            return node.keys.get(0);
        }

        /**
         * 从左兄弟借一个键
         */
        private void borrowFromLeft(BTreeNode parent, int index) {
            BTreeNode child = parent.children.get(index);
            BTreeNode leftSibling = parent.children.get(index - 1);

            // 将父节点的键下移到子节点
            child.keys.add(0, parent.keys.get(index - 1));

            // 将左兄弟的最后一个键上移到父节点
            parent.keys.set(index - 1, leftSibling.keys.remove(leftSibling.keys.size() - 1));

            // 如果左兄弟不是叶子节点，将其最后一个子节点移到子节点
            if (!leftSibling.isLeaf) {
                child.children.add(0, leftSibling.children.remove(leftSibling.children.size() - 1));
            }
        }

        /**
         * 从右兄弟借一个键
         */
        private void borrowFromRight(BTreeNode parent, int index) {
            BTreeNode child = parent.children.get(index);
            BTreeNode rightSibling = parent.children.get(index + 1);

            // 将父节点的键下移到子节点
            child.keys.add(parent.keys.get(index));

            // 将右兄弟的第一个键上移到父节点
            parent.keys.set(index, rightSibling.keys.remove(0));

            // 如果右兄弟不是叶子节点，将其第一个子节点移到子节点
            if (!rightSibling.isLeaf) {
                child.children.add(rightSibling.children.remove(0));
            }
        }

        /**
         * 合并子节点
         */
        private void merge(BTreeNode parent, int index) {
            BTreeNode leftChild = parent.children.get(index);
            BTreeNode rightChild = parent.children.get(index + 1);

            // 将父节点的键下移到左子节点
            leftChild.keys.add(parent.keys.remove(index));

            // 将右子节点的所有键和子节点移到左子节点
            leftChild.keys.addAll(rightChild.keys);
            if (!leftChild.isLeaf) {
                leftChild.children.addAll(rightChild.children);
            }

            // 移除右子节点
            parent.children.remove(index + 1);
        }

        /**
         * 打印B树
         */
        public void print() {
            System.out.println("B树结构:");
            print(root, 0);
        }

        /**
         * 递归打印B树
         */
        private void print(BTreeNode node, int level) {
            // 打印当前节点的键
            System.out.print("Level " + level + ": ");
            for (int key : node.keys) {
                System.out.print(key + " ");
            }
            System.out.println();

            // 递归打印子节点
            if (!node.isLeaf) {
                for (BTreeNode child : node.children) {
                    print(child, level + 1);
                }
            }
        }

        /**
         * 打印B树的详细结构
         */
        public void printDetailed() {
            System.out.println("B树详细结构:");
            printDetailed(root, 0, "Root");
        }

        /**
         * 递归打印B树的详细结构
         */
        private void printDetailed(BTreeNode node, int level, String position) {
            // 打印当前节点信息
            System.out.printf("%s at Level %d: %s, Keys: %s, IsLeaf: %b%n", 
                    position, level, node, node.keys, node.isLeaf);

            // 递归打印子节点
            if (!node.isLeaf) {
                for (int i = 0; i < node.children.size(); i++) {
                    String childPosition = "Child " + i + " of " + node;
                    printDetailed(node.children.get(i), level + 1, childPosition);
                }
            }
        }
    }

    /**
     * 测试B树
     */
    public void testBTree() {
        System.out.println("=== B树示例 ===");
        
        // 创建一个3阶B树
        BTree bTree = new BTree(3);
        
        // 插入键
        int[] keys = {10, 20, 5, 6, 12, 30, 7, 17};
        for (int key : keys) {
            bTree.insert(key);
        }
        
        // 打印B树
        bTree.print();
        
        // 测试查找
        System.out.println("查找键6: " + bTree.search(6));
        System.out.println("查找键15: " + bTree.search(15));
        
        // 测试删除
        System.out.println("\n删除键6:");
        bTree.delete(6);
        bTree.print();
        
        System.out.println("删除键12:");
        bTree.delete(12);
        bTree.print();
        
        System.out.println("删除键10:");
        bTree.delete(10);
        bTree.print();
    }

    /**
     * B树的应用示例
     */
    public void applications() {
        System.out.println("\n=== B树应用示例 ===");
        
        // 示例1: 数据库索引
        System.out.println("示例1: 数据库索引");
        System.out.println("B树被广泛应用于数据库索引，因为它可以:");
        System.out.println("1. 减少磁盘I/O操作次数");
        System.out.println("2. 支持高效的范围查询");
        System.out.println("3. 保持树的平衡，确保查询性能稳定");
        
        // 示例2: 文件系统
        System.out.println("\n示例2: 文件系统");
        System.out.println("许多文件系统使用B树或其变体来管理文件和目录，例如:");
        System.out.println("1. ext4文件系统");
        System.out.println("2. XFS文件系统");
        System.out.println("3. JFS文件系统");
        
        // 示例3: 内存数据库
        System.out.println("\n示例3: 内存数据库");
        System.out.println("内存数据库也可以使用B树来组织数据，特别是当数据量较大时，");
        System.out.println("B树可以提供比哈希表更好的范围查询性能。");
    }

    /**
     * B树与其他数据结构的比较
     */
    public void compareWithOtherDataStructures() {
        System.out.println("\n=== B树与其他数据结构的比较 ===");
        
        System.out.println("1. B树 vs 二叉搜索树:");
        System.out.println("   - B树每个节点可以存储多个键，减少了树的高度");
        System.out.println("   - B树所有叶子节点在同一层，保证了查询性能的稳定性");
        System.out.println("   - B树更适合存储大量数据，特别是需要磁盘I/O的场景");
        
        System.out.println("\n2. B树 vs 红黑树:");
        System.out.println("   - B树的节点可以存储更多键，树的高度更低");
        System.out.println("   - B树的查询、插入和删除操作可能需要更少的磁盘I/O");
        System.out.println("   - 红黑树更适合内存中的数据结构");
        
        System.out.println("\n3. B树 vs B+树:");
        System.out.println("   - B+树的所有键都存储在叶子节点，内部节点只存储索引");
        System.out.println("   - B+树的叶子节点通过指针连接，更适合范围查询");
        System.out.println("   - B树的查询可能更快，因为数据可能在内部节点中找到");
    }
}
