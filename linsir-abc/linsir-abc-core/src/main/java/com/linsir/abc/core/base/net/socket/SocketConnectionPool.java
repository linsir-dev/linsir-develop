package com.linsir.abc.core.base.net.socket;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Socket连接池
 * 演示Socket连接池的实现，管理TCP连接复用
 *
 * 设计要点：
 * 1. 连接复用减少连接建立开销
 * 2. 连接有效性检查
 * 3. 线程安全的连接管理
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class SocketConnectionPool {

    private final String host;
    private final int port;
    private final int maxConnections;
    private final long connectionTimeout;
    private final BlockingQueue<Socket> availableConnections;
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private volatile boolean closed = false;

    public SocketConnectionPool(String host, int port, int maxConnections, long connectionTimeout) {
        this.host = host;
        this.port = port;
        this.maxConnections = maxConnections;
        this.connectionTimeout = connectionTimeout;
        this.availableConnections = new LinkedBlockingQueue<>(maxConnections);
    }

    /**
     * 获取连接
     */
    public Socket borrowConnection() throws IOException, InterruptedException {
        if (closed) {
            throw new IllegalStateException("连接池已关闭");
        }

        Socket connection = availableConnections.poll();

        if (connection == null) {
            if (activeConnections.get() < maxConnections) {
                connection = createConnection();
                activeConnections.incrementAndGet();
            } else {
                connection = availableConnections.poll(connectionTimeout, TimeUnit.MILLISECONDS);
                if (connection == null) {
                    throw new IOException("获取连接超时");
                }
            }
        }

        // 检查连接有效性
        if (!isValid(connection)) {
            closeConnection(connection);
            return borrowConnection();
        }

        return connection;
    }

    /**
     * 归还连接
     */
    public void returnConnection(Socket connection) {
        if (closed || !isValid(connection)) {
            closeConnection(connection);
            return;
        }

        availableConnections.offer(connection);
    }

    /**
     * 创建新连接
     */
    private Socket createConnection() throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), (int) connectionTimeout);
        return socket;
    }

    /**
     * 检查连接是否有效
     */
    private boolean isValid(Socket connection) {
        return connection != null && 
               connection.isConnected() && 
               !connection.isClosed() &&
               !connection.isInputShutdown() &&
               !connection.isOutputShutdown();
    }

    /**
     * 关闭连接
     */
    private void closeConnection(Socket connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                // ignore
            }
            activeConnections.decrementAndGet();
        }
    }

    /**
     * 关闭连接池
     */
    public void close() {
        closed = true;
        Socket connection;
        while ((connection = availableConnections.poll()) != null) {
            closeConnection(connection);
        }
    }

    /**
     * 获取活跃连接数
     */
    public int getActiveConnections() {
        return activeConnections.get();
    }

    /**
     * 获取可用连接数
     */
    public int getAvailableConnections() {
        return availableConnections.size();
    }

    /**
     * 演示连接池使用
     */
    public static void demonstrate() {
        System.out.println("=== Socket连接池演示 ===\n");

        int port = 12349;

        // 启动测试服务端
        Thread serverThread = new Thread(() -> {
            try (ServerSocket server = new ServerSocket(port)) {
                System.out.println("测试服务端启动");

                while (!Thread.interrupted()) {
                    Socket client = server.accept();
                    new Thread(() -> {
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(client.getInputStream()));
                             PrintWriter writer = new PrintWriter(client.getOutputStream(), true)) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                writer.println("Response: " + line);
                            }
                        } catch (IOException e) {
                            // ignore
                        }
                    }).start();
                }
            } catch (IOException e) {
                // ignore
            }
        });
        serverThread.start();

        try {
            Thread.sleep(300);

            // 创建连接池
            SocketConnectionPool pool = new SocketConnectionPool("localhost", port, 5, 5000);

            System.out.println("连接池创建: max=5");
            System.out.println("活跃连接: " + pool.getActiveConnections());
            System.out.println("可用连接: " + pool.getAvailableConnections());

            // 借用连接
            System.out.println("\n借用连接1...");
            Socket conn1 = pool.borrowConnection();
            System.out.println("活跃连接: " + pool.getActiveConnections());

            // 使用连接
            PrintWriter writer1 = new PrintWriter(conn1.getOutputStream(), true);
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
            writer1.println("Hello");
            System.out.println("收到: " + reader1.readLine());

            // 归还连接
            pool.returnConnection(conn1);
            System.out.println("归还连接后 - 可用连接: " + pool.getAvailableConnections());

            // 再次借用（应该是同一个连接）
            System.out.println("\n再次借用连接...");
            Socket conn2 = pool.borrowConnection();
            System.out.println("活跃连接: " + pool.getActiveConnections());

            PrintWriter writer2 = new PrintWriter(conn2.getOutputStream(), true);
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(conn2.getInputStream()));
            writer2.println("World");
            System.out.println("收到: " + reader2.readLine());

            pool.returnConnection(conn2);

            // 关闭连接池
            pool.close();
            System.out.println("\n连接池已关闭");

        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
        }

        serverThread.interrupt();
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
