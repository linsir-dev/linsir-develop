package com.linsir.spring.framework.spring_core.conversion.formatter;

import java.text.ParseException;
import java.util.Locale;

/**
 * 格式化接口
 * 用于对象与字符串之间的双向转换
 *
 * <p>Formatter 接口继承自 Printer 和 Parser，提供：
 * <ul>
 *   <li>print: 将对象格式化为字符串（考虑 Locale）</li>
 *   <li>parse: 将字符串解析为对象（考虑 Locale）</li>
 * </ul>
 * </p>
 *
 * <p>与 Converter 的区别：
 * <ul>
 *   <li>Formatter 专门处理字符串与对象的转换</li>
 *   <li>Formatter 支持国际化（Locale）</li>
 *   <li>Formatter 用于展示层面的格式化</li>
 * </ul>
 * </p>
 *
 * @param <T> 目标类型
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public interface Formatter<T> extends Printer<T>, Parser<T> {
}

/**
 * 打印接口
 * 将对象格式化为字符串
 *
 * @param <T> 目标类型
 */
interface Printer<T> {

    /**
     * 将对象打印为字符串
     *
     * @param object 要打印的对象
     * @param locale 本地化信息
     * @return 格式化后的字符串
     */
    String print(T object, Locale locale);
}

/**
 * 解析接口
 * 将字符串解析为对象
 *
 * @param <T> 目标类型
 */
interface Parser<T> {

    /**
     * 解析字符串为对象
     *
     * @param text 要解析的字符串
     * @param locale 本地化信息
     * @return 解析后的对象
     * @throws ParseException 解析失败时抛出
     */
    T parse(String text, Locale locale) throws ParseException;
}
