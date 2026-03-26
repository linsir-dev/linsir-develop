package com.linsir.abc.core.grammar.oop;

/**
 * 接口示例
 *
 * 本类演示 Java 接口的概念和使用
 * 对应 JDK: 接口和默认方法
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class InterfaceDemo {

    /**
     * 演示基本接口
     */
    public void demonstrateBasicInterface() {
        System.out.println("=== 基本接口 ===");

        Dog dog = new Dog("旺财");
        Cat cat = new Cat("咪咪");

        System.out.println("Dog 实现 Animal:");
        dog.eat();
        dog.sleep();

        System.out.println("\nCat 实现 Animal:");
        cat.eat();
        cat.sleep();

        // 使用接口引用
        Animal animal = dog;
        System.out.println("\n使用接口引用:");
        animal.eat();
    }

    /**
     * 演示默认方法
     */
    public void demonstrateDefaultMethod() {
        System.out.println("\n=== 默认方法 ===");

        Vehicle car = new Car();
        Vehicle bike = new Bike();

        System.out.println("Car:");
        car.start();
        car.stop();
        car.honk();  // 默认方法

        System.out.println("\nBike:");
        bike.start();
        bike.stop();
        bike.honk();  // 默认方法
    }

    /**
     * 演示多接口实现
     */
    public void demonstrateMultipleInterfaces() {
        System.out.println("\n=== 多接口实现 ===");

        SmartPhone phone = new SmartPhone();

        System.out.println("作为 Phone:");
        phone.call("123456789");

        System.out.println("\n作为 Camera:");
        phone.takePhoto();

        System.out.println("\n作为 MusicPlayer:");
        phone.play();
    }

    /**
     * 演示静态方法
     */
    public void demonstrateStaticMethod() {
        System.out.println("\n=== 接口静态方法 ===");

        // 直接通过接口调用
        Animal.describe();
        Vehicle.describe();
    }

    /**
     * 动物接口
     */
    interface Animal {
        void eat();
        void sleep();

        // 静态方法
        static void describe() {
            System.out.println("Animal 是动物接口");
        }
    }

    /**
     * 交通工具接口
     */
    interface Vehicle {
        void start();
        void stop();

        // 默认方法
        default void honk() {
            System.out.println("发出声音");
        }

        // 静态方法
        static void describe() {
            System.out.println("Vehicle 是交通工具接口");
        }
    }

    /**
     * 电话接口
     */
    interface Phone {
        void call(String number);
    }

    /**
     * 相机接口
     */
    interface Camera {
        void takePhoto();
    }

    /**
     * 音乐播放器接口
     */
    interface MusicPlayer {
        void play();
        void pause();
    }

    /**
     * 狗类
     */
    static class Dog implements Animal {
        private String name;

        public Dog(String name) {
            this.name = name;
        }

        @Override
        public void eat() {
            System.out.println(name + " 在吃东西");
        }

        @Override
        public void sleep() {
            System.out.println(name + " 在睡觉");
        }
    }

    /**
     * 猫类
     */
    static class Cat implements Animal {
        private String name;

        public Cat(String name) {
            this.name = name;
        }

        @Override
        public void eat() {
            System.out.println(name + " 在吃东西");
        }

        @Override
        public void sleep() {
            System.out.println(name + " 在睡觉");
        }
    }

    /**
     * 汽车类
     */
    static class Car implements Vehicle {
        @Override
        public void start() {
            System.out.println("汽车启动");
        }

        @Override
        public void stop() {
            System.out.println("汽车停止");
        }

        @Override
        public void honk() {
            System.out.println("汽车鸣笛: 嘀嘀!");
        }
    }

    /**
     * 自行车类
     */
    static class Bike implements Vehicle {
        @Override
        public void start() {
            System.out.println("自行车启动");
        }

        @Override
        public void stop() {
            System.out.println("自行车停止");
        }
    }

    /**
     * 智能手机类（实现多个接口）
     */
    static class SmartPhone implements Phone, Camera, MusicPlayer {
        @Override
        public void call(String number) {
            System.out.println("拨打电话: " + number);
        }

        @Override
        public void takePhoto() {
            System.out.println("拍照");
        }

        @Override
        public void play() {
            System.out.println("播放音乐");
        }

        @Override
        public void pause() {
            System.out.println("暂停音乐");
        }
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 接口演示                              ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        InterfaceDemo demo = new InterfaceDemo();
        demo.demonstrateBasicInterface();
        demo.demonstrateDefaultMethod();
        demo.demonstrateMultipleInterfaces();
        demo.demonstrateStaticMethod();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
