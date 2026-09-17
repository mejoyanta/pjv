import api from '../config/api';

export const branchService = {
  getBranches: (companyId) => api.get('/branches', { params: { companyId } }),
  createBranch: (data) => api.post('/branches', data),
};
