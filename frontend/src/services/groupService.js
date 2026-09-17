import api from '../config/api';

export const groupService = {
  // DataTables
  loadGroups: (params) => api.get('/groups', { params }),
  loadArchive: (params) => api.get('/groups/archive', { params }),
  getActiveGroups: () => api.get('/groups/list'),

  // CRUD
  getGroup: (id) => api.get(`/groups/${id}`),
  getGroupBySlug: (slug) => api.get(`/groups/view/${slug}`),
  createGroup: (data) => api.post('/groups', data),
  updateGroup: (id, data) => api.put(`/groups/${id}`, data),

  // Actions
  deleteGroup: (id) => api.delete(`/groups/${id}`),
  restoreGroup: (id) => api.post(`/groups/${id}/restore`),
  forceDeleteGroup: (id) => api.delete(`/groups/${id}/force`),

  // Access Permissions
  getGroupAccess: (slug) => api.get(`/groups/${slug}/access`),
  saveGroupAccess: (slug, permissions) => api.put(`/groups/${slug}/access`, { permissions }),
};
