# kube-apiserver和kube-scheduler的作用是什么？

## 概述

kube-apiserver 和 kube-scheduler 是 Kubernetes Master 节点的两个核心组件，它们在 Kubernetes 集群中扮演着重要的角色。kube-apiserver 是 Kubernetes 的 API 服务器，是集群的统一入口；kube-scheduler 是 Kubernetes 的调度器，负责将 Pod 调度到合适的节点上。

## kube-apiserver

### 定义

kube-apiserver 是 Kubernetes 的 API 服务器，是集群的统一入口，所有的组件都通过 kube-apiserver 与集群交互。

### 主要功能

#### 1. 提供 REST API

kube-apiserver 提供了 RESTful API，用于管理 Kubernetes 集群中的所有资源对象。

**支持的 API 版本**：
- **core/v1**：核心 API，包括 Pod、Service、Node 等
- **apps/v1**：应用 API，包括 Deployment、StatefulSet、DaemonSet 等
- **batch/v1**：批处理 API，包括 Job、CronJob 等
- **networking.k8s.io/v1**：网络 API，包括 Ingress、NetworkPolicy 等
- **storage.k8s.io/v1**：存储 API，包括 StorageClass、PersistentVolume 等

**示例**：
```bash
# 获取所有 Pod
curl -k https://<api-server>:6443/api/v1/namespaces/default/pods

# 获取所有 Deployment
curl -k https://<api-server>:6443/apis/apps/v1/namespaces/default/deployments

# 创建 Pod
curl -k -X POST https://<api-server>:6443/api/v1/namespaces/default/pods \
  -H "Content-Type: application/json" \
  -d '{
    "apiVersion": "v1",
    "kind": "Pod",
    "metadata": {
      "name": "nginx-pod"
    },
    "spec": {
      "containers": [{
        "name": "nginx",
        "image": "nginx:1.14.2"
      }]
    }
  }'
```

#### 2. 认证和授权

kube-apiserver 提供了多种认证和授权机制，确保集群的安全性。

**认证方式**：
- **Token 认证**：使用静态 Token 文件进行认证
- **证书认证**：使用客户端证书进行认证
- **OIDC 认证**：使用 OpenID Connect 进行认证
- **Basic Auth**：使用用户名和密码进行认证
- **ServiceAccount**：使用 ServiceAccount Token 进行认证

**授权方式**：
- **RBAC**：基于角色的访问控制
- **ABAC**：基于属性的访问控制
- **Node**：节点授权
- **Webhook**：Webhook 授权

**示例**：
```yaml
# RBAC 配置
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

#### 3. 准入控制

kube-apiserver 提供了准入控制机制，可以在资源创建或更新之前进行验证和修改。

**准入控制器**：
- **NamespaceLifecycle**：管理 Namespace 的生命周期
- **LimitRanger**：限制资源使用
- **ResourceQuota**：资源配额
- **ServiceAccount**：自动创建 ServiceAccount
- **PodSecurityPolicy**：Pod 安全策略
- **MutatingAdmissionWebhook**：修改资源的 Webhook
- **ValidatingAdmissionWebhook**：验证资源的 Webhook

**示例**：
```yaml
# 准入 Webhook 配置
apiVersion: admissionregistration.k8s.io/v1
kind: ValidatingWebhookConfiguration
metadata:
  name: my-webhook
webhooks:
- name: my-webhook.example.com
  rules:
  - apiGroups: [""]
    apiVersions: ["v1"]
    operations: ["CREATE", "UPDATE"]
    resources: ["pods"]
  clientConfig:
    service:
      name: my-webhook-service
      namespace: default
      path: /validate
  admissionReviewVersions: ["v1"]
```

#### 4. 数据验证

kube-apiserver 对所有请求进行数据验证，确保数据的正确性。

**验证内容**：
- **API 版本**：验证 API 版本是否正确
- **资源类型**：验证资源类型是否正确
- **字段验证**：验证字段是否正确
- **必填字段**：验证必填字段是否存在
- **字段类型**：验证字段类型是否正确

#### 5. 数据存储

kube-apiserver 将所有数据存储到 etcd 中，etcd 是 Kubernetes 的键值数据库。

**存储的数据**：
- **Pod**：Pod 的配置和状态
- **Service**：Service 的配置和状态
- **Deployment**：Deployment 的配置和状态
- **ConfigMap**：ConfigMap 的配置
- **Secret**：Secret 的配置
- **其他资源**：所有 Kubernetes 资源对象

### kube-apiserver 的架构

```
┌─────────────────────────────────────────────────────────────┐
│                    kube-apiserver                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   认证层     │  │   授权层     │  │  准入控制层   │  │
│  │              │  │              │  │              │  │
│  │ - Token      │  │ - RBAC       │  │ - Namespace  │  │
│  │ - Certificate│  │ - ABAC       │  │ - LimitRange │  │
│  │ - OIDC       │  │ - Node       │  │ - Resource   │  │
│  │ - Basic Auth │  │ - Webhook    │  │   Quota      │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  │
│         │                 │                 │              │
│         └─────────────────┴─────────────────┘              │
│                           │                                 │
│                    ┌──────▼──────┐                          │
│                    │  数据验证层  │                          │
│                    │             │                          │
│                    │ - API 版本  │                          │
│                    │ - 资源类型  │                          │
│                    │ - 字段验证  │                          │
│                    └──────┬──────┘                          │
│                           │                                 │
│                    ┌──────▼──────┐                          │
│                    │  数据存储层  │                          │
│                    │             │                          │
│                    │   etcd 集群 │                          │
│                    └─────────────┘                          │
└─────────────────────────────────────────────────────────────┘
```

### kube-apiserver 的配置

```yaml
# kube-apiserver 配置
apiVersion: v1
kind: Pod
metadata:
  name: kube-apiserver
  namespace: kube-system
spec:
  containers:
  - name: kube-apiserver
    image: k8s.gcr.io/kube-apiserver:v1.23.0
    command:
    - kube-apiserver
    - --advertise-address=192.168.1.100
    - --allow-privileged=true
    - --authorization-mode=Node,RBAC
    - --client-ca-file=/etc/kubernetes/pki/ca.crt
    - --enable-admission-plugins=NodeRestriction
    - --enable-bootstrap-token-auth=true
    - --etcd-servers=https://127.0.0.1:2379
    - --insecure-port=0
    - --kubelet-client-certificate=/etc/kubernetes/pki/apiserver-kubelet-client.crt
    - --kubelet-client-key=/etc/kubernetes/pki/apiserver-kubelet-client.key
    - --kubelet-preferred-address-types=InternalIP,ExternalIP,Hostname
    - --proxy-client-cert-file=/etc/kubernetes/pki/front-proxy-client.crt
    - --proxy-client-key-file=/etc/kubernetes/pki/front-proxy-client.key
    - --requestheader-allowed-names=front-proxy-client
    - --requestheader-client-ca-file=/etc/kubernetes/pki/front-proxy-ca.crt
    - --requestheader-extra-headers-prefix=X-Remote-Extra-
    - --requestheader-group-headers=X-Remote-Group
    - --requestheader-username-headers=X-Remote-User
    - --secure-port=6443
    - --service-account-issuer=https://kubernetes.default.svc.cluster.local
    - --service-account-key-file=/etc/kubernetes/pki/sa.pub
    - --service-account-signing-key-file=/etc/kubernetes/pki/sa.key
    - --service-cluster-ip-range=10.96.0.0/12
    - --tls-cert-file=/etc/kubernetes/pki/apiserver.crt
    - --tls-private-key-file=/etc/kubernetes/pki/apiserver.key
    volumeMounts:
    - name: etcd-certs
      mountPath: /etc/kubernetes/pki/etcd
      readOnly: true
    - name: ca-certs
      mountPath: /etc/kubernetes/pki
      readOnly: true
  volumes:
  - name: etcd-certs
    hostPath:
      path: /etc/kubernetes/pki/etcd
      type: DirectoryOrCreate
  - name: ca-certs
    hostPath:
      path: /etc/kubernetes/pki
      type: DirectoryOrCreate
```

## kube-scheduler

### 定义

kube-scheduler 是 Kubernetes 的调度器，负责将 Pod 调度到合适的节点上。

### 主要功能

#### 1. 监听未调度的 Pod

kube-scheduler 监听 API Server，获取未调度的 Pod。

**监听机制**：
- **Watch 机制**：使用 Kubernetes 的 Watch 机制监听 Pod 变化
- **Informer 机制**：使用 Informer 机制缓存 Pod 状态
- **队列机制**：将未调度的 Pod 放入调度队列

#### 2. 过滤节点

kube-scheduler 根据调度策略过滤不符合条件的节点（预选）。

**过滤条件**：
- **资源限制**：节点是否有足够的 CPU、内存、存储资源
- **节点选择器**：Pod 是否满足节点的 nodeSelector
- **节点亲和性**：Pod 是否满足节点的 nodeAffinity
- **污点和容忍度**：Pod 是否容忍节点的污点
- **Pod 亲和性和反亲和性**：Pod 是否满足 Pod 的亲和性和反亲和性
- **端口冲突**：Pod 的端口是否与节点上的端口冲突
- **卷绑定**：Pod 的卷是否可以绑定到节点

**示例**：
```yaml
# Pod 配置
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
spec:
  nodeSelector:
    disktype: ssd  # 节点选择器
  affinity:
    nodeAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
        nodeSelectorTerms:
        - matchExpressions:
          - key: kubernetes.io/e2e-az-name
            operator: In
            values:
            - e2e-az1
            - e2e-az2
      preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 1
        preference:
          matchExpressions:
          - key: another-node-label-key
            operator: In
            values:
            - another-node-label-value
  tolerations:
  - key: "key"
    operator: "Equal"
    value: "value"
    effect: "NoSchedule"
  containers:
  - name: nginx
    image: nginx:1.14.2
```

#### 3. 节点打分

kube-scheduler 对符合条件的节点进行打分（优选）。

**打分策略**：
- **资源利用率**：节点资源利用率越低，得分越高
- **镜像本地性**：节点上是否有 Pod 所需的镜像
- **节点分布**：Pod 是否均匀分布在节点上
- **节点标签**：节点的标签是否符合 Pod 的要求
- **节点污点**：节点的污点是否符合 Pod 的容忍度
- **Pod 亲和性**：Pod 是否满足亲和性要求

**示例**：
```yaml
# 调度器配置
apiVersion: kubescheduler.config.k8s.io/v1beta1
kind: KubeSchedulerConfiguration
profiles:
- schedulerName: default-scheduler
  plugins:
    queueSort:
      enabled:
      - Name: PrioritySort
    preFilter:
      enabled:
      - Name: NodeResourcesFit
      - Name: NodePorts
      - Name: NodeAffinity
      - Name: TaintToleration
      - Name: InterPodAffinity
    filter:
      enabled:
      - Name: NodeUnschedulable
      - Name: NodeResourcesFit
      - Name: NodePorts
      - Name: NodeAffinity
      - Name: TaintToleration
      - Name: InterPodAffinity
    postFilter:
      enabled:
      - Name: DefaultPreemption
    preScore:
      enabled:
      - Name: InterPodAffinity
      - Name: NodeResourcesFit
      - Name: PodTopologySpread
    score:
      enabled:
      - Name: NodeResourcesFit
      - Name: InterPodAffinity
      - Name: PodTopologySpread
    reserve:
      enabled:
      - Name: VolumeBinding
    permit:
      enabled:
      - Name: VolumeBinding
    preBind:
      enabled:
      - Name: VolumeBinding
    bind:
      enabled:
      - Name: DefaultBinder
    postBind:
      enabled:
      - Name: DefaultBinder
  pluginConfig:
  - name: NodeResourcesFit
    args:
      resources:
      - name: cpu
        weight: 1
      - name: memory
        weight: 1
```

#### 4. 选择节点

kube-scheduler 选择得分最高的节点，将 Pod 绑定到节点。

**绑定流程**：
1. 选择得分最高的节点
2. 将 Pod 绑定到节点
3. 更新 Pod 的状态
4. 通知 kubelet 启动 Pod

#### 5. 自定义调度器

kube-scheduler 支持自定义调度器，可以根据业务需求定制调度策略。

**自定义调度器示例**：
```yaml
# 自定义调度器
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
spec:
  schedulerName: my-custom-scheduler  # 指定自定义调度器
  containers:
  - name: nginx
    image: nginx:1.14.2
```

### kube-scheduler 的架构

```
┌─────────────────────────────────────────────────────────────┐
│                    kube-scheduler                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  监听 Pod    │  │  过滤节点    │  │  节点打分    │  │
│  │              │  │              │  │              │  │
│  │ - Watch      │  │ - 资源限制  │  │ - 资源利用率 │  │
│  │ - Informer   │  │ - 节点选择  │  │ - 镜像本地性 │  │
│  │ - 队列      │  │ - 亲和性    │  │ - 节点分布  │  │
│  │              │  │ - 污点容忍  │  │ - 节点标签  │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  │
│         │                 │                 │              │
│         └─────────────────┴─────────────────┘              │
│                           │                                 │
│                    ┌──────▼──────┐                          │
│                    │  选择节点    │                          │
│                    │             │                          │
│                    │ - 选择最高分 │                          │
│                    │   的节点     │                          │
│                    │ - 绑定 Pod  │                          │
│                    │ - 更新状态  │                          │
│                    └──────┬──────┘                          │
│                           │                                 │
│                    ┌──────▼──────┐                          │
│                    │  通知 kubelet│                          │
│                    │             │                          │
│                    │ - 启动 Pod  │                          │
│                    │ - 监控状态  │                          │
│                    └─────────────┘                          │
└─────────────────────────────────────────────────────────────┘
```

### kube-scheduler 的配置

```yaml
# kube-scheduler 配置
apiVersion: v1
kind: Pod
metadata:
  name: kube-scheduler
  namespace: kube-system
spec:
  containers:
  - name: kube-scheduler
    image: k8s.gcr.io/kube-scheduler:v1.23.0
    command:
    - kube-scheduler
    - --authentication-kubeconfig=/etc/kubernetes/scheduler.conf
    - --authorization-kubeconfig=/etc/kubernetes/scheduler.conf
    - --bind-address=0.0.0.0
    - --kubeconfig=/etc/kubernetes/scheduler.conf
    - --leader-elect=true
    - --port=10259
    volumeMounts:
    - name: kubeconfig
      mountPath: /etc/kubernetes/scheduler.conf
      readOnly: true
  volumes:
  - name: kubeconfig
    hostPath:
      path: /etc/kubernetes/scheduler.conf
      type: FileOrCreate
```

## kube-apiserver 和 kube-scheduler 的交互

### 调度流程

```
1. 用户提交 Pod 定义
   ↓
2. kubectl 调用 kube-apiserver 的 REST API
   ↓
3. kube-apiserver 验证请求，将 Pod 定义存储到 etcd
   ↓
4. kube-scheduler 监听到未调度的 Pod
   ↓
5. kube-scheduler 过滤不符合条件的节点（预选）
   ↓
6. kube-scheduler 对符合条件的节点打分（优选）
   ↓
7. kube-scheduler 选择得分最高的节点
   ↓
8. kube-scheduler 将 Pod 绑定到节点
   ↓
9. kube-scheduler 更新 Pod 的状态到 kube-apiserver
   ↓
10. kube-apiserver 将状态更新到 etcd
   ↓
11. kubelet 监听到分配到本节点的 Pod
   ↓
12. kubelet 调用容器运行时创建容器
   ↓
13. kubelet 定期上报 Pod 状态到 kube-apiserver
   ↓
14. kube-apiserver 将状态更新到 etcd
```

### 交互示例

```bash
# 1. 创建 Pod
kubectl apply -f pod.yaml

# 2. kube-apiserver 接收请求
# 3. kube-apiserver 验证请求
# 4. kube-apiserver 将 Pod 存储到 etcd

# 5. kube-scheduler 监听到未调度的 Pod
# 6. kube-scheduler 过滤节点
# 7. kube-scheduler 节点打分
# 8. kube-scheduler 选择节点
# 9. kube-scheduler 绑定 Pod 到节点

# 10. kubelet 监听到分配到本节点的 Pod
# 11. kubelet 创建容器
# 12. kubelet 上报状态

# 13. 查看 Pod 状态
kubectl get pods
kubectl describe pod <pod-name>
```

## 总结

kube-apiserver 和 kube-scheduler 是 Kubernetes Master 节点的两个核心组件。

**kube-apiserver 的作用**：
- 提供 REST API：集群的统一入口
- 认证和授权：确保集群的安全性
- 准入控制：在资源创建或更新之前进行验证和修改
- 数据验证：验证数据的正确性
- 数据存储：将数据存储到 etcd

**kube-scheduler 的作用**：
- 监听未调度的 Pod：获取需要调度的 Pod
- 过滤节点：根据调度策略过滤不符合条件的节点
- 节点打分：对符合条件的节点进行打分
- 选择节点：选择得分最高的节点
- 绑定 Pod：将 Pod 绑定到节点

**kube-apiserver 和 kube-scheduler 的交互**：
- kube-apiserver 接收 Pod 创建请求
- kube-scheduler 监听未调度的 Pod
- kube-scheduler 选择合适的节点
- kube-scheduler 将 Pod 绑定到节点
- kube-apiserver 更新 Pod 的状态

kube-apiserver 和 kube-scheduler 共同协作，实现了 Kubernetes 的调度功能，确保 Pod 能够被正确地调度到合适的节点上。