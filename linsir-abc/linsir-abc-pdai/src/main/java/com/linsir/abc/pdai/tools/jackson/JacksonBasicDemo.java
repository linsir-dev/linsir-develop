package com.linsir.abc.pdai.tools.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Jackson 基本功能示例
 * 演示 JSON 序列化和反序列化的基本操作
 */
public class JacksonBasicDemo {

    /**
     * 演示 Jackson 基本功能
     */
    public static void demonstrateBasicFeatures() {
        try {
            // 创建 ObjectMapper 实例
            ObjectMapper objectMapper = new ObjectMapper();

            // 1. Java 对象转 JSON 字符串
            System.out.println("=== 1. Java 对象转 JSON 字符串 ===");
            User user = new User(1, "John Doe", 30, "john@example.com", new Date());
            String jsonString = objectMapper.writeValueAsString(user);
            System.out.println("JSON 字符串:");
            System.out.println(jsonString);

            // 2. JSON 字符串转 Java 对象
            System.out.println("\n=== 2. JSON 字符串转 Java 对象 ===");
            User parsedUser = objectMapper.readValue(jsonString, User.class);
            System.out.println("解析后的 User 对象:");
            System.out.println(parsedUser);

            // 3. Java 集合转 JSON 字符串
            System.out.println("\n=== 3. Java 集合转 JSON 字符串 ===");
            List<User> userList = new ArrayList<>();
            userList.add(new User(1, "John Doe", 30, "john@example.com", new Date()));
            userList.add(new User(2, "Jane Smith", 25, "jane@example.com", new Date()));
            String jsonArray = objectMapper.writeValueAsString(userList);
            System.out.println("JSON 数组:");
            System.out.println(jsonArray);

            // 4. JSON 字符串转 Java 集合
            System.out.println("\n=== 4. JSON 字符串转 Java 集合 ===");
            User[] parsedUsers = objectMapper.readValue(jsonArray, User[].class);
            System.out.println("解析后的 User 数组:");
            for (User u : parsedUsers) {
                System.out.println(u);
            }

            // 5. Java Map 转 JSON 字符串
            System.out.println("\n=== 5. Java Map 转 JSON 字符串 ===");
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", 1);
            userMap.put("name", "John Doe");
            userMap.put("age", 30);
            userMap.put("email", "john@example.com");
            userMap.put("createdAt", new Date());
            String jsonMap = objectMapper.writeValueAsString(userMap);
            System.out.println("JSON Map:");
            System.out.println(jsonMap);

            // 6. JSON 字符串转 Java Map
            System.out.println("\n=== 6. JSON 字符串转 Java Map ===");
            Map<String, Object> parsedMap = objectMapper.readValue(jsonMap, Map.class);
            System.out.println("解析后的 Map:");
            parsedMap.forEach((key, value) -> {
                System.out.println(key + ": " + value);
            });

        } catch (IOException e) {
            System.err.println("处理 JSON 时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 用户实体类
     */
    public static class User {
        private int id;
        private String name;
        private int age;
        private String email;
        private Date createdAt;

        // 无参构造函数（Jackson 反序列化需要）
        public User() {
        }

        // 有参构造函数
        public User(int id, String name, int age, String email, Date createdAt) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.email = email;
            this.createdAt = createdAt;
        }

        // getter 和 setter 方法
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    ", email='" + email + '\'' +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }
}
