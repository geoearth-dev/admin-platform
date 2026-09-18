import { h } from 'vue';

import { ElTag } from 'element-plus';

import { $t } from '@/plugins/locale';
import type { VbenFormSchema } from '@/plugins/vben-ui/form-ui';
import type { DescriptionsItemType } from '@/plugins/vben-ui/shadcn-ui';
import type { VxeTableGridColumns } from '@/components/vxe-table';
import type { SysUser } from '@/types/base/api/system/user';
import { formatDateTime } from '@/utils/date';
import { deptTreeSelect } from '@/api/system/user';

/**
 * 提交新增、修改
 * @returns
 */
export function useFormSchema(
  isEdit = false,
  roleOptions: {
    label: string;
    value: number;
    disabled?: boolean;
  }[] = [],
  postOptions: {
    label: string;
    value: number;
    disabled?: boolean;
  }[] = [],
): VbenFormSchema[] {
  const schema: VbenFormSchema[] = [
    {
      component: 'Input',
      fieldName: 'userName',
      label: $t('system.user.name'),
      rules: 'required',
      componentProps: {
        maxlength: 30,
        disabled: isEdit,
        autocomplete: 'off',
      },
    },
    {
      component: 'Input',
      fieldName: 'nickName',
      label: $t('system.user.nickName'),
      rules: 'required',
      componentProps: { maxlength: 30 },
    },
    {
      component: 'InputPassword',
      fieldName: 'password',
      label: $t('authentication.password'),
      rules: 'required',
      componentProps: { autocomplete: 'new-password' },
      dependencies: {
        triggerFields: [],
        resolve: () => ({ if: !isEdit }),
      },
    },
    {
      component: 'ApiTreeSelect',
      fieldName: 'deptId',
      label: $t('system.user.dept'),
      rules: 'required',
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
    },
    {
      component: 'Input',
      fieldName: 'phoneNumber',
      label: $t('system.user.phoneNumber'),
      componentProps: { maxlength: 11 },
    },
    {
      component: 'Input',
      fieldName: 'email',
      label: $t('system.user.email'),
      componentProps: { maxlength: 50 },
    },
    {
      component: 'Select',
      fieldName: 'sex',
      label: $t('system.user.sex'),
      defaultValue: '2',
      componentProps: {
        options: [
          { label: $t('system.user.male'), value: '0' },
          { label: $t('system.user.female'), value: '1' },
          { label: $t('system.user.unknown'), value: '2' },
        ],
      },
    },
    {
      component: 'RadioGroup',
      fieldName: 'status',
      label: $t('system.user.status'),
      defaultValue: '1',
      componentProps: {
        options: [
          { label: $t('common.enabled'), value: '1' },
          { label: $t('common.disabled'), value: '0' },
        ],
      },
    },
    {
      component: 'Select',
      fieldName: 'postIds',
      label: $t('system.user.posts'),
      defaultValue: [],
      componentProps: {
        multiple: true,
        clearable: true,
        filterable: true,
        options: postOptions,
      },
    },
    {
      component: 'Select',
      fieldName: 'roleIds',
      label: $t('system.user.roles'),
      defaultValue: [],
      componentProps: {
        multiple: true,
        clearable: true,
        filterable: true,
        options: roleOptions,
      },
    },
    {
      component: 'Textarea',
      fieldName: 'remark',
      label: $t('system.user.remark'),
      formItemClass: 'md:col-span-2',
      componentProps: { rows: 3 },
    },
  ];
  return schema;
}
/**
 * 列表查询条件
 * @returns
 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      component: 'Input',
      fieldName: 'userName',
      label: $t('system.user.name'),
    },
    { component: 'Input', fieldName: 'id', label: $t('system.user.id') },
    {
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [
          { label: $t('common.enabled'), value: '1' },
          { label: $t('common.disabled'), value: '0' },
        ],
      },
      fieldName: 'status',
      label: $t('system.user.status'),
    },
    {
      component: 'Input',
      fieldName: 'phoneNumber',
      label: $t('system.user.phoneNumber'),
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
export function useDescriptionItems(row?: SysUser): {
  key: string;
  title: string;
  items: DescriptionsItemType[];
}[] {
  const display = (value?: string | number | null) =>
    value === undefined || value === null || value === '' ? '—' : String(value);
  const displayTime = (value?: string) => (value ? formatDateTime(value) : '—');
  const sexLabels: Record<string, string> = {
    '0': $t('system.user.male'),
    '1': $t('system.user.female'),
    '2': $t('system.user.unknown'),
  };
  const status = row?.status;
  const statusText =
    status === '1'
      ? $t('common.enabled')
      : status === '0'
        ? $t('common.disabled')
        : $t('system.user.unknown');
  // 详情接口的 roles 是候选列表，只展示 roleIds 中已分配的角色。
  const roleNames = row?.roleIds?.map((id) => {
    const role = row.roles?.find((item) => item.id === id);
    return role?.roleName || $t('system.user.roleIdValue', { id });
  });

  return [
    {
      key: 'basic',
      title: $t('system.user.basicInfo'),
      items: [
        {
          label: $t('system.user.id'),
          content: display(row?.id),
        },
        {
          label: $t('system.user.loginAccount'),
          content: display(row?.userName),
        },
        {
          label: $t('system.user.nickName'),
          content: display(row?.nickName),
        },
        {
          label: $t('system.user.dept'),
          content: display(row?.dept?.deptName),
        },
        {
          label: $t('system.user.phoneNumber'),
          content: display(row?.phoneNumber),
        },
        {
          label: $t('system.user.email'),
          content: display(row?.email),
        },
        {
          label: $t('system.user.status'),
          content: () =>
            h(
              ElTag,
              {
                type: status === '1' ? 'success' : status === '0' ? 'danger' : 'info',
              },
              {
                default: () => statusText,
              },
            ),
        },
        {
          label: $t('system.user.sex'),
          content: row?.sex ? (sexLabels[row.sex] ?? $t('system.user.unknown')) : '—',
        },
        {
          label: $t('system.user.postIds'),
          content: display(row?.postIds?.join('、')),
          span: 'filled',
        },
        {
          label: $t('system.user.roles'),
          content: display(roleNames?.join('、')),
          span: 'filled',
        },
      ],
    },
    {
      key: 'other',
      title: $t('system.user.otherInfo'),
      items: [
        {
          label: $t('system.user.createBy'),
          content: display(row?.createBy),
        },
        {
          label: $t('system.user.createTime'),
          content: displayTime(row?.createTime),
        },
        {
          label: $t('system.user.updateBy'),
          content: display(row?.updateBy),
        },
        {
          label: $t('system.user.updateTime'),
          content: displayTime(row?.updateTime),
        },
        {
          label: $t('system.user.lastLoginIp'),
          content: display(row?.lastLoginIp),
        },
        {
          label: $t('system.user.lastLoginTime'),
          content: displayTime(row?.lastLoginTime),
        },
        {
          label: $t('system.user.remark'),
          content: display(row?.remark),
          contentStyle: {
            whiteSpace: 'pre-wrap',
            overflowWrap: 'anywhere',
          },
          span: 'filled',
        },
      ],
    },
  ];
}

export function useColumns<T = SysUser>(
  onStatusChange?: (newStatus: string, row: T) => PromiseLike<boolean>,
): VxeTableGridColumns {
  return [
    {
      type: 'checkbox',
      width: 48,
      align: 'center',
    },
    {
      field: 'id',
      title: $t('system.user.id'),
      width: 80,
    },
    {
      field: 'userName',
      title: $t('system.user.name'),
      width: 100,
      slots: {
        default: 'userName',
      },
    },
    {
      field: 'nickName',
      title: $t('system.user.nickName'),
      width: 100,
    },
    {
      field: 'dept.deptName',
      title: $t('system.user.dept'),
      width: 100,
    },
    {
      field: 'phoneNumber',
      title: $t('system.user.phoneNumber'),
      width: 100,
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
      minWidth: 200,
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
