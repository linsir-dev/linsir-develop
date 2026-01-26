# Tomcat Container设计

## 1. 概述

Container是Tomcat的核心组件之一，负责管理和执行Servlet。Tomcat的Container采用了分层设计，从外到内依次为：Engine、Host、Context、Wrapper。这种设计使得Tomcat能够灵活地管理多个Web应用和Servlet。

## 2. Container层次结构

### 2.1 层次结构图

```
┌─────────────────────────────────────────┐
│           Engine (引擎)                  │
│  ┌─────────────────────────────────┐   │
│  │        Host (虚拟主机)          │   │
│  │  ┌─────────────────────────┐   │   │
│  │  │     Context (Web应用)    │   │   │
│  │  │  ┌─────────────────┐   │   │   │
│  │  │  │  Wrapper (Servlet)│   │   │   │
│  │  │  └─────────────────┘   │   │   │
│  │  └─────────────────────────┘   │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

### 2.2 Container接口层次

```java
public interface Container {
    // 基础方法
    String getName();
    void setName(String name);
    Container getParent();
    void setParent(Container container);
    
    // 子容器管理
    void addChild(Container child);
    Container findChild(String name);
    Container[] findChildren();
    void removeChild(Container child);
    
    // 生命周期管理
    void init() throws Exception;
    void start() throws Exception;
    void stop() throws Exception;
    void destroy() throws Exception;
    
    // 请求处理
    void addValve(Valve valve);
    Valve[] getValves();
    void removeValve(Valve valve);
    Pipeline getPipeline();
}

public interface Engine extends Container {
    String getDefaultHost();
    void setDefaultHost(String defaultHost);
    Service getService();
    void setService(Service service);
}

public interface Host extends Container {
    String getAppBase();
    void setAppBase(String appBase);
    boolean getAutoDeploy();
    void setAutoDeploy(boolean autoDeploy);
    boolean getDeployOnStartup();
    void setDeployOnStartup(boolean deployOnStartup);
}

public interface Context extends Container {
    String getPath();
    void setPath(String path);
    String getDocBase();
    void setDocBase(String docBase);
    ClassLoader getLoader();
    void setLoader(ClassLoader loader);
}

public interface Wrapper extends Container {
    String getServletClass();
    void setServletClass(String servletClass);
    Servlet getServlet();
    void setServlet(Servlet servlet);
    void load() throws ServletException;
    void unload() throws ServletException;
}
```

## 3. Engine设计

### 3.1 Engine概述

Engine是Container的最顶层，代表整个Servlet引擎。一个Service只能有一个Engine。

**主要职责**：
- 管理多个Host
- 处理所有Host的请求
- 提供基本的容器功能
- 作为Host的父容器

**配置示例**：

```xml
<Engine name="Catalina" defaultHost="localhost">
    <Host name="localhost" appBase="webapps" unpackWARs="true">
        <!-- Context配置 -->
    </Host>
    
    <Host name="www.example.com" appBase="webapps-example">
        <!-- Context配置 -->
    </Host>
</Engine>
```

### 3.2 Engine实现

```java
public class StandardEngine extends ContainerBase implements Engine {
    private String defaultHost = "localhost";
    private Service service;
    private String jvmRoute;
    
    public StandardEngine() {
        pipeline.setBasic(new StandardEngineValve());
    }
    
    @Override
    public void addChild(Container child) {
        if (!(child instanceof Host)) {
            throw new IllegalArgumentException(
                "Child container of Engine must be Host");
        }
        super.addChild(child);
    }
    
    @Override
    public String getDefaultHost() {
        return defaultHost;
    }
    
    @Override
    public void setDefaultHost(String defaultHost) {
        this.defaultHost = defaultHost;
    }
    
    @Override
    public Service getService() {
        return service;
    }
    
    @Override
    public void setService(Service service) {
        this.service = service;
    }
    
    public String getJvmRoute() {
        return jvmRoute;
    }
    
    public void setJvmRoute(String jvmRoute) {
        this.jvmRoute = jvmRoute;
    }
    
    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        
        // 初始化所有Host
        for (Container child : findChildren()) {
            if (child instanceof Lifecycle) {
                ((Lifecycle) child).init();
            }
        }
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        super.startInternal();
        
        // 启动所有Host
        for (Container child : findChildren()) {
            if (child instanceof Lifecycle) {
                ((Lifecycle) child).start();
            }
        }
    }
}
```

### 3.3 Engine Valve

Engine Valve负责处理Engine级别的请求。

```java
public class StandardEngineValve extends ValveBase {
    
    @Override
    public void invoke(Request request, Response response) 
            throws IOException, ServletException {
        
        // 获取请求的Host头
        String hostName = request.getServerName();
        
        // 选择对应的Host
        Host host = (Host) getContainer().findChild(hostName);
        
        if (host == null) {
            host = (Host) getContainer().findChild(
                ((StandardEngine) getContainer()).getDefaultHost());
        }
        
        if (host == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // 传递给Host处理
        host.getPipeline().getFirst().invoke(request, response);
    }
}
```

## 4. Host设计

### 4.1 Host概述

Host代表一个虚拟主机，可以配置多个域名。一个Engine可以有多个Host。

**主要职责**：
- 管理多个Context
- 支持虚拟主机
- 配置域名和别名
- 自动部署Web应用

**配置示例**：

```xml
<Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="true">
    <Alias>www.localhost.com</Alias>
    <Context path="" docBase="ROOT"/>
    <Context path="/examples" docBase="examples"/>
</Host>

<Host name="www.example.com" appBase="webapps-example">
    <Alias>example.com</Alias>
    <Context path="" docBase="example-app"/>
</Host>
```

### 4.2 Host实现

```java
public class StandardHost extends ContainerBase implements Host {
    private String appBase = "webapps";
    private boolean autoDeploy = true;
    private boolean deployOnStartup = true;
    private boolean unpackWARs = true;
    private String[] aliases;
    
    public StandardHost() {
        pipeline.setBasic(new StandardHostValve());
    }
    
    @Override
    public void addChild(Container child) {
        if (!(child instanceof Context)) {
            throw new IllegalArgumentException(
                "Child container of Host must be Context");
        }
        super.addChild(child);
    }
    
    @Override
    public String getAppBase() {
        return appBase;
    }
    
    @Override
    public void setAppBase(String appBase) {
        this.appBase = appBase;
    }
    
    @Override
    public boolean getAutoDeploy() {
        return autoDeploy;
    }
    
    @Override
    public void setAutoDeploy(boolean autoDeploy) {
        this.autoDeploy = autoDeploy;
    }
    
    @Override
    public boolean getDeployOnStartup() {
        return deployOnStartup;
    }
    
    @Override
    public void setDeployOnStartup(boolean deployOnStartup) {
        this.deployOnStartup = deployOnStartup;
    }
    
    @Override
    public boolean getUnpackWARs() {
        return unpackWARs;
    }
    
    @Override
    public void setUnpackWARs(boolean unpackWARs) {
        this.unpackWARs = unpackWARs;
    }
    
    @Override
    public String[] getAliases() {
        return aliases;
    }
    
    @Override
    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }
    
    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        
        // 初始化所有Context
        for (Container child : findChildren()) {
            if (child instanceof Lifecycle) {
                ((Lifecycle) child).init();
            }
        }
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        super.startInternal();
        
        // 启动所有Context
        for (Container child : findChildren()) {
            if (child instanceof Lifecycle) {
                ((Lifecycle) child).start();
            }
        }
        
        // 自动部署Web应用
        if (autoDeploy) {
            deployApps();
        }
    }
    
    private void deployApps() {
        // 扫描appBase目录
        File appBase = new File(getAppBase());
        if (!appBase.exists() || !appBase.isDirectory()) {
            return;
        }
        
        // 部署WAR文件
        File[] warFiles = appBase.listFiles((dir, name) -> name.endsWith(".war"));
        for (File warFile : warFiles) {
            deployWAR(warFile);
        }
        
        // 部署目录
        File[] dirs = appBase.listFiles(File::isDirectory);
        for (File dir : dirs) {
            deployDirectory(dir);
        }
    }
    
    private void deployWAR(File warFile) {
        String contextName = warFile.getName().replace(".war", "");
        
        // 创建Context
        StandardContext context = new StandardContext();
        context.setName(contextName);
        context.setPath("/" + contextName);
        context.setDocBase(warFile.getAbsolutePath());
        
        // 添加到Host
        addChild(context);
    }
    
    private void deployDirectory(File dir) {
        String contextName = dir.getName();
        
        // 创建Context
        StandardContext context = new StandardContext();
        context.setName(contextName);
        context.setPath("/" + contextName);
        context.setDocBase(dir.getAbsolutePath());
        
        // 添加到Host
        addChild(context);
    }
}
```

### 4.3 Host Valve

Host Valve负责处理Host级别的请求。

```java
public class StandardHostValve extends ValveBase {
    
    @Override
    public void invoke(Request request, Response response) 
            throws IOException, ServletException {
        
        // 获取请求的URI
        String uri = request.getRequestURI();
        
        // 选择对应的Context
        Context context = findContext(uri);
        
        if (context == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // 传递给Context处理
        context.getPipeline().getFirst().invoke(request, response);
    }
    
    private Context findContext(String uri) {
        Container[] children = getContainer().findChildren();
        
        for (Container child : children) {
            Context context = (Context) child;
            String contextPath = context.getPath();
            
            if (uri.startsWith(contextPath)) {
                return context;
            }
        }
        
        return null;
    }
}
```

## 5. Context设计

### 5.1 Context概述

Context代表一个Web应用，对应一个Web应用目录。一个Host可以有多个Context。

**主要职责**：
- 管理多个Wrapper
- 加载Web应用
- 提供Servlet上下文环境
- 管理Web应用资源

**配置示例**：

```xml
<Context path="/myapp" docBase="myapp" reloadable="true">
    <Resource name="jdbc/MyDB" auth="Container"
              type="javax.sql.DataSource"
              maxTotal="100" maxIdle="30" maxWaitMillis="10000"
              username="dbuser" password="dbpass"
              driverClassName="com.mysql.jdbc.Driver"
              url="jdbc:mysql://localhost:3306/mydb"/>
    
    <Environment name="maxExemptions" type="java.lang.Integer" value="15"/>
    
    <Parameter name="contextConfigLocation" value="/WEB-INF/applicationContext.xml"/>
</Context>
```

### 5.2 Context实现

```java
public class StandardContext extends ContainerBase implements Context {
    private String path;
    private String docBase;
    private ClassLoader loader;
    private ServletContext servletContext;
    private Map<String, Wrapper> servletMappings = new HashMap<>();
    private Map<String, Filter> filters = new HashMap<>();
    private Map<String, String> filterMappings = new HashMap<>();
    
    public StandardContext() {
        pipeline.setBasic(new StandardContextValve());
    }
    
    @Override
    public void addChild(Container child) {
        if (!(child instanceof Wrapper)) {
            throw new IllegalArgumentException(
                "Child container of Context must be Wrapper");
        }
        super.addChild(child);
    }
    
    @Override
    public String getPath() {
        return path;
    }
    
    @Override
    public void setPath(String path) {
        this.path = path;
    }
    
    @Override
    public String getDocBase() {
        return docBase;
    }
    
    @Override
    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }
    
    @Override
    public ClassLoader getLoader() {
        return loader;
    }
    
    @Override
    public void setLoader(ClassLoader loader) {
        this.loader = loader;
    }
    
    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }
    
    public void addServletMapping(String pattern, String servletName) {
        Wrapper wrapper = (Wrapper) findChild(servletName);
        if (wrapper != null) {
            servletMappings.put(pattern, wrapper);
        }
    }
    
    public void addFilter(String filterName, Filter filter) {
        filters.put(filterName, filter);
    }
    
    public void addFilterMapping(String filterName, String urlPattern) {
        filterMappings.put(urlPattern, filterName);
    }
    
    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        
        // 创建ServletContext
        servletContext = new ApplicationContext(this);
        
        // 初始化所有Wrapper
        for (Container child : findChildren()) {
            if (child instanceof Lifecycle) {
                ((Lifecycle) child).init();
            }
        }
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        super.startInternal();
        
        // 启动所有Wrapper
        for (Container child : findChildren()) {
            if (child instanceof Lifecycle) {
                ((Lifecycle) child).start();
            }
        }
        
        // 初始化所有Filter
        for (Filter filter : filters.values()) {
            try {
                filter.init(new FilterConfigImpl(servletContext));
            } catch (ServletException e) {
                throw new LifecycleException("Failed to initialize filter", e);
            }
        }
    }
}
```

### 5.3 Context Valve

Context Valve负责处理Context级别的请求。

```java
public class StandardContextValve extends ValveBase {
    
    @Override
    public void invoke(Request request, Response response) 
            throws IOException, ServletException {
        
        // 获取请求的URI
        String uri = request.getRequestURI();
        String contextPath = getPath();
        String servletPath = uri.substring(contextPath.length());
        
        // 选择对应的Wrapper
        Wrapper wrapper = findWrapper(servletPath);
        
        if (wrapper == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // 处理Filter链
        ApplicationFilterChain filterChain = createFilterChain(request, wrapper);
        
        // 传递给Filter链处理
        filterChain.doFilter(request.getRequest(), response.getResponse());
    }
    
    private Wrapper findWrapper(String servletPath) {
        // 精确匹配
        Wrapper wrapper = (Wrapper) servletMappings.get(servletPath);
        if (wrapper != null) {
            return wrapper;
        }
        
        // 通配符匹配
        for (Map.Entry<String, Wrapper> entry : servletMappings.entrySet()) {
            String pattern = entry.getKey();
            if (matchPattern(pattern, servletPath)) {
                return entry.getValue();
            }
        }
        
        return null;
    }
    
    private boolean matchPattern(String pattern, String path) {
        if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            return path.startsWith(prefix);
        }
        if (pattern.startsWith("*.")) {
            String extension = pattern.substring(2);
            return path.endsWith(extension);
        }
        if (pattern.equals("/")) {
            return true;
        }
        return false;
    }
    
    private ApplicationFilterChain createFilterChain(Request request, Wrapper wrapper) {
        ApplicationFilterChain filterChain = new ApplicationFilterChain();
        
        // 添加所有匹配的Filter
        for (Map.Entry<String, String> entry : filterMappings.entrySet()) {
            String urlPattern = entry.getKey();
            String filterName = entry.getValue();
            
            if (matchPattern(urlPattern, request.getRequestURI())) {
                Filter filter = filters.get(filterName);
                if (filter != null) {
                    filterChain.addFilter(filter);
                }
            }
        }
        
        // 添加Wrapper
        filterChain.setWrapper(wrapper);
        
        return filterChain;
    }
}
```

## 6. Wrapper设计

### 6.1 Wrapper概述

Wrapper代表一个Servlet包装器，封装了Servlet实例。一个Context可以有多个Wrapper。

**主要职责**：
- 管理Servlet生命周期
- 加载Servlet类
- 调用Servlet方法
- 管理Servlet配置

**配置示例**：

```xml
<servlet>
    <servlet-name>MyServlet</servlet-name>
    <servlet-class>com.example.MyServlet</servlet-class>
    <init-param>
        <param-name>configFile</param-name>
        <param-value>/WEB-INF/config.properties</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
    <servlet-name>MyServlet</servlet-name>
    <url-pattern>/myservlet</url-pattern>
</servlet-mapping>
```

### 6.2 Wrapper实现

```java
public class StandardWrapper extends ContainerBase implements Wrapper {
    private String servletClass;
    private Servlet instance;
    private Map<String, String> initParameters = new HashMap<>();
    private int loadOnStartup = -1;
    private boolean singleThreadModel = false;
    
    public StandardWrapper() {
        pipeline.setBasic(new StandardWrapperValve());
    }
    
    @Override
    public String getServletClass() {
        return servletClass;
    }
    
    @Override
    public void setServletClass(String servletClass) {
        this.servletClass = servletClass;
    }
    
    @Override
    public Servlet getServlet() {
        return instance;
    }
    
    @Override
    public void setServlet(Servlet servlet) {
        this.instance = servlet;
    }
    
    @Override
    public String getInitParameter(String name) {
        return initParameters.get(name);
    }
    
    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParameters.keySet());
    }
    
    @Override
    public void addInitParameter(String name, String value) {
        initParameters.put(name, value);
    }
    
    @Override
    public int getLoadOnStartup() {
        return loadOnStartup;
    }
    
    @Override
    public void setLoadOnStartup(int loadOnStartup) {
        this.loadOnStartup = loadOnStartup;
    }
    
    @Override
    public boolean isSingleThreadModel() {
        return singleThreadModel;
    }
    
    @Override
    public void setSingleThreadModel(boolean singleThreadModel) {
        this.singleThreadModel = singleThreadModel;
    }
    
    @Override
    public void load() throws ServletException {
        if (instance != null) {
            return;
        }
        
        try {
            Class<?> clazz = Thread.currentThread()
                .getContextClassLoader()
                .loadClass(servletClass);
            
            instance = (Servlet) clazz.newInstance();
            
            // 创建ServletConfig
            ServletConfig config = new StandardWrapperFacade(this);
            
            // 初始化Servlet
            instance.init(config);
        } catch (ClassNotFoundException e) {
            throw new ServletException("Servlet class not found: " + servletClass, e);
        } catch (InstantiationException e) {
            throw new ServletException("Failed to instantiate servlet: " + servletClass, e);
        } catch (IllegalAccessException e) {
            throw new ServletException("Failed to access servlet: " + servletClass, e);
        }
    }
    
    @Override
    public void unload() throws ServletException {
        if (instance == null) {
            return;
        }
        
        try {
            instance.destroy();
        } finally {
            instance = null;
        }
    }
}
```

### 6.3 Wrapper Valve

Wrapper Valve负责调用Servlet处理请求。

```java
public class StandardWrapperValve extends ValveBase {
    
    @Override
    public void invoke(Request request, Response response) 
            throws IOException, ServletException {
        
        StandardWrapper wrapper = (StandardWrapper) getContainer();
        
        // 加载Servlet
        Servlet servlet = wrapper.getServlet();
        if (servlet == null) {
            wrapper.load();
            servlet = wrapper.getServlet();
        }
        
        if (servlet == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        
        // 调用Servlet的service方法
        try {
            servlet.service(request.getRequest(), response.getResponse());
        } catch (ServletException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException("Servlet execution failed", e);
        }
    }
}
```

## 7. Pipeline和Valve

### 7.1 Pipeline概述

Pipeline是Tomcat中用于处理请求的管道，由多个Valve组成。每个Container都有一个Pipeline。

**Pipeline结构**：

```
┌─────────────────────────────────────────┐
│           Pipeline                      │
│  ┌─────────────────────────────────┐   │
│  │         Valve 1                 │   │
│  └──────────────┬──────────────────┘   │
│                 │                       │
│  ┌──────────────▼──────────────────┐   │
│  │         Valve 2                 │   │
│  └──────────────┬──────────────────┘   │
│                 │                       │
│  ┌──────────────▼──────────────────┐   │
│  │         Valve N                 │   │
│  └──────────────┬──────────────────┘   │
│                 │                       │
│  ┌──────────────▼──────────────────┐   │
│  │       Basic Valve              │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

### 7.2 Pipeline实现

```java
public class StandardPipeline implements Pipeline {
    protected Container container;
    protected Valve first;
    protected Valve basic;
    
    public StandardPipeline(Container container) {
        this.container = container;
    }
    
    @Override
    public Container getContainer() {
        return container;
    }
    
    @Override
    public void setContainer(Container container) {
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
    
    @Override
    public void invoke(Request request, Response response) 
            throws IOException, ServletException {
        if (first != null) {
            first.invoke(request, response);
        }
    }
}
```

### 7.3 Valve接口

```java
public interface Valve {
    Container getContainer();
    void setContainer(Container container);
    Valve getNext();
    void setNext(Valve valve);
    void invoke(Request request, Response response) 
        throws IOException, ServletException;
}

public abstract class ValveBase implements Valve, Contained {
    protected Container container;
    protected Valve next;
    protected boolean asyncSupported = false;
    
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
    
    public boolean isAsyncSupported() {
        return asyncSupported;
    }
    
    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }
}
```

## 8. ContainerBase

### 8.1 ContainerBase概述

ContainerBase是所有Container实现的基类，提供了通用的容器功能。

**主要功能**：
- 子容器管理
- Pipeline管理
- 生命周期管理
- 日志记录

### 8.2 ContainerBase实现

```java
public abstract class ContainerBase extends LifecycleMBeanBase 
        implements Container {
    
    protected String name;
    protected Container parent;
    protected HashMap<String, Container> children = new HashMap<>();
    protected Pipeline pipeline = new StandardPipeline(this);
    protected Logger logger = null;
    
    public ContainerBase() {
        pipeline.setBasic(createBasicValve());
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public Container getParent() {
        return parent;
    }
    
    @Override
    public void setParent(Container container) {
        this.parent = container;
    }
    
    @Override
    public void addChild(Container child) {
        child.setParent(this);
        children.put(child.getName(), child);
    }
    
    @Override
    public Container findChild(String name) {
        return children.get(name);
    }
    
    @Override
    public Container[] findChildren() {
        return children.values().toArray(new Container[0]);
    }
    
    @Override
    public void removeChild(Container child) {
        children.remove(child.getName());
    }
    
    @Override
    public void addValve(Valve valve) {
        pipeline.addValve(valve);
    }
    
    @Override
    public Valve[] getValves() {
        return pipeline.getValves();
    }
    
    @Override
    public void removeValve(Valve valve) {
        pipeline.removeValve(valve);
    }
    
    @Override
    public Pipeline getPipeline() {
        return pipeline;
    }
    
    protected abstract Valve createBasicValve();
    
    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        
        // 初始化Pipeline
        if (pipeline instanceof Lifecycle) {
            ((Lifecycle) pipeline).init();
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
}
```

## 9. 总结

Tomcat的Container设计体现了以下核心思想：

1. **分层设计**：Engine、Host、Context、Wrapper分层清晰，职责明确
2. **组合模式**：Container采用组合模式，可以灵活地组合子容器
3. **责任链模式**：Pipeline和Valve采用责任链模式，灵活处理请求
4. **生命周期管理**：统一的Lifecycle接口管理容器生命周期
5. **可扩展性**：通过Valve机制可以灵活扩展功能

Container设计是Tomcat架构的核心，为Servlet容器提供了强大而灵活的管理能力。深入理解Container设计，对于：
- 开发Tomcat扩展
- 优化Web应用性能
- 排查Tomcat问题
- 深入理解Servlet规范

都具有重要意义。Tomcat的Container设计是其优秀架构设计的重要体现，为Java Web应用提供了强大的支持。