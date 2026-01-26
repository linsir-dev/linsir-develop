# Tomcat LifeCycle机制

## 1. 概述

Tomcat的LifeCycle机制是管理组件生命周期的重要机制，它定义了组件从创建到销毁的完整生命周期。通过LifeCycle机制，Tomcat可以统一管理所有组件的初始化、启动、停止和销毁过程。

## 2. LifeCycle接口

### 2.1 LifeCycle接口定义

LifeCycle接口定义了组件生命周期的基本方法。

```java
public interface Lifecycle {
    String START_EVENT = "start";
    String BEFORE_START_EVENT = "before_start";
    String AFTER_START_EVENT = "after_start";
    String STOP_EVENT = "stop";
    String BEFORE_STOP_EVENT = "before_stop";
    String AFTER_STOP_EVENT = "after_stop";
    
    void addLifecycleListener(LifecycleListener listener);
    LifecycleListener[] findLifecycleListeners();
    void removeLifecycleListener(LifecycleListener listener);
    
    void init() throws LifecycleException;
    void start() throws LifecycleException;
    void stop() throws LifecycleException;
    void destroy() throws LifecycleException;
    
    LifecycleState getState();
    String getStateName();
}
```

### 2.2 LifecycleState枚举

LifecycleState定义了组件的所有可能状态。

```java
public enum LifecycleState {
    NEW(false),
    INITIALIZING(false),
    INITIALIZED(false),
    STARTING_PREP(false),
    STARTING(true),
    STARTED(true),
    STOPPING_PREP(true),
    STOPPING(false),
    STOPPED(false),
    DESTROYING(false),
    DESTROYED(false),
    FAILED(false);
    
    private final boolean available;
    
    LifecycleState(boolean available) {
        this.available = available;
    }
    
    public boolean isAvailable() {
        return available;
    }
}
```

**状态转换图**：

```
┌─────────┐
│  NEW    │
└────┬────┘
     │ init()
     ▼
┌─────────────────┐
│ INITIALIZING   │
└────┬────────────┘
     │
     ▼
┌─────────────────┐
│  INITIALIZED    │
└────┬────────────┘
     │ start()
     ▼
┌─────────────────┐
│ STARTING_PREP  │
└────┬────────────┘
     │
     ▼
┌─────────────────┐
│   STARTING     │
└────┬────────────┘
     │
     ▼
┌─────────────────┐
│   STARTED      │
└────┬────────────┘
     │ stop()
     ▼
┌─────────────────┐
│ STOPPING_PREP  │
└────┬────────────┘
     │
     ▼
┌─────────────────┐
│   STOPPING     │
└────┬────────────┘
     │
     ▼
┌─────────────────┐
│   STOPPED      │
└────┬────────────┘
     │ destroy()
     ▼
┌─────────────────┐
│  DESTROYING    │
└────┬────────────┘
     │
     ▼
┌─────────────────┐
│  DESTROYED     │
└─────────────────┘
```

## 3. LifecycleBase

### 3.1 LifecycleBase概述

LifecycleBase是LifeCycle接口的抽象基类，提供了生命周期管理的通用实现。

**主要功能**：
- 状态管理
- 事件通知
- 生命周期方法实现

### 3.2 LifecycleBase实现

```java
public abstract class LifecycleBase implements Lifecycle {
    private static final StringManager sm = StringManager.getManager(LifecycleBase.class);
    
    private final List<LifecycleListener> lifecycleListeners = new CopyOnWriteArrayList<>();
    private volatile LifecycleState state = LifecycleState.NEW;
    protected Object stateLock = new Object();
    
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
    
    @Override
    public LifecycleState getState() {
        return state;
    }
    
    @Override
    public String getStateName() {
        return getState().toString();
    }
    
    @Override
    public final void init() throws LifecycleException {
        if (state.equals(LifecycleState.NEW)) {
            setStateInternal(LifecycleState.INITIALIZING, null, false);
            try {
                initInternal();
            } catch (Throwable t) {
                setStateInternal(LifecycleState.FAILED, null, false);
                throw new LifecycleException(sm.getString("lifecycleBase.initFail", toString()), t);
            }
            setStateInternal(LifecycleState.INITIALIZED, null, false);
        } else {
            throw new LifecycleException(sm.getString("lifecycleBase.alreadyInitialized", toString()));
        }
    }
    
    @Override
    public final void start() throws LifecycleException {
        if (LifecycleState.STARTING_PREP.equals(state) || 
            LifecycleState.STARTING.equals(state) || 
            LifecycleState.STARTED.equals(state)) {
            throw new LifecycleException(sm.getString("lifecycleBase.alreadyStarted", toString()));
        }
        
        if (state.equals(LifecycleState.NEW)) {
            init();
        } else if (state.equals(LifecycleState.FAILED)) {
            stop();
        } else if (!state.equals(LifecycleState.INITIALIZED) && 
                   !state.equals(LifecycleState.STOPPED)) {
            throw new LifecycleException(sm.getString("lifecycleBase.invalidTransition", 
                new Object[] {state.name(), LifecycleState.STARTING_PREP.name()}));
        }
        
        setStateInternal(LifecycleState.STARTING_PREP, null, false);
        
        try {
            startInternal();
        } catch (Throwable t) {
            setStateInternal(LifecycleState.FAILED, null, false);
            throw new LifecycleException(sm.getString("lifecycleBase.startFail", toString()), t);
        }
        
        if (state.equals(LifecycleState.FAILED)) {
            stop();
        } else if (!state.equals(LifecycleState.STARTING)) {
            throw new LifecycleException(sm.getString("lifecycleBase.startFail", toString()));
        } else {
            setStateInternal(LifecycleState.STARTED, null, false);
        }
    }
    
    @Override
    public final void stop() throws LifecycleException {
        if (LifecycleState.STOPPING_PREP.equals(state) || 
            LifecycleState.STOPPING.equals(state) || 
            LifecycleState.STOPPED.equals(state)) {
            throw new LifecycleException(sm.getString("lifecycleBase.alreadyStopped", toString()));
        }
        
        if (state.equals(LifecycleState.NEW)) {
            state = LifecycleState.STOPPED;
            return;
        }
        
        if (!state.equals(LifecycleState.STARTED) && 
            !state.equals(LifecycleState.FAILED)) {
            throw new LifecycleException(sm.getString("lifecycleBase.invalidTransition", 
                new Object[] {state.name(), LifecycleState.STOPPING_PREP.name()}));
        }
        
        if (state.equals(LifecycleState.FAILED)) {
            setStateInternal(LifecycleState.STOPPING_PREP, null, false);
        } else {
            setStateInternal(LifecycleState.STOPPING_PREP, null, false);
        }
        
        try {
            stopInternal();
        } catch (Throwable t) {
            setStateInternal(LifecycleState.FAILED, null, false);
            throw new LifecycleException(sm.getString("lifecycleBase.stopFail", toString()), t);
        }
        
        if (state.equals(LifecycleState.FAILED)) {
            return;
        }
        
        setStateInternal(LifecycleState.STOPPED, null, false);
    }
    
    @Override
    public final void destroy() throws LifecycleException {
        if (state.equals(LifecycleState.DESTROYED)) {
            return;
        }
        
        if (!state.equals(LifecycleState.STOPPED) && 
            !state.equals(LifecycleState.FAILED) && 
            !state.equals(LifecycleState.NEW) && 
            !state.equals(LifecycleState.INITIALIZED)) {
            throw new LifecycleException(sm.getString("lifecycleBase.invalidTransition", 
                new Object[] {state.name(), LifecycleState.DESTROYING.name()}));
        }
        
        setStateInternal(LifecycleState.DESTROYING, null, false);
        
        try {
            destroyInternal();
        } catch (Throwable t) {
            setStateInternal(LifecycleState.FAILED, null, false);
            throw new LifecycleException(sm.getString("lifecycleBase.destroyFail", toString()), t);
        }
        
        setStateInternal(LifecycleState.DESTROYED, null, false);
    }
    
    protected abstract void initInternal() throws LifecycleException;
    
    protected abstract void startInternal() throws LifecycleException;
    
    protected abstract void stopInternal() throws LifecycleException;
    
    protected abstract void destroyInternal() throws LifecycleException;
    
    protected void setStateInternal(LifecycleState state, Object data, boolean check) {
        synchronized (stateLock) {
            if (check) {
                if (state == null) {
                    throw new NullPointerException(sm.getString("lifecycleBase.setState.nullState"));
                }
                if (this.state == null) {
                    throw new NullPointerException(sm.getString("lifecycleBase.setState.nullCurrentState"));
                }
                if (!this.state.validTransition(state)) {
                    throw new LifecycleException(sm.getString("lifecycleBase.invalidTransition", 
                        new Object[] {this.state.name(), state.name()}));
                }
            }
            
            this.state = state;
            
            String lifecycleEvent = state.getLifecycleEvent();
            if (lifecycleEvent != null) {
                fireLifecycleEvent(lifecycleEvent, data);
            }
        }
    }
    
    protected void fireLifecycleEvent(String type, Object data) {
        LifecycleEvent event = new LifecycleEvent(this, type, data);
        for (LifecycleListener listener : lifecycleListeners) {
            listener.lifecycleEvent(event);
        }
    }
}
```

## 4. LifecycleListener

### 4.1 LifecycleListener接口

LifecycleListener用于监听组件的生命周期事件。

```java
public interface LifecycleListener {
    void lifecycleEvent(LifecycleEvent event);
}
```

### 4.2 LifecycleEvent

LifecycleEvent表示生命周期事件。

```java
public final class LifecycleEvent extends EventObject {
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
        return this.type;
    }
    
    public Object getData() {
        return this.data;
    }
}
```

### 4.3 自定义LifecycleListener

```java
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

## 5. 组件生命周期实现

### 5.1 Server生命周期

```java
public class StandardServer extends LifecycleMBeanBase implements Server {
    
    private Service[] services = new Service[0];
    private int port = -1;
    private String shutdown;
    
    public StandardServer() {
        super();
    }
    
    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        
        // 初始化所有Service
        for (int i = 0; i < services.length; i++) {
            services[i].init();
        }
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        super.startInternal();
        
        // 启动所有Service
        for (int i = 0; i < services.length; i++) {
            services[i].start();
        }
        
        // 启动关闭监听器
        if (port == -1) {
            return;
        }
        
        try {
            shutdownSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            throw new LifecycleException(sm.getString("standardServer.shutdownSocket.fail", port), e);
        }
        
        shutdownThread = new Thread(new ShutdownListener(), "ContainerShutdownThread");
        shutdownThread.setDaemon(true);
        shutdownThread.start();
    }
    
    @Override
    protected void stopInternal() throws LifecycleException {
        super.stopInternal();
        
        // 停止所有Service
        for (int i = 0; i < services.length; i++) {
            services[i].stop();
        }
        
        // 关闭关闭监听器
        if (shutdownSocket != null) {
            try {
                shutdownSocket.close();
            } catch (IOException e) {
                // Ignore
            }
            shutdownSocket = null;
        }
        
        if (shutdownThread != null) {
            shutdownThread.interrupt();
            shutdownThread = null;
        }
    }
    
    @Override
    protected void destroyInternal() throws LifecycleException {
        super.destroyInternal();
        
        // 销毁所有Service
        for (int i = 0; i < services.length; i++) {
            services[i].destroy();
        }
    }
    
    protected class ShutdownListener implements Runnable {
        @Override
        public void run() {
            try {
                Socket socket = shutdownSocket.accept();
                if (shutdown != null) {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                    String command = reader.readLine();
                    if (shutdown.equals(command)) {
                        stop();
                        await();
                    }
                }
            } catch (IOException e) {
                // Ignore
            }
        }
    }
}
```

### 5.2 Service生命周期

```java
public class StandardService extends LifecycleMBeanBase implements Service {
    
    private String name;
    private Server server;
    private Connector[] connectors = new Connector[0];
    private Engine engine;
    
    public StandardService() {
        super();
    }
    
    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        
        // 初始化Engine
        if (engine != null) {
            engine.init();
        }
        
        // 初始化所有Connector
        for (int i = 0; i < connectors.length; i++) {
            connectors[i].init();
        }
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        super.startInternal();
        
        // 启动Engine
        if (engine != null) {
            engine.start();
        }
        
        // 启动所有Connector
        for (int i = 0; i < connectors.length; i++) {
            connectors[i].start();
        }
    }
    
    @Override
    protected void stopInternal() throws LifecycleException {
        super.stopInternal();
        
        // 停止所有Connector
        for (int i = 0; i < connectors.length; i++) {
            connectors[i].stop();
        }
        
        // 停止Engine
        if (engine != null) {
            engine.stop();
        }
    }
    
    @Override
    protected void destroyInternal() throws LifecycleException {
        super.destroyInternal();
        
        // 销毁所有Connector
        for (int i = 0; i < connectors.length; i++) {
            connectors[i].destroy();
        }
        
        // 销毁Engine
        if (engine != null) {
            engine.destroy();
        }
    }
}
```

### 5.3 Container生命周期

```java
public abstract class ContainerBase extends LifecycleMBeanBase implements Container {
    
    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        
        // 初始化Pipeline
        if (pipeline instanceof Lifecycle) {
            ((Lifecycle) pipeline).init();
        }
        
        // 初始化所有子容器
        for (Container child : findChildren()) {
            if (child instanceof Lifecycle) {
                ((Lifecycle) child).init();
            }
        }
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        super.startInternal();
        
        // 启动Pipeline
        if (pipeline instanceof Lifecycle) {
            ((Lifecycle) pipeline).start();
        }
        
        // 启动所有子容器
        for (Container child : findChildren()) {
            if (child instanceof Lifecycle) {
                ((Lifecycle) child).start();
            }
        }
    }
    
    @Override
    protected void stopInternal() throws LifecycleException {
        super.stopInternal();
        
        // 停止所有子容器
        for (Container child : findChildren()) {
            if (child instanceof Lifecycle) {
                ((Lifecycle) child).stop();
            }
        }
        
        // 停止Pipeline
        if (pipeline instanceof Lifecycle) {
            ((Lifecycle) pipeline).stop();
        }
    }
    
    @Override
    protected void destroyInternal() throws LifecycleException {
        super.destroyInternal();
        
        // 销毁所有子容器
        for (Container child : findChildren()) {
            if (child instanceof Lifecycle) {
                ((Lifecycle) child).destroy();
            }
        }
        
        // 销毁Pipeline
        if (pipeline instanceof Lifecycle) {
            ((Lifecycle) pipeline).destroy();
        }
    }
}
```

## 6. 生命周期事件

### 6.1 事件类型

| 事件类型 | 说明 | 触发时机 |
|---------|------|---------|
| BEFORE_START_EVENT | 组件即将启动 | start()方法调用前 |
| START_EVENT | 组件启动中 | startInternal()方法调用时 |
| AFTER_START_EVENT | 组件启动完成 | startInternal()方法调用后 |
| BEFORE_STOP_EVENT | 组件即将停止 | stop()方法调用前 |
| STOP_EVENT | 组件停止中 | stopInternal()方法调用时 |
| AFTER_STOP_EVENT | 组件停止完成 | stopInternal()方法调用后 |

### 6.2 事件监听示例

```java
public class LifecycleMonitor implements LifecycleListener {
    
    private static final Logger logger = LoggerFactory.getLogger(LifecycleMonitor.class);
    
    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        String type = event.getType();
        Lifecycle lifecycle = event.getLifecycle();
        
        switch (type) {
            case Lifecycle.BEFORE_START_EVENT:
                logger.info("组件即将启动: {}", lifecycle);
                break;
            case Lifecycle.START_EVENT:
                logger.info("组件启动中: {}", lifecycle);
                break;
            case Lifecycle.AFTER_START_EVENT:
                logger.info("组件启动完成: {}", lifecycle);
                break;
            case Lifecycle.BEFORE_STOP_EVENT:
                logger.info("组件即将停止: {}", lifecycle);
                break;
            case Lifecycle.STOP_EVENT:
                logger.info("组件停止中: {}", lifecycle);
                break;
            case Lifecycle.AFTER_STOP_EVENT:
                logger.info("组件停止完成: {}", lifecycle);
                break;
        }
    }
}
```

## 7. 使用示例

### 7.1 添加生命周期监听器

```java
public class TomcatServer {
    
    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.setBaseDir(".");
        
        // 添加生命周期监听器
        tomcat.getServer().addLifecycleListener(new MyLifecycleListener());
        
        // 添加Context
        StandardContext context = new StandardContext();
        context.setPath("");
        context.setDocBase(".");
        context.addLifecycleListener(new MyLifecycleListener());
        
        tomcat.getHost().addChild(context);
        
        // 启动Tomcat
        tomcat.start();
        tomcat.getServer().await();
    }
}
```

### 7.2 自定义生命周期组件

```java
public class MyComponent extends LifecycleBase {
    
    private String name;
    private boolean initialized = false;
    private boolean started = false;
    
    public MyComponent(String name) {
        this.name = name;
    }
    
    @Override
    protected void initInternal() throws LifecycleException {
        System.out.println(name + " 初始化中...");
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new LifecycleException("初始化被中断", e);
        }
        
        initialized = true;
        System.out.println(name + " 初始化完成");
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        System.out.println(name + " 启动中...");
        
        if (!initialized) {
            throw new LifecycleException("组件未初始化");
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new LifecycleException("启动被中断", e);
        }
        
        started = true;
        System.out.println(name + " 启动完成");
    }
    
    @Override
    protected void stopInternal() throws LifecycleException {
        System.out.println(name + " 停止中...");
        
        if (!started) {
            throw new LifecycleException("组件未启动");
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new LifecycleException("停止被中断", e);
        }
        
        started = false;
        System.out.println(name + " 停止完成");
    }
    
    @Override
    protected void destroyInternal() throws LifecycleException {
        System.out.println(name + " 销毁中...");
        
        if (started) {
            throw new LifecycleException("组件未停止");
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new LifecycleException("销毁被中断", e);
        }
        
        initialized = false;
        System.out.println(name + " 销毁完成");
    }
    
    @Override
    public String toString() {
        return "MyComponent[" + name + "]";
    }
}
```

## 8. 最佳实践

### 8.1 生命周期管理建议

1. **正确使用状态**：在实现生命周期方法时，要正确处理状态转换
2. **异常处理**：在生命周期方法中要妥善处理异常
3. **资源清理**：在destroy方法中要正确释放资源
4. **事件通知**：在状态变化时要及时通知监听器
5. **线程安全**：要考虑多线程环境下的状态管理

### 8.2 性能优化

1. **延迟初始化**：将耗时的初始化操作延迟到start方法
2. **异步启动**：对于耗时的启动操作，可以考虑异步执行
3. **资源复用**：在停止时保留可复用的资源，避免重复创建
4. **状态检查**：在执行操作前检查状态，避免无效操作

### 8.3 错误处理

```java
public class RobustComponent extends LifecycleBase {
    
    @Override
    protected void initInternal() throws LifecycleException {
        try {
            // 初始化逻辑
            initialize();
        } catch (Exception e) {
            // 记录错误日志
            log.error("初始化失败", e);
            
            // 清理资源
            cleanup();
            
            // 抛出异常
            throw new LifecycleException("初始化失败", e);
        }
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        try {
            // 启动逻辑
            start();
        } catch (Exception e) {
            // 记录错误日志
            log.error("启动失败", e);
            
            // 尝试停止
            try {
                stopInternal();
            } catch (Exception ex) {
                log.error("停止失败", ex);
            }
            
            // 抛出异常
            throw new LifecycleException("启动失败", e);
        }
    }
    
    @Override
    protected void stopInternal() throws LifecycleException {
        try {
            // 停止逻辑
            stop();
        } catch (Exception e) {
            // 记录错误日志
            log.error("停止失败", e);
            
            // 抛出异常
            throw new LifecycleException("停止失败", e);
        }
    }
    
    @Override
    protected void destroyInternal() throws LifecycleException {
        try {
            // 销毁逻辑
            destroy();
        } catch (Exception e) {
            // 记录错误日志
            log.error("销毁失败", e);
            
            // 抛出异常
            throw new LifecycleException("销毁失败", e);
        }
    }
}
```

## 9. 总结

Tomcat的LifeCycle机制是其核心功能之一，具有以下特点：

1. **统一管理**：所有组件都实现LifeCycle接口，统一管理生命周期
2. **状态管理**：通过LifecycleState枚举管理组件状态
3. **事件通知**：通过LifecycleListener监听生命周期事件
4. **模板方法**：LifecycleBase提供模板方法，简化子类实现
5. **异常处理**：完善的异常处理机制，确保组件安全

理解Tomcat的LifeCycle机制，对于：
- 开发Tomcat扩展
- 管理组件生命周期
- 实现自定义组件
- 排查启动和停止问题

都具有重要意义。Tomcat的LifeCycle机制是其优秀架构设计的重要体现，为组件管理提供了强大而灵活的支持。