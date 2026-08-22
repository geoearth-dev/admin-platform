package dev.geoearth.admin.config.cache;

import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

/**
 * 单个 Redis 缓存区域的定义。
 *
 * @param name            缓存名称
 * @param ttl             有效时间
 * @param valueSerializer 值序列化器
 */
public record RedisCacheSpec(
        String name,
        Duration ttl,
        RedisSerializer<?> valueSerializer
) {
}