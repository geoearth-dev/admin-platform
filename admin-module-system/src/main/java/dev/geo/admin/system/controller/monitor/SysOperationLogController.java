package dev.geo.admin.system.controller.monitor;

import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.excel.core.ExcelService;
import dev.geo.admin.mybatis.model.page.PageParam;
import dev.geo.admin.system.model.monitor.dto.OperationLogPageReqDTO;
import dev.geo.admin.system.model.monitor.entity.SysOperationLog;
import dev.geo.admin.system.service.monitor.ISysOperationLogService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志接口。
 */
@RestController
@RequestMapping("/monitor/operation-log")
@RequiredArgsConstructor
public class SysOperationLogController extends BaseController {
    private final ISysOperationLogService operationLogService;
    private final ExcelService excelService;

    @PreAuthorize("@se.hasPermission('monitor:operation-log:list')")
    @GetMapping("/list")
    public ApiResult<PageResult<SysOperationLog>> list(@Validated OperationLogPageReqDTO query) {
        return success(operationLogService.getOperationLogPage(query));
    }

    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@se.hasPermission('monitor:operation-log:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, @Validated OperationLogPageReqDTO query) {
        query.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<SysOperationLog> page = operationLogService.getOperationLogPage(query);
        excelService.exportExcel(response, page.getRecords(), SysOperationLog.class, "操作日志");
    }

    @PreAuthorize("@se.hasPermission('monitor:operation-log:query')")
    @GetMapping("/{id}")
    public ApiResult<SysOperationLog> getInfo(@PathVariable Long id) {
        return success(operationLogService.getOperationLogById(id));
    }

    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @PreAuthorize("@se.hasPermission('monitor:operation-log:remove')")
    @DeleteMapping("/{ids}")
    public ApiResult<Void> remove(@PathVariable Long[] ids) {
        return toApiResult(operationLogService.deleteOperationLogs(ids));
    }

    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("@se.hasPermission('monitor:operation-log:remove')")
    @DeleteMapping("/clean")
    public ApiResult<Void> clean() {
        operationLogService.clearOperationLogs();
        return success();
    }
}
