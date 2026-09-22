<script setup lang="ts">
import type { FormSchemaItem } from '@/components/profile';
import { ElAlert } from 'element-plus';
import { ProfileSecuritySetting } from '@/components/profile';

const formSchema: FormSchemaItem[] = [
  { fieldName: 'accountPassword', label: '账户密码', description: '使用账号密码登录，可在“修改密码”中更新密码。', value: true },
  { fieldName: 'securityPhone', label: '密保手机', description: '手机验证与绑定功能暂未开放。', value: false },
  { fieldName: 'securityQuestion', label: '密保问题', description: '密保问题功能暂未开放。', value: false },
  { fieldName: 'securityEmail', label: '安全邮箱', description: '邮箱验证与绑定功能暂未开放。', value: false },
  { fieldName: 'securityMfa', label: 'MFA 设备', description: '多因素认证功能暂未开放。', value: false },
];
</script>

<template>
  <section>
    <h2 class="mb-6 text-lg font-semibold">安全设置</h2>
    <ElAlert class="mb-6" type="info" :closable="false" title="此区域仅作功能展示，暂不支持操作。填写联系方式不代表已完成安全绑定。" />
    <ProfileSecuritySetting :form-schema="formSchema" disabled />
  </section>
</template>
