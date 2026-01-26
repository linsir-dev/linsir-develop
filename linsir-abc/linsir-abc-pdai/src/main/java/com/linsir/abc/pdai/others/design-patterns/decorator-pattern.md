# 装饰器模式

## 装饰器模式的定义

装饰器模式（Decorator Pattern）是一种结构型设计模式，它允许在不改变现有对象结构的情况下，动态地给对象添加额外的职责。装饰器模式通过创建一个包装对象，也就是装饰器来包裹真实的对象，从而在不改变原类文件和使用继承的情况下，动态地扩展一个对象的功能。装饰器模式又称为包装模式（Wrapper Pattern）。

## 装饰器模式的结构

```
┌─────────────────┐         ┌─────────────────┐
│   Component     │         │   Decorator     │
├─────────────────┤         ├─────────────────┤
│ + operation()   │         │ - component     │
└─────────────────┘         │ + operation()   │
        △                   └─────────────────┘
        │                           △
        │                           │
┌───────────────┐         ┌─────────────────┐
│ConcreteComponent│       │ConcreteDecorator│
├───────────────┤         ├─────────────────┤
│ + operation() │         │ + operation()   │
└───────────────┘         │ + addedBehavior()│
                         └─────────────────┘
```

## 装饰器模式的实现

### 1. 基本实现

```java
// 组件接口
interface Component {
    void operation();
}

// 具体组件
class ConcreteComponent implements Component {
    public void operation() {
        System.out.println("ConcreteComponent operation");
    }
}

// 装饰器抽象类
abstract class Decorator implements Component {
    protected Component component;
    
    public Decorator(Component component) {
        this.component = component;
    }
    
    public void operation() {
        component.operation();
    }
}

// 具体装饰器 A
class ConcreteDecoratorA extends Decorator {
    public ConcreteDecoratorA(Component component) {
        super(component);
    }
    
    public void operation() {
        super.operation();
        addedBehavior();
    }
    
    private void addedBehavior() {
        System.out.println("ConcreteDecoratorA added behavior");
    }
}

// 具体装饰器 B
class ConcreteDecoratorB extends Decorator {
    public ConcreteDecoratorB(Component component) {
        super(component);
    }
    
    public void operation() {
        super.operation();
        addedBehavior();
    }
    
    private void addedBehavior() {
        System.out.println("ConcreteDecoratorB added behavior");
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Component component = new ConcreteComponent();
        component.operation();
        
        Component decoratorA = new ConcreteDecoratorA(component);
        decoratorA.operation();
        
        Component decoratorB = new ConcreteDecoratorB(decoratorA);
        decoratorB.operation();
    }
}
```

### 2. 咖啡店示例

```java
// 饮料接口
interface Beverage {
    String getDescription();
    double cost();
}

// 浓缩咖啡
class Espresso implements Beverage {
    public String getDescription() {
        return "Espresso";
    }
    
    public double cost() {
        return 1.99;
    }
}

// 深烘焙咖啡
class DarkRoast implements Beverage {
    public String getDescription() {
        return "Dark Roast Coffee";
    }
    
    public double cost() {
        return 0.99;
    }
}

// 调料装饰器
abstract class CondimentDecorator implements Beverage {
    protected Beverage beverage;
    
    public CondimentDecorator(Beverage beverage) {
        this.beverage = beverage;
    }
    
    public abstract String getDescription();
}

// 摩卡调料
class Mocha extends CondimentDecorator {
    public Mocha(Beverage beverage) {
        super(beverage);
    }
    
    public String getDescription() {
        return beverage.getDescription() + ", Mocha";
    }
    
    public double cost() {
        return beverage.cost() + 0.20;
    }
}

// 奶泡调料
class Whip extends CondimentDecorator {
    public Whip(Beverage beverage) {
        super(beverage);
    }
    
    public String getDescription() {
        return beverage.getDescription() + ", Whip";
    }
    
    public double cost() {
        return beverage.cost() + 0.10;
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Beverage beverage = new Espresso();
        System.out.println(beverage.getDescription() + " $" + beverage.cost());
        
        Beverage beverage2 = new DarkRoast();
        beverage2 = new Mocha(beverage2);
        beverage2 = new Mocha(beverage2);
        beverage2 = new Whip(beverage2);
        System.out.println(beverage2.getDescription() + " $" + beverage2.cost());
    }
}
```

### 3. IO 流示例

```java
import java.io.*;

// 自定义装饰器：大写转换装饰器
class UpperCaseInputStream extends FilterInputStream {
    
    protected UpperCaseInputStream(InputStream in) {
        super(in);
    }
    
    @Override
    public int read() throws IOException {
        int c = super.read();
        return (c == -1) ? c : Character.toUpperCase(c);
    }
    
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int result = super.read(b, off, len);
        for (int i = off; i < off + result; i++) {
            b[i] = (byte) Character.toUpperCase(b[i]);
        }
        return result;
    }
}

// 客户端
public class Client {
    public static void main(String[] args) throws IOException {
        int c;
        try (InputStream in = new UpperCaseInputStream(
                new BufferedInputStream(
                        new FileInputStream("test.txt")))) {
            while ((c = in.read()) >= 0) {
                System.out.print((char) c);
            }
        }
    }
}
```

## 装饰器模式的优缺点

### 优点

1. **符合开闭原则**：装饰器模式符合开闭原则，可以在不修改原有代码的情况下扩展对象的功能。

2. **继承的替代方案**：装饰器模式提供了比继承更灵活的扩展对象功能的方式。

3. **动态扩展**：装饰器模式可以动态地扩展对象的功能，而不需要在编译时确定。

4. **单一职责**：装饰器模式将每个装饰器封装在一个独立的类中，符合单一职责原则。

5. **灵活组合**：装饰器模式可以灵活地组合多个装饰器，实现不同的功能组合。

### 缺点

1. **增加复杂度**：装饰器模式会增加类的数量，增加了系统的复杂度。

2. **多层嵌套**：装饰器模式可能会导致多层嵌套，使得代码难以理解和调试。

3. **灵活性带来的问题**：装饰器模式的灵活性也带来了一些问题，比如装饰器的顺序可能会影响最终的结果。

## 装饰器模式的使用场景

### 1. 图形界面组件

```java
// 组件接口
interface Component {
    void draw();
}

// 文本框
class TextBox implements Component {
    public void draw() {
        System.out.println("Drawing TextBox");
    }
}

// 滚动条装饰器
class ScrollBarDecorator implements Component {
    private Component component;
    
    public ScrollBarDecorator(Component component) {
        this.component = component;
    }
    
    public void draw() {
        component.draw();
        System.out.println("Adding ScrollBar");
    }
}

// 边框装饰器
class BorderDecorator implements Component {
    private Component component;
    
    public BorderDecorator(Component component) {
        this.component = component;
    }
    
    public void draw() {
        component.draw();
        System.out.println("Adding Border");
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Component textBox = new TextBox();
        textBox.draw();
        
        Component scrollBarTextBox = new ScrollBarDecorator(textBox);
        scrollBarTextBox.draw();
        
        Component borderScrollBarTextBox = new BorderDecorator(scrollBarTextBox);
        borderScrollBarTextBox.draw();
    }
}
```

### 2. 数据压缩

```java
// 数据源接口
interface DataSource {
    void writeData(String data);
    String readData();
}

// 文件数据源
class FileDataSource implements DataSource {
    private String filename;
    
    public FileDataSource(String filename) {
        this.filename = filename;
    }
    
    public void writeData(String data) {
        System.out.println("Writing data to file: " + filename);
    }
    
    public String readData() {
        System.out.println("Reading data from file: " + filename);
        return "data";
    }
}

// 压缩装饰器
class CompressionDecorator implements DataSource {
    private DataSource dataSource;
    
    public CompressionDecorator(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void writeData(String data) {
        System.out.println("Compressing data");
        dataSource.writeData(data);
    }
    
    public String readData() {
        String data = dataSource.readData();
        System.out.println("Decompressing data");
        return data;
    }
}

// 加密装饰器
class EncryptionDecorator implements DataSource {
    private DataSource dataSource;
    
    public EncryptionDecorator(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void writeData(String data) {
        System.out.println("Encrypting data");
        dataSource.writeData(data);
    }
    
    public String readData() {
        String data = dataSource.readData();
        System.out.println("Decrypting data");
        return data;
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        DataSource source = new FileDataSource("data.txt");
        source.writeData("Hello, World!");
        source.readData();
        
        DataSource compressedSource = new CompressionDecorator(source);
        compressedSource.writeData("Hello, World!");
        compressedSource.readData();
        
        DataSource encryptedCompressedSource = new EncryptionDecorator(compressedSource);
        encryptedCompressedSource.writeData("Hello, World!");
        encryptedCompressedSource.readData();
    }
}
```

### 3. 日志记录

```java
// 服务接口
interface Service {
    void execute();
}

// 具体服务
class ConcreteService implements Service {
    public void execute() {
        System.out.println("Executing service");
    }
}

// 日志装饰器
class LoggingDecorator implements Service {
    private Service service;
    
    public LoggingDecorator(Service service) {
        this.service = service;
    }
    
    public void execute() {
        System.out.println("Before execute");
        service.execute();
        System.out.println("After execute");
    }
}

// 性能监控装饰器
class PerformanceDecorator implements Service {
    private Service service;
    
    public PerformanceDecorator(Service service) {
        this.service = service;
    }
    
    public void execute() {
        long startTime = System.currentTimeMillis();
        service.execute();
        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Service service = new ConcreteService();
        service.execute();
        
        Service loggingService = new LoggingDecorator(service);
        loggingService.execute();
        
        Service performanceLoggingService = new PerformanceDecorator(loggingService);
        performanceLoggingService.execute();
    }
}
```

### 4. 缓存装饰器

```java
// 数据访问接口
interface DataAccessor {
    String getData(String key);
    void setData(String key, String value);
}

// 数据库访问器
class DatabaseAccessor implements DataAccessor {
    public String getData(String key) {
        System.out.println("Getting data from database: " + key);
        return "value";
    }
    
    public void setData(String key, String value) {
        System.out.println("Setting data to database: " + key);
    }
}

// 缓存装饰器
class CacheDecorator implements DataAccessor {
    private DataAccessor dataAccessor;
    private Map<String, String> cache = new HashMap<>();
    
    public CacheDecorator(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }
    
    public String getData(String key) {
        if (cache.containsKey(key)) {
            System.out.println("Getting data from cache: " + key);
            return cache.get(key);
        } else {
            String value = dataAccessor.getData(key);
            cache.put(key, value);
            return value;
        }
    }
    
    public void setData(String key, String value) {
        dataAccessor.setData(key, value);
        cache.put(key, value);
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        DataAccessor accessor = new DatabaseAccessor();
        accessor.getData("key1");
        accessor.getData("key1");
        
        DataAccessor cachedAccessor = new CacheDecorator(accessor);
        cachedAccessor.getData("key1");
        cachedAccessor.getData("key1");
    }
}
```

## 装饰器模式的注意事项

### 1. 装饰器的顺序

装饰器的顺序可能会影响最终的结果，需要注意装饰器的顺序。

```java
// 错误的装饰器顺序
Component component = new ConcreteDecoratorB(new ConcreteDecoratorA(new ConcreteComponent()));

// 正确的装饰器顺序
Component component = new ConcreteDecoratorA(new ConcreteDecoratorB(new ConcreteComponent()));
```

### 2. 装饰器的组合

装饰器可以灵活地组合，但需要注意装饰器的组合方式。

```java
// 单一装饰器
Component component = new ConcreteDecoratorA(new ConcreteComponent());

// 多个装饰器
Component component = new ConcreteDecoratorA(
    new ConcreteDecoratorB(
        new ConcreteDecoratorC(
            new ConcreteComponent()
        )
    )
);
```

### 3. 装饰器的接口

装饰器需要实现与组件相同的接口，否则会导致类型不匹配。

```java
// 组件接口
interface Component {
    void operation();
}

// 装饰器
class Decorator implements Component {
    protected Component component;
    
    public Decorator(Component component) {
        this.component = component;
    }
    
    public void operation() {
        component.operation();
    }
}
```

## 装饰器模式的最佳实践

### 1. 保持接口简单

装饰器模式需要保持接口简单，避免接口过于复杂。

```java
// 简单的接口
interface Component {
    void operation();
}

// 复杂的接口（不推荐）
interface Component {
    void operation1();
    void operation2();
    void operation3();
    void operation4();
}
```

### 2. 使用抽象装饰器

使用抽象装饰器可以简化具体装饰器的实现。

```java
// 抽象装饰器
abstract class Decorator implements Component {
    protected Component component;
    
    public Decorator(Component component) {
        this.component = component;
    }
    
    public void operation() {
        component.operation();
    }
}

// 具体装饰器
class ConcreteDecorator extends Decorator {
    public ConcreteDecorator(Component component) {
        super(component);
    }
    
    public void operation() {
        super.operation();
        addedBehavior();
    }
    
    private void addedBehavior() {
        System.out.println("Added behavior");
    }
}
```

### 3. 使用建造者模式创建装饰器

使用建造者模式可以简化装饰器的创建过程。

```java
class ComponentBuilder {
    private Component component;
    
    public ComponentBuilder(Component component) {
        this.component = component;
    }
    
    public ComponentBuilder addDecoratorA() {
        component = new ConcreteDecoratorA(component);
        return this;
    }
    
    public ComponentBuilder addDecoratorB() {
        component = new ConcreteDecoratorB(component);
        return this;
    }
    
    public Component build() {
        return component;
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Component component = new ComponentBuilder(new ConcreteComponent())
            .addDecoratorA()
            .addDecoratorB()
            .build();
        component.operation();
    }
}
```

## 装饰器模式与其他模式的区别

### 1. 装饰器模式 vs 代理模式

装饰器模式和代理模式都使用组合来包装对象，但它们的目的是不同的。装饰器模式的目的是动态地扩展对象的功能，而代理模式的目的是控制对对象的访问。

### 2. 装饰器模式 vs 适配器模式

装饰器模式和适配器模式都使用组合来包装对象，但它们的目的是不同的。装饰器模式的目的是动态地扩展对象的功能，而适配器模式的目的是将一个类的接口转换成客户希望的另一个接口。

### 3. 装饰器模式 vs 外观模式

装饰器模式和外观模式都使用组合来包装对象，但它们的目的是不同的。装饰器模式的目的是动态地扩展对象的功能，而外观模式的目的是为子系统中的一组接口提供一个一致的界面。

## 总结

装饰器模式是一种结构型设计模式，它允许在不改变现有对象结构的情况下，动态地给对象添加额外的职责。装饰器模式通过创建一个包装对象，也就是装饰器来包裹真实的对象，从而在不改变原类文件和使用继承的情况下，动态地扩展一个对象的功能。装饰器模式的优点包括符合开闭原则、继承的替代方案、动态扩展、单一职责和灵活组合。装饰器模式的缺点包括增加复杂度、多层嵌套和灵活性带来的问题。装饰器模式的使用场景包括图形界面组件、数据压缩、日志记录和缓存装饰器。装饰器模式的注意事项包括装饰器的顺序、装饰器的组合和装饰器的接口。装饰器模式的最佳实践包括保持接口简单、使用抽象装饰器和使用建造者模式创建装饰器。装饰器模式与其他模式的区别包括装饰器模式 vs 代理模式、装饰器模式 vs 适配器模式和装饰器模式 vs 外观模式。