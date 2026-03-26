package com.linsir.abc.core.base.io.stream;

import java.io.*;
import java.util.Date;

/**
 * Externalizable接口实现
 * 演示Externalizable接口的使用，提供完全自定义的序列化控制
 *
 * 设计要点：
 * 1. Externalizable继承自Serializable，但提供更细粒度的控制
 * 2. 必须实现writeExternal和readExternal方法
 3. 必须提供无参构造器
 * 4. 性能通常比Serializable更好（更少的反射开销）
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class ExternalizableImplementation {

    /**
     * 使用Externalizable的用户类
     */
    public static class User implements Externalizable {
        private static final long serialVersionUID = 1L;

        private Long id;
        private String username;
        private transient String password; // 即使标记transient，Externalizable也会按自定义逻辑处理
        private String email;
        private Date createdAt;
        private int loginCount;

        // 必须提供无参构造器
        public User() {
        }

        public User(Long id, String username, String password, String email, Date createdAt, int loginCount) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.email = email;
            this.createdAt = createdAt;
            this.loginCount = loginCount;
        }

        /**
         * 自定义序列化逻辑
         * 可以控制哪些字段被序列化，以及如何序列化
         */
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            // 序列化基本字段
            out.writeLong(id != null ? id : 0L);
            out.writeUTF(username != null ? username : "");

            // 密码加密后序列化（示例：简单反转）
            String encryptedPassword = password != null ? new StringBuilder(password).reverse().toString() : "";
            out.writeUTF(encryptedPassword);

            out.writeUTF(email != null ? email : "");
            out.writeLong(createdAt != null ? createdAt.getTime() : 0L);
            out.writeInt(loginCount);
        }

        /**
         * 自定义反序列化逻辑
         * 必须与writeExternal的顺序一致
         */
        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            long idValue = in.readLong();
            this.id = idValue != 0L ? idValue : null;

            this.username = in.readUTF();

            // 密码解密
            String encryptedPassword = in.readUTF();
            this.password = !encryptedPassword.isEmpty() ? new StringBuilder(encryptedPassword).reverse().toString() : null;

            this.email = in.readUTF();
            long time = in.readLong();
            this.createdAt = time != 0L ? new Date(time) : null;
            this.loginCount = in.readInt();
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
        public int getLoginCount() { return loginCount; }
        public void setLoginCount(int loginCount) { this.loginCount = loginCount; }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", email='" + email + '\'' +
                    ", createdAt=" + createdAt +
                    ", loginCount=" + loginCount +
                    '}';
        }
    }

    /**
     * 序列化Externalizable对象
     *
     * @param filePath 文件路径
     * @param obj Externalizable对象
     * @throws IOException 当IO操作失败时
     */
    public void serialize(String filePath, Externalizable obj) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(filePath)))) {
            oos.writeObject(obj);
            oos.flush();
        }
    }

    /**
     * 反序列化Externalizable对象
     *
     * @param filePath 文件路径
     * @return 反序列化的对象
     * @throws IOException 当IO操作失败时
     * @throws ClassNotFoundException 当类找不到时
     */
    @SuppressWarnings("unchecked")
    public <T extends Externalizable> T deserialize(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))) {
            return (T) ois.readObject();
        }
    }

    /**
     * 性能比较：Serializable vs Externalizable
     */
    public static void performanceComparison() throws IOException, ClassNotFoundException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "externalizable_perf");
        tempDir.mkdirs();

        int iterations = 10000;

        // 测试Serializable
        String serializableFile = new File(tempDir, "serializable.ser").getAbsolutePath();
        SerializableUser serUser = new SerializableUser(1L, "user1", "password", "user@example.com", new Date(), 100);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serializableFile))) {
                oos.writeObject(serUser);
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serializableFile))) {
                ois.readObject();
            }
        }
        long serializableTime = System.currentTimeMillis() - startTime;

        // 测试Externalizable
        String externalizableFile = new File(tempDir, "externalizable.ser").getAbsolutePath();
        User extUser = new User(1L, "user1", "password", "user@example.com", new Date(), 100);

        startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(externalizableFile))) {
                oos.writeObject(extUser);
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(externalizableFile))) {
                ois.readObject();
            }
        }
        long externalizableTime = System.currentTimeMillis() - startTime;

        System.out.println("性能比较 (" + iterations + " 次序列化/反序列化):");
        System.out.println("  Serializable:   " + serializableTime + " ms");
        System.out.println("  Externalizable: " + externalizableTime + " ms");
        System.out.println("  性能提升: " + String.format("%.2f", (double)serializableTime / externalizableTime) + "x");

        // 清理
        new File(serializableFile).delete();
        new File(externalizableFile).delete();
        tempDir.delete();
    }

    /**
     * 用于性能比较的Serializable类
     */
    public static class SerializableUser implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long id;
        private String username;
        private transient String password;
        private String email;
        private Date createdAt;
        private int loginCount;

        public SerializableUser() {
        }

        public SerializableUser(Long id, String username, String password, String email, Date createdAt, int loginCount) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.email = email;
            this.createdAt = createdAt;
            this.loginCount = loginCount;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
        public int getLoginCount() { return loginCount; }
        public void setLoginCount(int loginCount) { this.loginCount = loginCount; }
    }

    /**
     * 演示Externalizable的使用
     */
    public static void demonstrate() {
        ExternalizableImplementation impl = new ExternalizableImplementation();

        try {
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "externalizable_demo");
            tempDir.mkdirs();

            String userFile = new File(tempDir, "user.ser").getAbsolutePath();

            // 创建用户对象
            User original = new User(
                    1L,
                    "john_doe",
                    "mySecretPassword123",
                    "john@example.com",
                    new Date(),
                    42
            );

            System.out.println("原始用户: " + original);

            // 序列化
            impl.serialize(userFile, original);
            System.out.println("用户已序列化到: " + userFile);

            // 反序列化
            User restored = impl.deserialize(userFile);
            System.out.println("恢复用户: " + restored);

            // 验证密码是否正确解密
            System.out.println("密码一致: " + original.getPassword().equals(restored.getPassword()));

            // 性能比较
            System.out.println("\n" + "=".repeat(50));
            performanceComparison();

            // 清理
            new File(userFile).delete();
            tempDir.delete();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
