import type { Component, ComponentPublicInstance } from 'vue'

type MaybePromise<T> = T | Promise<T>

/** 接口原始记录，字段由 labelField/valueField 等配置决定。 */
export type ApiComponentDataItem = Record<string, unknown>

export type ApiComponentParams = Record<string, unknown>

export type ApiComponentOptionsItem = {
  [name: string]: unknown
  children?: ApiComponentOptionsItem[]
  disabled?: boolean
  label?: string
  value?: number | string
}

export type ApiComponentLabelFn = (item: ApiComponentDataItem) => string

export interface ApiComponentProps {
  /** 组件 */
  component: Component
  /** 是否将value从数字转为string */
  numberToString?: boolean
  /** 获取options数据的函数 */
  api?: (params: ApiComponentParams) => Promise<unknown>
  /** 传递给api的参数 */
  params?: ApiComponentParams
  /** 从api返回的结果中提取options数组的字段名 */
  resultField?: string
  /** label字段名 */
  labelField?: string
  /** 通过选项数据自定义label */
  labelFn?: ApiComponentLabelFn
  /** children字段名，需要层级数据的组件可用 */
  childrenField?: string
  /** value字段名 */
  valueField?: string
  /** disabled字段名 */
  disabledField?: string
  /** 组件接收options数据的属性名 */
  optionsPropName?: string
  /** 是否立即调用api */
  immediate?: boolean
  /** 每次`visibleEvent`事件发生时都重新请求数据 */
  alwaysLoad?: boolean
  /** 在api请求之前的回调函数 */
  beforeFetch?: (
    params: ApiComponentParams,
  ) => MaybePromise<ApiComponentParams | void>
  /** 在api请求之前的判断是否允许请求的回调函数 */
  shouldFetch?: (params: ApiComponentParams) => MaybePromise<boolean>
  /** 在api请求之后的回调函数 */
  afterFetch?: (response: unknown) => MaybePromise<unknown>
  /** 直接传入选项数据，也作为api返回空数据时的后备数据 */
  options?: object[]
  /** 组件的插槽名称，用来显示一个"加载中"的图标 */
  loadingSlot?: string
  /** 触发api请求的事件名 */
  visibleEvent?: string
  /** 组件的v-model属性名，默认为modelValue。部分组件可能为value */
  modelPropName?: string
  /**
   * 自动选择
   * - `first`：自动选择第一个选项
   * - `last`：自动选择最后一个选项
   * - `one`: 当请求的结果只有一个选项时，自动选择该选项
   * - 函数：自定义选择逻辑，函数的参数为请求的结果数组，返回值为选择的选项
   * - false：不自动选择(默认)
   */
  autoSelect?:
    | 'first'
    | 'last'
    | 'one'
    | ((
        items: ApiComponentOptionsItem[],
      ) => ApiComponentOptionsItem | undefined)
    | false
}

export type ApiComponentSharedProps = Omit<ApiComponentProps, 'component'>

export interface ApiComponentExpose {
  getOptions: () => ApiComponentOptionsItem[]
  /** 包装的控件可以返回单选值、多选数组或对象，这一层不解释其内容。 */
  getValue: () => unknown
  getComponentRef: () => ComponentPublicInstance | null
  updateParam: (params: ApiComponentParams) => void
}
