package dev.geo.admin.system.model.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色主键集合参数。
 */
@Getter
@Setter
public class RoleIdsDTO {
    @NotNull(message = "角色主键集合不能为空")
    private Long[] roleIds;
}
