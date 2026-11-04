// Vite 설정
// 로컬 개발: /api 요청을 로컬 백엔드(http://localhost:8080)로 넘긴다.
// 같은 출처로 요청하므로 리프레시 토큰 쿠키(경로 /api/public/auth/refresh)가 그대로 붙는다.
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  // dist를 하위 경로에 올려도 동작하도록 상대 경로 사용 (해시 라우팅)
  base: './',
  server: {
    port: 5173,
    proxy: {
      '/api': { target: 'http://localhost:8080' },
      // 채팅 웹소켓(STOMP)도 같은 출처로 연결한다
      '/ws': { target: 'ws://localhost:8080', ws: true }
    }
  }
});
