import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { fileURLToPath, URL } from "node:url";

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  server: {
    proxy: {
      // Proxy toutes les routes API vers le back Spring Boot
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        // si ton back n’est pas au même basePath, décommente pour réécrire
        // rewrite: (path) => path.replace(/^\/api/, "/api"),
      },
      // (optionnel) ressources static exposées par le back
      "/files": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
