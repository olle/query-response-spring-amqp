<template>
  <section class="overview">
    <h2>Success Rate</h2>
    <data class="big one">{{ successRate }}%</data>
    <h3>Queries</h3>
    <data>{{ countQueries }}M</data>
    <h3>Responses</h3>
    <data>{{ countResponses }}M</data>
    <h3>Fallback</h3>
    <data>{{ countFallbacks }}K</data>

    <h2>Latency (Avg)</h2>
    <time class="big two">{{ avgLatency }}ms</time>
    <h3>Min</h3>
    <time>{{ minLatency }}ms</time>
    <h3>Max</h3>
    <time>{{ maxLatency }}ms</time>

    <h2>Throughput (Avg)</h2>
    <data class="big three">{{ avgThroughput }}K/s</data>
    <h3>Queries</h3>
    <data>{{ throughputQueries }}K/s</data>
    <h3>Responses</h3>
    <data>{{ throughputResponses }}k/s</data>
  </section>
</template>

<script>
import store from "../store.js";
import { mapState } from "vuex";

export default {
  name: "qr-metrics",
  computed: {
    ...mapState({
      successRate: (s) => s.metrics.success_rate,
      countQueries: (s) => s.metrics.count_queries,
      countResponses: (s) => s.metrics.count_responses,
      countFallbacks: (s) => s.metrics.count_fallbacks,
      avgLatency: (s) => s.metrics.avg_latency,
      minLatency: (s) => s.metrics.min_latency,
      maxLatency: (s) => s.metrics.max_latency,
      avgThroughput: (s) => s.metrics.avg_throughput,
      throughputQueries: (s) => s.metrics.throughput_queries,
      throughputResponses: (s) => s.metrics.throughput_responses,
    }),
  },
  store,
};
</script>
