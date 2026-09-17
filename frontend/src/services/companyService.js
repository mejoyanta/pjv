import api from '../config/api';

export const companyService = {
  // DataTables
  loadCompanies: (params) => api.get('/companies', { params }),
  loadArchive: (params) => api.get('/companies/archive', { params }),

  // CRUD
  getCompany: (slug) => api.get(`/companies/${slug}`),
  getCompanyById: (id) => api.get('/companies/info', { params: { id } }),

  createCompany: (formData) => api.post('/companies', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),

  updateCompany: (slug, formData) => api.put(`/companies/${slug}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),

  // Actions
  deleteCompany: (id) => api.delete(`/companies/${id}`),
  restoreCompany: (id) => api.post(`/companies/${id}/restore`),
  forceDeleteCompany: (id) => api.delete(`/companies/${id}/force`),

  // PDF
  downloadPdf: (search) => {
    window.open(`http://localhost:8080/api/v1/companies/download${search ? `?search=${encodeURIComponent(search)}` : ''}`, '_blank');
  },

  // Documents
  getDocuments: (slug) => api.get(`/companies/${slug}/documents`),
  uploadDocument: (slug, formData) => api.post(`/companies/${slug}/documents`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  deleteDocument: (slug, documentId) => api.delete(`/companies/${slug}/documents/${documentId}`),

  // Categories
  getCategories: () => api.get('/categories'),
  createCategory: (params) => api.post('/categories', null, { params }),
};
