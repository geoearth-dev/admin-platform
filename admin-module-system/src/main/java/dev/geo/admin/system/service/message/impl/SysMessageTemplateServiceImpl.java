package dev.geo.admin.system.service.message.impl;

import cn.hutool.core.bean.BeanUtil;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.system.mapper.message.SysMessageTemplateMapper;
import dev.geo.admin.system.model.message.dto.MessageTemplatePageReqDTO;
import dev.geo.admin.system.model.message.dto.MessageTemplateSaveDTO;
import dev.geo.admin.system.model.message.entity.SysMessageTemplate;
import dev.geo.admin.system.service.message.ISysMessageTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 消息模板服务实现。
 */
@Service
@RequiredArgsConstructor
public class SysMessageTemplateServiceImpl implements ISysMessageTemplateService {
    private final SysMessageTemplateMapper templateMapper;

    @Override
    public PageResult<SysMessageTemplate> selectTemplatePage(MessageTemplatePageReqDTO query) {
        return templateMapper.selectTemplatePage(query);
    }

    @Override
    public List<SysMessageTemplate> selectTemplateList(MessageTemplatePageReqDTO query) {
        return templateMapper.selectTemplateList(query);
    }

    @Override
    public SysMessageTemplate selectTemplate(Long id) {
        SysMessageTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new ServiceException("消息模板不存在");
        }
        return template;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createTemplate(MessageTemplateSaveDTO request) {
        checkTemplateCode(request.getTemplateCode(), null);
        SysMessageTemplate template = BeanUtil.toBean(request, SysMessageTemplate.class);
        template.setDelFlag(0);
        return templateMapper.insert(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTemplate(MessageTemplateSaveDTO request) {
        if (request.getId() == null) {
            throw new ServiceException("消息模板ID不能为空");
        }
        checkTemplateCode(request.getTemplateCode(), request.getId());
        return templateMapper.updateById(BeanUtil.toBean(request, SysMessageTemplate.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTemplates(Collection<Long> ids) {
        return templateMapper.deleteByIds(ids);
    }

    private void checkTemplateCode(String templateCode, Long id) {
        SysMessageTemplate existing = templateMapper.selectByTemplateCode(templateCode);
        if (existing != null && !existing.getId().equals(id)) {
            throw new ServiceException("模板编码已存在");
        }
    }
}
