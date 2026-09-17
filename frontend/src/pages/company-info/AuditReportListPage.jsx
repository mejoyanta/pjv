import React, { useState, useEffect } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { auditReportService } from '../../services/companyInfoService';
import { companyService } from '../../services/companyService';

export default function AuditReportListPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [companies, setCompanies] = useState([]);

  // Form Modal state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    companyId: '',
    companyName: '',
    companyBin: '',
    companyAddress: '',
    ownerName: '',
    ownerMobile: '',
    vat: '15',
    date: ''
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
    const firstComp = companies.length > 0 ? companies[0] : null;
    setFormData({
      companyId: firstComp ? firstComp.id : '',
      companyName: firstComp ? firstComp.name : '',
      companyBin: firstComp ? (firstComp.bin || '') : '',
      companyAddress: firstComp ? (firstComp.address || '') : '',
      ownerName: firstComp ? (firstComp.ownerName || '') : '',
      ownerMobile: firstComp ? (firstComp.ownerPhone || '') : '',
      vat: '15',
      date: new Date().toISOString().split('T')[0]
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      companyId: item.companyId || '',
      companyName: item.companyName || '',
      companyBin: item.companyBin || '',
      companyAddress: item.companyAddress || '',
      ownerName: item.ownerName || '',
      ownerMobile: item.ownerMobile || '',
      vat: item.vat || '15',
      date: item.date || ''
    });
    setIsFormOpen(true);
  };

  const handleCompanySelect = (companyId) => {
    const selected = companies.find(c => String(c.id) === String(companyId));
    setFormData({
      ...formData,
      companyId,
      companyName: selected?.name || formData.companyName,
      companyBin: selected?.bin || formData.companyBin,
      companyAddress: selected?.address || formData.companyAddress,
      ownerName: selected?.ownerName || formData.ownerName,
      ownerMobile: selected?.ownerPhone || formData.ownerMobile
    });
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await auditReportService.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'Audit Report updated successfully!' });
      } else {
        await auditReportService.create(formData);
        setAlert({ type: 'success', message: 'Audit Report created successfully!' });
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
      await auditReportService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Audit Report deleted successfully!' });
      setReloadTrigger(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setIsDeleteOpen(false);
      setItemToDelete(null);
    }
  };

  const columns = [
    {
      title: '#',
      sortable: false,
      width: '50px',
      align: 'center',
      render: (row, idx) => idx
    },
    {
      title: 'Company',
      data: 'companyName',
      sortable: true,
      render: (row) => (
        <div>
          <span style={{ fontWeight: 600, color: '#48465b', display: 'block' }}>{row.companyName || '—'}</span>
          <span style={{ fontSize: 11.5, color: '#959cb6' }}>BIN: {row.companyBin || 'N/A'}</span>
        </div>
      )
    },
    {
      title: 'Owner',
      data: 'ownerName',
      sortable: true,
      render: (row) => (
        <div>
          <span>{row.ownerName || '—'}</span>
          {row.ownerMobile && <span style={{ fontSize: 11.5, color: '#959cb6', display: 'block' }}>{row.ownerMobile}</span>}
        </div>
      )
    },
    {
      title: 'VAT (%)',
      data: 'vat',
      sortable: false,
      align: 'center',
      render: (row) => <span className="badge badge-info">{row.vat ? `${row.vat}%` : '15%'}</span>
    },
    {
      title: 'Audit Date',
      data: 'date',
      sortable: true,
      render: (row) => row.date || '—'
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
            title="Edit Audit Report"
            onClick={() => openEditModal(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Audit Report"
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
        title="Audit Report (Salf)"
        breadcrumbs={[
          { label: 'Company Information', link: '/company' },
          { label: 'Audit Report (Salf)' }
        ]}
        actions={
          <button className="btn btn-brand" onClick={openCreateModal}>
            <i className="bi bi-plus-lg"></i>
            <span>Add Audit Report</span>
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
        title="Audit Report Records"
        columns={columns}
        fetchData={auditReportService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 650 }}>
            <div className="modal-header">
              <h4 className="modal-title">{editingItem ? 'Edit Audit Report' : 'Add Audit Report'}</h4>
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
                  <label className="form-label">Select Company <span className="text-danger">*</span></label>
                  <select
                    className="form-control"
                    required
                    value={formData.companyId}
                    onChange={(e) => handleCompanySelect(e.target.value)}
                  >
                    <option value="">Select Company</option>
                    {companies.map(c => (
                      <option key={c.id} value={c.id}>{c.name} ({c.bin || 'No BIN'})</option>
                    ))}
                  </select>
                </div>

                <div className="row">
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Company Name</label>
                    <input
                      type="text"
                      className="form-control"
                      value={formData.companyName}
                      onChange={(e) => setFormData({ ...formData, companyName: e.target.value })}
                    />
                  </div>
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Company BIN</label>
                    <input
                      type="text"
                      className="form-control"
                      value={formData.companyBin}
                      onChange={(e) => setFormData({ ...formData, companyBin: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Owner Name</label>
                    <input
                      type="text"
                      className="form-control"
                      value={formData.ownerName}
                      onChange={(e) => setFormData({ ...formData, ownerName: e.target.value })}
                    />
                  </div>
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Owner Mobile</label>
                    <input
                      type="text"
                      className="form-control"
                      value={formData.ownerMobile}
                      onChange={(e) => setFormData({ ...formData, ownerMobile: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Audit Date</label>
                    <input
                      type="date"
                      className="form-control"
                      value={formData.date}
                      onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                    />
                  </div>
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">VAT (%)</label>
                    <input
                      type="text"
                      className="form-control"
                      value={formData.vat}
                      onChange={(e) => setFormData({ ...formData, vat: e.target.value })}
                    />
                  </div>
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setIsFormOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-brand">
                  {editingItem ? 'Save Changes' : 'Create Audit'}
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
              <h4 className="modal-title">Audit Report Details</h4>
              <button
                onClick={() => setIsViewOpen(false)}
                style={{ background: 'none', border: 'none', fontSize: 22, cursor: 'pointer', color: '#959cb6' }}
              >
                &times;
              </button>
            </div>
            <div className="modal-body" style={{ padding: '20px 25px' }}>
              <div className="mb-3">
                <strong style={{ color: '#646c9a' }}>Company:</strong>
                <div style={{ fontSize: 16, fontWeight: 600 }}>{viewItem.companyName || '—'}</div>
                <div style={{ fontSize: 12, color: '#959cb6' }}>BIN: {viewItem.companyBin || '—'}</div>
              </div>
              <div className="row mb-3">
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Owner:</strong>
                  <div>{viewItem.ownerName || '—'}</div>
                </div>
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Phone:</strong>
                  <div>{viewItem.ownerMobile || '—'}</div>
                </div>
              </div>
              <div className="row mb-3">
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Audit Date:</strong>
                  <div>{viewItem.date || '—'}</div>
                </div>
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>VAT:</strong>
                  <div>{viewItem.vat || '15'}%</div>
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
        title="Delete Audit Report"
        message={`Are you sure you want to delete Audit Report for "${itemToDelete?.companyName || ''}"?`}
        confirmText="Delete"
        confirmBtnClass="btn-danger"
        onConfirm={confirmDelete}
        onCancel={() => setIsDeleteOpen(false)}
      />
    </div>
  );
}
