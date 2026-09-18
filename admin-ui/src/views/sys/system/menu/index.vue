<script lang="ts" setup>
import { computed, ref } from 'vue'
import { ElMessage, ElTag } from 'element-plus'
import { delMenu, listMenu } from '@/api/system/menu'
import { IconifyIcon } from '@/assets/icons'
import { Page } from '@/components/page'
import { VbenTableToolbar } from '@/components/table-toolbar'
import { useVbenVxeGrid } from '@/components/vxe-table'
import { MENU_PERMISSION } from '@/constants/permissions'
import { useAccess } from '@/plugins/effects/access/use-access'
import { $t } from '@/plugins/locale'
import { confirm, useVbenDrawer } from '@/plugins/vben-ui/popup-ui'
import { VbenButton, VbenTableAction } from '@/plugins/vben-ui/shadcn-ui'
import type { SysMenu } from '@/types/base/api/system/menu'
import {
  getMenuTypeOptions,
  menuTitle,
  useColumns,
  useGridFormSchema,
} from './data'
import Form from './modules/form.vue'
import { generateAccess } from '@/router/access'
import { useAccessStore } from '@/store/system/authorize/access'
import { filterMenuTree } from './menu-model'
import { MenuBadge } from '@/plugins/vben-ui/menu-ui'
const menuTypes = getMenuTypeOptions()

const { hasAccessByCodes } = useAccess()
const hasPermission = (auth?: string | string[]) =>
  !auth || hasAccessByCodes(Array.isArray(auth) ? auth : [auth])
const toolbarActions = computed<Array<'create'>>(() =>
  hasAccessByCodes([MENU_PERMISSION.add]) ? ['create'] : [],
)
const deleting = ref(false)
const querying = ref(false)
const expanded = ref(true)
const [FormDrawer, formDrawerApi] = useVbenDrawer({
  connectedComponent: Form,
  destroyOnClose: true,
})

const [Grid, gridApi] = useVbenVxeGrid<SysMenu>({
  tableTitle: $t('system.menu.list'),
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
    pagerConfig: { enabled: false },
    proxyConfig: {
      // 后端直接返回菜单数组，不是分页对象。
      response: { list: '' },
      ajax: {
        query: async (_params, values) => {
          querying.value = true
          try {
            const menus = await listMenu()
            return filterMenuTree(
              menus,
              typeof values.menuName === 'string' ? values.menuName : '',
              values.status === 0 || values.status === 1
                ? values.status
                : undefined,
              menuTitle,
            )
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
      expandAll: false,
    },
  },
})

async function onRefresh() {
  await gridApi.query()
  await gridApi.grid.setAllTreeExpand(expanded.value)
  const { accessibleMenus, accessibleRoutes } = await generateAccess()
  const accessStore = useAccessStore()
  accessStore.setAccessMenus(accessibleMenus)
  accessStore.setAccessRoutes(accessibleRoutes)
}
function onCreate() {
  if (deleting.value || !hasAccessByCodes([MENU_PERMISSION.add])) return
  formDrawerApi.setData({ parentId: 0, menuType: 'catalog' }).open()
}
function onEdit(row: SysMenu) {
  if (deleting.value || !hasAccessByCodes([MENU_PERMISSION.edit])) return
  // 抽屉根据 id 请求完整详情，避免用列表快照覆盖配置。
  formDrawerApi.setData({ id: row.id }).open()
}
function onAppend(row: SysMenu) {
  if (
    deleting.value ||
    row.menuType === 'button' ||
    row.link ||
    row.iframeSrc ||
    !hasAccessByCodes([MENU_PERMISSION.add])
  )
    return
  formDrawerApi
    .setData({
      parentId: row.id,
      menuType: row.menuType === 'catalog' ? 'menu' : 'button',
    })
    .open()
}
async function toggleExpand() {
  expanded.value = !expanded.value
  gridApi.setGridOptions({ treeConfig: { expandAll: expanded.value } })
  await gridApi.grid.setAllTreeExpand(expanded.value)
}
async function onDelete(row: SysMenu) {
  if (deleting.value || !hasAccessByCodes([MENU_PERMISSION.remove])) return
  deleting.value = true
  try {
    try {
      await confirm({
        title: $t('system.menu.deleteTitle'),
        content: $t('system.menu.deleteConfirm', {
          name: menuTitle(row.menuName),
        }),
        confirmText: $t('common.confirm'),
        cancelText: $t('common.cancel'),
      })
    } catch {
      return
    }
    await delMenu(row.id)
    ElMessage.success($t('system.menu.deleteSuccess'))
    await onRefresh()
  } catch {
    // 请求层提示子菜单、角色关联等删除失败原因，保留当前列表。
  } finally {
    deleting.value = false
  }
}
</script>

<template>
  <Page auto-content-height>
    <FormDrawer @success="onRefresh" />
    <Grid>
      <template #toolbar-actions>
        <VbenTableToolbar
          :actions="toolbarActions"
          :loading="{ create: deleting }"
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
              $t(expanded ? 'system.menu.collapseAll' : 'system.menu.expandAll')
            }}
          </VbenButton>
        </VbenTableToolbar>
      </template>
      <template #title="{ row }">
        <span class="inline-flex max-w-full items-center gap-2 align-middle">
          <IconifyIcon
            :icon="
              row.menuType === 'button'
                ? 'lucide:shield-check'
                : row.icon || 'lucide:folder'
            "
            class="size-4 shrink-0"
          />
          <span class="truncate">{{ menuTitle(row.menuName) }}</span>
          <span class="relative inline-flex min-w-5 items-center"
            ><MenuBadge
              :badge="row.badge || undefined"
              :badge-type="row.badgeType || undefined"
              :badge-variants="row.badgeVariants || undefined"
          /></span>
        </span>
      </template>
      <template #menuType="{ row }">
        <ElTag
          :type="menuTypes.find((type) => type.value === row.menuType)?.type"
        >
          {{ menuTypes.find((type) => type.value === row.menuType)?.label }}
        </ElTag>
      </template>
      <template #action="{ row }">
        <VbenTableAction
          align="center"
          :has-permission="hasPermission"
          :actions="[
            {
              text: $t('common.edit'),
              icon: 'lucide:square-pen',
              auth: MENU_PERMISSION.edit,
              disabled: deleting,
              onClick: () => onEdit(row),
            },
            {
              text: $t('system.menu.append'),
              icon: 'lucide:plus',
              auth: MENU_PERMISSION.add,
              ifShow: row.menuType !== 'button' && !row.link && !row.iframeSrc,
              disabled: deleting,
              onClick: () => onAppend(row),
            },
            {
              text: $t('common.delete'),
              icon: 'lucide:trash-2',
              danger: true,
              auth: MENU_PERMISSION.remove,
              disabled: deleting,
              onClick: () => onDelete(row),
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
