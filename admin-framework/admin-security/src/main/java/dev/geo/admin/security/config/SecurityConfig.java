package dev.geo.admin.security.config;


import dev.geo.admin.security.config.handle.RestAccessDeniedHandler;
import dev.geo.admin.security.config.properties.JwtProperties;
import dev.geo.admin.security.config.properties.PermitAllUrlProperties;
import dev.geo.admin.security.config.converter.RedisSessionJwtAuthenticationConverter;
import dev.geo.admin.security.config.handle.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

/**
 * spring security配置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {
    /**
     * 允许匿名访问的地址
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpess        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    private final PermitAllUrlProperties permitAllUrl;
    private final RedisSessionJwtAuthenticationConverter converter;

    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;


    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            CorsConfigurationSource corsConfigurationSource,
            JwtDecoder jwtDecoder
    ) {
        return httpSecurity
                // CSRF禁用，因为不使用session
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // 禁用HTTP响应标头
                .headers((headersCustomizer) -> {
                    headersCustomizer
                            .cacheControl(HeadersConfigurer.CacheControlConfig::disable)
                            .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin);
                })
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // 基于token，所以不需要session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 注解标记允许匿名访问的url
                .authorizeHttpRequests(authorize -> {
                    permitAllUrl.getUrls().forEach(url -> authorize.requestMatchers(url).permitAll());
                    // 对于登录login 注册register 验证码captchaImage 允许匿名访问
                    authorize
                            .requestMatchers(
                                    "/auth/login",
                                    "/auth/refresh",
                                    "/auth/logout",
                                    "/register",
                                    "/captcha")
                            .permitAll()
                            // 静态资源，可匿名访问
                            .requestMatchers(
                                    HttpMethod.GET,
                                    "/",
                                    "/*.html",
                                    "/**.html",
                                    "/**.css",
                                    "/**.js",
                                    "/profile/**")
                            .permitAll()
                            .requestMatchers(
                                    "/api",
                                    "/v3/api-docs/**",
                                    "/druid/**")
                            .permitAll()
                            // 除上面外的所有请求全部需要鉴权认证
                            .anyRequest()
                            .authenticated();
                })
                .oauth2ResourceServer(resourceServer ->
                        resourceServer
                                .authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler)
                                .jwt(jwt -> jwt
                                        .jwtAuthenticationConverter(converter)
                                        .decoder(jwtDecoder)
                                )
                )
                .build();

    }


    /**
     * 身份验证实现
     */
    @Bean
    AuthenticationManager authenticationManager(DaoAuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * 加密实现
     * {bcrypt}...  → BCryptPasswordEncoder
     * {pbkdf2}...  → Pbkdf2PasswordEncoder
     * {scrypt}...  → SCryptPasswordEncoder
     * {noop}...    → NoOpPasswordEncoder
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        DelegatingPasswordEncoder encoder = (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
        encoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
        return encoder;
    }


    @Bean
    JwtEncoder jwtEncoder(JwtProperties properties) {
        return NimbusJwtEncoder.withSecretKey(secretKey(properties))
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    JwtDecoder jwtDecoder(JwtProperties properties) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(secretKey(properties))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        OAuth2TokenValidator<@NonNull Jwt> defaultValidator = JwtValidators.createDefaultWithIssuer(properties.issuer());

        OAuth2TokenValidator<@NonNull Jwt> audienceValidator = new JwtClaimValidator<Collection<String>>(
                JwtClaimNames.AUD,
                audience -> audience != null && audience.contains(properties.audience()));

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(defaultValidator, audienceValidator));

        return decoder;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${security.cors.allowed-origins:http://localhost:5173}")
            List<String> allowedOrigins) {
        CorsConfiguration cors = new CorsConfiguration();

        cors.setAllowedOrigins(allowedOrigins);

        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        cors.setAllowedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE, "X-Requested-With"));

        cors.setExposedHeaders(List.of(HttpHeaders.WWW_AUTHENTICATE));

        cors.setAllowCredentials(true);

        cors.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", cors);

        return source;
    }

    private SecretKey secretKey(JwtProperties properties) {
        byte[] keyBytes;

        try {
            keyBytes = Base64.getDecoder().decode(properties.secret());
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("security.jwt.secret 必须是 Base64", exception);
        }

        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT 密钥解码后不得少于 32 字节");
        }

        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

}
