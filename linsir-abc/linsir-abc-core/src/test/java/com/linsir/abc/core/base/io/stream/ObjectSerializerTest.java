package com.linsir.abc.core.base.io.stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ObjectSerializer 测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class ObjectSerializerTest {

    private ObjectSerializer serializer;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        serializer = new ObjectSerializer();
    }

    @Test
    @DisplayName("测试单对象序列化和反序列化")
    void testSerializeAndDeserialize() throws IOException, ClassNotFoundException {
        String filePath = tempDir.resolve("person.ser").toString();

        ObjectSerializer.Address address = new ObjectSerializer.Address("北京", "中关村大街", "100080");
        ObjectSerializer.Person original = new ObjectSerializer.Person("张三", 30, "secret123", address);

        serializer.serialize(filePath, original);
        ObjectSerializer.Person restored = serializer.deserialize(filePath);

        assertEquals(original.getName(), restored.getName());
        assertEquals(original.getAge(), restored.getAge());
        assertEquals(original.getAddress().getCity(), restored.getAddress().getCity());
    }

    @Test
    @DisplayName("测试transient字段不会被序列化")
    void testTransientField() throws IOException, ClassNotFoundException {
        String filePath = tempDir.resolve("person.ser").toString();

        ObjectSerializer.Address address = new ObjectSerializer.Address("北京", "中关村", "100080");
        ObjectSerializer.Person original = new ObjectSerializer.Person("张三", 30, "secret123", address);

        serializer.serialize(filePath, original);
        ObjectSerializer.Person restored = serializer.deserialize(filePath);

        assertNotNull(original.getPassword());
        assertNull(restored.getPassword()); // transient字段应为null
    }

    @Test
    @DisplayName("测试对象列表序列化和反序列化")
    void testSerializeAndDeserializeList() throws IOException, ClassNotFoundException {
        String filePath = tempDir.resolve("persons.ser").toString();

        List<ObjectSerializer.Person> persons = new ArrayList<>();
        persons.add(new ObjectSerializer.Person("张三", 30, "pass1",
                new ObjectSerializer.Address("北京", "街道1", "100001")));
        persons.add(new ObjectSerializer.Person("李四", 25, "pass2",
                new ObjectSerializer.Address("上海", "街道2", "200002")));
        persons.add(new ObjectSerializer.Person("王五", 35, "pass3",
                new ObjectSerializer.Address("广州", "街道3", "510003")));

        serializer.serializeList(filePath, persons);
        List<ObjectSerializer.Person> restored = serializer.deserializeList(filePath);

        assertEquals(persons.size(), restored.size());
        for (int i = 0; i < persons.size(); i++) {
            assertEquals(persons.get(i).getName(), restored.get(i).getName());
            assertEquals(persons.get(i).getAge(), restored.get(i).getAge());
        }
    }

    @Test
    @DisplayName("测试深度拷贝")
    void testDeepCopy() throws IOException, ClassNotFoundException {
        ObjectSerializer.Address address = new ObjectSerializer.Address("北京", "中关村", "100080");
        ObjectSerializer.Person original = new ObjectSerializer.Person("张三", 30, "secret", address);

        ObjectSerializer.Person copy = serializer.deepCopy(original);

        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getAge(), copy.getAge());
        assertNotSame(original, copy);
        assertNotSame(original.getAddress(), copy.getAddress()); // 深度拷贝，地址对象不同
    }

    @Test
    @DisplayName("测试自定义序列化")
    void testCustomSerializable() throws IOException, ClassNotFoundException {
        String filePath = tempDir.resolve("custom.ser").toString();

        ObjectSerializer.CustomSerializable original = new ObjectSerializer.CustomSerializable("test data");
        int originalHash = original.getCachedHash();

        serializer.serialize(filePath, original);
        ObjectSerializer.CustomSerializable restored = serializer.deserialize(filePath);

        assertEquals(original.getData(), restored.getData());
        assertEquals(originalHash, restored.getCachedHash()); // 反序列化后恢复cachedHash
    }

    @Test
    @DisplayName("测试反序列化不存在的文件抛出异常")
    void testDeserializeNonExistentFile() {
        String filePath = tempDir.resolve("nonexistent.ser").toString();

        assertThrows(IOException.class, () -> serializer.deserialize(filePath));
    }
}
