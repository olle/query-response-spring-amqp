import { defineStore } from "pinia";
import { reactive } from "vue";

const INITIAL_METRICS = {
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
};

export const useMetricsStore = defineStore("metrics", () => {
  const metrics = reactive({ ...INITIAL_METRICS });
  return {
    metrics,
  };
});
