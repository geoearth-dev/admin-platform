import { useIsMobile, useSortable, type Sortable } from '@/plugins/composables';
import type { TabsEmits, TabsProps } from './types';

import { nextTick, onMounted, onUnmounted, shallowRef, watch } from 'vue';
type TabsDragEmit = (event: 'sortTabs', ...args: TabsEmits['sortTabs']) => void;
// 可能会找到拖拽的子元素，这里需要确保拖拽的dom时tab元素
function findParentElement(element: HTMLElement): HTMLElement | null {
  return element.closest<HTMLElement>('.group');
}

export function useTabsDrag(props: TabsProps, emit: TabsDragEmit) {
  const { isMobile } = useIsMobile();
  const sortableInstance = shallowRef<Sortable | null>(null);

  async function initTabsSortable() {
    await nextTick();

    const el = document.querySelector<HTMLElement>(`.${props.contentClass}`);

    if (!el) {
      console.warn('Element not found for sortable initialization');
      return;
    }

    const resetElState = (item: HTMLElement) => {
      el.style.cursor = '';
      item.classList.remove('dragging');
    };

    const { initializeSortable } = useSortable(el, {
      filter: (_evt, target) => {
        const parent = findParentElement(target);

        return !props.draggable || !parent?.classList.contains('draggable');
      },

      onStart(evt) {
        el.style.cursor = 'grabbing';
        evt.item.classList.add('dragging');
      },

      onEnd(evt) {
        const { item, oldIndex, newIndex } = evt;
        const parent = findParentElement(item);

        resetElState(item);

        if (!parent?.classList.contains('draggable')) {
          return;
        }

        if (
          oldIndex !== undefined &&
          newIndex !== undefined &&
          !Number.isNaN(oldIndex) &&
          !Number.isNaN(newIndex) &&
          oldIndex !== newIndex
        ) {
          emit('sortTabs', oldIndex, newIndex);
        }
      },

      onMove(evt) {
        const parent = findParentElement(evt.related);

        if (!props.draggable || !parent?.classList.contains('draggable')) {
          return false;
        }

        const isCurrentAffix = evt.dragged.classList.contains('affix-tab');
        const isRelatedAffix = evt.related.classList.contains('affix-tab');

        // 不允许固定标签和普通标签跨组拖动
        return isCurrentAffix === isRelatedAffix;
      },
    });

    sortableInstance.value = await initializeSortable();
  }

  async function init() {
    // 移动端下tab不需要拖拽
    if (isMobile.value) {
      return;
    }

    await initTabsSortable();
  }

  function destroy() {
    sortableInstance.value?.destroy();
    sortableInstance.value = null;
  }

  onMounted(init);

  watch(
    () => props.styleType,
    () => {
      destroy();
      void init();
    },
  );
  onUnmounted(() => {
    destroy;
  });
}
