# Tomcat一个请求的处理流程

## 1. 概述

Tomcat作为Servlet容器，其核心功能就是接收和处理HTTP请求。理解Tomcat的请求处理流程对于深入理解Servlet规范、优化应用性能和排查问题都具有重要意义。

## 2. 请求处理流程概览

### 2.1 整体流程图

```
客户端请求
    │
    ▼
Connector (连接器)
    │
    ├─ 1. 接收连接
    ├─ 2. 解析请求
    ├─ 3. 创建Request/Response对象
    └─ 4. 传递给Container
           │
           ▼
Container (容器)
    │
    ├─ 5. Engine处理
    ├─ 6. Host处理
    ├─ 7. Context处理
    ├─ 8. Wrapper处理
    └─ 9. 调用Servlet
           │
           ▼
Servlet处理
    │
    ├─ 10. 执行业务逻辑
    └─ 11. 生成响应
           │
           ▼
返回响应
    │
    ▼
客户端接收响应
```

### 2.2 时序图

```
客户端    Connector    Engine    Host    Context    Wrapper    Servlet
 │          │            │         │         │           │          │
 ├─请求────▶│            │         │         │           │          │
 │          ├─解析───────▶│         │         │           │          │
 │          │            ├─选择───▶│         │           │          │
 │          │            │         ├─选择───▶│           │          │
 │          │            │         │         ├─选择─────▶│          │
 │          │            │         │         │           ├─调用────▶│
 │          │            │         │         │           │          ├─处理─┐
 │          │            │         │         │           │          │      │
 │          │            │         │         │           │          ◀─────┘
 │          │            │         │         │           ◀─响应────┤
 │          │            │         │         ◀─响应────┤          │
 │          │            │         ◀─响应────┤          │          │
 │          │            ◀─响应────┤         │          │          │
 │          ◀─响应──────┤          │         │          │          │
 ◀─响应────┤            │          │         │          │          │
 │          │            │         │         │           │          │
```

## 3. Connector层处理

### 3.1 接收连接

当客户端发起HTTP请求时，Connector首先需要接收这个连接。

**步骤**：

1. **监听端口**：Endpoint监听配置的端口（如8080）
2. **接受连接**：使用ServerSocket.accept()接受客户端连接
3. **创建Socket**：为每个连接创建Socket对象
4. **分配线程**：从线程池分配一个线程处理该连接

**代码示例**：

```java
public class NioEndpoint extends AbstractEndpoint {
    private Acceptor acceptor;
    
    @Override
    public void bind() throws Exception {
        serverSock = ServerSocketChannel.open();
        serverSock.socket().bind(getAddress());
        serverSock.configureBlocking(true);
    }
    
    @Override
    public void startInternal() throws Exception {
        acceptor = new Acceptor();
        acceptor.start();
    }
    
    protected class Acceptor extends AbstractEndpoint.Acceptor {
        @Override
        public void run() {
            while (running) {
                try {
                    SocketChannel socket = serverSock.accept();
                    if (running) {
                        setSocketOptions(socket);
                    }
                } catch (IOException e) {
                    // 处理异常
                }
            }
        }
    }
}
```

### 3.2 解析请求

连接建立后，Connector需要解析HTTP请求。

**解析内容**：

1. **请求行**：方法、URI、HTTP版本
2. **请求头**：各种HTTP头部信息
3. **请求体**：POST请求的数据

**代码示例**：

```java
public class Http11Processor extends AbstractProcessor {
    private InputBuffer inputBuffer;
    
    @Override
    protected void prepareRequest() {
        inputBuffer = new InputBuffer(request);
        
        // 解析请求行
        parseRequestLine();
        
        // 解析请求头
        parseHeaders();
        
        // 解析请求体
        if (request.getContentLength() > 0) {
            parseBody();
        }
    }
    
    private void parseRequestLine() throws IOException {
        byte chr = 0;
        while ((chr = readByte()) != Constants.CR) {
            request.method().append((char) chr);
        }
        
        readByte(); // LF
        
        while ((chr = readByte()) != Constants.SP) {
            request.requestURI().append((char) chr);
        }
        
        while ((chr = readByte()) != Constants.CR) {
            request.protocol().append((char) chr);
        }
    }
}
```

### 3.3 创建Request/Response对象

解析完成后，Connector创建Request和Response对象。

**Request对象**：

```java
public class Request implements HttpServletRequest {
    private String method;
    private String requestURI;
    private String protocol;
    private Map<String, String> headers;
    private InputStream inputStream;
    
    // 实现HttpServletRequest接口的方法
    @Override
    public String getMethod() {
        return method;
    }
    
    @Override
    public String getRequestURI() {
        return requestURI;
    }
    
    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }
}
```

**Response对象**：

```java
public class Response implements HttpServletResponse {
    private OutputStream outputStream;
    private int status = SC_OK;
    private Map<String, String> headers;
    private boolean committed = false;
    
    // 实现HttpServletResponse接口的方法
    @Override
    public void setStatus(int sc) {
        if (isCommitted()) {
            return;
        }
        this.status = sc;
    }
    
    @Override
    public void setHeader(String name, String value) {
        if (isCommitted()) {
            return;
        }
        headers.put(name, value);
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        return outputStream;
    }
    
    @Override
    public void sendError(int sc) throws IOException {
        setStatus(sc);
        commit();
    }
}
```

### 3.4 传递给Container

Connector将Request和Response对象传递给Container处理。

**代码示例**：

```java
public class CoyoteAdapter implements Adapter {
    private Engine engine;
    
    @Override
    public void service(Request req, Response res) throws Exception {
        // 创建Tomcat内部的Request和Response对象
        org.apache.catalina.connector.Request request = 
            (org.apache.catalina.connector.Request) req.getNote(ADAPTER_NOTES);
        org.apache.catalina.connector.Response response = 
            (org.apache.catalina.connector.Response) res.getNote(ADAPTER_NOTES);
        
        // 设置Request和Response
        request.setConnector(connector);
        response.setConnector(connector);
        
        // 传递给Engine处理
        engine.service(request, response);
    }
}
```

## 4. Container层处理

### 4.1 Engine处理

Engine作为Container的最顶层，负责将请求分发给对应的Host。

**处理流程**：

1. **选择Host**：根据请求的Host头选择对应的Host
2. **传递请求**：将请求传递给选中的Host

**代码示例**：

```java
public class StandardEngine extends ContainerBase implements Engine {
    private Host[] hosts;
    
    @Override
    public void service(Request request, Response response) {
        // 获取请求的Host头
        String hostName = request.getServerName();
        
        // 选择对应的Host
        Host host = findHost(hostName);
        
        if (host == null) {
            host = findHost(defaultHost);
        }
        
        // 传递给Host处理
        host.service(request, response);
    }
    
    private Host findHost(String name) {
        for (Host host : hosts) {
            if (host.getName().equalsIgnoreCase(name)) {
                return host;
            }
        }
        return null;
    }
}
```

### 4.2 Host处理

Host负责将请求分发给对应的Context。

**处理流程**：

1. **选择Context**：根据请求的URI选择对应的Context
2. **传递请求**：将请求传递给选中的Context

**代码示例**：

```java
public class StandardHost extends ContainerBase implements Host {
    private Context[] contexts;
    
    @Override
    public void service(Request request, Response response) {
        // 获取请求的URI
        String uri = request.getRequestURI();
        
        // 选择对应的Context
        Context context = findContext(uri);
        
        if (context == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // 传递给Context处理
        context.service(request, response);
    }
    
    private Context findContext(String uri) {
        for (Context context : contexts) {
            String contextPath = context.getPath();
            if (uri.startsWith(contextPath)) {
                return context;
            }
        }
        return null;
    }
}
```

### 4.3 Context处理

Context负责将请求分发给对应的Wrapper。

**处理流程**：

1. **选择Wrapper**：根据请求的URI和Servlet映射选择对应的Wrapper
2. **传递请求**：将请求传递给选中的Wrapper

**代码示例**：

```java
public class StandardContext extends ContainerBase implements Context {
    private Map<String, Wrapper> servletMappings;
    
    @Override
    public void service(Request request, Response response) {
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
        
        // 传递给Wrapper处理
        wrapper.service(request, response);
    }
    
    private Wrapper findWrapper(String servletPath) {
        // 精确匹配
        Wrapper wrapper = servletMappings.get(servletPath);
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
}
```

### 4.4 Wrapper处理

Wrapper负责调用Servlet处理请求。

**处理流程**：

1. **获取Servlet实例**：从Wrapper中获取Servlet实例
2. **调用Servlet**：调用Servlet的service方法
3. **返回响应**：将Servlet的响应返回给客户端

**代码示例**：

```java
public class StandardWrapper extends ContainerBase implements Wrapper {
    private Servlet instance;
    private String servletClass;
    
    @Override
    public void service(Request request, Response response) {
        // 获取Servlet实例
        Servlet servlet = getServlet();
        
        if (servlet == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        
        // 调用Servlet的service方法
        try {
            servlet.service(request, response);
        } catch (ServletException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private Servlet getServlet() {
        if (instance == null) {
            try {
                Class<?> clazz = Class.forName(servletClass);
                instance = (Servlet) clazz.newInstance();
                instance.init(new StandardWrapperFacade(this));
            } catch (Exception e) {
                throw new RuntimeException("Failed to load servlet", e);
            }
        }
        return instance;
    }
}
```

## 5. Servlet处理

### 5.1 Servlet生命周期

Servlet的生命周期由Container管理，包括初始化、服务和销毁三个阶段。

**生命周期图**：

```
加载Servlet类
    │
    ▼
实例化Servlet
    │
    ▼
调用init()方法
    │
    ▼
┌─────────────────┐
│  service()方法   │ ◀──────┐
│  (多次调用)      │        │
└────────┬────────┘        │
         │                │
         │                │
         ▼                │
    调用destroy()方法      │
         │                │
         │                │
         ▼                │
    Servlet被垃圾回收      │
                          │
                          └── 新请求
```

**代码示例**：

```java
public class MyServlet extends HttpServlet {
    
    @Override
    public void init() throws ServletException {
        System.out.println("Servlet初始化");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Hello, World!</h1>");
        out.println("</body></html>");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    @Override
    public void destroy() {
        System.out.println("Servlet销毁");
    }
}
```

### 5.2 Filter链处理

在调用Servlet之前，请求会先经过Filter链。

**Filter链处理流程**：

```
请求
    │
    ▼
Filter1.doFilter()
    │
    ├─ 前置处理
    ├─ chain.doFilter()
    │       │
    │       ▼
    │   Filter2.doFilter()
    │       │
    │       ├─ 前置处理
    │       ├─ chain.doFilter()
    │       │       │
    │       │       ▼
    │       │   Servlet.service()
    │       │       │
    │       │       ├─ 业务逻辑
    │       │       └─ 返回响应
    │       │
    │       └─ 后置处理
    │
    └─ 后置处理
        │
        ▼
    响应
```

**代码示例**：

```java
public class LoggingFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Filter初始化");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        System.out.println("请求到达Filter");
        
        // 前置处理
        long startTime = System.currentTimeMillis();
        
        // 继续执行Filter链
        chain.doFilter(request, response);
        
        // 后置处理
        long endTime = System.currentTimeMillis();
        System.out.println("请求处理耗时: " + (endTime - startTime) + "ms");
    }
    
    @Override
    public void destroy() {
        System.out.println("Filter销毁");
    }
}
```

## 6. 响应处理

### 6.1 生成响应

Servlet处理完请求后，生成响应数据。

**代码示例**：

```java
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    // 设置响应头
    response.setContentType("text/html;charset=UTF-8");
    response.setHeader("Cache-Control", "no-cache");
    
    // 设置响应状态码
    response.setStatus(HttpServletResponse.SC_OK);
    
    // 写入响应体
    PrintWriter out = response.getWriter();
    out.println("<html><body>");
    out.println("<h1>Hello, World!</h1>");
    out.println("</body></html>");
    
    // 刷新缓冲区
    out.flush();
}
```

### 6.2 返回响应

Container将Servlet生成的响应返回给Connector，Connector再将响应返回给客户端。

**代码示例**：

```java
public class Response implements HttpServletResponse {
    private OutputBuffer outputBuffer;
    
    @Override
    public void flushBuffer() throws IOException {
        if (!isCommitted()) {
            commit();
        }
        outputBuffer.flush();
    }
    
    protected void commit() throws IOException {
        if (isCommitted()) {
            return;
        }
        
        // 写入状态行
        outputBuffer.writeBytes("HTTP/1.1 " + status + " " + getStatusMessage(status) + "\r\n");
        
        // 写入响应头
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            outputBuffer.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
        
        // 写入空行
        outputBuffer.writeBytes("\r\n");
        
        committed = true;
    }
}
```

## 7. 异常处理

### 7.1 异常处理流程

在请求处理过程中，可能会发生各种异常。Tomcat提供了完善的异常处理机制。

**异常处理流程图**：

```
发生异常
    │
    ▼
捕获异常
    │
    ├─ ServletException
    │   ├─ 调用error page
    │   └─ 返回错误响应
    │
    ├─ IOException
    │   ├─ 检查连接是否关闭
    │   └─ 返回错误响应
    │
    └─ RuntimeException
        ├─ 记录错误日志
        ├─ 调用error page
        └─ 返回错误响应
```

**代码示例**：

```java
public class StandardWrapperValve extends ValveBase {
    
    @Override
    public void invoke(Request request, Response response) 
            throws IOException, ServletException {
        
        Servlet servlet = wrapper.allocate();
        
        try {
            servlet.service(request, response);
        } catch (ServletException e) {
            // 处理ServletException
            handleException(request, response, e);
        } catch (IOException e) {
            // 处理IOException
            handleException(request, response, e);
        } catch (RuntimeException e) {
            // 处理RuntimeException
            handleException(request, response, e);
        }
    }
    
    private void handleException(Request request, Response response, Throwable t) {
        // 记录错误日志
        log.error("Servlet执行异常", t);
        
        // 检查是否配置了error page
        String errorPage = findErrorPage(t);
        if (errorPage != null) {
            // 转发到error page
            request.getRequestDispatcher(errorPage).forward(request, response);
        } else {
            // 返回默认错误响应
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
```

### 7.2 Error Page配置

在web.xml中配置错误页面。

**配置示例**：

```xml
<web-app>
    <!-- 配置错误页面 -->
    <error-page>
        <error-code>404</error-code>
        <location>/404.html</location>
    </error-page>
    
    <error-page>
        <error-code>500</error-code>
        <location>/500.html</location>
    </error-page>
    
    <error-page>
        <exception-type>java.lang.Exception</exception-type>
        <location>/error.html</location>
    </error-page>
</web-app>
```

## 8. 性能优化

### 8.1 连接池优化

使用连接池管理数据库连接，减少连接创建和销毁的开销。

**配置示例**：

```xml
<Resource name="jdbc/MyDB" auth="Container"
          type="javax.sql.DataSource"
          maxTotal="100"
          maxIdle="30"
          maxWaitMillis="10000"
          username="dbuser"
          password="dbpass"
          driverClassName="com.mysql.jdbc.Driver"
          url="jdbc:mysql://localhost:3306/mydb"/>
```

### 8.2 线程池优化

优化Connector的线程池配置，提高并发处理能力。

**配置示例**：

```xml
<Connector port="8080" protocol="HTTP/1.1"
           maxThreads="200"
           minSpareThreads="25"
           maxSpareThreads="75"
           acceptCount="100"
           connectionTimeout="20000"/>
```

### 8.3 异步处理

使用Servlet 3.0的异步处理机制，提高并发性能。

**代码示例**：

```java
@WebServlet(urlPatterns = "/async", asyncSupported = true)
public class AsyncServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 开启异步处理
        AsyncContext asyncContext = request.startAsync();
        
        // 在新线程中处理请求
        new Thread(() -> {
            try {
                // 模拟耗时操作
                Thread.sleep(1000);
                
                // 获取响应对象
                HttpServletResponse resp = (HttpServletResponse) asyncContext.getResponse();
                
                // 写入响应
                resp.setContentType("text/html;charset=UTF-8");
                PrintWriter out = resp.getWriter();
                out.println("<html><body>");
                out.println("<h1>Async Response</h1>");
                out.println("</body></html>");
                
                // 完成异步处理
                asyncContext.complete();
            } catch (Exception e) {
                asyncContext.complete();
            }
        }).start();
    }
}
```

## 9. 总结

Tomcat的请求处理流程是一个复杂而精密的过程，涉及多个组件的协作：

1. **Connector层**：接收连接、解析请求、创建Request/Response对象
2. **Container层**：Engine、Host、Context、Wrapper逐层处理，最终调用Servlet
3. **Servlet层**：执行业务逻辑，生成响应
4. **响应返回**：将响应返回给客户端

理解Tomcat的请求处理流程，有助于：
- 优化应用性能
- 排查问题
- 深入理解Servlet规范
- 开发高性能的Web应用

Tomcat的优秀设计使得它能够高效地处理大量并发请求，成为Java Web应用的主流容器。