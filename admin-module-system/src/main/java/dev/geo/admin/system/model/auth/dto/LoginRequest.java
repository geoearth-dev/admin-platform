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
        @Schema(description = "验证码")
        String code,
        @Schema(description = "唯一标识")
        String uuid,
        @Schema(description = "是否保持长期登录")
        boolean rememberMe
) {

}
