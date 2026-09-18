import { z } from 'zod'
import type { VxeTableGridColumns } from '@/components/vxe-table'
import { $t } from '@/plugins/locale'
import type { VbenFormSchema } from '@/plugins/vben-ui/form-ui'
import type { TreeSelect } from '@/types/base/api/common'
import type { DeptQueryParams, SysDept } from '@/types/base/api/system/dept'
import { formatDateTime } from '@/utils/date'

export function useSchema(): VbenFormSchema[] {
  return [
    {
      component: 'TreeSelect',
      fieldName: 'parentId',
      label: $t('system.dept.parentDept'),
      rules: 'selectRequired',
      formItemClass: 'col-span-full',
      componentProps: {
        data: [],
        nodeKey: 'id',
        props: { label: 'label', children: 'children', disabled: 'disabled' },
        checkStrictly: true,
        filterable: true,
        defaultExpandAll: true,
        class: 'w-full',
      },
    },
    {
      component: 'Input',
      fieldName: 'deptName',
      label: $t('system.dept.deptName'),
      componentProps: { maxlength: 30 },
      rules: z
        .string()
        .trim()
        .min(1, $t('ui.formRules.required', [$t('system.dept.deptName')]))
        .max(
          30,
          $t('ui.formRules.maxLength', [$t('system.dept.deptName'), 30]),
        ),
    },
    {
      component: 'InputNumber',
      fieldName: 'orderNum',
      label: $t('system.dept.orderNum'),
      rules: 'required',
      defaultValue: 0,
      componentProps: { min: 0, precision: 0, class: 'w-full' },
    },
    {
      component: 'Input',
      fieldName: 'leader',
      label: $t('system.dept.leader'),
    },
    {
      component: 'Input',
      fieldName: 'phone',
      label: $t('system.dept.phone'),
      componentProps: { maxlength: 11 },
      rules: z
        .string()
        .trim()
        .max(11, $t('ui.formRules.maxLength', [$t('system.dept.phone'), 11]))
        .optional(),
    },
    {
      component: 'Input',
      fieldName: 'email',
      label: $t('system.dept.email'),
      componentProps: { maxlength: 50 },
      rules: z
        .string()
        .trim()
        .max(50, $t('ui.formRules.maxLength', [$t('system.dept.email'), 50]))
        .refine(
          (value) => !value || z.email().safeParse(value).success,
          $t('system.dept.emailInvalid'),
        )
        .optional(),
    },
    {
      component: 'RadioGroup',
      fieldName: 'status',
      label: $t('system.dept.status'),
      defaultValue: '1',
      rules: 'required',
      componentProps: {
        options: [
          { label: $t('common.enabled'), value: '1' },
          { label: $t('common.disabled'), value: '0' },
        ],
      },
    },
  ]
}

export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      component: 'Input',
      fieldName: 'deptName',
      label: $t('system.dept.deptName'),
    },
    {
      component: 'Select',
      fieldName: 'status',
      label: $t('system.dept.status'),
      componentProps: {
        clearable: true,
        options: [
          { label: $t('common.enabled'), value: '1' },
          { label: $t('common.disabled'), value: '0' },
        ],
      },
    },
  ]
}

export function useColumns(): VxeTableGridColumns<SysDept> {
  return [
    {
      field: 'deptName',
      title: $t('system.dept.deptName'),
      align: 'left',
      fixed: 'left',
      treeNode: true,
      minWidth: 230,
    },
    { field: 'orderNum', title: $t('system.dept.orderNum'), width: 100 },
    { field: 'leader', title: $t('system.dept.leader'), minWidth: 110 },
    { field: 'phone', title: $t('system.dept.phone'), minWidth: 140 },
    { field: 'email', title: $t('system.dept.email'), minWidth: 180 },
    {
      field: 'status',
      title: $t('system.dept.status'),
      width: 100,
      cellRender: {
        name: 'CellTag',
        options: [
          { label: $t('common.enabled'), value: '1', type: 'success' },
          { label: $t('common.disabled'), value: '0', type: 'danger' },
        ],
      },
    },
    {
      field: 'createTime',
      title: $t('system.dept.createTime'),
      width: 180,
      formatter: ({ cellValue }) =>
        cellValue ? formatDateTime(cellValue) : '—',
    },
    {
      field: 'operation',
      title: $t('system.dept.operation'),
      width: 240,
      fixed: 'right',
      align: 'center',
      slots: { default: 'action' },
    },
  ]
}

/** 保留匹配部门的祖先，搜索子部门时仍能看清组织层级。 */
export function filterDeptTree(
  departments: SysDept[],
  query: DeptQueryParams,
): SysDept[] {
  const keyword = query.deptName?.trim().toLowerCase() ?? ''
  const included = new Set<number>()
  const nodes = new Map(departments.map((dept) => [dept.id, dept]))
  for (const dept of departments) {
    if (query.status !== undefined && dept.status !== query.status) continue
    if (keyword && !dept.deptName?.toLowerCase().includes(keyword)) continue
    let current: SysDept | undefined = dept
    while (current && !included.has(current.id)) {
      included.add(current.id)
      current =
        current.parentId == null ? undefined : nodes.get(current.parentId)
    }
  }
  return departments.filter((dept) => included.has(dept.id))
}

/** 编辑时传入后端 exclude 接口返回的候选部门，不重复计算后代。 */
export function getParentDeptTree(
  departments: SysDept[],
  current?: SysDept,
): TreeSelect[] {
  if (current?.parentId === 0) {
    return [{ id: 0, label: $t('system.dept.root'), children: [] }]
  }
  const nodes = new Map<number, TreeSelect>(
    departments.map((dept) => [
      dept.id,
      {
        id: dept.id,
        label: dept.deptName ?? '',
        disabled: dept.status !== '1',
        children: [],
      },
    ]),
  )
  const roots: TreeSelect[] = []
  for (const dept of departments) {
    const node = nodes.get(dept.id)!
    const parent = dept.parentId == null ? undefined : nodes.get(dept.parentId)
    if (parent) parent.children!.push(node)
    else roots.push(node)
  }
  // 数据权限可能不包含现有上级，仍需显示它的名称并允许保留原归属。
  if (current?.parentId != null && !nodes.has(current.parentId)) {
    roots.push({
      id: current.parentId,
      label: current.parentName || $t('system.dept.parentUnavailable'),
      disabled: true,
    })
  }
  return roots
}
