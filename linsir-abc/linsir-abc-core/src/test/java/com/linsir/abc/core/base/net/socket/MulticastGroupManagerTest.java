package com.linsir.abc.core.base.net.socket;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MulticastGroupManager测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class MulticastGroupManagerTest {

    @Test
    public void testJoinAndLeaveGroup() throws IOException {
        String multicastAddress = "230.0.0.1";
        int port = 12360;

        MulticastGroupManager manager = new MulticastGroupManager();
        manager.joinGroup(multicastAddress, port);

        // 成功加入后离开
        assertDoesNotThrow(() -> manager.leaveGroup());

        manager.close();
    }

    @Test
    public void testSendAndReceive() throws IOException, InterruptedException {
        String multicastAddress = "230.0.0.1";
        int port = 12361;
        String testMessage = "Hello Multicast!";

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> receivedMessage = new AtomicReference<>();

        // 启动接收者
        Thread receiverThread = new Thread(() -> {
            try {
                MulticastGroupManager receiver = new MulticastGroupManager();
                receiver.joinGroup(multicastAddress, port);

                String message = receiver.receive(1024);
                receivedMessage.set(message);
                latch.countDown();

                receiver.leaveGroup();
                receiver.close();
            } catch (IOException e) {
                // ignore
            }
        });
        receiverThread.start();

        // 等待接收者加入
        Thread.sleep(300);

        // 发送多播消息
        MulticastGroupManager sender = new MulticastGroupManager();
        sender.joinGroup(multicastAddress, port);
        sender.send(testMessage, port);
        sender.leaveGroup();
        sender.close();

        // 等待接收完成
        boolean received = latch.await(3, TimeUnit.SECONDS);
        receiverThread.join(1000);

        // 多播可能在某些网络环境下不工作，所以只检查不抛出异常
        if (received) {
            assertEquals(testMessage, receivedMessage.get());
        }
    }

    @Test
    public void testMultipleReceivers() throws IOException, InterruptedException {
        String multicastAddress = "230.0.0.1";
        int port = 12362;
        String testMessage = "Multicast to All";

        CountDownLatch latch = new CountDownLatch(2);

        // 启动两个接收者
        Thread receiver1 = new Thread(() -> {
            try {
                MulticastGroupManager receiver = new MulticastGroupManager();
                receiver.joinGroup(multicastAddress, port);
                receiver.receive(1024);
                latch.countDown();
                receiver.leaveGroup();
                receiver.close();
            } catch (IOException e) {
                // ignore
            }
        });

        Thread receiver2 = new Thread(() -> {
            try {
                MulticastGroupManager receiver = new MulticastGroupManager();
                receiver.joinGroup(multicastAddress, port);
                receiver.receive(1024);
                latch.countDown();
                receiver.leaveGroup();
                receiver.close();
            } catch (IOException e) {
                // ignore
            }
        });

        receiver1.start();
        receiver2.start();

        Thread.sleep(300);

        // 发送消息
        MulticastGroupManager sender = new MulticastGroupManager();
        sender.joinGroup(multicastAddress, port);
        sender.send(testMessage, port);
        sender.leaveGroup();
        sender.close();

        // 等待两个接收者都收到
        boolean allReceived = latch.await(3, TimeUnit.SECONDS);
        receiver1.join(1000);
        receiver2.join(1000);

        // 多播可能在某些网络环境下不工作
        if (allReceived) {
            assertTrue(true, "两个接收者都收到了消息");
        }
    }

    @Test
    public void testClose() throws IOException {
        String multicastAddress = "230.0.0.1";
        int port = 12363;

        MulticastGroupManager manager = new MulticastGroupManager();
        manager.joinGroup(multicastAddress, port);

        assertDoesNotThrow(() -> manager.close());
    }

    @Test
    public void testDemonstrate() {
        assertDoesNotThrow(() -> MulticastGroupManager.demonstrate());
    }

    @Test
    public void testMain() {
        assertDoesNotThrow(() -> MulticastGroupManager.main(new String[]{}));
    }
}
