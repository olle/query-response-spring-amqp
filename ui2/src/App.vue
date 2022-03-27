<template>
  <nav>
    <MyNavigation />
  </nav>
  <main>
    <router-view></router-view>
    <MyFooter />
  </main>
</template>

<script setup>
import MyNavigation from "./components/MyNavigation.vue";
import MyFooter from "./components/MyFooter.vue";
</script>
