import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export default function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();

  const [username, setUsername] = useState('joyanta319@gmail.com');
  const [password, setPassword] = useState('Bk123456789#');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await login(username, password);
      const from = location.state?.from?.pathname || '/company';
      navigate(from, { replace: true });
    } catch (err) {
      setError(err.message || 'Login failed. Please check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: '#f2f3f8',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: 20
    }}>
      <div style={{
        maxWidth: 440,
        width: '100%',
        backgroundColor: '#ffffff',
        borderRadius: 8,
        boxShadow: '0 10px 30px rgba(0,0,0,0.06)',
        padding: '40px 35px'
      }}>
        {/* Brand Header */}
        <div style={{ textAlign: 'center', marginBottom: 30 }}>
          <div style={{
            display: 'inline-flex',
            alignItems: 'center',
            justifyContent: 'center',
            width: 60,
            height: 60,
            borderRadius: '50%',
            backgroundColor: 'rgba(93, 120, 255, 0.1)',
            color: 'var(--kt-brand)',
            fontSize: 28,
            marginBottom: 12
          }}>
            <i className="bi bi-shield-check"></i>
          </div>
          <h2 style={{ fontSize: 22, fontWeight: 700, color: '#383748', marginBottom: 6 }}>
            VAT TAX SOLUTION
          </h2>
          <p style={{ fontSize: 13, color: '#959cb6' }}>Sign in to your administration dashboard</p>
        </div>

        {error && (
          <div style={{
            padding: '12px 16px',
            backgroundColor: 'rgba(253, 57, 122, 0.1)',
            borderLeft: '4px solid var(--kt-danger)',
            color: '#c9164e',
            borderRadius: 4,
            marginBottom: 20,
            fontSize: 13,
            fontWeight: 500
          }}>
            <i className="bi bi-exclamation-circle-fill" style={{ marginRight: 8 }}></i>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Username or Email</label>
            <div style={{ position: 'relative' }}>
              <input
                type="text"
                className="form-control"
                style={{ paddingLeft: 38, height: 42 }}
                required
                placeholder="Enter username or email"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
              <i className="bi bi-person" style={{
                position: 'absolute', left: 14, top: 12, color: '#a2a3b7', fontSize: 16
              }}></i>
            </div>
          </div>

          <div className="form-group" style={{ marginBottom: 25 }}>
            <label className="form-label">Password</label>
            <div style={{ position: 'relative' }}>
              <input
                type="password"
                className="form-control"
                style={{ paddingLeft: 38, height: 42 }}
                required
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
              <i className="bi bi-lock" style={{
                position: 'absolute', left: 14, top: 12, color: '#a2a3b7', fontSize: 16
              }}></i>
            </div>
          </div>

          <button
            type="submit"
            className="btn btn-brand"
            style={{ width: '100%', height: 44, fontSize: 14, fontWeight: 600 }}
            disabled={loading}
          >
            {loading ? (
              <span><i className="fa fa-spinner fa-spin" style={{ marginRight: 8 }}></i> Signing In...</span>
            ) : (
              <span>Sign In</span>
            )}
          </button>
        </form>

        <div style={{ textAlign: 'center', marginTop: 25, fontSize: 12, color: '#959cb6' }}>
          BARA BD VAT & Tax Solution &bull; Enterprise 2026
        </div>
      </div>
    </div>
  );
}
