<template>
  <ul>
    <router-link to="/" custom v-slot="{ navigate }">
      <li @click="navigate" @keypress.enter="navigate" role="link">
        <IconHome />
        <h2>Overview</h2>
      </li>
    </router-link>
    <router-link to="/queries" custom v-slot="{ navigate }">
      <li @click="navigate" @keypress.enter="navigate" role="link" class="disabled">
        <IconMessage2 />
        <h2>Queries Insight</h2>
      </li>
    </router-link>
    <router-link to="topology" custom v-slot="{ navigate }">
      <li @click="navigate" @keypress.enter="navigate" role="link" class="disabled">
        <IconRoute />
        <h2>Q/R Topology</h2>
      </li>
    </router-link>
    <router-link to="/live" custom v-slot="{ navigate }">
      <li @click="navigate" @keypress.enter="navigate" role="link" class="disabled">
        <IconActivity />
        <h2>Live</h2>
      </li>
    </router-link>
    <router-link to="/logging" custom v-slot="{ navigate }">
      <li @click="navigate" @keypress.enter="navigate" role="link" class="disabled">
        <IconFileText />
        <h2>Logs</h2>
      </li>
    </router-link>
    <router-link to="/cli" custom v-slot="{ navigate }">
      <li @click="navigate" @keypress.enter="navigate" role="link">
        <IconTerminal2 />
        <h2>Query CLI</h2>
      </li>
    </router-link>
    <router-link to="settings" custom v-slot="{ navigate }">
      <li @click="navigate" @keypress.enter="navigate" role="link" class="disabled">
        <IconSettings />
        <h2>Settings</h2>
      </li>
    </router-link>
    <qr-color-scheme-toggle />
    <qr-color-palette-toggle />
  </ul>
</template>

<script setup>
import {
  IconSettings,
  IconHome,
  IconMessage2,
  IconRoute,
  IconActivity,
  IconFileText,
  IconTerminal2,
} from "@tabler/icons-vue";

import QrColorSchemeToggle from "./QrColorSchemeToggle.vue";
import QrColorPaletteToggle from "./QrColorPaletteToggle.vue";
</script>

<style scoped>
ul {
  width: 100%;
  padding: 0;
  margin: 0;
  list-style: none;
  list-style-position: outside;
}

ul > li {
  width: 100%;
  margin: 0;
  padding: 1rem;
  text-align: center;
  cursor: pointer;
  color: var(--fg);
  fill: var(--fg);
}

ul > li.static {
  cursor: default;
  padding: 0.6rem 1rem;
}

ul > li:not(.disabled):hover,
ul > li:not(.disabled):active {
  background: var(--bg-hover);
  color: var(--link-high-contrast);
  fill: var(--link-high-contrast);
}

ul > li.static:hover,
ul > li.static:active {
  background: inherit;
  color: inherit;
  fill: inherit;
}

ul > li.active {
  color: var(--link-high-contrast);
  fill: var(--link-high-contrast);
}

ul > li.disabled {
  opacity: 0.2;
}

ul > li > h2 {
  font-size: 0.54rem;
  text-transform: uppercase;
  font-weight: 600;
}
</style>
