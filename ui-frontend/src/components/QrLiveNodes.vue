<template>
  <table class="nodes">
    <caption class="h2">
      Live Nodes
    </caption>
    <thead>
      <tr>
        <th class="h3">Name</th>
        <th class="h3">Pid</th>
        <th class="h3">Address</th>
        <th class="h3">Uptime</th>
        <th class="h3">Success Rate</th>
        <th class="h3">Queries</th>
        <th class="h3">Responses</th>
        <th class="h3">Latency</th>
        <th class="h3">Throughput</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="node in nodes" :key="node.key">
        <td>{{ node.name }}</td>
        <td>{{ node.pid }}</td>
        <td>{{ node.host }}</td>
        <td>
          <time>{{ node.uptime }}</time>
        </td>
        <td v-if="node.only_responses"><data>–</data></td>
        <td v-else>
          <data v-bind:class="node.successRateRank">
            {{ node.successRate }}
          </data>
          <small>
            <data data-abbr="Q" title="Queries">{{ node.countQueries }}</data>
            <data data-abbr="R" title="Responses">{{
              node.countResponses
            }}</data>
            <data data-abbr="F" title="Fallback">{{
              node.countFallbacks
            }}</data>
          </small>
        </td>
        <td>
          <data>{{ node.countQueries }}</data>
        </td>
        <td>
          <data>{{ node.publishedResponses }}</data>
        </td>
        <td>
          <time>{{ node.avgLatency }}</time>
          <small>
            <time data-abbr="Min" title="Min">{{ node.minLatency }}</time>
            <time data-abbr="Max" title="Max">{{ node.maxLatency }}</time>
          </small>
        </td>
        <td>
          <data>{{ node.avgThroughput }}</data>
          <small>
            <data data-abbr="Q" title="Queries">{{
              node.throughputQueries
            }}</data>
            <data data-abbr="R" title="Responses">{{
              node.throughputResponses
            }}</data>
          </small>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
import {
  toPercent,
  toRateRank,
  toNumberWithUnit,
  toThroughputPerSecond,
  toMillis,
} from "../metrics.js";
import { computed } from "vue";
import { useLiveNodesStore } from "../stores/useLiveNodesStore.js";

const store = useLiveNodesStore();

const nodes = computed(() => {
  let results = [];
  for (let uuid in store.nodes) {
    let node = { key: uuid };
    let entry = store.nodes[uuid];
    for (let i in entry) {
      node[entry[i].key] = entry[i].value;
    }
    node.successRate = toPercent(
      node.count_consumed_responses,
      node.count_queries
    );
    node.successRateRank = toRateRank(
      node.count_consumed_responses,
      node.count_queries
    );
    node.countQueries = toNumberWithUnit(node.count_queries);
    node.countResponses = toNumberWithUnit(node.count_consumed_responses);
    node.countFallbacks = toNumberWithUnit(node.count_fallbacks);
    node.publishedResponses = toNumberWithUnit(node.count_published_responses);
    node.throughputQueries = toThroughputPerSecond(node.throughput_queries);
    node.throughputResponses = toThroughputPerSecond(node.throughput_responses);
    node.avgThroughput = toThroughputPerSecond(node.avg_throughput);
    node.avgLatency = toMillis(node.avg_latency);
    node.minLatency = toMillis(node.min_latency);
    node.maxLatency = toMillis(node.max_latency);
    results.push(node);
  }
  return results;
});
</script>

<style scoped>
table {
  text-align: left;
}
</style>
