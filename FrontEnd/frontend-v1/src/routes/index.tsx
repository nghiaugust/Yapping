// src/routes/index.tsx
import { lazy, Suspense } from 'react';
import { Routes, Route } from 'react-router-dom';
import UserRoutes from './UserRoutes';
import AdminRoutes from './AdminRoutes';

const Login = lazy(() => import('../layouts/Login'));
const PageNotFound = lazy(() => import('../layouts/PageNotFound'));

export default function AppRoutes() {
  return (
    <Suspense fallback={<div style={{ textAlign: 'center', padding: '50px' }}>Đang tải...</div>}>
      <Routes>
        {/* Routes không yêu cầu token */}
        <Route path="/login" element={<Login />} />
        
        {/* Routes cho user */}
        <Route path="/*" element={<UserRoutes />} />
        
        {/* Routes cho admin */}
        <Route path="/admin/*" element={<AdminRoutes />} />
        
        {/* Route cho trang không tìm thấy */}
        <Route path="*" element={<PageNotFound />} />
      </Routes>
    </Suspense>
  );
}