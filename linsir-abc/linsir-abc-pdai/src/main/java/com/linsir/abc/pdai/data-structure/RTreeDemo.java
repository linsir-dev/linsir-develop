package com.linsir.abc.pdai.data-structure;

import java.util.ArrayList;
import java.util.List;

/**
 * R树示例代码
 * 
 * 说明：
 * 1. R树是一种空间索引数据结构，用于存储和查询多维空间数据
 * 2. R树的特点：
 *    - 每个节点代表一个矩形区域
 *    - 叶子节点存储空间对象及其边界矩形
 *    - 内部节点存储子节点的边界矩形
 *    - 所有叶子节点都在同一层
 *    - 对于一个m阶R树：
 *      - 根节点至少有2个子节点（除非它是叶子节点）
 *      - 非根节点至少有⌈m/2⌉个子节点
 *      - 每个节点最多有m个子节点
 * 3. R树的优势：
 *    - 支持高效的空间范围查询
 *    - 支持高效的最近邻查询
 *    - 适合存储多维空间数据
 * 4. 应用场景：
 *    - 地理信息系统(GIS)
 *    - 空间数据库
 *    - 计算机辅助设计(CAD)
 *    - 图像数据库
 */
public class RTreeDemo {

    /**
     * 矩形类，用于表示空间对象的边界
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
        double area() {
            return (x2 - x1) * (y2 - y1);
        }

        /**
         * 计算两个矩形的最小边界矩形(MBR)
         */
        static Rectangle merge(Rectangle r1, Rectangle r2) {
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
        boolean intersects(Rectangle other) {
            return !(x2 < other.x1 || x1 > other.x2 || y2 < other.y1 || y1 > other.y2);
        }

        /**
         * 检查当前矩形是否包含另一个矩形
         */
        boolean contains(Rectangle other) {
            return x1 <= other.x1 && y1 <= other.y1 && x2 >= other.x2 && y2 >= other.y2;
        }

        @Override
        public String toString() {
            return String.format("[%.1f, %.1f, %.1f, %.1f]", x1, y1, x2, y2);
        }
    }

    /**
     * 空间对象类
     */
    private static class SpatialObject {
        String id;
        Rectangle rectangle;

        SpatialObject(String id, double x1, double y1, double x2, double y2) {
            this.id = id;
            this.rectangle = new Rectangle(x1, y1, x2, y2);
        }

        @Override
        public String toString() {
            return id + ": " + rectangle;
        }
    }

    /**
     * R树节点类
     */
    private static class RTreeNode {
        List<RTreeNode> children; // 子节点列表
        List<Rectangle> rectangles; // 边界矩形列表
        List<SpatialObject> objects; // 空间对象列表（仅叶子节点使用）
        boolean isLeaf; // 是否为叶子节点

        RTreeNode(boolean isLeaf) {
            this.children = new ArrayList<>();
            this.rectangles = new ArrayList<>();
            this.objects = isLeaf ? new ArrayList<>() : null;
            this.isLeaf = isLeaf;
        }

        /**
         * 检查节点是否已满
         */
        boolean isFull(int order) {
            return isLeaf ? objects.size() >= order : children.size() >= order;
        }

        /**
         * 检查节点是否需要分裂
         */
        boolean needsSplit(int order) {
            return isLeaf ? objects.size() > order : children.size() > order;
        }
    }

    /**
     * R树类
     */
    private static class RTree {
        private RTreeNode root;
        private int order; // R树的阶

        /**
         * 构造函数
         * @param order R树的阶
         */
        RTree(int order) {
            this.order = order;
            this.root = new RTreeNode(true); // 初始根节点是叶子节点
        }

        /**
         * 插入空间对象
         */
        public void insert(SpatialObject obj) {
            if (root.isFull(order)) {
                // 根节点已满，需要分裂
                RTreeNode newRoot = new RTreeNode(false);
                RTreeNode[] splits = splitNode(root);
                newRoot.children.add(splits[0]);
                newRoot.children.add(splits[1]);
                newRoot.rectangles.add(calculateMBR(splits[0]));
                newRoot.rectangles.add(calculateMBR(splits[1]));
                root = newRoot;
            }
            insert(root, obj);
        }

        /**
         * 递归插入空间对象
         */
        private void insert(RTreeNode node, SpatialObject obj) {
            if (node.isLeaf) {
                // 叶子节点，直接插入
                node.objects.add(obj);
                node.rectangles.add(obj.rectangle);
                
                // 如果节点需要分裂
                if (node.needsSplit(order)) {
                    RTreeNode parent = findParent(root, node);
                    if (parent == null) {
                        // 根节点需要分裂
                        RTreeNode newRoot = new RTreeNode(false);
                        RTreeNode[] splits = splitNode(node);
                        newRoot.children.add(splits[0]);
                        newRoot.children.add(splits[1]);
                        newRoot.rectangles.add(calculateMBR(splits[0]));
                        newRoot.rectangles.add(calculateMBR(splits[1]));
                        root = newRoot;
                    } else {
                        // 非根节点需要分裂
                        int index = parent.children.indexOf(node);
                        parent.children.remove(index);
                        parent.rectangles.remove(index);
                        
                        RTreeNode[] splits = splitNode(node);
                        parent.children.add(splits[0]);
                        parent.children.add(splits[1]);
                        parent.rectangles.add(calculateMBR(splits[0]));
                        parent.rectangles.add(calculateMBR(splits[1]));
                        
                        // 如果父节点需要分裂
                        if (parent.needsSplit(order)) {
                            splitParent(parent);
                        }
                    }
                }
            } else {
                // 内部节点，选择最合适的子节点插入
                int bestChildIndex = chooseBestChild(node, obj.rectangle);
                RTreeNode bestChild = node.children.get(bestChildIndex);
                
                // 如果子节点已满，需要分裂
                if (bestChild.isFull(order)) {
                    RTreeNode[] splits = splitNode(bestChild);
                    node.children.remove(bestChildIndex);
                    node.rectangles.remove(bestChildIndex);
                    node.children.add(bestChildIndex, splits[0]);
                    node.children.add(bestChildIndex + 1, splits[1]);
                    node.rectangles.add(bestChildIndex, calculateMBR(splits[0]));
                    node.rectangles.add(bestChildIndex + 1, calculateMBR(splits[1]));
                    
                    // 重新选择最合适的子节点
                    bestChildIndex = chooseBestChild(node, obj.rectangle);
                    bestChild = node.children.get(bestChildIndex);
                }
                
                // 插入到子节点
                insert(bestChild, obj);
                
                // 更新边界矩形
                node.rectangles.set(bestChildIndex, calculateMBR(bestChild));
            }
        }

        /**
         * 选择最合适的子节点
         */
        private int chooseBestChild(RTreeNode node, Rectangle rect) {
            int bestIndex = 0;
            double minEnlargement = Double.MAX_VALUE;
            
            for (int i = 0; i < node.children.size(); i++) {
                Rectangle childRect = node.rectangles.get(i);
                double originalArea = childRect.area();
                Rectangle mergedRect = Rectangle.merge(childRect, rect);
                double enlargedArea = mergedRect.area();
                double enlargement = enlargedArea - originalArea;
                
                if (enlargement < minEnlargement) {
                    minEnlargement = enlargement;
                    bestIndex = i;
                }
            }
            
            return bestIndex;
        }

        /**
         * 分裂节点
         */
        private RTreeNode[] splitNode(RTreeNode node) {
            // 这里使用简单的线性分裂策略
            // 实际应用中可能会使用更复杂的分裂策略，如二次分裂
            RTreeNode left = new RTreeNode(node.isLeaf);
            RTreeNode right = new RTreeNode(node.isLeaf);
            
            if (node.isLeaf) {
                // 分裂叶子节点
                int mid = node.objects.size() / 2;
                for (int i = 0; i < mid; i++) {
                    left.objects.add(node.objects.get(i));
                    left.rectangles.add(node.rectangles.get(i));
                }
                for (int i = mid; i < node.objects.size(); i++) {
                    right.objects.add(node.objects.get(i));
                    right.rectangles.add(node.rectangles.get(i));
                }
            } else {
                // 分裂内部节点
                int mid = node.children.size() / 2;
                for (int i = 0; i < mid; i++) {
                    left.children.add(node.children.get(i));
                    left.rectangles.add(node.rectangles.get(i));
                }
                for (int i = mid; i < node.children.size(); i++) {
                    right.children.add(node.children.get(i));
                    right.rectangles.add(node.rectangles.get(i));
                }
            }
            
            return new RTreeNode[]{left, right};
        }

        /**
         * 分裂父节点
         */
        private void splitParent(RTreeNode node) {
            RTreeNode parent = findParent(root, node);
            if (parent == null) {
                // 根节点需要分裂
                RTreeNode newRoot = new RTreeNode(false);
                RTreeNode[] splits = splitNode(node);
                newRoot.children.add(splits[0]);
                newRoot.children.add(splits[1]);
                newRoot.rectangles.add(calculateMBR(splits[0]));
                newRoot.rectangles.add(calculateMBR(splits[1]));
                root = newRoot;
            } else {
                // 非根节点需要分裂
                int index = parent.children.indexOf(node);
                parent.children.remove(index);
                parent.rectangles.remove(index);
                
                RTreeNode[] splits = splitNode(node);
                parent.children.add(index, splits[0]);
                parent.children.add(index + 1, splits[1]);
                parent.rectangles.add(index, calculateMBR(splits[0]));
                parent.rectangles.add(index + 1, calculateMBR(splits[1]));
                
                // 如果父节点需要分裂，递归处理
                if (parent.needsSplit(order)) {
                    splitParent(parent);
                }
            }
        }

        /**
         * 查找节点的父节点
         */
        private RTreeNode findParent(RTreeNode node, RTreeNode child) {
            if (node.isLeaf) {
                return null;
            }
            
            for (RTreeNode c : node.children) {
                if (c == child) {
                    return node;
                }
                RTreeNode parent = findParent(c, child);
                if (parent != null) {
                    return parent;
                }
            }
            
            return null;
        }

        /**
         * 计算节点的最小边界矩形(MBR)
         */
        private Rectangle calculateMBR(RTreeNode node) {
            if (node.isLeaf) {
                if (node.objects.isEmpty()) {
                    return new Rectangle(0, 0, 0, 0);
                }
                Rectangle mbr = node.rectangles.get(0);
                for (int i = 1; i < node.rectangles.size(); i++) {
                    mbr = Rectangle.merge(mbr, node.rectangles.get(i));
                }
                return mbr;
            } else {
                if (node.children.isEmpty()) {
                    return new Rectangle(0, 0, 0, 0);
                }
                Rectangle mbr = node.rectangles.get(0);
                for (int i = 1; i < node.rectangles.size(); i++) {
                    mbr = Rectangle.merge(mbr, node.rectangles.get(i));
                }
                return mbr;
            }
        }

        /**
         * 范围查询
         */
        public List<SpatialObject> rangeQuery(Rectangle queryRect) {
            List<SpatialObject> results = new ArrayList<>();
            rangeQuery(root, queryRect, results);
            return results;
        }

        /**
         * 递归范围查询
         */
        private void rangeQuery(RTreeNode node, Rectangle queryRect, List<SpatialObject> results) {
            if (node.isLeaf) {
                // 叶子节点，检查每个对象是否与查询矩形相交
                for (int i = 0; i < node.objects.size(); i++) {
                    SpatialObject obj = node.objects.get(i);
                    if (obj.rectangle.intersects(queryRect)) {
                        results.add(obj);
                    }
                }
            } else {
                // 内部节点，检查每个子节点的边界矩形是否与查询矩形相交
                for (int i = 0; i < node.children.size(); i++) {
                    Rectangle rect = node.rectangles.get(i);
                    if (rect.intersects(queryRect)) {
                        rangeQuery(node.children.get(i), queryRect, results);
                    }
                }
            }
        }

        /**
         * 打印R树
         */
        public void print() {
            System.out.println("R树结构:");
            print(root, 0);
        }

        /**
         * 递归打印R树
         */
        private void print(RTreeNode node, int level) {
            // 打印当前节点的边界矩形
            System.out.print("Level " + level + ": ");
            if (node.isLeaf) {
                for (SpatialObject obj : node.objects) {
                    System.out.print(obj + " ");
                }
            } else {
                for (Rectangle rect : node.rectangles) {
                    System.out.print(rect + " ");
                }
            }
            System.out.println();

            // 递归打印子节点
            if (!node.isLeaf) {
                for (RTreeNode child : node.children) {
                    print(child, level + 1);
                }
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
        
        // 创建空间对象
        SpatialObject obj1 = new SpatialObject("A", 0, 0, 2, 2);
        SpatialObject obj2 = new SpatialObject("B", 1, 1, 3, 3);
        SpatialObject obj3 = new SpatialObject("C", 4, 4, 6, 6);
        SpatialObject obj4 = new SpatialObject("D", 5, 5, 7, 7);
        SpatialObject obj5 = new SpatialObject("E", 2, 4, 4, 6);
        
        // 插入空间对象
        rTree.insert(obj1);
        rTree.insert(obj2);
        rTree.insert(obj3);
        rTree.insert(obj4);
        rTree.insert(obj5);
        
        // 打印R树
        rTree.print();
        
        // 测试范围查询
        System.out.println("\n范围查询 [1, 1, 5, 5]:");
        Rectangle queryRect = new Rectangle(1, 1, 5, 5);
        List<SpatialObject> results = rTree.rangeQuery(queryRect);
        for (SpatialObject obj : results) {
            System.out.println(obj);
        }
    }

    /**
     * R树的应用示例
     */
    public void applications() {
        System.out.println("\n=== R树应用示例 ===");
        
        // 示例1: 地理信息系统(GIS)
        System.out.println("示例1: 地理信息系统(GIS)");
        System.out.println("R树在GIS中的应用:");
        System.out.println("1. 存储和索引地理空间对象，如点、线、多边形等");
        System.out.println("2. 支持高效的空间范围查询，如查找某个区域内的所有城市");
        System.out.println("3. 支持高效的最近邻查询，如查找距离某个点最近的医院");
        
        // 示例2: 空间数据库
        System.out.println("\n示例2: 空间数据库");
        System.out.println("R树在空间数据库中的应用:");
        System.out.println("1. 作为空间索引的实现方式");
        System.out.println("2. 加速空间查询操作，如ST_Contains, ST_Intersects等");
        System.out.println("3. 支持复杂的空间分析操作");
        
        // 示例3: 计算机辅助设计(CAD)
        System.out.println("\n示例3: 计算机辅助设计(CAD)");
        System.out.println("R树在CAD中的应用:");
        System.out.println("1. 存储和索引设计对象的空间位置");
        System.out.println("2. 支持高效的空间查询，如查找某个区域内的所有组件");
        System.out.println("3. 加速碰撞检测等操作");
    }

    /**
     * R树与其他空间索引的比较
     */
    public void compareWithOtherSpatialIndexes() {
        System.out.println("\n=== R树与其他空间索引的比较 ===");
        
        System.out.println("1. R树 vs Quad树:");
        System.out.println("   - R树: 支持可变大小的空间对象，更灵活");
        System.out.println("   - Quad树: 适合点对象，空间划分固定");
        
        System.out.println("\n2. R树 vs k-d树:");
        System.out.println("   - R树: 支持多维空间，适合范围查询");
        System.out.println("   - k-d树: 适合低维空间，最近邻查询效率高");
        
        System.out.println("\n3. R树 vs Grid索引:");
        System.out.println("   - R树: 自适应空间划分，适合分布不均匀的数据");
        System.out.println("   - Grid索引: 固定网格划分，适合分布均匀的数据");
    }
}
