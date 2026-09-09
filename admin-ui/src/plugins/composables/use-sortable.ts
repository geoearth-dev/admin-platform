import type Sortable from 'sortablejs';

function useSortable(container: HTMLElement, options: Sortable.Options = {}) {
  const initializeSortable = async (): Promise<Sortable> => {
    const { default: Sortable } = await import('sortablejs');

    return Sortable.create(container, {
      animation: 300,
      delay: 400,
      delayOnTouchOnly: true,
      ...options,
    });
  };

  return { initializeSortable };
}

export { useSortable };
export type { Sortable };
