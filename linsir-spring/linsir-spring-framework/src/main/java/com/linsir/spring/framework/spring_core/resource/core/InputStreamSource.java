package com.linsir.spring.framework.spring_core.resource.core;

import java.io.IOException;
import java.io.InputStream;

/**
 * 输入流源接口
 * 定义获取输入流的统一方式，是 Resource 接口的父接口
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
@FunctionalInterface
public interface InputStreamSource {

    /**
     * 获取输入流
     * 每次调用都应该返回一个新的输入流
     *
     * @return 输入流
     * @throws IOException 当无法打开输入流时抛出
     */
    InputStream getInputStream() throws IOException;
}
