import { watch } from 'vue';
import { preferences, usePreferences } from '../../preference';
import { convertToRgb } from '@/utils/color';
import { updateCSSVariables } from '@/utils/update-css-variables';

export function useElementPlusDesignTokens() {
  const { isDark } = usePreferences();
  const rootStyles = getComputedStyle(document.documentElement);

  const getCssVariableValueRaw = (variable: string) => {
    return rootStyles.getPropertyValue(variable);
  };

  const getCssVariableValue = (variable: string, isColor: boolean = true) => {
    const value = getCssVariableValueRaw(variable);
    return isColor ? convertToRgb(`hsl(${value})`) : value;
  };

  watch(
    () => preferences.theme,
    () => {
      const background = getCssVariableValue('--background');
      const border = getCssVariableValue('--border');
      const accent = getCssVariableValue('--accent');

      const variables: Record<string, string> = {
        '--el-bg-color': background,
        '--el-bg-color-overlay': getCssVariableValue('--popover'),
        '--el-bg-color-page': getCssVariableValue('--background-deep'),
        '--el-border-color': border,
        '--el-border-color-dark': border,
        '--el-border-color-extra-light': border,
        '--el-border-color-hover': accent,
        '--el-border-color-light': border,
        '--el-border-color-lighter': border,

        '--el-border-radius-base': getCssVariableValue('--radius', false),
        '--el-color-danger': getCssVariableValue('--destructive-500'),
        '--el-color-danger-dark-2': isDark.value
          ? getCssVariableValue('--destructive-400')
          : getCssVariableValue('--destructive-600'),
        '--el-color-danger-light-3': isDark.value
          ? getCssVariableValue('--destructive-600')
          : getCssVariableValue('--destructive-400'),
        '--el-color-danger-light-5': isDark.value
          ? getCssVariableValue('--destructive-700')
          : getCssVariableValue('--destructive-300'),
        '--el-color-danger-light-7': isDark.value
          ? getCssVariableValue('--destructive-800')
          : getCssVariableValue('--destructive-200'),
        '--el-color-danger-light-8': isDark.value
          ? getCssVariableValue('--destructive-900')
          : getCssVariableValue('--destructive-100'),
        '--el-color-danger-light-9': isDark.value
          ? getCssVariableValue('--destructive-950')
          : getCssVariableValue('--destructive-50'),

        '--el-color-error': getCssVariableValue('--destructive-500'),
        '--el-color-error-dark-2': isDark.value
          ? getCssVariableValue('--destructive-400')
          : getCssVariableValue('--destructive-600'),
        '--el-color-error-light-3': isDark.value
          ? getCssVariableValue('--destructive-600')
          : getCssVariableValue('--destructive-400'),
        '--el-color-error-light-5': isDark.value
          ? getCssVariableValue('--destructive-700')
          : getCssVariableValue('--destructive-300'),
        '--el-color-error-light-7': isDark.value
          ? getCssVariableValue('--destructive-800')
          : getCssVariableValue('--destructive-200'),
        '--el-color-error-light-8': isDark.value
          ? getCssVariableValue('--destructive-900')
          : getCssVariableValue('--destructive-100'),
        '--el-color-error-light-9': isDark.value
          ? getCssVariableValue('--destructive-950')
          : getCssVariableValue('--destructive-50'),

        '--el-color-info-light-5': border,
        '--el-color-info-light-8': border,
        '--el-color-info-light-9': getCssVariableValue('--info'), // getCssVariableValue('--secondary'),

        '--el-color-primary': getCssVariableValue('--primary-500'),
        '--el-color-primary-dark-2': isDark.value
          ? getCssVariableValue('--primary-400')
          : getCssVariableValue('--primary-600'),
        '--el-color-primary-light-3': isDark.value
          ? getCssVariableValue('--primary-600')
          : getCssVariableValue('--primary-400'),
        '--el-color-primary-light-5': isDark.value
          ? getCssVariableValue('--primary-700')
          : getCssVariableValue('--primary-300'),
        '--el-color-primary-light-7': isDark.value
          ? getCssVariableValue('--primary-800')
          : getCssVariableValue('--primary-200'),
        '--el-color-primary-light-8': isDark.value
          ? getCssVariableValue('--primary-900')
          : getCssVariableValue('--primary-100'),
        '--el-color-primary-light-9': isDark.value
          ? getCssVariableValue('--primary-950')
          : getCssVariableValue('--primary-50'),

        '--el-color-success': getCssVariableValue('--success-500'),
        '--el-color-success-dark-2': isDark.value
          ? getCssVariableValue('--success-400')
          : getCssVariableValue('--success-600'),
        '--el-color-success-light-3': isDark.value
          ? getCssVariableValue('--success-600')
          : getCssVariableValue('--success-400'),
        '--el-color-success-light-5': isDark.value
          ? getCssVariableValue('--success-700')
          : getCssVariableValue('--success-300'),
        '--el-color-success-light-7': isDark.value
          ? getCssVariableValue('--success-800')
          : getCssVariableValue('--success-200'),
        '--el-color-success-light-8': isDark.value
          ? getCssVariableValue('--success-900')
          : getCssVariableValue('--success-100'),
        '--el-color-success-light-9': isDark.value
          ? getCssVariableValue('--success-950')
          : getCssVariableValue('--success-50'),

        '--el-color-warning': getCssVariableValue('--warning-500'),
        '--el-color-warning-dark-2': isDark.value
          ? getCssVariableValue('--warning-400')
          : getCssVariableValue('--warning-600'),
        '--el-color-warning-light-3': isDark.value
          ? getCssVariableValue('--warning-600')
          : getCssVariableValue('--warning-400'),
        '--el-color-warning-light-5': isDark.value
          ? getCssVariableValue('--warning-700')
          : getCssVariableValue('--warning-300'),
        '--el-color-warning-light-7': isDark.value
          ? getCssVariableValue('--warning-800')
          : getCssVariableValue('--warning-200'),
        '--el-color-warning-light-8': isDark.value
          ? getCssVariableValue('--warning-900')
          : getCssVariableValue('--warning-100'),
        '--el-color-warning-light-9': isDark.value
          ? getCssVariableValue('--warning-950')
          : getCssVariableValue('--warning-50'),

        '--el-fill-color': getCssVariableValue('--accent'),
        '--el-fill-color-blank': background,
        '--el-fill-color-light': getCssVariableValue('--accent'),
        '--el-fill-color-lighter': getCssVariableValue('--accent-lighter'),

        '--el-fill-color-dark': getCssVariableValue('--accent-dark'),
        '--el-fill-color-darker': getCssVariableValue('--accent-darker'),

        // 解决ElLoading背景色问题
        '--el-mask-color': isDark.value ? 'rgba(0,0,0,.8)' : 'rgba(255,255,255,.9)',

        '--el-text-color-primary': getCssVariableValue('--foreground'),

        '--el-text-color-regular': getCssVariableValue('--foreground'),
      };

      updateCSSVariables(variables, `__vben_design_styles__`);
    },
    { immediate: true },
  );
}
