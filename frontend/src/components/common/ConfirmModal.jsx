import React from 'react';

export default function ConfirmModal({
  isOpen,
  title = 'Confirm Action',
  message = 'Are you sure you want to proceed?',
  confirmText = 'Confirm',
  cancelText = 'Cancel',
  confirmBtnClass = 'btn-danger',
  onConfirm,
  onCancel
}) {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content" style={{ maxWidth: 450 }}>
        <div className="modal-header">
          <h4 className="modal-title">{title}</h4>
          <button
            onClick={onCancel}
            style={{ background: 'none', border: 'none', fontSize: 20, cursor: 'pointer', color: '#959cb6' }}
          >
            &times;
          </button>
        </div>
        <div className="modal-body">
          <p style={{ fontSize: 13.5, color: '#595d6e', margin: 0 }}>{message}</p>
        </div>
        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onCancel}>
            {cancelText}
          </button>
          <button className={`btn ${confirmBtnClass}`} onClick={onConfirm}>
            {confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}
