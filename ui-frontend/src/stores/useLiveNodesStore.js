import { defineStore } from "pinia";
import { ref } from "vue";

export const useLiveNodesStore = defineStore("live-nodes", () => {
  const nodes = ref({});
  return {
    nodes,
  };
});
