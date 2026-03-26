package com.linsir.abc.core.base.net.socket;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * SocketConnectionPool测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class SocketConnectionPoolTest {

    @Test
    public void testConstructor() {
        SocketConnectionPool pool = new SocketConnectionPool("localhost", 12345, 5, 5000);

        assertNotNull(pool);
        assertEquals(0, pool.getActiveConnections());
        assertEquals(0, pool.getAvailableConnections());
    }

    @Test
    public void testBorrowAndReturnConnection() throws Exception {
        int port = 12350;

        // 启动测试服务端
        ServerSocket serverSocket = new ServerSocket(port);
        Thread serverThread = new Thread(() -> {
            try {
                Socket client = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

                String line;
                while ((line = reader.readLine()) != null) {
                    writer.println("Response: " + line);
                }
                client.close();
            } catch (IOException e) {
                // ignore
            }
        });
        serverThread.start();

        Thread.sleep(100);

        try {
            SocketConnectionPool pool = new SocketConnectionPool("localhost", port, 5, 5000);

            // 借用连接
            Socket conn = pool.borrowConnection();
            assertNotNull(conn);
            assertTrue(conn.isConnected());
            assertEquals(1, pool.getActiveConnections());

            // 使用连接
            PrintWriter writer = new PrintWriter(conn.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            writer.println("Hello");
            String response = reader.readLine();
            assertEquals("Response: Hello", response);

            // 归还连接
            pool.returnConnection(conn);
            assertEquals(1, pool.getAvailableConnections());

            pool.close();
        } finally {
            serverSocket.close();
            serverThread.join(1000);
        }
    }

    @Test
    public void testConnectionReuse() throws Exception {
        int port = 12351;

        ServerSocket serverSocket = new ServerSocket(port);
        Thread serverThread = new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    Socket client = serverSocket.accept();
                    new Thread(() -> {
                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                            String line;
                            while ((line = reader.readLine()) != null) {
                                writer.println("Echo: " + line);
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

        Thread.sleep(100);

        try {
            SocketConnectionPool pool = new SocketConnectionPool("localhost", port, 5, 5000);

            // 第一次借用
            Socket conn1 = pool.borrowConnection();
            pool.returnConnection(conn1);

            // 第二次借用（应该是同一个连接）
            Socket conn2 = pool.borrowConnection();

            // 活跃连接数应该还是1（复用）
            assertEquals(1, pool.getActiveConnections());

            pool.returnConnection(conn2);
            pool.close();
        } finally {
            serverSocket.close();
            serverThread.interrupt();
            serverThread.join(1000);
        }
    }

    @Test
    public void testClosePool() throws Exception {
        int port = 12352;

        ServerSocket serverSocket = new ServerSocket(port);
        Thread serverThread = new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    Socket client = serverSocket.accept();
                    new Thread(() -> {
                        try {
                            client.getInputStream().read();
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

        Thread.sleep(100);

        try {
            SocketConnectionPool pool = new SocketConnectionPool("localhost", port, 5, 5000);

            // 借用连接
            Socket conn = pool.borrowConnection();
            pool.returnConnection(conn);

            // 关闭连接池
            pool.close();

            // 关闭后不能再借用
            assertThrows(IllegalStateException.class, () -> pool.borrowConnection());
        } finally {
            serverSocket.close();
            serverThread.interrupt();
            serverThread.join(1000);
        }
    }

    @Test
    public void testBorrowFromClosedPool() {
        SocketConnectionPool pool = new SocketConnectionPool("localhost", 12345, 5, 5000);
        pool.close();

        assertThrows(IllegalStateException.class, () -> pool.borrowConnection());
    }

    @Test
    public void testDemonstrate() {
        assertDoesNotThrow(() -> SocketConnectionPool.demonstrate());
    }

    @Test
    public void testMain() {
        assertDoesNotThrow(() -> SocketConnectionPool.main(new String[]{}));
    }
}
