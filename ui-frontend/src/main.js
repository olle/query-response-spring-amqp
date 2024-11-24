import { createApp } from "vue";
import {
  createMemoryHistory,
  createRouter,
  createWebHistory,
} from "vue-router";
import { createPinia } from "pinia";
import App from "./App.vue";

// CSS Styles
import "./style.css";

import PageOverview from "./pages/PageOverview.vue";
import PageLive from "./components/PageLive.vue";
import PageQueries from "./components/PageQueries.vue";
import PageTopology from "./components/PageTopology.vue";
import PageLogging from "./components/PageLogging.vue";
import PageSettings from "./components/PageSettings.vue";
import PageCli from "./components/PageCli.vue";

const pinia = createPinia();

const app = createApp(App);

const routes = [
  { path: "/", component: PageOverview },
  // { path: "/live", component: PageLive },
  // { path: "/queries", component: PageQueries },
  // { path: "/topology", component: PageTopology },
  // { path: "/logging", component: PageLogging },
  // { path: "/settings", component: PageSettings },
  // { path: "/cli", component: PageCli },
];

app.use(
  createRouter({
    history: createMemoryHistory(),
    routes,
  })
);

app.use(pinia).mount("#app");
