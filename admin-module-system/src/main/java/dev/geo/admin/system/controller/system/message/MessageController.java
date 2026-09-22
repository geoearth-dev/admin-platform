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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;
import java.util.List;

/**
 * 当前用户站内消息接口。
 */
@Tag(name = "站内消息")
@RestController
@RequestMapping("/system/messages")
@RequiredArgsConstructor
public class MessageController extends BaseController {
    private final ISysMessageService messageService;
    private final MessageSseService messageSseService;
    private final ExcelService excelService;

    @PreAuthorize("@se.hasPermission('system:message:list')")
    @GetMapping
    @Operation(summary = "分页查询我的消息")
    public ApiResult<PageResult<MessageVO>> list(@Validated @ParameterObject MessagePageReqDTO query) {
        PageResult<SysMessage> page = messageService.selectMessagePage(query, SecurityUtils.getUserId());
        return success(toMessagePage(page));
    }

    @PreAuthorize("@se.hasPermission('system:message:query')")
    @GetMapping("/{id}")
    @Operation(summary = "查询消息详情")
    public ApiResult<MessageVO> getInfo(@Parameter(description = "消息 ID") @PathVariable Long id) {
        SysMessage message = messageService.selectMessage(id, SecurityUtils.getUserId());
        return success(BeanUtil.toBean(message, MessageVO.class));
    }

    @PreAuthorize("@se.hasPermission('system:message:list')")
    @GetMapping("/unread-count")
    @Operation(summary = "统计未读消息")
    public ApiResult<MessageUnreadCountVO> unreadCount() {
        return success(new MessageUnreadCountVO(messageService.countUnread(SecurityUtils.getUserId())));
    }

    /**
     * 所有登录用户共用在线通道；具体业务消息在发送时检查权限。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/stream", produces = "text/event-stream")
    @Operation(summary = "订阅消息推送", description = "通过 SSE 接收消息和心跳事件，请求需携带访问令牌。")
    public SseEmitter stream(JwtAuthenticationToken authentication,
                             HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("X-Accel-Buffering", "no");
        return messageSseService.connect(authentication.getToken().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/pong")
    @Operation(summary = "回复连接心跳", description = "回传心跳事件中的 connectionId 和 pingId，确认当前连接仍在线。")
    public ApiResult<Boolean> pong(
            JwtAuthenticationToken authentication,
            @Valid @RequestBody PongRequest request
    ) {
        return success(messageSseService.pong(
                authentication.getToken().getId(),
                request.connectionId(),
                request.pingId()
        ));
    }

    @Schema(description = "连接心跳回复")
    public record PongRequest(
            @Schema(description = "SSE 连接标识，由服务端事件返回") @NotBlank String connectionId,
            @Schema(description = "本次心跳标识，由 ping 事件返回") @NotBlank String pingId
    ) {
    }

    @PreAuthorize("@se.hasPermission('system:message:read')")
    @PutMapping("/{id}/read")
    @Operation(summary = "将消息标为已读")
    public ApiResult<Void> read(@Parameter(description = "消息 ID") @PathVariable Long id) {
        return toApiResult(messageService.markRead(id, SecurityUtils.getUserId()));
    }

    @PreAuthorize("@se.hasPermission('system:message:read')")
    @PutMapping("/read-all")
    @Operation(summary = "将符合条件的消息全部标为已读")
    public ApiResult<Void> readAll(@RequestBody MessageReadAllDTO request) {
        messageService.markAllRead(SecurityUtils.getUserId(), request);
        return success();
    }

    @PreAuthorize("@se.hasPermission('system:message:remove')")
    @Log(title = "站内消息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除我的消息")
    public ApiResult<Void> remove(@Parameter(description = "消息 ID 列表，多个用逗号分隔") @PathVariable Long[] ids) {
        return toApiResult(messageService.deleteMessages(Arrays.asList(ids), SecurityUtils.getUserId()));
    }

    @PreAuthorize("@se.hasPermission('system:message:send')")
    @Log(title = "站内消息", businessType = BusinessType.INSERT)
    @PostMapping("/send")
    @Operation(summary = "按模板发送消息", description = "按 templateCode 选择模板，用 variables 填充内容，返回新消息 ID。")
    public ApiResult<Long> send(@Validated @RequestBody MessageSendDTO request) {
        return success(messageService.sendMessage(request, SecurityUtils.getUserId()));
    }

    @PreAuthorize("@se.hasPermission('system:message:export')")
    @Log(title = "站内消息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @Operation(summary = "导出我的消息")
    public void export(HttpServletResponse response, @ParameterObject MessagePageReqDTO query) {
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
