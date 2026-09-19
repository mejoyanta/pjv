import api from '../config/api';

export const triggerPdfDownload = async (url, filename = 'purchases_report.pdf') => {
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

export const purchaseService = {
  load: (params) => api.get('/purchases', { params }),
  get: (id) => api.get(`/purchases/${id}`),
  create: (data) => api.post('/purchases', data),
  update: (id, data) => api.put(`/purchases/${id}`, data),
  delete: (id) => api.delete(`/purchases/${id}`),
  multipleDelete: (ids) => api.post('/purchases/multiple-delete', { ids }),
  getFormData: (companyId) => api.get('/purchases/form-data', { params: companyId ? { companyId } : {} }),
  downloadPdf: (companyId, search) => {
    const query = new URLSearchParams();
    if (companyId) query.append('companyId', companyId);
    if (search) query.append('search', search);
    const qStr = query.toString() ? `?${query.toString()}` : '';
    return triggerPdfDownload(`/purchases/download-pdf${qStr}`, 'product_purchases.pdf');
  }
};

export default purchaseService;
