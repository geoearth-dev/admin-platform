import type { VxeUIExport } from 'vxe-table';
import { h } from 'vue';
import { ElButton, ElImage, ElPopconfirm, ElSwitch, ElTag, type ButtonProps } from 'element-plus';
import type { VxeTableGridOptions } from './types';
import { $t, $te } from '@/plugins/locale';
import { IconifyIcon } from '@/assets/icons';
import { get } from '@/utils/inference';
import { objectOmit } from '@vueuse/core';

export interface OnActionClickParams<T extends object = Record<string, unknown>> {
  code: string;
  row: T;
}

export interface CellOperation {
  code: string;
  text?: string | ((row: Record<string, unknown>) => string);
  icon?: string | ((row: Record<string, unknown>) => string);
  show?: boolean | ((row: Record<string, unknown>) => boolean);
  disabled?: boolean | ((row: Record<string, unknown>) => boolean);
  loading?: boolean | ((row: Record<string, unknown>) => boolean);
  size?: ButtonProps['size'];
  type?: ButtonProps['type'];
  link?: boolean;
}
export function configVxeTable(vxeUI: VxeUIExport) {
  vxeUI.setConfig({
    grid: {
      align: 'center',
      border: false,
      columnConfig: { resizable: true },
      minHeight: 180,
      formConfig: { enabled: false }, // 全局禁用vxe-table的表单配置，使用formOptions
      proxyConfig: {
        autoLoad: true,
        response: { result: 'items', total: 'total', list: 'items' },
        showActiveMsg: true,
        showResponseMsg: false,
      },
      round: true,
      showOverflow: true,
      size: 'small',
    } as VxeTableGridOptions,
  });
  // 表格配置项可以用 cellRender: { name: 'CellImage' },
  vxeUI.renderer.add('CellImage', {
    renderTableDefault(renderOpts, params) {
      const { props } = renderOpts;
      const { column, row } = params;
      const src = row[column.field];
      return h(ElImage, {
        src,
        previewSrcList: [src],
        ...props,
      });
    },
  });

  vxeUI.renderer.add('CellLink', {
    renderTableDefault(renderOpts) {
      const { props } = renderOpts;
      return h(
        ElButton,
        {
          size: 'small',
          link: true,
          ...props,
        },
        { default: () => props?.text },
      );
    },
  });
  // 单元格渲染： Tag
  vxeUI.renderer.add('CellTag', {
    renderTableDefault({ options, props }, { column, row }) {
      const value = get(row, column.field);
      const tagOptions = options ?? [
        { color: 'success', label: $t('common.enabled'), value: 1 },
        { color: 'error', label: $t('common.disabled'), value: 0 },
      ];
      const tagItem = tagOptions.find((item) => item.value === value);
      return h(
        ElTag,
        {
          ...props,
          ...objectOmit(tagItem ?? {}, ['label']),
        },
        { default: () => tagItem?.label ?? value },
      );
    },
  });
  vxeUI.renderer.add('CellSwitch', {
    renderTableDefault({ attrs, props }, { column, row }) {
      const field = column.field;
      const loadingKey = `__loading_${field}`;

      const activeValue = props?.activeValue ?? '1';
      const inactiveValue = props?.inactiveValue ?? '0';

      return h(ElSwitch, {
        activeText: $t('common.enabled'),
        inactiveText: $t('common.disabled'),
        inlinePrompt: true,
        ...props,

        activeValue,
        inactiveValue,
        modelValue: row[field],
        loading: Boolean(row[loadingKey]),

        beforeChange: async (): Promise<boolean> => {
          if (row[loadingKey]) return false;

          const newValue = row[field] === activeValue ? inactiveValue : activeValue;

          row[loadingKey] = true;

          try {
            const result = await attrs?.beforeChange?.(newValue, row);
            return result !== false;
          } catch {
            return false;
          } finally {
            row[loadingKey] = false;
          }
        },

        'onUpdate:modelValue': (value: boolean | number | string) => {
          row[field] = value;
        },
      });
    },
  });

  /**
   * 注册表格的操作按钮渲染器
   */
  vxeUI.renderer.add('CellOperation', {
    renderTableDefault({ attrs, options, props }, { column, row }) {
      const defaultProps: Partial<CellOperation> = {
        size: 'small',
        link: true,
        ...props,
      };
      const presets: Record<string, Partial<CellOperation>> = {
        delete: {
          type: 'danger',
          text: $t('common.delete'),
        },
        edit: {
          text: $t('common.edit'),
        },
        detail: {
          text: $t('common.detail'),
        },
      };
      const items: Array<string | CellOperation> = options ?? ['edit', 'detail', 'delete'];
      const operations = items
        .map((item) => {
          const option: CellOperation = typeof item === 'string' ? { code: item } : item;
          const operation: CellOperation = {
            ...presets[option.code],
            ...defaultProps,
            ...option,
          };
          const localeKey = `common.${operation.code}`;
          const defaultText = $te(localeKey) ? $t(localeKey) : operation.code;
          return {
            code: operation.code,
            text:
              typeof operation.text === 'function'
                ? operation.text(row)
                : (operation.text ?? defaultText),
            icon: typeof operation.icon === 'function' ? operation.icon(row) : operation.icon,
            show:
              typeof operation.show === 'function' ? operation.show(row) : (operation.show ?? true),
            disabled:
              typeof operation.disabled === 'function'
                ? operation.disabled(row)
                : (operation.disabled ?? false),
            loading:
              typeof operation.loading === 'function'
                ? operation.loading(row)
                : (operation.loading ?? false),
            size: operation.size ?? 'small',
            type: operation.type ?? 'primary',
            link: operation.link ?? true,
          };
        })
        .filter((operation) => operation.show);
      type ResolvedOperation = (typeof operations)[number];
      function handleClick(operation: ResolvedOperation) {
        if (operation.disabled || operation.loading) return;
        return attrs?.onClick?.({
          code: operation.code,
          row,
        });
      }
      function renderBtn(operation: ResolvedOperation, listen = true) {
        return h(
          ElButton,
          {
            key: operation.code,
            size: operation.size,
            type: operation.type,
            link: operation.link,
            disabled: operation.disabled,
            loading: operation.loading,
            onClick: (event: MouseEvent) => {
              event.stopPropagation();
              if (listen) {
                return handleClick(operation);
              }
            },
          },
          {
            default: () => [
              operation.icon
                ? h(IconifyIcon, {
                    class: 'mr-1 size-5',
                    icon: operation.icon,
                  })
                : null,
              operation.text,
            ],
          },
        );
      }

      function renderConfirm(operation: ResolvedOperation) {
        const nameField = attrs?.nameField ?? 'name';
        const name = String(row[nameField] ?? '');
        return h(
          ElPopconfirm,
          {
            key: operation.code,
            placement: 'top-start',
            width: 260,
            teleported: true,
            title: $t('ui.actionMessage.deleteConfirm', [name]),
            confirmButtonText: $t('common.confirm'),
            cancelButtonText: $t('common.cancel'),
            confirmButtonType: 'danger',
            disabled: operation.disabled || operation.loading,
            onConfirm: () => handleClick(operation),
          },
          {
            reference: () => renderBtn(operation, false),
          },
        );
      }

      let align: string;

      switch (column.align) {
        case 'center': {
          align = 'center';
          break;
        }
        case 'left': {
          align = 'flex-start';
          break;
        }
        default: {
          align = 'flex-end';
          break;
        }
      }
      return h(
        'div',
        {
          class: 'flex items-center gap-2 table-operations',
          style: { justifyContent: align },
        },
        operations.map((operation) =>
          operation.code === 'delete' ? renderConfirm(operation) : renderBtn(operation),
        ),
      );
    },
  });
}
