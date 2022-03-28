<template>
  <li v-on:click="theme = theme === 'light' ? 'dark' : 'light'">
    <IconSun v-if="theme === 'light'"></IconSun>
    <IconMoon v-if="theme === 'dark'"></IconMoon>
  </li>
</template>

<script setup>
import IconSun from "./IconSun.vue";
import IconMoon from "./IconMoon.vue";
import { ref, watch, onMounted, onBeforeUnmount } from "vue";

const theme = ref();

watch(theme, (value) => {
  window.localStorage.setItem("theme", value);
  document.querySelector("html").setAttribute("data-theme", value);
});

const matchMedia = window.matchMedia("(prefers-color-scheme: dark)");

const onColorSchemeChange = (evt) => {
  theme.value = evt.matches ? "dark" : "light";
};

onMounted(() => {
  let storedTheme = window.localStorage.getItem("theme");
  if (storedTheme) {
    theme.value = storedTheme;
  } else {
    theme.value = matchMedia.matches ? "dark" : "light";
  }
  // Add listener
  matchMedia.addEventListener("change", onColorSchemeChange);
});

onBeforeUnmount(() => {
  // Remove listener
  matchMedia.removeEventListener("change", onColorSchemeChange);
});
</script>
