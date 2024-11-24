<template>
  <section class="wrapper">
    <nav>
      <QrNavigation />
    </nav>
    <main>
      <router-view></router-view>
      <qr-colors />
    </main>
    <footer>
      <QrFooter />
    </footer>
  </section>
</template>

<script setup>
import QrNavigation from "./components/QrNavigation.vue";
import QrFooter from "./components/QrFooter.vue";
import QrColors from "./components/QrColors.vue";
import { useWebSocket } from "@vueuse/core";
import { watch } from "vue";

const { status, data, close } = useWebSocket("ws://websocketurl", {
  autoReconnect: true,
  heartbeat: true,
});

watch(data, (msg) => {
  try {
    var message = JSON.parse(msg.data);
    if (message.metrics) {
      //store.commit("metrics", message.metrics);
    }
    if (message.nodes) {
      //store.commit("nodes", message.nodes);
    }
  } catch (err) {
    console.error("unexpected payload", msg.data);
  }
});

// import { addListener } from "./ws";
// addListener((msg) => {
// });
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
  padding: 1.2rem 1.2rem 0.4rem 1.2rem;
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
