import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import MasterLayout from './layouts/MasterLayout';

// Auth Page
import LoginPage from './pages/auth/LoginPage';

// Company Pages
import CompanyListPage from './pages/company/CompanyListPage';
import CompanyArchivePage from './pages/company/CompanyArchivePage';
import CompanyCreatePage from './pages/company/CompanyCreatePage';
import CompanyEditPage from './pages/company/CompanyEditPage';
import CompanyDocumentsPage from './pages/company/CompanyDocumentsPage';

// User Pages
import UserListPage from './pages/user/UserListPage';
import UserArchivePage from './pages/user/UserArchivePage';
import UserCreatePage from './pages/user/UserCreatePage';
import UserEditPage from './pages/user/UserEditPage';

// Group Pages
import GroupListPage from './pages/group/GroupListPage';
import GroupArchivePage from './pages/group/GroupArchivePage';
import GroupCreatePage from './pages/group/GroupCreatePage';
import GroupEditPage from './pages/group/GroupEditPage';
import GroupAccessPage from './pages/group/GroupAccessPage';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public Login Route */}
          <Route path="/login" element={<LoginPage />} />

          {/* Protected Dashboard Layout */}
          <Route element={<MasterLayout />}>
            <Route path="/" element={<Navigate to="/company" replace />} />

            {/* Company Routes */}
            <Route path="/company" element={<CompanyListPage />} />
            <Route path="/company/archive" element={<CompanyArchivePage />} />
            <Route path="/company/create" element={<CompanyCreatePage />} />
            <Route path="/company/:slug" element={<CompanyEditPage />} />
            <Route path="/company/:slug/documents" element={<CompanyDocumentsPage />} />

            {/* User Routes */}
            <Route path="/users" element={<UserListPage />} />
            <Route path="/users/archive" element={<UserArchivePage />} />
            <Route path="/users/create" element={<UserCreatePage />} />
            <Route path="/users/:id" element={<UserEditPage />} />

            {/* Group Routes */}
            <Route path="/groups" element={<GroupListPage />} />
            <Route path="/groups/archive" element={<GroupArchivePage />} />
            <Route path="/groups/create" element={<GroupCreatePage />} />
            <Route path="/groups/:id" element={<GroupEditPage />} />
            <Route path="/groups/:slug/access" element={<GroupAccessPage />} />

            {/* Fallback */}
            <Route path="*" element={<Navigate to="/company" replace />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
