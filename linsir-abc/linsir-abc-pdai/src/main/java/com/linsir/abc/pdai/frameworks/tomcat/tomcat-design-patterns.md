# Tomcat中的设计模式

## 1. 概述

Tomcat作为一个成熟的Servlet容器，在其设计和实现中广泛使用了多种设计模式。这些设计模式使得Tomcat具有良好的可扩展性、可维护性和灵活性。本文将详细介绍Tomcat中使用的主要设计模式。

## 2. 组合模式（Composite Pattern）

### 2.1 概述

组合模式允许将对象组合成树形结构来表示"部分-整体"的层次结构，使得客户端对单个对象和组合对象的使用具有一致性。

### 2.2 Tomcat中的应用

Tomcat的Container层次结构就是组合模式的典型应用。

```
Container (接口)
    │
    ├── Engine (实现)
    │       │
    │       └── Host (子容器)
    │               │
    │               └── Context (子容器)
    │                       │
    │                       └── Wrapper (子容器)
```

### 2.3 代码示例

```java
public interface Container {
    void addChild(Container child);
    void removeChild(Container child);
    Container findChild(String name);
    Container[] findChildren();
    
    void addValve(Valve valve);
    void removeValve(Valve valve);
    Pipeline getPipeline();
}

public abstract class ContainerBase implements Container {
    protected HashMap<String, Container> children = new HashMap<>();
    
    @Override
    public void addChild(Container child) {
        child.setParent(this);
        children.put(child.getName(), child);
    }
    
    @Override
    public void removeChild(Container child) {
        children.remove(child.getName());
    }
    
    @Override
    public Container findChild(String name) {
        return children.get(name);
    }
    
    @Override
    public Container[] findChildren() {
        return children.values().toArray(new Container[0]);
    }
}

public class StandardEngine extends ContainerBase {
    @Override
    public void addChild(Container child) {
        if (!(child instanceof Host)) {
            throw new IllegalArgumentException("Child must be a Host");
        }
        super.addChild(child);
    }
}

public class StandardHost extends ContainerBase {
    @Override
    public void addChild(Container child) {
        if (!(child instanceof Context)) {
            throw new IllegalArgumentException("Child must be a Context");
        }
        super.addChild(child);
    }
}
```

### 2.4 使用示例

```java
public class ContainerExample {
    public static void main(String[] args) {
        Engine engine = new StandardEngine();
        engine.setName("Catalina");
        
        Host host1 = new StandardHost();
        host1.setName("localhost");
        
        Host host2 = new StandardHost();
        host2.setName("www.example.com");
        
        Context context1 = new StandardContext();
        context1.setName("ROOT");
        
        Context context2 = new StandardContext();
        context2.setName("examples");
        
        Wrapper wrapper1 = new StandardWrapper();
        wrapper1.setName("DefaultServlet");
        
        Wrapper wrapper2 = new StandardWrapper();
        wrapper2.setName("JspServlet");
        
        engine.addChild(host1);
        engine.addChild(host2);
        
        host1.addChild(context1);
        host1.addChild(context2);
        
        context1.addChild(wrapper1);
        context2.addChild(wrapper2);
        
        printContainerTree(engine, 0);
    }
    
    private static void printContainerTree(Container container, int level) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append("  ");
        }
        
        System.out.println(indent + container.getName());
        
        for (Container child : container.findChildren()) {
            printContainerTree(child, level + 1);
        }
    }
}
```

## 3. 责任链模式（Chain of Responsibility Pattern）

### 3.1 概述

责任链模式为请求创建了一个接收者对象链，并沿着这条链传递该请求，直到有对象处理它为止。

### 3.2 Tomcat中的应用

Tomcat的Pipeline和Valve机制就是责任链模式的典型应用。

```
Pipeline
    │
    ├── Valve 1
    │       │
    │       └── Valve 2
    │               │
    │               └── Valve N
    │                       │
    │                       └── Basic Valve
```

### 3.3 代码示例

```java
public interface Valve {
    Container getContainer();
    void setContainer(Container container);
    Valve getNext();
    void setNext(Valve valve);
    void invoke(Request request, Response response) 
        throws IOException, ServletException;
}

public abstract class ValveBase implements Valve {
    protected Container container;
    protected Valve next;
    
    @Override
    public Container getContainer() {
        return container;
    }
    
    @Override
    public void setContainer(Container container) {
        this.container = container;
    }
    
    @Override
    public Valve getNext() {
        return next;
    }
    
    @Override
    public void setNext(Valve valve) {
        this.next = valve;
    }
}

public class AccessLogValve extends ValveBase {
    
    @Override
    public void invoke(Request request, Response response) 
            throws IOException, ServletException {
        
        String remoteAddr = request.getRemoteAddr();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String protocol = request.getProtocol();
        int status = response.getStatus();
        
        System.out.println(remoteAddr + " " + method + " " + uri + " " + 
                           protocol + " " + status);
        
        Valve next = getNext();
        if (next != null) {
            next.invoke(request, response);
        }
    }
}

public class StandardEngineValve extends ValveBase {
    
    @Override
    public void invoke(Request request, Response response) 
            throws IOException, ServletException {
        
        String hostName = request.getServerName();
        Host host = (Host) getContainer().findChild(hostName);
        
        if (host == null) {
            host = (Host) getContainer().findChild(
                ((StandardEngine) getContainer()).getDefaultHost());
        }
        
        if (host == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        host.getPipeline().getFirst().invoke(request, response);
    }
}
```

### 3.4 Pipeline实现

```java
public interface Pipeline {
    Valve getFirst();
    Valve getBasic();
    void addValve(Valve valve);
    void removeValve(Valve valve);
    Valve[] getValves();
}

public class StandardPipeline implements Pipeline {
    protected Container container;
    protected Valve first;
    protected Valve basic;
    
    public StandardPipeline(Container container) {
        this.container = container;
    }
    
    @Override
    public Valve getFirst() {
        return first;
    }
    
    @Override
    public Valve getBasic() {
        return basic;
    }
    
    @Override
    public void setBasic(Valve valve) {
        this.basic = valve;
        if (first == null) {
            first = valve;
        }
    }
    
    @Override
    public void addValve(Valve valve) {
        if (first == null) {
            first = valve;
            valve.setNext(basic);
        } else {
            Valve current = first;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(valve);
            valve.setNext(basic);
        }
    }
    
    @Override
    public void removeValve(Valve valve) {
        Valve current = first;
        Valve previous = null;
        
        while (current != null && current != valve) {
            previous = current;
            current = current.getNext();
        }
        
        if (current == valve) {
            if (previous == null) {
                first = current.getNext();
            } else {
                previous.setNext(current.getNext());
            }
        }
    }
    
    @Override
    public Valve[] getValves() {
        List<Valve> valves = new ArrayList<>();
        Valve current = first;
        
        while (current != null && current != basic) {
            valves.add(current);
            current = current.getNext();
        }
        
        return valves.toArray(new Valve[0]);
    }
}
```

## 4. 模板方法模式（Template Method Pattern）

### 4.1 概述

模板方法模式定义一个操作中的算法的骨架，而将一些步骤延迟到子类中。模板方法使得子类可以不改变一个算法的结构即可重定义该算法的某些特定步骤。

### 4.2 Tomcat中的应用

Tomcat的LifecycleBase就是模板方法模式的典型应用。

### 4.3 代码示例

```java
public abstract class LifecycleBase implements Lifecycle {
    
    @Override
    public final void init() throws LifecycleException {
        if (state.equals(LifecycleState.NEW)) {
            setStateInternal(LifecycleState.INITIALIZING, null, false);
            try {
                initInternal();
            } catch (Throwable t) {
                setStateInternal(LifecycleState.FAILED, null, false);
                throw new LifecycleException("初始化失败", t);
            }
            setStateInternal(LifecycleState.INITIALIZED, null, false);
        } else {
            throw new LifecycleException("已经初始化");
        }
    }
    
    @Override
    public final void start() throws LifecycleException {
        if (state.equals(LifecycleState.NEW)) {
            init();
        }
        
        setStateInternal(LifecycleState.STARTING_PREP, null, false);
        
        try {
            startInternal();
        } catch (Throwable t) {
            setStateInternal(LifecycleState.FAILED, null, false);
            throw new LifecycleException("启动失败", t);
        }
        
        setStateInternal(LifecycleState.STARTED, null, false);
    }
    
    @Override
    public final void stop() throws LifecycleException {
        setStateInternal(LifecycleState.STOPPING_PREP, null, false);
        
        try {
            stopInternal();
        } catch (Throwable t) {
            setStateInternal(LifecycleState.FAILED, null, false);
            throw new LifecycleException("停止失败", t);
        }
        
        setStateInternal(LifecycleState.STOPPED, null, false);
    }
    
    protected abstract void initInternal() throws LifecycleException;
    
    protected abstract void startInternal() throws LifecycleException;
    
    protected abstract void stopInternal() throws LifecycleException;
}

public class StandardEngine extends LifecycleBase {
    
    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        
        for (Container child : findChildren()) {
            if (child instanceof Lifecycle) {
                ((Lifecycle) child).init();
            }
        }
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        super.startInternal();
        
        for (Container child : findChildren()) {
            if (child instanceof Lifecycle) {
                ((Lifecycle) child).start();
            }
        }
    }
    
    @Override
    protected void stopInternal() throws LifecycleException {
        super.stopInternal();
        
        for (Container child : findChildren()) {
            if (child instanceof Lifecycle) {
                ((Lifecycle) child).stop();
            }
        }
    }
}
```

## 5. 观察者模式（Observer Pattern）

### 5.1 概述

观察者模式定义对象间的一种一对多的依赖关系，当一个对象的状态发生改变时，所有依赖于它的对象都得到通知并被自动更新。

### 5.2 Tomcat中的应用

Tomcat的LifecycleListener就是观察者模式的典型应用。

### 5.3 代码示例

```java
public interface LifecycleListener {
    void lifecycleEvent(LifecycleEvent event);
}

public class LifecycleEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    
    private final String type;
    private final Object data;
    
    public LifecycleEvent(Lifecycle lifecycle, String type) {
        this(lifecycle, type, null);
    }
    
    public LifecycleEvent(Lifecycle lifecycle, String type, Object data) {
        super(lifecycle);
        this.type = type;
        this.data = data;
    }
    
    public Lifecycle getLifecycle() {
        return (Lifecycle) getSource();
    }
    
    public String getType() {
        return type;
    }
    
    public Object getData() {
        return data;
    }
}

public abstract class LifecycleBase implements Lifecycle {
    private final List<LifecycleListener> lifecycleListeners = new CopyOnWriteArrayList<>();
    
    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        lifecycleListeners.add(listener);
    }
    
    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return lifecycleListeners.toArray(new LifecycleListener[0]);
    }
    
    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        lifecycleListeners.remove(listener);
    }
    
    protected void fireLifecycleEvent(String type, Object data) {
        LifecycleEvent event = new LifecycleEvent(this, type, data);
        for (LifecycleListener listener : lifecycleListeners) {
            listener.lifecycleEvent(event);
        }
    }
}

public class MyLifecycleListener implements LifecycleListener {
    
    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        String type = event.getType();
        Lifecycle lifecycle = event.getLifecycle();
        
        switch (type) {
            case Lifecycle.BEFORE_START_EVENT:
                System.out.println("组件即将启动: " + lifecycle);
                break;
            case Lifecycle.START_EVENT:
                System.out.println("组件启动中: " + lifecycle);
                break;
            case Lifecycle.AFTER_START_EVENT:
                System.out.println("组件启动完成: " + lifecycle);
                break;
            case Lifecycle.BEFORE_STOP_EVENT:
                System.out.println("组件即将停止: " + lifecycle);
                break;
            case Lifecycle.STOP_EVENT:
                System.out.println("组件停止中: " + lifecycle);
                break;
            case Lifecycle.AFTER_STOP_EVENT:
                System.out.println("组件停止完成: " + lifecycle);
                break;
        }
    }
}
```

## 6. 工厂模式（Factory Pattern）

### 6.1 概述

工厂模式提供了一种创建对象的最佳方式，在创建对象时不会对客户端暴露创建逻辑，并且是通过使用一个共同的接口来指向新创建的对象。

### 6.2 Tomcat中的应用

Tomcat的ObjectFactory就是工厂模式的典型应用。

### 6.3 代码示例

```java
public interface ObjectFactory {
    Object getObjectInstance(Object obj, Name name, Context nameCtx,
                          Hashtable<?, ?> environment) throws Exception;
}

public class StandardServerSocketFactory implements ObjectFactory {
    
    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                  Hashtable<?, ?> environment) throws Exception {
        if (obj instanceof String) {
            String className = (String) obj;
            try {
                Class<?> clazz = Class.forName(className);
                return clazz.newInstance();
            } catch (Exception e) {
                throw new NamingException("无法创建ServerSocketFactory", e);
            }
        }
        return null;
    }
}

public class StandardSocketFactory implements ObjectFactory {
    
    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                  Hashtable<?, ?> environment) throws Exception {
        if (obj instanceof String) {
            String className = (String) obj;
            try {
                Class<?> clazz = Class.forName(className);
                return clazz.newInstance();
            } catch (Exception e) {
                throw new NamingException("无法创建SocketFactory", e);
            }
        }
        return null;
    }
}
```

## 7. 单例模式（Singleton Pattern）

### 7.1 概述

单例模式确保一个类只有一个实例，并提供一个全局访问点。

### 7.2 Tomcat中的应用

Tomcat的Server实例就是单例模式的典型应用。

### 7.3 代码示例

```java
public class ServerFactory {
    private static volatile Server server;
    
    private ServerFactory() {
    }
    
    public static Server getServer() {
        if (server == null) {
            synchronized (ServerFactory.class) {
                if (server == null) {
                    server = new StandardServer();
                }
            }
        }
        return server;
    }
    
    public static void setServer(Server server) {
        ServerFactory.server = server;
    }
    
    public static void clear() {
        server = null;
    }
}
```

## 8. 适配器模式（Adapter Pattern）

### 8.1 概述

适配器模式将一个类的接口转换成客户希望的另外一个接口，使得原本由于接口不兼容而不能一起工作的那些类可以一起工作。

### 8.2 Tomcat中的应用

Tomcat的CoyoteAdapter就是适配器模式的典型应用。

### 8.3 代码示例

```java
public interface Adapter {
    void service(Request req, Response res) throws Exception;
}

public class CoyoteAdapter implements Adapter {
    
    private Connector connector;
    private Engine engine;
    
    public CoyoteAdapter(Connector connector, Engine engine) {
        this.connector = connector;
        this.engine = engine;
    }
    
    @Override
    public void service(Request req, Response res) throws Exception {
        org.apache.catalina.connector.Request request = 
            (org.apache.catalina.connector.Request) req.getNote(ADAPTER_NOTES);
        org.apache.catalina.connector.Response response = 
            (org.apache.catalina.connector.Response) res.getNote(ADAPTER_NOTES);
        
        request.setConnector(connector);
        response.setConnector(connector);
        
        engine.service(request, response);
    }
}
```

## 9. 装饰器模式（Decorator Pattern）

### 9.1 概述

装饰器模式动态地给一个对象添加一些额外的职责，就增加功能来说，装饰器模式相比生成子类更为灵活。

### 9.2 Tomcat中的应用

Tomcat的Request和Response的包装就是装饰器模式的典型应用。

### 9.3 代码示例

```java
public class HttpServletRequestWrapper implements HttpServletRequest {
    private HttpServletRequest request;
    
    public HttpServletRequestWrapper(HttpServletRequest request) {
        this.request = request;
    }
    
    @Override
    public String getMethod() {
        return request.getMethod();
    }
    
    @Override
    public String getRequestURI() {
        return request.getRequestURI();
    }
    
    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }
}

public class XssRequestWrapper extends HttpServletRequestWrapper {
    
    public XssRequestWrapper(HttpServletRequest request) {
        super(request);
    }
    
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return cleanXSS(value);
    }
    
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        
        String[] cleanValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleanValues[i] = cleanXSS(values[i]);
        }
        
        return cleanValues;
    }
    
    private String cleanXSS(String value) {
        if (value == null) {
            return null;
        }
        
        value = value.replaceAll("<", "&lt;");
        value = value.replaceAll(">", "&gt;");
        value = value.replaceAll("\"", "&quot;");
        value = value.replaceAll("'", "&#x27;");
        value = value.replaceAll("/", "&#x2F;");
        
        return value;
    }
}
```

## 10. 策略模式（Strategy Pattern）

### 10.1 概述

策略模式定义了一系列算法，并将每个算法封装起来，使它们可以相互替换，且算法的变化不会影响使用算法的客户。

### 10.2 Tomcat中的应用

Tomcat的Connector的Protocol实现就是策略模式的典型应用。

### 10.3 代码示例

```java
public interface ProtocolHandler {
    void init() throws Exception;
    void start() throws Exception;
    void stop() throws Exception;
    void destroy() throws Exception;
}

public class Http11NioProtocol implements ProtocolHandler {
    
    private NioEndpoint endpoint;
    
    public Http11NioProtocol() {
        endpoint = new NioEndpoint();
    }
    
    @Override
    public void init() throws Exception {
        endpoint.init();
    }
    
    @Override
    public void start() throws Exception {
        endpoint.start();
    }
    
    @Override
    public void stop() throws Exception {
        endpoint.stop();
    }
    
    @Override
    public void destroy() throws Exception {
        endpoint.destroy();
    }
}

public class Http11AprProtocol implements ProtocolHandler {
    
    private AprEndpoint endpoint;
    
    public Http11AprProtocol() {
        endpoint = new AprEndpoint();
    }
    
    @Override
    public void init() throws Exception {
        endpoint.init();
    }
    
    @Override
    public void start() throws Exception {
        endpoint.start();
    }
    
    @Override
    public void stop() throws Exception {
        endpoint.stop();
    }
    
    @Override
    public void destroy() throws Exception {
        endpoint.destroy();
    }
}
```

## 11. 代理模式（Proxy Pattern）

### 11.1 概述

代理模式为其他对象提供一种代理以控制对这个对象的访问。

### 11.2 Tomcat中的应用

Tomcat的JNDI代理就是代理模式的典型应用。

### 11.3 代码示例

```java
public class JndiProxy implements Context {
    
    private Context delegate;
    
    public JndiProxy(Context delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public Object lookup(Name name) throws NamingException {
        return delegate.lookup(name);
    }
    
    @Override
    public Object lookup(String name) throws NamingException {
        return delegate.lookup(name);
    }
    
    @Override
    public void bind(Name name, Object obj) throws NamingException {
        delegate.bind(name, obj);
    }
    
    @Override
    public void bind(String name, Object obj) throws NamingException {
        delegate.bind(name, obj);
    }
}
```

## 12. 总结

Tomcat中使用了多种设计模式，这些设计模式使得Tomcat具有良好的架构设计：

| 设计模式 | 应用场景 | 优势 |
|---------|---------|------|
| 组合模式 | Container层次结构 | 灵活管理容器层次 |
| 责任链模式 | Pipeline和Valve | 灵活处理请求 |
| 模板方法模式 | Lifecycle管理 | 统一生命周期管理 |
| 观察者模式 | LifecycleListener | 解耦事件通知 |
| 工厂模式 | ObjectFactory | 灵活创建对象 |
| 单例模式 | Server实例 | 确保唯一实例 |
| 适配器模式 | CoyoteAdapter | 兼容不同接口 |
| 装饰器模式 | Request/Response包装 | 动态添加功能 |
| 策略模式 | ProtocolHandler | 灵活切换算法 |
| 代理模式 | JNDI代理 | 控制对象访问 |

理解Tomcat中使用的设计模式，对于：
- 深入理解Tomcat架构
- 学习优秀的架构设计
- 开发Tomcat扩展
- 提高系统设计能力

都具有重要意义。Tomcat的设计模式应用是其优秀架构设计的重要体现，为Java Web应用提供了强大而灵活的支持。