# Git中5个区，和具体操作？

## 1. 概述

Git是一个分布式版本控制系统，它通过五个不同的区域来管理代码的版本和状态。理解这五个区域及其相互关系是掌握Git的关键。

## 2. Git的五个区域

### 2.1 区域概览图

```
┌─────────────────────────────────────────────────────────────┐
│                    工作区 (Working Directory)               │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  你当前编辑的文件，未添加到暂存区                     │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │ git add                         │
│                          ▼                                 │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              暂存区 (Staging Area)                   │   │
│  │  准备提交的文件，已添加到暂存区                       │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │ git commit                      │
│                          ▼                                 │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              本地仓库 (Local Repository)              │   │
│  │  本地提交的历史记录，HEAD指向当前分支                │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │ git push                        │
│                          ▼                                 │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              远程仓库 (Remote Repository)             │   │
│  │  远程服务器上的代码仓库，如GitHub、GitLab等          │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │ git fetch/pull                 │
│                          ▼                                 │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              远程跟踪分支 (Remote Tracking Branch)   │   │
│  │  本地对远程仓库分支的引用，如origin/master           │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 各区域详细说明

#### 2.2.1 工作区 (Working Directory)

工作区是你当前正在编辑的文件所在的目录。这是你进行代码修改的地方。

```bash
# 查看工作区状态
git status

# 查看工作区与暂存区的差异
git diff

# 查看工作区与本地仓库的差异
git diff HEAD

# 查看工作区与远程仓库的差异
git diff origin/master
```

#### 2.2.2 暂存区 (Staging Area)

暂存区也称为索引区（Index），是Git准备下一次提交的文件集合。你可以将工作区的文件添加到暂存区。

```bash
# 添加单个文件到暂存区
git add filename.java

# 添加所有修改的文件到暂存区
git add .

# 添加所有修改和新文件到暂存区
git add -A

# 添加指定目录到暂存区
git add src/

# 查看暂存区内容
git status

# 查看暂存区与本地仓库的差异
git diff --staged

# 查看暂存区与工作区的差异
git diff

# 从暂存区移除文件（保留工作区修改）
git reset filename.java

# 从暂存区移除文件（删除工作区修改）
git checkout -- filename.java

# 清空暂存区
git reset
```

#### 2.2.3 本地仓库 (Local Repository)

本地仓库是存储在`.git`目录中的数据库，包含了所有的提交历史。HEAD指针指向当前分支的最新提交。

```bash
# 提交暂存区的修改到本地仓库
git commit -m "提交信息"

# 提交并跳过暂存区（直接提交工作区修改）
git commit -am "提交信息"

# 修改最后一次提交
git commit --amend

# 修改最后一次提交信息
git commit --amend -m "新的提交信息"

# 查看提交历史
git log

# 查看提交历史（简化版）
git log --oneline

# 查看提交历史（图形化）
git log --graph --oneline

# 查看指定文件的提交历史
git log filename.java

# 查看提交的详细信息
git show <commit-hash>

# 回退到指定提交
git reset --hard <commit-hash>

# 回退到指定提交（保留工作区修改）
git reset --soft <commit-hash>

# 回退到指定提交（保留暂存区修改）
git reset --mixed <commit-hash>

# 创建新分支
git branch feature-branch

# 切换分支
git checkout feature-branch

# 创建并切换到新分支
git checkout -b feature-branch

# 合并分支
git merge feature-branch

# 删除分支
git branch -d feature-branch

# 强制删除分支
git branch -D feature-branch
```

#### 2.2.4 远程仓库 (Remote Repository)

远程仓库是存储在远程服务器上的代码仓库，如GitHub、GitLab、Bitbucket等。远程仓库用于团队协作和代码备份。

```bash
# 查看远程仓库
git remote -v

# 添加远程仓库
git remote add origin https://github.com/username/repository.git

# 删除远程仓库
git remote remove origin

# 修改远程仓库URL
git remote set-url origin https://github.com/username/new-repository.git

# 推送到远程仓库
git push origin master

# 推送所有分支到远程仓库
git push --all origin

# 推送标签到远程仓库
git push --tags origin

# 推送并设置上游分支
git push -u origin master

# 强制推送到远程仓库
git push -f origin master

# 从远程仓库拉取最新代码
git pull origin master

# 从远程仓库获取最新代码（不合并）
git fetch origin

# 从远程仓库获取并合并指定分支
git pull origin feature-branch

# 从远程仓库删除分支
git push origin --delete feature-branch
```

#### 2.2.5 远程跟踪分支 (Remote Tracking Branch)

远程跟踪分支是本地对远程仓库分支的引用，如`origin/master`、`origin/develop`等。它们记录了远程仓库分支的状态。

```bash
# 查看所有分支（包括远程跟踪分支）
git branch -a

# 查看远程跟踪分支
git branch -r

# 查看本地分支与远程跟踪分支的关系
git branch -vv

# 基于远程跟踪分支创建本地分支
git checkout -b local-branch origin/remote-branch

# 查看本地分支与远程跟踪分支的差异
git diff origin/master

# 查看远程跟踪分支的提交历史
git log origin/master

# 更新远程跟踪分支
git fetch origin

# 更新所有远程跟踪分支
git fetch --all

# 清理已删除的远程跟踪分支
git remote prune origin

# 设置本地分支跟踪远程分支
git branch --set-upstream-to=origin/master master

# 取消本地分支跟踪远程分支
git branch --unset-upstream master
```

## 3. 区域间的操作流程

### 3.1 基本工作流程

```bash
# 1. 在工作区修改文件
echo "new code" >> file.java

# 2. 查看工作区状态
git status

# 3. 添加文件到暂存区
git add file.java

# 4. 查看暂存区状态
git status

# 5. 提交到本地仓库
git commit -m "Add new code"

# 6. 推送到远程仓库
git push origin master
```

### 3.2 完整的协作流程

```bash
# 1. 从远程仓库克隆代码
git clone https://github.com/username/repository.git

# 2. 进入项目目录
cd repository

# 3. 创建新分支进行开发
git checkout -b feature/new-feature

# 4. 在工作区修改文件
echo "new feature" >> feature.java

# 5. 查看修改
git diff

# 6. 添加文件到暂存区
git add feature.java

# 7. 提交到本地仓库
git commit -m "Add new feature"

# 8. 拉取远程仓库最新代码
git fetch origin

# 9. 合并远程代码到当前分支
git rebase origin/master

# 10. 推送分支到远程仓库
git push origin feature/new-feature

# 11. 在远程仓库创建Pull Request

# 12. 合并后删除本地分支
git checkout master
git branch -d feature/new-feature

# 13. 拉取最新代码
git pull origin master
```

## 4. 常见场景操作

### 4.1 撤销工作区修改

```bash
# 撤销单个文件的修改
git checkout -- filename.java

# 撤销所有文件的修改
git checkout -- .

# 撤销指定目录的修改
git checkout -- src/
```

### 4.2 撤销暂存区修改

```bash
# 撤销单个文件的暂存
git reset HEAD filename.java

# 撤销所有文件的暂存
git reset HEAD

# 撤销指定目录的暂存
git reset HEAD src/
```

### 4.3 撤销提交

```bash
# 撤销最后一次提交（保留修改）
git reset --soft HEAD~1

# 撤销最后一次提交（不保留修改）
git reset --hard HEAD~1

# 撤销多次提交（保留修改）
git reset --soft HEAD~3

# 撤销多次提交（不保留修改）
git reset --hard HEAD~3

# 撤销指定提交
git revert <commit-hash>
```

### 4.4 修改提交信息

```bash
# 修改最后一次提交信息
git commit --amend -m "新的提交信息"

# 修改指定提交信息（需要使用rebase）
git rebase -i HEAD~3
# 在打开的编辑器中将pick改为edit
# 修改提交信息
git commit --amend -m "新的提交信息"
git rebase --continue
```

### 4.5 合并提交

```bash
# 合并最近3次提交
git rebase -i HEAD~3
# 在打开的编辑器中将后两次提交的pick改为squash
# 保存并编辑提交信息
```

### 4.6 解决冲突

```bash
# 1. 拉取远程代码
git pull origin master

# 2. 如果有冲突，查看冲突文件
git status

# 3. 手动解决冲突
# 编辑冲突文件，保留需要的代码

# 4. 标记冲突已解决
git add filename.java

# 5. 提交合并
git commit -m "Resolve merge conflict"

# 6. 推送到远程仓库
git push origin master
```

### 4.7 暂存工作区修改

```bash
# 暂存当前工作区修改
git stash

# 暂存并添加说明
git stash save "临时保存的修改"

# 查看暂存列表
git stash list

# 应用暂存的修改
git stash apply

# 应用并删除暂存
git stash pop

# 应用指定的暂存
git stash apply stash@{0}

# 删除指定的暂存
git stash drop stash@{0}

# 清空所有暂存
git stash clear
```

### 4.8 查看差异

```bash
# 查看工作区与暂存区的差异
git diff

# 查看暂存区与本地仓库的差异
git diff --staged

# 查看工作区与本地仓库的差异
git diff HEAD

# 查看工作区与远程仓库的差异
git diff origin/master

# 查看两个提交之间的差异
git diff <commit1> <commit2>

# 查看两个分支之间的差异
git diff master develop

# 查看指定文件的差异
git diff filename.java

# 查看指定文件的详细差异
git diff -U10 filename.java
```

## 5. 高级操作

### 5.1 Cherry-pick

```bash
# 将指定提交应用到当前分支
git cherry-pick <commit-hash>

# 将多个提交应用到当前分支
git cherry-pick <commit1> <commit2> <commit3>

# 将指定范围的提交应用到当前分支
git cherry-pick <commit1>^..<commit2>

# Cherry-pick时遇到冲突
git cherry-pick --continue
git cherry-pick --abort
```

### 5.2 Rebase

```bash
# 将当前分支变基到master
git rebase master

# 将当前分支变基到远程master
git rebase origin/master

# 交互式变基
git rebase -i HEAD~3

# 变基时遇到冲突
git rebase --continue
git rebase --abort
git rebase --skip
```

### 5.3 Bisect

```bash
# 开始二分查找
git bisect start

# 标记当前提交为坏的
git bisect bad

# 标记指定提交为好的
git bisect good <commit-hash>

# 标记当前提交为好的
git bisect good

# 查看二分查找状态
git bisect status

# 结束二分查找
git bisect reset
```

### 5.4 Clean

```bash
# 清理未跟踪的文件
git clean -f

# 清理未跟踪的文件和目录
git clean -fd

# 预览将要清理的文件
git clean -n

# 清理忽略的文件
git clean -fX

# 清理所有未跟踪的文件（包括忽略的）
git clean -fx
```

## 6. 总结

Git的五个区域及其相互关系是理解Git工作原理的关键：

1. **工作区**: 编辑代码的地方
2. **暂存区**: 准备提交的文件集合
3. **本地仓库**: 存储提交历史的数据库
4. **远程仓库**: 远程服务器上的代码仓库
5. **远程跟踪分支**: 本地对远程仓库分支的引用

掌握这些区域之间的操作流程，能够帮助你更好地使用Git进行版本控制和团队协作。熟练使用Git命令，可以提高开发效率，减少代码冲突和版本管理问题。