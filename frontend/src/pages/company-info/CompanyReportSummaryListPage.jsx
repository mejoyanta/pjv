import React, { useState, useEffect } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { companyReportService } from '../../services/companyInfoService';
import { companyService } from '../../services/companyService';

export default function CompanyReportSummaryListPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [companies, setCompanies] = useState([]);

  // Form Modal state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    companyId: '',
    companyBin: '',
    month: '',
    year: '',
    date: '',
    subDate: ''
  });

  // View Modal state
  const [viewItem, setViewItem] = useState(null);
  const [isViewOpen, setIsViewOpen] = useState(false);

  // Delete Confirm state
  const [itemToDelete, setItemToDelete] = useState(null);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);

  useEffect(() => {
    companyService.loadCompanies({ start: 0, length: 100 })
      .then(res => {
        if (res && res.data) setCompanies(res.data);
      })
      .catch(console.error);
  }, []);

  const openCreateModal = () => {
    setEditingItem(null);
    const now = new Date();
    const currentMonth = now.toLocaleString('default', { month: 'long' });
    const currentYear = now.getFullYear().toString();
    setFormData({
      companyId: companies.length > 0 ? companies[0].id : '',
      companyBin: companies.length > 0 ? (companies[0].bin || '') : '',
      month: currentMonth,
      year: currentYear,
      date: now.toISOString().split('T')[0],
      subDate: now.toISOString().split('T')[0]
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      companyId: item.companyId || '',
      companyBin: item.companyBin || '',
      month: item.month || '',
      year: item.year || '',
      date: item.date || '',
      subDate: item.subDate || ''
    });
    setIsFormOpen(true);
  };

  const handleCompanyChange = (companyId) => {
    const selected = companies.find(c => String(c.id) === String(companyId));
    setFormData({
      ...formData,
      companyId,
      companyBin: selected?.bin || formData.companyBin
    });
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await companyReportService.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'Company Report updated successfully!' });
      } else {
        await companyReportService.create(formData);
        setAlert({ type: 'success', message: 'Company Report created successfully!' });
      }
      setIsFormOpen(false);
      setReloadTrigger(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    }
  };

  const confirmDelete = async () => {
    if (!itemToDelete) return;
    try {
      await companyReportService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Company Report deleted successfully!' });
      setReloadTrigger(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setIsDeleteOpen(false);
      setItemToDelete(null);
    }
  };

  const months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];

  const columns = [
    {
      title: '#',
      sortable: false,
      width: '50px',
      align: 'center',
      render: (row, idx) => idx
    },
    {
      title: 'Company BIN',
      data: 'companyBin',
      sortable: true,
      render: (row) => (
        <div>
          <span style={{ fontWeight: 600, color: '#48465b', display: 'block' }}>{row.companyBin || 'N/A'}</span>
          <span style={{ fontSize: 11.5, color: '#959cb6' }}>Slug: {row.slug}</span>
        </div>
      )
    },
    {
      title: 'Month',
      data: 'month',
      sortable: true,
      render: (row) => <span className="badge badge-info">{row.month || '—'}</span>
    },
    {
      title: 'Year',
      data: 'year',
      sortable: true,
      render: (row) => <strong>{row.year || '—'}</strong>
    },
    {
      title: 'Report Date',
      data: 'date',
      sortable: true,
      render: (row) => row.date || '—'
    },
    {
      title: 'Submission Date',
      data: 'subDate',
      sortable: true,
      render: (row) => row.subDate || '—'
    },
    {
      title: 'Actions',
      sortable: false,
      align: 'center',
      width: '120px',
      render: (row) => (
        <div style={{ display: 'flex', gap: 6, justifyContent: 'center' }}>
          <button
            className="btn btn-sm btn-label-info btn-circle btn-icon"
            title="View Details"
            onClick={() => {
              setViewItem(row);
              setIsViewOpen(true);
            }}
          >
            <i className="bi bi-eye"></i>
          </button>
          <button
            className="btn btn-sm btn-label-brand btn-circle btn-icon"
            title="Edit Report"
            onClick={() => openEditModal(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Report"
            onClick={() => {
              setItemToDelete(row);
              setIsDeleteOpen(true);
            }}
          >
            <i className="bi bi-trash"></i>
          </button>
        </div>
      )
    }
  ];

  return (
    <div>
      <Subheader
        title="Company Report Summary"
        breadcrumbs={[
          { label: 'Company Information', link: '/company' },
          { label: 'Company Report Summary' }
        ]}
        actions={
          <button className="btn btn-brand" onClick={openCreateModal}>
            <i className="bi bi-plus-lg"></i>
            <span>Add Report</span>
          </button>
        }
      />

      {alert && (
        <Alert
          type={alert.type}
          message={alert.message}
          onClose={() => setAlert(null)}
        />
      )}

      <DataTable
        title="Company Report Summary Records"
        columns={columns}
        fetchData={companyReportService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 600 }}>
            <div className="modal-header">
              <h4 className="modal-title">{editingItem ? 'Edit Company Report' : 'Add Company Report'}</h4>
              <button
                onClick={() => setIsFormOpen(false)}
                style={{ background: 'none', border: 'none', fontSize: 22, cursor: 'pointer', color: '#959cb6' }}
              >
                &times;
              </button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body" style={{ padding: '20px 25px' }}>
                <div className="form-group mb-3">
                  <label className="form-label">Company <span className="text-danger">*</span></label>
                  <select
                    className="form-control"
                    required
                    value={formData.companyId}
                    onChange={(e) => handleCompanyChange(e.target.value)}
                  >
                    <option value="">Select Company</option>
                    {companies.map(c => (
                      <option key={c.id} value={c.id}>{c.name} ({c.bin || 'No BIN'})</option>
                    ))}
                  </select>
                </div>
                <div className="form-group mb-3">
                  <label className="form-label">Company BIN</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Enter company BIN"
                    value={formData.companyBin}
                    onChange={(e) => setFormData({ ...formData, companyBin: e.target.value })}
                  />
                </div>
                <div className="row">
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Month <span className="text-danger">*</span></label>
                    <select
                      className="form-control"
                      required
                      value={formData.month}
                      onChange={(e) => setFormData({ ...formData, month: e.target.value })}
                    >
                      <option value="">Select Month</option>
                      {months.map(m => (
                        <option key={m} value={m}>{m}</option>
                      ))}
                    </select>
                  </div>
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Year <span className="text-danger">*</span></label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="e.g. 2026"
                      value={formData.year}
                      onChange={(e) => setFormData({ ...formData, year: e.target.value })}
                    />
                  </div>
                </div>
                <div className="row">
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Report Date</label>
                    <input
                      type="date"
                      className="form-control"
                      value={formData.date}
                      onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                    />
                  </div>
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Submission Date</label>
                    <input
                      type="date"
                      className="form-control"
                      value={formData.subDate}
                      onChange={(e) => setFormData({ ...formData, subDate: e.target.value })}
                    />
                  </div>
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setIsFormOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-brand">
                  {editingItem ? 'Save Changes' : 'Create Report'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* View Modal */}
      {isViewOpen && viewItem && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 500 }}>
            <div className="modal-header">
              <h4 className="modal-title">Company Report Details</h4>
              <button
                onClick={() => setIsViewOpen(false)}
                style={{ background: 'none', border: 'none', fontSize: 22, cursor: 'pointer', color: '#959cb6' }}
              >
                &times;
              </button>
            </div>
            <div className="modal-body" style={{ padding: '20px 25px' }}>
              <div className="mb-3">
                <strong style={{ color: '#646c9a' }}>BIN:</strong>
                <div style={{ fontSize: 16, fontWeight: 600 }}>{viewItem.companyBin || 'N/A'}</div>
              </div>
              <div className="row mb-3">
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Month:</strong>
                  <div>{viewItem.month || '—'}</div>
                </div>
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Year:</strong>
                  <div>{viewItem.year || '—'}</div>
                </div>
              </div>
              <div className="row mb-3">
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Report Date:</strong>
                  <div>{viewItem.date || '—'}</div>
                </div>
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Submission Date:</strong>
                  <div>{viewItem.subDate || '—'}</div>
                </div>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setIsViewOpen(false)}>Close</button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Confirmation */}
      <ConfirmModal
        isOpen={isDeleteOpen}
        title="Delete Company Report"
        message={`Are you sure you want to delete report for ${viewItem?.month || ''} ${viewItem?.year || ''}?`}
        confirmText="Delete"
        confirmBtnClass="btn-danger"
        onConfirm={confirmDelete}
        onCancel={() => setIsDeleteOpen(false)}
      />
    </div>
  );
}
