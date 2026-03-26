package com.linsir.abc.core.base.io.stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

/**
 * ExternalizableImplementation测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExternalizableImplementationTest {

    private ExternalizableImplementation serializer;
    private Path tempDir;

    @BeforeEach
    public void setUp() throws IOException {
        serializer = new ExternalizableImplementation();
        tempDir = Files.createTempDirectory("externalizable_test");
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (tempDir != null) {
            Files.walk(tempDir)
                .sorted((a, b) -> -a.compareTo(b))
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        // ignore
                    }
                });
        }
    }

    @Test
    public void testSerializeAndDeserialize() throws IOException, ClassNotFoundException {
        String filePath = tempDir.resolve("user.ser").toString();

        Date now = new Date();
        ExternalizableImplementation.User original = new ExternalizableImplementation.User(
            1L,
            "john_doe",
            "mySecretPassword123",
            "john@example.com",
            now,
            42
        );

        // 序列化
        serializer.serialize(filePath, original);

        // 验证文件存在
        assertTrue(new File(filePath).exists());

        // 反序列化
        ExternalizableImplementation.User restored = serializer.deserialize(filePath);

        // 验证数据
        assertEquals(original.getId(), restored.getId());
        assertEquals(original.getUsername(), restored.getUsername());
        assertEquals(original.getPassword(), restored.getPassword()); // 密码应该被正确解密
        assertEquals(original.getEmail(), restored.getEmail());
        assertEquals(original.getCreatedAt().getTime(), restored.getCreatedAt().getTime());
        assertEquals(original.getLoginCount(), restored.getLoginCount());
    }

    @Test
    public void testSerializeWithNullValues() throws IOException, ClassNotFoundException {
        String filePath = tempDir.resolve("user_null.ser").toString();

        ExternalizableImplementation.User original = new ExternalizableImplementation.User();
        original.setId(null);
        original.setUsername(null);
        original.setPassword(null);
        original.setEmail(null);
        original.setCreatedAt(null);
        original.setLoginCount(0);

        // 序列化
        serializer.serialize(filePath, original);

        // 反序列化
        ExternalizableImplementation.User restored = serializer.deserialize(filePath);

        // 验证
        assertNull(restored.getId());
        assertEquals("", restored.getUsername());
        assertNull(restored.getPassword());
        assertEquals("", restored.getEmail());
        assertNull(restored.getCreatedAt());
        assertEquals(0, restored.getLoginCount());
    }

    @Test
    public void testPasswordEncryption() throws IOException, ClassNotFoundException {
        String filePath = tempDir.resolve("user_password.ser").toString();

        String originalPassword = "mySecretPassword123";
        ExternalizableImplementation.User original = new ExternalizableImplementation.User(
            1L,
            "test",
            originalPassword,
            "test@example.com",
            new Date(),
            1
        );

        // 序列化
        serializer.serialize(filePath, original);

        // 反序列化
        ExternalizableImplementation.User restored = serializer.deserialize(filePath);

        // 验证密码被正确加密和解密
        assertEquals(originalPassword, restored.getPassword());
    }

    @Test
    public void testUserToString() {
        Date now = new Date();
        ExternalizableImplementation.User user = new ExternalizableImplementation.User(
            1L,
            "john_doe",
            "password",
            "john@example.com",
            now,
            42
        );

        String str = user.toString();

        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("username='john_doe'"));
        assertTrue(str.contains("email='john@example.com'"));
        assertTrue(str.contains("loginCount=42"));
    }

    @Test
    public void testSerializableUser() {
        ExternalizableImplementation.SerializableUser user = new ExternalizableImplementation.SerializableUser(
            1L,
            "test",
            "password",
            "test@example.com",
            new Date(),
            10
        );

        assertEquals(1L, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(10, user.getLoginCount());
    }

    @Test
    public void testSerializableUserSetters() {
        ExternalizableImplementation.SerializableUser user = new ExternalizableImplementation.SerializableUser();

        user.setId(2L);
        user.setUsername("newuser");
        user.setPassword("newpass");
        user.setEmail("new@example.com");
        user.setCreatedAt(new Date());
        user.setLoginCount(20);

        assertEquals(2L, user.getId());
        assertEquals("newuser", user.getUsername());
        assertEquals("newpass", user.getPassword());
        assertEquals("new@example.com", user.getEmail());
        assertEquals(20, user.getLoginCount());
    }

    @Test
    public void testUserSetters() {
        ExternalizableImplementation.User user = new ExternalizableImplementation.User();

        user.setId(3L);
        user.setUsername("user3");
        user.setPassword("pass3");
        user.setEmail("user3@example.com");
        user.setCreatedAt(new Date());
        user.setLoginCount(30);

        assertEquals(3L, user.getId());
        assertEquals("user3", user.getUsername());
        assertEquals("pass3", user.getPassword());
        assertEquals("user3@example.com", user.getEmail());
        assertEquals(30, user.getLoginCount());
    }

    @Test
    public void testPerformanceComparison() {
        // 测试性能比较方法不抛出异常
        assertDoesNotThrow(() -> ExternalizableImplementation.performanceComparison());
    }

    @Test
    public void testDemonstrate() {
        // 测试演示方法不抛出异常
        assertDoesNotThrow(() -> ExternalizableImplementation.demonstrate());
    }

    @Test
    public void testMain() {
        // 测试main方法不抛出异常
        assertDoesNotThrow(() -> ExternalizableImplementation.main(new String[]{}));
    }
}
