# 如何控制滚动更新过程？

## 概述

Kubernetes 的滚动更新是一种零停机部署策略，通过逐步替换旧版本的 Pod 为新版本的 Pod，实现应用的平滑升级。滚动更新可以确保应用在更新过程中始终保持可用。

## 滚动更新的概念

### 1. 什么是滚动更新

滚动更新是一种逐步替换旧版本 Pod 为新版本 Pod 的更新策略。

**特点**：
- 零停机时间
- 逐步替换
- 可控的更新速度
- 支持回滚

**示例**：
```
初始状态：3 个 Pod（v1）
  ↓
滚动更新：创建 1 个新 Pod（v2），删除 1 个旧 Pod（v1）
  ↓
中间状态：2 个 Pod（v1）+ 1 个 Pod（v2）
  ↓
滚动更新：创建 1 个新 Pod（v2），删除 1 个旧 Pod（v1）
  ↓
中间状态：1 个 Pod（v1）+ 2 个 Pod（v2）
  ↓
滚动更新：创建 1 个新 Pod（v2），删除 1 个旧 Pod（v1）
  ↓
最终状态：3 个 Pod（v2）
```

### 2. 滚动更新的优势

- **零停机**：应用始终保持可用
- **平滑升级**：逐步替换，减少风险
- **快速回滚**：如果新版本有问题，可以快速回滚
- **可控速度**：可以控制更新的速度

## Deployment 的滚动更新

### 1. 更新 Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
spec:
  replicas: 3
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
        ports:
        - containerPort: 80
```

**更新镜像**：
```bash
# 使用 kubectl set image 命令
kubectl set image deployment/nginx-deployment nginx=nginx:1.15.0

# 使用 kubectl apply 命令
kubectl apply -f deployment.yaml
```

### 2. 查看更新状态

```bash
# 查看更新状态
kubectl rollout status deployment/nginx-deployment

# 查看 Deployment 状态
kubectl get deployments

# 查看 ReplicaSet 状态
kubectl get replicasets

# 查看 Pod 状态
kubectl get pods
```

### 3. 查看更新历史

```bash
# 查看更新历史
kubectl rollout history deployment/nginx-deployment

# 查看特定版本的详细信息
kubectl rollout history deployment/nginx-deployment --revision=2
```

## 滚动更新的配置参数

### 1. maxSurge

**定义**：滚动更新过程中，可以超出期望副本数的最大 Pod 数量。

**类型**：
- **整数**：具体的 Pod 数量
- **百分比**：期望副本数的百分比

**示例**：
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
spec:
  replicas: 10
  strategy:
    rollingUpdate:
      maxSurge: 2  # 最多可以超出 2 个 Pod
    type: RollingUpdate
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

**使用百分比**：
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
spec:
  replicas: 10
  strategy:
    rollingUpdate:
      maxSurge: 25%  # 最多可以超出 25% 的 Pod
    type: RollingUpdate
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

### 2. maxUnavailable

**定义**：滚动更新过程中，最多可以有多少个 Pod 不可用。

**类型**：
- **整数**：具体的 Pod 数量
- **百分比**：期望副本数的百分比

**示例**：
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
spec:
  replicas: 10
  strategy:
    rollingUpdate:
      maxUnavailable: 2  # 最多可以有 2 个 Pod 不可用
    type: RollingUpdate
  selector:
    matchLabels:
      app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.14.2
```

**使用百分比**：
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
spec:
  replicas: 10
  strategy:
    rollingUpdate:
      maxUnavailable: 25%  # 最多可以有 25% 的 Pod 不可用
    type: RollingUpdate
  selector:
    matchLabels:
      app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.14.2
```

### 3. 同时配置 maxSurge 和 maxUnavailable

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
spec:
  replicas: 10
  strategy:
    rollingUpdate:
      maxSurge: 1  # 最多可以超出 1 个 Pod
      maxUnavailable: 1  # 最多可以有 1 个 Pod 不可用
    type: RollingUpdate
  selector:
    matchLabels:
      app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.14.2
```

**更新过程**：
```
初始状态：10 个 Pod（v1）
  ↓
滚动更新：创建 1 个新 Pod（v2），删除 1 个旧 Pod（v1）
  ↓
中间状态：9 个 Pod（v1）+ 1 个 Pod（v2）
  ↓
滚动更新：创建 1 个新 Pod（v2），删除 1 个旧 Pod（v1）
  ↓
中间状态：8 个 Pod（v1）+ 2 个 Pod（v2）
  ↓
...
  ↓
最终状态：10 个 Pod（v2）
```

## 滚动更新的策略

### 1. RollingUpdate（滚动更新）

**定义**：逐步替换旧版本 Pod 为新版本 Pod。

**特点**：
- 零停机时间
- 逐步替换
- 可控的更新速度

**示例**：
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
spec:
  replicas: 3
  strategy:
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
    type: RollingUpdate
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

### 2. Recreate（重新创建）

**定义**：先删除所有旧版本 Pod，再创建新版本 Pod。

**特点**：
- 有停机时间
- 简单快速
- 适用于有状态应用

**示例**：
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
spec:
  replicas: 3
  strategy:
    type: Recreate
  selector:
    matchLabels:
      app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.14.2
```

## 滚动更新的监控

### 1. 查看更新状态

```bash
# 查看更新状态
kubectl rollout status deployment/nginx-deployment

# 输出示例
Waiting for deployment "nginx-deployment" rollout to finish: 1 out of 3 new replicas have been updated...
Waiting for deployment "nginx-deployment" rollout to finish: 2 out of 3 new replicas have been updated...
deployment "nginx-deployment" successfully rolled out
```

### 2. 查看 Pod 状态

```bash
# 查看 Pod 状态
kubectl get pods

# 输出示例
NAME                                READY   STATUS    RESTARTS   AGE
nginx-deployment-6b7c7b5d9-abc123   1/1     Running   0          10s
nginx-deployment-6b7c7b5d9-def456   1/1     Running   0          20s
nginx-deployment-6b7c7b5d9-ghi789   1/1     Running   0          30s
```

### 3. 查看 ReplicaSet 状态

```bash
# 查看 ReplicaSet 状态
kubectl get replicasets

# 输出示例
NAME                          DESIRED   CURRENT   READY   AGE
nginx-deployment-6b7c7b5d9      3         3         3       1m
nginx-deployment-7c8c8b6d0      0         0         0       5m
```

### 4. 查看 Deployment 事件

```bash
# 查看 Deployment 事件
kubectl describe deployment nginx-deployment

# 输出示例
Events:
  Type    Reason             Age   From                   Message
  ----    ------             ----  ----                   -------
  Normal  ScalingReplicaSet  5m    deployment-controller  Scaled up replica set nginx-deployment-7c8c8b6d0 to 3
  Normal  ScalingReplicaSet  1m    deployment-controller  Scaled up replica set nginx-deployment-6b7c7b5d9 to 1
  Normal  ScalingReplicaSet  50s   deployment-controller  Scaled down replica set nginx-deployment-7c8c8b6d0 to 2
  Normal  ScalingReplicaSet  40s   deployment-controller  Scaled up replica set nginx-deployment-6b7c7b5d9 to 2
  Normal  ScalingReplicaSet  30s   deployment-controller  Scaled down replica set nginx-deployment-7c8c8b6d0 to 1
  Normal  ScalingReplicaSet  20s   deployment-controller  Scaled up replica set nginx-deployment-6b7c7b5d9 to 3
  Normal  ScalingReplicaSet  10s   deployment-controller  Scaled down replica set nginx-deployment-7c8c8b6d0 to 0
```

## 滚动更新的回滚

### 1. 回滚到上一个版本

```bash
# 回滚到上一个版本
kubectl rollout undo deployment/nginx-deployment

# 查看回滚状态
kubectl rollout status deployment/nginx-deployment
```

### 2. 回滚到指定版本

```bash
# 查看更新历史
kubectl rollout history deployment/nginx-deployment

# 输出示例
REVISION  CHANGE-CAUSE
1         <none>
2         kubectl set image deployment/nginx-deployment nginx=nginx:1.15.0 --record=true

# 回滚到指定版本
kubectl rollout undo deployment/nginx-deployment --to-revision=1
```

### 3. 暂停和恢复滚动更新

```bash
# 暂停滚动更新
kubectl rollout pause deployment/nginx-deployment

# 恢复滚动更新
kubectl rollout resume deployment/nginx-deployment
```

## 滚动更新的最佳实践

### 1. 合理配置 maxSurge 和 maxUnavailable

**建议**：
- 生产环境：`maxSurge: 1`，`maxUnavailable: 0`
- 开发环境：`maxSurge: 25%`，`maxUnavailable: 25%`
- 高可用环境：`maxSurge: 0`，`maxUnavailable: 1`

**示例**：
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
spec:
  replicas: 10
  strategy:
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
    type: RollingUpdate
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

### 2. 使用就绪探针

**建议**：配置就绪探针，确保新 Pod 准备好接收流量后再删除旧 Pod。

**示例**：
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
spec:
  replicas: 3
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
        ports:
        - containerPort: 80
        readinessProbe:
          httpGet:
            path: /
            port: 80
          initialDelaySeconds: 10
          periodSeconds: 5
          successThreshold: 1
          failureThreshold: 3
```

### 3. 记录更新原因

**建议**：使用 `--record` 参数记录更新原因，方便后续排查。

**示例**：
```bash
# 更新镜像并记录原因
kubectl set image deployment/nginx-deployment nginx=nginx:1.15.0 --record=true

# 查看更新历史
kubectl rollout history deployment/nginx-deployment
```

### 4. 逐步更新

**建议**：对于大规模应用，可以分批次更新，减少风险。

**示例**：
```bash
# 第一批更新 30%
kubectl scale deployment/nginx-deployment --replicas=3
kubectl set image deployment/nginx-deployment nginx=nginx:1.15.0

# 等待第一批更新完成
kubectl rollout status deployment/nginx-deployment

# 第二批更新 60%
kubectl scale deployment/nginx-deployment --replicas=6
kubectl set image deployment/nginx-deployment nginx=nginx:1.15.0

# 等待第二批更新完成
kubectl rollout status deployment/nginx-deployment

# 第三批更新 100%
kubectl scale deployment/nginx-deployment --replicas=10
kubectl set image deployment/nginx-deployment nginx=nginx:1.15.0

# 等待第三批更新完成
kubectl rollout status deployment/nginx-deployment
```

## 滚动更新的常见问题

### 1. 更新卡住

**原因**：
- 新 Pod 无法启动
- 就绪探针失败
- 资源不足

**解决方案**：
- 检查 Pod 状态
- 检查就绪探针配置
- 检查资源配额

**示例**：
```bash
# 检查 Pod 状态
kubectl get pods

# 检查 Pod 详情
kubectl describe pod <pod-name>

# 检查 Pod 日志
kubectl logs <pod-name>
```

### 2. 更新失败

**原因**：
- 镜像拉取失败
- 配置错误
- 资源不足

**解决方案**：
- 检查镜像名称和标签
- 检查配置文件
- 检查资源配额

**示例**：
```bash
# 检查镜像
docker pull nginx:1.15.0

# 检查配置文件
kubectl describe deployment nginx-deployment

# 检查资源配额
kubectl describe quota
```

### 3. 回滚失败

**原因**：
- 历史版本被删除
- 配置不兼容
- 资源不足

**解决方案**：
- 检查更新历史
- 检查配置兼容性
- 检查资源配额

**示例**：
```bash
# 检查更新历史
kubectl rollout history deployment/nginx-deployment

# 检查特定版本
kubectl rollout history deployment/nginx-deployment --revision=1

# 检查资源配额
kubectl describe quota
```

## 总结

Kubernetes 的滚动更新是一种零停机部署策略，通过逐步替换旧版本的 Pod 为新版本的 Pod，实现应用的平滑升级。

**滚动更新的配置参数**：
- **maxSurge**：滚动更新过程中，可以超出期望副本数的最大 Pod 数量
- **maxUnavailable**：滚动更新过程中，最多可以有多少个 Pod 不可用

**滚动更新的策略**：
- **RollingUpdate**：逐步替换旧版本 Pod 为新版本 Pod（默认）
- **Recreate**：先删除所有旧版本 Pod，再创建新版本 Pod

**滚动更新的监控**：
- 查看更新状态：`kubectl rollout status`
- 查看 Pod 状态：`kubectl get pods`
- 查看 ReplicaSet 状态：`kubectl get replicasets`
- 查看 Deployment 事件：`kubectl describe deployment`

**滚动更新的回滚**：
- 回滚到上一个版本：`kubectl rollout undo`
- 回滚到指定版本：`kubectl rollout undo --to-revision=2`
- 暂停和恢复滚动更新：`kubectl rollout pause/resume`

**滚动更新的最佳实践**：
- 合理配置 maxSurge 和 maxUnavailable
- 使用就绪探针
- 记录更新原因
- 逐步更新

通过合理配置滚动更新，可以实现应用的平滑升级，确保应用在更新过程中始终保持可用。