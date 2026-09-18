package dev.geo.admin.quartz.service;

import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.quartz.model.SysJobLog;
import dev.geo.admin.quartz.model.dto.JobLogPageReqDTO;

import java.util.List;

public interface ISysJobLogService {
    PageResult<SysJobLog> selectJobLogPage(JobLogPageReqDTO query);

    List<SysJobLog> selectJobLogList(JobLogPageReqDTO query);

    SysJobLog selectJobLogById(Long id);

    void addJobLog(SysJobLog jobLog);

    int deleteJobLogByIds(Long[] ids);

    int deleteJobLogById(Long id);

    void cleanJobLog();
}
