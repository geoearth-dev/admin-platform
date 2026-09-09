import type { ElementPlusComponentProps } from '@/plugins/vben-ui/form-ui'
import type { VxeGridSlots, VxeGridSlotTypes } from 'vxe-table'

import type { Component, SlotsType } from 'vue'

import type {
  BaseFormComponentType,
  FormValues,
} from '@/plugins/vben-ui/form-ui'

import type { ExtendedVxeGridApi, VxeGridProps } from './types'

import { defineComponent, h, onBeforeUnmount } from 'vue'

import { useSelector } from '@tanstack/vue-store'

import { VxeGridApi } from './api'
import VxeGrid from './use-vxe-grid.vue'
import { initVxeTable } from './init'

type FilteredSlots<T> = {
  [
    K in keyof VxeGridSlots<T> as K extends 'form' ? never : K
  ]: VxeGridSlots<T>[K]
}

export function useVbenVxeGrid<
  T extends object = Record<string, unknown>,
  D extends BaseFormComponentType = BaseFormComponentType,
  P extends object = ElementPlusComponentProps,
  TFormValues extends FormValues = FormValues,
  TSubmitValues extends FormValues = TFormValues,
>(options: VxeGridProps<T, D, P, TFormValues, TSubmitValues>) {
  initVxeTable()
  const api = new VxeGridApi<T, D, P, TFormValues, TSubmitValues>(options)
  const extendedApi: ExtendedVxeGridApi<T, D, P, TFormValues, TSubmitValues> =
    api as ExtendedVxeGridApi<T, D, P, TFormValues, TSubmitValues>
  extendedApi.useStore = (selector) => {
    return useSelector(api.store, selector)
  }

  const Grid = defineComponent(
    (
      props: VxeGridProps<T, D, P, TFormValues, TSubmitValues>,
      { attrs, slots },
    ) => {
      onBeforeUnmount(() => {
        api.unmount()
      })
      api.setState({ ...props, ...attrs } as Partial<
        VxeGridProps<T, D, P, TFormValues, TSubmitValues>
      >)
      return () =>
        h(
          VxeGrid as Component,
          {
            ...props,
            ...attrs,
            api: extendedApi,
          },
          slots,
        )
    },
    {
      name: 'VbenVxeGrid',
      inheritAttrs: false,
      slots: Object as SlotsType<
        {
          // 表格标题
          'table-title': undefined
          // 工具栏左侧部分
          'toolbar-actions': VxeGridSlotTypes.DefaultSlotParams<T>
          // 工具栏右侧部分
          'toolbar-tools': VxeGridSlotTypes.DefaultSlotParams<T>
        } & FilteredSlots<T>
      >,
    },
  )
  return [Grid, extendedApi] as const
}

export type UseVbenVxeGrid = typeof useVbenVxeGrid
