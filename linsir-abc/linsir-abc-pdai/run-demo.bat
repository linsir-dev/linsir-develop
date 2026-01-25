@echo off

REM 获取 Maven 依赖的 classpath
for /f "delims=" %%i in ('mvn dependency:build-classpath -Dmdep.outputFile=classpath.txt -q') do set CLASSPATH=%%i

REM 添加项目编译后的类目录到 classpath
set CLASSPATH=%CLASSPATH%;target\classes

REM 运行 Apache Commons 演示
java com.linsir.abc.pdai.tools.apacheCommons.ApacheCommonsDemoMain

REM 清理临时文件
if exist classpath.txt del classpath.txt

pause
