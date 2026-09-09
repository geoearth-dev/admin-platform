import type { FormRuleValidator } from './types'
import { $t } from '@/plugins/locale'

function isEmptyRequiredValue(value: unknown) {
  return (
    value === null ||
    value === undefined ||
    (typeof value === 'string' && value.trim() === '') ||
    (Array.isArray(value) && value.length === 0)
  )
}

// 默认规则可直接使用；业务仍可通过 setupVbenForm({ rules }) 覆盖。
const FORM_RULES = new Map<string, FormRuleValidator>([
  [
    'required',
    (value, _params, context) =>
      !isEmptyRequiredValue(value) ||
      $t('ui.formRules.required', [context.label ?? context.name]),
  ],
  [
    'selectRequired',
    (value, _params, context) =>
      !isEmptyRequiredValue(value) ||
      $t('ui.formRules.selectRequired', [context.label ?? context.name]),
  ],
])

export function getFormRule(name: string) {
  return FORM_RULES.get(name)
}

export function registerFormRules(
  rules: Partial<Record<string, FormRuleValidator>>,
) {
  for (const [name, validator] of Object.entries(rules)) {
    if (validator) {
      FORM_RULES.set(name, validator)
    }
  }
}
