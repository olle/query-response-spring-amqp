<template>
  <div ref="chart" :class="props.ratio"></div>
</template>

<script setup>
import Chartist from "chartist";
import { ref, onMounted } from "vue";

const chart = ref(null);

const draw = function () {
  chart.value = new Chartist[props.type](
    chart.value,
    props.data,
    props.options,
    props.responsiveOptions
  );
};

onMounted(() => {
  draw();
});

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
</script>

<style>
.chart {
  font-size: 11px;
}
.ct-line {
  stroke-width: 4;
  stroke: var(--success);
  fill: none;
}
.ct-area {
  fill: var(--success);
  stroke: none;
  fill-opacity: 0.15;
}

.ct-grid {
  opacity: .1;
  stroke-width: 1;
}

.ct-label,
.ct-horizontal {
  color: var(--fg-light);
}

.failure .ct-line {
  stroke: var(--error);
}
.failure .ct-area {
  fill: var(--error);
}
.warning .ct-line {
  stroke: var(--warning);
}
.warning .ct-area {
  fill: var(--warning);
}
</style>
