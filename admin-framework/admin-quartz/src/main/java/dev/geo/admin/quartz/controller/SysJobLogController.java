package dev.geo.admin.quartz.controller;

import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.excel.core.ExcelService;
import dev.geo.admin.quartz.model.SysJobLog;
import dev.geo.admin.quartz.model.dto.JobLogPageReqDTO;
import dev.geo.admin.quartz.service.ISysJobLogService;
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
 * 调度日志操作处理
 *
 */
@Tag(name = "任务日志")
@RestController
@RequestMapping("/monitor/jobLog")
@RequiredArgsConstructor
public class SysJobLogController extends BaseController {
    private final ISysJobLogService jobLogService;
    private final ExcelService excelService;
    /**
     * 查询定时任务调度日志列表
     */
    @PreAuthorize("@se.hasPermission('monitor:job:list')")
    @GetMapping("/list")
    @Operation(summary = "分页查询任务日志")
    public ApiResult<PageResult<SysJobLog>> list(@Validated @ParameterObject JobLogPageReqDTO query) {
        return success(jobLogService.selectJobLogPage(query));
    }
    /**
     * 导出定时任务调度日志列表
     */
    @PreAuthorize("@se.hasPermission('monitor:job:export')")
    @Log(title = "任务调度日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @Operation(summary = "导出任务日志")
    public void export(HttpServletResponse response, @Validated @ParameterObject JobLogPageReqDTO query) {
        excelService.exportExcel(response, jobLogService.selectJobLogList(query), SysJobLog.class, "调度日志");
    }
    /**
     * 根据调度编号获取详细信息
     */
    @PreAuthorize("@se.hasPermission('monitor:job:query')")
    @GetMapping("/{id}")
    @Operation(summary = "查询任务日志详情")
    public ApiResult<SysJobLog> getInfo(@Parameter(description = "任务日志 ID") @PathVariable Long id) {
        return success(jobLogService.selectJobLogById(id));
    }
    /**
     * 删除定时任务调度日志
     */
    @PreAuthorize("@se.hasPermission('monitor:job:remove')")
    @Log(title = "定时任务调度日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除任务日志")
    public ApiResult<Void> remove(@Parameter(description = "任务日志 ID 列表，多个用逗号分隔") @PathVariable Long[] ids) {
        return toApiResult(jobLogService.deleteJobLogByIds(ids));
    }
    /**
     * 清空定时任务调度日志
     */
    @PreAuthorize("@se.hasPermission('monitor:job:remove')")
    @Log(title = "调度日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    @Operation(summary = "清空任务日志")
    public ApiResult<Void> clean() {
        jobLogService.cleanJobLog();
        return success();
    }
}
