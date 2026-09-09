package dev.geo.admin.common.exception.user;

/**
 * 验证码失效异常类
 */
public class CaptchaExpireException extends UserException {
    public CaptchaExpireException() {
        super("user.jcaptcha.expire", null);
    }
}
