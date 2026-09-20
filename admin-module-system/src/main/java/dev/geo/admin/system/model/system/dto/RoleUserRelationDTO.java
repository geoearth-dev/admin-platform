package dev.geo.admin.system.model.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 单个用户角色关系参数。
 */
@Getter
@Setter
@Schema(description = "用户角色关系")
public class RoleUserRelationDTO {
    @NotNull(message = "角色主键不能为空")
    @Schema(description = "角色 ID")
    private Long roleId;

    @NotNull(message = "用户主键不能为空")
    @Schema(description = "用户 ID")
    private Long userId;
}
