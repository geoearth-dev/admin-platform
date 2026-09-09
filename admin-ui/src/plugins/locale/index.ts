import { i18n, initI18n, loadLocalesMapFromDir, loadLocaleMessages } from './i18n';
import type { LocaleSetupOptions, SupportedLanguagesType } from './type';
import { elementLocale, loadThirdPartyMessage } from './third';
import type { App } from 'vue';
import { preferences } from '../preference';

const modules = import.meta.glob('./langs/**/*.json');

const localesMap = loadLocalesMapFromDir(/\.\/langs\/([^/]+)\/(.*)\.json$/, modules);

/**
 * 加载应用特有的语言包
 * 这里也可以改造为从服务端获取翻译数据
 * @param lang
 */
async function loadMessages(lang: SupportedLanguagesType) {
  const [appLocaleMessages] = await Promise.all([
    localesMap[lang]?.(),
    loadThirdPartyMessage(lang),
  ]);
  return appLocaleMessages?.default;
}

async function setupI18n(app: App, options: LocaleSetupOptions = {}) {
  await initI18n(app, {
    defaultLocale: preferences.app.locale,
    loadMessages,
    missingWarn: !import.meta.env.PROD,
    ...options,
  });
}
const $t = i18n.global.t;

export { type ImportLocaleFn, type LocaleSetupOptions, type SupportedLanguagesType } from './type';
export { $t, i18n, setupI18n, elementLocale, loadLocaleMessages };
export { useI18n } from 'vue-i18n';
