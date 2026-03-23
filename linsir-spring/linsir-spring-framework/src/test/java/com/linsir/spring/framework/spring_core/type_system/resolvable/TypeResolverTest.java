package com.linsir.spring.framework.spring_core.type_system.resolvable;

import com.linsir.spring.framework.spring_core.type_system.resolvable.container.DataHolder;
import com.linsir.spring.framework.spring_core.type_system.resolvable.container.GenericContainer;
import com.linsir.spring.framework.spring_core.type_system.resolvable.entity.User;
import com.linsir.spring.framework.spring_core.type_system.resolvable.processor.TypeResolver;
import com.linsir.spring.framework.spring_core.type_system.resolvable.service.BaseService;
import com.linsir.spring.framework.spring_core.type_system.resolvable.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 类型解析器测试类
 * 测试ResolvableType的各种使用场景
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
@DisplayName("类型解析器测试")
public class TypeResolverTest {

    /**
     * 测试解析类的泛型参数
     * UserService extends BaseService<User, Long>
     * 应该解析出User和Long
     */
    @Test
    @DisplayName("测试类泛型参数解析")
    public void testResolveClassGenerics() {
        Class<?>[] generics = TypeResolver.resolveClassGenerics(UserService.class, BaseService.class);

        assertNotNull(generics, "泛型参数不应为空");
        assertEquals(2, generics.length, "应该有2个泛型参数");
        assertEquals(User.class, generics[0], "第一个泛型参数应该是User");
        assertEquals(Long.class, generics[1], "第二个泛型参数应该是Long");
    }

    /**
     * 测试解析简单泛型字段
     * private List<String> stringList;
     */
    @Test
    @DisplayName("测试简单泛型字段解析")
    public void testResolveSimpleFieldGeneric() throws NoSuchFieldException {
        Field field = DataHolder.class.getDeclaredField("stringList");
        Class<?> genericType = TypeResolver.resolveFieldGeneric(field);

        assertEquals(String.class, genericType, "List的泛型参数应该是String");
    }

    /**
     * 测试解析Set泛型字段
     * private Set<Integer> integerSet;
     */
    @Test
    @DisplayName("测试Set泛型字段解析")
    public void testResolveSetFieldGeneric() throws NoSuchFieldException {
        Field field = DataHolder.class.getDeclaredField("integerSet");
        Class<?> genericType = TypeResolver.resolveFieldGeneric(field);

        assertEquals(Integer.class, genericType, "Set的泛型参数应该是Integer");
    }

    /**
     * 测试解析嵌套泛型字段
     * private List<List<String>> nestedList;
     */
    @Test
    @DisplayName("测试嵌套泛型字段解析")
    public void testResolveNestedFieldGeneric() throws NoSuchFieldException {
        Field field = DataHolder.class.getDeclaredField("nestedList");

        ResolvableType fieldType = ResolvableType.forField(field);

        // 第一层: List<List<String>>
        assertTrue(fieldType.resolve().isAssignableFrom(List.class), "应该是List类型");

        // 第二层泛型: List<String>
        ResolvableType nestedType = fieldType.getGeneric(0);
        assertTrue(nestedType.resolve().isAssignableFrom(List.class), "嵌套类型应该是List");

        // 最内层: String
        ResolvableType innerType = nestedType.getGeneric(0);
        assertEquals(String.class, innerType.resolve(), "最内层应该是String");
    }

    /**
     * 测试解析Map泛型字段
     * private Map<String, Object> stringObjectMap;
     */
    @Test
    @DisplayName("测试Map泛型字段解析")
    public void testResolveMapFieldGeneric() throws NoSuchFieldException {
        Field field = DataHolder.class.getDeclaredField("stringObjectMap");
        ResolvableType fieldType = ResolvableType.forField(field);

        // Map的Key类型
        Class<?> keyType = fieldType.getGeneric(0).resolve();
        assertEquals(String.class, keyType, "Map的Key应该是String");

        // Map的Value类型
        Class<?> valueType = fieldType.getGeneric(1).resolve();
        assertEquals(Object.class, valueType, "Map的Value应该是Object");
    }

    /**
     * 测试解析复杂Map泛型字段
     * private Map<String, List<Integer>> complexMap;
     */
    @Test
    @DisplayName("测试复杂Map泛型字段解析")
    public void testResolveComplexMapFieldGeneric() throws NoSuchFieldException {
        Field field = DataHolder.class.getDeclaredField("complexMap");
        ResolvableType fieldType = ResolvableType.forField(field);

        // Key类型
        assertEquals(String.class, fieldType.getGeneric(0).resolve(), "Key应该是String");

        // Value类型: List<Integer>
        ResolvableType valueType = fieldType.getGeneric(1);
        assertTrue(valueType.resolve().isAssignableFrom(List.class), "Value应该是List");
        assertEquals(Integer.class, valueType.getGeneric(0).resolve(), "List的泛型应该是Integer");
    }

    /**
     * 测试解析方法返回类型的泛型
     */
    @Test
    @DisplayName("测试方法返回类型泛型解析")
    public void testResolveMethodReturnGeneric() throws NoSuchMethodException {
        Method method = UserService.class.getMethod("findAll");
        Class<?> genericType = TypeResolver.resolveMethodReturnGeneric(method);

        assertEquals(User.class, genericType, "返回类型List的泛型应该是User");
    }

    /**
     * 测试解析方法参数的泛型类型
     */
    @Test
    @DisplayName("测试方法参数泛型解析")
    public void testResolveMethodParameterGeneric() throws NoSuchMethodException {
        Method method = UserService.class.getMethod("save", User.class);

        // 使用ResolvableType解析参数类型
        ResolvableType paramType = ResolvableType.forMethodParameter(method, 0);
        assertEquals(User.class, paramType.resolve(), "参数类型应该是User");
    }

    /**
     * 测试类型可分配性检查
     */
    @Test
    @DisplayName("测试类型可分配性检查")
    public void testIsAssignable() {
        // ArrayList可以分配给List
        boolean result1 = TypeResolver.isAssignable(java.util.ArrayList.class, List.class);
        assertTrue(result1, "ArrayList应该可以分配给List");

        // String不能分配给Integer
        boolean result2 = TypeResolver.isAssignable(String.class, Integer.class);
        assertFalse(result2, "String不应该可以分配给Integer");
    }

    /**
     * 测试数组组件类型获取
     */
    @Test
    @DisplayName("测试数组组件类型获取")
    public void testGetArrayComponentType() {
        Class<?> componentType = TypeResolver.getArrayComponentType(String[].class);
        assertEquals(String.class, componentType, "String数组的组件类型应该是String");
    }

    /**
     * 测试传统反射方式解析泛型
     */
    @Test
    @DisplayName("测试传统反射方式解析泛型")
    public void testResolveGenericsTraditionally() {
        Type[] generics = TypeResolver.resolveGenericsTraditionally(UserService.class, BaseService.class);

        assertNotNull(generics, "泛型参数不应为空");
        assertEquals(2, generics.length, "应该有2个泛型参数");

        // 传统方式返回的是Type对象
        assertTrue(generics[0] instanceof Class, "第一个泛型应该是Class类型");
        assertEquals(User.class, generics[0], "第一个泛型应该是User");
    }

    /**
     * 测试GenericContainer的泛型解析
     */
    @Test
    @DisplayName("测试GenericContainer泛型解析")
    public void testGenericContainerResolution() throws NoSuchFieldException {
        // 创建String类型的容器
        GenericContainer<String> container = new GenericContainer<>("test", "value");

        assertEquals("test", container.getName(), "名称应该正确");
        assertEquals("value", container.getElement(), "元素应该正确");
        assertEquals(String.class, container.getElementType(), "元素类型应该是String");
    }

    /**
     * 测试ResolvableType的as方法
     * 将类型视为指定类的子类型
     */
    @Test
    @DisplayName("测试ResolvableType的as方法")
    public void testResolvableTypeAs() {
        ResolvableType userServiceType = ResolvableType.forClass(UserService.class);

        // 将UserService视为BaseService的子类型
        ResolvableType baseServiceType = userServiceType.as(BaseService.class);
        assertNotNull(baseServiceType, "应该能转换为BaseService类型");

        // 获取泛型参数
        ResolvableType[] generics = baseServiceType.getGenerics();
        assertEquals(2, generics.length, "BaseService应该有2个泛型参数");
        assertEquals(User.class, generics[0].resolve(), "第一个泛型应该是User");
        assertEquals(Long.class, generics[1].resolve(), "第二个泛型应该是Long");
    }

    /**
     * 测试ResolvableType的isArray方法
     */
    @Test
    @DisplayName("测试数组类型检查")
    public void testIsArray() {
        ResolvableType arrayType = ResolvableType.forClass(String[].class);
        assertTrue(arrayType.isArray(), "String[]应该是数组类型");

        ResolvableType listType = ResolvableType.forClass(List.class);
        assertFalse(listType.isArray(), "List不是数组类型");
    }

    /**
     * 测试ResolvableType的getComponentType方法
     */
    @Test
    @DisplayName("测试数组组件类型")
    public void testGetComponentType() {
        ResolvableType arrayType = ResolvableType.forClass(String[].class);
        Class<?> componentType = arrayType.getComponentType().resolve();
        assertEquals(String.class, componentType, "String[]的组件类型应该是String");
    }
}
