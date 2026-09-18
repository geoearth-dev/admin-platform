import { parseMenuQuery } from './menu-model'
import type { TagProps } from 'element-plus'
import type { MenuType } from '@/types/base/api/system/menu'
import { z } from 'zod'
import type { VxeTableGridColumns } from '@/components/vxe-table'
import { $t, $te } from '@/plugins/locale'
import type { VbenFormSchema } from '@/plugins/vben-ui/form-ui'
import type { TreeSelect } from '@/types/base/api/common'
import type { SysMenu } from '@/types/base/api/system/menu'
import { formatDateTime } from '@/utils/date'

export function menuTitle(name: string) {
  return name && $te(name) ? $t(name) : name
}

export function getMenuTypeOptions(): Array<{
  label: string
  value: MenuType
  type: TagProps['type']
}> {
  return [
    { label: $t('system.menu.typeCatalog'), value: 'catalog', type: 'warning' },
    { label: $t('system.menu.typeMenu'), value: 'menu', type: 'success' },
    {
      label: $t('system.menu.typeEmbedded'),
      value: 'embedded',
      type: 'primary',
    },
    { label: $t('system.menu.typeLink'), value: 'link', type: 'primary' },
    { label: $t('system.menu.typeButton'), value: 'button', type: 'info' },
  ]
}

export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      component: 'Input',
      fieldName: 'menuName',
      label: $t('system.menu.menuName'),
    },
    {
      component: 'Select',
      fieldName: 'status',
      label: $t('system.menu.status'),
      componentProps: {
        clearable: true,
        options: [
          { label: $t('common.enabled'), value: 1 },
          { label: $t('common.disabled'), value: 0 },
        ],
      },
    },
  ]
}

export function useColumns(): VxeTableGridColumns<SysMenu> {
  return [
    {
      field: 'menuName',
      title: $t('system.menu.menuName'),
      minWidth: 230,
      align: 'left',
      fixed: 'left',
      treeNode: true,
      slots: { default: 'title' },
    },
    {
      field: 'routeName',
      title: $t('system.menu.routeName'),
      minWidth: 160,
      formatter: ({ row }) =>
        row.routeName ||
        (row.path ? row.path.charAt(0).toUpperCase() + row.path.slice(1) : '—'),
    },
    { field: 'order', title: $t('system.menu.order'), width: 90 },
    {
      field: 'menuType',
      title: $t('system.menu.type'),
      width: 100,
      slots: { default: 'menuType' },
    },
    { field: 'perms', title: $t('system.menu.authCode'), minWidth: 200 },
    {
      field: 'path',
      title: $t('system.menu.path'),
      minWidth: 160,
      align: 'left',
    },
    {
      field: 'component',
      title: $t('system.menu.componentOrLink'),
      minWidth: 200,
      align: 'left',
      formatter: ({ row }) => row.link || row.iframeSrc || row.component || '—',
    },
    {
      field: 'status',
      title: $t('system.menu.status'),
      width: 100,
      cellRender: {
        name: 'CellTag',
        options: [
          { label: $t('common.enabled'), value: 1, type: 'success' },
          { label: $t('common.disabled'), value: 0, type: 'danger' },
        ],
      },
    },
    {
      field: 'createTime',
      title: $t('system.menu.createTime'),
      width: 180,
      formatter: ({ cellValue }) =>
        cellValue ? formatDateTime(cellValue) : '—',
    },
    {
      field: 'operation',
      title: $t('system.menu.operation'),
      width: 240,
      fixed: 'right',
      align: 'center',
      slots: { default: 'action' },
    },
  ]
}

export function useFormSchema(): VbenFormSchema[] {
  const requiredText = (label: string, max: number) =>
    z
      .string()
      .trim()
      .min(1, $t('ui.formRules.required', [label]))
      .max(max)
  const show = (types: string[], extra: string[] = []) => ({
    triggerFields: ['menuType', ...extra],
    resolve: ({ values }: { values: Record<string, unknown> }) => ({
      if: types.includes(String(values.menuType)),
    }),
  })
  const routes = ['catalog', 'menu', 'embedded', 'link']
  const pages = ['menu', 'embedded']
  const urlRule = z
    .string()
    .trim()
    .refine((value) => {
      try {
        return ['http:', 'https:'].includes(new URL(value).protocol)
      } catch {
        return false
      }
    }, $t('system.menu.urlInvalid'))
    .max(200)
  const pathRule = requiredText($t('system.menu.path'), 200).refine(
    (value) => !value.includes('://') && !/[?#\\\s]/.test(value),
    $t('system.menu.pathHelp'),
  )
  const internalPath = z
    .string()
    .trim()
    .max(200)
    .refine(
      (value) =>
        !value ||
        (value.startsWith('/') &&
          !value.startsWith('//') &&
          !value.includes('://')),
      $t('system.menu.internalPathHelp'),
    )
    .nullish()
  const components = Object.keys(import.meta.glob('/src/views/**/*.vue'))
    .map((path) => path.replace('/src/views/', '').replace(/\.vue$/, ''))
    .sort()
  const checkbox = (
    fieldName: string,
    label: string,
    types: string[],
  ): VbenFormSchema => ({
    component: 'Checkbox',
    fieldName,
    defaultValue: false,
    dependencies: show(types),
    renderComponentContent: () => ({ default: () => $t(label) }),
  })
  return [
    {
      component: 'RadioGroup',
      fieldName: 'menuType',
      label: $t('system.menu.type'),
      defaultValue: 'catalog',
      rules: 'required',
      formItemClass: 'col-span-full',
      componentProps: { isButton: true, options: getMenuTypeOptions() },
    },
    {
      component: 'Input',
      fieldName: 'routeName',
      label: $t('system.menu.routeName'),
      help: $t('system.menu.routeNameHelp'),
      componentProps: { maxlength: 50 },
      dependencies: show(routes),
      rules: z.string().trim().max(50).nullish(),
    },
    {
      component: 'TreeSelect',
      fieldName: 'parentId',
      label: $t('system.menu.parent'),
      defaultValue: 0,
      rules: 'selectRequired',
      componentProps: {
        data: [],
        nodeKey: 'id',
        props: { label: 'label', children: 'children' },
        checkStrictly: true,
        filterable: true,
        defaultExpandAll: true,
        class: 'w-full',
      },
    },
    {
      component: 'Input',
      fieldName: 'menuName',
      label: $t('system.menu.menuTitle'),
      help: $t('system.menu.menuNameHelp'),
      componentProps: { maxlength: 50 },
      rules: requiredText($t('system.menu.menuTitle'), 50),
    },
    {
      component: 'InputNumber',
      fieldName: 'order',
      label: $t('system.menu.order'),
      defaultValue: 0,
      rules: z.number().int().min(0),
      componentProps: { min: 0, precision: 0 },
    },
    {
      component: 'Input',
      fieldName: 'path',
      label: $t('system.menu.path'),
      help: $t('system.menu.pathHelp'),
      componentProps: { maxlength: 200 },
      dependencies: {
        triggerFields: ['menuType'],
        resolve: ({ values }) => ({
          if: values.menuType !== 'button',
          rules: values.menuType !== 'button' ? pathRule : undefined,
        }),
      },
    },
    {
      component: 'Input',
      fieldName: 'activePath',
      label: $t('system.menu.activePath'),
      help: $t('system.menu.activePathHelp'),
      rules: internalPath,
      dependencies: show(pages),
    },
    ...(['icon', 'activeIcon'] as const).map((fieldName): VbenFormSchema => ({
      component: 'IconPicker',
      fieldName,
      label: $t('system.menu.' + fieldName),
      componentProps: { prefix: 'lucide' },
      dependencies: show(routes),
    })),
    {
      component: 'Select',
      fieldName: 'component',
      label: $t('system.menu.component'),
      help: $t('system.menu.componentHelp'),
      componentProps: {
        filterable: true,
        allowCreate: true,
        clearable: true,
        options: components.map((value) => ({ label: value, value })),
      },
      dependencies: {
        triggerFields: ['menuType'],
        resolve: ({ values }) => ({
          if: values.menuType === 'menu',
          rules:
            values.menuType === 'menu'
              ? requiredText($t('system.menu.component'), 255)
              : undefined,
        }),
      },
    },
    {
      component: 'Input',
      fieldName: 'linkSrc',
      label: $t('system.menu.linkSrc'),
      componentProps: { placeholder: 'https://example.com', maxlength: 200 },
      dependencies: {
        triggerFields: ['menuType'],
        resolve: ({ values }) => {
          const visible =
            values.menuType === 'link' || values.menuType === 'embedded'
          return { if: visible, rules: visible ? urlRule : undefined }
        },
      },
    },
    {
      component: 'Input',
      fieldName: 'perms',
      label: $t('system.menu.authCode'),
      help: $t('system.menu.authCodeHelp'),
      componentProps: { maxlength: 100 },
      dependencies: {
        triggerFields: ['menuType'],
        resolve: ({ values }) => ({
          rules:
            values.menuType === 'button'
              ? requiredText($t('system.menu.authCode'), 100)
              : z.string().max(100).nullish(),
        }),
      },
    },
    {
      component: 'RadioGroup',
      fieldName: 'status',
      label: $t('system.menu.status'),
      defaultValue: 1,
      rules: z.union([z.literal(0), z.literal(1)]),
      componentProps: {
        isButton: true,
        options: [
          { label: $t('common.enabled'), value: 1 },
          { label: $t('common.disabled'), value: 0 },
        ],
      },
    },
    {
      component: 'Select',
      fieldName: 'badgeType',
      label: $t('system.menu.badgeType.title'),
      componentProps: {
        clearable: true,
        options: [
          { label: $t('system.menu.badgeType.dot'), value: 'dot' },
          { label: $t('system.menu.badgeType.normal'), value: 'normal' },
        ],
      },
      dependencies: show(routes),
    },
    {
      component: 'Input',
      fieldName: 'badge',
      label: $t('system.menu.badge'),
      componentProps: { maxlength: 50 },
      dependencies: {
        triggerFields: ['menuType', 'badgeType'],
        resolve: ({ values }) => ({
          if: values.menuType !== 'button',
          disabled: values.badgeType !== 'normal',
        }),
      },
    },
    {
      component: 'Select',
      fieldName: 'badgeVariants',
      label: $t('system.menu.badgeVariants'),
      componentProps: {
        clearable: true,
        options: [
          'default',
          'destructive',
          'primary',
          'success',
          'warning',
        ].map((value) => ({ label: value, value })),
      },
      dependencies: show(routes),
    },
    {
      component: 'Divider',
      fieldName: 'advancedDivider',
      hideLabel: true,
      formItemClass: 'col-span-full',
      dependencies: show(routes),
      renderComponentContent: () => ({
        default: () => $t('system.menu.advancedSettings'),
      }),
    },
    {
      component: 'Input',
      fieldName: 'redirect',
      label: $t('system.menu.redirect'),
      help: $t('system.menu.redirectHelp'),
      rules: internalPath,
      dependencies: show(['catalog', 'menu']),
    },
    {
      component: 'Textarea',
      fieldName: 'query',
      label: $t('system.menu.query'),
      help: $t('system.menu.queryHelp', { example: '{"id":1}' }),
      componentProps: { rows: 3 },
      rules: z
        .string()
        .refine((value) => {
          try {
            parseMenuQuery(value)
            return true
          } catch {
            return false
          }
        }, $t('system.menu.queryInvalid'))
        .nullish(),
      dependencies: show(routes),
    },
    checkbox('keepAlive', 'system.menu.keepAlive', ['menu']),
    checkbox('affixTab', 'system.menu.affixTab', pages),
    checkbox('hideInMenu', 'system.menu.hideInMenu', routes),
    checkbox('hideChildrenInMenu', 'system.menu.hideChildrenInMenu', [
      'catalog',
      'menu',
    ]),
    checkbox('hideInBreadcrumb', 'system.menu.hideInBreadcrumb', routes),
    checkbox('hideInTab', 'system.menu.hideInTab', pages),
    checkbox('openInNewWindow', 'system.menu.openInNewWindow', pages),
    {
      ...checkbox('noBasicLayout', 'system.menu.noBasicLayout', pages),
      dependencies: {
        triggerFields: ['menuType', 'parentId'],
        resolve: ({ values }) => ({
          if: pages.includes(String(values.menuType)) && values.parentId === 0,
        }),
      },
    },
    {
      component: 'InputNumber',
      fieldName: 'affixTabOrder',
      label: $t('system.menu.affixTabOrder'),
      defaultValue: 0,
      componentProps: { min: 0, precision: 0 },
      rules: z.number().int().min(0),
      dependencies: {
        triggerFields: ['menuType', 'affixTab'],
        resolve: ({ values }) => ({
          if: pages.includes(String(values.menuType)) && !!values.affixTab,
        }),
      },
    },
    {
      component: 'InputNumber',
      fieldName: 'maxNumOfOpenTab',
      label: $t('system.menu.maxNumOfOpenTab'),
      help: $t('system.menu.maxNumOfOpenTabHelp'),
      defaultValue: -1,
      componentProps: { min: -1, precision: 0 },
      rules: z
        .number()
        .int()
        .refine(
          (value) => value === -1 || value > 0,
          $t('system.menu.maxNumOfOpenTabHelp'),
        ),
      dependencies: show(pages),
    },
  ]
}

/** 排除按钮、当前菜单及其后代，避免把菜单移动到自己下面。 */
export function getParentMenuTree(
  menus: SysMenu[],
  currentId?: number,
): TreeSelect[] {
  const excluded = new Set<number>()
  const descendants = currentId == null ? [] : [currentId]
  while (descendants.length) {
    const id = descendants.pop()!
    if (excluded.has(id)) continue
    excluded.add(id)
    descendants.push(
      ...menus.filter((menu) => menu.parentId === id).map((menu) => menu.id),
    )
  }
  const candidates = menus.filter(
    (menu) =>
      menu.menuType !== 'button' &&
      !menu.link &&
      !menu.iframeSrc &&
      !excluded.has(menu.id),
  )
  const nodes = new Map<number, TreeSelect>(
    candidates.map((menu) => [
      menu.id,
      { id: menu.id, label: menuTitle(menu.menuName), children: [] },
    ]),
  )
  const root: TreeSelect = {
    id: 0,
    label: $t('system.menu.root'),
    children: [],
  }
  for (const menu of candidates) {
    const parent = nodes.get(menu.parentId) ?? root
    parent.children!.push(nodes.get(menu.id)!)
  }
  return [root]
}
