package dev.geo.admin.system.controller.system;

import dev.geo.admin.common.config.AppConfig;
import dev.geo.admin.common.core.model.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统基础信息接口。
 */
@Tag(name = "服务信息")
@RestController
public class SysIndexController {
    @GetMapping("/")
    @Operation(summary = "检查服务是否可访问")
    public ApiResult<String> index() {
        return ApiResult.success("欢迎使用 " + AppConfig.getName());
    }
}
