package com.linsir.abc.core.base.lang.thread;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * ThreadLocalContext测试类
 */
public class ThreadLocalContextTest {

    /**
     * 测试用户ID的存取
     */
    @Test
    public void testUserId() {
        ThreadLocalContext.setUserId("user123");
        assertEquals("user123", ThreadLocalContext.getUserId());

        ThreadLocalContext.clear();
        assertNull(ThreadLocalContext.getUserId());
    }

    /**
     * 测试请求ID的存取
     */
    @Test
    public void testRequestId() {
        ThreadLocalContext.setRequestId("req456");
        assertEquals("req456", ThreadLocalContext.getRequestId());

        ThreadLocalContext.clear();
        assertNull(ThreadLocalContext.getRequestId());
    }

    /**
     * 测试上下文属性的存取
     */
    @Test
    public void testAttributes() {
        ThreadLocalContext.setAttribute("key1", "value1");
        ThreadLocalContext.setAttribute("key2", 123);

        assertEquals("value1", ThreadLocalContext.getAttribute("key1"));
        assertEquals(123, ThreadLocalContext.getAttribute("key2"));

        ThreadLocalContext.removeAttribute("key1");
        assertNull(ThreadLocalContext.getAttribute("key1"));

        assertNotNull(ThreadLocalContext.getAllAttributes());
        assertEquals(1, ThreadLocalContext.getAllAttributes().size());

        ThreadLocalContext.clear();
    }

    /**
     * 测试执行时间记录
     */
    @Test
    public void testElapsedTime() throws InterruptedException {
        ThreadLocalContext.recordStartTime();
        Thread.sleep(50);
        long elapsed = ThreadLocalContext.getElapsedTime();

        assertTrue(elapsed >= 50);

        ThreadLocalContext.clear();
    }

    /**
     * 测试线程隔离性
     */
    @Test
    public void testThreadIsolation() throws InterruptedException {
        ThreadLocalContext.setUserId("MainUser");

        final String[] childValue = new String[1];
        Thread childThread = new Thread(() -> {
            childValue[0] = ThreadLocalContext.getUserId();
            ThreadLocalContext.setUserId("ChildUser");
        });

        childThread.start();
        childThread.join();

        assertNull(childValue[0]);
        assertEquals("MainUser", ThreadLocalContext.getUserId());

        ThreadLocalContext.clear();
    }
}
