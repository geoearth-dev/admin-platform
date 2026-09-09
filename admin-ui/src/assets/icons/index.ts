import { createIconifyIcon } from './create-icon';

export * from './create-icon';

export * from './lucide';

export * from './svg';

export const MdiKeyboardEsc = createIconifyIcon('mdi:keyboard-esc');

export { default as EmptyIcon } from './vue/empty-icon.vue';
export { addCollection, addIcon, Icon as IconifyIcon, listIcons } from '@iconify/vue';
