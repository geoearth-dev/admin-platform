import { useEventListener } from '@vueuse/core'
import { onScopeDispose, watch } from 'vue'

import {
  startOnlineStream,
  type OnlineStreamOptions,
} from '@/utils/online-stream'

interface OnlineSessionOptions extends OnlineStreamOptions {
  enabled: () => boolean
}

/** 在线连接跟随登录状态，切换业务页面和刷新 Token 时保持连接。 */
export function useOnlineSession(options: OnlineSessionOptions) {
  let stopStream: (() => void) | undefined

  function stop() {
    stopStream?.()
    stopStream = undefined
  }

  function syncConnection() {
    stop()
    if (options.enabled()) {
      stopStream = startOnlineStream(options)
    }
  }

  watch(options.enabled, syncConnection, { immediate: true })
  useEventListener(window, 'pagehide', stop)
  useEventListener(window, 'pageshow', syncConnection)
  onScopeDispose(stop)

  return { stop, start: syncConnection }
}
