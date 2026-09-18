package dev.geo.admin.quartz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.mybatis.model.converter.PageResultConverter;
import dev.geo.admin.quartz.mapper.SysJobLogMapper;
import dev.geo.admin.quartz.model.SysJobLog;
import dev.geo.admin.quartz.model.dto.JobLogPageReqDTO;
import dev.geo.admin.quartz.service.ISysJobLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysJobLogServiceImpl implements ISysJobLogService {
    private final SysJobLogMapper jobLogMapper;

    @Override
    public PageResult<SysJobLog> selectJobLogPage(JobLogPageReqDTO query) {
        IPage<SysJobLog> page = new Page<>(query.getPageNum(), query.getPageSize());
        return PageResultConverter.of(jobLogMapper.selectJobLogPage(page, query));
    }

    @Override
    public List<SysJobLog> selectJobLogList(JobLogPageReqDTO query) {
        return jobLogMapper.selectJobLogList(query);
    }

    @Override
    public SysJobLog selectJobLogById(Long id) {
        SysJobLog log = jobLogMapper.selectJobLogById(id);
        if (log == null) {
            throw new ServiceException("任务日志不存在：" + id);
        }
        return log;
    }

    @Override
    public void addJobLog(SysJobLog jobLog) {
        jobLogMapper.insertJobLog(jobLog);
    }

    @Override
    public int deleteJobLogByIds(Long[] ids) {
        return ids == null || ids.length == 0 ? 0 : jobLogMapper.deleteJobLogByIds(ids);
    }

    @Override
    public int deleteJobLogById(Long id) {
        return jobLogMapper.deleteJobLogById(id);
    }

    @Override
    public void cleanJobLog() {
        jobLogMapper.cleanJobLog();
    }
}
