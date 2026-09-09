package dev.geo.admin.security.config;

import dev.geo.admin.security.authentication.SecurityExpression;
import dev.geo.admin.security.config.properties.PermitAllUrlProperties;
import dev.geo.admin.security.session.impl.RedisLoginSessionStore;
import dev.geo.admin.security.token.JwtTokenService;
import dev.geo.admin.security.config.converter.RedisSessionJwtAuthenticationConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
        SecurityConfig.class,
        SecurityExpression.class,
        PermitAllUrlProperties.class,
        RedisSessionJwtAuthenticationConverter.class,
        RedisLoginSessionStore.class,
        JwtTokenService.class
})
public class SecurityAutoConfig {
}
