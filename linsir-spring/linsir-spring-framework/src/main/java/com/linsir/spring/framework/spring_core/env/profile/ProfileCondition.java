package com.linsir.spring.framework.spring_core.env.profile;

import com.linsir.spring.framework.spring_core.env.core.Environment;

/**
 * Profile 条件判断工具类
 *
 * 提供静态方法用于判断 Profile 条件，简化条件判断代码。
 *
 * @author linsir
 * @since 1.0.0
 */
public final class ProfileCondition {

    private ProfileCondition() {
        // 工具类，禁止实例化
    }

    /**
     * 判断是否激活了指定的 Profile
     *
     * @param environment 环境对象
     * @param profile Profile 名称
     * @return 如果激活则返回 true
     */
    public static boolean isActive(Environment environment, String profile) {
        return environment.acceptsProfiles(profile);
    }

    /**
     * 判断是否激活了任意一个指定的 Profile
     *
     * @param environment 环境对象
     * @param profiles Profile 名称数组
     * @return 如果至少有一个激活则返回 true
     */
    public static boolean isAnyActive(Environment environment, String... profiles) {
        return environment.acceptsProfiles(profiles);
    }

    /**
     * 判断是否没有激活任何 Profile
     *
     * @param environment 环境对象
     * @return 如果没有激活任何 Profile 则返回 true
     */
    public static boolean isNoProfileActive(Environment environment) {
        String[] activeProfiles = environment.getActiveProfiles();
        return activeProfiles == null || activeProfiles.length == 0;
    }

    /**
     * 判断是否处于开发环境
     *
     * @param environment 环境对象
     * @return 如果是开发环境则返回 true
     */
    public static boolean isDev(Environment environment) {
        return isActive(environment, "dev") || isActive(environment, "development");
    }

    /**
     * 判断是否处于测试环境
     *
     * @param environment 环境对象
     * @return 如果是测试环境则返回 true
     */
    public static boolean isTest(Environment environment) {
        return isActive(environment, "test") || isActive(environment, "testing");
    }

    /**
     * 判断是否处于生产环境
     *
     * @param environment 环境对象
     * @return 如果是生产环境则返回 true
     */
    public static boolean isProd(Environment environment) {
        return isActive(environment, "prod") || isActive(environment, "production");
    }

    /**
     * 判断是否处于预发布环境
     *
     * @param environment 环境对象
     * @return 如果是预发布环境则返回 true
     */
    public static boolean isStaging(Environment environment) {
        return isActive(environment, "staging") || isActive(environment, "preprod");
    }
}
