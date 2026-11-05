import React from 'react';
import { createRoot } from 'react-dom/client';
import './styles/fonts.css';
import './styles/tokens.css';
import './styles/base.css';
import './styles/header.css';
import './styles/hero.css';
import './styles/flow.css';
import './styles/sections.css';
import './styles/sheet.css';
import './styles/app.css';
import { App } from './App.tsx';
import { SessionProvider } from './auth/session';
import { ToastProvider } from './components/toast';

createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ToastProvider>
      <SessionProvider>
        <App />
      </SessionProvider>
    </ToastProvider>
  </React.StrictMode>,
);
