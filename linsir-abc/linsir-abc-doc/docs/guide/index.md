# 指南

欢迎来到 Linsir ABC 指南！

## 概述

Linsir ABC 是一个基于 VitePress 构建的文档站点模板，旨在提供简洁、高效的文档编写体验。

## 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

## 目录结构

```
linsir-abc-doc/
├── docs/                   # 文档目录
│   ├── .vitepress/        # VitePress 配置
│   │   └── config.ts     # 站点配置文件
│   ├── guide/            # 指南文档
│   │   └── index.md     # 指南首页（当前页面）
│   └── index.md          # 站点首页
├── package.json          # 项目依赖
└── .gitignore           # Git 忽略文件
```

## 编写文档

### Markdown 支持

VitePress 支持标准的 Markdown 语法，同时扩展了许多有用的功能：

- **代码块高亮**：支持多种编程语言
- **表格**：清晰的数据展示
- **链接**：内部和外部链接
- **图片**：本地和远程图片

### 示例

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, Linsir ABC!");
    }
}
```

## 下一步

- 在 `docs/guide/` 目录下添加更多文档
- 自定义 `docs/.vitepress/config.ts` 配置
- 部署到 GitHub Pages 或其他平台

开始你的文档之旅吧！
