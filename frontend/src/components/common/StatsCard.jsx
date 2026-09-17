import React from 'react';

export default function StatsCard({ icon, label, value, color = 'var(--kt-brand)', bg = 'rgba(93, 120, 255, 0.1)' }) {
  return (
    <div className="stat-card">
      <div className="stat-icon" style={{ backgroundColor: bg, color: color }}>
        <i className={icon}></i>
      </div>
      <div className="stat-info">
        <h3>{typeof value === 'number' ? value.toLocaleString() : value}</h3>
        <p>{label}</p>
      </div>
    </div>
  );
}
