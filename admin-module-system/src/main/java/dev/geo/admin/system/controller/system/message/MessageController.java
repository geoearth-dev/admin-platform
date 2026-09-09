package dev.geo.admin.system.controller.system.message;

import cn.hutool.core.bean.BeanUtil;
import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.excel.core.ExcelService;
import dev.geo.admin.security.utils.SecurityUtils;
import dev.geo.admin.system.model.message.dto.MessagePageReqDTO;
import dev.geo.admin.system.model.message.dto.MessageReadAllDTO;
import dev.geo.admin.system.model.message.dto.MessageSendDTO;
import dev.geo.admin.system.model.message.entity.SysMessage;
import dev.geo.admin.system.model.message.vo.MessageUnreadCountVO;
import dev.geo.admin.system.model.message.vo.MessageVO;
import dev.geo.admin.system.service.message.ISysMessageService;
import dev.geo.admin.system.service.message.MessageSseService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;
import java.util.List;

/**
 * 当前用户站内消息接口。
 */
@RestController
@RequestMapping("/system/messages")
@RequiredArgsConstructor
public class MessageController extends BaseController {
    private final ISysMessageService messageService;
    private final MessageSseService messageSseService;
    private final ExcelService excelService;

    @PreAuthorize("@se.hasPermission('system:message:list')")
    @GetMapping
    public ApiResult<PageResult<MessageVO>> list(@Validated MessagePageReqDTO query) {
        PageResult<SysMessage> page = messageService.selectMessagePage(query, SecurityUtils.getUserId());
        return success(toMessagePage(page));
    }

    @PreAuthorize("@se.hasPermission('system:message:query')")
    @GetMapping("/{id}")
    public ApiResult<MessageVO> getInfo(@PathVariable Long id) {
        SysMessage message = messageService.selectMessage(id, SecurityUtils.getUserId());
        return success(BeanUtil.toBean(message, MessageVO.class));
    }

    @PreAuthorize("@se.hasPermission('system:message:list')")
    @GetMapping("/unread-count")
    public ApiResult<MessageUnreadCountVO> unreadCount() {
        return success(new MessageUnreadCountVO(messageService.countUnread(SecurityUtils.getUserId())));
    }

    /**
     * 建立当前用户的消息推送流。
     */
    @PreAuthorize("@se.hasPermission('system:message:list')")
    @GetMapping(value = "/stream", produces = "text/event-stream")
    public SseEmitter stream() {
        return messageSseService.connect(SecurityUtils.getUserId());
    }

    @PreAuthorize("@se.hasPermission('system:message:read')")
    @PutMapping("/{id}/read")
    public ApiResult<Void> read(@PathVariable Long id) {
        return toApiResult(messageService.markRead(id, SecurityUtils.getUserId()));
    }

    @PreAuthorize("@se.hasPermission('system:message:read')")
    @PutMapping("/read-all")
    public ApiResult<Void> readAll(@RequestBody MessageReadAllDTO request) {
        messageService.markAllRead(SecurityUtils.getUserId(), request);
        return success();
    }

    @PreAuthorize("@se.hasPermission('system:message:remove')")
    @Log(title = "站内消息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public ApiResult<Void> remove(@PathVariable Long[] ids) {
        return toApiResult(messageService.deleteMessages(Arrays.asList(ids), SecurityUtils.getUserId()));
    }

    @PreAuthorize("@se.hasPermission('system:message:send')")
    @Log(title = "站内消息", businessType = BusinessType.INSERT)
    @PostMapping("/send")
    public ApiResult<Long> send(@Validated @RequestBody MessageSendDTO request) {
        return success(messageService.sendMessage(request, SecurityUtils.getUserId()));
    }

    @PreAuthorize("@se.hasPermission('system:message:export')")
    @Log(title = "站内消息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MessagePageReqDTO query) {
        List<MessageVO> rows = BeanUtil.copyToList(
                messageService.selectMessageList(query, SecurityUtils.getUserId()), MessageVO.class);
        excelService.exportExcel(response, rows, MessageVO.class, "消息数据");
    }

    private PageResult<MessageVO> toMessagePage(PageResult<SysMessage> source) {
        return new PageResult<>(
                BeanUtil.copyToList(source.getRecords(), MessageVO.class),
                source.getTotal(), source.getPageNum(), source.getPageSize(), source.getPages());
    }
}
