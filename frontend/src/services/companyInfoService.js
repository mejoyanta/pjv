import api from '../config/api';

export const documentRegisterService = {
  load: (params) => api.get('/document-registers', { params }),
  get: (id) => api.get(`/document-registers/${id}`),
  create: (data) => api.post('/document-registers', data),
  update: (id, data) => api.put(`/document-registers/${id}`, data),
  delete: (id) => api.delete(`/document-registers/${id}`)
};

export const dvcService = {
  load: (params) => api.get('/dvcs', { params }),
  get: (id) => api.get(`/dvcs/${id}`),
  create: (data) => api.post('/dvcs', data),
  update: (id, data) => api.put(`/dvcs/${id}`, data),
  delete: (id) => api.delete(`/dvcs/${id}`)
};

export const companyReportService = {
  load: (params) => api.get('/company-reports', { params }),
  get: (id) => api.get(`/company-reports/${id}`),
  create: (data) => api.post('/company-reports', data),
  update: (id, data) => api.put(`/company-reports/${id}`, data),
  delete: (id) => api.delete(`/company-reports/${id}`)
};

export const auditReportService = {
  load: (params) => api.get('/audit-reports', { params }),
  get: (id) => api.get(`/audit-reports/${id}`),
  create: (data) => api.post('/audit-reports', data),
  update: (id, data) => api.put(`/audit-reports/${id}`, data),
  delete: (id) => api.delete(`/audit-reports/${id}`)
};

export const analyzeReportService = {
  load: (params) => api.get('/analyze-reports', { params }),
  get: (id) => api.get(`/analyze-reports/${id}`),
  create: (data) => api.post('/analyze-reports', data),
  update: (id, data) => api.put(`/analyze-reports/${id}`, data),
  delete: (id) => api.delete(`/analyze-reports/${id}`)
};

export const legalManagementService = {
  load: (params) => api.get('/legal-management', { params }),
  get: (id) => api.get(`/legal-management/${id}`),
  create: (data) => api.post('/legal-management', data),
  update: (id, data) => api.put(`/legal-management/${id}`, data),
  delete: (id) => api.delete(`/legal-management/${id}`)
};

export const taskManagementService = {
  load: (params) => api.get('/task-management', { params }),
  get: (id) => api.get(`/task-management/${id}`),
  create: (data) => api.post('/task-management', data),
  update: (id, data) => api.put(`/task-management/${id}`, data),
  delete: (id) => api.delete(`/task-management/${id}`)
};

export const companyNocService = {
  load: (params) => api.get('/company-noc', { params }),
  get: (id) => api.get(`/company-noc/${id}`),
  create: (data) => api.post('/company-noc', data),
  update: (id, data) => api.put(`/company-noc/${id}`, data),
  delete: (id) => api.delete(`/company-noc/${id}`)
};
