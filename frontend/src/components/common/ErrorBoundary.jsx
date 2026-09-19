import React from 'react';

export default class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null, errorInfo: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    this.setState({ errorInfo });
    const pageUrl = window.location.href;
    console.error(`[React Component Crash on Page: ${pageUrl}]`, error, errorInfo);

    // Also attempt to log client error to backend
    try {
      fetch('http://localhost:8081/api/v1/debug/log-client-error', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          pageUrl,
          errorMsg: error?.message || 'Component Crash',
          componentStack: errorInfo?.componentStack
        })
      }).catch(() => {});
    } catch (e) {}
  }

  render() {
    if (this.state.hasError) {
      const pageUrl = window.location.href;
      return (
        <div style={{
          margin: '30px auto',
          maxWidth: '900px',
          padding: '30px',
          backgroundColor: '#fff',
          borderRadius: '8px',
          boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
          borderTop: '5px solid #dc3545'
        }}>
          <h3 style={{ color: '#dc3545', display: 'flex', alignItems: 'center', gap: '10px', marginTop: 0 }}>
            <span style={{ fontSize: '24px' }}>⚠️</span>
            Error on Page
          </h3>

          <div style={{
            margin: '15px 0',
            padding: '12px 16px',
            backgroundColor: '#f8f9fa',
            border: '1px solid #e9ecef',
            borderRadius: '4px'
          }}>
            <strong style={{ color: '#495057' }}>Page URL: </strong>
            <code style={{ color: '#d63384', fontSize: '13px', fontWeight: 'bold' }}>{pageUrl}</code>
          </div>

          <div style={{
            margin: '15px 0',
            padding: '12px 16px',
            backgroundColor: '#fff5f8',
            border: '1px solid #f8d7da',
            borderRadius: '4px',
            color: '#b02a37'
          }}>
            <strong>Error Message: </strong>
            <span>{this.state.error?.message || 'Unknown render error occurred.'}</span>
          </div>

          {this.state.errorInfo && (
            <details style={{ marginTop: '15px' }}>
              <summary style={{ cursor: 'pointer', color: '#0d6efd', fontWeight: '500' }}>
                View Component Stack Trace
              </summary>
              <pre style={{
                marginTop: '10px',
                padding: '15px',
                backgroundColor: '#212529',
                color: '#f8f9fa',
                borderRadius: '4px',
                fontSize: '12px',
                overflowX: 'auto',
                whiteSpace: 'pre-wrap'
              }}>
                {this.state.errorInfo.componentStack}
              </pre>
            </details>
          )}

          <div style={{ marginTop: '25px', display: 'flex', gap: '12px' }}>
            <button
              onClick={() => window.location.reload()}
              style={{
                backgroundColor: '#0d6efd',
                color: '#fff',
                border: 'none',
                padding: '8px 18px',
                borderRadius: '4px',
                cursor: 'pointer',
                fontWeight: '500'
              }}
            >
              Reload Page
            </button>
            <button
              onClick={() => { window.location.href = '/dashboard/overview'; }}
              style={{
                backgroundColor: '#6c757d',
                color: '#fff',
                border: 'none',
                padding: '8px 18px',
                borderRadius: '4px',
                cursor: 'pointer',
                fontWeight: '500'
              }}
            >
              Back to Dashboard
            </button>
            <a
              href="http://localhost:8081/api/v1/debug/error-log?lines=200"
              target="_blank"
              rel="noreferrer"
              style={{
                backgroundColor: '#dc3545',
                color: '#fff',
                border: 'none',
                padding: '8px 18px',
                borderRadius: '4px',
                cursor: 'pointer',
                fontWeight: '500',
                textDecoration: 'none',
                display: 'inline-block'
              }}
            >
              View Server Error Log
            </a>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
