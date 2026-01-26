# 网络分析技术文档

本文档汇总了网络分析相关的技术文章，涵盖了网络基础、协议分析、安全攻击和网络工具等方面的内容。

## 目录

1. [网络基础](#网络基础)
2. [TCP/IP协议](#tcpip协议)
3. [DNS相关](#dns相关)
4. [网络安全](#网络安全)
5. [网络工具](#网络工具)
6. [完整流程分析](#完整流程分析)

## 网络基础

### OSI七层网络模型
- **文件**: [osi-network-model.md](osi-network-model.md)
- **内容**: 详细介绍了OSI七层网络模型的每一层功能、协议和数据单位，包括物理层、数据链路层、网络层、传输层、会话层、表示层和应用层。

## TCP/IP协议

### TCP三次握手
- **文件**: [tcp-three-way-handshake.md](tcp-three-way-handshake.md)
- **内容**: 详细解释了TCP连接建立的三次握手过程，包括SYN、SYN-ACK、ACK三个报文的交互过程，以及状态转换和安全考虑。

### TCP四次挥手
- **文件**: [tcp-four-way-handshake.md](tcp-four-way-handshake.md)
- **内容**: 详细解释了TCP连接关闭的四次挥手过程，包括FIN、ACK、FIN、ACK四个报文的交互过程，以及TIME_WAIT状态的作用。

### SYN洪泛攻击
- **文件**: [syn-flood-attack.md](syn-flood-attack.md)
- **内容**: 分析了SYN洪泛攻击的原理、危害和防御措施，包括半开连接、SYN cookie等技术。

## DNS相关

### DNS解析流程
- **文件**: [dns-resolution-process.md](dns-resolution-process.md)
- **内容**: 详细描述了DNS域名解析的完整流程，包括递归查询和迭代查询的过程，以及DNS缓存的作用。

### DNS为什么使用UDP
- **文件**: [dns-over-udp.md](dns-over-udp.md)
- **内容**: 解释了DNS协议选择UDP作为主要传输协议的原因，包括性能、可靠性和历史因素。

### DNS劫持
- **文件**: [dns-hijacking.md](dns-hijacking.md)
- **内容**: 详细介绍了DNS劫持的概念、原理、类型和防范措施，包括本地劫持、路由器劫持和运营商劫持。

### DNS污染
- **文件**: [dns-poisoning.md](dns-poisoning.md)
- **内容**: 详细介绍了DNS污染的概念、类型和防范措施，包括本地DNS污染、骨干网DNS污染和递归服务器DNS污染。

### DNS流量监控
- **文件**: [dns-traffic-monitoring.md](dns-traffic-monitoring.md)
- **内容**: 讨论了DNS流量监控的重要性、监控目标和监控方法，包括网络设备监控、DNS服务器监控和专用工具监控。

## 网络安全

### URL到页面加载的完整过程
- **文件**: [url-to-page-load.md](url-to-page-load.md)
- **内容**: 详细分析了从用户输入URL到页面完全加载的整个过程，包括DNS解析、TCP连接、HTTPS握手、HTTP请求、服务器处理、浏览器渲染等步骤。

## 网络工具

### 使用netstat查看服务和监听端口
- **文件**: [using-netstat.md](using-netstat.md)
- **内容**: 详细介绍了如何使用netstat命令查看网络连接、路由表、接口统计等信息，以及常用的参数和示例。

### 使用TCPDump进行抓包分析
- **文件**: [using-tcpdump.md](using-tcpdump.md)
- **内容**: 详细介绍了如何使用TCPDump命令进行网络数据包捕获和分析，包括常用的参数、过滤表达式和示例。

### 使用Wireshark进行抓包分析
- **文件**: [using-wireshark.md](using-wireshark.md)
- **内容**: 详细介绍了如何使用Wireshark图形化工具进行网络数据包捕获和分析，包括界面介绍、过滤表达式和高级功能。

## 完整流程分析

本部分的文章详细分析了网络通信的完整流程，从URL输入到页面加载，涵盖了DNS解析、TCP连接、HTTP请求等多个环节，帮助读者理解网络通信的全过程。

## 总结

本目录下的文章涵盖了网络分析的各个方面，从基础的OSI模型到具体的协议分析，从网络工具的使用到安全攻击的原理，为读者提供了全面的网络分析知识体系。这些文章不仅介绍了网络技术的基本概念和原理，还提供了实际的工具使用方法和案例分析，适合网络工程师、安全研究人员和开发人员参考学习。