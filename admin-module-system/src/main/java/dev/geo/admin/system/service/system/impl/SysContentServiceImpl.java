package dev.geo.admin.system.service.system.impl;

import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.system.mapper.system.SysContentMapper;
import dev.geo.admin.system.model.system.converter.SysContentConverter;
import dev.geo.admin.system.model.system.dto.SysContentPageReqDTO;
import dev.geo.admin.system.model.system.dto.SysContentSaveReqDTO;
import dev.geo.admin.system.model.system.entity.SysContent;
import dev.geo.admin.system.service.system.ISysContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统配置Service业务层处理
 */
@Service
@RequiredArgsConstructor
public class SysContentServiceImpl implements ISysContentService {
    private final SysContentMapper contentMapper;

    @Override
    public PageResult<SysContent> getSystemContentPage(SysContentPageReqDTO pageReq) {
        return contentMapper.selectPage(pageReq);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSystemContent(SysContentSaveReqDTO request) {
        SysContent content = getSystemContentById(request.getId());
        return contentMapper.updateById(content);
    }

    @Override
    public SysContent getSystemContentById(Long id) {
        SysContent content = contentMapper.selectById(id);
        if (content == null) {
            throw new ServiceException("系统展示配置不存在");
        }
        return content;
    }

}
