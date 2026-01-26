# ZuulFilter常用有那些方法？

## 一、ZuulFilter概述

### 1.1 什么是ZuulFilter

**定义**
ZuulFilter是Zuul的核心组件，用于在请求的不同阶段对请求进行处理。

**作用**
- 请求预处理：在请求路由到后端服务之前对请求进行处理
- 请求后处理：在请求路由到后端服务之后对响应进行处理
- 错误处理：在处理请求时发生错误时进行处理

### 1.2 ZuulFilter的类型

**ZuulFilter的类型**
- Pre过滤器：在请求路由到后端服务之前执行
- Route过滤器：在请求路由到后端服务时执行
- Post过滤器：在请求路由到后端服务之后执行
- Error过滤器：在处理请求时发生错误时执行

## 二、ZuulFilter的核心方法

### 2.1 filterType()

**方法定义**
```java
String filterType();
```

**方法作用**
返回过滤器的类型，可以是"pre"、"route"、"post"、"error"。

**返回值**
- "pre"：Pre过滤器
- "route"：Route过滤器
- "post"：Post过滤器
- "error"：Error过滤器

**示例**
```java
@Override
public String filterType() {
    return "pre";
}
```

### 2.2 filterOrder()

**方法定义**
```java
int filterOrder();
```

**方法作用**
返回过滤器的执行顺序，数值越小，执行顺序越靠前。

**返回值**
- 整数，表示过滤器的执行顺序

**示例**
```java
@Override
public int filterOrder() {
    return 0;
}
```

### 2.3 shouldFilter()

**方法定义**
```java
boolean shouldFilter();
```

**方法作用**
判断过滤器是否需要执行，返回true表示需要执行，返回false表示不需要执行。

**返回值**
- true：需要执行
- false：不需要执行

**示例**
```java
@Override
public boolean shouldFilter() {
    return true;
}
```

### 2.4 run()

**方法定义**
```java
Object run() throws ZuulException;
```

**方法作用**
过滤器的具体执行逻辑。

**返回值**
- Object：可以返回任意对象，通常返回null

**示例**
```java
@Override
public Object run() throws ZuulException {
    RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletRequest request = ctx.getRequest();
    
    System.out.println("Filter: " + request.getRequestURI());
    
    return null;
}
```

## 三、ZuulFilter的常用方法

### 3.1 RequestContext

**获取RequestContext**
```java
RequestContext ctx = RequestContext.getCurrentContext();
```

**RequestContext的常用方法**
```java
// 获取请求
HttpServletRequest request = ctx.getRequest();

// 获取响应
HttpServletResponse response = ctx.getResponse();

// 设置是否发送响应
ctx.setSendZuulResponse(true);

// 获取是否发送响应
boolean sendZuulResponse = ctx.sendZuulResponse();

// 设置响应状态码
ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());

// 获取响应状态码
int responseStatusCode = ctx.getResponseStatusCode();

// 设置响应体
ctx.setResponseBody("Unauthorized");

// 获取响应体
String responseBody = ctx.getResponseBody();

// 设置响应头
ctx.addZuulResponseHeader("Content-Type", "application/json");

// 获取响应头
String contentType = ctx.getZuulResponseHeaders("Content-Type");

// 设置请求属性
ctx.set("key", "value");

// 获取请求属性
Object value = ctx.get("key");

// 设置异常
ctx.setThrowable(new RuntimeException("Error"));

// 获取异常
Throwable throwable = ctx.getThrowable();
```

### 3.2 HttpServletRequest

**获取HttpServletRequest**
```java
HttpServletRequest request = ctx.getRequest();
```

**HttpServletRequest的常用方法**
```java
// 获取请求URI
String requestURI = request.getRequestURI();

// 获取请求URL
StringBuffer requestURL = request.getRequestURL();

// 获取请求方法
String method = request.getMethod();

// 获取请求参数
String parameter = request.getParameter("key");

// 获取请求头
String header = request.getHeader("Authorization");

// 获取请求体
BufferedReader reader = request.getReader();
StringBuilder sb = new StringBuilder();
String line;
while ((line = reader.readLine()) != null) {
    sb.append(line);
}
String body = sb.toString();

// 获取请求属性
Object attribute = request.getAttribute("key");

// 设置请求属性
request.setAttribute("key", "value");
```

### 3.3 HttpServletResponse

**获取HttpServletResponse**
```java
HttpServletResponse response = ctx.getResponse();
```

**HttpServletResponse的常用方法**
```java
// 设置响应状态码
response.setStatus(HttpStatus.OK.value());

// 设置响应头
response.setHeader("Content-Type", "application/json");

// 添加响应头
response.addHeader("Content-Type", "application/json");

// 设置响应体
response.getWriter().write("Hello World");

// 获取输出流
ServletOutputStream outputStream = response.getOutputStream();
outputStream.write("Hello World".getBytes());

// 获取写入器
PrintWriter writer = response.getWriter();
writer.write("Hello World");
```

## 四、ZuulFilter的示例

### 4.1 Pre过滤器示例

**Pre过滤器**
```java
@Component
public class AuthFilter extends ZuulFilter {
    
    @Override
    public String filterType() {
        return "pre";
    }
    
    @Override
    public int filterOrder() {
        return 0;
    }
    
    @Override
    public boolean shouldFilter() {
        return true;
    }
    
    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        
        String token = request.getHeader("Authorization");
        if (token == null || !validateToken(token)) {
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            ctx.setResponseBody("Unauthorized");
            return null;
        }
        
        return null;
    }
    
    private boolean validateToken(String token) {
        return true;
    }
}
```

### 4.2 Route过滤器示例

**Route过滤器**
```java
@Component
public class RouteFilter extends ZuulFilter {
    
    @Override
    public String filterType() {
        return "route";
    }
    
    @Override
    public int filterOrder() {
        return 0;
    }
    
    @Override
    public boolean shouldFilter() {
        return true;
    }
    
    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        
        System.out.println("Route Filter: " + request.getRequestURI());
        
        return null;
    }
}
```

### 4.3 Post过滤器示例

**Post过滤器**
```java
@Component
public class PostFilter extends ZuulFilter {
    
    @Override
    public String filterType() {
        return "post";
    }
    
    @Override
    public int filterOrder() {
        return 0;
    }
    
    @Override
    public boolean shouldFilter() {
        return true;
    }
    
    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        
        System.out.println("Post Filter: " + response.getStatus());
        
        return null;
    }
}
```

### 4.4 Error过滤器示例

**Error过滤器**
```java
@Component
public class ErrorFilter extends ZuulFilter {
    
    @Override
    public String filterType() {
        return "error";
    }
    
    @Override
    public int filterOrder() {
        return 0;
    }
    
    @Override
    public boolean shouldFilter() {
        return true;
    }
    
    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        Throwable throwable = ctx.getThrowable();
        
        System.out.println("Error Filter: " + throwable.getMessage());
        
        return null;
    }
}
```

## 五、ZuulFilter的最佳实践

### 5.1 过滤器执行顺序

**过滤器执行顺序**
- Pre过滤器：按照filterOrder()的值从小到大执行
- Route过滤器：按照filterOrder()的值从小到大执行
- Post过滤器：按照filterOrder()的值从大到小执行
- Error过滤器：按照filterOrder()的值从小到大执行

**示例**
```java
@Component
public class FirstPreFilter extends ZuulFilter {
    
    @Override
    public String filterType() {
        return "pre";
    }
    
    @Override
    public int filterOrder() {
        return 0;
    }
    
    @Override
    public boolean shouldFilter() {
        return true;
    }
    
    @Override
    public Object run() throws ZuulException {
        System.out.println("First Pre Filter");
        return null;
    }
}

@Component
public class SecondPreFilter extends ZuulFilter {
    
    @Override
    public String filterType() {
        return "pre";
    }
    
    @Override
    public int filterOrder() {
        return 1;
    }
    
    @Override
    public boolean shouldFilter() {
        return true;
    }
    
    @Override
    public Object run() throws ZuulException {
        System.out.println("Second Pre Filter");
        return null;
    }
}
```

### 5.2 过滤器条件判断

**过滤器条件判断**
```java
@Override
public boolean shouldFilter() {
    RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletRequest request = ctx.getRequest();
    
    String uri = request.getRequestURI();
    if (uri.startsWith("/user")) {
        return true;
    }
    
    return false;
}
```

### 5.3 过滤器异常处理

**过滤器异常处理**
```java
@Override
public Object run() throws ZuulException {
    try {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        
        System.out.println("Filter: " + request.getRequestURI());
        
        return null;
    } catch (Exception e) {
        throw new ZuulException(e, 500, "Internal Server Error");
    }
}
```

## 六、总结

ZuulFilter是Zuul的核心组件，用于在请求的不同阶段对请求进行处理。ZuulFilter的核心方法包括filterType()、filterOrder()、shouldFilter()、run()。

### 核心要点

1. **ZuulFilter定义**：Zuul的核心组件，用于在请求的不同阶段对请求进行处理
2. **ZuulFilter类型**：Pre过滤器、Route过滤器、Post过滤器、Error过滤器
3. **ZuulFilter核心方法**：filterType()、filterOrder()、shouldFilter()、run()
4. **ZuulFilter常用方法**：RequestContext、HttpServletRequest、HttpServletResponse
5. **ZuulFilter示例**：Pre过滤器、Route过滤器、Post过滤器、Error过滤器
6. **ZuulFilter最佳实践**：过滤器执行顺序、过滤器条件判断、过滤器异常处理

### 使用建议

1. **Pre过滤器**：用于请求预处理，如鉴权、限流、日志
2. **Route过滤器**：用于请求路由，如动态路由、路由转发
3. **Post过滤器**：用于请求后处理，如响应修改、日志记录
4. **Error过滤器**：用于错误处理，如异常捕获、错误响应

ZuulFilter是Zuul的核心组件，需要根据业务需求选择合适的过滤器类型和实现方式。
