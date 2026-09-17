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

// 1. Payments
export const paymentService = {
  load: (params) => api.get('/payments', { params }),
  get: (id) => api.get(`/payments/${id}`),
  create: (data) => api.post('/payments', data),
  update: (id, data) => api.put(`/payments/${id}`, data),
  delete: (id) => api.delete(`/payments/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/payments/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'payments_report.pdf')
};

// 2. Credit Invoice
export const creditInvoiceService = {
  load: (params) => api.get('/credit-invoices', { params }),
  get: (id) => api.get(`/credit-invoices/${id}`),
  create: (data) => api.post('/credit-invoices', data),
  update: (id, data) => api.put(`/credit-invoices/${id}`, data),
  delete: (id) => api.delete(`/credit-invoices/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/credit-invoices/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'credit_invoices_report.pdf')
};

// 3. Debit Invoice
export const debitInvoiceService = {
  load: (params) => api.get('/debit-invoices', { params }),
  get: (id) => api.get(`/debit-invoices/${id}`),
  create: (data) => api.post('/debit-invoices', data),
  update: (id, data) => api.put(`/debit-invoices/${id}`, data),
  delete: (id) => api.delete(`/debit-invoices/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/debit-invoices/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'debit_invoices_report.pdf')
};

// 4. Bank Treasury
export const bankTreasuryService = {
  load: (params) => api.get('/bank-treasuries', { params }),
  get: (id) => api.get(`/bank-treasuries/${id}`),
  create: (data) => api.post('/bank-treasuries', data),
  update: (id, data) => api.put(`/bank-treasuries/${id}`, data),
  delete: (id) => api.delete(`/bank-treasuries/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/bank-treasuries/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'bank_treasuries_report.pdf')
};

// 5. Bank Transactions
export const bankTransactionsService = {
  load: (params) => api.get('/bank-transactions', { params }),
  get: (id) => api.get(`/bank-transactions/${id}`),
  create: (data) => api.post('/bank-transactions', data),
  update: (id, data) => api.put(`/bank-transactions/${id}`, data),
  delete: (id) => api.delete(`/bank-transactions/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/bank-transactions/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'bank_transactions_report.pdf')
};

// 6. Bank Account Info (Bank Info Details)
export const bankInfoDetailService = {
  load: (params) => api.get('/bank-info-details', { params }),
  get: (id) => api.get(`/bank-info-details/${id}`),
  create: (data) => api.post('/bank-info-details', data),
  update: (id, data) => api.put(`/bank-info-details/${id}`, data),
  delete: (id) => api.delete(`/bank-info-details/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/bank-info-details/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'bank_info_details_report.pdf')
};

// 7. Bank & Branch
export const bankService = {
  load: (params) => api.get('/banks', { params }),
  get: (id) => api.get(`/banks/${id}`),
  create: (data) => api.post('/banks', data),
  update: (id, data) => api.put(`/banks/${id}`, data),
  delete: (id) => api.delete(`/banks/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/banks/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'banks_report.pdf')
};

export const bankBranchService = {
  load: (params) => api.get('/bank-branches', { params }),
  get: (id) => api.get(`/bank-branches/${id}`),
  create: (data) => api.post('/bank-branches', data),
  update: (id, data) => api.put(`/bank-branches/${id}`, data),
  delete: (id) => api.delete(`/bank-branches/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/bank-branches/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'bank_branches_report.pdf')
};

// 8. Mobile Banking (Accounts & Transactions)
export const mobileBankingAccountService = {
  load: (params) => api.get('/mobile-banking-accounts', { params }),
  get: (id) => api.get(`/mobile-banking-accounts/${id}`),
  create: (data) => api.post('/mobile-banking-accounts', data),
  update: (id, data) => api.put(`/mobile-banking-accounts/${id}`, data),
  delete: (id) => api.delete(`/mobile-banking-accounts/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/mobile-banking-accounts/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'mobile_banking_accounts_report.pdf')
};

export const mobileBankingTransactionService = {
  load: (params) => api.get('/mobile-banking-transactions', { params }),
  get: (id) => api.get(`/mobile-banking-transactions/${id}`),
  create: (data) => api.post('/mobile-banking-transactions', data),
  update: (id, data) => api.put(`/mobile-banking-transactions/${id}`, data),
  delete: (id) => api.delete(`/mobile-banking-transactions/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/mobile-banking-transactions/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'mobile_banking_transactions_report.pdf')
};

// 9. Any Other Increasing Adjustments (Rent Vat)
export const rentVatService = {
  load: (params) => api.get('/rent-vat', { params }),
  get: (id) => api.get(`/rent-vat/${id}`),
  create: (data) => api.post('/rent-vat', data),
  update: (id, data) => api.put(`/rent-vat/${id}`, data),
  delete: (id) => api.delete(`/rent-vat/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/rent-vat/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'rent_vat_report.pdf')
};

// 10. Any Other Decreasing Adjustments
export const adjustmentDecreaseService = {
  load: (params) => api.get('/adjustment-decrease', { params }),
  get: (id) => api.get(`/adjustment-decrease/${id}`),
  create: (data) => api.post('/adjustment-decrease', data),
  update: (id, data) => api.put(`/adjustment-decrease/${id}`, data),
  delete: (id) => api.delete(`/adjustment-decrease/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/adjustment-decrease/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'adjustment_decrease_report.pdf')
};

// 11. Packages
export const packageService = {
  load: (params) => api.get('/packages', { params }),
  get: (id) => api.get(`/packages/${id}`),
  create: (data) => api.post('/packages', data),
  update: (id, data) => api.put(`/packages/${id}`, data),
  delete: (id) => api.delete(`/packages/${id}`),
  downloadPdf: (search) => triggerPdfDownload(`/packages/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'packages_report.pdf')
};

// 12. Subscriptions
export const subscriptionService = {
  load: (params) => api.get('/subscriptions', { params }),
  get: (id) => api.get(`/subscriptions/${id}`),
  renew: (companyId, data) => api.post(`/subscriptions/renew/${companyId}`, data),
  downloadPdf: (search) => triggerPdfDownload(`/subscriptions/download-pdf${search ? `?search=${encodeURIComponent(search)}` : ''}`, 'subscriptions_report.pdf')
};
