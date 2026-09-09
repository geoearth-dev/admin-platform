package dev.geo.admin.excel.support;

import dev.geo.admin.excel.annotation.Excel;
import dev.geo.admin.excel.annotation.Excels;
import dev.geo.admin.excel.core.ExcelOptions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 解析实体类上的 Excel 注解，并完成导入导出方向和字段白名单过滤。
 */
public final class ExcelMetadataResolver {

    private ExcelMetadataResolver() {
    }

    public static List<ExcelField> resolve(
            Class<?> rowType,
            Excel.Type operationType,
            ExcelOptions options
    ) {
        List<ExcelField> result = new ArrayList<>();

        for (Field field : allFields(rowType)) {
            Excel single = field.getAnnotation(Excel.class);
            if (single != null) {
                addIfMatched(result, field, single, operationType, options);
            }

            Excels multiple = field.getAnnotation(Excels.class);
            if (multiple != null) {
                for (Excel annotation : multiple.value()) {
                    addIfMatched(result, field, annotation, operationType, options);
                }
            }
        }

        result.sort(Comparator.comparingInt(item -> item.annotation().sort()));
        return List.copyOf(result);
    }

    private static void addIfMatched(
            List<ExcelField> target,
            Field field,
            Excel annotation,
            Excel.Type operationType,
            ExcelOptions options
    ) {
        if (!matchesOperation(annotation, operationType)) {
            return;
        }

        ExcelField metadata = new ExcelField(field, annotation);
        String propertyPath = metadata.propertyPath();

        if (!options.includeFields().isEmpty()
                && !options.includeFields().contains(propertyPath)
                && !options.includeFields().contains(field.getName())) {
            return;
        }

        if (options.excludeFields().contains(propertyPath)
                || options.excludeFields().contains(field.getName())) {
            return;
        }

        target.add(metadata);
    }

    private static boolean matchesOperation(Excel annotation, Excel.Type operationType) {
        return annotation.type() == Excel.Type.ALL
                || annotation.type() == operationType;
    }

    private static List<Field> allFields(Class<?> type) {
        List<Class<?>> hierarchy = new ArrayList<>();
        Class<?> current = type;
        while (current != null && current != Object.class) {
            hierarchy.add(0, current);
            current = current.getSuperclass();
        }

        List<Field> fields = new ArrayList<>();
        for (Class<?> item : hierarchy) {
            fields.addAll(List.of(item.getDeclaredFields()));
        }
        return fields;
    }
}
