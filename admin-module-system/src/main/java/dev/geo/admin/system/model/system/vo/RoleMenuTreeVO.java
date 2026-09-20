package dev.geo.admin.system.model.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 角色菜单树响应。
 */
@Schema(description = "角色菜单选项")
public record RoleMenuTreeVO(
        @Schema(description = "已选菜单 ID 列表")
        List<Long> checkedKeys,
        @Schema(description = "菜单树")
        List<TreeSelect> menus
) {
}
