import './assets/css/main.css';

import { createApp } from 'vue';

import App from './App.vue';
import router from './router';
import { setupI18n } from '@/plugins/locale/index.ts';
import { overridesPreferences } from './preference';
import { initStores } from './store/index';
import { initPreferences } from './plugins/preference/index.ts';
import { ElLoading } from 'element-plus';
import { MotionPlugin } from '@vueuse/motion';
import { unmountGlobalLoading } from './plugins/config/inject-app-loading/unmount-global-loading.ts';
import { registerAccessDirective } from './plugins/effects/access/directive.ts';
import { VueQueryPlugin } from '@tanstack/vue-query';

const env = import.meta.env.PROD ? 'prod' : 'dev';
const appVersion = import.meta.env.VITE_APP_VERSION;
const namespace = `${import.meta.env.VITE_APP_NAMESPACE}-${appVersion}-${env}`;

// app偏好设置初始化
await initPreferences({ namespace, overrides: overridesPreferences });

const app = createApp(App);

// 注册Element Plus提供的v-loading指令
app.directive('loading', ElLoading.directive);

// 配置 pinia-store
await initStores(app, namespace);

// 国际化 i18n 配置
await setupI18n(app);

// 安装权限指令
registerAccessDirective(app);

// 配置路由及路由守卫
app.use(router);

// 配置@tanstack/vue-query
app.use(VueQueryPlugin);

// 配置Motion插件
app.use(MotionPlugin);

app.mount('#app');

// 移除并销毁loading
unmountGlobalLoading();
