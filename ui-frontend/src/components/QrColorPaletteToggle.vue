<template>
  <li @click="toggleShowColors()">
    <icon-palette />
  </li>
</template>

<script setup>
import IconPalette from "./IconPalette.vue";
import { useStore } from "vuex";
const store = useStore();
function toggleShowColors() {
  store.commit("toggleShowColorPalette");
}
</script>
