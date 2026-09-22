import { computed } from 'vue';
import { acceptHMRUpdate, defineStore } from 'pinia';

import {
  DEFAULT_TIME_ZONE_OPTIONS,
  preferences,
  preferencesManager,
  updatePreferences,
} from '@/plugins/preference';

/** 时区配置由偏好模块持久化，Store 提供统一的读取和操作入口。 */
const useTimezoneStore = defineStore('core-timezone', () => {
  const timezone = computed(() => preferences.app.timezone);

  function setTimezone(value: string) {
    updatePreferences({ app: { timezone: value } });
  }

  function getTimezoneOptions() {
    const options = DEFAULT_TIME_ZONE_OPTIONS.map((item) => ({
      label: item.label,
      value: item.timezone,
    }));

    // 浏览器或缓存中的时区可能不在预设列表中，保留当前选项。
    if (!options.some((item) => item.value === timezone.value)) {
      options.push({ label: timezone.value, value: timezone.value });
    }

    return options;
  }

  function $reset() {
    setTimezone(preferencesManager.getInitialPreferences().app.timezone);
  }

  return { timezone, setTimezone, getTimezoneOptions, $reset };
});

export { useTimezoneStore };

// 解决热更新问题
const hot = import.meta.hot;
if (hot) {
  hot.accept(acceptHMRUpdate(useTimezoneStore, hot));
}
