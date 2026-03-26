package com.linsir.abc.core.grammar.oop;

/**
 * 多态示例
 *
 * 本类演示 Java 多态的概念和使用
 * 对应 JDK: 面向对象多态
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class Polymorphism {

    /**
     * 演示运行时多态
     */
    public void demonstrateRuntimePolymorphism() {
        System.out.println("=== 运行时多态 ===");

        // 父类引用指向不同子类对象
        Shape shape1 = new Circle(5);
        Shape shape2 = new Rectangle(4, 5);
        Shape shape3 = new Triangle(3, 4);

        // 同样的方法调用，不同的行为
        Shape[] shapes = {shape1, shape2, shape3};

        System.out.println("遍历形状数组:");
        for (Shape shape : shapes) {
            System.out.println("  " + shape.getName() + " 面积: " + shape.calculateArea());
        }
    }

    /**
     * 演示动态绑定
     */
    public void demonstrateDynamicBinding() {
        System.out.println("\n=== 动态绑定 ===");

        Shape shape = new Circle(3);

        // 编译时类型是 Shape，运行时类型是 Circle
        System.out.println("编译时类型: Shape");
        System.out.println("运行时类型: " + shape.getClass().getSimpleName());

        // 调用的是 Circle 的 calculateArea()
        System.out.println("调用 calculateArea(): " + shape.calculateArea());
    }

    /**
     * 演示类型转换
     */
    public void demonstrateTypeCasting() {
        System.out.println("\n=== 类型转换 ===");

        Shape shape = new Circle(5);

        // 向上转型（自动）
        System.out.println("向上转型:");
        Shape upcasted = new Circle(3);
        System.out.println("  Circle -> Shape (自动)");

        // 向下转型（需要显式）
        System.out.println("\n向下转型:");
        if (upcasted instanceof Circle) {
            Circle circle = (Circle) upcasted;
            System.out.println("  Shape -> Circle (显式转换成功)");
            System.out.println("  半径: " + circle.getRadius());
        }

        // 错误的类型转换
        System.out.println("\n错误的类型转换:");
        Shape rectangle = new Rectangle(3, 4);
        if (rectangle instanceof Circle) {
            Circle circle = (Circle) rectangle;  // 运行时错误
        } else {
            System.out.println("  rectangle 不是 Circle 类型，不能转换");
        }

        // Java 16+ 的模式匹配
        System.out.println("\n模式匹配 instanceof (Java 16+):");
        if (shape instanceof Circle c) {
            System.out.println("  是 Circle，半径: " + c.getRadius());
        }
    }

    /**
     * 演示多态数组
     */
    public void demonstratePolymorphicArray() {
        System.out.println("\n=== 多态数组 ===");

        Shape[] shapes = new Shape[3];
        shapes[0] = new Circle(3);
        shapes[1] = new Rectangle(4, 5);
        shapes[2] = new Triangle(3, 4);

        double totalArea = 0;
        for (Shape shape : shapes) {
            totalArea += shape.calculateArea();
        }

        System.out.println("总面积: " + totalArea);
    }

    /**
     * 形状抽象类
     */
    static abstract class Shape {
        protected String name;

        public Shape(String name) {
            this.name = name;
        }

        public abstract double calculateArea();

        public String getName() {
            return name;
        }
    }

    /**
     * 圆形类
     */
    static class Circle extends Shape {
        private double radius;

        public Circle(double radius) {
            super("圆形");
            this.radius = radius;
        }

        @Override
        public double calculateArea() {
            return Math.PI * radius * radius;
        }

        public double getRadius() {
            return radius;
        }
    }

    /**
     * 矩形类
     */
    static class Rectangle extends Shape {
        private double width;
        private double height;

        public Rectangle(double width, double height) {
            super("矩形");
            this.width = width;
            this.height = height;
        }

        @Override
        public double calculateArea() {
            return width * height;
        }
    }

    /**
     * 三角形类
     */
    static class Triangle extends Shape {
        private double base;
        private double height;

        public Triangle(double base, double height) {
            super("三角形");
            this.base = base;
            this.height = height;
        }

        @Override
        public double calculateArea() {
            return 0.5 * base * height;
        }
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 多态演示                              ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        Polymorphism demo = new Polymorphism();
        demo.demonstrateRuntimePolymorphism();
        demo.demonstrateDynamicBinding();
        demo.demonstrateTypeCasting();
        demo.demonstratePolymorphicArray();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
