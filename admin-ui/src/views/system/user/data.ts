import { h } from 'vue';

import { ElTag } from 'element-plus';

import { $t } from '@/plugins/locale';
import type { VbenFormSchema } from '@/plugins/vben-ui/form-ui';
import type { DescriptionsItemType } from '@/plugins/vben-ui/shadcn-ui';
import type { VxeTableGridColumns } from '@/components/vxe-table';
import type { SysUser } from '@/types/base/api/system/user';
import { formatDateTime } from '@/utils/date';
import { deptTreeSelect } from '@/api/system/user';

export function useFormSchema(): VbenFormSchema[] {
  return [
    {
      component: 'Input',
      fieldName: 'name',
      label: $t('system.user.name'),
      rules: 'required',
    },
    {
      component: 'ApiTreeSelect',
      componentProps: {
        api: deptTreeSelect,
        class: 'w-full',
        labelField: 'label',
        valueField: 'id',
        childrenField: 'children',
        disabledField: 'disabled',
        clearable: true,
        filterable: true,
        checkStrictly: true,
      },
      fieldName: 'deptId',
      label: $t('system.user.dept'),
      rules: 'required',
    },
    {
      component: 'RadioGroup',
      componentProps: {
        buttonStyle: 'solid',
        options: [
          { label: $t('common.enabled'), value: '1' },
          { label: $t('common.disabled'), value: '0' },
        ],
        optionType: 'button',
      },
      defaultValue: '1',
      fieldName: 'status',
      label: $t('system.user.status'),
    },
    {
      component: 'Textarea',
      fieldName: 'remark',
      label: $t('system.user.remark'),
    },
  ];
}

export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      component: 'Input',
      fieldName: 'name',
      label: $t('system.user.name'),
    },
    { component: 'Input', fieldName: 'id', label: $t('system.user.id') },
    {
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [
          { label: $t('common.enabled'), value: 1 },
          { label: $t('common.disabled'), value: 0 },
        ],
      },
      fieldName: 'status',
      label: $t('system.user.status'),
    },
    {
      component: 'Input',
      fieldName: 'remark',
      label: $t('system.user.remark'),
    },
    {
      component: 'RangePicker',
      fieldName: 'createTime',
      label: $t('system.user.createTime'),
    },
  ];
}

/**
 * 用户详情描述列表项
 * @param row 用户数据
 */
export function useDescriptionItems(row?: SysUser): DescriptionsItemType[] {
  const enabled = row?.status === '1';
  return [
    { label: $t('system.user.name'), content: row?.userName },
    { label: $t('system.user.id'), content: row?.id },
    { label: $t('system.user.dept'), content: row?.dept?.deptName },
    {
      label: $t('system.user.status'),
      content: () =>
        h(
          ElTag,
          {
            color: enabled ? 'success' : 'error',
          },
          {
            default: () => (enabled ? $t('common.enabled') : $t('common.disabled')),
          },
        ),
    },
    { label: $t('system.user.createTime'), content: row?.createTime },
    { label: $t('system.user.remark'), content: row?.remark },
  ];
}

export function useColumns<T = SysUser>(
  onStatusChange?: (newStatus: string, row: T) => PromiseLike<boolean>,
): VxeTableGridColumns {
  return [
    {
      field: 'userName',
      title: $t('system.user.name'),
      width: 200,
    },
    {
      field: 'id',
      title: $t('system.user.id'),
      width: 200,
    },
    {
      cellRender: {
        attrs: { beforeChange: onStatusChange },
        name: onStatusChange ? 'CellSwitch' : 'CellTag',
      },
      field: 'status',
      title: $t('system.user.status'),
      width: 100,
      formatter: ({ cellValue }) => {
        switch (String(cellValue)) {
          case '1':
            return $t('common.enabled');
          case '0':
            return $t('common.disabled');
          default:
            return '-';
        }
      },
    },
    {
      field: 'remark',
      minWidth: 100,
      title: $t('system.user.remark'),
    },
    {
      field: 'createTime',
      title: $t('system.user.createTime'),
      width: 200,
      formatter: ({ cellValue }) => formatDateTime(cellValue),
    },
    {
      align: 'center',
      field: 'operation',
      fixed: 'right',
      slots: { default: 'action' },
      title: $t('system.user.operation'),
      width: 180,
    },
  ];
}
