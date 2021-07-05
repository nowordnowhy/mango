package com.mango.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

/**
 * 说明：Redisson自动配置
 *
 */
@Configuration
@ConditionalOnClass({RedissonClient.class}) //classpath 下有 RedissonClient 类才会启用
@ConditionalOnProperty(prefix = "redisson", name = "config-location") //配置文件有 redisson.config-location 属性， 才会启用 com.mango.config.RedissonAutoConfiguration
@EnableConfigurationProperties({com.mango.config.RedissonProperties.class})
public class RedissonAutoConfiguration {

    private final ResourceLoader resourceLoader;

    private final com.mango.config.RedissonProperties properties;

    // 构造方法注入
    public RedissonAutoConfiguration(com.mango.config.RedissonProperties properties, ResourceLoader resourceLoader) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedissonClient redissonClient() throws IOException {
        Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
        Config config = null;
        if (this.properties.getConfigLocation().endsWith(".yaml")) {
            config = Config.fromYAML(resource.getFile());
        } else if (this.properties.getConfigLocation().endsWith(".json")) {
            config = Config.fromJSON(resource.getFile());
        }
        return config != null ? Redisson.create(config) : Redisson.create();
    }

}




