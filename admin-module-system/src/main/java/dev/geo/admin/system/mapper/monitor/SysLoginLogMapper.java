package dev.geo.admin.system.mapper.monitor;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.mybatis.mapper.BaseMapperX;
import dev.geo.admin.system.model.monitor.dto.LoginLogPageReqDTO;
import dev.geo.admin.system.model.monitor.entity.SysLoginLog;

/**
 * 登录日志数据访问接口。
 */
public interface SysLoginLogMapper extends BaseMapperX<SysLoginLog> {

    /**
     * 分页查询登录日志，默认按登录时间倒序排列。
     */
    default PageResult<SysLoginLog> selectPage(LoginLogPageReqDTO query) {
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<SysLoginLog>()
                .like(StrUtil.isNotBlank(query.getUserName()), SysLoginLog::getUserName, query.getUserName())
                .eq(StrUtil.isNotBlank(query.getStatus()), SysLoginLog::getStatus, query.getStatus())
                .like(StrUtil.isNotBlank(query.getIpAddress()), SysLoginLog::getIpAddress, query.getIpAddress())
                .ge(query.getBeginDate() != null, SysLoginLog::getLoginTime,
                        query.getBeginDate() == null ? null : query.getBeginDate().atStartOfDay())
                .lt(query.getEndDate() != null, SysLoginLog::getLoginTime,
                        query.getEndDate() == null ? null : query.getEndDate().plusDays(1).atStartOfDay())
                .orderByDesc(SysLoginLog::getLoginTime)
                .orderByDesc(SysLoginLog::getId);
        return selectPage(query, wrapper);
    }
}
