# Tomcat JMX拓展机制

## 1. 概述

JMX（Java Management Extensions）是Java平台提供的一套管理和监控的标准API。Tomcat通过JMX提供了丰富的管理和监控功能，允许开发者和运维人员实时监控Tomcat的运行状态、性能指标，并进行动态配置和管理。

## 2. JMX基础

### 2.1 JMX架构

JMX架构由三个层次组成：

```
┌─────────────────────────────────────────┐
│        Instrumentation Level            │
│  (MBean - Managed Bean)                 │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│         Agent Level                     │
│  (MBeanServer - MBean服务器)            │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│        Distributed Services Level       │
│  (Connectors - 连接器)                   │
└─────────────────────────────────────────┘
```

### 2.2 MBean类型

| MBean类型 | 说明 | 示例 |
|----------|------|------|
| Standard MBean | 通过接口定义 | StandardServerMBean |
| Dynamic MBean | 动态实现 | DynamicMBean |
| Model MBean | 可配置的MBean | ModelMBean |
| Open MBean | 自描述的MBean | OpenMBean |

## 3. Tomcat中的JMX实现

### 3.1 JMXEnabledBase

JMXEnabledBase是Tomcat中支持JMX的基类。

```java
public abstract class JmxEnabled extends LifecycleMBeanBase {
    
    protected ObjectName oname = null;
    protected MBeanServer mserver = null;
    
    public JmxEnabled() {
        super();
    }
    
    protected void register(Object obj, ObjectName oname) throws Exception {
        MBeanServer mserver = getMBeanServer();
        if (mserver == null) {
            return;
        }
        
        try {
            mserver.registerMBean(obj, oname);
        } catch (InstanceAlreadyExistsException e) {
            mserver.unregisterMBean(oname);
            mserver.registerMBean(obj, oname);
        }
    }
    
    protected void unregister(ObjectName oname) throws Exception {
        MBeanServer mserver = getMBeanServer();
        if (mserver != null) {
            mserver.unregisterMBean(oname);
        }
    }
    
    protected MBeanServer getMBeanServer() {
        if (mserver == null) {
            mserver = Registry.getRegistry(null, null).getMBeanServer();
        }
        return mserver;
    }
}
```

### 3.2 LifecycleMBeanBase

LifecycleMBeanBase结合了生命周期和JMX功能。

```java
public abstract class LifecycleMBeanBase extends LifecycleBase 
        implements JmxEnabled {
    
    protected ObjectName oname = null;
    
    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        
        if (oname == null) {
            try {
                oname = register();
            } catch (Exception e) {
                throw new LifecycleException("注册MBean失败", e);
            }
        }
    }
    
    @Override
    protected void destroyInternal() throws LifecycleException {
        super.destroyInternal();
        
        if (oname != null) {
            try {
                unregister(oname);
            } catch (Exception e) {
                throw new LifecycleException("注销MBean失败", e);
            }
            oname = null;
        }
    }
    
    protected ObjectName register() throws Exception {
        String domain = getDomain();
        ObjectName name = getObjectNameKeyProperties();
        
        ObjectName oname = new ObjectName(domain + ":type=" + name);
        
        register(this, oname);
        
        return oname;
    }
    
    protected ObjectName getObjectNameKeyProperties() {
        return null;
    }
    
    protected String getDomain() {
        return "Catalina";
    }
}
```

## 4. Standard MBean实现

### 4.1 MBean接口定义

```java
public interface StandardServerMBean {
    String getInfo();
    String getServerInfo();
    int getPort();
    void setPort(int port);
    String getShutdown();
    void setShutdown(String shutdown);
    Service[] findServices();
    void addService(Service service);
    void removeService(Service service);
}
```

### 4.2 MBean实现

```java
public class StandardServer extends LifecycleMBeanBase 
        implements Server, StandardServerMBean {
    
    private int port = 8005;
    private String shutdown = "SHUTDOWN";
    private Service[] services = new Service[0];
    
    @Override
    public String getInfo() {
        return "Apache Tomcat/9.0.x";
    }
    
    @Override
    public String getServerInfo() {
        return "Apache Tomcat/9.0.x";
    }
    
    @Override
    public int getPort() {
        return port;
    }
    
    @Override
    public void setPort(int port) {
        this.port = port;
    }
    
    @Override
    public String getShutdown() {
        return shutdown;
    }
    
    @Override
    public void setShutdown(String shutdown) {
        this.shutdown = shutdown;
    }
    
    @Override
    public Service[] findServices() {
        return services;
    }
    
    @Override
    public void addService(Service service) {
        service.setServer(this);
        Service[] results = new Service[services.length + 1];
        System.arraycopy(services, 0, results, 0, services.length);
        results[services.length] = service;
        services = results;
    }
    
    @Override
    public void removeService(Service service) {
        int j = -1;
        for (int i = 0; i < services.length; i++) {
            if (service == services[i]) {
                j = i;
                break;
            }
        }
        if (j < 0) {
            return;
        }
        
        int k = 0;
        Service[] results = new Service[services.length - 1];
        for (int i = 0; i < services.length; i++) {
            if (i != j) {
                results[k++] = services[i];
            }
        }
        services = results;
    }
    
    @Override
    protected ObjectName getObjectNameKeyProperties() {
        return new ObjectName("type=Server");
    }
}
```

## 5. Dynamic MBean实现

### 5.1 DynamicMBean接口

```java
public interface DynamicMBean extends MBean {
    Object getAttribute(String attribute) 
        throws AttributeNotFoundException, 
               MBeanException, 
               ReflectionException;
    
    void setAttribute(Attribute attribute) 
        throws AttributeNotFoundException, 
               InvalidAttributeValueException, 
               MBeanException, 
               ReflectionException;
    
    AttributeList getAttributes(String[] attributes);
    
    AttributeList setAttributes(AttributeList attributes);
    
    Object invoke(String actionName, Object[] params, String[] signature) 
        throws MBeanException, 
               ReflectionException;
    
    MBeanInfo getMBeanInfo();
}
```

### 5.2 DynamicMBean实现示例

```java
public class DynamicServerMBean implements DynamicMBean {
    
    private StandardServer server;
    private MBeanInfo mBeanInfo;
    
    public DynamicServerMBean(StandardServer server) {
        this.server = server;
        this.mBeanInfo = buildMBeanInfo();
    }
    
    @Override
    public Object getAttribute(String attribute) 
            throws AttributeNotFoundException, 
                   MBeanException, 
                   ReflectionException {
        
        switch (attribute) {
            case "port":
                return server.getPort();
            case "shutdown":
                return server.getShutdown();
            case "serverInfo":
                return server.getServerInfo();
            default:
                throw new AttributeNotFoundException("Attribute not found: " + attribute);
        }
    }
    
    @Override
    public void setAttribute(Attribute attribute) 
            throws AttributeNotFoundException, 
                   InvalidAttributeValueException, 
                   MBeanException, 
                   ReflectionException {
        
        String name = attribute.getName();
        Object value = attribute.getValue();
        
        switch (name) {
            case "port":
                if (!(value instanceof Integer)) {
                    throw new InvalidAttributeValueException("Invalid value type");
                }
                server.setPort((Integer) value);
                break;
            case "shutdown":
                if (!(value instanceof String)) {
                    throw new InvalidAttributeValueException("Invalid value type");
                }
                server.setShutdown((String) value);
                break;
            default:
                throw new AttributeNotFoundException("Attribute not found: " + name);
        }
    }
    
    @Override
    public AttributeList getAttributes(String[] attributes) {
        AttributeList list = new AttributeList();
        
        for (String attribute : attributes) {
            try {
                Object value = getAttribute(attribute);
                list.add(new Attribute(attribute, value));
            } catch (Exception e) {
                // Ignore
            }
        }
        
        return list;
    }
    
    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        AttributeList result = new AttributeList();
        
        for (Object obj : attributes) {
            Attribute attribute = (Attribute) obj;
            try {
                setAttribute(attribute);
                result.add(attribute);
            } catch (Exception e) {
                // Ignore
            }
        }
        
        return result;
    }
    
    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) 
            throws MBeanException, ReflectionException {
        
        switch (actionName) {
            case "getServices":
                return server.findServices();
            case "addService":
                if (params != null && params.length > 0 && params[0] instanceof Service) {
                    server.addService((Service) params[0]);
                    return null;
                }
                throw new MBeanException(new IllegalArgumentException("Invalid parameters"));
            case "removeService":
                if (params != null && params.length > 0 && params[0] instanceof Service) {
                    server.removeService((Service) params[0]);
                    return null;
                }
                throw new MBeanException(new IllegalArgumentException("Invalid parameters"));
            default:
                throw new ReflectionException(new NoSuchMethodException("Method not found"));
        }
    }
    
    @Override
    public MBeanInfo getMBeanInfo() {
        return mBeanInfo;
    }
    
    private MBeanInfo buildMBeanInfo() {
        MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[] {
            new MBeanAttributeInfo("port", "int", "Shutdown port", true, true, false),
            new MBeanAttributeInfo("shutdown", "java.lang.String", "Shutdown command", true, true, false),
            new MBeanAttributeInfo("serverInfo", "java.lang.String", "Server information", true, false, false)
        };
        
        MBeanOperationInfo[] operations = new MBeanOperationInfo[] {
            new MBeanOperationInfo("getServices", "Get all services", null, "[Lorg.apache.catalina.Service;", MBeanOperationInfo.INFO),
            new MBeanOperationInfo("addService", "Add a service", 
                new MBeanParameterInfo[] {
                    new MBeanParameterInfo("service", "org.apache.catalina.Service", "Service to add")
                }, "void", MBeanOperationInfo.ACTION),
            new MBeanOperationInfo("removeService", "Remove a service", 
                new MBeanParameterInfo[] {
                    new MBeanParameterInfo("service", "org.apache.catalina.Service", "Service to remove")
                }, "void", MBeanOperationInfo.ACTION)
        };
        
        return new MBeanInfo(
            this.getClass().getName(),
            "Tomcat Server MBean",
            attributes,
            null,
            operations,
            null
        );
    }
}
```

## 6. JMX监控

### 6.1 连接MBeanServer

```java
public class JmxMonitor {
    
    private MBeanServerConnection connection;
    private JMXConnector connector;
    
    public void connect(String host, int port, String username, String password) 
            throws IOException {
        
        String url = String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, port);
        
        Map<String, Object> env = new HashMap<>();
        if (username != null && password != null) {
            String[] credentials = new String[] {username, password};
            env.put(JMXConnector.CREDENTIALS, credentials);
        }
        
        JMXServiceURL jmxUrl = new JMXServiceURL(url);
        connector = JMXConnectorFactory.connect(jmxUrl, env);
        connection = connector.getMBeanServerConnection();
    }
    
    public void disconnect() throws IOException {
        if (connector != null) {
            connector.close();
            connector = null;
            connection = null;
        }
    }
    
    public Object getAttribute(ObjectName name, String attribute) 
            throws Exception {
        return connection.getAttribute(name, attribute);
    }
    
    public void setAttribute(ObjectName name, Attribute attribute) 
            throws Exception {
        connection.setAttribute(name, attribute);
    }
    
    public Object invoke(ObjectName name, String operation, Object[] params, String[] signature) 
            throws Exception {
        return connection.invoke(name, operation, params, signature);
    }
}
```

### 6.2 监控Tomcat状态

```java
public class TomcatMonitor {
    
    private static final Logger logger = LoggerFactory.getLogger(TomcatMonitor.class);
    
    public static void monitorServer(String host, int port) {
        try {
            JmxMonitor monitor = new JmxMonitor();
            monitor.connect(host, port, null, null);
            
            ObjectName serverName = new ObjectName("Catalina:type=Server");
            
            String serverInfo = (String) monitor.getAttribute(serverName, "serverInfo");
            logger.info("Server Info: {}", serverInfo);
            
            ObjectName[] services = (ObjectName[]) monitor.invoke(serverName, "findServices", null, null);
            for (ObjectName serviceName : services) {
                monitorService(monitor, serviceName);
            }
            
            monitor.disconnect();
        } catch (Exception e) {
            logger.error("监控失败", e);
        }
    }
    
    private static void monitorService(JmxMonitor monitor, ObjectName serviceName) 
            throws Exception {
        String name = (String) monitor.getAttribute(serviceName, "name");
        logger.info("Service: {}", name);
        
        ObjectName[] connectors = (ObjectName[]) monitor.invoke(serviceName, "findConnectors", null, null);
        for (ObjectName connectorName : connectors) {
            monitorConnector(monitor, connectorName);
        }
        
        ObjectName engineName = (ObjectName) monitor.getAttribute(serviceName, "engine");
        monitorEngine(monitor, engineName);
    }
    
    private static void monitorConnector(JmxMonitor monitor, ObjectName connectorName) 
            throws Exception {
        String scheme = (String) monitor.getAttribute(connectorName, "scheme");
        String protocol = (String) monitor.getAttribute(connectorName, "protocol");
        int port = (Integer) monitor.getAttribute(connectorName, "port");
        
        logger.info("  Connector: {}://{}:{}", scheme, protocol, port);
        
        ObjectName threadPoolName = new ObjectName(connectorName.toString() + ",type=ThreadPool");
        int maxThreads = (Integer) monitor.getAttribute(threadPoolName, "maxThreads");
        int currentThreadsBusy = (Integer) monitor.getAttribute(threadPoolName, "currentThreadsBusy");
        int currentThreadCount = (Integer) monitor.getAttribute(threadPoolName, "currentThreadCount");
        
        logger.info("    Threads: {}/{}/{}", currentThreadsBusy, currentThreadCount, maxThreads);
    }
    
    private static void monitorEngine(JmxMonitor monitor, ObjectName engineName) 
            throws Exception {
        String name = (String) monitor.getAttribute(engineName, "name");
        logger.info("  Engine: {}", name);
        
        ObjectName[] hosts = (ObjectName[]) monitor.invoke(engineName, "findChildren", null, null);
        for (ObjectName hostName : hosts) {
            monitorHost(monitor, hostName);
        }
    }
    
    private static void monitorHost(JmxMonitor monitor, ObjectName hostName) 
            throws Exception {
        String name = (String) monitor.getAttribute(hostName, "name");
        logger.info("    Host: {}", name);
        
        ObjectName[] contexts = (ObjectName[]) monitor.invoke(hostName, "findChildren", null, null);
        for (ObjectName contextName : contexts) {
            monitorContext(monitor, contextName);
        }
    }
    
    private static void monitorContext(JmxMonitor monitor, ObjectName contextName) 
            throws Exception {
        String name = (String) monitor.getAttribute(contextName, "name");
        logger.info("      Context: {}", name);
        
        ObjectName[] wrappers = (ObjectName[]) monitor.invoke(contextName, "findChildren", null, null);
        for (ObjectName wrapperName : wrappers) {
            monitorWrapper(monitor, wrapperName);
        }
    }
    
    private static void monitorWrapper(JmxMonitor monitor, ObjectName wrapperName) 
            throws Exception {
        String name = (String) monitor.getAttribute(wrapperName, "name");
        String servletClass = (String) monitor.getAttribute(wrapperName, "servletClass");
        logger.info("        Wrapper: {} ({})", name, servletClass);
    }
}
```

## 7. JMX配置

### 7.1 启用JMX远程监控

在catalina.sh或catalina.bat中添加JMX参数：

```bash
CATALINA_OPTS="$CATALINA_OPTS -Dcom.sun.management.jmxremote"
CATALINA_OPTS="$CATALINA_OPTS -Dcom.sun.management.jmxremote.port=9999"
CATALINA_OPTS="$CATALINA_OPTS -Dcom.sun.management.jmxremote.ssl=false"
CATALINA_OPTS="$CATALINA_OPTS -Dcom.sun.management.jmxremote.authenticate=false"
```

### 7.2 启用认证

```bash
CATALINA_OPTS="$CATALINA_OPTS -Dcom.sun.management.jmxremote.authenticate=true"
CATALINA_OPTS="$CATALINA_OPTS -Dcom.sun.management.jmxremote.password.file=$CATALINA_BASE/conf/jmxremote.password"
CATALINA_OPTS="$CATALINA_OPTS -Dcom.sun.management.jmxremote.access.file=$CATALINA_BASE/conf/jmxremote.access"
```

### 7.3 jmxremote.access

```
admin readwrite
monitor readonly
```

### 7.4 jmxremote.password

```
admin adminpassword
monitor monitorpassword
```

## 8. 自定义MBean

### 8.1 创建自定义MBean

```java
public interface CustomMonitorMBean {
    long getRequestCount();
    long getErrorCount();
    double getAverageResponseTime();
    void reset();
}

public class CustomMonitor implements CustomMonitorMBean {
    
    private AtomicLong requestCount = new AtomicLong(0);
    private AtomicLong errorCount = new AtomicLong(0);
    private AtomicLong totalResponseTime = new AtomicLong(0);
    
    public void recordRequest(long responseTime, boolean error) {
        requestCount.incrementAndGet();
        if (error) {
            errorCount.incrementAndGet();
        }
        totalResponseTime.addAndGet(responseTime);
    }
    
    @Override
    public long getRequestCount() {
        return requestCount.get();
    }
    
    @Override
    public long getErrorCount() {
        return errorCount.get();
    }
    
    @Override
    public double getAverageResponseTime() {
        long count = requestCount.get();
        if (count == 0) {
            return 0;
        }
        return (double) totalResponseTime.get() / count;
    }
    
    @Override
    public void reset() {
        requestCount.set(0);
        errorCount.set(0);
        totalResponseTime.set(0);
    }
}
```

### 8.2 注册自定义MBean

```java
public class CustomMonitorValve extends ValveBase {
    
    private CustomMonitor monitor;
    private ObjectName monitorName;
    
    @Override
    public void initInternal() throws LifecycleException {
        super.initInternal();
        
        try {
            monitor = new CustomMonitor();
            monitorName = new ObjectName("Catalina:type=CustomMonitor");
            
            MBeanServer mserver = getMBeanServer();
            mserver.registerMBean(monitor, monitorName);
        } catch (Exception e) {
            throw new LifecycleException("注册自定义MBean失败", e);
        }
    }
    
    @Override
    public void destroyInternal() throws LifecycleException {
        super.destroyInternal();
        
        try {
            if (monitorName != null) {
                MBeanServer mserver = getMBeanServer();
                mserver.unregisterMBean(monitorName);
            }
        } catch (Exception e) {
            throw new LifecycleException("注销自定义MBean失败", e);
        }
    }
    
    @Override
    public void invoke(Request request, Response response) 
            throws IOException, ServletException {
        
        long startTime = System.currentTimeMillis();
        boolean error = false;
        
        try {
            Valve next = getNext();
            if (next != null) {
                next.invoke(request, response);
            }
        } catch (Exception e) {
            error = true;
            throw e;
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            monitor.recordRequest(responseTime, error);
        }
    }
}
```

## 9. JMX最佳实践

### 9.1 监控指标

```java
public class JmxMetrics {
    
    public static void collectMetrics(MBeanServerConnection connection) 
            throws Exception {
        
        ObjectName serverName = new ObjectName("Catalina:type=Server");
        
        collectThreadPoolMetrics(connection);
        collectRequestMetrics(connection);
        collectMemoryMetrics(connection);
        collectGCMetrics(connection);
    }
    
    private static void collectThreadPoolMetrics(MBeanServerConnection connection) 
            throws Exception {
        
        ObjectName threadPoolName = new ObjectName("Catalina:type=ThreadPool,*");
        Set<ObjectName> threadPools = connection.queryNames(threadPoolName, null);
        
        for (ObjectName name : threadPools) {
            int maxThreads = (Integer) connection.getAttribute(name, "maxThreads");
            int currentThreadsBusy = (Integer) connection.getAttribute(name, "currentThreadsBusy");
            int currentThreadCount = (Integer) connection.getAttribute(name, "currentThreadCount");
            
            double utilization = (double) currentThreadsBusy / maxThreads * 100;
            
            System.out.println("ThreadPool: " + name.getKeyProperty("name"));
            System.out.println("  Utilization: " + String.format("%.2f%%", utilization));
            System.out.println("  Threads: " + currentThreadsBusy + "/" + currentThreadCount + "/" + maxThreads);
        }
    }
    
    private static void collectRequestMetrics(MBeanServerConnection connection) 
            throws Exception {
        
        ObjectName requestProcessorName = new ObjectName("Catalina:type=RequestProcessor,*");
        Set<ObjectName> processors = connection.queryNames(requestProcessorName, null);
        
        long totalRequestCount = 0;
        long totalProcessingTime = 0;
        long totalErrorCount = 0;
        
        for (ObjectName name : processors) {
            long requestCount = (Long) connection.getAttribute(name, "requestCount");
            long processingTime = (Long) connection.getAttribute(name, "processingTime");
            long errorCount = (Long) connection.getAttribute(name, "errorCount");
            
            totalRequestCount += requestCount;
            totalProcessingTime += processingTime;
            totalErrorCount += errorCount;
        }
        
        double avgProcessingTime = totalRequestCount > 0 ? 
            (double) totalProcessingTime / totalRequestCount : 0;
        
        System.out.println("Request Metrics:");
        System.out.println("  Total Requests: " + totalRequestCount);
        System.out.println("  Total Errors: " + totalErrorCount);
        System.out.println("  Avg Processing Time: " + String.format("%.2f ms", avgProcessingTime));
    }
    
    private static void collectMemoryMetrics(MBeanServerConnection connection) 
            throws Exception {
        
        ObjectName memoryName = new ObjectName("java.lang:type=Memory");
        Object heapMemoryUsage = connection.getAttribute(memoryName, "HeapMemoryUsage");
        
        CompositeData heapUsage = (CompositeData) heapMemoryUsage;
        long used = (Long) heapUsage.get("used");
        long max = (Long) heapUsage.get("max");
        double utilization = (double) used / max * 100;
        
        System.out.println("Memory Metrics:");
        System.out.println("  Heap Usage: " + String.format("%.2f%%", utilization));
        System.out.println("  Heap Used: " + (used / 1024 / 1024) + " MB");
        System.out.println("  Heap Max: " + (max / 1024 / 1024) + " MB");
    }
    
    private static void collectGCMetrics(MBeanServerConnection connection) 
            throws Exception {
        
        ObjectName gcName = new ObjectName("java.lang:type=GarbageCollector,*");
        Set<ObjectName> gcCollectors = connection.queryNames(gcName, null);
        
        for (ObjectName name : gcCollectors) {
            String collectorName = name.getKeyProperty("name");
            long collectionCount = (Long) connection.getAttribute(name, "collectionCount");
            long collectionTime = (Long) connection.getAttribute(name, "collectionTime");
            
            System.out.println("GC Collector: " + collectorName);
            System.out.println("  Collections: " + collectionCount);
            System.out.println("  Total Time: " + collectionTime + " ms");
        }
    }
}
```

### 9.2 告警机制

```java
public class JmxAlert {
    
    private static final Logger logger = LoggerFactory.getLogger(JmxAlert.class);
    
    private double threadPoolThreshold = 80.0;
    private double memoryThreshold = 80.0;
    private double errorRateThreshold = 5.0;
    
    public void checkAlerts(MBeanServerConnection connection) 
            throws Exception {
        
        checkThreadPoolAlert(connection);
        checkMemoryAlert(connection);
        checkErrorRateAlert(connection);
    }
    
    private void checkThreadPoolAlert(MBeanServerConnection connection) 
            throws Exception {
        
        ObjectName threadPoolName = new ObjectName("Catalina:type=ThreadPool,*");
        Set<ObjectName> threadPools = connection.queryNames(threadPoolName, null);
        
        for (ObjectName name : threadPools) {
            int maxThreads = (Integer) connection.getAttribute(name, "maxThreads");
            int currentThreadsBusy = (Integer) connection.getAttribute(name, "currentThreadsBusy");
            
            double utilization = (double) currentThreadsBusy / maxThreads * 100;
            
            if (utilization > threadPoolThreshold) {
                logger.warn("ThreadPool utilization is high: {}% (threshold: {}%)", 
                    String.format("%.2f", utilization), threadPoolThreshold);
            }
        }
    }
    
    private void checkMemoryAlert(MBeanServerConnection connection) 
            throws Exception {
        
        ObjectName memoryName = new ObjectName("java.lang:type=Memory");
        Object heapMemoryUsage = connection.getAttribute(memoryName, "HeapMemoryUsage");
        
        CompositeData heapUsage = (CompositeData) heapMemoryUsage;
        long used = (Long) heapUsage.get("used");
        long max = (Long) heapUsage.get("max");
        double utilization = (double) used / max * 100;
        
        if (utilization > memoryThreshold) {
            logger.warn("Memory usage is high: {}% (threshold: {}%)", 
                String.format("%.2f", utilization), memoryThreshold);
        }
    }
    
    private void checkErrorRateAlert(MBeanServerConnection connection) 
            throws Exception {
        
        ObjectName requestProcessorName = new ObjectName("Catalina:type=RequestProcessor,*");
        Set<ObjectName> processors = connection.queryNames(requestProcessorName, null);
        
        long totalRequestCount = 0;
        long totalErrorCount = 0;
        
        for (ObjectName name : processors) {
            long requestCount = (Long) connection.getAttribute(name, "requestCount");
            long errorCount = (Long) connection.getAttribute(name, "errorCount");
            
            totalRequestCount += requestCount;
            totalErrorCount += errorCount;
        }
        
        if (totalRequestCount > 0) {
            double errorRate = (double) totalErrorCount / totalRequestCount * 100;
            
            if (errorRate > errorRateThreshold) {
                logger.warn("Error rate is high: {}% (threshold: {}%)", 
                    String.format("%.2f", errorRate), errorRateThreshold);
            }
        }
    }
}
```

## 10. 总结

Tomcat的JMX扩展机制是其重要的管理和监控功能，具有以下特点：

1. **标准化管理**：基于JMX标准，提供统一的管理接口
2. **实时监控**：实时监控Tomcat的运行状态和性能指标
3. **动态配置**：支持动态修改配置，无需重启
4. **扩展性强**：支持自定义MBean，扩展监控功能
5. **易于集成**：易于与监控系统集成，如Zabbix、Prometheus等

理解Tomcat的JMX扩展机制，对于：
- 监控Tomcat运行状态
- 诊断性能问题
- 实现自动化运维
- 开发监控工具

都具有重要意义。Tomcat的JMX机制是其优秀架构设计的重要体现，为Java Web应用的管理和监控提供了强大的支持。