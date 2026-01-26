# 什么是设计模式？

## 设计模式的定义

设计模式（Design Pattern）是一套被反复使用、多数人知晓的、经过分类编目的、代码设计经验的总结。使用设计模式是为了可重用代码、让代码更容易被他人理解、保证代码可靠性。

## 设计模式的起源

设计模式的概念最早由 Christopher Alexander 在 1977 年提出的，用于建筑领域。1994 年，Erich Gamma、Richard Helm、Ralph Johnson 和 John Vlissides（被称为 GoF，Gang of Four）将设计模式的概念引入到软件工程领域，出版了《设计模式：可复用面向对象软件的基础》一书，提出了 23 种经典设计模式。

## 设计模式的分类

### 1. 创建型模式（Creational Patterns）

创建型模式关注对象的创建过程，将对象的创建和使用分离：

- 单例模式（Singleton）
- 工厂方法模式（Factory Method）
- 抽象工厂模式（Abstract Factory）
- 建造者模式（Builder）
- 原型模式（Prototype）

### 2. 结构型模式（Structural Patterns）

结构型模式关注类和对象的组合，通过继承或组合来构建更大的结构：

- 适配器模式（Adapter）
- 桥接模式（Bridge）
- 组合模式（Composite）
- 装饰器模式（Decorator）
- 外观模式（Facade）
- 享元模式（Flyweight）
- 代理模式（Proxy）

### 3. 行为型模式（Behavioral Patterns）

行为型模式关注对象之间的通信和职责分配：

- 策略模式（Strategy）
- 模板方法模式（Template Method）
- 观察者模式（Observer）
- 迭代器模式（Iterator）
- 责任链模式（Chain of Responsibility）
- 命令模式（Command）
- 备忘录模式（Memento）
- 状态模式（State）
- 访问者模式（Visitor）
- 中介者模式（Mediator）
- 解释器模式（Interpreter）

## 设计模式的六大原则

### 1. 单一职责原则（Single Responsibility Principle，SRP）

一个类只负责一项职责。

```java
// 违反单一职责原则
class UserService {
    public void saveUser(User user) {
        // 保存用户
    }
    
    public void sendEmail(User user) {
        // 发送邮件
    }
}

// 遵循单一职责原则
class UserService {
    public void saveUser(User user) {
        // 保存用户
    }
}

class EmailService {
    public void sendEmail(User user) {
        // 发送邮件
    }
}
```

### 2. 开闭原则（Open-Closed Principle，OCP）

软件实体应该对扩展开放，对修改关闭。

```java
// 违反开闭原则
class PaymentService {
    public void pay(String type, double amount) {
        if (type.equals("alipay")) {
            // 支付宝支付
        } else if (type.equals("wechat")) {
            // 微信支付
        }
    }
}

// 遵循开闭原则
interface Payment {
    void pay(double amount);
}

class AlipayPayment implements Payment {
    public void pay(double amount) {
        // 支付宝支付
    }
}

class WechatPayment implements Payment {
    public void pay(double amount) {
        // 微信支付
    }
}

class PaymentService {
    public void pay(Payment payment, double amount) {
        payment.pay(amount);
    }
}
```

### 3. 里氏替换原则（Liskov Substitution Principle，LSP）

子类可以替换父类出现在父类能够出现的任何地方。

```java
// 违反里氏替换原则
class Rectangle {
    protected double width;
    protected double height;
    
    public void setWidth(double width) {
        this.width = width;
    }
    
    public void setHeight(double height) {
        this.height = height;
    }
    
    public double getArea() {
        return width * height;
    }
}

class Square extends Rectangle {
    public void setWidth(double width) {
        this.width = width;
        this.height = width;
    }
    
    public void setHeight(double height) {
        this.width = height;
        this.height = height;
    }
}

// 遵循里氏替换原则
interface Shape {
    double getArea();
}

class Rectangle implements Shape {
    protected double width;
    protected double height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    public double getArea() {
        return width * height;
    }
}

class Square implements Shape {
    private double side;
    
    public Square(double side) {
        this.side = side;
    }
    
    public double getArea() {
        return side * side;
    }
}
```

### 4. 接口隔离原则（Interface Segregation Principle，ISP）

使用多个专门的接口，而不是使用单一的总接口。

```java
// 违反接口隔离原则
interface UserService {
    void addUser(User user);
    void deleteUser(User user);
    void updateUser(User user);
    void getUser(Long id);
    void sendEmail(User user);
    void sendSms(User user);
}

// 遵循接口隔离原则
interface UserService {
    void addUser(User user);
    void deleteUser(User user);
    void updateUser(User user);
    void getUser(Long id);
}

interface NotificationService {
    void sendEmail(User user);
    void sendSms(User user);
}
```

### 5. 依赖倒置原则（Dependency Inversion Principle，DIP）

高层模块不应该依赖低层模块，两者都应该依赖其抽象。

```java
// 违反依赖倒置原则
class UserService {
    private MySQLUserRepository userRepository;
    
    public UserService() {
        this.userRepository = new MySQLUserRepository();
    }
    
    public void saveUser(User user) {
        userRepository.save(user);
    }
}

// 遵循依赖倒置原则
interface UserRepository {
    void save(User user);
}

class MySQLUserRepository implements UserRepository {
    public void save(User user) {
        // MySQL 保存逻辑
    }
}

class UserService {
    private UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
```

### 6. 迪米特法则（Law of Demeter，LoD）

一个对象应该对其他对象有尽可能少的了解。

```java
// 违反迪米特法则
class OrderService {
    public void processOrder(Order order) {
        User user = order.getUser();
        Address address = user.getAddress();
        String city = address.getCity();
        // 处理订单
    }
}

// 遵循迪米特法则
class OrderService {
    public void processOrder(Order order) {
        String city = order.getUserCity();
        // 处理订单
    }
}

class Order {
    private User user;
    
    public String getUserCity() {
        return user.getAddress().getCity();
    }
}
```

## 设计模式的优点

### 1. 提高代码复用性

设计模式是经过验证的解决方案，可以在多个项目中复用。

### 2. 提高代码可维护性

设计模式使代码结构清晰，易于理解和维护。

### 3. 提高代码可扩展性

设计模式遵循开闭原则，易于扩展新功能。

### 4. 提高代码可靠性

设计模式是经过实践验证的解决方案，减少了出错的可能性。

### 5. 提高团队协作效率

设计模式提供了一种通用的语言，团队成员可以快速理解代码。

## 设计模式的缺点

### 1. 增加代码复杂度

过度使用设计模式会增加代码的复杂度，降低代码的可读性。

### 2. 增加学习成本

设计模式需要学习和理解，增加了开发人员的学习成本。

### 3. 可能导致过度设计

为了使用设计模式而使用设计模式，可能导致过度设计。

### 4. 可能影响性能

某些设计模式会增加额外的对象创建和方法调用，可能影响性能。

## 设计模式的使用场景

### 1. 需要复用代码

当需要在多个项目中复用代码时，可以使用设计模式。

### 2. 需要提高代码质量

当需要提高代码的可维护性、可扩展性和可靠性时，可以使用设计模式。

### 3. 需要解决特定问题

当遇到特定的问题时，可以使用相应的设计模式来解决问题。

### 4. 需要提高团队协作效率

当需要提高团队协作效率时，可以使用设计模式作为通用的语言。

## 设计模式的学习路径

### 1. 初学者

- 学习设计模式的基本概念
- 学习设计模式的六大原则
- 学习常用的设计模式（单例、工厂、观察者、策略等）

### 2. 进阶者

- 深入理解设计模式的原理
- 学习设计模式的适用场景
- 学习设计模式的优缺点
- 在实际项目中应用设计模式

### 3. 高级者

- 掌握所有 23 种经典设计模式
- 理解设计模式的底层原理
- 能够根据实际情况选择合适的设计模式
- 能够创造新的设计模式

## 总结

设计模式是一套被反复使用、多数人知晓的、经过分类编目的、代码设计经验的总结。设计模式分为创建型模式、结构型模式和行为型模式三大类。设计模式遵循六大原则：单一职责原则、开闭原则、里氏替换原则、接口隔离原则、依赖倒置原则和迪米特法则。设计模式的优点包括提高代码复用性、可维护性、可扩展性、可靠性和团队协作效率。设计模式的缺点包括增加代码复杂度、学习成本、可能导致过度设计和影响性能。设计模式的使用场景包括需要复用代码、提高代码质量、解决特定问题和提高团队协作效率。学习设计模式需要循序渐进，从基本概念到深入理解，再到实际应用。