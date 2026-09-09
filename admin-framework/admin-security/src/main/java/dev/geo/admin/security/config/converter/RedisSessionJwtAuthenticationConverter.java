package dev.geo.admin.security.config.converter;

import dev.geo.admin.security.model.LoginPrincipal;
import dev.geo.admin.security.model.LoginSession;
import dev.geo.admin.security.session.LoginSessionStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * JWT 转换为 Spring Security 用户
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSessionJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final LoginSessionStore sessionStore;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String sessionId = jwt.getId();
        if (sessionId == null || sessionId.isBlank()) {
            throw new InvalidBearerTokenException("JWT 缺少 jti");
        }
        LoginSession session = sessionStore.find(sessionId).orElseThrow(() -> {
            log.warn("JWT 对应的 Redis 登录会话不存在: jti={}", sessionId);
            return new InvalidBearerTokenException("登录会话已注销或过期");
        });
        if (!session.userInfo().username().equals(jwt.getSubject())) {
            throw new InvalidBearerTokenException("JWT 与 Redis 登录会话不匹配");
        }
        LoginPrincipal principal = LoginPrincipal.fromSession(session);
        return new JwtAuthenticationToken(jwt, principal, principal.getAuthorities());
    }
}
