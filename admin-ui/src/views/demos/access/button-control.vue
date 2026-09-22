<script lang="ts" setup>
import { useRouter } from 'vue-router'

import { Page } from '@/components/page'
import { resetAllStores, useUserStore } from '@/store'

import {
  Button,
  Card,
  CardHeader,
  CardTitle,
  CardContent,
} from '@/plugins/vben-ui/shadcn-ui'

import { useAuthStore } from '@/store'
import { useAccess } from '@/plugins/effects/access/use-access'
import type { LoginParams } from '@/api/admin/auth'

const accounts: Record<string, LoginParams> = {
  admin: {
    password: '123456',
    username: 'admin',
    code: '',
    uuid: '',
    rememberMe: false,
  },
  super: {
    password: '123456',
    username: 'vben',
    code: '',
    uuid: '',
    rememberMe: false,
  },
  user: {
    password: '123456',
    username: 'jack',
    code: '',
    uuid: '',
    rememberMe: false,
  },
}

const { accessMode, hasAccessByCodes } = useAccess()
const authStore = useAuthStore()
const userStore = useUserStore()
const router = useRouter()

function roleButtonVariant(role: string) {
  return userStore.userRoles.includes(role) ? 'default' : 'outline'
}

async function changeAccount(role: string) {
  if (userStore.userRoles.includes(role)) {
    return
  }

  const account = accounts[role]
  resetAllStores()
  if (account) {
    await authStore.authLogin(account, async () => {
      router.go(0)
    })
  }
}
</script>

<template>
  <Page
    :title="`${accessMode === 'frontend' ? '前端' : '后端'}按钮访问权限演示`"
    description="切换不同的账号，观察按钮变化。"
  >
    <Card class="mb-5">
      <template #title>
        <span class="font-semibold">当前角色:</span>
        <span class="mx-4 text-lg text-primary">
          {{ userStore.userRoles?.[0] }}
        </span>
      </template>

      <Button
        :variant="roleButtonVariant('super')"
        @click="changeAccount('super')"
      >
        切换为 Super 账号
      </Button>

      <Button
        :variant="roleButtonVariant('admin')"
        class="mx-4"
        @click="changeAccount('admin')"
      >
        切换为 Admin 账号
      </Button>
      <Button
        :variant="roleButtonVariant('user')"
        @click="changeAccount('user')"
      >
        切换为 User 账号
      </Button>
    </Card>

    <Card class="mb-5">
      <CardHeader><CardTitle>组件形式控制 - 权限码</CardTitle></CardHeader>
      <CardContent>
        <AccessControl
          :codes="['AC_100100']"
          type="code"
        >
          <Button class="mr-4"> Super 账号可见 ["AC_100100"] </Button>
        </AccessControl>
        <AccessControl
          :codes="['AC_100030']"
          type="code"
        >
          <Button class="mr-4"> Admin 账号可见 ["AC_100030"] </Button>
        </AccessControl>
        <AccessControl
          :codes="['AC_1000001']"
          type="code"
        >
          <Button class="mr-4"> User 账号可见 ["AC_1000001"] </Button>
        </AccessControl>
        <AccessControl
          :codes="['AC_100100', 'AC_100030']"
          type="code"
        >
          <Button class="mr-4">
            Super & Admin 账号可见 ["AC_100100","AC_100030"]
          </Button>
        </AccessControl>
      </CardContent>
    </Card>

    <Card
      v-if="accessMode === 'frontend'"
      class="mb-5"
    >
      <CardHeader><CardTitle>组件形式控制 - 角色</CardTitle></CardHeader>
      <CardContent>
        <AccessControl
          :codes="['super']"
          type="role"
        >
          <Button class="mr-4"> Super 角色可见 </Button>
        </AccessControl>
        <AccessControl
          :codes="['admin']"
          type="role"
        >
          <Button class="mr-4"> Admin 角色可见 </Button>
        </AccessControl>
        <AccessControl
          :codes="['user']"
          type="role"
        >
          <Button class="mr-4"> User 角色可见 </Button>
        </AccessControl>
        <AccessControl
          :codes="['super', 'admin']"
          type="role"
        >
          <Button class="mr-4"> Super & Admin 角色可见 </Button>
        </AccessControl>
      </CardContent>
    </Card>

    <Card class="mb-5">
      <CardHeader><CardTitle>函数形式控制</CardTitle></CardHeader>
      <CardContent>
        <Button
          v-if="hasAccessByCodes(['AC_100100'])"
          class="mr-4"
        >
          Super 账号可见 ["AC_100100"]
        </Button>
        <Button
          v-if="hasAccessByCodes(['AC_100030'])"
          class="mr-4"
        >
          Admin 账号可见 ["AC_100030"]
        </Button>
        <Button
          v-if="hasAccessByCodes(['AC_1000001'])"
          class="mr-4"
        >
          User 账号可见 ["AC_1000001"]
        </Button>
        <Button
          v-if="hasAccessByCodes(['AC_100100', 'AC_100030'])"
          class="mr-4"
        >
          Super & Admin 账号可见 ["AC_100100","AC_100030"]
        </Button>
      </CardContent>
    </Card>

    <Card class="mb-5">
      <CardHeader><CardTitle>指令方式 - 权限码</CardTitle></CardHeader>
      <CardContent>
        <Button
          class="mr-4"
          v-access:code="['AC_100100']"
        >
          Super 账号可见 ["AC_100100"]
        </Button>
        <Button
          class="mr-4"
          v-access:code="['AC_100030']"
        >
          Admin 账号可见 ["AC_100030"]
        </Button>
        <Button
          class="mr-4"
          v-access:code="['AC_1000001']"
        >
          User 账号可见 ["AC_1000001"]
        </Button>
        <Button
          class="mr-4"
          v-access:code="['AC_100100', 'AC_100030']"
        >
          Super & Admin 账号可见 ["AC_100100","AC_100030"]
        </Button>
      </CardContent>
    </Card>

    <Card
      v-if="accessMode === 'frontend'"
      class="mb-5"
    >
      <CardHeader><CardTitle>指令方式 - 角色</CardTitle></CardHeader>
      <CardContent>
        <Button
          class="mr-4"
          v-access:role="['super']"
        >
          Super 角色可见
        </Button>
        <Button
          class="mr-4"
          v-access:role="['admin']"
        >
          Admin 角色可见
        </Button>
        <Button
          class="mr-4"
          v-access:role="['user']"
        >
          User 角色可见
        </Button>
        <Button
          class="mr-4"
          v-access:role="['super', 'admin']"
        >
          Super & Admin 角色可见
        </Button>
      </CardContent>
    </Card>
  </Page>
</template>
