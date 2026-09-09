package dev.geo.admin.system.model.system.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * 个人资料修改请求。
 */
public record ProfileUpdateDTO(
        @Size(max = 30) String nickName,
        @Email @Size(max = 50) String email,
        @Size(max = 11) String phoneNumber,
        String sex
) {
}
