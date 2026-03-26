package com.linsir.abc.core.base.nio.selector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;

/**
 * SelectorMultiplexer测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class SelectorMultiplexerTest {

    @Test
    public void testInit() throws IOException {
        SelectorMultiplexer multiplexer = new SelectorMultiplexer();
        multiplexer.init();

        assertNotNull(multiplexer.getSelector());
        assertTrue(multiplexer.getSelector().isOpen());
    }

    @Test
    public void testRegisterServerChannel() throws IOException {
        SelectorMultiplexer multiplexer = new SelectorMultiplexer();
        multiplexer.init();

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(0));

        multiplexer.registerServerChannel(serverChannel);

        assertFalse(serverChannel.isBlocking());

        serverChannel.close();
        multiplexer.close();
    }

    @Test
    public void testRegisterChannel() throws IOException {
        SelectorMultiplexer multiplexer = new SelectorMultiplexer();
        multiplexer.init();

        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);

        multiplexer.registerChannel(channel, SelectionKey.OP_READ);

        assertFalse(channel.isBlocking());

        channel.close();
        multiplexer.close();
    }

    @Test
    public void testStartAndStop() throws IOException, InterruptedException {
        SelectorMultiplexer multiplexer = new SelectorMultiplexer();
        multiplexer.init();

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(0));
        multiplexer.registerServerChannel(serverChannel);

        // 在后台线程启动Selector
        Thread selectorThread = new Thread(multiplexer::start);
        selectorThread.start();

        // 等待一段时间
        Thread.sleep(100);

        // 停止Selector
        multiplexer.stop();
        selectorThread.join(1000);

        serverChannel.close();
        multiplexer.close();
    }

    @Test
    public void testSelectorWithClientConnection() throws IOException, InterruptedException {
        SelectorMultiplexer multiplexer = new SelectorMultiplexer();
        multiplexer.init();

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(0));
        int port = serverChannel.socket().getLocalPort();
        multiplexer.registerServerChannel(serverChannel);

        // 启动Selector
        Thread selectorThread = new Thread(multiplexer::start);
        selectorThread.start();

        // 等待服务端启动
        Thread.sleep(100);

        // 创建客户端连接
        try (SocketChannel client = SocketChannel.open()) {
            client.connect(new InetSocketAddress("localhost", port));

            // 发送消息
            client.write(ByteBuffer.wrap("Test Message".getBytes()));

            // 等待服务端处理
            Thread.sleep(200);
        }

        // 停止Selector
        multiplexer.stop();
        selectorThread.join(1000);

        serverChannel.close();
        multiplexer.close();
    }

    @Test
    public void testClose() throws IOException {
        SelectorMultiplexer multiplexer = new SelectorMultiplexer();
        multiplexer.init();

        multiplexer.close();

        assertFalse(multiplexer.getSelector().isOpen());
    }

    @Test
    public void testDemonstrate() {
        assertDoesNotThrow(() -> SelectorMultiplexer.demonstrate());
    }

    @Test
    public void testMain() {
        assertDoesNotThrow(() -> SelectorMultiplexer.main(new String[]{}));
    }
}
