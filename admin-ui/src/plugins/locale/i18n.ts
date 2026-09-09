import { createI18n, type Locale } from "vue-i18n";
import type { ImportLocaleFn, LoadMessageFn, LocaleSetupOptions, SupportedLanguagesType } from "./type";
import { unref, type App } from "vue";
import { useSimpleLocale } from "./use-simple-locale";
const i18n = createI18n({
  globalInjection: true,
  legacy: false,
  locale: '',
  messages: {},
});

const { setSimpleLocale } = useSimpleLocale();
/**
 * Load locale modules with directory structure
 * @param regexp - Regular expression to match language and file names
 * @param modules - The modules object containing paths and import functions
 * @returns A map of locales to their corresponding import functions
 */
function loadLocalesMapFromDir(regexp: RegExp, modules: Record<string, () => Promise<unknown>>): Record<Locale, ImportLocaleFn> {
  const localesRaw: Record<Locale, Record<string, () => Promise<unknown>>> = {};
  const localesMap: Record<Locale, ImportLocaleFn> = {};

  // Iterate over the modules to extract language and file names
  for (const path in modules) {
    const match = path.match(regexp);
    if (match) {
      const [_, locale, fileName] = match;
      if (locale && fileName) {
        if (!localesRaw[locale]) {
          localesRaw[locale] = {};
        }
        if (modules[path]) {
          localesRaw[locale][fileName] = modules[path];
        }
      }
    }
  }

  // Convert raw locale data into async import functions
  for (const [locale, files] of Object.entries(localesRaw)) {
    localesMap[locale] = async () => {
      const messages: Record<string, string> = {};
      for (const [fileName, importFn] of Object.entries(files)) {
        messages[fileName] = ((await importFn()) as { default: string })?.default;
      }
      return { default: messages };
    };
  }

  return localesMap;
}


/**
 * Set i18n language
 * @param locale
 */
function setI18nLanguage(locale: Locale) {
  i18n.global.locale.value = locale;
  document?.querySelector('html')?.setAttribute('lang', locale);
}



/**
 * Load locale messages
 * @param lang
 */
async function loadLocaleMessages(lang: SupportedLanguagesType) {
  if (unref(i18n.global.locale) === lang) {
    return setI18nLanguage(lang);
  }
  setSimpleLocale(lang);

  // const message = await localesMap[lang]?.();
  // if (message?.default) {
  //   i18n.global.setLocaleMessage(lang, message.default);
  // }

  const mergeMessage = await loadMessages(lang);
  i18n.global.mergeLocaleMessage(lang, mergeMessage);

  return setI18nLanguage(lang);
}



let loadMessages: LoadMessageFn;
async function initI18n(app: App, options: LocaleSetupOptions = {}) {
  const { defaultLocale = 'zh-CN' } = options;
  // app可以自行扩展一些第三方库和组件库的国际化
  loadMessages = options.loadMessages || (async () => ({}));
  app.use(i18n);
  await loadLocaleMessages(defaultLocale);

  // 在控制台打印警告
  i18n.global.setMissingHandler((locale, key) => {
    if (options.missingWarn && key.includes('.')) {
      console.warn(
        `[intlify] Not found '${key}' key in '${locale}' locale messages.`,
      );
    }
  });
}


export {
  i18n,
  loadLocaleMessages,
  initI18n,
  loadLocalesMapFromDir
};
