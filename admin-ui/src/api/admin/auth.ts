import type { CaptchaInfo } from '@/types/base/api/login';
import { requestClient } from '@/utils/request';

/** 登录接口参数 */
export interface LoginParams {
  password: string;
  username: string;
  code: string;
  uuid: string;
  rememberMe: boolean;
}

/** 登录接口返回值 */
export interface LoginResult {
  accessToken: string;
  expiresIn: number;
  tokenType: 'Bearer';
}

/** 登录用户信息返回值 */
export interface LoginInfo {
  userId: number;
  username: string;
  nickName?: string;
  avatar?: string;
  permissions: string[];
  roles: string[];
  passwordCharRange?: string;
  isDefaultModifyPwd?: boolean;
  isPasswordExpired?: boolean;
}

// 获取验证码
export function getCodeImg(): Promise<CaptchaInfo> {
  return requestClient.get('/captcha', undefined, {
    noAuth: true,
    silent: true,
  });
}
/**
 * 登录
 */
export async function loginApi(data: LoginParams) {
  return requestClient.post<LoginResult>('/auth/login', data, {
    noAuth: true,
  });
}
/**
 * 获取登录用户信息
 */
export async function getUserInfoApi() {
  return requestClient.get<LoginInfo>('/auth/getInfo');
}

/**
 * 刷新accessToken
 */
export async function refreshTokenApi() {
  return requestClient.post<LoginResult>('/auth/refresh', undefined, {
    noAuth: true,
    silent: true,
  });
}

/**
 * 退出登录
 */
export async function logoutApi() {
  return requestClient.post('/auth/logout', undefined, {
    noAuth: true,
    silent: true,
  });
}
