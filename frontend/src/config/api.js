import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8081/api/v1',
  timeout: 30000,
  headers: {
    'Accept': 'application/json'
  }
});

// Request interceptor: attach current page URL so backend logs know which page triggered the request
api.interceptors.request.use((config) => {
  if (typeof window !== 'undefined' && window.location) {
    config.headers['X-Page-Url'] = window.location.href;
  }
  return config;
});

// Response interceptor: extract detailed error message and dispatch page error event
api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const pageUrl = typeof window !== 'undefined' && window.location ? window.location.href : 'Unknown Page';
    const status = error.response?.status;
    const statusText = error.response?.statusText || '';
    const errorMsg = error.response?.data?.message || error.message || 'Something went wrong. Please try again.';
    const method = error.config?.method?.toUpperCase() || 'REQUEST';
    const endpoint = `${method} ${error.config?.url || ''}`;

    console.error(`[Error on Page: ${pageUrl}] [${endpoint}] (Status: ${status}):`, errorMsg, error.response?.data);

    // Dispatch global event for PageErrorBanner
    if (typeof window !== 'undefined' && window.dispatchEvent) {
      window.dispatchEvent(new CustomEvent('app:page-error', {
        detail: {
          pageUrl,
          endpoint,
          status: status ? `${status} ${statusText}` : 'Network / Connection Error',
          message: errorMsg,
          rootCause: error.response?.data?.message || error.message,
          data: error.response?.data,
          time: new Date().toLocaleTimeString()
        }
      }));
    }

    return Promise.reject(new Error(errorMsg));
  }
);

export default api;
