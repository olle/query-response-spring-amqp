import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

// https://vitejs.dev/config/
const backend = "http://localhost:8080";

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      "/api": {
        target: backend,
        changeOrigin: true,
        secure: false,
      },
      "/ws": {
        target: backend,
        changeOrigin: true,
        ws: true,
      },
    },
  },
});
