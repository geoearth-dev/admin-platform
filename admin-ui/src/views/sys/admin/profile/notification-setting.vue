<script setup lang="ts">
import type { FormSchemaItem } from '@/components/profile';
import { ElAlert } from 'element-plus';
import { ProfileNotificationSetting } from '@/components/profile';

const formSchema: FormSchemaItem[] = [
  { fieldName: 'accountMessage', label: '账户消息', description: '账户相关消息的提醒偏好，后续开放。', value: false },
  { fieldName: 'systemMessage', label: '系统消息', description: '系统消息的提醒偏好，后续开放。', value: false },
  { fieldName: 'todoTask', label: '待办任务', description: '待办任务的提醒偏好，后续开放。', value: false },
];
</script>

<template>
  <section>
    <h2 class="mb-6 text-lg font-semibold">新消息提醒</h2>
    <ElAlert class="mb-6" type="info" :closable="false" title="以下为静态展示，暂不支持修改，不影响现有站内消息的接收。" />
    <ProfileNotificationSetting :form-schema="formSchema" disabled />
  </section>
</template>
