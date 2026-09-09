package dev.geo.admin.system.model.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 部门保存参数。
 */
@Getter
@Setter
public class DepartmentSaveDTO {
    private Long id;
    private Long parentId;

    @NotBlank(message = "部门名称不能为空")
    @Size(max = 30, message = "部门名称不能超过30个字符")
    private String deptName;

    @NotNull(message = "显示顺序不能为空")
    private Integer orderNum;

    private String leader;
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱不能超过50个字符")
    private String email;

    private String status;
}
