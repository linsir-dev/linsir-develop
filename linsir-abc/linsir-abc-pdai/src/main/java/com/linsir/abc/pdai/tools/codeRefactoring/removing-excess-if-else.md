# 如何去除多余的if else？

## 概述

if else语句是编程中最基本的控制流结构之一，但过多的if else嵌套会导致代码变得复杂、难以理解和维护。本文将介绍多种方法来去除或简化多余的if else语句，提高代码的可读性和可维护性。

## 1. 策略模式

### 适用场景

当有多个条件分支，每个分支执行不同的算法或行为时，适合使用策略模式。

### 传统实现

```java
public class PaymentService {
    
    public void processPayment(String paymentType, double amount) {
        if ("CREDIT_CARD".equals(paymentType)) {
            // 信用卡支付逻辑
            System.out.println("Processing credit card payment: " + amount);
        } else if ("PAYPAL".equals(paymentType)) {
            // PayPal支付逻辑
            System.out.println("Processing PayPal payment: " + amount);
        } else if ("ALIPAY".equals(paymentType)) {
            // 支付宝支付逻辑
            System.out.println("Processing Alipay payment: " + amount);
        } else {
            throw new IllegalArgumentException("Invalid payment type: " + paymentType);
        }
    }
}
```

### 策略模式实现

```java
// 1. 定义策略接口
public interface PaymentStrategy {
    void process(double amount);
}

// 2. 实现具体策略
public class CreditCardPayment implements PaymentStrategy {
    @Override
    public void process(double amount) {
        System.out.println("Processing credit card payment: " + amount);
    }
}

public class PayPalPayment implements PaymentStrategy {
    @Override
    public void process(double amount) {
        System.out.println("Processing PayPal payment: " + amount);
    }
}

public class AlipayPayment implements PaymentStrategy {
    @Override
    public void process(double amount) {
        System.out.println("Processing Alipay payment: " + amount);
    }
}

// 3. 使用策略工厂
public class PaymentStrategyFactory {
    private static final Map<String, PaymentStrategy> strategies = new HashMap<>();
    
    static {
        strategies.put("CREDIT_CARD", new CreditCardPayment());
        strategies.put("PAYPAL", new PayPalPayment());
        strategies.put("ALIPAY", new AlipayPayment());
    }
    
    public static PaymentStrategy getStrategy(String paymentType) {
        PaymentStrategy strategy = strategies.get(paymentType);
        if (strategy == null) {
            throw new IllegalArgumentException("Invalid payment type: " + paymentType);
        }
        return strategy;
    }
}

// 4. 使用策略
public class PaymentService {
    
    public void processPayment(String paymentType, double amount) {
        PaymentStrategy strategy = PaymentStrategyFactory.getStrategy(paymentType);
        strategy.process(amount);
    }
}
```

### 优势

- 消除了大量的if else语句
- 符合开闭原则，新增支付方式时只需添加新的策略实现
- 代码结构清晰，职责分明

## 2. 状态模式

### 适用场景

当对象的行为取决于其状态，并且状态可以在运行时改变时，适合使用状态模式。

### 传统实现

```java
public class OrderService {
    
    public void processOrder(String status, Order order) {
        if ("PENDING".equals(status)) {
            // 处理待付款订单
            System.out.println("Processing pending order: " + order.getId());
        } else if ("PAID".equals(status)) {
            // 处理已付款订单
            System.out.println("Processing paid order: " + order.getId());
        } else if ("SHIPPED".equals(status)) {
            // 处理已发货订单
            System.out.println("Processing shipped order: " + order.getId());
        } else if ("DELIVERED".equals(status)) {
            // 处理已送达订单
            System.out.println("Processing delivered order: " + order.getId());
        } else {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
    }
}
```

### 状态模式实现

```java
// 1. 定义状态接口
public interface OrderState {
    void process(Order order);
}

// 2. 实现具体状态
public class PendingState implements OrderState {
    @Override
    public void process(Order order) {
        System.out.println("Processing pending order: " + order.getId());
    }
}

public class PaidState implements OrderState {
    @Override
    public void process(Order order) {
        System.out.println("Processing paid order: " + order.getId());
    }
}

public class ShippedState implements OrderState {
    @Override
    public void process(Order order) {
        System.out.println("Processing shipped order: " + order.getId());
    }
}

public class DeliveredState implements OrderState {
    @Override
    public void process(Order order) {
        System.out.println("Processing delivered order: " + order.getId());
    }
}

// 3. 状态上下文
public class OrderContext {
    private static final Map<String, OrderState> states = new HashMap<>();
    
    static {
        states.put("PENDING", new PendingState());
        states.put("PAID", new PaidState());
        states.put("SHIPPED", new ShippedState());
        states.put("DELIVERED", new DeliveredState());
    }
    
    public void processOrder(String status, Order order) {
        OrderState state = states.get(status);
        if (state == null) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
        state.process(order);
    }
}

// 4. 使用
public class OrderService {
    
    private OrderContext orderContext = new OrderContext();
    
    public void processOrder(String status, Order order) {
        orderContext.processOrder(status, order);
    }
}
```

### 优势

- 消除了状态判断的if else语句
- 状态转换逻辑清晰
- 符合开闭原则，新增状态时只需添加新的状态实现

## 3. 命令模式

### 适用场景

当需要将请求封装为对象，以便使用不同的请求参数化客户端时，适合使用命令模式。

### 传统实现

```java
public class CommandExecutor {
    
    public void executeCommand(String command, String[] args) {
        if ("CREATE".equals(command)) {
            // 执行创建命令
            System.out.println("Executing create command with args: " + Arrays.toString(args));
        } else if ("UPDATE".equals(command)) {
            // 执行更新命令
            System.out.println("Executing update command with args: " + Arrays.toString(args));
        } else if ("DELETE".equals(command)) {
            // 执行删除命令
            System.out.println("Executing delete command with args: " + Arrays.toString(args));
        } else if ("READ".equals(command)) {
            // 执行读取命令
            System.out.println("Executing read command with args: " + Arrays.toString(args));
        } else {
            throw new IllegalArgumentException("Invalid command: " + command);
        }
    }
}
```

### 命令模式实现

```java
// 1. 定义命令接口
public interface Command {
    void execute(String[] args);
}

// 2. 实现具体命令
public class CreateCommand implements Command {
    @Override
    public void execute(String[] args) {
        System.out.println("Executing create command with args: " + Arrays.toString(args));
    }
}

public class UpdateCommand implements Command {
    @Override
    public void execute(String[] args) {
        System.out.println("Executing update command with args: " + Arrays.toString(args));
    }
}

public class DeleteCommand implements Command {
    @Override
    public void execute(String[] args) {
        System.out.println("Executing delete command with args: " + Arrays.toString(args));
    }
}

public class ReadCommand implements Command {
    @Override
    public void execute(String[] args) {
        System.out.println("Executing read command with args: " + Arrays.toString(args));
    }
}

// 3. 命令注册表
public class CommandRegistry {
    private static final Map<String, Command> commands = new HashMap<>();
    
    static {
        commands.put("CREATE", new CreateCommand());
        commands.put("UPDATE", new UpdateCommand());
        commands.put("DELETE", new DeleteCommand());
        commands.put("READ", new ReadCommand());
    }
    
    public static Command getCommand(String commandName) {
        Command command = commands.get(commandName);
        if (command == null) {
            throw new IllegalArgumentException("Invalid command: " + commandName);
        }
        return command;
    }
}

// 4. 使用命令
public class CommandExecutor {
    
    public void executeCommand(String command, String[] args) {
        Command cmd = CommandRegistry.getCommand(command);
        cmd.execute(args);
    }
}
```

### 优势

- 消除了命令判断的if else语句
- 命令与执行分离，易于扩展
- 支持命令队列和撤销操作

## 4. 工厂方法模式

### 适用场景

当需要根据不同条件创建不同类型的对象时，适合使用工厂方法模式。

### 传统实现

```java
public class ProductFactory {
    
    public Product createProduct(String type) {
        if ("ELECTRONICS".equals(type)) {
            return new ElectronicsProduct();
        } else if ("CLOTHING".equals(type)) {
            return new ClothingProduct();
        } else if ("FOOD".equals(type)) {
            return new FoodProduct();
        } else {
            throw new IllegalArgumentException("Invalid product type: " + type);
        }
    }
}
```

### 工厂方法模式实现

```java
// 1. 产品接口
public interface Product {
    void produce();
}

// 2. 具体产品
public class ElectronicsProduct implements Product {
    @Override
    public void produce() {
        System.out.println("Producing electronics product");
    }
}

public class ClothingProduct implements Product {
    @Override
    public void produce() {
        System.out.println("Producing clothing product");
    }
}

public class FoodProduct implements Product {
    @Override
    public void produce() {
        System.out.println("Producing food product");
    }
}

// 3. 工厂接口
public interface ProductFactory {
    Product createProduct();
}

// 4. 具体工厂
public class ElectronicsFactory implements ProductFactory {
    @Override
    public Product createProduct() {
        return new ElectronicsProduct();
    }
}

public class ClothingFactory implements ProductFactory {
    @Override
    public Product createProduct() {
        return new ClothingProduct();
    }
}

public class FoodFactory implements ProductFactory {
    @Override
    public Product createProduct() {
        return new FoodProduct();
    }
}

// 5. 工厂注册表
public class FactoryRegistry {
    private static final Map<String, ProductFactory> factories = new HashMap<>();
    
    static {
        factories.put("ELECTRONICS", new ElectronicsFactory());
        factories.put("CLOTHING", new ClothingFactory());
        factories.put("FOOD", new FoodFactory());
    }
    
    public static Product createProduct(String type) {
        ProductFactory factory = factories.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("Invalid product type: " + type);
        }
        return factory.createProduct();
    }
}

// 6. 使用
public class Client {
    public static void main(String[] args) {
        Product product = FactoryRegistry.createProduct("ELECTRONICS");
        product.produce();
    }
}
```

### 优势

- 消除了对象创建的if else语句
- 符合开闭原则，新增产品类型时只需添加新的产品和工厂实现
- 产品创建逻辑集中管理

## 5. 枚举映射

### 适用场景

当条件分支基于有限的常量值时，适合使用枚举映射。

### 传统实现

```java
public class DiscountCalculator {
    
    public double calculateDiscount(String customerType, double amount) {
        if ("REGULAR".equals(customerType)) {
            return amount * 0.05;
        } else if ("PREMIUM".equals(customerType)) {
            return amount * 0.10;
        } else if ("VIP".equals(customerType)) {
            return amount * 0.15;
        } else {
            throw new IllegalArgumentException("Invalid customer type: " + customerType);
        }
    }
}
```

### 枚举映射实现

```java
// 1. 定义枚举
public enum CustomerType {
    REGULAR(0.05),
    PREMIUM(0.10),
    VIP(0.15);
    
    private final double discountRate;
    
    CustomerType(double discountRate) {
        this.discountRate = discountRate;
    }
    
    public double getDiscountRate() {
        return discountRate;
    }
}

// 2. 使用枚举
public class DiscountCalculator {
    
    public double calculateDiscount(String customerType, double amount) {
        CustomerType type = CustomerType.valueOf(customerType);
        return amount * type.getDiscountRate();
    }
}
```

### 优势

- 代码简洁，消除了if else语句
- 类型安全，避免了字符串常量的错误
- 枚举值集中管理，易于维护

## 6. 函数式编程

### 适用场景

当使用Java 8+时，可以利用函数式编程特性简化if else语句。

### 传统实现

```java
public class StringProcessor {
    
    public String process(String input, String operation) {
        if ("UPPERCASE".equals(operation)) {
            return input.toUpperCase();
        } else if ("LOWERCASE".equals(operation)) {
            return input.toLowerCase();
        } else if ("TRIM".equals(operation)) {
            return input.trim();
        } else {
            throw new IllegalArgumentException("Invalid operation: " + operation);
        }
    }
}
```

### 函数式编程实现

```java
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class StringProcessor {
    
    private static final Map<String, Function<String, String>> operations = new HashMap<>();
    
    static {
        operations.put("UPPERCASE", String::toUpperCase);
        operations.put("LOWERCASE", String::toLowerCase);
        operations.put("TRIM", String::trim);
    }
    
    public String process(String input, String operation) {
        Function<String, String> function = operations.get(operation);
        if (function == null) {
            throw new IllegalArgumentException("Invalid operation: " + operation);
        }
        return function.apply(input);
    }
}
```

### 优势

- 代码简洁，使用Lambda表达式
- 易于扩展，只需添加新的函数映射
- 符合函数式编程风格

## 7. 卫语句

### 适用场景

当有多个条件需要检查，每个条件都可能提前返回时，适合使用卫语句。

### 传统实现

```java
public class UserValidator {
    
    public boolean validate(User user) {
        if (user != null) {
            if (user.getUsername() != null) {
                if (user.getUsername().length() >= 3) {
                    if (user.getEmail() != null) {
                        if (user.getEmail().contains("@")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
```

### 卫语句实现

```java
public class UserValidator {
    
    public boolean validate(User user) {
        if (user == null) return false;
        if (user.getUsername() == null) return false;
        if (user.getUsername().length() < 3) return false;
        if (user.getEmail() == null) return false;
        if (!user.getEmail().contains("@")) return false;
        return true;
    }
}
```

### 优势

- 消除了嵌套的if语句，代码更扁平
- 逻辑清晰，每个条件检查都有明确的返回
- 易于理解和维护

## 8. 表驱动法

### 适用场景

当有大量的条件分支，且每个分支执行的逻辑相似时，适合使用表驱动法。

### 传统实现

```java
public class TaxCalculator {
    
    public double calculateTax(String country, double amount) {
        if ("USA".equals(country)) {
            return amount * 0.08;
        } else if ("CHINA".equals(country)) {
            return amount * 0.10;
        } else if ("JAPAN".equals(country)) {
            return amount * 0.05;
        } else if ("GERMANY".equals(country)) {
            return amount * 0.19;
        } else if ("FRANCE".equals(country)) {
            return amount * 0.20;
        } else {
            throw new IllegalArgumentException("Invalid country: " + country);
        }
    }
}
```

### 表驱动法实现

```java
public class TaxCalculator {
    
    private static final Map<String, Double> taxRates = new HashMap<>();
    
    static {
        taxRates.put("USA", 0.08);
        taxRates.put("CHINA", 0.10);
        taxRates.put("JAPAN", 0.05);
        taxRates.put("GERMANY", 0.19);
        taxRates.put("FRANCE", 0.20);
    }
    
    public double calculateTax(String country, double amount) {
        Double rate = taxRates.get(country);
        if (rate == null) {
            throw new IllegalArgumentException("Invalid country: " + country);
        }
        return amount * rate;
    }
}
```

### 优势

- 消除了大量的if else语句
- 数据与逻辑分离，易于维护和扩展
- 查找效率高，特别适合大量条件的场景

## 9. 多态

### 适用场景

当有不同类型的对象需要执行不同的行为时，适合使用多态。

### 传统实现

```java
public class ShapeDrawer {
    
    public void draw(String shapeType) {
        if ("CIRCLE".equals(shapeType)) {
            System.out.println("Drawing circle");
        } else if ("RECTANGLE".equals(shapeType)) {
            System.out.println("Drawing rectangle");
        } else if ("TRIANGLE".equals(shapeType)) {
            System.out.println("Drawing triangle");
        } else {
            throw new IllegalArgumentException("Invalid shape type: " + shapeType);
        }
    }
}
```

### 多态实现

```java
// 1. 抽象基类
public abstract class Shape {
    public abstract void draw();
}

// 2. 具体实现
public class Circle extends Shape {
    @Override
    public void draw() {
        System.out.println("Drawing circle");
    }
}

public class Rectangle extends Shape {
    @Override
    public void draw() {
        System.out.println("Drawing rectangle");
    }
}

public class Triangle extends Shape {
    @Override
    public void draw() {
        System.out.println("Drawing triangle");
    }
}

// 3. 形状工厂
public class ShapeFactory {
    private static final Map<String, Shape> shapes = new HashMap<>();
    
    static {
        shapes.put("CIRCLE", new Circle());
        shapes.put("RECTANGLE", new Rectangle());
        shapes.put("TRIANGLE", new Triangle());
    }
    
    public static Shape createShape(String shapeType) {
        Shape shape = shapes.get(shapeType);
        if (shape == null) {
            throw new IllegalArgumentException("Invalid shape type: " + shapeType);
        }
        return shape;
    }
}

// 4. 使用
public class ShapeDrawer {
    
    public void draw(String shapeType) {
        Shape shape = ShapeFactory.createShape(shapeType);
        shape.draw();
    }
}
```

### 优势

- 消除了类型判断的if else语句
- 利用多态实现行为的差异化
- 符合开闭原则，新增形状时只需添加新的实现类

## 10. 模板方法模式

### 适用场景

当有多个算法，它们的结构相似但具体实现不同时，适合使用模板方法模式。

### 传统实现

```java
public class DataProcessor {
    
    public void process(String dataType, String data) {
        if ("XML".equals(dataType)) {
            // 解析XML
            System.out.println("Parsing XML: " + data);
            // 验证XML
            System.out.println("Validating XML");
            // 处理XML
            System.out.println("Processing XML data");
        } else if ("JSON".equals(dataType)) {
            // 解析JSON
            System.out.println("Parsing JSON: " + data);
            // 验证JSON
            System.out.println("Validating JSON");
            // 处理JSON
            System.out.println("Processing JSON data");
        } else if ("CSV".equals(dataType)) {
            // 解析CSV
            System.out.println("Parsing CSV: " + data);
            // 验证CSV
            System.out.println("Validating CSV");
            // 处理CSV
            System.out.println("Processing CSV data");
        } else {
            throw new IllegalArgumentException("Invalid data type: " + dataType);
        }
    }
}
```

### 模板方法模式实现

```java
// 1. 抽象模板类
public abstract class DataProcessorTemplate {
    
    // 模板方法
    public final void process(String data) {
        parse(data);
        validate();
        processData();
    }
    
    // 抽象方法
    protected abstract void parse(String data);
    protected abstract void validate();
    protected abstract void processData();
}

// 2. 具体实现
public class XmlProcessor extends DataProcessorTemplate {
    
    @Override
    protected void parse(String data) {
        System.out.println("Parsing XML: " + data);
    }
    
    @Override
    protected void validate() {
        System.out.println("Validating XML");
    }
    
    @Override
    protected void processData() {
        System.out.println("Processing XML data");
    }
}

public class JsonProcessor extends DataProcessorTemplate {
    
    @Override
    protected void parse(String data) {
        System.out.println("Parsing JSON: " + data);
    }
    
    @Override
    protected void validate() {
        System.out.println("Validating JSON");
    }
    
    @Override
    protected void processData() {
        System.out.println("Processing JSON data");
    }
}

public class CsvProcessor extends DataProcessorTemplate {
    
    @Override
    protected void parse(String data) {
        System.out.println("Parsing CSV: " + data);
    }
    
    @Override
    protected void validate() {
        System.out.println("Validating CSV");
    }
    
    @Override
    protected void processData() {
        System.out.println("Processing CSV data");
    }
}

// 3. 处理器工厂
public class DataProcessorFactory {
    private static final Map<String, DataProcessorTemplate> processors = new HashMap<>();
    
    static {
        processors.put("XML", new XmlProcessor());
        processors.put("JSON", new JsonProcessor());
        processors.put("CSV", new CsvProcessor());
    }
    
    public static DataProcessorTemplate getProcessor(String dataType) {
        DataProcessorTemplate processor = processors.get(dataType);
        if (processor == null) {
            throw new IllegalArgumentException("Invalid data type: " + dataType);
        }
        return processor;
    }
}

// 4. 使用
public class DataProcessor {
    
    public void process(String dataType, String data) {
        DataProcessorTemplate processor = DataProcessorFactory.getProcessor(dataType);
        processor.process(data);
    }
}
```

### 优势

- 消除了算法选择的if else语句
- 封装了算法的不变部分，扩展了可变部分
- 符合开闭原则，新增数据类型时只需添加新的处理器实现

## 11. 规则引擎

### 适用场景

当有复杂的业务规则需要处理时，适合使用规则引擎。

### 传统实现

```java
public class LoanApprovalService {
    
    public boolean approveLoan(LoanApplication application) {
        if (application.getCreditScore() < 600) {
            return false;
        } else if (application.getIncome() < 3000) {
            return false;
        } else if (application.getLoanAmount() > application.getIncome() * 5) {
            return false;
        } else if (application.getEmploymentYears() < 2) {
            return false;
        } else {
            return true;
        }
    }
}
```

### 规则引擎实现

```java
// 1. 规则接口
public interface LoanRule {
    boolean evaluate(LoanApplication application);
    String getMessage();
}

// 2. 具体规则
public class CreditScoreRule implements LoanRule {
    @Override
    public boolean evaluate(LoanApplication application) {
        return application.getCreditScore() >= 600;
    }
    
    @Override
    public String getMessage() {
        return "Credit score is too low";
    }
}

public class IncomeRule implements LoanRule {
    @Override
    public boolean evaluate(LoanApplication application) {
        return application.getIncome() >= 3000;
    }
    
    @Override
    public String getMessage() {
        return "Income is too low";
    }
}

public class LoanAmountRule implements LoanRule {
    @Override
    public boolean evaluate(LoanApplication application) {
        return application.getLoanAmount() <= application.getIncome() * 5;
    }
    
    @Override
    public String getMessage() {
        return "Loan amount is too high";
    }
}

public class EmploymentRule implements LoanRule {
    @Override
    public boolean evaluate(LoanApplication application) {
        return application.getEmploymentYears() >= 2;
    }
    
    @Override
    public String getMessage() {
        return "Employment years are too short";
    }
}

// 3. 规则引擎
public class LoanRuleEngine {
    private List<LoanRule> rules = new ArrayList<>();
    
    public LoanRuleEngine() {
        rules.add(new CreditScoreRule());
        rules.add(new IncomeRule());
        rules.add(new LoanAmountRule());
        rules.add(new EmploymentRule());
    }
    
    public boolean approveLoan(LoanApplication application) {
        for (LoanRule rule : rules) {
            if (!rule.evaluate(application)) {
                System.out.println(rule.getMessage());
                return false;
            }
        }
        return true;
    }
}

// 4. 使用
public class LoanApprovalService {
    
    private LoanRuleEngine ruleEngine = new LoanRuleEngine();
    
    public boolean approveLoan(LoanApplication application) {
        return ruleEngine.approveLoan(application);
    }
}
```

### 优势

- 消除了复杂的业务规则if else语句
- 规则可以动态添加和修改
- 规则逻辑清晰，易于维护

## 12. 组合模式

### 适用场景

当需要处理对象的树形结构，并且希望客户端能够统一处理单个对象和对象组合时，适合使用组合模式。

### 传统实现

```java
public class FileSystem {
    
    public void process(String path) {
        if (path.endsWith(".txt")) {
            // 处理文本文件
            System.out.println("Processing text file: " + path);
        } else if (path.endsWith(".jpg") || path.endsWith(".png")) {
            // 处理图片文件
            System.out.println("Processing image file: " + path);
        } else if (path.endsWith("/")) {
            // 处理目录
            System.out.println("Processing directory: " + path);
        } else {
            throw new IllegalArgumentException("Invalid path: " + path);
        }
    }
}
```

### 组合模式实现

```java
// 1. 组件接口
public interface FileSystemComponent {
    void process();
}

// 2. 叶节点
public class File implements FileSystemComponent {
    private String name;
    private String extension;
    
    public File(String name, String extension) {
        this.name = name;
        this.extension = extension;
    }
    
    @Override
    public void process() {
        if (".txt".equals(extension)) {
            System.out.println("Processing text file: " + name + extension);
        } else if (".jpg".equals(extension) || ".png".equals(extension)) {
            System.out.println("Processing image file: " + name + extension);
        }
    }
}

// 3. 组合节点
public class Directory implements FileSystemComponent {
    private String name;
    private List<FileSystemComponent> components = new ArrayList<>();
    
    public Directory(String name) {
        this.name = name;
    }
    
    public void addComponent(FileSystemComponent component) {
        components.add(component);
    }
    
    @Override
    public void process() {
        System.out.println("Processing directory: " + name);
        for (FileSystemComponent component : components) {
            component.process();
        }
    }
}

// 4. 使用
public class FileSystem {
    
    public void process(FileSystemComponent component) {
        component.process();
    }
}
```

### 优势

- 消除了文件类型判断的if else语句
- 统一处理单个文件和目录
- 支持复杂的文件系统结构

## 总结

去除多余的if else语句的方法有很多，选择哪种方法取决于具体的场景：

| 方法 | 适用场景 | 优势 |
|------|----------|------|
| 策略模式 | 多个条件分支执行不同算法 | 符合开闭原则，易于扩展 |
| 状态模式 | 对象行为取决于状态 | 状态转换清晰，易于维护 |
| 命令模式 | 将请求封装为对象 | 命令与执行分离，支持撤销 |
| 工厂方法模式 | 根据条件创建不同对象 | 对象创建逻辑集中，易于扩展 |
| 枚举映射 | 基于有限常量值的分支 | 类型安全，代码简洁 |
| 函数式编程 | Java 8+的函数式特性 | 代码简洁，表达力强 |
| 卫语句 | 多个条件检查提前返回 | 代码扁平，逻辑清晰 |
| 表驱动法 | 大量相似的条件分支 | 数据与逻辑分离，易于维护 |
| 多态 | 不同类型对象的不同行为 | 利用多态实现行为差异化 |
| 模板方法模式 | 结构相似的算法 | 封装不变部分，扩展可变部分 |
| 规则引擎 | 复杂的业务规则 | 规则可动态配置，易于维护 |
| 组合模式 | 处理树形结构 | 统一处理单个对象和组合 |

在实际开发中，应该根据具体的业务场景选择合适的方法，有时也可以结合多种方法来达到最佳效果。去除多余的if else语句不仅可以提高代码的可读性和可维护性，还可以使代码更加优雅和高效。
