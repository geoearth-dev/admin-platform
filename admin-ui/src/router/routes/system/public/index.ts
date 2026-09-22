import { LOGIN_PATH } from '@/constants/core';
import { AuthPageLayout, BasicLayout } from '@/layout';
import { $t } from '@/plugins/locale';

import type { RouteRecordRaw } from 'vue-router'

/** 启动时注册的系统固定路由，包含根布局、登录页和 404。 */
const systemPublicRouter: RouteRecordRaw[] = [
  {
    path: '/',
    component: BasicLayout,
    name: 'Index',
    meta: { title: '首页', hideInBreadcrumb: true,hideInMenu:true },
    children: [],
  },
  {
    path: '/auth',
    redirect: LOGIN_PATH,
    component: AuthPageLayout,
    name: 'Authentication',
    meta: {
      title: 'Authentication',
      hideInTab: true,
       hideInMenu:true ,
      ignoreAccess: true,},
    children: [
      {
        name: 'Login',
        path: 'login',
        component: () => import('@/views/sys/admin/authentication/login.vue'),
        meta: { title: $t('page.auth.login') },
      },
        {
        name: 'CodeLogin',
        path: 'code-login',
        component: () => import('@/views/sys/admin/authentication/code-login.vue'),
        meta: {
          title: $t('page.auth.codeLogin'),
        },
      },
      {
        name: 'QrCodeLogin',
        path: 'qrcode-login',
        component: () =>   import('@/views/sys/admin/authentication/qrcode-login.vue'),
        meta: {
          title: $t('page.auth.qrcodeLogin'),
        },
      },
      {
        name: 'ForgetPassword',
        path: 'forget-password',
        component: () =>
          import('@/views/sys/admin/authentication/forget-password.vue'),
        meta: {
          title: $t('page.auth.forgetPassword'),
        },
      },
      {
        name: 'Register',
        path: 'register',
        component: () => import('@/views/sys/admin/authentication/register.vue'),
        meta: {
          title: $t('page.auth.register'),
        },
      },
    ],
  },
  //全局404页面
  {
    component: () => import('@/views/sys/admin/fallback/not-found.vue'),
    meta: {
      hideInBreadcrumb: true,
      hideInMenu: true,
      hideInTab: true,
      title: '404',
    },
    name: 'FallbackNotFound',
    path: '/:path(.*)*',
  },
]

export { systemPublicRouter }
