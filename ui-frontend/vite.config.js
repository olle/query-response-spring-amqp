import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
        ws: true,
        // configure: (proxy, _options) => {
        //   proxy.on("error", (err, _req, _res) => {
        //     console.log("proxy error", err);
        //   });
        //   proxy.on("proxyReq", (proxyReq, req, _res) => {
        //     console.log("Sending Request to the Target:", req.method, req.url);
        //   });
        //   proxy.on("proxyRes", (proxyRes, req, _res) => {
        //     console.log(
        //       "Received Response from the Target:",
        //       proxyRes.statusCode,
        //       req.url
        //     );
        //   });
        // },
      },
    },
  },
});
