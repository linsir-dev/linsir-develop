package com.linsir.abc.pdai.Base.obj;

import java.util.ArrayList;
import java.util.List;

/**
 * Zoo：组合示例（has-a），包含多个 Animal
 */
public class Zoo {
    private final List<Animal> animals = new ArrayList<>();

    public void add(Animal a) {
        animals.add(a);
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public void showAll() {
        for (Animal a : animals) {
            System.out.println("\n-- " + a.getClass().getSimpleName() + " --");
            a.info();      // final 方法，展示 toString
            a.sound();     // 多��：调用实际对象的实现
            a.move();      // 可能被覆写
            a.eat("treat");// 方法重载示例
        }
    }
}