# 什么是合成/聚合复用原则？

## 合成/聚合复用原则的定义

合成/聚合复用原则（Composition/Aggregation Reuse Principle）是面向对象编程中的一个重要原则，它强调通过合成/聚合来实现代码复用，而不是通过继承。合成/聚合复用原则指出，"优先使用合成/聚合，而不是继承"（Favor Composition over Inheritance）。

## 合成/聚合复用原则的核心概念

### 1. 合成（Composition）

合成是指将一个对象作为另一个对象的成员变量，通过组合多个对象来构建更复杂的对象。合成是一种"has-a"关系。

#### 合成的特点

- **"has-a"关系**：合成表示"有一个"关系
- **运行时组合**：合成在运行时确定
- **灵活组合**：可以动态地组合对象
- **黑盒复用**：复用对象的实现细节

#### 合成的示例

```java
// 合成示例
class Engine {
    public void start() {
        System.out.println("Engine started");
    }
}

class Wheel {
    public void rotate() {
        System.out.println("Wheel rotating");
    }
}

class Car {
    private Engine engine;
    private List<Wheel> wheels;
    
    public Car(Engine engine, List<Wheel> wheels) {
        this.engine = engine;
        this.wheels = wheels;
    }
    
    public void start() {
        engine.start();
        for (Wheel wheel : wheels) {
            wheel.rotate();
        }
    }
}

// Car通过合成Engine和Wheel来实现功能，而不是继承
```

### 2. 聚合（Aggregation）

聚合是合成的一种特殊形式，它表示一种"整体-部分"关系。聚合强调整体对象的生命周期独立于部分对象的生命周期。

#### 聚合的特点

- **"整体-部分"关系**：聚合表示"整体-部分"关系
- **生命周期独立**：整体对象和部分对象的生命周期独立
- **弱耦合**：整体对象和部分对象之间的耦合较弱
- **可替换性**：部分对象可以独立替换

#### 聚合的示例

```java
// 聚合示例
class Department {
    private String name;
    private List<Employee> employees;
    
    public Department(String name) {
        this.name = name;
        this.employees = new ArrayList<>();
    }
    
    public void addEmployee(Employee employee) {
        employees.add(employee);
    }
    
    public void removeEmployee(Employee employee) {
        employees.remove(employee);
    }
    
    public List<Employee> getEmployees() {
        return employees;
    }
}

class Employee {
    private String name;
    
    public Employee(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}

// Department通过聚合Employee来实现功能
// Department和Employee的生命周期独立
```

## 合成/聚合复用原则与继承的区别

### 1. 继承的局限性

继承是一种"is-a"关系，它存在一些局限性：

#### 继承的问题

- **紧耦合**：继承导致类之间的紧耦合
- **脆弱性**：基类的变化会影响所有子类
- **不灵活**：继承在编译时确定，运行时无法改变
- **爆炸性继承**：多层继承导致类层次结构复杂

#### 继承的示例

```java
// 继承示例
class Animal {
    public void eat() {
        System.out.println("Animal eating");
    }
}

class Dog extends Animal {
    @Override
    public void eat() {
        System.out.println("Dog eating");
    }
    
    public void bark() {
        System.out.println("Dog barking");
    }
}

class Cat extends Animal {
    @Override
    public void eat() {
        System.out.println("Cat eating");
    }
    
    public void meow() {
        System.out.println("Cat meowing");
    }
}

// 问题：如果Animal的eat方法发生变化，会影响所有子类
```

### 2. 合成/聚合的优势

合成/聚合复用原则相比继承有许多优势：

#### 合成/聚合的优点

- **松耦合**：合成/聚合导致类之间的松耦合
- **灵活性高**：合成/聚合在运行时确定，可以动态组合
- **易于维护**：修改一个类不会影响其他类
- **可测试性强**：可以独立测试每个类
- **代码复用**：通过组合多个对象来实现代码复用

#### 合成/聚合的示例

```java
// 合成/聚合示例
interface Flyable {
    void fly();
}

class Bird implements Flyable {
    @Override
    public void fly() {
        System.out.println("Bird flying");
    }
}

class Airplane implements Flyable {
    @Override
    public void fly() {
        System.out.println("Airplane flying");
    }
}

class SuperHero {
    private Flyable flyable;
    
    public SuperHero(Flyable flyable) {
        this.flyable = flyable;
    }
    
    public void fly() {
        flyable.fly();
    }
}

// SuperHero通过合成Flyable来实现飞行功能
// 可以在运行时动态切换Bird或Airplane
```

## 合成/聚合复用原则的实现模式

### 1. 策略模式（Strategy Pattern）

策略模式使用合成来实现算法的动态切换。

#### 策略模式的特点

- **算法封装**：将算法封装在独立的类中
- **动态切换**：可以在运行时动态切换算法
- **易于扩展**：可以轻松添加新的算法

#### 策略模式的实现

```java
// 策略模式示例
interface SortStrategy {
    void sort(int[] array);
}

class BubbleSort implements SortStrategy {
    @Override
    public void sort(int[] array) {
        // 冒泡排序实现
    }
}

class QuickSort implements SortStrategy {
    @Override
    public void sort(int[] array) {
        // 快速排序实现
    }
}

class Sorter {
    private SortStrategy sortStrategy;
    
    public Sorter(SortStrategy sortStrategy) {
        this.sortStrategy = sortStrategy;
    }
    
    public void sort(int[] array) {
        sortStrategy.sort(array);
    }
    
    public void setSortStrategy(SortStrategy sortStrategy) {
        this.sortStrategy = sortStrategy;
    }
}

// Sorter通过合成SortStrategy来实现排序功能
```

### 2. 装饰器模式（Decorator Pattern）

装饰器模式使用合成来动态地添加对象的功能。

#### 装饰器模式的特点

- **功能扩展**：可以动态地添加对象的功能
- **不修改类**：不修改原始类的代码
- **灵活组合**：可以灵活地组合多个装饰器

#### 装饰器模式的实现

```java
// 装饰器模式示例
interface Coffee {
    double cost();
    String description();
}

class SimpleCoffee implements Coffee {
    @Override
    public double cost() {
        return 1.0;
    }
    
    @Override
    public String description() {
        return "Simple Coffee";
    }
}

class MilkDecorator implements Coffee {
    private Coffee coffee;
    
    public MilkDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
    
    @Override
    public double cost() {
        return coffee.cost() + 0.5;
    }
    
    @Override
    public String description() {
        return coffee.description() + ", Milk";
    }
}

class SugarDecorator implements Coffee {
    private Coffee coffee;
    
    public SugarDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
    
    @Override
    public double cost() {
        return coffee.cost() + 0.2;
    }
    
    @Override
    public String description() {
        return coffee.description() + ", Sugar";
    }
}

// 装饰器通过合成Coffee来动态添加功能
```

### 3. 组合模式（Composite Pattern）

组合模式使用合成来构建树形结构。

#### 组合模式的特点

- **树形结构**：构建树形结构
- **统一处理**：统一处理单个对象和组合对象
- **递归遍历**：可以递归遍历树形结构

#### 组合模式的实现

```java
// 组合模式示例
interface FileSystemNode {
    void print();
    void add(FileSystemNode node);
    void remove(FileSystemNode node);
}

class File implements FileSystemNode {
    private String name;
    
    public File(String name) {
        this.name = name;
    }
    
    @Override
    public void print() {
        System.out.println("File: " + name);
    }
    
    @Override
    public void add(FileSystemNode node) {
        throw new UnsupportedOperationException("Cannot add to file");
    }
    
    @Override
    public void remove(FileSystemNode node) {
        throw new UnsupportedOperationException("Cannot remove from file");
    }
}

class Directory implements FileSystemNode {
    private String name;
    private List<FileSystemNode> children;
    
    public Directory(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }
    
    @Override
    public void print() {
        System.out.println("Directory: " + name);
        for (FileSystemNode child : children) {
            child.print();
        }
    }
    
    @Override
    public void add(FileSystemNode node) {
        children.add(node);
    }
    
    @Override
    public void remove(FileSystemNode node) {
        children.remove(node);
    }
}

// Directory通过合成FileSystemNode来构建树形结构
```

## 合成/聚合复用原则的应用场景

### 1. 用户界面开发

在用户界面开发中，合成/聚合复用原则广泛应用：

#### 用户界面的应用

- **组件组合**：通过组合多个组件来构建复杂的用户界面
- **布局管理**：通过合成不同的布局来管理用户界面
- **事件处理**：通过合成不同的事件处理器来处理用户交互

#### 用户界面的示例

```java
// 用户界面示例
class Button {
    private String label;
    
    public Button(String label) {
        this.label = label;
    }
    
    public void click() {
        System.out.println("Button " + label + " clicked");
    }
}

class TextField {
    private String text;
    
    public TextField(String text) {
        this.text = text;
    }
    
    public void input(String text) {
        this.text = text;
    }
}

class Form {
    private List<Button> buttons;
    private List<TextField> textFields;
    
    public Form() {
        this.buttons = new ArrayList<>();
        this.textFields = new ArrayList<>();
    }
    
    public void addButton(Button button) {
        buttons.add(button);
    }
    
    public void addTextField(TextField textField) {
        textFields.add(textField);
    }
    
    public void render() {
        for (TextField textField : textFields) {
            System.out.println("TextField: " + textField.getText());
        }
        for (Button button : buttons) {
            button.click();
        }
    }
}

// Form通过合成Button和TextField来构建用户界面
```

### 2. 游戏开发

在游戏开发中，合成/聚合复用原则广泛应用：

#### 游戏的应用

- **角色组合**：通过组合不同的角色来构建游戏角色
- **装备系统**：通过组合不同的装备来构建装备系统
- **技能系统**：通过组合不同的技能来构建技能系统

#### 游戏的示例

```java
// 游戏示例
interface Weapon {
    void attack();
    void defend();
}

class Sword implements Weapon {
    @Override
    public void attack() {
        System.out.println("Sword attacking");
    }
    
    @Override
    public void defend() {
        System.out.println("Sword defending");
    }
}

class Shield implements Weapon {
    @Override
    public void attack() {
        System.out.println("Shield attacking");
    }
    
    @Override
    public void defend() {
        System.out.println("Shield defending");
    }
}

class Character {
    private String name;
    private List<Weapon> weapons;
    
    public Character(String name) {
        this.name = name;
        this.weapons = new ArrayList<>();
    }
    
    public void addWeapon(Weapon weapon) {
        weapons.add(weapon);
    }
    
    public void attack() {
        System.out.println(name + " attacking");
        for (Weapon weapon : weapons) {
            weapon.attack();
        }
    }
    
    public void defend() {
        System.out.println(name + " defending");
        for (Weapon weapon : weapons) {
            weapon.defend();
        }
    }
}

// Character通过合成Weapon来构建角色系统
```

### 3. 业务系统开发

在业务系统开发中，合成/聚合复用原则广泛应用：

#### 业务系统的应用

- **订单系统**：通过组合不同的订单项来构建订单
- **报表系统**：通过组合不同的报表项来构建报表
- **工作流系统**：通过组合不同的工作流步骤来构建工作流

#### 业务系统的示例

```java
// 业务系统示例
class OrderItem {
    private String name;
    private double price;
    private int quantity;
    
    public OrderItem(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    
    public double getTotal() {
        return price * quantity;
    }
}

class Order {
    private String orderId;
    private List<OrderItem> items;
    
    public Order(String orderId) {
        this.orderId = orderId;
        this.items = new ArrayList<>();
    }
    
    public void addItem(OrderItem item) {
        items.add(item);
    }
    
    public double getTotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getTotal();
        }
        return total;
    }
}

// Order通过合成OrderItem来构建订单系统
```

## 合成/聚合复用原则的优缺点

### 1. 优点

- **松耦合**：合成/聚合导致类之间的松耦合
- **灵活性高**：合成/聚合在运行时确定，可以动态组合
- **易于维护**：修改一个类不会影响其他类
- **可测试性强**：可以独立测试每个类
- **代码复用**：通过组合多个对象来实现代码复用
- **易于扩展**：可以轻松添加新的功能

### 2. 缺点

- **复杂度增加**：需要设计更多的类和接口
- **对象数量增加**：可能增加对象的数量
- **性能开销**：合成/聚合可能带来一定的性能开销
- **设计难度**：需要仔细设计合成/聚合关系

## 合成/聚合复用原则的注意事项

### 1. 识别"has-a"关系

正确识别"has-a"关系是应用合成/聚合复用原则的关键：

#### "has-a"关系的判断

- **整体-部分**：如果A是B的一部分，则使用合成
- **拥有关系**：如果A拥有B，则使用合成
- **包含关系**：如果A包含B，则使用合成

#### "has-a"关系的示例

```java
// "has-a"关系示例
class Engine {
    public void start() {
        System.out.println("Engine started");
    }
}

class Car {
    private Engine engine;
    
    public Car(Engine engine) {
        this.engine = engine;
    }
    
    public void start() {
        engine.start();
    }
}

// Car"has-a"Engine，使用合成
```

### 2. 识别"is-a"关系

正确识别"is-a"关系是避免滥用继承的关键：

#### "is-a"关系的判断

- **继承关系**：如果A是B的一种，则使用继承
- **类型关系**：如果A是B的子类型，则使用继承
- **行为关系**：如果A的行为与B相同，则使用继承

#### "is-a"关系的示例

```java
// "is-a"关系示例
class Animal {
    public void eat() {
        System.out.println("Animal eating");
    }
}

class Dog extends Animal {
    @Override
    public void eat() {
        System.out.println("Dog eating");
    }
    
    public void bark() {
        System.out.println("Dog barking");
    }
}

// Dog"is-a"Animal，使用继承
```

### 3. 避免过度合成

避免过度合成，保持设计的简洁性：

#### 过度合成的问题

- **不必要的组合**：组合了不必要的对象
- **复杂度增加**：增加了系统的复杂度
- **维护困难**：过度合成导致维护困难

#### 避免过度合成的方法

- **按需组合**：根据实际需求组合对象
- **简化设计**：保持设计的简洁性
- **重构优化**：定期重构优化合成关系

## 合成/聚合复用原则的最佳实践

### 1. 设计接口

设计良好的接口来支持合成/聚合复用：

#### 接口设计的原则

- **接口细化**：将大接口拆分为多个小接口
- **按需依赖**：客户端只依赖它需要的接口
- **高内聚**：每个接口的方法高度相关

#### 接口设计的示例

```java
// 接口设计示例
interface Flyable {
    void fly();
}

interface Swimmable {
    void swim();
}

class Bird implements Flyable, Swimmable {
    @Override
    public void fly() {
        System.out.println("Bird flying");
    }
    
    @Override
    public void swim() {
        System.out.println("Bird swimming");
    }
}

class Fish implements Flyable, Swimmable {
    @Override
    public void fly() {
        throw new UnsupportedOperationException("Fish cannot fly");
    }
    
    @Override
    public void swim() {
        System.out.println("Fish swimming");
    }
}

// 通过细化接口来支持合成/聚合复用
```

### 2. 使用依赖注入

使用依赖注入来实现合成/聚合复用：

#### 依赖注入的优势

- **松耦合**：降低类之间的耦合
- **易于测试**：可以轻松地替换依赖
- **灵活性高**：可以灵活地配置依赖

#### 依赖注入的示例

```java
// 依赖注入示例
interface Engine {
    void start();
}

class GasEngine implements Engine {
    @Override
    public void start() {
        System.out.println("Gas engine started");
    }
}

class ElectricEngine implements Engine {
    @Override
    public void start() {
        System.out.println("Electric engine started");
    }
}

class Car {
    private Engine engine;
    
    public Car(Engine engine) {
        this.engine = engine;
    }
    
    public void start() {
        engine.start();
    }
}

// 通过依赖注入来配置Engine
```

### 3. 使用建造者模式

使用建造者模式来构建复杂的合成对象：

#### 建造者模式的优势

- **分步构建**：可以分步构建复杂的对象
- **灵活性高**：可以灵活地配置对象的构建过程
- **易于扩展**：可以轻松添加新的构建步骤

#### 建造者模式的示例

```java
// 建造者模式示例
class Computer {
    private String cpu;
    private String ram;
    private String storage;
    
    public void setCpu(String cpu) {
        this.cpu = cpu;
    }
    
    public void setRam(String ram) {
        this.ram = ram;
    }
    
    public void setStorage(String storage) {
        this.storage = storage;
    }
    
    public void print() {
        System.out.println("Computer: " + cpu + ", " + ram + ", " + storage);
    }
}

class ComputerBuilder {
    private Computer computer;
    
    public ComputerBuilder() {
        this.computer = new Computer();
    }
    
    public ComputerBuilder setCpu(String cpu) {
        computer.setCpu(cpu);
        return this;
    }
    
    public ComputerBuilder setRam(String ram) {
        computer.setRam(ram);
        return this;
    }
    
    public ComputerBuilder setStorage(String storage) {
        computer.setStorage(storage);
        return this;
    }
    
    public Computer build() {
        return computer;
    }
}

// 通过建造者模式来构建复杂的合成对象
```

## 总结

合成/聚合复用原则是面向对象编程中的一个重要原则，它强调通过合成/聚合来实现代码复用，而不是通过继承。合成/聚合复用原则指出，"优先使用合成/聚合，而不是继承"。合成/聚合复用原则包括合成（"has-a"关系）和聚合（"整体-部分"关系），它们都强调在运行时组合对象，而不是在编译时继承。合成/聚合复用原则相比继承有许多优势，包括松耦合、灵活性高、易于维护、可测试性强和代码复用。合成/聚合复用原则在用户界面开发、游戏开发和业务系统开发中广泛应用。应用合成/聚合复用原则需要正确识别"has-a"关系和"is-a"关系，避免过度合成，设计良好的接口，使用依赖注入和建造者模式。合成/聚合复用原则的最佳实践包括设计接口、使用依赖注入和使用建造者模式。