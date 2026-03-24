package com.linsir.spring.framework.spring_core.bytecode.objenesis;

/**
 * Objenesis运行时异常
 *
 * <p>当Objenesis无法实例化对象时抛出的异常。
 * 这是Objjenesis库中所有异常的基类。
 *
 * <p>常见原因：
 * <ul>
 *   <li>类没有默认构造函数且无法绕过</li>
 *   <li>安全管理器阻止实例化</li>
 *   <li>底层实例化策略不可用</li>
 *   <li>内存分配失败</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 * @see Objenesis
 * @see ObjenesisStd
 */
public class ObjenesisException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     *
     * @param msg 异常消息
     */
    public ObjenesisException(String msg) {
        super(msg);
    }

    /**
     * 构造函数
     *
     * @param msg 异常消息
     * @param cause 原始异常
     */
    public ObjenesisException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * 构造函数
     *
     * @param cause 原始异常
     */
    public ObjenesisException(Throwable cause) {
        super(cause);
    }
}
