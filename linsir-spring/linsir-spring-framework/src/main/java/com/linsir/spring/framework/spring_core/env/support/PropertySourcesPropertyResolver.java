package com.linsir.spring.framework.spring_core.env.support;

import com.linsir.spring.framework.spring_core.env.resolver.PropertyResolver;
import com.linsir.spring.framework.spring_core.env.source.PropertySource;

import java.util.HashSet;
import java.util.Set;

/**
 * 基于 PropertySources 的属性解析器实现
 *
 * 从 MutablePropertySources 中解析属性值，支持占位符解析。
 *
 * @author linsir
 * @since 1.0.0
 */
public class PropertySourcesPropertyResolver implements PropertyResolver {

    /**
     * 占位符前缀
     */
    private static final String PLACEHOLDER_PREFIX = "${";

    /**
     * 占位符后缀
     */
    private static final String PLACEHOLDER_SUFFIX = "}";

    /**
     * 默认值分隔符
     */
    private static final String VALUE_SEPARATOR = ":";

    /**
     * 属性源集合
     */
    private final MutablePropertySources propertySources;

    /**
     * 创建一个新的 PropertySourcesPropertyResolver
     *
     * @param propertySources 属性源集合
     */
    public PropertySourcesPropertyResolver(MutablePropertySources propertySources) {
        this.propertySources = propertySources;
    }

    @Override
    public boolean containsProperty(String key) {
        if (this.propertySources == null) {
            return false;
        }
        for (PropertySource<?> propertySource : this.propertySources) {
            if (propertySource.containsProperty(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getProperty(String key) {
        return getProperty(key, String.class);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key, Class<T> targetType) {
        if (this.propertySources == null) {
            return null;
        }
        for (PropertySource<?> propertySource : this.propertySources) {
            Object value = propertySource.getProperty(key);
            if (value != null) {
                return convertValueIfNecessary(value, targetType);
            }
        }
        return null;
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        T value = getProperty(key, targetType);
        return value != null ? value : defaultValue;
    }

    @Override
    public String getRequiredProperty(String key) throws IllegalStateException {
        String value = getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Required key '" + key + "' not found");
        }
        return value;
    }

    @Override
    public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
        T value = getProperty(key, targetType);
        if (value == null) {
            throw new IllegalStateException("Required key '" + key + "' not found");
        }
        return value;
    }

    @Override
    public String resolvePlaceholders(String text) {
        if (text == null || !text.contains(PLACEHOLDER_PREFIX)) {
            return text;
        }
        return parseStringValue(text, new HashSet<>());
    }

    @Override
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        if (text == null || !text.contains(PLACEHOLDER_PREFIX)) {
            return text;
        }
        String result = parseStringValue(text, new HashSet<>());
        if (result.contains(PLACEHOLDER_PREFIX)) {
            throw new IllegalArgumentException("Could not resolve placeholders in: " + text);
        }
        return result;
    }

    /**
     * 解析字符串中的占位符
     *
     * @param strVal 原始字符串
     * @param visitedPlaceholders 已访问的占位符集合（防止循环引用）
     * @return 解析后的字符串
     */
    private String parseStringValue(String strVal, Set<String> visitedPlaceholders) {
        StringBuilder result = new StringBuilder(strVal);
        int startIndex = strVal.indexOf(PLACEHOLDER_PREFIX);

        while (startIndex != -1) {
            int endIndex = findPlaceholderEndIndex(result, startIndex);
            if (endIndex == -1) {
                break;
            }

            String placeholder = result.substring(startIndex + PLACEHOLDER_PREFIX.length(), endIndex);
            String originalPlaceholder = placeholder;

            if (!visitedPlaceholders.add(originalPlaceholder)) {
                throw new IllegalArgumentException(
                    "Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
            }

            // 递归解析占位符
            placeholder = parseStringValue(placeholder, visitedPlaceholders);

            // 获取占位符对应的值
            String propVal = resolvePlaceholder(placeholder);

            // 处理默认值
            if (propVal == null) {
                int separatorIndex = placeholder.indexOf(VALUE_SEPARATOR);
                if (separatorIndex != -1) {
                    String actualPlaceholder = placeholder.substring(0, separatorIndex);
                    String defaultValue = placeholder.substring(separatorIndex + VALUE_SEPARATOR.length());
                    propVal = resolvePlaceholder(actualPlaceholder);
                    if (propVal == null) {
                        propVal = defaultValue;
                    }
                }
            }

            if (propVal != null) {
                // 递归解析属性值
                propVal = parseStringValue(propVal, visitedPlaceholders);
                result.replace(startIndex, endIndex + PLACEHOLDER_SUFFIX.length(), propVal);
                startIndex = result.indexOf(PLACEHOLDER_PREFIX, startIndex + propVal.length());
            } else {
                startIndex = result.indexOf(PLACEHOLDER_PREFIX, endIndex + PLACEHOLDER_SUFFIX.length());
            }

            visitedPlaceholders.remove(originalPlaceholder);
        }

        return result.toString();
    }

    /**
     * 查找占位符结束位置
     *
     * @param buf 字符串构建器
     * @param startIndex 开始位置
     * @return 结束位置，如果不存在则返回 -1
     */
    private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
        int index = startIndex + PLACEHOLDER_PREFIX.length();
        int withinNestedPlaceholder = 0;

        while (index < buf.length()) {
            if (substringMatch(buf, index, PLACEHOLDER_SUFFIX)) {
                if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--;
                    index = index + PLACEHOLDER_SUFFIX.length();
                } else {
                    return index;
                }
            } else if (substringMatch(buf, index, PLACEHOLDER_PREFIX)) {
                withinNestedPlaceholder++;
                index = index + PLACEHOLDER_PREFIX.length();
            } else {
                index++;
            }
        }

        return -1;
    }

    /**
     * 检查子字符串是否匹配
     *
     * @param str 字符串
     * @param index 开始位置
     * @param substring 子字符串
     * @return 如果匹配则返回 true
     */
    private boolean substringMatch(CharSequence str, int index, CharSequence substring) {
        for (int j = 0; j < substring.length(); j++) {
            int i = index + j;
            if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 解析占位符
     *
     * @param placeholder 占位符
     * @return 占位符对应的值
     */
    protected String resolvePlaceholder(String placeholder) {
        if (this.propertySources == null) {
            return null;
        }
        for (PropertySource<?> propertySource : this.propertySources) {
            Object value = propertySource.getProperty(placeholder);
            if (value != null) {
                return String.valueOf(value);
            }
        }
        return null;
    }

    /**
     * 将值转换为指定类型
     *
     * @param value 原始值
     * @param targetType 目标类型
     * @param <T> 目标类型
     * @return 转换后的值
     */
    @SuppressWarnings("unchecked")
    private <T> T convertValueIfNecessary(Object value, Class<T> targetType) {
        if (targetType.isInstance(value)) {
            return (T) value;
        }

        // 基本类型转换
        if (targetType == String.class) {
            return (T) String.valueOf(value);
        } else if (targetType == Integer.class || targetType == int.class) {
            return (T) Integer.valueOf(String.valueOf(value));
        } else if (targetType == Long.class || targetType == long.class) {
            return (T) Long.valueOf(String.valueOf(value));
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return (T) Boolean.valueOf(String.valueOf(value));
        } else if (targetType == Double.class || targetType == double.class) {
            return (T) Double.valueOf(String.valueOf(value));
        } else if (targetType == Float.class || targetType == float.class) {
            return (T) Float.valueOf(String.valueOf(value));
        }

        throw new IllegalArgumentException(
            "Cannot convert value '" + value + "' to type " + targetType.getName());
    }
}
