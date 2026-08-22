package dev.geoearth.admin.service.dictionary;

import dev.geoearth.admin.model.entity.dictionary.SysDictData;
import dev.geoearth.admin.model.vo.dictionary.DictOptionVO;

import java.util.List;

public interface SysDictCacheService {

    /**
     * 获取指定类型的缓存。
     *
     * @return null表示缓存不存在，空集合表示已经缓存但没有数据
     */
    List<DictOptionVO> getItems(String dictType);

    void putItems(String dictType, List<SysDictData> items);

    void deleteItems(String dictType);
}
