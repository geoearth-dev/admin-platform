package dev.geo.admin.system.controller.auth;

import dev.geo.admin.common.annotation.Anonymous;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.system.model.auth.vo.CaptchaVO;
import dev.geo.admin.system.service.auth.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码操作处理
 */
@Tag(name = "登录认证")
@RestController
@RequiredArgsConstructor
public class CaptchaController {
    private final CaptchaService captchaService;

    /**
     * 生成验证码
     */
    @Anonymous
    @GetMapping("/captcha")
    @Operation(summary = "获取图形验证码")
    public ResponseEntity<ApiResult<CaptchaVO>> captcha() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(ApiResult.success(captchaService.generate()));
    }
}
