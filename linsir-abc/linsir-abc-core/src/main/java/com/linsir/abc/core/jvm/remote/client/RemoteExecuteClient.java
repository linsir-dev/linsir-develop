package com.linsir.abc.core.jvm.remote.client;

import com.linsir.abc.core.jvm.remote.model.ExecuteRequest;
import com.linsir.abc.core.jvm.remote.model.ExecuteResult;

import java.io.*;
import java.net.Socket;
import java.util.Map;

/**
 * 远程执行客户端
 *
 * 功能：向服务器发送Java代码并获取执行结果
 *
 * 核心特性：
 * 1. 支持发送Java源代码到远程服务器执行
 * 2. 支持传递上下文对象
 * 3. 自动提取类名
 * 4. 支持超时设置
 *
 * 使用示例：
 * <pre>
 * RemoteExecuteClient client = new RemoteExecuteClient("localhost", 9999);
 * String sourceCode = "public class Hello { " +
 *     "public Object execute(ExecuteContext ctx) { " +
 *     "    ctx.getOut().println(\"Hello World\"); " +
 *     "    return \"Success\"; " +
 *     "} " +
 * "}";
 * ExecuteResult result = client.execute(sourceCode);
 * System.out.println(result.getOutput());
 * </pre>
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class RemoteExecuteClient {

    /**
     * 服务器主机地址
     */
    private final String host;

    /**
     * 服务器端口
     */
    private final int port;

    /**
     * 连接超时时间（毫秒）
     */
    private int connectionTimeout = 5000;

    /**
     * 读取超时时间（毫秒）
     */
    private int readTimeout = 30000;

    /**
     * 构造远程执行客户端
     *
     * @param host 服务器主机地址
     * @param port 服务器端口
     */
    public RemoteExecuteClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 设置连接超时时间
     *
     * @param timeout 超时时间（毫秒）
     */
    public void setConnectionTimeout(int timeout) {
        this.connectionTimeout = timeout;
    }

    /**
     * 设置读取超时时间
     *
     * @param timeout 超时时间（毫秒）
     */
    public void setReadTimeout(int timeout) {
        this.readTimeout = timeout;
    }

    /**
     * 执行远程代码（完整版）
     *
     * @param className 类名
     * @param sourceCode Java源代码
     * @param contextObjects 上下文对象
     * @return 执行结果
     * @throws Exception 执行失败时抛出
     */
    public ExecuteResult execute(String className, String sourceCode,
                                  Map<String, Object> contextObjects) throws Exception {

        // 构建请求
        ExecuteRequest request = new ExecuteRequest();
        request.setClassName(className);
        request.setSourceCode(sourceCode);
        request.setContextObjects(contextObjects);

        // 发送请求并获取结果
        return sendRequest(request);
    }

    /**
     * 执行远程代码（简化版）
     *
     * 自动从源代码中提取类名
     *
     * @param sourceCode Java源代码
     * @return 执行结果
     * @throws Exception 执行失败时抛出
     */
    public ExecuteResult execute(String sourceCode) throws Exception {
        String className = extractClassName(sourceCode);
        return execute(className, sourceCode, null);
    }

    /**
     * 执行远程代码（带上下文）
     *
     * @param sourceCode Java源代码
     * @param contextObjects 上下文对象
     * @return 执行结果
     * @throws Exception 执行失败时抛出
     */
    public ExecuteResult execute(String sourceCode, Map<String, Object> contextObjects) throws Exception {
        String className = extractClassName(sourceCode);
        return execute(className, sourceCode, contextObjects);
    }

    /**
     * 发送执行请求到服务器
     *
     * @param request 执行请求
     * @return 执行结果
     * @throws Exception 通信失败时抛出
     */
    private ExecuteResult sendRequest(ExecuteRequest request) throws Exception {
        try (Socket socket = new Socket()) {
            // 连接服务器
            socket.connect(new java.net.InetSocketAddress(host, port), connectionTimeout);
            socket.setSoTimeout(readTimeout);

            try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                // 发送请求
                out.writeObject(request);
                out.flush();

                // 接收结果
                Object response = in.readObject();
                if (response instanceof ExecuteResult) {
                    return (ExecuteResult) response;
                } else {
                    throw new IOException("服务器返回了意外的响应类型: " + response.getClass());
                }
            }
        }
    }

    /**
     * 从源代码中提取类名
     *
     * 提取规则：
     * 1. 查找 "public class" 关键字
     * 2. 提取后面的类名（直到遇到空格、{ 或 extends/implements）
     *
     * @param sourceCode Java源代码
     * @return 类名，如果无法提取返回 "DynamicClass"
     */
    private String extractClassName(String sourceCode) {
        if (sourceCode == null || sourceCode.isEmpty()) {
            return "DynamicClass";
        }

        // 查找 public class
        String[] patterns = {"public class", "class"};
        for (String pattern : patterns) {
            int classIndex = sourceCode.indexOf(pattern);
            if (classIndex != -1) {
                int start = classIndex + pattern.length();
                // 跳过空格
                while (start < sourceCode.length() && Character.isWhitespace(sourceCode.charAt(start))) {
                    start++;
                }
                // 查找类名结束位置
                int end = start;
                while (end < sourceCode.length()) {
                    char c = sourceCode.charAt(end);
                    if (Character.isWhitespace(c) || c == '{' || c == '<') {
                        break;
                    }
                    end++;
                }
                if (end > start) {
                    return sourceCode.substring(start, end).trim();
                }
            }
        }

        return "DynamicClass";
    }

    /**
     * 获取服务器主机地址
     *
     * @return 主机地址
     */
    public String getHost() {
        return host;
    }

    /**
     * 获取服务器端口
     *
     * @return 端口号
     */
    public int getPort() {
        return port;
    }

    /**
     * 主方法，用于测试客户端
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        String host = "localhost";
        int port = 9999;

        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("无效的端口号，使用默认端口: 9999");
            }
        }

        RemoteExecuteClient client = new RemoteExecuteClient(host, port);

        // 示例代码
        String sourceCode =
                "import com.linsir.abc.core.jvm.remote.context.ExecuteContext;\n" +
                        "public class HelloWorld {\n" +
                        "    public Object execute(ExecuteContext context) {\n" +
                        "        context.getOut().println(\"Hello, Remote Execution!\");\n" +
                        "        context.getOut().println(\"Current time: \" + System.currentTimeMillis());\n" +
                        "        return \"Success\";\n" +
                        "    }\n" +
                        "}";

        try {
            System.out.println("连接到服务器: " + host + ":" + port);
            ExecuteResult result = client.execute(sourceCode);

            if (result.isSuccess()) {
                System.out.println("执行成功!");
                System.out.println("输出:");
                System.out.println(result.getOutput());
                System.out.println("返回值: " + result.getResult());
            } else {
                System.out.println("执行失败!");
                if (result.getException() != null) {
                    System.out.println("异常: " + result.getException().getMessage());
                    result.getException().printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("执行出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
