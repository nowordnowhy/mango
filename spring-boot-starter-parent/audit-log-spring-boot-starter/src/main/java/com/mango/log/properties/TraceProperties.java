package com.mango.log.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 日志链路追踪配置
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "mango.trace")
@RefreshScope
public class TraceProperties {
    /**
     * 是否开启日志链路追踪
     */
    private Boolean enable = false;

    /**
     * 是否开启用户信息传递
     */
    private Boolean transferUser = true;
}
