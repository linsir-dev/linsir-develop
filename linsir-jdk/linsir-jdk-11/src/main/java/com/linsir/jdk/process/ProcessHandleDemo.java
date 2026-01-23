package com.linsir.jdk.process;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * JDK 9+ 进程API (Process Handle)示例
 * 
 * 说明：
 * 1. JDK 9引入了Process Handle API，用于管理和监控操作系统进程
 * 2. ProcessHandle接口提供了获取进程信息、监控进程状态、列出所有进程等功能
 * 3. ProcessHandle.Info接口提供了获取进程详细信息的方法
 */
public class ProcessHandleDemo {

    /**
     * 获取当前进程信息
     */
    public void currentProcessInfo() {
        System.out.println("=== 获取当前进程信息 ===");
        
        // 获取当前进程的ProcessHandle
        ProcessHandle currentProcess = ProcessHandle.current();
        
        // 获取进程ID
        long pid = currentProcess.pid();
        System.out.println("当前进程ID: " + pid);
        
        // 获取进程信息
        Optional<ProcessHandle.Info> info = currentProcess.info();
        System.out.println("进程命令: " + info.map(ProcessHandle.Info::command).orElse(Optional.empty()));
        System.out.println("进程参数: " + info.map(ProcessHandle.Info::arguments).orElse(Optional.empty()));
        System.out.println("进程启动时间: " + info.map(ProcessHandle.Info::startInstant).orElse(Optional.empty()));
        System.out.println("进程用户: " + info.map(ProcessHandle.Info::user).orElse(Optional.empty()));
        
        // 检查进程是否存活
        boolean isAlive = currentProcess.isAlive();
        System.out.println("进程是否存活: " + isAlive);
    }

    /**
     * 启动新进程
     */
    public void startNewProcess() {
        System.out.println("\n=== 启动新进程 ===");
        
        try {
            // 启动一个新进程（Windows系统使用cmd命令）
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "echo Hello from new process");
            Process process = processBuilder.start();
            
            // 获取进程的ProcessHandle
            ProcessHandle processHandle = process.toHandle();
            System.out.println("新进程ID: " + processHandle.pid());
            
            // 等待进程完成
            int exitCode = process.waitFor();
            System.out.println("进程退出码: " + exitCode);
            
            // 检查进程是否存活
            boolean isAlive = processHandle.isAlive();
            System.out.println("进程是否存活: " + isAlive);
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监控进程状态
     */
    public void monitorProcess() {
        System.out.println("\n=== 监控进程状态 ===");
        
        try {
            // 启动一个睡眠进程
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "timeout 3");
            Process process = processBuilder.start();
            ProcessHandle processHandle = process.toHandle();
            
            System.out.println("启动监控进程，ID: " + processHandle.pid());
            
            // 注册进程终止监听器
            CompletableFuture<ProcessHandle> onExit = processHandle.onExit();
            onExit.thenAccept(ph -> {
                System.out.println("进程已终止，ID: " + ph.pid());
                System.out.println("进程退出码: " + ph.info().map(ProcessHandle.Info::exitCode).orElse(Optional.empty()));
            });
            
            // 等待进程终止
            processHandle.onExit().join();
            System.out.println("监控完成");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 列出所有进程
     */
    public void listAllProcesses() {
        System.out.println("\n=== 列出所有进程 ===");
        
        // 获取所有进程的流
        List<ProcessHandle> processes = ProcessHandle.allProcesses()
                .collect(Collectors.toList());
        
        System.out.println("当前系统进程数量: " + processes.size());
        
        // 打印前10个进程的信息
        System.out.println("\n前10个进程的信息:");
        processes.stream()
                .limit(10)
                .forEach(ph -> {
                    Optional<ProcessHandle.Info> info = ph.info();
                    System.out.printf("PID: %d, 命令: %s, 用户: %s%n", 
                            ph.pid(), 
                            info.map(ProcessHandle.Info::command).orElse(Optional.empty()),
                            info.map(ProcessHandle.Info::user).orElse(Optional.empty()));
                });
    }

    /**
     * 查找特定进程
     */
    public void findProcess(long pid) {
        System.out.println("\n=== 查找特定进程 ===");
        
        // 根据PID查找进程
        Optional<ProcessHandle> processHandle = ProcessHandle.of(pid);
        
        if (processHandle.isPresent()) {
            ProcessHandle ph = processHandle.get();
            System.out.println("找到进程，ID: " + ph.pid());
            
            Optional<ProcessHandle.Info> info = ph.info();
            System.out.println("进程命令: " + info.map(ProcessHandle.Info::command).orElse(Optional.empty()));
            System.out.println("进程是否存活: " + ph.isAlive());
        } else {
            System.out.println("未找到进程，ID: " + pid);
        }
    }

    /**
     * 进程树操作
     */
    public void processTree() {
        System.out.println("\n=== 进程树操作 ===");
        
        // 获取当前进程的父进程
        Optional<ProcessHandle> parentProcess = ProcessHandle.current().parent();
        System.out.println("当前进程的父进程: " + parentProcess.map(ph -> ph.pid()).orElse(-1L));
        
        // 获取当前进程的子进程
        System.out.println("\n当前进程的子进程:");
        ProcessHandle.current().children()
                .forEach(ph -> {
                    System.out.println("子进程ID: " + ph.pid());
                    Optional<ProcessHandle.Info> info = ph.info();
                    System.out.println("  命令: " + info.map(ProcessHandle.Info::command).orElse(Optional.empty()));
                });
    }

    /**
     * 计算进程运行时间
     */
    public void processRunTime() {
        System.out.println("\n=== 计算进程运行时间 ===");
        
        // 获取当前进程
        ProcessHandle currentProcess = ProcessHandle.current();
        
        // 获取进程启动时间
        Optional<Instant> startTime = currentProcess.info().map(ProcessHandle.Info::startInstant);
        
        if (startTime.isPresent()) {
            Instant now = Instant.now();
            Duration runTime = Duration.between(startTime.get(), now);
            System.out.println("当前进程已运行: " + runTime.toMillis() + " 毫秒");
            System.out.println("当前进程已运行: " + runTime.toSeconds() + " 秒");
        }
    }

    /**
     * 终止进程
     */
    public void destroyProcess() {
        System.out.println("\n=== 终止进程 ===");
        
        try {
            // 启动一个长时间运行的进程
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "timeout 10");
            Process process = processBuilder.start();
            ProcessHandle processHandle = process.toHandle();
            
            System.out.println("启动进程，ID: " + processHandle.pid());
            System.out.println("进程是否存活: " + processHandle.isAlive());
            
            // 等待2秒后终止进程
            Thread.sleep(2000);
            
            // 尝试优雅终止进程
            boolean destroyed = processHandle.destroy();
            System.out.println("尝试优雅终止进程: " + destroyed);
            
            // 等待进程终止
            processHandle.onExit().join();
            System.out.println("进程是否存活: " + processHandle.isAlive());
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
