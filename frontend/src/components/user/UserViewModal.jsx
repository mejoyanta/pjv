import React from 'react';

export default function UserViewModal({ isOpen, user, onClose }) {
  if (!isOpen || !user) return null;

  const initials = user.name ? user.name.substring(0, 2).toUpperCase() : 'U';

  return (
    <div className="modal-overlay">
      <div className="modal-content" style={{ maxWidth: 650 }}>
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            <div className="kt-badge-avatar" style={{ width: 44, height: 44, fontSize: 16 }}>
              {initials}
            </div>
            <div>
              <h4 className="modal-title">{user.name}</h4>
              <span style={{ fontSize: 12, color: '#959cb6' }}>Username: {user.username} | Email: {user.email}</span>
            </div>
          </div>
          <button
            onClick={onClose}
            style={{ background: 'none', border: 'none', fontSize: 22, cursor: 'pointer', color: '#959cb6' }}
          >
            &times;
          </button>
        </div>

        <div className="modal-body">
          <div className="row">
            <div className="col-md-6 form-group">
              <label className="form-label">Company</label>
              <div>{user.companyName || 'N/A'}</div>
            </div>
            <div className="col-md-6 form-group">
              <label className="form-label">Company Branch</label>
              <div>{user.companyBranchName || 'Main Branch'}</div>
            </div>
            <div className="col-md-6 form-group">
              <label className="form-label">Group / Role</label>
              <div><span className="badge badge-info">{user.groupName || 'User'}</span></div>
            </div>
            <div className="col-md-6 form-group">
              <label className="form-label">Status</label>
              <div>
                <span className={`badge ${user.status === 'ACTIVE' || user.status === 'active' ? 'badge-success' : 'badge-danger'}`}>
                  {user.status}
                </span>
                {user.isOnline && (
                  <span className="badge badge-success" style={{ marginLeft: 6 }}>Online</span>
                )}
              </div>
            </div>
            <div className="col-md-6 form-group">
              <label className="form-label">Designation</label>
              <div>{user.designationName || 'N/A'}</div>
            </div>
            <div className="col-md-6 form-group">
              <label className="form-label">Department</label>
              <div>{user.departmentName || 'N/A'}</div>
            </div>
            <div className="col-md-6 form-group">
              <label className="form-label">Contact Number</label>
              <div>{user.contact || 'N/A'}</div>
            </div>
            <div className="col-md-6 form-group">
              <label className="form-label">NID</label>
              <div>{user.nid || 'N/A'}</div>
            </div>
          </div>
        </div>

        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose}>Close</button>
        </div>
      </div>
    </div>
  );
}
