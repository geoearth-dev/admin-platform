package dev.geo.admin.system.service.monitor.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.system.mapper.monitor.SysOperationLogMapper;
import dev.geo.admin.system.model.monitor.dto.OperationLogPageReqDTO;
import dev.geo.admin.system.model.monitor.entity.SysOperationLog;
import dev.geo.admin.system.service.monitor.ISysOperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 操作日志服务实现。
 */
@Service
@RequiredArgsConstructor
public class SysOperationLogServiceImpl implements ISysOperationLogService {
    private final SysOperationLogMapper operationLogMapper;

    @Override
    public PageResult<SysOperationLog> getOperationLogPage(OperationLogPageReqDTO query) {
        return operationLogMapper.selectPage(query);
    }

    @Override
    public void insertOperationLog(SysOperationLog operationLog) {
        operationLogMapper.insert(operationLog);
    }

    @Override
    public int deleteOperationLogs(Long[] ids) {
        return operationLogMapper.deleteByIds(Arrays.asList(ids));
    }

    @Override
    public SysOperationLog getOperationLogById(Long id) {
        return operationLogMapper.selectById(id);
    }

    @Override
    public void clearOperationLogs() {
        operationLogMapper.delete(new LambdaQueryWrapper<SysOperationLog>().isNotNull(SysOperationLog::getId));
    }
}
