package dev.geo.admin.system.service.auth;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.config.AppConfig;
import dev.geo.admin.common.constant.CacheConstants;
import dev.geo.admin.common.exception.user.CaptchaException;
import dev.geo.admin.common.exception.user.CaptchaExpireException;
import dev.geo.admin.common.utils.I18nMessageUtil;
import dev.geo.admin.redis.RedisCache;
import dev.geo.admin.security.event.LoginAuditEvent;
import dev.geo.admin.security.utils.captcha.CaptchaUtil;
import dev.geo.admin.security.utils.captcha.entity.CaptchaResult;
import dev.geo.admin.security.utils.captcha.enums.CaptchaType;
import dev.geo.admin.system.model.auth.vo.CaptchaVO;
import dev.geo.admin.system.service.system.ISysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final ApplicationEventPublisher eventPublisher;

    private final I18nMessageUtil messages;
    private final RedisCache redisCache;
    private final ISysConfigService configService;

    /**
     * 生成图形验证码。
     */
    public CaptchaVO generate() {
        if (!configService.selectCaptchaEnabled()) {
            return CaptchaVO.disabled();
        }
        // 保存验证码信息
        String uuid = IdUtil.simpleUUID();
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;

        // 生成验证码
        String captchaType = AppConfig.getCaptchaType();

        CaptchaType type;

        try {
            type = CaptchaType.valueOf(captchaType.trim().toUpperCase());
        } catch (Exception exception) {
            type = CaptchaType.MATH;
        }

        CaptchaResult result = CaptchaUtil.generate(type);
        String code = result.answer();
        String base64Image = result.image();

        redisCache.setCacheObject(verifyKey, code, Duration.ofMinutes(2));

        return CaptchaVO.enabled(uuid, base64Image);
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    public void validateCaptcha(String username, String code, String uuid) {
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled) {
            String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StrUtil.emptyIfNull(uuid);
            String captcha = redisCache.getCacheObject(verifyKey, String.class);
            if (captcha == null) {
                eventPublisher.publishEvent(LoginAuditEvent.failure(username, messages.get("user.jcaptcha.expire")));
                throw new CaptchaExpireException();
            }
            redisCache.deleteObject(verifyKey);
            if (!code.equalsIgnoreCase(captcha)) {
                eventPublisher.publishEvent(LoginAuditEvent.failure(username, messages.get("user.jcaptcha.error")));
                throw new CaptchaException();
            }
        }
    }
}
