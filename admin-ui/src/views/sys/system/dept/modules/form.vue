<script lang="ts" setup>
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  addDept,
  getDept,
  listDept,
  listDeptExcludeChild,
  updateDept,
} from '@/api/system/dept'
import { DEPT_PERMISSION } from '@/constants/permissions'
import { useAccess } from '@/plugins/effects/access/use-access'
import { $t } from '@/plugins/locale'
import { useVbenForm } from '@/plugins/vben-ui/form-ui'
import { useVbenDrawer } from '@/plugins/vben-ui/popup-ui'
import { VbenButton } from '@/plugins/vben-ui/shadcn-ui'
import type { DeptSaveParams, SysDept } from '@/types/base/api/system/dept'
import { getParentDeptTree, useSchema } from '../data'

const emit = defineEmits<{ success: [] }>()
const { hasAccessByCodes } = useAccess()
const formData = ref<SysDept>()
const parentDepartments = ref<SysDept[]>([])
const initialValues = ref<Partial<DeptSaveParams>>({})
const busy = ref(false)
const ready = ref(false)
const [Form, formApi] = useVbenForm({
  schema: useSchema(),
  showDefaultActions: false,
  commonConfig: { componentProps: { class: 'w-full' }, colon: true },
  wrapperClass: 'grid-cols-1 md:grid-cols-2 gap-x-4',
})
const title = computed(() =>
  $t(formData.value ? 'system.dept.editTitle' : 'system.dept.createTitle'),
)

async function restoreValues() {
  await formApi.reset()
  await formApi.setValues({ ...initialValues.value })
}
async function resetForm() {
  if (busy.value || !ready.value) return
  busy.value = true
  try {
    await restoreValues()
  } finally {
    busy.value = false
  }
}

const [Drawer, drawerApi] = useVbenDrawer({
  async onOpenChange(open) {
    if (!open) return
    const data = drawerApi.getData<{ id?: number; parentId?: number }>()
    formData.value = undefined
    ready.value = false
    busy.value = true
    drawerApi.lock()
    try {
      const [departments, dept] = await Promise.all([
        data.id == null ? listDept() : listDeptExcludeChild(data.id),
        data.id == null ? Promise.resolve(undefined) : getDept(data.id),
      ])
      if (data.id != null && !dept) {
        ElMessage.warning($t('system.dept.notFound'))
        await drawerApi.close()
        return
      }
      formData.value = dept
      parentDepartments.value = departments
      formApi.updateSchema([
        {
          fieldName: 'parentId',
          componentProps: {
            data: getParentDeptTree(departments, dept),
            // 顶级部门保持根归属；新增只能挂到已有的正常部门下。
            disabled: dept?.parentId === 0,
          },
        },
      ])
      initialValues.value = {
        parentId: dept?.parentId ?? data.parentId,
        deptName: dept?.deptName ?? '',
        orderNum: dept?.orderNum ?? 0,
        leader: dept?.leader ?? '',
        phone: dept?.phone ?? '',
        email: dept?.email ?? '',
        status: dept?.status ?? '1',
      }
      await restoreValues()
      ready.value = true
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
      !ready.value ||
      !hasAccessByCodes([
        formData.value ? DEPT_PERMISSION.edit : DEPT_PERMISSION.add,
      ])
    )
      return
    busy.value = true
    try {
      const { valid } = await formApi.validate()
      if (!valid) return
      const values = await formApi.getValues<
        DeptSaveParams & Record<string, unknown>
      >()
      const parent = parentDepartments.value.find(
        (dept) => dept.id === values.parentId,
      )
      const keepParent =
        !!formData.value && values.parentId === formData.value.parentId
      if (!keepParent && (!parent || parent.status !== '1')) {
        ElMessage.warning($t('system.dept.parentRequired'))
        return
      }
      // 提交 DTO 字段，祖级列表由后端维护，避免覆盖 children 等列表数据。
      const payload: DeptSaveParams = {
        id: formData.value?.id,
        parentId: values.parentId,
        deptName: values.deptName.trim(),
        orderNum: values.orderNum,
        leader: values.leader?.trim() ?? '',
        phone: values.phone?.trim() ?? '',
        email: values.email?.trim() ?? '',
        status: values.status,
      }
      drawerApi.lock()
      if (formData.value) await updateDept(payload)
      else await addDept(payload)
      ElMessage.success($t('ui.actionMessage.operationSuccess'))
      emit('success')
      await drawerApi.close()
    } catch {
      // 请求层已提示，保留输入供修改或重试。
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
    class="w-[640px] max-w-full"
  >
    <Form />
    <template #prepend-footer>
      <div class="flex-auto">
        <VbenButton
          variant="outline"
          :disabled="busy || !ready"
          @click="resetForm"
        >
          {{ $t('common.reset') }}
        </VbenButton>
      </div>
    </template>
  </Drawer>
</template>
