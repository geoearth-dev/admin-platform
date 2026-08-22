package dev.geoearth.admin.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 业务操作类型
 */
@Getter
@RequiredArgsConstructor
public enum BusinessType {
    OTHER(0, "其他"),
    QUERY(1, "查询"),
    INSERT(2, "新增"),
    UPDATE(3, "修改"),
    DELETE(4, "删除"),
    ENABLE(5, "启用"),
    DISABLE(6, "停用"),
    IMPORT(7, "导入"),
    EXPORT(8, "导出");
    private final int code;

    private final String description;
}
