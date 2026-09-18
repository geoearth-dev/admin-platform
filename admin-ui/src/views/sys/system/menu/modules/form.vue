<script lang="ts" setup>
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addMenu, getMenu, listMenu, updateMenu } from '@/api/system/menu'
import { MENU_PERMISSION } from '@/constants/permissions'
import { useAccess } from '@/plugins/effects/access/use-access'
import { $t } from '@/plugins/locale'
import { useVbenForm } from '@/plugins/vben-ui/form-ui'
import { useVbenDrawer } from '@/plugins/vben-ui/popup-ui'
import type { MenuType, SysMenu } from '@/types/base/api/system/menu'
import type { MenuFormValues } from '../menu-model'
import { toMenuFormValues, toMenuSaveParams } from '../menu-model'
import { getMenuTypeOptions, getParentMenuTree, useFormSchema } from '../data'

const emit = defineEmits<{ success: [] }>()
const { hasAccessByCodes } = useAccess()
const formData = ref<SysMenu>()
const busy = ref(false)
const hasChildren = ref(false)
const [Form, formApi] = useVbenForm({
  schema: useFormSchema(),
  showDefaultActions: false,
  commonConfig: { componentProps: { class: 'w-full' }, colon: true },
  wrapperClass: 'grid-cols-1 md:grid-cols-2 gap-x-4',
})
const title = computed(() =>
  $t(formData.value ? 'system.menu.editTitle' : 'system.menu.createTitle'),
)

const [Drawer, drawerApi] = useVbenDrawer({
  async onOpenChange(open) {
    if (!open) return
    const data = drawerApi.getData<{
      id?: number
      parentId?: number
      menuType?: MenuType
    }>()
    formData.value = undefined
    busy.value = true
    drawerApi.lock()
    try {
      const [menus, menu] = await Promise.all([
        listMenu(),
        data.id == null ? Promise.resolve(undefined) : getMenu(data.id),
      ])
      if (data.id != null && !menu) {
        ElMessage.warning($t('system.menu.notFound'))
        await drawerApi.close()
        return
      }
      formData.value = menu
      hasChildren.value =
        !!menu && menus.some((item) => item.parentId === menu.id)
      formApi.updateSchema([
        {
          fieldName: 'parentId',
          componentProps: { data: getParentMenuTree(menus, menu?.id) },
        },
        {
          fieldName: 'menuType',
          componentProps: {
            options: getMenuTypeOptions().map(({ label, value }) => ({
              label,
              value,
              disabled:
                ['button', 'link', 'embedded'].includes(value) &&
                hasChildren.value,
            })),
          },
        },
      ])
      await formApi.reset()
      await formApi.setValues({
        ...toMenuFormValues(menu, data.parentId, data.menuType),
      })
    } catch {
      await drawerApi.close()
    } finally {
      busy.value = false
      drawerApi.unlock()
    }
  },
  async onConfirm() {
    if (
      busy.value ||
      !hasAccessByCodes([
        formData.value ? MENU_PERMISSION.edit : MENU_PERMISSION.add,
      ])
    )
      return
    busy.value = true
    try {
      const { valid } = await formApi.validate()
      if (!valid) return
      const values = await formApi.getValues<
        MenuFormValues & Record<string, unknown>
      >()
      if (
        hasChildren.value &&
        ['button', 'link', 'embedded'].includes(values.menuType)
      ) {
        ElMessage.warning($t('system.menu.hasChildren'))
        return
      }
      drawerApi.lock()
      const payload = toMenuSaveParams(values, formData.value?.id)
      if (formData.value) await updateMenu(payload)
      else await addMenu(payload)
      ElMessage.success($t('ui.actionMessage.operationSuccess'))
      emit('success')
      await drawerApi.close()
    } catch {
      // 请求层已提示，保留表单供修改或重试。
    } finally {
      busy.value = false
      drawerApi.unlock()
    }
  },
})
</script>

<template>
  <Drawer
    :title="title"
    class="w-[800px] max-w-full"
  >
    <Form />
  </Drawer>
</template>
