package dev.geo.admin.quartz.service;

import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.exception.job.TaskException;
import dev.geo.admin.quartz.model.SysJob;
import dev.geo.admin.quartz.model.dto.JobPageReqDTO;
import org.quartz.SchedulerException;

import java.util.List;

public interface ISysJobService {
    PageResult<SysJob> selectJobPage(JobPageReqDTO query);

    List<SysJob> selectJobList(JobPageReqDTO query);

    SysJob selectJobById(Long id);

    int pauseJob(SysJob job) throws SchedulerException;

    int resumeJob(SysJob job) throws SchedulerException;

    int deleteJob(SysJob job) throws SchedulerException;

    void deleteJobByIds(Long[] ids) throws SchedulerException;

    int changeStatus(SysJob job) throws SchedulerException;

    boolean run(SysJob job) throws SchedulerException;

    int insertJob(SysJob job) throws SchedulerException, TaskException;

    int updateJob(SysJob job) throws SchedulerException, TaskException;

    boolean checkCronExpressionIsValid(String cronExpression);
}
