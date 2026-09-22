import { getBrowserLocale } from './plugins/locale/utils';
import type { Preferences } from './plugins/preference';
import type { DeepPartial } from './types';
import { getSystemTimezone } from './utils/date';

/**
 * @description 项目配置文件
 * 只需要覆盖项目中的一部分配置，不需要的配置不用覆盖，会自动使用默认配置
 * !!! 更改配置后请清空缓存，否则可能不生效
 */
export const overridesPreferences: DeepPartial<Preferences> = {
  // overrides
  app: {
    name: import.meta.env.VITE_APP_TITLE,
    locale: getBrowserLocale(),
    timezone: getSystemTimezone(),
  },
  copyright: {
    icp: '',
    icpLink: 'https://beian.miit.gov.cn/',
  },
};
