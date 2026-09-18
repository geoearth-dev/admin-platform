<script lang="ts" setup>
import { computed, nextTick, ref } from 'vue';

import { addUser, updateUser } from '@/api/system/user';
import { $t } from '@/plugins/locale';

import { useFormSchema } from '../data';
import { useVbenDrawer } from '@/plugins/vben-ui/popup-ui';
import { useVbenForm } from '@/plugins/vben-ui/form-ui';
import type { SysUser, UserSaveParams } from '@/types/base/api/system/user';
import { roleOptions } from '@/api/system/role';
import { postOptions } from '@/api/system/post';
import { ElMessage } from 'element-plus';
// import { listMenu } from '@/api/admin/menu';

const emit = defineEmits<{ success: [] }>();

const [Form, formApi] = useVbenForm({
  schema: useFormSchema(),
  showDefaultActions: false,
  wrapperClass: 'grid-cols-1 md:grid-cols-2',
});
const id = ref<SysUser['id']>();
const submitting = ref(false);
const [Drawer, drawerApi] = useVbenDrawer({
  placement: 'right',
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) return;
    const data = drawerApi.getData<Partial<SysUser>>();
    id.value = data.id;
    drawerApi.lock();
    try {
      const [roles, posts] = await Promise.all([roleOptions(), postOptions()]);
      const roleList = roles
        // 与现有用户详情接口的管理员角色过滤规则一致。
        .filter((role) => data.id === 1 || role.id !== 1)
        .map((role) => ({
          label: role.roleName,
          value: role.id,
          disabled: role.status !== '1',
        }));
      const postList = posts.map((post) => ({
        label: post.postName,
        value: post.id,
        disabled: post.status !== '1',
      }));
      formApi.setState({
        schema: useFormSchema(id.value != null, roleList, postList),
      });
      await nextTick();
      await formApi.reset();
      await formApi.setValues({
        userName: data.userName ?? '',
        nickName: data.nickName ?? '',
        password: '',
        deptId: data.deptId,
        phoneNumber: data.phoneNumber ?? '',
        email: data.email ?? '',
        sex: data.sex ?? '2',
        status: data.status ?? '1',
        postIds: data.postIds ?? [],
        roleIds: data.roleIds ?? [],
        remark: data.remark ?? '',
      });
    } catch {
      // 请求层已经显示错误；不打开一个缺少选项的表单继续提交。
      drawerApi.close();
    } finally {
      drawerApi.unlock();
    }
  },
  async onConfirm() {
    if (submitting.value) return;

    const { valid } = await formApi.validate();
    if (!valid) return;
    const values = await formApi.getValues<UserSaveParams & Record<string, unknown>>();
    const payload: UserSaveParams = {
      id: id.value,
      userName: values.userName,
      nickName: values.nickName,
      deptId: values.deptId,
      phoneNumber: values.phoneNumber,
      email: values.email,
      sex: values.sex,
      status: values.status,
      postIds: values.postIds ?? [],
      roleIds: values.roleIds ?? [],
      remark: values.remark,
    };
    if (id.value == null) {
      payload.password = values.password;
    }
    submitting.value = true;
    drawerApi.lock();
    try {
      if (id.value == null) {
        await addUser(payload);
      } else {
        await updateUser(payload);
      }
      ElMessage.success($t('ui.actionMessage.operationSuccess'));
      emit('success');
      drawerApi.close();
    } catch {
      // 请求层已经提示，保留表单供修改或重试。
    } finally {
      submitting.value = false;
      drawerApi.unlock();
    }
  },
});

const getDrawerTitle = computed(() => {
  return id.value != null
    ? $t('system.user.editTitle')
    : $t('system.user.createTitle');
});
</script>
<template>
  <Drawer :title="getDrawerTitle" class="w-[800px] max-w-full sm:max-w-[800px]">
    <Form />
  </Drawer>
</template>
