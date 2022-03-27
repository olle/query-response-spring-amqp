<template>
  <nav>
    <QrNavigation />
  </nav>
  <main>
    <router-view></router-view>
    <QrFooter />
  </main>
</template>

<script setup>
import QrNavigation from "./components/QrNavigation.vue";
import QrFooter from "./components/QrFooter.vue";
</script>
