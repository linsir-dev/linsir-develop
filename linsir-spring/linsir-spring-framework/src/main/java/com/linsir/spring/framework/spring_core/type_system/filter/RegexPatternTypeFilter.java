package com.linsir.spring.framework.spring_core.type_system.filter;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * 正则表达式类型过滤器
 * 根据类名匹配正则表达式来筛选类
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
public class RegexPatternTypeFilter implements TypeFilter {

    /**
     * 正则表达式模式
     */
    private final Pattern pattern;

    public RegexPatternTypeFilter(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public RegexPatternTypeFilter(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        String className = metadataReader.getClassMetadata().getClassName();
        return pattern.matcher(className).matches();
    }

    public Pattern getPattern() {
        return pattern;
    }
}
