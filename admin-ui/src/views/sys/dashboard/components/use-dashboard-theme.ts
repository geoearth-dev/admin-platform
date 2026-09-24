import { computed, onMounted, ref } from 'vue'
import { useMutationObserver } from '@vueuse/core'
import { convertToRgb, TinyColor } from '@/utils/color/convert'

// Read native tokens without writing inline variables back to the global theme.
export function useDashboardTheme() {
  const read = () => {
    const style = getComputedStyle(document.documentElement)
    const color = (name: string) =>
      convertToRgb('hsl(' + style.getPropertyValue('--' + name) + ')')
    return {
      primary: color('primary'),
      card: color('card'),
      foreground: color('foreground'),
      muted: color('muted-foreground'),
      border: color('border'),
      popover: color('popover'),
      success: color('success'),
      warning: color('warning'),
      destructive: color('destructive'),
    }
  }
  const colors = ref(read())
  const refresh = () => {
    colors.value = read()
  }
  onMounted(refresh)
  useMutationObserver(document.documentElement, refresh, {
    attributes: true,
    attributeFilter: ['class', 'style', 'data-theme'],
  })
  return computed(() => ({
    ...colors.value,
    primarySoft: new TinyColor(colors.value.primary)
      .mix(colors.value.card, 55)
      .toRgbString(),
    primaryFaint: new TinyColor(colors.value.primary)
      .mix(colors.value.card, 85)
      .toRgbString(),
    primaryArea: new TinyColor(colors.value.primary)
      .setAlpha(0.16)
      .toRgbString(),
  }))
}
