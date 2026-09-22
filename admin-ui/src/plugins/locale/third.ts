import type { Language } from 'element-plus/es/locales';

import elementLocale_zh from 'element-plus/es/locale/lang/zh-cn';
import elementLocale_en from 'element-plus/es/locale/lang/en';
import { ref } from 'vue';
import type { SupportedLanguagesType } from './type';
import dayjs from 'dayjs';


const elementLocale = ref<Language>(elementLocale_zh);
/**
 * 加载element-plus的语言包
 * @param lang
 */
async function loadElementLocale(lang: SupportedLanguagesType) {
  switch (lang) {
    case 'en-US': {
      elementLocale.value = elementLocale_en;
      break;
    }
    case 'zh-CN': {
      elementLocale.value = elementLocale_zh;
      break;
    }
  }
}

/**
 * 加载dayjs的语言包
 * @param lang
 */
async function loadDayjsLocale(lang: SupportedLanguagesType) {
  if (lang === 'zh-CN') {
    await import('dayjs/locale/zh-cn');
  } else {
    await import('dayjs/locale/en');
  }
  dayjs.locale(lang === 'zh-CN' ? 'zh-cn' : 'en');
}

/**
 * 加载第三方组件库的语言包
 * @param lang
 */
async function loadThirdPartyMessage(lang: SupportedLanguagesType) {
  await Promise.all([loadElementLocale(lang), loadDayjsLocale(lang)]);
}

export { loadThirdPartyMessage, elementLocale }
