# 如何查看Docker容器的日志？

## 概述

查看 Docker 容器的日志是调试和监控容器的重要手段。Docker 提供了多种查看容器日志的方法，包括实时查看、过滤日志、查看历史日志等。掌握查看 Docker 容器日志的方法对于运维和开发非常重要。

## 查看容器日志的基本方法

### 1. docker logs 命令

**功能**：查看容器的日志。

**语法**：
```bash
docker logs [OPTIONS] CONTAINER
```

**常用选项**：
- `-f, --follow`：跟踪日志输出（类似于 tail -f）
- `--tail`：从日志末尾开始显示
- `-t, --timestamps`：显示时间戳
- `--details`：显示额外的详细信息
- `--since`：显示指定时间之后的日志
- `--until`：显示指定时间之前的日志
- `--show-log-opts`：显示日志驱动选项

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

### 2. 实时查看日志

**功能**：实时跟踪容器的日志输出。

**示例**：
```bash
# 实时查看日志
docker logs -f webserver

# 实时查看日志并显示时间戳
docker logs -f -t webserver

# 实时查看日志的最后 100 行
docker logs -f --tail 100 webserver
```

### 3. 查看指定数量的日志

**功能**：查看容器日志的最后 N 行。

**示例**：
```bash
# 查看最后 10 行日志
docker logs --tail 10 webserver

# 查看最后 100 行日志
docker logs --tail 100 webserver

# 查看最后 1000 行日志
docker logs --tail 1000 webserver

# 查看所有日志
docker logs --tail all webserver
```

### 4. 查看指定时间范围的日志

**功能**：查看指定时间范围内的日志。

**示例**：
```bash
# 查看指定时间之后的日志
docker logs --since 2023-01-01T00:00:00 webserver

# 查看指定时间之前的日志
docker logs --until 2023-01-01T00:00:00 webserver

# 查看指定时间范围内的日志
docker logs --since 2023-01-01T00:00:00 --until 2023-01-02T00:00:00 webserver

# 查看最近 1 小时的日志
docker logs --since 1h webserver

# 查看最近 30 分钟的日志
docker logs --since 30m webserver
```

## 日志驱动

### 1. 查看日志驱动

**功能**：查看容器使用的日志驱动。

**示例**：
```bash
# 查看容器日志驱动
docker inspect webserver | grep -A 10 "LogConfig"

# 查看容器日志驱动
docker inspect -f '{{.HostConfig.LogConfig.Type}}' webserver
```

### 2. 常用日志驱动

**类型**：
- **json-file**：默认的日志驱动，将日志以 JSON 格式存储在文件中
- **syslog**：将日志发送到 syslog 守护进程
- **journald**：将日志发送到 journald 守护进程
- **gelf**：将日志发送到 GELF 兼容的端点
- **fluentd**：将日志发送到 fluentd 守护进程
- **awslogs**：将日志发送到 Amazon CloudWatch Logs
- **splunk**：将日志发送到 Splunk
- **etwlogs**：将日志发送到 Windows Event Tracing（仅限 Windows）
- **gcplogs**：将日志发送到 Google Cloud Platform Logging
- **logentries**：将日志发送到 Logentries

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

### 3. 配置日志驱动

**功能**：配置容器的日志驱动。

**示例**：
```bash
# 配置 json-file 日志驱动
docker run -d --name webserver \
  --log-driver json-file \
  --log-opt max-size=10m \
  --log-opt max-file=3 \
  nginx

# 配置 syslog 日志驱动
docker run -d --name webserver \
  --log-driver syslog \
  --log-opt syslog-address=tcp://192.168.0.42:123 \
  nginx

# 配置 fluentd 日志驱动
docker run -d --name webserver \
  --log-driver fluentd \
  --log-opt fluentd-address=localhost:24224 \
  --log-opt tag=docker.webserver \
  nginx
```

## 日志文件位置

### 1. 查看日志文件位置

**功能**：查看容器日志文件的位置。

**示例**：
```bash
# 查看容器日志文件位置
docker inspect webserver | grep -A 5 "LogPath"

# 查看容器日志文件位置
docker inspect -f '{{.LogPath}}' webserver
```

### 2. 直接查看日志文件

**功能**：直接查看容器日志文件。

**示例**：
```bash
# 查看容器日志文件
cat /var/lib/docker/containers/<container-id>/<container-id>-json.log

# 实时查看容器日志文件
tail -f /var/lib/docker/containers/<container-id>/<container-id>-json.log
```

## 日志过滤

### 1. 过滤日志内容

**功能**：过滤容器日志的内容。

**示例**：
```bash
# 过滤包含特定字符串的日志
docker logs webserver | grep "error"

# 过滤不包含特定字符串的日志
docker logs webserver | grep -v "debug"

# 过滤多个字符串
docker logs webserver | grep -E "error|warning"

# 实时过滤日志
docker logs -f webserver | grep "error"
```

### 2. 统计日志

**功能**：统计容器日志的数量。

**示例**：
```bash
# 统计日志行数
docker logs webserver | wc -l

# 统计包含特定字符串的日志行数
docker logs webserver | grep "error" | wc -l

# 统计日志中的错误数量
docker logs webserver | grep -i "error" | wc -l
```

## 日志轮转

### 1. 配置日志轮转

**功能**：配置容器日志的轮转策略。

**示例**：
```bash
# 配置日志轮转
docker run -d --name webserver \
  --log-driver json-file \
  --log-opt max-size=10m \
  --log-opt max-file=3 \
  nginx

# 配置全局日志轮转
sudo vi /etc/docker/daemon.json

# 添加以下内容
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  }
}

# 重启 Docker
sudo systemctl restart docker
```

### 2. 清理日志

**功能**：清理容器日志。

**示例**：
```bash
# 清理容器日志
docker run --rm -v /var/lib/docker:/docker alpine sh -c "cd /docker/containers && find . -type f -name '*.log' -exec truncate -s 0 {} \;"

# 清理所有容器日志
docker run --rm -v /var/lib/docker:/docker alpine sh -c "cd /docker/containers && find . -type f -name '*.log' -exec truncate -s 0 {} \;"
```

## 日志监控

### 1. 使用日志监控工具

**工具**：
- **ELK Stack**：Elasticsearch、Logstash、Kibana
- **Fluentd**：开源的数据收集器
- **Promtail**：Grafana Loki 的日志代理
- **Filebeat**：轻量级的日志收集器

**示例**：
```bash
# 使用 Fluentd 收集日志
docker run -d --name fluentd \
  -v /fluentd/log:/fluentd/log \
  fluent/fluentd

# 使用 Filebeat 收集日志
docker run -d --name filebeat \
  -v /var/lib/docker/containers:/var/lib/docker/containers:ro \
  -v /var/run/docker.sock:/var/run/docker.sock:ro \
  elastic/filebeat:7.15.0
```

### 2. 使用日志分析工具

**工具**：
- **Grafana**：开源的分析和监控平台
- **Kibana**：开源的分析和可视化平台
- **Splunk**：商业的分析和监控平台

**示例**：
```bash
# 使用 Grafana 监控日志
docker run -d --name grafana \
  -p 3000:3000 \
  grafana/grafana

# 使用 Kibana 分析日志
docker run -d --name kibana \
  -p 5601:5601 \
  -e ELASTICSEARCH_HOSTS=http://elasticsearch:9200 \
  kibana:7.15.0
```

## 日志最佳实践

### 1. 使用结构化日志

**建议**：
- 使用 JSON 格式记录日志
- 使用统一的日志格式
- 包含时间戳、日志级别、消息等信息

**示例**：
```json
{
  "timestamp": "2023-01-01T00:00:00Z",
  "level": "info",
  "message": "Request received",
  "request_id": "123456",
  "user_id": "789"
}
```

### 2. 使用日志级别

**建议**：
- 使用不同的日志级别（debug、info、warning、error、fatal）
- 根据环境配置不同的日志级别
- 生产环境使用 warning 或 error 级别

**示例**：
```bash
# 配置日志级别
docker run -d --name webserver \
  -e LOG_LEVEL=info \
  nginx

# 配置日志级别为 warning
docker run -d --name webserver \
  -e LOG_LEVEL=warning \
  nginx
```

### 3. 使用日志聚合

**建议**：
- 使用日志聚合工具收集所有容器的日志
- 使用日志聚合工具分析日志
- 使用日志聚合工具监控日志

**示例**：
```bash
# 使用 ELK Stack 聚合日志
docker-compose up -d

# docker-compose.yml
version: '3'
services:
  elasticsearch:
    image: elasticsearch:7.15.0
    ports:
      - "9200:9200"
  logstash:
    image: logstash:7.15.0
    ports:
      - "5044:5044"
    depends_on:
      - elasticsearch
  kibana:
    image: kibana:7.15.0
    ports:
      - "5601:5601"
    depends_on:
      - elasticsearch
```

### 4. 使用日志备份

**建议**：
- 定期备份容器日志
- 使用日志备份工具
- 使用日志备份策略

**示例**：
```bash
# 备份容器日志
docker logs webserver > webserver.log

# 定期备份容器日志
docker logs webserver > /backup/webserver-$(date +%Y%m%d).log

# 使用日志备份工具
docker run --rm -v /var/lib/docker:/docker -v /backup:/backup alpine sh -c "cd /docker/containers && find . -type f -name '*.log' -exec cp {} /backup/ \;"
```

## 总结

查看 Docker 容器的日志是调试和监控容器的重要手段。Docker 提供了多种查看容器日志的方法，包括实时查看、过滤日志、查看历史日志等。

**查看容器日志的基本方法**：
- **docker logs 命令**：查看容器的日志
- **实时查看日志**：使用 `-f` 选项实时跟踪日志输出
- **查看指定数量的日志**：使用 `--tail` 选项查看最后 N 行日志
- **查看指定时间范围的日志**：使用 `--since` 和 `--until` 选项查看指定时间范围内的日志

**日志驱动**：
- **json-file**：默认的日志驱动
- **syslog**：将日志发送到 syslog 守护进程
- **journald**：将日志发送到 journald 守护进程
- **fluentd**：将日志发送到 fluentd 守护进程
- 其他日志驱动：gelf、awslogs、splunk、etwlogs、gcplogs、logentries

**日志文件位置**：
- **查看日志文件位置**：使用 `docker inspect` 命令查看
- **直接查看日志文件**：直接查看 `/var/lib/docker/containers/<container-id>/<container-id>-json.log` 文件

**日志过滤**：
- **过滤日志内容**：使用 `grep` 命令过滤日志
- **统计日志**：使用 `wc` 命令统计日志数量

**日志轮转**：
- **配置日志轮转**：使用 `--log-opt` 选项配置日志轮转
- **清理日志**：使用 `truncate` 命令清理日志

**日志监控**：
- **使用日志监控工具**：ELK Stack、Fluentd、Promtail、Filebeat
- **使用日志分析工具**：Grafana、Kibana、Splunk

**日志最佳实践**：
- 使用结构化日志
- 使用日志级别
- 使用日志聚合
- 使用日志备份

通过掌握查看 Docker 容器日志的方法，可以更好地调试和监控容器。