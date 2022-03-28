import { createApp } from "vue";
import { createRouter, createWebHistory } from "vue-router";
import App from "./App.vue";
import "./style.css";

const app = createApp(App);
app.use(
  createRouter({
    history: createWebHistory(),
    routes: [],
  })
);
app.mount("#app");
