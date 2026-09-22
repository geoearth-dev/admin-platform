import type { SupportedLanguagesType } from './type';

export function getBrowserLocale(): SupportedLanguagesType {
  for (const language of navigator.languages) {
    const code = language.toLowerCase().split('-')[0];

    if (code === 'zh') return 'zh-CN';
    if (code === 'en') return 'en-US';
  }

  return 'en-US';
}
