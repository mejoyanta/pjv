import React from 'react';

export default function GroupViewModal({ isOpen, group, onClose }) {
  if (!isOpen || !group) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content" style={{ maxWidth: 550 }}>
        <div className="modal-header">
          <h4 className="modal-title">Group Details: {group.name}</h4>
          <button
            onClick={onClose}
            style={{ background: 'none', border: 'none', fontSize: 22, cursor: 'pointer', color: '#959cb6' }}
          >
            &times;
          </button>
        </div>

        <div className="modal-body">
          <div className="form-group">
            <label className="form-label">Group Name</label>
            <div style={{ fontWeight: 600 }}>{group.name}</div>
          </div>

          <div className="form-group">
            <label className="form-label">Hierarchy Structure</label>
            <div style={{ fontSize: 13, color: '#595d6e' }}>
              Mother Admin: <strong>{group.motherAdmin}</strong> &rarr; Admin: <strong>{group.admin}</strong>
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Assigned Users Count</label>
            <div><span className="badge badge-info">{group.usersCount || 0} Users</span></div>
          </div>

          <div className="form-group">
            <label className="form-label">Description</label>
            <div>{group.description || 'No description provided'}</div>
          </div>
        </div>

        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose}>Close</button>
        </div>
      </div>
    </div>
  );
}
