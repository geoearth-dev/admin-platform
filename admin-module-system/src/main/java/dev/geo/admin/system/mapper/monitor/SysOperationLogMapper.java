package dev.geo.admin.system.mapper.monitor;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.mybatis.mapper.BaseMapperX;
import dev.geo.admin.system.model.monitor.dto.OperationLogPageReqDTO;
import dev.geo.admin.system.model.monitor.entity.SysOperationLog;

import java.util.Arrays;

/**
 * 操作日志数据访问接口。
 */
public interface SysOperationLogMapper extends BaseMapperX<SysOperationLog> {

    /**
     * 分页查询操作日志，默认按操作时间倒序排列。
     */
    default PageResult<SysOperationLog> selectPage(OperationLogPageReqDTO query) {
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<SysOperationLog>()
                .like(StrUtil.isNotBlank(query.getTitle()), SysOperationLog::getTitle, query.getTitle())
                .like(StrUtil.isNotBlank(query.getUserName()), SysOperationLog::getUserName, query.getUserName())
                .eq(query.getBusinessType() != null, SysOperationLog::getBusinessType, query.getBusinessType())
                .in(ArrayUtil.isNotEmpty(query.getBusinessTypes()), SysOperationLog::getBusinessType,
                        ArrayUtil.isEmpty(query.getBusinessTypes()) ? null : Arrays.asList(query.getBusinessTypes()))
                .eq(query.getStatus() != null, SysOperationLog::getStatus, query.getStatus())
                .eq(StrUtil.isNotBlank(query.getHttpMethod()), SysOperationLog::getHttpMethod, query.getHttpMethod())
                .ge(query.getBeginDate() != null, SysOperationLog::getOperationTime,
                        query.getBeginDate() == null ? null : query.getBeginDate().atStartOfDay())
                .lt(query.getEndDate() != null, SysOperationLog::getOperationTime,
                        query.getEndDate() == null ? null : query.getEndDate().plusDays(1).atStartOfDay())
                .orderByDesc(SysOperationLog::getOperationTime)
                .orderByDesc(SysOperationLog::getId);
        return selectPage(query, wrapper);
    }
}
