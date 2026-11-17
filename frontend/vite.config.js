import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// Proxy /api về backend để cookie refresh_token (SameSite=Strict) luôn cùng origin khi chạy dev
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': { target: 'http://localhost:8080', changeOrigin: true },
    },
  },
})
