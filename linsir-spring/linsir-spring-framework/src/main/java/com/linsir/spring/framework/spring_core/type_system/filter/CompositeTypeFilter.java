package com.linsir.spring.framework.spring_core.type_system.filter;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 组合类型过滤器
 * 支持多个过滤器的AND和OR组合
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
public class CompositeTypeFilter implements TypeFilter {

    /**
     * 组合模式
     */
    public enum MatchMode {
        /**
         * 所有过滤器都必须匹配（AND）
         */
        ALL,
        /**
         * 任意一个过滤器匹配即可（OR）
         */
        ANY
    }

    /**
     * 过滤器列表
     */
    private final List<TypeFilter> filters;

    /**
     * 匹配模式
     */
    private final MatchMode matchMode;

    public CompositeTypeFilter(MatchMode matchMode, TypeFilter... filters) {
        this.matchMode = matchMode;
        this.filters = new ArrayList<>(Arrays.asList(filters));
    }

    public CompositeTypeFilter(TypeFilter... filters) {
        this(MatchMode.ALL, filters);
    }

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        if (filters.isEmpty()) {
            return true;
        }

        if (matchMode == MatchMode.ALL) {
            // AND模式：所有过滤器都必须匹配
            for (TypeFilter filter : filters) {
                if (!filter.match(metadataReader, metadataReaderFactory)) {
                    return false;
                }
            }
            return true;
        } else {
            // OR模式：任意一个匹配即可
            for (TypeFilter filter : filters) {
                if (filter.match(metadataReader, metadataReaderFactory)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 添加过滤器
     *
     * @param filter 要添加的过滤器
     */
    public void addFilter(TypeFilter filter) {
        this.filters.add(filter);
    }

    public List<TypeFilter> getFilters() {
        return new ArrayList<>(filters);
    }

    public MatchMode getMatchMode() {
        return matchMode;
    }
}
