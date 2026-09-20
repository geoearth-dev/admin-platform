package dev.geo.admin.system.model.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色数据权限更新参数。
 */
@Getter
@Setter
@Schema(description = "角色数据权限")
public class RoleDataScopeDTO {
    @NotNull(message = "角色主键不能为空")
    @Schema(description = "记录 ID")
    private Long id;
    @Schema(description = "数据范围：1全部，2指定部门，3本部门，4本部门及下级，5本人")
    private String dataScope;
    /** 部门树勾选是否父子联动，true 表示联动。 */
    @Schema(description = "部门勾选是否父子联动")
    private boolean deptCheckLinked;
    @Schema(description = "数据范围为指定部门时使用的部门 ID 列表")
    private Long[] deptIds;
}
