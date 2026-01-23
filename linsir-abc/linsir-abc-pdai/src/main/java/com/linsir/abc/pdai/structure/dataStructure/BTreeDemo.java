package com.linsir.abc.pdai.structure.dataStructure;

/**
 * B树示例代码
 * 
 * 说明：
 * 1. B树是一种多路搜索树，它在磁盘或其他直接存取辅助存储设备上有广泛应用
 * 2. B树的特点：
 *    - 每个节点可以有多个子节点和多个键
 *    - 所有叶子节点都在同一层
 *    - 对于一个m阶的B树：
 *      - 根节点至少有2个子节点（除非它是叶子节点）
 *      - 每个非根节点至少有⌈m/2⌉个子节点
 *      - 每个节点最多有m个子节点
 *      - 每个节点的键值按升序排列
 * 3. B树的时间复杂度：
 *    - 查找、插入、删除的时间复杂度均为O(log_m n)
 * 4. 应用场景：
 *    - 数据库和文件系统的索引结构
 *    - 处理大量数据，需要减少磁盘I/O操作的场景
 */
public class BTreeDemo {

    /**
     * B树节点类
     */
    private static class BTreeNode {
        int[] keys;         // 键数组
        BTreeNode[] children; // 子节点数组
        int degree;         // B树的阶
        int keyCount;       // 当前节点的键数量
        boolean isLeaf;     // 是否为叶子节点

        BTreeNode(int degree, boolean isLeaf) {
            this.degree = degree;
            this.isLeaf = isLeaf;
            this.keys = new int[2 * degree - 1]; // 最多2d-1个键
            this.children = new BTreeNode[2 * degree]; // 最多2d个子节点
            this.keyCount = 0;
        }

        /**
         * 遍历节点及其子树
         */
        public void traverse() {
            int i;
            for (i = 0; i < keyCount; i++) {
                if (!isLeaf) {
                    children[i].traverse();
                }
                System.out.print(keys[i] + " ");
            }
            if (!isLeaf) {
                children[i].traverse();
            }
        }

        /**
         * 查找键
         */
        public BTreeNode search(int key) {
            int i = 0;
            while (i < keyCount && key > keys[i]) {
                i++;
            }

            if (i < keyCount && key == keys[i]) {
                return this;
            }

            if (isLeaf) {
                return null;
            }

            return children[i].search(key);
        }
    }

    /**
     * B树类
     */
    private static class BTree {
        private BTreeNode root;
        private int degree; // B树的阶

        BTree(int degree) {
            this.degree = degree;
            this.root = null;
        }

        /**
         * 遍历B树
         */
        public void traverse() {
            if (root != null) {
                root.traverse();
            }
            System.out.println();
        }

        /**
         * 查找键
         */
        public BTreeNode search(int key) {
            return root == null ? null : root.search(key);
        }

        /**
         * 插入键
         */
        public void insert(int key) {
            if (root == null) {
                // 创建根节点
                root = new BTreeNode(degree, true);
                root.keys[0] = key;
                root.keyCount = 1;
            } else {
                // 如果根节点已满，需要分裂
                if (root.keyCount == 2 * degree - 1) {
                    BTreeNode newRoot = new BTreeNode(degree, false);
                    newRoot.children[0] = root;
                    splitChild(newRoot, 0, root);

                    int i = 0;
                    if (newRoot.keys[0] < key) {
                        i++;
                    }
                    insertNonFull(newRoot.children[i], key);
                    root = newRoot;
                } else {
                    insertNonFull(root, key);
                }
            }
        }

        /**
         * 分裂子节点
         */
        private void splitChild(BTreeNode parent, int index, BTreeNode child) {
            BTreeNode newNode = new BTreeNode(child.degree, child.isLeaf);
            newNode.keyCount = degree - 1;

            // 复制child的后d-1个键到newNode
            for (int i = 0; i < degree - 1; i++) {
                newNode.keys[i] = child.keys[i + degree];
            }

            // 如果child不是叶子节点，复制后d个子节点到newNode
            if (!child.isLeaf) {
                for (int i = 0; i < degree; i++) {
                    newNode.children[i] = child.children[i + degree];
                }
            }

            child.keyCount = degree - 1;

            // 在parent中为newNode腾出空间
            for (int i = parent.keyCount; i > index; i--) {
                parent.children[i + 1] = parent.children[i];
            }
            parent.children[index + 1] = newNode;

            for (int i = parent.keyCount - 1; i >= index; i--) {
                parent.keys[i + 1] = parent.keys[i];
            }
            parent.keys[index] = child.keys[degree - 1];
            parent.keyCount++;
        }

        /**
         * 向非满节点插入键
         */
        private void insertNonFull(BTreeNode node, int key) {
            int i = node.keyCount - 1;

            if (node.isLeaf) {
                // 如果是叶子节点，直接插入
                while (i >= 0 && key < node.keys[i]) {
                    node.keys[i + 1] = node.keys[i];
                    i--;
                }
                node.keys[i + 1] = key;
                node.keyCount++;
            } else {
                // 找到子节点的位置
                while (i >= 0 && key < node.keys[i]) {
                    i--;
                }
                i++;

                // 如果子节点已满，需要分裂
                if (node.children[i].keyCount == 2 * degree - 1) {
                    splitChild(node, i, node.children[i]);
                    if (key > node.keys[i]) {
                        i++;
                    }
                }
                insertNonFull(node.children[i], key);
            }
        }

        /**
         * 删除键
         */
        public void delete(int key) {
            if (root == null) {
                System.out.println("B树为空");
                return;
            }

            deleteKey(root, key);

            // 如果根节点的键数量为0，更新根节点
            if (root.keyCount == 0) {
                if (root.isLeaf) {
                    root = null;
                } else {
                    root = root.children[0];
                }
            }
        }

        /**
         * 删除键的辅助方法
         */
        private void deleteKey(BTreeNode node, int key) {
            int index = findKey(node, key);

            if (index < node.keyCount && node.keys[index] == key) {
                // 键在当前节点中
                if (node.isLeaf) {
                    // 情况1：键在叶子节点中
                    deleteFromLeaf(node, index);
                } else {
                    // 情况2：键在非叶子节点中
                    deleteFromNonLeaf(node, index);
                }
            } else {
                // 键不在当前节点中
                if (node.isLeaf) {
                    // 情况3：键不在B树中
                    System.out.println("键 " + key + " 不在B树中");
                    return;
                }

                // 确定键应该在哪个子节点中
                boolean shouldMerge = (index == node.keyCount) ? 
                    (node.children[index].keyCount < degree) : 
                    (node.children[index].keyCount < degree);

                // 如果子节点的键数量小于degree，需要合并或借键
                if (shouldMerge) {
                    if (index != 0 && node.children[index - 1].keyCount >= degree) {
                        // 从左兄弟借键
                        borrowFromPrev(node, index);
                    } else if (index != node.keyCount && node.children[index + 1].keyCount >= degree) {
                        // 从右兄弟借键
                        borrowFromNext(node, index);
                    } else {
                        // 合并子节点
                        if (index != node.keyCount) {
                            merge(node, index);
                        } else {
                            merge(node, index - 1);
                        }
                    }
                }

                // 递归删除
                if (index > node.keyCount) {
                    deleteKey(node.children[index - 1], key);
                } else {
                    deleteKey(node.children[index], key);
                }
            }
        }

        /**
         * 查找键在节点中的位置
         */
        private int findKey(BTreeNode node, int key) {
            int index = 0;
            while (index < node.keyCount && node.keys[index] < key) {
                index++;
            }
            return index;
        }

        /**
         * 从叶子节点删除键
         */
        private void deleteFromLeaf(BTreeNode node, int index) {
            for (int i = index + 1; i < node.keyCount; i++) {
                node.keys[i - 1] = node.keys[i];
            }
            node.keyCount--;
        }

        /**
         * 从非叶子节点删除键
         */
        private void deleteFromNonLeaf(BTreeNode node, int index) {
            int key = node.keys[index];

            // 如果左子节点的键数量 >= degree，找到前驱键并替换
            if (node.children[index].keyCount >= degree) {
                int predecessor = getPredecessor(node, index);
                node.keys[index] = predecessor;
                deleteKey(node.children[index], predecessor);
            } 
            // 如果右子节点的键数量 >= degree，找到后继键并替换
            else if (node.children[index + 1].keyCount >= degree) {
                int successor = getSuccessor(node, index);
                node.keys[index] = successor;
                deleteKey(node.children[index + 1], successor);
            } 
            // 左右子节点的键数量都 < degree，合并子节点
            else {
                merge(node, index);
                deleteKey(node.children[index], key);
            }
        }

        /**
         * 获取前驱键
         */
        private int getPredecessor(BTreeNode node, int index) {
            BTreeNode current = node.children[index];
            while (!current.isLeaf) {
                current = current.children[current.keyCount];
            }
            return current.keys[current.keyCount - 1];
        }

        /**
         * 获取后继键
         */
        private int getSuccessor(BTreeNode node, int index) {
            BTreeNode current = node.children[index + 1];
            while (!current.isLeaf) {
                current = current.children[0];
            }
            return current.keys[0];
        }

        /**
         * 从左兄弟借键
         */
        private void borrowFromPrev(BTreeNode node, int index) {
            BTreeNode child = node.children[index];
            BTreeNode sibling = node.children[index - 1];

            // 将父节点的键下移到子节点
            for (int i = child.keyCount - 1; i >= 0; i--) {
                child.keys[i + 1] = child.keys[i];
            }

            if (!child.isLeaf) {
                for (int i = child.keyCount; i >= 0; i--) {
                    child.children[i + 1] = child.children[i];
                }
            }

            child.keys[0] = node.keys[index - 1];

            if (!child.isLeaf) {
                child.children[0] = sibling.children[sibling.keyCount];
            }

            // 将兄弟节点的键上移到父节点
            node.keys[index - 1] = sibling.keys[sibling.keyCount - 1];

            child.keyCount++;
            sibling.keyCount--;
        }

        /**
         * 从右兄弟借键
         */
        private void borrowFromNext(BTreeNode node, int index) {
            BTreeNode child = node.children[index];
            BTreeNode sibling = node.children[index + 1];

            // 将父节点的键下移到子节点
            child.keys[child.keyCount] = node.keys[index];

            if (!child.isLeaf) {
                child.children[child.keyCount + 1] = sibling.children[0];
            }

            // 将兄弟节点的键上移到父节点
            node.keys[index] = sibling.keys[0];

            for (int i = 1; i < sibling.keyCount; i++) {
                sibling.keys[i - 1] = sibling.keys[i];
            }

            if (!sibling.isLeaf) {
                for (int i = 1; i <= sibling.keyCount; i++) {
                    sibling.children[i - 1] = sibling.children[i];
                }
            }

            child.keyCount++;
            sibling.keyCount--;
        }

        /**
         * 合并子节点
         */
        private void merge(BTreeNode node, int index) {
            BTreeNode child = node.children[index];
            BTreeNode sibling = node.children[index + 1];

            // 将父节点的键下移到子节点
            child.keys[degree - 1] = node.keys[index];

            // 复制兄弟节点的键到子节点
            for (int i = 0; i < sibling.keyCount; i++) {
                child.keys[i + degree] = sibling.keys[i];
            }

            // 复制兄弟节点的子节点到子节点
            if (!child.isLeaf) {
                for (int i = 0; i <= sibling.keyCount; i++) {
                    child.children[i + degree] = sibling.children[i];
                }
            }

            // 移动父节点的键和子节点
            for (int i = index + 1; i < node.keyCount; i++) {
                node.keys[i - 1] = node.keys[i];
            }

            for (int i = index + 2; i <= node.keyCount; i++) {
                node.children[i - 1] = node.children[i];
            }

            child.keyCount += sibling.keyCount + 1;
            node.keyCount--;
        }

        /**
         * 打印B树的结构
         */
        public void print() {
            System.out.println("B树结构:");
            printHelper(root, 0);
        }

        /**
         * 打印B树的辅助方法
         */
        private void printHelper(BTreeNode node, int level) {
            if (node != null) {
                System.out.print("Level " + level + ": ");
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
     * 测试B树
     */
    public void testBTree() {
        System.out.println("=== B树示例 ===");
        
        // 创建一个3阶B树
        BTree bTree = new BTree(3);
        
        // 插入元素
        bTree.insert(10);
        bTree.insert(20);
        bTree.insert(5);
        bTree.insert(6);
        bTree.insert(12);
        bTree.insert(30);
        bTree.insert(7);
        bTree.insert(17);
        bTree.insert(19);
        bTree.insert(25);
        bTree.insert(28);
        bTree.insert(35);
        
        // 打印B树结构
        bTree.print();
        
        // 遍历B树
        System.out.print("B树遍历: ");
        bTree.traverse();
        
        // 查找元素
        int key = 12;
        BTreeNode node = bTree.search(key);
        System.out.println("查找键 " + key + ": " + (node != null ? "找到" : "未找到"));
        
        key = 15;
        node = bTree.search(key);
        System.out.println("查找键 " + key + ": " + (node != null ? "找到" : "未找到"));
        
        // 删除元素
        System.out.println("\n删除键 6:");
        bTree.delete(6);
        bTree.print();
        System.out.print("B树遍历: ");
        bTree.traverse();
        
        System.out.println("\n删除键 12:");
        bTree.delete(12);
        bTree.print();
        System.out.print("B树遍历: ");
        bTree.traverse();
        
        System.out.println("\n删除键 10:");
        bTree.delete(10);
        bTree.print();
        System.out.print("B树遍历: ");
        bTree.traverse();
    }

    /**
     * B树应用示例
     */
    public void bTreeApplications() {
        System.out.println("\n=== B树应用示例 ===");
        
        // 示例1：数据库索引
        System.out.println("示例1：数据库索引");
        BTree dbIndex = new BTree(3);
        
        // 插入一些记录的主键
        int[] primaryKeys = {101, 102, 103, 104, 105, 106, 107, 108, 109, 110};
        for (int key : primaryKeys) {
            dbIndex.insert(key);
        }
        
        System.out.println("数据库索引结构:");
        dbIndex.print();
        
        // 查找记录
        int searchKey = 105;
        BTreeNode result = dbIndex.search(searchKey);
        System.out.println("查找主键 " + searchKey + " 的记录: " + (result != null ? "找到" : "未找到"));
        
        // 删除记录
        System.out.println("\n删除主键 103 的记录:");
        dbIndex.delete(103);
        dbIndex.print();
        
        // 示例2：文件系统索引
        System.out.println("\n示例2：文件系统索引");
        BTree fileIndex = new BTree(4);
        
        // 插入一些文件的inode号
        int[] inodeNumbers = {1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010, 1011, 1012};
        for (int inode : inodeNumbers) {
            fileIndex.insert(inode);
        }
        
        System.out.println("文件系统索引结构:");
        fileIndex.print();
        
        // 查找文件
        int inodeToFind = 1007;
        BTreeNode fileNode = fileIndex.search(inodeToFind);
        System.out.println("查找inode号 " + inodeToFind + " 的文件: " + (fileNode != null ? "找到" : "未找到"));
    }

    /**
     * B树性能分析
     */
    public void bTreePerformance() {
        System.out.println("\n=== B树性能分析 ===");
        
        int[] degrees = {3, 5, 10};
        int operations = 1000;
        
        for (int degree : degrees) {
            BTree bTree = new BTree(degree);
            
            // 测试插入性能
            long startTime = System.nanoTime();
            for (int i = 0; i < operations; i++) {
                bTree.insert((int) (Math.random() * 10000));
            }
            long insertTime = System.nanoTime() - startTime;
            
            // 测试查找性能
            startTime = System.nanoTime();
            for (int i = 0; i < operations; i++) {
                bTree.search((int) (Math.random() * 10000));
            }
            long searchTime = System.nanoTime() - startTime;
            
            System.out.println("阶数为" + degree + "的B树:");
            System.out.println("插入" + operations + "个元素的时间: " + insertTime + " ns");
            System.out.println("查找" + operations + "个元素的时间: " + searchTime + " ns");
            System.out.println();
        }
        
        // 分析结果
        System.out.println("性能分析:");
        System.out.println("1. 随着B树阶数的增加，树的高度减少，磁盘I/O操作次数减少");
        System.out.println("2. 阶数过大可能会导致内存使用增加，节点分裂和合并操作变复杂");
        System.out.println("3. 实际应用中，B树的阶数通常根据磁盘块大小和数据大小来确定");
    }
}
