package dev.geo.admin.security.model;

/**
 * 返回给前端 DTO
 *
 * @param tokenType
 * @param accessToken
 * @param refreshToken
 * @param expiresIn
 */
public record TokenResponse(
        String tokenType,
        String accessToken,
        // Access Token 剩余秒数。
        long expiresIn,
        String refreshToken,
        // Refresh Session 剩余秒数。
        long refreshExpiresIn,
        // 是否生成持久化 Cookie。
        boolean rememberMe
) {
    public static TokenResponse bearer(
            String accessToken,
            long expiresIn,
            String refreshToken,
            long refreshExpiresIn,
            boolean rememberMe
    ) {
        return new TokenResponse("Bearer",
                accessToken,
                expiresIn,
                refreshToken,
                refreshExpiresIn,
                rememberMe
        );
    }
}