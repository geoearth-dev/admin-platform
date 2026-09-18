<script lang="ts" setup>
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { delDept, listDept } from '@/api/system/dept'
import { IconifyIcon } from '@/assets/icons'
import { Page } from '@/components/page'
import { VbenTableToolbar } from '@/components/table-toolbar'
import { useVbenVxeGrid } from '@/components/vxe-table'
import { DEPT_PERMISSION } from '@/constants/permissions'
import { useAccess } from '@/plugins/effects/access/use-access'
import { $t } from '@/plugins/locale'
import { confirm, useVbenDrawer } from '@/plugins/vben-ui/popup-ui'
import { VbenButton, VbenTableAction } from '@/plugins/vben-ui/shadcn-ui'
import type { SysDept } from '@/types/base/api/system/dept'
import { filterDeptTree, useColumns, useGridFormSchema } from './data'
import Form from './modules/form.vue'

const { hasAccessByCodes } = useAccess()
const hasPermission = (auth?: string | string[]) =>
  !auth || hasAccessByCodes(Array.isArray(auth) ? auth : [auth])
const toolbarActions = computed<Array<'create'>>(() =>
  hasAccessByCodes([DEPT_PERMISSION.add]) ? ['create'] : [],
)
const deleting = ref(false)
const querying = ref(false)
const expanded = ref(true)
const [FormDrawer, formDrawerApi] = useVbenDrawer({
  connectedComponent: Form,
  destroyOnClose: true,
})

const [Grid, gridApi] = useVbenVxeGrid<SysDept>({
  tableTitle: $t('system.dept.list'),
  formOptions: {
    schema: useGridFormSchema(),
    compact: true,
    showCollapseButton: false,
    wrapperClass:
      'grid-cols-1 md:grid-cols-[minmax(0,1fr)_minmax(0,1fr)_auto] gap-x-4',
  },
  gridOptions: {
    columns: useColumns(),
    height: 'auto',
    keepSource: true,
    pagerConfig: { enabled: false },
    proxyConfig: {
      response: { list: '' },
      ajax: {
        query: async (_params, values) => {
          querying.value = true
          try {
            // 取当前账号有权查看的部门，再保留搜索结果的祖先节点。
            const departments = await listDept()
            return filterDeptTree(departments, {
              deptName:
                typeof values.deptName === 'string'
                  ? values.deptName
                  : undefined,
              status:
                values.status === '0' || values.status === '1'
                  ? values.status
                  : undefined,
            })
          } finally {
            querying.value = false
          }
        },
      },
    },
    rowConfig: { keyField: 'id' },
    toolbarConfig: { custom: true, refresh: true, zoom: true, search: true },
    treeConfig: {
      parentField: 'parentId',
      rowField: 'id',
      transform: true,
      expandAll: true,
    },
  },
})

async function refreshGrid() {
  await gridApi.query()
  await gridApi.grid.setAllTreeExpand(expanded.value)
}
async function toggleExpand() {
  expanded.value = !expanded.value
  gridApi.setGridOptions({ treeConfig: { expandAll: expanded.value } })
  await gridApi.grid.setAllTreeExpand(expanded.value)
}
function onCreate() {
  if (deleting.value || !hasAccessByCodes([DEPT_PERMISSION.add])) return
  formDrawerApi.setData({}).open()
}
function onEdit(row: SysDept) {
  if (deleting.value || !hasAccessByCodes([DEPT_PERMISSION.edit])) return
  formDrawerApi.setData({ id: row.id }).open()
}
function onAppend(row: SysDept) {
  if (
    deleting.value ||
    row.status !== '1' ||
    !hasAccessByCodes([DEPT_PERMISSION.add])
  )
    return
  formDrawerApi.setData({ parentId: row.id }).open()
}
async function onDelete(row: SysDept) {
  if (
    deleting.value ||
    row.parentId === 0 ||
    !hasAccessByCodes([DEPT_PERMISSION.remove])
  )
    return
  deleting.value = true
  try {
    try {
      await confirm({
        title: $t('system.dept.deleteTitle'),
        content: $t('system.dept.deleteConfirm', { name: row.deptName ?? '' }),
        confirmText: $t('common.confirm'),
        cancelText: $t('common.cancel'),
      })
    } catch {
      return
    }
    await delDept(row.id)
    ElMessage.success($t('system.dept.deleteSuccess'))
    await refreshGrid()
  } catch {
    // 请求层会提示子部门、关联用户或数据权限等失败原因。
  } finally {
    deleting.value = false
  }
}
</script>

<template>
  <Page auto-content-height>
    <FormDrawer @success="refreshGrid" />
    <Grid>
      <template #toolbar-actions>
        <VbenTableToolbar
          :actions="toolbarActions"
          @create="onCreate"
        >
          <VbenButton
            variant="outline"
            :disabled="querying"
            @click="toggleExpand"
          >
            <IconifyIcon
              :icon="
                expanded ? 'lucide:chevrons-down-up' : 'lucide:chevrons-up-down'
              "
            />
            {{
              $t(expanded ? 'system.dept.collapseAll' : 'system.dept.expandAll')
            }}
          </VbenButton>
        </VbenTableToolbar>
      </template>
      <template #action="{ row }">
        <VbenTableAction
          align="center"
          :has-permission="hasPermission"
          :actions="[
            {
              text: $t('common.edit'),
              icon: 'lucide:square-pen',
              auth: DEPT_PERMISSION.edit,
              disabled: deleting,
              onClick: () => onEdit(row),
            },
            {
              text: $t('system.dept.append'),
              icon: 'lucide:plus',
              auth: DEPT_PERMISSION.add,
              disabled: deleting || row.status !== '1',
              onClick: () => onAppend(row),
            },
            {
              text: $t('common.delete'),
              icon: 'lucide:trash-2',
              danger: true,
              auth: DEPT_PERMISSION.remove,
              ifShow: row.parentId !== 0,
              disabled: deleting || !!row.children?.length,
              onClick: () => onDelete(row),
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
