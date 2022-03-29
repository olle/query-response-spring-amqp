import { createApp } from "vue";
import { createRouter, createWebHistory } from "vue-router";
import App from "./App.vue";
import "./style.css";

import PageLive from "./components/PageLive.vue";
import PageQueries from "./components/PageQueries.vue";
import PageTopology from "./components/PageTopology.vue";
import PageLogging from "./components/PageLogging.vue";
import PageSettings from "./components/PageSettings.vue";
import PageCli from "./components/PageCli.vue";

const app = createApp(App);

app.use(
  createRouter({
    history: createWebHistory(),
    routes: [
      { path: "/live", component: PageLive },
      { path: "/queries", component: PageQueries },
      { path: "/topology", component: PageTopology },
      { path: "/logging", component: PageLogging },
      { path: "/settings", component: PageSettings },
      { path: "/cli", component: PageCli },
    ],
  })
);

app.mount("#app");
