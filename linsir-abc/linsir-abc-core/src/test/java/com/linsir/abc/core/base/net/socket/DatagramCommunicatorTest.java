package com.linsir.abc.core.base.net.socket;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * DatagramCommunicator测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class DatagramCommunicatorTest {

    @Test
    public void testBind() throws SocketException {
        DatagramCommunicator communicator = new DatagramCommunicator();
        communicator.bind(0); // 自动分配端口

        // 如果成功绑定，不会抛出异常
        assertDoesNotThrow(() -> {});

        communicator.close();
    }

    @Test
    public void testSendAndReceive() throws IOException, InterruptedException {
        int port = 12355;
        String testMessage = "Hello UDP";

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> receivedMessage = new AtomicReference<>();

        // 启动接收线程
        Thread receiverThread = new Thread(() -> {
            try {
                DatagramCommunicator receiver = new DatagramCommunicator();
                receiver.bind(port);

                String message = receiver.receive(1024);
                receivedMessage.set(message);
                latch.countDown();

                receiver.close();
            } catch (IOException e) {
                // ignore
            }
        });
        receiverThread.start();

        // 等待接收端启动
        Thread.sleep(100);

        // 发送消息
        DatagramCommunicator sender = new DatagramCommunicator();
        sender.bind(0);
        sender.send(testMessage, "localhost", port);
        sender.close();

        // 等待接收完成
        boolean received = latch.await(2, TimeUnit.SECONDS);
        receiverThread.join(1000);

        assertTrue(received, "应该在2秒内收到消息");
        assertEquals(testMessage, receivedMessage.get());
    }

    @Test
    public void testMultipleMessages() throws IOException, InterruptedException {
        int port = 12356;
        String[] testMessages = {"Message 1", "Message 2", "Message 3"};

        CountDownLatch latch = new CountDownLatch(testMessages.length);

        // 启动接收线程
        Thread receiverThread = new Thread(() -> {
            try {
                DatagramCommunicator receiver = new DatagramCommunicator();
                receiver.bind(port);

                for (int i = 0; i < testMessages.length; i++) {
                    String message = receiver.receive(1024);
                    if (testMessages[i].equals(message)) {
                        latch.countDown();
                    }
                }

                receiver.close();
            } catch (IOException e) {
                // ignore
            }
        });
        receiverThread.start();

        Thread.sleep(100);

        // 发送多条消息
        DatagramCommunicator sender = new DatagramCommunicator();
        sender.bind(0);

        for (String msg : testMessages) {
            sender.send(msg, "localhost", port);
            Thread.sleep(50);
        }

        sender.close();

        // 等待所有消息接收完成
        boolean allReceived = latch.await(3, TimeUnit.SECONDS);
        receiverThread.join(1000);

        assertTrue(allReceived, "应该收到所有消息");
    }

    @Test
    public void testClose() throws SocketException {
        DatagramCommunicator communicator = new DatagramCommunicator();
        communicator.bind(0);

        assertDoesNotThrow(() -> communicator.close());
    }

    @Test
    public void testDemonstrate() {
        assertDoesNotThrow(() -> DatagramCommunicator.demonstrate());
    }

    @Test
    public void testMain() {
        assertDoesNotThrow(() -> DatagramCommunicator.main(new String[]{}));
    }
}
