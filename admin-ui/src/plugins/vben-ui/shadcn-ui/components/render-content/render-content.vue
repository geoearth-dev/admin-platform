<script lang="ts">
import type { Component, PropType } from 'vue';

import { defineComponent, h } from 'vue';

export default defineComponent({
  name: 'RenderContent',
  props: {
    content: {
      default: undefined,
      // 类型声明应放在 type 上，否则 Vue 仍会把 content 推断成宽泛的 Function。
      type: [Object, String, Function] as PropType<Component | string>,
    },
    renderBr: {
      default: false,
      type: Boolean,
    },
  },
  setup(props, { attrs, slots }) {
    return () => {
      if (!props.content) {
        return null;
      }
      if (typeof props.content === 'string') {
        if (props.renderBr) {
          const lines = props.content.split('\n');
          const result = [];
          for (const [i, line] of lines.entries()) {
            result.push(h('p', { key: i }, line));
          }
          return result;
        } else {
          return props.content;
        }
      }
      return h(
        props.content,
        {
          ...attrs,
          props: {
            ...props,
            ...attrs,
          },
        },
        slots,
      );
    };
  },
});
</script>
