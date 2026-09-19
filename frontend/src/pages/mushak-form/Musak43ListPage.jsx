import React, { useState, useEffect } from 'react';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { mushak43Service } from '../../services/mushakService';

export default function Musak43ListPage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalRecords, setTotalRecords] = useState(0);
  const [filteredRecords, setFilteredRecords] = useState(0);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  // Filter States
  const [companies, setCompanies] = useState([]);
  const [branches, setBranches] = useState([]);
  const [products, setProducts] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState('');
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');
  const [search, setSearch] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');

  // Selection & UI States
  const [selectedIds, setSelectedIds] = useState([]);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);
  const [reloadTrigger, setReloadTrigger] = useState(0);

  // Create / Edit Modal State
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    companyId: '',
    companyBranchId: '',
    productId: '',
    productServiceDetails: '',
    hsCode: '',
    date: new Date().toISOString().split('T')[0],
    submissionId: '',
    submissionDate: new Date().toISOString().split('T')[0],
    basePrice: 0,
    purchaseQuantity: 1,
    totalAdditionalCost: 0,
    profit: '0',
    sellPrice: 0,
    hdWholesaleRate: 0,
    hdRetailerAmount: 0,
    amendmentComment: ''
  });

  // Modals
  const [viewItem, setViewItem] = useState(null);
  const [isViewOpen, setIsViewOpen] = useState(false);
  const [itemToDelete, setItemToDelete] = useState(null);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);

  useEffect(() => {
    mushak43Service.getFormData()
      .then(res => {
        if (res?.data?.data) {
          setCompanies(res.data.data.companies || []);
          setBranches(res.data.data.branches || []);
          setProducts(res.data.data.products || []);
        } else if (res?.data) {
          setCompanies(res.data.companies || []);
          setBranches(res.data.branches || []);
          setProducts(res.data.products || []);
        }
      })
      .catch(console.error);
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search);
      setPage(0);
    }, 400);
    return () => clearTimeout(timer);
  }, [search]);

  useEffect(() => {
    setLoading(true);
    const params = {
      draw: 1,
      start: page * pageSize,
      length: pageSize,
      searchValue: debouncedSearch,
    };
    if (selectedCompany) params.companyId = selectedCompany;
    if (fromDate) params.fromDate = fromDate;
    if (toDate) params.toDate = toDate;

    mushak43Service.load(params)
      .then(res => {
        setData(res.data || []);
        setTotalRecords(res.recordsTotal || 0);
        setFilteredRecords(res.recordsFiltered || 0);
      })
      .catch(err => {
        console.error('Failed to load Mushak 4.3:', err);
        setAlert({ type: 'danger', message: 'Failed to load Mushak 4.3 data.' });
      })
      .finally(() => setLoading(false));
  }, [page, pageSize, debouncedSearch, selectedCompany, fromDate, toDate, reloadTrigger]);

  const handleSelectAll = (e) => {
    if (e.target.checked) {
      setSelectedIds(data.map(item => item.id));
    } else {
      setSelectedIds([]);
    }
  };

  const handleSelectRow = (id) => {
    if (selectedIds.includes(id)) {
      setSelectedIds(selectedIds.filter(item => item !== id));
    } else {
      setSelectedIds([...selectedIds, id]);
    }
  };

  const openCreateModal = () => {
    setEditingItem(null);
    setFormData({
      companyId: companies.length > 0 ? companies[0].id : '',
      companyBranchId: '',
      productId: '',
      productServiceDetails: '',
      hsCode: '',
      date: new Date().toISOString().split('T')[0],
      submissionId: '',
      submissionDate: new Date().toISOString().split('T')[0],
      basePrice: 0,
      purchaseQuantity: 1,
      totalAdditionalCost: 0,
      profit: '0',
      sellPrice: 0,
      hdWholesaleRate: 0,
      hdRetailerAmount: 0,
      amendmentComment: ''
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      companyId: item.companyId || '',
      companyBranchId: item.companyBranchId || '',
      productId: item.productId || '',
      productServiceDetails: item.productServiceDetails || '',
      hsCode: item.hsCode || '',
      date: item.date ? String(item.date).substring(0, 10) : new Date().toISOString().split('T')[0],
      submissionId: item.submissionId || '',
      submissionDate: item.submissionDate ? String(item.submissionDate).substring(0, 10) : new Date().toISOString().split('T')[0],
      basePrice: item.purchasePrice != null ? item.purchasePrice : (item.basePrice || 0),
      purchaseQuantity: item.qtyCost != null ? item.qtyCost : (item.purchaseQuantity || 1),
      totalAdditionalCost: item.additionalCost != null ? item.additionalCost : (item.totalAdditionalCost || 0),
      profit: item.profit != null ? String(item.profit) : '0',
      sellPrice: item.salePrice != null ? item.salePrice : (item.sellPrice || 0),
      hdWholesaleRate: item.wholesalePrice != null ? item.wholesalePrice : (item.hdWholesaleRate || 0),
      hdRetailerAmount: item.retailerAmount != null ? item.retailerAmount : (item.hdRetailerAmount || 0),
      amendmentComment: item.amendmentComment || ''
    });
    setIsFormOpen(true);
  };

  // Recalculate sellPrice when base, additional cost, or profit changes
  const handleCalcChange = (updates) => {
    const updated = { ...formData, ...updates };
    const base = parseFloat(updated.basePrice) || 0;
    const additional = parseFloat(updated.totalAdditionalCost) || 0;
    const profit = parseFloat(updated.profit) || 0;
    const totalCost = base + additional;
    updated.sellPrice = totalCost + profit;
    setFormData(updated);
  };

  const handleProductSelect = (prodId) => {
    const p = products.find(prod => String(prod.id) === String(prodId));
    if (p) {
      setFormData(prev => ({
        ...prev,
        productId: p.id,
        productServiceDetails: p.name || '',
        hsCode: p.hsCode || prev.hsCode,
        companyId: p.companyId || prev.companyId
      }));
    } else {
      setFormData(prev => ({ ...prev, productId: prodId }));
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await mushak43Service.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'Mushak 4.3 updated successfully!' });
      } else {
        await mushak43Service.create(formData);
        setAlert({ type: 'success', message: 'Mushak 4.3 created successfully!' });
      }
      setIsFormOpen(false);
      setReloadTrigger(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message || 'Failed to save Mushak 4.3.' });
    }
  };

  const confirmDelete = async () => {
    if (!itemToDelete) return;
    try {
      await mushak43Service.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Mushak 4.3 record deleted successfully!' });
      setReloadTrigger(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message || 'Failed to delete record.' });
    } finally {
      setIsDeleteOpen(false);
      setItemToDelete(null);
    }
  };

  const handlePdfDownload = async (id) => {
    try {
      setIsPdfLoading(true);
      await mushak43Service.downloadPdf(id);
    } catch (err) {
      setAlert({ type: 'danger', message: 'Failed to download PDF.' });
    } finally {
      setIsPdfLoading(false);
    }
  };

  const filteredBranches = formData.companyId
    ? branches.filter(b => String(b.companyId) === String(formData.companyId))
    : branches;

  const totalPages = Math.ceil(filteredRecords / pageSize);

  return (
    <div className="kt-content kt-grid__item kt-grid__item--fluid" id="kt_content">
      <Subheader
        title="4.3 Mushak Forms"
        breadcrumbs={[
          { title: 'Mushak Form', url: '/mushak-form/musak-4-3' },
          { title: '4.3 Mushak Forms' }
        ]}
        actions={
          <div style={{ display: 'flex', gap: 8 }}>
            <button
              type="button"
              className="btn btn-brand btn-elevate btn-icon-sm"
              onClick={openCreateModal}
            >
              <i className="la la-plus"></i> Add Mushak 4.3
            </button>
          </div>
        }
      />

      {alert && (
        <div className="mb-3">
          <Alert type={alert.type} message={alert.message} onClose={() => setAlert(null)} />
        </div>
      )}

      <div className="kt-portlet kt-portlet--mobile">
        <div className="kt-portlet__head kt-portlet__head--lg">
          <div className="kt-portlet__head-label">
            <span className="kt-portlet__head-icon">
              <i className="kt-font-brand flaticon2-line-chart"></i>
            </span>
            <h3 className="kt-portlet__head-title">Mushak 4.3 (Price Declaration)</h3>
          </div>
          <div className="kt-portlet__head-toolbar">
            <div className="kt-portlet__head-wrapper">
              <button
                type="button"
                className="btn btn-clean btn-icon-sm"
                onClick={() => setReloadTrigger(prev => prev + 1)}
              >
                <i className="la la-refresh"></i> Reload
              </button>
            </div>
          </div>
        </div>

        <div className="kt-portlet__body">
          {/* Filters Row */}
          <div className="kt-form kt-form--label-right mb-4">
            <div className="row align-items-center">
              <div className="col-md-3">
                <label className="form-label text-muted small">Company</label>
                <select
                  className="form-control"
                  value={selectedCompany}
                  onChange={(e) => { setSelectedCompany(e.target.value); setPage(0); }}
                >
                  <option value="">All Companies</option>
                  {companies.map(c => (
                    <option key={c.id} value={c.id}>{c.name}</option>
                  ))}
                </select>
              </div>

              <div className="col-md-3">
                <label className="form-label text-muted small">From Date</label>
                <input
                  type="date"
                  className="form-control"
                  value={fromDate}
                  onChange={(e) => { setFromDate(e.target.value); setPage(0); }}
                />
              </div>

              <div className="col-md-3">
                <label className="form-label text-muted small">To Date</label>
                <input
                  type="date"
                  className="form-control"
                  value={toDate}
                  onChange={(e) => { setToDate(e.target.value); setPage(0); }}
                />
              </div>

              <div className="col-md-3">
                <label className="form-label text-muted small">Search</label>
                <div className="kt-input-icon kt-input-icon--left">
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Search HS code, details..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                  />
                  <span className="kt-input-icon__icon kt-input-icon__icon--left">
                    <span><i className="la la-search"></i></span>
                  </span>
                </div>
              </div>
            </div>
          </div>

          {/* Table */}
          <div className="table-responsive" style={{ minHeight: '300px' }}>
            <table className="table table-striped table-bordered table-hover">
              <thead className="thead-light">
                <tr>
                  <th style={{ width: '30px' }} className="text-center">
                    <input
                      type="checkbox"
                      checked={data.length > 0 && selectedIds.length === data.length}
                      onChange={handleSelectAll}
                    />
                  </th>
                  <th>SL</th>
                  <th>Submission ID</th>
                  <th>Submission Date</th>
                  <th>BOE/Invoice Date</th>
                  <th>BOE No</th>
                  <th>Company</th>
                  <th>Branch</th>
                  <th>Product Details</th>
                  <th>HS Code</th>
                  <th>Purchase Price</th>
                  <th>Qty Cost</th>
                  <th>Additional Cost</th>
                  <th>Profit</th>
                  <th>Sale Price</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td colSpan="16" className="text-center py-5 text-muted">
                      <div className="spinner-border text-primary" role="status">
                        <span className="sr-only">Loading...</span>
                      </div>
                      <div className="mt-2">Loading Mushak 4.3 data...</div>
                    </td>
                  </tr>
                ) : data.length === 0 ? (
                  <tr>
                    <td colSpan="16" className="text-center py-5 text-muted">
                      No records found.
                    </td>
                  </tr>
                ) : (
                  data.map((item, idx) => (
                    <tr key={item.id}>
                      <td className="text-center">
                        <input
                          type="checkbox"
                          checked={selectedIds.includes(item.id)}
                          onChange={() => handleSelectRow(item.id)}
                        />
                      </td>
                      <td>{page * pageSize + idx + 1}</td>
                      <td><span className="badge badge-info">{item.submissionId || 'N/A'}</span></td>
                      <td>{item.submissionDate || 'N/A'}</td>
                      <td>{item.boeDate || 'N/A'}</td>
                      <td>{item.billOfEntry || 'N/A'}</td>
                      <td>{item.companyName || '—'}</td>
                      <td>{item.branchName || 'N/A'}</td>
                      <td>{item.productServiceDetails || 'N/A'}</td>
                      <td><code>{item.hsCode || 'N/A'}</code></td>
                      <td className="text-right">{Number(item.purchasePrice || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                      <td className="text-right">{Number(item.qtyCost || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                      <td className="text-right">{Number(item.additionalCost || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                      <td className="text-right">{Number(item.profit || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                      <td className="text-right font-weight-bold">{Number(item.salePrice || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                      <td className="text-center text-nowrap">
                        <button
                          type="button"
                          className="btn btn-sm btn-clean btn-icon btn-icon-md text-info"
                          title="View Details"
                          onClick={() => { setViewItem(item); setIsViewOpen(true); }}
                        >
                          <i className="la la-eye"></i>
                        </button>
                        <button
                          type="button"
                          className="btn btn-sm btn-clean btn-icon btn-icon-md text-primary"
                          title="Edit 4.3"
                          onClick={() => openEditModal(item)}
                        >
                          <i className="la la-edit"></i>
                        </button>
                        <button
                          type="button"
                          className="btn btn-sm btn-clean btn-icon btn-icon-md text-danger"
                          title="Download PDF"
                          onClick={() => handlePdfDownload(item.id)}
                          disabled={isPdfLoading}
                        >
                          <i className="la la-file-pdf-o"></i>
                        </button>
                        <button
                          type="button"
                          className="btn btn-sm btn-clean btn-icon btn-icon-md text-danger"
                          title="Delete"
                          onClick={() => { setItemToDelete(item); setIsDeleteOpen(true); }}
                        >
                          <i className="la la-trash"></i>
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          <div className="d-flex justify-content-between align-items-center mt-3">
            <div className="text-muted small">
              Showing {data.length > 0 ? page * pageSize + 1 : 0} to {Math.min((page + 1) * pageSize, filteredRecords)} of {filteredRecords} entries
            </div>
            <div className="btn-group">
              <button
                className="btn btn-sm btn-secondary"
                disabled={page === 0}
                onClick={() => setPage(prev => prev - 1)}
              >
                Previous
              </button>
              <span className="btn btn-sm btn-light disabled">
                Page {page + 1} of {Math.max(1, totalPages)}
              </span>
              <button
                className="btn btn-sm btn-secondary"
                disabled={page >= totalPages - 1}
                onClick={() => setPage(prev => prev + 1)}
              >
                Next
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay" style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', zIndex: 1050, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 15 }}>
          <div className="modal-content" style={{ maxWidth: 850, maxHeight: '92vh', overflowY: 'auto', background: '#fff', borderRadius: 8, boxShadow: '0 5px 20px rgba(0,0,0,0.3)' }}>
            <div className="modal-header" style={{ padding: '15px 20px', borderBottom: '1px solid #ebedf2', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h5 style={{ margin: 0, fontWeight: 600 }}>{editingItem ? 'Edit Mushak 4.3' : 'Create Mushak 4.3 (Price Declaration)'}</h5>
              <button type="button" className="btn btn-sm btn-label-secondary" onClick={() => setIsFormOpen(false)} style={{ border: 'none', background: 'none', fontSize: 20 }}>×</button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body" style={{ padding: '20px' }}>
                <div className="row">
                  {/* Company */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Company <span className="text-danger">*</span></label>
                    <select
                      className="form-control"
                      value={formData.companyId}
                      onChange={(e) => setFormData({ ...formData, companyId: e.target.value, companyBranchId: '' })}
                      required
                    >
                      <option value="">Select Company Name</option>
                      {companies.map(c => (
                        <option key={c.id} value={c.id}>{c.name}</option>
                      ))}
                    </select>
                  </div>

                  {/* Branch */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Company Branch</label>
                    <select
                      className="form-control"
                      value={formData.companyBranchId}
                      onChange={(e) => setFormData({ ...formData, companyBranchId: e.target.value })}
                    >
                      <option value="">Company Branch</option>
                      {filteredBranches.map(b => (
                        <option key={b.id} value={b.id}>{b.name}</option>
                      ))}
                    </select>
                  </div>

                  {/* Existing Product Auto-Fill */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Select Product (Optional)</label>
                    <select
                      className="form-control"
                      value={formData.productId}
                      onChange={(e) => handleProductSelect(e.target.value)}
                    >
                      <option value="">Select Product to Autofill</option>
                      {products.map(p => (
                        <option key={p.id} value={p.id}>{p.name} ({p.hsCode || 'No HS'})</option>
                      ))}
                    </select>
                  </div>

                  {/* Product Details */}
                  <div className="col-12 col-md-8 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Product / Service Details <span className="text-danger">*</span></label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="Product Description or Details"
                      value={formData.productServiceDetails}
                      onChange={(e) => setFormData({ ...formData, productServiceDetails: e.target.value })}
                    />
                  </div>

                  {/* HS Code */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>HS Code <span className="text-danger">*</span></label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="HS-Code"
                      value={formData.hsCode}
                      onChange={(e) => setFormData({ ...formData, hsCode: e.target.value })}
                    />
                  </div>

                  {/* Date */}
                  <div className="col-12 col-md-3 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Date</label>
                    <input
                      type="date"
                      className="form-control"
                      value={formData.date}
                      onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                    />
                  </div>

                  {/* Submission ID */}
                  <div className="col-12 col-md-3 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Submission ID</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. SUB-43-001"
                      value={formData.submissionId}
                      onChange={(e) => setFormData({ ...formData, submissionId: e.target.value })}
                    />
                  </div>

                  {/* Submission Date */}
                  <div className="col-12 col-md-3 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Submission Date</label>
                    <input
                      type="date"
                      className="form-control"
                      value={formData.submissionDate}
                      onChange={(e) => setFormData({ ...formData, submissionDate: e.target.value })}
                    />
                  </div>

                  {/* Quantity */}
                  <div className="col-12 col-md-3 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Quantity</label>
                    <input
                      type="number"
                      step="any"
                      min="1"
                      className="form-control"
                      value={formData.purchaseQuantity}
                      onChange={(e) => setFormData({ ...formData, purchaseQuantity: parseFloat(e.target.value) || 1 })}
                    />
                  </div>

                  {/* Base / Raw Material Price */}
                  <div className="col-12 col-md-3 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Raw Material Price (Base)</label>
                    <input
                      type="number"
                      step="any"
                      min="0"
                      className="form-control"
                      value={formData.basePrice}
                      onChange={(e) => handleCalcChange({ basePrice: parseFloat(e.target.value) || 0 })}
                    />
                  </div>

                  {/* Additional Overheads Cost */}
                  <div className="col-12 col-md-3 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Total Additional Cost</label>
                    <input
                      type="number"
                      step="any"
                      min="0"
                      className="form-control"
                      value={formData.totalAdditionalCost}
                      onChange={(e) => handleCalcChange({ totalAdditionalCost: parseFloat(e.target.value) || 0 })}
                    />
                  </div>

                  {/* Profit */}
                  <div className="col-12 col-md-3 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Profit</label>
                    <input
                      type="text"
                      className="form-control"
                      value={formData.profit}
                      onChange={(e) => handleCalcChange({ profit: e.target.value })}
                    />
                  </div>

                  {/* Sale Price (Auto-calculated) */}
                  <div className="col-12 col-md-3 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Sale Price (Cost + Profit)</label>
                    <input
                      type="number"
                      step="any"
                      min="0"
                      className="form-control font-weight-bold text-success"
                      value={formData.sellPrice}
                      onChange={(e) => setFormData({ ...formData, sellPrice: parseFloat(e.target.value) || 0 })}
                    />
                  </div>

                  {/* Wholesale Rate */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Wholesale Rate</label>
                    <input
                      type="number"
                      step="any"
                      min="0"
                      className="form-control"
                      value={formData.hdWholesaleRate}
                      onChange={(e) => setFormData({ ...formData, hdWholesaleRate: parseFloat(e.target.value) || 0 })}
                    />
                  </div>

                  {/* Retailer Rate */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Retailer Amount</label>
                    <input
                      type="number"
                      step="any"
                      min="0"
                      className="form-control"
                      value={formData.hdRetailerAmount}
                      onChange={(e) => setFormData({ ...formData, hdRetailerAmount: parseFloat(e.target.value) || 0 })}
                    />
                  </div>

                  {/* Amendment Comment */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Amendment Comment</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Comment if amended"
                      value={formData.amendmentComment}
                      onChange={(e) => setFormData({ ...formData, amendmentComment: e.target.value })}
                    />
                  </div>
                </div>
              </div>
              <div className="modal-footer" style={{ padding: '15px 20px', borderTop: '1px solid #ebedf2', display: 'flex', justifyContent: 'flex-end', gap: 10 }}>
                <button type="button" className="btn btn-secondary" onClick={() => setIsFormOpen(false)}>Cancel</button>
                <button type="submit" className="btn btn-brand">{editingItem ? 'Update Mushak 4.3' : 'Submit'}</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* View Modal */}
      {isViewOpen && viewItem && (
        <div className="modal-overlay" style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', zIndex: 1050, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 15 }}>
          <div className="modal-content" style={{ maxWidth: 650, background: '#fff', borderRadius: 8, boxShadow: '0 5px 20px rgba(0,0,0,0.3)' }}>
            <div className="modal-header" style={{ padding: '15px 20px', borderBottom: '1px solid #ebedf2', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h5 style={{ margin: 0, fontWeight: 600 }}>Mushak 4.3 Details</h5>
              <button type="button" className="btn btn-sm btn-label-secondary" onClick={() => setIsViewOpen(false)} style={{ border: 'none', background: 'none', fontSize: 20 }}>×</button>
            </div>
            <div className="modal-body" style={{ padding: 20 }}>
              <table className="table table-bordered">
                <tbody>
                  <tr><th style={{ width: '40%' }}>Product/Service Details</th><td>{viewItem.productServiceDetails || '—'}</td></tr>
                  <tr><th>HS Code</th><td>{viewItem.hsCode || '—'}</td></tr>
                  <tr><th>Company / Branch</th><td>{viewItem.companyName || '—'} / {viewItem.branchName || 'N/A'}</td></tr>
                  <tr><th>Submission ID / Date</th><td>{viewItem.submissionId || 'N/A'} ({viewItem.submissionDate || 'N/A'})</td></tr>
                  <tr><th>BOE No / Date</th><td>{viewItem.billOfEntry || 'N/A'} ({viewItem.boeDate || 'N/A'})</td></tr>
                  <tr><th>Purchase Price</th><td>{Number(viewItem.purchasePrice || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td></tr>
                  <tr><th>Quantity Cost</th><td>{Number(viewItem.qtyCost || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td></tr>
                  <tr><th>Additional Costs</th><td>{Number(viewItem.additionalCost || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td></tr>
                  <tr><th>Profit</th><td>{Number(viewItem.profit || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td></tr>
                  <tr><th>Sale Price</th><td className="font-weight-bold text-success">{Number(viewItem.salePrice || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td></tr>
                  <tr><th>Wholesale Price</th><td>{Number(viewItem.wholesalePrice || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td></tr>
                  <tr><th>Retailer Amount</th><td>{Number(viewItem.retailerAmount || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td></tr>
                </tbody>
              </table>
            </div>
            <div className="modal-footer" style={{ padding: '15px 20px', borderTop: '1px solid #ebedf2', display: 'flex', justifyContent: 'flex-end' }}>
              <button className="btn btn-secondary" onClick={() => setIsViewOpen(false)}>Close</button>
            </div>
          </div>
        </div>
      )}

      <ConfirmModal
        isOpen={isDeleteOpen}
        title="Confirm Deletion"
        message="Are you sure you want to delete this Mushak 4.3 declaration?"
        onConfirm={confirmDelete}
        onCancel={() => setIsDeleteOpen(false)}
      />
    </div>
  );
}
