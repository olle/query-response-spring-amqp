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
import { useStore } from "vuex";
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
  height: 100,
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

const store = useStore();

function successRates() {
  return { series: [[1, 2, 3, 4, 5, 6, 7, 8, 9, 33, 44, 55, 66, 77]] };
  //return { series: [[...store.state.metrics.success_rates]] };
}
function successRate() {
  return `${store.state.metrics.success_rate}%`;
}
function successRateRank() {
  var rate = store.state.metrics.success_rate;
  if (rate < 20.0) {
    return "failure";
  } else if (rate < 60.0) {
    return "warning";
  }
}

function countQueries() {
  return toNumberWithUnit(store.state.metrics.count_queries);
}
function countResponses() {
  return toNumberWithUnit(store.state.metrics.count_responses);
}
function countFallbacks() {
  return toNumberWithUnit(store.state.metrics.count_fallbacks);
}

function latencies() {
  return { series: [[...store.state.metrics.avg_latencies]] };
}
function avgLatency() {
  return toMillis(store.state.metrics.avg_latency);
}
function minLatency() {
  return toMillis(store.state.metrics.min_latency);
}
function maxLatency() {
  return toMillis(store.state.metrics.max_latency);
}

function throughputs() {
  return { series: [[...store.state.metrics.avg_throughputs]] };
}
function avgThroughput() {
  return toThroughputPerSecond(store.state.metrics.avg_throughput);
}
function throughputQueries() {
  return toThroughputPerSecond(store.state.metrics.throughput_queries);
}
function throughputResponses() {
  return toThroughputPerSecond(store.state.metrics.throughput_responses);
}

// import {
//   toNumberWithUnit,
//   toMillis,
//   toThroughputPerSecond,
// } from "../metrics.js";
// import { mapState } from "vuex";
// import Vue from "vue";

// import Chartist from "vue-chartist";
// Vue.use(Chartist);

//   computed: {
//     ...mapState({

//     }),
//   }
// };
</script>

<style scoped>
main > article > section {
  background: var(--panel);
  border: var(--border);
  padding: 0.8rem 1rem;
}

main > article > section > data,
main > article > section > time {
  display: inline-block;
}

main > article > section > data.big,
main > article > section > time.big {
  font-size: 2rem;
  font-weight: var(--bold);
}

/* OVERVIEW ----------------------------------------------------------------- */

section.overview {
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
}

@media (min-width: 1000px) {
  section.overview {
    grid-template-columns: min-content 5.8rem 1fr 6rem 5.8rem 1fr min-content 5.8rem 1fr;
    grid-template-rows: 1.7rem 6rem 1rem 2rem;
    grid-template-areas:
      "t1 t1 t1 t2 t2 t2 t3 t3 t3"
      "b1 b1a b1a b2 b2a b2a b3 b3a b3a"
      "l1a l1b l1c l2a l2b l2c l3a l3b l3c"
      "v1a v1b v1c v2a v2b v2c v3a v3b v3c";
  }
}

section.overview > h2:nth-of-type(1) {
  grid-area: t1;
}

section.overview > h2:nth-of-type(2) {
  grid-area: t2;
}

section.overview > h2:nth-of-type(3) {
  grid-area: t3;
}

section.overview > .big {
  align-self: end;
}

section.overview > .big.one {
  grid-area: b1;
}
section.overview > .big.two {
  grid-area: b2;
}
section.overview > .big.three {
  grid-area: b3;
}

section.overview > .chart.one {
  grid-area: b1a;
}
section.overview > .chart.two {
  grid-area: b2a;
}
section.overview > .chart.three {
  grid-area: b3a;
}
</style>
