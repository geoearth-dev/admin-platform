package dev.geo.admin.system.mapper.system;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.mybatis.mapper.BaseMapperX;
import dev.geo.admin.system.model.system.dto.SysConfigPageReqDTO;
import dev.geo.admin.system.model.system.entity.SysConfig;

/**
 * 参数配置 数据层
 */
public interface SysConfigMapper extends BaseMapperX<SysConfig> {

    /**
     * 根据查询条件分页查询参数配置。
     */
    default PageResult<SysConfig> selectPage(SysConfigPageReqDTO query) {
        LambdaQueryWrapper<SysConfig> wrapper = Wrappers.lambdaQuery(SysConfig.class)
                .like(StrUtil.isNotBlank(query.getConfigName()), SysConfig::getConfigName, query.getConfigName())
                .like(StrUtil.isNotBlank(query.getConfigKey()), SysConfig::getConfigKey, query.getConfigKey())
                .eq(StrUtil.isNotBlank(query.getConfigType()), SysConfig::getConfigType, query.getConfigType());
        if (query.getBeginDate() != null) {
            wrapper.ge(SysConfig::getCreateTime, query.getBeginDate().atStartOfDay());
        }
        if (query.getEndDate() != null) {
            // 使用次日零点的开区间，完整覆盖结束日期且不对数据库列执行函数。
            wrapper.lt(SysConfig::getCreateTime, query.getEndDate().plusDays(1).atStartOfDay());
        }
        wrapper.orderByDesc(SysConfig::getCreateTime)
                .orderByDesc(SysConfig::getId);
        return selectPage(query, wrapper);
    }

    /**
     * 根据参数键名查询配置。
     */
    default SysConfig selectByConfigKey(String configKey) {
        LambdaQueryWrapper<SysConfig> wrapper = Wrappers.lambdaQuery(SysConfig.class)
                .eq(SysConfig::getConfigKey, configKey)
                .orderByDesc(SysConfig::getCreateTime)
                .orderByDesc(SysConfig::getId);
        return selectOne(wrapper);
    }
}
