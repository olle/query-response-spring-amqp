<template>
  <ul v-if="showColorPalette()">
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
import { useStore } from "vuex";
const store = useStore();

function showColorPalette() {
  return store.state.showColorPalette;
}
</script>

<style scoped>
.color {
  display: inline-block;
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  margin: 0.3rem;
}

.color-fg {
  background: var(--fg);
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
