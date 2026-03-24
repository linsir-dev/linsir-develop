package com.linsir.spring.framework.spring_core.bytecode.objenesis;

import com.linsir.spring.framework.spring_core.bytecode.objenesis.instantiator.ObjectInstantiator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Objenesis测试类
 *
 * <p>测试Objenesis对象实例化功能，包括：
 * <ul>
 *   <li>基本对象实例化</li>
 *   <li>实例化器缓存</li>
 *   <li>多种实例化策略</li>
 *   <li>异常处理</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 */
public class ObjenesisTest {

    /**
     * 测试目标类 - 有默认构造函数
     */
    public static class Person {
        private String name;
        private int age;

        public Person() {
            this.name = "default";
            this.age = 0;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    /**
     * 测试目标类 - 无默认构造函数
     */
    public static class Employee {
        private final String name;
        private final int id;

        public Employee(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }
    }

    /**
     * 测试目标类 - 私有构造函数
     */
    public static class Singleton {
        private static final Singleton INSTANCE = new Singleton();
        private String value;

        private Singleton() {
            this.value = "singleton";
        }

        public static Singleton getInstance() {
            return INSTANCE;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * 测试基本实例化
     */
    @Test
    public void testBasicInstantiation() {
        Objenesis objenesis = new ObjenesisStd();

        Person person = objenesis.newInstance(Person.class);

        assertNotNull(person);
        assertNull(person.getName()); // 构造函数未执行，字段为默认值
        assertEquals(0, person.getAge());
    }

    /**
     * 测试实例化无默认构造函数的类
     */
    @Test
    public void testInstantiationWithoutDefaultConstructor() {
        Objenesis objenesis = new ObjenesisStd();

        Employee employee = objenesis.newInstance(Employee.class);

        assertNotNull(employee);
        assertNull(employee.getName()); // 构造函数未执行，字段为默认值
        assertEquals(0, employee.getId());
    }

    /**
     * 测试实例化私有构造函数的类
     */
    @Test
    public void testInstantiationWithPrivateConstructor() {
        Objenesis objenesis = new ObjenesisStd();

        Singleton singleton = objenesis.newInstance(Singleton.class);

        assertNotNull(singleton);
        assertNull(singleton.getValue()); // 构造函数未执行，字段为默认值
    }

    /**
     * 测试获取实例化器
     */
    @Test
    public void testGetInstantiator() {
        Objenesis objenesis = new ObjenesisStd();

        ObjectInstantiator<Person> instantiator = objenesis.getInstantiatorOf(Person.class);

        assertNotNull(instantiator);

        // 使用实例化器创建多个实例
        Person person1 = instantiator.newInstance();
        Person person2 = instantiator.newInstance();

        assertNotNull(person1);
        assertNotNull(person2);
        assertNotSame(person1, person2); // 是不同的实例
    }

    /**
     * 测试实例化器缓存
     */
    @Test
    public void testInstantiatorCaching() {
        ObjenesisStd objenesis = new ObjenesisStd(true);

        // 获取两次实例化器
        ObjectInstantiator<Person> instantiator1 = objenesis.getInstantiatorOf(Person.class);
        ObjectInstantiator<Person> instantiator2 = objenesis.getInstantiatorOf(Person.class);

        // 应该返回同一个实例化器（缓存）
        assertSame(instantiator1, instantiator2);

        // 缓存大小应该是1
        assertEquals(1, objenesis.getCacheSize());
    }

    /**
     * 测试禁用缓存
     */
    @Test
    public void testDisableCaching() {
        ObjenesisStd objenesis = new ObjenesisStd(false);

        // 获取两次实例化器
        ObjectInstantiator<Person> instantiator1 = objenesis.getInstantiatorOf(Person.class);
        ObjectInstantiator<Person> instantiator2 = objenesis.getInstantiatorOf(Person.class);

        // 应该返回不同的实例化器（未缓存）
        assertNotSame(instantiator1, instantiator2);

        // 缓存大小应该是0
        assertEquals(0, objenesis.getCacheSize());
    }

    /**
     * 测试清除缓存
     */
    @Test
    public void testClearCache() {
        ObjenesisStd objenesis = new ObjenesisStd(true);

        // 获取实例化器
        objenesis.getInstantiatorOf(Person.class);
        objenesis.getInstantiatorOf(Employee.class);

        assertEquals(2, objenesis.getCacheSize());

        // 清除缓存
        objenesis.clearCache();

        assertEquals(0, objenesis.getCacheSize());
    }

    /**
     * 测试实例化null类
     */
    @Test
    public void testInstantiateNullClass() {
        Objenesis objenesis = new ObjenesisStd();

        assertThrows(IllegalArgumentException.class, () -> {
            objenesis.newInstance(null);
        });
    }

    /**
     * 测试实例化基本类型
     */
    @Test
    public void testInstantiatePrimitiveType() {
        Objenesis objenesis = new ObjenesisStd();

        assertThrows(IllegalArgumentException.class, () -> {
            objenesis.newInstance(int.class);
        });
    }

    /**
     * 测试实例化接口
     */
    @Test
    public void testInstantiateInterface() {
        Objenesis objenesis = new ObjenesisStd();

        assertThrows(IllegalArgumentException.class, () -> {
            objenesis.newInstance(Runnable.class);
        });
    }

    /**
     * 测试实例化数组
     */
    @Test
    public void testInstantiateArray() {
        Objenesis objenesis = new ObjenesisStd();

        assertThrows(IllegalArgumentException.class, () -> {
            objenesis.newInstance(int[].class);
        });
    }

    /**
     * 测试多次实例化
     */
    @Test
    public void testMultipleInstantiations() {
        Objenesis objenesis = new ObjenesisStd();

        // 创建多个实例
        Person person1 = objenesis.newInstance(Person.class);
        Person person2 = objenesis.newInstance(Person.class);
        Person person3 = objenesis.newInstance(Person.class);

        // 验证都是不同的实例
        assertNotSame(person1, person2);
        assertNotSame(person2, person3);
        assertNotSame(person1, person3);

        // 验证都可以正常使用
        person1.setName("张三");
        person2.setName("李四");
        person3.setName("王五");

        assertEquals("张三", person1.getName());
        assertEquals("李四", person2.getName());
        assertEquals("王五", person3.getName());
    }

    /**
     * 测试实例化后设置值
     */
    @Test
    public void testSetValuesAfterInstantiation() {
        Objenesis objenesis = new ObjenesisStd();

        Person person = objenesis.newInstance(Person.class);

        // 初始值为默认值
        assertNull(person.getName());
        assertEquals(0, person.getAge());

        // 设置值
        person.setName("测试");
        person.setAge(25);

        // 验证值已设置
        assertEquals("测试", person.getName());
        assertEquals(25, person.getAge());
    }
}
