# Docker镜像相关操作有哪些？

## 概述

Docker 镜像是创建容器的只读模板，它包含了应用程序及其所有依赖项。Docker 镜像操作包括拉取、查看、构建、删除、标记、推送等。掌握 Docker 镜像操作对于使用 Docker 非常重要。

## Docker 镜像的基本概念

### 1. 什么是 Docker 镜像

**定义**：Docker 镜像是创建容器的只读模板，它包含了应用程序及其所有依赖项。

**特点**：
- 只读：镜像本身是只读的
- 分层：镜像由多个层组成
- 可复用：相同的镜像可以创建多个容器
- 可共享：镜像可以共享和分发

**示例**：
```
Docker 镜像
    ├── 应用程序
    ├── 依赖项
    ├── 运行时
    └── 操作系统
```

### 2. Docker 镜像的分层结构

**分层结构**：
```
┌─────────────────────────────────┐
│          容器层（可写）         │
├─────────────────────────────────┤
│          镜像层 3（只读）        │
├─────────────────────────────────┤
│          镜像层 2（只读）        │
├─────────────────────────────────┤
│          镜像层 1（只读）        │
└─────────────────────────────────┘
```

**说明**：
- 镜像由多个只读层组成
- 容器在镜像层之上添加一个可写层
- 相同的层可以共享

## Docker 镜像的常用操作

### 1. 拉取镜像（docker pull）

**功能**：从 Docker 仓库拉取镜像到本地。

**语法**：
```bash
docker pull [OPTIONS] NAME[:TAG|@DIGEST]
```

**常用选项**：
- `-a, --all-tags`：拉取所有标签的镜像
- `--disable-content-trust`：跳过镜像验证
- `--platform`：指定平台（linux/amd64、linux/arm64 等）

**示例**：
```bash
# 拉取最新版本的镜像
docker pull nginx

# 拉取指定版本的镜像
docker pull nginx:1.18

# 拉取所有标签的镜像
docker pull -a nginx

# 拉取指定平台的镜像
docker pull --platform linux/amd64 nginx
```

### 2. 查看镜像（docker images）

**功能**：列出本地的 Docker 镜像。

**语法**：
```bash
docker images [OPTIONS] [REPOSITORY[:TAG]]
```

**常用选项**：
- `-a, --all`：显示所有镜像（包括中间层）
- `--digests`：显示镜像的摘要
- `-f, --filter`：过滤输出
- `--format`：格式化输出
- `--no-trunc`：不截断输出
- `-q, --quiet`：只显示镜像 ID

**示例**：
```bash
# 查看所有镜像
docker images

# 查看所有镜像（包括中间层）
docker images -a

# 查看镜像的摘要
docker images --digests

# 过滤输出
docker images --filter "dangling=true"

# 格式化输出
docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}"

# 只显示镜像 ID
docker images -q
```

### 3. 构建镜像（docker build）

**功能**：根据 Dockerfile 构建 Docker 镜像。

**语法**：
```bash
docker build [OPTIONS] PATH | URL | -
```

**常用选项**：
- `-t, --tag`：为镜像指定名称和标签
- `-f, --file`：指定 Dockerfile 的路径
- `--build-arg`：设置构建时的变量
- `--no-cache`：不使用缓存构建镜像
- `--pull`：总是尝试拉取镜像的新版本
- `--rm`：构建成功后删除中间容器
- `--target`：指定构建的目标阶段

**示例**：
```bash
# 构建镜像
docker build -t myapp:1.0 .

# 指定 Dockerfile 的路径
docker build -f /path/to/Dockerfile -t myapp:1.0 .

# 设置构建时的变量
docker build --build-arg VERSION=1.0 -t myapp:1.0 .

# 不使用缓存构建镜像
docker build --no-cache -t myapp:1.0 .

# 总是尝试拉取镜像的新版本
docker build --pull -t myapp:1.0 .

# 指定构建的目标阶段
docker build --target builder -t myapp:1.0 .
```

### 4. 删除镜像（docker rmi）

**功能**：删除一个或多个 Docker 镜像。

**语法**：
```bash
docker rmi [OPTIONS] IMAGE [IMAGE...]
```

**常用选项**：
- `-f, --force`：强制删除镜像
- `--no-prune`：不删除未标记的父镜像

**示例**：
```bash
# 删除镜像
docker rmi nginx

# 删除多个镜像
docker rmi nginx mysql

# 强制删除镜像
docker rmi -f nginx

# 删除所有镜像
docker rmi $(docker images -q)

# 删除所有未使用的镜像
docker image prune
```

### 5. 标记镜像（docker tag）

**功能**：为镜像创建一个新的标签。

**语法**：
```bash
docker tag SOURCE_IMAGE[:TAG] TARGET_IMAGE[:TAG]
```

**示例**：
```bash
# 标记镜像
docker tag myapp:1.0 username/myapp:1.0

# 标记镜像为 latest
docker tag myapp:1.0 myapp:latest

# 标记镜像为多个版本
docker tag myapp:1.0 username/myapp:1.0
docker tag myapp:1.0 username/myapp:latest
```

### 6. 推送镜像（docker push）

**功能**：将镜像推送到 Docker 仓库。

**语法**：
```bash
docker push [OPTIONS] NAME[:TAG]
```

**常用选项**：
- `--disable-content-trust`：跳过镜像验证
- `--quiet`：抑制详细输出

**示例**：
```bash
# 推送镜像到 Docker Hub
docker push username/myapp:1.0

# 推送镜像的所有标签
docker push username/myapp

# 推送镜像到私有仓库
docker push registry.example.com/username/myapp:1.0
```

### 7. 搜索镜像（docker search）

**功能**：在 Docker Hub 上搜索镜像。

**语法**：
```bash
docker search [OPTIONS] TERM
```

**常用选项**：
- `-f, --filter`：过滤输出
- `--limit`：限制结果数量
- `--no-trunc`：不截断输出

**示例**：
```bash
# 搜索镜像
docker search nginx

# 过滤输出
docker search --filter "is-official=true" nginx

# 限制结果数量
docker search --limit 10 nginx

# 不截断输出
docker search --no-trunc nginx
```

### 8. 查看镜像历史（docker history）

**功能**：显示镜像的历史记录。

**语法**：
```bash
docker history [OPTIONS] IMAGE
```

**常用选项**：
- `--no-trunc`：不截断输出
- `-q, --quiet`：只显示镜像 ID

**示例**：
```bash
# 查看镜像历史
docker history nginx

# 不截断输出
docker history --no-trunc nginx

# 只显示镜像 ID
docker history -q nginx
```

### 9. 查看镜像详细信息（docker inspect）

**功能**：显示镜像的详细信息。

**语法**：
```bash
docker inspect [OPTIONS] IMAGE|CONTAINER [IMAGE|CONTAINER...]
```

**常用选项**：
- `-f, --format`：格式化输出
- `--type`：返回指定类型的信息（image、container）

**示例**：
```bash
# 查看镜像详细信息
docker inspect nginx

# 格式化输出
docker inspect -f '{{.Config.Image}}' nginx

# 查看镜像的层
docker inspect -f '{{.RootFS.Layers}}' nginx
```

### 10. 导出镜像（docker save）

**功能**：将镜像保存为 tar 文件。

**语法**：
```bash
docker save [OPTIONS] IMAGE [IMAGE...]
```

**常用选项**：
- `-o, --output`：指定输出文件

**示例**：
```bash
# 导出镜像
docker save -o nginx.tar nginx

# 导出多个镜像
docker save -o images.tar nginx mysql

# 导出镜像到标准输出
docker save nginx > nginx.tar
```

### 11. 导入镜像（docker load）

**功能**：从 tar 文件加载镜像。

**语法**：
```bash
docker load [OPTIONS]
```

**常用选项**：
- `-i, --input`：指定输入文件
- `-q, --quiet`：抑制详细输出

**示例**：
```bash
# 从文件加载镜像
docker load -i nginx.tar

# 从标准输入加载镜像
docker load < nginx.tar

# 抑制详细输出
docker load -q -i nginx.tar
```

### 12. 清理镜像（docker image prune）

**功能**：删除未使用的镜像。

**语法**：
```bash
docker image prune [OPTIONS]
```

**常用选项**：
- `-a, --all`：删除所有未使用的镜像
- `--filter`：应用过滤条件
- `-f, --force`：不提示确认

**示例**：
```bash
# 删除未使用的镜像
docker image prune

# 删除所有未使用的镜像
docker image prune -a

# 应用过滤条件
docker image prune --filter "until=24h"

# 不提示确认
docker image prune -f
```

## Dockerfile 的常用指令

### 1. FROM

**功能**：指定基础镜像。

**语法**：
```dockerfile
FROM <image>[:<tag>] [AS <name>]
```

**示例**：
```dockerfile
FROM ubuntu:20.04

FROM nginx:1.18 AS builder
```

### 2. RUN

**功能**：执行命令。

**语法**：
```dockerfile
RUN <command>
RUN ["executable", "param1", "param2"]
```

**示例**：
```dockerfile
RUN apt-get update && apt-get install -y nginx

RUN ["/bin/bash", "-c", "echo hello"]
```

### 3. COPY

**功能**：复制文件。

**语法**：
```dockerfile
COPY [--chown=<user>:<group>] <src>... <dest>
COPY [--chown=<user>:<group>] ["<src>",... "<dest>"]
```

**示例**：
```dockerfile
COPY index.html /var/www/html/

COPY --chown=www-data:www-data index.html /var/www/html/
```

### 4. ADD

**功能**：添加文件。

**语法**：
```dockerfile
ADD [--chown=<user>:<group>] <src>... <dest>
ADD [--chown=<user>:<group>] ["<src>",... "<dest>"]
```

**示例**：
```dockerfile
ADD https://example.com/file.tar.gz /tmp/

ADD --chown=www-data:www-data file.tar.gz /tmp/
```

### 5. CMD

**功能**：指定容器启动时执行的命令。

**语法**：
```dockerfile
CMD ["executable","param1","param2"]
CMD command param1 param2
CMD ["param1","param2"]
```

**示例**：
```dockerfile
CMD ["nginx", "-g", "daemon off;"]

CMD nginx -g "daemon off;"
```

### 6. ENTRYPOINT

**功能**：指定容器入口点。

**语法**：
```dockerfile
ENTRYPOINT ["executable", "param1", "param2"]
ENTRYPOINT command param1 param2
```

**示例**：
```dockerfile
ENTRYPOINT ["nginx", "-g", "daemon off;"]

ENTRYPOINT nginx -g "daemon off;"
```

### 7. ENV

**功能**：设置环境变量。

**语法**：
```dockerfile
ENV <key>=<value> ...
ENV <key> <value>
```

**示例**：
```dockerfile
ENV APP_ENV=production
ENV PATH="/usr/local/bin:${PATH}"
```

### 8. EXPOSE

**功能**：暴露端口。

**语法**：
```dockerfile
EXPOSE <port> [<port>/<protocol>...]
```

**示例**：
```dockerfile
EXPOSE 80
EXPOSE 443/tcp
EXPOSE 53/udp
```

### 9. VOLUME

**功能**：创建挂载点。

**语法**：
```dockerfile
VOLUME ["/data"]
VOLUME /var/log /var/db
```

**示例**：
```dockerfile
VOLUME ["/data"]
VOLUME /var/log /var/db
```

### 10. USER

**功能**：指定运行容器时的用户。

**语法**：
```dockerfile
USER <user>[:<group>]
USER <UID>[:<GID>]
```

**示例**：
```dockerfile
USER nginx
USER 1000:1000
```

### 11. WORKDIR

**功能**：设置工作目录。

**语法**：
```dockerfile
WORKDIR /path/to/workdir
```

**示例**：
```dockerfile
WORKDIR /app
```

### 12. ARG

**功能**：定义构建时的变量。

**语法**：
```dockerfile
ARG <name>[=<default value>]
```

**示例**：
```dockerfile
ARG VERSION=1.0
ARG BUILD_DATE
```

### 13. ONBUILD

**功能**：在镜像被用作另一个镜像的基础镜像时触发。

**语法**：
```dockerfile
ONBUILD [INSTRUCTION]
```

**示例**：
```dockerfile
ONBUILD COPY . /app/src
```

### 14. STOPSIGNAL

**功能**：设置发送给容器退出的系统调用信号。

**语法**：
```dockerfile
STOPSIGNAL signal
```

**示例**：
```dockerfile
STOPSIGNAL SIGTERM
```

### 15. HEALTHCHECK

**功能**：定义容器健康检查。

**语法**：
```dockerfile
HEALTHCHECK [OPTIONS] CMD command
HEALTHCHECK NONE
```

**示例**：
```dockerfile
HEALTHCHECK --interval=5m --timeout=3s \
  CMD curl -f http://localhost/ || exit 1
```

## Docker 镜像的最佳实践

### 1. 使用官方镜像

**建议**：
- 优先使用官方镜像
- 官方镜像更安全、更稳定

**示例**：
```bash
# 使用官方镜像
docker pull nginx
docker pull mysql
docker pull redis
```

### 2. 最小化镜像大小

**建议**：
- 使用 Alpine 镜像
- 删除不必要的文件
- 合并 RUN 指令

**示例**：
```dockerfile
# 使用 Alpine 镜像
FROM alpine:3.14

# 合并 RUN 指令
RUN apt-get update && \
    apt-get install -y nginx && \
    rm -rf /var/lib/apt/lists/*
```

### 3. 使用多阶段构建

**建议**：
- 使用多阶段构建减小镜像大小
- 将构建环境和运行环境分离

**示例**：
```dockerfile
# 构建阶段
FROM golang:1.16 AS builder
WORKDIR /app
COPY . .
RUN go build -o myapp

# 运行阶段
FROM alpine:3.14
WORKDIR /app
COPY --from=builder /app/myapp .
CMD ["./myapp"]
```

### 4. 定期更新镜像

**建议**：
- 定期更新基础镜像
- 定期更新依赖项

**示例**：
```bash
# 定期更新镜像
docker pull nginx:latest
docker pull mysql:latest
docker pull redis:latest
```

### 5. 扫描镜像漏洞

**建议**：
- 使用 Docker 扫描镜像漏洞
- 使用第三方工具扫描镜像漏洞

**示例**：
```bash
# 扫描镜像漏洞
docker scan nginx:latest

# 使用第三方工具扫描镜像漏洞
trivy image nginx:latest
```

## 总结

Docker 镜像是创建容器的只读模板，它包含了应用程序及其所有依赖项。Docker 镜像操作包括拉取、查看、构建、删除、标记、推送等。

**Docker 镜像的常用操作**：
- **拉取镜像（docker pull）**：从 Docker 仓库拉取镜像到本地
- **查看镜像（docker images）**：列出本地的 Docker 镜像
- **构建镜像（docker build）**：根据 Dockerfile 构建 Docker 镜像
- **删除镜像（docker rmi）**：删除一个或多个 Docker 镜像
- **标记镜像（docker tag）**：为镜像创建一个新的标签
- **推送镜像（docker push）**：将镜像推送到 Docker 仓库
- **搜索镜像（docker search）**：在 Docker Hub 上搜索镜像
- **查看镜像历史（docker history）**：显示镜像的历史记录
- **查看镜像详细信息（docker inspect）**：显示镜像的详细信息
- **导出镜像（docker save）**：将镜像保存为 tar 文件
- **导入镜像（docker load）**：从 tar 文件加载镜像
- **清理镜像（docker image prune）**：删除未使用的镜像

**Dockerfile 的常用指令**：
- **FROM**：指定基础镜像
- **RUN**：执行命令
- **COPY**：复制文件
- **ADD**：添加文件
- **CMD**：指定容器启动时执行的命令
- **ENTRYPOINT**：指定容器入口点
- **ENV**：设置环境变量
- **EXPOSE**：暴露端口
- **VOLUME**：创建挂载点
- **USER**：指定运行容器时的用户
- **WORKDIR**：设置工作目录
- **ARG**：定义构建时的变量
- **ONBUILD**：在镜像被用作另一个镜像的基础镜像时触发
- **STOPSIGNAL**：设置发送给容器退出的系统调用信号
- **HEALTHCHECK**：定义容器健康检查

**Docker 镜像的最佳实践**：
- 使用官方镜像
- 最小化镜像大小
- 使用多阶段构建
- 定期更新镜像
- 扫描镜像漏洞

通过掌握 Docker 镜像操作，可以更好地使用 Docker 进行开发、测试和部署。