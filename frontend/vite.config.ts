import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/autenticacao': 'http://localhost:8080',
      '/cartas': 'http://localhost:8080',
    },
  },
})
