import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  const target = env.EFY_PROXY_TARGET || 'http://localhost:8080';

  // 백엔드 CORS는 배포 프론트 주소만 허용하므로 Origin 헤더를 지워서 서버 간 요청처럼 넘긴다.
  const dropOrigin = (proxy) => {
    proxy.on('proxyReq', (req) => req.removeHeader('origin'));
    proxy.on('proxyReqWs', (req) => req.removeHeader('origin'));
  };

  return {
    plugins: [react()],
    server: {
      port: 5173,
      proxy: {
        // Refresh 토큰 쿠키(HttpOnly, SameSite=Strict)는 localhost 쿠키로 저장된다.
        '/api': { target, changeOrigin: true, secure: true, cookieDomainRewrite: '', configure: dropOrigin },
        '/ws': { target, changeOrigin: true, secure: true, ws: true, configure: dropOrigin },
      },
    },
  };
});
