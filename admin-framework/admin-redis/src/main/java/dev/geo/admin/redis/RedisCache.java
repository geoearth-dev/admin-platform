package dev.geo.admin.redis;

import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * spring redis 工具类
 **/
@Component
public class RedisCache {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisCache(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @param ttl   Duration
     */
    public <T> void setCacheObject(final String key, final T value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, Expiration.from(timeout, timeUnit));
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, Expiration.from(timeout, unit));
    }

    /**
     * 获取有效时间
     *
     * @param key Redis键
     * @return 有效时间
     */
    public long getExpire(final String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 判断 key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key, final Class<T> targetType) {
        final Object value = redisTemplate.opsForValue().get(key);
        return convertValue(value, targetType);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public Object getCacheObject(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key key
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param keys 多个对象
     * @return boolean
     */
    public boolean deleteObject(Collection<String> keys) {
        Long deletedCount = redisTemplate.delete(keys);
        return deletedCount > 0;
    }

    public <T> List<T> getCacheObjectList(String key, Class<T> elementType) {
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        if (!(value instanceof Collection<?> collection)) {
            throw new IllegalStateException("Redis缓存值不是集合类型：" + key);
        }

        return collection.stream()
                .map(element -> convertValue(element, elementType))
                .toList();
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key, final Class<T> elementType) {
        final List<Object> values = redisTemplate.opsForList().range(key, 0, -1);
        if (values == null) {
            return List.of();
        }
        return values.stream().map(elementType::cast).toList();
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public BoundSetOperations<String, Object> setCacheSet(final String key, final Set<?> dataSet) {
        BoundSetOperations<String, Object> operation = redisTemplate.boundSetOps(key);
        if (dataSet != null && !dataSet.isEmpty()) {
            operation.add(dataSet.toArray());
        }
        return operation;
    }

    /**
     * 获得缓存的set
     *
     * @param key key
     * @return Set
     */
    public Set<?> getCacheSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key     键
     * @param dataMap dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap, final Duration ttl) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
            redisTemplate.expire(key, ttl);
        }
    }

    /**
     * 缓存Map
     *
     * @param key     键
     * @param dataMap dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }


    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获得缓存的Map
     *
     * @param key 键
     * @return Map
     */
    public <T> Map<String, T> getCacheMap(final String key) {
        return redisTemplate.<String, T>opsForHash().entries(key);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public Object getCacheMapValue(String key, String hKey) {
        return redisTemplate.opsForHash().get(key, hKey);
    }

    public <T> T getCacheMapValue(String key, String hKey, Class<T> targetType) {
        Object value = getCacheMapValue(key, hKey);
        return convertValue(value, targetType);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<String> hKeys) {
        return redisTemplate.<String, T>opsForHash().multiGet(key, hKeys);
    }

    /**
     * 删除Hash中的某条数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return 是否成功
     */
    public boolean deleteCacheMapValue(final String key, final String hKey) {
        return redisTemplate.opsForHash().delete(key, hKey) > 0;
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        return keys == null ? Set.of() : keys;
    }

    /**
     * 执行 Redis Lua 脚本。
     *
     * @param script Redis脚本
     * @param keys   脚本使用的Redis键，对应KEYS参数
     * @param args   脚本参数，对应ARGV参数
     * @param <T>    脚本返回类型
     * @return 脚本执行结果
     */
    public <T> @Nullable T execute(RedisScript<T> script, List<String> keys, Object... args) {

        return redisTemplate.execute(script, keys, args);
    }

    private <T> T convertValue(Object value, Class<T> targetType) {
        if (value == null) {
            return null;
        }

        if (targetType.isInstance(value)) {
            return targetType.cast(value);
        }

        return objectMapper.convertValue(value, targetType);
    }
}
