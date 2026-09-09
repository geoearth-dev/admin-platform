package dev.geo.admin.security.token;

import cn.hutool.core.util.IdUtil;
import dev.geo.admin.common.constant.Constants;
import dev.geo.admin.common.utils.ServletUtils;
import dev.geo.admin.common.utils.ip.IpUtils;
import dev.geo.admin.security.config.properties.JwtProperties;
import dev.geo.admin.security.model.LoginPrincipal;
import dev.geo.admin.security.model.LoginSession;
import dev.geo.admin.security.model.TokenResponse;
import dev.geo.admin.security.session.LoginSessionStore;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.config.Elements.JWT;

@Service
public class JwtTokenService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final JwtEncoder jwtEncoder;
    private final JwtProperties properties;
    private final LoginSessionStore sessionStore;
    private final Clock clock;

    public JwtTokenService(
            JwtEncoder jwtEncoder,
            JwtProperties properties,
            LoginSessionStore sessionStore
    ) {
        this.jwtEncoder = jwtEncoder;
        this.properties = properties;
        this.sessionStore = sessionStore;
        this.clock = Clock.systemUTC();
    }

    /**
     * 登录时创建 Access Token 和 Refresh Session。
     */
    public TokenResponse create(LoginPrincipal user, boolean rememberMe) {
        Instant now = clock.instant();

        String sessionId = IdUtil.fastUUID();

        String refreshToken = newRefreshToken(sessionId);
        Duration refreshTtl = rememberMe ? properties.rememberRefreshTokenTtl() : properties.refreshTokenTtl();
        LoginSession session = new LoginSession(
                sessionId,
                user.getUserInfo(),
                now,
                now.plus(refreshTtl),
                rememberMe
        );

        sessionStore.create(session, hash(refreshToken), refreshTtl);

        return createResponse(session, refreshToken, now);
    }

    public TokenResponse refresh(String refreshToken) {
        String sessionId = parseSessionId(refreshToken);

        LoginSession session = sessionStore.find(sessionId).orElseThrow(() ->
                new InvalidBearerTokenException("刷新令牌无效或已过期")
        );

        Instant now = clock.instant();

        if (!session.expiresAt().isAfter(now)) {
            sessionStore.delete(sessionId);
            throw new InvalidBearerTokenException("刷新令牌已过期");
        }

        String newRefreshToken = newRefreshToken(sessionId);

        boolean rotated = sessionStore.rotateRefreshToken(sessionId, hash(refreshToken), hash(newRefreshToken));

        if (!rotated) {
            // 旧 Refresh Token 被再次使用，可能发生泄露。
            sessionStore.delete(sessionId);

            throw new InvalidBearerTokenException("检测到刷新令牌重复使用，会话已失效");
        }

        return createResponse(session, newRefreshToken, now);
    }

    /**
     * 根据 Session ID 撤销会话。
     */
    public void revoke(String sessionId) {
        if (sessionId != null && !sessionId.isBlank()) {
            sessionStore.delete(sessionId);
        }
    }

    /**
     * 根据 Refresh Token 撤销会话。
     */
    public void revokeByRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        try {
            revoke(parseSessionId(refreshToken));
        } catch (InvalidBearerTokenException ignored) {
            /*
             * 登出接口保持幂等。
             * Refresh Token 无效时，仍然允许客户端清除 Cookie。
             */
        }
    }

    private TokenResponse createResponse(LoginSession session, String refreshToken, Instant issuedAt) {
        Instant expiresAt = issuedAt.plus(properties.accessTokenTtl());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .subject(session.userInfo().username())
                .audience(List.of(properties.audience()))
                .issuedAt(issuedAt)
            .expiresAt(expiresAt)
                .id(session.sessionId())
                .claim(Constants.JWT_USERID, session.userInfo().userId())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type(JWT).build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        long refreshExpiresIn = Duration.between(issuedAt, session.expiresAt()).toSeconds();
        return TokenResponse.bearer(
                accessToken,
                properties.accessTokenTtl().toSeconds(),
                refreshToken,
                refreshExpiresIn,
                session.rememberMe()
        );
    }

    private String newRefreshToken(String sessionId) {
        byte[] randomBytes = new byte[32];

        SECURE_RANDOM.nextBytes(randomBytes);

        String secret = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        return sessionId + "." + secret;
    }

    private String parseSessionId(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidBearerTokenException("刷新令牌不能为空");
        }

        int separator = token.indexOf('.');

        if (separator <= 0 || separator == token.length() - 1) {
            throw new InvalidBearerTokenException("刷新令牌格式错误");
        }

        try {
            return UUID.fromString(token.substring(0, separator)).toString();
        } catch (IllegalArgumentException exception) {
            throw new InvalidBearerTokenException("刷新令牌格式错误");
        }
    }

    private String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("当前 JDK 不支持 SHA-256", exception);
        }
    }

}
