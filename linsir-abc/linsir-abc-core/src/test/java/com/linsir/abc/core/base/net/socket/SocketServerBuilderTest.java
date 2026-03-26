package com.linsir.abc.core.base.net.socket;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * SocketServerBuilder测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class SocketServerBuilderTest {

    @Test
    public void testBind() throws IOException {
        SocketServerBuilder server = new SocketServerBuilder();
        server.bind(0); // 自动分配端口

        assertNotNull(server);
    }

    @Test
    public void testWithThreadPool() throws IOException {
        SocketServerBuilder server = new SocketServerBuilder();
        server.bind(0)
              .withThreadPool(5);

        assertNotNull(server);
    }

    @Test
    public void testWithHandler() throws IOException {
        SocketServerBuilder server = new SocketServerBuilder();
        server.bind(0)
              .withHandler(clientSocket -> {
                  // 简单的处理器
              });

        assertNotNull(server);
    }

    @Test
    public void testBuilderChain() throws IOException {
        SocketServerBuilder server = new SocketServerBuilder();
        server.bind(0)
              .withThreadPool(3)
              .withHandler(clientSocket -> {});

        assertNotNull(server);
    }

    @Test
    public void testStartWithoutBind() {
        SocketServerBuilder server = new SocketServerBuilder();
        server.withHandler(clientSocket -> {});

        assertThrows(IllegalStateException.class, () -> server.start());
    }

    @Test
    public void testStartWithoutHandler() throws IOException {
        SocketServerBuilder server = new SocketServerBuilder();
        server.bind(0);

        assertThrows(IllegalStateException.class, () -> server.start());
    }

    @Test
    public void testServerStartAndStop() throws IOException, InterruptedException {
        SocketServerBuilder server = new SocketServerBuilder();
        server.bind(0)
              .withHandler(clientSocket -> {});

        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (Exception e) {
                // ignore
            }
        });
        serverThread.start();

        // 等待服务器启动
        Thread.sleep(200);

        // 停止服务器
        server.stop();
        serverThread.join(1000);
    }

    @Test
    public void testEchoServer() throws IOException, InterruptedException {
        int port = 12348;

        SocketServerBuilder server = new SocketServerBuilder();
        server.bind(port)
              .withThreadPool(2)
              .withHandler(clientSocket -> {
                  try (BufferedReader reader = new BufferedReader(
                          new InputStreamReader(clientSocket.getInputStream()));
                       PrintWriter writer = new PrintWriter(
                               new OutputStreamWriter(clientSocket.getOutputStream()), true)) {

                      String line;
                      while ((line = reader.readLine()) != null) {
                          writer.println("Echo: " + line);
                          if (line.equalsIgnoreCase("bye")) {
                              break;
                          }
                      }
                  } catch (IOException e) {
                      // ignore
                  }
              });

        Thread serverThread = new Thread(server::start);
        serverThread.start();

        // 等待服务器启动
        Thread.sleep(300);

        // 连接并测试
        try (Socket client = new Socket("localhost", port);
             PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(client.getInputStream()))) {

            writer.println("Hello");
            String response = reader.readLine();

            assertEquals("Echo: Hello", response);

            writer.println("bye");
            String finalResponse = reader.readLine();
            assertEquals("Echo: bye", finalResponse);

        } catch (IOException e) {
            fail("客户端连接失败: " + e.getMessage());
        }

        server.stop();
        serverThread.join(1000);
    }

    @Test
    public void testDemonstrateEchoServer() {
        assertDoesNotThrow(() -> SocketServerBuilder.demonstrateEchoServer());
    }

    @Test
    public void testMain() {
        assertDoesNotThrow(() -> SocketServerBuilder.main(new String[]{}));
    }
}
