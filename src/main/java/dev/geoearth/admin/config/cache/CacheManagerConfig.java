package dev.geoearth.admin.config.cache;

import org.springframework.context.annotation.Configuration;

/**
 * redis配置
 */
@Configuration
//@EnableCaching
public class CacheManagerConfig {
    // Redis缓存配置
//    @Bean
//    public RedisCacheManagerBuilderCustomizer redisCacheManager() {
//
//        return builder -> {
//            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//                    .entryTtl(Duration.ofHours(24))// 默认过期时间1小时
//                    .disableCachingNullValues()  // 不缓存null值
//                    .computePrefixWith(cacheName -> cacheName)
//                    .serializeKeysWith(RedisSerializationContext.SerializationPair
//                            .fromSerializer(StringRedisSerializer.UTF_8))
//                    .serializeValuesWith(RedisSerializationContext.SerializationPair
//                            .fromSerializer(new JacksonJsonRedisSerializer<>(Object.class)));
//
//            // 针对不同缓存进行个性化配置
//            Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
//            cacheConfigurations.put(CacheConstants.SYS_DICT_KEY, config.entryTtl(Duration.ofMinutes(30)));
//            cacheConfigurations.put("user", config.entryTtl(Duration.ofHours(2)));
//            cacheConfigurations.put("orders", config.entryTtl(Duration.ofMinutes(10)));
//
//            builder.withInitialCacheConfigurations(cacheConfigurations).transactionAware().build();
//        };
//
//
//    }


    // 简单的ConcurrentMap缓存管理器
//    @Bean
//    public CacheManager cacheManager() {
//        SimpleCacheManager cacheManager = new SimpleCacheManager();
//        cacheManager.setCaches(Arrays.asList(
//                new ConcurrentMapCache("users"),
//                new ConcurrentMapCache("products"),
//                new ConcurrentMapCache("orders")
//        ));
//        return cacheManager;
//    }

    // Caffeine缓存配置（高性能本地缓存）
//    @Bean
//    public CacheManager caffeineCacheManager() {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
//        cacheManager.setCaffeine(Caffeine.newBuilder()
//                .initialCapacity(100)
//                .maximumSize(1000)
//                .expireAfterWrite(10, TimeUnit.MINUTES)
//                .weakKeys()
//                .recordStats());
//        return cacheManager;
//    }


}