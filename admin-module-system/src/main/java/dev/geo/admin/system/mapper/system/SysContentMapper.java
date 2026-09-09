package dev.geo.admin.system.mapper.system;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.mybatis.mapper.BaseMapperX;
import dev.geo.admin.system.model.system.dto.SysContentPageReqDTO;
import dev.geo.admin.system.model.system.entity.SysContent;

/**
 * 系统展示配置数据访问层。
 */
public interface SysContentMapper extends BaseMapperX<SysContent> {

    default PageResult<SysContent> selectPage(SysContentPageReqDTO query) {
        LambdaQueryWrapper<SysContent> wrapper = Wrappers.lambdaQuery(SysContent.class)
                .eq(query.getId() != null, SysContent::getId, query.getId())
                .like(StrUtil.isNotBlank(query.getSysName()), SysContent::getSysName, query.getSysName())
                .eq(query.getStatus() != null, SysContent::getStatus, query.getStatus())
                .orderByDesc(SysContent::getCreateTime)
                .orderByDesc(SysContent::getId);
        return selectPage(query, wrapper);
    }
}
