package com.linsir.spring.framework.spring_core.bytecode.cglib.reflect;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * FastClass测试类
 *
 * <p>测试CGLIB FastClass的核心功能，包括：
 * <ul>
 *   <li>方法索引获取</li>
 *   <li>方法调用</li>
 *   <li>性能优化</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 */
public class FastClassTest {

    /**
     * 测试目标类
     */
    public static class SampleService {
        private String name;

        public SampleService() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String greet(String greeting) {
            return greeting + ", " + name;
        }

        public int calculate(int a, int b) {
            return a + b;
        }

        public void voidMethod() {
            // 无返回值方法
        }

        private String privateMethod() {
            return "private";
        }

        public static String staticMethod() {
            return "static";
        }
    }

    /**
     * 测试创建FastClass
     */
    @Test
    public void testCreateFastClass() {
        FastClass fastClass = FastClass.create(SampleService.class);

        assertNotNull(fastClass);
        assertEquals(SampleService.class, fastClass.getTargetClass());
    }

    /**
     * 测试获取方法索引
     */
    @Test
    public void testGetMethodIndex() {
        FastClass fastClass = FastClass.create(SampleService.class);

        // 获取getName方法的索引
        int getNameIndex = fastClass.getIndex("getName", new Class<?>[0]);
        assertTrue(getNameIndex >= 0);

        // 获取setName方法的索引
        int setNameIndex = fastClass.getIndex("setName", new Class<?>[]{String.class});
        assertTrue(setNameIndex >= 0);

        // 获取不存在的方法索引
        int notFoundIndex = fastClass.getIndex("notExistMethod", new Class<?>[0]);
        assertEquals(-1, notFoundIndex);
    }

    /**
     * 测试调用无参方法
     */
    @Test
    public void testInvokeNoArgMethod() throws InvocationTargetException {
        FastClass fastClass = FastClass.create(SampleService.class);

        SampleService service = new SampleService();
        service.setName("张三");

        int index = fastClass.getIndex("getName", new Class<?>[0]);
        Object result = fastClass.invoke(index, service, null);

        assertEquals("张三", result);
    }

    /**
     * 测试调用有参方法
     */
    @Test
    public void testInvokeWithArgsMethod() throws InvocationTargetException {
        FastClass fastClass = FastClass.create(SampleService.class);

        SampleService service = new SampleService();

        // 调用setName
        int setNameIndex = fastClass.getIndex("setName", new Class<?>[]{String.class});
        fastClass.invoke(setNameIndex, service, new Object[]{"李四"});

        // 调用getName
        int getNameIndex = fastClass.getIndex("getName", new Class<?>[0]);
        Object result = fastClass.invoke(getNameIndex, service, null);

        assertEquals("李四", result);
    }

    /**
     * 测试调用多参数方法
     */
    @Test
    public void testInvokeMultiArgMethod() throws InvocationTargetException {
        FastClass fastClass = FastClass.create(SampleService.class);

        SampleService service = new SampleService();

        int index = fastClass.getIndex("calculate", new Class<?>[]{int.class, int.class});
        Object result = fastClass.invoke(index, service, new Object[]{10, 20});

        assertEquals(30, result);
    }

    /**
     * 测试调用void方法
     */
    @Test
    public void testInvokeVoidMethod() throws InvocationTargetException {
        FastClass fastClass = FastClass.create(SampleService.class);

        SampleService service = new SampleService();

        int index = fastClass.getIndex("voidMethod", new Class<?>[0]);
        Object result = fastClass.invoke(index, service, null);

        assertNull(result);
    }

    /**
     * 测试获取方法数量
     */
    @Test
    public void testGetMethodCount() {
        FastClass fastClass = FastClass.create(SampleService.class);

        int count = fastClass.getMethodCount();
        assertTrue(count > 0);
    }

    /**
     * 测试通过索引获取方法
     */
    @Test
    public void testGetMethodByIndex() {
        FastClass fastClass = FastClass.create(SampleService.class);

        int index = fastClass.getIndex("getName", new Class<?>[0]);
        Method method = fastClass.getMethod(index);

        assertNotNull(method);
        assertEquals("getName", method.getName());
    }

    /**
     * 测试调用不存在的方法索引
     */
    @Test
    public void testInvokeInvalidIndex() {
        FastClass fastClass = FastClass.create(SampleService.class);
        SampleService service = new SampleService();

        assertThrows(IllegalArgumentException.class, () -> {
            fastClass.invoke(-1, service, null);
        });
    }

    /**
     * 测试FastMethod包装器
     */
    @Test
    public void testFastMethod() throws InvocationTargetException {
        FastClass fastClass = FastClass.create(SampleService.class);

        // 创建FastMethod
        int index = fastClass.getIndex("greet", new Class<?>[]{String.class});
        Method method = fastClass.getMethod(index);
        FastMethod fastMethod = new FastMethod(fastClass, method, index);

        SampleService service = new SampleService();
        service.setName("王五");

        // 使用FastMethod调用
        Object result = fastMethod.invoke(service, new Object[]{"Hello"});

        assertEquals("Hello, 王五", result);
        assertEquals("greet", fastMethod.getName());
        assertEquals(String.class, fastMethod.getReturnType());
    }

    /**
     * 测试性能对比（简化版）
     */
    @Test
    public void testPerformanceComparison() throws Exception {
        FastClass fastClass = FastClass.create(SampleService.class);
        SampleService service = new SampleService();

        int index = fastClass.getIndex("calculate", new Class<?>[]{int.class, int.class});
        Method method = SampleService.class.getMethod("calculate", int.class, int.class);

        int iterations = 1000;

        // 测试反射调用
        long reflectStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            method.invoke(service, 1, 2);
        }
        long reflectTime = System.nanoTime() - reflectStart;

        // 测试FastClass调用
        long fastStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            fastClass.invoke(index, service, new Object[]{1, 2});
        }
        long fastTime = System.nanoTime() - fastStart;

        // FastClass应该不比反射慢太多（由于简化实现，可能性能相近）
        // 在实际CGLIB实现中，FastClass会明显快于反射
        System.out.println("反射调用时间: " + reflectTime / 1000000 + "ms");
        System.out.println("FastClass调用时间: " + fastTime / 1000000 + "ms");
    }
}
