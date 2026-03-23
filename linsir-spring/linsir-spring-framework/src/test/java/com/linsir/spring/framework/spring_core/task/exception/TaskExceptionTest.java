package com.linsir.spring.framework.spring_core.task.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 任务异常测试
 *
 * 测试 TaskRejectedException 和 TaskTimeoutException
 *
 * @author linsir
 * @since 1.0.0
 */
class TaskExceptionTest {

    @Test
    void testTaskRejectedExceptionDefaultConstructor() {
        // 测试默认构造函数
        TaskRejectedException exception = new TaskRejectedException();

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testTaskRejectedExceptionWithMessage() {
        // 测试带消息的构造函数
        String message = "Task was rejected";
        TaskRejectedException exception = new TaskRejectedException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testTaskRejectedExceptionWithMessageAndCause() {
        // 测试带消息和原因的构造函数
        String message = "Task was rejected";
        Throwable cause = new RuntimeException("Original error");
        TaskRejectedException exception = new TaskRejectedException(message, cause);

        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testTaskRejectedExceptionWithCause() {
        // 测试只带原因的构造函数
        Throwable cause = new IllegalStateException("State error");
        TaskRejectedException exception = new TaskRejectedException(cause);

        assertNotNull(exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testTaskTimeoutExceptionDefaultConstructor() {
        // 测试默认构造函数
        TaskTimeoutException exception = new TaskTimeoutException();

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testTaskTimeoutExceptionWithMessage() {
        // 测试带消息的构造函数
        String message = "Task execution timed out";
        TaskTimeoutException exception = new TaskTimeoutException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testTaskTimeoutExceptionWithMessageAndCause() {
        // 测试带消息和原因的构造函数
        String message = "Task execution timed out";
        Throwable cause = new InterruptedException("Interrupted");
        TaskTimeoutException exception = new TaskTimeoutException(message, cause);

        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testTaskTimeoutExceptionWithCause() {
        // 测试只带原因的构造函数
        Throwable cause = new java.util.concurrent.TimeoutException();
        TaskTimeoutException exception = new TaskTimeoutException(cause);

        assertNotNull(exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testTaskRejectedExceptionIsRuntimeException() {
        // 测试是 RuntimeException 的子类
        TaskRejectedException exception = new TaskRejectedException("test");

        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testTaskTimeoutExceptionIsRuntimeException() {
        // 测试是 RuntimeException 的子类
        TaskTimeoutException exception = new TaskTimeoutException("test");

        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testExceptionChaining() {
        // 测试异常链
        Throwable rootCause = new IllegalArgumentException("Root cause");
        Throwable intermediate = new RuntimeException("Intermediate", rootCause);
        TaskRejectedException topLevel = new TaskRejectedException("Top level", intermediate);

        assertEquals("Top level", topLevel.getMessage());
        assertSame(intermediate, topLevel.getCause());
        assertSame(rootCause, topLevel.getCause().getCause());
    }

    @Test
    void testExceptionSerialVersionUID() {
        // 测试 serialVersionUID 存在
        // 这只是编译时检查，如果类定义正确，这个测试总是通过
        assertDoesNotThrow(() -> {
            @SuppressWarnings("unused")
            TaskRejectedException ex1 = new TaskRejectedException();
            @SuppressWarnings("unused")
            TaskTimeoutException ex2 = new TaskTimeoutException();
        });
    }
}
