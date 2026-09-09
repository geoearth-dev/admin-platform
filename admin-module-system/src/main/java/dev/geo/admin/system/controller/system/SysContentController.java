package dev.geo.admin.system.controller.system;

import dev.geo.admin.common.annotation.Anonymous;
import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.system.model.system.converter.SysContentConverter;
import dev.geo.admin.system.model.system.dto.SysContentPageReqDTO;
import dev.geo.admin.system.model.system.dto.SysContentSaveReqDTO;
import dev.geo.admin.system.model.system.entity.SysContent;
import dev.geo.admin.system.model.system.vo.SysContentPublicVO;
import dev.geo.admin.system.model.system.vo.SysContentRespVO;
import dev.geo.admin.system.service.system.ISysContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统展示内容接口，维护系统名称、Logo、版权和联系方式等信息。
 */
@Tag(name = "系统展示配置")
@RestController
@RequiredArgsConstructor
public class SysContentController extends BaseController {
    private final ISysContentService contentService;

    @Operation(summary = "分页查询系统展示配置")
    @PreAuthorize("@se.hasPermission('system:content:list')")
    @GetMapping("/system/content/list")
    public ApiResult<PageResult<SysContentRespVO>> list(@Validated SysContentPageReqDTO query) {
        PageResult<SysContent> page = contentService.getSystemContentPage(query);
        return success(page.convert(SysContentConverter::toRespVO));
    }

    /**
     * 登录页需要读取系统名称和 Logo，因此允许匿名访问。
     */
    @Anonymous
    @Operation(summary = "查询系统展示配置详情")
    @GetMapping("/sys/content/{id}")
    public ApiResult<SysContentPublicVO> getInfo(@PathVariable Long id) {
        SysContent content = contentService.getSystemContentById(id);
        return success(SysContentConverter.toPublicVO(content));
    }

    @Operation(summary = "修改系统展示配置")
    @PreAuthorize("@se.hasPermission('system:content:edit')")
    @Log(title = "系统展示配置", businessType = BusinessType.UPDATE)
    @PostMapping("/system/content/edit")
    public ApiResult<Void> edit(@Validated @RequestBody SysContentSaveReqDTO request) {
        return toApiResult(contentService.updateSystemContent(request));
    }

}
