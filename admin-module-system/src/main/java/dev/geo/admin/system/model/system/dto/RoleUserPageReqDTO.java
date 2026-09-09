package dev.geo.admin.system.model.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色关联用户分页查询参数。
 */
@Getter
@Setter
public class RoleUserPageReqDTO extends UserPageReqDTO {
    @NotNull(message = "角色主键不能为空")
    private Long roleId;
}
