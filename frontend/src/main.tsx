import { createRoot } from 'react-dom/client';
import { App } from './App';
import { SessionProvider } from './auth/session';
import { ToastProvider } from './components/toast';
import './styles.css';

createRoot(document.getElementById('root')!).render(
  <ToastProvider>
    <SessionProvider>
      <App />
    </SessionProvider>
  </ToastProvider>
);
