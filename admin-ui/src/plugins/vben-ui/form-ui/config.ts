import type { Component } from 'vue'

import type {
  BaseFormComponentType,
  FormCommonConfig,
  VbenFormAdapterOptions,
} from './types'

import {
  VbenCheckbox,
  Input as VbenInput,
  VbenInputPassword,
  VbenPinInput,
  VbenSelect,
} from '@/plugins/vben-ui/shadcn-ui'

import VbenFormFieldArray from './components/form-field-array.vue'
import { warnDeprecatedOnce } from './deprecation'
import { registerFormRules } from './rule-registry'
import { globalShareState } from '@/plugins/global-state'
import {
  elementPlusComponents,
  elementPlusModelProps,
} from './adapter/element-plus'

const DEFAULT_MODEL_PROP_NAME = 'modelValue'

export const DEFAULT_FORM_COMMON_CONFIG: FormCommonConfig = {}

export const COMPONENT_MAP: Record<BaseFormComponentType, Component> = {
  VbenCheckbox,
  VbenFormFieldArray,
  VbenInput,
  VbenInputPassword,
  VbenPinInput,
  VbenSelect,
  ...elementPlusComponents,
}

// 大部分组件使用 modelValue；Upload 的文件列表通过 fileList 绑定。
export const COMPONENT_BIND_EVENT_MAP: Partial<
  Record<BaseFormComponentType, string>
> = { ...elementPlusModelProps }

export function setupVbenForm<
  T extends BaseFormComponentType = BaseFormComponentType,
>(options: VbenFormAdapterOptions<T>) {
  const { config, defineRules, rules } = options

  const { changeEventFallback = false, emptyStateValue = undefined } =
    config ?? {}

  Object.assign(DEFAULT_FORM_COMMON_CONFIG, {
    changeEventFallback,
    emptyStateValue,
  })

  if (defineRules) {
    warnDeprecatedOnce(
      'setup-vben-form-define-rules',
      '[Vben Form] `setupVbenForm({ defineRules })` is deprecated. Use `setupVbenForm({ rules })` instead.',
    )
    registerFormRules(defineRules)
  }
  if (rules) {
    registerFormRules(rules)
  }

  const baseModelPropName = config?.baseModelPropName ?? DEFAULT_MODEL_PROP_NAME
  const modelPropNameMap: Partial<Record<string, string>> | undefined =
    config?.modelPropNameMap

  const components = globalShareState.getComponents()

  for (const [key, component] of Object.entries(components)) {
    // 全局注册表存储 unknown；在接入表单时验证组件边界。
    if (
      typeof component !== 'function' &&
      (typeof component !== 'object' || component === null)
    ) {
      console.warn(`[Vben Form] Component ${key} is not a Vue component.`)
      continue
    }
    COMPONENT_MAP[key] = component as Component
  }

  for (const key of Object.keys(COMPONENT_MAP)) {
    // 同时覆盖内置组件，重复 setup 时不会保留上一轮的映射。
    COMPONENT_BIND_EVENT_MAP[key] =
      modelPropNameMap?.[key] ?? elementPlusModelProps[key] ?? baseModelPropName
  }
}
