<template>
  <ul v-if="show">
    <li class="color color-bg"></li>
    <li class="color color-panel"></li>
    <li class="color color-fg"></li>
    <li class="color color-link"></li>
    <li class="color color-link-hi-c"></li>
    <li class="color color-success"></li>
    <li class="color color-warning"></li>
    <li class="color color-error"></li>
  </ul>
</template>

<script setup>
import { storeToRefs } from "pinia";
import { useColorPaletteStore } from "../stores/useColorPaletteStore";
const store = useColorPaletteStore();
const { show } = storeToRefs(store);
</script>

<style scoped>
.color {
  display: inline-block;
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  margin: 0.3rem;
}

.color-bg {
  background: var(--bg);
  border: 1px solid var(--fg);
}
.color-fg {
  background: var(--fg);
  border: 1px solid var(--bg);
}

.color-link {
  background: var(--link);
}

.color-link-hi-c {
  background: var(--link-high-contrast);
}

.color-success {
  background: var(--success);
}

.color-warning {
  background: var(--warning);
}

.color-error {
  background: var(--error);
}

.color-panel {
  background: var(--panel);
}
</style>
