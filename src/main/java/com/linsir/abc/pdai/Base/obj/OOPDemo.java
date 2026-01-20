package com.linsir.abc.pdai.Base.obj;

/**
 * 主程序：展示继承、多态、封装、接口、向上/向下转型等 OOP 特性
 */
public class OOPDemo {

    public static void main(String[] args) {
        System.out.println("=== Java 面向对象（OOP）示例 ===");

        // 调用抽象类的静态方法（示例 static）
        Animal.species();

        // 创建一个 Zoo（组合：has-a）
        Zoo zoo = new Zoo();
        zoo.add(new Dog(1, "Buddy"));
        zoo.add(new Cat(2, "Kitty"));
        zoo.add(new Bird(3, "Tweety"));
        zoo.add(new Fish(4, "Nemo"));

        System.out.println("\n-- 通过 Zoo 展示动物（多态：Animal 引用调用子类实现） --");
        zoo.showAll();

        // 向上转型（upcast）和向下转型（downcast）
        Animal a = new Dog(5, "Rex"); // upcast: Dog -> Animal
        a.sound(); // 动态绑定到 Dog.sound()

        // 向下转型前使用 instanceof 检查安全性
        if (a instanceof Dog) {
            Dog d = (Dog) a; // downcast
            d.fetch();       // Dog 特有方法
        }

        // 接口 instanceof 演示
        System.out.println("\n-- 接口能力检查（Flyable / Swimmable） --");
        for (Animal an : zoo.getAnimals()) {
            if (an instanceof Flyable) {
                ((Flyable) an).fly();
            }
            if (an instanceof Swimmable) {
                ((Swimmable) an).swim();
            }
        }

        // 封装示例：访问 BaseEntity 的字段通过 getter/setter
        Animal cat = zoo.getAnimals().stream().filter(x -> x.getName().equals("Kitty")).findFirst().orElse(null);
        if (cat != null) {
            System.out.println("\n封装示例：访问和修改对象的属性（通过 getter/setter）");// Original name: " + cat.getName());
            System.out.println("原始 name: " + cat.getName());
            cat.setName("Kitty-Cat");
            System.out.println("修改后 name: " + cat.getName());
        }

        System.out.println("\n示例结束。");
    }
}