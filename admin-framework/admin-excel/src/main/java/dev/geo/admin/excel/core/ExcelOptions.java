package dev.geo.admin.excel.core;

import java.util.Set;

/**
 * 单次 Excel 操作选项。
 */
public record ExcelOptions(
        Set<String> includeFields,
        Set<String> excludeFields,
        int templateRows
) {
    public static final ExcelOptions DEFAULT = new ExcelOptions(Set.of(), Set.of(), 100);

    public ExcelOptions {
        includeFields = includeFields == null ? Set.of() : Set.copyOf(includeFields);
        excludeFields = excludeFields == null ? Set.of() : Set.copyOf(excludeFields);
        if (!includeFields.isEmpty() && !excludeFields.isEmpty()) {
            throw new IllegalArgumentException("includeFields 与 excludeFields 不能同时使用");
        }
        if (templateRows < 1) {
            throw new IllegalArgumentException("模板行数必须大于 0");
        }
    }

    public static ExcelOptions include(String... fields) {
        return new ExcelOptions(Set.of(fields), Set.of(), 100);
    }

    public static ExcelOptions exclude(String... fields) {
        return new ExcelOptions(Set.of(), Set.of(fields), 100);
    }
}
