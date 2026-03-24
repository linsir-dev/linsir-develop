package com.linsir.spring.framework.spring_core.bytecode.cglib.proxy;

import java.lang.reflect.Method;

/**
 * 回调过滤器接口
 *
 * <p>用于决定每个方法使用哪个回调对象。当设置多个回调时，
 * 通过回调过滤器可以为不同方法指定不同的回调索引。
 *
 * <p>使用示例：
 * <pre>{@code
 * enhancer.setCallbacks(new Callback[]{
 *     new LoggingInterceptor(),
 *     new TransactionInterceptor(),
 *     NoOp.INSTANCE
 * });
 *
 * enhancer.setCallbackFilter(new CallbackFilter() {
 *     @Override
 *     public int accept(Method method) {
 *         // 查询方法使用索引0的回调（日志）
 *         if (method.getName().startsWith("query")) {
 *             return 0;
 *         }
 *         // 更新方法使用索引1的回调（事务）
 *         if (method.getName().startsWith("update")) {
 *             return 1;
 *         }
 *         // 其他方法使用索引2的回调（无操作）
 *         return 2;
 *     }
 * });
 * }</pre>
 *
 * @author linsir
 * @since 1.0
 * @see Enhancer#setCallbackFilter(CallbackFilter)
 * @see Enhancer#setCallbacks(Callback[])
 */
public interface CallbackFilter {

    /**
     * 确定方法使用的回调索引
     *
     * <p>返回的索引对应 {@link Enhancer#setCallbacks(Callback[])} 中设置的回调数组位置。
     *
     * @param method 被拦截的方法
     * @return 回调数组中的索引
     */
    int accept(Method method);
}
