# Docker容器相关操作有哪些？

## 概述

Docker 容器是运行应用程序的轻量级虚拟环境，它由镜像创建，并且可以启动、停止、删除。Docker 容器操作包括运行、查看、停止、启动、删除、进入、日志等。掌握 Docker 容器操作对于使用 Docker 非常重要。

## Docker 容器的基本概念

### 1. 什么是 Docker 容器

**定义**：Docker 容器是运行应用程序的轻量级虚拟环境，它由镜像创建，并且可以启动、停止、删除。

**特点**：
- 轻量级：比虚拟机更轻量
- 快速启动：几秒钟内启动
- 可移植：可以在任何支持 Docker 的环境中运行
- 隔离：容器之间相互隔离
- 可重复：相同的镜像产生相同的容器

**示例**：
```
Docker 容器
    ├── 应用程序
    ├── 依赖项
    ├── 运行时
    └── 可写层
```

### 2. Docker 容器的生命周期

**生命周期**：
```
创建（Created）
    ↓
运行（Running）
    ↓
暂停（Paused）
    ↓
停止（Stopped）
    ↓
删除（Removed）
```

**说明**：
- **创建（Created）**：容器已创建但未启动
- **运行（Running）**：容器正在运行
- **暂停（Paused）**：容器已暂停
- **停止（Stopped）**：容器已停止
- **删除（Removed）**：容器已删除

## Docker 容器的常用操作

### 1. 运行容器（docker run）

**功能**：创建并启动一个新的容器。

**语法**：
```bash
docker run [OPTIONS] IMAGE [COMMAND] [ARG...]
```

**常用选项**：
- `-d, --detach`：后台运行容器
- `--name`：指定容器名称
- `-p, --publish`：发布容器端口到主机
- `-v, --volume`：挂载卷
- `-e, --env`：设置环境变量
- `--rm`：容器退出时自动删除
- `--restart`：容器退出时的重启策略
- `--network`：连接到指定网络
- `--cpus`：限制 CPU 使用
- `--memory`：限制内存使用

**示例**：
```bash
# 后台运行容器
docker run -d --name webserver nginx

# 发布端口
docker run -d --name webserver -p 80:80 nginx

# 挂载卷
docker run -d --name webserver -v /data:/data nginx

# 设置环境变量
docker run -d --name webserver -e APP_ENV=production nginx

# 容器退出时自动删除
docker run --rm --name webserver nginx

# 容器退出时自动重启
docker run -d --name webserver --restart always nginx

# 连接到指定网络
docker run -d --name webserver --network mynetwork nginx

# 限制 CPU 使用
docker run -d --name webserver --cpus="1.0" nginx

# 限制内存使用
docker run -d --name webserver --memory="512m" nginx
```

### 2. 查看容器（docker ps）

**功能**：列出容器。

**语法**：
```bash
docker ps [OPTIONS]
```

**常用选项**：
- `-a, --all`：显示所有容器（包括停止的）
- `-n, --last`：显示最后创建的 n 个容器
- `-l, --latest`：显示最新创建的容器
- `--no-trunc`：不截断输出
- `-q, --quiet`：只显示容器 ID
- `-s, --size`：显示容器大小

**示例**：
```bash
# 查看运行的容器
docker ps

# 查看所有容器
docker ps -a

# 查看最后创建的 5 个容器
docker ps -n 5

# 查看最新创建的容器
docker ps -l

# 不截断输出
docker ps --no-trunc

# 只显示容器 ID
docker ps -q

# 显示容器大小
docker ps -s
```

### 3. 停止容器（docker stop）

**功能**：停止一个或多个运行中的容器。

**语法**：
```bash
docker stop [OPTIONS] CONTAINER [CONTAINER...]
```

**常用选项**：
- `-t, --time`：等待容器停止的时间（秒）

**示例**：
```bash
# 停止容器
docker stop webserver

# 停止多个容器
docker stop webserver database

# 等待容器停止 10 秒
docker stop -t 10 webserver

# 停止所有容器
docker stop $(docker ps -q)
```

### 4. 启动容器（docker start）

**功能**：启动一个或多个已停止的容器。

**语法**：
```bash
docker start [OPTIONS] CONTAINER [CONTAINER...]
```

**常用选项**：
- `-a, --attach`：连接到容器的 STDIN、STDOUT 和 STDERR
- `-i, --interactive`：保持 STDIN 打开
- `--detach-keys`：覆盖分离容器的键序列

**示例**：
```bash
# 启动容器
docker start webserver

# 启动多个容器
docker start webserver database

# 连接到容器的 STDIN、STDOUT 和 STDERR
docker start -a webserver

# 保持 STDIN 打开
docker start -i webserver
```

### 5. 重启容器（docker restart）

**功能**：重启一个或多个容器。

**语法**：
```bash
docker restart [OPTIONS] CONTAINER [CONTAINER...]
```

**常用选项**：
- `-t, --time`：等待容器停止的时间（秒）

**示例**：
```bash
# 重启容器
docker restart webserver

# 重启多个容器
docker restart webserver database

# 等待容器停止 10 秒
docker restart -t 10 webserver

# 重启所有容器
docker restart $(docker ps -q)
```

### 6. 删除容器（docker rm）

**功能**：删除一个或多个容器。

**语法**：
```bash
docker rm [OPTIONS] CONTAINER [CONTAINER...]
```

**常用选项**：
- `-f, --force`：强制删除运行中的容器
- `-l, --link`：删除指定的链接
- `-v, --volumes`：删除与容器关联的卷

**示例**：
```bash
# 删除容器
docker rm webserver

# 删除多个容器
docker rm webserver database

# 强制删除运行中的容器
docker rm -f webserver

# 删除容器及其关联的卷
docker rm -v webserver

# 删除所有已停止的容器
docker rm $(docker ps -a -q)
```

### 7. 暂停容器（docker pause）

**功能**：暂停一个或多个容器中的所有进程。

**语法**：
```bash
docker pause CONTAINER [CONTAINER...]
```

**示例**：
```bash
# 暂停容器
docker pause webserver

# 暂停多个容器
docker pause webserver database
```

### 8. 恢复容器（docker unpause）

**功能**：恢复一个或多个容器中的所有进程。

**语法**：
```bash
docker unpause CONTAINER [CONTAINER...]
```

**示例**：
```bash
# 恢复容器
docker unpause webserver

# 恢复多个容器
docker unpause webserver database
```

### 9. 杀死容器（docker kill）

**功能**：杀死一个或多个运行中的容器。

**语法**：
```bash
docker kill [OPTIONS] CONTAINER [CONTAINER...]
```

**常用选项**：
- `-s, --signal`：发送给容器的信号（默认为 SIGKILL）

**示例**：
```bash
# 杀死容器
docker kill webserver

# 杀死多个容器
docker kill webserver database

# 发送 SIGTERM 信号
docker kill -s SIGTERM webserver

# 杀死所有容器
docker kill $(docker ps -q)
```

### 10. 查看容器日志（docker logs）

**功能**：查看容器的日志。

**语法**：
```bash
docker logs [OPTIONS] CONTAINER
```

**常用选项**：
- `-f, --follow`：跟踪日志输出
- `--tail`：从日志末尾开始显示
- `-t, --timestamps`：显示时间戳
- `--details`：显示额外的详细信息
- `--since`：显示指定时间之后的日志
- `--until`：显示指定时间之前的日志

**示例**：
```bash
# 查看容器日志
docker logs webserver

# 跟踪日志输出
docker logs -f webserver

# 从日志末尾开始显示 100 行
docker logs --tail 100 webserver

# 显示时间戳
docker logs -t webserver

# 显示指定时间之后的日志
docker logs --since 2023-01-01T00:00:00 webserver

# 显示指定时间之前的日志
docker logs --until 2023-01-01T00:00:00 webserver
```

### 11. 进入容器（docker exec）

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

### 12. 查看容器详细信息（docker inspect）

**功能**：显示容器的详细信息。

**语法**：
```bash
docker inspect [OPTIONS] NAME|ID [NAME|ID...]
```

**常用选项**：
- `-f, --format`：格式化输出
- `-s, --size`：显示容器大小
- `--type`：返回指定类型的信息（container、image）

**示例**：
```bash
# 查看容器详细信息
docker inspect webserver

# 格式化输出
docker inspect -f '{{.State.Status}}' webserver

# 查看容器 IP 地址
docker inspect -f '{{.NetworkSettings.IPAddress}}' webserver

# 查看容器端口映射
docker inspect -f '{{.NetworkSettings.Ports}}' webserver
```

### 13. 查看容器资源使用情况（docker stats）

**功能**：显示容器的实时资源使用情况。

**语法**：
```bash
docker stats [OPTIONS] [CONTAINER...]
```

**常用选项**：
- `-a, --all`：显示所有容器（包括停止的）
- `--no-stream`：禁用流式更新
- `--no-trunc`：不截断输出

**示例**：
```bash
# 查看所有容器的资源使用情况
docker stats

# 查看指定容器的资源使用情况
docker stats webserver

# 禁用流式更新
docker stats --no-stream

# 不截断输出
docker stats --no-trunc
```

### 14. 查看容器进程（docker top）

**功能**：显示容器中运行的进程。

**语法**：
```bash
docker top CONTAINER [ps OPTIONS]
```

**示例**：
```bash
# 查看容器中运行的进程
docker top webserver

# 使用 ps 选项
docker top webserver aux
```

### 15. 查看容器端口映射（docker port）

**功能**：显示容器的端口映射。

**语法**：
```bash
docker port CONTAINER [PRIVATE_PORT[/PROTO]]
```

**示例**：
```bash
# 查看容器所有端口映射
docker port webserver

# 查看容器指定端口映射
docker port webserver 80
```

### 16. 查看容器文件系统更改（docker diff）

**功能**：检查容器文件系统上的文件或目录的更改。

**语法**：
```bash
docker diff CONTAINER
```

**示例**：
```bash
# 查看容器文件系统更改
docker diff webserver
```

### 17. 从容器复制文件（docker cp）

**功能**：在容器和本地文件系统之间复制文件/文件夹。

**语法**：
```bash
docker cp [OPTIONS] CONTAINER:SRC_PATH DEST_PATH|-
docker cp [OPTIONS] SRC_PATH|- CONTAINER:DEST_PATH
```

**常用选项**：
- `-a, --archive`：归档模式（复制所有 UID/GID 信息）
- `-L, --follow-link`：始终跟随符号链接
- `-p, --preserve`：保留文件的权限、所有者和时间戳

**示例**：
```bash
# 从容器复制文件到本地
docker cp webserver:/var/www/html/index.html .

# 从本地复制文件到容器
docker cp index.html webserver:/var/www/html/

# 复制目录
docker cp webserver:/var/www/html/ ./html
```

### 18. 导出容器（docker export）

**功能**：将容器的文件系统导出为 tar 归档文件。

**语法**：
```bash
docker export [OPTIONS] CONTAINER
```

**常用选项**：
- `-o, --output`：指定输出文件

**示例**：
```bash
# 导出容器
docker export webserver > webserver.tar

# 指定输出文件
docker export -o webserver.tar webserver
```

### 19. 清理容器（docker container prune）

**功能**：删除所有已停止的容器。

**语法**：
```bash
docker container prune [OPTIONS]
```

**常用选项**：
- `--filter`：应用过滤条件
- `-f, --force`：不提示确认

**示例**：
```bash
# 删除所有已停止的容器
docker container prune

# 应用过滤条件
docker container prune --filter "until=24h"

# 不提示确认
docker container prune -f
```

## Docker 容器的最佳实践

### 1. 使用有意义的容器名称

**建议**：
- 使用有意义的容器名称
- 便于管理和识别

**示例**：
```bash
# 使用有意义的容器名称
docker run -d --name nginx-webserver nginx
docker run -d --name mysql-database mysql
docker run -d --name redis-cache redis
```

### 2. 限制容器资源

**建议**：
- 限制容器的 CPU 使用
- 限制容器的内存使用
- 防止容器占用过多资源

**示例**：
```bash
# 限制容器的 CPU 使用
docker run -d --name webserver --cpus="1.0" nginx

# 限制容器的内存使用
docker run -d --name webserver --memory="512m" nginx

# 同时限制 CPU 和内存
docker run -d --name webserver --cpus="1.0" --memory="512m" nginx
```

### 3. 使用数据卷

**建议**：
- 使用数据卷持久化数据
- 使用数据卷共享数据
- 使用数据卷备份数据

**示例**：
```bash
# 使用数据卷持久化数据
docker run -d --name webserver -v /data:/data nginx

# 使用数据卷共享数据
docker run -d --name webserver1 -v /data:/data nginx
docker run -d --name webserver2 -v /data:/data nginx
```

### 4. 使用健康检查

**建议**：
- 使用健康检查监控容器状态
- 使用健康检查自动重启容器
- 使用健康检查实现负载均衡

**示例**：
```dockerfile
# 在 Dockerfile 中定义健康检查
HEALTHCHECK --interval=5m --timeout=3s \
  CMD curl -f http://localhost/ || exit 1
```

```bash
# 在运行容器时定义健康检查
docker run -d --name webserver \
  --health-cmd="curl -f http://localhost/ || exit 1" \
  --health-interval=5m \
  --health-timeout=3s \
  nginx
```

### 5. 使用日志驱动

**建议**：
- 使用日志驱动收集容器日志
- 使用日志驱动分析容器日志
- 使用日志驱动监控容器状态

**示例**：
```bash
# 使用 json-file 日志驱动（默认）
docker run -d --name webserver --log-driver json-file nginx

# 使用 syslog 日志驱动
docker run -d --name webserver --log-driver syslog nginx

# 使用 journald 日志驱动
docker run -d --name webserver --log-driver journald nginx

# 使用 fluentd 日志驱动
docker run -d --name webserver --log-driver fluentd nginx
```

## 总结

Docker 容器是运行应用程序的轻量级虚拟环境，它由镜像创建，并且可以启动、停止、删除。Docker 容器操作包括运行、查看、停止、启动、删除、进入、日志等。

**Docker 容器的常用操作**：
- **运行容器（docker run）**：创建并启动一个新的容器
- **查看容器（docker ps）**：列出容器
- **停止容器（docker stop）**：停止一个或多个运行中的容器
- **启动容器（docker start）**：启动一个或多个已停止的容器
- **重启容器（docker restart）**：重启一个或多个容器
- **删除容器（docker rm）**：删除一个或多个容器
- **暂停容器（docker pause）**：暂停一个或多个容器中的所有进程
- **恢复容器（docker unpause）**：恢复一个或多个容器中的所有进程
- **杀死容器（docker kill）**：杀死一个或多个运行中的容器
- **查看容器日志（docker logs）**：查看容器的日志
- **进入容器（docker exec）**：在运行的容器中执行命令
- **查看容器详细信息（docker inspect）**：显示容器的详细信息
- **查看容器资源使用情况（docker stats）**：显示容器的实时资源使用情况
- **查看容器进程（docker top）**：显示容器中运行的进程
- **查看容器端口映射（docker port）**：显示容器的端口映射
- **查看容器文件系统更改（docker diff）**：检查容器文件系统上的文件或目录的更改
- **从容器复制文件（docker cp）**：在容器和本地文件系统之间复制文件/文件夹
- **导出容器（docker export）**：将容器的文件系统导出为 tar 归档文件
- **清理容器（docker container prune）**：删除所有已停止的容器

**Docker 容器的最佳实践**：
- 使用有意义的容器名称
- 限制容器资源
- 使用数据卷
- 使用健康检查
- 使用日志驱动

通过掌握 Docker 容器操作，可以更好地使用 Docker 进行开发、测试和部署。