import type { VxeUIExport } from 'vxe-table'
import { h } from 'vue'
import { ElButton, ElImage } from 'element-plus'

export interface OnActionClickParams<
  T extends object = Record<string, unknown>,
> {
  code: string
  row: T
}
interface Operation {
  code: string
  text?: string
}

export function configureElementPlusTable(ui: VxeUIExport) {
  ui.setConfig({
    grid: {
      align: 'center',
      border: false,
      round: true,
      size: 'small',
      showOverflow: true,
      columnConfig: { resizable: true },
      minHeight: 180,
      formConfig: { enabled: false },
      proxyConfig: {
        autoLoad: true,
        response: { result: 'items', total: 'total', list: 'items' },
        showActiveMsg: true,
        showResponseMsg: false,
      },
    },
  })
  ui.renderer.add('CellImage', {
    renderTableDefault({ props }, { row, column }) {
      const src = String(row[column.field] ?? '')
      return h(ElImage, {
        class: 'size-10',
        src,
        previewSrcList: [src],
        previewTeleported: true,
        ...props,
      })
    },
  })
  ui.renderer.add('CellLink', {
    renderTableDefault({ props, attrs }, params) {
      return h(
        ElButton,
        {
          size: 'small',
          link: true,
          type: 'primary',
          ...props,
          onClick: () => attrs?.onClick?.(params),
        },
        () => props?.text ?? '',
      )
    },
  })
  ui.renderer.add('CellOperation', {
    renderTableDefault({ options, attrs, props }, { row }) {
      const actions: Array<string | Operation> = options ?? []
      const labels: Record<string, string> = {
        view: '查看',
        edit: '编辑',
        delete: '删除',
      }
      return h(
        'div',
        { class: 'inline-flex gap-2' },
        actions.map((value) => {
          const action = typeof value === 'string' ? { code: value } : value
          return h(
            ElButton,
            {
              key: action.code,
              link: true,
              type: action.code === 'delete' ? 'danger' : 'primary',
              ...props,
              onClick: () => attrs?.onClick?.({ code: action.code, row }),
            },
            () => action.text ?? labels[action.code] ?? action.code,
          )
        }),
      )
    },
  })
}
