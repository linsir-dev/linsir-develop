package com.linsir.spring.framework.spring_core.env.profile;

import com.linsir.spring.framework.spring_core.env.core.Environment;

/**
 * Profile 条件接口
 *
 * 用于判断当前环境是否满足特定的 Profile 条件。
 * 支持简单的 Profile 名称匹配和复杂的逻辑组合。
 *
 * @author linsir
 * @since 1.0.0
 */
@FunctionalInterface
public interface Profiles {

    /**
     * 判断当前环境是否匹配
     *
     * @param environment 环境对象
     * @return 如果匹配则返回 true
     */
    boolean matches(Environment environment);

    /**
     * 解析 Profile 表达式
     *
     * 支持的表达式格式：
     * - "dev" - 匹配 dev profile
     * - "!dev" - 不匹配 dev profile
     * - "dev | prod" - 匹配 dev 或 prod
     * - "dev & test" - 同时匹配 dev 和 test
     *
     * @param expression Profile 表达式
     * @return Profiles 实例
     */
    static Profiles parse(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return env -> false;
        }

        String expr = expression.trim();

        // 处理或表达式 (|)
        if (expr.contains("|")) {
            String[] parts = expr.split("\\|");
            Profiles[] profiles = new Profiles[parts.length];
            for (int i = 0; i < parts.length; i++) {
                profiles[i] = parse(parts[i].trim());
            }
            return env -> {
                for (Profiles p : profiles) {
                    if (p.matches(env)) {
                        return true;
                    }
                }
                return false;
            };
        }

        // 处理与表达式 (&)
        if (expr.contains("&")) {
            String[] parts = expr.split("&");
            Profiles[] profiles = new Profiles[parts.length];
            for (int i = 0; i < parts.length; i++) {
                profiles[i] = parse(parts[i].trim());
            }
            return env -> {
                for (Profiles p : profiles) {
                    if (!p.matches(env)) {
                        return false;
                    }
                }
                return true;
            };
        }

        // 处理否定表达式 (!)
        if (expr.startsWith("!")) {
            String profile = expr.substring(1).trim();
            return env -> !env.acceptsProfiles(profile);
        }

        // 简单 Profile 名称
        return env -> env.acceptsProfiles(expr);
    }

    /**
     * 创建一个匹配任意 Profile 的 Profiles
     *
     * @param profiles Profile 名称数组
     * @return Profiles 实例
     */
    static Profiles of(String... profiles) {
        if (profiles == null || profiles.length == 0) {
            return env -> false;
        }

        return env -> {
            for (String profile : profiles) {
                if (env.acceptsProfiles(profile)) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * 创建一个匹配所有 Profile 的 Profiles
     *
     * @param profiles Profile 名称数组
     * @return Profiles 实例
     */
    static Profiles allOf(String... profiles) {
        if (profiles == null || profiles.length == 0) {
            return env -> true;
        }

        return env -> {
            for (String profile : profiles) {
                if (!env.acceptsProfiles(profile)) {
                    return false;
                }
            }
            return true;
        };
    }
}
