import type { Instance, Placement, Props } from 'tippy.js'
import type {
  Directive,
  DirectiveBinding,
  Ref,
  ShallowRef,
  VNode,
  WatchStopHandle,
} from 'vue'

import { tippy } from 'vue-tippy'
import { shallowRef, watchEffect } from 'vue'

type Options = Partial<Props>
type BindingValue = Options | string | undefined
type TippyEvents = {
  onTippyShow?: Props['onShow']
  onTippyShown?: Props['onShown']
  onTippyHide?: Props['onHide']
  onTippyHidden?: Props['onHidden']
  onTippyMount?: Props['onMount']
}

export function resolveTheme(theme: string | undefined, isDark: boolean) {
  if (theme === undefined || theme === 'auto') return isDark ? '' : 'light'
  return theme === 'dark' ? '' : theme
}

function getOptions(
  el: HTMLElement,
  binding: DirectiveBinding<BindingValue>,
  vnode: VNode,
  fallbackContent = '',
): Options {
  // 克隆配置，避免给调用方的响应式对象写入 placement、arrow 等属性。
  const options: Options =
    typeof binding.value === 'string'
      ? { content: binding.value }
      : { ...binding.value }
  const placement = Object.keys(binding.modifiers).find(
    (key) => key !== 'arrow',
  ) as Placement | undefined
  if (placement) options.placement ??= placement
  if (binding.modifiers.arrow) options.arrow ??= true

  const events = vnode.props as TippyEvents | null
  if (events?.onTippyShow) options.onShow = events.onTippyShow
  if (events?.onTippyShown) options.onShown = events.onTippyShown
  if (events?.onTippyHide) options.onHide = events.onTippyHide
  if (events?.onTippyHidden) options.onHidden = events.onTippyHidden
  if (events?.onTippyMount) options.onMount = events.onTippyMount

  const title = el.getAttribute('title')
  options.content ??= title ?? el.getAttribute('content') ?? fallbackContent
  if (title !== null) el.removeAttribute('title')
  return options
}

export default function useTippyDirective(
  isDark: Readonly<Ref<boolean>>,
  defaultTheme?: string,
): Directive<HTMLElement, BindingValue> {
  const instances = new WeakMap<
    HTMLElement,
    {
      instance: Instance
      fallbackContent: string
      options: ShallowRef<Options>
      stop: WatchStopHandle
    }
  >()

  return {
    mounted(el, binding, vnode) {
      const fallbackContent =
        el.getAttribute('title') ?? el.getAttribute('content') ?? ''
      const options = shallowRef(
        getOptions(el, binding, vnode, fallbackContent),
      )
      const instance = tippy(el, {
        ...options.value,
        theme: resolveTheme(options.value.theme ?? defaultTheme, isDark.value),
      })
      const stop = watchEffect(() => {
        instance.setProps({
          ...options.value,
          theme: resolveTheme(
            options.value.theme ?? defaultTheme,
            isDark.value,
          ),
        })
      })
      instances.set(el, { instance, options, stop, fallbackContent })
    },
    updated(el, binding, vnode) {
      const entry = instances.get(el)
      if (entry)
        entry.options.value = getOptions(
          el,
          binding,
          vnode,
          entry.fallbackContent,
        )
    },
    unmounted(el) {
      const entry = instances.get(el)
      if (!entry) return
      entry.stop()
      entry.instance.destroy()
      instances.delete(el)
    },
  }
}
