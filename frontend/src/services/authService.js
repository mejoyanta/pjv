import api from '../config/api';

export const authService = {
  login: (username, password) => api.post('/auth/login', { username, password }),
  logout: (userId) => api.post('/auth/logout', null, { params: { userId } }),
};
