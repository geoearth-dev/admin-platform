package dev.geo.admin.system.model.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * 个人资料修改请求。
 */
@Schema(description = "个人资料修改参数")
public record ProfileUpdateDTO(
        @Schema(description = "用户昵称")
        @Size(max = 30) String nickName,
        @Schema(description = "邮箱")
        @Email @Size(max = 50) String email,
        @Schema(description = "手机号码")
        @Size(max = 11) String phoneNumber,
        @Schema(description = "性别：0男，1女，2未知")
        String sex
) {
}
