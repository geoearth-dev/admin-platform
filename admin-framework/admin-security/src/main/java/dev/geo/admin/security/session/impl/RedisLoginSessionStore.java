package dev.geo.admin.security.session.impl;

import dev.geo.admin.common.constant.CacheConstants;
import dev.geo.admin.redis.RedisCache;
import dev.geo.admin.security.model.LoginSession;
import dev.geo.admin.security.session.LoginSessionStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Component
@RequiredArgsConstructor
public class RedisLoginSessionStore implements LoginSessionStore {
    private final RedisCache redisCache;

    @Override
    public void create(LoginSession session, String refreshTokenHash, Duration ttl) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("refreshHash", refreshTokenHash);
        values.put("session", session);
        String redisKey = key(session.sessionId());
        redisCache.setCacheMap(redisKey, values, ttl);
    }

    @Override
    public Optional<LoginSession> find(String sessionId) {
        LoginSession session = redisCache.getCacheMapValue(key(sessionId), "session", LoginSession.class);
        if (session == null) {
            return Optional.empty();
        }
        return Optional.of(session);
    }

    @Override
    public List<LoginSession> findAll() {
        Collection<String> keys = redisCache.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
        if (keys == null || keys.isEmpty()) {
            return List.of();
        }
        return keys.stream()
                .map(key -> redisCache.<LoginSession>getCacheMapValue(key, "session", LoginSession.class))
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 让 Refresh Token 使用一次后立即作废，并替换成一个新的 Refresh Token。
     * 防止 Refresh Token 被重复使用
     */
    @Override
    public boolean rotateRefreshToken(String sessionId, String expectedHash, String newHash) {
        DefaultRedisScript<Long> rotateRefreshScript = new DefaultRedisScript<>("""
                local current = redis.call(
                    'HGET', KEYS[1], 'refreshHash'
                )
                if current == ARGV[1] then
                    redis.call(
                        'HSET',
                        KEYS[1],
                        'refreshHash',
                        ARGV[2]
                    )
                    return 1
                end
                
                return 0
                """, Long.class);
        Long result = redisCache.execute(
                rotateRefreshScript,
                List.of(key(sessionId)),
                expectedHash,
                newHash
        );
        return Objects.equals(1L, result);
    }

    @Override
    public void delete(String sessionId) {
        redisCache.deleteObject(key(sessionId));
    }

    private String key(String sessionId) {
        return CacheConstants.LOGIN_TOKEN_KEY + sessionId;
    }

}
