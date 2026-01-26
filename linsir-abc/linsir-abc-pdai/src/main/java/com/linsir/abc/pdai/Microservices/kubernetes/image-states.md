# image的状态有哪些？

## 概述

Kubernetes 中镜像的状态描述了容器镜像在集群中的拉取和使用情况。了解镜像状态有助于排查镜像相关的问题，确保 Pod 能够正常运行。

## 镜像状态类型

### 1. Pulling

**定义**：正在拉取镜像。

**描述**：kubelet 正在从镜像仓库拉取镜像。

**示例**：
```bash
# 查看 Pod 状态
kubectl get pods

# 输出示例
NAME         READY   STATUS              RESTARTS   AGE
nginx-pod    0/1     ContainerCreating   0          10s

# 查看 Pod 详情
kubectl describe pod nginx-pod

# 输出示例
Events:
  Type     Reason   Age   From               Message
  ----     ------   ----  ----               -------
  Normal   Pulling  5s    kubelet          Pulling image "nginx:1.14.2"
```

### 2. Pulled

**定义**：镜像拉取成功。

**描述**：kubelet 成功从镜像仓库拉取镜像。

**示例**：
```bash
# 查看 Pod 状态
kubectl get pods

# 输出示例
NAME         READY   STATUS    RESTARTS   AGE
nginx-pod    1/1     Running   0          30s

# 查看 Pod 详情
kubectl describe pod nginx-pod

# 输出示例
Events:
  Type     Reason   Age   From               Message
  ----     ------   ----  ----               -------
  Normal   Pulling  30s   kubelet          Pulling image "nginx:1.14.2"
  Normal   Pulled   20s   kubelet          Successfully pulled image "nginx:1.14.2"
  Normal   Created  15s   kubelet          Created container nginx
  Normal   Started  10s   kubelet          Started container nginx
```

### 3. PullError

**定义**：镜像拉取失败。

**描述**：kubelet 从镜像仓库拉取镜像失败。

**常见原因**：
- 镜像不存在
- 镜像仓库不可访问
- 网络问题
- 认证失败

**示例**：
```bash
# 查看 Pod 状态
kubectl get pods

# 输出示例
NAME         READY   STATUS             RESTARTS   AGE
nginx-pod    0/1     ImagePullBackOff   0          1m

# 查看 Pod 详情
kubectl describe pod nginx-pod

# 输出示例
Events:
  Type     Reason     Age   From               Message
  ----     ------     ----  ----               -------
  Normal   Pulling    60s   kubelet          Pulling image "nginx:1.14.2"
  Warning  Failed     50s   kubelet          Failed to pull image "nginx:1.14.2": rpc error: code = Unknown desc = Error response from daemon: pull access denied for nginx, repository does not exist or may require 'docker login'
  Warning  Failed     40s   kubelet          Error: ErrImagePull
  Warning  Failed     30s   kubelet          Error: ImagePullBackOff
```

### 4. ErrImagePull

**定义**：镜像拉取错误。

**描述**：kubelet 从镜像仓库拉取镜像时发生错误。

**常见原因**：
- 镜像名称或标签错误
- 镜像仓库地址错误
- 镜像仓库认证失败

**示例**：
```bash
# 查看 Pod 状态
kubectl get pods

# 输出示例
NAME         READY   STATUS          RESTARTS   AGE
nginx-pod    0/1     ErrImagePull    0          30s

# 查看 Pod 详情
kubectl describe pod nginx-pod

# 输出示例
Events:
  Type     Reason        Age   From               Message
  ----     ------        ----  ----               -------
  Normal   Scheduled     35s   default-scheduler   Successfully assigned default/nginx-pod to node-1
  Normal   Pulling       30s   kubelet          Pulling image "nginx:1.14.2"
  Warning  Failed        25s   kubelet          Failed to pull image "nginx:1.14.2": rpc error: code = Unknown desc = Error response from daemon: pull access denied for nginx, repository does not exist or may require 'docker login'
  Warning  Failed        20s   kubelet          Error: ErrImagePull
```

### 5. ErrImageInspect

**定义**：镜像检查错误。

**描述**：kubelet 检查镜像时发生错误。

**常见原因**：
- 镜像损坏
- 镜像格式不正确
- 镜像仓库返回错误

**示例**：
```bash
# 查看 Pod 状态
kubectl get pods

# 输出示例
NAME         READY   STATUS             RESTARTS   AGE
nginx-pod    0/1     ImageInspectError 0          30s

# 查看 Pod 详情
kubectl describe pod nginx-pod

# 输出示例
Events:
  Type     Reason              Age   From               Message
  ----     ------              ----  ----               -------
  Normal   Scheduled           35s   default-scheduler   Successfully assigned default/nginx-pod to node-1
  Normal   Pulled              30s   kubelet          Successfully pulled image "nginx:1.14.2"
  Warning  Failed              25s   kubelet          Error: ErrImageInspect
```

### 6. ErrImageNeverPull

**定义**：从不拉取镜像。

**描述**：kubelet 配置为从不拉取镜像，但本地不存在该镜像。

**常见原因**：
- `imagePullPolicy` 设置为 `Never`
- 本地不存在该镜像

**示例**：
```bash
# 查看 Pod 状态
kubectl get pods

# 输出示例
NAME         READY   STATUS              RESTARTS   AGE
nginx-pod    0/1     ErrImageNeverPull   0          30s

# 查看 Pod 详情
kubectl describe pod nginx-pod

# 输出示例
Events:
  Type     Reason              Age   From               Message
  ----     ------              ----  ----               -------
  Normal   Scheduled           35s   default-scheduler   Successfully assigned default/nginx-pod to node-1
  Warning  Failed              30s   kubelet          Failed to pull image "nginx:1.14.2": rpc error: code = NotFound desc = failed to pull and unpack image "nginx:1.14.2": no match for platform in manifest: not found
  Warning  Failed              25s   kubelet          Error: ErrImageNeverPull
```

### 7. ImagePullBackOff

**定义**：镜像拉取失败，等待重试。

**描述**：kubelet 拉取镜像失败，等待一段时间后重试。

**重试策略**：
- **重试次数**：默认为 3 次
- **重试间隔**：指数退避，从 10 秒开始，最大 5 分钟

**示例**：
```bash
# 查看 Pod 状态
kubectl get pods

# 输出示例
NAME         READY   STATUS             RESTARTS   AGE
nginx-pod    0/1     ImagePullBackOff   0          5m

# 查看 Pod 详情
kubectl describe pod nginx-pod

# 输出示例
Events:
  Type     Reason     Age   From               Message
  ----     ------     ----  ----               -------
  Normal   Pulling    5m    kubelet          Pulling image "nginx:1.14.2"
  Warning  Failed     4m    kubelet          Failed to pull image "nginx:1.14.2": rpc error: code = Unknown desc = Error response from daemon: pull access denied for nginx, repository does not exist or may require 'docker login'
  Warning  Failed     3m    kubelet          Error: ErrImagePull
  Warning  Failed     2m    kubelet          Error: ImagePullBackOff
  Normal   BackOff    1m    kubelet          Back-off pulling image "nginx:1.14.2"
```

### 8. CrashLoopBackOff

**定义**：容器崩溃，等待重启。

**描述**：容器启动后立即崩溃，kubelet 等待一段时间后重启容器。

**重试策略**：
- **重试次数**：默认为 5 次
- **重试间隔**：指数退避，从 10 秒开始，最大 5 分钟

**示例**：
```bash
# 查看 Pod 状态
kubectl get pods

# 输出示例
NAME         READY   STATUS              RESTARTS   AGE
nginx-pod    0/1     CrashLoopBackOff    5          10m

# 查看 Pod 详情
kubectl describe pod nginx-pod

# 输出示例
Events:
  Type     Reason     Age   From               Message
  ----     ------     ----  ----               -------
  Normal   Scheduled  10m   default-scheduler   Successfully assigned default/nginx-pod to node-1
  Normal   Pulled     10m   kubelet          Successfully pulled image "nginx:1.14.2"
  Normal   Created    10m   kubelet          Created container nginx
  Normal   Started    10m   kubelet          Started container nginx
  Warning  BackOff    9m    kubelet          Back-off restarting failed container
  Normal   Pulled     8m    kubelet          Successfully pulled image "nginx:1.14.2"
  Normal   Created    8m    kubelet          Created container nginx
  Normal   Started    8m    kubelet          Started container nginx
  Warning  BackOff    7m    kubelet          Back-off restarting failed container
  ...
```

## 镜像状态转换

### 1. 正常流程

```
Pulling → Pulled → Created → Started → Running
```

### 2. 拉取失败流程

```
Pulling → ErrImagePull → ImagePullBackOff
```

### 3. 检查失败流程

```
Pulling → Pulled → ErrImageInspect → ImageInspectError
```

### 4. 崩溃重启流程

```
Pulling → Pulled → Created → Started → Running → CrashLoopBackOff
```

## 镜像状态排查

### 1. 查看 Pod 状态

```bash
# 查看 Pod 状态
kubectl get pods

# 查看 Pod 详情
kubectl describe pod <pod-name>
```

### 2. 查看 Pod 事件

```bash
# 查看 Pod 事件
kubectl get events --field-selector involvedObject.name=<pod-name>

# 查看所有事件
kubectl get events --all-namespaces
```

### 3. 查看 kubelet 日志

```bash
# 查看 kubelet 日志
journalctl -u kubelet -f

# 查看 kubelet 日志（指定节点）
ssh node-1
journalctl -u kubelet -f
```

### 4. 查看容器日志

```bash
# 查看 Pod 日志
kubectl logs <pod-name>

# 查看容器日志
kubectl logs <pod-name> -c <container-name>

# 查看之前的容器日志
kubectl logs <pod-name> --previous
```

## 镜像状态常见问题

### 1. ImagePullBackOff

**原因**：
- 镜像不存在
- 镜像仓库不可访问
- 网络问题
- 认证失败

**解决方案**：
- 检查镜像名称和标签
- 检查镜像仓库地址
- 检查网络连接
- 检查镜像仓库认证

**示例**：
```bash
# 检查镜像是否存在
docker pull nginx:1.14.2

# 检查镜像仓库地址
curl https://registry-1.docker.io/v2/

# 检查网络连接
ping registry-1.docker.io

# 检查镜像仓库认证
kubectl create secret docker-registry my-registry-secret \
  --docker-server=registry.example.com \
  --docker-username=user \
  --docker-password=password \
  --docker-email=user@example.com
```

### 2. ErrImagePull

**原因**：
- 镜像名称或标签错误
- 镜像仓库地址错误
- 镜像仓库认证失败

**解决方案**：
- 检查镜像名称和标签
- 检查镜像仓库地址
- 检查镜像仓库认证

**示例**：
```bash
# 检查镜像名称和标签
docker images | grep nginx

# 检查镜像仓库地址
curl https://registry.example.com/v2/

# 检查镜像仓库认证
kubectl get secret my-registry-secret -o yaml
```

### 3. ErrImageInspect

**原因**：
- 镜像损坏
- 镜像格式不正确
- 镜像仓库返回错误

**解决方案**：
- 重新拉取镜像
- 检查镜像格式
- 检查镜像仓库

**示例**：
```bash
# 重新拉取镜像
docker pull nginx:1.14.2

# 检查镜像格式
docker inspect nginx:1.14.2

# 检查镜像仓库
curl https://registry.example.com/v2/nginx/manifests/1.14.2
```

### 4. CrashLoopBackOff

**原因**：
- 容器启动失败
- 容器启动后立即崩溃
- 容器配置错误

**解决方案**：
- 查看容器日志
- 检查容器配置
- 检查容器启动命令

**示例**：
```bash
# 查看容器日志
kubectl logs <pod-name>

# 检查容器配置
kubectl describe pod <pod-name>

# 进入容器调试
kubectl exec -it <pod-name> -- /bin/bash
```

## 镜像状态监控

### 1. 使用 kubectl 监控

```bash
# 监控 Pod 状态
watch kubectl get pods

# 监控 Pod 事件
watch kubectl get events --field-selector involvedObject.name=<pod-name>
```

### 2. 使用 Prometheus 监控

**Prometheus 指标**：
- `kubelet_image_pull_duration_seconds`：镜像拉取耗时
- `kubelet_image_pull_errors_total`：镜像拉取错误总数
- `kubelet_image_pull_successes_total`：镜像拉取成功总数

**PromQL 查询**：
```promql
# 镜像拉取错误率
rate(kubelet_image_pull_errors_total[5m])

# 镜像拉取成功率
rate(kubelet_image_pull_successes_total[5m])

# 镜像拉取耗时
histogram_quantile(0.99, kubelet_image_pull_duration_seconds)
```

### 3. 使用 Grafana 可视化

**Grafana Dashboard**：
- Kubernetes Pods
- Kubernetes Nodes
- Kubernetes Cluster

## 总结

Kubernetes 中镜像的状态描述了容器镜像在集群中的拉取和使用情况。

**镜像状态类型**：
- **Pulling**：正在拉取镜像
- **Pulled**：镜像拉取成功
- **PullError**：镜像拉取失败
- **ErrImagePull**：镜像拉取错误
- **ErrImageInspect**：镜像检查错误
- **ErrImageNeverPull**：从不拉取镜像
- **ImagePullBackOff**：镜像拉取失败，等待重试
- **CrashLoopBackOff**：容器崩溃，等待重启

**镜像状态转换**：
- **正常流程**：Pulling → Pulled → Created → Started → Running
- **拉取失败流程**：Pulling → ErrImagePull → ImagePullBackOff
- **检查失败流程**：Pulling → Pulled → ErrImageInspect → ImageInspectError
- **崩溃重启流程**：Pulling → Pulled → Created → Started → Running → CrashLoopBackOff

**镜像状态排查**：
- 查看 Pod 状态
- 查看 Pod 事件
- 查看 kubelet 日志
- 查看容器日志

**镜像状态常见问题**：
- **ImagePullBackOff**：检查镜像名称、镜像仓库地址、网络连接、镜像仓库认证
- **ErrImagePull**：检查镜像名称和标签、镜像仓库地址、镜像仓库认证
- **ErrImageInspect**：重新拉取镜像、检查镜像格式、检查镜像仓库
- **CrashLoopBackOff**：查看容器日志、检查容器配置、检查容器启动命令

通过了解镜像状态，可以快速排查镜像相关的问题，确保 Pod 能够正常运行。