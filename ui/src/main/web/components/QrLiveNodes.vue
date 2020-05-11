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
        <th class="h3">Latency (Avg)</th>
        <th class="h3">Throughput (Avg)</th>
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
        <td>
          <data v-bind:class="node.successRateRank">{{
            node.successRate
          }}</data>
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
        <td>-</td>
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
      <!--tr>
        <td>jembo</td>
        <td>0</td>
        <td>lb1-jembo.some.cloud</td>
        <td>
          <time>3 hours</time>
        </td>
        <td>
          <data class="warning">12%</data>
          <small>
            <data data-abbr="Q" title="Queries">232K</data>
            <data data-abbr="R" title="Responses">13K</data>
            <data data-abbr="F" title="Fallback">13K</data>
          </small>
        </td>
        <td>
          <time>44ms</time>
          <small>
            <time data-abbr="Min" title="Min">2ms</time>
            <time data-abbr="Max" title="Max">231ms</time>
          </small>
        </td>
        <td>
          <data>55K/s</data>
          <small>
            <data data-abbr="Q" title="Queries">2K/s</data>
            <data data-abbr="R" title="Responses">312K/s</data>
          </small>
        </td>
      </tr -->
    </tbody>
  </table>
</template>

<script>
import store from "../store.js";
import {
  toPercent,
  toRateRank,
  toNumberWithUnit,
  toThroughputPerSecond,
} from "../metrics.js";
import { mapState } from "vuex";

export default {
  name: "qr-live-nodes",
  computed: {
    ...mapState({
      nodes: (s) => {
        let results = [];
        for (let uuid in s.nodes) {
          let node = { key: uuid };
          let entry = s.nodes[uuid];
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
          node.throughputQueries = toThroughputPerSecond(
            node.throughput_queries
          );
          node.throughputResponses = toThroughputPerSecond(
            node.throughput_responses
          );
          node.avgThroughput = toThroughputPerSecond(node.avg_throughput);
          results.push(node);
        }
        return results;
      },
    }),
  },
  store,
};
</script>
