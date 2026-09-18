import type { VxeTableGridColumns } from '@/components/vxe-table';
import { $t } from '@/plugins/locale';
import type { VbenFormSchema } from '@/plugins/vben-ui/form-ui';
import type { SysRole } from '@/types/base/api/system/role';
import { formatDateTime } from '@/utils/date';
/**
 * 提交新增、修改
 * @returns
 */
export function useFormSchema(): VbenFormSchema[] {
  return [
    {
      component: 'Input',
      fieldName: 'roleName',
      label: $t('system.role.roleName'),
      rules: 'required',
      componentProps: { maxlength: 30 },
    },
    {
      component: 'Input',
      fieldName: 'roleKey',
      label: $t('system.role.roleKey'),
      help: $t('system.role.roleKeyHelp'),
      rules: 'required',
      componentProps: { maxlength: 100 },
    },
    {
      component: 'InputNumber',
      fieldName: 'roleSort',
      label: $t('system.role.roleSort'),
      rules: 'required',
      defaultValue: 0,
      componentProps: { min: 0, precision: 0 },
    },
    {
      component: 'RadioGroup',
      fieldName: 'status',
      label: $t('system.role.status'),
      defaultValue: '1',
      componentProps: {
        options: [
          { label: $t('common.enabled'), value: '1' },
          { label: $t('common.disabled'), value: '0' },
        ],
      },
    },
    {
      component: 'Textarea',
      fieldName: 'remark',
      label: $t('system.role.remark'),
    },
  ];
}

export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      component: 'Input',
      fieldName: 'roleName',
      label: $t('system.role.roleName'),
    },
    {
      component: 'Input',
      fieldName: 'roleKey',
      label: $t('system.role.roleKey'),
    },
    {
      component: 'Select',
      fieldName: 'status',
      label: $t('system.role.status'),
      componentProps: {
        clearable: true,
        options: [
          { label: $t('common.enabled'), value: '1' },
          { label: $t('common.disabled'), value: '0' },
        ],
      },
    },
    {
      component: 'RangePicker',
      fieldName: 'createTime',
      label: $t('system.role.createTime'),
    },
  ];
}

export function useColumns(
  onStatusChange: (status: string, row: SysRole) => Promise<boolean>,
  canEdit: boolean,
): VxeTableGridColumns<SysRole> {
  return [
    { type: 'checkbox', width: 48, align: 'center' },
    { field: 'id', title: $t('system.role.id'), width: 100 },
    { field: 'roleName', title: $t('system.role.roleName'), minWidth: 160 },
    { field: 'roleKey', title: $t('system.role.roleKey'), minWidth: 140 },
    { field: 'roleSort', title: $t('system.role.roleSort'), width: 100 },
    {
      field: 'status',
      title: $t('system.role.status'),
      width: 110,
      cellRender: {
        name: 'CellSwitch',
        attrs: { beforeChange: onStatusChange },
        props: { activeValue: '1', inactiveValue: '0', disabled: !canEdit },
      },
    },
    { field: 'remark', title: $t('system.role.remark'), minWidth: 120 },
    {
      field: 'createTime',
      title: $t('system.role.createTime'),
      width: 180,
      formatter: ({ cellValue }) => (cellValue ? formatDateTime(cellValue) : '—'),
    },
    {
      field: 'operation',
      title: $t('system.role.operation'),
      width: 200,
      fixed: 'right',
      align: 'center',
      slots: { default: 'action' },
    },
  ];
}
