<template>
  <li @click="toggleDark()">
    <IconMoon v-if="isDark"></IconMoon>
    <IconSun v-if="!isDark"></IconSun>
  </li>
</template>

<script setup>
import { IconSun, IconMoon } from "@tabler/icons-vue";
import { useDark, useToggle } from "@vueuse/core";

const isDark = useDark({ attribute: "data-theme" });
const toggleDark = useToggle(isDark);
</script>
