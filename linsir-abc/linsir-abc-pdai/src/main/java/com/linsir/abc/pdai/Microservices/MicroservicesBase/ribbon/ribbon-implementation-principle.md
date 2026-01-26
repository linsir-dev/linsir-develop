# Ribbon底层实现原理？

## 一、Ribbon底层实现原理概述

### 1.1 Ribbon的核心原理

**核心原理**
Ribbon的底层实现原理是基于客户端负载均衡，通过拦截HTTP请求，在发送请求之前选择一个服务实例，然后将请求发送到选择的服务实例。

**实现步骤**
1. 从服务注册中心获取服务列表
2. 根据负载均衡策略选择一个服务实例
3. 拦截HTTP请求
4. 将请求发送到选择的服务实例

### 1.2 Ribbon的核心组件

**Ribbon的核心组件**
- ILoadBalancer：负载均衡器接口
- IRule：负载均衡策略接口
- IPing：健康检查接口
- ServerList：服务列表接口
- ServerListUpdater：服务列表更新接口

## 二、Ribbon的核心组件详解

### 2.1 ILoadBalancer

**接口定义**
```java
public interface ILoadBalancer {
    
    void addServers(List<Server> newServers);
    
    Server chooseServer(Object key);
    
    void markServerDown(Server server);
    
    List<Server> getReachableServers();
    
    List<Server> getAllServers();
}
```

**实现类**
- BaseLoadBalancer：基础负载均衡器
- DynamicServerListLoadBalancer：动态服务列表负载均衡器
- ZoneAwareLoadBalancer：区域感知负载均衡器

**实现原理**
```java
public class BaseLoadBalancer extends AbstractLoadBalancer {
    
    private List<Server> allServerList = new ArrayList<Server>();
    private List<Server> upServerList = new ArrayList<Server>();
    private IRule rule;
    
    @Override
    public Server chooseServer(Object key) {
        if (rule == null) {
            return null;
        }
        return rule.choose(key);
    }
    
    @Override
    public void markServerDown(Server server) {
        if (server == null) {
            return;
        }
        if (!upServerList.contains(server)) {
            return;
        }
        upServerList.remove(server);
    }
    
    @Override
    public List<Server> getReachableServers() {
        return Collections.unmodifiableList(upServerList);
    }
    
    @Override
    public List<Server> getAllServers() {
        return Collections.unmodifiableList(allServerList);
    }
}
```

### 2.2 IRule

**接口定义**
```java
public interface IRule {
    
    Server choose(Object key);
    
    void setLoadBalancer(ILoadBalancer lb);
    
    ILoadBalancer getLoadBalancer();
}
```

**实现类**
- RoundRobinRule：轮询策略
- RandomRule：随机策略
- RetryRule：重试策略
- WeightedResponseTimeRule：响应时间加权策略
- BestAvailableRule：最小连接数策略
- AvailabilityFilteringRule：可用性过滤策略
- ZoneAvoidanceRule：区域感知策略

**实现原理**
```java
public class RoundRobinRule extends AbstractLoadBalancerRule {
    
    private AtomicInteger nextServerCyclicCounter;
    
    public RoundRobinRule() {
        nextServerCyclicCounter = new AtomicInteger(0);
    }
    
    @Override
    public Server choose(Object key) {
        ILoadBalancer lb = getLoadBalancer();
        if (lb == null) {
            return null;
        }
        
        List<Server> servers = lb.getReachableServers();
        if (servers == null || servers.isEmpty()) {
            return null;
        }
        
        int index = nextServerCyclicCounter.getAndIncrement();
        if (index >= Integer.MAX_VALUE) {
            nextServerCyclicCounter.set(0);
            index = nextServerCyclicCounter.getAndIncrement();
        }
        
        return servers.get(index % servers.size());
    }
}
```

### 2.3 IPing

**接口定义**
```java
public interface IPing {
    
    boolean isAlive(Server server);
}
```

**实现类**
- DummyPing：虚拟健康检查
- NIWSDiscoveryPing：服务发现健康检查
- PingUrl：URL健康检查

**实现原理**
```java
public class PingUrl implements IPing {
    
    private String pingAppendString = "";
    
    @Override
    public boolean isAlive(Server server) {
        if (server == null) {
            return false;
        }
        
        String urlStr = "";
        if (isSecure()) {
            urlStr = "https://";
        } else {
            urlStr = "http://";
        }
        
        urlStr += server.getId();
        urlStr += getPingAppendString();
        
        boolean isAlive = false;
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(urlStr);
        
        try {
            HttpResponse response = httpClient.execute(httpGet);
            isAlive = response.getStatusLine().getStatusCode() == 200;
        } catch (IOException e) {
            isAlive = false;
        } finally {
            httpGet.releaseConnection();
        }
        
        return isAlive;
    }
}
```

### 2.4 ServerList

**接口定义**
```java
public interface ServerList<T extends Server> {
    
    List<T> getInitialListOfServers();
    
    List<T> getUpdatedListOfServers();
}
```

**实现类**
- StaticServerList：静态服务列表
- DiscoveryEnabledNIWSServerList：服务发现服务列表

**实现原理**
```java
public class DiscoveryEnabledNIWSServerList extends AbstractServerList<DiscoveryEnabledServer> {
    
    private volatile List<DiscoveryEnabledServer> serverList;
    
    @Override
    public List<DiscoveryEnabledServer> getInitialListOfServers() {
        return getUpdatedListOfServers();
    }
    
    @Override
    public List<DiscoveryEnabledServer> getUpdatedListOfServers() {
        List<DiscoveryEnabledServer> servers = new ArrayList<DiscoveryEnabledServer>();
        
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        for (ServiceInstance instance : instances) {
            DiscoveryEnabledServer server = new DiscoveryEnabledServer(instance);
            servers.add(server);
        }
        
        serverList = servers;
        return servers;
    }
}
```

### 2.5 ServerListUpdater

**接口定义**
```java
public interface ServerListUpdater {
    
    void start(UpdateAction updateAction);
    
    void stop();
}
```

**实现类**
- PollingServerListUpdater：轮询服务列表更新器
- EurekaNotificationServerListUpdater：Eureka通知服务列表更新器

**实现原理**
```java
public class PollingServerListUpdater implements ServerListUpdater {
    
    private static final Logger logger = LoggerFactory.getLogger(PollingServerListUpdater.class);
    
    private static long LISTOFSERVERS_CACHE_UPDATE_DELAY = 1000L;
    private static long LISTOFSERVERS_CACHE_REPEAT_INTERVAL = 30 * 1000L;
    
    private final ScheduledExecutorService scheduler;
    private final long initialDelayMs;
    private final long refreshIntervalMs;
    
    public PollingServerListUpdater() {
        this(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "PollingServerListUpdater-" + System.currentTimeMillis());
                thread.setDaemon(true);
                return thread;
            }
        }, LISTOFSERVERS_CACHE_UPDATE_DELAY, LISTOFSERVERS_CACHE_REPEAT_INTERVAL);
    }
    
    @Override
    public void start(final UpdateAction updateAction) {
        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(
            new Runnable() {
                @Override
                public void run() {
                    try {
                        updateAction.doUpdate();
                    } catch (Exception e) {
                        logger.warn("Failed one update cycle", e);
                    }
                }
            }, initialDelayMs, refreshIntervalMs, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void stop() {
        scheduler.shutdown();
    }
}
```

## 三、Ribbon的请求拦截

### 3.1 LoadBalancerInterceptor

**拦截器定义**
```java
public class LoadBalancerInterceptor implements ClientHttpRequestInterceptor {
    
    private LoadBalancerClient loadBalancer;
    
    public LoadBalancerInterceptor(LoadBalancerClient loadBalancer) {
        this.loadBalancer = loadBalancer;
    }
    
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        URI originalUri = request.getURI();
        String serviceName = originalUri.getHost();
        
        return this.loadBalancer.execute(serviceName, request, execution);
    }
}
```

**拦截器原理**
1. 拦截HTTP请求
2. 获取服务名称
3. 调用LoadBalancerClient执行负载均衡
4. 返回响应

### 3.2 LoadBalancerClient

**接口定义**
```java
public interface LoadBalancerClient {
    
    <T> T execute(String serviceId, LoadBalancerRequest<T> request) throws IOException;
    
    <T> T execute(String serviceId, ServiceInstance serviceInstance, LoadBalancerRequest<T> request) throws IOException;
    
    URI reconstructURI(ServiceInstance instance, URI original);
}
```

**实现类**
```java
public class RibbonLoadBalancerClient implements LoadBalancerClient {
    
    private SpringClientFactory clientFactory;
    
    @Override
    public <T> T execute(String serviceId, LoadBalancerRequest<T> request) throws IOException {
        ILoadBalancer loadBalancer = getLoadBalancer(serviceId);
        Server server = loadBalancer.chooseServer(serviceId);
        
        if (server == null) {
            throw new IllegalStateException("No instances available for " + serviceId);
        }
        
        RibbonServer ribbonServer = new RibbonServer(serviceId, server, isSecure(server, serviceId), serverIntrospector(serviceId).getMetadata(server));
        
        return execute(serviceId, ribbonServer, request);
    }
    
    @Override
    public <T> T execute(String serviceId, ServiceInstance serviceInstance, LoadBalancerRequest<T> request) throws IOException {
        try {
            return request.apply(serviceInstance);
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public URI reconstructURI(ServiceInstance instance, URI original) {
        Assert.notNull(instance, "instance can not be null");
        String serviceId = instance.getServiceId();
        
        RibbonLoadBalancerContext context = this.clientFactory.getLoadBalancerContext(serviceId);
        
        URI uri;
        Server server;
        if (instance instanceof RibbonServer) {
            RibbonServer ribbonServer = (RibbonServer) instance;
            server = ribbonServer.getServer();
            uri = updateToSecureConnectionIfNeeded(original, ribbonServer);
        } else {
            server = new Server(instance.getUri().getHost(), instance.getUri().getPort());
            uri = original;
        }
        
        return context.reconstructURIWithServer(server, uri);
    }
}
```

## 四、Ribbon的工作流程

### 4.1 工作流程图

**工作流程图**
```
┌─────────────────────────────────────────────────────┐
│                   客户端                           │
└─────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────┐
│              LoadBalancerInterceptor                │
│                                                         │
│  1. 拦截HTTP请求                                       │
│  2. 获取服务名称                                       │
│  3. 调用LoadBalancerClient执行负载均衡                  │
└─────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────┐
│              RibbonLoadBalancerClient               │
│                                                         │
│  1. 获取ILoadBalancer                                  │
│  2. 调用IRule选择服务实例                              │
│  3. 调用IPing检查服务实例健康状态                       │
│  4. 发送请求到选择的服务实例                            │
└─────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────┐
│                   服务实例                           │
└─────────────────────────────────────────────────────┘
```

### 4.2 工作流程详解

**步骤1：拦截HTTP请求**
```java
@Override
public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    URI originalUri = request.getURI();
    String serviceName = originalUri.getHost();
    
    return this.loadBalancer.execute(serviceName, request, execution);
}
```

**步骤2：获取ILoadBalancer**
```java
protected ILoadBalancer getLoadBalancer(String serviceId) {
    return this.clientFactory.getLoadBalancer(serviceId);
}
```

**步骤3：调用IRule选择服务实例**
```java
@Override
public Server chooseServer(Object key) {
    if (rule == null) {
        return null;
    }
    return rule.choose(key);
}
```

**步骤4：调用IPing检查服务实例健康状态**
```java
@Override
public boolean isAlive(Server server) {
    if (server == null) {
        return false;
    }
    
    String urlStr = "";
    if (isSecure()) {
        urlStr = "https://";
    } else {
        urlStr = "http://";
    }
    
    urlStr += server.getId();
    urlStr += getPingAppendString();
    
    boolean isAlive = false;
    HttpClient httpClient = new DefaultHttpClient();
    HttpGet httpGet = new HttpGet(urlStr);
    
    try {
        HttpResponse response = httpClient.execute(httpGet);
        isAlive = response.getStatusLine().getStatusCode() == 200;
    } catch (IOException e) {
        isAlive = false;
    } finally {
        httpGet.releaseConnection();
    }
    
    return isAlive;
}
```

**步骤5：发送请求到选择的服务实例**
```java
@Override
public <T> T execute(String serviceId, ServiceInstance serviceInstance, LoadBalancerRequest<T> request) throws IOException {
    try {
        return request.apply(serviceInstance);
    } catch (IOException ex) {
        throw ex;
    } catch (Exception ex) {
        throw new RuntimeException(ex);
    }
}
```

## 五、总结

Ribbon的底层实现原理是基于客户端负载均衡，通过拦截HTTP请求，在发送请求之前选择一个服务实例，然后将请求发送到选择的服务实例。

### 核心要点

1. **Ribbon核心原理**：基于客户端负载均衡，通过拦截HTTP请求，在发送请求之前选择一个服务实例
2. **Ribbon核心组件**：ILoadBalancer、IRule、IPing、ServerList、ServerListUpdater
3. **Ribbon请求拦截**：LoadBalancerInterceptor拦截HTTP请求，调用LoadBalancerClient执行负载均衡
4. **Ribbon工作流程**：拦截HTTP请求 -> 获取ILoadBalancer -> 调用IRule选择服务实例 -> 调用IPing检查服务实例健康状态 -> 发送请求到选择的服务实例

### 实现原理

1. **ILoadBalancer**：负载均衡器接口，负责管理服务列表和选择服务实例
2. **IRule**：负载均衡策略接口，负责根据负载均衡策略选择服务实例
3. **IPing**：健康检查接口，负责检查服务实例的健康状态
4. **ServerList**：服务列表接口，负责获取服务列表
5. **ServerListUpdater**：服务列表更新接口，负责更新服务列表

Ribbon的底层实现原理是基于客户端负载均衡，通过拦截HTTP请求，在发送请求之前选择一个服务实例，然后将请求发送到选择的服务实例。
