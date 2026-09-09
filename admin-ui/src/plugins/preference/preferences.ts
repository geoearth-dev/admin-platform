import type { DeepPartial } from '@/types/base';
import { isMacOs } from '@/utils/inference';
import { merge, mergeWithArrayOverride } from '@/utils/merge';
import { reactive, readonly, markRaw, watch } from 'vue';
import type { Preferences, InitialOptions } from './types';
import { updateThemeCSSVariables } from './effects';
import { defaultPreferences } from './defaults';
import { breakpointsTailwind, useBreakpoints, useDebounceFn } from '@vueuse/core';
import { StorageManager } from '@/utils/cache';

const STORAGE_KEYS = {
  MAIN: 'preferences',
  LOCALE: 'preferences-locale',
  THEME: 'preferences-theme',
};

class PreferenceManager {
  private cache: StorageManager | undefined;
  private debouncedSave: () => void;
  private initialPreferences: Preferences = defaultPreferences;
  private isInitialized = false;
  private state: Preferences;

  constructor() {
    // 构造函数不再同步读取缓存，使用默认值初始化
    // 真正的缓存加载在 initPreferences 中完成（已经是 async）
    this.state = reactive<Preferences>({ ...defaultPreferences });
    this.debouncedSave = useDebounceFn(() => this.saveToCache(), 150);
  }

  /**
   * 清除所有缓存的偏好设置
   */
  clearCache = async () => {
    await Promise.all(Object.values(STORAGE_KEYS).map((key) => this.cache?.removeItem(key)));
  };

  /**
   * 获取初始化偏好设置
   */
  getInitialPreferences = () => {
    return this.initialPreferences;
  };

  /**
   * 获取当前偏好设置（只读）
   */
  getPreferences = () => {
    return readonly(this.state);
  };

  /**
   * 初始化偏好设置
   * @param options - 初始化配置项
   * @param options.namespace - 命名空间，用于隔离不同应用的配置
   * @param options.overrides - 要覆盖的偏好设置
   */
  initPreferences = async ({ namespace, overrides }: InitialOptions) => {
    // 防止重复初始化
    if (this.isInitialized) {
      return;
    }

    // 使用命名空间初始化存储管理器
    this.cache = new StorageManager({ prefix: namespace });

    // 合并初始偏好设置：前面的对象优先，后面的对象仅补齐缺失字段
    this.initialPreferences = merge({}, overrides, defaultPreferences);

    // 加载缓存的偏好设置，并仅用缓存补齐初始化配置中未显式设置的字段
    const cachedPreferences = (await this.loadFromCache()) || {};

    const mergedPreference = mergeWithArrayOverride(
      cachedPreferences, // 用户缓存的设置优先
      this.initialPreferences, // 初始设置仅补齐缺失字段
    );

    // 更新偏好设置
    this.updatePreferences(mergedPreference);

    await this.saveToCache();

    // 设置监听器
    this.setupWatcher();

    // 初始化平台标识
    this.initPlatform();

    this.isInitialized = true;
  };

  /**
   * 重置偏好设置到初始状态
   */
  resetPreferences = async () => {
    // 将状态重置为初始偏好设置
    Object.assign(this.state, this.initialPreferences);

    // 保存偏好设置至缓存
    await this.saveToCache();

    // 直接触发 UI 更新
    this.handleUpdates(this.state);
  };

  /**
   * 更新偏好设置
   * @param updates - 要更新的偏好设置
   */
  updatePreferences = (updates: DeepPartial<Preferences>) => {
    // 深度合并更新内容和当前状态
    const mergedState = mergeWithArrayOverride({}, updates, markRaw(this.state));
    Object.assign(this.state, mergedState);

    // 根据更新的值执行更新
    this.handleUpdates(updates);

    // 保存到缓存（fire-and-forget，通过 debounce 控制频率）
    this.debouncedSave();
  };
  /**
   * 处理更新
   * @param updates - 更新的偏好设置
   */
  private handleUpdates(updates: DeepPartial<Preferences>) {
    const { theme, app } = updates;
    if (theme && (Object.keys(theme).length > 0 || Reflect.has(theme, 'fontSize'))) {
      updateThemeCSSVariables(this.state);
    }

    if (app && (Reflect.has(app, 'colorGrayMode') || Reflect.has(app, 'colorWeakMode'))) {
      this.updateColorMode(this.state);
    }
  }

  /**
   * 初始化平台标识
   */
  private initPlatform() {
    document.documentElement.dataset.platform = isMacOs() ? 'macOs' : 'window';
  }

  /**
   * 从缓存加载偏好设置
   * @returns 缓存的偏好设置，如果不存在则返回 null
   */
  private async loadFromCache(): Promise<null | Preferences> {
    return this.cache?.getItem<Preferences>(STORAGE_KEYS.MAIN) ?? null;
  }

  /**
   * 保存偏好设置到缓存
   */
  private async saveToCache() {
    try {
      await this.cache?.setItem(STORAGE_KEYS.MAIN, this.state);
      await this.cache?.setItem(STORAGE_KEYS.LOCALE, this.state.app.locale);
      await this.cache?.setItem(STORAGE_KEYS.THEME, this.state.theme.mode);
    } catch (error) {
      console.error('Failed to save preferences to cache:', error);
    }
  }

  /**
   * 监听状态和系统偏好设置的变化
   */
  private setupWatcher() {
    if (this.isInitialized) {
      return;
    }
    // 监听断点，判断是否移动端
    const breakpoints = useBreakpoints(breakpointsTailwind);
    const isMobile = breakpoints.smaller('md');
    watch(
      () => isMobile.value,
      (val) => {
        this.updatePreferences({
          app: { isMobile: val },
        });
      },
      { immediate: true },
    );

    // 监听系统主题偏好设置变化
    window
      .matchMedia('(prefers-color-scheme: dark)')
      .addEventListener('change', ({ matches: isDark }) => {
        // 仅在自动模式下跟随系统主题
        if (this.state.theme.mode === 'auto') {
          // 先应用实际的主题
          this.updatePreferences({
            theme: { mode: isDark ? 'dark' : 'light' },
          });
          // 再恢复为 auto 模式，保持跟随系统的状态
          this.updatePreferences({
            theme: { mode: 'auto' },
          });
        }
      });
  }

  /**
   * 更新页面颜色模式（灰色、色弱）
   * @param preference - 偏好设置
   */
  private updateColorMode(preference: Preferences) {
    const { colorGrayMode, colorWeakMode } = preference.app;
    const dom = document.documentElement;
    dom.classList.toggle('invert-mode', colorWeakMode);
    dom.classList.toggle('grayscale-mode', colorGrayMode);
  }
}

export const preferencesManager = new PreferenceManager();
export { PreferenceManager };
