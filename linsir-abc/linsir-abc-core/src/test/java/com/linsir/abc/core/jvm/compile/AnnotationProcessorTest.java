package com.linsir.abc.core.jvm.compile;

import com.linsir.abc.core.jvm.compile.test.BaseEntity;
import com.linsir.abc.core.jvm.compile.test.Product;
import com.linsir.abc.core.jvm.compile.test.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 注解处理器测试类
 * <p>
 * 测试AutoToString注解处理器的功能，验证生成的toString实现是否正确。
 * </p>
 *
 * @author linsir
 * @version 1.0
 * @since 2026-03-28
 */
@DisplayName("第10章 前端编译与优化 - 注解处理器测试")
public class AnnotationProcessorTest {

    /**
     * 测试User类的toString生成
     * <p>
     * 验证：
     * 1. 生成的toString方法存在且可调用
     * 2. 非排除字段包含在输出中
     * 3. 被排除的password字段不包含在输出中
     * 4. 静态字段不包含在输出中
     * </p>
     */
    @Test
    @DisplayName("测试User类的toString生成 - 排除敏感字段")
    void testUserToStringGeneration() {
        // 创建测试对象
        User user = new User(1L, "zhangsan", "secret123", 25);

        // 使用生成的toString实现
        String result = UserToStringImpl.toString(user);

        // 验证结果
        assertNotNull(result, "toString结果不应为null");
        assertTrue(result.contains("User{"), "应包含类名");
        assertTrue(result.contains("id=1"), "应包含id字段");
        assertTrue(result.contains("username=zhangsan"), "应包含username字段");
        assertTrue(result.contains("age=25"), "应包含age字段");
        assertFalse(result.contains("password"), "不应包含被排除的password字段");
        assertFalse(result.contains("DEFAULT_ROLE"), "不应包含静态字段");

        System.out.println("User toString输出: " + result);
    }

    /**
     * 测试Product类的toString生成（包含父类字段）
     * <p>
     * 验证：
     * 1. 当前类字段包含在输出中
     * 2. 父类字段包含在输出中（includeSuper=true）
     * 3. 被排除的costPrice字段不包含在输出中
     * </p>
     */
    @Test
    @DisplayName("测试Product类的toString生成 - 包含父类字段")
    void testProductToStringWithSuperFields() {
        // 创建测试对象
        LocalDateTime now = LocalDateTime.now();
        Product product = new Product(
                100L, now, now,
                "iPhone 15", "Apple iPhone 15",
                new BigDecimal("5999.00"),
                new BigDecimal("4999.00"),
                100
        );

        // 使用生成的toString实现
        String result = ProductToStringImpl.toString(product);

        // 验证结果
        assertNotNull(result, "toString结果不应为null");
        assertTrue(result.contains("Product{"), "应包含类名");
        assertTrue(result.contains("id=100"), "应包含父类id字段");
        assertTrue(result.contains("name=iPhone 15"), "应包含name字段");
        assertTrue(result.contains("price=5999.00"), "应包含price字段");
        assertTrue(result.contains("stock=100"), "应包含stock字段");
        assertFalse(result.contains("costPrice"), "不应包含被排除的costPrice字段");

        System.out.println("Product toString输出: " + result);
    }

    /**
     * 测试toString方法处理null对象
     * <p>
     * 验证生成的toString方法能正确处理null输入。
     * </p>
     */
    @Test
    @DisplayName("测试toString处理null对象")
    void testToStringWithNullObject() {
        String result = UserToStringImpl.toString(null);

        assertEquals("null", result, "null对象应返回字符串'null'");
        System.out.println("Null对象toString输出: " + result);
    }

    /**
     * 测试生成的toString格式
     * <p>
     * 验证输出格式符合预期：ClassName{field1=value1, field2=value2}
     * </p>
     */
    @Test
    @DisplayName("测试toString输出格式")
    void testToStringFormat() {
        User user = new User(1L, "test", "pass", 20);
        String result = UserToStringImpl.toString(user);

        // 验证格式
        assertTrue(result.startsWith("User{"), "应以类名开头");
        assertTrue(result.endsWith("}"), "应以}结尾");
        assertTrue(result.contains(", "), "字段之间应有逗号和空格分隔");
        assertTrue(result.contains("="), "字段名和值之间应有等号");

        System.out.println("格式验证输出: " + result);
    }

    /**
     * 测试BaseEntity类的toString生成
     * <p>
     * BaseEntity本身没有使用@AutoToString注解，
     * 但作为Product的父类，其字段会被包含在Product的toString中。
     * </p>
     */
    @Test
    @DisplayName("测试BaseEntity字段在子类toString中的包含")
    void testBaseEntityFieldsInSubclassToString() {
        LocalDateTime createTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updateTime = LocalDateTime.of(2024, 1, 2, 15, 30);

        Product product = new Product(
                200L, createTime, updateTime,
                "MacBook Pro", "Apple MacBook Pro",
                new BigDecimal("14999.00"),
                new BigDecimal("12999.00"),
                50
        );

        String result = ProductToStringImpl.toString(product);

        // 验证父类字段
        assertTrue(result.contains("createTime=" + createTime.toString()),
                "应包含父类createTime字段");
        assertTrue(result.contains("updateTime=" + updateTime.toString()),
                "应包含父类updateTime字段");

        System.out.println("包含父类字段的toString输出: " + result);
    }

    /**
     * 测试User类的getter方法
     * <p>
     * 验证User类的基本功能正常。
     * </p>
     */
    @Test
    @DisplayName("测试User类基本功能")
    void testUserBasicFunctionality() {
        User user = new User(1L, "lisi", "password123", 30);

        assertEquals(1L, user.getId());
        assertEquals("lisi", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals(30, user.getAge());
        assertEquals("USER", User.getDefaultRole());

        // 测试setter
        user.setUsername("wangwu");
        assertEquals("wangwu", user.getUsername());
    }

    /**
     * 测试Product类的getter方法
     * <p>
     * 验证Product类的基本功能正常。
     * </p>
     */
    @Test
    @DisplayName("测试Product类基本功能")
    void testProductBasicFunctionality() {
        LocalDateTime now = LocalDateTime.now();
        Product product = new Product(
                1L, now, now,
                "iPad", "Apple iPad",
                new BigDecimal("3999.00"),
                new BigDecimal("2999.00"),
                200
        );

        assertEquals(1L, product.getId());
        assertEquals("iPad", product.getName());
        assertEquals(new BigDecimal("3999.00"), product.getPrice());
        assertEquals(new BigDecimal("2999.00"), product.getCostPrice());
        assertEquals(200, product.getStock());
    }
}
