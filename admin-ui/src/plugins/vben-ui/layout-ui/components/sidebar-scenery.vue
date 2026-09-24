<script setup lang="ts">
import { useI18n } from '@/plugins/locale';
import mountains from '@/views/sys/dashboard/assets/dashboard-mountains.png';
import { version } from '../../../../../package.json';

const { t } = useI18n();
withDefaults(defineProps<{ availableSpace?: number }>(), { availableSpace: 0 });
</script>

<template>
  <div class="sidebar-scenery" :class="{ 'is-crowded': availableSpace < 160 }" aria-hidden="true">
    <img :src="mountains" alt="" class="sidebar-scenery-mountains" />
    <div class="sidebar-scenery-caption">
      <p>{{ t('dashboardDemo.sidebar.lineOne') }}</p>
      <p>{{ t('dashboardDemo.sidebar.lineTwo') }}</p>
    </div>
    <span class="sidebar-scenery-version">v{{ version }}</span>
  </div>
</template>

<style scoped>
.sidebar-scenery {
  position: relative;
  height: 260px;
  overflow: hidden;
  pointer-events: none;
  color: hsl(var(--muted-foreground));
}
.sidebar-scenery-mountains {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 175px;
  object-fit: cover;
  object-position: 70% center;
  opacity: 0.65;
  mask-image: linear-gradient(transparent, #000 40%, #000 60%, transparent);
}
.dark .sidebar-scenery-mountains {
  filter: brightness(0.65) saturate(0.6);
  opacity: 0.38;
  mix-blend-mode: luminosity;
}
.sidebar-scenery-caption {
  position: absolute;
  inset: 140px 16px auto;
  text-align: center;
  font-family: STKaiti, KaiTi, Georgia, serif;
  font-size: 15px;
  line-height: 1.8;
  transform: rotate(-6deg);
}
.sidebar-scenery-caption::after {
  content: '';
  display: block;
  width: 70%;
  height: 1px;
  margin: 6px auto;
  background: linear-gradient(90deg, transparent, currentColor, transparent);
  opacity: 0.5;
}
.sidebar-scenery-version {
  position: absolute;
  inset: auto 0 6px;
  text-align: center;
  font-size: 11px;
  opacity: 0.65;
}
/* Keep a gap above the rotated caption; long menus take priority over decoration. */
.sidebar-scenery.is-crowded .sidebar-scenery-caption,
.sidebar-scenery.is-crowded .sidebar-scenery-version {
  visibility: hidden;
}
.sidebar-scenery.is-crowded .sidebar-scenery-mountains {
  opacity: 0.14;
}
</style>
