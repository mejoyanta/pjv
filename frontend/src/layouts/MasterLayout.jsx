import React from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Header from './Header';

export default function MasterLayout() {
  return (
    <div className="kt-grid">
      <Sidebar />
      <div className="kt-wrapper">
        <Header />
        <main className="kt-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
