# Git开发实践

本目录包含Git相关的技术文档，涵盖了Git的核心概念、区域管理和日常开发中的代码提交实践。

## 文档列表

### 1. [Git中5个区，和具体操作？](git-five-areas.md)
详细介绍Git的五个区域及其相互关系，以及各区域之间的操作命令。

**主要内容：**
- Git的五个区域概述
- 工作区、暂存区、本地仓库、远程仓库、远程跟踪分支
- 各区域的详细说明和操作命令
- 区域间的操作流程
- 常见场景操作
- 高级操作技巧

**适合人群：** 想要深入理解Git工作原理的开发者

---

### 2. [平时是怎么提交代码的？](git-commit-workflow.md)
详细讲解日常开发中的代码提交流程和最佳实践。

**主要内容：**
- 提交前准备工作
- 基本提交流程
- Conventional Commits提交信息规范
- Git Flow和GitHub Flow分支策略
- 日常提交场景
- 提交前检查清单
- 常见问题和解决方案
- 最佳实践

**适合人群：** 想要规范代码提交流程的开发者

---

## 学习路径建议

### 初学者
1. 先阅读 [Git中5个区，和具体操作？](git-five-areas.md)，了解Git的基本概念和工作原理
2. 然后阅读 [平时是怎么提交代码的？](git-commit-workflow.md)，学习基本的代码提交流程

### 进阶开发者
1. 深入学习Git的五个区域及其相互关系
2. 掌握Conventional Commits提交信息规范
3. 学习Git Flow和GitHub Flow分支策略
4. 熟练掌握各种Git命令和操作

### 高级开发者
1. 研究Git的高级操作技巧
2. 制定团队的Git工作流程规范
3. 优化代码提交和审查流程
4. 深入研究Git的内部实现原理

## 核心概念

### Git的五个区域

| 区域 | 说明 | 常用命令 |
|------|------|----------|
| 工作区 | 当前编辑的文件所在目录 | `git status`, `git diff` |
| 暂存区 | 准备提交的文件集合 | `git add`, `git diff --staged` |
| 本地仓库 | 存储提交历史的数据库 | `git commit`, `git log` |
| 远程仓库 | 远程服务器上的代码仓库 | `git push`, `git pull` |
| 远程跟踪分支 | 本地对远程仓库分支的引用 | `git branch -r`, `git fetch` |

### Conventional Commits规范

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type类型：**
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构代码
- `perf`: 性能优化
- `test`: 测试相关
- `chore`: 构建过程或辅助工具
- `build`: 构建系统或外部依赖
- `ci`: CI配置文件和脚本
- `revert`: 回退提交

### 分支策略

#### Git Flow
```
master (主分支)
    │
    ├─ develop (开发分支)
    │       │
    │       ├─ feature/* (功能分支)
    │       │
    │       ├─ release/* (发布分支)
    │       │
    │       └─ hotfix/* (修复分支)
    │
    └─ tags (版本标签)
```

#### GitHub Flow
```
master (主分支)
    │
    └─ feature/* (功能分支)
```

## 常用命令速查

### 基本操作

```bash
# 查看状态
git status

# 添加文件到暂存区
git add filename.java
git add .

# 提交到本地仓库
git commit -m "提交信息"

# 推送到远程仓库
git push origin master

# 拉取远程代码
git pull origin master
```

### 分支操作

```bash
# 创建分支
git branch feature-branch

# 切换分支
git checkout feature-branch

# 创建并切换分支
git checkout -b feature-branch

# 合并分支
git merge feature-branch

# 删除分支
git branch -d feature-branch
```

### 查看操作

```bash
# 查看提交历史
git log

# 查看提交历史（简化版）
git log --oneline

# 查看提交历史（图形化）
git log --graph --oneline

# 查看差异
git diff
git diff --staged
git diff HEAD
```

### 撤销操作

```bash
# 撤销工作区修改
git checkout -- filename.java

# 撤销暂存区修改
git reset HEAD filename.java

# 撤销提交
git reset --soft HEAD~1
git reset --hard HEAD~1

# 修改最后一次提交
git commit --amend
```

## 实践建议

### 日常开发

1. **小步提交**: 每完成一个小功能或修复一个bug就提交
2. **频繁提交**: 保持提交的频率，避免大量代码堆积
3. **原子提交**: 每次提交只做一件事，保持提交的原子性
4. **提交前检查**: 提交前运行代码检查和测试
5. **关联Issue**: 提交信息中关联相关的Issue编号

### 团队协作

1. **遵循规范**: 使用Conventional Commits规范编写提交信息
2. **代码审查**: 提交前进行代码审查
3. **分支策略**: 采用合适的分支策略（Git Flow或GitHub Flow）
4. **冲突解决**: 及时解决代码冲突，保持代码库整洁
5. **文档更新**: 代码修改时同步更新文档

### 最佳实践

1. **提交信息**: 清晰描述做了什么，遵循Conventional Commits规范
2. **提交频率**: 保持提交的频率，避免大量代码堆积
3. **提交内容**: 每次提交只包含相关的代码修改
4. **提交前检查**: 提交前运行代码检查和测试
5. **代码审查**: 提交前进行代码审查

## 常见问题

### 1. 提交后发现错误

```bash
# 修改最后一次提交
git add UserService.java
git commit --amend -m "fix(user): correct user validation logic"

# 强制推送到远程仓库
git push -f origin master
```

### 2. 提交了错误的文件

```bash
# 从最后一次提交中移除文件
git reset HEAD~1
git reset UserService.java
git commit -m "feat(user): add user service"

# 强制推送到远程仓库
git push -f origin master
```

### 3. 提交信息写错了

```bash
# 修改最后一次提交信息
git commit --amend -m "feat(user): add user registration"

# 强制推送到远程仓库
git push -f origin master
```

### 4. 需要合并多个提交

```bash
# 交互式rebase
git rebase -i HEAD~3

# 在编辑器中将后两次提交的pick改为squash

# 保存并编辑提交信息

# 强制推送到远程仓库
git push -f origin master
```

### 5. 需要拆分提交

```bash
# 重置到指定提交
git reset HEAD~1

# 分别添加文件
git add UserService.java
git commit -m "feat(user): add user service"

git add UserValidator.java
git commit -m "feat(user): add user validation"

# 推送到远程仓库
git push -f origin master
```

## 相关资源

### 官方文档
- [Git官方文档](https://git-scm.com/doc)
- [Pro Git书籍](https://git-scm.com/book/zh/v2)
- [Conventional Commits规范](https://www.conventionalcommits.org/)

### 推荐工具
- [GitHub](https://github.com/)
- [GitLab](https://gitlab.com/)
- [Bitbucket](https://bitbucket.org/)
- [SourceTree](https://www.sourcetreeapp.com/)
- [GitKraken](https://www.gitkraken.com/)

### 推荐书籍
- 《Pro Git》
- 《Git权威指南》
- 《版本控制之道》

### 相关技术
- GitHub Actions
- GitLab CI/CD
- Jenkins
- 代码审查工具

## 贡献

欢迎对本文档提出改进建议和补充内容。

## 许可

本文档仅供学习和参考使用。