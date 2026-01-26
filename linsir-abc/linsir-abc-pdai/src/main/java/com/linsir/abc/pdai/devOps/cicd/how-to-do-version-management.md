# 如何做版本管理？

## 版本管理的定义

版本管理（Version Management）是指对软件的不同版本进行管理的过程，包括版本控制、版本号管理、分支管理、发布管理等。版本管理是软件开发的重要组成部分，可以帮助团队协作、追踪变更、回滚错误、发布新版本。

## 版本管理的核心概念

### 1. 版本控制

使用版本控制系统管理代码：

```bash
# Git 基本操作
git init
git add .
git commit -m "Initial commit"
git remote add origin <repository-url>
git push -u origin master
```

### 2. 版本号管理

使用语义化版本号：

```
主版本号.次版本号.修订号
例如：1.2.3
```

### 3. 分支管理

使用分支管理开发流程：

```
master (主分支)
  ├── develop (开发分支)
  │   ├── feature/new-feature (功能分支)
  │   ├── feature/another-feature (功能分支)
  │   ├── release/1.0.0 (发布分支)
  │   └── hotfix/1.0.1 (修复分支)
  └── hotfix/1.0.1 (修复分支)
```

### 4. 发布管理

管理软件的发布流程：

```
开发 → 测试 → 预发布 → 生产
```

## 版本控制系统

### 1. Git

Git 是目前最流行的分布式版本控制系统：

```bash
# Git 基本操作
git clone <repository-url>
git add <file>
git commit -m "Commit message"
git push origin <branch>
git pull origin <branch>
git branch <branch-name>
git checkout <branch-name>
git merge <branch-name>
```

### 2. Git 工作流程

#### 2.1 Git Flow 工作流程

```
master (主分支)
  ├── develop (开发分支)
  │   ├── feature/* (功能分支)
  │   └── release/* (发布分支)
  └── hotfix/* (修复分支)
```

```bash
# Git Flow 命令
git flow init
git flow feature start new-feature
git flow feature finish new-feature
git flow release start 1.0.0
git flow release finish 1.0.0
git flow hotfix start 1.0.1
git flow hotfix finish 1.0.1
```

#### 2.2 GitHub Flow 工作流程

```
master (主分支)
  ├── feature/* (功能分支)
  └── hotfix/* (修复分支)
```

```bash
# GitHub Flow 命令
git checkout -b feature/new-feature
git add .
git commit -m "Add new feature"
git push origin feature/new-feature
# 创建 Pull Request
# 代码审查
# 合并到 master
```

#### 2.3 GitLab Flow 工作流程

```
master (主分支)
  ├── develop (开发分支)
  │   └── feature/* (功能分支)
  └── production (生产分支)
```

```bash
# GitLab Flow 命令
git checkout -b feature/new-feature
git add .
git commit -m "Add new feature"
git push origin feature/new-feature
# 创建 Merge Request
# 代码审查
# 合并到 develop
# 合并到 master
# 合并到 production
```

## 版本号管理

### 1. 语义化版本号

语义化版本号（Semantic Versioning）是一种版本号命名规范：

```
主版本号.次版本号.修订号
例如：1.2.3
```

#### 1.1 版本号规则

- **主版本号（Major）**：不兼容的 API 修改
- **次版本号（Minor）**：向下兼容的功能性新增
- **修订号（Patch）**：向下兼容的问题修正

#### 1.2 版本号示例

```
1.0.0 → 1.0.1 (修复 bug)
1.0.1 → 1.1.0 (新增功能)
1.1.0 → 2.0.0 (不兼容的 API 修改)
```

### 2. 版本号管理工具

#### 2.1 Maven 版本管理

```xml
<!-- pom.xml -->
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>myapp</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>2.7.0</version>
        </dependency>
    </dependencies>
</project>
```

#### 2.2 npm 版本管理

```json
{
  "name": "myapp",
  "version": "1.0.0",
  "description": "My Application",
  "main": "index.js",
  "scripts": {
    "start": "node index.js",
    "test": "jest"
  },
  "dependencies": {
    "express": "^4.18.1"
  },
  "devDependencies": {
    "jest": "^28.1.0"
  }
}
```

#### 2.3 Gradle 版本管理

```groovy
// build.gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '2.7.0'
}

group = 'com.example'
version = '1.0.0'
sourceCompatibility = '11'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

### 3. 版本号管理最佳实践

#### 3.1 使用语义化版本号

```java
// 版本号管理示例
public class Version {
    private int major;
    private int minor;
    private int patch;
    
    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }
    
    public Version incrementMajor() {
        return new Version(major + 1, 0, 0);
    }
    
    public Version incrementMinor() {
        return new Version(major, minor + 1, 0);
    }
    
    public Version incrementPatch() {
        return new Version(major, minor, patch + 1);
    }
    
    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
```

#### 3.2 使用版本管理工具

```bash
# npm 版本管理
npm version patch   # 1.0.0 → 1.0.1
npm version minor   # 1.0.1 → 1.1.0
npm version major   # 1.1.0 → 2.0.0
```

#### 3.3 使用版本标签

```bash
# Git 标签
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

## 分支管理

### 1. 分支策略

#### 1.1 Git Flow 分支策略

```
master (主分支)
  ├── develop (开发分支)
  │   ├── feature/new-feature (功能分支)
  │   ├── feature/another-feature (功能分支)
  │   ├── release/1.0.0 (发布分支)
  │   └── release/1.1.0 (发布分支)
  ├── hotfix/1.0.1 (修复分支)
  └── hotfix/1.0.2 (修复分支)
```

#### 1.2 GitHub Flow 分支策略

```
master (主分支)
  ├── feature/new-feature (功能分支)
  ├── feature/another-feature (功能分支)
  ├── hotfix/1.0.1 (修复分支)
  └── hotfix/1.0.2 (修复分支)
```

#### 1.3 GitLab Flow 分支策略

```
master (主分支)
  ├── develop (开发分支)
  │   ├── feature/new-feature (功能分支)
  │   └── feature/another-feature (功能分支)
  ├── production (生产分支)
  ├── staging (预发布分支)
  └── hotfix/1.0.1 (修复分支)
```

### 2. 分支命名规范

#### 2.1 功能分支

```
feature/<feature-name>
例如：feature/user-login
```

#### 2.2 修复分支

```
hotfix/<hotfix-name>
例如：hotfix/login-bug
```

#### 2.3 发布分支

```
release/<version>
例如：release/1.0.0
```

#### 2.4 实验分支

```
experiment/<experiment-name>
例如：experiment/new-ui
```

### 3. 分支管理最佳实践

#### 3.1 短生命周期分支

```bash
# 创建功能分支
git checkout -b feature/new-feature

# 开发完成后，合并到 develop
git checkout develop
git merge feature/new-feature

# 删除功能分支
git branch -d feature/new-feature
```

#### 3.2 频繁合并

```bash
# 频繁合并到 develop
git checkout develop
git pull origin develop
git checkout feature/new-feature
git merge develop
```

#### 3.3 代码审查

```bash
# 创建 Pull Request
git push origin feature/new-feature
# 在 GitHub/GitLab 上创建 Pull Request
# 进行代码审查
# 合并到 develop
```

## 发布管理

### 1. 发布流程

#### 1.1 发布准备

```bash
# 创建发布分支
git checkout -b release/1.0.0

# 更新版本号
# 更新 CHANGELOG.md
# 更新文档

# 提交变更
git add .
git commit -m "Prepare release 1.0.0"

# 推送到远程
git push origin release/1.0.0
```

#### 1.2 发布测试

```bash
# 部署到测试环境
kubectl apply -f k8s/deployment-test.yaml

# 运行测试
mvn test

# 验证功能
# 性能测试
# 安全测试
```

#### 1.3 发布上线

```bash
# 合并到 master
git checkout master
git merge release/1.0.0

# 创建标签
git tag -a v1.0.0 -m "Release version 1.0.0"

# 推送到远程
git push origin master
git push origin v1.0.0

# 部署到生产环境
kubectl apply -f k8s/deployment-production.yaml
```

### 2. 发布策略

#### 2.1 蓝绿发布

```yaml
# Kubernetes 蓝绿发布
apiVersion: v1
kind: Service
metadata:
  name: myapp
spec:
  selector:
    app: myapp
    version: blue
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
```

#### 2.2 灰度发布

```yaml
# Kubernetes 灰度发布
apiVersion: v1
kind: Service
metadata:
  name: myapp
spec:
  selector:
    app: myapp
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp-v1
spec:
  replicas: 9
  selector:
    matchLabels:
      app: myapp
      version: v1
  template:
    metadata:
      labels:
        app: myapp
        version: v1
    spec:
      containers:
      - name: myapp
        image: myapp:1.0.0
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp-v2
spec:
  replicas: 1
  selector:
    matchLabels:
      app: myapp
      version: v2
    spec:
      containers:
      - name: myapp
        image: myapp:2.0.0
```

#### 2.3 金丝雀发布

```yaml
# Kubernetes 金丝雀发布
apiVersion: v1
kind: Service
metadata:
  name: myapp
spec:
  selector:
    app: myapp
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp-stable
spec:
  replicas: 9
  selector:
    matchLabels:
      app: myapp
      version: stable
  template:
    metadata:
      labels:
        app: myapp
        version: stable
    spec:
      containers:
      - name: myapp
        image: myapp:1.0.0
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp-canary
spec:
  replicas: 1
  selector:
    matchLabels:
      app: myapp
      version: canary
    spec:
      containers:
      - name: myapp
        image: myapp:2.0.0
```

### 3. 发布管理最佳实践

#### 3.1 自动化发布

```yaml
# GitLab CI 自动化发布
deploy_production:
  stage: deploy
  script:
    - echo "Deploying to production"
    - kubectl apply -f k8s/deployment-production.yaml
  environment:
    name: production
    url: https://example.com
  when: manual
  only:
    - master
```

#### 3.2 回滚机制

```bash
# Kubernetes 回滚
kubectl rollout undo deployment/myapp

# Git 回滚
git revert <commit-hash>
git push origin master
```

#### 3.3 发布通知

```yaml
# 发布通知
notify:
  stage: notify
  script:
    - curl -X POST $SLACK_WEBHOOK_URL
      -H 'Content-Type: application/json'
      -d '{"text":"Version 1.0.0 has been released to production"}'
  when: manual
```

## 版本管理工具

### 1. Git

Git 是最流行的分布式版本控制系统：

```bash
# Git 基本操作
git init
git clone <repository-url>
git add <file>
git commit -m "Commit message"
git push origin <branch>
git pull origin <branch>
git branch <branch-name>
git checkout <branch-name>
git merge <branch-name>
```

### 2. SVN

SVN 是集中式版本控制系统：

```bash
# SVN 基本操作
svn checkout <repository-url>
svn add <file>
svn commit -m "Commit message"
svn update
```

### 3. Mercurial

Mercurial 是分布式版本控制系统：

```bash
# Mercurial 基本操作
hg init
hg clone <repository-url>
hg add <file>
hg commit -m "Commit message"
hg push
hg pull
```

## 版本管理最佳实践

### 1. 使用版本控制系统

使用版本控制系统管理代码：

```bash
# Git 工作流程
git clone <repository-url>
git checkout -b feature/new-feature
git add .
git commit -m "Add new feature"
git push origin feature/new-feature
```

### 2. 使用语义化版本号

使用语义化版本号：

```bash
# npm 版本管理
npm version patch   # 1.0.0 → 1.0.1
npm version minor   # 1.0.1 → 1.1.0
npm version major   # 1.1.0 → 2.0.0
```

### 3. 使用分支策略

使用合适的分支策略：

```bash
# Git Flow 工作流程
git flow init
git flow feature start new-feature
git flow feature finish new-feature
git flow release start 1.0.0
git flow release finish 1.0.0
```

### 4. 使用代码审查

使用代码审查提高代码质量：

```bash
# 创建 Pull Request
git push origin feature/new-feature
# 在 GitHub/GitLab 上创建 Pull Request
# 进行代码审查
# 合并到 develop
```

### 5. 使用自动化测试

使用自动化测试确保代码质量：

```yaml
# GitLab CI 自动化测试
test:
  stage: test
  script:
    - mvn test
  coverage: '/Total.*?([0-9]{1,3})%/'
```

## 版本管理常见问题

### 1. 版本冲突

**问题**：多人同时修改同一文件，导致版本冲突

**解决方案**：
- 频繁合并
- 使用分支
- 代码审查
- 沟通协调

### 2. 版本回滚困难

**问题**：版本回滚困难，影响用户体验

**解决方案**：
- 使用标签
- 使用版本号
- 使用回滚机制
- 备份数据

### 3. 版本发布风险

**问题**：版本发布风险高，影响用户体验

**解决方案**：
- 使用灰度发布
- 使用蓝绿发布
- 使用金丝雀发布
- 快速回滚

### 4. 版本管理复杂

**问题**：版本管理复杂，难以维护

**解决方案**：
- 使用版本管理工具
- 使用分支策略
- 使用自动化工具
- 文档化流程

## 总结

版本管理是指对软件的不同版本进行管理的过程，包括版本控制、版本号管理、分支管理、发布管理等。版本管理的核心概念包括版本控制、版本号管理、分支管理和发布管理。版本控制系统包括 Git、SVN 和 Mercurial。版本号管理使用语义化版本号，包括主版本号、次版本号和修订号。分支管理使用合适的分支策略，如 Git Flow、GitHub Flow 和 GitLab Flow。发布管理包括发布流程、发布策略和发布最佳实践。版本管理的最佳实践包括使用版本控制系统、使用语义化版本号、使用分支策略、使用代码审查和使用自动化测试。版本管理的常见问题包括版本冲突、版本回滚困难、版本发布风险和版本管理复杂。