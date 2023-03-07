<template>
  <li @click="toggleShowColors()">
    <IconPalette />
  </li>
</template>

<script setup>
import { IconPalette } from "@tabler/icons-vue";
import { useStore } from "vuex";
const store = useStore();
function toggleShowColors() {
  store.commit("toggleShowColorPalette");
}
</script>
