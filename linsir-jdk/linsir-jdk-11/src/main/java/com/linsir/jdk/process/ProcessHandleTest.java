package com.linsir.jdk.process;

/**
 * 进程API (Process Handle)测试类
 */
public class ProcessHandleTest {
    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println("JDK 9+ 进程API (Process Handle)测试");
        System.out.println("=============================================");
        
        ProcessHandleDemo demo = new ProcessHandleDemo();
        
        // 测试获取当前进程信息
        demo.currentProcessInfo();
        
        // 测试启动新进程
        demo.startNewProcess();
        
        // 测试监控进程状态
        demo.monitorProcess();
        
        // 测试列出所有进程
        demo.listAllProcesses();
        
        // 测试查找特定进程（使用当前进程ID）
        long currentPid = ProcessHandle.current().pid();
        demo.findProcess(currentPid);
        
        // 测试进程树操作
        demo.processTree();
        
        // 测试计算进程运行时间
        demo.processRunTime();
        
        // 测试终止进程
        demo.destroyProcess();
        
        System.out.println("\n=============================================");
        System.out.println("测试完成");
        System.out.println("=============================================");
        
        // 说明：
        // 1. Process Handle API是JDK 9+的特性，用于管理和监控操作系统进程
        // 2. 提供了获取进程信息、监控进程状态、列出所有进程等功能
        // 3. 可以通过ProcessBuilder启动新进程，然后获取其ProcessHandle
        // 4. 可以注册进程终止监听器，监控进程的终止状态
        // 5. 可以获取进程树信息，包括父进程和子进程
    }
}
