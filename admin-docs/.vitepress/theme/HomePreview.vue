<script setup lang="ts">
import { computed } from "vue";
import { useData, withBase } from "vitepress";

const { lang } = useData();
const content = computed(() =>
  lang.value === "zh-CN"
    ? {
        title: "先看看，工作是什么样子",
        description: "数据分析与工作台预览。图表和业务数据为静态演示内容。",
        analytics: "数据分析",
        workspace: "工作台",
        hint: "点击查看完整图片",
      }
    : {
        title: "A workspace you can make your own",
        description:
          "Explore the analytics and workspace previews. Charts and business data are static demos.",
        analytics: "Analytics",
        workspace: "Workspace",
        hint: "View full image",
      },
);
</script>

<template>
  <section class="home-preview" aria-labelledby="preview-heading">
    <h2 id="preview-heading">{{ content.title }}</h2>
    <p class="preview-description">{{ content.description }}</p>
    <div class="preview-grid">
      <a
        v-for="item in [
          { file: 'analysis.png', title: content.analytics },
          { file: 'workbench.png', title: content.workspace },
        ]"
        :key="item.file"
        class="preview-card"
        :href="withBase(`/images/${item.file}`)"
        target="_blank"
        rel="noopener noreferrer"
        :aria-label="`${item.title} — ${content.hint}`"
      >
        <div class="preview-card-heading">
          <h3>{{ item.title }}</h3>
          <span aria-hidden="true">↗</span>
        </div>
        <img
          :src="withBase(`/images/${item.file}`)"
          :alt="item.title"
          loading="lazy"
          width="2559"
          height="1347"
        />
      </a>
    </div>
  </section>
</template>
