package dev.geo.admin.system.mapper.message;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.mybatis.mapper.BaseMapperX;
import dev.geo.admin.system.model.message.dto.MessageTemplatePageReqDTO;
import dev.geo.admin.system.model.message.entity.SysMessageTemplate;

import java.util.List;

/**
 * 消息模板数据访问接口。
 */
public interface SysMessageTemplateMapper extends BaseMapperX<SysMessageTemplate> {

    default PageResult<SysMessageTemplate> selectTemplatePage(MessageTemplatePageReqDTO query) {
        return selectPage(query, buildQuery(query));
    }

    default List<SysMessageTemplate> selectTemplateList(MessageTemplatePageReqDTO query) {
        return selectList(buildQuery(query));
    }

    default SysMessageTemplate selectByTemplateCode(String templateCode) {
        return selectOne(SysMessageTemplate::getTemplateCode, templateCode);
    }

    private LambdaQueryWrapper<SysMessageTemplate> buildQuery(MessageTemplatePageReqDTO query) {
        return new LambdaQueryWrapper<SysMessageTemplate>()
                .like(StrUtil.isNotBlank(query.getTemplateCode()),
                        SysMessageTemplate::getTemplateCode, query.getTemplateCode())
                .like(StrUtil.isNotBlank(query.getTemplateName()),
                        SysMessageTemplate::getTemplateName, query.getTemplateName())
                .eq(query.getCategory() != null, SysMessageTemplate::getCategory, query.getCategory())
                .eq(StrUtil.isNotBlank(query.getStatus()), SysMessageTemplate::getStatus, query.getStatus())
                .orderByDesc(SysMessageTemplate::getCreateTime)
                .orderByDesc(SysMessageTemplate::getId);
    }
}
