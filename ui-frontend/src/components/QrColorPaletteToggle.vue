<template>
  <li @click="store.toggle()">
    <IconPalette />
  </li>
</template>

<script setup>
import { IconPalette } from "@tabler/icons-vue";
import { useColorPaletteStore } from "../stores/useColorPaletteStore";
const store = useColorPaletteStore();
function toggleShowColors() {
  store.commit("toggleShowColorPalette");
}
</script>
