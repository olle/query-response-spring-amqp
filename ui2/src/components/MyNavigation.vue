<template>
  <ul>
    <router-link to="/" tag="li">
      <home-icon></home-icon>
      <h2>Overview</h2>
    </router-link>
    <router-link class="disabled" to="/queries" tag="li">
      <queries-icon></queries-icon>
      <h2>Queries Insight</h2>
    </router-link>
    <router-link class="disabled" to="topology" tag="li">
      <topology-icon></topology-icon>
      <h2>Q/R Topology</h2>
    </router-link>
    <router-link class="disabled" to="/live" tag="li">
      <live-icon></live-icon>
      <h2>Live</h2>
    </router-link>
    <router-link class="disabled" to="/logging" tag="li">
      <logging-icon></logging-icon>
      <h2>Logs</h2>
    </router-link>
    <router-link to="/cli" tag="li">
      <prompt-icon></prompt-icon>
      <h2>Query CLI</h2>
    </router-link>
  </ul>
  <ul>
    <router-link class="disabled" to="settings" tag="li">
      <settings-icon></settings-icon>
      <h2>Settings</h2>
    </router-link>
    <qr-color-scheme-toggle></qr-color-scheme-toggle>
  </ul>
</template>
