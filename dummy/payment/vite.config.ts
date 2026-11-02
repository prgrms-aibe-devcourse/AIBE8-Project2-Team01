// Vite 설정: 더미 화면(index.html)과 와이어프레임(wireframe.html) 두 페이지를 빌드한다
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  // dist를 하위 경로에 올려도 동작하도록 상대 경로 사용
  base: './',
  server: { port: 5173 },
  build: {
    rollupOptions: {
      // 프로젝트 루트 기준 상대 경로
      input: {
        main: 'index.html',
        wireframe: 'wireframe.html'
      }
    }
  }
});
