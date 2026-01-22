package com.linsir.abc.pdai.io.patterns;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 异步IO（AIO）模式示例
 * 演示基于回调机制的异步IO
 */
public class AIODemo {
    private static final int PORT = 8085;
    
    /**
     * 启动AIO服务器
     */
    public static void startServer() {
        System.out.println("5. 异步IO（AIO）模式示例:");
        System.out.println("启动AIO服务器，端口: " + PORT);
        System.out.println("特点: 基于回调机制，IO操作完全异步");
        
        try {
            // 创建AsynchronousServerSocketChannel
            AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            
            System.out.println("AIO服务器已启动，等待连接...");
            
            // 启动一个简单的客户端进行测试
            new Thread(() -> {
                try {
                    // 等待服务器启动
                    Thread.sleep(500);
                    clientTest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            
            // 接受连接（异步）
            serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
                @Override
                public void completed(AsynchronousSocketChannel socketChannel, Void attachment) {
                    // 继续接受新连接
                    serverSocketChannel.accept(null, this);
                    
                    // 处理当前连接
                    try {
                        System.out.println("接收到客户端连接: " + socketChannel.getRemoteAddress());
                    } catch (IOException e) {
                        System.err.println("获取客户端地址失败: " + e.getMessage());
                        e.printStackTrace();
                    }
                    handleConnection(socketChannel, serverSocketChannel);
                }
                
                @Override
                public void failed(Throwable exc, Void attachment) {
                    System.err.println("接受连接失败: " + exc.getMessage());
                    exc.printStackTrace();
                    try {
                        serverSocketChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            
            // 等待异步操作完成
            Thread.sleep(3000);
            
            // 关闭服务器
            serverSocketChannel.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    /**
     * 处理客户端连接
     */
    private static void handleConnection(AsynchronousSocketChannel socketChannel, AsynchronousServerSocketChannel serverSocketChannel) {
        // 读取数据（异步）
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        socketChannel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if (result > 0) {
                    buffer.flip();
                    String clientMessage = new String(buffer.array(), 0, result);
                    System.out.println("接收到客户端消息: " + clientMessage);
                    
                    // 发送响应（异步）
                    String response = "AIO Server Response: " + clientMessage;
                    ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
                    socketChannel.write(responseBuffer, null, new CompletionHandler<Integer, Void>() {
                        @Override
                        public void completed(Integer result, Void attachment) {
                            System.out.println("发送响应: " + response);
                            try {
                                socketChannel.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        
                        @Override
                        public void failed(Throwable exc, Void attachment) {
                            System.err.println("发送响应失败: " + exc.getMessage());
                            exc.printStackTrace();
                            try {
                                socketChannel.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
            
            @Override
            public void failed(Throwable exc, ByteBuffer buffer) {
                System.err.println("读取数据失败: " + exc.getMessage());
                exc.printStackTrace();
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * 客户端测试
     */
    private static void clientTest() {
        try {
            // 创建AsynchronousSocketChannel
            AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
            
            // 连接服务器（异步）
            socketChannel.connect(new InetSocketAddress("localhost", PORT), null, new CompletionHandler<Void, Void>() {
                @Override
                public void completed(Void result, Void attachment) {
                    System.out.println("AIO客户端连接成功");
                    
                    // 发送消息（异步）
                    String message = "Hello AIO Server!";
                    ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
                    socketChannel.write(buffer, null, new CompletionHandler<Integer, Void>() {
                        @Override
                        public void completed(Integer result, Void attachment) {
                            System.out.println("AIO客户端发送消息: " + message);
                            
                            // 读取响应（异步）
                            ByteBuffer responseBuffer = ByteBuffer.allocate(1024);
                            socketChannel.read(responseBuffer, responseBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                                @Override
                                public void completed(Integer result, ByteBuffer responseBuffer) {
                                    if (result > 0) {
                                        responseBuffer.flip();
                                        String response = new String(responseBuffer.array(), 0, result);
                                        System.out.println("AIO客户端接收到响应: " + response);
                                    }
                                    try {
                                        socketChannel.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                
                                @Override
                                public void failed(Throwable exc, ByteBuffer responseBuffer) {
                                    System.err.println("读取响应失败: " + exc.getMessage());
                                    exc.printStackTrace();
                                    try {
                                        socketChannel.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        
                        @Override
                        public void failed(Throwable exc, Void attachment) {
                            System.err.println("发送消息失败: " + exc.getMessage());
                            exc.printStackTrace();
                            try {
                                socketChannel.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                
                @Override
                public void failed(Throwable exc, Void attachment) {
                    System.err.println("连接服务器失败: " + exc.getMessage());
                    exc.printStackTrace();
                    try {
                        socketChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            
            // 等待异步操作完成
            Thread.sleep(2000);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}