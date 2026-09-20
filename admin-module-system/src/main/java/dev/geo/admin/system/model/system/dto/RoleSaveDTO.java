package dev.geo.admin.system.model.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "角色保存参数")
public class RoleSaveDTO {
    @Schema(description = "记录 ID")
    private Long id;

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 30, message = "角色名称不能超过30个字符")
    @Schema(description = "角色名称")
    private String roleName;

    @NotBlank(message = "权限标识不能为空")
    @Size(max = 100, message = "权限标识不能超过100个字符")
    @Schema(description = "角色标识，如 admin")
    private String roleKey;

    @NotNull(message = "显示顺序不能为空")
    @Schema(description = "显示顺序，越小越靠前")
    private Integer roleSort;

    @Schema(description = "数据范围：1全部，2指定部门，3本部门，4本部门及下级，5本人")
    private String dataScope;
    /** 菜单树勾选是否父子联动，true 表示联动。 */
    @Schema(description = "菜单勾选是否父子联动")
    private boolean menuCheckLinked;
    /** 部门树勾选是否父子联动，true 表示联动。 */
    @Schema(description = "部门勾选是否父子联动")
    private boolean deptCheckLinked;
    @Schema(description = "状态：1启用，0停用")
    private String status;
    @Schema(description = "菜单 ID 列表")
    private Long[] menuIds;
    @Schema(description = "数据范围为指定部门时使用的部门 ID 列表")
    private Long[] deptIds;
    @Schema(description = "备注")
    private String remark;
}
