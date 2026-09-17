import React from 'react';
import { NavLink } from 'react-router-dom';

export default function Sidebar() {
  return (
    <aside className="kt-aside">
      <div className="kt-aside__brand">
        <div className="kt-aside__brand-logo">
          <i className="bi bi-shield-check" style={{ color: 'var(--kt-brand)', fontSize: 24 }}></i>
          <span>VAT SOLUTION</span>
        </div>
      </div>

      <div className="kt-aside-menu">
        <div className="kt-menu__section">Company Information</div>

        <NavLink to="/company" end className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-buildings kt-menu__link-icon"></i>
          <span>Company List</span>
        </NavLink>

        <NavLink to="/company/create" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-building-add kt-menu__link-icon"></i>
          <span>Create Company</span>
        </NavLink>

        <NavLink to="/company/archive" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-archive kt-menu__link-icon"></i>
          <span>Company Archive</span>
        </NavLink>

        <div className="kt-menu__section" style={{ marginTop: 15 }}>User Management</div>

        <NavLink to="/users" end className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-people kt-menu__link-icon"></i>
          <span>Users List</span>
        </NavLink>

        <NavLink to="/users/create" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-person-plus kt-menu__link-icon"></i>
          <span>Create User</span>
        </NavLink>

        <NavLink to="/users/archive" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-person-x kt-menu__link-icon"></i>
          <span>User Archive</span>
        </NavLink>

        <div className="kt-menu__section" style={{ marginTop: 15 }}>Security & Access</div>

        <NavLink to="/groups" end className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-diagram-3 kt-menu__link-icon"></i>
          <span>Groups List</span>
        </NavLink>

        <NavLink to="/groups/create" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-plus-square kt-menu__link-icon"></i>
          <span>Create Group</span>
        </NavLink>

        <NavLink to="/groups/archive" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-folder-x kt-menu__link-icon"></i>
          <span>Group Archive</span>
        </NavLink>

        <div className="kt-menu__section" style={{ marginTop: 15 }}>API Developer Tools</div>

        <a
          href="http://localhost:8080/swagger-ui.html"
          target="_blank"
          rel="noopener noreferrer"
          className="kt-menu__item"
        >
          <i className="bi bi-code-slash kt-menu__link-icon"></i>
          <span>Swagger REST API</span>
          <i className="bi bi-box-arrow-up-right" style={{ marginLeft: 'auto', fontSize: 11, opacity: 0.6 }}></i>
        </a>
      </div>
    </aside>
  );
}
