import type { VxeGridProps, VxeUIExport } from 'vxe-table'

import type { Recordable } from '@/types'

import type { VxeGridApi } from './api'

import { formatDate, formatDateTime } from '@/utils/date'
import { isFunction } from 'es-toolkit/compat'

export function extendProxyOptions(
  api: VxeGridApi,
  options: VxeGridProps,
  getFormValues: () => Recordable<unknown>,
) {
  ;[
    'query',
    'querySuccess',
    'queryError',
    'queryAll',
    'queryAllSuccess',
    'queryAllError',
  ].forEach((key) => {
    extendProxyOption(key, api, options, getFormValues)
  })
}

function extendProxyOption(
  key: string,
  api: VxeGridApi,
  options: VxeGridProps,
  getFormValues: () => Recordable<unknown>,
) {
  const { proxyConfig } = options
  const configFn = (proxyConfig?.ajax as Recordable<unknown>)?.[key]
  if (!isFunction(configFn)) {
    return options
  }

  const wrapperFn = async (
    params: Recordable<unknown>,
    customValues: Recordable<unknown>,
    ...args: Recordable<unknown>[]
  ) => {
    const formValues = getFormValues()
    const data = await configFn(
      params,
      {
        /**
         * 开启toolbarConfig.refresh功能
         * 点击刷新按钮 这里的值为PointerEvent 会携带错误参数
         */
        ...(customValues instanceof Event ? {} : customValues),
        ...formValues,
      },
      ...args,
    )
    return data
  }
  api.setState({
    gridOptions: {
      proxyConfig: {
        ajax: {
          [key]: wrapperFn,
        },
      },
    },
  })
}

export function extendsDefaultFormatter(vxeUI: VxeUIExport) {
  vxeUI.formats.add('formatDate', {
    tableCellFormatMethod({ cellValue }) {
      return formatDate(cellValue)
    },
  })

  vxeUI.formats.add('formatDateTime', {
    tableCellFormatMethod({ cellValue }) {
      return formatDateTime(cellValue)
    },
  })
}
