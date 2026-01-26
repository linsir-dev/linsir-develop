# Tomcat整体架构的设计

## 1. Tomcat概述

Apache Tomcat是一个开源的Servlet容器，实现了Java Servlet、JavaServer Pages（JSP）、Java Expression Language和Java WebSocket等技术规范。Tomcat作为Web服务器和Servlet容器，为Java Web应用提供了运行环境。

### 1.1 Tomcat的核心功能

- **Servlet容器**：管理和执行Servlet
- **JSP引擎**：编译和执行JSP页面
- **Web服务器**：处理HTTP请求和响应
- **连接器**：处理客户端连接
- **安全管理**：提供安全认证和授权

### 1.2 Tomcat的版本演进

| 版本 | Servlet规范 | JSP规范 | EL规范 | WebSocket规范 | 发布时间 |
|-----|-----------|---------|--------|--------------|---------|
| Tomcat 10 | 5.0 | 3.0 | 4.0 | 2.0 | 2020 |
| Tomcat 9 | 4.0 | 2.3 | 3.0 | 1.1 | 2017 |
| Tomcat 8.5 | 3.1 | 2.3 | 3.0 | 1.1 | 2016 |
| Tomcat 8 | 3.1 | 2.3 | 3.0 | 1.1 | 2014 |
| Tomcat 7 | 3.0 | 2.2 | 2.2 | 1.1 | 2011 |

## 2. Tomcat整体架构

Tomcat的架构设计采用了分层和模块化的设计思想，主要分为两个核心部分：**Connector（连接器）**和**Container（容器）**。

### 2.1 架构图

```
┌─────────────────────────────────────────────────────────┐
│                      Server                             │
│  ┌─────────────────────────────────────────────────┐   │
│  │              Service                             │   │
│  │  ┌──────────────┐      ┌──────────────────┐    │   │
│  │  │  Connector   │      │    Container      │    │   │
│  │  │  (连接器)    │─────▶│    (容器)         │    │   │
│  │  │              │      │                  │    │   │
│  │  │  - HTTP      │      │  ┌──────────────┐  │    │   │
│  │  │  - AJP       │      │  │   Engine     │  │    │   │
│  │  │  - HTTP/2    │      │  │   (引擎)     │  │    │   │
│  │  └──────────────┘      │  └──────┬───────┘  │    │   │
│  │                       │         │          │    │   │
│  │                       │  ┌──────▼───────┐  │    │   │
│  │                       │  │   Host       │  │    │   │
│  │                       │  │   (主机)     │  │    │   │
│  │                       │  └──────┬───────┘  │    │   │
│  │                       │         │          │    │   │
│  │                       │  ┌──────▼───────┐  │    │   │
│  │                       │  │   Context    │  │    │   │
│  │                       │  │   (应用)     │  │    │   │
│  │                       │  └──────┬───────┘  │    │   │
│  │                       │         │          │    │   │
│  │                       │  ┌──────▼───────┐  │    │   │
│  │                       │  │  Wrapper     │  │    │   │
│  │                       │  │  (Servlet)   │  │    │   │
│  │                       │  └──────────────┘  │    │   │
│  │                       └──────────────────┘    │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### 2.2 核心组件

#### 2.2.1 Server

Server是Tomcat中最顶层的组件，代表整个Tomcat服务器实例。

**主要职责**：
- 管理多个Service组件
- 监听关闭端口（默认8005）
- 优雅关闭Tomcat

**配置示例**：

```xml
<Server port="8005" shutdown="SHUTDOWN">
    <Service name="Catalina">
        <!-- Connector和Container配置 -->
    </Service>
</Server>
```

#### 2.2.2 Service

Service是Server的子组件，代表一个服务，包含一个或多个Connector和一个Container。

**主要职责**：
- 组合Connector和Container
- 提供统一的服务接口

**配置示例**：

```xml
<Service name="Catalina">
    <Connector port="8080" protocol="HTTP/1.1"/>
    <Connector port="8009" protocol="AJP/1.3"/>
    <Engine name="Catalina" defaultHost="localhost">
        <!-- Container配置 -->
    </Engine>
</Service>
```

#### 2.2.3 Connector

Connector负责处理客户端连接，接收HTTP请求，并将请求传递给Container处理。

**主要职责**：
- 监听网络端口
- 接收客户端连接
- 解析HTTP请求
- 封装HTTP响应
- 支持多种协议（HTTP、AJP、HTTP/2等）

**支持的协议**：

| 协议 | 说明 | 端口 |
|-----|------|------|
| HTTP/1.1 | 标准HTTP协议 | 8080 |
| AJP | Apache JServ Protocol | 8009 |
| HTTP/2 | HTTP/2协议 | 8443 |
| HTTP/3 | HTTP/3协议（QUIC） | 8443 |

**配置示例**：

```xml
<!-- HTTP连接器 -->
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443"/>

<!-- AJP连接器 -->
<Connector port="8009" protocol="AJP/1.3"
           redirectPort="8443"/>

<!-- HTTPS连接器 -->
<Connector port="8443" protocol="HTTP/1.1"
           SSLEnabled="true"
           maxThreads="150"
           scheme="https"
           secure="true"
           clientAuth="false"
           sslProtocol="TLS"/>
```

#### 2.2.4 Container

Container负责管理和执行Servlet，包含Engine、Host、Context、Wrapper四个层次的容器。

**容器层次结构**：

```
Engine (引擎)
    └── Host (虚拟主机)
            └── Context (Web应用)
                    └── Wrapper (Servlet包装器)
```

## 3. Connector详解

### 3.1 Connector架构

Connector采用了**多线程模型**和**NIO模型**来提高性能。

```
┌─────────────────────────────────────────┐
│         Connector                      │
│  ┌─────────────────────────────────┐   │
│  │      ProtocolHandler          │   │
│  │  (协议处理器)                  │   │
│  │  - Http11Protocol             │   │
│  │  - AjpProtocol                │   │
│  └──────────────┬──────────────┘   │
│                 │                   │
│  ┌──────────────▼──────────────┐   │
│  │      Endpoint               │   │
│  │  (端点)                      │   │
│  │  - JIoEndpoint              │   │
│  │  - NioEndpoint              │   │
│  │  - Nio2Endpoint             │   │
│  └──────────────┬──────────────┘   │
│                 │                   │
│  ┌──────────────▼──────────────┐   │
│  │      Processor              │   │
│  │  (处理器)                    │   │
│  │  - SocketProcessor           │   │
│  │  - AjpProcessor             │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

### 3.2 Connector工作流程

1. **监听端口**：Endpoint监听指定端口
2. **接收连接**：接收客户端连接请求
3. **创建Socket**：为每个连接创建Socket
4. **分配线程**：从线程池分配线程处理连接
5. **解析请求**：Processor解析HTTP请求
6. **传递请求**：将请求传递给Container处理
7. **返回响应**：接收Container的响应并返回给客户端

### 3.3 Connector实现方式

#### 3.3.1 BIO（Blocking I/O）

传统的阻塞I/O模型，每个连接占用一个线程。

**特点**：
- 简单易用
- 并发能力有限
- 线程开销大

**配置示例**：

```xml
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           maxThreads="200"/>
```

#### 3.3.2 NIO（Non-blocking I/O）

非阻塞I/O模型，使用Selector实现多路复用。

**特点**：
- 高并发性能
- 线程利用率高
- 支持更多连接

**配置示例**：

```xml
<Connector port="8080" protocol="org.apache.coyote.http11.Http11NioProtocol"
           connectionTimeout="20000"
           maxThreads="200"/>
```

#### 3.3.3 NIO2（Non-blocking I/O 2）

基于Java 7的异步I/O模型。

**特点**：
- 完全异步
- 性能最优
- 需要Java 7+

**配置示例**：

```xml
<Connector port="8080" protocol="org.apache.coyote.http11.Http11Nio2Protocol"
           connectionTimeout="20000"
           maxThreads="200"/>
```

#### 3.3.4 APR（Apache Portable Runtime）

使用本地库（APR）提供更高性能。

**特点**：
- 性能最优
- 需要安装APR库
- 支持SSL加速

**配置示例**：

```xml
<Connector port="8080" protocol="org.apache.coyote.http11.Http11AprProtocol"
           connectionTimeout="20000"
           maxThreads="200"/>
```

## 4. Container详解

### 4.1 Container层次结构

Container采用分层设计，从外到内依次为：Engine、Host、Context、Wrapper。

```
┌─────────────────────────────────────────┐
│           Engine (引擎)                  │
│  ┌─────────────────────────────────┐   │
│  │        Host (虚拟主机)          │   │
│  │  ┌─────────────────────────┐   │   │
│  │  │     Context (Web应用)    │   │
│  │  │  ┌─────────────────┐   │   │   │
│  │  │  │  Wrapper (Servlet)│   │   │   │
│  │  │  └─────────────────┘   │   │   │
│  │  └─────────────────────────┘   │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

### 4.2 Engine

Engine是Container的最顶层，代表整个Servlet引擎。

**主要职责**：
- 管理多个Host
- 处理所有Host的请求
- 提供基本的容器功能

**配置示例**：

```xml
<Engine name="Catalina" defaultHost="localhost">
    <Host name="localhost" appBase="webapps" unpackWARs="true">
        <!-- Context配置 -->
    </Host>
</Engine>
```

### 4.3 Host

Host代表一个虚拟主机，可以配置多个域名。

**主要职责**：
- 管理多个Context
- 支持虚拟主机
- 配置域名和别名

**配置示例**：

```xml
<Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="true">
    <Context path="" docBase="ROOT"/>
    <Context path="/examples" docBase="examples"/>
</Host>

<Host name="www.example.com" appBase="webapps-example">
    <Context path="" docBase="example-app"/>
</Host>
```

### 4.4 Context

Context代表一个Web应用，对应一个Web应用目录。

**主要职责**：
- 管理多个Wrapper
- 加载Web应用
- 提供Servlet上下文环境

**配置示例**：

```xml
<Context path="/myapp" docBase="myapp" reloadable="true">
    <Resource name="jdbc/MyDB" auth="Container"
              type="javax.sql.DataSource"
              maxTotal="100" maxIdle="30" maxWaitMillis="10000"
              username="dbuser" password="dbpass"
              driverClassName="com.mysql.jdbc.Driver"
              url="jdbc:mysql://localhost:3306/mydb"/>
</Context>
```

### 4.5 Wrapper

Wrapper代表一个Servlet包装器，封装了Servlet实例。

**主要职责**：
- 管理Servlet生命周期
- 加载Servlet类
- 调用Servlet方法

**配置示例**：

```xml
<servlet>
    <servlet-name>MyServlet</servlet-name>
    <servlet-class>com.example.MyServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
    <servlet-name>MyServlet</servlet-name>
    <url-pattern>/myservlet</url-pattern>
</servlet-mapping>
```

## 5. Tomcat目录结构

### 5.1 标准目录结构

```
tomcat/
├── bin/              # 启动和关闭脚本
│   ├── startup.sh
│   ├── shutdown.sh
│   ├── catalina.sh
│   └── ...
├── conf/             # 配置文件
│   ├── server.xml    # 主配置文件
│   ├── web.xml       # Web应用默认配置
│   ├── tomcat-users.xml # 用户配置
│   └── ...
├── lib/              # Tomcat类库
├── logs/             # 日志文件
├── temp/             # 临时文件
├── webapps/          # Web应用目录
│   ├── ROOT/
│   ├── examples/
│   └── ...
├── work/             # 工作目录（JSP编译后的文件）
└── ...
```

### 5.2 配置文件说明

| 配置文件 | 说明 |
|---------|------|
| server.xml | Tomcat主配置文件，配置Server、Service、Connector、Container等 |
| web.xml | Web应用默认配置，定义Servlet、Filter、Listener等 |
| tomcat-users.xml | 配置Tomcat管理用户 |
| context.xml | 全局Context配置 |
| catalina.properties | Catalina属性配置 |
| logging.properties | 日志配置 |

## 6. Tomcat启动流程

### 6.1 启动流程图

```
启动脚本 (startup.sh)
    │
    ▼
Catalina类
    │
    ▼
解析配置文件 (server.xml)
    │
    ▼
创建Server对象
    │
    ▼
创建Service对象
    │
    ▼
创建Connector对象
    │
    ▼
创建Container对象
    │
    ▼
初始化各组件 (init)
    │
    ▼
启动各组件 (start)
    │
    ▼
监听端口，等待请求
```

### 6.2 启动代码示例

```java
public class Catalina {
    protected Server server;
    
    public void start() {
        // 1. 解析配置文件
        parseServerXml();
        
        // 2. 初始化Server
        server.init();
        
        // 3. 启动Server
        server.start();
        
        // 4. 等待关闭命令
        await();
    }
    
    protected void parseServerXml() {
        // 解析server.xml配置文件
        // 创建Server、Service、Connector、Container等对象
    }
}
```

## 7. Tomcat性能优化

### 7.1 Connector优化

```xml
<Connector port="8080" protocol="HTTP/1.1"
           maxThreads="200"
           minSpareThreads="25"
           maxSpareThreads="75"
           acceptCount="100"
           connectionTimeout="20000"
           enableLookups="false"
           compression="on"
           compressionMinSize="2048"
           noCompressionUserAgents="gozilla, traviata"
           compressableMimeType="text/html,text/xml,text/plain,text/css,text/javascript,application/javascript"/>
```

**参数说明**：

| 参数 | 说明 | 推荐值 |
|-----|------|-------|
| maxThreads | 最大线程数 | 200-500 |
| minSpareThreads | 最小空闲线程 | 25-50 |
| maxSpareThreads | 最大空闲线程 | 50-100 |
| acceptCount | 等待队列长度 | 100-200 |
| connectionTimeout | 连接超时时间 | 20000ms |
| enableLookups | 是否启用DNS查询 | false |
| compression | 是否启用压缩 | on |

### 7.2 JVM优化

```bash
#!/bin/bash
JAVA_OPTS="-server \
-Xms2g \
-Xmx2g \
-Xmn1g \
-XX:MetaspaceSize=128m \
-XX:MaxMetaspaceSize=256m \
-XX:+UseG1GC \
-XX:MaxGCPauseMillis=200 \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=/logs/heapdump.hprof \
-XX:+PrintGCDetails \
-XX:+PrintGCDateStamps \
-Xloggc:/logs/gc.log"

export JAVA_OPTS
```

### 7.3 操作系统优化

```bash
# 增加文件描述符限制
ulimit -n 65535

# 优化TCP参数
echo "net.core.somaxconn = 1024" >> /etc/sysctl.conf
echo "net.ipv4.tcp_tw_reuse = 1" >> /etc/sysctl.conf
echo "net.ipv4.tcp_tw_recycle = 0" >> /etc/sysctl.conf
sysctl -p
```

## 8. Tomcat集群

### 8.1 集群架构

```
                    ┌─────────┐
                    │  Nginx  │
                    │ (负载均衡)│
                    └────┬────┘
                         │
            ┌────────────┼────────────┐
            │            │            │
            ▼            ▼            ▼
      ┌─────────┐  ┌─────────┐  ┌─────────┐
      │Tomcat 1 │  │Tomcat 2 │  │Tomcat 3 │
      └─────────┘  └─────────┘  └─────────┘
            │            │            │
            └────────────┼────────────┘
                         │
                    ┌────▼────┐
                    │Redis集群│
                    │(Session)│
                    └─────────┘
```

### 8.2 Session共享配置

```xml
<Cluster className="org.apache.catalina.ha.tcp.SimpleTcpCluster">
    <Manager className="org.apache.catalina.ha.session.DeltaManager"
            expireSessionsOnShutdown="false"
            notifyListenersOnReplication="true"/>
    
    <Channel className="org.apache.catalina.tribes.group.GroupChannel">
        <Membership className="org.apache.catalina.tribes.membership.McastService"
                    address="228.0.0.4"
                    port="45564"
                    frequency="500"
                    dropTime="3000"/>
        
        <Receiver className="org.apache.catalina.tribes.transport.nio.NioReceiver"
                  address="auto"
                  port="4000"
                  autoBind="100"
                  selectorTimeout="5000"
                  maxThreads="6"/>
        
        <Sender className="org.apache.catalina.tribes.transport.ReplicationTransmitter">
            <Transport className="org.apache.catalina.tribes.transport.nio.PooledParallelSender"/>
        </Sender>
        
        <Interceptor className="org.apache.catalina.tribes.group.interceptors.TcpFailureDetector"/>
        <Interceptor className="org.apache.catalina.tribes.group.interceptors.MessageDispatch15Interceptor"/>
    </Channel>
    
    <Valve className="org.apache.catalina.ha.tcp.ReplicationValve"
           filter=""/>
    
    <Deployer className="org.apache.catalina.ha.deploy.FarmWarDeployer"
              tempDir="/tmp/war-temp/"
              deployDir="/tmp/war-deploy/"
              watchDir="/tmp/war-listen/"
              watchEnabled="false"/>
    
    <ClusterListener className="org.apache.catalina.ha.session.ClusterSessionListener"/>
</Cluster>
```

## 9. 总结

Tomcat的整体架构设计体现了以下核心思想：

1. **分层设计**：Server、Service、Connector、Container分层清晰
2. **模块化**：各组件职责明确，耦合度低
3. **可扩展性**：支持多种Connector实现和协议
4. **高性能**：支持NIO、NIO2、APR等多种I/O模型
5. **灵活性**：配置灵活，支持多种部署方式

Tomcat作为Java Web应用的主流容器，其优秀的架构设计为高性能、高可用的Web应用提供了坚实的基础。深入理解Tomcat的架构设计，对于开发和运维Java Web应用具有重要意义。