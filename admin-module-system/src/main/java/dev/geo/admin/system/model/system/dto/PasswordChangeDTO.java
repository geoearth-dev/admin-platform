package dev.geo.admin.system.model.system.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 当前用户修改密码请求。
 */
public record PasswordChangeDTO(
        @NotBlank String oldPassword,
        @NotBlank @Size(min = 5, max = 20) String newPassword
) {
}
