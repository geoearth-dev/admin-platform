package dev.geo.admin.quartz.controller;

import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.constant.Constants;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.common.exception.job.TaskException;
import dev.geo.admin.excel.core.ExcelService;
import dev.geo.admin.quartz.model.SysJob;
import dev.geo.admin.quartz.model.dto.JobPageReqDTO;
import dev.geo.admin.quartz.service.ISysJobService;
import dev.geo.admin.quartz.util.CronUtils;
import dev.geo.admin.quartz.util.ScheduleUtils;
import dev.geo.admin.security.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 定时任务管理。
 */
@RestController
@RequestMapping("/monitor/job")
@RequiredArgsConstructor
public class SysJobController extends BaseController {
    private final ISysJobService jobService;
    private final ExcelService excelService;
    /**
     * 查询定时任务列表
     */
    @PreAuthorize("@se.hasPermission('monitor:job:list')")
    @GetMapping("/list")
    public ApiResult<PageResult<SysJob>> list(@Validated JobPageReqDTO query) {
        return success(jobService.selectJobPage(query));
    }
    /**
     * 导出定时任务列表
     */
    @PreAuthorize("@se.hasPermission('monitor:job:export')")
    @Log(title = "定时任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, @Validated JobPageReqDTO query) {
        excelService.exportExcel(response, jobService.selectJobList(query), SysJob.class, "定时任务");
    }
    /**
     * 获取定时任务详细信息
     */
    @PreAuthorize("@se.hasPermission('monitor:job:query')")
    @GetMapping("/{id}")
    public ApiResult<SysJob> getInfo(@PathVariable Long id) {
        return success(jobService.selectJobById(id));
    }
    /**
     * 新增定时任务
     */
    @PreAuthorize("@se.hasPermission('monitor:job:add')")
    @Log(title = "定时任务", businessType = BusinessType.INSERT)
    @PostMapping
    public ApiResult<Void> add(@Validated @RequestBody SysJob job) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(job.getCronExpression())) {
            return error("新增任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        } else if (StrUtil.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI)) {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        } else if (StrUtil.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS})) {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        } else if (StrUtil.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.HTTP, Constants.HTTPS})) {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        } else if (StrUtil.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR)) {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        } else if (!ScheduleUtils.whiteList(job.getInvokeTarget())) {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
        }
        job.setCreateBy(SecurityUtils.getUsername());
        jobService.insertJob(job);
        return success();
    }
    /**
     * 修改定时任务
     */
    @PreAuthorize("@se.hasPermission('monitor:job:edit')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public ApiResult<Void> edit(@Validated @RequestBody SysJob job) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(job.getCronExpression()))
        {
            return error("修改任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }
        else if (StrUtil.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI))
        {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        }
        else if (StrUtil.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS }))
        {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        }
        else if (StrUtil.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.HTTP, Constants.HTTPS }))
        {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        }
        else if (StrUtil.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR))
        {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        }
//        else if (!ScheduleUtils.whiteList(job.getInvokeTarget()))
//        {
//            return error("修改任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
//        }
        job.setCreateBy(SecurityUtils.getUsername());
        jobService.updateJob(job);
        return success();
    }
    /**
     * 定时任务状态修改
     */
    @PreAuthorize("@se.hasPermission('monitor:job:changeStatus')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public ApiResult<Void> changeStatus(@RequestBody SysJob job) throws SchedulerException {
        SysJob newJob = jobService.selectJobById(job.getId());
        newJob.setStatus(job.getStatus());
        return toApiResult(jobService.changeStatus(newJob));
    }
    /**
     * 定时任务立即执行一次
     */
    @PreAuthorize("@se.hasPermission('monitor:job:changeStatus')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/run")
    public ApiResult<Void> run(@RequestBody SysJob job) throws SchedulerException {
        boolean result = jobService.run(job);
        return result ? success() : error("任务不存在或已过期！");
    }
    /**
     * 删除定时任务
     */
    @PreAuthorize("@se.hasPermission('monitor:job:remove')")
    @Log(title = "定时任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public ApiResult<Void> remove(@PathVariable Long[] ids) throws SchedulerException {
        jobService.deleteJobByIds(ids);
        return success();
    }
}
