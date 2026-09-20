package dev.geo.admin.system.model.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色关联用户分页查询参数。
 */
@Getter
@Setter
@Schema(description = "角色用户查询条件")
public class RoleUserPageReqDTO extends UserPageReqDTO {
    @NotNull(message = "角色主键不能为空")
    @Schema(description = "角色 ID")
    private Long roleId;
}
