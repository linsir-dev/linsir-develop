package com.linsir.abc.core.jvm.remote;

import com.linsir.abc.core.jvm.remote.classloader.HotSwapClassLoader;
import com.linsir.abc.core.jvm.remote.client.RemoteExecuteClient;
import com.linsir.abc.core.jvm.remote.compiler.DynamicCompiler;
import com.linsir.abc.core.jvm.remote.context.ExecuteContext;
import com.linsir.abc.core.jvm.remote.exception.CompileException;
import com.linsir.abc.core.jvm.remote.model.ExecuteResult;
import com.linsir.abc.core.jvm.remote.server.RemoteExecuteServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 远程执行功能测试类
 *
 * 测试内容：
 * 1. 动态编译器测试
 * 2. 热替换类加载器测试
 * 3. 执行上下文测试
 * 4. 远程执行服务器/客户端测试
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RemoteExecuteTest {

    private static final int TEST_PORT = 19999;
    private static RemoteExecuteServer server;
    private static ExecutorService serverExecutor;

    /**
     * 在所有测试之前启动服务器
     */
    @BeforeAll
    static void setUpServer() throws IOException {
        server = new RemoteExecuteServer(TEST_PORT, 5);
        serverExecutor = Executors.newSingleThreadExecutor();
        serverExecutor.submit(() -> {
            try {
                server.start();
            } catch (IOException e) {
                System.err.println("服务器启动失败: " + e.getMessage());
            }
        });

        // 等待服务器启动
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 在所有测试之后停止服务器
     */
    @AfterAll
    static void tearDownServer() {
        if (server != null) {
            server.stop();
        }
        if (serverExecutor != null) {
            serverExecutor.shutdown();
        }
    }

    // ==================== 动态编译器测试 ====================

    @Test
    @Order(1)
    @DisplayName("测试动态编译器 - 编译简单类")
    void testDynamicCompilerSimple() throws CompileException {
        DynamicCompiler compiler = new DynamicCompiler();

        String sourceCode =
                "public class SimpleTest {\n" +
                        "    public String sayHello() {\n" +
                        "        return \"Hello, World!\";\n" +
                        "    }\n" +
                        "}";

        byte[] bytecode = compiler.compile("SimpleTest", sourceCode);

        assertNotNull(bytecode);
        assertTrue(bytecode.length > 0);
        System.out.println("编译成功，字节码大小: " + bytecode.length + " bytes");
    }

    @Test
    @Order(2)
    @DisplayName("测试动态编译器 - 编译包含execute方法的类")
    void testDynamicCompilerWithExecute() throws CompileException {
        DynamicCompiler compiler = new DynamicCompiler();

        String sourceCode =
                "import com.linsir.abc.core.jvm.remote.context.ExecuteContext;\n" +
                        "public class ExecuteTest {\n" +
                        "    public Object execute(ExecuteContext context) {\n" +
                        "        context.getOut().println(\"Test Output\");\n" +
                        "        return \"Success\";\n" +
                        "    }\n" +
                        "}";

        byte[] bytecode = compiler.compile("ExecuteTest", sourceCode);

        assertNotNull(bytecode);
        assertTrue(bytecode.length > 0);
        System.out.println("编译成功，字节码大小: " + bytecode.length + " bytes");
    }

    @Test
    @Order(3)
    @DisplayName("测试动态编译器 - 编译错误代码")
    void testDynamicCompilerError() {
        DynamicCompiler compiler = new DynamicCompiler();

        String sourceCode =
                "public class ErrorTest {\n" +
                        "    public String sayHello() {\n" +
                        "        return \"Hello  // 缺少分号\n" +
                        "    }\n" +
                        "}";

        assertThrows(CompileException.class, () -> {
            compiler.compile("ErrorTest", sourceCode);
        });
    }

    @Test
    @Order(4)
    @DisplayName("测试动态编译器 - 批量编译")
    void testDynamicCompilerBatch() throws CompileException {
        DynamicCompiler compiler = new DynamicCompiler();

        Map<String, String> sources = new HashMap<>();
        sources.put("ClassA", "public class ClassA { public String getName() { return \"A\"; } }");
        sources.put("ClassB", "public class ClassB { public String getName() { return \"B\"; } }");
        sources.put("ClassC", "public class ClassC { public String getName() { return \"C\"; } }");

        Map<String, byte[]> bytecodes = compiler.compileBatch(sources);

        assertEquals(3, bytecodes.size());
        assertTrue(bytecodes.containsKey("ClassA"));
        assertTrue(bytecodes.containsKey("ClassB"));
        assertTrue(bytecodes.containsKey("ClassC"));

        System.out.println("批量编译成功，共编译 " + bytecodes.size() + " 个类");
    }

    // ==================== 热替换类加载器测试 ====================

    @Test
    @Order(5)
    @DisplayName("测试热替换类加载器 - 加载单个类")
    void testHotSwapClassLoaderSingle() throws Exception {
        DynamicCompiler compiler = new DynamicCompiler();
        String sourceCode = "public class LoaderTest { public String test() { return \"Loaded\"; } }";
        byte[] bytecode = compiler.compile("LoaderTest", sourceCode);

        HotSwapClassLoader loader = new HotSwapClassLoader(Thread.currentThread().getContextClassLoader());
        loader.addClass("LoaderTest", bytecode);

        Class<?> clazz = loader.loadClass("LoaderTest");
        assertNotNull(clazz);
        assertEquals("LoaderTest", clazz.getName());

        Object instance = clazz.getDeclaredConstructor().newInstance();
        Object result = clazz.getMethod("test").invoke(instance);
        assertEquals("Loaded", result);

        loader.clear();
        System.out.println("热替换类加载器测试通过");
    }

    @Test
    @Order(6)
    @DisplayName("测试热替换类加载器 - 类隔离")
    void testHotSwapClassLoaderIsolation() throws Exception {
        DynamicCompiler compiler = new DynamicCompiler();
        String sourceCode = "public class IsolationTest { public int value = 42; }";
        byte[] bytecode = compiler.compile("IsolationTest", sourceCode);

        // 创建两个独立的类加载器
        HotSwapClassLoader loader1 = new HotSwapClassLoader(Thread.currentThread().getContextClassLoader());
        HotSwapClassLoader loader2 = new HotSwapClassLoader(Thread.currentThread().getContextClassLoader());

        loader1.addClass("IsolationTest", bytecode);
        loader2.addClass("IsolationTest", bytecode);

        Class<?> clazz1 = loader1.loadClass("IsolationTest");
        Class<?> clazz2 = loader2.loadClass("IsolationTest");

        // 验证类隔离
        assertNotSame(clazz1, clazz2);
        assertNotEquals(clazz1.getClassLoader(), clazz2.getClassLoader());

        loader1.clear();
        loader2.clear();
        System.out.println("类隔离测试通过");
    }

    @Test
    @Order(7)
    @DisplayName("测试热替换类加载器 - 系统类委托")
    void testHotSwapClassLoaderSystemClass() throws Exception {
        HotSwapClassLoader loader = new HotSwapClassLoader(Thread.currentThread().getContextClassLoader());

        // 加载系统类，应该委托给父类加载器
        Class<?> stringClass = loader.loadClass("java.lang.String");
        assertNotNull(stringClass);
        assertEquals("java.lang.String", stringClass.getName());

        // 验证系统类由Bootstrap ClassLoader加载
        assertNull(stringClass.getClassLoader());

        loader.clear();
        System.out.println("系统类委托测试通过");
    }

    // ==================== 执行上下文测试 ====================

    @Test
    @Order(8)
    @DisplayName("测试执行上下文 - 输出捕获")
    void testExecuteContextOutput() {
        ExecuteContext context = new ExecuteContext();

        context.redirectSystemOut();
        try {
            System.out.println("Test Line 1");
            System.out.println("Test Line 2");
        } finally {
            context.restoreSystemOut();
        }

        String output = context.getOutput();
        assertTrue(output.contains("Test Line 1"));
        assertTrue(output.contains("Test Line 2"));

        System.out.println("输出捕获测试通过");
    }

    @Test
    @Order(9)
    @DisplayName("测试执行上下文 - 上下文对象管理")
    void testExecuteContextObjects() {
        ExecuteContext context = new ExecuteContext();

        // 添加对象
        context.addContextObject("string", "Hello");
        context.addContextObject("number", 42);
        context.addContextObject("object", new Object());

        // 验证对象存在
        assertTrue(context.hasContextObject("string"));
        assertTrue(context.hasContextObject("number"));

        // 获取对象
        assertEquals("Hello", context.getContextObject("string"));
        assertEquals(42, context.getContextObject("number"));

        // 带类型获取
        String str = context.getContextObject("string", String.class);
        assertEquals("Hello", str);

        // 移除对象
        context.removeContextObject("string");
        assertFalse(context.hasContextObject("string"));

        System.out.println("上下文对象管理测试通过");
    }

    @Test
    @Order(10)
    @DisplayName("测试执行上下文 - 参数管理")
    void testExecuteContextParameters() {
        ExecuteContext context = new ExecuteContext();

        context.setParameter("timeout", 5000);
        context.setParameter("retry", 3);

        assertEquals(5000, context.getParameter("timeout"));
        assertEquals(3, context.getParameter("retry"));

        // 带默认值
        assertEquals(5000, context.getParameter("timeout", 1000));
        assertEquals(1000, context.getParameter("nonexistent", 1000));

        System.out.println("参数管理测试通过");
    }

    // ==================== 远程执行测试 ====================

    @Test
    @Order(11)
    @DisplayName("测试远程执行 - 简单代码执行")
    void testRemoteExecuteSimple() throws Exception {
        RemoteExecuteClient client = new RemoteExecuteClient("localhost", TEST_PORT);

        String sourceCode =
                "import com.linsir.abc.core.jvm.remote.context.ExecuteContext;\n" +
                        "public class RemoteTest1 {\n" +
                        "    public Object execute(ExecuteContext context) {\n" +
                        "        context.getOut().println(\"Remote execution works!\");\n" +
                        "        return \"Success\";\n" +
                        "    }\n" +
                        "}";

        ExecuteResult result = client.execute(sourceCode);

        assertTrue(result.isSuccess(), "执行应该成功");
        assertTrue(result.getOutput().contains("Remote execution works!"));
        assertEquals("Success", result.getResult());

        System.out.println("远程执行测试通过");
        System.out.println("输出: " + result.getOutput());
    }

    @Test
    @Order(12)
    @DisplayName("测试远程执行 - 带上下文对象")
    void testRemoteExecuteWithContext() throws Exception {
        RemoteExecuteClient client = new RemoteExecuteClient("localhost", TEST_PORT);

        String sourceCode =
                "import com.linsir.abc.core.jvm.remote.context.ExecuteContext;\n" +
                        "public class RemoteTest2 {\n" +
                        "    public Object execute(ExecuteContext context) {\n" +
                        "        String message = (String) context.getContextObject(\"message\");\n" +
                        "        Integer count = (Integer) context.getContextObject(\"count\");\n" +
                        "        context.getOut().println(\"Message: \" + message);\n" +
                        "        context.getOut().println(\"Count: \" + count);\n" +
                        "        return message + \" - \" + count;\n" +
                        "    }\n" +
                        "}";

        Map<String, Object> context = new HashMap<>();
        context.put("message", "Hello from client");
        context.put("count", 100);

        ExecuteResult result = client.execute(sourceCode, context);

        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().contains("Message: Hello from client"));
        assertTrue(result.getOutput().contains("Count: 100"));
        assertEquals("Hello from client - 100", result.getResult());

        System.out.println("带上下文对象的远程执行测试通过");
    }

    @Test
    @Order(13)
    @DisplayName("测试远程执行 - 异常处理")
    void testRemoteExecuteException() throws Exception {
        RemoteExecuteClient client = new RemoteExecuteClient("localhost", TEST_PORT);

        String sourceCode =
                "import com.linsir.abc.core.jvm.remote.context.ExecuteContext;\n" +
                        "public class RemoteTest3 {\n" +
                        "    public Object execute(ExecuteContext context) {\n" +
                        "        throw new RuntimeException(\"Test Exception\");\n" +
                        "    }\n" +
                        "}";

        ExecuteResult result = client.execute(sourceCode);

        assertFalse(result.isSuccess(), "执行应该失败");
        assertNotNull(result.getException(), "应该有异常信息");

        System.out.println("异常处理测试通过");
        System.out.println("异常: " + result.getException().getMessage());
    }

    @Test
    @Order(14)
    @DisplayName("测试远程执行 - 多次执行隔离")
    void testRemoteExecuteIsolation() throws Exception {
        RemoteExecuteClient client = new RemoteExecuteClient("localhost", TEST_PORT);

        // 第一次执行
        String sourceCode1 =
                "import com.linsir.abc.core.jvm.remote.context.ExecuteContext;\n" +
                        "public class Counter {\n" +
                        "    private static int count = 0;\n" +
                        "    public Object execute(ExecuteContext context) {\n" +
                        "        count++;\n" +
                        "        context.getOut().println(\"Count: \" + count);\n" +
                        "        return count;\n" +
                        "    }\n" +
                        "}";

        ExecuteResult result1 = client.execute(sourceCode1);
        assertTrue(result1.isSuccess());
        assertEquals(1, result1.getResult());

        // 第二次执行（应该是新的类加载器，count重新初始化）
        ExecuteResult result2 = client.execute(sourceCode1);
        assertTrue(result2.isSuccess());
        assertEquals(1, result2.getResult(), "每次执行应该使用新的类加载器，count应该重新初始化");

        System.out.println("多次执行隔离测试通过");
    }

    @Test
    @Order(15)
    @DisplayName("测试远程执行 - 计算任务")
    void testRemoteExecuteComputation() throws Exception {
        RemoteExecuteClient client = new RemoteExecuteClient("localhost", TEST_PORT);

        String sourceCode =
                "import com.linsir.abc.core.jvm.remote.context.ExecuteContext;\n" +
                        "public class Calculator {\n" +
                        "    public Object execute(ExecuteContext context) {\n" +
                        "        int n = 100;\n" +
                        "        long sum = 0;\n" +
                        "        for (int i = 1; i <= n; i++) {\n" +
                        "            sum += i;\n" +
                        "        }\n" +
                        "        context.getOut().println(\"Sum of 1 to \" + n + \" = \" + sum);\n" +
                        "        return sum;\n" +
                        "    }\n" +
                        "}";

        ExecuteResult result = client.execute(sourceCode);

        assertTrue(result.isSuccess());
        assertEquals(5050L, result.getResult());

        System.out.println("计算任务测试通过");
        System.out.println("输出: " + result.getOutput());
    }
}
