package dev.geo.admin.excel.core;

import java.util.List;
import java.util.Objects;

/**
 * 多 Sheet 导出描述。
 */
public record ExcelSheet<T>(
        String sheetName,
        List<T> rows,
        Class<T> rowType,
        String title
) {
    public ExcelSheet {
        if (sheetName == null || sheetName.isBlank()) {
            throw new IllegalArgumentException("Sheet 名称不能为空");
        }
        Objects.requireNonNull(rowType, "Excel 数据类型不能为空");
        rows = rows == null ? List.of() : List.copyOf(rows);
        title = title == null ? "" : title;
    }

    public ExcelSheet(String sheetName, List<T> rows, Class<T> rowType) {
        this(sheetName, rows, rowType, "");
    }
}
