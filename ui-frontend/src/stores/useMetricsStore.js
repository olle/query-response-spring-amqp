import { defineStore } from "pinia";
import { computed, ref } from "vue";
import { useNow } from "@vueuse/core";

const HISTORY_LENGTH = 10;
const STALE_AFTER_MS = 15_000;

const emptyHistory = () => Array(HISTORY_LENGTH).fill(0);

const initialMetrics = () => ({
  telemetry_at: null,
  count_queries: 0,
  count_responses: 0,
  count_fallbacks: 0,
  quality: 0.0,
  quality_history: emptyHistory(),
  coverage: 0.0,
  avg_latency: 0.0,
  avg_latencies: emptyHistory(),
  min_latency: 0,
  max_latency: 0,
  avg_throughput: 0.0,
  avg_throughputs: emptyHistory(),
  throughput_queries: 0.0,
  throughput_responses: 0.0,
});

export const useMetricsStore = defineStore("metrics", () => {
  const metrics = ref(initialMetrics());
  const now = useNow({ interval: 1000 });

  const hasTelemetry = computed(() => metrics.value.telemetry_at != null);
  const telemetryAge = computed(() => {
    if (!hasTelemetry.value) {
      return null;
    }
    return Math.max(0, now.value.getTime() - metrics.value.telemetry_at);
  });
  const isStale = computed(
    () => telemetryAge.value != null && telemetryAge.value > STALE_AFTER_MS
  );

  function update(nextMetrics) {
    metrics.value = { ...initialMetrics(), ...nextMetrics };
  }

  return {
    metrics,
    hasTelemetry,
    telemetryAge,
    isStale,
    update,
  };
});
