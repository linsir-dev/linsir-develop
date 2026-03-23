package com.linsir.spring.framework.spring_core.conversion.formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日期格式化器
 * 用于 Date 类型与字符串之间的双向转换
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public class DateFormatter implements Formatter<Date> {

    private final String pattern;

    /**
     * 构造日期格式化器
     *
     * @param pattern 日期格式模式，如 "yyyy-MM-dd"
     */
    public DateFormatter(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern must not be empty");
        }
        this.pattern = pattern;
    }

    @Override
    public String print(Date object, Locale locale) {
        if (object == null) {
            return "";
        }
        return createDateFormat(locale).format(object);
    }

    @Override
    public Date parse(String text, Locale locale) throws ParseException {
        if (text == null || text.isEmpty()) {
            return null;
        }
        return createDateFormat(locale).parse(text);
    }

    /**
     * 创建日期格式化对象
     *
     * @param locale 本地化信息
     * @return SimpleDateFormat 实例
     */
    private SimpleDateFormat createDateFormat(Locale locale) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setLenient(false);
        return dateFormat;
    }

    /**
     * 获取日期格式模式
     *
     * @return 日期格式模式
     */
    public String getPattern() {
        return pattern;
    }
}
