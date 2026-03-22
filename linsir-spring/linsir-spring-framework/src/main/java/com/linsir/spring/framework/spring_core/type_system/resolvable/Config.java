package com.linsir.spring.framework.spring_core.type_system.resolvable;

/**
 * Configuration class for demonstrating type resolution
 */
public class Config {

    /**
     * Generic configuration holder
     */
    public static class ConfigHolder<T> {
        private T config;
        private String name;

        public ConfigHolder(String name, T config) {
            this.name = name;
            this.config = config;
        }

        public T getConfig() {
            return config;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Database configuration
     */
    public static class DatabaseConfig {
        private String url;
        private String username;
        private String password;

        public DatabaseConfig(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }

        public String getUrl() {
            return url;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    /**
     * Cache configuration
     */
    public static class CacheConfig {
        private int ttl;
        private int maxSize;

        public CacheConfig(int ttl, int maxSize) {
            this.ttl = ttl;
            this.maxSize = maxSize;
        }

        public int getTtl() {
            return ttl;
        }

        public int getMaxSize() {
            return maxSize;
        }
    }
}
