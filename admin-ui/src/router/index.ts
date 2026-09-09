import { createRouter, createWebHistory } from 'vue-router';
import { setupRouterGuard } from './guard';
import { constantRoutes } from './routes';
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: constantRoutes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0 };
    }
  },
});

//路由守卫
setupRouterGuard(router);

export default router;
