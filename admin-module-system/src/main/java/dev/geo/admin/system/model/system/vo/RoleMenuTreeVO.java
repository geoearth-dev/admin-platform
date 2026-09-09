package dev.geo.admin.system.model.system.vo;

import java.util.List;

/**
 * 角色菜单树响应。
 */
public record RoleMenuTreeVO(
        List<Long> checkedKeys,
        List<TreeSelect> menus
) {
}
