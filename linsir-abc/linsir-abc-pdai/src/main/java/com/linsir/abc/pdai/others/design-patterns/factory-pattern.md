# 工厂模式

## 工厂模式的定义

工厂模式（Factory Pattern）是一种创建型设计模式，它提供了一种创建对象的最佳方式。在工厂模式中，我们在创建对象时不会对客户端暴露创建逻辑，并且是通过使用一个共同的接口来指向新创建的对象。

工厂模式主要分为三种：
1. 简单工厂模式（Simple Factory）
2. 工厂方法模式（Factory Method）
3. 抽象工厂模式（Abstract Factory）

## 1. 简单工厂模式

### 1.1 简单工厂模式的定义

简单工厂模式（Simple Factory Pattern）又称为静态工厂方法模式，它属于类创建型模式。在简单工厂模式中，可以根据参数的不同返回不同类的实例。简单工厂模式专门定义一个类来负责创建其他类的实例，被创建的实例通常都具有共同的父类。

### 1.2 简单工厂模式的结构

```
┌─────────────────┐         ┌─────────────────┐
│    Factory      │────────>│    Product      │
├─────────────────┤         ├─────────────────┤
│ + create(type)  │         │ + operation()   │
└─────────────────┘         └─────────────────┘
                                    △
                                    │
                    ┌───────────────┼───────────────┐
                    │               │               │
        ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
        │ ConcreteProductA│ │ ConcreteProductB│ │ ConcreteProductC│
        ├─────────────────┤ ├─────────────────┤ ├─────────────────┤
        │ + operation()   │ │ + operation()   │ │ + operation()   │
        └─────────────────┘ └─────────────────┘ └─────────────────┘
```

### 1.3 简单工厂模式的实现

```java
// 产品接口
interface Product {
    void operation();
}

// 具体产品 A
class ConcreteProductA implements Product {
    public void operation() {
        System.out.println("ConcreteProductA operation");
    }
}

// 具体产品 B
class ConcreteProductB implements Product {
    public void operation() {
        System.out.println("ConcreteProductB operation");
    }
}

// 简单工厂
class SimpleFactory {
    public static Product createProduct(String type) {
        if (type.equals("A")) {
            return new ConcreteProductA();
        } else if (type.equals("B")) {
            return new ConcreteProductB();
        }
        return null;
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Product productA = SimpleFactory.createProduct("A");
        productA.operation();
        
        Product productB = SimpleFactory.createProduct("B");
        productB.operation();
    }
}
```

### 1.4 简单工厂模式的优缺点

**优点**：
- 工厂类包含必要的逻辑判断，可以决定在什么时候创建哪一个产品类的实例，客户端可以免除直接创建产品对象的责任，而仅仅"消费"产品
- 通过引入配置文件，可以在不修改任何客户端代码的情况下更换和增加新的具体产品类，在一定程度上提高了系统的灵活性

**缺点**：
- 工厂类集中了所有产品的创建逻辑，职责过重，一旦不能正常工作，整个系统都要受到影响
- 增加系统中类的个数（引入了新的工厂类），增加了系统的复杂度和理解难度
- 系统扩展困难，一旦添加新产品就不得不修改工厂逻辑，在产品类型较多时，有可能造成工厂逻辑过于复杂，不利于系统的扩展和维护

## 2. 工厂方法模式

### 2.1 工厂方法模式的定义

工厂方法模式（Factory Method Pattern）又称为工厂模式，也叫虚拟构造器模式或多态工厂模式。在工厂方法模式中，工厂父类负责定义创建产品对象的公共接口，而工厂子类则负责生成具体的产品对象，这样做的目的是将产品类的实例化操作延迟到工厂子类中完成，即通过工厂子类来确定究竟应该实例化哪一个具体产品类。

### 2.2 工厂方法模式的结构

```
┌─────────────────┐         ┌─────────────────┐
│   Factory       │────────>│    Product      │
├─────────────────┤         ├─────────────────┤
│ + create()       │         │ + operation()   │
└─────────────────┘         └─────────────────┘
        △                           △
        │                           │
┌───────────────┐         ┌─────────────────┐
│ConcreteFactory│         │ConcreteProduct │
├───────────────┤         ├─────────────────┤
│ + create()    │         │ + operation()   │
└───────────────┘         └─────────────────┘
```

### 2.3 工厂方法模式的实现

```java
// 产品接口
interface Product {
    void operation();
}

// 具体产品 A
class ConcreteProductA implements Product {
    public void operation() {
        System.out.println("ConcreteProductA operation");
    }
}

// 具体产品 B
class ConcreteProductB implements Product {
    public void operation() {
        System.out.println("ConcreteProductB operation");
    }
}

// 工厂接口
interface Factory {
    Product create();
}

// 具体工厂 A
class ConcreteFactoryA implements Factory {
    public Product create() {
        return new ConcreteProductA();
    }
}

// 具体工厂 B
class ConcreteFactoryB implements Factory {
    public Product create() {
        return new ConcreteProductB();
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Factory factoryA = new ConcreteFactoryA();
        Product productA = factoryA.create();
        productA.operation();
        
        Factory factoryB = new ConcreteFactoryB();
        Product productB = factoryB.create();
        productB.operation();
    }
}
```

### 2.4 工厂方法模式的优缺点

**优点**：
- 在工厂方法模式中，工厂方法用来创建客户所需要的产品，同时还向客户隐藏了哪种具体产品类将被实例化这一细节，用户只需要关心所需产品对应的工厂，无须关心创建细节，甚至无须知道具体产品类的类名
- 基于工厂角色和产品角色的多态性设计是工厂方法模式的关键。它能够使工厂可以自主确定创建何种产品对象，而如何创建这个对象的细节则完全封装在具体工厂内部
- 在系统中加入新产品时，无须修改抽象工厂和抽象产品提供的接口，也无须修改客户端，也无须修改其他的具体工厂和具体产品，而只要添加一个具体工厂和具体产品就可以了，这样，系统的可扩展性也就变得非常好，完全符合"开闭原则"

**缺点**：
- 在添加新产品时，需要编写新的具体产品类，而且还要提供与之对应的具体工厂类，系统中类的个数将成对增加，在一定程度上增加了系统的复杂度，有更多的类需要编译和运行，会给系统带来一些额外的开销
- 由于考虑到系统的可扩展性，需要引入抽象层，在客户端代码中均使用抽象层进行定义，增加了系统的抽象性和理解难度

## 3. 抽象工厂模式

### 3.1 抽象工厂模式的定义

抽象工厂模式（Abstract Factory Pattern）是一种创建型设计模式，它提供了一种方式，可以将一组具有同一主题的单独的工厂封装起来。在正常使用中，客户端程序需要创建抽象工厂的具体实现，然后使用抽象工厂作为接口来创建这一主题的具体对象。客户端程序不需要知道（或关心）它从这些内部的工厂方法中获得对象的具体类型，因为客户端程序仅将这些产品视为抽象的"产品"。

### 3.2 抽象工厂模式的结构

```
┌─────────────────┐         ┌─────────────────┐
│AbstractFactory  │────────>│AbstractProductA│
├─────────────────┤         ├─────────────────┤
│ + createA()     │         │ + operation()   │
│ + createB()     │         └─────────────────┘
└─────────────────┘                  △
        △                            │
        │                ┌───────────┴───────────┐
┌───────────────┐        │                       │
│ConcreteFactory│┌─────────────────┐ ┌─────────────────┐
├───────────────┤│ConcreteProductA1│ │ConcreteProductA2│
│ + createA()   │├─────────────────┤ ├─────────────────┤
│ + createB()   ││ + operation()   │ │ + operation()   │
└───────────────┘└─────────────────┘ └─────────────────┘
        △
        │
┌─────────────────┐         ┌─────────────────┐
│AbstractProductB │         │ConcreteProductB1│
├─────────────────┤         ├─────────────────┤
│ + operation()   │         │ + operation()   │
└─────────────────┘         └─────────────────┘
        △
        │
┌─────────────────┐
│ConcreteProductB2│
├─────────────────┤
│ + operation()   │
└─────────────────┘
```

### 3.3 抽象工厂模式的实现

```java
// 抽象产品 A
interface AbstractProductA {
    void operation();
}

// 具体产品 A1
class ConcreteProductA1 implements AbstractProductA {
    public void operation() {
        System.out.println("ConcreteProductA1 operation");
    }
}

// 具体产品 A2
class ConcreteProductA2 implements AbstractProductA {
    public void operation() {
        System.out.println("ConcreteProductA2 operation");
    }
}

// 抽象产品 B
interface AbstractProductB {
    void operation();
}

// 具体产品 B1
class ConcreteProductB1 implements AbstractProductB {
    public void operation() {
        System.out.println("ConcreteProductB1 operation");
    }
}

// 具体产品 B2
class ConcreteProductB2 implements AbstractProductB {
    public void operation() {
        System.out.println("ConcreteProductB2 operation");
    }
}

// 抽象工厂
interface AbstractFactory {
    AbstractProductA createProductA();
    AbstractProductB createProductB();
}

// 具体工厂 1
class ConcreteFactory1 implements AbstractFactory {
    public AbstractProductA createProductA() {
        return new ConcreteProductA1();
    }
    
    public AbstractProductB createProductB() {
        return new ConcreteProductB1();
    }
}

// 具体工厂 2
class ConcreteFactory2 implements AbstractFactory {
    public AbstractProductA createProductA() {
        return new ConcreteProductA2();
    }
    
    public AbstractProductB createProductB() {
        return new ConcreteProductB2();
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        AbstractFactory factory1 = new ConcreteFactory1();
        AbstractProductA productA1 = factory1.createProductA();
        AbstractProductB productB1 = factory1.createProductB();
        productA1.operation();
        productB1.operation();
        
        AbstractFactory factory2 = new ConcreteFactory2();
        AbstractProductA productA2 = factory2.createProductA();
        AbstractProductB productB2 = factory2.createProductB();
        productA2.operation();
        productB2.operation();
    }
}
```

### 3.4 抽象工厂模式的优缺点

**优点**：
- 抽象工厂模式隔离了具体类的生成，使得客户并不需要知道什么被创建。由于这种隔离，更换一个具体工厂就变得相对容易，所有的具体工厂都实现了抽象工厂中定义的那些公共接口，因此只需改变具体工厂的实例，就可以在某种程度上改变整个软件系统的行为
- 当一个族中的多个对象被设计成一起工作时，它能够保证客户端始终只使用同一个族中的对象
- 增加新的具体工厂和产品族很方便，无须修改已有系统，符合"开闭原则"

**缺点**：
- 在添加新的产品对象时，难以扩展抽象工厂来生产新种类的产品，这是因为在抽象工厂角色中规定了所有可能被创建的产品集合，要支持新种类的产品就意味着要对该接口进行扩展，而这将涉及到对抽象工厂角色及其所有子类的修改，显然会带来较大的不便
- 开闭原则的倾斜性（增加新的工厂和产品族容易，增加新的产品等级结构麻烦）

## 工厂模式的使用场景

### 1. 简单工厂模式的使用场景

- 工厂类负责创建的对象比较少，由于创建的对象较少，不会造成工厂方法中的业务逻辑太过复杂
- 客户端只知道传入工厂类的参数，对于如何创建对象不关心

### 2. 工厂方法模式的使用场景

- 客户端不需要知道它所创建的对象的类
- 客户端可以通过子类来指定创建对应的对象

### 3. 抽象工厂模式的使用场景

- 一个系统不应当依赖于产品类实例如何被创建、组合和表达的细节，这对于所有形态的工厂模式都是重要的
- 系统中有多于一个的产品族，而每次只使用其中某一产品族
- 属于同一个产品族的产品将在一起使用，这一约束必须在系统的设计中体现出来
- 系统提供一个产品类的库，所有的产品以同样的接口出现，从而使客户端不依赖于实现

## 工厂模式的实际应用

### 1. 日志记录器

```java
// 日志记录器接口
interface Logger {
    void log(String message);
}

// 文件日志记录器
class FileLogger implements Logger {
    public void log(String message) {
        System.out.println("File Logger: " + message);
    }
}

// 数据库日志记录器
class DatabaseLogger implements Logger {
    public void log(String message) {
        System.out.println("Database Logger: " + message);
    }
}

// 日志记录器工厂
interface LoggerFactory {
    Logger createLogger();
}

// 文件日志记录器工厂
class FileLoggerFactory implements LoggerFactory {
    public Logger createLogger() {
        return new FileLogger();
    }
}

// 数据库日志记录器工厂
class DatabaseLoggerFactory implements LoggerFactory {
    public Logger createLogger() {
        return new DatabaseLogger();
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        LoggerFactory factory = new FileLoggerFactory();
        Logger logger = factory.createLogger();
        logger.log("This is a log message");
    }
}
```

### 2. 数据库连接

```java
// 数据库连接接口
interface Connection {
    void connect();
    void disconnect();
}

// MySQL 连接
class MySQLConnection implements Connection {
    public void connect() {
        System.out.println("MySQL Connected");
    }
    
    public void disconnect() {
        System.out.println("MySQL Disconnected");
    }
}

// PostgreSQL 连接
class PostgreSQLConnection implements Connection {
    public void connect() {
        System.out.println("PostgreSQL Connected");
    }
    
    public void disconnect() {
        System.out.println("PostgreSQL Disconnected");
    }
}

// 数据库连接工厂
interface ConnectionFactory {
    Connection createConnection();
}

// MySQL 连接工厂
class MySQLConnectionFactory implements ConnectionFactory {
    public Connection createConnection() {
        return new MySQLConnection();
    }
}

// PostgreSQL 连接工厂
class PostgreSQLConnectionFactory implements ConnectionFactory {
    public Connection createConnection() {
        return new PostgreSQLConnection();
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        ConnectionFactory factory = new MySQLConnectionFactory();
        Connection connection = factory.createConnection();
        connection.connect();
        connection.disconnect();
    }
}
```

## 总结

工厂模式是一种创建型设计模式，它提供了一种创建对象的最佳方式。工厂模式主要分为三种：简单工厂模式、工厂方法模式和抽象工厂模式。简单工厂模式又称为静态工厂方法模式，它可以根据参数的不同返回不同类的实例。工厂方法模式又称为工厂模式，它定义一个用于创建对象的接口，让子类决定实例化哪一个类。抽象工厂模式提供了一种方式，可以将一组具有同一主题的单独的工厂封装起来。工厂模式的优点包括解耦对象的创建和使用、符合开闭原则、提高系统的灵活性和可扩展性。工厂模式的缺点包括增加系统的复杂度、增加类的个数、难以扩展新的产品等级结构。工厂模式的使用场景包括日志记录器、数据库连接、配置管理器等。