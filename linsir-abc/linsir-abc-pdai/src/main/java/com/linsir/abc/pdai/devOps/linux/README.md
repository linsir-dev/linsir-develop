# Linux 技术文档

## 文档列表

### Linux 基础
- [什么是Linux？](what-is-linux.md)
- [UNIX和LINUX有什么区别？](unix-vs-linux.md)
- [什么是BASH？](what-is-bash.md)
- [Linux基础知识问答](linux-basics.md)

## 学习路径

### 初级
1. [什么是Linux？](what-is-linux.md) - 了解 Linux 的基本概念
2. [UNIX和LINUX有什么区别？](unix-vs-linux.md) - 理解 UNIX 和 Linux 的区别
3. [什么是BASH？](what-is-bash.md) - 学习 Bash Shell 的基本概念

### 中级
1. [Linux基础知识问答](linux-basics.md) - 掌握 Linux 的常用命令和操作
2. 学习文件系统、权限管理、进程管理
3. 学习 Shell 脚本编程

### 高级
1. 深入理解 Linux 内核
2. 掌握系统管理和调优
3. 学习网络配置和安全

## 核心概念

### Linux
- Linux 是一个开源的类 Unix 操作系统内核
- Linux 操作系统基于 Linux 内核，结合了 GNU 工具和其他开源软件
- Linux 的特点：开源、免费、多用户、多任务、稳定、安全、可定制、跨平台

### Linux 内核
- Linux 内核是 Linux 操作系统的核心组件
- 负责管理硬件资源、进程管理、内存管理、文件系统、设备驱动、网络协议栈等
- 内核版本：5.x（稳定版本）

### Bash Shell
- Bash（Bourne Again Shell）是 Linux 和 Unix 系统中最常用的 Shell 程序
- Bash 是 Bourne Shell（sh）的增强版本
- Bash 的功能：命令执行、变量、数组、函数、条件语句、循环语句、输入输出重定向、管道、命令替换、通配符

### 文件系统
- Linux 使用树形文件系统结构
- 根目录：`/`
- 常用目录：`/bin`、`/etc`、`/home`、`/usr`、`/var`、`/tmp`

### 权限管理
- 文件权限：读（r）、写（w）、执行（x）
- 权限对象：所有者（u）、组（g）、其他用户（o）
- 权限表示：数字表示法（755）、符号表示法（u+x）

## 常用命令

### 文件操作
```bash
# 列出文件和目录
ls -l

# 切换目录
cd /home/user

# 显示当前目录
pwd

# 创建目录
mkdir test

# 删除文件
rm test.txt

# 复制文件
cp test.txt test_copy.txt

# 移动文件
mv test.txt /tmp/
```

### 文本操作
```bash
# 显示文件内容
cat test.txt

# 分页显示文件内容
less test.txt

# 搜索文本
grep "hello" test.txt

# 显示文件开头
head -n 10 test.txt

# 显示文件结尾
tail -n 10 test.txt
```

### 权限管理
```bash
# 修改文件权限
chmod 755 test.txt

# 修改文件所有者
chown user test.txt

# 修改文件所属组
chgrp group test.txt
```

### 进程管理
```bash
# 显示进程
ps aux

# 显示系统资源使用情况
top

# 终止进程
kill 1234

# 终止所有同名进程
killall firefox
```

### 系统信息
```bash
# 查看内核版本
uname -r

# 查看系统信息
uname -a

# 查看磁盘使用情况
df -h

# 查看内存使用情况
free -h

# 查看系统运行时间
uptime
```

## Bash Shell 基础

### 变量
```bash
# 定义变量
name="John"

# 使用变量
echo $name

# 定义只读变量
readonly name="John"

# 删除变量
unset name
```

### 数组
```bash
# 定义数组
fruits=("apple" "banana" "orange")

# 访问数组元素
echo ${fruits[0]}

# 访问所有元素
echo ${fruits[@]}

# 获取数组长度
echo ${#fruits[@]}
```

### 函数
```bash
# 定义函数
function hello() {
    echo "Hello, $1!"
}

# 调用函数
hello "World"
```

### 条件语句
```bash
# if 语句
if [ $1 -gt 10 ]; then
    echo "$1 is greater than 10"
elif [ $1 -eq 10 ]; then
    echo "$1 is equal to 10"
else
    echo "$1 is less than 10"
fi
```

### 循环语句
```bash
# for 循环
for i in {1..10}; do
    echo $i
done

# while 循环
count=1
while [ $count -le 10 ]; do
    echo $count
    count=$((count + 1))
done
```

### 输入输出重定向
```bash
# 输出重定向
ls -l > output.txt

# 输出追加
ls -l >> output.txt

# 输入重定向
wc -l < input.txt

# 错误输出重定向
ls -l 2> error.txt

# 同时重定向标准输出和错误输出
ls -l > output.txt 2>&1
```

### 管道
```bash
# 管道
ls -l | grep ".txt"

# 多个管道
ls -l | grep ".txt" | wc -l

# 管道和重定向
ls -l | grep ".txt" > output.txt
```

## 文件系统

### 目录结构
```
/
├── bin        # 可执行文件
├── boot       # 启动文件
├── dev        # 设备文件
├── etc        # 配置文件
├── home       # 用户主目录
├── lib        # 库文件
├── media      # 可移动媒体
├── mnt        # 挂载点
├── opt        # 可选软件包
├── proc       # 进程信息
├── root       # root 用户主目录
├── run        # 运行时数据
├── sbin       # 系统可执行文件
├── srv        # 服务数据
├── sys        # 系统信息
├── tmp        # 临时文件
├── usr        # 用户程序
└── var        # 可变数据
```

### 文件类型
```bash
# 普通文件：-
# 目录文件：d
# 符号链接：l
# 设备文件：b（块设备）、c（字符设备）
# 套接字文件：s
# 命名管道：p

# 查看文件类型
file filename
```

### 符号链接
```bash
# 创建符号链接
ln -s /path/to/source /path/to/link

# 查看符号链接
ls -l /path/to/link

# 删除符号链接
rm /path/to/link

# 查看符号链接指向的文件
readlink /path/to/link
```

### 硬链接
```bash
# 创建硬链接
ln /path/to/source /path/to/link

# 查看硬链接
ls -l /path/to/link

# 删除硬链接
rm /path/to/link

# 查看文件 inode
ls -i /path/to/source
```

## 权限管理

### 权限类型
- **读权限（r）**：可以读取文件内容或列出目录内容
- **写权限（w）**：可以修改文件内容或在目录中创建/删除文件
- **执行权限（x）**：可以执行文件或进入目录

### 权限对象
- **所有者（u）**：文件的所有者
- **组（g）**：文件所属的组
- **其他用户（o）**：其他所有用户

### 权限表示
```bash
# 数字表示法
chmod 755 file.txt  # rwxr-xr-x
chmod 644 file.txt  # rw-r--r--
chmod 777 file.txt  # rwxrwxrwx

# 符号表示法
chmod u+x file.txt  # 给所有者添加执行权限
chmod g-w file.txt  # 给组删除写权限
chmod o+r file.txt  # 给其他用户添加读权限
```

### 更改所有者和组
```bash
# 更改所有者
chown user file.txt

# 更改组
chgrp group file.txt

# 同时更改所有者和组
chown user:group file.txt

# 递归更改
chown -R user:group /path/to/directory
```

## 进程管理

### 查看进程
```bash
# 显示进程
ps aux

# 显示所有进程
ps -ef

# 显示进程树
pstree

# 显示系统资源使用情况
top

# 显示系统资源使用情况（交互式）
htop
```

### 终止进程
```bash
# 终止前台进程
Ctrl + C

# 终止进程
kill 1234

# 强制终止进程
kill -9 1234

# 终止所有同名进程
killall firefox

# 根据进程名终止进程
pkill firefox
```

### 后台运行
```bash
# 在后台运行程序
./program &

# 使用 nohup 在后台运行程序
nohup ./program &

# 使用 screen 运行程序
screen -S sessionname
./program
# 按 Ctrl + A + D 分离会话

# 重新连接会话
screen -r sessionname
```

## 系统管理

### 系统信息
```bash
# 查看内核版本
uname -r

# 查看系统信息
uname -a

# 查看系统运行时间
uptime

# 查看系统负载
cat /proc/loadavg

# 查看系统版本
cat /etc/os-release
```

### 磁盘管理
```bash
# 查看磁盘使用情况
df -h

# 查看目录大小
du -sh /path/to/directory

# 查看分区
fdisk -l

# 挂载分区
mount /dev/sda1 /mnt/data

# 卸载分区
umount /mnt/data
```

### 内存管理
```bash
# 查看内存使用情况
free -h

# 查看内存详细信息
cat /proc/meminfo

# 查看交换空间
swapon -s

# 启用交换空间
swapon /swapfile

# 禁用交换空间
swapoff /swapfile
```

### 网络管理
```bash
# 查看网络接口
ifconfig

# 查看网络连接
netstat -an

# 查看网络连接（更详细）
ss -an

# 测试网络连接
ping google.com

# 查看路由表
route -n

# 查看网络配置
ip addr show
```

## 包管理

### Debian/Ubuntu
```bash
# 更新软件包列表
sudo apt-get update

# 升级软件包
sudo apt-get upgrade

# 安装软件包
sudo apt-get install package-name

# 删除软件包
sudo apt-get remove package-name

# 删除软件包及其配置文件
sudo apt-get purge package-name

# 搜索软件包
apt-cache search package-name

# 查看软件包信息
apt-cache show package-name
```

### Red Hat/CentOS
```bash
# 更新软件包列表
sudo yum check-update

# 升级软件包
sudo yum update

# 安装软件包
sudo yum install package-name

# 删除软件包
sudo yum remove package-name

# 搜索软件包
yum search package-name

# 查看软件包信息
yum info package-name
```

### Arch Linux
```bash
# 更新软件包列表
sudo pacman -Sy

# 升级软件包
sudo pacman -Su

# 安装软件包
sudo pacman -S package-name

# 删除软件包
sudo pacman -R package-name

# 删除软件包及其依赖
sudo pacman -Rs package-name

# 搜索软件包
pacman -Ss package-name

# 查看软件包信息
pacman -Si package-name
```

## Shell 脚本编程

### 创建脚本
```bash
#!/bin/bash

# 这是一个简单的 Bash 脚本
echo "Hello, World!"

# 定义变量
name="John"

# 使用变量
echo "Hello, $name!"

# 条件语句
if [ $name == "John" ]; then
    echo "Hello, John!"
else
    echo "Hello, $name!"
fi

# 循环语句
for i in {1..10}; do
    echo $i
done
```

### 执行脚本
```bash
# 给脚本添加执行权限
chmod +x script.sh

# 执行脚本
./script.sh

# 使用 bash 执行脚本
bash script.sh
```

### 调试脚本
```bash
# 调试脚本
bash -x script.sh

# 检查语法错误
bash -n script.sh

# 打印每个命令在执行前的内容
bash -v script.sh
```

## 常见问题

### 1. 如何查看 Linux 内核版本？
```bash
uname -r
```

### 2. 如何查看系统信息？
```bash
uname -a
```

### 3. 如何查看磁盘使用情况？
```bash
df -h
```

### 4. 如何查看内存使用情况？
```bash
free -h
```

### 5. 如何查看进程？
```bash
ps aux
```

### 6. 如何终止进程？
```bash
kill 1234
```

### 7. 如何更改文件权限？
```bash
chmod 755 file.txt
```

### 8. 如何更改文件所有者？
```bash
chown user file.txt
```

### 9. 如何创建符号链接？
```bash
ln -s /path/to/source /path/to/link
```

### 10. 如何查看文件内容？
```bash
cat file.txt
```

## 最佳实践

### 1. 使用 sudo 而不是 root
```bash
# 推荐
sudo apt-get update

# 不推荐
su -
apt-get update
```

### 2. 定期更新系统
```bash
# Debian/Ubuntu
sudo apt-get update
sudo apt-get upgrade

# Red Hat/CentOS
sudo yum update

# Arch Linux
sudo pacman -Syu
```

### 3. 备份重要数据
```bash
# 使用 rsync 备份
rsync -avz /path/to/source /path/to/destination

# 使用 tar 备份
tar -czvf backup.tar.gz /path/to/source
```

### 4. 使用版本控制
```bash
# 初始化 Git 仓库
git init

# 添加文件到仓库
git add .

# 提交更改
git commit -m "Initial commit"

# 推送到远程仓库
git push origin master
```

### 5. 使用配置文件
```bash
# 编辑 ~/.bashrc
vi ~/.bashrc

# 添加别名
alias ll='ls -l'
alias la='ls -a'

# 重新加载配置文件
source ~/.bashrc
```

## 参考资源

- [Linux 官方网站](https://www.kernel.org/)
- [GNU 项目](https://www.gnu.org/)
- [Bash 手册](https://www.gnu.org/software/bash/manual/)
- [Linux 文档项目](https://tldp.org/)
- [Ubuntu 文档](https://help.ubuntu.com/)
- [Red Hat 文档](https://access.redhat.com/documentation/)
- [Arch Linux Wiki](https://wiki.archlinux.org/)