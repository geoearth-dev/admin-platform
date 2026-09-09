package dev.geo.admin.common.exception.user;

/**
 * 验证码错误异常类
 */
public class CaptchaException extends UserException {

    public CaptchaException() {
        super("user.jcaptcha.error", null);
    }
}
