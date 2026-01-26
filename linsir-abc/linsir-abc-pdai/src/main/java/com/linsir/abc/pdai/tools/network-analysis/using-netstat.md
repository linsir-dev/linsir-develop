# 如何使用 netstat 查看服务及监听端口？

netstat（Network Statistics）是一个强大的网络工具，用于显示网络连接、路由表、接口统计信息、伪装连接和多播成员资格等。它是网络管理员和系统管理员的必备工具之一，可以帮助我们监控网络状态、排查网络问题、识别可疑连接等。本文将详细介绍如何使用 netstat 查看服务及监听端口。

## 目录

1. [netstat 工具简介](#netstat-工具简介)
2. [netstat 命令的基本语法](#netstat-命令的基本语法)
3. [netstat 常用选项](#netstat-常用选项)
4. [如何使用 netstat 查看监听端口](#如何使用-netstat-查看监听端口)
5. [如何使用 netstat 查看网络连接](#如何使用-netstat-查看网络连接)
6. [如何使用 netstat 查看路由表](#如何使用-netstat-查看路由表)
7. [如何使用 netstat 查看接口统计信息](#如何使用-netstat-查看接口统计信息)
8. [如何使用 netstat 查看进程与端口的关联](#如何使用-netstat-查看进程与端口的关联)
9. [netstat 命令的高级用法](#netstat-命令的高级用法)
10. [netstat 与其他工具的对比](#netstat-与其他工具的对比)
11. [netstat 常见问题和解决方案](#netstat-常见问题和解决方案)
12. [案例分析](#案例分析)
13. [总结](#总结)

## netstat 工具简介

netstat 是一个命令行工具，可用于显示网络连接、路由表、接口统计信息等网络相关信息。它在 Windows、Linux、macOS 等操作系统中都有提供，虽然在不同系统中的具体实现和选项可能略有不同，但基本功能是相似的。

netstat 的主要功能包括：

- 显示活动的网络连接
- 显示监听的端口
- 显示路由表
- 显示网络接口的统计信息
- 显示网络协议的统计信息
- 显示进程与端口的关联

## netstat 命令的基本语法

netstat 命令的基本语法如下：

### Linux/macOS 系统

```bash
netstat [选项]
```

### Windows 系统

```cmd
netstat [选项]
```

## netstat 常用选项

netstat 命令有很多选项，以下是一些常用的选项：

### 通用选项

| 选项 | 描述 |
|------|------|
| `-a` 或 `--all` | 显示所有连接和监听端口 |
| `-t` 或 `--tcp` | 显示 TCP 连接 |
| `-u` 或 `--udp` | 显示 UDP 连接 |
| `-n` 或 `--numeric` | 以数字形式显示地址和端口号，不进行域名解析 |
| `-l` 或 `--listening` | 显示监听状态的端口 |
| `-p` 或 `--programs` | 显示与连接关联的进程 ID 和进程名（需要 root 权限） |
| `-r` 或 `--route` | 显示路由表 |
| `-s` 或 `--statistics` | 显示网络协议的统计信息 |
| `-i` 或 `--interfaces` | 显示网络接口的统计信息 |
| `-c` 或 `--continuous` | 持续显示网络状态，每隔一秒刷新一次 |

### Windows 系统特有选项

| 选项 | 描述 |
|------|------|
| `-o` | 显示与连接关联的进程 ID |
| `-b` | 显示与连接关联的可执行文件和 DLL 文件（需要管理员权限） |
| `-f` | 显示完全限定域名（FQDN） |
| `-e` | 显示以太网统计信息 |
| `-s` | 显示每个协议的统计信息 |
| `-x` | 显示 NetworkDirect 连接、监听和共享端点 |
| `-y` | 显示所有连接的 TCP 连接模板 |

## 如何使用 netstat 查看监听端口

监听端口是指应用程序正在等待连接的端口。查看监听端口可以帮助我们了解系统上运行的服务，以及是否有未授权的服务在运行。

### 查看所有监听端口

使用 `-a` 和 `-l` 选项可以查看所有监听端口：

#### Linux/macOS 系统

```bash
netstat -al
```

#### Windows 系统

```cmd
netstat -a
```

### 查看 TCP 监听端口

使用 `-t` 和 `-l` 选项可以查看 TCP 监听端口：

#### Linux/macOS 系统

```bash
netstat -tl
```

#### Windows 系统

```cmd
netstat -a -t
```

### 查看 UDP 监听端口

使用 `-u` 和 `-l` 选项可以查看 UDP 监听端口：

#### Linux/macOS 系统

```bash
netstat -ul
```

#### Windows 系统

```cmd
netstat -a -u
```

### 以数字形式查看监听端口

使用 `-n` 选项可以以数字形式显示地址和端口号，不进行域名解析，这样可以加快命令的执行速度：

#### Linux/macOS 系统

```bash
netstat -tln  # 查看 TCP 监听端口（数字形式）
netstat -uln  # 查看 UDP 监听端口（数字形式）
```

#### Windows 系统

```cmd
netstat -a -t -n  # 查看 TCP 监听端口（数字形式）
netstat -a -u -n  # 查看 UDP 监听端口（数字形式）
```

### 查看监听端口并显示进程信息

使用 `-p` 选项可以显示与监听端口关联的进程 ID 和进程名，这样可以知道是哪个应用程序在使用该端口：

#### Linux/macOS 系统

```bash
netstat -tlnp  # 查看 TCP 监听端口并显示进程信息
netstat -ulnp  # 查看 UDP 监听端口并显示进程信息
```

#### Windows 系统

```cmd
netstat -a -t -o  # 查看 TCP 监听端口并显示进程 ID
netstat -a -u -o  # 查看 UDP 监听端口并显示进程 ID
```

## 如何使用 netstat 查看网络连接

网络连接是指当前正在进行的网络通信，包括已建立的连接、正在建立的连接、已关闭的连接等。查看网络连接可以帮助我们了解系统的网络活动，以及是否有可疑的连接。

### 查看所有网络连接

使用 `-a` 选项可以查看所有网络连接：

#### Linux/macOS 系统

```bash
netstat -a
```

#### Windows 系统

```cmd
netstat -a
```

### 查看 TCP 网络连接

使用 `-t` 选项可以查看 TCP 网络连接：

#### Linux/macOS 系统

```bash
netstat -t
```

#### Windows 系统

```cmd
netstat -a -t
```

### 查看 UDP 网络连接

使用 `-u` 选项可以查看 UDP 网络连接：

#### Linux/macOS 系统

```bash
netstat -u
```

#### Windows 系统

```cmd
netstat -a -u
```

### 以数字形式查看网络连接

使用 `-n` 选项可以以数字形式显示地址和端口号，不进行域名解析：

#### Linux/macOS 系统

```bash
netstat -tn  # 查看 TCP 网络连接（数字形式）
netstat -un  # 查看 UDP 网络连接（数字形式）
```

#### Windows 系统

```cmd
netstat -a -t -n  # 查看 TCP 网络连接（数字形式）
netstat -a -u -n  # 查看 UDP 网络连接（数字形式）
```

### 查看网络连接并显示进程信息

使用 `-p` 选项可以显示与网络连接关联的进程 ID 和进程名：

#### Linux/macOS 系统

```bash
netstat -tnp  # 查看 TCP 网络连接并显示进程信息
netstat -unp  # 查看 UDP 网络连接并显示进程信息
```

#### Windows 系统

```cmd
netstat -a -t -o  # 查看 TCP 网络连接并显示进程 ID
netstat -a -u -o  # 查看 UDP 网络连接并显示进程 ID
```

## 如何使用 netstat 查看路由表

路由表是指网络设备用来决定数据包转发路径的表格。查看路由表可以帮助我们了解网络的拓扑结构，以及数据包的转发路径。

使用 `-r` 选项可以查看路由表：

### Linux/macOS 系统

```bash
netstat -r
```

### Windows 系统

```cmd
netstat -r
```

使用 `-n` 选项可以以数字形式显示路由表，不进行域名解析：

### Linux/macOS 系统

```bash
netstat -rn
```

### Windows 系统

```cmd
netstat -r -n
```

## 如何使用 netstat 查看接口统计信息

接口统计信息包括网络接口的接收和发送数据包的数量、错误数、丢弃数等。查看接口统计信息可以帮助我们了解网络接口的状态，以及是否有网络故障。

### Linux/macOS 系统

使用 `-i` 选项可以查看网络接口的统计信息：

```bash
netstat -i
```

使用 `-s` 选项可以查看网络协议的统计信息：

```bash
netstat -s
```

### Windows 系统

使用 `-e` 选项可以查看以太网统计信息：

```cmd
netstat -e
```

使用 `-s` 选项可以查看每个协议的统计信息：

```cmd
netstat -s
```

## 如何使用 netstat 查看进程与端口的关联

查看进程与端口的关联可以帮助我们了解哪个进程在使用哪个端口，以及是否有未授权的进程在使用端口。

### Linux/macOS 系统

使用 `-p` 选项可以显示与连接关联的进程 ID 和进程名：

```bash
netstat -tlnp  # 查看 TCP 监听端口与进程的关联
netstat -ulnp  # 查看 UDP 监听端口与进程的关联
netstat -tnp   # 查看 TCP 连接与进程的关联
netstat -unp   # 查看 UDP 连接与进程的关联
```

### Windows 系统

使用 `-o` 选项可以显示与连接关联的进程 ID：

```cmd
netstat -a -t -o  # 查看 TCP 连接与进程的关联
netstat -a -u -o  # 查看 UDP 连接与进程的关联
```

使用 `-b` 选项可以显示与连接关联的可执行文件和 DLL 文件（需要管理员权限）：

```cmd
netstat -a -t -b  # 查看 TCP 连接与可执行文件的关联
netstat -a -u -b  # 查看 UDP 连接与可执行文件的关联
```

## netstat 命令的高级用法

### 持续显示网络状态

使用 `-c` 选项可以持续显示网络状态，每隔一秒刷新一次，这对于监控网络活动非常有用：

#### Linux/macOS 系统

```bash
netstat -tlnp -c  # 持续查看 TCP 监听端口与进程的关联
```

### 过滤特定端口的连接

使用管道和 grep 命令可以过滤特定端口的连接：

#### Linux/macOS 系统

```bash
netstat -tlnp | grep 80  # 查看使用端口 80 的进程
netstat -tnp | grep 443  # 查看使用端口 443 的连接
```

#### Windows 系统

在 Windows 系统中，可以使用 findstr 命令来过滤：

```cmd
netstat -a -n | findstr :80  # 查看使用端口 80 的连接
netstat -a -n | findstr :443  # 查看使用端口 443 的连接
```

### 过滤特定 IP 地址的连接

使用管道和 grep 命令可以过滤特定 IP 地址的连接：

#### Linux/macOS 系统

```bash
netstat -tnp | grep 192.168.1.1  # 查看与 IP 地址 192.168.1.1 相关的连接
```

#### Windows 系统

```cmd
netstat -a -n | findstr 192.168.1.1  # 查看与 IP 地址 192.168.1.1 相关的连接
```

### 查看 ESTABLISHED 状态的连接

使用管道和 grep 命令可以查看 ESTABLISHED 状态的连接：

#### Linux/macOS 系统

```bash
netstat -tnp | grep ESTABLISHED  # 查看已建立的 TCP 连接
```

#### Windows 系统

```cmd
netstat -a -n | findstr ESTABLISHED  # 查看已建立的 TCP 连接
```

## netstat 与其他工具的对比

netstat 是一个传统的网络工具，但在现代系统中，有一些新的工具可以替代它，或者提供更多的功能。以下是 netstat 与一些现代网络工具的对比：

### netstat vs ss

ss 是 Linux 系统中的一个现代网络工具，它是 iproute2 包的一部分，用于替代 netstat。ss 比 netstat 更快，尤其是在处理大量连接时。

**ss 的优势**：
- 速度更快，尤其是在处理大量连接时
- 提供更多的选项和功能
- 支持更多的过滤条件

**ss 的基本用法**：

```bash
ss -tln  # 查看 TCP 监听端口
ss -uln  # 查看 UDP 监听端口
ss -tnp  # 查看 TCP 连接并显示进程信息
ss -rn   # 查看路由表
```

### netstat vs lsof

lsof（List Open Files）是一个用于显示系统中打开文件的工具，它也可以用来查看网络连接和监听端口。

**lsof 的优势**：
- 可以显示更多关于文件和进程的信息
- 支持更多的过滤条件

**lsof 的基本用法**：

```bash
lsof -i :80  # 查看使用端口 80 的进程
lsof -i tcp  # 查看所有 TCP 连接
lsof -i udp  # 查看所有 UDP 连接
```

### netstat vs netstat-nat

netstat-nat 是一个用于查看网络地址转换（NAT）连接的工具。

**netstat-nat 的优势**：
- 专门用于查看 NAT 连接
- 提供更多关于 NAT 连接的信息

**netstat-nat 的基本用法**：

```bash
netstat-nat  # 查看所有 NAT 连接
netstat-nat -n  # 以数字形式显示 NAT 连接
```

## netstat 常见问题和解决方案

### 问题 1：netstat 命令执行速度慢

**原因**：netstat 命令默认会进行域名解析，将 IP 地址转换为域名，这会导致命令执行速度变慢。

**解决方案**：使用 `-n` 选项，以数字形式显示地址和端口号，不进行域名解析：

```bash
netstat -tn  # 查看 TCP 网络连接（数字形式）
netstat -uln  # 查看 UDP 监听端口（数字形式）
```

### 问题 2：netstat 命令无法显示进程信息

**原因**：在 Linux/macOS 系统中，使用 `-p` 选项显示进程信息需要 root 权限；在 Windows 系统中，使用 `-b` 选项显示可执行文件信息需要管理员权限。

**解决方案**：以 root 或管理员身份运行 netstat 命令：

#### Linux/macOS 系统

```bash
sudo netstat -tlnp  # 以 root 身份查看 TCP 监听端口与进程的关联
```

#### Windows 系统

以管理员身份打开命令提示符，然后运行：

```cmd
netstat -a -t -b  # 以管理员身份查看 TCP 连接与可执行文件的关联
```

### 问题 3：netstat 命令显示的连接状态不明确

**原因**：netstat 命令显示的连接状态可能使用缩写，导致不明确。

**解决方案**：了解 TCP 连接状态的缩写含义：

| 状态缩写 | 状态含义 |
|---------|---------|
| `LISTEN` | 监听状态，等待连接 |
| `SYN_SENT` | 已发送 SYN 报文，等待 SYN-ACK 报文 |
| `SYN_RECV` | 已接收 SYN 报文，发送了 SYN-ACK 报文，等待 ACK 报文 |
| `ESTABLISHED` | 连接已建立，数据传输中 |
| `FIN_WAIT1` | 已发送 FIN 报文，等待 FIN-ACK 报文 |
| `FIN_WAIT2` | 已接收 FIN-ACK 报文，等待 FIN 报文 |
| `TIME_WAIT` | 连接已关闭，等待 2MSL 时间 |
| `CLOSE_WAIT` | 已接收 FIN 报文，等待应用程序关闭连接 |
| `LAST_ACK` | 已发送 FIN-ACK 报文，等待 ACK 报文 |
| `CLOSED` | 连接已关闭 |

### 问题 4：netstat 命令在 Windows 系统中显示的信息过多

**原因**：netstat 命令默认会显示所有连接和监听端口，包括 IPv4 和 IPv6 的连接。

**解决方案**：使用过滤选项，只显示需要的信息：

```cmd
netstat -a -t -n | findstr LISTEN  # 只查看 TCP 监听端口（数字形式）
netstat -a -n | findstr :80  # 只查看使用端口 80 的连接
```

## 案例分析

### 案例 1：识别未授权的服务

**背景**：系统管理员发现服务器上有一个未授权的服务在运行，需要确定该服务使用的端口和进程。

**解决方案**：使用 netstat 命令查看监听端口和进程信息：

#### Linux/macOS 系统

```bash
sudo netstat -tlnp  # 查看 TCP 监听端口与进程的关联
sudo netstat -ulnp  # 查看 UDP 监听端口与进程的关联
```

#### Windows 系统

以管理员身份打开命令提示符，然后运行：

```cmd
netstat -a -t -b  # 查看 TCP 连接与可执行文件的关联
netstat -a -u -b  # 查看 UDP 连接与可执行文件的关联
```

**结果**：通过查看 netstat 的输出，系统管理员找到了未授权的服务使用的端口和进程，然后终止了该进程，并移除了相关的文件。

### 案例 2：排查网络连接问题

**背景**：用户报告无法访问服务器上的某个服务，系统管理员需要排查网络连接问题。

**解决方案**：

1. **检查服务是否在监听**：
   
   #### Linux/macOS 系统
   
   ```bash
   netstat -tlnp | grep 端口号  # 查看服务是否在监听指定端口
   ```
   
   #### Windows 系统
   
   ```cmd
   netstat -a -t -n | findstr :端口号  # 查看服务是否在监听指定端口
   ```

2. **检查是否有防火墙规则阻止连接**：
   
   使用防火墙工具（如 iptables、firewalld 或 Windows 防火墙）检查是否有规则阻止连接。

3. **检查网络连接状态**：
   
   #### Linux/macOS 系统
   
   ```bash
   netstat -tnp | grep 端口号  # 查看与指定端口相关的连接
   ```
   
   #### Windows 系统
   
   ```cmd
   netstat -a -t -n | findstr :端口号  # 查看与指定端口相关的连接
   ```

**结果**：通过排查，系统管理员发现防火墙规则阻止了连接，修改防火墙规则后，用户可以正常访问服务。

### 案例 3：监控网络活动

**背景**：系统管理员需要监控服务器的网络活动，特别是与外部服务器的连接。

**解决方案**：使用 netstat 命令持续显示网络状态：

#### Linux/macOS 系统

```bash
netstat -tnp -c  # 持续查看 TCP 连接与进程的关联
```

#### Windows 系统

在 Windows 系统中，可以使用 PowerShell 脚本来持续显示网络状态：

```powershell
while ($true) {
    Clear-Host
    netstat -a -t -n
    Start-Sleep -Seconds 1
}
```

**结果**：系统管理员通过持续监控网络状态，发现了一个异常的连接，及时采取了措施，避免了潜在的安全问题。

## 总结

netstat 是一个强大的网络工具，用于显示网络连接、路由表、接口统计信息等网络相关信息。它是网络管理员和系统管理员的必备工具之一，可以帮助我们监控网络状态、排查网络问题、识别可疑连接等。

本文介绍了 netstat 命令的基本语法、常用选项，以及如何使用 netstat 查看服务及监听端口、网络连接、路由表、接口统计信息等。同时，本文还介绍了 netstat 命令的高级用法、与其他工具的对比、常见问题和解决方案，以及实际案例分析。

虽然在现代系统中，有一些新的工具可以替代 netstat，或者提供更多的功能，但 netstat 仍然是一个非常有用的工具，特别是在一些旧系统中，或者在需要快速查看网络状态时。

通过掌握 netstat 命令的使用方法，我们可以更好地监控和管理网络，及时发现和解决网络问题，提高系统的安全性和可靠性。
