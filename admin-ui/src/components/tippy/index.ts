import type { DefaultProps, Props } from 'tippy.js'
import type { App } from 'vue'

import { defineComponent, h, watchEffect } from 'vue'
import { setDefaultProps, Tippy as TippyComponent } from 'vue-tippy'
import { usePreferences } from '@/plugins/preference'
import useTippyDirective, { resolveTheme } from './directive'

import 'tippy.js/dist/tippy.css'
import 'tippy.js/dist/backdrop.css'
import 'tippy.js/themes/light.css'
import 'tippy.js/animations/scale.css'
import 'tippy.js/animations/shift-toward.css'
import 'tippy.js/animations/shift-away.css'
import 'tippy.js/animations/perspective.css'

export type TippyProps = Partial<Props>

export function initTippy(app: App, options: Partial<DefaultProps> = {}) {
  const { isDark } = usePreferences()
  setDefaultProps({ allowHTML: true, delay: [500, 200], ...options })

  const stop = watchEffect(() => {
    setDefaultProps({ theme: resolveTheme(options.theme, isDark.value) })
  })
  app.onUnmount(stop)
  app.directive('tippy', useTippyDirective(isDark, options.theme))
}

export const Tippy = defineComponent({
  name: 'Tippy',
  inheritAttrs: false,
  props: {
    theme: { type: String, default: 'auto' },
  },
  setup(props, { attrs, slots }) {
    const { isDark } = usePreferences()
    return () =>
      h(
        TippyComponent,
        {
          ...attrs,
          theme: resolveTheme(props.theme, isDark.value),
        },
        slots,
      )
  },
})
