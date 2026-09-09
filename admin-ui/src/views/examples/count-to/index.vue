<script lang="ts" setup>
import type { CountToProps, TransitionPresets } from '@/components/count-to';

import { onUnmounted, reactive } from 'vue';

import { CountTo, TransitionPresetsKeys } from '@/components/count-to';
import { Page } from '@/components/page';
import { IconifyIcon } from '@/assets/icons';

import {
  ElButton,
  ElCard,
  ElCol,
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElMessage,
  ElRow,
  ElSelectV2,
  ElSwitch,
} from 'element-plus';

const props = reactive<CountToProps & { transition: TransitionPresets }>({
  decimal: '.',
  decimals: 2,
  decimalStyle: {
    fontSize: 'small',
    fontStyle: 'italic',
  },
  delay: 0,
  disabled: false,
  duration: 2000,
  endVal: 100_000,
  mainStyle: {
    color: 'hsl(var(--primary))',
    fontSize: 'xx-large',
    fontWeight: 'bold',
  },
  prefix: '￥',
  prefixStyle: {
    paddingRight: '0.5rem',
  },
  separator: ',',
  startVal: 0,
  suffix: '元',
  suffixStyle: {
    paddingLeft: '0.5rem',
  },
  transition: 'easeOutQuart',
});

function changeNumber() {
  props.endVal = Math.floor(Math.random() * 100_000_000) / 10 ** (props.decimals || 0);
}

function openDocumentation() {
  window.open('https://vueuse.org/core/useTransition/', '_blank');
}

let notice: ReturnType<typeof ElMessage.info> | undefined;
onUnmounted(() => notice?.close());

function onStarted() {
  notice?.close();
  notice = ElMessage.info({
    message: '动画已开始',
    duration: 0,
  });
}

function onFinished() {
  notice?.close();
  ElMessage.success({
    message: '动画已结束',
    duration: 2000,
  });
}
</script>
<template>
  <Page title="CountTo" description="数字滚动动画组件。使用">
    <template #description>
      <span> 使用useTransition封装的数字滚动动画组件，每次改变当前值都会产生过渡动画。 </span>
      <ElButton link type="primary" @click="openDocumentation"> 查看useTransition文档 </ElButton>
    </template>
    <ElCard header="基本用法">
      <div class="flex-center w-full pb-4">
        <CountTo v-bind="props" @started="onStarted" @finished="onFinished" />
      </div>
      <ElForm :model="props">
        <ElRow :gutter="20">
          <ElCol :span="8">
            <ElFormItem label="初始值" prop="startVal">
              <ElInputNumber v-model="props.startVal" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="8">
            <ElFormItem label="当前值" prop="endVal">
              <ElInputNumber v-model="props.endVal" class="w-full" :precision="props.decimals">
                <template #suffix>
                  <IconifyIcon
                    v-tippy="`设置一个随机值`"
                    class="size-5 cursor-pointer outline-hidden"
                    icon="ix:random-filled"
                    @click="changeNumber"
                  />
                </template>
              </ElInputNumber>
            </ElFormItem>
          </ElCol>
          <ElCol :span="8">
            <ElFormItem label="禁用动画" prop="disabled">
              <ElSwitch v-model="props.disabled" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="8">
            <ElFormItem label="延迟动画" prop="delay">
              <ElInputNumber v-model="props.delay" :min="0" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="8">
            <ElFormItem label="持续时间" prop="duration">
              <ElInputNumber v-model="props.duration" :min="0" />
            </ElFormItem>
          </ElCol>

          <ElCol :span="8">
            <ElFormItem label="小数位数" prop="decimals">
              <ElInputNumber v-model="props.decimals" :min="0" :precision="0" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="8">
            <ElFormItem label="分隔符" prop="separator">
              <ElInput v-model="props.separator" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="8">
            <ElFormItem label="小数点" prop="decimal">
              <ElInput v-model="props.decimal" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="8">
            <ElFormItem label="动画" prop="transition">
              <ElSelectV2
                v-model="props.transition"
                :options="
                  TransitionPresetsKeys.map((p: string) => ({
                    label: p,
                    value: p,
                  }))
                "
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="8">
            <ElFormItem label="前缀" prop="prefix">
              <ElInput v-model="props.prefix" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="8">
            <ElFormItem label="后缀" prop="suffix">
              <ElInput v-model="props.suffix" />
            </ElFormItem>
          </ElCol>
        </ElRow>
      </ElForm>
    </ElCard>
  </Page>
</template>
