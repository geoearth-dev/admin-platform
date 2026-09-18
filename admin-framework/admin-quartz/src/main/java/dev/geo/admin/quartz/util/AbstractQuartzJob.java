package dev.geo.admin.quartz.util;

import dev.geo.admin.common.constant.ScheduleConstants;
import dev.geo.admin.common.utils.spring.SpringUtils;
import dev.geo.admin.quartz.model.SysJob;
import dev.geo.admin.quartz.model.SysJobLog;
import dev.geo.admin.quartz.service.ISysJobLogService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 抽象quartz调用
 */
public abstract class AbstractQuartzJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(AbstractQuartzJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        SysJob job = (SysJob) context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES);
        if (job == null) {
            log.error("任务数据缺失：{}", context.getJobDetail().getKey());
            return;
        }
        Date startTime = new Date();
        Exception failure = null;
        try {
            doExecute(context, job);
        } catch (Exception e) {
            failure = e;
            log.error("任务执行失败：{}", job.getJobName(), e);
        } finally {
            SysJobLog jobLog = new SysJobLog();
            jobLog.setJobName(job.getJobName());
            jobLog.setJobGroup(job.getJobGroup());
            jobLog.setInvokeTarget(job.getInvokeTarget());
            jobLog.setStartTime(startTime);
            jobLog.setEndTime(new Date());
            long duration = jobLog.getEndTime().getTime() - startTime.getTime();
            jobLog.setJobMessage(job.getJobName() + " 总共耗时：" + duration + "毫秒");
            jobLog.setStatus(failure == null ? ScheduleConstants.EXECUTION_SUCCESS : ScheduleConstants.EXECUTION_FAIL);
            if (failure != null) {
                jobLog.setExceptionInfo(StringUtils.substring(ExceptionUtils.getStackTrace(failure), 0, 2000));
            }
            try {
                SpringUtils.getBean(ISysJobLogService.class).addJobLog(jobLog);
            } catch (Exception e) {
                log.error("任务日志写入失败，任务id={}", job.getId(), e);
            }
        }
    }

    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception;
}
