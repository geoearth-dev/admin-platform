package dev.geo.admin.system.controller.system.message;

import cn.hutool.core.bean.BeanUtil;
import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.excel.core.ExcelService;
import dev.geo.admin.system.model.message.dto.MessageTemplatePageReqDTO;
import dev.geo.admin.system.model.message.dto.MessageTemplateSaveDTO;
import dev.geo.admin.system.model.message.entity.SysMessageTemplate;
import dev.geo.admin.system.model.message.vo.MessageTemplateVO;
import dev.geo.admin.system.service.message.ISysMessageTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 消息模板管理接口。
 */
@Tag(name = "消息模板")
@RestController
@RequestMapping("/system/message-templates")
@RequiredArgsConstructor
public class MessageTemplateController extends BaseController {
    private final ISysMessageTemplateService templateService;
    private final ExcelService excelService;

    @PreAuthorize("@se.hasPermission('system:message-template:list')")
    @GetMapping
    @Operation(summary = "分页查询消息模板")
    public ApiResult<PageResult<MessageTemplateVO>> list(@Validated @ParameterObject MessageTemplatePageReqDTO query) {
        return success(toTemplatePage(templateService.selectTemplatePage(query)));
    }

    @PreAuthorize("@se.hasPermission('system:message-template:query')")
    @GetMapping("/{id}")
    @Operation(summary = "查询消息模板详情")
    public ApiResult<MessageTemplateVO> getInfo(@Parameter(description = "消息模板 ID") @PathVariable Long id) {
        return success(BeanUtil.toBean(templateService.selectTemplate(id), MessageTemplateVO.class));
    }

    @PreAuthorize("@se.hasPermission('system:message-template:add')")
    @Log(title = "消息模板", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增消息模板")
    public ApiResult<Void> add(@Validated @RequestBody MessageTemplateSaveDTO request) {
        return toApiResult(templateService.createTemplate(request));
    }

    @PreAuthorize("@se.hasPermission('system:message-template:edit')")
    @Log(title = "消息模板", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改消息模板")
    public ApiResult<Void> edit(@Validated @RequestBody MessageTemplateSaveDTO request) {
        return toApiResult(templateService.updateTemplate(request));
    }

    @PreAuthorize("@se.hasPermission('system:message-template:remove')")
    @Log(title = "消息模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除消息模板")
    public ApiResult<Void> remove(@Parameter(description = "消息模板 ID 列表，多个用逗号分隔") @PathVariable Long[] ids) {
        return toApiResult(templateService.deleteTemplates(Arrays.asList(ids)));
    }

    @PreAuthorize("@se.hasPermission('system:message-template:export')")
    @Log(title = "消息模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @Operation(summary = "导出消息模板")
    public void export(HttpServletResponse response, @ParameterObject MessageTemplatePageReqDTO query) {
        List<MessageTemplateVO> rows = BeanUtil.copyToList(
                templateService.selectTemplateList(query), MessageTemplateVO.class);
        excelService.exportExcel(response, rows, MessageTemplateVO.class, "消息模板");
    }

    private PageResult<MessageTemplateVO> toTemplatePage(PageResult<SysMessageTemplate> source) {
        return new PageResult<>(
                BeanUtil.copyToList(source.getRecords(), MessageTemplateVO.class),
                source.getTotal(), source.getPageNum(), source.getPageSize(), source.getPages());
    }
}
