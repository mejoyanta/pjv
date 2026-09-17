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
        <NavLink to="/dashboard/overview" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-grid-fill kt-menu__link-icon"></i>
          <span style={{ fontWeight: 600 }}>DASHBOARD</span>
        </NavLink>

        <div className="kt-menu__section" style={{ marginTop: 15 }}>Company Information</div>

        <NavLink to="/company" end className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-buildings kt-menu__link-icon"></i>
          <span>Company</span>
        </NavLink>

        <NavLink to="/document-register" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-file-earmark-text kt-menu__link-icon"></i>
          <span>Document Register</span>
        </NavLink>

        <NavLink to="/company-report-summary" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-bar-chart-line kt-menu__link-icon"></i>
          <span>Company Report Summary</span>
        </NavLink>

        <NavLink to="/dvc-files" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-folder-check kt-menu__link-icon"></i>
          <span>DVC Files</span>
        </NavLink>

        <NavLink to="/audit-report" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-journal-check kt-menu__link-icon"></i>
          <span>Audit Report (Salf)</span>
        </NavLink>

        <NavLink to="/analyze-report" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-graph-up-arrow kt-menu__link-icon"></i>
          <span>Analyze Report</span>
        </NavLink>

        <NavLink to="/legal-management" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-briefcase kt-menu__link-icon"></i>
          <span>Legal Management</span>
        </NavLink>

        <NavLink to="/task-management" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-check2-square kt-menu__link-icon"></i>
          <span>Task Management</span>
        </NavLink>

        <NavLink to="/company-noc" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-patch-check kt-menu__link-icon"></i>
          <span>Company NOC</span>
        </NavLink>

        <div className="kt-menu__section" style={{ marginTop: 15 }}>Basic Configuration</div>

        <NavLink to="/currency" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-cash-coin kt-menu__link-icon"></i>
          <span>Currency</span>
        </NavLink>

        <NavLink to="/unit" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-boxes kt-menu__link-icon"></i>
          <span>Unit</span>
        </NavLink>

        <NavLink to="/port" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-geo-alt kt-menu__link-icon"></i>
          <span>Port</span>
        </NavLink>

        <NavLink to="/cpc-item-no" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-upc-scan kt-menu__link-icon"></i>
          <span>CPC & Item No</span>
        </NavLink>

        <NavLink to="/designation-department" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-person-badge kt-menu__link-icon"></i>
          <span>Designation & Dept</span>
        </NavLink>

        <NavLink to="/price-additional" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-tag kt-menu__link-icon"></i>
          <span>Additional Price Area</span>
        </NavLink>

        <NavLink to="/material" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-box-seam kt-menu__link-icon"></i>
          <span>Material</span>
        </NavLink>

        <NavLink to="/product" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-cart-check kt-menu__link-icon"></i>
          <span>Product</span>
        </NavLink>

        <NavLink to="/barcode" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-upc kt-menu__link-icon"></i>
          <span>Barcode</span>
        </NavLink>

        <NavLink to="/supplier" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-truck kt-menu__link-icon"></i>
          <span>Supplier</span>
        </NavLink>

        <NavLink to="/priority-supplier" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-star kt-menu__link-icon"></i>
          <span>Priority Supplier</span>
        </NavLink>

        <NavLink to="/customer" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-people-fill kt-menu__link-icon"></i>
          <span>Customer</span>
        </NavLink>

        <NavLink to="/priority-customer" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-award kt-menu__link-icon"></i>
          <span>Priority Customer</span>
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
          href={`${(import.meta.env.VITE_API_URL || 'http://localhost:8081/api/v1').replace(/\/api\/v1\/?$/, '')}/swagger-ui.html`}
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
