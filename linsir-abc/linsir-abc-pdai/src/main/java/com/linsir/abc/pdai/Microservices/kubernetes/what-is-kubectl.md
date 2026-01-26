# 什么是Kubectl？

## 概述

kubectl 是 Kubernetes 的命令行工具（CLI），用于管理 Kubernetes 集群。kubectl 提供了丰富的命令，可以用于部署应用、查看和管理集群资源、查看日志等。kubectl 是与 Kubernetes 集群交互的主要工具。

## kubectl 的安装

### 1. Windows 安装

```bash
# 使用 Chocolatey 安装
choco install kubernetes-cli

# 使用 Scoop 安装
scoop install kubectl

# 手动下载安装
# 1. 下载 kubectl.exe
# 2. 将 kubectl.exe 添加到 PATH
```

### 2. macOS 安装

```bash
# 使用 Homebrew 安装
brew install kubectl

# 手动下载安装
# 1. 下载 kubectl
# 2. 添加执行权限
chmod +x kubectl
# 3. 移动到 PATH
sudo mv kubectl /usr/local/bin/kubectl
```

### 3. Linux 安装

```bash
# 使用包管理器安装（Debian/Ubuntu）
sudo apt-get update && sudo apt-get install -y kubectl

# 使用包管理器安装（Fedora/CentOS）
sudo dnf install kubectl

# 使用包管理器安装（Arch Linux）
sudo pacman -S kubectl

# 手动下载安装
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
```

## kubectl 的配置

### 1. 查看配置

```bash
# 查看配置
kubectl config view

# 查看当前上下文
kubectl config current-context

# 查看所有上下文
kubectl config get-contexts
```

### 2. 配置集群

```bash
# 设置集群
kubectl config set-cluster my-cluster --server=https://kubernetes.example.com

# 设置用户
kubectl config set-credentials my-user --username=admin --password=secret

# 设置上下文
kubectl config set-context my-context --cluster=my-cluster --user=my-user

# 使用上下文
kubectl config use-context my-context
```

### 3. 配置 Namespace

```bash
# 设置默认 Namespace
kubectl config set-context --current --namespace=dev

# 查看 Namespace
kubectl config view --minify | grep namespace
```

## kubectl 的基本命令

### 1. 资源管理

```bash
# 创建资源
kubectl create -f deployment.yaml

# 应用配置
kubectl apply -f deployment.yaml

# 删除资源
kubectl delete -f deployment.yaml

# 更新资源
kubectl replace -f deployment.yaml
```

### 2. 查看资源

```bash
# 查看所有 Pod
kubectl get pods

# 查看 Pod 的详细信息
kubectl describe pod <pod-name>

# 查看所有 Namespace
kubectl get namespaces

# 查看所有 Service
kubectl get services

# 查看所有 Deployment
kubectl get deployments

# 查看所有资源
kubectl get all
```

### 3. 查看日志

```bash
# 查看 Pod 日志
kubectl logs <pod-name>

# 实时查看日志
kubectl logs -f <pod-name>

# 查看容器日志
kubectl logs <pod-name> -c <container-name>

# 查看最近的日志
kubectl logs --tail=100 <pod-name>

# 查看特定时间的日志
kubectl logs --since=1h <pod-name>
```

### 4. 进入容器

```bash
# 进入容器
kubectl exec -it <pod-name> -- /bin/bash

# 进入容器（指定容器）
kubectl exec -it <pod-name> -c <container-name> -- /bin/bash

# 在容器中执行命令
kubectl exec <pod-name> -- ls /app

# 在容器中执行命令（指定容器）
kubectl exec <pod-name> -c <container-name> -- ls /app
```

### 5. 端口转发

```bash
# 转发本地端口到 Pod 端口
kubectl port-forward <pod-name> <local-port>:<pod-port>

# 转发本地端口到 Service 端口
kubectl port-forward svc/<service-name> <local-port>:<service-port>

# 转发本地端口到 Deployment 端口
kubectl port-forward deployment/<deployment-name> <local-port>:<container-port>
```

## kubectl 的高级命令

### 1. 扩缩容

```bash
# 扩容 Deployment
kubectl scale deployment <deployment-name> --replicas=5

# 扩容 StatefulSet
kubectl scale statefulset <statefulset-name> --replicas=3

# 自动扩缩容
kubectl autoscale deployment <deployment-name> --min=2 --max=10 --cpu-percent=80
```

### 2. 滚动更新

```bash
# 更新镜像
kubectl set image deployment/<deployment-name> <container-name>=<image>:<tag>

# 查看更新状态
kubectl rollout status deployment/<deployment-name>

# 查看更新历史
kubectl rollout history deployment/<deployment-name>

# 回滚到上一个版本
kubectl rollout undo deployment/<deployment-name>

# 回滚到指定版本
kubectl rollout undo deployment/<deployment-name> --to-revision=2
```

### 3. 调试

```bash
# 查看 Pod 事件
kubectl describe pod <pod-name>

# 查看 Node 事件
kubectl describe node <node-name>

# 查看 Deployment 事件
kubectl describe deployment <deployment-name>

# 查看所有事件
kubectl get events --all-namespaces
```

### 4. 资源编辑

```bash
# 编辑 Deployment
kubectl edit deployment <deployment-name>

# 编辑 Service
kubectl edit service <service-name>

# 编辑 ConfigMap
kubectl edit configmap <configmap-name>

# 编辑 Secret
kubectl edit secret <secret-name>
```

## kubectl 的常用操作

### 1. Pod 操作

```bash
# 创建 Pod
kubectl run nginx --image=nginx:1.14.2

# 查看 Pod
kubectl get pods

# 查看 Pod 详情
kubectl describe pod <pod-name>

# 删除 Pod
kubectl delete pod <pod-name>

# 强制删除 Pod
kubectl delete pod <pod-name> --force --grace-period=0
```

### 2. Deployment 操作

```bash
# 创建 Deployment
kubectl create deployment nginx --image=nginx:1.14.2

# 扩容 Deployment
kubectl scale deployment nginx --replicas=3

# 更新 Deployment
kubectl set image deployment/nginx nginx=nginx:1.15.0

# 回滚 Deployment
kubectl rollout undo deployment/nginx

# 删除 Deployment
kubectl delete deployment nginx
```

### 3. Service 操作

```bash
# 创建 Service
kubectl expose deployment nginx --port=80 --type=LoadBalancer

# 查看 Service
kubectl get services

# 查看 Service 详情
kubectl describe service <service-name>

# 删除 Service
kubectl delete service <service-name>
```

### 4. ConfigMap 操作

```bash
# 创建 ConfigMap
kubectl create configmap my-config --from-file=config.txt

# 查看 ConfigMap
kubectl get configmaps

# 查看 ConfigMap 详情
kubectl describe configmap <configmap-name>

# 删除 ConfigMap
kubectl delete configmap <configmap-name>
```

### 5. Secret 操作

```bash
# 创建 Secret
kubectl create secret generic my-secret --from-literal=password=secret

# 查看 Secret
kubectl get secrets

# 查看 Secret 详情
kubectl describe secret <secret-name>

# 删除 Secret
kubectl delete secret <secret-name>
```

## kubectl 的输出格式

### 1. 常用输出格式

```bash
# 表格格式（默认）
kubectl get pods -o wide

# JSON 格式
kubectl get pods -o json

# YAML 格式
kubectl get pods -o yaml

# 自定义列
kubectl get pods -o custom-columns=NAME:.metadata.name,STATUS:.status.phase

# 自定义列（使用模板）
kubectl get pods -o go-template='{{range .items}}{{.metadata.name}}{{"\n"}}{{end}}'
```

### 2. 输出到文件

```bash
# 输出到文件
kubectl get pods -o yaml > pods.yaml

# 追加到文件
kubectl get pods -o yaml >> pods.yaml
```

## kubectl 的标签和选择器

### 1. 添加标签

```bash
# 添加标签
kubectl label pod <pod-name> app=nginx

# 添加多个标签
kubectl label pod <pod-name> app=nginx env=prod

# 更新标签
kubectl label pod <pod-name> app=web --overwrite
```

### 2. 删除标签

```bash
# 删除标签
kubectl label pod <pod-name> app-

# 删除多个标签
kubectl label pod <pod-name> app- env-
```

### 3. 使用选择器

```bash
# 查询带有特定标签的 Pod
kubectl get pods -l app=nginx

# 查询带有多个标签的 Pod
kubectl get pods -l app=nginx,env=prod

# 查询标签不等于特定值的 Pod
kubectl get pods -l app!=nginx

# 查询标签存在或不存在的 Pod
kubectl get pods -l app
kubectl get pods -l '!app'
```

## kubectl 的注解

### 1. 添加注解

```bash
# 添加注解
kubectl annotate pod <pod-name> description="This is a nginx pod"

# 添加多个注解
kubectl annotate pod <pod-name> description="This is a nginx pod" monitoring="enabled"
```

### 2. 删除注解

```bash
# 删除注解
kubectl annotate pod <pod-name> description-

# 删除多个注解
kubectl annotate pod <pod-name> description- monitoring-
```

### 3. 查看注解

```bash
# 查看注解
kubectl describe pod <pod-name>

# 查看 Pod 的注解
kubectl get pod <pod-name> -o jsonpath='{.metadata.annotations}'
```

## kubectl 的资源配额

### 1. 查看资源使用

```bash
# 查看 Pod 的资源使用
kubectl top pods

# 查看 Node 的资源使用
kubectl top nodes

# 查看 Namespace 的资源使用
kubectl top pods -n <namespace>
```

### 2. 查看资源限制

```bash
# 查看 Pod 的资源限制
kubectl describe pod <pod-name> | grep -A 5 Limits

# 查看 Deployment 的资源限制
kubectl describe deployment <deployment-name> | grep -A 5 Limits
```

## kubectl 的故障排查

### 1. 查看状态

```bash
# 查看集群状态
kubectl cluster-info

# 查看节点状态
kubectl get nodes

# 查看 Pod 状态
kubectl get pods

# 查看所有 Namespace 的 Pod 状态
kubectl get pods --all-namespaces
```

### 2. 查看事件

```bash
# 查看所有事件
kubectl get events --all-namespaces

# 查看 Pod 事件
kubectl describe pod <pod-name>

# 查看 Node 事件
kubectl describe node <node-name>
```

### 3. 查看日志

```bash
# 查看 Pod 日志
kubectl logs <pod-name>

# 查看容器日志
kubectl logs <pod-name> -c <container-name>

# 查看之前的容器日志
kubectl logs <pod-name> --previous
```

## kubectl 的自动补全

### 1. Bash 自动补全

```bash
# 安装 Bash 自动补全
source <(kubectl completion bash)

# 永久启用 Bash 自动补全
echo "source <(kubectl completion bash)" >> ~/.bashrc
source ~/.bashrc
```

### 2. Zsh 自动补全

```bash
# 安装 Zsh 自动补全
source <(kubectl completion zsh)

# 永久启用 Zsh 自动补全
echo "source <(kubectl completion zsh)" >> ~/.zshrc
source ~/.zshrc
```

### 3. PowerShell 自动补全

```powershell
# 安装 PowerShell 自动补全
kubectl completion powershell | Out-String | Invoke-Expression

# 永久启用 PowerShell 自动补全
Add-Content -Path $PROFILE -Value "kubectl completion powershell | Out-String | Invoke-Expression"
```

## kubectl 的最佳实践

### 1. 使用配置文件

```bash
# 使用配置文件创建资源
kubectl apply -f deployment.yaml

# 使用配置文件删除资源
kubectl delete -f deployment.yaml

# 使用多个配置文件
kubectl apply -f deployment.yaml -f service.yaml

# 使用目录中的所有配置文件
kubectl apply -f ./k8s/
```

### 2. 使用 Namespace

```bash
# 在指定 Namespace 中创建资源
kubectl apply -f deployment.yaml -n dev

# 查看指定 Namespace 中的资源
kubectl get pods -n dev

# 设置默认 Namespace
kubectl config set-context --current --namespace=dev
```

### 3. 使用标签和选择器

```bash
# 使用标签查询资源
kubectl get pods -l app=nginx

# 使用标签删除资源
kubectl delete pods -l app=nginx

# 使用标签更新资源
kubectl label pods -l app=nginx env=prod
```

### 4. 使用输出格式

```bash
# 使用 wide 格式查看资源
kubectl get pods -o wide

# 使用 JSON 格式查看资源
kubectl get pods -o json

# 使用 YAML 格式查看资源
kubectl get pods -o yaml
```

## kubectl 的常用技巧

### 1. 快速查看资源

```bash
# 快速查看所有资源
kubectl get all

# 快速查看所有 Namespace 的资源
kubectl get all --all-namespaces

# 快速查看所有 Pod
kubectl get pods --all-namespaces
```

### 2. 快速删除资源

```bash
# 快速删除所有 Pod
kubectl delete pods --all

# 快速删除所有 Deployment
kubectl delete deployments --all

# 快速删除所有 Service
kubectl delete services --all
```

### 3. 快速复制资源

```bash
# 复制 Pod
kubectl get pod <pod-name> -o yaml | kubectl apply -f -

# 复制 Deployment
kubectl get deployment <deployment-name> -o yaml | kubectl apply -f -
```

## 总结

kubectl 是 Kubernetes 的命令行工具，用于管理 Kubernetes 集群。

**kubectl 的特点**：
- 功能强大：提供丰富的命令，可以管理所有 Kubernetes 资源
- 跨平台：支持 Windows、macOS、Linux
- 易于使用：命令简单直观，支持自动补全
- 灵活输出：支持多种输出格式

**kubectl 的基本命令**：
- 资源管理：`kubectl create`、`kubectl apply`、`kubectl delete`
- 查看资源：`kubectl get`、`kubectl describe`
- 查看日志：`kubectl logs`
- 进入容器：`kubectl exec`
- 端口转发：`kubectl port-forward`

**kubectl 的高级命令**：
- 扩缩容：`kubectl scale`、`kubectl autoscale`
- 滚动更新：`kubectl set image`、`kubectl rollout`
- 调试：`kubectl describe`、`kubectl get events`
- 资源编辑：`kubectl edit`

**kubectl 的最佳实践**：
- 使用配置文件
- 使用 Namespace
- 使用标签和选择器
- 使用输出格式

kubectl 是与 Kubernetes 集群交互的主要工具，掌握 kubectl 的使用对于管理 Kubernetes 集群至关重要。