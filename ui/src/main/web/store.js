import Vue from "vue";
import Vuex from "vuex";

Vue.use(Vuex);

const initializeMetrics = (store) => {
  if (!localStorage.getItem("query-response/metrics")) {
    localStorage.setItem(
      "query-response/metrics",
      JSON.stringify(store.state.metrics)
    );
  }
};

const shovel = (store) => {
  console.log("SHOVELING!", store);
  let metrics = localStorage.getItem("query-response/metrics");
  if (metrics) {
    store.commit("metrics", JSON.parse(metrics));
  }
  setTimeout(() => shovel(store), 1234);
};

export default new Vuex.Store({
  strict: process.env.NODE_ENV !== "production",
  state: {
    // @see QrMetrics.vue
    metrics: {
      success_rate: 0,
      success_rate_rank: 0.0,
      count_queries: 0,
      count_responses: 0,
      count_fallbacks: 0,
      avg_latency: 0,
      avg_latency_unit: "ms",
      avg_latency_rank: 0.0,
      min_latency: 0,
      min_latency_unit: "ms",
      max_latency: 0,
      max_latency_unit: "ms",
      avg_throughput: 0,
      avg_throughput_unit: "s",
      avg_throughput_rank: 0.0,
      throughput_queries: 0,
      throughput_queries_unit: "s",
      throughput_responses: 0,
      throughput_responses_unit: "s",
    },
  },
  mutations: {
    metrics: function(state, payload) {
      state.metrics = payload;
      return state;
    },
  },
  actions: {
    initialize: function() {
      initializeMetrics(this);
      setTimeout(() => shovel(this), 1234);
    },
  },
});
