import React from 'react';

export default function Alert({ type = 'success', message, onClose }) {
  if (!message) return null;

  const isSuccess = type === 'success';
  const bgColor = isSuccess ? 'rgba(10, 187, 135, 0.1)' : 'rgba(253, 57, 122, 0.1)';
  const borderColor = isSuccess ? 'var(--kt-success)' : 'var(--kt-danger)';
  const textColor = isSuccess ? '#0a805c' : '#c9164e';
  const icon = isSuccess ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill';

  return (
    <div style={{
      padding: '12px 18px',
      borderRadius: 4,
      backgroundColor: bgColor,
      borderLeft: `4px solid ${borderColor}`,
      color: textColor,
      marginBottom: 20,
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      fontSize: 13,
      fontWeight: 500
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
        <i className={`bi ${icon}`} style={{ fontSize: 16 }}></i>
        <span>{message}</span>
      </div>
      {onClose && (
        <button
          onClick={onClose}
          style={{ background: 'none', border: 'none', cursor: 'pointer', color: textColor, fontSize: 16 }}
        >
          &times;
        </button>
      )}
    </div>
  );
}
