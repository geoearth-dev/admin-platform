package dev.geo.admin.system.model.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 角色部门树响应。
 */
@Schema(description = "角色部门选项")
public record RoleDeptTreeVO(
        @Schema(description = "已选部门 ID 列表")
        List<Long> checkedKeys,
        @Schema(description = "部门树")
        List<TreeSelect> departments
) {
}
