package dev.geo.admin.system.model.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 当前用户修改密码请求。
 */
@Schema(description = "修改我的密码参数")
public record PasswordChangeDTO(
        @Schema(description = "原密码")
        @NotBlank String oldPassword,
        @Schema(description = "新密码，5至20个字符，不能与原密码相同")
        @NotBlank @Size(min = 5, max = 20) String newPassword
) {
}
