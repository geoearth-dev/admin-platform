<script setup lang="ts">
import { ref } from 'vue'

import { Page } from '@/components/page'

import {
  Button,
  Card,
  CardHeader,
  CardTitle,
  CardContent,
} from '@/plugins/vben-ui/shadcn-ui'

import imageBase64 from './base64'
import {
  downloadFileFromBase64,
  downloadFileFromImageUrl,
  downloadFileFromUrl,
  downloadFileFromBlobPart,
} from '@/utils/download'
import {
  downloadFile1,
  downloadFile2,
  TEST_DOWNLOAD_URL,
  TEST_IMAGE_URL,
} from '@/api/example/download'

const downloadResult = ref('')

function getBlob() {
  downloadFile1().then((res) => {
    downloadResult.value = `获取Blob成功，长度：${res.size}`
  })
}

function getResponse() {
  downloadFile2().then((res) => {
    downloadResult.value = `获取Response成功，headers：${JSON.stringify(res.headers)},长度：${res.data.size}`
  })
}
</script>

<template>
  <Page title="文件下载示例">
    <Card>
      <CardHeader><CardTitle>根据文件地址下载文件</CardTitle></CardHeader>
      <CardContent>
        <Button
          variant="default"
          @click="
            downloadFileFromUrl({
              source: TEST_DOWNLOAD_URL,
              fileName: 'download-test.bin',
              target: '_self',
            })
          "
        >
          Download File
        </Button>
      </CardContent>
    </Card>

    <Card class="my-5">
      <CardHeader><CardTitle>根据地址下载图片</CardTitle></CardHeader>
      <CardContent>
        <Button
          variant="default"
          @click="
            downloadFileFromImageUrl({
              source: TEST_IMAGE_URL,
              fileName: 'test-image.png',
            })
          "
        >
          Download File
        </Button>
      </CardContent>
    </Card>

    <Card class="my-5">
      <CardHeader><CardTitle>base64流下载</CardTitle></CardHeader>
      <CardContent>
        <Button
          variant="default"
          @click="
            downloadFileFromBase64({
              source: imageBase64,
              fileName: 'image.png',
            })
          "
        >
          Download Image
        </Button>
      </CardContent>
    </Card>
    <Card class="my-5">
      <CardHeader><CardTitle>文本下载</CardTitle></CardHeader>
      <CardContent>
        <Button
          variant="default"
          @click="
            downloadFileFromBlobPart({
              source: 'text content',
              fileName: 'test.txt',
            })
          "
        >
          Download TxT
        </Button>
      </CardContent>
    </Card>

    <Card class="my-5">
      <CardHeader><CardTitle>Request download</CardTitle></CardHeader>
      <CardContent>
        <Button
          variant="default"
          @click="getBlob"
        >
          获取Blob
        </Button>
        <Button
          variant="default"
          class="ml-4"
          @click="getResponse"
        >
          获取Response
        </Button>
        <div class="mt-4">{{ downloadResult }}</div>
      </CardContent>
    </Card>
  </Page>
</template>
