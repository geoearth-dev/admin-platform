package dev.geo.admin.security.model;

import java.time.Instant;

public record LoginSession(
        String sessionId,
        LoginUserInfo userInfo,
        Instant loginAt,
        Instant expiresAt,
        boolean rememberMe
) {
}
