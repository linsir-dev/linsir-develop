# 什么是Docker?

## 概述

Docker 是一个开源的容器化平台，它允许开发者将应用程序及其依赖项打包到一个轻量级、可移植的容器中，然后可以在任何支持 Docker 的环境中运行。Docker 基于 Linux 容器（LXC）技术，但提供了更简单、更强大的用户体验。Docker 是目前最流行的容器化平台之一，广泛应用于开发、测试、部署和运维等领域。

## Docker 的定义

### 1. 什么是 Docker

**定义**：Docker 是一个开源的容器化平台，它使用 Linux 容器技术来创建、部署和运行应用程序。Docker 容器是轻量级的、可移植的、自包含的运行环境。

**核心概念**：
- **容器（Container）**：运行应用程序的轻量级虚拟环境
- **镜像（Image）**：创建容器的只读模板
- **仓库（Repository）**：存储和分发镜像的地方
- **Dockerfile**：构建镜像的脚本文件

**示例**：
```
Dockerfile
    ↓
构建镜像
    ↓
推送镜像到仓库
    ↓
拉取镜像
    ↓
运行容器
```

### 2. Docker 的历史

**发展历程**：
- 2010 年：dotCloud 公司成立
- 2013 年：Docker 作为开源项目发布
- 2014 年：Docker 1.0 发布
- 2015 年：Docker 公司成立
- 2016 年：Docker 1.12 引入 Swarm 模式
- 2017 年：Docker 企业版发布
- 2019 年：Docker 引入多阶段构建
- 2020 年：Docker 引入 BuildKit

**示例**：
```
Docker 时间线：
2010: dotCloud 公司成立
2013: Docker 作为开源项目发布
2014: Docker 1.0 发布
2015: Docker 公司成立
2016: Docker 1.12 引入 Swarm 模式
2017: Docker 企业版发布
2019: Docker 引入多阶段构建
2020: Docker 引入 BuildKit
```

## Docker 的核心概念

### 1. 容器（Container）

**定义**：容器是运行应用程序的轻量级虚拟环境，它包含了应用程序及其所有依赖项。

**特点**：
- 轻量级：比虚拟机更轻量
- 快速启动：几秒钟内启动
- 可移植：可以在任何支持 Docker 的环境中运行
- 隔离：容器之间相互隔离
- 可重复：相同的镜像产生相同的容器

**示例**：
```bash
# 运行容器
docker run -d --name webserver -p 80:80 nginx

# 查看容器
docker ps

# 停止容器
docker stop webserver

# 启动容器
docker start webserver

# 删除容器
docker rm webserver
```

### 2. 镜像（Image）

**定义**：镜像是创建容器的只读模板，它包含了应用程序及其所有依赖项。

**特点**：
- 只读：镜像本身是只读的
- 分层：镜像由多个层组成
- 可复用：相同的镜像可以创建多个容器
- 可共享：镜像可以共享和分发

**示例**：
```bash
# 拉取镜像
docker pull nginx:latest

# 查看镜像
docker images

# 构建镜像
docker build -t myapp:1.0 .

# 删除镜像
docker rmi nginx:latest
```

### 3. 仓库（Repository）

**定义**：仓库是存储和分发镜像的地方，类似于代码仓库。

**类型**：
- **公共仓库**：Docker Hub、阿里云镜像仓库等
- **私有仓库**：企业内部的镜像仓库

**示例**：
```bash
# 登录到 Docker Hub
docker login

# 推送镜像到 Docker Hub
docker push username/myapp:1.0

# 拉取镜像从 Docker Hub
docker pull username/myapp:1.0

# 搜索镜像
docker search nginx
```

### 4. Dockerfile

**定义**：Dockerfile 是构建镜像的脚本文件，它包含了一系列构建镜像的指令。

**常用指令**：
- `FROM`：指定基础镜像
- `RUN`：执行命令
- `COPY`：复制文件
- `ADD`：添加文件
- `CMD`：指定容器启动时执行的命令
- `ENTRYPOINT`：指定容器入口点
- `ENV`：设置环境变量
- `EXPOSE`：暴露端口
- `VOLUME`：创建挂载点

**示例**：
```dockerfile
FROM ubuntu:20.04

RUN apt-get update && apt-get install -y nginx

COPY index.html /var/www/html/

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

## Docker 的架构

### 1. Docker 的组件

**组件**：
- **Docker 客户端（Docker Client）**：用户与 Docker 交互的命令行工具
- **Docker 守护进程（Docker Daemon）**：Docker 的核心组件，负责管理容器和镜像
- **Docker 镜像（Docker Image）**：创建容器的只读模板
- **Docker 容器（Docker Container）**：运行应用程序的轻量级虚拟环境
- **Docker 仓库（Docker Registry）**：存储和分发镜像的地方

**架构图**：
```
┌─────────────────────────────────────────────────────────┐
│                    Docker Client                      │
│                  (命令行工具）                          │
└────────────────────┬────────────────────────────────┘
                     │
                     │ REST API
                     ↓
┌─────────────────────────────────────────────────────────┐
│                  Docker Daemon                       │
│                  (守护进程）                          │
│  ┌──────────────┐  ┌──────────────┐              │
│  │   Images     │  │  Containers  │              │
│  └──────────────┘  └──────────────┘              │
└────────────────────┬────────────────────────────────┘
                     │
                     │
                     ↓
┌─────────────────────────────────────────────────────────┐
│              Docker Registry                        │
│              (镜像仓库）                              │
│  ┌──────────────┐  ┌──────────────┐              │
│  │   Public     │  │   Private   │              │
│  │  (Docker Hub)│  │  (Registry)  │              │
│  └──────────────┘  └──────────────┘              │
└─────────────────────────────────────────────────────────┘
```

### 2. Docker 客户端

**功能**：
- 提供命令行界面
- 与 Docker 守护进程通信
- 发送构建、运行、管理容器和镜像的命令

**示例**：
```bash
# Docker 客户端命令
docker run
docker build
docker ps
docker images
docker pull
docker push
```

### 3. Docker 守护进程

**功能**：
- 监听 Docker API 请求
- 管理容器和镜像
- 管理网络和存储
- 处理容器生命周期

**示例**：
```bash
# 启动 Docker 守护进程
sudo systemctl start docker

# 停止 Docker 守护进程
sudo systemctl stop docker

# 重启 Docker 守护进程
sudo systemctl restart docker

# 查看 Docker 守护进程状态
sudo systemctl status docker
```

## Docker 的优势

### 1. 轻量级

**优势**：
- 容器比虚拟机更轻量
- 容器启动更快
- 容器占用更少的资源

**示例**：
```
虚拟机：
- 启动时间：几分钟
- 资源占用：GB 级别
- 镜像大小：GB 级别

容器：
- 启动时间：几秒钟
- 资源占用：MB 级别
- 镜像大小：MB 级别
```

### 2. 可移植性

**优势**：
- 容器可以在任何支持 Docker 的环境中运行
- 容器可以在开发、测试、生产环境中保持一致
- 容器可以在不同的云平台上运行

**示例**：
```bash
# 在本地运行容器
docker run -d --name webserver -p 80:80 nginx

# 在云平台上运行容器
# AWS ECS、Azure Container Instances、Google Cloud Run
```

### 3. 快速部署

**优势**：
- 容器可以快速部署
- 容器可以快速扩展
- 容器可以快速回滚

**示例**：
```bash
# 快速部署容器
docker run -d --name webserver -p 80:80 nginx

# 快速扩展容器
docker-compose up --scale webserver=3

# 快速回滚容器
docker run -d --name webserver -p 80:80 nginx:1.18
```

### 4. 隔离性

**优势**：
- 容器之间相互隔离
- 容器与主机相互隔离
- 容器可以限制资源

**示例**：
```bash
# 限制容器资源
docker run -d --name webserver -p 80:80 --memory="512m" --cpus="1.0" nginx

# 查看容器资源使用情况
docker stats webserver
```

### 5. 可重复性

**优势**：
- 相同的镜像产生相同的容器
- 容器可以重复创建和删除
- 容器可以版本控制

**示例**：
```bash
# 创建相同的容器
docker run -d --name webserver1 -p 8080:80 nginx
docker run -d --name webserver2 -p 8081:80 nginx

# 版本控制
docker tag myapp:1.0 myapp:latest
docker push username/myapp:1.0
docker push username/myapp:latest
```

## Docker 的应用场景

### 1. 开发环境

**应用**：
- 快速搭建开发环境
- 保持开发环境一致
- 快速切换不同的开发环境

**示例**：
```bash
# 搭建开发环境
docker run -it --name dev -v $(pwd):/workspace ubuntu:20.04

# 快速切换开发环境
docker run -it --name dev-python -v $(pwd):/workspace python:3.9
docker run -it --name dev-node -v $(pwd):/workspace node:16
```

### 2. 测试环境

**应用**：
- 快速搭建测试环境
- 保持测试环境一致
- 快速清理测试环境

**示例**：
```bash
# 搭建测试环境
docker-compose up -d

# 运行测试
docker-compose exec web pytest

# 清理测试环境
docker-compose down
```

### 3. 生产环境

**应用**：
- 快速部署应用程序
- 快速扩展应用程序
- 快速回滚应用程序

**示例**：
```bash
# 部署应用程序
docker run -d --name webserver -p 80:80 nginx

# 扩展应用程序
docker-compose up --scale webserver=3

# 回滚应用程序
docker run -d --name webserver -p 80:80 nginx:1.18
```

### 4. 微服务架构

**应用**：
- 每个微服务运行在独立的容器中
- 容器之间通过网络通信
- 容器可以独立部署和扩展

**示例**：
```yaml
# docker-compose.yml
version: '3'
services:
  web:
    image: nginx
    ports:
      - "80:80"
  api:
    image: myapp:1.0
    ports:
      - "8080:8080"
  db:
    image: mysql:5.7
    environment:
      MYSQL_ROOT_PASSWORD: password
```

## Docker 的安装

### 1. 在 Ubuntu 上安装 Docker

**步骤**：
1. 更新软件包索引
2. 安装依赖包
3. 添加 Docker 官方 GPG 密钥
4. 添加 Docker 仓库
5. 安装 Docker
6. 启动 Docker

**示例**：
```bash
# 更新软件包索引
sudo apt-get update

# 安装依赖包
sudo apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release

# 添加 Docker 官方 GPG 密钥
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# 添加 Docker 仓库
echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 安装 Docker
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io

# 启动 Docker
sudo systemctl start docker
sudo systemctl enable docker

# 验证安装
docker --version
```

### 2. 在 CentOS 上安装 Docker

**步骤**：
1. 安装依赖包
2. 添加 Docker 仓库
3. 安装 Docker
4. 启动 Docker

**示例**：
```bash
# 安装依赖包
sudo yum install -y yum-utils

# 添加 Docker 仓库
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo

# 安装 Docker
sudo yum install -y docker-ce docker-ce-cli containerd.io

# 启动 Docker
sudo systemctl start docker
sudo systemctl enable docker

# 验证安装
docker --version
```

### 3. 在 Windows 上安装 Docker

**步骤**：
1. 下载 Docker Desktop for Windows
2. 安装 Docker Desktop
3. 启动 Docker Desktop
4. 验证安装

**示例**：
```
1. 访问 https://www.docker.com/products/docker-desktop
2. 下载 Docker Desktop for Windows
3. 运行安装程序
4. 启动 Docker Desktop
5. 打开 PowerShell，运行 docker --version
```

## Docker 的基本操作

### 1. 镜像操作

**常用命令**：
- `docker pull`：拉取镜像
- `docker images`：查看镜像
- `docker build`：构建镜像
- `docker rmi`：删除镜像
- `docker tag`：标记镜像

**示例**：
```bash
# 拉取镜像
docker pull nginx:latest

# 查看镜像
docker images

# 构建镜像
docker build -t myapp:1.0 .

# 删除镜像
docker rmi nginx:latest

# 标记镜像
docker tag myapp:1.0 username/myapp:1.0
```

### 2. 容器操作

**常用命令**：
- `docker run`：运行容器
- `docker ps`：查看容器
- `docker stop`：停止容器
- `docker start`：启动容器
- `docker rm`：删除容器

**示例**：
```bash
# 运行容器
docker run -d --name webserver -p 80:80 nginx

# 查看容器
docker ps

# 停止容器
docker stop webserver

# 启动容器
docker start webserver

# 删除容器
docker rm webserver
```

### 3. 日志操作

**常用命令**：
- `docker logs`：查看容器日志
- `docker logs -f`：实时查看容器日志
- `docker logs --tail`：查看容器日志的最后几行

**示例**：
```bash
# 查看容器日志
docker logs webserver

# 实时查看容器日志
docker logs -f webserver

# 查看容器日志的最后 100 行
docker logs --tail 100 webserver
```

## 总结

Docker 是一个开源的容器化平台，它使用 Linux 容器技术来创建、部署和运行应用程序。Docker 容器是轻量级的、可移植的、自包含的运行环境。

**Docker 的核心概念**：
- 容器（Container）：运行应用程序的轻量级虚拟环境
- 镜像（Image）：创建容器的只读模板
- 仓库（Repository）：存储和分发镜像的地方
- Dockerfile：构建镜像的脚本文件

**Docker 的架构**：
- Docker 客户端（Docker Client）：用户与 Docker 交互的命令行工具
- Docker 守护进程（Docker Daemon）：Docker 的核心组件，负责管理容器和镜像
- Docker 镜像（Docker Image）：创建容器的只读模板
- Docker 容器（Docker Container）：运行应用程序的轻量级虚拟环境
- Docker 仓库（Docker Registry）：存储和分发镜像的地方

**Docker 的优势**：
- 轻量级：容器比虚拟机更轻量
- 可移植性：容器可以在任何支持 Docker 的环境中运行
- 快速部署：容器可以快速部署、扩展和回滚
- 隔离性：容器之间相互隔离
- 可重复性：相同的镜像产生相同的容器

**Docker 的应用场景**：
- 开发环境：快速搭建开发环境
- 测试环境：快速搭建测试环境
- 生产环境：快速部署应用程序
- 微服务架构：每个微服务运行在独立的容器中

通过理解 Docker 的概念、架构、优势、应用场景和基本操作，可以更好地使用 Docker 进行开发、测试和部署。