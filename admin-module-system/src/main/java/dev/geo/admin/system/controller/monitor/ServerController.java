package dev.geo.admin.system.controller.monitor;

import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.config.server.Server;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器运行状态接口。
 */
@Tag(name = "服务监控")
@RestController
@RequestMapping("/monitor/server")
public class ServerController {
    @PreAuthorize("@se.hasPermission('monitor:server:list')")
    @GetMapping
    @Operation(summary = "获取服务器运行信息")
    public ApiResult<Server> getInfo() throws InterruptedException {
        Server server = new Server();
        server.copyTo();
        return ApiResult.success(server);
    }
}
