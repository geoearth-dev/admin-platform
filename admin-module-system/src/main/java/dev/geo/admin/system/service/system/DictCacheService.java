package dev.geo.admin.system.service.system;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.constant.CacheConstants;
import dev.geo.admin.redis.RedisCache;
import dev.geo.admin.system.model.system.entity.SysDictData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DictCacheService {
    private final RedisCache redisCache;
    /**
     * 分隔符
     */
    public final String SEPARATOR = ",";

    /**
     * 设置字典缓存
     *
     * @param key      参数键
     * @param dataList 字典数据列表
     */
    public void setDictCache(String key, List<SysDictData> dataList) {
        redisCache.setCacheObject(getCacheKey(key), dataList);
    }

    /**
     * 获取字典缓存
     *
     * @param key 参数键
     * @return dataList 字典数据列表
     */
    public List<SysDictData> getDictCache(String key) {
        return redisCache.getCacheObjectList(getCacheKey(key), SysDictData.class);

    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    public String getDictLabel(String dictType, String dictValue) {
        if (StrUtil.isEmpty(dictValue)) {
            return StrUtil.EMPTY;
        }
        return getDictLabel(dictType, dictValue, SEPARATOR);
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType  字典类型
     * @param dictLabel 字典标签
     * @return 字典值
     */
    public String getDictValue(String dictType, String dictLabel) {
        if (StrUtil.isEmpty(dictLabel)) {
            return StrUtil.EMPTY;
        }
        return getDictValue(dictType, dictLabel, SEPARATOR);
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @param separator 分隔符
     * @return 字典标签
     */
    public String getDictLabel(String dictType, String dictValue, String separator) {
        List<SysDictData> dataList = getDictCache(dictType);
        if (ObjectUtil.isNull(dataList) || StrUtil.isEmpty(dictValue)) {
            return StrUtil.EMPTY;
        }
        Map<String, String> dictMap = dataList.stream().collect(HashMap::new, (map, dict) -> map.put(dict.getDictValue(), dict.getDictLabel()), Map::putAll);
        return getDictString(dictValue, separator, dictMap);
    }


    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType  字典类型
     * @param dictLabel 字典标签
     * @param separator 分隔符
     * @return 字典值
     */
    public String getDictValue(String dictType, String dictLabel, String separator) {
        List<SysDictData> dataList = getDictCache(dictType);
        if (ObjectUtil.isNull(dataList) || StrUtil.isEmpty(dictLabel)) {
            return StrUtil.EMPTY;
        }
        Map<String, String> dictMap = dataList.stream().collect(HashMap::new, (map, dict) -> map.put(dict.getDictLabel(), dict.getDictValue()), Map::putAll);
        return getDictString(dictLabel, separator, dictMap);
    }

    /**
     * 根据字典类型获取字典所有值
     *
     * @param dictType 字典类型
     * @return 字典值
     */
    public String getDictValues(String dictType) {
        StringBuilder propertyString = new StringBuilder();
        List<SysDictData> dataList = getDictCache(dictType);
        if (ObjectUtil.isNull(dataList)) {
            return StrUtil.EMPTY;
        }
        for (SysDictData dict : dataList) {
            propertyString.append(dict.getDictValue()).append(SEPARATOR);
        }
        return StrUtil.removeSuffix(propertyString.toString(), SEPARATOR);
    }

    /**
     * 根据字典类型获取字典所有标签
     *
     * @param dictType 字典类型
     * @return 字典值
     */
    public String getDictLabels(String dictType) {
        StringBuilder propertyString = new StringBuilder();
        List<SysDictData> dataList = getDictCache(dictType);
        if (ObjectUtil.isNull(dataList)) {
            return StrUtil.EMPTY;
        }
        for (SysDictData dict : dataList) {
            propertyString.append(dict.getDictLabel()).append(SEPARATOR);
        }
        return StrUtil.removeSuffix(propertyString.toString(), SEPARATOR);
    }

    /**
     * 删除指定字典缓存
     *
     * @param key 字典键
     */
    public void removeDictCache(String key) {
        redisCache.deleteObject(getCacheKey(key));
    }

    /**
     * 清空字典缓存
     */
    public void clearDictCache() {
        Collection<String> keys = redisCache.keys(CacheConstants.SYS_DICT_KEY + "*");
        redisCache.deleteObject(keys);
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    public String getCacheKey(String configKey) {
        return CacheConstants.SYS_DICT_KEY + configKey;
    }


    private String getDictString(String dictValue, String separator, Map<String, String> dictMap) {
        if (!StrUtil.contains(dictValue, separator)) {
            return dictMap.getOrDefault(dictValue, StrUtil.EMPTY);
        }
        StringBuilder labelBuilder = new StringBuilder();
        for (String seperatedValue : dictValue.split(separator)) {
            if (dictMap.containsKey(seperatedValue)) {
                labelBuilder.append(dictMap.get(seperatedValue)).append(separator);
            }
        }
        return StrUtil.removeSuffix(labelBuilder.toString(), separator);
    }
}
