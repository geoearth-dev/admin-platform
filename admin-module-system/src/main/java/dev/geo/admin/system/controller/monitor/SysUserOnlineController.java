package dev.geo.admin.system.controller.monitor;

import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.system.model.monitor.dto.OnlineSessionQueryDTO;
import dev.geo.admin.system.model.monitor.entity.SysUserOnline;
import dev.geo.admin.system.service.monitor.ISysUserOnlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在线会话管理接口。
 */
@RestController
@RequestMapping("/monitor/online-session")
@RequiredArgsConstructor
public class SysUserOnlineController extends BaseController {
    private final ISysUserOnlineService onlineService;

    @PreAuthorize("@se.hasPermission('monitor:online-session:list')")
    @GetMapping("/list")
    public ApiResult<List<SysUserOnline>> list(OnlineSessionQueryDTO query) {
        return success(onlineService.getOnlineSessions(query));
    }

    @PreAuthorize("@se.hasPermission('monitor:online-session:force-logout')")
    @Log(title = "在线会话", businessType = BusinessType.FORCE)
    @DeleteMapping("/{sessionId}")
    public ApiResult<Void> terminateSession(@PathVariable String sessionId) {
        onlineService.terminateSession(sessionId);
        return success();
    }
}
