package com.linsir.abc.pdai.Base.equalsdemo;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 演示 equals() 和 hashCode() 的区别
 * <p>
 * 1. 默认继承 Object，未重写 equals 和 hashCode，使用 Object 的实现。
 * 2. 只重写 equals，未重写 hashCode，会导致在 HashSet 等集合中无法正常工作。
 * 3. 正确重写 equals 和 hashCode，确保在集合中能正常工作。
 * ### 核心区别解析
运行上述代码会得到以下结论：

1. equals() : 用于判断两个对象 逻辑上是否相等 。
   
   - 默认行为 ：比较内存地址（ == ）。
   - 重写后 ：通常比较对象的内容（如 name 和 age）。
   - 示例结果 ：在情况2和3中，两个不同对象只要内容相同， equals() 都返回 true 。
2. hashCode() : 用于计算对象的 哈希码 ，主要用于哈希表（如 HashMap , HashSet ）中确定存储位置。
   
   - 默认行为 ：基于内存地址计算。
   - 重写后 ：基于对象内容计算。
### 为什么必须同时重写？
这是 Java 的一条重要契约： 如果两个对象 equals() 返回 true，那么它们的 hashCode() 必须相等。

如果不遵守（如代码中的 情况2 ）：

- 现象 ： p1.equals(p2) 是 true ，但 p1 和 p2 的哈希码不同。
- 后果 ：当你把它们放入 HashSet 或 HashMap 时，集合会认为它们是两个完全不同的元素（因为它们落在不同的哈希桶中）。
- Bug ：你会发现 HashSet 中有两个重复的人（Alice, 20），导致去重失败或 get() 方法取不到值。
总结 ：

- 只重写 equals ：对象可以正确比较，但在集合框架（Map/Set）中会出Bug。
- 同时重写 ：保证逻辑相等的对象拥有相同的哈希特征，从而在集合中正确去重和索引。
 * 
 */
public class EqualsHashCodeDemo {

    // 情况1：默认继承 Object，未重写 equals 和 hashCode
    static class PersonDefault {
        String name;
        int age;

        public PersonDefault(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    // 情况2：只重写 equals，未重写 hashCode
    static class PersonEqualsOnly {
        String name;
        int age;

        public PersonEqualsOnly(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PersonEqualsOnly that = (PersonEqualsOnly) o;
            return age == that.age && Objects.equals(name, that.name);
        }
        // 忘记重写 hashCode()
    }

    // 情况3：正确重写 equals 和 hashCode
    static class PersonCorrect {
        String name;
        int age;

        public PersonCorrect(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PersonCorrect that = (PersonCorrect) o;
            return age == that.age && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 演示 equals() 和 hashCode() 的区别 ===");

        // 测试1：默认情况
        System.out.println("\n1. 默认情况 (未重写 equals 和 hashCode):");
        PersonDefault p1 = new PersonDefault("Alice", 20);
        PersonDefault p2 = new PersonDefault("Alice", 20);
        test(p1, p2);

        // 测试2：只重写 equals
        System.out.println("\n2. 只重写 equals (未重写 hashCode):");
        PersonEqualsOnly p3 = new PersonEqualsOnly("Bob", 25);
        PersonEqualsOnly p4 = new PersonEqualsOnly("Bob", 25);
        test(p3, p4);

        // 测试3：正确重写
        System.out.println("\n3. 正确重写 equals 和 hashCode:");
        PersonCorrect p5 = new PersonCorrect("Charlie", 30);
        PersonCorrect p6 = new PersonCorrect("Charlie", 30);
        test(p5, p6);
    }

    private static void test(Object o1, Object o2) {
        System.out.println("   对象1: " + o1.getClass().getSimpleName() + "@" + Integer.toHexString(o1.hashCode()));
        System.out.println("   对象2: " + o2.getClass().getSimpleName() + "@" + Integer.toHexString(o2.hashCode()));
        
        System.out.println("   o1 == o2: " + (o1 == o2));
        System.out.println("   o1.equals(o2): " + o1.equals(o2));

        Set<Object> set = new HashSet<>();
        set.add(o1);
        set.add(o2);
        System.out.println("   放入 HashSet 后的大小: " + set.size());
        
        if (o1.equals(o2) && set.size() == 2) {
            System.out.println("   [警告] equals为true但HashSet大小为2，说明hashCode不同，违反了Java约定！");
        } else if (o1.equals(o2) && set.size() == 1) {
            System.out.println("   [正常] equals为true且HashSet大小为1，符合Java约定。");
        }
    }
}
