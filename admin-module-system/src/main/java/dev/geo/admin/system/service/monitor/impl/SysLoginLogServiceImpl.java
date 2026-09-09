package dev.geo.admin.system.service.monitor.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.system.mapper.monitor.SysLoginLogMapper;
import dev.geo.admin.system.model.monitor.dto.LoginLogPageReqDTO;
import dev.geo.admin.system.model.monitor.entity.SysLoginLog;
import dev.geo.admin.system.service.monitor.ISysLoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 登录日志服务实现。
 */
@Service
@RequiredArgsConstructor
public class SysLoginLogServiceImpl implements ISysLoginLogService {
    private final SysLoginLogMapper loginLogMapper;

    @Override
    public PageResult<SysLoginLog> getLoginLogPage(LoginLogPageReqDTO query) {
        return loginLogMapper.selectPage(query);
    }

    @Override
    public void insertLoginLog(SysLoginLog loginLog) {
        loginLogMapper.insert(loginLog);
    }

    @Override
    public int deleteLoginLogs(Long[] ids) {
        return loginLogMapper.deleteByIds(Arrays.asList(ids));
    }

    @Override
    public void clearLoginLogs() {
        loginLogMapper.delete(new LambdaQueryWrapper<SysLoginLog>().isNotNull(SysLoginLog::getId));
    }
}
