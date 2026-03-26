package com.linsir.abc.core.grammar.oop;

/**
 * 继承示例
 *
 * 本类演示 Java 继承的概念和使用
 * 对应 JDK: 面向对象继承
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class Inheritance {

    /**
     * 演示基本继承
     */
    public void demonstrateBasicInheritance() {
        System.out.println("=== 基本继承 ===");

        // 创建父类对象
        Animal animal = new Animal("动物", 5);
        System.out.println("父类对象:");
        animal.eat();
        animal.sleep();

        // 创建子类对象
        Dog dog = new Dog("旺财", 3, "金毛");
        System.out.println("\n子类对象:");
        dog.eat();    // 继承自父类
        dog.sleep();  // 继承自父类
        dog.bark();   // 子类特有

        // 使用继承的 getter
        System.out.println("\n狗的信息:");
        System.out.println("  名字: " + dog.getName());
        System.out.println("  年龄: " + dog.getAge());
        System.out.println("  品种: " + dog.getBreed());
    }

    /**
     * 演示方法重写
     */
    public void demonstrateMethodOverride() {
        System.out.println("\n=== 方法重写 ===");

        Animal animal = new Animal("动物", 5);
        Dog dog = new Dog("旺财", 3, "金毛");
        Cat cat = new Cat("咪咪", 2, "橘色");

        System.out.println("Animal 的 eat():");
        animal.eat();

        System.out.println("\nDog 的 eat() (重写):");
        dog.eat();

        System.out.println("\nCat 的 eat() (重写):");
        cat.eat();

        // 使用 super 调用父类方法
        System.out.println("\n使用 super 调用父类方法:");
        dog.eatWithSuper();
    }

    /**
     * 演示构造方法链
     */
    public void demonstrateConstructorChaining() {
        System.out.println("\n=== 构造方法链 ===");

        System.out.println("创建 Dog 对象:");
        new Dog("旺财", 3, "金毛");

        System.out.println("\n创建 Cat 对象:");
        new Cat("咪咪", 2, "橘色");
    }

    /**
     * 演示多态
     */
    public void demonstratePolymorphism() {
        System.out.println("\n=== 多态演示 ===");

        // 父类引用指向子类对象
        Animal animal1 = new Dog("旺财", 3, "金毛");
        Animal animal2 = new Cat("咪咪", 2, "橘色");

        System.out.println("animal1 (实际是 Dog):");
        animal1.eat();  // 调用 Dog 的 eat()

        System.out.println("\nanimal2 (实际是 Cat):");
        animal2.eat();  // 调用 Cat 的 eat()

        // 类型检查
        System.out.println("\n类型检查:");
        if (animal1 instanceof Dog) {
            System.out.println("animal1 是 Dog 类型");
            Dog dog = (Dog) animal1;
            dog.bark();
        }
    }

    /**
     * 动物类（父类）
     */
    static class Animal {
        private String name;
        private int age;

        public Animal(String name, int age) {
            this.name = name;
            this.age = age;
            System.out.println("  Animal 构造方法被调用");
        }

        public void eat() {
            System.out.println(name + " 在吃东西");
        }

        public void sleep() {
            System.out.println(name + " 在睡觉");
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    /**
     * 狗类（子类）
     */
    static class Dog extends Animal {
        private String breed;

        public Dog(String name, int age, String breed) {
            super(name, age);  // 调用父类构造方法
            this.breed = breed;
            System.out.println("  Dog 构造方法被调用");
        }

        @Override
        public void eat() {
            System.out.println(getName() + " (狗) 在吃骨头");
        }

        public void eatWithSuper() {
            System.out.println("狗的 eatWithSuper():");
            super.eat();  // 调用父类方法
        }

        public void bark() {
            System.out.println(getName() + " 汪汪叫");
        }

        public String getBreed() {
            return breed;
        }
    }

    /**
     * 猫类（子类）
     */
    static class Cat extends Animal {
        private String color;

        public Cat(String name, int age, String color) {
            super(name, age);
            this.color = color;
            System.out.println("  Cat 构造方法被调用");
        }

        @Override
        public void eat() {
            System.out.println(getName() + " (猫) 在吃鱼");
        }

        public void meow() {
            System.out.println(getName() + " 喵喵叫");
        }

        public String getColor() {
            return color;
        }
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 继承演示                              ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        Inheritance demo = new Inheritance();
        demo.demonstrateBasicInheritance();
        demo.demonstrateMethodOverride();
        demo.demonstrateConstructorChaining();
        demo.demonstratePolymorphism();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
