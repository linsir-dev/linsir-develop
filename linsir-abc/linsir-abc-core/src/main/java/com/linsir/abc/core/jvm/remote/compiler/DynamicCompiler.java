package com.linsir.abc.core.jvm.remote.compiler;

import com.linsir.abc.core.jvm.remote.exception.CompileException;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.util.*;

/**
 * 动态Java编译器
 *
 * 功能：将Java源代码字符串编译为字节码
 *
 * 核心特性：
 * 1. 在内存中完成编译，不生成文件
 * 2. 支持自定义编译选项
 * 3. 提供编译诊断信息
 *
 * 使用示例：
 * <pre>
 * DynamicCompiler compiler = new DynamicCompiler();
 * byte[] bytecode = compiler.compile("Hello", "public class Hello { ... }");
 * </pre>
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class DynamicCompiler {

    /**
     * Java编译器实例
     */
    private final JavaCompiler compiler;

    /**
     * 标准文件管理器
     */
    private final StandardJavaFileManager standardFileManager;

    /**
     * 构造动态编译器
     *
     * @throws IllegalStateException 如果无法获取Java编译器（可能使用JRE运行而非JDK）
     */
    public DynamicCompiler() {
        this.compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("无法获取Java编译器，请确保使用JDK运行");
        }
        this.standardFileManager = compiler.getStandardFileManager(null, null, null);
    }

    /**
     * 编译Java源代码
     *
     * @param className 类名（全限定名）
     * @param sourceCode Java源代码
     * @return 编译后的字节码
     * @throws CompileException 编译失败时抛出
     */
    public byte[] compile(String className, String sourceCode) throws CompileException {
        // 创建内存中的Java文件对象
        JavaFileObject sourceFile = new JavaSourceFromString(className, sourceCode);

        // 创建字节码输出管理器
        BytecodeOutputManager outputManager = new BytecodeOutputManager();

        // 配置编译选项
        List<String> options = Arrays.asList("-encoding", "UTF-8");

        // 创建诊断信息收集器
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        // 创建编译任务
        JavaCompiler.CompilationTask task = compiler.getTask(
                null,                           // 输出Writer
                outputManager,                  // 文件管理器
                diagnostics,                    // 诊断信息收集器
                options,                        // 编译选项
                null,                           // 需要编译的类名
                Collections.singletonList(sourceFile)  // 编译单元
        );

        // 执行编译
        Boolean success = task.call();

        if (!success) {
            // 收集编译错误信息
            StringBuilder errorMessage = new StringBuilder("编译失败: " + className + "\n");
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                errorMessage.append("行: ").append(diagnostic.getLineNumber())
                        .append(", 列: ").append(diagnostic.getColumnNumber())
                        .append(", 错误: ").append(diagnostic.getMessage(null))
                        .append("\n");
            }
            throw new CompileException(errorMessage.toString());
        }

        byte[] bytecode = outputManager.getBytecode(className);
        if (bytecode == null) {
            throw new CompileException("编译成功但未获取到字节码: " + className);
        }

        return bytecode;
    }

    /**
     * 批量编译多个Java源文件
     *
     * @param sources Map<类名, 源代码>
     * @return Map<类名, 字节码>
     * @throws CompileException 编译失败时抛出
     */
    public Map<String, byte[]> compileBatch(Map<String, String> sources) throws CompileException {
        Map<String, byte[]> result = new HashMap<>();

        for (Map.Entry<String, String> entry : sources.entrySet()) {
            byte[] bytecode = compile(entry.getKey(), entry.getValue());
            result.put(entry.getKey(), bytecode);
        }

        return result;
    }

    /**
     * 内存中的Java源文件
     *
     * 功能：将字符串包装为JavaFileObject，用于内存编译
     */
    private static class JavaSourceFromString extends SimpleJavaFileObject {

        /**
         * Java源代码
         */
        private final String code;

        /**
         * 构造内存中的Java源文件
         *
         * @param name 类名
         * @param code Java源代码
         */
        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    /**
     * 内存中的字节码输出管理器
     *
     * 功能：管理编译输出的字节码，存储在内存中而非文件系统
     */
    private static class BytecodeOutputManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

        /**
         * 类名到字节码文件的映射
         */
        private final Map<String, ByteArrayJavaFileObject> bytecodeMap = new HashMap<>();

        /**
         * 构造字节码输出管理器
         */
        BytecodeOutputManager() {
            super(ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null));
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className,
                                                   JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            ByteArrayJavaFileObject fileObject = new ByteArrayJavaFileObject(className, kind);
            bytecodeMap.put(className, fileObject);
            return fileObject;
        }

        /**
         * 获取指定类的字节码
         *
         * @param className 类名
         * @return 字节码数组，如果不存在返回null
         */
        byte[] getBytecode(String className) {
            ByteArrayJavaFileObject fileObject = bytecodeMap.get(className);
            return fileObject != null ? fileObject.getBytecode() : null;
        }
    }

    /**
     * 内存中的字节码文件
     *
     * 功能：将字节码存储在内存中的ByteArrayOutputStream
     */
    private static class ByteArrayJavaFileObject extends SimpleJavaFileObject {

        /**
         * 字节码输出流
         */
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        /**
         * 构造内存中的字节码文件
         *
         * @param name 类名
         * @param kind 文件类型
         */
        ByteArrayJavaFileObject(String name, Kind kind) {
            super(URI.create("bytes:///" + name.replace('.', '/') + kind.extension), kind);
        }

        @Override
        public OutputStream openOutputStream() {
            return outputStream;
        }

        /**
         * 获取字节码
         *
         * @return 字节码数组
         */
        byte[] getBytecode() {
            return outputStream.toByteArray();
        }
    }
}
