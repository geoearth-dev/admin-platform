import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';

import { ElMessage, ElNotification } from 'element-plus';
import { defineStore } from 'pinia';
import { useAccessStore } from './access';
import { useUserStore } from './user';
import { $t } from '@/plugins/locale';

import { LOGIN_PATH } from '@/constants/core';
import { preferences } from '@/plugins/preference';
import {
  getUserInfoApi,
  loginApi,
  logoutApi,
  refreshTokenApi,
  type LoginParams,
} from '@/api/admin/auth/auth';
import { requestClient } from '@/utils/request';
import { resetAccessRoutes } from '@/router/access';

/**
 * @zh_CN 登录、会话权限相关
 */
export const useAuthStore = defineStore('auth', () => {
  const accessStore = useAccessStore();
  const userStore = useUserStore();
  const router = useRouter();

  const loginLoading = ref(false);
  /**
   * 是否已经执行过启动会话检查。
   *
   * 页面刷新后初始值为 false，路由守卫会调用 restoreSession()。
   */
  const sessionInitialized = ref(false);

  const isAuthenticated = computed(() => {
    return Boolean(accessStore.accessToken && userStore.userInfo);
  });

  requestClient.configureAuth({
    getToken: () => accessStore.accessToken,
    refreshToken: refreshAccessToken,
    onUnauthorized: handleSessionExpired,
  });
  /**
   * 异步处理登录操作
   * 完整登录状态必须同时包含：
   *  accessToken、userInfo、permissions
   * @param params 登录表单数据
   */
  async function authLogin(params: LoginParams, onSuccess?: () => Promise<void> | void) {
    if (loginLoading.value) {
      return null;
    }
    loginLoading.value = true;
    try {
      const result = await loginApi(params);
      if (!result.accessToken) {
        throw new Error('登录接口未返回 accessToken');
      }
      // 获取到 accessToken
      accessStore.setAccessToken(result.accessToken);
      accessStore.setIsAccessChecked(false);

      // 获取用户信息并存储到 accessStore 中
      const userInfo = await fetchUserInfo();

      // 登录已经成功，不需要路由守卫再次执行 refresh。
      sessionInitialized.value = true;

      if (accessStore.loginExpired) {
        accessStore.setLoginExpired(false);
      }

      if (onSuccess) {
        await onSuccess();
      } else {
        const redirect = router.currentRoute.value.query.redirect;
        const target = typeof redirect === 'string' ? redirect : preferences.app.defaultHomePath;
        await router.replace(target);
      }
      ElNotification({
        message: `${$t('authentication.loginSuccessDesc')}:${userInfo.nickName ?? userInfo.username}`,
        title: $t('authentication.loginSuccess'),
        type: 'success',
      });
    } catch (error) {
      clearSession();
      throw error;
    } finally {
      loginLoading.value = false;
    }
  }

  /**
   * 通过HttpCookie中的 RefreshToken  获取新的 AccessToken。
   *
   * 并发控制放在请求拦截器中，这里只负责调用接口和保存 Token。
   */
  async function refreshAccessToken() {
    const result = await refreshTokenApi();
    if (!result.accessToken) {
      throw new Error('刷新接口未返回 Access Token');
    }
    const newToken = result.accessToken;
    accessStore.setAccessToken(newToken);
    return newToken;
  }
  /**
   * 页面刷新时恢复登录状态。
   *
   * AccessToken不持久化，页面刷新会先调用refresh，然后再请求getInfo。
   */
  async function restoreSession() {
    if (sessionInitialized.value) {
      return isAuthenticated.value;
    }
    try {
      if (!accessStore.accessToken) {
        await refreshAccessToken();
      }
      if (!userStore.userInfo) {
        await fetchUserInfo();
      }
      return true;
    } catch {
      /*
       * 没有 Refresh Cookie、Cookie 已过期或 Redis 会话不存在，
       * 都视为当前没有登录。
       *
       * 启动检查失败，清理本地登录状态，避免出现不一致的情况。
       * 例如：本地有 AccessToken，但后端没有 RefreshToken，刷新页面后会出现 401。
       * 这种情况需要清理本地 AccessToken，避免出现不一致的情况。
       */
      clearSession();
      /*
       * 这里不需要跳转到登录页，路由守卫会处理。
       */
      return false;
    } finally {
      sessionInitialized.value = true;
    }
  }
  /**
   * 获取用户信息。
   * 更新permissions
   */
  async function fetchUserInfo() {
    const userInfo = await getUserInfoApi();
    userStore.setUserInfo(userInfo);
    accessStore.setPermissions(userInfo.permissions ?? []);
    return userInfo;
  }
  /**
   * AccessToken和RefreshToken都已经失效。
   *
   * 该方法由请求拦截器在 refresh 失败后调用。
   */
  async function handleSessionExpired(): Promise<void> {
    const currentRoute = router.currentRoute.value;
    const redirect = currentRoute.fullPath;

    clearSession();
    if (currentRoute.path !== LOGIN_PATH) {
      ElMessage.warning('登录状态已过期，请重新登录');
      await router.replace({
        path: LOGIN_PATH,
        query: {
          redirect,
        },
      });
    }
  }

  /**
   * 主动退出登录。
   */
  async function logout(redirect: boolean = true) {
    const currentRoute = router.currentRoute.value;
    const redirectPath = currentRoute.fullPath;
    try {
      await logoutApi();
    } catch (error) {
      console.warn('退出登录接口请求失败', error);
    } finally {
      clearSession();
      // 登录页带上当前路由地址
      await router.replace({
        path: LOGIN_PATH,
        query: redirect && currentRoute.path !== LOGIN_PATH ? { redirect: redirectPath } : {},
      });
    }
  }
  /**
   * 清理客户端认证和权限状态。
   */
  function clearSession(): void {
    resetAccessRoutes();
    userStore.$reset();
    accessStore.$reset();
    /*
     * 当前未登录状态，防止进入登录页后再次请求 refresh。
     */
    sessionInitialized.value = true;
  }
  function $reset() {
    loginLoading.value = false;
    sessionInitialized.value = false;
  }

  return {
    isAuthenticated,
    loginLoading,
    sessionInitialized,
    $reset,
    authLogin,
    clearSession,
    fetchUserInfo,
    handleSessionExpired,
    logout,
    refreshAccessToken,
    restoreSession,
  };
});
