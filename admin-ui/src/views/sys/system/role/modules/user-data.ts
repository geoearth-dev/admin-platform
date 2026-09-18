import type { VxeTableGridColumns } from '@/components/vxe-table';
import type { VbenFormSchema } from '@/plugins/vben-ui/form-ui';
import type { SysUser } from '@/types/base/api/system/user';
import { $t } from '@/plugins/locale';
import { formatDateTime } from '@/utils/date';

export function useUserSearchSchema(): VbenFormSchema[] {
  return [
    { component: 'Input', fieldName: 'userName', label: $t('system.user.loginAccount') },
    { component: 'Input', fieldName: 'phoneNumber', label: $t('system.user.phoneNumber') },
  ];
}

export function useUserColumns(): NonNullable<VxeTableGridColumns<SysUser>> {
  return [
    { type: 'checkbox', width: 48, align: 'center' },
    { field: 'userName', title: $t('system.user.loginAccount'), minWidth: 130 },
    { field: 'nickName', title: $t('system.user.nickName'), minWidth: 120 },
    { field: 'email', title: $t('system.user.email'), minWidth: 170 },
    { field: 'phoneNumber', title: $t('system.user.phoneNumber'), minWidth: 140 },
    {
      field: 'status',
      title: $t('system.user.status'),
      width: 100,
      cellRender: {
        name: 'CellTag',
        props: { size: 'small' },
        options: [
          { value: '1', label: $t('common.enabled'), type: 'success' },
          { value: '0', label: $t('common.disabled'), type: 'danger' },
        ],
      },
    },
    {
      field: 'createTime',
      title: $t('system.user.createTime'),
      width: 180,
      formatter: ({ cellValue }) => (cellValue ? formatDateTime(cellValue) : '—'),
    },
  ];
}
