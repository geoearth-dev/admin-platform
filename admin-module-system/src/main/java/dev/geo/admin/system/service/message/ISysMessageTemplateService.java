package dev.geo.admin.system.service.message;

import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.system.model.message.dto.MessageTemplatePageReqDTO;
import dev.geo.admin.system.model.message.dto.MessageTemplateSaveDTO;
import dev.geo.admin.system.model.message.entity.SysMessageTemplate;

import java.util.Collection;
import java.util.List;

/**
 * 消息模板服务。
 */
public interface ISysMessageTemplateService {
    PageResult<SysMessageTemplate> selectTemplatePage(MessageTemplatePageReqDTO query);

    List<SysMessageTemplate> selectTemplateList(MessageTemplatePageReqDTO query);

    SysMessageTemplate selectTemplate(Long id);

    int createTemplate(MessageTemplateSaveDTO request);

    int updateTemplate(MessageTemplateSaveDTO request);

    int deleteTemplates(Collection<Long> ids);
}
