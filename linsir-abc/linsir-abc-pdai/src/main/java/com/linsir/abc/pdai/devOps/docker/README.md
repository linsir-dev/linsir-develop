# Docker 技术文档

## 文档列表

1. [什么是虚拟化技术？](what-is-virtualization.md)
2. [什么是Docker?](what-is-docker.md)
3. [Docker和虚拟机的区别？](docker-vs-vm.md)
4. [Docker的架构？](docker-architecture.md)
5. [Docker镜像相关操作有哪些？](docker-image-operations.md)
6. [Docker容器相关操作有哪些？](docker-container-operations.md)
7. [如何查看Docker容器的日志？](docker-logs.md)
8. [如何启动Docker容器？参数含义？如何进入Docker后台模式？有什么区别？](docker-start-container.md)

## 学习路径

### 初学者
1. 什么是虚拟化技术？ → 了解虚拟化的基本概念
2. 什么是Docker? → 了解 Docker 的基本概念
3. Docker和虚拟机的区别？ → 了解 Docker 和虚拟机的区别
4. Docker的架构？ → 了解 Docker 的整体架构

### 进阶者
1. Docker镜像相关操作有哪些？ → 掌握 Docker 镜像的常用操作
2. Docker容器相关操作有哪些？ → 掌握 Docker 容器的常用操作
3. 如何查看Docker容器的日志？ → 掌握查看容器日志的方法
4. 如何启动Docker容器？参数含义？如何进入Docker后台模式？有什么区别？ → 掌握启动容器的方法

## 核心概念

### 虚拟化技术
- 虚拟化技术的定义
- 虚拟化技术的类型
- 虚拟化技术的应用场景
- 虚拟化技术的优缺点

### Docker
- Docker 的定义
- Docker 的特点
- Docker 的应用场景
- Docker 的优势

### Docker 和虚拟机的区别
- 架构差异
- 性能差异
- 启动速度差异
- 资源占用差异
- 隔离性差异
- 可移植性差异

### Docker 的架构
- Docker 客户端
- Docker 守护进程
- Docker 镜像
- Docker 容器
- Docker 仓库
- Docker 的工作流程
- Docker 的底层技术

### Docker 镜像
- Docker 镜像的定义
- Docker 镜像的分层结构
- Docker 镜像的常用操作
- Dockerfile 的常用指令
- Docker 镜像的最佳实践

### Docker 容器
- Docker 容器的定义
- Docker 容器的生命周期
- Docker 容器的常用操作
- Docker 容器的最佳实践

### Docker 日志
- 查看容器日志的方法
- 日志驱动
- 日志文件位置
- 日志过滤
- 日志轮转
- 日志监控
- 日志最佳实践

### Docker 容器启动
- docker run 命令
- docker run 常用参数
- 前台模式和后台模式
- 交互式模式
- 进入 Docker 容器

## 常用命令

### 镜像操作
```bash
# 拉取镜像
docker pull nginx

# 查看镜像
docker images

# 构建镜像
docker build -t myapp:1.0 .

# 删除镜像
docker rmi nginx

# 标记镜像
docker tag myapp:1.0 username/myapp:1.0

# 推送镜像
docker push username/myapp:1.0

# 搜索镜像
docker search nginx
```

### 容器操作
```bash
# 运行容器
docker run -d --name webserver -p 80:80 nginx

# 查看容器
docker ps

# 停止容器
docker stop webserver

# 启动容器
docker start webserver

# 重启容器
docker restart webserver

# 删除容器
docker rm webserver

# 进入容器
docker exec -it webserver /bin/bash
```

### 日志操作
```bash
# 查看容器日志
docker logs webserver

# 实时查看日志
docker logs -f webserver

# 查看最后 100 行日志
docker logs --tail 100 webserver

# 显示时间戳
docker logs -t webserver
```

## 最佳实践

### 镜像最佳实践
- 使用官方镜像
- 最小化镜像大小
- 使用多阶段构建
- 定期更新镜像
- 扫描镜像漏洞

### 容器最佳实践
- 使用有意义的容器名称
- 限制容器资源
- 使用数据卷
- 使用健康检查
- 使用日志驱动

### 日志最佳实践
- 使用结构化日志
- 使用日志级别
- 使用日志聚合
- 使用日志备份

## 参考资源

- [Docker 官方文档](https://docs.docker.com/)
- [Docker Hub](https://hub.docker.com/)
- [Docker 教程](https://docs.docker.com/get-started/)
- [Docker 最佳实践](https://docs.docker.com/develop/dev-best-practices/)