import React, { useState, useEffect } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { prioritySupplierService } from '../../services/basicConfigService';
import { companyService } from '../../services/companyService';

export default function PrioritySupplierListPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);
  const [companies, setCompanies] = useState([]);

  // Form Modal state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    address: '',
    binTin: '',
    mobileNo: '',
    email: '',
    bankName: '',
    acNo: '',
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
      address: '',
      binTin: '',
      mobileNo: '',
      email: '',
      bankName: '',
      acNo: '',
      companyId: companies.length > 0 ? companies[0].id : ''
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      name: item.name || '',
      address: item.address || '',
      binTin: item.binTin || '',
      mobileNo: item.mobileNo || '',
      email: item.email || '',
      bankName: item.bankName || '',
      acNo: item.acNo || '',
      companyId: item.companyId || (companies.length > 0 ? companies[0].id : '')
    });
    setIsFormOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await prioritySupplierService.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'Priority Supplier updated successfully!' });
      } else {
        await prioritySupplierService.create(formData);
        setAlert({ type: 'success', message: 'Priority Supplier created successfully!' });
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
      await prioritySupplierService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Priority Supplier deleted successfully!' });
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
      await prioritySupplierService.downloadPdf();
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
      width: '50px',
      align: 'center',
      render: (row, idx) => idx
    },
    {
      title: 'Supplier Name',
      data: 'name',
      sortable: true,
      render: (row) => <span style={{ fontWeight: 600, color: '#48465b' }}>{row.name}</span>
    },
    {
      title: 'BIN / TIN',
      data: 'binTin',
      sortable: true,
      render: (row) => row.binTin || '—'
    },
    {
      title: 'Mobile',
      data: 'mobileNo',
      sortable: true,
      render: (row) => row.mobileNo || '—'
    },
    {
      title: 'Bank & A/C',
      sortable: false,
      render: (row) => (
        <span>{row.bankName || '—'} {row.acNo ? `(${row.acNo})` : ''}</span>
      )
    },
    {
      title: 'Company',
      sortable: false,
      render: (row) => row.company ? row.company.name : '—'
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
            title="Edit Priority Supplier"
            onClick={() => openEditModal(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Priority Supplier"
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
        title="Priority Supplier"
        breadcrumbs={[
          { label: 'Basic Configuration' },
          { label: 'Priority Supplier' }
        ]}
        actions={
          <div style={{ display: 'flex', gap: 8 }}>
            <button className="btn btn-outline-danger" onClick={handleDownloadPdf} disabled={isPdfLoading}>
              <i className="bi bi-file-earmark-pdf"></i>
              <span>{isPdfLoading ? 'Exporting...' : 'Export PDF'}</span>
            </button>
            <button className="btn btn-brand" onClick={openCreateModal}>
              <i className="bi bi-plus-lg"></i>
              <span>Add Priority Supplier</span>
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
        title="Priority Supplier Records"
        columns={columns}
        fetchData={prioritySupplierService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 600 }}>
            <div className="modal-header">
              <h5>{editingItem ? 'Edit Priority Supplier' : 'Add New Priority Supplier'}</h5>
              <button className="btn btn-sm btn-label-secondary" onClick={() => setIsFormOpen(false)}>×</button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body" style={{ padding: '20px' }}>
                <div className="row" style={{ display: 'flex', gap: 15, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Supplier Name *</label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="Supplier / Company Name"
                      value={formData.name}
                      onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>BIN / TIN</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="BIN / TIN number"
                      value={formData.binTin}
                      onChange={(e) => setFormData({ ...formData, binTin: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row" style={{ display: 'flex', gap: 15, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Mobile Number</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. 01711223344"
                      value={formData.mobileNo}
                      onChange={(e) => setFormData({ ...formData, mobileNo: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Email</label>
                    <input
                      type="email"
                      className="form-control"
                      placeholder="e.g. supplier@example.com"
                      value={formData.email}
                      onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row" style={{ display: 'flex', gap: 15, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Bank Name</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. Dutch Bangla Bank"
                      value={formData.bankName}
                      onChange={(e) => setFormData({ ...formData, bankName: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Account No</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Account Number"
                      value={formData.acNo}
                      onChange={(e) => setFormData({ ...formData, acNo: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row" style={{ display: 'flex', gap: 15, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Address</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Street / Office Address"
                      value={formData.address}
                      onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Company *</label>
                    <select
                      className="form-control"
                      required
                      value={formData.companyId}
                      onChange={(e) => setFormData({ ...formData, companyId: e.target.value })}
                    >
                      <option value="">-- Select Company --</option>
                      {companies.map(c => (
                        <option key={c.id} value={c.id}>{c.name}</option>
                      ))}
                    </select>
                  </div>
                </div>
              </div>
              <div className="modal-footer" style={{ display: 'flex', justifyContent: 'flex-end', gap: 10 }}>
                <button type="button" className="btn btn-secondary" onClick={() => setIsFormOpen(false)}>Cancel</button>
                <button type="submit" className="btn btn-brand">{editingItem ? 'Update' : 'Create'}</button>
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
              <h5>Priority Supplier Details</h5>
              <button className="btn btn-sm btn-label-secondary" onClick={() => setIsViewOpen(false)}>×</button>
            </div>
            <div className="modal-body" style={{ padding: 20 }}>
              <table className="table table-bordered">
                <tbody>
                  <tr><th style={{ width: '35%' }}>Name</th><td>{viewItem.name}</td></tr>
                  <tr><th>BIN / TIN</th><td>{viewItem.binTin || '—'}</td></tr>
                  <tr><th>Mobile</th><td>{viewItem.mobileNo || '—'}</td></tr>
                  <tr><th>Email</th><td>{viewItem.email || '—'}</td></tr>
                  <tr><th>Bank Name</th><td>{viewItem.bankName || '—'}</td></tr>
                  <tr><th>A/C No</th><td>{viewItem.acNo || '—'}</td></tr>
                  <tr><th>Address</th><td>{viewItem.address || '—'}</td></tr>
                  <tr><th>Company</th><td>{viewItem.company ? viewItem.company.name : '—'}</td></tr>
                  <tr><th>Created Date</th><td>{viewItem.createdAt ? new Date(viewItem.createdAt).toLocaleString() : '—'}</td></tr>
                </tbody>
              </table>
            </div>
            <div className="modal-footer" style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <button className="btn btn-secondary" onClick={() => setIsViewOpen(false)}>Close</button>
            </div>
          </div>
        </div>
      )}

      <ConfirmModal
        isOpen={isDeleteOpen}
        title="Confirm Deletion"
        message={`Are you sure you want to delete priority supplier "${itemToDelete?.name}"?`}
        onConfirm={confirmDelete}
        onCancel={() => setIsDeleteOpen(false)}
      />
    </div>
  );
}
