package dev.geo.admin.system.model.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "部门保存参数")
public class DepartmentSaveDTO {
    @Schema(description = "记录 ID")
    private Long id;
    @Schema(description = "上级 ID，0表示根节点")
    private Long parentId;

    @NotBlank(message = "部门名称不能为空")
    @Size(max = 30, message = "部门名称不能超过30个字符")
    @Schema(description = "部门名称")
    private String deptName;

    @NotNull(message = "显示顺序不能为空")
    @Schema(description = "显示顺序，越小越靠前")
    private Integer orderNum;

    @Schema(description = "负责人")
    private String leader;
    @Schema(description = "联系电话")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱不能超过50个字符")
    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "状态：1启用，0停用")
    private String status;
}
