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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 登录日志接口。
 */
@Tag(name = "登录日志")
@RestController
@RequestMapping("/monitor/login-log")
@RequiredArgsConstructor
public class SysLoginLogController extends BaseController {
    private final ISysLoginLogService loginLogService;
    private final UserPasswordService passwordService;
    private final ExcelService excelService;

    @PreAuthorize("@se.hasPermission('monitor:login-log:list')")
    @GetMapping("/list")
    @Operation(summary = "分页查询登录日志")
    public ApiResult<PageResult<SysLoginLog>> list(@Validated @ParameterObject LoginLogPageReqDTO query) {
        return success(loginLogService.getLoginLogPage(query));
    }

    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@se.hasPermission('monitor:login-log:export')")
    @PostMapping("/export")
    @Operation(summary = "导出登录日志")
    public void export(HttpServletResponse response, @Validated @ParameterObject LoginLogPageReqDTO query) {
        query.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<SysLoginLog> page = loginLogService.getLoginLogPage(query);
        excelService.exportExcel(response, page.getRecords(), SysLoginLog.class, "登录日志");
    }

    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @PreAuthorize("@se.hasPermission('monitor:login-log:remove')")
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除登录日志")
    public ApiResult<Void> remove(@Parameter(description = "登录日志 ID 列表，多个用逗号分隔") @PathVariable Long[] ids) {
        return toApiResult(loginLogService.deleteLoginLogs(ids));
    }

    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("@se.hasPermission('monitor:login-log:remove')")
    @DeleteMapping("/clean")
    @Operation(summary = "清空登录日志")
    public ApiResult<Void> clean() {
        loginLogService.clearLoginLogs();
        return success();
    }

    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @PreAuthorize("@se.hasPermission('monitor:login-log:unlock')")
    @GetMapping("/unlock/{userName}")
    @Operation(summary = "解除账号登录锁定")
    public ApiResult<Void> unlock(@Parameter(description = "待解锁的用户账号") @PathVariable String userName) {
        passwordService.clearPasswordRetry(userName);
        return success();
    }
}
