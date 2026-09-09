import type { ElementPlusComponentProps } from '@/plugins/vben-ui/form-ui'
import type {
  VxeGridListeners,
  VxeGridPropTypes,
  VxeGridProps as VxeTableGridProps,
  VxeUIExport,
} from 'vxe-table'

import type { Ref } from 'vue'

import type { ClassType } from '@/types'

import type {
  BaseFormComponentType,
  FormValues,
  VbenFormProps,
} from '@/plugins/vben-ui/form-ui'

import type { VxeGridApi } from './api'
import type { ViewedRowOptions } from './viewed-row'

import { useVbenForm } from '@/plugins/vben-ui/form-ui'

export interface VxePaginationInfo {
  currentPage: number
  pageSize: number
  total: number
}

interface ToolbarConfigOptions extends VxeGridPropTypes.ToolbarConfig {
  /** 是否显示切换搜索表单的按钮 */
  search?: boolean
}

export type VxeTableGridColumns<T extends object = Record<string, unknown>> =
  VxeTableGridOptions<T>['columns']

export interface VxeTableGridOptions<
  T extends object = Record<string, unknown>,
> extends VxeTableGridProps<T> {
  /** 工具栏配置 */
  toolbarConfig?: ToolbarConfigOptions
}

export interface SeparatorOptions {
  show?: boolean
  backgroundColor?: string
}

export interface VxeGridProps<
  T extends object = Record<string, unknown>,
  D extends BaseFormComponentType = BaseFormComponentType,
  P extends object = ElementPlusComponentProps,
  TFormValues extends FormValues = FormValues,
  TSubmitValues extends FormValues = TFormValues,
> {
  /**
   * 数据
   */
  tableData?: T[]
  /**
   * 标题
   */
  tableTitle?: string
  /**
   * 标题帮助
   */
  tableTitleHelp?: string
  /**
   * 组件class
   */
  class?: ClassType
  /**
   * vxe-grid class
   */
  gridClass?: ClassType
  /**
   * vxe-grid 配置
   */
  gridOptions?: VxeTableGridOptions<T>
  /**
   * vxe-grid 事件
   */
  gridEvents?: VxeGridListeners<T>
  /**
   * 表单配置
   */
  formOptions?: VbenFormProps<D, P, TFormValues, TSubmitValues>
  /**
   * 显示搜索表单
   */
  showSearchForm?: boolean
  /**
   * 搜索表单与表格主体之间的分隔条
   */
  separator?: boolean | SeparatorOptions
  /**
   * 已读行功能
   */
  viewedRowOptions?: boolean | ViewedRowOptions<T>
}

export type ExtendedVxeGridApi<
  D extends object = Record<string, unknown>,
  F extends BaseFormComponentType = BaseFormComponentType,
  P extends object = ElementPlusComponentProps,
  TFormValues extends FormValues = FormValues,
  TSubmitValues extends FormValues = TFormValues,
> = VxeGridApi<D, F, P, TFormValues, TSubmitValues> & {
  useStore: <S = NoInfer<VxeGridProps<D, F, P, TFormValues, TSubmitValues>>>(
    selector?: (
      state: NoInfer<VxeGridProps<D, F, P, TFormValues, TSubmitValues>>,
    ) => S,
  ) => Readonly<Ref<S>>
}

export interface SetupVxeTable {
  configVxeTable: (ui: VxeUIExport) => void
  useVbenForm?: typeof useVbenForm
}
