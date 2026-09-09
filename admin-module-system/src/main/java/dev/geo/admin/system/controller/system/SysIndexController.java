package dev.geo.admin.system.controller.system;

import dev.geo.admin.common.config.AppConfig;
import dev.geo.admin.common.core.model.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统基础信息接口。
 */
@RestController
public class SysIndexController {
    @GetMapping("/")
    public ApiResult<String> index() {
        return ApiResult.success("欢迎使用 " + AppConfig.getName());
    }
}
