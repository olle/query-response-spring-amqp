<template>
  <ul>
    <router-link to="/" tag="li">
      <IconHome />
      <h2>Overview</h2>
    </router-link>
    <router-link class="disabled" to="/queries" tag="li">
      <IconQueries />
      <h2>Queries Insight</h2>
    </router-link>
    <router-link class="disabled" to="topology" tag="li">
      <IconTopology />
      <h2>Q/R Topology</h2>
    </router-link>
    <router-link class="disabled" to="/live" tag="li">
      <IconLive />
      <h2>Live</h2>
    </router-link>
    <router-link class="disabled" to="/logging" tag="li">
      <IconLogging />
      <h2>Logs</h2>
    </router-link>
    <router-link to="/cli" tag="li">
      <IconPrompt />
      <h2>Query CLI</h2>
    </router-link>
  </ul>
  <ul>
    <router-link class="disabled" to="settings" tag="li">
      <IconSettings />
      <h2>Settings</h2>
    </router-link>
    <QrColorSchemeToggle />
  </ul>
</template>

<script setup>
import IconSettings from "./IconSettings.vue";
import IconHome from "./IconHome.vue";
import IconQueries from "./IconQueries.vue";
import IconTopology from "./IconTopology.vue";
import IconLive from "./IconLive.vue";
import IconLogging from "./IconLogging.vue";
import IconPrompt from "./IconPrompt.vue";
import QrColorSchemeToggle from "./QrColorSchemeToggle.vue";
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
  font-size: 0.5rem;
  text-transform: uppercase;
  font-weight: 600;
}
</style>
