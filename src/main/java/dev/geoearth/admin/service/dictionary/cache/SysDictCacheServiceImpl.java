package dev.geoearth.admin.service.dictionary.cache;

import dev.geoearth.admin.common.constant.CacheConstants;
import dev.geoearth.admin.common.utils.JsonCodec;
import dev.geoearth.admin.model.converter.dictionray.SysDictConverter;
import dev.geoearth.admin.model.entity.dictionary.SysDictData;
import dev.geoearth.admin.model.vo.dictionary.DictOptionVO;
import dev.geoearth.admin.service.dictionary.SysDictCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典 Redis 缓存实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictCacheServiceImpl implements SysDictCacheService {
    private final StringRedisTemplate stringRedisTemplate;
    private final JsonCodec jsonCodec;
    private static final String DICT_KEY = CacheConstants.SYS_DICT_KEY;

    @Override
    public List<DictOptionVO> getItems(String dictType) {
        Object value = stringRedisTemplate.opsForHash().get(DICT_KEY, dictType);
        if (value == null) {
            return null;
        }
        List<SysDictData> dataList = jsonCodec.fromJsonList(value.toString(), SysDictData.class);
        return dataList.stream().map(SysDictConverter::toVO).toList();

    }


    public void putItems(String dictType, List<SysDictData> items) {
        String json = jsonCodec.toJson(items);
        stringRedisTemplate.opsForHash().put(DICT_KEY, dictType, json);
    }

    public void deleteItems(String dictType) {
        stringRedisTemplate.opsForHash().delete(DICT_KEY, dictType);
    }

    /**
     * 按字典类型加载完整字典快照。
     *
     * @param dictType 字典类型，例如 gender
     * @return 字典缓存快照
     */
    public List<SysDictData> load(String dictType) {
        return List.of();
    }

    /**
     * 清除指定字典类型的缓存。
     *
     * @param dictType 字典类型
     */
    public void evict(String dictType) {

    }
}