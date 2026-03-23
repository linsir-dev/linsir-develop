package com.linsir.spring.framework.spring_core.reflection.processor;

import com.linsir.spring.framework.spring_core.reflection.model.Autowired;
import com.linsir.spring.framework.spring_core.reflection.model.User;
import com.linsir.spring.framework.spring_core.reflection.service.UserRepository;
import com.linsir.spring.framework.spring_core.reflection.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AutowiredAnnotationProcessor 测试类
 * 测试依赖注入处理器的功能
 */
@DisplayName("AutowiredAnnotationProcessor 依赖注入测试")
class AutowiredAnnotationProcessorTest {

    private AutowiredAnnotationProcessor processor;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        processor = new AutowiredAnnotationProcessor();
        userRepository = new UserRepository();
        processor.registerBean(userRepository);
    }

    @Test
    @DisplayName("测试注册 Bean")
    void testRegisterBean() {
        assertTrue(processor.containsBean(UserRepository.class), "应该包含 UserRepository");
        assertEquals(userRepository, processor.getBean(UserRepository.class), "应该返回正确的 Bean");
    }

    @Test
    @DisplayName("测试依赖注入 - 字段注入")
    void testProcess_FieldInjection() {
        // 创建 UserService 实例
        UserService userService = new UserService();

        // 验证注入前 userRepository 为 null
        assertNull(userService.getUserRepository(), "注入前 userRepository 应该为 null");

        // 执行依赖注入
        processor.process(userService);

        // 验证注入成功
        assertNotNull(userService.getUserRepository(), "注入后 userRepository 不应该为 null");
        assertEquals(userRepository, userService.getUserRepository(), "应该注入正确的 Bean");
    }

    @Test
    @DisplayName("测试依赖注入 - 创建 Bean 并注入")
    void testCreateBean() {
        // 先注册 UserRepository
        processor.registerBean(userRepository);

        // 创建 UserService 并自动注入
        UserService userService = processor.createBean(UserService.class);

        // 验证创建和注入
        assertNotNull(userService, "应该创建 UserService 实例");
        assertNotNull(userService.getUserRepository(), "应该自动注入 userRepository");
    }

    @Test
    @DisplayName("测试依赖注入 - 必需的依赖不存在")
    void testProcess_RequiredDependencyNotFound() {
        // 创建新的处理器（不注册任何 Bean）
        AutowiredAnnotationProcessor emptyProcessor = new AutowiredAnnotationProcessor();

        // 创建 UserService 实例
        UserService userService = new UserService();

        // 尝试注入，应该抛出异常
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> emptyProcessor.process(userService),
            "必需的依赖不存在应该抛出异常"
        );

        assertTrue(exception.getMessage().contains("No bean of type"), "异常信息应该包含 Bean 类型信息");
    }

    @Test
    @DisplayName("测试获取 Bean")
    void testGetBean() {
        processor.registerBean(userRepository);

        UserRepository retrieved = processor.getBean(UserRepository.class);
        assertNotNull(retrieved, "应该获取到 Bean");
        assertEquals(userRepository, retrieved, "应该获取到正确的 Bean");
    }

    @Test
    @DisplayName("测试判断 Bean 是否存在")
    void testContainsBean() {
        assertTrue(processor.containsBean(UserRepository.class), "应该包含已注册的 Bean");
        assertFalse(processor.containsBean(UserService.class), "不应该包含未注册的 Bean");
    }

    @Test
    @DisplayName("测试清空容器")
    void testClear() {
        processor.registerBean(userRepository);
        assertEquals(1, processor.getBeanCount(), "注册后应该有 1 个 Bean");

        processor.clear();
        assertEquals(0, processor.getBeanCount(), "清空后应该没有 Bean");
        assertFalse(processor.containsBean(UserRepository.class), "清空后不应该包含 Bean");
    }

    @Test
    @DisplayName("测试获取 Bean 数量")
    void testGetBeanCount() {
        // setUp 中已经注册了 UserRepository，所以初始有 1 个（可能还有其接口）
        int initialCount = processor.getBeanCount();
        assertTrue(initialCount >= 1, "初始应该有至少 1 个 Bean");

        processor.registerBean(new UserService());
        // UserService 实现了 IUserService 接口，所以会注册多个条目
        assertTrue(processor.getBeanCount() > initialCount, "注册后 Bean 数量应该增加");
    }

    @Test
    @DisplayName("测试依赖注入 - 实际使用")
    void testProcess_RealUsage() {
        // 注册 UserRepository
        processor.registerBean(userRepository);

        // 创建并注入 UserService
        UserService userService = new UserService();
        processor.process(userService);

        // 使用注入后的 UserService 进行操作
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        // 保存用户
        User savedUser = userService.save(user);

        // 验证保存成功
        assertNotNull(savedUser.getId(), "保存后应该有 ID");

        // 查询用户
        User foundUser = userService.findById(savedUser.getId());
        assertNotNull(foundUser, "应该能查询到用户");
        assertEquals("testUser", foundUser.getUsername(), "用户名应该匹配");
    }

    @Test
    @DisplayName("测试注册 null Bean - 应该抛出异常")
    void testRegisterBean_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            processor.registerBean(null);
        }, "注册 null Bean 应该抛出异常");
    }

    @Test
    @DisplayName("测试处理 null 目标 - 应该抛出异常")
    void testProcess_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            processor.process(null);
        }, "处理 null 目标应该抛出异常");
    }
}
