package com.linsir.core.c8;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println("服务器启动了");
        //1.创建套接字： 指定服务器的端口号
        ServerSocket ss = null;
        Socket s = null;
        int count = 0;//定义一个计数器，用来计数  客户端的请求
        try {
            ss = new ServerSocket(8080);
            while(true){//加入死循环，服务器一直监听客户端是否发送数据
                s = ss.accept();//等待接收客户端的数据
                //每次过来的客户端的请求 靠 线程处理：
                new ServerThread(s).start();
                count++;
                //输入请求的客户端的信息：
                System.out.println("当前是第"+count+"个用户访问我们的服务器,对应的用户是："+s.getInetAddress());
            }
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }
}
