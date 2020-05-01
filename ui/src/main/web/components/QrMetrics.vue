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
      avgLatency: (s) => n2ms(s.metrics.avg_latency),
      minLatency: (s) => n2ms(s.metrics.min_latency),
      maxLatency: (s) => n2ms(s.metrics.max_latency),
      avgThroughput: (s) => n2tp(s.metrics.avg_throughput),
      throughputQueries: (s) => n2tp(s.metrics.throughput_queries),
      throughputResponses: (s) => n2tp(s.metrics.throughput_responses),
    }),
  },
  store,
};

const n2tp = (d) => {
  return `${Math.round(d * 10 + Number.EPSILON) / 10}/s`;
};

const n2ms = (ms) => {
  return `${Math.round(ms * 10 + Number.EPSILON) / 10}ms`;
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
