package com.linsir.abc.core.jvm.remote.server;

import com.linsir.abc.core.jvm.remote.classloader.HotSwapClassLoader;
import com.linsir.abc.core.jvm.remote.compiler.DynamicCompiler;
import com.linsir.abc.core.jvm.remote.context.ExecuteContext;
import com.linsir.abc.core.jvm.remote.model.ExecuteRequest;
import com.linsir.abc.core.jvm.remote.model.ExecuteResult;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 远程执行服务器
 *
 * 功能：接收客户端发送的Java代码，编译执行后返回结果
 *
 * 核心特性：
 * 1. 支持多客户端并发连接
 * 2. 使用线程池处理请求
 * 3. 每个请求使用独立的类加载器，实现代码隔离
 * 4. 支持上下文对象传递
 *
 * 使用示例：
 * <pre>
 * RemoteExecuteServer server = new RemoteExecuteServer(9999);
 * server.start();
 * // 服务器开始监听端口...
 * server.stop();
 * </pre>
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class RemoteExecuteServer {

    /**
     * 服务器监听端口
     */
    private final int port;

    /**
     * 线程池，用于处理客户端请求
     */
    private final ExecutorService executor;

    /**
     * 服务器运行状态标志
     */
    private volatile boolean running = false;

    /**
     * 服务器Socket
     */
    private ServerSocket serverSocket;

    /**
     * 默认线程池大小
     */
    private static final int DEFAULT_THREAD_POOL_SIZE = 10;

    /**
     * 构造远程执行服务器
     *
     * @param port 监听端口
     */
    public RemoteExecuteServer(int port) {
        this(port, DEFAULT_THREAD_POOL_SIZE);
    }

    /**
     * 构造远程执行服务器
     *
     * @param port 监听端口
     * @param threadPoolSize 线程池大小
     */
    public RemoteExecuteServer(int port, int threadPoolSize) {
        this.port = port;
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
    }

    /**
     * 启动服务器
     *
     * 此方法会阻塞，直到服务器停止
     *
     * @throws IOException 启动失败时抛出
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        System.out.println("远程执行服务器已启动，端口: " + port);

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("接受客户端连接: " + clientSocket.getInetAddress());
                executor.submit(new ClientHandler(clientSocket));
            } catch (IOException e) {
                if (running) {
                    System.err.println("接受客户端连接失败: " + e.getMessage());
                }
            }
        }
    }

    /**
     * 停止服务器
     *
     * 关闭ServerSocket和线程池
     */
    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("关闭服务器Socket失败: " + e.getMessage());
        }

        // 优雅关闭线程池
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        System.out.println("远程执行服务器已停止");
    }

    /**
     * 检查服务器是否正在运行
     *
     * @return true表示正在运行
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * 客户端请求处理器
     *
     * 处理单个客户端的连接和请求
     */
    private static class ClientHandler implements Runnable {

        /**
         * 客户端Socket
         */
        private final Socket clientSocket;

        /**
         * 构造客户端处理器
         *
         * @param socket 客户端Socket
         */
        ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

                // 读取执行请求
                ExecuteRequest request = (ExecuteRequest) in.readObject();
                System.out.println("收到执行请求: " + request.getClassName());

                // 执行代码
                ExecuteResult result = executeCode(request);

                // 返回结果
                out.writeObject(result);
                out.flush();

                System.out.println("执行结果: " + (result.isSuccess() ? "成功" : "失败"));

            } catch (Exception e) {
                System.err.println("处理客户端请求失败: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("关闭客户端连接失败: " + e.getMessage());
                }
            }
        }

        /**
         * 执行代码
         *
         * 执行流程：
         * 1. 编译Java源代码
         * 2. 创建类加载器并加载类
         * 3. 创建执行上下文
         * 4. 执行代码
         * 5. 返回执行结果
         *
         * @param request 执行请求
         * @return 执行结果
         */
        private ExecuteResult executeCode(ExecuteRequest request) {
            HotSwapClassLoader classLoader = null;
            try {
                // 1. 编译代码
                DynamicCompiler compiler = new DynamicCompiler();
                byte[] bytecode = compiler.compile(request.getClassName(), request.getSourceCode());

                // 2. 创建类加载器并加载类
                classLoader = new HotSwapClassLoader(Thread.currentThread().getContextClassLoader());
                classLoader.addClass(request.getClassName(), bytecode);
                Class<?> clazz = classLoader.loadClass(request.getClassName());

                // 3. 创建执行上下文
                ExecuteContext context = new ExecuteContext();
                if (request.getContextObjects() != null) {
                    context.addContextObjects(request.getContextObjects());
                }

                // 4. 执行代码
                Object instance = clazz.getDeclaredConstructor().newInstance();

                // 查找execute方法
                java.lang.reflect.Method executeMethod = findExecuteMethod(clazz);
                if (executeMethod != null) {
                    // 重定向系统输出
                    context.redirectSystemOut();
                    try {
                        Object result = executeMethod.invoke(instance, context);
                        return new ExecuteResult(true, context.getOutput(), result, null);
                    } finally {
                        // 恢复系统输出
                        context.restoreSystemOut();
                    }
                } else {
                    return new ExecuteResult(false, "", null,
                            new Exception("未找到execute方法，请确保类中包含 'public Object execute(ExecuteContext context)' 方法"));
                }

            } catch (Exception e) {
                return new ExecuteResult(false, "", null, e);
            } finally {
                // 清理类加载器
                if (classLoader != null) {
                    classLoader.clear();
                }
            }
        }

        /**
         * 查找execute方法
         *
         * 查找条件：
         * 1. 方法名为"execute"
         * 2. 参数数量为1
         * 3. 参数类型为ExecuteContext
         *
         * @param clazz 类对象
         * @return Method对象，如果找不到返回null
         */
        private java.lang.reflect.Method findExecuteMethod(Class<?> clazz) {
            for (java.lang.reflect.Method method : clazz.getMethods()) {
                if ("execute".equals(method.getName()) &&
                        method.getParameterCount() == 1 &&
                        method.getParameterTypes()[0] == ExecuteContext.class) {
                    return method;
                }
            }
            return null;
        }
    }

    /**
     * 主方法，用于启动服务器
     *
     * @param args 命令行参数，第一个参数为端口号（可选，默认为9999）
     */
    public static void main(String[] args) {
        int port = 9999;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("无效的端口号，使用默认端口: 9999");
            }
        }

        RemoteExecuteServer server = new RemoteExecuteServer(port);

        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        try {
            server.start();
        } catch (IOException e) {
            System.err.println("启动服务器失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
