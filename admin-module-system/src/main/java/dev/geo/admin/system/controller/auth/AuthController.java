package dev.geo.admin.system.controller.auth;

import cn.hutool.core.convert.Convert;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.security.model.LoginPrincipal;
import dev.geo.admin.security.model.TokenResponse;
import dev.geo.admin.security.utils.SecurityUtils;
import dev.geo.admin.system.model.auth.dto.LoginRequest;
import dev.geo.admin.system.model.auth.vo.UserInfoVO;
import dev.geo.admin.system.model.system.entity.SysMenu;
import dev.geo.admin.system.model.system.vo.RouterVo;
import dev.geo.admin.system.service.auth.AuthService;
import dev.geo.admin.system.service.system.ISysConfigService;
import dev.geo.admin.system.service.system.ISysMenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final String REFRESH_COOKIE_NAME = "refresh_token";

    @Value("${security.cookie.secure:true}")
    private boolean cookieSecure;
    @Value("${security.cookie.same-site:Strict}")
    private String cookieSameSite;
    @Value("${security.cookie.path:/}")
    private String cookiePath;

    private final AuthService authService;
    private final ISysConfigService configService;
    private final ISysMenuService menuService;

    @PostMapping("/login")
    public ResponseEntity<ApiResult<Map<String, Object>>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse token = authService.login(
                request.username(),
                request.password(),
                request.code(),
                request.uuid(),
                request.rememberMe()
        );
        return buildTokenResponse(token);

    }


    @PostMapping("/refresh")
    public ResponseEntity<ApiResult<Map<String, Object>>> refresh(
            @CookieValue(
                    name = REFRESH_COOKIE_NAME,
                    required = false
            ) String refreshToken
    ) {
        TokenResponse token = authService.refresh(refreshToken);
        return buildTokenResponse(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResult<Void>> logout(
            @CookieValue(
                    name = REFRESH_COOKIE_NAME,
                    required = false
            ) String refreshToken) {
        authService.logoutByRefreshToken(refreshToken);
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        clearRefreshCookie().toString()
                )
                .cacheControl(CacheControl.noStore())
                .body(ApiResult.success());
    }

    /**
     * 获取当前用户、角色和权限信息。
     */
    @GetMapping("/getInfo")
    public ApiResult<UserInfoVO> getInfo() {
        LoginPrincipal principal = SecurityUtils.getLoginPrincipal();
        String passwordCharRange = configService.selectConfigByKey("sys.account.passwordCharRange");
        UserInfoVO result = new UserInfoVO(
                principal.getUserId(),
                principal.getUsername(),
                principal.getNickName(),
                principal.getAvatar(),
                principal.getPermissions(),
                principal.getRolesKey(),
                passwordCharRange,
                initPasswordIsModify(principal.getPasswordUpdateTime()),
                passwordIsExpiration(principal.getPasswordUpdateTime())
        );
        return ApiResult.success(result);
    }

    /**
     * 获取当前用户可访问的前端路由。
     */
    @GetMapping("/getRouters")
    public ApiResult<List<RouterVo>> getRouters() {
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(SecurityUtils.getUserId());
        return ApiResult.success(menuService.buildMenus(menus));
    }

    private ResponseEntity<ApiResult<Map<String, Object>>> buildTokenResponse(TokenResponse token) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("tokenType", token.tokenType());
        response.put("accessToken", token.accessToken());
        response.put("expiresIn", token.expiresIn());
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        buildRefreshCookie(token).toString()
                )
                .cacheControl(CacheControl.noStore())
                .body(ApiResult.success(response));

    }

    private ResponseCookie buildRefreshCookie(TokenResponse token) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
                .from(REFRESH_COOKIE_NAME, token.refreshToken())
                .httpOnly(true)
                .secure(cookieSecure) //生产环境必须为 true
                .sameSite(cookieSameSite)
                .path(cookiePath);

        // rememberMe=false 时不设置 Max-Age， 浏览器关闭后 Cookie 自动消失。
        if (token.rememberMe()) {
            builder.maxAge(Duration.ofSeconds(token.refreshExpiresIn()));
        }
        return builder.build();
    }

    private ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path(cookiePath)
                .maxAge(Duration.ZERO)
                .build();
    }

    // 检查初始密码是否提醒修改
    private boolean initPasswordIsModify(Instant pwdUpdateDate) {
        Integer initPasswordModify = Convert.toInt(
                configService.selectConfigByKey("sys.account.initPasswordModify")
        );
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    // 检查密码是否过期
    private boolean passwordIsExpiration(Instant pwdUpdateDate) {
        Integer validateDays = Convert.toInt(
                configService.selectConfigByKey("sys.account.passwordValidateDays")
        );
        if (validateDays == null || validateDays <= 0) {
            return false;
        }
        if (pwdUpdateDate == null) {
            return true;
        }
        return !pwdUpdateDate.plus(validateDays, ChronoUnit.DAYS).isAfter(Instant.now());
    }
}
