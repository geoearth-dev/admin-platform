import type { Component, ComponentPublicInstance } from 'vue'

import { defineAsyncComponent, defineComponent, h, shallowRef } from 'vue'
import {
  ElButton,
  ElCheckbox,
  ElCheckboxButton,
  ElCheckboxGroup,
  ElDatePicker,
  ElDivider,
  ElInput,
  ElInputNumber,
  ElMention,
  ElRadio,
  ElRadioButton,
  ElRadioGroup,
  ElRate,
  ElSelectV2,
  ElSpace,
  ElSwitch,
  ElTimePicker,
  ElTreeSelect,
  ElUpload,
} from 'element-plus'



import { ApiComponent } from '@/components/api-component'
import { IconPicker } from '@/components/icon-picker'
import { $t } from '@/plugins/locale'

type Props = Record<string, unknown>

/** 包装只负责默认参数和透传；提示词在渲染时读取，以响应语言切换。 */
function wrap(
  name: string,
  component: Component,
  defaults: Props = {},
  placeholder?: 'input' | 'select',
  transform?: (props: Props) => Props,
) {
  return defineComponent({
    name,
    inheritAttrs: false,
    setup(_, { attrs, slots, expose }) {
      const inner = shallowRef<ComponentPublicInstance>()
      expose(
        new Proxy(
          {},
          {
            get: (_, key) =>
              inner.value ? Reflect.get(inner.value, key) : undefined,
            has: (_, key) => !!inner.value && Reflect.has(inner.value, key),
          },
        ),
      )
      return () => {
        const props = {
          ...defaults,
          ...(placeholder
            ? { placeholder: $t(`ui.placeholder.${placeholder}`) }
            : {}),
          ...attrs,
        }
        return h(
          component,
          { ...(transform ? transform(props) : props), ref: inner },
          slots,
        )
      }
    },
  })
}

/** 日期/时间范围控件的 name、id 需要成对传递。 */
function rangeProps(props: Props): Props {
  if (
    !props.isRange &&
    !(typeof props.type === 'string' && props.type.includes('range'))
  ) {
    return props
  }
  const result = { ...props }
  for (const key of ['name', 'id']) {
    const value = result[key]
    if (typeof value === 'string') result[key] = [value, `${value}_end`]
  }
  return result
}

function optionGroup(
  name: string,
  group: Component,
  item: Component,
  button: Component,
) {
  return defineComponent({
    name,
    inheritAttrs: false,
    setup(_, { attrs, slots, expose }) {
      const inner = shallowRef<ComponentPublicInstance>()
      expose(
        new Proxy(
          {},
          {
            get: (_, key) =>
              inner.value ? Reflect.get(inner.value, key) : undefined,
            has: (_, key) => !!inner.value && Reflect.has(inner.value, key),
          },
        ),
      )
      return () => {
        const { options, isButton, ...props } = attrs
        const optionSlot = () =>
          (Array.isArray(options) ? options : []).map((option: unknown) => {
            if (typeof option !== 'object' || option === null) return null
            const optionProps: Props = { ...option }
            const label = optionProps.label
            return h(
              isButton ? button : item,
              {
                ...optionProps,
                key:
                  typeof optionProps.value === 'string' ||
                  typeof optionProps.value === 'number'
                    ? optionProps.value
                    : undefined,
              },
              {
                default: () =>
                  typeof label === 'string' || typeof label === 'number'
                    ? String(label)
                    : '',
              },
            )
          })
        return h(
          group,
          { ...props, ref: inner },
          { ...slots, default: slots.default ?? optionSlot },
        )
      }
    },
  })
}

function actionProps(type: 'primary' | 'default') {
  return (props: Props): Props => {
    const {
      type: requestedType,
      content: _content,
      show: _show,
      ...rest
    } = props
    // 表单动作传来的 type="button" 是 HTML 类型，不是 ElButton 的主题类型。
    const native =
      requestedType === 'button' ||
      requestedType === 'submit' ||
      requestedType === 'reset'
    return {
      ...rest,
      type: native ? type : (requestedType ?? type),
      nativeType: native ? requestedType : 'button',
    }
  }
}

/** 仅供本地表单使用，不写入全局组件注册中心。 */
export const elementPlusComponents = {
  ApiSelect: wrap(
    'FormApiSelect',
    ApiComponent,
    {
      component: ElSelectV2,
      loadingSlot: 'loading',
      visibleEvent: 'onVisibleChange',
    },
    'select',
  ),
  ApiTreeSelect: wrap(
    'FormApiTreeSelect',
    ApiComponent,
    {
      component: ElTreeSelect,
      props: { label: 'label', children: 'children' },
      nodeKey: 'value',
      optionsPropName: 'data',
      loadingSlot: 'loading',
      visibleEvent: 'onVisibleChange',
    },
    'select',
  ),
  DefaultButton: wrap(
    'FormDefaultButton',
    ElButton,
    { size: 'small' },
    undefined,
    actionProps('default'),
  ),
  PrimaryButton: wrap(
    'FormPrimaryButton',
    ElButton,
    { size: 'small' },
    undefined,
    actionProps('primary'),
  ),
  Checkbox: ElCheckbox,
  CheckboxGroup: optionGroup(
    'FormCheckboxGroup',
    ElCheckboxGroup,
    ElCheckbox,
    ElCheckboxButton,
  ),
  Radio: ElRadio,
  RadioGroup: optionGroup(
    'FormRadioGroup',
    ElRadioGroup,
    ElRadio,
    ElRadioButton,
  ),
  DatePicker: wrap('FormDatePicker', ElDatePicker, {}, 'select', rangeProps),
  RangePicker: wrap(
    'FormRangePicker',
    ElDatePicker,
    { type: 'daterange' },
    undefined,
    rangeProps,
  ),
  TimePicker: wrap('FormTimePicker', ElTimePicker, {}, 'select', rangeProps),
  Divider: ElDivider,
  IconPicker: wrap(
    'FormIconPicker',
    IconPicker,
    {
      inputComponent: h(ElInput),
      iconSlot: 'append',
      modelValueProp: 'modelValue',
    },
    'select',
  ),
  Input: wrap('FormInput', ElInput, {}, 'input'),
  InputPassword: wrap(
    'FormInputPassword',
    ElInput,
    { type: 'password', showPassword: true },
    'input',
  ),
  Textarea: wrap('FormTextarea', ElInput, { type: 'textarea' }, 'input'),
  InputNumber: wrap('FormInputNumber', ElInputNumber, {}, 'input'),
  Mentions: wrap('FormMentions', ElMention, {}, 'input'),
  RichEditor: wrap(
    'FormRichEditor',
    defineAsyncComponent(() => import('@/components/tiptap/tiptap.vue')),
    {},
    'input',
    ({ disabled, ...props }) => ({
      ...props,
      editable: disabled ? false : props.editable,
    }),
  ),
  Rate: ElRate,
  Select: wrap('FormSelect', ElSelectV2, {}, 'select'),
  Space: ElSpace,
  Switch: ElSwitch,
  TreeSelect: wrap('FormTreeSelect', ElTreeSelect, {}, 'select'),
  Upload: ElUpload,
} satisfies Record<string, Component>

export const elementPlusModelProps: Record<string, string> = {
  Upload: 'fileList',
}
