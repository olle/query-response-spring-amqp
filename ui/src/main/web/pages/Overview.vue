<template>
  <article id="overview">
    <h1>Query/Response System Overview</h1>
    <qr-metrics></qr-metrics>
    <qr-live-nodes></qr-live-nodes>
  </article>
</template>

<script>
import QrMetrics from "../components/QrMetrics.vue";
import QrLiveNodes from "../components/QrLiveNodes.vue";

export default {
  name: "Overview",
  components: {
    QrMetrics,
    QrLiveNodes,
  },
};
</script>
