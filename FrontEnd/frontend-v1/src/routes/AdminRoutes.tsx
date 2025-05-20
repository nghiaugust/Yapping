// src/routes/AdminRoutes.tsx
import { lazy } from 'react';
import { Route, Routes } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';

const LayoutAdmin = lazy(() => import('../layouts/LayoutAdmin'));
const AccountManagement = lazy(() => import('../pages/admin/AccountManagement'));

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
        <Route path="accounts" element={<AccountManagement />} />
      </Route>
    </Routes>
  );
}