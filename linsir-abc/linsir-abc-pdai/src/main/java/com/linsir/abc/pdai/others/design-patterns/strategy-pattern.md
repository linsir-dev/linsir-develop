# 策略模式

## 策略模式的定义

策略模式（Strategy Pattern）是一种行为型设计模式，它定义了一系列算法，并将每个算法封装起来，使它们可以相互替换。策略模式让算法独立于使用它的客户端而变化。策略模式又称为政策模式（Policy Pattern）。

## 策略模式的结构

```
┌─────────────────┐         ┌─────────────────┐
│    Context      │────────>│    Strategy      │
├─────────────────┤         ├─────────────────┤
│ - strategy      │         │ + execute()      │
│ + setStrategy() │         └─────────────────┘
│ + execute()     │                  △
└─────────────────┘                  │
                    ┌───────────────┼───────────────┐
                    │               │               │
        ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
        │ConcreteStrategyA│ │ConcreteStrategyB│ │ConcreteStrategyC│
        ├─────────────────┤ ├─────────────────┤ ├─────────────────┤
        │ + execute()     │ │ + execute()     │ │ + execute()     │
        └─────────────────┘ └─────────────────┘ └─────────────────┘
```

## 策略模式的实现

### 1. 基本实现

```java
// 策略接口
interface Strategy {
    void execute();
}

// 具体策略 A
class ConcreteStrategyA implements Strategy {
    public void execute() {
        System.out.println("ConcreteStrategyA execute");
    }
}

// 具体策略 B
class ConcreteStrategyB implements Strategy {
    public void execute() {
        System.out.println("ConcreteStrategyB execute");
    }
}

// 上下文
class Context {
    private Strategy strategy;
    
    public Context(Strategy strategy) {
        this.strategy = strategy;
    }
    
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
    
    public void execute() {
        strategy.execute();
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Context context = new Context(new ConcreteStrategyA());
        context.execute();
        
        context.setStrategy(new ConcreteStrategyB());
        context.execute();
    }
}
```

### 2. 支付策略示例

```java
// 支付策略接口
interface PaymentStrategy {
    void pay(double amount);
}

// 支付宝支付策略
class AlipayStrategy implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using Alipay");
    }
}

// 微信支付策略
class WechatPayStrategy implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using WeChat Pay");
    }
}

// 信用卡支付策略
class CreditCardStrategy implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using Credit Card");
    }
}

// 购物车
class ShoppingCart {
    private List<Item> items = new ArrayList<>();
    private PaymentStrategy paymentStrategy;
    
    public void addItem(Item item) {
        items.add(item);
    }
    
    public void removeItem(Item item) {
        items.remove(item);
    }
    
    public double calculateTotal() {
        double total = 0;
        for (Item item : items) {
            total += item.getPrice();
        }
        return total;
    }
    
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
    
    public void checkout() {
        double amount = calculateTotal();
        paymentStrategy.pay(amount);
    }
}

// 商品
class Item {
    private String name;
    private double price;
    
    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }
    
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        
        cart.addItem(new Item("Item1", 100));
        cart.addItem(new Item("Item2", 200));
        
        cart.setPaymentStrategy(new AlipayStrategy());
        cart.checkout();
        
        cart.setPaymentStrategy(new WechatPayStrategy());
        cart.checkout();
    }
}
```

### 3. 排序策略示例

```java
// 排序策略接口
interface SortStrategy {
    void sort(int[] array);
}

// 冒泡排序策略
class BubbleSortStrategy implements SortStrategy {
    public void sort(int[] array) {
        System.out.println("Bubble Sort");
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }
}

// 快速排序策略
class QuickSortStrategy implements SortStrategy {
    public void sort(int[] array) {
        System.out.println("Quick Sort");
        quickSort(array, 0, array.length - 1);
    }
    
    private void quickSort(int[] array, int low, int high) {
        if (low < high) {
            int pi = partition(array, low, high);
            quickSort(array, low, pi - 1);
            quickSort(array, pi + 1, high);
        }
    }
    
    private int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (array[j] < pivot) {
                i++;
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        return i + 1;
    }
}

// 归并排序策略
class MergeSortStrategy implements SortStrategy {
    public void sort(int[] array) {
        System.out.println("Merge Sort");
        mergeSort(array, 0, array.length - 1);
    }
    
    private void mergeSort(int[] array, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(array, left, mid);
            mergeSort(array, mid + 1, right);
            merge(array, left, mid, right);
        }
    }
    
    private void merge(int[] array, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        
        int[] leftArray = new int[n1];
        int[] rightArray = new int[n2];
        
        for (int i = 0; i < n1; i++) {
            leftArray[i] = array[left + i];
        }
        for (int j = 0; j < n2; j++) {
            rightArray[j] = array[mid + 1 + j];
        }
        
        int i = 0, j = 0;
        int k = left;
        while (i < n1 && j < n2) {
            if (leftArray[i] <= rightArray[j]) {
                array[k] = leftArray[i];
                i++;
            } else {
                array[k] = rightArray[j];
                j++;
            }
            k++;
        }
        
        while (i < n1) {
            array[k] = leftArray[i];
            i++;
            k++;
        }
        
        while (j < n2) {
            array[k] = rightArray[j];
            j++;
            k++;
        }
    }
}

// 排序器
class Sorter {
    private SortStrategy sortStrategy;
    
    public Sorter(SortStrategy sortStrategy) {
        this.sortStrategy = sortStrategy;
    }
    
    public void setSortStrategy(SortStrategy sortStrategy) {
        this.sortStrategy = sortStrategy;
    }
    
    public void sort(int[] array) {
        sortStrategy.sort(array);
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        int[] array1 = {64, 34, 25, 12, 22, 11, 90};
        int[] array2 = {64, 34, 25, 12, 22, 11, 90};
        int[] array3 = {64, 34, 25, 12, 22, 11, 90};
        
        Sorter sorter = new Sorter(new BubbleSortStrategy());
        sorter.sort(array1);
        System.out.println(Arrays.toString(array1));
        
        sorter.setSortStrategy(new QuickSortStrategy());
        sorter.sort(array2);
        System.out.println(Arrays.toString(array2));
        
        sorter.setSortStrategy(new MergeSortStrategy());
        sorter.sort(array3);
        System.out.println(Arrays.toString(array3));
    }
}
```

## 策略模式的优缺点

### 优点

1. **符合开闭原则**：策略模式符合开闭原则，可以在不修改原有代码的情况下增加新的策略。

2. **避免多重条件语句**：策略模式可以避免使用多重条件语句，使代码更加清晰。

3. **提高代码复用性**：策略模式将算法封装在独立的类中，提高了代码的复用性。

4. **提高代码可维护性**：策略模式将算法的实现与使用分离，提高了代码的可维护性。

5. **提高代码可扩展性**：策略模式可以方便地增加新的策略，提高了代码的可扩展性。

### 缺点

1. **增加类的数量**：策略模式会增加类的数量，增加了系统的复杂度。

2. **客户端必须知道所有策略**：客户端必须知道所有的策略，并根据实际情况选择合适的策略。

3. **策略之间可能存在依赖**：不同的策略之间可能存在依赖关系，增加了系统的复杂度。

## 策略模式的使用场景

### 1. 支付系统

```java
// 支付策略接口
interface PaymentStrategy {
    void pay(double amount);
}

// 支付宝支付策略
class AlipayStrategy implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using Alipay");
    }
}

// 微信支付策略
class WechatPayStrategy implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using WeChat Pay");
    }
}

// 支付处理器
class PaymentProcessor {
    private PaymentStrategy paymentStrategy;
    
    public PaymentProcessor(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
    
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
    
    public void processPayment(double amount) {
        paymentStrategy.pay(amount);
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        PaymentProcessor processor = new PaymentProcessor(new AlipayStrategy());
        processor.processPayment(100);
        
        processor.setPaymentStrategy(new WechatPayStrategy());
        processor.processPayment(200);
    }
}
```

### 2. 压缩系统

```java
// 压缩策略接口
interface CompressionStrategy {
    void compress(String file);
}

// ZIP 压缩策略
class ZipCompressionStrategy implements CompressionStrategy {
    public void compress(String file) {
        System.out.println("Compressing " + file + " using ZIP");
    }
}

// GZIP 压缩策略
class GzipCompressionStrategy implements CompressionStrategy {
    public void compress(String file) {
        System.out.println("Compressing " + file + " using GZIP");
    }
}

// 压缩器
class Compressor {
    private CompressionStrategy compressionStrategy;
    
    public Compressor(CompressionStrategy compressionStrategy) {
        this.compressionStrategy = compressionStrategy;
    }
    
    public void setCompressionStrategy(CompressionStrategy compressionStrategy) {
        this.compressionStrategy = compressionStrategy;
    }
    
    public void compress(String file) {
        compressionStrategy.compress(file);
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Compressor compressor = new Compressor(new ZipCompressionStrategy());
        compressor.compress("file.txt");
        
        compressor.setCompressionStrategy(new GzipCompressionStrategy());
        compressor.compress("file.txt");
    }
}
```

### 3. 路径规划系统

```java
// 路径规划策略接口
interface RouteStrategy {
    void planRoute(String start, String end);
}

// 最短路径策略
class ShortestRouteStrategy implements RouteStrategy {
    public void planRoute(String start, String end) {
        System.out.println("Planning shortest route from " + start + " to " + end);
    }
}

// 最快路径策略
class FastestRouteStrategy implements RouteStrategy {
    public void planRoute(String start, String end) {
        System.out.println("Planning fastest route from " + start + " to " + end);
    }
}

// 路径规划器
class RoutePlanner {
    private RouteStrategy routeStrategy;
    
    public RoutePlanner(RouteStrategy routeStrategy) {
        this.routeStrategy = routeStrategy;
    }
    
    public void setRouteStrategy(RouteStrategy routeStrategy) {
        this.routeStrategy = routeStrategy;
    }
    
    public void planRoute(String start, String end) {
        routeStrategy.planRoute(start, end);
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        RoutePlanner planner = new RoutePlanner(new ShortestRouteStrategy());
        planner.planRoute("A", "B");
        
        planner.setRouteStrategy(new FastestRouteStrategy());
        planner.planRoute("A", "B");
    }
}
```

### 4. 数据验证系统

```java
// 验证策略接口
interface ValidationStrategy {
    boolean validate(String input);
}

// 邮箱验证策略
class EmailValidationStrategy implements ValidationStrategy {
    public boolean validate(String input) {
        return input.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}

// 手机号验证策略
class PhoneValidationStrategy implements ValidationStrategy {
    public boolean validate(String input) {
        return input.matches("^\\d{11}$");
    }
}

// 验证器
class Validator {
    private ValidationStrategy validationStrategy;
    
    public Validator(ValidationStrategy validationStrategy) {
        this.validationStrategy = validationStrategy;
    }
    
    public void setValidationStrategy(ValidationStrategy validationStrategy) {
        this.validationStrategy = validationStrategy;
    }
    
    public boolean validate(String input) {
        return validationStrategy.validate(input);
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Validator validator = new Validator(new EmailValidationStrategy());
        System.out.println(validator.validate("test@example.com"));
        
        validator.setValidationStrategy(new PhoneValidationStrategy());
        System.out.println(validator.validate("13800138000"));
    }
}
```

## 策略模式的注意事项

### 1. 策略的选择

客户端需要根据实际情况选择合适的策略，如果选择不当，可能会导致系统性能下降。

### 2. 策略的切换

策略的切换应该是线程安全的，否则可能会导致数据不一致。

```java
class Context {
    private volatile Strategy strategy;
    
    public Context(Strategy strategy) {
        this.strategy = strategy;
    }
    
    public synchronized void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
    
    public synchronized void execute() {
        strategy.execute();
    }
}
```

### 3. 策略的初始化

某些策略可能需要初始化，应该在策略创建时进行初始化。

```java
interface Strategy {
    void init();
    void execute();
    void destroy();
}

class ConcreteStrategy implements Strategy {
    public void init() {
        // 初始化逻辑
    }
    
    public void execute() {
        // 执行逻辑
    }
    
    public void destroy() {
        // 销毁逻辑
    }
}
```

## 策略模式的最佳实践

### 1. 使用工厂模式创建策略

使用工厂模式来创建策略，可以简化客户端的代码。

```java
class StrategyFactory {
    public static Strategy createStrategy(String type) {
        if (type.equals("A")) {
            return new ConcreteStrategyA();
        } else if (type.equals("B")) {
            return new ConcreteStrategyB();
        }
        return null;
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Strategy strategy = StrategyFactory.createStrategy("A");
        Context context = new Context(strategy);
        context.execute();
    }
}
```

### 2. 使用枚举定义策略

使用枚举来定义策略，可以简化策略的创建和使用。

```java
enum StrategyEnum {
    STRATEGY_A(new ConcreteStrategyA()),
    STRATEGY_B(new ConcreteStrategyB());
    
    private Strategy strategy;
    
    StrategyEnum(Strategy strategy) {
        this.strategy = strategy;
    }
    
    public Strategy getStrategy() {
        return strategy;
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Strategy strategy = StrategyEnum.STRATEGY_A.getStrategy();
        Context context = new Context(strategy);
        context.execute();
    }
}
```

### 3. 使用 Lambda 表达式

在 Java 8 中，可以使用 Lambda 表达式来简化策略的创建。

```java
@FunctionalInterface
interface Strategy {
    void execute();
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Strategy strategyA = () -> System.out.println("StrategyA execute");
        Strategy strategyB = () -> System.out.println("StrategyB execute");
        
        Context context = new Context(strategyA);
        context.execute();
        
        context.setStrategy(strategyB);
        context.execute();
    }
}
```

## 总结

策略模式是一种行为型设计模式，它定义了一系列算法，并将每个算法封装起来，使它们可以相互替换。策略模式让算法独立于使用它的客户端而变化。策略模式的优点包括符合开闭原则、避免多重条件语句、提高代码复用性、提高代码可维护性和提高代码可扩展性。策略模式的缺点包括增加类的数量、客户端必须知道所有策略和策略之间可能存在依赖。策略模式的使用场景包括支付系统、压缩系统、路径规划系统和数据验证系统。策略模式的注意事项包括策略的选择、策略的切换和策略的初始化。策略模式的最佳实践包括使用工厂模式创建策略、使用枚举定义策略和使用 Lambda 表达式。