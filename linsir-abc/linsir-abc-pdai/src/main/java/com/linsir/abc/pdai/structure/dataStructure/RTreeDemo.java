package com.linsir.abc.pdai.structure.dataStructure;

/**
 * R树示例代码
 * 
 * 说明：
 * 1. R树是一种空间索引数据结构，用于存储和查询多维空间中的对象
 * 2. R树的特点：
 *    - 适用于多维空间数据的索引
 *    - 支持范围查询和最近邻查询
 *    - 对于一个m阶的R树：
 *      - 根节点至少有2个子节点（除非它是叶子节点）
 *      - 每个非根节点至少有⌈m/2⌉个子节点
 *      - 每个节点最多有m个子节点
 *      - 每个节点存储的是空间对象的最小边界矩形（MBR）
 * 3. R树的时间复杂度：
 *    - 查找、插入、删除的时间复杂度均为O(log_m n)
 * 4. 应用场景：
 *    - 地理信息系统（GIS）
 *    - 空间数据库
 *    - 多媒体数据库
 *    - 计算机辅助设计（CAD）
 */
public class RTreeDemo {

    /**
     * 矩形类，用于表示空间对象的最小边界矩形（MBR）
     */
    private static class Rectangle {
        double x1, y1; // 左下角坐标
        double x2, y2; // 右上角坐标

        Rectangle(double x1, double y1, double x2, double y2) {
            this.x1 = Math.min(x1, x2);
            this.y1 = Math.min(y1, y2);
            this.x2 = Math.max(x1, x2);
            this.y2 = Math.max(y1, y2);
        }

        /**
         * 计算矩形的面积
         */
        public double area() {
            return (x2 - x1) * (y2 - y1);
        }

        /**
         * 计算两个矩形的最小边界矩形
         */
        public static Rectangle union(Rectangle r1, Rectangle r2) {
            return new Rectangle(
                Math.min(r1.x1, r2.x1),
                Math.min(r1.y1, r2.y1),
                Math.max(r1.x2, r2.x2),
                Math.max(r1.y2, r2.y2)
            );
        }

        /**
         * 检查两个矩形是否相交
         */
        public boolean intersects(Rectangle other) {
            return !(this.x2 < other.x1 || this.x1 > other.x2 || this.y2 < other.y1 || this.y1 > other.y2);
        }

        /**
         * 检查矩形是否包含另一个矩形
         */
        public boolean contains(Rectangle other) {
            return this.x1 <= other.x1 && this.y1 <= other.y1 && this.x2 >= other.x2 && this.y2 >= other.y2;
        }

        @Override
        public String toString() {
            return "[" + x1 + ", " + y1 + "] - [" + x2 + ", " + y2 + "]";
        }
    }

    /**
     * R树节点类
     */
    private static class RTreeNode {
        Rectangle[] mbrs;    // 最小边界矩形数组
        Object[] data;       // 数据对象数组（仅叶子节点使用）
        RTreeNode[] children; // 子节点数组（仅非叶子节点使用）
        int degree;          // R树的阶
        int entryCount;      // 当前节点的条目数量
        boolean isLeaf;      // 是否为叶子节点

        RTreeNode(int degree, boolean isLeaf) {
            this.degree = degree;
            this.isLeaf = isLeaf;
            this.mbrs = new Rectangle[degree];
            this.data = isLeaf ? new Object[degree] : null;
            this.children = isLeaf ? null : new RTreeNode[degree];
            this.entryCount = 0;
        }

        /**
         * 添加条目到节点
         */
        public void addEntry(Rectangle mbr, Object data, RTreeNode child) {
            mbrs[entryCount] = mbr;
            if (isLeaf) {
                this.data[entryCount] = data;
            } else {
                children[entryCount] = child;
            }
            entryCount++;
        }

        /**
         * 计算节点的最小边界矩形
         */
        public Rectangle getMBR() {
            if (entryCount == 0) {
                return null;
            }

            Rectangle mbr = mbrs[0];
            for (int i = 1; i < entryCount; i++) {
                mbr = Rectangle.union(mbr, mbrs[i]);
            }
            return mbr;
        }

        /**
         * 查找与查询矩形相交的所有条目
         */
        public void search(Rectangle query, java.util.List<Object> results) {
            for (int i = 0; i < entryCount; i++) {
                if (mbrs[i].intersects(query)) {
                    if (isLeaf) {
                        results.add(data[i]);
                    } else {
                        children[i].search(query, results);
                    }
                }
            }
        }

        /**
         * 打印节点信息
         */
        public void print(int level) {
            String indent = String.valueOf(level);
            System.out.println(indent + "Node (level " + level + ", " + (isLeaf ? "Leaf" : "Non-leaf") + ", entries: " + entryCount + ")");
            
            for (int i = 0; i < entryCount; i++) {
                System.out.println(indent + "  Entry " + i + ": MBR=" + mbrs[i]);
                if (!isLeaf) {
                    children[i].print(level + 1);
                } else {
                    System.out.println(indent + "    Data: " + data[i]);
                }
            }
        }
    }

    /**
     * R树类
     */
    private static class RTree {
        private RTreeNode root;
        private int degree; // R树的阶

        RTree(int degree) {
            this.degree = Math.max(2, degree); // 确保阶数至少为2
            this.root = new RTreeNode(degree, true);
        }

        /**
         * 插入空间对象
         */
        public void insert(Rectangle mbr, Object data) {
            // 插入到根节点
            InsertResult result = insert(root, mbr, data);
            
            // 如果根节点分裂，创建新的根节点
            if (result != null) {
                RTreeNode newRoot = new RTreeNode(degree, false);
                newRoot.addEntry(root.getMBR(), null, root);
                newRoot.addEntry(result.mbr, null, result.node);
                root = newRoot;
            }
        }

        /**
         * 插入的辅助方法
         */
        private InsertResult insert(RTreeNode node, Rectangle mbr, Object data) {
            if (node.isLeaf) {
                // 叶子节点，直接插入
                node.addEntry(mbr, data, null);
                
                // 检查是否需要分裂
                if (node.entryCount >= degree) {
                    return split(node);
                }
                return null;
            } else {
                // 非叶子节点，选择最合适的子节点插入
                int bestChildIndex = chooseSubtree(node, mbr);
                InsertResult result = insert(node.children[bestChildIndex], mbr, data);
                
                // 更新子节点的MBR
                node.mbrs[bestChildIndex] = node.children[bestChildIndex].getMBR();
                
                // 如果子节点分裂，处理分裂结果
                if (result != null) {
                    node.addEntry(result.mbr, null, result.node);
                    
                    // 检查是否需要分裂
                    if (node.entryCount >= degree) {
                        return split(node);
                    }
                }
                return null;
            }
        }

        /**
         * 选择最合适的子树插入
         */
        private int chooseSubtree(RTreeNode node, Rectangle mbr) {
            int bestIndex = 0;
            double minEnlargement = Double.MAX_VALUE;
            
            for (int i = 0; i < node.entryCount; i++) {
                double originalArea = node.mbrs[i].area();
                Rectangle union = Rectangle.union(node.mbrs[i], mbr);
                double newArea = union.area();
                double enlargement = newArea - originalArea;
                
                if (enlargement < minEnlargement) {
                    minEnlargement = enlargement;
                    bestIndex = i;
                } else if (enlargement == minEnlargement) {
                    // 如果扩大面积相同，选择面积较小的MBR
                    if (node.mbrs[i].area() < node.mbrs[bestIndex].area()) {
                        bestIndex = i;
                    }
                }
            }
            
            return bestIndex;
        }

        /**
         * 分裂节点
         */
        private InsertResult split(RTreeNode node) {
            // 创建新的节点
            RTreeNode newNode = new RTreeNode(degree, node.isLeaf);
            
            // 选择分裂轴和分裂点（这里使用简单的线性扫描方法）
            int[] splitResult = pickSeeds(node);
            int seed1 = splitResult[0];
            int seed2 = splitResult[1];
            
            // 将两个种子分别放入原节点和新节点
            newNode.addEntry(node.mbrs[seed2], node.isLeaf ? node.data[seed2] : null, node.isLeaf ? null : node.children[seed2]);
            
            // 移除种子2
            removeEntry(node, seed2);
            
            // 重新分配剩余的条目
            while (node.entryCount > 0) {
                if (newNode.entryCount >= (degree + 1) / 2) {
                    // 新节点已满，将所有剩余条目保留在原节点
                    break;
                } else if (node.entryCount == (degree + 1) / 2) {
                    // 原节点即将达到最小条目数，将所有剩余条目保留在原节点
                    break;
                } else {
                    // 选择下一个条目分配
                    int nextEntry = pickNext(node, newNode);
                    newNode.addEntry(node.mbrs[nextEntry], node.isLeaf ? node.data[nextEntry] : null, node.isLeaf ? null : node.children[nextEntry]);
                    removeEntry(node, nextEntry);
                }
            }
            
            return new InsertResult(newNode.getMBR(), newNode);
        }

        /**
         * 选择分裂的种子条目
         */
        private int[] pickSeeds(RTreeNode node) {
            int seed1 = 0;
            int seed2 = 1;
            double maxDistance = 0;
            
            // 计算所有条目对之间的距离，选择距离最大的一对作为种子
            for (int i = 0; i < node.entryCount; i++) {
                for (int j = i + 1; j < node.entryCount; j++) {
                    double distance = calculateDistance(node.mbrs[i], node.mbrs[j]);
                    if (distance > maxDistance) {
                        maxDistance = distance;
                        seed1 = i;
                        seed2 = j;
                    }
                }
            }
            
            return new int[]{seed1, seed2};
        }

        /**
         * 计算两个矩形之间的距离
         */
        private double calculateDistance(Rectangle r1, Rectangle r2) {
            double dx = Math.max(0, Math.max(r1.x1 - r2.x2, r2.x1 - r1.x2));
            double dy = Math.max(0, Math.max(r1.y1 - r2.y2, r2.y1 - r1.y2));
            return Math.sqrt(dx * dx + dy * dy);
        }

        /**
         * 选择下一个要分配的条目
         */
        private int pickNext(RTreeNode node, RTreeNode newNode) {
            int bestEntry = 0;
            double maxDifference = Double.MIN_VALUE;
            
            for (int i = 0; i < node.entryCount; i++) {
                double cost1 = Rectangle.union(node.getMBR(), node.mbrs[i]).area() - node.getMBR().area();
                double cost2 = Rectangle.union(newNode.getMBR(), node.mbrs[i]).area() - newNode.getMBR().area();
                double difference = Math.abs(cost1 - cost2);
                
                if (difference > maxDifference) {
                    maxDifference = difference;
                    bestEntry = i;
                }
            }
            
            return bestEntry;
        }

        /**
         * 从节点中移除条目
         */
        private void removeEntry(RTreeNode node, int index) {
            // 移动后续条目
            for (int i = index; i < node.entryCount - 1; i++) {
                node.mbrs[i] = node.mbrs[i + 1];
                if (node.isLeaf) {
                    node.data[i] = node.data[i + 1];
                } else {
                    node.children[i] = node.children[i + 1];
                }
            }
            
            // 清空最后一个条目
            node.mbrs[node.entryCount - 1] = null;
            if (node.isLeaf) {
                node.data[node.entryCount - 1] = null;
            } else {
                node.children[node.entryCount - 1] = null;
            }
            
            node.entryCount--;
        }

        /**
         * 范围查询
         */
        public java.util.List<Object> search(Rectangle query) {
            java.util.List<Object> results = new java.util.ArrayList<>();
            search(root, query, results);
            return results;
        }

        /**
         * 范围查询的辅助方法
         */
        private void search(RTreeNode node, Rectangle query, java.util.List<Object> results) {
            node.search(query, results);
        }

        /**
         * 打印R树的结构
         */
        public void print() {
            System.out.println("R树结构:");
            print(root, 0);
        }

        /**
         * 打印的辅助方法
         */
        private void print(RTreeNode node, int level) {
            node.print(level);
        }

        /**
         * 插入结果类
         */
        private static class InsertResult {
            Rectangle mbr;
            RTreeNode node;

            InsertResult(Rectangle mbr, RTreeNode node) {
                this.mbr = mbr;
                this.node = node;
            }
        }
    }

    /**
     * 测试R树
     */
    public void testRTree() {
        System.out.println("=== R树示例 ===");
        
        // 创建一个3阶R树
        RTree rTree = new RTree(3);
        
        // 插入空间对象
        rTree.insert(new Rectangle(0, 0, 10, 10), "区域1");
        rTree.insert(new Rectangle(5, 5, 15, 15), "区域2");
        rTree.insert(new Rectangle(20, 20, 30, 30), "区域3");
        rTree.insert(new Rectangle(25, 25, 35, 35), "区域4");
        rTree.insert(new Rectangle(10, 20, 20, 30), "区域5");
        rTree.insert(new Rectangle(0, 20, 10, 30), "区域6");
        rTree.insert(new Rectangle(15, 0, 25, 10), "区域7");
        rTree.insert(new Rectangle(20, 0, 30, 10), "区域8");
        
        // 打印R树结构
        rTree.print();
        
        // 范围查询
        System.out.println("\n范围查询 [5, 5, 25, 25]:");
        Rectangle query = new Rectangle(5, 5, 25, 25);
        java.util.List<Object> results = rTree.search(query);
        for (Object result : results) {
            System.out.println("找到: " + result);
        }
        
        // 另一个范围查询
        System.out.println("\n范围查询 [15, 15, 35, 35]:");
        query = new Rectangle(15, 15, 35, 35);
        results = rTree.search(query);
        for (Object result : results) {
            System.out.println("找到: " + result);
        }
    }

    /**
     * R树应用示例
     */
    public void rTreeApplications() {
        System.out.println("\n=== R树应用示例 ===");
        
        // 示例1：地理信息系统（GIS）
        System.out.println("示例1：地理信息系统（GIS）");
        RTree gisIndex = new RTree(3);
        
        // 插入城市
        gisIndex.insert(new Rectangle(116.4, 39.9, 116.5, 40.0), "北京");
        gisIndex.insert(new Rectangle(121.4, 31.2, 121.5, 31.3), "上海");
        gisIndex.insert(new Rectangle(113.3, 23.1, 113.4, 23.2), "广州");
        gisIndex.insert(new Rectangle(114.3, 30.6, 114.4, 30.7), "武汉");
        gisIndex.insert(new Rectangle(104.1, 30.7, 104.2, 30.8), "成都");
        
        // 查询指定区域内的城市
        System.out.println("\n查询东经110-120，北纬30-40之间的城市:");
        Rectangle gisQuery = new Rectangle(110, 30, 120, 40);
        java.util.List<Object> gisResults = gisIndex.search(gisQuery);
        for (Object city : gisResults) {
            System.out.println("城市: " + city);
        }
        
        // 示例2：空间数据库
        System.out.println("\n示例2：空间数据库");
        RTree spatialDB = new RTree(4);
        
        // 插入建筑物
        spatialDB.insert(new Rectangle(0, 0, 10, 10), "建筑物A");
        spatialDB.insert(new Rectangle(5, 5, 15, 15), "建筑物B");
        spatialDB.insert(new Rectangle(20, 20, 30, 30), "建筑物C");
        spatialDB.insert(new Rectangle(25, 25, 35, 35), "建筑物D");
        spatialDB.insert(new Rectangle(10, 20, 20, 30), "建筑物E");
        spatialDB.insert(new Rectangle(0, 20, 10, 30), "建筑物F");
        spatialDB.insert(new Rectangle(15, 0, 25, 10), "建筑物G");
        spatialDB.insert(new Rectangle(20, 0, 30, 10), "建筑物H");
        spatialDB.insert(new Rectangle(35, 35, 45, 45), "建筑物I");
        spatialDB.insert(new Rectangle(40, 40, 50, 50), "建筑物J");
        
        // 查询指定区域内的建筑物
        System.out.println("\n查询坐标[10, 10, 30, 30]范围内的建筑物:");
        Rectangle dbQuery = new Rectangle(10, 10, 30, 30);
        java.util.List<Object> dbResults = spatialDB.search(dbQuery);
        for (Object building : dbResults) {
            System.out.println("建筑物: " + building);
        }
        
        // 打印R树结构
        System.out.println("\nR树结构:");
        spatialDB.print();
    }

    /**
     * R树性能分析
     */
    public void rTreePerformance() {
        System.out.println("\n=== R树性能分析 ===");
        
        int[] degrees = {3, 5, 10};
        int operations = 1000;
        
        for (int degree : degrees) {
            RTree rTree = new RTree(degree);
            
            // 生成随机空间对象
            java.util.Random random = new java.util.Random();
            java.util.List<Rectangle> rectangles = new java.util.ArrayList<>();
            
            for (int i = 0; i < operations; i++) {
                double x1 = random.nextDouble() * 1000;
                double y1 = random.nextDouble() * 1000;
                double x2 = x1 + random.nextDouble() * 100;
                double y2 = y1 + random.nextDouble() * 100;
                rectangles.add(new Rectangle(x1, y1, x2, y2));
            }
            
            // 测试插入性能
            long startTime = System.nanoTime();
            for (int i = 0; i < operations; i++) {
                rTree.insert(rectangles.get(i), "Object " + i);
            }
            long insertTime = System.nanoTime() - startTime;
            
            // 测试查询性能
            Rectangle query = new Rectangle(400, 400, 600, 600);
            startTime = System.nanoTime();
            java.util.List<Object> results = rTree.search(query);
            long searchTime = System.nanoTime() - startTime;
            
            System.out.println("阶数为" + degree + "的R树:");
            System.out.println("插入" + operations + "个空间对象的时间: " + insertTime + " ns");
            System.out.println("范围查询的时间: " + searchTime + " ns");
            System.out.println("查询结果数量: " + results.size());
            System.out.println();
        }
        
        // 分析结果
        System.out.println("性能分析:");
        System.out.println("1. 随着R树阶数的增加，树的高度减少，查询性能提高");
        System.out.println("2. 阶数过大可能会导致插入性能下降，因为分裂操作变得更复杂");
        System.out.println("3. 实际应用中，R树的阶数通常根据空间对象的大小和查询模式来确定");
    }
}
