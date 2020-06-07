import Vue from "vue";
import Vuex from "vuex";
import mutations from "./mutations";
import { addListener } from "./ws";

Vue.use(Vuex);

const initializeMetrics = (state) => {
  if (!localStorage.getItem("query-response/metrics")) {
    localStorage.setItem(
      "query-response/metrics",
      JSON.stringify(state.metrics)
    );
  }
};

export default new Vuex.Store({
  strict: process.env.NODE_ENV !== "production",
  state: {
    // @see QrMetrics.vue
    metrics: {
      count_queries: 0,
      count_responses: 0,
      count_fallbacks: 0,
      success_rate: 0.0,
      success_rates: [0],
      avg_latency: 0.0,
      avg_latencies: [0],
      min_latency: 0,
      max_latency: 0,
      avg_throughput: 0.0,
      avg_throughputs: [0],
      throughput_queries: 0.0,
      throughput_responses: 0.0,
    },
    // @see QrLiveNodes.vue
    nodes: {},
  },
  mutations,
  actions: {
    initialize({ state, dispatch }) {
      initializeMetrics(state);
      dispatch("shovel");
    },
    shovel({ commit, dispatch }) {
      [
        ["query-response/metrics", "metrics"],
        ["query-response/nodes", "nodes"],
      ].forEach(([key, store]) => {
        let data = localStorage.getItem(key);
        if (data) {
          commit(store, JSON.parse(data));
        }
      });
      setTimeout(() => dispatch("shovel"), 789);
    },
  },
});

const updateMetrics = (data) => {
  updateData("query-response/metrics", data);
};

const updateData = (key, data) => {
  let prev = JSON.parse(localStorage.getItem(key));
  localStorage.setItem(key, JSON.stringify({ ...prev, ...data }));
};

const updateNodes = (data) => {
  localStorage.setItem("query-response/nodes", JSON.stringify(data));
};

addListener((msg) => {
  try {
    var message = JSON.parse(msg.data);
    console.log("got message", message);
    if (message.metrics) {
      updateMetrics(message.metrics);
    }
    if (message.nodes) {
      updateNodes(message.nodes);
    }
  } catch (err) {
    console.error("unexpected payload", msg.data);
  }
});
