<template>
  <li
    v-bind:class="{ active: active, disabled: disabled }"
    v-bind:title="title"
  >
    <slot></slot>
    <h2>{{ title }}</h2>
  </li>
</template>

<script>
import HomeIcon from "./HomeIcon.vue";
import LiveIcon from "./LiveIcon.vue";
import QueriesIcon from "./QueriesIcon.vue";
import TopologyIcon from "./TopologyIcon.vue";
import LoggingIcon from "./LoggingIcon.vue";
import SettingsIcon from "./SettingsIcon.vue";

import Vue from "vue";

Vue.component("home-icon", HomeIcon);
Vue.component("live-icon", LiveIcon);
Vue.component("queries-icon", QueriesIcon);
Vue.component("topology-icon", TopologyIcon);
Vue.component("logging-icon", LoggingIcon);
Vue.component("settings-icon", SettingsIcon);

export default {
  name: "qr-nav",
  props: {
    title: String,
    page: String,
    active: {
      type: Boolean,
      default: false,
      required: false
    },
    disabled: {
      type: Boolean,
      default: false,
      required: false
    }
  }
};
</script>
