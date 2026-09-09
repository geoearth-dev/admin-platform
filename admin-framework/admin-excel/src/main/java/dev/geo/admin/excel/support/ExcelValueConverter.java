package dev.geo.admin.excel.support;

import dev.geo.admin.excel.annotation.Excel;
import dev.geo.admin.excel.annotation.ExcelHandlerAdapter;
import dev.geo.admin.excel.core.ExcelException;
import dev.geo.admin.excel.dict.ExcelDictResolver;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Excel 值转换器，负责字典、表达式、日期、数字和自定义处理器转换。
 */
public final class ExcelValueConverter {

    private final ExcelDictResolver dictResolver;
    private final AutowireCapableBeanFactory beanFactory;
    private final Map<Class<?>, ExcelHandlerAdapter> handlerCache = new ConcurrentHashMap<>();

    public ExcelValueConverter(
            ExcelDictResolver dictResolver,
            AutowireCapableBeanFactory beanFactory
    ) {
        this.dictResolver = dictResolver;
        this.beanFactory = beanFactory;
    }

    public Object readProperty(Object row, ExcelField metadata) {
        try {
            Object value = metadata.field().get(row);
            if (value == null || metadata.annotation().targetAttr().isBlank()) {
                return value;
            }

            for (String name : metadata.annotation().targetAttr().split("\\.")) {
                if (value == null) {
                    return null;
                }
                Field nested = findField(value.getClass(), name);
                nested.trySetAccessible();
                value = nested.get(value);
            }
            return value;
        } catch (ReflectiveOperationException exception) {
            throw new ExcelException("读取 Excel 字段失败：" + metadata.propertyPath(), exception);
        }
    }

    public void writeProperty(Object row, ExcelField metadata, Object rawValue, Cell cell, Workbook workbook) {
        try {
            Object converted = importValue(rawValue, metadata, cell, workbook);
            Field targetField = metadata.field();
            Object target = row;

            if (!metadata.annotation().targetAttr().isBlank()) {
                Object nested = targetField.get(row);
                if (nested == null) {
                    nested = targetField.getType().getDeclaredConstructor().newInstance();
                    targetField.set(row, nested);
                }
                target = nested;

                String[] path = metadata.annotation().targetAttr().split("\\.");
                for (int index = 0; index < path.length - 1; index++) {
                    Field part = findField(target.getClass(), path[index]);
                    part.trySetAccessible();
                    Object next = part.get(target);
                    if (next == null) {
                        next = part.getType().getDeclaredConstructor().newInstance();
                        part.set(target, next);
                    }
                    target = next;
                }
                targetField = findField(target.getClass(), path[path.length - 1]);
                targetField.trySetAccessible();
            }

            targetField.set(target, convertType(converted, targetField.getType(), metadata.annotation().dateFormat()));
        } catch (ReflectiveOperationException | IllegalArgumentException exception) {
            throw new ExcelException("写入 Excel 字段失败：" + metadata.propertyPath(), exception);
        }
    }

    public Object exportValue(Object value, ExcelField metadata, Cell cell, Workbook workbook) {
        Excel annotation = metadata.annotation();
        Object result = value;

        if (result == null || result.toString().isBlank()) {
            result = annotation.defaultValue();
        }

        if (result != null && !annotation.readConverterExp().isBlank()) {
            result = convertByExpression(result.toString(), annotation.readConverterExp(), annotation.separator(), false);
        } else if (result != null && !annotation.dictType().isBlank()) {
            result = dictResolver.toLabel(annotation.dictType(), result.toString(), annotation.separator());
        }

        if (result != null && !annotation.dateFormat().isBlank()) {
            result = formatDate(result, annotation.dateFormat());
        }

        if (result instanceof BigDecimal decimal && annotation.scale() >= 0) {
            RoundingMode mode = annotation.roundingMode();
            result = decimal.setScale(annotation.scale(), mode);
        }

        if (annotation.handler() != ExcelHandlerAdapter.class) {
            result = handler(annotation.handler()).format(result, annotation.args(), cell, workbook);
        }

        if (result != null && !annotation.suffix().isBlank()) {
            result = result + annotation.suffix();
        }

        return result;
    }

    private Object importValue(Object value, ExcelField metadata, Cell cell, Workbook workbook) {
        Excel annotation = metadata.annotation();
        Object result = value;

        if (result != null && !annotation.suffix().isBlank()) {
            String text = result.toString();
            if (text.endsWith(annotation.suffix())) {
                result = text.substring(0, text.length() - annotation.suffix().length());
            }
        }

        if (result != null && !annotation.readConverterExp().isBlank()) {
            result = convertByExpression(result.toString(), annotation.readConverterExp(), annotation.separator(), true);
        } else if (result != null && !annotation.dictType().isBlank()) {
            result = dictResolver.toValue(annotation.dictType(), result.toString(), annotation.separator());
        }

        if (annotation.handler() != ExcelHandlerAdapter.class) {
            result = handler(annotation.handler()).format(result, annotation.args(), cell, workbook);
        }
        return result;
    }

    private ExcelHandlerAdapter handler(Class<? extends ExcelHandlerAdapter> handlerType) {
        return handlerCache.computeIfAbsent(handlerType, type -> {
            Object candidate = beanFactory.createBean(type);
            if (!(candidate instanceof ExcelHandlerAdapter adapter)) {
                throw new ExcelException("Excel 处理器必须实现 ExcelHandlerAdapter：" + type.getName());
            }
            return adapter;
        });
    }

    private static Object convertType(Object value, Class<?> targetType, String datePattern) {
        if (value == null || value.toString().isBlank()) {
            return targetType.isPrimitive() ? primitiveDefault(targetType) : null;
        }
        if (targetType.isInstance(value)) {
            return value;
        }

        String text = value.toString().trim();
        if (targetType == String.class) {
            return text.matches("^-?\\d+\\.0$") ? text.substring(0, text.length() - 2) : text;
        }
        if (targetType == Integer.class || targetType == int.class) {
            return new BigDecimal(text).intValue();
        }
        if (targetType == Long.class || targetType == long.class) {
            return new BigDecimal(text).longValue();
        }
        if (targetType == Double.class || targetType == double.class) {
            return Double.valueOf(text);
        }
        if (targetType == Float.class || targetType == float.class) {
            return Float.valueOf(text);
        }
        if (targetType == Short.class || targetType == short.class) {
            return Short.valueOf(text);
        }
        if (targetType == BigDecimal.class) {
            return new BigDecimal(text);
        }
        if (targetType == Boolean.class || targetType == boolean.class) {
            return "1".equals(text) || "true".equalsIgnoreCase(text) || "是".equals(text);
        }
        if (targetType == LocalDate.class) {
            if (value instanceof Date date) {
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
            String pattern = datePattern.isBlank() ? "yyyy-MM-dd" : datePattern;
            return LocalDate.parse(text, DateTimeFormatter.ofPattern(pattern));
        }
        if (targetType == LocalDateTime.class) {
            if (value instanceof Date date) {
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            String pattern = datePattern.isBlank() ? "yyyy-MM-dd HH:mm:ss" : datePattern;
            return LocalDateTime.parse(text, DateTimeFormatter.ofPattern(pattern));
        }
        if (targetType == Date.class) {
            if (value instanceof Date) {
                return value;
            }
            String pattern = datePattern.isBlank() ? "yyyy-MM-dd HH:mm:ss" : datePattern;
            if (pattern.contains("H") || pattern.contains("m") || pattern.contains("s")) {
                LocalDateTime dateTime = LocalDateTime.parse(text, DateTimeFormatter.ofPattern(pattern));
                return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
            }
            LocalDate date = LocalDate.parse(text, DateTimeFormatter.ofPattern(pattern));
            return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        if (targetType.isEnum()) {
            @SuppressWarnings({"rawtypes", "unchecked"})
            Object enumValue = Enum.valueOf((Class<? extends Enum>) targetType, text);
            return enumValue;
        }
        throw new ExcelException("暂不支持的 Excel 字段类型：" + targetType.getName());
    }

    private static Object primitiveDefault(Class<?> type) {
        if (type == boolean.class) {
            return false;
        }
        if (type == char.class) {
            return '\0';
        }
        if (type == byte.class) {
            return (byte) 0;
        }
        if (type == short.class) {
            return (short) 0;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == long.class) {
            return 0L;
        }
        if (type == float.class) {
            return 0F;
        }
        if (type == double.class) {
            return 0D;
        }
        throw new ExcelException("未知的基本类型：" + type.getName());
    }

    private static String formatDate(Object value, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        if (value instanceof LocalDateTime dateTime) {
            return dateTime.format(formatter);
        }
        if (value instanceof LocalDate date) {
            return date.format(formatter);
        }
        if (value instanceof Date date) {
            return date.toInstant().atZone(ZoneId.systemDefault()).format(formatter);
        }
        return value.toString();
    }

    private static String convertByExpression(
            String source,
            String expression,
            String separator,
            boolean reverse
    ) {
        String[] values = source.split(java.util.regex.Pattern.quote(separator));
        StringBuilder result = new StringBuilder();

        for (String value : values) {
            for (String item : expression.split(",")) {
                String[] pair = item.split("=", 2);
                if (pair.length != 2) {
                    continue;
                }
                String from = reverse ? pair[1] : pair[0];
                String to = reverse ? pair[0] : pair[1];
                if (from.equals(value)) {
                    if (!result.isEmpty()) {
                        result.append(separator);
                    }
                    result.append(to);
                    break;
                }
            }
        }
        return result.isEmpty() ? source : result.toString();
    }

    private static Field findField(Class<?> type, String name) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(type.getName() + "." + name);
    }
}
