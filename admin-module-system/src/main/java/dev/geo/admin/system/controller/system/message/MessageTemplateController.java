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
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 消息模板管理接口。
 */
@RestController
@RequestMapping("/system/message-templates")
@RequiredArgsConstructor
public class MessageTemplateController extends BaseController {
    private final ISysMessageTemplateService templateService;
    private final ExcelService excelService;

    @PreAuthorize("@se.hasPermission('system:message-template:list')")
    @GetMapping
    public ApiResult<PageResult<MessageTemplateVO>> list(@Validated MessageTemplatePageReqDTO query) {
        return success(toTemplatePage(templateService.selectTemplatePage(query)));
    }

    @PreAuthorize("@se.hasPermission('system:message-template:query')")
    @GetMapping("/{id}")
    public ApiResult<MessageTemplateVO> getInfo(@PathVariable Long id) {
        return success(BeanUtil.toBean(templateService.selectTemplate(id), MessageTemplateVO.class));
    }

    @PreAuthorize("@se.hasPermission('system:message-template:add')")
    @Log(title = "消息模板", businessType = BusinessType.INSERT)
    @PostMapping
    public ApiResult<Void> add(@Validated @RequestBody MessageTemplateSaveDTO request) {
        return toApiResult(templateService.createTemplate(request));
    }

    @PreAuthorize("@se.hasPermission('system:message-template:edit')")
    @Log(title = "消息模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public ApiResult<Void> edit(@Validated @RequestBody MessageTemplateSaveDTO request) {
        return toApiResult(templateService.updateTemplate(request));
    }

    @PreAuthorize("@se.hasPermission('system:message-template:remove')")
    @Log(title = "消息模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public ApiResult<Void> remove(@PathVariable Long[] ids) {
        return toApiResult(templateService.deleteTemplates(Arrays.asList(ids)));
    }

    @PreAuthorize("@se.hasPermission('system:message-template:export')")
    @Log(title = "消息模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MessageTemplatePageReqDTO query) {
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
