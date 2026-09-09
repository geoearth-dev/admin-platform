import type { SetupVxeTable } from './types'
import { effectScope, watch } from 'vue'
import { usePreferences } from '@/plugins/preference'
import { useVbenForm } from '@/plugins/vben-ui/form-ui'
import {
  VxeUI,
  VxeButton,
  VxeCheckbox,
  VxeForm,
  VxeIcon,
  VxeInput,
  VxeLoading,
  VxeModal,
  VxeNumberInput,
  VxePager,
  VxeRadioGroup,
  VxeSelect,
  VxeTooltip,
  VxeUpload,
} from 'vxe-pc-ui'
import {
  VxeColgroup,
  VxeColumn,
  VxeGrid,
  VxeTable,
  VxeToolbar,
} from 'vxe-table'
import enUS from 'vxe-pc-ui/lib/language/en-US'
import zhCN from 'vxe-pc-ui/lib/language/zh-CN'
import { extendsDefaultFormatter } from './extends'
import { configureElementPlusTable } from './adapter'

import 'vxe-pc-ui/lib/style.css'
import 'vxe-table/lib/style.css'

let initialized = false
export let useTableForm: typeof useVbenForm = useVbenForm

// VXE 语言包在 CommonJS 构建下可能多包一层 default。
function unwrapLocale(
  module: Record<string, unknown>,
): Record<string, unknown> {
  return (module.default ?? module) as Record<string, unknown>
}

export function initVxeTable() {
  if (initialized) return
  ;[
    VxeTable,
    VxeColumn,
    VxeColgroup,
    VxeGrid,
    VxeToolbar,
    VxeButton,
    VxeCheckbox,
    VxeForm,
    VxeIcon,
    VxeInput,
    VxeLoading,
    VxeModal,
    VxeNumberInput,
    VxePager,
    VxeRadioGroup,
    VxeSelect,
    VxeTooltip,
    VxeUpload,
  ].forEach((component) => VxeUI.component(component))
  VxeUI.setI18n('zh-CN', unwrapLocale(zhCN))
  VxeUI.setI18n('en-US', unwrapLocale(enUS))
  // 全局配置只初始化一次，不跟随某个表格组件的卸载而停止同步。
  effectScope(true).run(() => {
    const { isDark, locale } = usePreferences()
    watch(
      [isDark, locale],
      ([dark, language]) => {
        VxeUI.setTheme(dark ? 'dark' : 'light')
        VxeUI.setLanguage(language === 'en-US' ? 'en-US' : 'zh-CN')
      },
      { immediate: true },
    )
  })
  extendsDefaultFormatter(VxeUI)
  configureElementPlusTable(VxeUI)
  initialized = true
}

export function setupVbenVxeTable(options: SetupVxeTable) {
  initVxeTable()
  if (options.useVbenForm) useTableForm = options.useVbenForm
  options.configVxeTable(VxeUI)
}
