package com.linsir.spring.framework.spring_core.bytecode.cglib.core;

/**
 * 类生成策略接口
 *
 * <p>定义了类生成的策略接口，允许自定义字节码生成过程。
 * 可以通过实现此接口来插入自定义的字节码生成逻辑，如：
 * <ul>
 *   <li>调试信息输出</li>
 *   <li>字节码转换</li>
 *   <li>性能监控</li>
 *   <li>安全检查</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * enhancer.setStrategy(new GeneratorStrategy() {
 *     @Override
 *     public byte[] generate(ClassGenerator cg) throws Exception {
 *         // 先生成原始字节码
 *         byte[] bytes = DefaultGeneratorStrategy.INSTANCE.generate(cg);
 *
 *         // 然后可以转换字节码（如添加日志、修改行为等）
 *         byte[] transformed = transform(bytes);
 *
 *         return transformed;
 *     }
 * });
 * }</pre>
 *
 * @author linsir
 * @since 1.0
 * @see AbstractClassGenerator
 * @see DefaultGeneratorStrategy
 */
public interface GeneratorStrategy {

    /**
     * 生成类的字节码
     *
     * <p>根据类生成器定义的信息，生成对应的字节码数组。
     *
     * @param cg 类生成器
     * @return 生成的字节码数组
     * @throws Exception 生成过程中可能抛出的异常
     */
    byte[] generate(ClassGenerator cg) throws Exception;

    /**
     * 类生成器接口
     *
     * <p>定义了类生成所需的基本信息。
     */
    interface ClassGenerator {
        /**
         * 生成类
         *
         * @param visitor 类访问器
         * @throws Exception 生成异常
         */
        void generateClass(ClassVisitor visitor) throws Exception;

        /**
         * 获取类名
         *
         * @return 类名
         */
        String getClassName();

        /**
         * 获取父类名
         *
         * @return 父类名
         */
        String getSuperClassName();
    }

    /**
     * 类访问器接口（简化版，实际对应ASM的ClassVisitor）
     */
    interface ClassVisitor {
        // 简化实现，实际应该包含visit、visitMethod等方法
    }
}
