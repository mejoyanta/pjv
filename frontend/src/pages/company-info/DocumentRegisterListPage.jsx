import React, { useState, useEffect } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { documentRegisterService } from '../../services/companyInfoService';
import { companyService } from '../../services/companyService';

export default function DocumentRegisterListPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [companies, setCompanies] = useState([]);

  // Form Modal state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    ref: '',
    date: '',
    token: '',
    comments: '',
    companyId: ''
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
      name: '',
      ref: '',
      date: new Date().toISOString().split('T')[0],
      token: '',
      comments: '',
      companyId: companies.length > 0 ? companies[0].id : ''
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      name: item.name || '',
      ref: item.ref || '',
      date: item.date || '',
      token: item.token || '',
      comments: item.comments || '',
      companyId: item.companyId || ''
    });
    setIsFormOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await documentRegisterService.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'Document Register updated successfully!' });
      } else {
        await documentRegisterService.create(formData);
        setAlert({ type: 'success', message: 'Document Register created successfully!' });
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
      await documentRegisterService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Document Register deleted successfully!' });
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
      title: 'Document Name',
      data: 'name',
      sortable: true,
      render: (row) => (
        <div>
          <span style={{ fontWeight: 600, color: '#48465b', display: 'block' }}>{row.name || 'Untitled Document'}</span>
          <span style={{ fontSize: 11.5, color: '#959cb6' }}>Slug: {row.slug}</span>
        </div>
      )
    },
    {
      title: 'Ref Number',
      data: 'ref',
      sortable: true,
      render: (row) => row.ref || '—'
    },
    {
      title: 'Date',
      data: 'date',
      sortable: true,
      render: (row) => row.date || '—'
    },
    {
      title: 'Token',
      data: 'token',
      sortable: true,
      render: (row) => row.token || '—'
    },
    {
      title: 'Comments',
      data: 'comments',
      sortable: false,
      render: (row) => row.comments ? (
        <span title={row.comments} style={{ maxWidth: 200, display: 'inline-block', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
          {row.comments}
        </span>
      ) : '—'
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
            title="Edit Document"
            onClick={() => openEditModal(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Document"
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
        title="Document Register"
        breadcrumbs={[
          { label: 'Company Information', link: '/company' },
          { label: 'Document Register' }
        ]}
        actions={
          <button className="btn btn-brand" onClick={openCreateModal}>
            <i className="bi bi-plus-lg"></i>
            <span>Add Document</span>
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
        title="Document Register Records"
        columns={columns}
        fetchData={documentRegisterService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 600 }}>
            <div className="modal-header">
              <h4 className="modal-title">{editingItem ? 'Edit Document Register' : 'Add Document Register'}</h4>
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
                  <label className="form-label">Document Name <span className="text-danger">*</span></label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    placeholder="Enter document name"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  />
                </div>
                <div className="row">
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Ref Number</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. REF-2026-001"
                      value={formData.ref}
                      onChange={(e) => setFormData({ ...formData, ref: e.target.value })}
                    />
                  </div>
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Date</label>
                    <input
                      type="date"
                      className="form-control"
                      value={formData.date}
                      onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                    />
                  </div>
                </div>
                <div className="form-group mb-3">
                  <label className="form-label">Token / Code</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Security or tracking token"
                    value={formData.token}
                    onChange={(e) => setFormData({ ...formData, token: e.target.value })}
                  />
                </div>
                <div className="form-group mb-3">
                  <label className="form-label">Comments</label>
                  <textarea
                    className="form-control"
                    rows={3}
                    placeholder="Enter remarks or comments"
                    value={formData.comments}
                    onChange={(e) => setFormData({ ...formData, comments: e.target.value })}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setIsFormOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-brand">
                  {editingItem ? 'Save Changes' : 'Create Document'}
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
              <h4 className="modal-title">Document Details</h4>
              <button
                onClick={() => setIsViewOpen(false)}
                style={{ background: 'none', border: 'none', fontSize: 22, cursor: 'pointer', color: '#959cb6' }}
              >
                &times;
              </button>
            </div>
            <div className="modal-body" style={{ padding: '20px 25px' }}>
              <div className="mb-3">
                <strong style={{ color: '#646c9a' }}>Document Name:</strong>
                <div style={{ fontSize: 15, fontWeight: 600 }}>{viewItem.name}</div>
              </div>
              <div className="row mb-3">
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Ref No:</strong>
                  <div>{viewItem.ref || '—'}</div>
                </div>
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Date:</strong>
                  <div>{viewItem.date || '—'}</div>
                </div>
              </div>
              <div className="mb-3">
                <strong style={{ color: '#646c9a' }}>Token:</strong>
                <div><code>{viewItem.token || '—'}</code></div>
              </div>
              <div className="mb-3">
                <strong style={{ color: '#646c9a' }}>Comments:</strong>
                <p style={{ marginTop: 4 }}>{viewItem.comments || 'No comments provided.'}</p>
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
        title="Delete Document Register"
        message={`Are you sure you want to delete "${itemToDelete?.name || 'this document'}"?`}
        confirmText="Delete"
        confirmBtnClass="btn-danger"
        onConfirm={confirmDelete}
        onCancel={() => setIsDeleteOpen(false)}
      />
    </div>
  );
}
