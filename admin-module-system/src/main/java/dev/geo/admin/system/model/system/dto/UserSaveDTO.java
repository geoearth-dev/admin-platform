package dev.geo.admin.system.model.system.dto;

import dev.geo.admin.common.xss.Xss;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "用户保存参数")
public class UserSaveDTO {
    @Schema(description = "用户 ID，新增时不传，修改时必传")
    private Long id;
    @Schema(description = "所属部门 ID")
    private Long deptId;

    @Xss(message = "用户名不能包含脚本字符")
    @NotBlank(message = "用户名不能为空")
    @Size(max = 30, message = "用户名不能超过30个字符")
    @Schema(description = "用户账号")
    private String userName;

    @Xss(message = "用户昵称不能包含脚本字符")
    @NotBlank(message = "用户昵称不能为空")
    @Size(max = 30, message = "用户昵称不能超过30个字符")
    @Schema(description = "用户昵称")
    private String nickName;

    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱不能超过50个字符")
    @Schema(description = "邮箱")
    private String email;

    @Size(max = 11, message = "手机号码不能超过11个字符")
    @Schema(description = "手机号码")
    private String phoneNumber;

    @Schema(description = "性别：0男，1女，2未知")
    private String sex;
    @Schema(description = "头像地址")
    private String avatar;
    @Schema(description = "初始密码；修改已有用户请使用重置密码接口")
    private String password;
    @Schema(description = "状态：1启用，0停用")
    private String status;
    @Schema(description = "角色 ID 列表")
    private Long[] roleIds;
    @Schema(description = "岗位 ID 列表")
    private Long[] postIds;
    @Schema(description = "备注")
    private String remark;
}
