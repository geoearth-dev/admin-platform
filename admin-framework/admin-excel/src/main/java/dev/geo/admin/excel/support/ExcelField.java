package dev.geo.admin.excel.support;

import dev.geo.admin.excel.annotation.Excel;

import java.lang.reflect.Field;

/**
 * 一个可导入或导出的 Excel 字段定义。
 */
public record ExcelField(Field field, Excel annotation) {

    public ExcelField {
        field.trySetAccessible();
    }

    public String propertyPath() {
        return annotation.targetAttr().isBlank()
                ? field.getName()
                : field.getName() + "." + annotation.targetAttr();
    }
}
