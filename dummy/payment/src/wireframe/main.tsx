// 와이어프레임 진입점
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { Wireframe } from './Wireframe';
import './wireframe.css';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Wireframe />
  </StrictMode>
);
