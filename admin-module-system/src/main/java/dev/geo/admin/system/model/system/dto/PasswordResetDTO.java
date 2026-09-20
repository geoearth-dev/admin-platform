package dev.geo.admin.system.model.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户密码重置参数。
 */
@Getter
@Setter
@Schema(description = "管理员重置密码参数")
public class PasswordResetDTO {
    @NotNull(message = "用户主键不能为空")
    @Schema(description = "记录 ID")
    private Long id;

    @NotBlank(message = "新密码不能为空")
    @Schema(description = "密码")
    private String password;
}
