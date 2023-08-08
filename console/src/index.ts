import { definePlugin } from "@halo-dev/console-shared";
import HomeView from "./views/HomeView.vue";
import { IconPlug } from "@halo-dev/components";
import { markRaw } from "vue";

export default definePlugin({
  components: {},
  routes: [
    {
      parentName: "Root",
      route: {
        path: "/nps",
        name: "Nps",
        component: HomeView,
        meta: {
          permissions: ["plugin:nps:view"],
          searchable: true,
          menu: {
            name: "nps管理",
            group: "tool",
            icon: markRaw(IconPlug),
            priority: 0,
          },
        },
      },
    },
  ],
  extensionPoints: {},
});
