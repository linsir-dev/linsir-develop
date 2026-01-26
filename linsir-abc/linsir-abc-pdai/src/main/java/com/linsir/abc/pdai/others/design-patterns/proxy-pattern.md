# 代理模式

## 代理模式的定义

代理模式（Proxy Pattern）是一种结构型设计模式，它为其他对象提供一种代理以控制对这个对象的访问。代理模式在客户端和目标对象之间起到中介作用，可以在不改变目标对象的情况下，通过代理对象来控制对目标对象的访问。代理模式又称为委托模式（Delegate Pattern）。

## 代理模式的结构

```
┌─────────────────┐         ┌─────────────────┐
│    Subject      │         │     Proxy       │
├─────────────────┤         ├─────────────────┤
│ + request()      │         │ - subject       │
└─────────────────┘         │ + request()      │
        △                   └─────────────────┘
        │                           
        │                           
┌───────────────┐                 
│RealSubject    │                 
├───────────────┤                 
│ + request()   │                 
└───────────────┘                 
```

## 代理模式的实现

### 1. 基本实现

```java
// 主题接口
interface Subject {
    void request();
}

// 真实主题
class RealSubject implements Subject {
    public void request() {
        System.out.println("RealSubject request");
    }
}

// 代理
class Proxy implements Subject {
    private RealSubject realSubject;
    
    public void request() {
        if (realSubject == null) {
            realSubject = new RealSubject();
        }
        preRequest();
        realSubject.request();
        postRequest();
    }
    
    private void preRequest() {
        System.out.println("Pre request");
    }
    
    private void postRequest() {
        System.out.println("Post request");
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Subject proxy = new Proxy();
        proxy.request();
    }
}
```

### 2. 静态代理

```java
// 接口
interface UserService {
    void addUser(String username);
    void deleteUser(String username);
}

// 真实实现
class UserServiceImpl implements UserService {
    public void addUser(String username) {
        System.out.println("Add user: " + username);
    }
    
    public void deleteUser(String username) {
        System.out.println("Delete user: " + username);
    }
}

// 代理
class UserServiceProxy implements UserService {
    private UserService userService;
    
    public UserServiceProxy(UserService userService) {
        this.userService = userService;
    }
    
    public void addUser(String username) {
        System.out.println("Before add user");
        userService.addUser(username);
        System.out.println("After add user");
    }
    
    public void deleteUser(String username) {
        System.out.println("Before delete user");
        userService.deleteUser(username);
        System.out.println("After delete user");
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        UserService proxy = new UserServiceProxy(userService);
        proxy.addUser("John");
        proxy.deleteUser("John");
    }
}
```

### 3. 动态代理（JDK 动态代理）

```java
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// 接口
interface UserService {
    void addUser(String username);
    void deleteUser(String username);
}

// 真实实现
class UserServiceImpl implements UserService {
    public void addUser(String username) {
        System.out.println("Add user: " + username);
    }
    
    public void deleteUser(String username) {
        System.out.println("Delete user: " + username);
    }
}

// 调用处理器
class LogInvocationHandler implements InvocationHandler {
    private Object target;
    
    public LogInvocationHandler(Object target) {
        this.target = target;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Before method: " + method.getName());
        Object result = method.invoke(target, args);
        System.out.println("After method: " + method.getName());
        return result;
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        LogInvocationHandler handler = new LogInvocationHandler(userService);
        UserService proxy = (UserService) Proxy.newProxyInstance(
            userService.getClass().getClassLoader(),
            userService.getClass().getInterfaces(),
            handler
        );
        proxy.addUser("John");
        proxy.deleteUser("John");
    }
}
```

### 4. CGLIB 动态代理

```java
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

// 类（不需要实现接口）
class UserService {
    public void addUser(String username) {
        System.out.println("Add user: " + username);
    }
    
    public void deleteUser(String username) {
        System.out.println("Delete user: " + username);
    }
}

// 方法拦截器
class LogMethodInterceptor implements MethodInterceptor {
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("Before method: " + method.getName());
        Object result = proxy.invokeSuper(obj, args);
        System.out.println("After method: " + method.getName());
        return result;
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserService.class);
        enhancer.setCallback(new LogMethodInterceptor());
        UserService proxy = (UserService) enhancer.create();
        proxy.addUser("John");
        proxy.deleteUser("John");
    }
}
```

## 代理模式的类型

### 1. 远程代理（Remote Proxy）

远程代理为一个位于不同地址空间的对象提供一个本地代表，使得客户端可以像访问本地对象一样访问远程对象。

```java
// 远程接口
interface RemoteService {
    String getData();
}

// 远程实现
class RemoteServiceImpl implements RemoteService {
    public String getData() {
        return "Remote data";
    }
}

// 远程代理
class RemoteServiceProxy implements RemoteService {
    private RemoteService remoteService;
    
    public String getData() {
        if (remoteService == null) {
            remoteService = new RemoteServiceImpl();
        }
        return remoteService.getData();
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        RemoteService proxy = new RemoteServiceProxy();
        System.out.println(proxy.getData());
    }
}
```

### 2. 虚拟代理（Virtual Proxy）

虚拟代理根据需要创建开销很大的对象，只有当真正需要时才创建。

```java
// 图像接口
interface Image {
    void display();
}

// 真实图像
class RealImage implements Image {
    private String filename;
    
    public RealImage(String filename) {
        this.filename = filename;
        loadFromDisk();
    }
    
    private void loadFromDisk() {
        System.out.println("Loading " + filename);
    }
    
    public void display() {
        System.out.println("Displaying " + filename);
    }
}

// 虚拟代理
class ProxyImage implements Image {
    private RealImage realImage;
    private String filename;
    
    public ProxyImage(String filename) {
        this.filename = filename;
    }
    
    public void display() {
        if (realImage == null) {
            realImage = new RealImage(filename);
        }
        realImage.display();
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Image image = new ProxyImage("test.jpg");
        image.display();
        image.display();
    }
}
```

### 3. 保护代理（Protection Proxy）

保护代理控制对原始对象的访问，用于对象应该有不同的访问权限的时候。

```java
// 接口
interface Document {
    void view();
    void edit();
}

// 真实文档
class RealDocument implements Document {
    private String content;
    
    public RealDocument(String content) {
        this.content = content;
    }
    
    public void view() {
        System.out.println("Viewing: " + content);
    }
    
    public void edit() {
        System.out.println("Editing: " + content);
    }
}

// 保护代理
class ProtectionProxy implements Document {
    private RealDocument realDocument;
    private String role;
    
    public ProtectionProxy(RealDocument realDocument, String role) {
        this.realDocument = realDocument;
        this.role = role;
    }
    
    public void view() {
        realDocument.view();
    }
    
    public void edit() {
        if ("admin".equals(role)) {
            realDocument.edit();
        } else {
            System.out.println("Permission denied");
        }
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Document document = new RealDocument("Secret document");
        
        Document adminProxy = new ProtectionProxy(document, "admin");
        adminProxy.view();
        adminProxy.edit();
        
        Document userProxy = new ProtectionProxy(document, "user");
        userProxy.view();
        userProxy.edit();
    }
}
```

### 4. 智能引用代理（Smart Reference Proxy）

智能引用代理取代了简单的指针，它在访问对象时执行一些附加操作，比如计算引用次数、检查锁等。

```java
// 接口
interface Resource {
    void use();
}

// 真实资源
class RealResource implements Resource {
    private String name;
    
    public RealResource(String name) {
        this.name = name;
        System.out.println("Creating resource: " + name);
    }
    
    public void use() {
        System.out.println("Using resource: " + name);
    }
}

// 智能引用代理
class SmartReferenceProxy implements Resource {
    private RealResource realResource;
    private String name;
    private int referenceCount = 0;
    
    public SmartReferenceProxy(String name) {
        this.name = name;
    }
    
    public void use() {
        referenceCount++;
        System.out.println("Reference count: " + referenceCount);
        if (realResource == null) {
            realResource = new RealResource(name);
        }
        realResource.use();
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Resource proxy = new SmartReferenceProxy("resource1");
        proxy.use();
        proxy.use();
        proxy.use();
    }
}
```

## 代理模式的优缺点

### 优点

1. **职责清晰**：代理模式将访问控制与业务逻辑分离，使职责更加清晰。

2. **高扩展性**：代理模式可以在不修改目标对象的情况下，通过代理对象来扩展功能。

3. **智能化**：代理模式可以在访问目标对象之前或之后执行一些附加操作。

4. **保护性**：代理模式可以控制对目标对象的访问，提供保护机制。

### 缺点

1. **增加复杂度**：代理模式会增加系统的复杂度，增加类的数量。

2. **性能影响**：代理模式可能会影响系统的性能，因为需要通过代理来访问目标对象。

3. **延迟初始化**：某些代理模式需要延迟初始化，可能会影响系统的响应时间。

## 代理模式的使用场景

### 1. 日志记录

```java
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// 接口
interface UserService {
    void addUser(String username);
    void deleteUser(String username);
}

// 真实实现
class UserServiceImpl implements UserService {
    public void addUser(String username) {
        System.out.println("Add user: " + username);
    }
    
    public void deleteUser(String username) {
        System.out.println("Delete user: " + username);
    }
}

// 日志代理
class LogProxy implements InvocationHandler {
    private Object target;
    
    public LogProxy(Object target) {
        this.target = target;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Log: " + method.getName() + " called with args: " + Arrays.toString(args));
        Object result = method.invoke(target, args);
        System.out.println("Log: " + method.getName() + " returned: " + result);
        return result;
    }
    
    public static <T> T createProxy(T target) {
        return (T) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            new LogProxy(target)
        );
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        UserService proxy = LogProxy.createProxy(userService);
        proxy.addUser("John");
        proxy.deleteUser("John");
    }
}
```

### 2. 权限控制

```java
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// 接口
interface DocumentService {
    void viewDocument(String documentId);
    void editDocument(String documentId);
    void deleteDocument(String documentId);
}

// 真实实现
class DocumentServiceImpl implements DocumentService {
    public void viewDocument(String documentId) {
        System.out.println("View document: " + documentId);
    }
    
    public void editDocument(String documentId) {
        System.out.println("Edit document: " + documentId);
    }
    
    public void deleteDocument(String documentId) {
        System.out.println("Delete document: " + documentId);
    }
}

// 权限代理
class PermissionProxy implements InvocationHandler {
    private Object target;
    private String role;
    
    public PermissionProxy(Object target, String role) {
        this.target = target;
        this.role = role;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (methodName.startsWith("edit") || methodName.startsWith("delete")) {
            if (!"admin".equals(role)) {
                throw new SecurityException("Permission denied");
            }
        }
        return method.invoke(target, args);
    }
    
    public static <T> T createProxy(T target, String role) {
        return (T) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            new PermissionProxy(target, role)
        );
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        DocumentService service = new DocumentServiceImpl();
        
        DocumentService adminProxy = PermissionProxy.createProxy(service, "admin");
        adminProxy.viewDocument("doc1");
        adminProxy.editDocument("doc1");
        adminProxy.deleteDocument("doc1");
        
        DocumentService userProxy = PermissionProxy.createProxy(service, "user");
        userProxy.viewDocument("doc1");
        try {
            userProxy.editDocument("doc1");
        } catch (SecurityException e) {
            System.out.println("Permission denied for edit");
        }
    }
}
```

### 3. 缓存

```java
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

// 接口
interface DataService {
    String getData(String key);
}

// 真实实现
class DataServiceImpl implements DataService {
    public String getData(String key) {
        System.out.println("Fetching data from database: " + key);
        return "Data for " + key;
    }
}

// 缓存代理
class CacheProxy implements InvocationHandler {
    private Object target;
    private Map<String, String> cache = new HashMap<>();
    
    public CacheProxy(Object target) {
        this.target = target;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String key = (String) args[0];
        if (cache.containsKey(key)) {
            System.out.println("Getting data from cache: " + key);
            return cache.get(key);
        } else {
            Object result = method.invoke(target, args);
            cache.put(key, (String) result);
            return result;
        }
    }
    
    public static <T> T createProxy(T target) {
        return (T) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            new CacheProxy(target)
        );
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        DataService service = new DataServiceImpl();
        DataService proxy = CacheProxy.createProxy(service);
        System.out.println(proxy.getData("key1"));
        System.out.println(proxy.getData("key1"));
        System.out.println(proxy.getData("key2"));
        System.out.println(proxy.getData("key2"));
    }
}
```

### 4. 延迟加载

```java
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// 接口
interface HeavyObject {
    void doSomething();
}

// 真实实现
class RealHeavyObject implements HeavyObject {
    public RealHeavyObject() {
        System.out.println("Creating heavy object...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Heavy object created");
    }
    
    public void doSomething() {
        System.out.println("Doing something...");
    }
}

// 延迟加载代理
class LazyLoadingProxy implements InvocationHandler {
    private HeavyObject target;
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (target == null) {
            target = new RealHeavyObject();
        }
        return method.invoke(target, args);
    }
    
    public static HeavyObject createProxy() {
        return (HeavyObject) Proxy.newProxyInstance(
            LazyLoadingProxy.class.getClassLoader(),
            new Class[]{HeavyObject.class},
            new LazyLoadingProxy()
        );
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        System.out.println("Creating proxy...");
        HeavyObject proxy = LazyLoadingProxy.createProxy();
        System.out.println("Proxy created");
        
        System.out.println("Calling doSomething...");
        proxy.doSomething();
        
        System.out.println("Calling doSomething again...");
        proxy.doSomething();
    }
}
```

## 代理模式的注意事项

### 1. 代理的选择

根据实际需求选择合适的代理类型，比如远程代理、虚拟代理、保护代理或智能引用代理。

### 2. 性能考虑

代理模式可能会影响系统的性能，需要考虑性能问题，特别是在高并发场景下。

### 3. 线程安全

代理模式需要考虑线程安全问题，特别是在多线程环境下使用代理时。

```java
class ThreadSafeProxy implements InvocationHandler {
    private Object target;
    private final Object lock = new Object();
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        synchronized (lock) {
            return method.invoke(target, args);
        }
    }
}
```

## 代理模式的最佳实践

### 1. 使用动态代理

使用动态代理可以减少代码量，提高灵活性。

```java
public class DynamicProxy {
    public static <T> T createProxy(T target, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            handler
        );
    }
}
```

### 2. 使用责任链

使用责任链可以组合多个代理，实现更复杂的功能。

```java
class ChainProxy implements InvocationHandler {
    private List<InvocationHandler> handlers;
    private Object target;
    
    public ChainProxy(Object target, List<InvocationHandler> handlers) {
        this.target = target;
        this.handlers = handlers;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        for (InvocationHandler handler : handlers) {
            result = handler.invoke(proxy, method, args);
        }
        return result;
    }
}
```

### 3. 使用注解

使用注解可以简化代理的配置和使用。

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Log {
    String value() default "";
}

class LogProxy implements InvocationHandler {
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log log = method.getAnnotation(Log.class);
        if (log != null) {
            System.out.println("Log: " + log.value());
        }
        return method.invoke(target, args);
    }
}
```

## 代理模式与其他模式的区别

### 1. 代理模式 vs 装饰器模式

代理模式和装饰器模式都使用组合来包装对象，但它们的目的是不同的。代理模式的目的是控制对对象的访问，而装饰器模式的目的是动态地扩展对象的功能。

### 2. 代理模式 vs 适配器模式

代理模式和适配器模式都使用组合来包装对象，但它们的目的是不同的。代理模式的目的是控制对对象的访问，而适配器模式的目的是将一个类的接口转换成客户希望的另一个接口。

### 3. 代理模式 vs 外观模式

代理模式和外观模式都使用组合来包装对象，但它们的目的是不同的。代理模式的目的是控制对对象的访问，而外观模式的目的是为子系统中的一组接口提供一个一致的界面。

## 总结

代理模式是一种结构型设计模式，它为其他对象提供一种代理以控制对这个对象的访问。代理模式在客户端和目标对象之间起到中介作用，可以在不改变目标对象的情况下，通过代理对象来控制对目标对象的访问。代理模式的类型包括远程代理、虚拟代理、保护代理和智能引用代理。代理模式的优点包括职责清晰、高扩展性、智能化和保护性。代理模式的缺点包括增加复杂度、性能影响和延迟初始化。代理模式的使用场景包括日志记录、权限控制、缓存和延迟加载。代理模式的注意事项包括代理的选择、性能考虑和线程安全。代理模式的最佳实践包括使用动态代理、使用责任链和使用注解。代理模式与其他模式的区别包括代理模式 vs 装饰器模式、代理模式 vs 适配器模式和代理模式 vs 外观模式。