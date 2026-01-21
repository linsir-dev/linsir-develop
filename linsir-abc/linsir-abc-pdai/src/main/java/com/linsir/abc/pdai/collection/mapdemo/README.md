# Map集合示例

## 功能说明

本示例代码展示了Java集合框架中Map接口的各种实现类的使用方法和特性，包括：

1. **HashMap**：
   - 基于哈希表实现
   - 无序（不保证迭代顺序）
   - 允许null键和null值
   - 键不重复，重复键会覆盖原有值
   - 非线程安全

2. **TreeMap**：
   - 基于红黑树实现
   - 有序（按键的自然顺序排序）
   - 不允许null键
   - 允许null值
   - 键不重复
   - 非线程安全

3. **LinkedHashMap**：
   - 基于哈希表和双向链表实现
   - 有序（按插入顺序）
   - 允许null键和null值
   - 键不重复
   - 非线程安全

4. **Hashtable**：
   - 基于哈希表实现
   - 无序
   - 不允许null键和null值
   - 键不重复
   - 线程安全（方法加synchronized锁）

5. **ConcurrentHashMap**：
   - 基于分段哈希表实现
   - 无序
   - 不允许null键和null值
   - 键不重复
   - 线程安全（采用分段锁或CAS机制，并发性能优于Hashtable）

## 示例代码结构

- **MapDemo.java**：包含各种Map实现类的使用示例，展示了：
  - 不同Map实现的创建和初始化
  - 元素的添加、修改和删除
  - 遍历Map的方法
  - 各种Map实现的特性对比
  - Map接口的常用方法使用

## 运行说明

1. 编译并运行MapDemo.java文件
2. 观察控制台输出，了解不同Map实现的行为差异
3. 注意各种Map实现对null键和null值的处理方式
4. 对比不同Map实现的排序特性

## 核心知识点

- Map是一种键值对（key-value）映射的数据结构
- 键（key）必须唯一，值（value）可以重复
- 不同的Map实现适用于不同的场景：
  - **HashMap**：适用于大多数场景，查询速度快
  - **TreeMap**：适用于需要按键排序的场景
  - **LinkedHashMap**：适用于需要保持插入顺序的场景
  - **Hashtable**：适用于需要线程安全的场景（但性能较差）
  - **ConcurrentHashMap**：适用于高并发场景（性能优于Hashtable）