package dev.geo.admin.security.utils.captcha.entity;

public record CaptchaResult(
        String answer,
        String image
) {
}