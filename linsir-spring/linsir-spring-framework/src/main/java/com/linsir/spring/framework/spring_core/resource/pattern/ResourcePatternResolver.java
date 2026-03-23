package com.linsir.spring.framework.spring_core.resource.pattern;

import com.linsir.spring.framework.spring_core.resource.core.Resource;
import com.linsir.spring.framework.spring_core.resource.loader.ResourceLoader;

import java.io.IOException;

/**
 * 资源模式解析器接口
 * 扩展 ResourceLoader，支持 Ant 风格的路径模式匹配
 *
 * <p>支持的模式：</p>
 * <ul>
 *   <li>? - 匹配单个字符</li>
 *   <li>* - 匹配零个或多个字符</li>
 *   <li>** - 匹配任意层级的目录</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public interface ResourcePatternResolver extends ResourceLoader {

    /**
     * 类路径所有匹配前缀
     * 用于搜索所有类路径下的资源
     */
    String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    /**
     * 根据路径模式获取资源数组
     *
     * @param locationPattern 路径模式（支持 Ant 风格通配符）
     * @return 匹配的资源数组，不会返回 null
     * @throws IOException 当解析失败时抛出
     */
    Resource[] getResources(String locationPattern) throws IOException;
}
