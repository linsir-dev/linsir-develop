package com.linsir.abc.core.grammar.oop;

/**
 * 抽象类示例
 *
 * 本类演示 Java 抽象类的概念和使用
 * 对应 JDK: 抽象类和抽象方法
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class AbstractClass {

    /**
     * 演示抽象类的使用
     */
    public void demonstrateAbstractClass() {
        System.out.println("=== 抽象类演示 ===");

        // 不能实例化抽象类
        // Animal animal = new Animal("动物");  // 编译错误

        // 使用具体子类
        Dog dog = new Dog("旺财", 3);
        Cat cat = new Cat("咪咪", 2);

        System.out.println("Dog:");
        dog.eat();
        dog.sleep();
        dog.makeSound();

        System.out.println("\nCat:");
        cat.eat();
        cat.sleep();
        cat.makeSound();
    }

    /**
     * 演示模板方法模式
     */
    public void demonstrateTemplateMethod() {
        System.out.println("\n=== 模板方法模式 ===");

        DataProcessor csvProcessor = new CsvProcessor();
        DataProcessor xmlProcessor = new XmlProcessor();

        System.out.println("处理 CSV 文件:");
        csvProcessor.process("data.csv");

        System.out.println("\n处理 XML 文件:");
        xmlProcessor.process("data.xml");
    }

    /**
     * 动物抽象类
     */
    static abstract class Animal {
        protected String name;
        protected int age;

        public Animal(String name, int age) {
            this.name = name;
            this.age = age;
        }

        // 抽象方法
        public abstract void makeSound();

        // 具体方法
        public void eat() {
            System.out.println(name + " 在吃东西");
        }

        // 具体方法
        public void sleep() {
            System.out.println(name + " 在睡觉");
        }

        // 钩子方法
        public boolean isMammal() {
            return true;
        }
    }

    /**
     * 狗类
     */
    static class Dog extends Animal {
        public Dog(String name, int age) {
            super(name, age);
        }

        @Override
        public void makeSound() {
            System.out.println(name + " 汪汪叫");
        }
    }

    /**
     * 猫类
     */
    static class Cat extends Animal {
        public Cat(String name, int age) {
            super(name, age);
        }

        @Override
        public void makeSound() {
            System.out.println(name + " 喵喵叫");
        }
    }

    /**
     * 数据处理抽象类（模板方法模式）
     */
    static abstract class DataProcessor {

        // 模板方法
        public final void process(String fileName) {
            readFile(fileName);
            parseData();
            validateData();
            saveData();
        }

        // 具体方法
        private void readFile(String fileName) {
            System.out.println("  读取文件: " + fileName);
        }

        // 抽象方法
        protected abstract void parseData();

        // 具体方法
        private void validateData() {
            System.out.println("  验证数据");
        }

        // 具体方法
        private void saveData() {
            System.out.println("  保存数据到数据库");
        }
    }

    /**
     * CSV 处理器
     */
    static class CsvProcessor extends DataProcessor {
        @Override
        protected void parseData() {
            System.out.println("  解析 CSV 格式数据");
        }
    }

    /**
     * XML 处理器
     */
    static class XmlProcessor extends DataProcessor {
        @Override
        protected void parseData() {
            System.out.println("  解析 XML 格式数据");
        }
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 抽象类演示                            ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        AbstractClass demo = new AbstractClass();
        demo.demonstrateAbstractClass();
        demo.demonstrateTemplateMethod();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
