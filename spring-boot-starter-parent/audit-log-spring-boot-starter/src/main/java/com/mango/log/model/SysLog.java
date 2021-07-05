package com.mango.log.model;

import lombok.Data;

import java.util.Date;

/**
 * 系统日志
 **/
@Data
public class SysLog {
    private static final long serialVersionUID = 5577816609973594437L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 应用名
     */
    private String applicationName;

    /**
     * traceId
     */
    private String traceId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户操作(注解说明)
     */
    private String operation;

    /**
     * 请求地址
     */
    private String requestUrl;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 请求耗时
     */
    private Long costTime;

    /**
     * IP
     */
    private String ip;

    /**
     * 类方法
     */
    private String classMethod;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 租户id
     */
    private String customerId;

    /**
     * 是否有异常 0 ：否 ，1： 是
     */
    private Integer isException;

    /**
     * 异常信息
     */
    private String exception;

    /**
     * 请求参数
     */
    private String params;

}
