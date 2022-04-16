import { createApp } from "vue";
import { createRouter, createWebHistory } from "vue-router";
import { createStore } from "vuex";
import App from "./App.vue";

// CSS Styles
import "./style.css";

import { addListener } from "./ws";

import PageOverview from "./components/PageOverview.vue";
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
      { path: "/", component: PageOverview },
      { path: "/live", component: PageLive },
      { path: "/queries", component: PageQueries },
      { path: "/topology", component: PageTopology },
      { path: "/logging", component: PageLogging },
      { path: "/settings", component: PageSettings },
      { path: "/cli", component: PageCli },
    ],
  })
);

app.use(
  createStore({
    strict: process.env.NODE_ENV !== "production",
    state: {
      showColorPalette: false,
      // @see QrMetrics.vue
      metrics: {
        count_queries: 0,
        count_responses: 0,
        count_fallbacks: 0,
        success_rate: 0.0,
        success_rates: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        avg_latency: 0.0,
        avg_latencies: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        min_latency: 0,
        max_latency: 0,
        avg_throughput: 0.0,
        avg_throughputs: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        throughput_queries: 0.0,
        throughput_responses: 0.0,
      },
      // @see QrLiveNodes.vue
      nodes: {},
    },
    mutations: {
      toggleShowColorPalette(state) {
        state.showColorPalette = !state.showColorPalette;
      },
    },
  })
);

app.mount("#app");
