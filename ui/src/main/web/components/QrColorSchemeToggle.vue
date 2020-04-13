<template>
  <li>
    <moon-icon v-if="currentColorScheme === 'light'"></moon-icon>
    <sun-icon v-if="currentColorScheme === 'dark'"></sun-icon>
  </li>
</template>

<script>
import SunIcon from "./SunIcon.vue";
import MoonIcon from "./MoonIcon.vue";

export default {
  name: "qr-color-scheme-toggle",
  computed: {
    currentColorScheme: () => "light"
  },
  components: {
    SunIcon,
    MoonIcon
  }
};
</script>
