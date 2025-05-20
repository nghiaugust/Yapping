// src/routes/ProtectedRoute.tsx
import { Navigate, Outlet } from 'react-router-dom';
import { ReactNode } from 'react';

interface ProtectedRouteProps {
  allowedRole: 'USER' | 'ADMIN';
  children?: ReactNode;
}

export default function ProtectedRoute({ allowedRole, children }: ProtectedRouteProps) {
  const token = localStorage.getItem('access_token');
  const userRole = localStorage.getItem('user_role');

  // Nếu không có token, chuyển hướng đến trang login
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  // Nếu vai trò không khớp với allowedRole, chuyển hướng đến trang chủ hoặc login
  if (userRole !== allowedRole) {
    return <Navigate to="/" replace />;
  }

  // Nếu có children, render nó; nếu không, render Outlet cho các tuyến đường lồng nhau
  return children ? <>{children}</> : <Outlet />;
}