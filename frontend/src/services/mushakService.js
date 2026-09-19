import api from '../config/api';

export const triggerPdfDownload = async (url, filename = 'document.pdf') => {
  try {
    const response = await api.get(url, { responseType: 'blob' });
    const blob = new Blob([response.data], { type: 'application/pdf' });
    const blobUrl = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = blobUrl;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(blobUrl);
  } catch (error) {
    console.error('Error downloading PDF:', error);
    throw error;
  }
};

export const mushak43Service = {
  load: (params) => api.get('/mushak-4-3', { params }),
  get: (id) => api.get(`/mushak-4-3/${id}`),
  getBySlug: (slug) => api.get(`/mushak-4-3/slug/${slug}`),
  create: (data) => api.post('/mushak-4-3', data),
  update: (id, data) => api.put(`/mushak-4-3/${id}`, data),
  delete: (id) => api.delete(`/mushak-4-3/${id}`),
  getFormData: (companyId) => api.get('/mushak-4-3/form-data', { params: companyId ? { companyId } : {} }),
  downloadPdf: (id) => triggerPdfDownload(`/mushak-4-3/${id}/pdf`, `mushak_4_3_${id}.pdf`),
};

export const mushak62Service = {
  load: (params) => api.get('/mushak-6-2', { params }),
  get: (id) => api.get(`/mushak-6-2/${id}`),
  getBySlug: (slug) => api.get(`/mushak-6-2/slug/${slug}`),
  create: (data) => api.post('/mushak-6-2', data),
  update: (id, data) => api.put(`/mushak-6-2/${id}`, data),
  delete: (id) => api.delete(`/mushak-6-2/${id}`),
  getFormData: (companyId) => api.get('/mushak-6-2/form-data', { params: companyId ? { companyId } : {} }),
  downloadPdf: (id) => triggerPdfDownload(`/mushak-6-2/${id}/pdf`, `mushak_6_2_${id}.pdf`),
};

export const mushak621Service = {
  load: (params) => api.get('/mushak-6-2-1', { params }),
  get: (id) => api.get(`/mushak-6-2-1/${id}`),
  getBySlug: (slug) => api.get(`/mushak-6-2-1/slug/${slug}`),
  create: (data) => api.post('/mushak-6-2-1', data),
  update: (id, data) => api.put(`/mushak-6-2-1/${id}`, data),
  delete: (id) => api.delete(`/mushak-6-2-1/${id}`),
  getFormData: (companyId) => api.get('/mushak-6-2-1/form-data', { params: companyId ? { companyId } : {} }),
  downloadPdf: (id) => triggerPdfDownload(`/mushak-6-2-1/${id}/pdf`, `mushak_6_2_1_${id}.pdf`),
};

export const mushak63Service = {
  load: (params) => api.get('/mushak-6-3', { params }),
  get: (id) => api.get(`/mushak-6-3/${id}`),
  getBySlug: (slug) => api.get(`/mushak-6-3/slug/${slug}`),
  create: (data) => api.post('/mushak-6-3', data),
  update: (id, data) => api.put(`/mushak-6-3/${id}`, data),
  delete: (id) => api.delete(`/mushak-6-3/${id}`),
  updateStatus: (id, status, reason) => api.post(`/mushak-6-3/${id}/status`, null, { params: { status, reason } }),
  getFormData: (companyId) => api.get('/mushak-6-3/form-data', { params: companyId ? { companyId } : {} }),
  downloadPdf: (id) => triggerPdfDownload(`/mushak-6-3/${id}/pdf`, `mushak_6_3_${id}.pdf`),
};

export const mushak610Service = {
  load: (params) => api.get('/mushak-6-10', { params }),
  get: (id) => api.get(`/mushak-6-10/${id}`),
  getBySlug: (slug) => api.get(`/mushak-6-10/slug/${slug}`),
  getFormData: () => api.get('/mushak-6-10/form-data'),
  downloadPdf: (id) => triggerPdfDownload(`/mushak-6-10/${id}/pdf`, `mushak_6_10_${id}.pdf`),
};

export const mushak91Service = {
  load: (params) => api.get('/mushak-9-1', { params }),
  get: (id) => api.get(`/mushak-9-1/${id}`),
  getBySlug: (slug) => api.get(`/mushak-9-1/slug/${slug}`),
  create: (data) => api.post('/mushak-9-1', data),
  update: (id, data) => api.put(`/mushak-9-1/${id}`, data),
  delete: (id) => api.delete(`/mushak-9-1/${id}`),
  getFormData: (companyId) => api.get('/mushak-9-1/form-data', { params: companyId ? { companyId } : {} }),
  downloadPdf: (id) => triggerPdfDownload(`/mushak-9-1/${id}/pdf`, `mushak_9_1_${id}.pdf`),
};

export const mushak91OnlineService = {
  load: (params) => api.get('/mushak-9-1-online', { params }),
  get: (id) => api.get(`/mushak-9-1-online/${id}`),
  getBySlug: (slug) => api.get(`/mushak-9-1-online/slug/${slug}`),
  create: (data) => api.post('/mushak-9-1-online', data),
  update: (id, data) => api.put(`/mushak-9-1-online/${id}`, data),
  delete: (id) => api.delete(`/mushak-9-1-online/${id}`),
  getFormData: () => api.get('/mushak-9-1-online/form-data'),
};
