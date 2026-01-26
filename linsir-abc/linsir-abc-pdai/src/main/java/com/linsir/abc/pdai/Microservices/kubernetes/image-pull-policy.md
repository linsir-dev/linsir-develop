# K8s中镜像的下载策略是什么？

## 概述

Kubernetes 中镜像的下载策略通过 `imagePullPolicy` 参数控制，决定了 kubelet 如何拉取容器镜像。`imagePullPolicy` 有三种策略：`Always`、`IfNotPresent` 和 `Never`。

## 镜像下载策略

### 1. Always

**定义**：总是拉取镜像，无论本地是否已有该镜像。

**适用场景**：
- 使用 `:latest` 标签的镜像
- 需要确保使用最新版本的镜像
- 镜像更新频繁

**示例**：
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
spec:
  containers:
  - name: nginx
    image: nginx:latest
    imagePullPolicy: Always
```

**工作流程**：
```
1. kubelet 检查 Pod 的镜像策略
   ↓
2. 发现 imagePullPolicy 为 Always
   ↓
3. kubelet 从镜像仓库拉取镜像
   ↓
4. kubelet 使用拉取的镜像创建容器
```

### 2. IfNotPresent

**定义**：如果本地不存在该镜像，则拉取镜像；如果本地已存在，则使用本地镜像。

**适用场景**：
- 使用固定标签的镜像（如 `:v1.14.2`）
- 镜像版本固定，不需要频繁更新
- 节省网络带宽和拉取时间

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
    imagePullPolicy: IfNotPresent
```

**工作流程**：
```
1. kubelet 检查 Pod 的镜像策略
   ↓
2. 发现 imagePullPolicy 为 IfNotPresent
   ↓
3. kubelet 检查本地是否存在该镜像
   ↓
4. 如果本地不存在，从镜像仓库拉取镜像
   ↓
5. 如果本地存在，使用本地镜像
   ↓
6. kubelet 使用镜像创建容器
```

### 3. Never

**定义**：从不拉取镜像，只使用本地镜像。如果本地不存在该镜像，则 Pod 启动失败。

**适用场景**：
- 使用私有镜像仓库
- 镜像已经预加载到节点上
- 离线环境

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
    imagePullPolicy: Never
```

**工作流程**：
```
1. kubelet 检查 Pod 的镜像策略
   ↓
2. 发现 imagePullPolicy 为 Never
   ↓
3. kubelet 检查本地是否存在该镜像
   ↓
4. 如果本地不存在，Pod 启动失败
   ↓
5. 如果本地存在，使用本地镜像
   ↓
6. kubelet 使用镜像创建容器
```

## 默认策略

### 1. 根据镜像标签自动选择

Kubernetes 根据镜像标签自动选择 `imagePullPolicy`：

- **`:latest` 标签**：默认使用 `Always`
- **其他标签**：默认使用 `IfNotPresent`
- **无标签**：默认使用 `Always`

**示例**：
```yaml
# 使用 :latest 标签，默认策略为 Always
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
spec:
  containers:
  - name: nginx
    image: nginx:latest  # 默认 imagePullPolicy: Always

# 使用固定标签，默认策略为 IfNotPresent
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
spec:
  containers:
  - name: nginx
    image: nginx:1.14.2  # 默认 imagePullPolicy: IfNotPresent
```

### 2. 显式指定策略

建议显式指定 `imagePullPolicy`，避免使用默认策略。

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
    imagePullPolicy: IfNotPresent  # 显式指定策略
```

## 镜像拉取失败处理

### 1. 镜像拉取失败

如果镜像拉取失败，kubelet 会重试拉取镜像。

**重试策略**：
- **重试次数**：默认为 3 次
- **重试间隔**：默认为 5 秒
- **超时时间**：默认为 2 分钟

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
    imagePullPolicy: Always
```

**查看镜像拉取失败**：
```bash
# 查看 Pod 状态
kubectl get pods

# 查看 Pod 详情
kubectl describe pod <pod-name>

# 查看 Pod 事件
kubectl get events --field-selector involvedObject.name=<pod-name>
```

### 2. 镜像拉取超时

如果镜像拉取超时，kubelet 会重试拉取镜像。

**超时配置**：
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
spec:
  containers:
  - name: nginx
    image: nginx:1.14.2
    imagePullPolicy: Always
  imagePullSecrets:
  - name: my-registry-secret
```

## 镜像拉取认证

### 1. Docker Registry Secret

如果使用私有镜像仓库，需要创建 Docker Registry Secret。

**创建 Secret**：
```bash
# 创建 Docker Registry Secret
kubectl create secret docker-registry my-registry-secret \
  --docker-server=registry.example.com \
  --docker-username=user \
  --docker-password=password \
  --docker-email=user@example.com
```

**使用 Secret**：
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
spec:
  containers:
  - name: nginx
    image: registry.example.com/nginx:1.14.2
    imagePullPolicy: Always
  imagePullSecrets:
  - name: my-registry-secret
```

### 2. ServiceAccount 关联 Secret

可以将 Secret 关联到 ServiceAccount，所有使用该 ServiceAccount 的 Pod 都可以使用该 Secret。

**创建 ServiceAccount**：
```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: my-service-account
imagePullSecrets:
- name: my-registry-secret
```

**使用 ServiceAccount**：
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
spec:
  serviceAccountName: my-service-account
  containers:
  - name: nginx
    image: registry.example.com/nginx:1.14.2
    imagePullPolicy: Always
```

## 镜像拉取优化

### 1. 使用镜像缓存

Kubernetes 支持镜像缓存，可以加速镜像拉取。

**配置镜像缓存**：
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
spec:
  containers:
  - name: nginx
    image: nginx:1.14.2
    imagePullPolicy: IfNotPresent
```

### 2. 使用本地镜像仓库

可以使用本地镜像仓库，减少网络传输。

**部署本地镜像仓库**：
```bash
# 部署 Harbor
helm install harbor harbor/harbor

# 部署 Docker Registry
docker run -d -p 5000:5000 --restart=always --name registry registry:2
```

**使用本地镜像仓库**：
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
spec:
  containers:
  - name: nginx
    image: localhost:5000/nginx:1.14.2
    imagePullPolicy: Always
```

### 3. 预加载镜像

可以在节点上预加载镜像，避免运行时拉取。

**预加载镜像**：
```bash
# 在节点上拉取镜像
docker pull nginx:1.14.2

# 或使用 kubectl 预加载镜像
kubectl create -f nginx-pod.yaml
```

## 镜像拉取策略的最佳实践

### 1. 生产环境

**建议**：
- 使用固定标签（如 `:v1.14.2`）
- 使用 `IfNotPresent` 策略
- 显式指定 `imagePullPolicy`
- 避免使用 `:latest` 标签

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
    imagePullPolicy: IfNotPresent
```

### 2. 开发环境

**建议**：
- 使用 `:latest` 标签
- 使用 `Always` 策略
- 确保使用最新版本的镜像

**示例**：
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
spec:
  containers:
  - name: nginx
    image: nginx:latest
    imagePullPolicy: Always
```

### 3. 离线环境

**建议**：
- 使用 `Never` 策略
- 预加载镜像到节点
- 使用本地镜像仓库

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
    imagePullPolicy: Never
```

## 镜像拉取策略的监控

### 1. 查看镜像拉取状态

```bash
# 查看 Pod 状态
kubectl get pods

# 查看 Pod 详情
kubectl describe pod <pod-name>

# 查看 Pod 事件
kubectl get events --field-selector involvedObject.name=<pod-name>
```

### 2. 查看镜像拉取日志

```bash
# 查看 kubelet 日志
journalctl -u kubelet -f

# 查看 Pod 日志
kubectl logs <pod-name>
```

### 3. 监控镜像拉取

可以使用 Prometheus 监控镜像拉取情况。

**Prometheus 指标**：
- `kubelet_image_pull_duration_seconds`：镜像拉取耗时
- `kubelet_image_pull_errors_total`：镜像拉取错误总数
- `kubelet_image_pull_successes_total`：镜像拉取成功总数

## 总结

Kubernetes 中镜像的下载策略通过 `imagePullPolicy` 参数控制，有三种策略：

**Always**：
- 总是拉取镜像
- 适用于使用 `:latest` 标签的镜像
- 确保使用最新版本的镜像

**IfNotPresent**：
- 如果本地不存在该镜像，则拉取镜像
- 如果本地已存在，则使用本地镜像
- 适用于使用固定标签的镜像
- 节省网络带宽和拉取时间

**Never**：
- 从不拉取镜像，只使用本地镜像
- 如果本地不存在该镜像，则 Pod 启动失败
- 适用于使用私有镜像仓库或离线环境

**默认策略**：
- `:latest` 标签：默认使用 `Always`
- 其他标签：默认使用 `IfNotPresent`
- 无标签：默认使用 `Always`

**最佳实践**：
- 生产环境：使用固定标签，使用 `IfNotPresent` 策略
- 开发环境：使用 `:latest` 标签，使用 `Always` 策略
- 离线环境：使用 `Never` 策略，预加载镜像

通过合理配置镜像拉取策略，可以优化镜像拉取性能，提高 Pod 启动速度。