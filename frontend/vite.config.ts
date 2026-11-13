import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// Backend de desenvolvimento. Pode ser sobrescrito com VITE_API_TARGET
// (ex.: VITE_API_TARGET=http://localhost:18080 npm run dev).
const apiTarget = process.env.VITE_API_TARGET ?? 'http://localhost:8080'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/autenticacao': apiTarget,
      '/cartas': apiTarget,
    },
  },
})
