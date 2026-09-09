package dev.geo.admin.system.model.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "图形验证码响应")
public record CaptchaVO(

        @Schema(description = "是否启用验证码")
        boolean enabled,

        @Schema(description = "验证码唯一标识")
        String uuid,

        @Schema(description = "Base64 图片")
        String image

) {

    public static CaptchaVO disabled() {
        return new CaptchaVO(false, null, null);
    }

    public static CaptchaVO enabled(String uuid, String image) {
        return new CaptchaVO(true, uuid, image);
    }
}
