# 观察者模式

## 观察者模式的定义

观察者模式（Observer Pattern）是一种行为型设计模式，它定义对象间的一种一对多的依赖关系，当一个对象的状态发生改变时，所有依赖于它的对象都得到通知并被自动更新。观察者模式又称为发布-订阅（Publish-Subscribe）模式、模型-视图（Model-View）模式、源-监听器（Source-Listener）模式或从属者（Dependents）模式。

## 观察者模式的结构

```
┌─────────────────┐         ┌─────────────────┐
│   Subject       │────────>│   Observer      │
├─────────────────┤         ├─────────────────┤
│ - observers     │         │ + update()      │
│ + attach()      │         └─────────────────┘
│ + detach()      │                  △
│ + notify()      │                  │
└─────────────────┘    ┌─────────────┴─────────────┐
                       │                           │
        ┌─────────────────┐         ┌─────────────────┐
        │ConcreteObserverA│         │ConcreteObserverB│
        ├─────────────────┤         ├─────────────────┤
        │ + update()      │         │ + update()      │
        └─────────────────┘         └─────────────────┘
```

## 观察者模式的实现

### 1. 基本实现

```java
// 观察者接口
interface Observer {
    void update(String message);
}

// 具体观察者 A
class ConcreteObserverA implements Observer {
    private String name;
    
    public ConcreteObserverA(String name) {
        this.name = name;
    }
    
    public void update(String message) {
        System.out.println(name + " received: " + message);
    }
}

// 具体观察者 B
class ConcreteObserverB implements Observer {
    private String name;
    
    public ConcreteObserverB(String name) {
        this.name = name;
    }
    
    public void update(String message) {
        System.out.println(name + " received: " + message);
    }
}

// 主题接口
interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notify(String message);
}

// 具体主题
class ConcreteSubject implements Subject {
    private List<Observer> observers = new ArrayList<>();
    
    public void attach(Observer observer) {
        observers.add(observer);
    }
    
    public void detach(Observer observer) {
        observers.remove(observer);
    }
    
    public void notify(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Subject subject = new ConcreteSubject();
        
        Observer observerA = new ConcreteObserverA("ObserverA");
        Observer observerB = new ConcreteObserverB("ObserverB");
        
        subject.attach(observerA);
        subject.attach(observerB);
        
        subject.notify("Hello, World!");
        
        subject.detach(observerA);
        
        subject.notify("Goodbye, World!");
    }
}
```

### 2. 使用 Java 内置的观察者模式

```java
import java.util.Observable;
import java.util.Observer;

// 具体主题
class ConcreteSubject extends Observable {
    private String state;
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
        setChanged();
        notifyObservers(state);
    }
}

// 具体观察者
class ConcreteObserver implements Observer {
    private String name;
    
    public ConcreteObserver(String name) {
        this.name = name;
    }
    
    public void update(Observable o, Object arg) {
        System.out.println(name + " received: " + arg);
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        ConcreteSubject subject = new ConcreteSubject();
        
        Observer observerA = new ConcreteObserver("ObserverA");
        Observer observerB = new ConcreteObserver("ObserverB");
        
        subject.addObserver(observerA);
        subject.addObserver(observerB);
        
        subject.setState("Hello, World!");
        
        subject.deleteObserver(observerA);
        
        subject.setState("Goodbye, World!");
    }
}
```

## 观察者模式的优缺点

### 优点

1. **降低耦合度**：观察者模式降低了主题与观察者之间的耦合度，两者之间是抽象耦合关系。

2. **符合开闭原则**：观察者模式符合开闭原则，增加新的观察者不需要修改原有代码。

3. **广播通信**：观察者模式支持广播通信，一个主题可以通知多个观察者。

4. **灵活性强**：观察者模式可以灵活地增加和删除观察者，而不影响主题和其他观察者。

### 缺点

1. **性能问题**：如果观察者很多，通知所有观察者会花费很多时间。

2. **循环依赖**：如果观察者和主题之间存在循环依赖，可能会导致系统崩溃。

3. **顺序不确定**：观察者模式无法保证观察者的通知顺序。

4. **调试困难**：观察者模式使得调试变得困难，因为观察者之间的交互是隐式的。

## 观察者模式的使用场景

### 1. 事件处理系统

```java
// 事件监听器接口
interface EventListener {
    void onEvent(Event event);
}

// 具体事件监听器
class ClickListener implements EventListener {
    public void onEvent(Event event) {
        System.out.println("Click event: " + event.getData());
    }
}

// 事件源
class EventSource {
    private List<EventListener> listeners = new ArrayList<>();
    
    public void addListener(EventListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(EventListener listener) {
        listeners.remove(listener);
    }
    
    public void fireEvent(Event event) {
        for (EventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}

// 事件
class Event {
    private String data;
    
    public Event(String data) {
        this.data = data;
    }
    
    public String getData() {
        return data;
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        EventSource eventSource = new EventSource();
        
        EventListener clickListener = new ClickListener();
        eventSource.addListener(clickListener);
        
        Event event = new Event("Button clicked");
        eventSource.fireEvent(event);
    }
}
```

### 2. 消息队列

```java
// 消息监听器接口
interface MessageListener {
    void onMessage(Message message);
}

// 具体消息监听器
class EmailListener implements MessageListener {
    public void onMessage(Message message) {
        System.out.println("Email: " + message.getContent());
    }
}

// 消息队列
class MessageQueue {
    private List<MessageListener> listeners = new ArrayList<>();
    
    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }
    
    public void publish(Message message) {
        for (MessageListener listener : listeners) {
            listener.onMessage(message);
        }
    }
}

// 消息
class Message {
    private String content;
    
    public Message(String content) {
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        MessageQueue messageQueue = new MessageQueue();
        
        MessageListener emailListener = new EmailListener();
        messageQueue.addListener(emailListener);
        
        Message message = new Message("Hello, World!");
        messageQueue.publish(message);
    }
}
```

### 3. 股票价格监控

```java
// 股票观察者接口
interface StockObserver {
    void update(String stock, double price);
}

// 具体股票观察者
class Investor implements StockObserver {
    private String name;
    
    public Investor(String name) {
        this.name = name;
    }
    
    public void update(String stock, double price) {
        System.out.println(name + " received: " + stock + " price is " + price);
    }
}

// 股票市场
class StockMarket {
    private Map<String, Double> stocks = new HashMap<>();
    private List<StockObserver> observers = new ArrayList<>();
    
    public void addStock(String stock, double price) {
        stocks.put(stock, price);
    }
    
    public void updatePrice(String stock, double price) {
        stocks.put(stock, price);
        notifyObservers(stock, price);
    }
    
    public void addObserver(StockObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(StockObserver observer) {
        observers.remove(observer);
    }
    
    private void notifyObservers(String stock, double price) {
        for (StockObserver observer : observers) {
            observer.update(stock, price);
        }
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        StockMarket stockMarket = new StockMarket();
        
        stockMarket.addStock("AAPL", 150.0);
        stockMarket.addStock("GOOG", 2800.0);
        
        StockObserver investor1 = new Investor("Investor1");
        StockObserver investor2 = new Investor("Investor2");
        
        stockMarket.addObserver(investor1);
        stockMarket.addObserver(investor2);
        
        stockMarket.updatePrice("AAPL", 155.0);
        stockMarket.updatePrice("GOOG", 2850.0);
    }
}
```

### 4. 社交媒体关注

```java
// 关注者接口
interface Follower {
    void receivePost(String post);
}

// 具体关注者
class User implements Follower {
    private String name;
    
    public User(String name) {
        this.name = name;
    }
    
    public void receivePost(String post) {
        System.out.println(name + " received: " + post);
    }
}

// 博主
class Blogger {
    private List<Follower> followers = new ArrayList<>();
    
    public void addFollower(Follower follower) {
        followers.add(follower);
    }
    
    public void removeFollower(Follower follower) {
        followers.remove(follower);
    }
    
    public void publishPost(String post) {
        for (Follower follower : followers) {
            follower.receivePost(post);
        }
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Blogger blogger = new Blogger();
        
        Follower user1 = new User("User1");
        Follower user2 = new User("User2");
        
        blogger.addFollower(user1);
        blogger.addFollower(user2);
        
        blogger.publishPost("Hello, World!");
        blogger.publishPost("Goodbye, World!");
    }
}
```

## 观察者模式的注意事项

### 1. 避免循环依赖

观察者和主题之间不应该存在循环依赖，否则可能会导致系统崩溃。

### 2. 异步通知

如果观察者很多，通知所有观察者会花费很多时间，可以考虑使用异步通知。

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ConcreteSubject implements Subject {
    private List<Observer> observers = new ArrayList<>();
    private ExecutorService executor = Executors.newCachedThreadPool();
    
    public void attach(Observer observer) {
        observers.add(observer);
    }
    
    public void detach(Observer observer) {
        observers.remove(observer);
    }
    
    public void notify(String message) {
        for (Observer observer : observers) {
            executor.submit(() -> observer.update(message));
        }
    }
}
```

### 3. 错误处理

观察者的 update 方法可能会抛出异常，需要进行错误处理。

```java
class ConcreteSubject implements Subject {
    private List<Observer> observers = new ArrayList<>();
    
    public void attach(Observer observer) {
        observers.add(observer);
    }
    
    public void detach(Observer observer) {
        observers.remove(observer);
    }
    
    public void notify(String message) {
        for (Observer observer : observers) {
            try {
                observer.update(message);
            } catch (Exception e) {
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
}
```

### 4. 内存泄漏

如果观察者没有正确地从主题中移除，可能会导致内存泄漏。

```java
class ConcreteSubject implements Subject {
    private List<Observer> observers = new ArrayList<>();
    private List<WeakReference<Observer>> weakObservers = new ArrayList<>();
    
    public void attach(Observer observer) {
        weakObservers.add(new WeakReference<>(observer));
    }
    
    public void detach(Observer observer) {
        weakObservers.removeIf(ref -> ref.get() == observer);
    }
    
    public void notify(String message) {
        Iterator<WeakReference<Observer>> iterator = weakObservers.iterator();
        while (iterator.hasNext()) {
            WeakReference<Observer> ref = iterator.next();
            Observer observer = ref.get();
            if (observer == null) {
                iterator.remove();
            } else {
                observer.update(message);
            }
        }
    }
}
```

## 观察者模式的最佳实践

### 1. 使用事件对象

使用事件对象来封装事件数据，而不是直接传递原始数据。

```java
class Event {
    private String type;
    private Object data;
    
    public Event(String type, Object data) {
        this.type = type;
        this.data = data;
    }
    
    public String getType() {
        return type;
    }
    
    public Object getData() {
        return data;
    }
}

interface Observer {
    void update(Event event);
}
```

### 2. 使用过滤器

使用过滤器来过滤不需要通知的观察者。

```java
interface ObserverFilter {
    boolean accept(Observer observer, Event event);
}

class ConcreteSubject implements Subject {
    private List<Observer> observers = new ArrayList<>();
    private List<ObserverFilter> filters = new ArrayList<>();
    
    public void attach(Observer observer) {
        observers.add(observer);
    }
    
    public void detach(Observer observer) {
        observers.remove(observer);
    }
    
    public void addFilter(ObserverFilter filter) {
        filters.add(filter);
    }
    
    public void notify(Event event) {
        for (Observer observer : observers) {
            boolean accepted = true;
            for (ObserverFilter filter : filters) {
                if (!filter.accept(observer, event)) {
                    accepted = false;
                    break;
                }
            }
            if (accepted) {
                observer.update(event);
            }
        }
    }
}
```

### 3. 使用优先级

使用优先级来控制观察者的通知顺序。

```java
interface Observer {
    void update(Event event);
    int getPriority();
}

class ConcreteSubject implements Subject {
    private List<Observer> observers = new ArrayList<>();
    
    public void attach(Observer observer) {
        observers.add(observer);
        observers.sort(Comparator.comparingInt(Observer::getPriority).reversed());
    }
    
    public void detach(Observer observer) {
        observers.remove(observer);
    }
    
    public void notify(Event event) {
        for (Observer observer : observers) {
            observer.update(event);
        }
    }
}
```

## 总结

观察者模式是一种行为型设计模式，它定义对象间的一种一对多的依赖关系，当一个对象的状态发生改变时，所有依赖于它的对象都得到通知并被自动更新。观察者模式又称为发布-订阅模式、模型-视图模式、源-监听器模式或从属者模式。观察者模式的优点包括降低耦合度、符合开闭原则、支持广播通信和灵活性强。观察者模式的缺点包括性能问题、循环依赖、顺序不确定和调试困难。观察者模式的使用场景包括事件处理系统、消息队列、股票价格监控和社交媒体关注。观察者模式的注意事项包括避免循环依赖、异步通知、错误处理和内存泄漏。观察者模式的最佳实践包括使用事件对象、使用过滤器和使用优先级。