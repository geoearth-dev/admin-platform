export * from './authentication';
export * from './basic';
export * from './iframe';
export * from './widgets';

const BasicLayout = () => import('./basic.vue');
const AuthPageLayout = () => import('./auth.vue');

const IFrameView = () => import('./iframe').then((m) => m.IFrameView);

export { AuthPageLayout, BasicLayout, IFrameView };
