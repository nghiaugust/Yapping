// src/routes/UserRoutes.tsx
import { lazy } from 'react';
import { Route, Routes } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';

const LayoutUser = lazy(() => import('../layouts/LayoutUser'));
const Home = lazy(() => import('../pages/user/Home'));
const UserProfile = lazy(() => import('../pages/user/UserProfile'));
const CreatePostUnified = lazy(() => import('../pages/user/CreatePostUnified'));
const Resources = lazy(() => import('../pages/user/Resources'));
const Search = lazy(() => import('../pages/user/Search'));
const Bookmarks = lazy(() => import('../pages/user/Bookmarks'));

export default function UserRoutes() {
  return (
    <Routes>
      <Route
        element={
          <ProtectedRoute allowedRole="USER">
            <LayoutUser />
          </ProtectedRoute>
        }
      >        
        <Route index element={<Home />} />
        <Route path="profile" element={<UserProfile />} />        
        <Route path="create-post" element={<CreatePostUnified />} />
        <Route path="resources" element={<Resources />} />
        <Route path="search" element={<Search />} />
        <Route path="bookmarks" element={<Bookmarks />} />
      </Route>
    </Routes>
  );
}