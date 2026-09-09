package dev.geo.admin.system.model.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色数据权限更新参数。
 */
@Getter
@Setter
public class RoleDataScopeDTO {
    @NotNull(message = "角色主键不能为空")
    private Long id;
    private String dataScope;
    private boolean deptCheckStrictly;
    private Long[] deptIds;
}
