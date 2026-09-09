package dev.geo.admin.system.controller.monitor;

import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.excel.core.ExcelService;
import dev.geo.admin.mybatis.model.page.PageParam;
import dev.geo.admin.system.model.monitor.dto.LoginLogPageReqDTO;
import dev.geo.admin.system.model.monitor.entity.SysLoginLog;
import dev.geo.admin.system.service.auth.UserPasswordService;
import dev.geo.admin.system.service.monitor.ISysLoginLogService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 登录日志接口。
 */
@RestController
@RequestMapping("/monitor/login-log")
@RequiredArgsConstructor
public class SysLoginLogController extends BaseController {
    private final ISysLoginLogService loginLogService;
    private final UserPasswordService passwordService;
    private final ExcelService excelService;

    @PreAuthorize("@se.hasPermission('monitor:login-log:list')")
    @GetMapping("/list")
    public ApiResult<PageResult<SysLoginLog>> list(@Validated LoginLogPageReqDTO query) {
        return success(loginLogService.getLoginLogPage(query));
    }

    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@se.hasPermission('monitor:login-log:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, @Validated LoginLogPageReqDTO query) {
        query.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<SysLoginLog> page = loginLogService.getLoginLogPage(query);
        excelService.exportExcel(response, page.getRecords(), SysLoginLog.class, "登录日志");
    }

    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @PreAuthorize("@se.hasPermission('monitor:login-log:remove')")
    @DeleteMapping("/{ids}")
    public ApiResult<Void> remove(@PathVariable Long[] ids) {
        return toApiResult(loginLogService.deleteLoginLogs(ids));
    }

    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("@se.hasPermission('monitor:login-log:remove')")
    @DeleteMapping("/clean")
    public ApiResult<Void> clean() {
        loginLogService.clearLoginLogs();
        return success();
    }

    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @PreAuthorize("@se.hasPermission('monitor:login-log:unlock')")
    @GetMapping("/unlock/{userName}")
    public ApiResult<Void> unlock(@PathVariable String userName) {
        passwordService.clearPasswordRetry(userName);
        return success();
    }
}
