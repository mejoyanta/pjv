import React, { useState } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { packageService } from '../../services/paymentInfoService';

export default function PackageListPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);

  // Form Modal state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    durationMonths: 12,
    price: '',
    status: 1
  });

  // Delete Confirm state
  const [itemToDelete, setItemToDelete] = useState(null);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);

  const openCreateModal = () => {
    setEditingItem(null);
    setFormData({
      name: '',
      description: '',
      durationMonths: 12,
      price: '',
      status: 1
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      name: item.name || '',
      description: item.description || '',
      durationMonths: item.durationMonths || 12,
      price: item.price || '',
      status: item.status !== undefined ? item.status : 1
    });
    setIsFormOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await packageService.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'Package updated successfully!' });
      } else {
        await packageService.create(formData);
        setAlert({ type: 'success', message: 'Package created successfully!' });
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
      await packageService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Package deleted successfully!' });
      setReloadTrigger(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setIsDeleteOpen(false);
      setItemToDelete(null);
    }
  };

  const handleDownloadPdf = async () => {
    setIsPdfLoading(true);
    try {
      await packageService.downloadPdf();
    } catch (err) {
      setAlert({ type: 'danger', message: 'Failed to download PDF report' });
    } finally {
      setIsPdfLoading(false);
    }
  };

  const columns = [
    {
      title: '#',
      sortable: false,
      width: '60px',
      align: 'center',
      render: (row, idx) => idx
    },
    {
      title: 'Package Name',
      data: 'name',
      sortable: true,
      render: (row) => <span style={{ fontWeight: 600, color: '#48465b' }}>{row.name}</span>
    },
    {
      title: 'Duration (Months)',
      data: 'durationMonths',
      sortable: true,
      align: 'center',
      render: (row) => (
        <span className="kt-badge kt-badge--inline kt-badge--primary" style={{ fontWeight: 600 }}>
          {row.durationMonths || 1} Month{row.durationMonths > 1 ? 's' : ''}
        </span>
      )
    },
    {
      title: 'Price (BDT)',
      data: 'price',
      sortable: true,
      align: 'right',
      render: (row) => (
        <span style={{ fontWeight: 700, color: '#0abb87' }}>
          {Number(row.price || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
        </span>
      )
    },
    {
      title: 'Description',
      data: 'description',
      sortable: false,
      render: (row) => <span className="text-muted text-truncate" style={{ maxWidth: 220, display: 'inline-block' }}>{row.description || '—'}</span>
    },
    {
      title: 'Status',
      data: 'status',
      sortable: true,
      render: (row) => {
        const isActive = row.status === 1 || String(row.status) === '1' || row.status === true;
        return (
          <span className={`kt-badge kt-badge--inline ${isActive ? 'kt-badge--success' : 'kt-badge--danger'}`} style={{ fontWeight: 600 }}>
            {isActive ? 'Active' : 'Inactive'}
          </span>
        );
      }
    },
    {
      title: 'Actions',
      sortable: false,
      align: 'center',
      width: '120px',
      render: (row) => (
        <div style={{ display: 'flex', gap: 6, justifyContent: 'center' }}>
          <button
            className="btn btn-sm btn-label-brand btn-circle btn-icon"
            title="Edit Package"
            onClick={() => openEditModal(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Package"
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
        title="Packages"
        breadcrumbs={[
          { label: 'Payment Information' },
          { label: 'Packages' }
        ]}
        actions={
          <div style={{ display: 'flex', gap: 8 }}>
            <button className="btn btn-outline-danger" onClick={handleDownloadPdf} disabled={isPdfLoading}>
              <i className="bi bi-file-earmark-pdf"></i>
              <span>{isPdfLoading ? 'Exporting...' : 'Export PDF'}</span>
            </button>
            <button className="btn btn-brand" onClick={openCreateModal}>
              <i className="bi bi-plus-lg"></i>
              <span>Add Package</span>
            </button>
          </div>
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
        title="Package Offerings"
        columns={columns}
        fetchData={packageService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 500 }}>
            <div className="modal-header">
              <h5>{editingItem ? 'Edit Package' : 'New Package'}</h5>
              <button className="btn btn-sm btn-label-secondary" onClick={() => setIsFormOpen(false)}>×</button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body" style={{ padding: '20px' }}>
                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Package Name *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    placeholder="e.g. Standard 1 Year Plan"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  />
                </div>

                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Duration (Months) *</label>
                    <input
                      type="number"
                      className="form-control"
                      required
                      min="1"
                      value={formData.durationMonths}
                      onChange={(e) => setFormData({ ...formData, durationMonths: parseInt(e.target.value) || 1 })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Price (BDT) *</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-control"
                      required
                      placeholder="0.00"
                      value={formData.price}
                      onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                    />
                  </div>
                </div>

                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Status</label>
                  <select
                    className="form-control"
                    value={formData.status}
                    onChange={(e) => setFormData({ ...formData, status: parseInt(e.target.value) })}
                  >
                    <option value={1}>Active</option>
                    <option value={0}>Inactive</option>
                  </select>
                </div>

                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Description</label>
                  <textarea
                    className="form-control"
                    rows="3"
                    placeholder="Package perks, user limits..."
                    value={formData.description}
                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  />
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setIsFormOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-brand">
                  {editingItem ? 'Update Package' : 'Save Package'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Modal */}
      <ConfirmModal
        isOpen={isDeleteOpen}
        title="Delete Package"
        message={`Are you sure you want to delete package "${itemToDelete?.name}"? This action cannot be undone.`}
        confirmText="Yes, Delete"
        confirmType="danger"
        onConfirm={confirmDelete}
        onClose={() => {
          setIsDeleteOpen(false);
          setItemToDelete(null);
        }}
      />
    </div>
  );
}
