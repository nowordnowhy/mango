package com.mango.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 说明：Redisson的配置属性
 */
@ConfigurationProperties(prefix = RedissonProperties.MYBATIS_PREFIX)
public class RedissonProperties {

    /**
     * 配置文件前缀
     */
    public static final String MYBATIS_PREFIX = "redisson";

    /**
     * 配置文件路径
     */
    private String configLocation;


    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

}

