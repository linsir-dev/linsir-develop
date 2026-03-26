package com.linsir.abc.core.grammar.method;

/**
 * 参数传递示例
 *
 * 本类演示 Java 中的值传递机制
 * 对应 JDK: 参数传递机制
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ParameterPassing {

    /**
     * 演示基本类型的值传递
     * 基本类型传递的是值的副本，方法内修改不影响原变量
     *
     * @param num 整数参数
     */
    public void modifyPrimitive(int num) {
        System.out.println("  方法内修改前: num = " + num);
        num = 100;
        System.out.println("  方法内修改后: num = " + num);
    }

    /**
     * 演示引用类型的值传递
     * 引用类型传递的是引用的副本，方法内修改对象内容会影响原对象
     *
     * @param arr 数组参数
     */
    public void modifyArray(int[] arr) {
        System.out.println("  方法内修改前: arr[0] = " + arr[0]);
        arr[0] = 999;
        System.out.println("  方法内修改后: arr[0] = " + arr[0]);
    }

    /**
     * 演示引用重新赋值
     * 方法内重新赋值引用不会影响原引用
     *
     * @param arr 数组参数
     */
    public void reassignArray(int[] arr) {
        System.out.println("  方法内重新赋值前: arr = " + java.util.Arrays.toString(arr));
        arr = new int[]{100, 200, 300};
        System.out.println("  方法内重新赋值后: arr = " + java.util.Arrays.toString(arr));
    }

    /**
     * 演示对象的修改
     *
     * @param person Person对象
     */
    public void modifyObject(Person person) {
        System.out.println("  方法内修改前: " + person);
        person.setName("李四");
        person.setAge(30);
        System.out.println("  方法内修改后: " + person);
    }

    /**
     * 演示对象引用的重新赋值
     *
     * @param person Person对象
     */
    public void reassignObject(Person person) {
        System.out.println("  方法内重新赋值前: " + person);
        person = new Person("王五", 40);
        System.out.println("  方法内重新赋值后: " + person);
    }

    /**
     * 演示 String 的不可变性
     *
     * @param str 字符串参数
     */
    public void modifyString(String str) {
        System.out.println("  方法内修改前: str = " + str);
        str = str + " World";
        System.out.println("  方法内修改后: str = " + str);
    }

    /**
     * 演示包装类的值传递
     *
     * @param num Integer对象
     */
    public void modifyWrapper(Integer num) {
        System.out.println("  方法内修改前: num = " + num);
        num = 999;  // 创建新的Integer对象
        System.out.println("  方法内修改后: num = " + num);
    }

    /**
     * 演示值传递
     */
    public void demonstrateValuePassing() {
        System.out.println("=== 值传递演示 ===");

        // 基本类型
        System.out.println("\n1. 基本类型:");
        int num = 10;
        System.out.println("调用前: num = " + num);
        modifyPrimitive(num);
        System.out.println("调用后: num = " + num + " (未改变)");

        // 数组类型
        System.out.println("\n2. 数组类型（修改内容）:");
        int[] arr = {1, 2, 3};
        System.out.println("调用前: arr = " + java.util.Arrays.toString(arr));
        modifyArray(arr);
        System.out.println("调用后: arr = " + java.util.Arrays.toString(arr) + " (内容已改变)");

        // 数组重新赋值
        System.out.println("\n3. 数组类型（重新赋值引用）:");
        int[] arr2 = {1, 2, 3};
        System.out.println("调用前: arr2 = " + java.util.Arrays.toString(arr2));
        reassignArray(arr2);
        System.out.println("调用后: arr2 = " + java.util.Arrays.toString(arr2) + " (未改变)");

        // 对象类型
        System.out.println("\n4. 对象类型（修改内容）:");
        Person person = new Person("张三", 25);
        System.out.println("调用前: " + person);
        modifyObject(person);
        System.out.println("调用后: " + person + " (内容已改变)");

        // 对象重新赋值
        System.out.println("\n5. 对象类型（重新赋值引用）:");
        Person person2 = new Person("张三", 25);
        System.out.println("调用前: " + person2);
        reassignObject(person2);
        System.out.println("调用后: " + person2 + " (未改变)");

        // String类型
        System.out.println("\n6. String类型:");
        String str = "Hello";
        System.out.println("调用前: str = " + str);
        modifyString(str);
        System.out.println("调用后: str = " + str + " (未改变，因为String不可变)");

        // 包装类
        System.out.println("\n7. 包装类:");
        Integer wrapper = 10;
        System.out.println("调用前: num = " + wrapper);
        modifyWrapper(wrapper);
        System.out.println("调用后: num = " + wrapper + " (未改变)");
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 参数传递演示                          ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        ParameterPassing demo = new ParameterPassing();
        demo.demonstrateValuePassing();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("总结:");
        System.out.println("• Java 只有值传递，没有引用传递");
        System.out.println("• 基本类型：传递值的副本");
        System.out.println("• 引用类型：传递引用的副本（地址的副本）");
        System.out.println("• 修改对象内容会影响原对象");
        System.out.println("• 重新赋值引用不会影响原引用");
    }

    /**
     * Person类
     */
    static class Person {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }
}
