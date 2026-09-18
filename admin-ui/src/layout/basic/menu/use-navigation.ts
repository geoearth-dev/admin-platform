import { isHttpUrl } from '@/utils/inference'
import { openWindow, openRouteInNewWindow } from '@/utils/window'
import { useRouter } from 'vue-router'

function useNavigation() {
  const router = useRouter()

  const navigation = async (path: string) => {
    if (isHttpUrl(path)) {
      openWindow(path, { target: '_blank' })
      return
    }
    // 每次读取当前路由，菜单保存后重新注册的配置可以立即生效。
    const { openInNewWindow, query, link } = router.resolve(path).meta
    if (link) {
      openWindow(link, { target: '_blank' })
    } else if (openInNewWindow) {
      openRouteInNewWindow(router.resolve({ path, query }).href)
    } else {
      await router.push({ path, query })
    }
  }

  const willOpenedByWindow = (path: string) => {
    if (isHttpUrl(path)) return true
    const { link, openInNewWindow } = router.resolve(path).meta
    return !!(link || openInNewWindow)
  }

  return { navigation, willOpenedByWindow }
}

export { useNavigation }
