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
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 调度日志操作处理
 *
 */
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
    public ApiResult<PageResult<SysJobLog>> list(@Validated JobLogPageReqDTO query) {
        return success(jobLogService.selectJobLogPage(query));
    }
    /**
     * 导出定时任务调度日志列表
     */
    @PreAuthorize("@se.hasPermission('monitor:job:export')")
    @Log(title = "任务调度日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, @Validated JobLogPageReqDTO query) {
        excelService.exportExcel(response, jobLogService.selectJobLogList(query), SysJobLog.class, "调度日志");
    }
    /**
     * 根据调度编号获取详细信息
     */
    @PreAuthorize("@se.hasPermission('monitor:job:query')")
    @GetMapping("/{id}")
    public ApiResult<SysJobLog> getInfo(@PathVariable Long id) {
        return success(jobLogService.selectJobLogById(id));
    }
    /**
     * 删除定时任务调度日志
     */
    @PreAuthorize("@se.hasPermission('monitor:job:remove')")
    @Log(title = "定时任务调度日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public ApiResult<Void> remove(@PathVariable Long[] ids) {
        return toApiResult(jobLogService.deleteJobLogByIds(ids));
    }
    /**
     * 清空定时任务调度日志
     */
    @PreAuthorize("@se.hasPermission('monitor:job:remove')")
    @Log(title = "调度日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public ApiResult<Void> clean() {
        jobLogService.cleanJobLog();
        return success();
    }
}
