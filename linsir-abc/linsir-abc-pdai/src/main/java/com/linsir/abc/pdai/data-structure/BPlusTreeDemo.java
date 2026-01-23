package com.linsir.abc.pdai.data-structure;

import java.util.ArrayList;
import java.util.List;

/**
 * B+树示例代码
 * 
 * 说明：
 * 1. B+树是B树的一种变体，特别适合于范围查询和顺序访问
 * 2. B+树的特点：
 *    - 所有键都存储在叶子节点，内部节点只存储索引键
 *    - 叶子节点通过指针连接，形成一个有序链表
 *    - 所有叶子节点都在同一层
 *    - 对于一个m阶B+树：
 *      - 根节点至少有2个子节点（除非它是叶子节点）
 *      - 非根节点至少有⌈m/2⌉个子节点
 *      - 每个节点最多有m个子节点
 *      - 内部节点的键数比子节点数少1
 *      - 叶子节点的键数在⌈m/2⌉-1到m-1之间
 * 3. B+树的优势：
 *    - 更适合范围查询，因为叶子节点形成有序链表
 *    - 所有查询都必须到达叶子节点，查询性能更稳定
 *    - 内部节点只存储索引键，相同大小的节点可以存储更多键
 * 4. 应用场景：
 *    - 数据库索引
 *    - 文件系统
 *    - 需要频繁进行范围查询的场景
 */
public class BPlusTreeDemo {

    /**
     * B+树节点类
     */
    private static class BPlusTreeNode {
        List<Integer> keys; // 键列表
        List<BPlusTreeNode> children; // 子节点列表
        BPlusTreeNode next; // 叶子节点的后继指针
        boolean isLeaf; // 是否为叶子节点

        BPlusTreeNode(boolean isLeaf) {
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
            this.next = null;
            this.isLeaf = isLeaf;
        }
    }

    /**
     * B+树类
     */
    private static class BPlusTree {
        private BPlusTreeNode root;
        private BPlusTreeNode leaf; // 指向第一个叶子节点
        private int order; // B+树的阶

        /**
         * 构造函数
         * @param order B+树的阶
         */
        BPlusTree(int order) {
            this.order = order;
            this.root = new BPlusTreeNode(true); // 初始根节点是叶子节点
            this.leaf = root;
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
        private boolean search(BPlusTreeNode node, int key) {
            if (node.isLeaf) {
                // 在叶子节点中查找
                for (int k : node.keys) {
                    if (k == key) {
                        return true;
                    }
                }
                return false;
            }

            // 在内部节点中查找
            int i = 0;
            while (i < node.keys.size() && key >= node.keys.get(i)) {
                i++;
            }
            return search(node.children.get(i), key);
        }

        /**
         * 范围查询
         */
        public List<Integer> rangeSearch(int start, int end) {
            List<Integer> result = new ArrayList<>();
            if (start > end) {
                return result;
            }

            // 找到第一个大于等于start的叶子节点
            BPlusTreeNode currentLeaf = findLeaf(root, start);

            // 遍历叶子节点，收集在[start, end]范围内的键
            while (currentLeaf != null) {
                for (int key : currentLeaf.keys) {
                    if (key >= start && key <= end) {
                        result.add(key);
                    } else if (key > end) {
                        return result;
                    }
                }
                currentLeaf = currentLeaf.next;
            }

            return result;
        }

        /**
         * 查找包含key的叶子节点
         */
        private BPlusTreeNode findLeaf(BPlusTreeNode node, int key) {
            if (node.isLeaf) {
                return node;
            }

            int i = 0;
            while (i < node.keys.size() && key >= node.keys.get(i)) {
                i++;
            }
            return findLeaf(node.children.get(i), key);
        }

        /**
         * 插入键
         */
        public void insert(int key) {
            BPlusTreeNode r = root;

            // 如果根节点是叶子节点且已满
            if (r.isLeaf && r.keys.size() == order - 1) {
                // 创建新的根节点
                BPlusTreeNode newRoot = new BPlusTreeNode(false);
                root = newRoot;
                newRoot.children.add(r);
                // 分裂叶子节点
                splitLeaf(r, newRoot, 0);
                // 插入键
                insertIntoLeaf(findLeaf(newRoot, key), key);
            } else {
                // 找到适合插入的叶子节点
                BPlusTreeNode leaf = findLeaf(r, key);
                // 如果叶子节点已满
                if (leaf.keys.size() == order - 1) {
                    // 分裂叶子节点
                    splitLeaf(leaf, leaf.parent, getChildIndex(leaf.parent, leaf));
                    // 重新找到适合插入的叶子节点
                    leaf = findLeaf(root, key);
                }
                // 插入键
                insertIntoLeaf(leaf, key);
            }
        }

        /**
         * 获取子节点在父节点中的索引
         */
        private int getChildIndex(BPlusTreeNode parent, BPlusTreeNode child) {
            for (int i = 0; i < parent.children.size(); i++) {
                if (parent.children.get(i) == child) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 分裂叶子节点
         */
        private void splitLeaf(BPlusTreeNode leaf, BPlusTreeNode parent, int index) {
            // 创建新的叶子节点
            BPlusTreeNode newLeaf = new BPlusTreeNode(true);
            
            // 计算分裂点
            int splitPoint = (order - 1) / 2;
            
            // 移动后半部分键到新叶子节点
            for (int i = splitPoint; i < leaf.keys.size(); i++) {
                newLeaf.keys.add(leaf.keys.get(i));
            }
            leaf.keys.subList(splitPoint, leaf.keys.size()).clear();
            
            // 更新叶子节点的后继指针
            newLeaf.next = leaf.next;
            leaf.next = newLeaf;
            
            // 如果是第一次分裂，更新leaf指针
            if (this.leaf == leaf) {
                this.leaf = leaf;
            }
            
            // 如果父节点为空，创建新的父节点
            if (parent == null) {
                parent = new BPlusTreeNode(false);
                parent.children.add(leaf);
                root = parent;
            }
            
            // 将新叶子节点的最小键插入到父节点
            parent.keys.add(index, newLeaf.keys.get(0));
            parent.children.add(index + 1, newLeaf);
            
            // 更新父指针
            leaf.parent = parent;
            newLeaf.parent = parent;
            
            // 如果父节点已满，分裂父节点
            if (parent.keys.size() == order) {
                splitInternal(parent, parent.parent, getChildIndex(parent.parent, parent));
            }
        }

        /**
         * 分裂内部节点
         */
        private void splitInternal(BPlusTreeNode node, BPlusTreeNode parent, int index) {
            // 创建新的内部节点
            BPlusTreeNode newNode = new BPlusTreeNode(false);
            
            // 计算分裂点
            int splitPoint = (order - 1) / 2;
            int splitKey = node.keys.get(splitPoint);
            
            // 移动后半部分键到新内部节点
            for (int i = splitPoint + 1; i < node.keys.size(); i++) {
                newNode.keys.add(node.keys.get(i));
            }
            node.keys.subList(splitPoint, node.keys.size()).clear();
            
            // 移动后半部分子节点到新内部节点
            for (int i = splitPoint + 1; i < node.children.size(); i++) {
                newNode.children.add(node.children.get(i));
                node.children.get(i).parent = newNode;
            }
            node.children.subList(splitPoint + 1, node.children.size()).clear();
            
            // 如果父节点为空，创建新的父节点
            if (parent == null) {
                parent = new BPlusTreeNode(false);
                parent.children.add(node);
                root = parent;
            }
            
            // 将分裂键插入到父节点
            parent.keys.add(index, splitKey);
            parent.children.add(index + 1, newNode);
            
            // 更新父指针
            newNode.parent = parent;
            
            // 如果父节点已满，分裂父节点
            if (parent.keys.size() == order) {
                splitInternal(parent, parent.parent, getChildIndex(parent.parent, parent));
            }
        }

        /**
         * 插入键到叶子节点
         */
        private void insertIntoLeaf(BPlusTreeNode leaf, int key) {
            // 找到插入位置
            int i = 0;
            while (i < leaf.keys.size() && key > leaf.keys.get(i)) {
                i++;
            }
            // 插入键
            leaf.keys.add(i, key);
        }

        /**
         * 打印B+树
         */
        public void print() {
            System.out.println("B+树结构:");
            print(root, 0);
        }

        /**
         * 递归打印B+树
         */
        private void print(BPlusTreeNode node, int level) {
            // 打印当前节点的键
            System.out.print("Level " + level + ": ");
            for (int key : node.keys) {
                System.out.print(key + " ");
            }
            System.out.println();

            // 递归打印子节点
            if (!node.isLeaf) {
                for (BPlusTreeNode child : node.children) {
                    print(child, level + 1);
                }
            }
        }

        /**
         * 打印叶子节点链表
         */
        public void printLeafList() {
            System.out.println("叶子节点链表:");
            BPlusTreeNode current = leaf;
            while (current != null) {
                for (int key : current.keys) {
                    System.out.print(key + " ");
                }
                System.out.print("-> ");
                current = current.next;
            }
            System.out.println("null");
        }

        /**
         * 打印B+树的详细结构
         */
        public void printDetailed() {
            System.out.println("B+树详细结构:");
            printDetailed(root, 0, "Root");
        }

        /**
         * 递归打印B+树的详细结构
         */
        private void printDetailed(BPlusTreeNode node, int level, String position) {
            // 打印当前节点信息
            System.out.printf("%s at Level %d: %s, Keys: %s, IsLeaf: %b, Next: %s%n", 
                    position, level, node, node.keys, node.isLeaf, node.next);

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
     * 测试B+树
     */
    public void testBPlusTree() {
        System.out.println("=== B+树示例 ===");
        
        // 创建一个3阶B+树
        BPlusTree bPlusTree = new BPlusTree(3);
        
        // 插入键
        int[] keys = {10, 20, 5, 6, 12, 30, 7, 17, 25, 35, 40, 45};
        for (int key : keys) {
            bPlusTree.insert(key);
        }
        
        // 打印B+树
        bPlusTree.print();
        
        // 打印叶子节点链表
        bPlusTree.printLeafList();
        
        // 测试查找
        System.out.println("查找键6: " + bPlusTree.search(6));
        System.out.println("查找键15: " + bPlusTree.search(15));
        
        // 测试范围查询
        System.out.println("范围查询[10, 30]: " + bPlusTree.rangeSearch(10, 30));
        System.out.println("范围查询[20, 40]: " + bPlusTree.rangeSearch(20, 40));
    }

    /**
     * B+树的应用示例
     */
    public void applications() {
        System.out.println("\n=== B+树应用示例 ===");
        
        // 示例1: 数据库索引
        System.out.println("示例1: 数据库索引");
        System.out.println("B+树被广泛应用于数据库索引，因为它:");
        System.out.println("1. 更适合范围查询，例如SELECT * FROM table WHERE id BETWEEN 100 AND 200");
        System.out.println("2. 所有查询都到达叶子节点，查询性能更稳定");
        System.out.println("3. 叶子节点形成有序链表，支持高效的顺序扫描");
        
        // 示例2: 文件系统
        System.out.println("\n示例2: 文件系统");
        System.out.println("B+树在文件系统中的应用:");
        System.out.println("1. 用于索引文件块的位置");
        System.out.println("2. 支持高效的文件查找和范围查询");
        System.out.println("3. 适合处理大量文件的场景");
        
        // 示例3: 时间序列数据
        System.out.println("\n示例3: 时间序列数据");
        System.out.println("B+树适合存储时间序列数据，因为:");
        System.out.println("1. 时间序列数据通常需要范围查询，例如查询某一天的数据");
        System.out.println("2. 时间序列数据是有序的，适合B+树的结构");
        System.out.println("3. B+树的叶子节点链表支持高效的顺序访问");
    }

    /**
     * B+树与B树的比较
     */
    public void compareWithBTree() {
        System.out.println("\n=== B+树与B树的比较 ===");
        
        System.out.println("1. 存储结构:");
        System.out.println("   - B树: 键存储在所有节点中");
        System.out.println("   - B+树: 键只存储在叶子节点中，内部节点只存储索引键");
        
        System.out.println("\n2. 查询性能:");
        System.out.println("   - B树: 某些查询可能在内部节点找到键，不需要到达叶子节点");
        System.out.println("   - B+树: 所有查询都必须到达叶子节点，查询性能更稳定");
        
        System.out.println("\n3. 范围查询:");
        System.out.println("   - B树: 需要递归遍历多个节点，效率较低");
        System.out.println("   - B+树: 叶子节点形成有序链表，只需遍历链表，效率较高");
        
        System.out.println("\n4. 存储效率:");
        System.out.println("   - B树: 内部节点存储键和数据，存储效率较低");
        System.out.println("   - B+树: 内部节点只存储索引键，相同大小的节点可以存储更多键，存储效率较高");
        
        System.out.println("\n5. 适用场景:");
        System.out.println("   - B树: 适合随机访问较多的场景");
        System.out.println("   - B+树: 适合范围查询和顺序访问较多的场景，如数据库索引");
    }
}
