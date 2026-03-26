package com.linsir.abc.core.grammar.oop;

/**
 * 类与对象示例
 *
 * 本类演示 Java 类和对象的基本概念
 * 对应 JDK: 面向对象基础
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ClassAndObject {

    /**
     * 演示类的定义
     */
    public void demonstrateClassDefinition() {
        System.out.println("=== 类的定义 ===");

        // 使用默认构造方法创建对象
        Student s1 = new Student();
        System.out.println("默认构造: " + s1);

        // 使用带参构造方法
        Student s2 = new Student("张三", 20, "计算机科学");
        System.out.println("带参构造: " + s2);

        // 使用构造方法链
        Student s3 = new Student("李四");
        System.out.println("构造链: " + s3);
    }

    /**
     * 演示封装
     */
    public void demonstrateEncapsulation() {
        System.out.println("\n=== 封装演示 ===");

        Student student = new Student();

        // 使用 setter 方法设置值
        student.setName("王五");
        student.setAge(25);
        student.setMajor("软件工程");

        // 使用 getter 方法获取值
        System.out.println("姓名: " + student.getName());
        System.out.println("年龄: " + student.getAge());
        System.out.println("专业: " + student.getMajor());

        // 测试数据验证
        System.out.println("\n数据验证:");
        student.setAge(-5);  // 无效年龄
        student.setAge(150); // 无效年龄
    }

    /**
     * 演示 this 关键字
     */
    public void demonstrateThisKeyword() {
        System.out.println("\n=== this 关键字 ===");

        Student s1 = new Student("张三", 20, "计算机");
        Student s2 = new Student("李四", 22, "软件工程");

        System.out.println("s1 信息: " + s1);
        System.out.println("s2 信息: " + s2);

        // 链式调用
        System.out.println("\n链式调用:");
        Student s3 = new Student();
        s3.setName("王五").setAge(25).setMajor("网络工程");
        System.out.println("s3 信息: " + s3);
    }

    /**
     * 演示静态成员
     */
    public void demonstrateStaticMembers() {
        System.out.println("\n=== 静态成员 ===");

        // 访问静态变量
        System.out.println("学校名称: " + Student.getSchoolName());

        // 修改静态变量
        Student.setSchoolName("科技大学");
        System.out.println("修改后: " + Student.getSchoolName());

        // 静态方法
        System.out.println("\n静态方法:");
        System.out.println("学生总数: " + Student.getStudentCount());

        // 创建学生
        new Student("A", 20, "专业A");
        new Student("B", 21, "专业B");
        System.out.println("创建2个学生后总数: " + Student.getStudentCount());
    }

    /**
     * 演示代码块
     */
    public void demonstrateCodeBlocks() {
        System.out.println("\n=== 代码块 ===");

        System.out.println("创建第一个对象:");
        new DemoClass();

        System.out.println("\n创建第二个对象:");
        new DemoClass();
    }

    /**
     * 学生类
     */
    static class Student {
        // 静态变量
        private static String schoolName = "理工大学";
        private static int studentCount = 0;

        // 实例变量
        private String name;
        private int age;
        private String major;

        // 构造方法
        public Student() {
            this("未知", 18, "未分配");
        }

        public Student(String name) {
            this(name, 18, "未分配");
        }

        public Student(String name, int age, String major) {
            this.name = name;
            this.age = age;
            this.major = major;
            studentCount++;
        }

        // Getter 和 Setter
        public String getName() {
            return name;
        }

        public Student setName(String name) {
            this.name = name;
            return this;
        }

        public int getAge() {
            return age;
        }

        public Student setAge(int age) {
            if (age < 0 || age > 120) {
                System.out.println("  错误: 年龄 " + age + " 无效");
                return this;
            }
            this.age = age;
            return this;
        }

        public String getMajor() {
            return major;
        }

        public Student setMajor(String major) {
            this.major = major;
            return this;
        }

        // 静态方法
        public static String getSchoolName() {
            return schoolName;
        }

        public static void setSchoolName(String name) {
            schoolName = name;
        }

        public static int getStudentCount() {
            return studentCount;
        }

        @Override
        public String toString() {
            return "Student{name='" + name + "', age=" + age + ", major='" + major + "'}";
        }
    }

    /**
     * 演示代码块用类
     */
    static class DemoClass {
        // 静态代码块
        static {
            System.out.println("  静态代码块执行（只执行一次）");
        }

        // 实例代码块
        {
            System.out.println("  实例代码块执行（每次创建对象都执行）");
        }

        // 构造方法
        public DemoClass() {
            System.out.println("  构造方法执行");
        }
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 类与对象演示                          ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        ClassAndObject demo = new ClassAndObject();
        demo.demonstrateClassDefinition();
        demo.demonstrateEncapsulation();
        demo.demonstrateThisKeyword();
        demo.demonstrateStaticMembers();
        demo.demonstrateCodeBlocks();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
