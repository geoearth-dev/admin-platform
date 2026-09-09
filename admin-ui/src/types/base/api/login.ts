/** 验证码响应 */
export interface CaptchaInfo {
  /** 验证码缓存key */
  uuid: string;
  /** 验证码图片Base64 */
  image: string;
  /** 验证码开关 */
  enabled: boolean;
}
