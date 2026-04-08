package com.linsir.system.core.constants;

/**
 * 通用常量定义
 *
 * @author linsir
 * @version 1.0.0
 */
public final class CommonConstants {

    private CommonConstants() {
        // 私有构造函数，防止实例化
    }

    // ==================== 系统相关 ====================

    /**
     * 系统编码
     */
    public static final String SYSTEM_ENCODING = "UTF-8";

    /**
     * 默认语言
     */
    public static final String DEFAULT_LANGUAGE = "zh_CN";

    // ==================== 状态相关 ====================

    /**
     * 启用状态
     */
    public static final Integer STATUS_ENABLE = 1;

    /**
     * 禁用状态
     */
    public static final Integer STATUS_DISABLE = 0;

    /**
     * 是
     */
    public static final Integer YES = 1;

    /**
     * 否
     */
    public static final Integer NO = 0;

    // ==================== 删除标志 ====================

    /**
     * 未删除
     */
    public static final Integer DELETED_NO = 0;

    /**
     * 已删除
     */
    public static final Integer DELETED_YES = 1;

    // ==================== 分页相关 ====================

    /**
     * 默认页码
     */
    public static final long DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页条数
     */
    public static final long DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大每页条数
     */
    public static final long MAX_PAGE_SIZE = 100;

    // ==================== 时间格式 ====================

    /**
     * 日期时间格式
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期格式
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 时间格式
     */
    public static final String TIME_PATTERN = "HH:mm:ss";

    // ==================== 符号常量 ====================

    /**
     * 逗号
     */
    public static final String COMMA = ",";

    /**
     * 点
     */
    public static final String DOT = ".";

    /**
     * 斜杠
     */
    public static final String SLASH = "/";

    /**
     * 冒号
     */
    public static final String COLON = ":";

    /**
     * 分号
     */
    public static final String SEMICOLON = ";";

    /**
     * 横线
     */
    public static final String DASH = "-";

    /**
     * 下划线
     */
    public static final String UNDERSCORE = "_";

    /**
     * 空字符串
     */
    public static final String EMPTY = "";
}
