package dev.geo.admin.quartz.util;

import dev.geo.admin.common.constant.Constants;
import dev.geo.admin.common.constant.ScheduleConstants;
import dev.geo.admin.common.exception.job.TaskException;
import dev.geo.admin.common.utils.spring.SpringUtils;
import dev.geo.admin.quartz.model.SysJob;
import org.quartz.*;
import org.springframework.aop.support.AopUtils;

import java.util.Arrays;

/**
 * 定时任务工具类
 */
public class ScheduleUtils {

    /**
     * 得到quartz任务类
     *
     * @param sysJob 执行计划
     * @return 具体执行任务类
     */
    private static Class<? extends Job> getQuartzJobClass(SysJob sysJob) {
        boolean isConcurrent = "1".equals(sysJob.getConcurrent());
         return isConcurrent ? QuartzJobExecution.class : QuartzDisallowConcurrentExecution.class;
    }

    /**
     * 构建任务触发对象
     */
    public static TriggerKey getTriggerKey(Long id, String jobGroup) {
        return TriggerKey.triggerKey(ScheduleConstants.TASK_CLASS_NAME + id, jobGroup);
    }

    /**
     * 构建任务键对象
     */
    public static JobKey getJobKey(Long id, String jobGroup) {
        return JobKey.jobKey(ScheduleConstants.TASK_CLASS_NAME + id, jobGroup);
    }

    public static void createScheduleJob(Scheduler scheduler, SysJob job) throws SchedulerException, TaskException {
        Class<? extends Job> jobClass = getQuartzJobClass(job);
        // 构建job信息
        Long jobId = job.getId();
        String jobGroup = job.getJobGroup();
        JobKey key = getJobKey(jobId, jobGroup);
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(key).build();

        // 表达式调度构建器
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
        cronScheduleBuilder = handleCronScheduleMisfirePolicy(job, cronScheduleBuilder);

        // 按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey(jobId, jobGroup))
                .withSchedule(cronScheduleBuilder).build();

        // 放入参数，运行时的方法可以获取
        jobDetail.getJobDataMap().put(ScheduleConstants.TASK_PROPERTIES, job);

        // 判断是否存在
        if (scheduler.checkExists(key)) {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(key);
        }
        // 判断任务是否过期
        if (CronUtils.getNextExecution(job.getCronExpression()) != null) {
            // 执行调度任务
            scheduler.scheduleJob(jobDetail, trigger);
        }
        // 暂停任务
        if (ScheduleConstants.Status.PAUSE.getValue().equals(job.getStatus())) {
            scheduler.pauseJob(key);
        }
    }

    /**
     * 设置定时任务策略
     */
    public static CronScheduleBuilder handleCronScheduleMisfirePolicy(SysJob job, CronScheduleBuilder builder) throws TaskException {
        return switch (job.getMisfirePolicy()) {
            case ScheduleConstants.MISFIRE_DEFAULT -> builder;
            case ScheduleConstants.MISFIRE_IGNORE_MISFIRES -> builder.withMisfireHandlingInstructionIgnoreMisfires();
            case ScheduleConstants.MISFIRE_FIRE_AND_PROCEED -> builder.withMisfireHandlingInstructionFireAndProceed();
            case ScheduleConstants.MISFIRE_DO_NOTHING -> builder.withMisfireHandlingInstructionDoNothing();
            default -> throw new TaskException("The task misfire policy '" + job.getMisfirePolicy()
                    + "' cannot be used in cron schedule tasks", TaskException.Code.CONFIG_ERROR);
        };
    }

    /**
     * 检查包名是否为白名单配置
     *
     * @param target 目标字符串
     * @return 结果
     */
    public static boolean whiteList(String target) {
        try {
            String name = JobInvokeUtil.getBeanName(target);
            Class<?> type = JobInvokeUtil.isValidClassName(name)
                    ? Class.forName(name, false, ScheduleUtils.class.getClassLoader())
                    : AopUtils.getTargetClass(SpringUtils.getBean(name, Object.class));
            String packageName = type.getPackageName();
            return Arrays.stream(Constants.JOB_WHITELIST_STR)
                    .anyMatch(allowed -> packageName.equals(allowed) || packageName.startsWith(allowed + "."));
        } catch (ClassNotFoundException | RuntimeException e) {
            return false;
        }
    }
}
