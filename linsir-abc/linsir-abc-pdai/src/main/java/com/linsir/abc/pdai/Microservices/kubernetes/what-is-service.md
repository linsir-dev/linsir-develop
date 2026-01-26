# K8s的Service是什么？

## 概述

Service 是 Kubernetes 中的一种资源对象，用于定义一组 Pod 的访问策略。Service 为 Pod 提供稳定的网络访问入口，解决了 Pod IP 不稳定的问题。Service 通过标签选择器选择 Pod，并将流量负载均衡到这些 Pod 上。

## Service 的作用

### 1. 稳定的网络访问

**定义**：Service 为 Pod 提供稳定的网络访问入口，解决 Pod IP 不稳定的问题。

**问题**：
- Pod 的 IP 地址在重启后会改变
- Pod 的 IP 地址在调度到其他 Node 后会改变
- 无法直接通过 Pod IP 访问 Pod

**解决方案**：
- Service 提供稳定的虚拟 IP（ClusterIP）
- Service 通过标签选择器选择 Pod
- Service 将流量负载均衡到 Pod 上

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
```

### 2. 服务发现

**定义**：Service 实现服务发现，使应用能够通过服务名称访问其他服务。

**特点**：
- 通过 DNS 解析服务名称
- 自动更新服务端点
- 支持跨 Namespace 访问

**示例**：
```bash
# 在 Pod 中访问 Service
curl http://nginx-service

# 跨 Namespace 访问 Service
curl http://nginx-service.default.svc.cluster.local
```

### 3. 负载均衡

**定义**：Service 将流量负载均衡到后端的多个 Pod 上。

**负载均衡策略**：
- 默认：随机选择
- 会话保持：基于客户端 IP
- 自定义：通过注解配置

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
  sessionAffinity: ClientIP  # 会话保持
  sessionAffinityConfig:
    clientIP:
      timeoutSeconds: 10800  # 会话保持时间
```

## Service 的类型

### 1. ClusterIP

**定义**：ClusterIP 类型的 Service 只能在集群内部访问，默认类型。

**特点**：
- 只能在集群内部访问
- 分配集群内部的虚拟 IP
- 适用于集群内部服务通信

**示例**：
```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
spec:
  type: ClusterIP  # 默认类型
  selector:
    app: nginx
  ports:
  - protocol: TCP
    port: 80
    targetPort: 80
```

**访问方式**：
```bash
# 在 Pod 中访问 Service
curl http://nginx-service

# 通过 DNS 访问 Service
curl http://nginx-service.default.svc.cluster.local
```

### 2. NodePort

**定义**：NodePort 类型的 Service 可以通过 Node 的 IP 和端口访问。

**特点**：
- 在每个 Node 上开放一个端口
- 可以通过 Node IP 和端口访问
- 适用于集群外部访问

**示例**：
```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
spec:
  type: NodePort  # NodePort 类型
  selector:
    app: nginx
  ports:
  - protocol: TCP
    port: 80
    targetPort: 80
    nodePort: 30080  # NodePort 端口范围：30000-32767
```

**访问方式**：
```bash
# 通过 Node IP 和端口访问
curl http://<node-ip>:30080

# 通过 Service 的 ClusterIP 访问
curl http://nginx-service
```

### 3. LoadBalancer

**定义**：LoadBalancer 类型的 Service 通过云厂商的负载均衡器暴露服务。

**特点**：
- 使用云厂商的负载均衡器
- 自动配置负载均衡器
- 适用于生产环境

**示例**：
```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
spec:
  type: LoadBalancer  # LoadBalancer 类型
  selector:
    app: nginx
  ports:
  - protocol: TCP
    port: 80
    targetPort: 80
```

**访问方式**：
```bash
# 通过负载均衡器的 IP 访问
curl http://<loadbalancer-ip>

# 查看负载均衡器的 IP
kubectl get svc nginx-service
```

### 4. ExternalName

**定义**：ExternalName 类型的 Service 将服务映射到外部 DNS 名称。

**特点**：
- 将服务映射到外部 DNS 名称
- 不创建代理
- 适用于外部服务

**示例**：
```yaml
apiVersion: v1
kind: Service
metadata:
  name: external-service
spec:
  type: ExternalName  # ExternalName 类型
  externalName: example.com
```

**访问方式**：
```bash
# 通过 Service 名称访问外部服务
curl http://external-service
```

## Service 的配置

### 1. 选择器

**定义**：Service 通过标签选择器选择 Pod。

**示例**：
```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
spec:
  selector:
    app: nginx  # 选择标签为 app=nginx 的 Pod
  ports:
  - protocol: TCP
    port: 80
    targetPort: 80
```

### 2. 端口配置

**定义**：Service 配置端口映射，将外部流量转发到 Pod 的端口。

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
  - name: http
    protocol: TCP
    port: 80  # Service 端口
    targetPort: 8080  # Pod 端口
  - name: https
    protocol: TCP
    port: 443
    targetPort: 8443
```

### 3. 会话保持

**定义**：Service 可以配置会话保持，将同一客户端的流量路由到同一个 Pod。

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
  sessionAffinity: ClientIP  # 会话保持
  sessionAffinityConfig:
    clientIP:
      timeoutSeconds: 10800  # 会话保持时间
```

### 4. 外部流量策略

**定义**：Service 可以配置外部流量策略，控制外部流量的路由方式。

**示例**：
```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
spec:
  type: NodePort
  selector:
    app: nginx
  ports:
  - protocol: TCP
    port: 80
    targetPort: 80
    nodePort: 30080
  externalTrafficPolicy: Local  # 外部流量策略
```

**外部流量策略**：
- **Cluster**：外部流量可以路由到任何 Node 上的 Pod（默认）
- **Local**：外部流量只能路由到接收流量的 Node 上的 Pod

## Service 的管理

### 1. 查看 Service

```bash
# 查看 Service
kubectl get svc

# 输出示例
NAME            TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)        AGE
nginx-service   ClusterIP   10.96.0.1       <none>        80/TCP         10m
```

### 2. 查看 Service 详情

```bash
# 查看 Service 详情
kubectl describe svc nginx-service

# 输出示例
Name:              nginx-service
Namespace:         default
Labels:            <none>
Annotations:       <none>
Selector:          app=nginx
Type:              ClusterIP
IP Family Policy:   SingleStack
IP Families:       IPv4
IP:                10.96.0.1
IPs:               10.96.0.1
Port:              <unset>  80/TCP
TargetPort:        80/TCP
Endpoints:         10.244.1.5:80,10.244.2.5:80,10.244.3.5:80
Session Affinity:  None
Events:            <none>
```

### 3. 查看 Service 端点

```bash
# 查看 Service 端点
kubectl get endpoints nginx-service

# 输出示例
NAME            ENDPOINTS                                      AGE
nginx-service   10.244.1.5:80,10.244.2.5:80,10.244.3.5:80   10m
```

### 4. 删除 Service

```bash
# 删除 Service
kubectl delete svc nginx-service
```

## Service 的 DNS

### 1. Service 的 DNS 名称

**定义**：Service 的 DNS 名称格式为 `<service-name>.<namespace>.svc.cluster.local`。

**示例**：
```bash
# 在 Pod 中访问 Service
curl http://nginx-service

# 通过完整 DNS 名称访问 Service
curl http://nginx-service.default.svc.cluster.local

# 跨 Namespace 访问 Service
curl http://nginx-service.other-namespace.svc.cluster.local
```

### 2. Headless Service

**定义**：Headless Service 不分配 ClusterIP，DNS 返回所有 Pod 的 IP。

**示例**：
```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
spec:
  clusterIP: None  # Headless Service
  selector:
    app: nginx
  ports:
  - protocol: TCP
    port: 80
    targetPort: 80
```

**访问方式**：
```bash
# 查询 DNS，返回所有 Pod 的 IP
nslookup nginx-service.default.svc.cluster.local

# 输出示例
Server:    10.96.0.10
Address 1: 10.96.0.10 kube-dns.kube-system.svc.cluster.local

Name:      nginx-service.default.svc.cluster.local
Address 1: 10.244.1.5
Address 2: 10.244.2.5
Address 3: 10.244.3.5
```

## Service 的最佳实践

### 1. 使用标签选择器

**建议**：使用标签选择器选择 Pod，避免硬编码 Pod IP。

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
```

### 2. 配置会话保持

**建议**：对于有状态的应用，配置会话保持。

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
  sessionAffinity: ClientIP
  sessionAffinityConfig:
    clientIP:
      timeoutSeconds: 10800
```

### 3. 使用 Headless Service

**建议**：对于需要直接访问 Pod 的应用，使用 Headless Service。

**示例**：
```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
spec:
  clusterIP: None
  selector:
    app: nginx
  ports:
  - protocol: TCP
    port: 80
    targetPort: 80
```

### 4. 配置外部流量策略

**建议**：对于需要保留客户端 IP 的应用，配置外部流量策略为 Local。

**示例**：
```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
spec:
  type: NodePort
  selector:
    app: nginx
  ports:
  - protocol: TCP
    port: 80
    targetPort: 80
    nodePort: 30080
  externalTrafficPolicy: Local
```

## Service 的常见问题

### 1. Service 无法访问

**原因**：
- Service 端点为空
- Pod 未正常运行
- 网络配置错误

**解决方案**：
- 检查 Service 端点
- 检查 Pod 状态
- 检查网络配置

**示例**：
```bash
# 检查 Service 端点
kubectl get endpoints nginx-service

# 检查 Pod 状态
kubectl get pods -l app=nginx

# 检查 Service 详情
kubectl describe svc nginx-service
```

### 2. Service 端点为空

**原因**：
- 标签选择器配置错误
- Pod 标签不匹配
- Pod 未正常运行

**解决方案**：
- 检查标签选择器配置
- 检查 Pod 标签
- 检查 Pod 状态

**示例**：
```bash
# 检查 Service 标签选择器
kubectl get svc nginx-service -o yaml

# 检查 Pod 标签
kubectl get pods -l app=nginx --show-labels

# 检查 Pod 状态
kubectl get pods -l app=nginx
```

### 3. Service 负载均衡不均匀

**原因**：
- 默认负载均衡策略为随机
- 会话保持配置错误
- Pod 资源不均衡

**解决方案**：
- 配置会话保持
- 检查 Pod 资源
- 使用自定义负载均衡策略

**示例**：
```bash
# 检查 Service 配置
kubectl describe svc nginx-service

# 检查 Pod 资源
kubectl top pods -l app=nginx

# 检查 Service 端点
kubectl get endpoints nginx-service
```

## 总结

Service 是 Kubernetes 中的一种资源对象，用于定义一组 Pod 的访问策略。

**Service 的作用**：
- **稳定的网络访问**：为 Pod 提供稳定的网络访问入口
- **服务发现**：实现服务发现，使应用能够通过服务名称访问其他服务
- **负载均衡**：将流量负载均衡到后端的多个 Pod 上

**Service 的类型**：
- **ClusterIP**：只能在集群内部访问，默认类型
- **NodePort**：可以通过 Node 的 IP 和端口访问
- **LoadBalancer**：通过云厂商的负载均衡器暴露服务
- **ExternalName**：将服务映射到外部 DNS 名称

**Service 的配置**：
- **选择器**：通过标签选择器选择 Pod
- **端口配置**：配置端口映射
- **会话保持**：配置会话保持
- **外部流量策略**：配置外部流量策略

**Service 的 DNS**：
- **Service 的 DNS 名称**：`<service-name>.<namespace>.svc.cluster.local`
- **Headless Service**：不分配 ClusterIP，DNS 返回所有 Pod 的 IP

**Service 的最佳实践**：
- 使用标签选择器
- 配置会话保持
- 使用 Headless Service
- 配置外部流量策略

**Service 的常见问题**：
- Service 无法访问：检查 Service 端点、Pod 状态、网络配置
- Service 端点为空：检查标签选择器配置、Pod 标签、Pod 状态
- Service 负载均衡不均匀：配置会话保持、检查 Pod 资源、使用自定义负载均衡策略

通过合理使用 Service，可以实现 Pod 的稳定访问、服务发现和负载均衡。