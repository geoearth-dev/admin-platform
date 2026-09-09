package dev.geo.admin.system.resolve.resolver;

import dev.geo.admin.excel.dict.ExcelDictResolver;
import dev.geo.admin.system.model.system.entity.SysDictData;
import dev.geo.admin.system.service.system.DictCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SystemExcelDictResolver implements ExcelDictResolver {
    private final DictCacheService dictCacheService;

    /**
     * 导出时：字典值转换为字典标签。
     */
    @Override
    public String toLabel(String dictType, String value, String separator) {
        return dictCacheService.getDictLabel(dictType, value, separator);
    }

    /**
     * 导入时：字典标签转换为字典值。
     */
    @Override
    public String toValue(String dictType, String label, String separator) {
        return dictCacheService.getDictValue(dictType, label, separator);
    }

    /**
     * 获取字典的全部标签，用于生成 Excel 下拉选项。
     */
    @Override
    public List<String> labels(String dictType) {
        List<SysDictData> dataList = dictCacheService.getDictCache(dictType);
        if (dataList == null || dataList.isEmpty()) {
            return List.of();
        }

        return dataList.stream()
                .map(SysDictData::getDictLabel)
                .toList();
    }
}
