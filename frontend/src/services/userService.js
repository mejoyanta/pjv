import api from '../config/api';

export const userService = {
  // DataTables
  loadUsers: (params) => api.get('/users', { params }),
  loadArchive: (params) => api.get('/users/archive', { params }),
  getStats: (companyId) => api.get('/users/stats', { params: { companyId } }),

  // CRUD
  getUser: (id) => api.get(`/users/${id}`),
  createUser: (formData, actorCompanyId) => api.post('/users', formData, {
    params: { actorCompanyId },
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  updateUser: (id, formData, actorCompanyId) => api.put(`/users/${id}`, formData, {
    params: { actorCompanyId },
    headers: { 'Content-Type': 'multipart/form-data' }
  }),

  // Actions
  deleteUser: (id) => api.delete(`/users/${id}`),
  restoreUser: (id) => api.post(`/users/${id}/restore`),
  forceDeleteUser: (id) => api.delete(`/users/${id}/force`),

  // Dropdowns
  getDesignations: (companyId) => api.get('/users/designations', { params: { companyId } }),
  getDepartments: (companyId) => api.get('/users/departments', { params: { companyId } }),
};
