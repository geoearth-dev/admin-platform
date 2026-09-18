import type { FormProps, RowProps } from 'element-plus'

export type GenerateType = 'file' | 'dialog'

export interface GenerateOptions {
  type: GenerateType
  fileName?: string
}

export type OptionValue = string | number | boolean
export type FieldValue =
  | OptionValue
  | Date
  | null
  | undefined
  | Array<OptionValue | Date>
  | Array<Array<string | number>>

export interface FieldOption {
  id?: number
  label: string
  value: OptionValue
  disabled?: boolean
  children?: FieldOption[]
}

/** 组件面板、画布和代码生成器共用的配置。 */
export interface ComponentConfig {
  tag?: string
  tagIcon?: string
  label?: string
  layout?: 'colFormItem' | 'rowFormItem'
  layoutTree?: boolean
  changeTag?: boolean
  document?: string
  componentName?: string
  class?: string
  vModel?: string
  defaultValue?: FieldValue
  children?: ComponentConfig[]
  span?: number
  labelWidth?: number | null
  size?: FormProps['size']
  required?: boolean
  placeholder?: string
  disabled?: boolean
  clearable?: boolean
  readonly?: boolean
  style?: { width?: string | number }
  icon?: string
  default?: string
  gutter?: number
  justify?: RowProps['justify']
  align?: RowProps['align']
  maxlength?: number | null
  autosize?: { minRows?: number; maxRows?: number }
  prepend?: string
  append?: string
  filterable?: boolean
  separator?: string
  format?: string
  optionType?: 'default' | 'button'
  border?: boolean
  multiple?: boolean
  range?: boolean
  min?: number
  max?: number
  step?: number
  precision?: number
  type?: string
  name?: string
  buttonText?: string
  showTip?: boolean
  tip?: string
  regList?: Array<{ pattern: string; message: string }>
  options?: FieldOption[]
  dataType?: 'dynamic' | 'static'
  props?: {
    props: {
      multiple?: boolean
      value?: string
      label?: string
      children?: string
      checkStrictly?: boolean
      emitPath?: boolean
    }
  }
  valueKey?: string
  labelKey?: string
  childrenKey?: string
  action?: string
  sizeUnit?: 'KB' | 'MB' | 'GB'
  fileSize?: number
  accept?: string
  'auto-upload'?: boolean
  'is-range'?: boolean
  'value-format'?: string
  'active-value'?: OptionValue
  'inactive-value'?: OptionValue
  'show-word-limit'?: boolean
  'prefix-icon'?: string
  'suffix-icon'?: string
  'show-password'?: boolean
  'controls-position'?: '' | 'right'
  'step-strictly'?: boolean
  'active-text'?: string
  'inactive-text'?: string
  'active-color'?: string | null
  'inactive-color'?: string | null
  'show-all-levels'?: boolean
  'show-stops'?: boolean
  'start-placeholder'?: string
  'end-placeholder'?: string
  'range-separator'?: string
  'picker-options'?: { selectableRange?: string }
  'allow-half'?: boolean
  'show-text'?: boolean
  'show-score'?: boolean
  'show-alpha'?: boolean
  'color-format'?: string
  'list-type'?: 'text' | 'picture' | 'picture-card'
}

/** 左侧待添加的组件尚未分配画布 ID。 */
export interface PaletteItem extends ComponentConfig {
  label: string
  tagIcon: string
}

export interface DrawingItem extends ComponentConfig {
  formId: number
  renderKey?: number
  layout: 'colFormItem' | 'rowFormItem'
  children: DrawingItem[]
}

export interface FormConfig {
  formRef: string
  formModel: string
  formRules: string
  size: FormProps['size']
  labelPosition: FormProps['labelPosition']
  labelWidth: number
  gutter: number
  disabled: boolean
  span: number
  formBtns: boolean
  unFocusedComponentBorder: boolean
}
