package com.mango.log.service.impl;

import com.mango.log.model.SysLog;
import com.mango.log.properties.LogDbProperties;
import com.mango.log.service.IAuditService;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * 审计日志实现类-数据库
 */
@Slf4j
@ConditionalOnProperty(name = "mango.audit-com.mango.log.com.mango.log-type", havingValue = "db")
@ConditionalOnClass(JdbcTemplate.class)
public class DbAuditServiceImpl implements IAuditService {

    private static final String INSERT_SQL = " insert into sys_log " +
            "( user_name, application_name, trace_id, user_id, operation, request_url, request_method, cost_time, ip, class_method, create_time, customer_id, is_exception, exception, params)" +
            " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private final JdbcTemplate jdbcTemplate;

    public DbAuditServiceImpl(@Autowired(required = false) LogDbProperties logDbProperties, DataSource dataSource) {
        //优先使用配置的日志数据源，否则使用默认的数据源
        if (logDbProperties != null && StringUtils.isNotEmpty(logDbProperties.getJdbcUrl())) {
            dataSource = new HikariDataSource(logDbProperties);
        }
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @PostConstruct
    public void init() {
        String sql = "CREATE TABLE IF NOT EXISTS sys_log (\n" +
                "  id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
                "  user_name varchar(50) DEFAULT NULL COMMENT '用户名',\n" +
                "  application_name varchar(255) DEFAULT NULL COMMENT '应用名',\n" +
                "  trace_id varchar(255) DEFAULT NULL COMMENT 'traceId',\n" +
                "  user_id varchar(50) DEFAULT NULL COMMENT '用户id',\n" +
                "  operation varchar(1024) DEFAULT NULL COMMENT '用户操作(注解说明)',\n" +
                "  request_url varchar(255) DEFAULT NULL COMMENT '请求地址',\n" +
                "  request_method varchar(256) DEFAULT NULL COMMENT '请求方式',\n" +
                "  params text COMMENT '请求参数',\n" +
                "  cost_time bigint(20) DEFAULT NULL COMMENT '请求耗时',\n" +
                "  ip varchar(256) DEFAULT NULL COMMENT 'IP',\n" +
                "  class_method varchar(255) DEFAULT NULL COMMENT '类方法',\n" +
                "  create_time datetime DEFAULT NULL COMMENT '创建时间',\n" +
                "  customer_id int(11) DEFAULT NULL COMMENT '租户id',\n" +
                "  is_exception int(10) unsigned DEFAULT '0' COMMENT '是否有异常 0 ：否 ，1： 是',\n" +
                "  exception varchar(1024) DEFAULT NULL COMMENT '异常信息',\n" +
                "  PRIMARY KEY (id),\n" +
                "  KEY idx_user_id (user_id)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='系统日志';";

        this.jdbcTemplate.execute(sql);
    }

    @Async
    @Override
    public void save(SysLog sysLog) {
        this.jdbcTemplate.update(INSERT_SQL
                , sysLog.getUserName(), sysLog.getApplicationName(), sysLog.getTraceId()
                , sysLog.getUserId(), sysLog.getOperation(), sysLog.getRequestUrl()
                , sysLog.getRequestMethod(), sysLog.getCostTime(), sysLog.getIp()
                , sysLog.getClassMethod(), sysLog.getCreateTime(), sysLog.getCustomerId()
                , sysLog.getIsException(), sysLog.getException(), sysLog.getParams());
    }
}
