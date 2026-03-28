package com.linsir.abc.core.jvm.tuning.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * 命令执行器
 * 优化的外部命令执行工具，使用进程池和超时控制
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class CommandExecutor {

    private static final Logger LOGGER = Logger.getLogger(CommandExecutor.class.getName());

    /**
     * 默认线程池大小
     */
    private static final int DEFAULT_POOL_SIZE = 4;

    /**
     * 默认命令执行超时（秒）
     */
    private static final long DEFAULT_TIMEOUT_SECONDS = 5;

    /**
     * 执行器线程池
     */
    private final ExecutorService executor;

    /**
     * 命令执行超时时间
     */
    private final long timeout;

    /**
     * 超时时间单位
     */
    private final TimeUnit timeoutUnit;

    public CommandExecutor() {
        this(DEFAULT_POOL_SIZE, DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public CommandExecutor(int poolSize, long timeout, TimeUnit timeoutUnit) {
        this.executor = Executors.newFixedThreadPool(poolSize, r -> {
            Thread t = new Thread(r, "command-executor");
            t.setDaemon(true);
            return t;
        });
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
    }

    /**
     * 执行命令
     *
     * @param command 命令字符串
     * @return 命令输出
     * @throws ExecutionException   执行异常
     * @throws TimeoutException     超时异常
     * @throws InterruptedException 中断异常
     */
    public String execute(String command) throws ExecutionException, TimeoutException, InterruptedException {
        return execute(command.split("\\s+"));
    }

    /**
     * 执行命令
     *
     * @param commands 命令及参数列表
     * @return 命令输出
     * @throws ExecutionException   执行异常
     * @throws TimeoutException     超时异常
     * @throws InterruptedException 中断异常
     */
    public String execute(String[] commands) throws ExecutionException, TimeoutException, InterruptedException {
        Future<String> future = executor.submit(() -> executeInternal(commands));
        return future.get(timeout, timeoutUnit);
    }

    /**
     * 执行命令（带自定义超时）
     *
     * @param command 命令
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return 命令输出
     * @throws ExecutionException   执行异常
     * @throws TimeoutException     超时异常
     * @throws InterruptedException 中断异常
     */
    public String execute(String command, long timeout, TimeUnit unit)
            throws ExecutionException, TimeoutException, InterruptedException {
        Future<String> future = executor.submit(() -> executeInternal(command.split("\\s+")));
        return future.get(timeout, unit);
    }

    /**
     * 使用ProcessBuilder执行命令
     *
     * @param commands 命令列表
     * @return 命令输出
     * @throws IOException          IO异常
     * @throws InterruptedException 中断异常
     */
    public String executeWithProcessBuilder(List<String> commands) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        boolean finished = process.waitFor(timeout, timeoutUnit);
        if (!finished) {
            process.destroyForcibly();
            throw new IOException("Command execution timeout");
        }

        int exitCode = process.exitValue();
        if (exitCode != 0) {
            LOGGER.warning("Command exited with code " + exitCode);
        }

        return output.toString();
    }

    /**
     * 内部执行方法
     *
     * @param commands 命令数组
     * @return 命令输出
     * @throws IOException          IO异常
     * @throws InterruptedException 中断异常
     */
    private String executeInternal(String[] commands) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        boolean finished = process.waitFor(timeout, timeoutUnit);
        if (!finished) {
            process.destroyForcibly();
            throw new IOException("Command execution timeout after " + timeout + " " + timeoutUnit);
        }

        return output.toString();
    }

    /**
     * 异步执行命令
     *
     * @param command  命令
     * @param callback 回调函数
     */
    public void executeAsync(String command, CommandCallback callback) {
        executor.submit(() -> {
            try {
                String result = execute(command);
                callback.onSuccess(result);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * 批量执行命令
     *
     * @param commands 命令列表
     * @return 执行结果列表
     */
    public java.util.List<CommandResult> executeBatch(List<String> commands) {
        java.util.List<CommandResult> results = new java.util.ArrayList<>();

        for (String command : commands) {
            try {
                String output = execute(command);
                results.add(new CommandResult(command, output, null));
            } catch (Exception e) {
                results.add(new CommandResult(command, null, e.getMessage()));
            }
        }

        return results;
    }

    /**
     * 关闭执行器
     */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 命令回调接口
     */
    public interface CommandCallback {
        void onSuccess(String output);
        void onError(Throwable error);
    }

    /**
     * 命令执行结果
     */
    public static class CommandResult {
        private final String command;
        private final String output;
        private final String error;

        public CommandResult(String command, String output, String error) {
            this.command = command;
            this.output = output;
            this.error = error;
        }

        public String getCommand() {
            return command;
        }

        public String getOutput() {
            return output;
        }

        public String getError() {
            return error;
        }

        public boolean isSuccess() {
            return error == null;
        }

        @Override
        public String toString() {
            return "CommandResult{" +
                    "command='" + command + '\'' +
                    ", success=" + isSuccess() +
                    ", outputLength=" + (output != null ? output.length() : 0) +
                    '}';
        }
    }
}
