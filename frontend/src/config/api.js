import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1',
  timeout: 30000,
  headers: {
    'Accept': 'application/json'
  }
});

// Response interceptor
api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const errorMsg = error.response?.data?.message || error.message || 'Something went wrong. Please try again.';
    console.error('API Error:', errorMsg, error.response?.data);
    return Promise.reject(new Error(errorMsg));
  }
);

export default api;
