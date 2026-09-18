import { defineComponent, h, resolveComponent } from 'vue'
import type { Component, CSSProperties, PropType, VNodeChild } from 'vue'
import {
  ElButton,
  ElCascader,
  ElCheckbox,
  ElCheckboxButton,
  ElCheckboxGroup,
  ElColorPicker,
  ElDatePicker,
  ElIcon,
  ElInput,
  ElInputNumber,
  ElOption,
  ElRadio,
  ElRadioButton,
  ElRadioGroup,
  ElRate,
  ElSelect,
  ElSlider,
  ElSwitch,
  ElTimePicker,
  ElUpload,
} from 'element-plus'
import * as ElementPlusIcons from '@element-plus/icons-vue'
import type { ComponentConfig } from './types'
// 动态 h() 组件不会经过模板的按需样式解析。
import 'element-plus/es/components/button/style/css'
import 'element-plus/es/components/cascader/style/css'
import 'element-plus/es/components/checkbox/style/css'
import 'element-plus/es/components/checkbox-button/style/css'
import 'element-plus/es/components/checkbox-group/style/css'
import 'element-plus/es/components/color-picker/style/css'
import 'element-plus/es/components/date-picker/style/css'
import 'element-plus/es/components/icon/style/css'
import 'element-plus/es/components/input/style/css'
import 'element-plus/es/components/input-number/style/css'
import 'element-plus/es/components/option/style/css'
import 'element-plus/es/components/radio/style/css'
import 'element-plus/es/components/radio-button/style/css'
import 'element-plus/es/components/radio-group/style/css'
import 'element-plus/es/components/rate/style/css'
import 'element-plus/es/components/select/style/css'
import 'element-plus/es/components/slider/style/css'
import 'element-plus/es/components/switch/style/css'
import 'element-plus/es/components/time-picker/style/css'
import 'element-plus/es/components/upload/style/css'

export type RenderConfig = ComponentConfig

type CreateElement = typeof h
type ChildBuilder = (
  create: CreateElement,
  conf: RenderConfig,
  key: string,
) => VNodeChild
type SlotBuilder = (
  create: CreateElement,
  conf: RenderConfig,
  key: string,
) => (() => VNodeChild) | undefined
type BuilderMap<T> = Partial<Record<string, Record<string, T>>>

const components: Partial<Record<string, Component>> = {
  'el-button': ElButton,
  'el-cascader': ElCascader,
  'el-checkbox': ElCheckbox,
  'el-checkbox-button': ElCheckboxButton,
  'el-checkbox-group': ElCheckboxGroup,
  'el-color-picker': ElColorPicker,
  'el-date-picker': ElDatePicker,
  'el-icon': ElIcon,
  'el-input': ElInput,
  'el-input-number': ElInputNumber,
  'el-option': ElOption,
  'el-radio': ElRadio,
  'el-radio-button': ElRadioButton,
  'el-radio-group': ElRadioGroup,
  'el-rate': ElRate,
  'el-select': ElSelect,
  'el-slider': ElSlider,
  'el-switch': ElSwitch,
  'el-time-picker': ElTimePicker,
  'el-upload': ElUpload,
}
const icons: Readonly<Partial<Record<string, Component>>> = ElementPlusIcons

function getComponent(name: string): Component | string {
  return components[name] ?? resolveComponent(name)
}

function makeMap(
  str: string,
  expectsLowerCase = false,
): (value: string) => boolean {
  const values = new Set(str.split(','))
  return (value) => values.has(expectsLowerCase ? value.toLowerCase() : value)
}

const isAttr = makeMap(
  'accept,accept-charset,accesskey,action,align,alt,async,autocomplete,' +
    'autofocus,autoplay,autosave,bgcolor,border,buffered,challenge,charset,' +
    'checked,cite,class,code,codebase,color,cols,colspan,content,http-equiv,' +
    'name,contenteditable,contextmenu,controls,coords,data,datetime,default,' +
    'defer,dir,dirname,disabled,download,draggable,dropzone,enctype,method,for,' +
    'form,formaction,headers,height,hidden,high,href,hreflang,http-equiv,' +
    'icon,id,ismap,itemprop,keytype,kind,label,lang,language,list,loop,low,' +
    'manifest,max,maxlength,media,method,GET,POST,min,multiple,email,file,' +
    'muted,name,novalidate,open,optimum,pattern,ping,placeholder,poster,' +
    'preload,radiogroup,readonly,rel,required,reversed,rows,rowspan,sandbox,' +
    'scope,scoped,seamless,selected,shape,size,type,text,password,sizes,span,' +
    'spellcheck,src,srcdoc,srclang,srcset,start,step,style,summary,tabindex,' +
    'target,title,type,usemap,value,width,wrap,prefix-icon',
)
const isNotProps = makeMap(
  'layout,prepend,append,regList,tag,document,changeTag,defaultValue',
)

const componentChild: BuilderMap<ChildBuilder> = {
  'el-button': {
    default(_create, conf) {
      return conf.default ?? ''
    },
  },
  'el-select': {
    options(create, conf) {
      return (conf.options ?? []).map((item) =>
        create(getComponent('el-option'), {
          label: item.label,
          value: item.value,
          disabled: item.disabled,
        }),
      )
    },
  },
  'el-radio-group': {
    options(create, conf) {
      const name = conf.optionType === 'button' ? 'el-radio-button' : 'el-radio'
      return (conf.options ?? []).map((item) =>
        create(
          getComponent(name),
          {
            value: item.value,
            border: conf.border,
            disabled: item.disabled,
          },
          () => item.label,
        ),
      )
    },
  },
  'el-checkbox-group': {
    options(create, conf) {
      const name =
        conf.optionType === 'button' ? 'el-checkbox-button' : 'el-checkbox'
      return (conf.options ?? []).map((item) =>
        create(
          getComponent(name),
          {
            value: item.value,
            border: conf.border,
            disabled: item.disabled,
          },
          () => item.label,
        ),
      )
    },
  },
  'el-upload': {
    'list-type': (create, conf) =>
      conf['list-type'] === 'picture-card'
        ? create(ElIcon, null, () => create(ElementPlusIcons.Plus))
        : create(
            ElButton,
            { type: 'primary', icon: ElementPlusIcons.Upload },
            () => conf.buttonText ?? '',
          ),
  },
}

const componentSlot: BuilderMap<SlotBuilder> = {
  'el-input': {
    prepend: (_create, conf) => (conf.prepend ? () => conf.prepend : undefined),
    append: (_create, conf) => (conf.append ? () => conf.append : undefined),
  },
  'el-upload': {
    tip: (create, conf) =>
      conf.showTip
        ? () =>
            create(
              'div',
              { class: 'el-upload__tip' },
              `只能上传不超过${conf.fileSize ?? ''}${conf.sizeUnit ?? ''}的${conf.accept ?? ''}文件`,
            )
        : undefined,
  },
}

export default defineComponent({
  name: 'GeneratorRender',
  // 在下面显式传递 attrs，包含父组件 v-model 的值和更新监听器。
  inheritAttrs: false,
  props: {
    conf: { type: Object as PropType<RenderConfig>, required: true },
  },
  render() {
    const dataObject: {
      attrs: Record<string, unknown>
      props: Record<string, unknown>
      style: CSSProperties
    } = { attrs: {}, props: {}, style: {} }
    const confClone = this.conf
    if (!confClone.tag) return null
    const children: VNodeChild[] = []
    const slots: Record<string, () => VNodeChild> = {}

    const childBuilders = componentChild[confClone.tag]
    if (childBuilders) {
      for (const [key, builder] of Object.entries(childBuilders)) {
        children.push(builder(h, confClone, key))
      }
    }
    const slotBuilders = componentSlot[confClone.tag]
    if (slotBuilders) {
      for (const [key, builder] of Object.entries(slotBuilders)) {
        const slot = builder(h, confClone, key)
        if (slot) slots[key] = slot
      }
    }

    for (const [key, value] of Object.entries(confClone)) {
      if (key === 'style') {
        dataObject.style = confClone.style ?? {}
      } else if (key === 'props') {
        Object.assign(dataObject.props, confClone.props)
      } else if (isAttr(key)) {
        dataObject.attrs[key] = value
      } else if (!isNotProps(key)) {
        dataObject.props[key] = value
      }
    }
    if (children.length) slots.default = () => children

    const props: Record<string, unknown> = {
      ...dataObject.props,
      ...dataObject.attrs,
      style: dataObject.style,
      ...this.$attrs,
    }
    for (const key of ['icon', 'prefix-icon', 'suffix-icon']) {
      const name = props[key]
      if (typeof name === 'string' && icons[name]) props[key] = icons[name]
    }
    return h(getComponent(confClone.tag), props, slots)
  },
})
