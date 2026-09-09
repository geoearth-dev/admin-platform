package dev.geo.admin.system.service.monitor;

import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.system.model.monitor.dto.LoginLogPageReqDTO;
import dev.geo.admin.system.model.monitor.entity.SysLoginLog;

/**
 * 登录日志服务。
 */
public interface ISysLoginLogService {

    PageResult<SysLoginLog> getLoginLogPage(LoginLogPageReqDTO query);

    void insertLoginLog(SysLoginLog loginLog);

    int deleteLoginLogs(Long[] ids);

    void clearLoginLogs();
}
