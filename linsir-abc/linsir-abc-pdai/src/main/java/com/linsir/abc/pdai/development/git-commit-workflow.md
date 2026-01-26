# 平时是怎么提交代码的？

## 1. 概述

代码提交是日常开发中最频繁的操作之一。良好的提交习惯和规范能够提高团队协作效率，便于代码审查和问题追踪。本文将详细介绍日常开发中的代码提交流程和最佳实践。

## 2. 提交前准备

### 2.1 查看当前状态

```bash
# 查看工作区状态
git status

# 查看未跟踪的文件
git status --short

# 查看修改的文件
git diff

# 查看暂存区的文件
git diff --staged
```

### 2.2 检查代码质量

```bash
# 运行代码检查工具
npm run lint
mvn checkstyle:check
gradle checkstyleMain

# 运行单元测试
npm test
mvn test
gradle test

# 运行集成测试
mvn verify
gradle integrationTest
```

### 2.3 清理不必要的文件

```bash
# 查看未跟踪的文件
git clean -n

# 清理未跟踪的文件
git clean -f

# 清理未跟踪的文件和目录
git clean -fd
```

## 3. 基本提交流程

### 3.1 单文件提交

```bash
# 1. 修改文件
echo "new code" >> UserService.java

# 2. 查看修改
git diff UserService.java

# 3. 添加文件到暂存区
git add UserService.java

# 4. 提交到本地仓库
git commit -m "Add user service method"

# 5. 推送到远程仓库
git push origin master
```

### 3.2 多文件提交

```bash
# 1. 修改多个文件
echo "code1" >> Service1.java
echo "code2" >> Service2.java
echo "code3" >> Service3.java

# 2. 查看所有修改
git diff

# 3. 添加所有修改的文件
git add .

# 4. 提交到本地仓库
git commit -m "Add multiple services"

# 5. 推送到远程仓库
git push origin master
```

### 3.3 部分文件提交

```bash
# 1. 修改多个文件
echo "code1" >> Service1.java
echo "code2" >> Service2.java
echo "code3" >> Service3.java

# 2. 只添加部分文件
git add Service1.java Service2.java

# 3. 提交到本地仓库
git commit -m "Add service1 and service2"

# 4. 添加剩余文件
git add Service3.java

# 5. 再次提交
git commit -m "Add service3"

# 6. 推送到远程仓库
git push origin master
```

## 4. 提交信息规范

### 4.1 Conventional Commits规范

Conventional Commits是一种提交信息规范，格式如下：

```
<type>(<scope>): <subject>

<body>

<footer>
```

#### 4.1.1 Type类型

| Type | 说明 | 示例 |
|------|------|------|
| feat | 新功能 | feat: add user authentication |
| fix | 修复bug | fix: resolve login issue |
| docs | 文档更新 | docs: update API documentation |
| style | 代码格式调整 | style: format code with prettier |
| refactor | 重构代码 | refactor: simplify user service |
| perf | 性能优化 | perf: improve database query |
| test | 测试相关 | test: add unit tests for user service |
| chore | 构建过程或辅助工具 | chore: update dependencies |
| build | 构建系统或外部依赖 | build: upgrade webpack to version 5 |
| ci | CI配置文件和脚本 | ci: add GitHub Actions workflow |
| revert | 回退提交 | revert: feat(user): add user authentication |

#### 4.1.2 Scope范围

Scope用于说明提交影响的模块或组件。

```bash
# 示例
feat(user): add user registration
fix(auth): resolve token expiration issue
docs(api): update user API documentation
refactor(service): simplify order service logic
test(controller): add unit tests for product controller
```

#### 4.1.3 Subject主题

Subject是对提交的简短描述，不超过50个字符。

```bash
# 好的示例
feat(user): add user registration
fix(auth): resolve token expiration issue
docs(api): update user API documentation

# 不好的示例
add user registration functionality to the system
fix the issue where token expires too quickly
update the API documentation for user endpoints
```

#### 4.1.4 Body正文

Body是对提交的详细描述，说明做了什么以及为什么这么做。

```bash
feat(user): add user registration

- Add user registration endpoint
- Implement email verification
- Add password encryption
- Create user registration form
```

#### 4.1.5 Footer脚注

Footer用于关联Issue或添加破坏性变更说明。

```bash
feat(user): add user registration

- Add user registration endpoint
- Implement email verification

Closes #123
```

### 4.2 提交信息示例

```bash
# 新功能
feat(user): add user registration

- Add user registration endpoint POST /api/users/register
- Implement email verification with token
- Add password encryption using BCrypt
- Create user registration form with validation

Closes #123

# 修复bug
fix(auth): resolve token expiration issue

- Fix token expiration time calculation
- Update token refresh logic
- Add token validation middleware

Fixes #456

# 文档更新
docs(api): update user API documentation

- Update user registration endpoint documentation
- Add request/response examples
- Update authentication requirements

# 重构
refactor(service): simplify order service logic

- Extract common logic to utility class
- Simplify order processing flow
- Improve code readability

# 性能优化
perf(database): improve query performance

- Add database index on user_id column
- Optimize JOIN queries
- Implement query result caching

# 测试
test(controller): add unit tests for product controller

- Add test cases for product listing
- Add test cases for product details
- Add test cases for product search
```

## 5. 分支策略

### 5.1 Git Flow工作流

Git Flow是一种经典的分支管理策略，包含以下分支：

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

#### 5.1.1 创建功能分支

```bash
# 1. 切换到develop分支
git checkout develop

# 2. 拉取最新代码
git pull origin develop

# 3. 创建功能分支
git checkout -b feature/user-registration

# 4. 开发功能
echo "user registration code" >> UserService.java

# 5. 提交代码
git add UserService.java
git commit -m "feat(user): add user registration"

# 6. 推送到远程仓库
git push -u origin feature/user-registration

# 7. 创建Pull Request到develop分支
```

#### 5.1.2 创建发布分支

```bash
# 1. 切换到develop分支
git checkout develop

# 2. 拉取最新代码
git pull origin develop

# 3. 创建发布分支
git checkout -b release/v1.0.0

# 4. 更新版本号
echo "1.0.0" > VERSION

# 5. 提交代码
git add VERSION
git commit -m "chore: bump version to 1.0.0"

# 6. 推送到远程仓库
git push -u origin release/v1.0.0

# 7. 测试发布版本

# 8. 合并到master分支
git checkout master
git merge release/v1.0.0

# 9. 创建版本标签
git tag -a v1.0.0 -m "Release version 1.0.0"

# 10. 推送标签
git push origin v1.0.0

# 11. 合并到develop分支
git checkout develop
git merge release/v1.0.0

# 12. 删除发布分支
git branch -d release/v1.0.0
git push origin --delete release/v1.0.0
```

#### 5.1.3 创建修复分支

```bash
# 1. 切换到master分支
git checkout master

# 2. 拉取最新代码
git pull origin master

# 3. 创建修复分支
git checkout -b hotfix/critical-bug

# 4. 修复bug
echo "bug fix" >> BugFix.java

# 5. 提交代码
git add BugFix.java
git commit -m "fix: resolve critical bug"

# 6. 推送到远程仓库
git push -u origin hotfix/critical-bug

# 7. 合并到master分支
git checkout master
git merge hotfix/critical-bug

# 8. 创建版本标签
git tag -a v1.0.1 -m "Hotfix version 1.0.1"

# 9. 推送标签
git push origin v1.0.1

# 10. 合并到develop分支
git checkout develop
git merge hotfix/critical-bug

# 11. 删除修复分支
git branch -d hotfix/critical-bug
git push origin --delete hotfix/critical-bug
```

### 5.2 GitHub Flow工作流

GitHub Flow是一种简化的分支管理策略，只使用master分支和功能分支。

```
master (主分支)
    │
    └─ feature/* (功能分支)
```

#### 5.2.1 创建功能分支

```bash
# 1. 切换到master分支
git checkout master

# 2. 拉取最新代码
git pull origin master

# 3. 创建功能分支
git checkout -b feature/user-registration

# 4. 开发功能
echo "user registration code" >> UserService.java

# 5. 提交代码
git add UserService.java
git commit -m "feat(user): add user registration"

# 6. 推送到远程仓库
git push -u origin feature/user-registration

# 7. 创建Pull Request到master分支

# 8. 代码审查

# 9. 合并到master分支

# 10. 删除功能分支
git checkout master
git branch -d feature/user-registration
git push origin --delete feature/user-registration
```

## 6. 日常提交场景

### 6.1 小步提交

```bash
# 1. 修改文件
echo "code1" >> UserService.java

# 2. 立即提交
git add UserService.java
git commit -m "feat(user): add user service method"

# 3. 继续修改
echo "code2" >> UserService.java

# 4. 再次提交
git add UserService.java
git commit -m "feat(user): add user validation"

# 5. 推送到远程仓库
git push origin master
```

### 6.2 修复提交

```bash
# 1. 发现bug
echo "bug fix" >> UserService.java

# 2. 查看修改
git diff UserService.java

# 3. 提交修复
git add UserService.java
git commit -m "fix(user): resolve null pointer exception"

# 4. 推送到远程仓库
git push origin master
```

### 6.3 重构提交

```bash
# 1. 重构代码
echo "refactored code" >> UserService.java

# 2. 查看修改
git diff UserService.java

# 3. 提交重构
git add UserService.java
git commit -m "refactor(user): simplify user service logic"

# 4. 推送到远程仓库
git push origin master
```

### 6.4 文档提交

```bash
# 1. 更新文档
echo "updated documentation" >> README.md

# 2. 查看修改
git diff README.md

# 3. 提交文档
git add README.md
git commit -m "docs: update user service documentation"

# 4. 推送到远程仓库
git push origin master
```

### 6.5 配置提交

```bash
# 1. 更新配置
echo "new configuration" > application.properties

# 2. 查看修改
git diff application.properties

# 3. 提交配置
git add application.properties
git commit -m "chore: update database configuration"

# 4. 推送到远程仓库
git push origin master
```

## 7. 提交前检查清单

### 7.1 代码质量检查

```bash
# 1. 运行代码检查
npm run lint
mvn checkstyle:check
gradle checkstyleMain

# 2. 运行单元测试
npm test
mvn test
gradle test

# 3. 运行集成测试
mvn verify
gradle integrationTest

# 4. 检查代码覆盖率
npm run coverage
mvn jacoco:report
gradle jacocoTestReport
```

### 7.2 提交信息检查

```bash
# 1. 检查提交信息格式
git log -1 --format="%s"

# 2. 检查提交信息长度
git log -1 --format="%s" | wc -c

# 3. 检查提交信息是否包含Issue编号
git log -1 --format="%s" | grep -E "#[0-9]+"
```

### 7.3 文件检查

```bash
# 1. 检查是否有敏感信息
git diff | grep -i "password\|secret\|token"

# 2. 检查是否有调试代码
git diff | grep -i "console.log\|debugger\|TODO\|FIXME"

# 3. 检查是否有大文件
git diff --stat | grep -E "\s+[0-9]{4,}\s+"
```

## 8. 常见问题和解决方案

### 8.1 提交后发现错误

```bash
# 1. 修改最后一次提交
git add UserService.java
git commit --amend -m "fix(user): correct user validation logic"

# 2. 强制推送到远程仓库
git push -f origin master
```

### 8.2 提交了错误的文件

```bash
# 1. 从最后一次提交中移除文件
git reset HEAD~1
git reset UserService.java
git commit -m "feat(user): add user service"

# 2. 强制推送到远程仓库
git push -f origin master
```

### 8.3 提交信息写错了

```bash
# 1. 修改最后一次提交信息
git commit --amend -m "feat(user): add user registration"

# 2. 强制推送到远程仓库
git push -f origin master
```

### 8.4 需要合并多个提交

```bash
# 1. 交互式rebase
git rebase -i HEAD~3

# 2. 在编辑器中将后两次提交的pick改为squash

# 3. 保存并编辑提交信息

# 4. 强制推送到远程仓库
git push -f origin master
```

### 8.5 需要拆分提交

```bash
# 1. 重置到指定提交
git reset HEAD~1

# 2. 分别添加文件
git add UserService.java
git commit -m "feat(user): add user service"

git add UserValidator.java
git commit -m "feat(user): add user validation"

# 3. 推送到远程仓库
git push -f origin master
```

## 9. 最佳实践

### 9.1 提交频率

- **小步提交**: 每完成一个小功能或修复一个bug就提交
- **频繁提交**: 保持提交的频率，避免大量代码堆积
- **原子提交**: 每次提交只做一件事，保持提交的原子性

### 9.2 提交信息

- **清晰描述**: 提交信息应该清晰描述做了什么
- **使用规范**: 遵循Conventional Commits规范
- **关联Issue**: 提交信息中关联相关的Issue编号
- **避免废话**: 提交信息中避免使用废话和重复信息

### 9.3 提交内容

- **相关代码**: 每次提交只包含相关的代码修改
- **测试代码**: 提交时包含相关的测试代码
- **文档更新**: 代码修改时同步更新文档
- **配置文件**: 配置文件修改单独提交

### 9.4 提交前检查

- **代码检查**: 提交前运行代码检查工具
- **单元测试**: 提交前运行单元测试
- **集成测试**: 提交前运行集成测试
- **代码审查**: 提交前进行代码审查

## 10. 总结

良好的代码提交习惯和规范对于团队协作和代码管理至关重要：

1. **遵循规范**: 使用Conventional Commits规范编写提交信息
2. **小步提交**: 保持提交的原子性，每次提交只做一件事
3. **频繁提交**: 保持提交的频率，避免大量代码堆积
4. **提交前检查**: 提交前运行代码检查和测试
5. **关联Issue**: 提交信息中关联相关的Issue编号
6. **代码审查**: 提交前进行代码审查

掌握这些提交技巧和最佳实践，能够提高开发效率，减少代码冲突，便于代码审查和问题追踪。