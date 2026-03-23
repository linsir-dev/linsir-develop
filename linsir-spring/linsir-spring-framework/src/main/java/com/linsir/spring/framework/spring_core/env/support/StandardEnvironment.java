package com.linsir.spring.framework.spring_core.env.support;

import com.linsir.spring.framework.spring_core.env.source.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * 标准环境实现
 *
 * 提供标准的环境实现，适用于非 Web 应用程序。
 * 默认包含系统属性和系统环境变量。
 *
 * @author linsir
 * @since 1.0.0
 */
public class StandardEnvironment extends AbstractEnvironment {

    /**
     * Servlet 上下文初始化参数属性源名称
     */
    public static final String SERVLET_CONTEXT_PROPERTY_SOURCE_NAME = "servletContextInitParams";

    /**
     * Servlet 配置参数属性源名称
     */
    public static final String SERVLET_CONFIG_PROPERTY_SOURCE_NAME = "servletConfigInitParams";

    /**
     * JNDI 属性源名称
     */
    public static final String JNDI_PROPERTY_SOURCE_NAME = "jndiProperties";

    /**
     * 创建一个新的 StandardEnvironment
     */
    public StandardEnvironment() {
        super();
    }

    @Override
    protected void customizePropertySources(MutablePropertySources propertySources) {
        // 添加 Servlet 上下文参数（如果在 Web 环境中）
        // 这里简化处理，实际应该检查是否在 Web 环境中

        // 添加 Servlet 配置参数（如果在 Web 环境中）
        // 这里简化处理

        // 添加 JNDI 属性（如果可用）
        // 这里简化处理

        // 调用父类方法添加系统属性和环境变量
        super.customizePropertySources(propertySources);
    }

    /**
     * 添加命令行参数
     *
     * @param args 命令行参数数组
     */
    public void addCommandLineArgs(String[] args) {
        if (args == null || args.length == 0) {
            return;
        }

        Map<String, Object> commandLineArgs = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("--")) {
                String keyValue = arg.substring(2);
                int equalsIndex = keyValue.indexOf('=');

                if (equalsIndex > 0) {
                    String key = keyValue.substring(0, equalsIndex);
                    String value = keyValue.substring(equalsIndex + 1);
                    commandLineArgs.put(key, value);
                } else if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    commandLineArgs.put(keyValue, args[i + 1]);
                    i++;
                } else {
                    commandLineArgs.put(keyValue, "true");
                }
            }
        }

        if (!commandLineArgs.isEmpty()) {
            getPropertySources().addFirst(
                new MapPropertySource("commandLineArgs", commandLineArgs)
            );
        }
    }
}
