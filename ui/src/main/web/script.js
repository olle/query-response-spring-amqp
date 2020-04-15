import Vue from "vue";
import VueRouter from "vue-router";

// Components
import QrColorSchemeToggle from "./components/QrColorSchemeToggle.vue";

// Icons
import HomeIcon from "./icons/HomeIcon.vue";
import LiveIcon from "./icons/LiveIcon.vue";
import QueriesIcon from "./icons/QueriesIcon.vue";
import TopologyIcon from "./icons/TopologyIcon.vue";
import LoggingIcon from "./icons/LoggingIcon.vue";
import SettingsIcon from "./icons/SettingsIcon.vue";
Vue.component("home-icon", HomeIcon);
Vue.component("live-icon", LiveIcon);
Vue.component("queries-icon", QueriesIcon);
Vue.component("topology-icon", TopologyIcon);
Vue.component("logging-icon", LoggingIcon);
Vue.component("settings-icon", SettingsIcon);

// Pages
import Overview from "./pages/Overview.vue";
import Live from "./pages/Live.vue";
import Queries from "./pages/Queries.vue";
import Topology from "./pages/Topology.vue";
import Logging from "./pages/Logging.vue";
import Settings from "./pages/Settings.vue";

// Router ---------------------------------------------------------------------

Vue.use(VueRouter);

const DEFAULT_PAGE_TITLE = "Query/Response";

const meta = (label) => ({
  meta: { title: `${DEFAULT_PAGE_TITLE} | ${label}` },
});

const router = new VueRouter({
  linkActiveClass: "",
  linkExactActiveClass: "active",
  routes: [
    { path: "/", component: Overview, ...meta("Overview") },
    { path: "/live", component: Live, ...meta("Live") },
    { path: "/queries", component: Queries, ...meta("Queries Insight") },
    { path: "/topology", component: Topology, ...meta("Q/R Topology") },
    { path: "/logging", component: Logging, ...meta("Logging") },
    { path: "/settings", component: Settings, ...meta("Settings") },
  ],
});

// App Init.
new Vue({
  router,
  watch: {
    $route(to, _from) {
      document.title = to.meta.title || DEFAULT_PAGE_TITLE;
    },
  },
  components: { QrColorSchemeToggle },
}).$mount("#app");

// // METRICS --------------------------------------------------------------------

// let metrics = {
//   success: {
//     rate: 0,
//     grade: 0.0,
//     queries: 0,
//     responses: 0,
//     fallbacks: 0,
//   },
//   latency: {
//     average: 0,
//     unit: "ms",
//     grade: 0.0,
//   },
//   throughput: {
//     average: 0,
//     unit: "s",
//     queries: 0,
//     responses: 0,
//     fallbacks: 0,
//   },
// };

// const n2t = (num) => {
//   if (typeof num !== "number" || num < 1) {
//     return "0";
//   } else if (num > 99999999999999999999) {
//     return ">100E";
//   }
//   let i = Math.floor(Math.log(num) / Math.log(1000));
//   let q = 10 ** Math.min(3, i);
//   let value = Math.round((num / 1000 ** i + Number.EPSILON) * q) / q;
//   let suffix = ["", "k", "M", "G", "T", "P", "E"][i];
//   return `${value}${suffix}`;
// };
