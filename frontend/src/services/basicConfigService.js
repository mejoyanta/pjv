import api from '../config/api';

export const triggerPdfDownload = async (url, filename = 'report.pdf') => {
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

export const currencyService = {
  load: (params) => api.get('/currencies', { params }),
  get: (id) => api.get(`/currencies/${id}`),
  create: (data) => api.post('/currencies', data),
  update: (id, data) => api.put(`/currencies/${id}`, data),
  delete: (id) => api.delete(`/currencies/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/currencies/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'currencies_report.pdf')
};

export const unitService = {
  load: (params) => api.get('/units', { params }),
  get: (id) => api.get(`/units/${id}`),
  create: (data) => api.post('/units', data),
  update: (id, data) => api.put(`/units/${id}`, data),
  delete: (id) => api.delete(`/units/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/units/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'units_report.pdf')
};

export const portService = {
  load: (params) => api.get('/ports', { params }),
  get: (id) => api.get(`/ports/${id}`),
  create: (data) => api.post('/ports', data),
  update: (id, data) => api.put(`/ports/${id}`, data),
  delete: (id) => api.delete(`/ports/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/ports/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'ports_report.pdf')
};

export const cpcItemNoService = {
  load: (params) => api.get('/cpc-item-nos', { params }),
  get: (id) => api.get(`/cpc-item-nos/${id}`),
  create: (data) => api.post('/cpc-item-nos', data),
  update: (id, data) => api.put(`/cpc-item-nos/${id}`, data),
  delete: (id) => api.delete(`/cpc-item-nos/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/cpc-item-nos/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'cpc_item_nos_report.pdf')
};

export const departmentService = {
  load: (params) => api.get('/departments', { params }),
  get: (id) => api.get(`/departments/${id}`),
  create: (data) => api.post('/departments', data),
  update: (id, data) => api.put(`/departments/${id}`, data),
  delete: (id) => api.delete(`/departments/${id}`),
  downloadPdf: (companyId, search) => {
    const q = new URLSearchParams();
    if (companyId) q.append('companyId', companyId);
    if (search) q.append('search', search);
    return triggerPdfDownload(`/departments/download-pdf?${q.toString()}`, 'departments_report.pdf');
  }
};

export const designationService = {
  load: (params) => api.get('/designations', { params }),
  get: (id) => api.get(`/designations/${id}`),
  create: (data) => api.post('/designations', data),
  update: (id, data) => api.put(`/designations/${id}`, data),
  delete: (id) => api.delete(`/designations/${id}`),
  downloadPdf: (companyId, search) => {
    const q = new URLSearchParams();
    if (companyId) q.append('companyId', companyId);
    if (search) q.append('search', search);
    return triggerPdfDownload(`/designations/download-pdf?${q.toString()}`, 'designations_report.pdf');
  }
};

export const additionalPriceService = {
  load: (params) => api.get('/additional-prices', { params }),
  get: (id) => api.get(`/additional-prices/${id}`),
  create: (data) => api.post('/additional-prices', data),
  update: (id, data) => api.put(`/additional-prices/${id}`, data),
  delete: (id) => api.delete(`/additional-prices/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/additional-prices/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'additional_prices_report.pdf')
};

export const materialService = {
  load: (params) => api.get('/materials', { params }),
  get: (id) => api.get(`/materials/${id}`),
  create: (data) => api.post('/materials', data),
  update: (id, data) => api.put(`/materials/${id}`, data),
  delete: (id) => api.delete(`/materials/${id}`),
  downloadPdf: (companyId, search) => {
    const q = new URLSearchParams();
    if (companyId) q.append('companyId', companyId);
    if (search) q.append('search', search);
    return triggerPdfDownload(`/materials/download-pdf?${q.toString()}`, 'materials_report.pdf');
  }
};

export const productService = {
  load: (params) => api.get('/products', { params }),
  get: (id) => api.get(`/products/${id}`),
  create: (data) => api.post('/products', data),
  update: (id, data) => api.put(`/products/${id}`, data),
  delete: (id) => api.delete(`/products/${id}`),
  downloadPdf: (companyId, search) => {
    const q = new URLSearchParams();
    if (companyId) q.append('companyId', companyId);
    if (search) q.append('search', search);
    return triggerPdfDownload(`/products/download-pdf?${q.toString()}`, 'products_report.pdf');
  }
};

export const barcodeService = {
  getItems: (companyId) => api.get('/barcodes/items', { params: { companyId } }),
  downloadBarcodePdf: async (items) => {
    const res = await api.post('/barcodes/generate-pdf', items, { responseType: 'blob' });
    const blob = new Blob([res.data], { type: 'application/pdf' });
    const blobUrl = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = blobUrl;
    link.setAttribute('download', 'barcode_labels_sheet.pdf');
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(blobUrl);
  }
};

export const supplierService = {
  load: (params) => api.get('/suppliers', { params }),
  get: (id) => api.get(`/suppliers/${id}`),
  create: (data) => api.post('/suppliers', data),
  update: (id, data) => api.put(`/suppliers/${id}`, data),
  delete: (id) => api.delete(`/suppliers/${id}`),
  downloadPdf: (companyId, search) => {
    const q = new URLSearchParams();
    if (companyId) q.append('companyId', companyId);
    if (search) q.append('search', search);
    return triggerPdfDownload(`/suppliers/download-pdf?${q.toString()}`, 'suppliers_report.pdf');
  }
};

export const prioritySupplierService = {
  load: (params) => api.get('/priority-suppliers', { params }),
  get: (id) => api.get(`/priority-suppliers/${id}`),
  create: (data) => api.post('/priority-suppliers', data),
  update: (id, data) => api.put(`/priority-suppliers/${id}`, data),
  delete: (id) => api.delete(`/priority-suppliers/${id}`),
  downloadPdf: (companyId, search) => {
    const q = new URLSearchParams();
    if (companyId) q.append('companyId', companyId);
    if (search) q.append('search', search);
    return triggerPdfDownload(`/priority-suppliers/download-pdf?${q.toString()}`, 'priority_suppliers_report.pdf');
  }
};

export const customerService = {
  load: (params) => api.get('/customers', { params }),
  get: (id) => api.get(`/customers/${id}`),
  create: (data) => api.post('/customers', data),
  update: (id, data) => api.put(`/customers/${id}`, data),
  delete: (id) => api.delete(`/customers/${id}`),
  downloadPdf: (companyId, search) => {
    const q = new URLSearchParams();
    if (companyId) q.append('companyId', companyId);
    if (search) q.append('search', search);
    return triggerPdfDownload(`/customers/download-pdf?${q.toString()}`, 'customers_report.pdf');
  }
};

export const priorityCustomerService = {
  load: (params) => api.get('/priority-customers', { params }),
  get: (id) => api.get(`/priority-customers/${id}`),
  create: (data) => api.post('/priority-customers', data),
  update: (id, data) => api.put(`/priority-customers/${id}`, data),
  delete: (id) => api.delete(`/priority-customers/${id}`),
  downloadPdf: (companyId, search) => {
    const q = new URLSearchParams();
    if (companyId) q.append('companyId', companyId);
    if (search) q.append('search', search);
    return triggerPdfDownload(`/priority-customers/download-pdf?${q.toString()}`, 'priority_customers_report.pdf');
  }
};
