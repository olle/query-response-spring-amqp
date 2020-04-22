<template>
  <section class="overview">
    <h2>Success Rate</h2>
    <data class="big one" v-bind:class="successRateRank">{{
      successRate
    }}</data>
    <h3>Queries</h3>
    <data>{{ countQueries }}</data>
    <h3>Responses</h3>
    <data>{{ countResponses }}</data>
    <h3>Fallback</h3>
    <data>{{ countFallbacks }}</data>
    <h2>Latency (Avg)</h2>
    <time class="big two">{{ avgLatency }}</time>
    <h3>Min</h3>
    <time>{{ minLatency }}</time>
    <h3>Max</h3>
    <time>{{ maxLatency }}</time>
    <h2>Throughput (Avg)</h2>
    <data class="big three">{{ avgThroughput }}</data>
    <h3>Queries</h3>
    <data>{{ throughputQueries }}</data>
    <h3>Responses</h3>
    <data>{{ throughputResponses }}</data>
  </section>
</template>

<script>
import store from "../store.js";
import { mapState } from "vuex";

export default {
  name: "qr-metrics",
  computed: {
    ...mapState({
      successRate: (s) =>
        ns2p(s.metrics.count_responses, s.metrics.count_queries),
      successRateRank: (s) => {
        let rate =
          s.metrics.count_responses / Math.max(1, s.metrics.count_queries);
        if (rate < 0.2) {
          return "failure";
        } else if (rate < 0.6) {
          return "warning";
        }
      },
      countQueries: (s) => n2t(s.metrics.count_queries),
      countResponses: (s) => n2t(s.metrics.count_responses),
      countFallbacks: (s) => n2t(s.metrics.count_fallbacks),
      avgLatency: (s) =>
        `${s.metrics.avg_latency}${s.metrics.avg_latency_unit}`,
      minLatency: (s) =>
        `${s.metrics.min_latency}${s.metrics.min_latency_unit}`,
      maxLatency: (s) =>
        `${s.metrics.max_latency}${s.metrics.max_latency_unit}`,
      avgThroughput: (s) =>
        `${n2t(s.metrics.avg_throughput)}/${s.metrics.avg_throughput_unit}`,
      throughputQueries: (s) =>
        `${n2t(s.metrics.throughput_queries)}/${
          s.metrics.throughput_queries_unit
        }`,
      throughputResponses: (s) =>
        `${n2t(s.metrics.throughput_responses)}/${
          s.metrics.throughput_responses_unit
        }`,
    }),
  },
  store,
};

const ns2p = (n, d) => {
  return `${
    Math.round((n / Math.max(1, d)) * 100 * 10 + Number.EPSILON) / 10
  }%`;
};

const n2t = (num) => {
  if (typeof num !== "number" || num < 1) {
    return "0";
  } else if (num > 99999999999999999999) {
    return ">100E";
  }
  let i = Math.floor(Math.log(num) / Math.log(1000));
  let q = 10 ** Math.min(3, i);
  let value = Math.round((num / 1000 ** i + Number.EPSILON) * q) / q;
  let suffix = ["", "k", "M", "G", "T", "P", "E"][i];
  return `${value}${suffix}`;
};
</script>
