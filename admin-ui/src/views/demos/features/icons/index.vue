<script lang="ts" setup>
import { h, ref } from 'vue'
import { ElInput } from 'element-plus'

import { Page } from '@/components/page'
import { IconPicker } from '@/components/icon-picker'
import {
  BookOpenText,
  Download,
  Inbox,
  LoaderCircle,
  RotateCw,
  Settings,
  MdiKeyboardEsc,
  SvgAvatar1Icon,
  SvgAvatar2Icon,
  SvgAvatar3Icon,
  SvgAvatar4Icon,
  SvgBellIcon,
  SvgCakeIcon,
  SvgCardIcon,
  SvgDownloadIcon,
  SvgGithubIcon,
  SvgGoogleIcon,
  SvgQQChatIcon,
  SvgWeChatIcon,
} from '@/assets/icons'

import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
} from '@/plugins/vben-ui/shadcn-ui'

const iconValue1 = ref('lucide:badge-check')
const iconValue2 = ref('svg:avatar-1')
const iconValue3 = ref('lucide:settings')
const iconValue4 = ref('lucide:book-open-text')

const inputComponent = h(ElInput)
</script>

<template>
  <Page title="图标">
    <template #description>
      <div class="mt-2 text-foreground/80">
        图标可在
        <a
          class="text-primary"
          href="https://icon-sets.iconify.design/"
          target="_blank"
        >
          Iconify
        </a>
        中查找，支持多种图标库，如 Material Design, Font Awesome, Jam Icons 等。
      </div>
    </template>

    <Card class="mb-5">
      <CardHeader><CardTitle>Iconify</CardTitle></CardHeader>
      <CardContent>
        <div class="flex items-center gap-5">
          <SvgGithubIcon class="size-8" />
          <SvgGoogleIcon class="size-8" />
          <SvgQQChatIcon class="size-8" />
          <SvgWeChatIcon class="size-8" />
          <MdiKeyboardEsc class="size-8" />
        </div>
      </CardContent>
    </Card>

    <Card class="mb-5">
      <CardHeader><CardTitle>Svg Icons</CardTitle></CardHeader>
      <CardContent>
        <div class="flex items-center gap-5">
          <SvgAvatar1Icon class="size-8" />
          <SvgAvatar2Icon class="size-8 text-red-500" />
          <SvgAvatar3Icon class="size-8 text-green-500" />
          <SvgAvatar4Icon class="size-8" />
          <SvgCakeIcon class="size-8" />
          <SvgBellIcon class="size-8" />
          <SvgCardIcon class="size-8" />
          <SvgDownloadIcon class="size-8" />
        </div>
      </CardContent>
    </Card>

    <Card class="mb-5">
      <CardHeader><CardTitle>Tailwind CSS</CardTitle></CardHeader>
      <CardContent>
        <div class="flex items-center gap-5 text-3xl">
          <Download class="size-8 text-primary" />
          <BookOpenText class="size-8" />
          <Inbox class="size-8" />
          <LoaderCircle class="size-8 animate-spin" />
          <RotateCw class="size-8 animate-pulse" />
          <Settings class="size-8" />
        </div>
      </CardContent>
    </Card>

    <Card class="mb-5">
      <CardHeader><CardTitle>图标选择器</CardTitle></CardHeader>
      <CardContent>
        <div class="mb-5 flex items-center gap-5">
          <span>原始样式(Iconify):</span>
          <IconPicker
            v-model="iconValue1"
            class="w-50"
          />
        </div>
        <div class="mb-5 flex items-center gap-5">
          <span>原始样式(svg):</span>
          <IconPicker
            v-model="iconValue2"
            class="w-50"
            prefix="svg"
          />
        </div>
        <div class="mb-5 flex items-center gap-5">
          <span>自定义Input:</span>
          <IconPicker
            :input-component="inputComponent"
            v-model="iconValue3"
            icon-slot="append"
          />
        </div>
        <div class="flex items-center gap-5">
          <span>显示为一个Icon:</span>
          <ElInput
            v-model="iconValue4"
            clearable
            placeholder="点击右侧选择图标"
            class="w-75"
          >
            <template #append>
              <IconPicker
                v-model="iconValue4"
                type="icon"
              />
            </template>
          </ElInput>
        </div>
      </CardContent>
    </Card>
  </Page>
</template>
