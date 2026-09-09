package dev.geo.admin.excel.dict;

import java.util.List;

/**
 * Excel 字典解析扩展点。
 *
 * <p>Excel 模块只依赖该接口，业务模块可使用数据库、Redis 或远程服务实现。</p>
 */
public interface ExcelDictResolver {

    String toLabel(String dictType, String value, String separator);

    String toValue(String dictType, String label, String separator);

    List<String> labels(String dictType);
}
