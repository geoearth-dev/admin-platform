package dev.geo.admin.system.model.system.dto;

import dev.geo.admin.common.xss.Xss;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户保存参数。
 */
@Getter
@Setter
public class UserSaveDTO {
    private Long id;
    private Long deptId;

    @Xss(message = "用户名不能包含脚本字符")
    @NotBlank(message = "用户名不能为空")
    @Size(max = 30, message = "用户名不能超过30个字符")
    private String userName;

    @Xss(message = "用户昵称不能包含脚本字符")
    @NotBlank(message = "用户昵称不能为空")
    @Size(max = 30, message = "用户昵称不能超过30个字符")
    private String nickName;

    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱不能超过50个字符")
    private String email;

    @Size(max = 11, message = "手机号码不能超过11个字符")
    private String phoneNumber;

    private String sex;
    private String avatar;
    private String password;
    private String status;
    private Long[] roleIds;
    private Long[] postIds;
    private String remark;
}
