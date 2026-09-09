package dev.geo.admin.security.session;

import dev.geo.admin.security.model.LoginSession;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface LoginSessionStore {
    void create(LoginSession session, String refreshTokenHash, Duration ttl);

    Optional<LoginSession> find(String sessionId);

    /**
     * 查询当前有效的登录会话。
     */
    List<LoginSession> findAll();

    boolean rotateRefreshToken(String sessionId, String expectedHash, String newHash);

    void delete(String sessionId);
}
