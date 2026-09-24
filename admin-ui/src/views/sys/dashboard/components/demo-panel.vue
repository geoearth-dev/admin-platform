<script setup lang="ts">
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from '@/plugins/vben-ui/shadcn-ui'
defineProps<{ title: string }>()
</script>
<template>
  <Card class="demo-panel gap-0 py-0">
    <CardHeader class="demo-panel-heading">
      <CardTitle class="text-[15px] leading-snug">{{ title }}</CardTitle>
      <div class="demo-panel-action"><slot name="action" /></div>
    </CardHeader>
    <CardContent class="demo-panel-body"><slot /></CardContent>
  </Card>
</template>
