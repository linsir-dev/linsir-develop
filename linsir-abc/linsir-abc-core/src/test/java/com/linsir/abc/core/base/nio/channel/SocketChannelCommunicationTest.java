package com.linsir.abc.core.base.nio.channel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * SocketChannelCommunication测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class SocketChannelCommunicationTest {

    @Test
    public void testCreateClient() throws IOException {
        SocketChannelCommunication comm = new SocketChannelCommunication();

        // 先创建服务端
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(0)); // 自动分配端口
        int port = serverChannel.socket().getLocalPort();

        // 在后台线程接受连接
        Thread serverThread = new Thread(() -> {
            try {
                SocketChannel client = serverChannel.accept();
                client.close();
                serverChannel.close();
            } catch (IOException e) {
                // ignore
            }
        });
        serverThread.start();

        // 创建客户端连接
        SocketChannel clientChannel = comm.createClient("localhost", port);

        assertNotNull(clientChannel);
        assertTrue(clientChannel.isConnected());

        clientChannel.close();
    }

    @Test
    public void testCreateServer() throws IOException {
        SocketChannelCommunication comm = new SocketChannelCommunication();
        ServerSocketChannel serverChannel = comm.createServer(0); // 自动分配端口

        assertNotNull(serverChannel);
        assertTrue(serverChannel.isOpen());
        assertTrue(serverChannel.socket().isBound());

        serverChannel.close();
    }

    @Test
    public void testSendAndReceiveMessage() throws IOException, InterruptedException {
        SocketChannelCommunication comm = new SocketChannelCommunication();

        // 创建服务端
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(0));
        int port = serverChannel.socket().getLocalPort();

        // 服务端线程
        Thread serverThread = new Thread(() -> {
            try {
                SocketChannel client = serverChannel.accept();

                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int bytesRead = client.read(buffer);

                if (bytesRead > 0) {
                    buffer.flip();
                    String message = StandardCharsets.UTF_8.decode(buffer).toString();

                    // Echo回显
                    ByteBuffer response = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
                    client.write(response);
                }

                client.close();
                serverChannel.close();
            } catch (IOException e) {
                // ignore
            }
        });
        serverThread.start();

        // 等待服务端启动
        Thread.sleep(100);

        // 客户端发送消息
        SocketChannel clientChannel = comm.createClient("localhost", port);
        String message = "Hello, Server!";
        comm.sendMessage(clientChannel, message);

        // 关闭输出以告诉服务端数据发送完毕
        clientChannel.shutdownOutput();

        // 接收响应
        String response = comm.receiveMessage(clientChannel);

        assertEquals(message, response.trim());

        clientChannel.close();
        serverThread.join(1000);
    }

    @Test
    public void testConfigureBlocking() throws IOException {
        SocketChannel channel = SocketChannel.open();

        assertTrue(channel.isBlocking()); // 默认阻塞模式

        channel.configureBlocking(false);
        assertFalse(channel.isBlocking());

        channel.close();
    }

    @Test
    public void testNonBlockingConnect() throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);

        // 非阻塞连接
        boolean connected = channel.connect(new InetSocketAddress("localhost", 12345));

        // 由于没有服务在12345端口，连接不会立即完成
        assertFalse(connected);

        channel.close();
    }

    @Test
    public void testDemonstrateClientServer() {
        assertDoesNotThrow(() -> SocketChannelCommunication.demonstrateClientServer());
    }

    @Test
    public void testDemonstrateNonBlocking() {
        assertDoesNotThrow(() -> SocketChannelCommunication.demonstrateNonBlocking());
    }

    @Test
    public void testDemonstrate() {
        assertDoesNotThrow(() -> SocketChannelCommunication.demonstrate());
    }

    @Test
    public void testMain() {
        assertDoesNotThrow(() -> SocketChannelCommunication.main(new String[]{}));
    }
}
