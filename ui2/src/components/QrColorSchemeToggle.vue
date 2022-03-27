<template>
  <li v-on:click="theme = theme === 'light' ? 'dark' : 'light'">
    <IconSun v-if="theme === 'light'"></IconSun>
    <IconMoon v-if="theme === 'dark'"></IconMoon>
  </li>
</template>

<script>
import IconSun from "./IconSun.vue";
import IconMoon from "./IconMoon.vue";

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
    IconSun,
    IconMoon,
  },
};
</script>
