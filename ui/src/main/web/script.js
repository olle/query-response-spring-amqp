import Vue from "vue";
import Vuex from "vuex";
import VueRouter from "vue-router";

// Components
import QrColorSchemeToggle from "./components/QrColorSchemeToggle.vue";

// Pages
import Overview from "./pages/Overview.vue";
import Live from "./pages/Live.vue";
import Queries from "./pages/Queries.vue";
import Topology from "./pages/Topology.vue";
import Logging from "./pages/Logging.vue";
import Settings from "./pages/Settings.vue";

// Store
import store from "./store.js";

// Icons ----------------------------------------------------------------------

import HomeIcon from "./icons/HomeIcon.vue";
import LiveIcon from "./icons/LiveIcon.vue";
import QueriesIcon from "./icons/QueriesIcon.vue";
import TopologyIcon from "./icons/TopologyIcon.vue";
import LoggingIcon from "./icons/LoggingIcon.vue";
import SettingsIcon from "./icons/SettingsIcon.vue";
import BoltIcon from "./icons/BoltIcon.vue";

Vue.component("home-icon", HomeIcon);
Vue.component("live-icon", LiveIcon);
Vue.component("queries-icon", QueriesIcon);
Vue.component("topology-icon", TopologyIcon);
Vue.component("logging-icon", LoggingIcon);
Vue.component("settings-icon", SettingsIcon);
Vue.component("bolt-icon", BoltIcon);

// Router ---------------------------------------------------------------------

Vue.use(Vuex);
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

Vue.component("current-year", {
  template: "<span>{{ new Date().getFullYear() }}</span>",
});

// App Init.
new Vue({
  el: "#app",
  store,
  router,
  created: function () {
    this.$store.dispatch("initialize");
  },
  watch: {
    $route(to) {
      document.title = to.meta.title || DEFAULT_PAGE_TITLE;
    },
  },
  components: { QrColorSchemeToggle },
});
