<template>
  <section class="wrapper">
    <nav>
      <QrNavigation />
    </nav>
    <main>
      <router-view></router-view>
    </main>
    <footer>
      <QrFooter />
    </footer>
  </section>
</template>

<script setup>
import QrNavigation from "./components/QrNavigation.vue";
import QrFooter from "./components/QrFooter.vue";
</script>

<style scoped>
.wrapper {
  padding: 0;
  margin: 0;
  display: grid;
  height: 100%;
  grid-template-areas:
    "nav main"
    "nav footer";
  grid-template-columns: 5rem auto;
  grid-template-rows: auto 2rem;
}

nav {
  grid-area: nav;
  background: var(--panel);
  border-right: var(--border);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

main {
  grid-area: main;
  height: 100%;
  padding: 1.2rem 1.2rem 0.4rem 6.2rem;
  display: flex;
  flex-direction: column;
  text-align: center;
}

footer {
  grid-area: footer;
}

main > article {
  flex: 1;
}
</style>
