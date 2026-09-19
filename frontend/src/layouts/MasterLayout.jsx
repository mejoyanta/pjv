import React from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Header from './Header';
import PageErrorBanner from '../components/common/PageErrorBanner';
import ErrorBoundary from '../components/common/ErrorBoundary';

export default function MasterLayout() {
  return (
    <div className="kt-grid">
      <Sidebar />
      <div className="kt-wrapper">
        <Header />
        <main className="kt-content" style={{ position: 'relative' }}>
          <PageErrorBanner />
          <ErrorBoundary>
            <Outlet />
          </ErrorBoundary>
        </main>
      </div>
    </div>
  );
}
