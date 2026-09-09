package dev.geo.admin.system.service.auth;

import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.constant.UserConstants;
import dev.geo.admin.common.exception.user.BlackListException;
import dev.geo.admin.common.exception.user.UserNotExistsException;
import dev.geo.admin.common.exception.user.UserPasswordNotMatchException;
import dev.geo.admin.common.utils.I18nMessageUtil;
import dev.geo.admin.common.utils.ip.IpUtils;
import dev.geo.admin.security.event.LoginAuditEvent;
import dev.geo.admin.security.model.LoginPrincipal;
import dev.geo.admin.security.model.TokenResponse;
import dev.geo.admin.security.token.JwtTokenService;
import dev.geo.admin.system.service.system.ISysConfigService;
import dev.geo.admin.system.service.system.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

/**
 * 认证业务服务。
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;
    private final I18nMessageUtil messages;
    private final ISysConfigService configService;
    private final ISysUserService userService;
    private final UserPasswordService passwordService;
    private final CaptchaService captchaService;

    public TokenResponse login(String username, String password, String captcha, String uuid, boolean rememberMe) {
        captchaService.validateCaptcha(username, captcha, uuid);
        loginPreCheck(username, password);
        passwordService.checkPasswordRetry(username);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(username, password)
            );
            LoginPrincipal principal = (LoginPrincipal) authentication.getPrincipal();
            if (principal == null) {
                throw new IllegalStateException("登录用户信息不能为空");
            }
            passwordService.clearPasswordRetry(username);
            eventPublisher.publishEvent(LoginAuditEvent.success(username, messages.get("user.login.success")));
            userService.updateLoginLog(principal.getUserId(), principal.getIp(), Instant.now());

            return tokenService.create(principal, rememberMe);
        } catch (BadCredentialsException exception) {
            passwordService.recordPasswordFailure(username);
            eventPublisher.publishEvent(LoginAuditEvent.failure(username, messages.get("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        } catch (AuthenticationException exception) {
            eventPublisher.publishEvent(LoginAuditEvent.failure(username, exception.getMessage()));
            throw exception;
        }
    }

    public TokenResponse refresh(String refreshToken) {
        return tokenService.refresh(refreshToken);
    }

    public void logout(String sessionId) {
        tokenService.revoke(sessionId);
    }

    private void loginPreCheck(String username, String password) {
        // 用户名或密码为空 错误
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            eventPublisher.publishEvent(LoginAuditEvent.failure(username, messages.get("not.null")));
            throw new UserNotExistsException();
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            eventPublisher.publishEvent(LoginAuditEvent.failure(username, messages.get("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            eventPublisher.publishEvent(LoginAuditEvent.failure(username, messages.get("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        // IP黑名单校验
        String blockedIps = configService.selectConfigByKey("sys.login.blackIPList");
        if (IpUtils.isMatchedIp(blockedIps, IpUtils.getIp())) {
            eventPublisher.publishEvent(LoginAuditEvent.failure(username, messages.get("login.blocked")));
            throw new BlackListException();
        }
    }
    public void logoutByRefreshToken(String refreshToken) {
        tokenService.revokeByRefreshToken(refreshToken);
    }
}
