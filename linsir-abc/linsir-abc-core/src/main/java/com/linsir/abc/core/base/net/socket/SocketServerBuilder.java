package com.linsir.abc.core.base.net.socket;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * Socket服务端构建器
 * 演示ServerSocket的使用，构建TCP服务端
 *
 * 设计要点：
 * 1. ServerSocket监听端口，接受客户端连接
 * 2. 使用线程池处理多客户端
 * 3. 支持优雅关闭
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class SocketServerBuilder {

    private ServerSocket serverSocket;
    private ExecutorService executor;
    private volatile boolean running = false;
    private ClientHandler clientHandler;

    /**
     * 客户端处理器接口
     */
    @FunctionalInterface
    public interface ClientHandler {
        void handle(Socket clientSocket);
    }

    /**
     * 绑定端口
     *
     * @param port 端口号
     * @return this
     * @throws IOException 当IO操作失败时
     */
    public SocketServerBuilder bind(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        return this;
    }

    /**
     * 设置线程池大小
     *
     * @param poolSize 线程池大小
     * @return this
     */
    public SocketServerBuilder withThreadPool(int poolSize) {
        executor = Executors.newFixedThreadPool(poolSize);
        return this;
    }

    /**
     * 设置客户端处理器
     *
     * @param handler 处理器
     * @return this
     */
    public SocketServerBuilder withHandler(ClientHandler handler) {
        this.clientHandler = handler;
        return this;
    }

    /**
     * 启动服务端
     */
    public void start() {
        if (serverSocket == null) {
            throw new IllegalStateException("ServerSocket未初始化，请先调用bind()");
        }
        if (clientHandler == null) {
            throw new IllegalStateException("ClientHandler未设置");
        }
        if (executor == null) {
            executor = Executors.newCachedThreadPool();
        }

        running = true;
        System.out.println("Socket服务端启动，监听端口: " + serverSocket.getLocalPort());

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("接受连接: " + clientSocket.getInetAddress());

                executor.submit(() -> {
                    try {
                        clientHandler.handle(clientSocket);
                    } finally {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                });
            } catch (IOException e) {
                if (running) {
                    System.err.println("接受连接错误: " + e.getMessage());
                }
            }
        }
    }

    /**
     * 停止服务端
     */
    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (executor != null) {
                executor.shutdown();
            }
        } catch (IOException e) {
            System.err.println("关闭服务端错误: " + e.getMessage());
        }
    }

    /**
     * 演示Echo服务端
     */
    public static void demonstrateEchoServer() {
        System.out.println("=== Socket服务端演示 ===\n");

        int port = 12348;

        SocketServerBuilder server = new SocketServerBuilder();
        server.bind(port)
              .withThreadPool(5)
              .withHandler(clientSocket -> {
                  try (BufferedReader reader = new BufferedReader(
                          new InputStreamReader(clientSocket.getInputStream()));
                       PrintWriter writer = new PrintWriter(
                               new OutputStreamWriter(clientSocket.getOutputStream()), true)) {

                      String line;
                      while ((line = reader.readLine()) != null) {
                          System.out.println("收到: " + line);
                          writer.println("Echo: " + line);

                          if (line.equalsIgnoreCase("bye")) {
                              break;
                          }
                      }
                  } catch (IOException e) {
                      System.err.println("处理客户端错误: " + e.getMessage());
                  }
              });

        // 在后台启动服务端
        Thread serverThread = new Thread(server::start);
        serverThread.start();

        // 等待服务端启动
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 启动测试客户端
        try (Socket client = new Socket("localhost", port);
             PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(client.getInputStream()))) {

            String[] messages = {"Hello", "World", "bye"};
            for (String msg : messages) {
                writer.println(msg);
                String response = reader.readLine();
                System.out.println("客户端收到: " + response);
            }

        } catch (IOException e) {
            System.err.println("客户端错误: " + e.getMessage());
        }

        // 停止服务端
        server.stop();

        try {
            serverThread.join(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        demonstrateEchoServer();
    }
}
