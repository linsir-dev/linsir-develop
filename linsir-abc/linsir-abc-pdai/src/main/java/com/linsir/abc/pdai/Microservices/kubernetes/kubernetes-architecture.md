# Kubernetes的整体架构？

## 概述

Kubernetes 采用主从（Master-Worker）架构，集群由一个或多个 Master 节点和多个 Worker 节点组成。Master 节点负责集群的管理和控制，Worker 节点负责运行容器化的应用。

## 架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        Master 节点                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │ kube-apiserver│  │kube-scheduler│  │kube-controller│    │
│  │              │  │              │  │    -manager   │    │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘    │
│         │                 │                 │              │
│         └─────────────────┴─────────────────┘              │
│                           │                                 │
│                    ┌──────▼──────┐                          │
│                    │  etcd 集群  │                          │
│                    │  (数据存储) │                          │
│                    └─────────────┘                          │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           │ API 调用
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                       Worker 节点                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │   kubelet    │  │kube-proxy    │  │  容器运行时   │    │
│  │              │  │              │  │  (Docker等)   │    │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘    │
│         │                 │                 │              │
│         └─────────────────┴─────────────────┘              │
│                           │                                 │
│                    ┌──────▼──────┐                          │
│                    │    Pod      │                          │
│                    │  (容器组)   │                          │
│                    └─────────────┘                          │
└─────────────────────────────────────────────────────────────┘
```

## Master 节点组件

### 1. kube-apiserver

**作用**：Kubernetes API 服务器，是集群的统一入口。

**功能**：
- 提供 REST API 接口
- 处理认证、授权、准入控制
- 验证和配置 API 对象的数据
- 作为集群的唯一入口，所有组件都通过 API Server 交互

**特点**：
- 无状态，可水平扩展
- 支持多种认证方式：Token、证书、OIDC 等
- 支持多种授权方式：RBAC、ABAC、Node 等

### 2. etcd

**作用**：Kubernetes 的键值数据库，存储集群的所有状态数据。

**功能**：
- 存储集群配置信息
- 存储所有资源对象的状态
- 提供数据一致性保证
- 支持集群部署，保证高可用

**特点**：
- 基于 Raft 协议，保证数据一致性
- 支持事务操作
- 支持监听机制（Watch）
- 支持数据快照和恢复

**存储的数据示例**：
```yaml
# Pod 对象
/api/v1/namespaces/default/pods/nginx-pod

# Service 对象
/api/v1/namespaces/default/services/nginx-service

# Deployment 对象
/apis/apps/v1/namespaces/default/deployments/nginx-deployment
```

### 3. kube-scheduler

**作用**：负责调度 Pod 到合适的 Worker 节点上。

**功能**：
- 监听未调度的 Pod
- 根据调度策略选择合适的节点
- 考虑资源需求、硬件约束、策略约束等
- 支持自定义调度器

**调度流程**：
```
1. 监听未调度的 Pod
   ↓
2. 过滤不符合条件的节点（预选）
   ↓
3. 对符合条件的节点打分（优选）
   ↓
4. 选择得分最高的节点
   ↓
5. 将 Pod 绑定到节点
```

**调度策略示例**：
- 资源限制：CPU、内存、存储
- 节点选择器：nodeSelector、nodeAffinity
- 污点和容忍度：Taints and Tolerations
- Pod 亲和性和反亲和性：PodAffinity、PodAntiAffinity
- 自定义调度器：通过 schedulerName 指定

### 4. kube-controller-manager

**作用**：运行控制器进程，维护集群状态。

**功能**：
- 节点控制器（Node Controller）
- 副本控制器（Replication Controller）
- 端点控制器（Endpoints Controller）
- 服务账号和令牌控制器（ServiceAccount & Token Controller）
- 命名空间控制器（Namespace Controller）
- 资源配额控制器（ResourceQuota Controller）

**工作原理**：
```
1. 监听 API Server 中的资源变化
   ↓
2. 对比期望状态和实际状态
   ↓
3. 如果状态不一致，执行调整操作
   ↓
4. 将实际状态调整为期望状态
```

**示例：副本控制器**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
spec:
  replicas: 3  # 期望副本数
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.14.2
```

### 5. cloud-controller-manager

**作用**：云控制器管理器，用于对接云服务商。

**功能**：
- 节点控制器（Node Controller）
- 路由控制器（Route Controller）
- 服务控制器（Service Controller）
- 数据卷控制器（Volume Controller）

**支持的云服务商**：
- AWS
- Azure
- GCP
- 阿里云
- 腾讯云
- 华为云

## Worker 节点组件

### 1. kubelet

**作用**：运行在每个 Worker 节点上的代理，负责管理节点上的容器。

**功能**：
- 监听 API Server，获取 Pod 分配信息
- 与容器运行时交互，创建和管理容器
- 定期上报节点和 Pod 的状态
- 执行健康检查
- 挂载和卸载存储卷

**工作流程**：
```
1. 监听 API Server，获取分配到本节点的 Pod
   ↓
2. 下载 Pod 所需的镜像
   ↓
3. 创建并启动容器
   ↓
4. 监控容器状态
   ↓
5. 定期上报状态到 API Server
   ↓
6. 如果容器失败，根据重启策略重启容器
```

### 2. kube-proxy

**作用**：维护节点上的网络规则，实现 Service 的负载均衡。

**功能**：
- 监听 API Server，获取 Service 和 Endpoints 变化
- 在节点上配置 iptables 或 ipvs 规则
- 实现 Service 的负载均衡
- 支持 ClusterIP、NodePort、LoadBalancer 等类型

**工作模式**：
- **userspace 模式**：在用户空间转发流量（已废弃）
- **iptables 模式**：使用 iptables 规则转发流量
- **ipvs 模式**：使用 IPVS 转发流量（性能更好）

**示例：iptables 规则**
```bash
# Service 的 ClusterIP 规则
-A KUBE-SERVICES -d 10.96.0.1/32 -p tcp -m comment --comment "default/kubernetes:https cluster IP" -m tcp --dport 443 -j KUBE-SVC-NPX46M4PTMTKRN6Y

# Pod 的转发规则
-A KUBE-SVC-NPX46M4PTMTKRN6Y -m comment --comment "default/kubernetes:https" -m statistic --mode random --probability 0.33332999982 -j KUBE-SEP-XXXXX
-A KUBE-SVC-NPX46M4PTMTKRN6Y -m comment --comment "default/kubernetes:https" -m statistic --mode random --probability 0.50000000000 -j KUBE-SEP-YYYYY
-A KUBE-SVC-NPX46M4PTMTKRN6Y -m comment --comment "default/kubernetes:https" -j KUBE-SEP-ZZZZZ
```

### 3. 容器运行时（Container Runtime）

**作用**：负责运行容器。

**支持的运行时**：
- Docker
- containerd
- CRI-O
- rkt

**容器运行时接口（CRI）**：
```go
// CRI 接口定义
type RuntimeServiceServer interface {
    RunPodSandbox(context.Context, *RunPodSandboxRequest) (*RunPodSandboxResponse, error)
    StopPodSandbox(context.Context, *StopPodSandboxRequest) (*StopPodSandboxResponse, error)
    RemovePodSandbox(context.Context, *RemovePodSandboxRequest) (*RemovePodSandboxResponse, error)
    CreateContainer(context.Context, *CreateContainerRequest) (*CreateContainerResponse, error)
    StartContainer(context.Context, *StartContainerRequest) (*StartContainerResponse, error)
    StopContainer(context.Context, *StopContainerRequest) (*StopContainerResponse, error)
    RemoveContainer(context.Context, *RemoveContainerRequest) (*RemoveContainerResponse, error)
}
```

## 核心概念

### 1. Pod

**定义**：Kubernetes 中最小的部署单元，包含一个或多个容器。

**特点**：
- 共享网络和存储
- 临时性，可以被创建、删除、重建
- 通过 IP 地址访问

**示例**：
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
spec:
  containers:
  - name: nginx
    image: nginx:1.14.2
    ports:
    - containerPort: 80
```

### 2. Node

**定义**：Kubernetes 集群中的工作节点，可以是物理机或虚拟机。

**状态**：
- Ready：节点可用
- NotReady：节点不可用
- Unknown：节点状态未知

### 3. Namespace

**定义**：用于隔离集群资源的虚拟集群。

**作用**：
- 资源隔离
- 权限控制
- 资源配额

**示例**：
```bash
# 创建命名空间
kubectl create namespace dev

# 在指定命名空间中创建资源
kubectl apply -f deployment.yaml -n dev
```

### 4. Service

**定义**：定义一组 Pod 的访问策略。

**类型**：
- ClusterIP：集群内部访问
- NodePort：通过节点端口访问
- LoadBalancer：通过负载均衡器访问
- ExternalName：映射到外部 DNS 名称

**示例**：
```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
spec:
  selector:
    app: nginx
  ports:
  - protocol: TCP
    port: 80
    targetPort: 80
  type: LoadBalancer
```

## 组件交互流程

### 1. 创建 Pod 流程

```
1. 用户通过 kubectl 提交 Pod 定义
   ↓
2. kubectl 调用 kube-apiserver 的 REST API
   ↓
3. kube-apiserver 验证请求，将 Pod 定义存储到 etcd
   ↓
4. kube-scheduler 监听到未调度的 Pod
   ↓
5. kube-scheduler 选择合适的节点，将 Pod 绑定到节点
   ↓
6. kubelet 监听到分配到本节点的 Pod
   ↓
7. kubelet 调用容器运行时创建容器
   ↓
8. kubelet 定期上报 Pod 状态到 kube-apiserver
   ↓
9. kube-apiserver 将状态更新到 etcd
```

### 2. Service 流量转发流程

```
1. 客户端访问 Service 的 ClusterIP
   ↓
2. 流量到达节点上的 iptables/ipvs 规则
   ↓
3. kube-proxy 配置的规则将流量转发到后端 Pod
   ↓
4. 流量到达 Pod 的容器
   ↓
5. 容器处理请求并返回响应
```

## 高可用架构

### 1. Master 节点高可用

**方案**：
- 部署多个 Master 节点（通常 3 个或 5 个）
- 使用负载均衡器（如 HAProxy、Nginx）分发请求
- etcd 集群部署（奇数个节点）

**架构**：
```
                    ┌─────────────┐
                    │  负载均衡器  │
                    └──────┬──────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
   ┌────▼────┐       ┌────▼────┐       ┌────▼────┐
   │ Master1 │       │ Master2 │       │ Master3 │
   └────┬────┘       └────┬────┘       └────┬────┘
        │                 │                 │
        └─────────────────┴─────────────────┘
                           │
                    ┌──────▼──────┐
                    │  etcd 集群   │
                    └─────────────┘
```

### 2. Worker 节点高可用

**方案**：
- 部署多个 Worker 节点
- 使用反亲和性策略分散 Pod
- 使用多副本保证服务可用性

## 总结

Kubernetes 的整体架构采用主从架构，包含以下核心组件：

**Master 节点**：
- kube-apiserver：API 服务器，集群统一入口
- etcd：键值数据库，存储集群状态
- kube-scheduler：调度器，负责 Pod 调度
- kube-controller-manager：控制器管理器，维护集群状态
- cloud-controller-manager：云控制器管理器，对接云服务商

**Worker 节点**：
- kubelet：节点代理，管理容器
- kube-proxy：网络代理，实现负载均衡
- 容器运行时：运行容器（Docker、containerd 等）

**核心概念**：
- Pod：最小部署单元
- Node：工作节点
- Namespace：资源隔离
- Service：服务发现和负载均衡

Kubernetes 的架构设计使其具有高可用性、可扩展性和灵活性，能够满足各种规模的容器化应用部署需求。