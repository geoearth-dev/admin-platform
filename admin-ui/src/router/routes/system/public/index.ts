import { LOGIN_PATH } from '@/constants/core'
import type { RouteRecordRaw } from 'vue-router'

/* Layout */
const BasicLayout = () => import('@/layout/basic.vue')
const AuthPageLayout = () => import('@/layout/auth.vue')

/** 启动时注册的系统固定路由，包含根布局、登录页和 404。 */
const systemPublicRouter: RouteRecordRaw[] = [
  {
    path: '/',
    component: BasicLayout,
    name: 'Index',
    meta: { title: '首页', hideInBreadcrumb: true },
    children: [],
  },
  {
    path: '/auth',
    redirect: LOGIN_PATH,
    component: AuthPageLayout,
    name: 'Authentication',
    meta: { title: 'Authentication', hideInTab: true },
    children: [
      {
        name: 'Login',
        path: 'login',
        component: () => import('@/views/system/authentication/login.vue'),
        meta: { title: '登录' },
      },
    ],
  },
  //全局404页面
  {
    component: () => import('@/views/system/fallback/not-found.vue'),
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
