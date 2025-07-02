// src/routes/AdminRoutes.tsx
import { lazy } from 'react';
import { Route, Routes, Navigate } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';

const LayoutAdmin = lazy(() => import('../layouts/LayoutAdmin'));
const AccountManagement = lazy(() => import('../pages/admin/AccountManagement'));
const Reports = lazy(() => import('../pages/admin/Reports'));
const PostManagement = lazy(() => import('../pages/admin/PostManagement'));

export default function AdminRoutes() {
  return (
    <Routes>
      <Route
        element={
          <ProtectedRoute allowedRole="ADMIN">
            <LayoutAdmin />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="accounts" replace />} />
        <Route path="accounts" element={<AccountManagement />} />
        <Route path="reports" element={<Reports />} />
        <Route path="posts" element={<PostManagement />} />
      </Route>
    </Routes>
  );
}