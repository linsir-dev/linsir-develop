package com.linsir.spring.framework.spring_core.env.support;

import com.linsir.spring.framework.spring_core.env.core.ConfigurableEnvironment;
import com.linsir.spring.framework.spring_core.env.source.MapPropertySource;
import com.linsir.spring.framework.spring_core.env.source.PropertySource;
import com.linsir.spring.framework.spring_core.env.source.SystemEnvironmentPropertySource;

import java.util.*;

/**
 * 环境抽象基类
 *
 * 提供 ConfigurableEnvironment 接口的通用实现。
 *
 * @author linsir
 * @since 1.0.0
 */
public abstract class AbstractEnvironment implements ConfigurableEnvironment {

    /**
     * 激活的 Profile 属性名
     */
    public static final String ACTIVE_PROFILES_PROPERTY_NAME = "spring.profiles.active";

    /**
     * 默认的 Profile 属性名
     */
    public static final String DEFAULT_PROFILES_PROPERTY_NAME = "spring.profiles.default";

    /**
     * 激活的 Profile 集合
     */
    private final Set<String> activeProfiles = new LinkedHashSet<>();

    /**
     * 默认的 Profile 集合
     */
    private final Set<String> defaultProfiles = new LinkedHashSet<>(Arrays.asList(DEFAULT_PROFILES));

    /**
     * 属性源集合
     */
    private final MutablePropertySources propertySources;

    /**
     * 属性解析器
     */
    private final PropertySourcesPropertyResolver propertyResolver;

    /**
     * 创建一个新的 AbstractEnvironment
     */
    public AbstractEnvironment() {
        this.propertySources = new MutablePropertySources();
        this.propertyResolver = new PropertySourcesPropertyResolver(this.propertySources);
        customizePropertySources(this.propertySources);
    }

    /**
     * 自定义属性源
     * 子类可以重写此方法添加自定义的属性源
     *
     * @param propertySources 属性源集合
     */
    protected void customizePropertySources(MutablePropertySources propertySources) {
        // 添加系统环境变量
        Map<String, Object> envMap = new HashMap<>();
        System.getenv().forEach((k, v) -> envMap.put(k, v));
        propertySources.addLast(
            new SystemEnvironmentPropertySource(envMap)
        );

        // 添加系统属性
        Map<String, Object> systemProperties = new HashMap<>();
        System.getProperties().forEach((k, v) -> systemProperties.put(String.valueOf(k), v));
        propertySources.addLast(
            new MapPropertySource("systemProperties", systemProperties)
        );
    }

    @Override
    public String[] getActiveProfiles() {
        // 从属性源中读取
        String profiles = getProperty(ACTIVE_PROFILES_PROPERTY_NAME);
        if (profiles != null && !profiles.isEmpty()) {
            return profiles.split(",");
        }
        return this.activeProfiles.toArray(new String[0]);
    }

    @Override
    public String[] getDefaultProfiles() {
        // 从属性源中读取
        String profiles = getProperty(DEFAULT_PROFILES_PROPERTY_NAME);
        if (profiles != null && !profiles.isEmpty()) {
            return profiles.split(",");
        }
        return this.defaultProfiles.toArray(new String[0]);
    }

    @Override
    public boolean acceptsProfiles(String profile) {
        if (profile == null || profile.isEmpty()) {
            return false;
        }

        // 处理否定表达式 (!profile)
        if (profile.startsWith("!")) {
            return !acceptsProfiles(profile.substring(1));
        }

        // 检查是否在激活的 Profile 中
        Set<String> activeProfiles = new HashSet<>(Arrays.asList(getActiveProfiles()));
        if (activeProfiles.isEmpty()) {
            // 如果没有激活的 Profile，使用默认的
            activeProfiles = new HashSet<>(Arrays.asList(getDefaultProfiles()));
        }

        return activeProfiles.contains(profile);
    }

    @Override
    public boolean acceptsProfiles(String... profiles) {
        if (profiles == null || profiles.length == 0) {
            return false;
        }

        for (String profile : profiles) {
            if (acceptsProfiles(profile)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void setActiveProfiles(String... profiles) {
        this.activeProfiles.clear();
        if (profiles != null) {
            Collections.addAll(this.activeProfiles, profiles);
        }
    }

    @Override
    public void addActiveProfile(String profile) {
        if (profile != null && !profile.isEmpty()) {
            this.activeProfiles.add(profile);
        }
    }

    @Override
    public void setDefaultProfiles(String... profiles) {
        this.defaultProfiles.clear();
        if (profiles != null) {
            Collections.addAll(this.defaultProfiles, profiles);
        }
    }

    @Override
    public MutablePropertySources getPropertySources() {
        return this.propertySources;
    }

    @Override
    public void merge(ConfigurableEnvironment environment) {
        if (environment == null) {
            return;
        }

        // 合并属性源
        MutablePropertySources otherSources = environment.getPropertySources();
        for (PropertySource<?> propertySource : otherSources) {
            if (!this.propertySources.contains(propertySource.getName())) {
                this.propertySources.addLast(propertySource);
            }
        }

        // 合并激活的 Profile
        for (String profile : environment.getActiveProfiles()) {
            addActiveProfile(profile);
        }
    }

    // PropertyResolver 方法委托给 propertyResolver

    @Override
    public boolean containsProperty(String key) {
        return this.propertyResolver.containsProperty(key);
    }

    @Override
    public String getProperty(String key) {
        return this.propertyResolver.getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return this.propertyResolver.getProperty(key, defaultValue);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType) {
        return this.propertyResolver.getProperty(key, targetType);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return this.propertyResolver.getProperty(key, targetType, defaultValue);
    }

    @Override
    public String getRequiredProperty(String key) throws IllegalStateException {
        return this.propertyResolver.getRequiredProperty(key);
    }

    @Override
    public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
        return this.propertyResolver.getRequiredProperty(key, targetType);
    }

    @Override
    public String resolvePlaceholders(String text) {
        return this.propertyResolver.resolvePlaceholders(text);
    }

    @Override
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        return this.propertyResolver.resolveRequiredPlaceholders(text);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " {activeProfiles=" + Arrays.toString(getActiveProfiles()) +
               ", defaultProfiles=" + Arrays.toString(getDefaultProfiles()) +
               ", propertySources=" + this.propertySources + "}";
    }
}
