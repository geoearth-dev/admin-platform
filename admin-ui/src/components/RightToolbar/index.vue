<template>
  <div class="top-right-btn" :style="style">
    <el-row>
      <el-tooltip
        class="item"
        effect="dark"
        :content="showSearch ? '隐藏搜索' : '显示搜索'"
        placement="top"
        v-if="search"
      >
        <el-button circle :icon="Search" @click="toggleSearch()" />
      </el-tooltip>
      <el-tooltip class="item" effect="dark" content="刷新" placement="top">
        <el-button circle :icon="Refresh" @click="refresh()" />
      </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        content="显隐列"
        placement="top"
        v-if="Object.keys(columns).length > 0"
      >
        <el-button circle :icon="Menu" @click="showColumn()" v-if="showColumnsType == 'transfer'" />
        <el-dropdown
          trigger="click"
          :hide-on-click="false"
          style="padding-left: 12px"
          v-if="showColumnsType == 'checkbox'"
        >
          <el-button circle :icon="Menu" />
          <template #dropdown>
            <el-dropdown-menu>
              <!-- 全选/反选 按钮 -->
              <el-dropdown-item>
                <el-checkbox
                  :indeterminate="isIndeterminate"
                  :model-value="isChecked"
                  @change="toggleCheckAll"
                >
                  列展示
                </el-checkbox>
              </el-dropdown-item>
              <div class="check-line"></div>
              <template v-for="(item, key) in columns" :key="item.key ?? key">
                <el-dropdown-item>
                  <el-checkbox
                    v-model="item.visible"
                    @change="checkboxChange"
                    :label="item.label"
                  />
                </el-dropdown-item>
              </template>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-tooltip>
    </el-row>
    <el-dialog :title="title" v-model="open" append-to-body>
      <el-transfer
        :titles="['显示', '隐藏']"
        :model-value="value"
        :data="transferData"
        @change="dataChange"
      ></el-transfer>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import type { TableShowColumns } from '@/types/base/api/common';
import { computed, getCurrentInstance, ref, nextTick, onMounted } from 'vue';
import type { PropType } from 'vue';
import { StorageManager } from '@/utils/cache';
import { Menu, Refresh, Search } from '@element-plus/icons-vue';
import type { TransferKey } from 'element-plus';
onMounted(restoreStorage);
// 列配置
interface ToolbarColumn {
  key: string;
  label: string;
  visible: boolean;
}
type ToolbarColumns = ToolbarColumn[] | Record<string, ToolbarColumn>;

const cache = new StorageManager({
  prefix: `${import.meta.env.VITE_APP_NAMESPACE}-table-columns`,
});

const props = defineProps({
  /* 是否显示检索条件 */
  showSearch: {
    type: Boolean,
    default: true,
  },
  /* 显隐列信息（数组格式、对象格式） */
  columns: {
    type: [Array, Object] as PropType<ToolbarColumns>,
    default: () => ({}),
  },
  /* 是否显示检索图标 */
  search: {
    type: Boolean,
    default: true,
  },
  /* 显隐列类型（transfer穿梭框、checkbox复选框） */
  showColumnsType: {
    type: String as PropType<'transfer' | 'checkbox'>,
    default: 'checkbox',
  },
  /* 右外边距 */
  gutter: {
    type: Number,
    default: 10,
  },
  /* 列显隐状态记忆的 localStorage key（传入则启用记忆，不传则不记忆） */
  storageKey: {
    type: String,
    default: '',
  },
});

const emits = defineEmits<{
  'update:showSearch': [visible: boolean];
  queryTable: [];
}>();

// 弹出层标题
const title = ref('显示/隐藏');
// 是否显示弹出层
const open = ref(false);

const style = computed(() => {
  const ret: Record<string, string> = {};
  if (props.gutter) {
    ret.marginRight = `${props.gutter / 2}px`;
  }
  return ret;
});

// 数组对应下标字符串，对象对应属性名，保留原来的缓存格式。
const columnEntries = computed(() => Object.entries(props.columns));
// 是否全选/半选 状态
const isChecked = computed(
  () => columnEntries.value.length > 0 && columnEntries.value.every(([, column]) => column.visible),
);
// 是否部分显示。
const isIndeterminate = computed(
  () => columnEntries.value.some(([, column]) => column.visible) && !isChecked.value,
);
// 穿梭框继续使用下标作为 key。
const transferData = computed(() =>
  columnEntries.value.map(([, column], index) => ({
    key: index,
    label: column.label,
  })),
);
// 从列状态计算隐藏项，缓存恢复后也能自动同步。
const value = computed(() =>
  columnEntries.value
    .map(([, column], index) => (column.visible ? undefined : index))
    .filter((index): index is number => index !== undefined),
);

// 搜索
const { proxy } = getCurrentInstance()!;
function toggleSearch(): void {
  let el: HTMLElement | null = proxy!.$el;
  let formEl: HTMLElement | null = null;
  while ((el = el!.parentElement) && el !== document.body) {
    if ((formEl = el.querySelector('.el-form'))) break;
  }
  if (!formEl) return emits('update:showSearch', !props.showSearch);
  animateSearch(formEl, props.showSearch);
}
function animateSearch(el: HTMLElement, isHide: boolean): void {
  const DURATION = 260;
  const TRANSITION = 'max-height 0.25s ease, opacity 0.2s ease';
  const clear = () =>
    Object.assign(el.style, { transition: '', maxHeight: '', opacity: '', overflow: '' });
  Object.assign(el.style, { overflow: 'hidden', transition: '' });
  if (isHide) {
    Object.assign(el.style, {
      maxHeight: el.scrollHeight + 'px',
      opacity: '1',
      transition: TRANSITION,
    });
    requestAnimationFrame(() => Object.assign(el.style, { maxHeight: '0', opacity: '0' }));
    setTimeout(() => {
      emits('update:showSearch', false);
      clear();
    }, DURATION);
  } else {
    emits('update:showSearch', true);
    nextTick(() => {
      Object.assign(el.style, { maxHeight: '0', opacity: '0' });
      requestAnimationFrame(() =>
        requestAnimationFrame(() => {
          Object.assign(el.style, {
            transition: TRANSITION,
            maxHeight: el.scrollHeight + 'px',
            opacity: '1',
          });
        }),
      );
      setTimeout(clear, DURATION);
    });
  }
}

// 刷新
function refresh(): void {
  emits('queryTable');
}

// 穿梭框变化：与 transferData 使用相同的下标。
function dataChange(data: TransferKey[]): void {
  columnEntries.value.forEach(([, column], index) => {
    column.visible = !data.includes(index);
  });

  void saveStorage();
}

// 打开显隐列dialog
function showColumn(): void {
  open.value = true;
}

// 单勾选
function checkboxChange(): void {
  void saveStorage();
}

// 恢复列显隐缓存。
async function restoreStorage(): Promise<void> {
  if (!props.storageKey) return;

  try {
    const saved = await cache.getItem<Record<string, boolean>>(props.storageKey);

    if (!saved) return;

    for (const [key, column] of columnEntries.value) {
      const visible = saved[key];

      if (typeof visible === 'boolean') {
        column.visible = visible;
      }
    }
  } catch (error) {
    console.warn('恢复列显隐状态失败', error);
  }
}

// 切换全选/反选
function toggleCheckAll(): void {
  const newValue = !isChecked.value;
  if (Array.isArray(props.columns)) {
    props.columns.forEach((col: TableShowColumns) => (col.visible = newValue));
  } else {
    Object.values(props.columns).forEach((col) => ((col as TableShowColumns).visible = newValue));
  }
  saveStorage();
}

// 将当前列显隐状态持久化到 localStorage
// 保存列显隐缓存。
async function saveStorage(): Promise<void> {
  if (!props.storageKey) return;

  const state: Record<string, boolean> = {};

  for (const [key, column] of columnEntries.value) {
    state[key] = column.visible;
  }

  try {
    await cache.setItem(props.storageKey, state);
  } catch (error) {
    console.warn('保存列显隐状态失败', error);
  }
}
</script>

<style lang="scss" scoped>
:deep(.el-transfer__button) {
  border-radius: 50%;
  display: block;
  margin-left: 0px;
}

:deep(.el-transfer__button:first-child) {
  margin-bottom: 10px;
}

:deep(.el-dropdown-menu__item) {
  line-height: 30px;
  padding: 0 17px;
}

.check-line {
  width: 90%;
  height: 1px;
  background-color: #ccc;
  margin: 3px auto;
}
</style>
