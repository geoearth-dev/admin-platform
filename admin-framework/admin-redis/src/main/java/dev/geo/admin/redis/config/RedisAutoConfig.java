package dev.geo.admin.redis.config;

import dev.geo.admin.redis.RedisCache;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
        RedisConfig.class,
        RedisCache.class
})
public class RedisAutoConfig {
}
