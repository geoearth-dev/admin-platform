package dev.geo.admin.system.service.monitor;

import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.system.model.monitor.dto.OperationLogPageReqDTO;
import dev.geo.admin.system.model.monitor.entity.SysOperationLog;

/**
 * 操作日志服务。
 */
public interface ISysOperationLogService {

    PageResult<SysOperationLog> getOperationLogPage(OperationLogPageReqDTO query);

    void insertOperationLog(SysOperationLog operationLog);

    int deleteOperationLogs(Long[] ids);

    SysOperationLog getOperationLogById(Long id);

    void clearOperationLogs();
}
