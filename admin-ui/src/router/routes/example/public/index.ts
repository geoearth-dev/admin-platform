import type { RouteRecordRaw } from 'vue-router'
/* Layout */
const BasicLayout = () => import('@/layout/basic.vue')

// 示例模块固定路由；保留独立布局，菜单中隐藏详情入口。
const examplePublicRouter: RouteRecordRaw[] = [
  {
    path: '/example/genStudent',
    name: 'GenStudent',
    component: BasicLayout,
    redirect: { name: 'studentDetail' },
    meta: { title: '学生管理', hideInMenu: true },
    children: [
      {
        path: 'studentDetail',
        component: () => import('@/views/example/genStudent/detail/index.vue'),
        name: 'studentDetail',
        meta: {
          title: '学生详情',
          hideInMenu: true,
          keepAlive: false,
          activePath: '/example/student',
        },
      },
    ],
  },
]
export { examplePublicRouter }
