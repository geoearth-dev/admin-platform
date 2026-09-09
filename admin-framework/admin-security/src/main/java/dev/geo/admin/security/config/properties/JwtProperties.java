package dev.geo.admin.security.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        @NotBlank String issuer,
        @NotBlank String audience,
        @NotBlank String secret,
        @NotNull Duration accessTokenTtl,
        @NotNull Duration refreshTokenTtl,
        @NotNull Duration rememberRefreshTokenTtl
) {
    public JwtProperties {
        validateDuration(accessTokenTtl, "accessTokenTtl");
        validateDuration(refreshTokenTtl, "refreshTokenTtl");
        validateDuration(rememberRefreshTokenTtl, "rememberRefreshTokenTtl");
    }
    private static void validateDuration(Duration duration, String name) {
        if (duration != null && (duration.isZero() || duration.isNegative())) {
            throw new IllegalArgumentException(name + " 必须大于零");
        }
    }
}