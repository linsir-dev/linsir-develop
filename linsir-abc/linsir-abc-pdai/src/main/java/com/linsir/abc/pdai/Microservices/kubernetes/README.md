# Kubernetes 技术文档

## 文档列表

### 基础概念
- [什么是Kubernetes？](what-is-kubernetes.md)
- [Kubernetes与Docker有什么关系？](kubernetes-vs-docker.md)
- [Kubernetes的整体架构？](kubernetes-architecture.md)
- [Kubernetes中有哪些核心概念？](kubernetes-core-concepts.md)

### 工具和组件
- [什么是Heapster？](what-is-heapster.md)
- [什么是Minikube？](what-is-minikube.md)
- [什么是Kubectl？](what-is-kubectl.md)
- [kube-apiserver和kube-scheduler的作用是什么？](kube-apiserver-and-kube-scheduler.md)

### Pod 管理
- [kubenetes针对pod资源对象的健康监测机制？](pod-health-check.md)
- [K8s中镜像的下载策略是什么？](image-pull-policy.md)
- [image的状态有哪些？](image-states.md)
- [pod的重启策略是什么？](pod-restart-policy.md)
- [描述一下pod的生命周期有哪些状态？](pod-lifecycle.md)
- [创建一个pod的流程是什么？](pod-creation-process.md)
- [删除一个Pod会发生什么事情？](pod-deletion-process.md)

### 工作负载
- [DaemonSet资源对象的特性？](daemonset-features.md)
- [说说你对Job这种资源对象的了解？](job-understanding.md)
- [Replica Set 和 Replication Controller 之间有什么区别？](replicaset-vs-replicationcontroller.md)

### 部署和更新
- [如何控制滚动更新过程？](rolling-update.md)

### 网络
- [K8s的Service是什么？](what-is-service.md)
- [k8s是怎么进行服务注册的？](service-registration.md)
- [k8s集群外流量怎么访问Pod？](external-traffic-access.md)

### 存储
- [k8s数据持久化的方式有哪些？](data-persistence.md)

## 学习路径

### 初级
1. [什么是Kubernetes？](what-is-kubernetes.md) - 了解 Kubernetes 的基本概念
2. [Kubernetes与Docker有什么关系？](kubernetes-vs-docker.md) - 理解 Kubernetes 与 Docker 的关系
3. [什么是Minikube？](what-is-minikube.md) - 学习如何使用 Minikube 进行本地开发
4. [什么是Kubectl？](what-is-kubectl.md) - 掌握 Kubectl 的基本用法

### 中级
1. [Kubernetes的整体架构？](kubernetes-architecture.md) - 理解 Kubernetes 的整体架构
2. [Kubernetes中有哪些核心概念？](kubernetes-core-concepts.md) - 掌握 Kubernetes 的核心概念
3. [kubenetes针对pod资源对象的健康监测机制？](pod-health-check.md) - 学习 Pod 的健康监测机制
4. [K8s的Service是什么？](what-is-service.md) - 理解 Service 的概念和作用

### 高级
1. [kube-apiserver和kube-scheduler的作用是什么？](kube-apiserver-and-kube-scheduler.md) - 深入理解 Kubernetes 的核心组件
2. [如何控制滚动更新过程？](rolling-update.md) - 掌握滚动更新的配置和管理
3. [k8s集群外流量怎么访问Pod？](external-traffic-access.md) - 学习集群外流量访问的各种方式
4. [k8s数据持久化的方式有哪些？](data-persistence.md) - 掌握数据持久化的各种方式

## 核心概念

### Pod
- Pod 是 Kubernetes 中最小的可部署单元
- Pod 包含一个或多个容器
- Pod 的生命周期包括 Pending、Running、Succeeded、Failed、Unknown 等状态
- Pod 支持健康检查、资源限制、优雅终止等特性

### Service
- Service 为 Pod 提供稳定的网络访问入口
- Service 支持多种类型：ClusterIP、NodePort、LoadBalancer、ExternalName
- Service 实现服务发现和负载均衡
- Service 通过标签选择器选择 Pod

### Deployment
- Deployment 管理 ReplicaSet
- Deployment 支持声明式的更新和回滚
- Deployment 支持滚动更新
- Deployment 推荐用于无状态应用

### ReplicaSet
- ReplicaSet 确保指定数量的 Pod 副本运行
- ReplicaSet 支持集合操作的标签选择器
- ReplicaSet 支持滚动更新的回滚
- ReplicaSet 是 ReplicationController 的升级版本

### DaemonSet
- DaemonSet 确保在每个 Node 上运行一个 Pod 副本
- DaemonSet 适用于日志收集、监控、网络插件等场景
- DaemonSet 支持滚动更新
- DaemonSet 支持节点选择器

### Job
- Job 用于运行一次性任务
- Job 支持任务重试
- Job 支持并行执行
- Job 适用于批处理、数据迁移等场景

## 常用命令

### Pod 管理
```bash
# 查看 Pod
kubectl get pods

# 查看 Pod 详情
kubectl describe pod <pod-name>

# 查看 Pod 日志
kubectl logs <pod-name>

# 进入 Pod
kubectl exec -it <pod-name> -- /bin/bash

# 删除 Pod
kubectl delete pod <pod-name>
```

### Service 管理
```bash
# 查看 Service
kubectl get svc

# 查看 Service 详情
kubectl describe svc <service-name>

# 删除 Service
kubectl delete svc <service-name>
```

### Deployment 管理
```bash
# 查看 Deployment
kubectl get deployments

# 查看 Deployment 详情
kubectl describe deployment <deployment-name>

# 更新 Deployment
kubectl set image deployment/<deployment-name> <container-name>=<image>:<tag>

# 查看更新状态
kubectl rollout status deployment/<deployment-name>

# 查看更新历史
kubectl rollout history deployment/<deployment-name>

# 回滚到上一个版本
kubectl rollout undo deployment/<deployment-name>
```

### 存储管理
```bash
# 查看 PersistentVolume
kubectl get pv

# 查看 PersistentVolumeClaim
kubectl get pvc

# 查看 StorageClass
kubectl get sc
```

## 最佳实践

### 1. 使用 Deployment 管理 Pod
推荐使用 Deployment 管理无状态应用，而不是直接使用 ReplicaSet 或 ReplicationController。

### 2. 配置健康检查
为 Pod 配置存活探针和就绪探针，确保 Pod 的健康状态。

### 3. 配置资源限制
为 Pod 配置资源限制，避免 Pod 占用过多资源。

### 4. 使用标签和选择器
使用标签和选择器，方便管理 Pod 和 Service。

### 5. 配置优雅终止
为 Pod 配置优雅终止，确保 Pod 能够优雅地关闭。

### 6. 使用 ConfigMap 和 Secret
使用 ConfigMap 管理配置数据，使用 Secret 管理敏感数据。

### 7. 使用 PersistentVolumeClaim
使用 PersistentVolumeClaim 请求数据持久化资源，而不是直接使用 PersistentVolume。

### 8. 使用 StorageClass
使用 StorageClass 实现动态卷供应，简化存储管理。

### 9. 监控和日志
监控和日志，及时发现和解决问题。

### 10. 使用 Ingress
使用 Ingress 管理集群外流量访问，提供更好的性能和安全性。

## 常见问题

### 1. Pod 无法启动
检查 Pod 状态、Pod 详情、Pod 日志，排查问题。

### 2. Service 无法访问
检查 Service 端点、Pod 状态、网络配置，排查问题。

### 3. 存储无法挂载
检查 PersistentVolumeClaim、PersistentVolume、存储配置，排查问题。

### 4. 集群外流量无法访问
检查 Service 类型、网络配置、防火墙规则，排查问题。

## 参考资源

- [Kubernetes 官方文档](https://kubernetes.io/docs/)
- [Kubernetes GitHub](https://github.com/kubernetes/kubernetes)
- [Kubectl 命令参考](https://kubernetes.io/docs/reference/generated/kubectl/kubectl-commands)
- [Kubernetes API 参考](https://kubernetes.io/docs/reference/kubernetes-api/)