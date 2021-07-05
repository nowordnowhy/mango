package com.mango.log.service.impl;

import com.mango.log.model.SysLog;
import com.mango.log.service.IAuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * 审计日志实现类-打印日志
 */
@Slf4j
@ConditionalOnProperty(name = "mango.audit-com.mango.log.com.mango.log-type", havingValue = "logger", matchIfMissing = true)
public class LoggerAuditServiceImpl implements IAuditService {
    private static final String MSG_PATTERN = "{}|{}|{}|{}|{}|{}|{}";

    /**
     * 格式为：{时间}|{应用名}|{类方法名}|{租户id}|{用户id}|{操作信息}|{参数}
     * 例子：2020-02-04 09:13:34.650|com.central.user.controller.SysUserController.saveOrUpdate|1|admin|11111|新增用户:admin|{参数}
     */
    @Override
    public void save(SysLog sysLog) {
        log.debug(MSG_PATTERN,
                sysLog.getCreateTime(), sysLog.getClassMethod(),
                sysLog.getUserId(), sysLog.getApplicationName(),
                sysLog.getCustomerId(), sysLog.getOperation(), sysLog.getParams());
    }
}
