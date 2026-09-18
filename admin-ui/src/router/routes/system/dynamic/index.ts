import { $t } from '@/plugins/locale';
import type { RouteRecordRaw } from 'vue-router';

// 本地首页路由不配置访问条件，所有已登录用户可访问。
// 动态路由统一挂在 Index 下，目录不再重复配置 BasicLayout。
const systemDynamicRouter: RouteRecordRaw[] = [
  {
    path: '/monitor/job-log/index/:jobId(\\d+)',
    name: 'JobLog',
    component: () => import('@/views/sys/monitor/job/log.vue'),
    meta: {
      title: '调度日志',
      hideInMenu: true,
      activeMenu: '/monitor/job',
      permissions: ['monitor:job:list'],
    },
  },
  {
    path: '/system/dict-data',
    meta: { title: '', hideInMenu: true, permissions: ['system:dict:list'] },
    children: [
      {
        path: 'index/:dictId(\\d+)',
        component: () => import('@/views/sys/system/dict/data.vue'),
        name: 'Data',
        meta: { title: '字典数据', activeMenu: '/system/dict' },
      },
    ],
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    redirect: '/dashboard/analytics',
    meta: {
      icon: 'lucide:layout-dashboard',
      order: -1,
      title: $t('page.dashboard.title'),
    },
    children: [
      {
        name: 'Analytics',
        path: 'analytics',
        component: () => import('@/views/sys/dashboard/analytics/index.vue'),
        meta: {
          affixTab: true,
          icon: 'lucide:area-chart',
          title: $t('page.dashboard.analytics'),
        },
      },
      {
        name: 'Workspace',
        path: 'workspace',
        component: () => import('@/views/sys/dashboard/workspace/index.vue'),
        meta: {
          icon: 'carbon:workspace',
          title: $t('page.dashboard.workspace'),
        },
      },
    ],
  },
  {
    name: 'VbenAbout',
    path: '/admin/about',
    component: () => import('@/views/sys/admin/about/index.vue'),
    meta: {
      icon: 'lucide:copyright',
      title: $t('demos.vben.about'),
      order: 9999,
    },
  },
  {
    name: 'Profile',
    path: '/profile',
    component: () => import('@/views/sys/admin/profile/index.vue'),
    meta: {
      icon: 'lucide:user',
      hideInMenu: true,
      title: $t('page.auth.profile'),
    },
  },
];

export { systemDynamicRouter };
