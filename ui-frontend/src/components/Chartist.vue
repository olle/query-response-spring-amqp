<template>
  <div ref="chartElement" :class="props.ratio"></div>
</template>

<script setup>
import { BarChart, LineChart, PieChart } from "chartist";
import "chartist/styles.css";
import { onBeforeUnmount, onMounted, ref, watch } from "vue";

const chartTypes = {
  Line: LineChart,
  Bar: BarChart,
  Pie: PieChart,
};

const chartElement = ref(null);
let chart = null;

const props = defineProps({
  ratio: {
    type: String,
    default: "ct-square",
  },
  data: {
    type: Object,
    default: function _default() {
      return {
        series: [],
        labels: [],
      };
    },
  },
  options: {
    type: Object,
    default: function _default() {
      return {};
    },
  },
  type: {
    type: String,
    required: true,
    validator: function validator(val) {
      return val === "Pie" || val === "Line" || val === "Bar";
    },
  },
  eventHandlers: {
    type: Array,
    default: function _default() {
      return [];
    },
  },
  responsiveOptions: {
    type: Array,
    default: function _default() {
      return [];
    },
  },
  noData: {
    type: Object,
    default: function _default() {
      return {
        message: "",
        class: "ct-nodata",
      };
    },
  },
});

function destroy() {
  if (!chart) {
    return;
  }
  chart.detach();
  chart = null;
}

function draw() {
  destroy();

  if (!chartElement.value) {
    return;
  }

  chart = new chartTypes[props.type](
    chartElement.value,
    props.data,
    props.options,
    props.responsiveOptions
  );

  for (const { event, fn } of props.eventHandlers) {
    chart.on(event, fn);
  }
}

function update() {
  if (!chart) {
    draw();
    return;
  }
  chart.update(props.data, props.options);
}

onMounted(draw);

watch(() => props.type, draw);
watch(() => props.responsiveOptions, draw, { deep: true });
watch(() => [props.data, props.options], update, { deep: true });

onBeforeUnmount(destroy);
</script>

<style>
.chart {
  font-size: 11px;
  /* Chartist 2.x resolves its series color through this variable. Setting it
     here beats the library's red default without fighting its selectors. */
  --ct-series-a: var(--success);
}
.chart.warning {
  --ct-series-a: var(--warning);
}
.chart.failure {
  --ct-series-a: var(--error);
}
.ct-line {
  stroke-width: 4;
  fill: none;
}
.ct-area {
  stroke: none;
  fill-opacity: 0.15;
}
.ct-labels {
  text-align: right;
}

.ct-grid {
  opacity: .1;
  stroke-width: 1;
}

.ct-label,
.ct-horizontal {
  color: var(--fg-light);
}
</style>
