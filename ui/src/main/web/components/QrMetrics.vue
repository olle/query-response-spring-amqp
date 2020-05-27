<template>
  <section class="overview">
    <h2>Success Rate</h2>
    <chartist
      v-bind:class="successRateRank"
      class="chart one"
      type="Line"
      :data="successRates"
      :options="percentChart"
    ></chartist>
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
    <chartist
      class="chart two"
      type="Line"
      :data="latencies"
      :options="normalChart"
    ></chartist>
    <time class="big two">{{ avgLatency }}</time>
    <h3>Min</h3>
    <time>{{ minLatency }}</time>
    <h3>Max</h3>
    <time>{{ maxLatency }}</time>
    <h2>Throughput</h2>
    <chartist
      class="chart three"
      type="Line"
      :data="throughputs"
      :options="normalChart"
    ></chartist>
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
} from "../metrics.js";
import { mapState } from "vuex";
import Vue from "vue";

import Chartist from "vue-chartist";
Vue.use(Chartist);

export default {
  name: "qr-metrics",
  data: function () {

    const normalChart = {
      axisX: {
        showLabel: false,
        showGrid: false,
        offset: 0,
      },
      height: 93,
      showPoint: false,
      showArea: true,
      fullWidth: true,
      chartPadding: {
        top: 13,
        right: 0,
        bottom: 0,
        left: 0,
      },
    };

    const percentChart = {
      ...normalChart,
      axisY: {
        type: Chartist.FixedScaleAxis,
        high: 100,
        low: 0,
      },
    };

    return {
      normalChart: normalChart,
      percentChart: percentChart,
    };
  },
  computed: {
    ...mapState({
      successRate: (s) => `${s.metrics.success_rate}%`,
      successRateRank: (s) => {
        let rate = s.metrics.success_rate;
        if (rate < 20.0) {
          return "failure";
        } else if (rate < 60.0) {
          return "warning";
        }
      },
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
      successRates: (s) => ({ series: [[...s.metrics.success_rates]] }),
      latencies: (s) => ({ series: [[...s.metrics.avg_latencies]] }),
      throughputs: (s) => ({ series: [[...s.metrics.avg_throughputs]] }),
    }),
  },
  store,
};
</script>
