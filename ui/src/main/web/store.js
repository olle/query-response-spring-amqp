import Vue from "vue";
import Vuex from "vuex";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    metrics: {
      success: {
        rate: 0,
        grade: 0.0,
        queries: 0,
        responses: 0,
        fallbacks: 0,
      },
      latency: {
        average: 0,
        unit: "ms",
        grade: 0.0,
      },
      throughput: {
        average: 0,
        unit: "s",
        queries: 0,
        responses: 0,
        fallbacks: 0,
      },
    },
  },
});
