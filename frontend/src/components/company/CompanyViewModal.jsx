import React from 'react';

export default function CompanyViewModal({ isOpen, company, onClose }) {
  if (!isOpen || !company) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content" style={{ maxWidth: 800 }}>
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            <div className="kt-badge-avatar" style={{ width: 44, height: 44, fontSize: 16 }}>
              {company.name ? company.name.substring(0, 2).toUpperCase() : 'CO'}
            </div>
            <div>
              <h4 className="modal-title">{company.name}</h4>
              <span style={{ fontSize: 12, color: '#959cb6' }}>ID: {company.username} | BIN: {company.bin || 'N/A'}</span>
            </div>
          </div>
          <button
            onClick={onClose}
            style={{ background: 'none', border: 'none', fontSize: 22, cursor: 'pointer', color: '#959cb6' }}
          >
            &times;
          </button>
        </div>

        <div className="modal-body" style={{ padding: '20px 25px' }}>
          <div className="row">
            {/* General Info */}
            <div className="col-12" style={{ marginBottom: 15 }}>
              <h5 style={{ fontSize: 13, fontWeight: 700, color: 'var(--kt-brand)', borderBottom: '1px solid #ebedf2', paddingBottom: 6 }}>
                General Information
              </h5>
            </div>
            <div className="col-md-4 form-group">
              <label className="form-label">Category</label>
              <div>{company.categoryName || 'N/A'}</div>
            </div>
            <div className="col-md-4 form-group">
              <label className="form-label">Email</label>
              <div>{company.email || 'N/A'}</div>
            </div>
            <div className="col-md-4 form-group">
              <label className="form-label">Phone</label>
              <div>{company.phone || 'N/A'}</div>
            </div>
            <div className="col-md-4 form-group">
              <label className="form-label">Status</label>
              <div>
                <span className={`badge ${company.status === 'ACTIVE' || company.status === 'active' ? 'badge-success' : 'badge-danger'}`}>
                  {company.status}
                </span>
              </div>
            </div>
            <div className="col-md-4 form-group">
              <label className="form-label">Subscription Expiry</label>
              <div>{company.subscriptionExpireDate || 'N/A'}</div>
            </div>
            <div className="col-md-4 form-group">
              <label className="form-label">Company Level</label>
              <div>{company.companyLevel || 'Small'}</div>
            </div>

            {/* Owner Info */}
            <div className="col-12" style={{ margin: '15px 0 10px' }}>
              <h5 style={{ fontSize: 13, fontWeight: 700, color: 'var(--kt-brand)', borderBottom: '1px solid #ebedf2', paddingBottom: 6 }}>
                Owner & Contact Details
              </h5>
            </div>
            <div className="col-md-4 form-group">
              <label className="form-label">Owner Name</label>
              <div>{company.ownerName || 'N/A'}</div>
            </div>
            <div className="col-md-4 form-group">
              <label className="form-label">Owner Phone</label>
              <div>{company.ownerPhone || 'N/A'}</div>
            </div>
            <div className="col-md-4 form-group">
              <label className="form-label">Ownership Type</label>
              <div>{company.ownershipType || 'N/A'}</div>
            </div>
            <div className="col-md-4 form-group">
              <label className="form-label">Contact Person</label>
              <div>{company.contactPersonName || 'N/A'}</div>
            </div>
            <div className="col-md-4 form-group">
              <label className="form-label">Contact Phone</label>
              <div>{company.contactPersonPhone || 'N/A'}</div>
            </div>
            <div className="col-md-4 form-group">
              <label className="form-label">Designation</label>
              <div>{company.contactPersonDesignation || 'N/A'}</div>
            </div>

            {/* Addresses */}
            <div className="col-12" style={{ margin: '15px 0 10px' }}>
              <h5 style={{ fontSize: 13, fontWeight: 700, color: 'var(--kt-brand)', borderBottom: '1px solid #ebedf2', paddingBottom: 6 }}>
                Address Information
              </h5>
            </div>
            <div className="col-md-6 form-group">
              <label className="form-label">Company Address</label>
              <div>{company.address || 'N/A'}</div>
            </div>
            <div className="col-md-6 form-group">
              <label className="form-label">VAT Office Address</label>
              <div>{company.vatOfficeAddress || 'N/A'}</div>
            </div>
            <div className="col-md-6 form-group">
              <label className="form-label">Division Address</label>
              <div>{company.divisionAddress || 'N/A'}</div>
            </div>
            <div className="col-md-6 form-group">
              <label className="form-label">Circle Address</label>
              <div>{company.circleAddress || 'N/A'}</div>
            </div>

            {/* Charges */}
            <div className="col-12" style={{ margin: '15px 0 10px' }}>
              <h5 style={{ fontSize: 13, fontWeight: 700, color: 'var(--kt-brand)', borderBottom: '1px solid #ebedf2', paddingBottom: 6 }}>
                Financial Charges
              </h5>
            </div>
            <div className="col-md-4 form-group">
              <label className="form-label">Service Charge</label>
              <div>{company.serviceCharge != null ? `৳ ${company.serviceCharge}` : 'N/A'}</div>
            </div>
            <div className="col-md-4 form-group">
              <label className="form-label">Rent VAT Charge</label>
              <div>{company.rentVatCharge != null ? `৳ ${company.rentVatCharge}` : 'N/A'}</div>
            </div>
            <div className="col-md-4 form-group">
              <label className="form-label">Consultancy Charge</label>
              <div>{company.consultancyCharge != null ? `৳ ${company.consultancyCharge}` : 'N/A'}</div>
            </div>
          </div>
        </div>

        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose}>
            Close
          </button>
        </div>
      </div>
    </div>
  );
}
