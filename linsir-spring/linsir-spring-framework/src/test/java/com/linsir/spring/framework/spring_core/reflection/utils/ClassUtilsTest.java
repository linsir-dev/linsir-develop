package com.linsir.spring.framework.spring_core.reflection.utils;

import com.linsir.spring.framework.spring_core.reflection.model.Order;
import com.linsir.spring.framework.spring_core.reflection.model.Product;
import com.linsir.spring.framework.spring_core.reflection.model.User;
import com.linsir.spring.framework.spring_core.reflection.service.BaseService;
import com.linsir.spring.framework.spring_core.reflection.service.OrderService;
import com.linsir.spring.framework.spring_core.reflection.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassUtils 测试类
 * 测试类工具的各种功能
 */
@DisplayName("ClassUtils 类工具测试")
class ClassUtilsTest {

    // ==================== 类加载测试 ====================

    @Test
    @DisplayName("测试获取默认类加载器")
    void testGetDefaultClassLoader() {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        assertNotNull(classLoader, "应该返回类加载器");
    }

    @Test
    @DisplayName("测试加载类")
    void testForName() throws ClassNotFoundException {
        Class<?> clazz = ClassUtils.forName("java.lang.String");
        assertEquals(String.class, clazz, "应该加载 String 类");
    }

    @Test
    @DisplayName("测试加载类 - 使用指定类加载器")
    void testForNameWithClassLoader() throws ClassNotFoundException {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        Class<?> clazz = ClassUtils.forName("java.util.ArrayList", classLoader);
        assertEquals(java.util.ArrayList.class, clazz, "应该使用指定类加载器加载类");
    }

    @Test
    @DisplayName("测试加载类 - 类不存在")
    void testForName_ClassNotFound() {
        assertThrows(ClassNotFoundException.class, () -> {
            ClassUtils.forName("com.nonexistent.Class");
        }, "不存在的类应该抛出 ClassNotFoundException");
    }

    // ==================== 类名处理测试 ====================

    @Test
    @DisplayName("测试获取短类名")
    void testGetShortName() {
        String shortName = ClassUtils.getShortName("com.example.UserService");
        assertEquals("UserService", shortName, "应该返回短类名");
    }

    @Test
    @DisplayName("测试获取短类名 - 内部类")
    void testGetShortName_InnerClass() {
        String shortName = ClassUtils.getShortName("com.example.UserService$InnerClass");
        assertEquals("UserService.InnerClass", shortName, "内部类名中的 $ 应该替换为 .");
    }

    @Test
    @DisplayName("测试获取短类名 - 从类对象")
    void testGetShortNameFromClass() {
        String shortName = ClassUtils.getShortName(UserService.class);
        assertEquals("UserService", shortName, "应该返回短类名");
    }

    @Test
    @DisplayName("测试获取类文件名称")
    void testGetClassFileName() {
        String fileName = ClassUtils.getClassFileName(UserService.class);
        assertEquals("UserService.class", fileName, "应该返回类文件名称");
    }

    @Test
    @DisplayName("测试包名转资源路径")
    void testClassPackageAsResourcePath() {
        String path = ClassUtils.classPackageAsResourcePath(UserService.class);
        assertEquals("com/linsir/spring/framework/spring_core/reflection/service", path, "应该返回资源路径");
    }

    @Test
    @DisplayName("测试添加资源路径到包路径")
    void testAddResourcePathToPackagePath() {
        String fullPath = ClassUtils.addResourcePathToPackagePath(UserService.class, "config.xml");
        assertTrue(fullPath.endsWith("/config.xml"), "应该添加资源路径");
        assertTrue(fullPath.contains("com/linsir/spring/framework/spring_core/reflection/service"), "应该包含包路径");
    }

    @Test
    @DisplayName("测试获取合格类名 - 普通类")
    void testGetQualifiedName_Normal() {
        String name = ClassUtils.getQualifiedName(UserService.class);
        assertEquals(UserService.class.getName(), name, "应该返回类全名");
    }

    @Test
    @DisplayName("测试获取合格类名 - 数组类型")
    void testGetQualifiedName_Array() {
        String name = ClassUtils.getQualifiedName(User[].class);
        assertEquals("com.linsir.spring.framework.spring_core.reflection.model.User[]", name, "应该返回数组类型名");
    }

    @Test
    @DisplayName("测试获取合格类名 - 原始类型数组")
    void testGetQualifiedName_PrimitiveArray() {
        String name = ClassUtils.getQualifiedName(int[].class);
        assertEquals("int[]", name, "应该返回原始类型数组名");
    }

    // ==================== 类型判断测试 ====================

    @Test
    @DisplayName("测试判断原始类型")
    void testIsPrimitive() {
        assertTrue(ClassUtils.isPrimitive(int.class), "int 是原始类型");
        assertTrue(ClassUtils.isPrimitive(boolean.class), "boolean 是原始类型");
        assertFalse(ClassUtils.isPrimitive(Integer.class), "Integer 不是原始类型");
        assertFalse(ClassUtils.isPrimitive(String.class), "String 不是原始类型");
    }

    @Test
    @DisplayName("测试判断原始类型包装类")
    void testIsPrimitiveWrapper() {
        assertTrue(ClassUtils.isPrimitiveWrapper(Integer.class), "Integer 是包装类");
        assertTrue(ClassUtils.isPrimitiveWrapper(Boolean.class), "Boolean 是包装类");
        assertFalse(ClassUtils.isPrimitiveWrapper(int.class), "int 不是包装类");
        assertFalse(ClassUtils.isPrimitiveWrapper(String.class), "String 不是包装类");
    }

    @Test
    @DisplayName("测试判断原始类型或包装类")
    void testIsPrimitiveOrWrapper() {
        assertTrue(ClassUtils.isPrimitiveOrWrapper(int.class), "int 是原始类型");
        assertTrue(ClassUtils.isPrimitiveOrWrapper(Integer.class), "Integer 是包装类");
        assertFalse(ClassUtils.isPrimitiveOrWrapper(String.class), "String 既不是原始类型也不是包装类");
    }

    @Test
    @DisplayName("测试判断数组类型")
    void testIsArray() {
        assertTrue(ClassUtils.isArray(User[].class), "User[] 是数组");
        assertTrue(ClassUtils.isArray(int[].class), "int[] 是数组");
        assertFalse(ClassUtils.isArray(User.class), "User 不是数组");
    }

    @Test
    @DisplayName("测试判断原始类型数组")
    void testIsPrimitiveArray() {
        assertTrue(ClassUtils.isPrimitiveArray(int[].class), "int[] 是原始类型数组");
        assertTrue(ClassUtils.isPrimitiveArray(boolean[].class), "boolean[] 是原始类型数组");
        assertFalse(ClassUtils.isPrimitiveArray(User[].class), "User[] 不是原始类型数组");
        assertFalse(ClassUtils.isPrimitiveArray(Integer[].class), "Integer[] 不是原始类型数组");
    }

    @Test
    @DisplayName("测试判断内部类")
    void testIsInnerClass() {
        // Order.OrderItem 是静态内部类
        assertFalse(ClassUtils.isInnerClass(Order.OrderItem.class), "静态内部类不是内部类");
        assertTrue(ClassUtils.isStaticInnerClass(Order.OrderItem.class), "OrderItem 是静态内部类");
    }

    @Test
    @DisplayName("测试判断 CGLIB 代理类")
    void testIsCglibProxyClass() {
        assertFalse(ClassUtils.isCglibProxyClass(UserService.class), "普通类不是 CGLIB 代理");
    }

    @Test
    @DisplayName("测试判断 JDK 动态代理")
    void testIsJdkDynamicProxy() {
        assertFalse(ClassUtils.isJdkDynamicProxy(UserService.class), "普通类不是 JDK 动态代理");
    }

    // ==================== 类型转换测试 ====================

    @Test
    @DisplayName("测试解析原始类型为包装类型")
    void testResolvePrimitiveIfNecessary() {
        assertEquals(Integer.class, ClassUtils.resolvePrimitiveIfNecessary(int.class), "int 应该解析为 Integer");
        assertEquals(Boolean.class, ClassUtils.resolvePrimitiveIfNecessary(boolean.class), "boolean 应该解析为 Boolean");
        assertEquals(String.class, ClassUtils.resolvePrimitiveIfNecessary(String.class), "String 应该保持不变");
    }

    @Test
    @DisplayName("测试解析类名为原始类型")
    void testResolvePrimitiveClassName() {
        assertEquals(int.class, ClassUtils.resolvePrimitiveClassName("int"), "应该解析 int");
        assertEquals(boolean.class, ClassUtils.resolvePrimitiveClassName("boolean"), "应该解析 boolean");
        assertEquals(long.class, ClassUtils.resolvePrimitiveClassName("long"), "应该解析 long");
        assertNull(ClassUtils.resolvePrimitiveClassName("String"), "String 不是原始类型");
        assertNull(ClassUtils.resolvePrimitiveClassName("Integer"), "Integer 不是原始类型");
    }

    // ==================== 继承关系测试 ====================

    @Test
    @DisplayName("测试获取所有接口")
    void testGetAllInterfaces() {
        List<Class<?>> interfaces = ClassUtils.getAllInterfaces(OrderService.class);

        // OrderService 继承 BaseService，BaseService 没有实现接口
        // 但可以通过其他类测试
        List<Class<?>> userInterfaces = ClassUtils.getAllInterfaces(User.class);
        assertNotNull(userInterfaces, "应该返回接口列表");
    }

    @Test
    @DisplayName("测试获取所有接口 - ArrayList")
    void testGetAllInterfaces_ArrayList() {
        List<Class<?>> interfaces = ClassUtils.getAllInterfaces(java.util.ArrayList.class);

        assertTrue(interfaces.contains(List.class), "应该包含 List 接口");
        assertTrue(interfaces.contains(Serializable.class), "应该包含 Serializable 接口");
    }

    @Test
    @DisplayName("测试获取所有接口作为数组")
    void testGetAllInterfacesAsArray() {
        Class<?>[] interfaces = ClassUtils.getAllInterfacesAsArray(java.util.ArrayList.class);

        assertTrue(interfaces.length > 0, "应该返回接口数组");
    }

    @Test
    @DisplayName("测试获取继承树")
    void testGetInheritanceTree() {
        List<Class<?>> inheritance = ClassUtils.getInheritanceTree(OrderService.class);

        assertTrue(inheritance.contains(OrderService.class), "应该包含 OrderService");
        assertTrue(inheritance.contains(BaseService.class), "应该包含 BaseService");
        assertTrue(inheritance.contains(Object.class), "应该包含 Object");
    }

    // ==================== 类存在性测试 ====================

    @Test
    @DisplayName("测试判断类是否存在")
    void testIsPresent() {
        assertTrue(ClassUtils.isPresent("java.lang.String"), "String 类应该存在");
        assertTrue(ClassUtils.isPresent("java.util.ArrayList"), "ArrayList 类应该存在");
        assertFalse(ClassUtils.isPresent("com.nonexistent.Class"), "不存在的类应该返回 false");
    }

    @Test
    @DisplayName("测试判断类是否存在 - 使用指定类加载器")
    void testIsPresentWithClassLoader() {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        assertTrue(ClassUtils.isPresent("java.lang.String", classLoader), "应该使用指定类加载器判断");
    }
}
