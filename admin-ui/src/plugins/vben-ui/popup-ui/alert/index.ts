export type { AlertProps, BeforeCloseScope, IconType, PromptProps } from './alert.ts';
export { useAlertContext } from './alert.ts';
export { default as Alert } from './alert.vue';
export {
  vbenAlert as alert,
  clearAllAlerts,
  vbenConfirm as confirm,
  vbenPrompt as prompt,
} from './AlertBuilder.ts';
