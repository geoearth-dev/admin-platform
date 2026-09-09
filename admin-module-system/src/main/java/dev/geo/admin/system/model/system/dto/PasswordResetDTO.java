package dev.geo.admin.system.model.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户密码重置参数。
 */
@Getter
@Setter
public class PasswordResetDTO {
    @NotNull(message = "用户主键不能为空")
    private Long id;

    @NotBlank(message = "新密码不能为空")
    private String password;
}
