export * from './authentication';
export * from './basic';
export * from './iframe';
export * from './widgets';

const BasicLayout = () => import('./basic.vue');
const AuthPageLayout = () => import('./auth.vue');

export { AuthPageLayout, BasicLayout };
