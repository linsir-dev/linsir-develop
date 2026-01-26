# 什么是Minikube？

## 概述

Minikube 是一个工具，可以在本地轻松运行 Kubernetes 集群。Minikube 在本地计算机上的虚拟机（VM）或容器中运行单节点 Kubernetes 集群，非常适合开发、测试和学习 Kubernetes。

## Minikube 的特点

### 1. 本地运行

- **单节点集群**：在本地运行单节点 Kubernetes 集群
- **轻量级**：资源占用少，适合本地开发
- **快速启动**：启动速度快，可以快速创建和销毁集群

### 2. 跨平台支持

- **Windows**：支持 Windows 操作系统
- **macOS**：支持 macOS 操作系统
- **Linux**：支持 Linux 操作系统

### 3. 多种驱动支持

- **VirtualBox**：使用 VirtualBox 作为虚拟化驱动
- **VMware**：使用 VMware 作为虚拟化驱动
- **Hyper-V**：使用 Hyper-V 作为虚拟化驱动（Windows）
- **KVM**：使用 KVM 作为虚拟化驱动（Linux）
- **Docker**：使用 Docker 作为容器驱动
- **Podman**：使用 Podman 作为容器驱动

### 4. 丰富的功能

- **Dashboard**：提供 Kubernetes Dashboard
- **插件系统**：支持多种插件
- **负载均衡器**：支持 LoadBalancer 类型 Service
- **存储类**：支持多种存储类
- **网络插件**：支持多种网络插件

## Minikube 的安装

### 1. Windows 安装

```bash
# 使用 Chocolatey 安装
choco install minikube

# 使用 Scoop 安装
scoop install minikube

# 手动下载安装
# 1. 下载 minikube-installer.exe
# 2. 运行安装程序
# 3. 将 minikube 添加到 PATH
```

### 2. macOS 安装

```bash
# 使用 Homebrew 安装
brew install minikube

# 手动下载安装
# 1. 下载 minikube-darwin-amd64
# 2. 添加执行权限
chmod +x minikube-darwin-amd64
# 3. 移动到 PATH
sudo mv minikube-darwin-amd64 /usr/local/bin/minikube
```

### 3. Linux 安装

```bash
# 使用包管理器安装（Debian/Ubuntu）
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube

# 使用包管理器安装（Fedora/CentOS）
sudo dnf install minikube

# 使用包管理器安装（Arch Linux）
sudo pacman -S minikube
```

### 4. 安装 kubectl

Minikube 需要配合 kubectl 使用：

```bash
# Windows
choco install kubernetes-cli

# macOS
brew install kubectl

# Linux
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
```

## Minikube 的使用

### 1. 启动集群

```bash
# 启动集群（使用默认驱动）
minikube start

# 启动集群（指定驱动）
minikube start --driver=docker

# 启动集群（指定 Kubernetes 版本）
minikube start --kubernetes-version=v1.23.0

# 启动集群（指定资源）
minikube start --cpus=4 --memory=8192

# 启动集群（指定网络）
minikube start --network-plugin=cni
```

### 2. 查看集群状态

```bash
# 查看集群状态
minikube status

# 查看 Kubernetes 版本
kubectl version

# 查看节点
kubectl get nodes
```

### 3. 访问 Dashboard

```bash
# 启动 Dashboard
minikube dashboard

# 在后台启动 Dashboard
minikube dashboard --url

# 使用 kubectl 代理访问
kubectl proxy
# 然后访问 http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/http:kubernetes-dashboard:/proxy/
```

### 4. 管理集群

```bash
# 停止集群
minikube stop

# 删除集群
minikube delete

# 暂停集群
minikube pause

# 恢复集群
minikube unpause
```

### 5. 配置集群

```bash
# 查看配置
minikube config view

# 设置配置
minikube config set driver docker
minikube config set cpus 4
minikube config set memory 8192

# 删除配置
minikube config unset driver
```

## Minikube 的插件

### 1. 查看插件

```bash
# 查看所有插件
minikube addons list

# 查看启用的插件
minikube addons list | grep enabled
```

### 2. 启用插件

```bash
# 启用 Dashboard
minikube addons enable dashboard

# 启用 Ingress
minikube addons enable ingress

# 启用 Metrics Server
minikube addons enable metrics-server

# 启用 Registry
minikube addons enable registry
```

### 3. 禁用插件

```bash
# 禁用 Dashboard
minikube addons disable dashboard

# 禁用 Ingress
minikube addons disable ingress
```

## Minikube 的常用插件

### 1. Dashboard

**功能**：提供 Kubernetes Web UI

**启用**：
```bash
minikube addons enable dashboard
minikube dashboard
```

### 2. Ingress

**功能**：提供 Ingress 控制器

**启用**：
```bash
minikube addons enable ingress
```

**使用**：
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: example-ingress
spec:
  rules:
  - host: example.local
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: example-service
            port:
              number: 80
```

### 3. Metrics Server

**功能**：提供资源监控数据

**启用**：
```bash
minikube addons enable metrics-server
```

**使用**：
```bash
# 查看 Pod 的资源使用情况
kubectl top pods

# 查看 Node 的资源使用情况
kubectl top nodes
```

### 4. Registry

**功能**：提供本地 Docker Registry

**启用**：
```bash
minikube addons enable registry
```

**使用**：
```bash
# 推送镜像到本地 Registry
docker tag myimage localhost:5000/myimage
docker push localhost:5000/myimage
```

## Minikube 的网络

### 1. 查看集群 IP

```bash
# 查看集群 IP
minikube ip

# 查看集群 URL
minikube url
```

### 2. 端口转发

```bash
# 转发本地端口到 Pod 端口
kubectl port-forward <pod-name> <local-port>:<pod-port>

# 转发本地端口到 Service 端口
kubectl port-forward svc/<service-name> <local-port>:<service-port>
```

### 3. 访问 Service

```bash
# 获取 Service URL
minikube service <service-name>

# 获取 Service URL（在后台）
minikube service <service-name> --url
```

## Minikube 的存储

### 1. 查看存储类

```bash
# 查看存储类
kubectl get storageclass

# 查看默认存储类
kubectl get storageclass -o yaml | grep -i storageclass.kubernetes.io/is-default-class
```

### 2. 创建持久化存储

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: my-pvc
spec:
  accessModes:
  - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
```

### 3. 使用持久化存储

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: my-pod
spec:
  containers:
  - name: my-container
    image: nginx
    volumeMounts:
    - name: my-volume
      mountPath: /data
  volumes:
  - name: my-volume
    persistentVolumeClaim:
      claimName: my-pvc
```

## Minikube 的镜像

### 1. 构建镜像

```bash
# 构建镜像
docker build -t myimage:latest .

# 标记镜像
docker tag myimage:latest minikube/myimage:latest

# 加载镜像到 Minikube
minikube image load myimage:latest
```

### 2. 使用本地镜像

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: my-pod
spec:
  containers:
  - name: my-container
    image: myimage:latest
    imagePullPolicy: Never
```

### 3. 推送镜像到 Registry

```bash
# 推送到 Docker Hub
docker push username/myimage:latest

# 推送到本地 Registry
docker tag myimage:latest localhost:5000/myimage:latest
docker push localhost:5000/myimage:latest
```

## Minikube 的日志

### 1. 查看日志

```bash
# 查看 Minikube 日志
minikube logs

# 查看特定组件的日志
minikube logs --file=apiserver.log
minikube logs --file=scheduler.log
minikube logs --file=controller.log
```

### 2. 查看 Pod 日志

```bash
# 查看 Pod 日志
kubectl logs <pod-name>

# 查看容器日志
kubectl logs <pod-name> -c <container-name>

# 实时查看日志
kubectl logs -f <pod-name>
```

## Minikube 的故障排查

### 1. 检查集群状态

```bash
# 检查集群状态
minikube status

# 检查节点状态
kubectl get nodes

# 检查 Pod 状态
kubectl get pods --all-namespaces
```

### 2. 查看事件

```bash
# 查看所有事件
kubectl get events --all-namespaces

# 查看 Pod 事件
kubectl describe pod <pod-name>
```

### 3. 重启集群

```bash
# 停止集群
minikube stop

# 删除集群
minikube delete

# 重新启动集群
minikube start
```

## Minikube 的最佳实践

### 1. 资源配置

```bash
# 根据需要配置资源
minikube start --cpus=4 --memory=8192 --disk-size=50g
```

### 2. 使用插件

```bash
# 启用常用插件
minikube addons enable ingress
minikube addons enable metrics-server
minikube addons enable registry
```

### 3. 版本管理

```bash
# 指定 Kubernetes 版本
minikube start --kubernetes-version=v1.23.0

# 更新 Minikube
minikube update-check
```

### 4. 多集群管理

```bash
# 创建多个集群
minikube start -p cluster1
minikube start -p cluster2

# 切换集群
kubectl config use-context cluster1
kubectl config use-context cluster2

# 列出所有集群
minikube profile list
```

## Minikube 的限制

### 1. 单节点集群

- **限制**：Minikube 只能运行单节点集群
- **影响**：无法测试高可用性、多节点调度等功能

### 2. 资源限制

- **限制**：受限于本地机器的资源
- **影响**：无法运行大规模应用

### 3. 功能限制

- **限制**：某些 Kubernetes 功能可能不可用
- **影响**：无法测试所有 Kubernetes 功能

### 4. 性能限制

- **限制**：性能不如生产环境
- **影响**：无法准确模拟生产环境性能

## 总结

Minikube 是一个强大的本地 Kubernetes 集群工具，适合开发、测试和学习 Kubernetes。

**Minikube 的特点**：
- 本地运行：在本地运行单节点 Kubernetes 集群
- 跨平台支持：支持 Windows、macOS、Linux
- 多种驱动支持：支持 VirtualBox、VMware、Docker 等
- 丰富的功能：支持 Dashboard、插件系统、负载均衡器等

**Minikube 的使用**：
- 启动集群：`minikube start`
- 访问 Dashboard：`minikube dashboard`
- 管理集群：`minikube stop`、`minikube delete`
- 配置集群：`minikube config set`

**Minikube 的插件**：
- Dashboard：提供 Kubernetes Web UI
- Ingress：提供 Ingress 控制器
- Metrics Server：提供资源监控数据
- Registry：提供本地 Docker Registry

**Minikube 的最佳实践**：
- 合理配置资源
- 启用常用插件
- 管理多个集群
- 定期更新版本

Minikube 是学习 Kubernetes 的最佳工具，可以快速搭建本地 Kubernetes 集群，方便开发和测试。虽然 Minikube 有一些限制，但对于学习和开发来说已经足够了。