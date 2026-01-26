# 监控体系监控哪些内容？

## 引言

一个完善的监控体系需要监控系统的各个方面，从基础设施到业务指标，从应用性能到用户体验。本文将详细介绍监控体系需要监控的内容。

## 监控体系的层次

### 1. 基础设施监控

监控基础设施层，包括服务器、网络、存储等：

```yaml
# 示例：Node Exporter 配置
apiVersion: v1
kind: DaemonSet
metadata:
  name: node-exporter
  namespace: monitoring
spec:
  selector:
    matchLabels:
      app: node-exporter
  template:
    metadata:
      labels:
        app: node-exporter
    spec:
      containers:
      - name: node-exporter
        image: prom/node-exporter:latest
        ports:
        - containerPort: 9100
          hostPort: 9100
        resources:
          requests:
            cpu: 100m
            memory: 100Mi
          limits:
            cpu: 200m
            memory: 200Mi
```

#### 1.1 服务器监控

监控服务器的运行状态：

##### CPU 监控

- CPU 使用率
- CPU 负载
- CPU 上下文切换
- CPU 中断次数

```yaml
# CPU 监控指标
cpu_usage_percent: CPU 使用率
cpu_load_average: CPU 负载
cpu_context_switches: CPU 上下文切换
cpu_interrupts: CPU 中断次数
```

##### 内存监控

- 内存使用率
- 内存交换率
- 内存缓存
- 内存缓冲区

```yaml
# 内存监控指标
memory_usage_percent: 内存使用率
memory_swap_percent: 内存交换率
memory_cache: 内存缓存
memory_buffer: 内存缓冲区
```

##### 磁盘监控

- 磁盘使用率
- 磁盘 I/O
- 磁盘 IOPS
- 磁盘延迟

```yaml
# 磁盘监控指标
disk_usage_percent: 磁盘使用率
disk_io_read: 磁盘读取 I/O
disk_io_write: 磁盘写入 I/O
disk_iops: 磁盘 IOPS
disk_latency: 磁盘延迟
```

##### 网络监控

- 网络流量
- 网络连接数
- 网络错误
- 网络延迟

```yaml
# 网络监控指标
network_in_bytes: 网络入流量
network_out_bytes: 网络出流量
network_connections: 网络连接数
network_errors: 网络错误
network_latency: 网络延迟
```

#### 1.2 网络监控

监控网络的运行状态：

##### 网络设备监控

- 交换机状态
- 路由器状态
- 防火墙状态
- 负载均衡器状态

```yaml
# 网络设备监控指标
switch_status: 交换机状态
router_status: 路由器状态
firewall_status: 防火墙状态
load_balancer_status: 负载均衡器状态
```

##### 网络链路监控

- 链路带宽
- 链路延迟
- 链路丢包率
- 链路抖动

```yaml
# 网络链路监控指标
link_bandwidth: 链路带宽
link_latency: 链路延迟
link_packet_loss: 链路丢包率
link_jitter: 链路抖动
```

#### 1.3 存储监控

监控存储的运行状态：

##### 存储设备监控

- 存储使用率
- 存储性能
- 存储健康状态
- 存储冗余状态

```yaml
# 存储设备监控指标
storage_usage_percent: 存储使用率
storage_iops: 存储 IOPS
storage_throughput: 存储吞吐量
storage_health: 存储健康状态
storage_redundancy: 存储冗余状态
```

##### 数据库监控

- 数据库连接数
- 数据库查询性能
- 数据库锁等待
- 数据库死锁

```yaml
# 数据库监控指标
database_connections: 数据库连接数
database_query_time: 数据库查询时间
database_lock_waits: 数据库锁等待
database_deadlocks: 数据库死锁
```

### 2. 应用监控

监控应用层，包括应用性能、应用健康等：

```java
// 示例：Spring Boot Actuator
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ApplicationHealthIndicator implements HealthIndicator {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Override
    public Health health() {
        Health.Builder builder = Health.up();
        
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                builder.withDetail("database", "OK");
            } else {
                builder.down().withDetail("database", "Connection invalid");
            }
        } catch (SQLException e) {
            builder.down().withDetail("database", e.getMessage());
        }
        
        try {
            redisTemplate.opsForValue().get("health-check");
            builder.withDetail("redis", "OK");
        } catch (Exception e) {
            builder.down().withDetail("redis", e.getMessage());
        }
        
        return builder.build();
    }
}
```

#### 2.1 应用性能监控

监控应用的性能指标：

##### 响应时间

- 平均响应时间
- 中位数响应时间
- 95 分位响应时间
- 99 分位响应时间

```yaml
# 响应时间监控指标
response_time_avg: 平均响应时间
response_time_p50: 50 分位响应时间
response_time_p95: 95 分位响应时间
response_time_p99: 99 分位响应时间
```

##### 吞吐量

- 每秒请求数
- 每分钟请求数
- 每小时请求数
- 每天请求数

```yaml
# 吞吐量监控指标
requests_per_second: 每秒请求数
requests_per_minute: 每分钟请求数
requests_per_hour: 每小时请求数
requests_per_day: 每天请求数
```

##### 并发数

- 当前并发数
- 峰值并发数
- 平均并发数
- 最大并发数

```yaml
# 并发数监控指标
concurrent_requests_current: 当前并发数
concurrent_requests_peak: 峰值并发数
concurrent_requests_avg: 平均并发数
concurrent_requests_max: 最大并发数
```

##### 错误率

- HTTP 错误率
- 应用错误率
- 系统错误率
- 业务错误率

```yaml
# 错误率监控指标
error_rate_http: HTTP 错误率
error_rate_application: 应用错误率
error_rate_system: 系统错误率
error_rate_business: 业务错误率
```

#### 2.2 应用健康监控

监控应用的健康状态：

##### 应用状态

- 应用运行状态
- 应用启动时间
- 应用运行时间
- 应用重启次数

```yaml
# 应用状态监控指标
application_status: 应用运行状态
application_start_time: 应用启动时间
application_uptime: 应用运行时间
application_restart_count: 应用重启次数
```

##### 依赖服务状态

- 数据库连接状态
- 缓存连接状态
- 消息队列状态
- 外部服务状态

```yaml
# 依赖服务状态监控指标
dependency_database_status: 数据库连接状态
dependency_cache_status: 缓存连接状态
dependency_mq_status: 消息队列状态
dependency_external_service_status: 外部服务状态
```

#### 2.3 应用日志监控

监控应用的日志：

##### 日志级别

- DEBUG 日志数量
- INFO 日志数量
- WARN 日志数量
- ERROR 日志数量

```yaml
# 日志级别监控指标
log_debug_count: DEBUG 日志数量
log_info_count: INFO 日志数量
log_warn_count: WARN 日志数量
log_error_count: ERROR 日志数量
```

##### 日志关键词

- 错误关键词
- 警告关键词
- 异常关键词
- 性能关键词

```yaml
# 日志关键词监控指标
log_error_keywords: 错误关键词
log_warning_keywords: 警告关键词
log_exception_keywords: 异常关键词
log_performance_keywords: 性能关键词
```

### 3. 业务监控

监控业务层，包括业务指标、业务流程等：

```java
// 示例：业务指标监控
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class BusinessMetricsService {
    
    private final MeterRegistry registry;
    
    public BusinessMetricsService(MeterRegistry registry) {
        this.registry = registry;
    }
    
    public void recordOrder(Order order) {
        Counter.builder("orders.total")
            .tag("status", order.getStatus())
            .description("Total number of orders")
            .register(registry)
            .increment();
        
        Gauge.builder("orders.amount", () -> order.getAmount())
            .description("Total order amount")
            .register(registry);
    }
    
    public void recordUserLogin(String userId) {
        Counter.builder("users.login")
            .tag("user", userId)
            .description("Total number of user logins")
            .register(registry)
            .increment();
    }
    
    public void recordPayment(Payment payment) {
        Timer.builder("payments.duration")
            .tag("status", payment.getStatus())
            .description("Payment processing time")
            .register(registry)
            .record(payment.getDuration(), TimeUnit.MILLISECONDS);
    }
}
```

#### 3.1 业务指标监控

监控业务指标：

##### 订单指标

- 订单数量
- 订单金额
- 订单转化率
- 订单取消率

```yaml
# 订单指标监控
orders_total: 订单数量
orders_amount: 订单金额
orders_conversion_rate: 订单转化率
orders_cancellation_rate: 订单取消率
```

##### 用户指标

- 用户数量
- 活跃用户数
- 新增用户数
- 流失用户数

```yaml
# 用户指标监控
users_total: 用户数量
users_active: 活跃用户数
users_new: 新增用户数
users_churn: 流失用户数
```

##### 收入指标

- 总收入
- 日收入
- 月收入
- 年收入

```yaml
# 收入指标监控
revenue_total: 总收入
revenue_daily: 日收入
revenue_monthly: 月收入
revenue_yearly: 年收入
```

##### 转化指标

- 注册转化率
- 购买转化率
- 支付转化率
- 复购转化率

```yaml
# 转化指标监控
conversion_registration: 注册转化率
conversion_purchase: 购买转化率
conversion_payment: 支付转化率
conversion_repurchase: 复购转化率
```

#### 3.2 业务流程监控

监控业务流程：

##### 注册流程

- 注册开始数
- 注册完成数
- 注册成功率
- 注册失败原因

```yaml
# 注册流程监控
registration_start: 注册开始数
registration_complete: 注册完成数
registration_success_rate: 注册成功率
registration_failure_reasons: 注册失败原因
```

##### 下单流程

- 下单开始数
- 下单完成数
- 下单成功率
- 下单失败原因

```yaml
# 下单流程监控
order_start: 下单开始数
order_complete: 下单完成数
order_success_rate: 下单成功率
order_failure_reasons: 下单失败原因
```

##### 支付流程

- 支付开始数
- 支付完成数
- 支付成功率
- 支付失败原因

```yaml
# 支付流程监控
payment_start: 支付开始数
payment_complete: 支付完成数
payment_success_rate: 支付成功率
payment_failure_reasons: 支付失败原因
```

### 4. 用户体验监控

监控用户体验，包括页面性能、用户行为等：

```javascript
// 示例：前端性能监控
// 记录页面加载时间
window.addEventListener('load', function() {
    const timing = performance.timing;
    const loadTime = timing.loadEventEnd - timing.navigationStart;
    const domReadyTime = timing.domContentLoadedEventEnd - timing.navigationStart;
    const firstPaintTime = timing.responseStart - timing.navigationStart;
    
    // 发送到监控系统
    fetch('/api/metrics/page-performance', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            url: window.location.href,
            loadTime: loadTime,
            domReadyTime: domReadyTime,
            firstPaintTime: firstPaintTime,
            timestamp: Date.now()
        })
    });
});

// 记录 API 请求时间
const originalFetch = window.fetch;
window.fetch = function(...args) {
    const startTime = performance.now();
    return originalFetch.apply(this, args).then(response => {
        const duration = performance.now() - startTime;
        
        fetch('/api/metrics/api-performance', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                url: args[0],
                method: args[1]?.method || 'GET',
                duration: duration,
                status: response.status,
                timestamp: Date.now()
            })
        });
        
        return response;
    });
};
```

#### 4.1 页面性能监控

监控页面性能：

##### 页面加载时间

- 总加载时间
- DOM 加载时间
- 首屏渲染时间
- 完全加载时间

```yaml
# 页面加载时间监控
page_load_total: 总加载时间
page_load_dom: DOM 加载时间
page_load_first_paint: 首屏渲染时间
page_load_complete: 完全加载时间
```

##### 资源加载时间

- CSS 加载时间
- JS 加载时间
- 图片加载时间
- 字体加载时间

```yaml
# 资源加载时间监控
resource_load_css: CSS 加载时间
resource_load_js: JS 加载时间
resource_load_image: 图片加载时间
resource_load_font: 字体加载时间
```

#### 4.2 用户行为监控

监控用户行为：

##### 用户访问路径

- 访问页面
- 访问顺序
- 访问深度
- 访问时长

```yaml
# 用户访问路径监控
user_visit_pages: 访问页面
user_visit_sequence: 访问顺序
user_visit_depth: 访问深度
user_visit_duration: 访问时长
```

##### 用户交互行为

- 点击行为
- 滚动行为
- 表单提交
- 搜索行为

```yaml
# 用户交互行为监控
user_interaction_click: 点击行为
user_interaction_scroll: 滚动行为
user_interaction_form_submit: 表单提交
user_interaction_search: 搜索行为
```

### 5. 安全监控

监控安全相关指标：

```yaml
# 示例：安全监控配置
groups:
  - name: security.rules
    rules:
      - alert: FailedLoginAttempts
        expr: rate(failed_login_attempts_total[5m]) > 10
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High number of failed login attempts"
          description: "{{ $value }} failed login attempts per second"
      
      - alert: SQLInjectionAttempts
        expr: rate(sql_injection_attempts_total[5m]) > 5
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "SQL injection attempts detected"
          description: "{{ $value }} SQL injection attempts per second"
      
      - alert: XSSAttempts
        expr: rate(xss_attempts_total[5m]) > 5
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "XSS attempts detected"
          description: "{{ $value }} XSS attempts per second"
```

#### 5.1 访问控制监控

监控访问控制：

##### 登录监控

- 登录成功数
- 登录失败数
- 登录失败率
- 异常登录

```yaml
# 登录监控指标
login_success: 登录成功数
login_failure: 登录失败数
login_failure_rate: 登录失败率
login_anomaly: 异常登录
```

##### 权限监控

- 权限访问数
- 权限拒绝数
- 权限提升尝试
- 未授权访问

```yaml
# 权限监控指标
permission_access: 权限访问数
permission_denied: 权限拒绝数
permission_escalation: 权限提升尝试
unauthorized_access: 未授权访问
```

#### 5.2 攻击监控

监控攻击行为：

##### SQL 注入监控

- SQL 注入尝试数
- SQL 注入成功率
- SQL 注入来源
- SQL 注入类型

```yaml
# SQL 注入监控指标
sql_injection_attempts: SQL 注入尝试数
sql_injection_success: SQL 注入成功率
sql_injection_source: SQL 注入来源
sql_injection_type: SQL 注入类型
```

##### XSS 攻击监控

- XSS 攻击尝试数
- XSS 攻击成功率
- XSS 攻击来源
- XSS 攻击类型

```yaml
# XSS 攻击监控指标
xss_attempts: XSS 攻击尝试数
xss_success: XSS 攻击成功率
xss_source: XSS 攻击来源
xss_type: XSS 攻击类型
```

### 6. 容量监控

监控容量相关指标：

```yaml
# 示例：容量监控配置
groups:
  - name: capacity.rules
    rules:
      - alert: HighCPUUsage
        expr: cpu_usage_percent > 80
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High CPU usage"
          description: "CPU usage is {{ $value }}%"
      
      - alert: HighMemoryUsage
        expr: memory_usage_percent > 80
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High memory usage"
          description: "Memory usage is {{ $value }}%"
      
      - alert: HighDiskUsage
        expr: disk_usage_percent > 80
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High disk usage"
          description: "Disk usage is {{ $value }}%"
```

#### 6.1 资源容量监控

监控资源容量：

##### CPU 容量

- CPU 使用率
- CPU 剩余容量
- CPU 峰值使用率
- CPU 平均使用率

```yaml
# CPU 容量监控指标
cpu_usage_percent: CPU 使用率
cpu_capacity_remaining: CPU 剩余容量
cpu_usage_peak: CPU 峰值使用率
cpu_usage_avg: CPU 平均使用率
```

##### 内存容量

- 内存使用率
- 内存剩余容量
- 内存峰值使用率
- 内存平均使用率

```yaml
# 内存容量监控指标
memory_usage_percent: 内存使用率
memory_capacity_remaining: 内存剩余容量
memory_usage_peak: 内存峰值使用率
memory_usage_avg: 内存平均使用率
```

##### 存储容量

- 存储使用率
- 存储剩余容量
- 存储增长趋势
- 存储预测

```yaml
# 存储容量监控指标
storage_usage_percent: 存储使用率
storage_capacity_remaining: 存储剩余容量
storage_growth_trend: 存储增长趋势
storage_forecast: 存储预测
```

#### 6.2 业务容量监控

监控业务容量：

##### 用户容量

- 用户数量
- 用户增长趋势
- 用户容量预测
- 用户容量预警

```yaml
# 用户容量监控指标
users_total: 用户数量
users_growth_trend: 用户增长趋势
users_capacity_forecast: 用户容量预测
users_capacity_warning: 用户容量预警
```

##### 请求容量

- 请求量
- 请求增长趋势
- 请求容量预测
- 请求容量预警

```yaml
# 请求容量监控指标
requests_total: 请求量
requests_growth_trend: 请求增长趋势
requests_capacity_forecast: 请求容量预测
requests_capacity_warning: 请求容量预警
```

## 监控指标分类

### 1. 基础指标

基础指标是监控系统最基础的指标：

- 计数器（Counter）：单调递增的值
- 仪表（Gauge）：可增可减的值
- 直方图（Histogram）：分布统计
- 摘要（Summary）：分位数统计

```yaml
# 基础指标示例
counter_requests_total: 总请求数（计数器）
gauge_cpu_usage: CPU 使用率（仪表）
histogram_request_duration: 请求持续时间分布（直方图）
summary_response_time: 响应时间摘要（摘要）
```

### 2. 聚合指标

聚合指标是通过对基础指标进行聚合得到的指标：

- 求和（Sum）
- 平均（Avg）
- 最大（Max）
- 最小（Min）

```yaml
# 聚合指标示例
sum_requests_total: 总请求数（求和）
avg_cpu_usage: 平均 CPU 使用率（平均）
max_response_time: 最大响应时间（最大）
min_response_time: 最小响应时间（最小）
```

### 3. 派生指标

派生指标是通过对基础指标进行计算得到的指标：

- 比率（Rate）
- 增长率（Growth Rate）
- 百分比（Percentage）
- 分位数（Quantile）

```yaml
# 派生指标示例
rate_requests_per_second: 每秒请求数（比率）
growth_rate_users: 用户增长率（增长率）
percentage_error_rate: 错误率百分比（百分比）
quantile_response_time_p95: 95 分位响应时间（分位数）
```

## 总结

一个完善的监控体系需要监控系统的各个方面，包括基础设施监控、应用监控、业务监控、用户体验监控、安全监控和容量监控。基础设施监控包括服务器监控、网络监控和存储监控。应用监控包括应用性能监控、应用健康监控和应用日志监控。业务监控包括业务指标监控和业务流程监控。用户体验监控包括页面性能监控和用户行为监控。安全监控包括访问控制监控和攻击监控。容量监控包括资源容量监控和业务容量监控。监控指标可以分为基础指标、聚合指标和派生指标。通过全方位的监控，可以及时发现和解决问题，提高系统的稳定性和可靠性。