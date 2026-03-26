package com.linsir.abc.core.grammar.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * 通配符示例
 *
 * 本类演示 Java 泛型通配符的使用
 * 对应 JDK: 泛型通配符
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class Wildcards {

    /**
     * 演示无界通配符
     */
    public void demonstrateUnboundedWildcard() {
        System.out.println("=== 无界通配符 ===");

        List<?> list1 = new ArrayList<String>();
        List<?> list2 = new ArrayList<Integer>();

        // 可以读取，但不能写入（除了 null）
        List<String> strList = new ArrayList<>();
        strList.add("Hello");
        strList.add("World");

        List<?> wildcardList = strList;
        System.out.println("读取: " + wildcardList.get(0));

        // wildcardList.add("Test");  // 编译错误！
        wildcardList.add(null);  // 可以添加 null

        System.out.println("列表大小: " + wildcardList.size());
    }

    /**
     * 演示上界通配符
     */
    public void demonstrateUpperBoundedWildcard() {
        System.out.println("\n=== 上界通配符 (<? extends T>) ===");

        List<Integer> intList = new ArrayList<>();
        intList.add(1);
        intList.add(2);
        intList.add(3);

        List<Double> doubleList = new ArrayList<>();
        doubleList.add(1.1);
        doubleList.add(2.2);

        System.out.println("Integer 列表总和: " + sum(intList));
        System.out.println("Double 列表总和: " + sum(doubleList));
    }

    /**
     * 使用上界通配符计算总和
     *
     * @param list 数字列表
     * @return 总和
     */
    public double sum(List<? extends Number> list) {
        double total = 0;
        for (Number n : list) {
            total += n.doubleValue();
        }
        return total;
    }

    /**
     * 演示下界通配符
     */
    public void demonstrateLowerBoundedWildcard() {
        System.out.println("\n=== 下界通配符 (<? super T>) ===");

        List<Number> numList = new ArrayList<>();
        List<Object> objList = new ArrayList<>();

        // 可以添加 Integer 到 Number 或 Object 的列表
        addNumbers(numList);
        addNumbers(objList);

        System.out.println("Number 列表: " + numList);
        System.out.println("Object 列表: " + objList);
    }

    /**
     * 使用下界通配符添加数字
     *
     * @param list 列表
     */
    public void addNumbers(List<? super Integer> list) {
        list.add(1);
        list.add(2);
        list.add(3);
        // 可以安全地添加 Integer
    }

    /**
     * 演示 PECS 原则
     */
    public void demonstratePECS() {
        System.out.println("\n=== PECS 原则 ===");
        System.out.println("Producer Extends, Consumer Super");

        // Producer（生产者）使用 extends
        List<Integer> intList = new ArrayList<>();
        intList.add(1);
        intList.add(2);
        intList.add(3);

        System.out.println("从列表读取（Producer）:");
        printNumbers(intList);

        // Consumer（消费者）使用 super
        System.out.println("\n向列表写入（Consumer）:");
        List<Number> numList = new ArrayList<>();
        copyNumbers(intList, numList);
        System.out.println("复制后的列表: " + numList);
    }

    /**
     * 打印数字（Producer - 使用 extends）
     *
     * @param list 数字列表
     */
    public void printNumbers(List<? extends Number> list) {
        for (Number n : list) {
            System.out.println("  " + n);
        }
    }

    /**
     * 复制数字（Consumer - 使用 super）
     *
     * @param src 源列表
     * @param dest 目标列表
     */
    public void copyNumbers(List<? extends Number> src, List<? super Number> dest) {
        for (Number n : src) {
            dest.add(n);
        }
    }

    /**
     * 演示通配符的限制
     */
    public void demonstrateWildcardLimitations() {
        System.out.println("\n=== 通配符限制 ===");

        List<Integer> intList = new ArrayList<>();
        intList.add(1);
        intList.add(2);

        List<? extends Number> extendsList = intList;
        // extendsList.add(3);  // 编译错误！不能写入

        List<? super Integer> superList = new ArrayList<Number>();
        superList.add(1);
        superList.add(2);
        // Integer i = superList.get(0);  // 编译错误！不能安全读取
        Object obj = superList.get(0);  // 只能读取为 Object

        System.out.println("上界通配符列表: " + extendsList);
        System.out.println("下界通配符列表: " + superList);
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 泛型通配符演示                        ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        Wildcards demo = new Wildcards();
        demo.demonstrateUnboundedWildcard();
        demo.demonstrateUpperBoundedWildcard();
        demo.demonstrateLowerBoundedWildcard();
        demo.demonstratePECS();
        demo.demonstrateWildcardLimitations();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
        System.out.println("\n总结:");
        System.out.println("• <?> - 无界通配符：只读，可写入 null");
        System.out.println("• <? extends T> - 上界：只读（Producer）");
        System.out.println("• <? super T> - 下界：可写（Consumer）");
        System.out.println("• PECS 原则：Producer Extends, Consumer Super");
    }
}
