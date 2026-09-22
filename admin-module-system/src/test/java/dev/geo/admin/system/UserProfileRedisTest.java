package dev.geo.admin.system;

import dev.geo.admin.common.constant.CacheConstants;
import dev.geo.admin.redis.RedisCache;
import dev.geo.admin.redis.config.RedisConfig;
import dev.geo.admin.security.model.LoginSession;
import dev.geo.admin.security.model.LoginUserInfo;
import dev.geo.admin.security.session.impl.RedisLoginSessionStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** 显式启用的本地 Redis 集成测试，仅操作随机生成的测试键。 */
@EnabledIfEnvironmentVariable(named = "PROFILE_REDIS_TEST", matches = "true")
class UserProfileRedisTest {
    @Test
    void profileUpdatePreservesRefreshHashAndExpiryAndDoesNotRecreateDeletedSession() {
        var configuration = new RedisStandaloneConfiguration("127.0.0.1", 6379);
        String password = System.getenv("PROFILE_REDIS_PASSWORD");
        if (password != null && !password.isBlank()) configuration.setPassword(password);
        var factory = new LettuceConnectionFactory(configuration);
        factory.afterPropertiesSet();
        try {
            var mapper = JsonMapper.builder().findAndAddModules().build();
            var template = new RedisConfig().redisTemplate(factory, mapper);
            template.afterPropertiesSet();
            var store = new RedisLoginSessionStore(new RedisCache(template, mapper), event -> {});
            String id = "profile-test-" + UUID.randomUUID();
            String key = CacheConstants.LOGIN_TOKEN_KEY + id;
            var info = new LoginUserInfo(-1L, "profile-test", "old", "old@example.com", "",
                    null, "127.0.0.1", "", "", "", null, "", Set.of(), Set.of("profile:read"));
            var session = new LoginSession(id, info, Instant.now(), Instant.now().plusSeconds(60), false);
            try {
                store.create(session, "initial-hash", Duration.ofSeconds(60));
                long ttlBefore = template.getExpire(key);
                assertTrue(store.rotateRefreshToken(id, "initial-hash", "rotated-hash"));
                var updated = info.withProfile("new", "new@example.com", "/profile/avatar/test.png");
                store.updateUserInfo(id, updated);
                var saved = store.find(id).orElseThrow();
                assertEquals(updated, saved.userInfo());
                assertEquals(session.loginAt(), saved.loginAt());
                assertEquals(session.expiresAt(), saved.expiresAt());
                assertEquals("rotated-hash", template.opsForHash().get(key, "refreshHash"));
                long ttlAfter = template.getExpire(key);
                assertTrue(ttlAfter > 0 && ttlAfter <= ttlBefore);
                assertTrue(store.rotateRefreshToken(id, "rotated-hash", "next-hash"));
                store.delete(id);
                store.updateUserInfo(id, updated);
                assertFalse(template.hasKey(key));
            } finally {
                template.delete(key);
            }
        } finally {
            factory.destroy();
        }
    }
}
