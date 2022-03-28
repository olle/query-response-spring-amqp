<template>
  <ul>
    <router-link to="/" custom v-slot="{ navigate }">
      <li @click="navigate" @keypress.enter="navigate" role="link">
        <IconHome />
        <h2>Overview</h2>
      </li>
    </router-link>
    <router-link to="/queries" custom v-slot="{ navigate }">
      <li
        @click="navigate"
        @keypress.enter="navigate"
        role="link"
        class="disabled"
      >
        <IconQueries />
        <h2>Queries Insight</h2>
      </li>
    </router-link>
    <router-link to="topology" custom v-slot="{ navigate }">
      <li
        @click="navigate"
        @keypress.enter="navigate"
        role="link"
        class="disabled"
      >
        <IconTopology />
        <h2>Q/R Topology</h2>
      </li>
    </router-link>
    <router-link to="/live" custom v-slot="{ navigate }">
      <li
        @click="navigate"
        @keypress.enter="navigate"
        role="link"
        class="disabled"
      >
        <IconLive />
        <h2>Live</h2>
      </li>
    </router-link>
    <router-link to="/logging" custom v-slot="{ navigate }">
      <li
        @click="navigate"
        @keypress.enter="navigate"
        role="link"
        class="disabled"
      >
        <IconLogging />
        <h2>Logs</h2>
      </li>
    </router-link>
    <router-link to="/cli" custom v-slot="{ navigate }">
      <li
        @click="navigate"
        @keypress.enter="navigate"
        role="link"
      >
        <IconPrompt />
        <h2>Query CLI</h2>
      </li>
    </router-link>
  </ul>
  <ul>
    <router-link to="settings" custom v-slot="{ navigate }">
      <li
        @click="navigate"
        @keypress.enter="navigate"
        role="link"
        class="disabled"
      >
        <IconSettings />
        <h2>Settings</h2>
      </li>
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
