import { definePlugin } from "@halo-dev/console-shared";
import HomeView from "./views/HomeView.vue";
import VPN from "~icons/mdi/vpn";

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
            icon: markRaw(VPN),
            priority: 0,
          },
        },
      },
    },
  ],
  extensionPoints: {},
});
