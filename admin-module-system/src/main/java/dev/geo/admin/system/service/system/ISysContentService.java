package dev.geo.admin.system.service.system;

import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.system.model.system.dto.SysContentPageReqDTO;
import dev.geo.admin.system.model.system.dto.SysContentSaveReqDTO;
import dev.geo.admin.system.model.system.entity.SysContent;

/**
 * 系统配置Service接口
 */
public interface ISysContentService {

    /**
     * 获得系统配置分页列表
     *
     * @param pageReq 分页请求
     * @return 系统配置分页列表
     */
    PageResult<SysContent> getSystemContentPage(SysContentPageReqDTO pageReq);

    /**
     * 更新系统配置
     *
     * @param updateReqVO 系统配置信息
     */
    int updateSystemContent(SysContentSaveReqDTO updateReqVO);

    /**
     * 获得系统配置详情
     *
     * @param id 系统配置编号
     * @return 系统配置
     */
    SysContent getSystemContentById(Long id);

}
