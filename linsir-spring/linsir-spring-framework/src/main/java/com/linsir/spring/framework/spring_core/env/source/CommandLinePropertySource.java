package com.linsir.spring.framework.spring_core.env.source;

import java.util.HashMap;
import java.util.Map;

/**
 * 命令行参数属性源
 *
 * 从命令行参数中解析属性值，支持 --key=value 和 --key value 两种格式。
 *
 * @author linsir
 * @since 1.0.0
 */
public class CommandLinePropertySource extends MapPropertySource {

    /**
     * 默认的属性源名称
     */
    public static final String COMMAND_LINE_PROPERTY_SOURCE_NAME = "commandLineArgs";

    /**
     * 创建一个新的 CommandLinePropertySource
     *
     * @param name 属性源名称
     * @param args 命令行参数数组
     */
    public CommandLinePropertySource(String name, String[] args) {
        super(name, parseArgs(args));
    }

    /**
     * 使用默认名称创建 CommandLinePropertySource
     *
     * @param args 命令行参数数组
     */
    public CommandLinePropertySource(String[] args) {
        this(COMMAND_LINE_PROPERTY_SOURCE_NAME, args);
    }

    /**
     * 解析命令行参数
     *
     * @param args 命令行参数数组
     * @return 解析后的属性 Map
     */
    private static Map<String, Object> parseArgs(String[] args) {
        Map<String, Object> result = new HashMap<>();
        if (args == null) {
            return result;
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            // 处理 --key=value 格式
            if (arg.startsWith("--")) {
                String keyValue = arg.substring(2);
                int equalsIndex = keyValue.indexOf('=');

                if (equalsIndex > 0) {
                    // --key=value 格式
                    String key = keyValue.substring(0, equalsIndex);
                    String value = keyValue.substring(equalsIndex + 1);
                    result.put(key, value);
                } else if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    // --key value 格式
                    result.put(keyValue, args[i + 1]);
                    i++; // 跳过下一个参数
                } else {
                    // --flag 格式（布尔标志）
                    result.put(keyValue, "true");
                }
            }
        }

        return result;
    }
}
