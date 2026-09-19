import React, { useState, useEffect } from 'react';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { mushak63Service } from '../../services/mushakService';

export default function Musak63ListPage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalRecords, setTotalRecords] = useState(0);
  const [filteredRecords, setFilteredRecords] = useState(0);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  // Filters
  const [companies, setCompanies] = useState([]);
  const [branches, setBranches] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState('');
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');
  const [search, setSearch] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');

  // Selection & UI
  const [selectedIds, setSelectedIds] = useState([]);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);
  const [reloadTrigger, setReloadTrigger] = useState(0);

  // Form Modal State (Create & Edit)
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    companyId: '',
    companyBranchId: '',
    viNo: '',
    date: new Date().toISOString().split('T')[0],
    buyerType: 'registered',
    paymentStatus: 'pending',
    comments: '',
    rejectionReason: ''
  });

  // Status Modal State (CR / Payment status approval)
  const [isStatusModalOpen, setIsStatusModalOpen] = useState(false);
  const [statusItem, setStatusItem] = useState(null);
  const [statusForm, setStatusForm] = useState({ status: 'approved', reason: '' });

  // View modal
  const [viewItem, setViewItem] = useState(null);
  const [isViewOpen, setIsViewOpen] = useState(false);

  // Delete modal
  const [itemToDelete, setItemToDelete] = useState(null);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);

  useEffect(() => {
    mushak63Service.getFormData()
      .then(res => {
        if (res?.data?.data) {
          setCompanies(res.data.data.companies || []);
          setBranches(res.data.data.branches || []);
        } else if (res?.data) {
          setCompanies(res.data.companies || []);
          setBranches(res.data.branches || []);
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

    mushak63Service.load(params)
      .then(res => {
        setData(res.data || []);
        setTotalRecords(res.recordsTotal || 0);
        setFilteredRecords(res.recordsFiltered || 0);
      })
      .catch(err => {
        console.error('Failed to load Mushak 6.3:', err);
        setAlert({ type: 'danger', message: 'Failed to load Mushak 6.3 data.' });
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
      viNo: Math.floor(100000 + Math.random() * 900000),
      date: new Date().toISOString().split('T')[0],
      buyerType: 'registered',
      paymentStatus: 'pending',
      comments: '',
      rejectionReason: ''
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      companyId: item.companyId || '',
      companyBranchId: item.companyBranchId || '',
      viNo: item.viNo || '',
      date: item.date ? String(item.date).substring(0, 10) : new Date().toISOString().split('T')[0],
      buyerType: item.buyerType || 'registered',
      paymentStatus: item.paymentStatus || 'pending',
      comments: item.comments || '',
      rejectionReason: item.rejectionReason || ''
    });
    setIsFormOpen(true);
  };

  const openStatusModal = (item) => {
    setStatusItem(item);
    setStatusForm({
      status: item.paymentStatus === 'approved' ? 'pending' : 'approved',
      reason: ''
    });
    setIsStatusModalOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await mushak63Service.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'Mushak 6.3 updated successfully!' });
      } else {
        await mushak63Service.create(formData);
        setAlert({ type: 'success', message: 'Mushak 6.3 created successfully!' });
      }
      setIsFormOpen(false);
      setReloadTrigger(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message || 'Failed to save Mushak 6.3.' });
    }
  };

  const handleStatusSubmit = async (e) => {
    e.preventDefault();
    if (!statusItem) return;
    try {
      await mushak63Service.updateStatus(statusItem.id, statusForm.status, statusForm.reason);
      setAlert({ type: 'success', message: `Status updated to ${statusForm.status} successfully!` });
      setIsStatusModalOpen(false);
      setReloadTrigger(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message || 'Failed to update status.' });
    }
  };

  const confirmDelete = async () => {
    if (!itemToDelete) return;
    try {
      await mushak63Service.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Mushak 6.3 record deleted successfully!' });
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
      await mushak63Service.downloadPdf(id);
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
        title="6.3 Mushak Forms"
        breadcrumbs={[
          { title: 'Mushak Form', url: '/mushak-form/musak-6-3' },
          { title: '6.3 Mushak Forms' }
        ]}
        actions={
          <div style={{ display: 'flex', gap: 8 }}>
            <button
              type="button"
              className="btn btn-brand btn-elevate btn-icon-sm"
              onClick={openCreateModal}
            >
              <i className="la la-plus"></i> Add Mushak 6.3
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
            <h3 className="kt-portlet__head-title">Mushak 6.3 (VAT Invoice / Challan)</h3>
          </div>
          <div className="kt-portlet__head-toolbar">
            <button
              type="button"
              className="btn btn-clean btn-icon-sm"
              onClick={() => setReloadTrigger(prev => prev + 1)}
            >
              <i className="la la-refresh"></i> Reload
            </button>
          </div>
        </div>

        <div className="kt-portlet__body">
          {/* Filters */}
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
                    placeholder="Search VI No, buyer..."
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
                  <th>Date</th>
                  <th>VI No</th>
                  <th>Company</th>
                  <th>Branch</th>
                  <th>Buyer</th>
                  <th>Buyer BIN/NID</th>
                  <th>VAT Amount</th>
                  <th>Total Amount</th>
                  <th>Payment / CR Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td colSpan="12" className="text-center py-5 text-muted">
                      <div className="spinner-border text-primary" role="status">
                        <span className="sr-only">Loading...</span>
                      </div>
                      <div className="mt-2">Loading Mushak 6.3 data...</div>
                    </td>
                  </tr>
                ) : data.length === 0 ? (
                  <tr>
                    <td colSpan="12" className="text-center py-5 text-muted">
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
                      <td>{item.date || 'N/A'}</td>
                      <td><span className="badge badge-primary">{item.viNo}</span></td>
                      <td>{item.companyName || '—'}</td>
                      <td>{item.branchName || 'N/A'}</td>
                      <td>{item.buyer || 'N/A'}</td>
                      <td><code>{item.buyerBin || 'N/A'}</code></td>
                      <td className="text-right">{Number(item.vatAmount || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                      <td className="text-right font-weight-bold">{Number(item.totalAmount || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                      <td>
                        <span className={`badge ${item.paymentStatus === 'approved' ? 'badge-success' : item.paymentStatus === 'rejected' ? 'badge-danger' : 'badge-warning'}`}>
                          {item.paymentStatus || 'pending'}
                        </span>
                      </td>
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
                          title="Edit 6.3"
                          onClick={() => openEditModal(item)}
                        >
                          <i className="la la-edit"></i>
                        </button>
                        <button
                          type="button"
                          className="btn btn-sm btn-clean btn-icon btn-icon-md text-warning"
                          title="Change Status / CR Approval"
                          onClick={() => openStatusModal(item)}
                        >
                          <i className="la la-check-circle"></i>
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
          <div className="modal-content" style={{ maxWidth: 750, maxHeight: '92vh', overflowY: 'auto', background: '#fff', borderRadius: 8, boxShadow: '0 5px 20px rgba(0,0,0,0.3)' }}>
            <div className="modal-header" style={{ padding: '15px 20px', borderBottom: '1px solid #ebedf2', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h5 style={{ margin: 0, fontWeight: 600 }}>{editingItem ? 'Edit Mushak 6.3' : 'Create Mushak 6.3 (VAT Invoice)'}</h5>
              <button type="button" className="btn btn-sm btn-label-secondary" onClick={() => setIsFormOpen(false)} style={{ border: 'none', background: 'none', fontSize: 20 }}>×</button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body" style={{ padding: '20px' }}>
                <div className="row">
                  {/* Company */}
                  <div className="col-12 col-md-6 mb-3">
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
                  <div className="col-12 col-md-6 mb-3">
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

                  {/* VI No */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>VI No <span className="text-danger">*</span></label>
                    <input
                      type="number"
                      className="form-control"
                      required
                      value={formData.viNo}
                      onChange={(e) => setFormData({ ...formData, viNo: parseInt(e.target.value) || '' })}
                    />
                  </div>

                  {/* Date */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Invoice Date <span className="text-danger">*</span></label>
                    <input
                      type="date"
                      className="form-control"
                      required
                      value={formData.date}
                      onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                    />
                  </div>

                  {/* Buyer Type */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Buyer Type</label>
                    <select
                      className="form-control"
                      value={formData.buyerType}
                      onChange={(e) => setFormData({ ...formData, buyerType: e.target.value })}
                    >
                      <option value="registered">Registered</option>
                      <option value="unregistered">Unregistered</option>
                    </select>
                  </div>

                  {/* Payment Status */}
                  <div className="col-12 col-md-6 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Payment / CR Status</label>
                    <select
                      className="form-control"
                      value={formData.paymentStatus}
                      onChange={(e) => setFormData({ ...formData, paymentStatus: e.target.value })}
                    >
                      <option value="pending">Pending</option>
                      <option value="approved">Approved</option>
                      <option value="rejected">Rejected</option>
                    </select>
                  </div>

                  {/* Comments */}
                  <div className="col-12 col-md-6 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Comments</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Optional comments"
                      value={formData.comments}
                      onChange={(e) => setFormData({ ...formData, comments: e.target.value })}
                    />
                  </div>
                </div>
              </div>
              <div className="modal-footer" style={{ padding: '15px 20px', borderTop: '1px solid #ebedf2', display: 'flex', justifyContent: 'flex-end', gap: 10 }}>
                <button type="button" className="btn btn-secondary" onClick={() => setIsFormOpen(false)}>Cancel</button>
                <button type="submit" className="btn btn-brand">{editingItem ? 'Update Mushak 6.3' : 'Submit'}</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Status / CR Approval Modal */}
      {isStatusModalOpen && statusItem && (
        <div className="modal-overlay" style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', zIndex: 1050, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 15 }}>
          <div className="modal-content" style={{ maxWidth: 500, background: '#fff', borderRadius: 8, boxShadow: '0 5px 20px rgba(0,0,0,0.3)' }}>
            <div className="modal-header" style={{ padding: '15px 20px', borderBottom: '1px solid #ebedf2', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h5 style={{ margin: 0, fontWeight: 600 }}>Update CR / Payment Status - VI #{statusItem.viNo}</h5>
              <button type="button" className="btn btn-sm btn-label-secondary" onClick={() => setIsStatusModalOpen(false)} style={{ border: 'none', background: 'none', fontSize: 20 }}>×</button>
            </div>
            <form onSubmit={handleStatusSubmit}>
              <div className="modal-body" style={{ padding: 20 }}>
                <div className="form-group mb-3">
                  <label style={{ fontWeight: 600, marginBottom: 5 }}>Select Status <span className="text-danger">*</span></label>
                  <select
                    className="form-control"
                    value={statusForm.status}
                    onChange={(e) => setStatusForm({ ...statusForm, status: e.target.value })}
                  >
                    <option value="approved">Approved</option>
                    <option value="pending">Pending</option>
                    <option value="rejected">Rejected</option>
                  </select>
                </div>
                <div className="form-group mb-3">
                  <label style={{ fontWeight: 600, marginBottom: 5 }}>Reason / Remarks</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Reason (if rejecting or approving)"
                    value={statusForm.reason}
                    onChange={(e) => setStatusForm({ ...statusForm, reason: e.target.value })}
                  />
                </div>
              </div>
              <div className="modal-footer" style={{ padding: '15px 20px', borderTop: '1px solid #ebedf2', display: 'flex', justifyContent: 'flex-end', gap: 10 }}>
                <button type="button" className="btn btn-secondary" onClick={() => setIsStatusModalOpen(false)}>Cancel</button>
                <button type="submit" className="btn btn-brand">Update Status</button>
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
              <h5 style={{ margin: 0, fontWeight: 600 }}>Mushak 6.3 Invoice Details</h5>
              <button type="button" className="btn btn-sm btn-label-secondary" onClick={() => setIsViewOpen(false)} style={{ border: 'none', background: 'none', fontSize: 20 }}>×</button>
            </div>
            <div className="modal-body" style={{ padding: 20 }}>
              <table className="table table-bordered">
                <tbody>
                  <tr><th style={{ width: '40%' }}>VI No</th><td><span className="badge badge-primary">{viewItem.viNo}</span></td></tr>
                  <tr><th>Date</th><td>{viewItem.date || '—'}</td></tr>
                  <tr><th>Company / Branch</th><td>{viewItem.companyName || '—'} / {viewItem.branchName || 'N/A'}</td></tr>
                  <tr><th>Buyer Name</th><td>{viewItem.buyer || 'N/A'}</td></tr>
                  <tr><th>Buyer BIN / NID</th><td>{viewItem.buyerBin || 'N/A'}</td></tr>
                  <tr><th>Buyer Address</th><td>{viewItem.buyerAddress || 'N/A'}</td></tr>
                  <tr><th>Buyer Type</th><td>{viewItem.buyerType || 'registered'}</td></tr>
                  <tr><th>VAT Amount</th><td>{Number(viewItem.vatAmount || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td></tr>
                  <tr><th>Total Amount</th><td className="font-weight-bold text-success">{Number(viewItem.totalAmount || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td></tr>
                  <tr><th>Payment Status</th><td><span className="badge badge-secondary">{viewItem.paymentStatus || 'pending'}</span></td></tr>
                  <tr><th>Comments</th><td>{viewItem.comments || '—'}</td></tr>
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
        message="Are you sure you want to delete this Mushak 6.3 invoice?"
        onConfirm={confirmDelete}
        onCancel={() => setIsDeleteOpen(false)}
      />
    </div>
  );
}
