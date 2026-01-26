# 如何启动Docker容器？参数含义？如何进入Docker后台模式？有什么区别？

## 概述

启动 Docker 容器是使用 Docker 的基本操作之一。Docker 提供了多种启动容器的方式，包括前台模式、后台模式、交互式模式等。理解如何启动 Docker 容器、参数含义、后台模式等对于使用 Docker 非常重要。

## 启动 Docker 容器的基本方法

### 1. docker run 命令

**功能**：创建并启动一个新的容器。

**语法**：
```bash
docker run [OPTIONS] IMAGE [COMMAND] [ARG...]
```

**示例**：
```bash
# 启动容器
docker run nginx

# 启动容器并指定名称
docker run --name webserver nginx

# 启动容器并映射端口
docker run -p 80:80 nginx
```

### 2. docker start 命令

**功能**：启动一个或多个已停止的容器。

**语法**：
```bash
docker start [OPTIONS] CONTAINER [CONTAINER...]
```

**示例**：
```bash
# 启动容器
docker start webserver

# 启动多个容器
docker start webserver database
```

## docker run 常用参数

### 1. 后台运行（-d, --detach）

**功能**：在后台运行容器。

**示例**：
```bash
# 后台运行容器
docker run -d nginx

# 后台运行容器并指定名称
docker run -d --name webserver nginx
```

### 2. 指定容器名称（--name）

**功能**：为容器指定一个名称。

**示例**：
```bash
# 指定容器名称
docker run --name webserver nginx

# 后台运行容器并指定名称
docker run -d --name webserver nginx
```

### 3. 端口映射（-p, --publish）

**功能**：将容器端口映射到主机端口。

**语法**：
```bash
-p <host_port>:<container_port>
-p <host_port>:<container_port>/<protocol>
```

**示例**：
```bash
# 映射容器 80 端口到主机 80 端口
docker run -p 80:80 nginx

# 映射容器 80 端口到主机 8080 端口
docker run -p 8080:80 nginx

# 映射容器 443 端口到主机 8443 端口
docker run -p 8443:443 nginx

# 映射容器 80 端口到主机 80 端口（TCP）
docker run -p 80:80/tcp nginx

# 映射容器 53 端口到主机 53 端口（UDP）
docker run -p 53:53/udp bind9

# 随机映射容器 80 端口到主机端口
docker run -p 80 nginx
```

### 4. 挂载卷（-v, --volume）

**功能**：将主机目录或数据卷挂载到容器中。

**语法**：
```bash
-v <host_path>:<container_path>[:<options>]
-v <volume_name>:<container_path>[:<options>]
```

**常用选项**：
- `ro`：只读
- `rw`：读写（默认）
- `z`：重新标记 SELinux 标签
- `Z`：重新标记 SELinux 标签（递归）

**示例**：
```bash
# 挂载主机目录到容器
docker run -v /host/path:/container/path nginx

# 挂载数据卷到容器
docker run -v myvolume:/data nginx

# 只读挂载
docker run -v /host/path:/container/path:ro nginx

# 重新标记 SELinux 标签
docker run -v /host/path:/container/path:z nginx

# 挂载多个目录
docker run -v /host/path1:/container/path1 -v /host/path2:/container/path2 nginx
```

### 5. 设置环境变量（-e, --env）

**功能**：设置容器的环境变量。

**语法**：
```bash
-e <key>=<value>
-e <key>
--env-file <file>
```

**示例**：
```bash
# 设置环境变量
docker run -e APP_ENV=production nginx

# 从环境文件读取环境变量
docker run --env-file .env nginx

# 设置多个环境变量
docker run -e APP_ENV=production -e DB_HOST=localhost nginx
```

### 6. 限制资源（--cpus、--memory）

**功能**：限制容器的 CPU 和内存使用。

**示例**：
```bash
# 限制 CPU 使用
docker run --cpus="1.0" nginx

# 限制内存使用
docker run --memory="512m" nginx

# 同时限制 CPU 和内存
docker run --cpus="1.0" --memory="512m" nginx

# 限制 CPU 使用（百分比）
docker run --cpus="0.5" nginx

# 限制内存使用（字节）
docker run --memory="536870912" nginx
```

### 7. 连接到网络（--network）

**功能**：将容器连接到指定的网络。

**示例**：
```bash
# 连接到默认的 bridge 网络
docker run --network bridge nginx

# 连接到 host 网络
docker run --network host nginx

# 连接到 none 网络
docker run --network none nginx

# 连接到自定义网络
docker run --network mynetwork nginx

# 连接到多个网络
docker run --network mynetwork1 --network mynetwork2 nginx
```

### 8. 容器退出时自动删除（--rm）

**功能**：容器退出时自动删除容器。

**示例**：
```bash
# 容器退出时自动删除
docker run --rm nginx

# 容器退出时自动删除并指定名称
docker run --rm --name webserver nginx
```

### 9. 容器退出时自动重启（--restart）

**功能**：容器退出时自动重启。

**选项**：
- `no`：不自动重启（默认）
- `on-failure[:max-retries]`：容器非正常退出时重启
- `always`：总是重启
- `unless-stopped`：总是重启，除非容器被明确停止

**示例**：
```bash
# 容器非正常退出时重启
docker run --restart on-failure nginx

# 容器非正常退出时重启（最多 3 次）
docker run --restart on-failure:3 nginx

# 总是重启
docker run --restart always nginx

# 总是重启，除非容器被明确停止
docker run --restart unless-stopped nginx
```

### 10. 工作目录（-w, --workdir）

**功能**：设置容器的工作目录。

**示例**：
```bash
# 设置工作目录
docker run -w /app nginx

# 设置工作目录并执行命令
docker run -w /app nginx pwd
```

### 11. 用户（-u, --user）

**功能**：指定运行容器的用户。

**示例**：
```bash
# 指定用户
docker run -u nginx nginx

# 指定用户和组
docker run -u nginx:nginx nginx

# 指定 UID 和 GID
docker run -u 1000:1000 nginx
```

### 12. 交互式模式（-i, --interactive）

**功能**：保持 STDIN 打开。

**示例**：
```bash
# 交互式运行容器
docker run -i ubuntu

# 交互式运行容器并指定命令
docker run -i ubuntu /bin/bash
```

### 13. 伪终端（-t, --tty）

**功能**：分配一个伪终端。

**示例**：
```bash
# 分配伪终端
docker run -t ubuntu

# 交互式运行容器并分配伪终端
docker run -it ubuntu /bin/bash
```

## 前台模式和后台模式

### 1. 前台模式

**定义**：容器在前台运行，容器的 STDOUT 和 STDERR 直接连接到终端。

**特点**：
- 容器的输出直接显示在终端上
- 容器退出时终端返回
- 适合调试和开发

**示例**：
```bash
# 前台运行容器
docker run nginx

# 前台运行容器并指定命令
docker run ubuntu /bin/bash
```

### 2. 后台模式

**定义**：容器在后台运行，容器的 STDOUT 和 STDERR 不连接到终端。

**特点**：
- 容器的输出不直接显示在终端上
- 容器退出时终端不返回
- 适合生产环境

**示例**：
```bash
# 后台运行容器
docker run -d nginx

# 后台运行容器并指定名称
docker run -d --name webserver nginx
```

### 3. 前台模式和后台模式的区别

| 特性 | 前台模式 | 后台模式 |
|------|----------|----------|
| 输出显示 | 直接显示在终端上 | 不直接显示在终端上 |
| 终端返回 | 容器退出时返回 | 容器退出时不返回 |
| 适用场景 | 调试和开发 | 生产环境 |
| 参数 | 无 | `-d, --detach` |

**示例**：
```bash
# 前台运行容器
docker run nginx
# 输出直接显示在终端上
# 容器退出时终端返回

# 后台运行容器
docker run -d nginx
# 输出不直接显示在终端上
# 容器退出时终端不返回
```

## 交互式模式

### 1. 交互式前台模式

**定义**：容器在前台运行，并且可以与容器交互。

**特点**：
- 可以与容器交互
- 容器的 STDOUT 和 STDERR 直接连接到终端
- 容器的 STDIN 连接到终端

**示例**：
```bash
# 交互式前台运行容器
docker run -it ubuntu /bin/bash

# 交互式前台运行容器并指定名称
docker run -it --name ubuntu-container ubuntu /bin/bash
```

### 2. 交互式后台模式

**定义**：容器在后台运行，但可以进入容器进行交互。

**特点**：
- 容器在后台运行
- 可以进入容器进行交互
- 适合生产环境

**示例**：
```bash
# 后台运行容器
docker run -d --name webserver nginx

# 进入容器进行交互
docker exec -it webserver /bin/bash
```

### 3. 交互式模式的区别

| 特性 | 交互式前台模式 | 交互式后台模式 |
|------|--------------|--------------|
| 运行方式 | 前台运行 | 后台运行 |
| 交互方式 | 直接交互 | 需要进入容器 |
| 适用场景 | 调试和开发 | 生产环境 |
| 参数 | `-it` | `-d` + `docker exec -it` |

**示例**：
```bash
# 交互式前台运行容器
docker run -it ubuntu /bin/bash
# 直接与容器交互

# 交互式后台运行容器
docker run -d --name webserver nginx
docker exec -it webserver /bin/bash
# 需要进入容器进行交互
```

## 进入 Docker 容器

### 1. docker exec 命令

**功能**：在运行的容器中执行命令。

**语法**：
```bash
docker exec [OPTIONS] CONTAINER COMMAND [ARG...]
```

**常用选项**：
- `-d, --detach`：后台运行命令
- `-e, --env`：设置环境变量
- `-i, --interactive`：保持 STDIN 打开
- `-t, --tty`：分配一个伪终端
- `-u, --user`：指定用户名或 UID
- `-w, --workdir`：指定工作目录

**示例**：
```bash
# 在容器中执行命令
docker exec webserver ls

# 在容器中执行交互式命令
docker exec -it webserver /bin/bash

# 在容器中执行后台命令
docker exec -d webserver sleep 10

# 指定用户执行命令
docker exec -u root webserver ls

# 指定工作目录执行命令
docker exec -w /tmp webserver ls
```

### 2. docker attach 命令

**功能**：连接到运行中的容器。

**语法**：
```bash
docker attach [OPTIONS] CONTAINER
```

**常用选项**：
- `--detach-keys`：覆盖分离容器的键序列
- `--no-stdin`：不附加 STDIN
- `--sig-proxy`：代理所有接收的信号到进程

**示例**：
```bash
# 连接到容器
docker attach webserver

# 连接到容器并指定分离键序列
docker attach --detach-keys="ctrl-p,ctrl-q" webserver
```

### 3. docker exec 和 docker attach 的区别

| 特性 | docker exec | docker attach |
|------|------------|--------------|
| 功能 | 在容器中执行命令 | 连接到容器 |
| 是否启动新进程 | 是 | 否 |
| 是否可以交互 | 是 | 是 |
| 适用场景 | 执行命令、调试 | 查看容器输出 |

**示例**：
```bash
# 使用 docker exec 在容器中执行命令
docker exec -it webserver /bin/bash
# 启动新的进程

# 使用 docker attach 连接到容器
docker attach webserver
# 不启动新的进程
```

## 总结

启动 Docker 容器是使用 Docker 的基本操作之一。Docker 提供了多种启动容器的方式，包括前台模式、后台模式、交互式模式等。

**docker run 常用参数**：
- **后台运行（-d, --detach）**：在后台运行容器
- **指定容器名称（--name）**：为容器指定一个名称
- **端口映射（-p, --publish）**：将容器端口映射到主机端口
- **挂载卷（-v, --volume）**：将主机目录或数据卷挂载到容器中
- **设置环境变量（-e, --env）**：设置容器的环境变量
- **限制资源（--cpus、--memory）**：限制容器的 CPU 和内存使用
- **连接到网络（--network）**：将容器连接到指定的网络
- **容器退出时自动删除（--rm）**：容器退出时自动删除容器
- **容器退出时自动重启（--restart）**：容器退出时自动重启
- **工作目录（-w, --workdir）**：设置容器的工作目录
- **用户（-u, --user）**：指定运行容器的用户
- **交互式模式（-i, --interactive）**：保持 STDIN 打开
- **伪终端（-t, --tty）**：分配一个伪终端

**前台模式和后台模式**：
- **前台模式**：容器在前台运行，容器的 STDOUT 和 STDERR 直接连接到终端
- **后台模式**：容器在后台运行，容器的 STDOUT 和 STDERR 不连接到终端
- **区别**：输出显示、终端返回、适用场景

**交互式模式**：
- **交互式前台模式**：容器在前台运行，并且可以与容器交互
- **交互式后台模式**：容器在后台运行，但可以进入容器进行交互
- **区别**：运行方式、交互方式、适用场景

**进入 Docker 容器**：
- **docker exec 命令**：在运行的容器中执行命令
- **docker attach 命令**：连接到运行中的容器
- **区别**：是否启动新进程、是否可以交互、适用场景

通过掌握如何启动 Docker 容器、参数含义、后台模式等，可以更好地使用 Docker 进行开发、测试和部署。