package dev.geo.admin.system.controller.common;

import dev.geo.admin.common.core.model.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "模拟请求")
@RequestMapping("/mock")
@RestController
public class MockController {
    @Operation(summary = "临时模拟HTTP错误状态码，用于测试前端拦截器")
    @GetMapping("/status")
    public ApiResult<Void> mockStatus(@RequestParam("status") int status, HttpServletResponse response) {

        // 此接口只用于模拟 4xx、5xx 错误。
        if (status < 400 || status > 599) {
            response.setStatus(400);
            return ApiResult.error(400, "status 必须在 400～599 之间");
        }

        // 设置真正的 HTTP 状态码。
        response.setStatus(status);

        // 同时返回项目约定的响应体。
        return ApiResult.error(status, "模拟 HTTP " + status);
    }
}
