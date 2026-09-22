package dev.geo.admin.system.model.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 个人资料修改请求。
 */
@Schema(description = "个人资料修改参数")
public record ProfileUpdateDTO(
        @Schema(description = "用户昵称")
        @NotBlank @Size(max = 30) String nickName,
        @Schema(description = "邮箱")
        @Email @Size(max = 50) String email,
        @Schema(description = "手机号码")
        @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号码格式不正确") String phoneNumber,
        @Schema(description = "性别：0男，1女，2未知")
        @NotBlank @Pattern(regexp = "[012]", message = "性别取值不正确") String sex,
        @Schema(description = "备注")
        @Size(max = 500) String remark
) {
    public ProfileUpdateDTO {
        nickName = nickName == null ? "" : nickName.trim();
        email = email == null ? "" : email.trim();
        phoneNumber = phoneNumber == null ? "" : phoneNumber.trim();
        remark = remark == null ? "" : remark.trim();
    }
}
