package com.linsir.abc.core.grammar.datatype;

/**
 * 引用类型示例
 * 
 * 本类演示 Java 引用类型的内存特性、null 引用和字符串常量池
 * 对应 JDK: java.lang.Object 及其子类
 * 
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ReferenceTypes {
    
    /**
     * 演示引用类型的内存特性
     * 展示引用赋值、对象共享和相等性判断
     */
    public void demonstrateReferenceBehavior() {
        System.out.println("=== 引用类型内存特性 ===");
        
        // 创建 Person 对象，person1 保存对象的引用（内存地址）
        Person person1 = new Person("张三", 25);
        
        // person2 指向同一个对象，现在有两个引用指向堆中的同一对象
        Person person2 = person1;
        
        System.out.println("person1: " + person1);
        System.out.println("person2: " + person2);
        
        // == 比较的是引用（内存地址），不是对象内容
        System.out.println("person1 == person2 ? " + (person1 == person2));
        
        // 通过 person2 修改对象状态
        person2.setAge(30);
        
        // 由于 person1 和 person2 指向同一对象，person1 也能看到修改
        System.out.println("\n通过 person2 修改 age 为 30 后:");
        System.out.println("person1.getAge() = " + person1.getAge());
        System.out.println("person2.getAge() = " + person2.getAge());
        
        // 创建具有相同内容的新对象
        Person person3 = new Person("张三", 30);
        
        System.out.println("\n创建 person3 (内容相同的新对象):");
        System.out.println("person3: " + person3);
        
        // equals 比较对象内容（需要正确重写）
        System.out.println("person1.equals(person3) ? " + person1.equals(person3));
        
        // == 比较引用，person1 和 person3 是不同对象
        System.out.println("person1 == person3 ? " + (person1 == person3));
    }
    
    /**
     * 演示 null 引用
     * null 表示引用不指向任何对象
     */
    public void demonstrateNullReference() {
        System.out.println("\n=== Null 引用 ===");
        
        // 声明一个 null 引用
        Person person = null;
        System.out.println("person = " + person);
        System.out.println("person 是否为 null ? " + (person == null));
        
        // 在访问 null 引用的方法或属性时会抛出 NullPointerException
        // 以下代码会抛出异常，因此注释掉
        // String name = person.getName();  // NullPointerException!
        
        System.out.println("注意: 访问 null 引用的成员会导致 NullPointerException");
        
        // 安全使用引用的方式
        if (person != null) {
            System.out.println(person.getName());
        } else {
            System.out.println("person 为 null，安全跳过访问");
        }
    }
    
    /**
     * 演示字符串常量池
     * 字符串是不可变对象，Java 使用常量池优化内存
     */
    public void demonstrateStringPool() {
        System.out.println("\n=== 字符串常量池 ===");
        
        // 使用字符串字面量创建，存储在常量池中
        String s1 = "Hello";
        String s2 = "Hello";  // 复用常量池中的同一对象
        
        // 使用 new 创建，在堆内存中创建新对象
        String s3 = new String("Hello");
        
        System.out.println("s1 = \"Hello\"");
        System.out.println("s2 = \"Hello\"");
        System.out.println("s3 = new String(\"Hello\")");
        
        // == 比较引用地址
        System.out.println("\n使用 == 比较引用:");
        System.out.println("s1 == s2 ? " + (s1 == s2) + " (都指向常量池同一对象)");
        System.out.println("s1 == s3 ? " + (s1 == s3) + " (s3 是堆中新对象)");
        
        // equals 比较内容
        System.out.println("\n使用 equals 比较内容:");
        System.out.println("s1.equals(s2) ? " + s1.equals(s2));
        System.out.println("s1.equals(s3) ? " + s1.equals(s3));
        
        // intern() 方法：将字符串放入常量池并返回常量池引用
        String s4 = s3.intern();
        System.out.println("\n使用 intern() 方法:");
        System.out.println("s4 = s3.intern()");
        System.out.println("s1 == s4 ? " + (s1 == s4) + " (intern 返回常量池引用)");
    }
    
    /**
     * 主方法，运行所有演示
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        ReferenceTypes demo = new ReferenceTypes();
        
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 引用类型演示                          ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");
        
        demo.demonstrateReferenceBehavior();
        demo.demonstrateNullReference();
        demo.demonstrateStringPool();
        
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
    
    /**
     * 内部类：Person
     * 用于演示引用类型的行为
     */
    static class Person {
        /** 姓名 */
        private String name;
        /** 年龄 */
        private int age;
        
        /**
         * 构造方法
         * 
         * @param name 姓名
         * @param age 年龄
         */
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        /**
         * 获取姓名
         * @return 姓名
         */
        public String getName() {
            return name;
        }
        
        /**
         * 设置姓名
         * @param name 姓名
         */
        public void setName(String name) {
            this.name = name;
        }
        
        /**
         * 获取年龄
         * @return 年龄
         */
        public int getAge() {
            return age;
        }
        
        /**
         * 设置年龄
         * @param age 年龄
         */
        public void setAge(int age) {
            this.age = age;
        }
        
        /**
         * 重写 toString 方法
         * @return 对象的字符串表示
         */
        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
        
        /**
         * 重写 equals 方法，基于内容比较
         * @param obj 要比较的对象
         * @return 如果内容相等返回 true
         */
        @Override
        public boolean equals(Object obj) {
            // 同一引用，必然相等
            if (this == obj) return true;
            
            // null 或不同类型，不相等
            if (obj == null || getClass() != obj.getClass()) return false;
            
            // 类型转换并比较字段
            Person person = (Person) obj;
            return age == person.age && 
                   (name != null ? name.equals(person.name) : person.name == null);
        }
        
        /**
         * 重写 hashCode 方法，与 equals 保持一致
         * @return 哈希码
         */
        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + age;
            return result;
        }
    }
}
