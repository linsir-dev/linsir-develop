package com.linsir.abc.pdai.base.generic;

/**
 * 泛型测试类
 * 演示Java泛型的使用
 */
public class GenericTest {
    public static void main(String[] args) {
        System.out.println("=== 泛型类示例 ===");
        // 创建泛型类实例，指定类型为Integer
        GenericClass<Integer> integerGeneric = new GenericClass<>(100);
        integerGeneric.showType();
        System.out.println("值: " + integerGeneric.getValue());

        // 创建泛型类实例，指定类型为String
        GenericClass<String> stringGeneric = new GenericClass<>("Hello Generic");
        stringGeneric.showType();
        System.out.println("值: " + stringGeneric.getValue());

        System.out.println("\n=== 泛型接口示例 ===");
        // 创建泛型接口实现类实例
        GenericInterfaceImpl stringImpl = new GenericInterfaceImpl();
        stringImpl.setValue("泛型接口实现");
        System.out.println("值: " + stringImpl.getValue());

        System.out.println("\n=== 泛型方法示例 ===");
        // 测试泛型方法1：获取最大值
        Integer[] intArray = {1, 3, 5, 2, 4};
        Integer maxInt = GenericMethod.getMax(intArray);
        System.out.println("整数数组最大值: " + maxInt);

        String[] stringArray = {"apple", "banana", "orange", "pear"};
        String maxString = GenericMethod.getMax(stringArray);
        System.out.println("字符串数组最大值: " + maxString);

        // 测试泛型方法2：打印值和类型
        System.out.println("\n测试printValue方法:");
        GenericMethod.printValue(100);
        GenericMethod.printValue("Hello");
        GenericMethod.printValue(3.14);
    }
}
