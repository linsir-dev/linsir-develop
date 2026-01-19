

### windows平台上启动项目

（1）解压
$ unzip app.zip

（2）赋予脚本执行权限
$ cd app
$ chmod +x bin/app bin/wrapper-linux-x86-*

（3）启动项目
$ ./bin/app start

（4）查询项目状态
$ ./bin/app status

（5）停止项目
$ ./bin/app stop

（6）重启项目
$ ./bin/app restart

（7）查看日志
$ tail -f logs/wrapper.log 


### linux平台上启动项目

（1）解压
$ unzip app.zip

（2）赋予脚本执行权限
$ cd app
$ chmod +x bin/app bin/wrapper-linux-x86-*

（3）启动项目
$ ./bin/app start

（4）查询项目状态
$ ./bin/app status

（5）停止项目
$ ./bin/app stop

（6）重启项目
$ ./bin/app restart

（7）查看日志
$ tail -f logs/wrapper.log