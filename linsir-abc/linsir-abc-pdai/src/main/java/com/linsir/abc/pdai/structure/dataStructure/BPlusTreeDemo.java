package com.linsir.abc.pdai.structure.dataStructure;

/**
 * B+树示例代码
 * 
 * 说明：
 * 1. B+树是一种改进的B树，它在B树的基础上做了一些优化，更适合文件系统和数据库索引
 * 2. B+树的特点：
 *    - 所有键都存储在叶子节点中，非叶子节点只存储键的副本用于索引
 *    - 叶子节点形成一个有序链表，便于范围查询
 *    - 对于一个m阶的B+树：
 *      - 根节点至少有2个子节点（除非它是叶子节点）
 *      - 每个非根节点至少有⌈m/2⌉个子节点
 *      - 每个节点最多有m个子节点
 *      - 每个节点的键值按升序排列
 * 3. B+树的时间复杂度：
 *    - 查找、插入、删除的时间复杂度均为O(log_m n)
 * 4. 应用场景：
 *    - 数据库和文件系统的索引结构
 *    - 需要频繁进行范围查询的场景
 *    - 处理大量数据，需要减少磁盘I/O操作的场景
 */
public class BPlusTreeDemo {

    /**
     * B+树节点类
     */
    private static class BPlusTreeNode {
        int[] keys;         // 键数组
        BPlusTreeNode[] children; // 子节点数组
        BPlusTreeNode next; // 指向下一个叶子节点的指针
        int degree;         // B+树的阶
        int keyCount;       // 当前节点的键数量
        boolean isLeaf;     // 是否为叶子节点

        BPlusTreeNode(int degree, boolean isLeaf) {
            this.degree = degree;
            this.isLeaf = isLeaf;
            this.keys = new int[degree]; // 最多d个键
            this.children = isLeaf ? null : new BPlusTreeNode[degree + 1]; // 非叶子节点最多d+1个子节点
            this.next = null;
            this.keyCount = 0;
        }

        /**
         * 遍历节点及其子树
         */
        public void traverse() {
            if (isLeaf) {
                for (int i = 0; i < keyCount; i++) {
                    System.out.print(keys[i] + " ");
                }
            } else {
                for (int i = 0; i < keyCount; i++) {
                    children[i].traverse();
                }
                children[keyCount].traverse();
            }
        }

        /**
         * 打印叶子节点链表
         */
        public void printLeafNodes() {
            BPlusTreeNode current = this;
            System.out.print("叶子节点链表: ");
            while (current != null) {
                for (int i = 0; i < current.keyCount; i++) {
                    System.out.print(current.keys[i] + " ");
                }
                current = current.next;
            }
            System.out.println();
        }

        /**
         * 查找键
         */
        public BPlusTreeNode search(int key) {
            int i = 0;
            while (i < keyCount && key > keys[i]) {
                i++;
            }

            if (isLeaf) {
                if (i < keyCount && key == keys[i]) {
                    return this;
                } else {
                    return null;
                }
            } else {
                return children[i].search(key);
            }
        }

        /**
         * 范围查询
         */
        public void rangeQuery(int start, int end) {
            BPlusTreeNode leaf = findLeafNode(start);
            if (leaf == null) {
                System.out.println("范围查询: 未找到起始键");
                return;
            }

            System.out.print("范围查询 [" + start + ", " + end + "]: ");
            while (leaf != null) {
                for (int i = 0; i < leaf.keyCount; i++) {
                    if (leaf.keys[i] >= start && leaf.keys[i] <= end) {
                        System.out.print(leaf.keys[i] + " ");
                    } else if (leaf.keys[i] > end) {
                        System.out.println();
                        return;
                    }
                }
                leaf = leaf.next;
            }
            System.out.println();
        }

        /**
         * 查找包含指定键的叶子节点
         */
        private BPlusTreeNode findLeafNode(int key) {
            BPlusTreeNode current = this;
            while (!current.isLeaf) {
                int i = 0;
                while (i < current.keyCount && key > current.keys[i]) {
                    i++;
                }
                current = current.children[i];
            }
            return current;
        }
    }

    /**
     * B+树类
     */
    private static class BPlusTree {
        private BPlusTreeNode root;
        private int degree; // B+树的阶
        private BPlusTreeNode firstLeaf; // 第一个叶子节点

        BPlusTree(int degree) {
            this.degree = degree;
            this.root = new BPlusTreeNode(degree, true);
            this.firstLeaf = root;
        }

        /**
         * 遍历B+树
         */
        public void traverse() {
            if (root != null) {
                root.traverse();
            }
            System.out.println();
        }

        /**
         * 打印叶子节点链表
         */
        public void printLeafNodes() {
            if (firstLeaf != null) {
                firstLeaf.printLeafNodes();
            }
        }

        /**
         * 查找键
         */
        public BPlusTreeNode search(int key) {
            return root == null ? null : root.search(key);
        }

        /**
         * 范围查询
         */
        public void rangeQuery(int start, int end) {
            if (root != null) {
                root.rangeQuery(start, end);
            }
        }

        /**
         * 插入键
         */
        public void insert(int key) {
            BPlusTreeNode leaf = findLeafNode(key);

            // 检查键是否已存在
            for (int i = 0; i < leaf.keyCount; i++) {
                if (leaf.keys[i] == key) {
                    System.out.println("键 " + key + " 已存在");
                    return;
                }
            }

            // 插入到叶子节点
            if (leaf.keyCount < degree) {
                int i = leaf.keyCount - 1;
                while (i >= 0 && key < leaf.keys[i]) {
                    leaf.keys[i + 1] = leaf.keys[i];
                    i--;
                }
                leaf.keys[i + 1] = key;
                leaf.keyCount++;
            } else {
                // 叶子节点已满，需要分裂
                splitLeaf(leaf, key);
            }
        }

        /**
         * 查找包含指定键应该插入位置的叶子节点
         */
        private BPlusTreeNode findLeafNode(int key) {
            BPlusTreeNode current = root;
            while (!current.isLeaf) {
                int i = 0;
                while (i < current.keyCount && key > current.keys[i]) {
                    i++;
                }
                current = current.children[i];
            }
            return current;
        }

        /**
         * 分裂叶子节点
         */
        private void splitLeaf(BPlusTreeNode leaf, int key) {
            // 创建新的叶子节点
            BPlusTreeNode newLeaf = new BPlusTreeNode(degree, true);
            
            // 合并键并排序
            int[] allKeys = new int[degree + 1];
            int i, j;
            
            // 复制原有键
            for (i = 0; i < degree; i++) {
                allKeys[i] = leaf.keys[i];
            }
            // 插入新键
            i = degree - 1;
            while (i >= 0 && key < allKeys[i]) {
                allKeys[i + 1] = allKeys[i];
                i--;
            }
            allKeys[i + 1] = key;
            
            // 分配键到原叶子节点和新叶子节点
            int splitPoint = (degree + 1) / 2;
            leaf.keyCount = 0;
            for (i = 0; i < splitPoint; i++) {
                leaf.keys[i] = allKeys[i];
                leaf.keyCount++;
            }
            
            newLeaf.keyCount = 0;
            for (j = 0; i < degree + 1; i++, j++) {
                newLeaf.keys[j] = allKeys[i];
                newLeaf.keyCount++;
            }
            
            // 更新叶子节点链表指针
            newLeaf.next = leaf.next;
            leaf.next = newLeaf;
            
            // 向上传递分裂信息
            insertIntoParent(leaf, newLeaf.keys[0], newLeaf);
        }

        /**
         * 向上传递分裂信息到父节点
         */
        private void insertIntoParent(BPlusTreeNode left, int key, BPlusTreeNode right) {
            if (left == root) {
                // 创建新的根节点
                BPlusTreeNode newRoot = new BPlusTreeNode(degree, false);
                newRoot.keys[0] = key;
                newRoot.children[0] = left;
                newRoot.children[1] = right;
                newRoot.keyCount = 1;
                root = newRoot;
                return;
            }

            // 找到父节点和left在父节点中的位置
            BPlusTreeNode parent = findParent(root, left);
            int index = 0;
            while (index < parent.keyCount && parent.children[index] != left) {
                index++;
            }

            // 插入到父节点
            if (parent.keyCount < degree) {
                // 父节点未满，直接插入
                for (int i = parent.keyCount - 1; i >= index; i--) {
                    parent.keys[i + 1] = parent.keys[i];
                    parent.children[i + 2] = parent.children[i + 1];
                }
                parent.keys[index] = key;
                parent.children[index + 1] = right;
                parent.keyCount++;
            } else {
                // 父节点已满，需要分裂
                splitNonLeaf(parent, index, key, right);
            }
        }

        /**
         * 查找父节点
         */
        private BPlusTreeNode findParent(BPlusTreeNode current, BPlusTreeNode child) {
            if (current.isLeaf) {
                return null;
            }
            for (int i = 0; i <= current.keyCount; i++) {
                if (current.children[i] == child) {
                    return current;
                }
                BPlusTreeNode parent = findParent(current.children[i], child);
                if (parent != null) {
                    return parent;
                }
            }
            return null;
        }

        /**
         * 分裂非叶子节点
         */
        private void splitNonLeaf(BPlusTreeNode node, int index, int key, BPlusTreeNode right) {
            // 创建新的非叶子节点
            BPlusTreeNode newNode = new BPlusTreeNode(degree, false);
            
            // 合并键和子节点
            int[] allKeys = new int[degree + 1];
            BPlusTreeNode[] allChildren = new BPlusTreeNode[degree + 2];
            
            // 复制原有键和子节点
            for (int i = 0; i < degree; i++) {
                allKeys[i] = node.keys[i];
            }
            for (int i = 0; i <= degree; i++) {
                allChildren[i] = node.children[i];
            }
            
            // 插入新键和子节点
            for (int i = degree - 1; i >= index; i--) {
                allKeys[i + 1] = allKeys[i];
            }
            allKeys[index] = key;
            
            for (int i = degree; i >= index + 1; i--) {
                allChildren[i + 1] = allChildren[i];
            }
            allChildren[index + 1] = right;
            
            // 分配键和子节点到原节点和新节点
            int splitPoint = (degree + 1) / 2;
            node.keyCount = 0;
            for (int i = 0; i < splitPoint - 1; i++) {
                node.keys[i] = allKeys[i];
                node.keyCount++;
            }
            for (int i = 0; i < splitPoint; i++) {
                node.children[i] = allChildren[i];
            }
            
            newNode.keyCount = 0;
            for (int i = splitPoint, j = 0; i < degree + 1; i++, j++) {
                if (i != splitPoint) {
                    newNode.keys[j] = allKeys[i];
                    newNode.keyCount++;
                }
                newNode.children[j] = allChildren[i];
            }
            
            // 向上传递分裂信息
            insertIntoParent(node, allKeys[splitPoint - 1], newNode);
        }

        /**
         * 删除键
         */
        public void delete(int key) {
            if (root == null) {
                System.out.println("B+树为空");
                return;
            }

            BPlusTreeNode leaf = findLeafNode(key);
            if (leaf == null) {
                System.out.println("键 " + key + " 不在B+树中");
                return;
            }

            // 查找键在叶子节点中的位置
            int index = -1;
            for (int i = 0; i < leaf.keyCount; i++) {
                if (leaf.keys[i] == key) {
                    index = i;
                    break;
                }
            }

            if (index == -1) {
                System.out.println("键 " + key + " 不在B+树中");
                return;
            }

            // 从叶子节点中删除键
            deleteFromLeaf(leaf, index);
        }

        /**
         * 从叶子节点中删除键
         */
        private void deleteFromLeaf(BPlusTreeNode leaf, int index) {
            // 移动键
            for (int i = index; i < leaf.keyCount - 1; i++) {
                leaf.keys[i] = leaf.keys[i + 1];
            }
            leaf.keyCount--;

            // 检查是否需要合并或借键
            if (leaf.keyCount < (degree + 1) / 2 && leaf != root) {
                BPlusTreeNode parent = findParent(root, leaf);
                int leafIndex = 0;
                while (leafIndex < parent.keyCount + 1 && parent.children[leafIndex] != leaf) {
                    leafIndex++;
                }

                BPlusTreeNode leftSibling = (leafIndex > 0) ? parent.children[leafIndex - 1] : null;
                BPlusTreeNode rightSibling = (leafIndex < parent.keyCount) ? parent.children[leafIndex + 1] : null;

                if (leftSibling != null && leftSibling.keyCount > (degree + 1) / 2) {
                    // 从左兄弟借键
                    borrowFromLeftSibling(leaf, leftSibling, parent, leafIndex - 1);
                } else if (rightSibling != null && rightSibling.keyCount > (degree + 1) / 2) {
                    // 从右兄弟借键
                    borrowFromRightSibling(leaf, rightSibling, parent, leafIndex);
                } else {
                    // 合并叶子节点
                    if (leftSibling != null) {
                        mergeLeaves(leftSibling, leaf, parent, leafIndex - 1);
                    } else if (rightSibling != null) {
                        mergeLeaves(leaf, rightSibling, parent, leafIndex);
                    }
                }
            }

            // 更新根节点
            if (root.keyCount == 0 && !root.isLeaf) {
                root = root.children[0];
            }
        }

        /**
         * 从左兄弟借键
         */
        private void borrowFromLeftSibling(BPlusTreeNode leaf, BPlusTreeNode leftSibling, BPlusTreeNode parent, int parentIndex) {
            // 从左兄弟借最后一个键
            leaf.keys[leaf.keyCount] = leftSibling.keys[leftSibling.keyCount - 1];
            leaf.keyCount++;
            leftSibling.keyCount--;

            // 更新父节点的键
            parent.keys[parentIndex] = leaf.keys[0];
        }

        /**
         * 从右兄弟借键
         */
        private void borrowFromRightSibling(BPlusTreeNode leaf, BPlusTreeNode rightSibling, BPlusTreeNode parent, int parentIndex) {
            // 从右兄弟借第一个键
            leaf.keys[leaf.keyCount] = rightSibling.keys[0];
            leaf.keyCount++;

            // 移动右兄弟的键
            for (int i = 0; i < rightSibling.keyCount - 1; i++) {
                rightSibling.keys[i] = rightSibling.keys[i + 1];
            }
            rightSibling.keyCount--;

            // 更新父节点的键
            parent.keys[parentIndex] = rightSibling.keys[0];
        }

        /**
         * 合并叶子节点
         */
        private void mergeLeaves(BPlusTreeNode left, BPlusTreeNode right, BPlusTreeNode parent, int parentIndex) {
            // 将右叶子节点的键合并到左叶子节点
            for (int i = 0; i < right.keyCount; i++) {
                left.keys[left.keyCount + i] = right.keys[i];
            }
            left.keyCount += right.keyCount;
            left.next = right.next;

            // 从父节点中删除键
            deleteFromNonLeaf(parent, parentIndex);
        }

        /**
         * 从非叶子节点中删除键
         */
        private void deleteFromNonLeaf(BPlusTreeNode node, int index) {
            // 移动键和子节点
            for (int i = index; i < node.keyCount - 1; i++) {
                node.keys[i] = node.keys[i + 1];
            }
            for (int i = index + 1; i < node.keyCount; i++) {
                node.children[i] = node.children[i + 1];
            }
            node.keyCount--;

            // 检查是否需要合并或借键
            if (node.keyCount < (degree + 1) / 2 && node != root) {
                BPlusTreeNode parent = findParent(root, node);
                int nodeIndex = 0;
                while (nodeIndex < parent.keyCount + 1 && parent.children[nodeIndex] != node) {
                    nodeIndex++;
                }

                BPlusTreeNode leftSibling = (nodeIndex > 0) ? parent.children[nodeIndex - 1] : null;
                BPlusTreeNode rightSibling = (nodeIndex < parent.keyCount) ? parent.children[nodeIndex + 1] : null;

                if (leftSibling != null && leftSibling.keyCount > (degree + 1) / 2) {
                    // 从左兄弟借键
                    borrowFromLeftSiblingNonLeaf(node, leftSibling, parent, nodeIndex - 1);
                } else if (rightSibling != null && rightSibling.keyCount > (degree + 1) / 2) {
                    // 从右兄弟借键
                    borrowFromRightSiblingNonLeaf(node, rightSibling, parent, nodeIndex);
                } else {
                    // 合并非叶子节点
                    if (leftSibling != null) {
                        mergeNonLeaves(leftSibling, node, parent, nodeIndex - 1);
                    } else if (rightSibling != null) {
                        mergeNonLeaves(node, rightSibling, parent, nodeIndex);
                    }
                }
            }

            // 更新根节点
            if (root.keyCount == 0 && !root.isLeaf) {
                root = root.children[0];
            }
        }

        /**
         * 从左兄弟借键（非叶子节点）
         */
        private void borrowFromLeftSiblingNonLeaf(BPlusTreeNode node, BPlusTreeNode leftSibling, BPlusTreeNode parent, int parentIndex) {
            // 移动键和子节点
            for (int i = node.keyCount; i > 0; i--) {
                node.keys[i] = node.keys[i - 1];
            }
            for (int i = node.keyCount + 1; i > 0; i--) {
                node.children[i] = node.children[i - 1];
            }

            // 从父节点和左兄弟借键
            node.keys[0] = parent.keys[parentIndex];
            node.children[0] = leftSibling.children[leftSibling.keyCount];
            node.keyCount++;

            // 更新父节点和左兄弟
            parent.keys[parentIndex] = leftSibling.keys[leftSibling.keyCount - 1];
            leftSibling.keyCount--;
        }

        /**
         * 从右兄弟借键（非叶子节点）
         */
        private void borrowFromRightSiblingNonLeaf(BPlusTreeNode node, BPlusTreeNode rightSibling, BPlusTreeNode parent, int parentIndex) {
            // 从父节点和右兄弟借键
            node.keys[node.keyCount] = parent.keys[parentIndex];
            node.children[node.keyCount + 1] = rightSibling.children[0];
            node.keyCount++;

            // 更新父节点和右兄弟
            parent.keys[parentIndex] = rightSibling.keys[0];
            for (int i = 0; i < rightSibling.keyCount - 1; i++) {
                rightSibling.keys[i] = rightSibling.keys[i + 1];
            }
            for (int i = 0; i < rightSibling.keyCount; i++) {
                rightSibling.children[i] = rightSibling.children[i + 1];
            }
            rightSibling.keyCount--;
        }

        /**
         * 合并非叶子节点
         */
        private void mergeNonLeaves(BPlusTreeNode left, BPlusTreeNode right, BPlusTreeNode parent, int parentIndex) {
            // 将父节点的键和右节点的键合并到左节点
            left.keys[left.keyCount] = parent.keys[parentIndex];
            left.keyCount++;
            for (int i = 0; i < right.keyCount; i++) {
                left.keys[left.keyCount + i] = right.keys[i];
            }
            for (int i = 0; i <= right.keyCount; i++) {
                left.children[left.keyCount + i] = right.children[i];
            }
            left.keyCount += right.keyCount;

            // 从父节点中删除键
            deleteFromNonLeaf(parent, parentIndex);
        }

        /**
         * 打印B+树的结构
         */
        public void print() {
            System.out.println("B+树结构:");
            printHelper(root, 0);
        }

        /**
         * 打印B+树的辅助方法
         */
        private void printHelper(BPlusTreeNode node, int level) {
            if (node != null) {
                System.out.print("Level " + level + (node.isLeaf ? " (Leaf): " : ": "));
                for (int i = 0; i < node.keyCount; i++) {
                    System.out.print(node.keys[i] + " ");
                }
                System.out.println();

                if (!node.isLeaf) {
                    for (int i = 0; i <= node.keyCount; i++) {
                        printHelper(node.children[i], level + 1);
                    }
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
        
        // 插入元素
        bPlusTree.insert(10);
        bPlusTree.insert(20);
        bPlusTree.insert(5);
        bPlusTree.insert(6);
        bPlusTree.insert(12);
        bPlusTree.insert(30);
        bPlusTree.insert(7);
        bPlusTree.insert(17);
        bPlusTree.insert(19);
        bPlusTree.insert(25);
        bPlusTree.insert(28);
        bPlusTree.insert(35);
        
        // 打印B+树结构
        bPlusTree.print();
        
        // 遍历B+树
        System.out.print("B+树遍历: ");
        bPlusTree.traverse();
        
        // 打印叶子节点链表
        bPlusTree.printLeafNodes();
        
        // 查找元素
        int key = 12;
        BPlusTreeNode node = bPlusTree.search(key);
        System.out.println("查找键 " + key + ": " + (node != null ? "找到" : "未找到"));
        
        // 范围查询
        bPlusTree.rangeQuery(10, 30);
        
        // 删除元素
        System.out.println("\n删除键 12:");
        bPlusTree.delete(12);
        bPlusTree.print();
        bPlusTree.printLeafNodes();
        
        System.out.println("\n删除键 10:");
        bPlusTree.delete(10);
        bPlusTree.print();
        bPlusTree.printLeafNodes();
        
        // 再次范围查询
        bPlusTree.rangeQuery(10, 30);
    }

    /**
     * B+树应用示例
     */
    public void bPlusTreeApplications() {
        System.out.println("\n=== B+树应用示例 ===");
        
        // 示例1：数据库索引
        System.out.println("示例1：数据库索引");
        BPlusTree dbIndex = new BPlusTree(3);
        
        // 插入一些记录的主键
        int[] primaryKeys = {101, 102, 103, 104, 105, 106, 107, 108, 109, 110};
        for (int key : primaryKeys) {
            dbIndex.insert(key);
        }
        
        System.out.println("数据库索引结构:");
        dbIndex.print();
        
        // 查找记录
        int searchKey = 105;
        BPlusTreeNode result = dbIndex.search(searchKey);
        System.out.println("查找主键 " + searchKey + " 的记录: " + (result != null ? "找到" : "未找到"));
        
        // 范围查询
        System.out.println("查询主键在103到108之间的记录:");
        dbIndex.rangeQuery(103, 108);
        
        // 示例2：文件系统索引
        System.out.println("\n示例2：文件系统索引");
        BPlusTree fileIndex = new BPlusTree(4);
        
        // 插入一些文件的inode号
        int[] inodeNumbers = {1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010, 1011, 1012};
        for (int inode : inodeNumbers) {
            fileIndex.insert(inode);
        }
        
        System.out.println("文件系统索引结构:");
        fileIndex.print();
        
        // 查找文件
        int inodeToFind = 1007;
        BPlusTreeNode fileNode = fileIndex.search(inodeToFind);
        System.out.println("查找inode号 " + inodeToFind + " 的文件: " + (fileNode != null ? "找到" : "未找到"));
        
        // 范围查询
        System.out.println("查询inode号在1005到1010之间的文件:");
        fileIndex.rangeQuery(1005, 1010);
    }

    /**
     * B+树与B树的比较
     */
    public void compareWithBTree() {
        System.out.println("\n=== B+树与B树的比较 ===");
        
        System.out.println("B+树的优势:");
        System.out.println("1. 所有键都在叶子节点中，非叶子节点只存储索引，相同大小的节点可以存储更多的键");
        System.out.println("2. 叶子节点形成有序链表，便于范围查询");
        System.out.println("3. 查找效率更稳定，因为所有查找都必须到达叶子节点");
        System.out.println("4. 更适合磁盘存储，因为可以更好地利用预读");
        
        System.out.println("\nB树的优势:");
        System.out.println("1. 对于点查询，可能不需要到达叶子节点，理论上更快");
        System.out.println("2. 插入和删除操作可能更简单，因为不需要维护叶子节点链表");
        
        System.out.println("\n应用场景选择:");
        System.out.println("- B+树：适合需要频繁进行范围查询的场景，如数据库索引");
        System.out.println("- B树：适合需要频繁进行点查询的场景");
    }
}
