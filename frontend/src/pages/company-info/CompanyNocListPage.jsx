import React, { useState, useEffect } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { companyNocService } from '../../services/companyInfoService';
import { companyService } from '../../services/companyService';

export default function CompanyNocListPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [companies, setCompanies] = useState([]);

  // Form Modal state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    companyId: '',
    nocNo: '',
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
    setFormData({
      companyId: companies.length > 0 ? companies[0].id : '',
      nocNo: '',
      date: new Date().toISOString().split('T')[0]
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      companyId: item.companyId || '',
      nocNo: item.nocNo || '',
      date: item.date || ''
    });
    setIsFormOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await companyNocService.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'Company NOC updated successfully!' });
      } else {
        await companyNocService.create(formData);
        setAlert({ type: 'success', message: 'Company NOC created successfully!' });
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
      await companyNocService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Company NOC deleted successfully!' });
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
      title: 'NOC Number',
      data: 'nocNo',
      sortable: true,
      render: (row) => (
        <div>
          <span style={{ fontWeight: 600, color: '#48465b', display: 'block' }}>{row.nocNo || '—'}</span>
          <span style={{ fontSize: 11.5, color: '#959cb6' }}>Slug: {row.slug}</span>
        </div>
      )
    },
    {
      title: 'Issue Date',
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
            title="Edit NOC"
            onClick={() => openEditModal(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete NOC"
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
        title="Company NOC"
        breadcrumbs={[
          { label: 'Company Information', link: '/company' },
          { label: 'Company NOC' }
        ]}
        actions={
          <button className="btn btn-brand" onClick={openCreateModal}>
            <i className="bi bi-plus-lg"></i>
            <span>Generate NOC</span>
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
        title="Company NOC Certificates"
        columns={columns}
        fetchData={companyNocService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 500 }}>
            <div className="modal-header">
              <h4 className="modal-title">{editingItem ? 'Edit Company NOC' : 'Generate Company NOC'}</h4>
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
                    onChange={(e) => setFormData({ ...formData, companyId: e.target.value })}
                  >
                    <option value="">Select Company</option>
                    {companies.map(c => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>

                <div className="form-group mb-3">
                  <label className="form-label">NOC Number <span className="text-danger">*</span></label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    placeholder="e.g. NOC-2026-0089"
                    value={formData.nocNo}
                    onChange={(e) => setFormData({ ...formData, nocNo: e.target.value })}
                  />
                </div>

                <div className="form-group mb-3">
                  <label className="form-label">Issue Date</label>
                  <input
                    type="date"
                    className="form-control"
                    value={formData.date}
                    onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setIsFormOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-brand">
                  {editingItem ? 'Save Changes' : 'Generate NOC'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* View Modal */}
      {isViewOpen && viewItem && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 450 }}>
            <div className="modal-header">
              <h4 className="modal-title">NOC Certificate Details</h4>
              <button
                onClick={() => setIsViewOpen(false)}
                style={{ background: 'none', border: 'none', fontSize: 22, cursor: 'pointer', color: '#959cb6' }}
              >
                &times;
              </button>
            </div>
            <div className="modal-body" style={{ padding: '20px 25px' }}>
              <div className="mb-3">
                <strong style={{ color: '#646c9a' }}>NOC Certificate No:</strong>
                <div style={{ fontSize: 16, fontWeight: 600 }}>{viewItem.nocNo}</div>
              </div>
              <div className="mb-3">
                <strong style={{ color: '#646c9a' }}>Issue Date:</strong>
                <div>{viewItem.date || '—'}</div>
              </div>
              <div className="mb-3">
                <strong style={{ color: '#646c9a' }}>Slug:</strong>
                <div><code>{viewItem.slug || '—'}</code></div>
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
        title="Delete NOC Certificate"
        message={`Are you sure you want to delete NOC Certificate "${itemToDelete?.nocNo || ''}"?`}
        confirmText="Delete"
        confirmBtnClass="btn-danger"
        onConfirm={confirmDelete}
        onCancel={() => setIsDeleteOpen(false)}
      />
    </div>
  );
}
