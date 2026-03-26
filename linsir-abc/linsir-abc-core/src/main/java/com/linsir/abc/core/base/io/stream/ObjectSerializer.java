package com.linsir.abc.core.base.io.stream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 对象序列化器
 * 演示ObjectInputStream/ObjectOutputStream的使用，实现对象的序列化和反序列化
 *
 * 设计要点：
 * 1. Serializable接口标记可序列化类
 * 2. transient关键字排除字段序列化
 * 3. serialVersionUID用于版本控制
 * 4. 自定义writeObject/readObject实现特殊序列化逻辑
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class ObjectSerializer {

    /**
     * 序列化对象到文件
     *
     * @param filePath 文件路径
     * @param object 要序列化的对象
     * @throws IOException 当IO操作失败时
     */
    public void serialize(String filePath, Serializable object) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {

            oos.writeObject(object);
            oos.flush();
        }
    }

    /**
     * 从文件反序列化对象
     *
     * @param filePath 文件路径
     * @return 反序列化的对象
     * @throws IOException 当IO操作失败时
     * @throws ClassNotFoundException 当类找不到时
     */
    @SuppressWarnings("unchecked")
    public <T> T deserialize(String filePath) throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ObjectInputStream ois = new ObjectInputStream(bis)) {

            return (T) ois.readObject();
        }
    }

    /**
     * 序列化对象列表
     *
     * @param filePath 文件路径
     * @param objects 对象列表
     * @throws IOException 当IO操作失败时
     */
    public void serializeList(String filePath, List<? extends Serializable> objects) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(filePath)))) {

            oos.writeInt(objects.size());
            for (Serializable obj : objects) {
                oos.writeObject(obj);
            }
            oos.flush();
        }
    }

    /**
     * 反序列化对象列表
     *
     * @param filePath 文件路径
     * @return 对象列表
     * @throws IOException 当IO操作失败时
     * @throws ClassNotFoundException 当类找不到时
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> deserializeList(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))) {

            int size = ois.readInt();
            List<T> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                list.add((T) ois.readObject());
            }
            return list;
        }
    }

    /**
     * 深度拷贝对象（通过序列化）
     *
     * @param object 原对象
     * @return 拷贝的对象
     * @throws IOException 当IO操作失败时
     * @throws ClassNotFoundException 当类找不到时
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T deepCopy(T object) throws IOException, ClassNotFoundException {
        // 序列化到内存
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
        }

        // 从内存反序列化
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (T) ois.readObject();
        }
    }

    /**
     * 示例可序列化类
     */
    public static class Person implements Serializable {
        private static final long serialVersionUID = 1L;

        private String name;
        private int age;
        private transient String password; // transient字段不会被序列化
        private Address address;

        public Person() {
        }

        public Person(String name, int age, String password, Address address) {
            this.name = name;
            this.age = age;
            this.password = password;
            this.address = address;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", password='" + password + '\'' +
                    ", address=" + address +
                    '}';
        }
    }

    /**
     * 地址类（可序列化）
     */
    public static class Address implements Serializable {
        private static final long serialVersionUID = 1L;

        private String city;
        private String street;
        private String zipCode;

        public Address() {
        }

        public Address(String city, String street, String zipCode) {
            this.city = city;
            this.street = street;
            this.zipCode = zipCode;
        }

        // Getters and Setters
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }

        @Override
        public String toString() {
            return "Address{" +
                    "city='" + city + '\'' +
                    ", street='" + street + '\'' +
                    ", zipCode='" + zipCode + '\'' +
                    '}';
        }
    }

    /**
     * 自定义序列化类
     */
    public static class CustomSerializable implements Serializable {
        private static final long serialVersionUID = 1L;

        private String data;
        private transient int cachedHash; // 不序列化缓存值

        public CustomSerializable(String data) {
            this.data = data;
            this.cachedHash = data.hashCode();
        }

        /**
         * 自定义序列化方法
         */
        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject(); // 序列化非transient字段
            // 可以添加额外的序列化逻辑
        }

        /**
         * 自定义反序列化方法
         */
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject(); // 反序列化非transient字段
            // 恢复transient字段
            this.cachedHash = (data != null) ? data.hashCode() : 0;
        }

        public String getData() { return data; }
        public int getCachedHash() { return cachedHash; }

        @Override
        public String toString() {
            return "CustomSerializable{" +
                    "data='" + data + '\'' +
                    ", cachedHash=" + cachedHash +
                    '}';
        }
    }

    /**
     * 演示对象序列化的使用
     */
    public static void demonstrate() {
        ObjectSerializer serializer = new ObjectSerializer();

        try {
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "object_serializer_demo");
            tempDir.mkdirs();

            // 测试单对象序列化
            String personFile = new File(tempDir, "person.ser").getAbsolutePath();

            Address address = new Address("北京", "中关村大街", "100080");
            Person original = new Person("张三", 30, "secret123", address);

            System.out.println("原始对象: " + original);

            // 序列化
            serializer.serialize(personFile, original);
            System.out.println("对象已序列化到: " + personFile);

            // 反序列化
            Person restored = serializer.deserialize(personFile);
            System.out.println("恢复对象: " + restored);

            // 注意：transient字段password不会被序列化
            System.out.println("原始密码: " + original.getPassword());
            System.out.println("恢复密码: " + restored.getPassword()); // 应为null

            // 测试对象列表序列化
            String listFile = new File(tempDir, "persons.ser").getAbsolutePath();

            List<Person> persons = new ArrayList<>();
            persons.add(new Person("张三", 30, "pass1", new Address("北京", "街道1", "100001")));
            persons.add(new Person("李四", 25, "pass2", new Address("上海", "街道2", "200002")));
            persons.add(new Person("王五", 35, "pass3", new Address("广州", "街道3", "510003")));

            serializer.serializeList(listFile, persons);
            System.out.println("\n对象列表已序列化");

            List<Person> restoredList = serializer.deserializeList(listFile);
            System.out.println("恢复的对象列表:");
            for (Person p : restoredList) {
                System.out.println("  " + p.getName() + ", " + p.getAge() + "岁, " + p.getAddress().getCity());
            }

            // 测试深度拷贝
            System.out.println("\n深度拷贝测试:");
            Person copy = serializer.deepCopy(original);
            System.out.println("原对象地址: " + original.getAddress());
            System.out.println("拷贝地址: " + copy.getAddress());
            System.out.println("地址对象相同: " + (original.getAddress() == copy.getAddress())); // 应为false

            // 测试自定义序列化
            String customFile = new File(tempDir, "custom.ser").getAbsolutePath();
            CustomSerializable custom = new CustomSerializable("Hello, Custom!");
            System.out.println("\n自定义序列化对象: " + custom);

            serializer.serialize(customFile, custom);
            CustomSerializable restoredCustom = serializer.deserialize(customFile);
            System.out.println("恢复的自定义对象: " + restoredCustom);
            System.out.println("缓存哈希值已恢复: " + (restoredCustom.getCachedHash() != 0));

            // 清理
            new File(personFile).delete();
            new File(listFile).delete();
            new File(customFile).delete();
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
