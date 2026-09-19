import React, { useState } from 'react';
import { NavLink, useLocation } from 'react-router-dom';

export default function Sidebar() {
  const location = useLocation();
  const [isPurchaseOpen, setIsPurchaseOpen] = useState(
    location.pathname.startsWith('/purchase') || true
  );
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

        <div className="kt-menu__section" style={{ marginTop: 15 }}>Stock Management</div>

        {/* Product Purchase Submenu Group */}
        <div className="kt-menu__item-group">
          <div
            className={`kt-menu__item kt-menu__item--submenu ${location.pathname.startsWith('/purchase') ? 'active' : ''}`}
            onClick={() => setIsPurchaseOpen(prev => !prev)}
            style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              <i className="bi bi-box2-fill kt-menu__link-icon"></i>
              <span style={{ fontWeight: 500 }}>Product Purchase</span>
            </div>
            <i className={`bi bi-chevron-${isPurchaseOpen ? 'down' : 'right'}`} style={{ fontSize: 11 }}></i>
          </div>

          {isPurchaseOpen && (
            <div className="kt-menu__subnav" style={{ paddingLeft: 28, display: 'flex', flexDirection: 'column', gap: 2 }}>
              <NavLink to="/purchase/create/foreign" className={({ isActive }) => `kt-menu__item py-1 ${isActive ? 'active' : ''}`} style={{ fontSize: 13 }}>
                <i className="bi bi-dot"></i>
                <span>Import Purchase</span>
              </NavLink>
              <NavLink to="/purchase/create/local" className={({ isActive }) => `kt-menu__item py-1 ${isActive ? 'active' : ''}`} style={{ fontSize: 13 }}>
                <i className="bi bi-dot"></i>
                <span>Local Purchase</span>
              </NavLink>
              <NavLink to="/purchase" end className={({ isActive }) => `kt-menu__item py-1 ${isActive ? 'active' : ''}`} style={{ fontSize: 13 }}>
                <i className="bi bi-dot"></i>
                <span>List</span>
              </NavLink>
            </div>
          )}
        </div>

        <div className="kt-menu__section" style={{ marginTop: 15 }}>Payment Information</div>

        <NavLink to="/payments" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-wallet2 kt-menu__link-icon"></i>
          <span>Payments</span>
        </NavLink>

        <NavLink to="/credit-invoice" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-receipt-cutoff kt-menu__link-icon"></i>
          <span>Credit Invoice</span>
        </NavLink>

        <NavLink to="/debit-invoice" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-receipt kt-menu__link-icon"></i>
          <span>Debit Invoice</span>
        </NavLink>

        <NavLink to="/bank-treasury" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-bank2 kt-menu__link-icon"></i>
          <span>Bank Treasury</span>
        </NavLink>

        <NavLink to="/bank-transactions" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-cash-stack kt-menu__link-icon"></i>
          <span>Bank Transactions</span>
        </NavLink>

        <NavLink to="/bank-info-details" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-credit-card-2-front kt-menu__link-icon"></i>
          <span>Bank Account Info</span>
        </NavLink>

        <NavLink to="/bank-branch" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-bank kt-menu__link-icon"></i>
          <span>Bank & Branch</span>
        </NavLink>

        <NavLink to="/mobile-banking" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-phone kt-menu__link-icon"></i>
          <span>Mobile Banking</span>
        </NavLink>

        <NavLink to="/rent-vat" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-graph-up kt-menu__link-icon"></i>
          <span>Any Other Increasing Adjustments</span>
        </NavLink>

        <NavLink to="/adjustment-decrease" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-graph-down kt-menu__link-icon"></i>
          <span>Any Other Decreasing Adjustments</span>
        </NavLink>

        <NavLink to="/packages" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-box2-heart kt-menu__link-icon"></i>
          <span>Packages</span>
        </NavLink>

        <NavLink to="/subscription" className={({ isActive }) => `kt-menu__item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-calendar-check kt-menu__link-icon"></i>
          <span>Subscription</span>
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
