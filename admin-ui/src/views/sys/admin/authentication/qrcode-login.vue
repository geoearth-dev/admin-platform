<script lang="ts" setup>
import { AuthenticationQrCodeLogin } from '@/components/authentication'
import { LOGIN_PATH } from '@/constants/core'

defineOptions({ name: 'QrCodeLogin' })
</script>

<template>
  <AuthenticationQrCodeLogin
    description="扫码登录功能暂未接入"
    :login-path="LOGIN_PATH"
  />
</template>
