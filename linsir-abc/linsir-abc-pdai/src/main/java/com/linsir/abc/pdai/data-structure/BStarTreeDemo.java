package com.linsir.abc.pdai.data-structure;

import java.util.ArrayList;
import java.util.List;

/**
 * B*树示例代码
 * 
 * 说明：
 * 1. B*树是B+树的一种变体，进一步优化了空间利用率和查询性能
 * 2. B*树的特点：
 *    - 所有键都存储在叶子节点，内部节点只存储索引键
 *    - 叶子节点通过指针连接，形成一个有序链表
 *    - 所有叶子节点都在同一层
 *    - 对于一个m阶B*树：
 *      - 根节点至少有2个子节点（除非它是叶子节点）
 *      - 非根节点至少有⌈2m/3⌉个子节点（比B+树更严格）
 *      - 每个节点最多有m个子节点
 *      - 内部节点的键数比子节点数少1
 *      - 叶子节点的键数在⌈2m/3⌉-1到m-1之间
 * 3. B*树的优势：
 *    - 更高的空间利用率，因为节点填充因子更高
 *    - 更少的分裂操作，因为分裂阈值更高
 *    - 更好的查询性能，因为树的高度更低
 * 4. 应用场景：
 *    - 数据库索引
 *    - 文件系统
 *    - 需要更高空间利用率的场景
 */
public class BStarTreeDemo {

    /**
     * B*树节点类
     */
    private static class BStarTreeNode {
        List<Integer> keys; // 键列表
        List<BStarTreeNode> children; // 子节点列表
        BStarTreeNode next; // 叶子节点的后继指针
        BStarTreeNode prev; // 叶子节点的前驱指针
        boolean isLeaf; // 是否为叶子节点

        BStarTreeNode(boolean isLeaf) {
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
            this.next = null;
            this.prev = null;
            this.isLeaf = isLeaf;
        }
    }

    /**
     * B*树类
     */
    private static class BStarTree {
        private BStarTreeNode root;
        private BStarTreeNode leaf; // 指向第一个叶子节点
        private int order; // B*树的阶

        /**
         * 构造函数
         * @param order B*树的阶
         */
        BStarTree(int order) {
            this.order = order;
            this.root = new BStarTreeNode(true); // 初始根节点是叶子节点
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
        private boolean search(BStarTreeNode node, int key) {
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
            BStarTreeNode currentLeaf = findLeaf(root, start);

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
        private BStarTreeNode findLeaf(BStarTreeNode node, int key) {
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
            BStarTreeNode r = root;

            // 如果根节点是叶子节点且已满
            if (r.isLeaf && r.keys.size() == order - 1) {
                // 创建新的根节点
                BStarTreeNode newRoot = new BStarTreeNode(false);
                root = newRoot;
                newRoot.children.add(r);
                // 分裂叶子节点
                splitLeaf(r, newRoot, 0);
                // 插入键
                insertIntoLeaf(findLeaf(newRoot, key), key);
            } else {
                // 找到适合插入的叶子节点
                BStarTreeNode leaf = findLeaf(r, key);
                // 如果叶子节点已满
                if (leaf.keys.size() == order - 1) {
                    // 尝试与兄弟节点合并
                    if (canMergeWithSibling(leaf)) {
                        // 与兄弟节点合并
                        mergeWithSibling(leaf);
                        // 重新找到适合插入的叶子节点
                        leaf = findLeaf(root, key);
                    } else {
                        // 分裂叶子节点
                        splitLeaf(leaf, leaf.parent, getChildIndex(leaf.parent, leaf));
                        // 重新找到适合插入的叶子节点
                        leaf = findLeaf(root, key);
                    }
                }
                // 插入键
                insertIntoLeaf(leaf, key);
            }
        }

        /**
         * 检查是否可以与兄弟节点合并
         */
        private boolean canMergeWithSibling(BStarTreeNode node) {
            if (node.parent == null) {
                return false;
            }

            int index = getChildIndex(node.parent, node);
            
            // 检查左兄弟
            if (index > 0) {
                BStarTreeNode leftSibling = node.parent.children.get(index - 1);
                if (leftSibling.keys.size() + node.keys.size() <= order - 1) {
                    return true;
                }
            }
            
            // 检查右兄弟
            if (index < node.parent.children.size() - 1) {
                BStarTreeNode rightSibling = node.parent.children.get(index + 1);
                if (node.keys.size() + rightSibling.keys.size() <= order - 1) {
                    return true;
                }
            }
            
            return false;
        }

        /**
         * 与兄弟节点合并
         */
        private void mergeWithSibling(BStarTreeNode node) {
            if (node.parent == null) {
                return;
            }

            int index = getChildIndex(node.parent, node);
            
            if (index > 0) {
                // 与左兄弟合并
                BStarTreeNode leftSibling = node.parent.children.get(index - 1);
                
                // 移动键和子节点
                leftSibling.keys.addAll(node.keys);
                if (!node.isLeaf) {
                    leftSibling.children.addAll(node.children);
                }
                
                // 更新叶子节点的指针
                if (node.isLeaf) {
                    leftSibling.next = node.next;
                    if (node.next != null) {
                        node.next.prev = leftSibling;
                    }
                }
                
                // 从父节点中移除键和子节点
                node.parent.keys.remove(index - 1);
                node.parent.children.remove(index);
                
                // 如果父节点是空的，更新根节点
                if (node.parent == root && node.parent.keys.isEmpty()) {
                    root = leftSibling;
                    node.parent = null;
                }
            } else if (index < node.parent.children.size() - 1) {
                // 与右兄弟合并
                BStarTreeNode rightSibling = node.parent.children.get(index + 1);
                
                // 移动键和子节点
                node.keys.addAll(rightSibling.keys);
                if (!node.isLeaf) {
                    node.children.addAll(rightSibling.children);
                }
                
                // 更新叶子节点的指针
                if (node.isLeaf) {
                    node.next = rightSibling.next;
                    if (rightSibling.next != null) {
                        rightSibling.next.prev = node;
                    }
                }
                
                // 从父节点中移除键和子节点
                node.parent.keys.remove(index);
                node.parent.children.remove(index + 1);
                
                // 如果父节点是空的，更新根节点
                if (node.parent == root && node.parent.keys.isEmpty()) {
                    root = node;
                    node.parent = null;
                }
            }
        }

        /**
         * 获取子节点在父节点中的索引
         */
        private int getChildIndex(BStarTreeNode parent, BStarTreeNode child) {
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
        private void splitLeaf(BStarTreeNode leaf, BStarTreeNode parent, int index) {
            // 创建新的叶子节点
            BStarTreeNode newLeaf = new BStarTreeNode(true);
            
            // 计算分裂点（B*树的分裂点比B+树更靠右）
            int splitPoint = (2 * order) / 3;
            
            // 移动后半部分键到新叶子节点
            for (int i = splitPoint; i < leaf.keys.size(); i++) {
                newLeaf.keys.add(leaf.keys.get(i));
            }
            leaf.keys.subList(splitPoint, leaf.keys.size()).clear();
            
            // 更新叶子节点的指针
            newLeaf.next = leaf.next;
            if (leaf.next != null) {
                leaf.next.prev = newLeaf;
            }
            leaf.next = newLeaf;
            newLeaf.prev = leaf;
            
            // 如果是第一次分裂，更新leaf指针
            if (this.leaf == leaf) {
                this.leaf = leaf;
            }
            
            // 如果父节点为空，创建新的父节点
            if (parent == null) {
                parent = new BStarTreeNode(false);
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
        private void splitInternal(BStarTreeNode node, BStarTreeNode parent, int index) {
            // 创建新的内部节点
            BStarTreeNode newNode = new BStarTreeNode(false);
            
            // 计算分裂点（B*树的分裂点比B+树更靠右）
            int splitPoint = (2 * order) / 3;
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
                parent = new BStarTreeNode(false);
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
        private void insertIntoLeaf(BStarTreeNode leaf, int key) {
            // 找到插入位置
            int i = 0;
            while (i < leaf.keys.size() && key > leaf.keys.get(i)) {
                i++;
            }
            // 插入键
            leaf.keys.add(i, key);
        }

        /**
         * 打印B*树
         */
        public void print() {
            System.out.println("B*树结构:");
            print(root, 0);
        }

        /**
         * 递归打印B*树
         */
        private void print(BStarTreeNode node, int level) {
            // 打印当前节点的键
            System.out.print("Level " + level + ": ");
            for (int key : node.keys) {
                System.out.print(key + " ");
            }
            System.out.println();

            // 递归打印子节点
            if (!node.isLeaf) {
                for (BStarTreeNode child : node.children) {
                    print(child, level + 1);
                }
            }
        }

        /**
         * 打印叶子节点链表
         */
        public void printLeafList() {
            System.out.println("叶子节点链表:");
            BStarTreeNode current = leaf;
            while (current != null) {
                for (int key : current.keys) {
                    System.out.print(key + " ");
                }
                System.out.print("<-> ");
                current = current.next;
            }
            System.out.println("null");
        }

        /**
         * 打印B*树的详细结构
         */
        public void printDetailed() {
            System.out.println("B*树详细结构:");
            printDetailed(root, 0, "Root");
        }

        /**
         * 递归打印B*树的详细结构
         */
        private void printDetailed(BStarTreeNode node, int level, String position) {
            // 打印当前节点信息
            System.out.printf("%s at Level %d: %s, Keys: %s, IsLeaf: %b, Prev: %s, Next: %s%n", 
                    position, level, node, node.keys, node.isLeaf, node.prev, node.next);

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
     * 测试B*树
     */
    public void testBStarTree() {
        System.out.println("=== B*树示例 ===");
        
        // 创建一个3阶B*树
        BStarTree bStarTree = new BStarTree(3);
        
        // 插入键
        int[] keys = {10, 20, 5, 6, 12, 30, 7, 17, 25, 35, 40, 45};
        for (int key : keys) {
            bStarTree.insert(key);
        }
        
        // 打印B*树
        bStarTree.print();
        
        // 打印叶子节点链表
        bStarTree.printLeafList();
        
        // 测试查找
        System.out.println("查找键6: " + bStarTree.search(6));
        System.out.println("查找键15: " + bStarTree.search(15));
        
        // 测试范围查询
        System.out.println("范围查询[10, 30]: " + bStarTree.rangeSearch(10, 30));
        System.out.println("范围查询[20, 40]: " + bStarTree.rangeSearch(20, 40));
    }

    /**
     * B*树的应用示例
     */
    public void applications() {
        System.out.println("\n=== B*树应用示例 ===");
        
        // 示例1: 数据库索引
        System.out.println("示例1: 数据库索引");
        System.out.println("B*树在数据库索引中的优势:");
        System.out.println("1. 更高的空间利用率，减少索引占用的存储空间");
        System.out.println("2. 更少的分裂操作，提高插入性能");
        System.out.println("3. 更低的树高度，提高查询性能");
        
        // 示例2: 文件系统
        System.out.println("\n示例2: 文件系统");
        System.out.println("B*树在文件系统中的应用:");
        System.out.println("1. 用于索引文件块的位置");
        System.out.println("2. 更高的空间利用率，适合存储大量文件");
        System.out.println("3. 更好的并发性能，因为分裂操作更少");
        
        // 示例3: 内存数据库
        System.out.println("\n示例3: 内存数据库");
        System.out.println("B*树在内存数据库中的优势:");
        System.out.println("1. 更高的内存利用率，存储更多数据");
        System.out.println("2. 更好的缓存局部性，提高查询性能");
        System.out.println("3. 更少的内存分配操作，提高插入性能");
    }

    /**
     * B*树与B+树的比较
     */
    public void compareWithBPlusTree() {
        System.out.println("\n=== B*树与B+树的比较 ===");
        
        System.out.println("1. 节点填充因子:");
        System.out.println("   - B+树: 至少⌈m/2⌉个子节点");
        System.out.println("   - B*树: 至少⌈2m/3⌉个子节点（更高）");
        
        System.out.println("\n2. 分裂操作:");
        System.out.println("   - B+树: 当节点达到m个子节点时分裂");
        System.out.println("   - B*树: 当节点达到m个子节点时分裂，但会尝试与兄弟节点合并");
        
        System.out.println("\n3. 空间利用率:");
        System.out.println("   - B+树: 较低，因为节点填充因子较低");
        System.out.println("   - B*树: 较高，因为节点填充因子较高");
        
        System.out.println("\n4. 树的高度:");
        System.out.println("   - B+树: 较高，因为空间利用率较低");
        System.out.println("   - B*树: 较低，因为空间利用率较高");
        
        System.out.println("\n5. 适用场景:");
        System.out.println("   - B+树: 适合一般的范围查询场景");
        System.out.println("   - B*树: 适合需要更高空间利用率的场景");
    }
}
