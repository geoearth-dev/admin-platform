package dev.geo.admin.security.utils.captcha;

import dev.geo.admin.security.utils.captcha.entity.CaptchaResult;
import dev.geo.admin.security.utils.captcha.enums.CaptchaType;
import dev.geo.admin.security.utils.captcha.generator.CaptchaCharGenerator;
import dev.geo.admin.security.utils.captcha.generator.CaptchaMathGenerator;

public final class CaptchaUtil {

    public static CaptchaResult generate() {
        return generate(CaptchaType.CHAR);
    }

    /**
     * 根据类型生成验证码。
     *
     * @param type 验证码类型
     * @return 验证码图片和答案
     */
    public static CaptchaResult generate(CaptchaType type) {
        if (type == null) {
            throw new IllegalArgumentException("验证码类型不能为空");
        }
        return switch (type) {
            case CHAR -> CaptchaCharGenerator.generate();
            case MATH -> CaptchaMathGenerator.generate();
        };
    }
}
