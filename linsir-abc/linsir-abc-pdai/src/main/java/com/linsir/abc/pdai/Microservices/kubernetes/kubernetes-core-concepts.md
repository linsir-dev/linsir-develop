# Kubernetes中有哪些核心概念？

## 概述

Kubernetes 有许多核心概念，理解这些概念对于掌握 Kubernetes 至关重要。本文将详细介绍 Kubernetes 的核心概念，包括 Pod、Node、Namespace、Service、Volume、ConfigMap、Secret、Deployment、StatefulSet、DaemonSet、Job、CronJob 等。

## 1. Pod

### 定义

Pod 是 Kubernetes 中最小的部署单元，包含一个或多个紧密相关的容器。

### 特点

- **共享网络**：Pod 中的容器共享同一个网络命名空间，可以通过 localhost 互相访问
- **共享存储**：Pod 中的容器可以共享 Volume
- **临时性**：Pod 是临时的，可以被创建、删除、重建
- **IP 地址**：每个 Pod 都有独立的 IP 地址

### 示例

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
  labels:
    app: nginx
spec:
  containers:
  - name: nginx
    image: nginx:1.14.2
    ports:
    - containerPort: 80
  - name: sidecar
    image: busybox
    command: ['sh', '-c', 'while true; do echo hello; sleep 10;done']
```

### Pod 生命周期

```
Pending → Running → Succeeded/Failed
```

## 2. Node

### 定义

Node 是 Kubernetes 集群中的工作节点，可以是物理机或虚拟机。

### 特点

- **运行 Pod**：Node 负责运行 Pod
- **资源管理**：Node 提供计算、存储、网络资源
- **状态监控**：Kubernetes 定期检查 Node 状态

### Node 状态

- **Ready**：节点可用，可以运行 Pod
- **NotReady**：节点不可用，不能运行 Pod
- **Unknown**：节点状态未知

### 查看 Node

```bash
kubectl get nodes
kubectl describe node <node-name>
```

## 3. Namespace

### 定义

Namespace 用于隔离集群资源的虚拟集群。

### 作用

- **资源隔离**：不同 Namespace 中的资源相互隔离
- **权限控制**：可以为不同 Namespace 设置不同的权限
- **资源配额**：可以为 Namespace 设置资源配额

### 示例

```bash
# 创建 Namespace
kubectl create namespace dev

# 在指定 Namespace 中创建资源
kubectl apply -f deployment.yaml -n dev

# 查看 Namespace
kubectl get namespaces
```

### 默认 Namespace

- **default**：默认 Namespace
- **kube-system**：Kubernetes 系统组件
- **kube-public**：公共资源
- **kube-node-lease**：节点租约

## 4. Service

### 定义

Service 定义一组 Pod 的访问策略，提供稳定的网络访问。

### 类型

- **ClusterIP**：集群内部访问（默认）
- **NodePort**：通过节点端口访问
- **LoadBalancer**：通过负载均衡器访问
- **ExternalName**：映射到外部 DNS 名称

### 示例

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

### Service 发现

- **环境变量**：通过环境变量获取 Service 信息
- **DNS**：通过 CoreDNS 解析 Service 名称

## 5. Volume

### 定义

Volume 用于 Pod 中的数据存储和共享。

### 类型

- **emptyDir**：临时存储，Pod 删除时数据丢失
- **hostPath**：主机路径，Pod 删除时数据保留
- **NFS**：网络文件系统
- **PersistentVolume**：持久化存储
- **ConfigMap**：配置文件
- **Secret**：敏感信息

### 示例

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
spec:
  containers:
  - name: nginx
    image: nginx:1.14.2
    volumeMounts:
    - name: html
      mountPath: /usr/share/nginx/html
  volumes:
  - name: html
    emptyDir: {}
```

## 6. PersistentVolume (PV)

### 定义

PersistentVolume 是集群级别的存储资源，独立于 Pod 生命周期。

### 访问模式

- **ReadWriteOnce**：单节点读写
- **ReadOnlyMany**：多节点只读
- **ReadWriteMany**：多节点读写
- **ReadWriteOncePod**：单 Pod 读写

### 示例

```yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: pv-nginx
spec:
  capacity:
    storage: 10Gi
  accessModes:
  - ReadWriteOnce
  persistentVolumeReclaimPolicy: Retain
  storageClassName: manual
  hostPath:
    path: /mnt/data
```

## 7. PersistentVolumeClaim (PVC)

### 定义

PersistentVolumeClaim 是用户对存储资源的声明，用于绑定 PV。

### 示例

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: pvc-nginx
spec:
  accessModes:
  - ReadWriteOnce
  resources:
    requests:
      storage: 5Gi
  storageClassName: manual
```

## 8. ConfigMap

### 定义

ConfigMap 用于存储配置数据，以键值对形式存储。

### 用途

- **配置文件**：存储应用配置文件
- **环境变量**：为容器提供环境变量
- **命令行参数**：为容器提供命令行参数

### 示例

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: nginx-config
data:
  nginx.conf: |
    user nginx;
    worker_processes auto;
    error_log /var/log/nginx/error.log;
    pid /run/nginx.pid;
    
    events {
        worker_connections 1024;
    }
    
    http {
        include /etc/nginx/mime.types;
        default_type application/octet-stream;
        
        server {
            listen 80;
            server_name localhost;
            
            location / {
                root /usr/share/nginx/html;
                index index.html;
            }
        }
    }
```

### 使用 ConfigMap

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
spec:
  containers:
  - name: nginx
    image: nginx:1.14.2
    volumeMounts:
    - name: config
      mountPath: /etc/nginx/nginx.conf
      subPath: nginx.conf
  volumes:
  - name: config
    configMap:
      name: nginx-config
```

## 9. Secret

### 定义

Secret 用于存储敏感信息，如密码、密钥、证书等。

### 类型

- **Opaque**：通用 Secret
- **kubernetes.io/service-account-token**：Service Account Token
- **kubernetes.io/dockercfg**：Docker Registry 凭证
- **kubernetes.io/dockerconfigjson**：Docker Registry 凭证（JSON 格式）
- **kubernetes.io/basic-auth**：基本认证
- **kubernetes.io/ssh-auth**：SSH 认证
- **kubernetes.io/tls**：TLS 证书

### 示例

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: mysql-secret
type: Opaque
data:
  username: YWRtaW4=  # base64 编码的 admin
  password: cGFzc3dvcmQ=  # base64 编码的 password
```

### 使用 Secret

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: mysql-pod
spec:
  containers:
  - name: mysql
    image: mysql:5.7
    env:
    - name: MYSQL_ROOT_PASSWORD
      valueFrom:
        secretKeyRef:
          name: mysql-secret
          key: password
```

## 10. Deployment

### 定义

Deployment 用于管理无状态应用的部署和更新。

### 特点

- **声明式更新**：通过声明期望状态来更新应用
- **滚动更新**：支持滚动更新，零停机部署
- **回滚**：支持回滚到之前的版本
- **扩缩容**：支持自动和手动扩缩容

### 示例

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

### 更新 Deployment

```bash
# 更新镜像
kubectl set image deployment/nginx-deployment nginx=nginx:1.15.0

# 查看更新状态
kubectl rollout status deployment/nginx-deployment

# 回滚
kubectl rollout undo deployment/nginx-deployment
```

## 11. StatefulSet

### 定义

StatefulSet 用于管理有状态应用的部署，如数据库、消息队列等。

### 特点

- **稳定的网络标识**：每个 Pod 都有稳定的网络标识
- **稳定的持久化存储**：每个 Pod 都有稳定的持久化存储
- **有序部署**：Pod 按顺序部署和删除
- **有序更新**：Pod 按顺序更新

### 示例

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: mysql-statefulset
spec:
  serviceName: mysql
  replicas: 3
  selector:
    matchLabels:
      app: mysql
  template:
    metadata:
      labels:
        app: mysql
    spec:
      containers:
      - name: mysql
        image: mysql:5.7
        ports:
        - containerPort: 3306
          name: mysql
        volumeMounts:
        - name: mysql-persistent-storage
          mountPath: /var/lib/mysql
  volumeClaimTemplates:
  - metadata:
      name: mysql-persistent-storage
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: 10Gi
```

## 12. DaemonSet

### 定义

DaemonSet 确保在每个 Node 上都运行一个 Pod 副本。

### 用途

- **日志收集**：在每个节点上运行日志收集代理
- **监控**：在每个节点上运行监控代理
- **网络插件**：在每个节点上运行网络插件

### 示例

```yaml
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: fluentd
  namespace: kube-system
spec:
  selector:
    matchLabels:
      name: fluentd
  template:
    metadata:
      labels:
        name: fluentd
    spec:
      containers:
      - name: fluentd
        image: fluent/fluentd:v1.4.2-2
        resources:
          limits:
            memory: 200Mi
          requests:
            cpu: 100m
            memory: 200Mi
        volumeMounts:
        - name: varlog
          mountPath: /var/log
        - name: varlibdockercontainers
          mountPath: /var/lib/docker/containers
          readOnly: true
      terminationGracePeriodSeconds: 30
      volumes:
      - name: varlog
        hostPath:
          path: /var/log
      - name: varlibdockercontainers
        hostPath:
          path: /var/lib/docker/containers
```

## 13. Job

### 定义

Job 用于运行一次性任务，确保任务成功完成。

### 特点

- **一次性任务**：运行一次性任务
- **重试机制**：支持任务失败重试
- **并行执行**：支持并行执行多个 Pod

### 示例

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: pi
spec:
  template:
    spec:
      containers:
      - name: pi
        image: perl
        command: ["perl",  "-Mbignum=bpi", "-wle", "print bpi(2000)"]
      restartPolicy: Never
  backoffLimit: 4
```

## 14. CronJob

### 定义

CronJob 用于定时执行任务。

### 特点

- **定时执行**：按 Cron 表达式定时执行任务
- **并发控制**：支持并发控制
- **历史限制**：可以限制保留的历史任务数量

### 示例

```yaml
apiVersion: batch/v1beta1
kind: CronJob
metadata:
  name: hello
spec:
  schedule: "*/1 * * * *"
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: hello
            image: busybox
            args:
            - /bin/sh
            - -c
            - date; echo Hello from the Kubernetes cluster
          restartPolicy: OnFailure
```

## 15. Ingress

### 定义

Ingress 是集群的入口，用于管理外部访问集群内服务的规则。

### 特点

- **HTTP/HTTPS 路由**：支持 HTTP 和 HTTPS 路由
- **负载均衡**：支持负载均衡
- **SSL/TLS 终止**：支持 SSL/TLS 终止
- **虚拟主机**：支持虚拟主机

### 示例

```yaml
apiVersion: networking.k8s.io/v1beta1
kind: Ingress
metadata:
  name: nginx-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
  - host: example.com
    http:
      paths:
      - path: /
        backend:
          serviceName: nginx-service
          servicePort: 80
```

## 16. ServiceAccount

### 定义

ServiceAccount 为 Pod 提供身份标识，用于访问 Kubernetes API。

### 用途

- **API 访问**：Pod 通过 ServiceAccount 访问 Kubernetes API
- **权限控制**：通过 RBAC 控制 ServiceAccount 的权限
- **Secret 挂载**：自动挂载 Token Secret 到 Pod

### 示例

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: my-service-account
---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: my-role
rules:
- apiGroups: [""]
  resources: ["pods"]
  verbs: ["get", "list", "watch"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: my-role-binding
subjects:
- kind: ServiceAccount
  name: my-service-account
roleRef:
  kind: Role
  name: my-role
  apiGroup: rbac.authorization.k8s.io
```

## 17. Label 和 Selector

### 定义

Label 是键值对，用于标识和选择资源对象。Selector 用于选择带有特定 Label 的资源。

### 示例

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
  labels:
    app: nginx
    env: prod
---
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
spec:
  selector:
    app: nginx  # 选择带有 app=nginx 标签的 Pod
  ports:
  - port: 80
    targetPort: 80
```

### 使用 Selector

```bash
# 查询带有特定标签的 Pod
kubectl get pods -l app=nginx

# 查询带有多个标签的 Pod
kubectl get pods -l app=nginx,env=prod
```

## 18. Annotation

### 定义

Annotation 是键值对，用于存储任意非标识性数据。

### 用途

- **元数据**：存储资源的元数据
- **配置**：存储配置信息
- **监控**：存储监控信息

### 示例

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
  annotations:
    description: "This is a nginx pod"
    monitoring: "enabled"
```

## 19. ResourceQuota

### 定义

ResourceQuota 用于限制 Namespace 的资源使用。

### 示例

```yaml
apiVersion: v1
kind: ResourceQuota
metadata:
  name: compute-resources
  namespace: dev
spec:
  hard:
    requests.cpu: "4"
    requests.memory: 8Gi
    limits.cpu: "8"
    limits.memory: 16Gi
```

## 20. LimitRange

### 定义

LimitRange 用于限制 Pod 或容器的资源使用。

### 示例

```yaml
apiVersion: v1
kind: LimitRange
metadata:
  name: cpu-limit-range
  namespace: dev
spec:
  limits:
  - default:
      cpu: 1
      memory: 512Mi
    defaultRequest:
      cpu: 0.5
      memory: 256Mi
    type: Container
```

## 总结

Kubernetes 的核心概念包括：

1. **Pod**：最小部署单元
2. **Node**：工作节点
3. **Namespace**：资源隔离
4. **Service**：服务发现和负载均衡
5. **Volume**：存储管理
6. **PersistentVolume (PV)**：持久化存储
7. **PersistentVolumeClaim (PVC)**：存储声明
8. **ConfigMap**：配置管理
9. **Secret**：敏感信息管理
10. **Deployment**：无状态应用部署
11. **StatefulSet**：有状态应用部署
12. **DaemonSet**：守护进程集
13. **Job**：一次性任务
14. **CronJob**：定时任务
15. **Ingress**：集群入口
16. **ServiceAccount**：身份标识
17. **Label 和 Selector**：标签和选择器
18. **Annotation**：注解
19. **ResourceQuota**：资源配额
20. **LimitRange**：资源限制

理解这些核心概念是掌握 Kubernetes 的基础，它们共同构成了 Kubernetes 的强大功能。