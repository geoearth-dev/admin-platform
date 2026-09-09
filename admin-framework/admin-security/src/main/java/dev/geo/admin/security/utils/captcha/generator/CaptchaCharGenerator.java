package dev.geo.admin.security.utils.captcha.generator;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import dev.geo.admin.security.utils.captcha.entity.CaptchaResult;

public final class CaptchaCharGenerator {
    /**
     * 排除0、O、1、I、L等容易混淆的字符。
     */
    private static final String CAPTCHA_CHARACTERS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    private static final int CAPTCHA_WIDTH = 160;
    private static final int CAPTCHA_HEIGHT = 60;
    private static final int CAPTCHA_LENGTH = 4;
    private static final int INTERFERENCE_COUNT = 80;

    /**
     * 生成字符验证码。
     */
    public static CaptchaResult generate() {
        RandomGenerator generator = new RandomGenerator(CAPTCHA_CHARACTERS, CAPTCHA_LENGTH);

        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(
                CAPTCHA_WIDTH,
                CAPTCHA_HEIGHT,
                CAPTCHA_LENGTH,
                INTERFERENCE_COUNT
        );

        captcha.setGenerator(generator);
        captcha.createCode();
        return new CaptchaResult(captcha.getCode(), captcha.getImageBase64());
    }

}
