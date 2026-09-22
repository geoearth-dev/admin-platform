package dev.geo.admin.security.session;

import dev.geo.admin.security.model.LoginSession;
import dev.geo.admin.security.model.LoginUserInfo;

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

    /** 更新已有会话的用户资料，保留过期时间和刷新令牌，不重建已注销的会话。 */
    void updateUserInfo(String sessionId, LoginUserInfo userInfo);

    boolean rotateRefreshToken(String sessionId, String expectedHash, String newHash);

    default void delete(String sessionId) {
        delete(sessionId, false);
    }

    /** 撤销会话，并通知在线连接；forced 表示管理员强制下线。 */
    void delete(String sessionId, boolean forced);
}
