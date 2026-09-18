package dev.geo.admin.quartz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.geo.admin.common.constant.ScheduleConstants;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.common.exception.job.TaskException;
import dev.geo.admin.mybatis.model.converter.PageResultConverter;
import dev.geo.admin.quartz.mapper.SysJobMapper;
import dev.geo.admin.quartz.model.SysJob;
import dev.geo.admin.quartz.model.dto.JobPageReqDTO;
import dev.geo.admin.quartz.service.ISysJobService;
import dev.geo.admin.quartz.util.CronUtils;
import dev.geo.admin.quartz.util.ScheduleUtils;
import lombok.RequiredArgsConstructor;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 定时任务调度信息 服务层
 */
@Service
@RequiredArgsConstructor
public class SysJobServiceImpl implements ISysJobService {
    private final Scheduler scheduler;
    private final SysJobMapper jobMapper;

    /**
     * 获取quartz调度器的计划任务分页列表
     *
     * @param query 调度信息
     * @return PageResult<SysJob>
     */
    @Override
    public PageResult<SysJob> selectJobPage(JobPageReqDTO query) {
        IPage<SysJob> page = new Page<>(query.getPageNum(), query.getPageSize());
        return PageResultConverter.of(jobMapper.selectJobPage(page, query));
    }

    /**
     * 获取quartz调度器的计划任务列表
     *
     * @param query 调度信息
     * @return List<SysJob>
     */
    @Override
    public List<SysJob> selectJobList(JobPageReqDTO query) {
        return jobMapper.selectJobList(query);
    }

    /**
     * 通过调度任务ID查询调度信息
     *
     * @param id 调度任务ID
     * @return 调度任务对象信息
     */
    @Override
    public SysJob selectJobById(Long id) {
        return jobMapper.selectJobById(id);
    }

    /**
     * 暂停任务
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pauseJob(SysJob job) throws SchedulerException {
        Long jobId = job.getId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        int rows = jobMapper.updateJob(job);
        if (rows > 0) {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 恢复任务
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resumeJob(SysJob job) throws SchedulerException {
        Long jobId = job.getId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        int rows = jobMapper.updateJob(job);
        if (rows > 0) {
            scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 任务调度状态修改
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(SysJob job) throws SchedulerException {
        int rows = 0;
        String status = job.getStatus();
        if (ScheduleConstants.Status.NORMAL.getValue().equals(status)) {
            rows = resumeJob(job);
        } else if (ScheduleConstants.Status.PAUSE.getValue().equals(status)) {
            rows = pauseJob(job);
        }
        return rows;
    }

    /**
     * 立即运行任务
     *
     * @param job 调度信息
     */
    @Override
    public boolean run(SysJob job) throws SchedulerException {
        Long jobId = job.getId();
        String jobGroup = job.getJobGroup();
        JobKey key = ScheduleUtils.getJobKey(jobId, jobGroup);
        if (!scheduler.checkExists(key)) {
            return false;
        }
        SysJob stored = jobMapper.selectJobById(jobId);
        // 参数
        JobDataMap data = new JobDataMap();
        data.put(ScheduleConstants.TASK_PROPERTIES, stored);
        scheduler.triggerJob(key, data);
        return true;
    }

    /**
     * 新增任务
     *
     * @param job 调度信息 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertJob(SysJob job) throws SchedulerException, TaskException {
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        int rows = jobMapper.insertJob(job);
        if (rows > 0) {
            ScheduleUtils.createScheduleJob(scheduler, job);
        }
        return rows;
    }

    /**
     * 更新任务的时间表达式
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateJob(SysJob job) throws SchedulerException, TaskException {
        Long jobId = job.getId();
        int rows = jobMapper.updateJob(job);
        if (rows > 0) {
            SysJob properties = selectJobById(jobId);
            String jobGroup = properties.getJobGroup();
            // 判断是否存在
            JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
            if (scheduler.checkExists(jobKey)) {
                // 防止创建时存在数据问题 先移除，然后在执行创建操作
                scheduler.deleteJob(jobKey);
            }
            ScheduleUtils.createScheduleJob(scheduler, job);
        }
        return rows;
    }

    /**
     * 删除任务后，所对应的trigger也将被删除
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteJob(SysJob job) throws SchedulerException {
        SysJob stored = selectJobById(job.getId());
        int rows = jobMapper.deleteJobById(stored.getId());
        if (rows > 0) {
            scheduler.deleteJob(ScheduleUtils.getJobKey(stored.getId(), stored.getJobGroup()));
        }
        return rows;
    }

    /**
     * 批量删除调度信息
     *
     * @param ids 需要删除的任务ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobByIds(Long[] ids) throws SchedulerException {
        if (ids == null || ids.length == 0) {
            throw new ServiceException("请选择要删除的任务");
        }
        List<SysJob> jobs = Arrays.stream(ids).distinct().map(this::selectJobById).toList();
        for (SysJob job : jobs) {
            deleteJob(job);
        }
    }

    /**
     * 校验cron表达式是否有效
     *
     * @param cronExpression 表达式
     * @return 结果
     */
    @Override
    public boolean checkCronExpressionIsValid(String cronExpression) {
        return CronUtils.isValid(cronExpression);
    }

}
