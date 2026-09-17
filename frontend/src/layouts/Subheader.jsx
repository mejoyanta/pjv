import React from 'react';
import { Link } from 'react-router-dom';

export default function Subheader({ title, breadcrumbs = [], actions = null }) {
  return (
    <div className="kt-subheader">
      <div style={{ display: 'flex', alignItems: 'center' }}>
        <h3 className="kt-subheader__title">{title}</h3>
        {breadcrumbs.length > 0 && (
          <div className="kt-subheader__breadcrumbs">
            <Link to="/" style={{ color: '#959cb6' }}>Home</Link>
            {breadcrumbs.map((b, i) => (
              <React.Fragment key={i}>
                <i className="bi bi-chevron-right"></i>
                {b.link ? (
                  <Link to={b.link} style={{ color: '#959cb6' }}>{b.label}</Link>
                ) : (
                  <span style={{ color: '#48465b', fontWeight: 500 }}>{b.label}</span>
                )}
              </React.Fragment>
            ))}
          </div>
        )}
      </div>

      {actions && <div>{actions}</div>}
    </div>
  );
}
