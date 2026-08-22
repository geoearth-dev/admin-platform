package dev.geoearth.admin.service.dictionary;

import dev.geoearth.admin.common.core.entity.PageParam;
import dev.geoearth.admin.common.core.response.PageResponse;
import dev.geoearth.admin.model.dto.dictionary.DictDataPageQuery;
import dev.geoearth.admin.model.dto.dictionary.DictTypePageQuery;
import dev.geoearth.admin.model.entity.dictionary.SysDictData;
import dev.geoearth.admin.model.entity.dictionary.SysDictType;
import dev.geoearth.admin.model.vo.dictionary.DictOptionVO;

import java.util.List;
import java.util.Map;

/**
 * 系统字典维护业务接口。
 */
public interface SysDictService {

    /**
     * 查询全部字典类型。
     */
    PageResponse<SysDictType> listTypes(PageParam pageParam, DictTypePageQuery query);

    /**
     * 按字典类型查询字典数据。
     */
    PageResponse<SysDictData> listData(PageParam pageParam, DictDataPageQuery query);

    /**
     * 新增字典类型。
     */
    int createType(SysDictType type);

    /**
     * 修改字典类型。
     */
    int updateType(SysDictType dict);

    /**
     * 在指定字典类型下新增字典数据。
     */
    int createData(SysDictData data);

    /**
     * 修改字典数据。
     */
    int updateData(SysDictData request);

    /**
     * 停用字典类型及其全部字典数据。
     */
    void disableType(Long dictId);

    /**
     * 启用字典类型及其全部字典数据。
     */
    void enableType(Long dictId);

    /**
     * 停用单条字典数据。
     */
    void disableData(Long dictCode);

    /**
     * 启用单条字典数据。
     */
    void enableData(Long dictCode);


    /**
     * 全量重新加载字典。
     */
    void reloadAllDictData();


    /**
     * 根据dictType从数据库查询字典数据。
     *
     * @param dictType 字典类型
     */
    List<DictOptionVO> getItems(String dictType);

    /**
     * 根据dictType从数据库查询字典数据。
     *
     * @param dictType 字典类型
     */
    Map<String, String> getLabelMap(String dictType);

    /**
     * 获取一个字典值对应的标签。
     */
    String getLabel(String dictType, String dictValue);
}
