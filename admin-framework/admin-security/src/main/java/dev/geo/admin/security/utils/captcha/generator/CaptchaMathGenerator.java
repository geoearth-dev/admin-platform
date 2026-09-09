package dev.geo.admin.security.utils.captcha.generator;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.core.math.Calculator;
import dev.geo.admin.security.utils.captcha.entity.CaptchaResult;

public final class CaptchaMathGenerator {
    private static final int CAPTCHA_WIDTH = 160;
    private static final int CAPTCHA_HEIGHT = 60;
    private static final int CAPTCHA_LENGTH = 1;
    private static final int SHEAR_THICKNESS = 2;

    /**
     * 生成加数学计算验证码。
     */
    public static CaptchaResult generate() {

        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(
                CAPTCHA_WIDTH,
                CAPTCHA_HEIGHT,
                CAPTCHA_LENGTH,
                SHEAR_THICKNESS
        );

        captcha.setGenerator(new MathGenerator(CAPTCHA_LENGTH,false));
        captcha.createCode();
        String expression = captcha.getCode();
        int answer = (int) Calculator.conversion(expression.replace("=", ""));
        return new CaptchaResult(String.valueOf(answer), captcha.getImageBase64());
    }

}
