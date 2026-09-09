import type {
  ElCheckbox,
  ElCheckboxGroup,
  ElDatePicker,
  ElDivider,
  ElInput,
  ElInputNumber,
  ElMention,
  ElRadio,
  ElRadioGroup,
  ElRate,
  ElSelectV2,
  ElSpace,
  ElSwitch,
  ElTimePicker,
  ElTreeSelect,
  ElUpload,
} from 'element-plus'
import type { ApiComponentSharedProps } from '@/components/api-component'
import type { IconPickerProps } from '@/components/icon-picker'
import type { VbenTiptap } from '@/components/tiptap'

export interface FormOption {
  label: string | number
  value: string | number | boolean
  disabled?: boolean
}

type GroupOptions = { options?: FormOption[]; isButton?: boolean }

/** 用公开的组件参数类型，避免把 Element Plus 内部默认值标记为必填参数。 */
export interface ElementPlusComponentProps {
  ApiSelect: ApiComponentSharedProps &
    Omit<InstanceType<typeof ElSelectV2>['$props'], 'options'>
  ApiTreeSelect: ApiComponentSharedProps &
    Omit<InstanceType<typeof ElTreeSelect>['$props'], 'data'>
  Checkbox: InstanceType<typeof ElCheckbox>['$props']
  CheckboxGroup: InstanceType<typeof ElCheckboxGroup>['$props'] & GroupOptions
  Radio: InstanceType<typeof ElRadio>['$props']
  RadioGroup: InstanceType<typeof ElRadioGroup>['$props'] & GroupOptions
  DatePicker: InstanceType<typeof ElDatePicker>['$props']
  RangePicker: InstanceType<typeof ElDatePicker>['$props']
  TimePicker: InstanceType<typeof ElTimePicker>['$props']
  Divider: InstanceType<typeof ElDivider>['$props']
  IconPicker: IconPickerProps
  Input: InstanceType<typeof ElInput>['$props']
  InputPassword: InstanceType<typeof ElInput>['$props']
  Textarea: InstanceType<typeof ElInput>['$props']
  InputNumber: InstanceType<typeof ElInputNumber>['$props']
  Mentions: Parameters<typeof ElMention>[0]
  RichEditor: InstanceType<typeof VbenTiptap>['$props'] & { disabled?: boolean }
  Rate: InstanceType<typeof ElRate>['$props']
  Select: InstanceType<typeof ElSelectV2>['$props']
  Space: InstanceType<typeof ElSpace>['$props']
  Switch: InstanceType<typeof ElSwitch>['$props']
  TreeSelect: InstanceType<typeof ElTreeSelect>['$props']
  Upload: InstanceType<typeof ElUpload>['$props']
}

export type ElementPlusComponentType = keyof ElementPlusComponentProps
