package dev.geo.admin.system.controller.monitor;

import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.system.model.monitor.dto.OnlineSessionQueryDTO;
import dev.geo.admin.system.model.monitor.entity.SysUserOnline;
import dev.geo.admin.system.service.monitor.ISysUserOnlineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在线会话管理接口。
 */
@Tag(name = "在线会话")
@RestController
@RequestMapping("/monitor/online-session")
@RequiredArgsConstructor
public class SysUserOnlineController extends BaseController {
    private final ISysUserOnlineService onlineService;

    @PreAuthorize("@se.hasPermission('monitor:online-session:list')")
    @GetMapping("/list")
    @Operation(summary = "查询在线会话")
    public ApiResult<List<SysUserOnline>> list(@ParameterObject OnlineSessionQueryDTO query) {
        return success(onlineService.getOnlineSessions(query));
    }

    @PreAuthorize("@se.hasPermission('monitor:online-session:force-logout')")
    @Log(title = "在线会话", businessType = BusinessType.FORCE)
    @DeleteMapping("/{sessionId}")
    @Operation(summary = "强制退出指定会话", description = "仅退出 sessionId 指定的会话，不影响该用户的其他会话。")
    public ApiResult<Void> terminateSession(@Parameter(description = "待退出的登录会话 ID") @PathVariable String sessionId) {
        onlineService.terminateSession(sessionId);
        return success();
    }
}
