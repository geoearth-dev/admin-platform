import { h } from "vue";
import DefaultTheme from "vitepress/theme";
import HomePreview from "./HomePreview.vue";
import "./style.css";

export default {
  extends: DefaultTheme,
  Layout: () =>
    h(DefaultTheme.Layout, null, {
      "home-features-after": () => h(HomePreview),
    }),
};
