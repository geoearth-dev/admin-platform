package dev.geo.admin.system.model.system.vo;

import java.util.List;

/**
 * 角色部门树响应。
 */
public record RoleDeptTreeVO(List<Long> checkedKeys, List<TreeSelect> departments) {
}
