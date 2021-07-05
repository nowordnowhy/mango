package com.mango.log.service;

import com.mango.log.model.SysLog;

/**
 * 审计日志接口
 */
public interface IAuditService {
    void save(SysLog sysLog);
}
