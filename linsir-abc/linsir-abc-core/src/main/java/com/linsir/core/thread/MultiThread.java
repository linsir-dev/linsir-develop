package com.linsir.core.thread;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class MultiThread {


    public static  void  main(String[] args) throws IOException {
//        获取MXBean
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false,false);

        /*
         *
         * [6] Monitor Ctrl-Break //监听中断信号
         * [5] Attach Listener  //添加事件
         * [4] Signal Dispatcher // 分发处理给JVM信号的线程
         * [3] Finalizer //调用对象finalize方法的线程
         * [2] Reference Handler //清楚reference线程
         *[1] main //main线程,程序入口
         * */
        // 遍历线程信息，仅打印线程ID和线程名称信息
        for (ThreadInfo threadInfo : threadInfos) {
            System.out.println("[" + threadInfo.getThreadId() + "] " + threadInfo.
                    getThreadName());
        }

        System.in.read();

    }
}
