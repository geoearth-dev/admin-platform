package dev.geo.admin.system.model.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色保存参数。
 */
@Getter
@Setter
public class RoleSaveDTO {
    private Long id;

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 30, message = "角色名称不能超过30个字符")
    private String roleName;

    @NotBlank(message = "权限标识不能为空")
    @Size(max = 100, message = "权限标识不能超过100个字符")
    private String roleKey;

    @NotNull(message = "显示顺序不能为空")
    private Integer roleSort;

    private String dataScope;
    private boolean menuCheckStrictly;
    private boolean deptCheckStrictly;
    private String status;
    private Long[] menuIds;
    private Long[] deptIds;
    private String remark;
}
