<template>
  <section class="overview">
    <h2>Success Rate</h2>
    <div class="chart one"><line-chart></line-chart></div>
    <data class="big one" v-bind:class="successRateRank">{{
      successRate
    }}</data>
    <h3>Queries</h3>
    <data>{{ countQueries }}</data>
    <h3>Responses</h3>
    <data>{{ countResponses }}</data>
    <h3>Fallbacks</h3>
    <data>{{ countFallbacks }}</data>
    <h2>Latency</h2>
    <div class="chart two"><line-chart></line-chart></div>
    <time class="big two">{{ avgLatency }}</time>
    <h3>Min</h3>
    <time>{{ minLatency }}</time>
    <h3>Max</h3>
    <time>{{ maxLatency }}</time>
    <h2>Throughput</h2>
    <div class="chart three"><line-chart></line-chart></div>
    <data class="big three">{{ avgThroughput }}</data>
    <h3>Queries</h3>
    <data>{{ throughputQueries }}</data>
    <h3>Responses</h3>
    <data>{{ throughputResponses }}</data>
  </section>
</template>

<script>
import store from "../store.js";
import {
  toNumberWithUnit,
  toMillis,
  toThroughputPerSecond,
  toPercent,
  toRateRank,
} from "../metrics.js";
import { mapState } from "vuex";
import Vue from "vue";
//import Chart from "chartjs";

Vue.component("line-chart", {
  mounted() {
    console.log("EL: ", this.$refs);
  },
  template: "<canvas>chart-goes-here</canvas>",
});

export default {
  name: "qr-metrics",
  computed: {
    ...mapState({
      successRate: (s) =>
        toPercent(s.metrics.count_responses, s.metrics.count_queries),
      successRateRank: (s) =>
        toRateRank(s.metrics.count_responses, s.metrics.count_queries),
      countQueries: (s) => toNumberWithUnit(s.metrics.count_queries),
      countResponses: (s) => toNumberWithUnit(s.metrics.count_responses),
      countFallbacks: (s) => toNumberWithUnit(s.metrics.count_fallbacks),
      avgLatency: (s) => toMillis(s.metrics.avg_latency),
      minLatency: (s) => toMillis(s.metrics.min_latency),
      maxLatency: (s) => toMillis(s.metrics.max_latency),
      avgThroughput: (s) => toThroughputPerSecond(s.metrics.avg_throughput),
      throughputQueries: (s) =>
        toThroughputPerSecond(s.metrics.throughput_queries),
      throughputResponses: (s) =>
        toThroughputPerSecond(s.metrics.throughput_responses),
    }),
  },
  store,
};
</script>
