import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';

export default function PageErrorBanner() {
  const location = useLocation();
  const [currentError, setCurrentError] = useState(null);
  const [expanded, setExpanded] = useState(false);
  const [copied, setCopied] = useState(false);

  // Clear or reset error when location changes
  useEffect(() => {
    setCurrentError(null);
    setExpanded(false);
  }, [location.pathname]);

  // Listen for global API or frontend errors
  useEffect(() => {
    const handleErrorEvent = (event) => {
      const err = event.detail;
      setCurrentError(err);
    };

    // Catch unhandled runtime errors in window
    const handleWindowError = (event) => {
      const pageUrl = window.location.href;
      setCurrentError({
        pageUrl,
        endpoint: 'Client-Side JavaScript / Render',
        status: 'Uncaught Error',
        message: event.message || 'JavaScript runtime error occurred on page',
        time: new Date().toLocaleTimeString(),
        stack: event.error?.stack
      });
    };

    window.addEventListener('app:page-error', handleErrorEvent);
    window.addEventListener('error', handleWindowError);

    return () => {
      window.removeEventListener('app:page-error', handleErrorEvent);
      window.removeEventListener('error', handleWindowError);
    };
  }, []);

  if (!currentError) return null;

  const copyErrorText = () => {
    const text = `Page URL: ${currentError.pageUrl}\nError: ${currentError.message}\nEndpoint: ${currentError.endpoint || 'N/A'}\nStatus: ${currentError.status || 'N/A'}\nTime: ${currentError.time}`;
    navigator.clipboard.writeText(text);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div style={{
      margin: '0 0 20px 0',
      padding: '16px 20px',
      backgroundColor: '#fff5f8',
      border: '1px solid #f8d7da',
      borderLeft: '5px solid #dc3545',
      borderRadius: '6px',
      boxShadow: '0 4px 12px rgba(220, 53, 69, 0.15)',
      fontFamily: 'inherit',
      fontSize: '13px',
      color: '#333',
      position: 'relative',
      zIndex: 9999
    }}>
      <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: '15px' }}>
        <div style={{ display: 'flex', alignItems: 'flex-start', gap: '12px', flex: 1 }}>
          <span style={{
            display: 'inline-flex',
            alignItems: 'center',
            justifyContent: 'center',
            width: '28px',
            height: '28px',
            borderRadius: '50%',
            backgroundColor: '#dc3545',
            color: '#fff',
            fontSize: '16px',
            fontWeight: 'bold',
            flexShrink: 0
          }}>
            !
          </span>

          <div style={{ flex: 1 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', flexWrap: 'wrap', marginBottom: '6px' }}>
              <span style={{ fontWeight: '700', color: '#dc3545', fontSize: '14px' }}>
                Error on Current Page
              </span>
              <span style={{
                background: '#e9ecef',
                padding: '2px 8px',
                borderRadius: '4px',
                fontSize: '11px',
                fontFamily: 'monospace',
                color: '#495057'
              }}>
                {currentError.time}
              </span>
              {currentError.status && (
                <span style={{
                  background: '#f8d7da',
                  color: '#721c24',
                  padding: '2px 8px',
                  borderRadius: '4px',
                  fontSize: '11px',
                  fontWeight: '600'
                }}>
                  Status: {currentError.status}
                </span>
              )}
            </div>

            {/* Page URL Display */}
            <div style={{ marginBottom: '6px', wordBreak: 'break-all' }}>
              <strong style={{ color: '#495057' }}>Page URL: </strong>
              <code style={{
                background: '#f1f3f5',
                padding: '2px 6px',
                borderRadius: '3px',
                color: '#d63384',
                fontWeight: '600'
              }}>
                {currentError.pageUrl}
              </code>
            </div>

            {/* Error Message */}
            <div style={{ marginBottom: '6px', color: '#b02a37', fontWeight: '600', fontSize: '13.5px' }}>
              <strong>Error Message: </strong>
              <span>{currentError.message}</span>
            </div>

            {/* Endpoint */}
            {currentError.endpoint && (
              <div style={{ fontSize: '12px', color: '#6c757d' }}>
                <strong>API Endpoint: </strong>
                <code>{currentError.endpoint}</code>
              </div>
            )}

            {/* Expandable Root Cause / Details */}
            {expanded && (
              <div style={{
                marginTop: '10px',
                padding: '10px',
                backgroundColor: '#212529',
                color: '#f8f9fa',
                borderRadius: '4px',
                fontSize: '12px',
                fontFamily: 'monospace',
                maxHeight: '200px',
                overflowY: 'auto'
              }}>
                <div><strong>Root Cause:</strong> {currentError.rootCause || currentError.message}</div>
                {currentError.stack && (
                  <pre style={{ margin: '8px 0 0 0', whiteSpace: 'pre-wrap', fontSize: '11px', color: '#adb5bd' }}>
                    {currentError.stack}
                  </pre>
                )}
                {currentError.data && (
                  <pre style={{ margin: '8px 0 0 0', whiteSpace: 'pre-wrap', fontSize: '11px', color: '#adb5bd' }}>
                    {JSON.stringify(currentError.data, null, 2)}
                  </pre>
                )}
              </div>
            )}
          </div>
        </div>

        {/* Action Buttons */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', flexShrink: 0 }}>
          <button
            onClick={() => setExpanded(!expanded)}
            style={{
              background: '#fff',
              border: '1px solid #ced4da',
              borderRadius: '4px',
              padding: '4px 10px',
              fontSize: '12px',
              cursor: 'pointer',
              color: '#495057'
            }}
          >
            {expanded ? 'Hide Details' : 'View Details'}
          </button>

          <button
            onClick={copyErrorText}
            style={{
              background: '#fff',
              border: '1px solid #ced4da',
              borderRadius: '4px',
              padding: '4px 10px',
              fontSize: '12px',
              cursor: 'pointer',
              color: '#495057'
            }}
          >
            {copied ? 'Copied!' : 'Copy'}
          </button>

          <a
            href="http://localhost:8081/api/v1/debug/error-log?lines=200"
            target="_blank"
            rel="noreferrer"
            style={{
              background: '#dc3545',
              border: 'none',
              borderRadius: '4px',
              padding: '5px 10px',
              fontSize: '12px',
              cursor: 'pointer',
              color: '#fff',
              textDecoration: 'none',
              display: 'inline-block'
            }}
          >
            Server Error Log
          </a>

          <button
            onClick={() => setCurrentError(null)}
            title="Dismiss"
            style={{
              background: 'none',
              border: 'none',
              fontSize: '20px',
              cursor: 'pointer',
              color: '#6c757d',
              padding: '0 5px'
            }}
          >
            &times;
          </button>
        </div>
      </div>
    </div>
  );
}
