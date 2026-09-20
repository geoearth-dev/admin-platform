package dev.geo.admin.system.model.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "用户登录对象")
public record LoginRequest(
        @NotBlank
        @Size(max = 64)
        @Schema(description = "用户名")
        String username,

        @NotBlank
        @Size(max = 128)
        @Schema(description = "用户密码")
        String password,

        @Schema(description = "图形验证码，启用验证码时填写")
        String code,

        @Schema(description = "验证码标识，由 /captcha 返回")
        String uuid,

        @Schema(description = "是否保持长期登录")
        boolean rememberMe
) {

}
