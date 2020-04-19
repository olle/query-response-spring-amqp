<template>
  <li v-on:click="theme = theme === 'light' ? 'dark' : 'light'">
    <moon-icon v-if="theme === 'light'"></moon-icon>
    <sun-icon v-if="theme === 'dark'"></sun-icon>
  </li>
</template>

<script>
import SunIcon from "../icons/SunIcon.vue";
import MoonIcon from "../icons/MoonIcon.vue";

export default {
  name: "qr-color-scheme-toggle",
  created: function () {
    let storedTheme = window.localStorage.getItem("theme");
    if (storedTheme) {
      this.theme = storedTheme;
    } else {
      this.theme = window.matchMedia("(prefers-color-scheme: dark)").matches
        ? "dark"
        : "light";
    }
    window.matchMedia("(prefers-color-scheme: dark)").addListener((evt) => {
      this.theme = evt.matches ? "dark" : "light";
    });
  },
  data: () => ({
    theme: "light",
  }),
  watch: {
    theme: (theme) => {
      window.localStorage.setItem("theme", theme);
      document.querySelector("body").setAttribute("data-theme", theme);
    },
  },
  components: {
    SunIcon,
    MoonIcon,
  },
};
</script>
