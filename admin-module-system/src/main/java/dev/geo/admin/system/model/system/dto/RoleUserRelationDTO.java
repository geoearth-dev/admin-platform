package dev.geo.admin.system.model.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 单个用户角色关系参数。
 */
@Getter
@Setter
public class RoleUserRelationDTO {
    @NotNull(message = "角色主键不能为空")
    private Long roleId;

    @NotNull(message = "用户主键不能为空")
    private Long userId;
}
