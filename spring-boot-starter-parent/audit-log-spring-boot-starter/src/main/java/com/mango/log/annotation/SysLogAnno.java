package com.mango.log.annotation;

import java.lang.annotation.*;

/**
 * 审计日志注解
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLogAnno {
    /**
     * 操作信息
     *
     * @return
     */
    String operation();

}
