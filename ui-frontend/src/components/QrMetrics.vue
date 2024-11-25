<template>
  <section class="overview">
    <h2>Success Rate</h2>
    <chartist
      v-bind:class="successRateRank()"
      class="chart one"
      type="Line"
      :data="successRates()"
      :options="percentChart"
    ></chartist>
    <data class="big one" v-bind:class="successRateRank()">
      {{ successRate() }}
    </data>
    <h3>Queries</h3>
    <data>{{ countQueries() }}</data>
    <h3>Responses</h3>
    <data>{{ countResponses() }}</data>
    <h3>Fallbacks</h3>
    <data>{{ countFallbacks() }}</data>

    <h2>Latency</h2>
    <chartist
      class="chart two"
      type="Line"
      :data="latencies()"
      :options="normalChart"
    ></chartist>
    <time class="big two">{{ avgLatency() }}</time>
    <h3>Min</h3>
    <time>{{ minLatency() }}</time>
    <h3>Max</h3>
    <time>{{ maxLatency() }}</time>

    <h2>Throughput</h2>
    <chartist
      class="chart three"
      type="Line"
      :data="throughputs()"
      :options="normalChart"
    ></chartist>
    <data class="big three">{{ avgThroughput() }}</data>
    <h3>Queries</h3>
    <data>{{ throughputQueries() }}</data>
    <h3>Responses</h3>
    <data>{{ throughputResponses() }}</data>
  </section>
</template>

<script setup>
import Chartist from "./Chartist.vue";
import { useMetricsStore } from "../stores/useMetricsStore.js";
import {
  toNumberWithUnit,
  toMillis,
  toThroughputPerSecond,
} from "../metrics.js";

const normalChart = {
  axisX: {
    showLabel: false,
    showGrid: false,
    offset: 0,
  },
  showPoint: false,
  showArea: true,
  fullWidth: true,
  chartPadding: {
    top: 2,
    right: 2,
    bottom: 2,
    left: 2,
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

const store = useMetricsStore();

function successRates() {
  return { series: [[...Object.values(store.metrics.success_rates || [0])]] };
}
function successRate() {
  return `${store.metrics.success_rate}%`;
}
function successRateRank() {
  var rate = store.metrics.success_rate;
  if (rate < 20.0) {
    return "failure";
  } else if (rate < 60.0) {
    return "warning";
  }
}

function countQueries() {
  return toNumberWithUnit(store.metrics.count_queries);
}
function countResponses() {
  return toNumberWithUnit(store.metrics.count_responses);
}
function countFallbacks() {
  return toNumberWithUnit(store.metrics.count_fallbacks);
}

function latencies() {
  return { series: [[...Object.values(store.metrics.avg_latencies || [0])]] };
}
function avgLatency() {
  return toMillis(store.metrics.avg_latency);
}
function minLatency() {
  return toMillis(store.metrics.min_latency);
}
function maxLatency() {
  return toMillis(store.metrics.max_latency);
}

function throughputs() {
  return { series: [[...Object.values(store.metrics.avg_throughputs || [0])]] };
}
function avgThroughput() {
  return toThroughputPerSecond(store.metrics.avg_throughput);
}
function throughputQueries() {
  return toThroughputPerSecond(store.metrics.throughput_queries);
}
function throughputResponses() {
  return toThroughputPerSecond(store.metrics.throughput_responses);
}
</script>

<style scoped>
.overview {
  background: var(--panel);
  border: var(--border);
  padding: 0.8rem 1rem;
  border-radius: var(--border-radius);
  text-align: left;
}

.overview > .chart {
  padding-bottom: 0.4rem;
  display: none;
}
.overview > .chart {
  width: 13vw;
  height: 6rem;
}
@media (min-width: 1000px) {
  .overview > .chart {
    display: initial;
  }
}
@media (min-width: 1200px) {
  .overview > .chart {
    width: 100%;
  }
}

data,
time {
  color: var(--success);
}

data.big,
time.big {
  font-size: 2.1rem;
  font-weight: var(--bold);
}

.overview {
  min-width: 37rem;
  display: grid;
  grid-template-columns: 5.3rem 1fr 2.1rem 1fr 5.3rem 1fr;
  column-gap: 1rem;
  grid-template-rows: 1.7rem 3rem repeat(3, 2rem);
  grid-template-areas:
    "t1 t1 t2 t2 t3 t3"
    "b1 b1 b2 b2 b3 b3"
    "l1a v1a l2a v2a l3a v3a"
    "l1b v1b l2b v2b l3b v3b"
    "l1c v1c . . . .";
  margin-bottom: 1rem;
  row-gap: 0.2rem;
}

@media (min-width: 1000px) {
  .overview {
    grid-template-columns: min-content 5.8rem 1fr 6rem 5.8rem 1fr min-content 5.8rem 1fr;
    grid-template-rows: 1.7rem 6rem 1rem 2rem;
    grid-template-areas:
      "t1 t1 t1 t2 t2 t2 t3 t3 t3"
      "b1 b1a b1a b2 b2a b2a b3 b3a b3a"
      "l1a l1b l1c l2a l2b l2c l3a l3b l3c"
      "v1a v1b v1c v2a v2b v2c v3a v3b v3c";
  }
}

h2 {
  font-weight: var(--bold);
}

h3,
data:not(.big),
time:not(.big) {
  align-self: center;
}

h2:nth-of-type(1) {
  grid-area: t1;
}

h2:nth-of-type(2) {
  grid-area: t2;
}

h2:nth-of-type(3) {
  grid-area: t3;
}

.big {
  align-self: end;
}

.big.one {
  grid-area: b1;
}
.big.two {
  grid-area: b2;
}
.big.three {
  grid-area: b3;
}

.chart.one {
  grid-area: b1a;
}
.chart.two {
  grid-area: b2a;
}
.chart.three {
  grid-area: b3a;
}

h3:nth-of-type(1) {
  grid-area: l1a;
}

h3:nth-of-type(2) {
  grid-area: l1b;
}

h3:nth-of-type(3) {
  grid-area: l1c;
}

h3:nth-of-type(4) {
  grid-area: l2a;
}

h3:nth-of-type(5) {
  grid-area: l2b;
}

h3:nth-of-type(6) {
  grid-area: l3a;
}

h3:nth-of-type(7) {
  grid-area: l3b;
}

data:nth-of-type(2) {
  grid-area: v1a;
}

data:nth-of-type(3) {
  grid-area: v1b;
}

data:nth-of-type(4) {
  grid-area: v1c;
}

time:nth-of-type(2) {
  grid-area: v2a;
}

time:nth-of-type(3) {
  grid-area: v2b;
}

data:nth-of-type(6) {
  grid-area: v3a;
}

data:nth-of-type(7) {
  grid-area: v3b;
}
</style>
