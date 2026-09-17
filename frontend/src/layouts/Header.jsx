import React from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function Header() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const displayName = user?.name || 'Administrator';
  const roleName = user?.groupName || 'Super Admin';
  const initials = displayName.substring(0, 2).toUpperCase();

  return (
    <header className="kt-header">
      <div style={{ display: 'flex', alignItems: 'center', gap: 15 }}>
        <span style={{ fontSize: 13, fontWeight: 500, color: '#74788d' }}>
          VAT & Tax Management System
        </span>
      </div>

      <div className="kt-header__topbar">
        <a
          href={`${(import.meta.env.VITE_API_URL || 'http://localhost:8081/api/v1').replace(/\/api\/v1\/?$/, '')}/swagger-ui.html`}
          target="_blank"
          rel="noopener noreferrer"
          className="btn btn-label-brand btn-sm"
          style={{ textDecoration: 'none' }}
        >
          <i className="bi bi-file-earmark-code"></i>
          <span>Swagger Docs</span>
        </a>

        <div className="kt-header__user">
          <div className="kt-badge-avatar">{initials}</div>
          <div style={{ display: 'flex', flexDirection: 'column', marginRight: 10 }}>
            <span className="kt-header__user-name" style={{ fontSize: 13, fontWeight: 600 }}>{displayName}</span>
            <span style={{ fontSize: 11, color: '#a2a3b7' }}>{roleName}</span>
          </div>

          <button
            className="btn btn-label-danger btn-sm"
            title="Sign Out"
            onClick={handleLogout}
            style={{ padding: '5px 10px' }}
          >
            <i className="bi bi-box-arrow-right"></i>
            <span>Logout</span>
          </button>
        </div>
      </div>
    </header>
  );
}
