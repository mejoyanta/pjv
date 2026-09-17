import React, { useState } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { bankInfoDetailService } from '../../services/paymentInfoService';

export default function BankInfoDetailPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);

  // Form Modal state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    bankName: '',
    branchName: '',
    accountName: '',
    accountNumber: '',
    swiftCode: '',
    routingNo: ''
  });

  // Delete Confirm state
  const [itemToDelete, setItemToDelete] = useState(null);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);

  const openCreateModal = () => {
    setEditingItem(null);
    setFormData({
      bankName: '',
      branchName: '',
      accountName: '',
      accountNumber: '',
      swiftCode: '',
      routingNo: ''
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      bankName: item.bankName || '',
      branchName: item.branchName || '',
      accountName: item.accountName || '',
      accountNumber: item.accountNumber || '',
      swiftCode: item.swiftCode || '',
      routingNo: item.routingNo || ''
    });
    setIsFormOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await bankInfoDetailService.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'Bank Account Info updated successfully!' });
      } else {
        await bankInfoDetailService.create(formData);
        setAlert({ type: 'success', message: 'Bank Account Info created successfully!' });
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
      await bankInfoDetailService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Bank Account Info deleted successfully!' });
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
      await bankInfoDetailService.downloadPdf();
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
      title: 'Bank & Branch',
      data: 'bankName',
      sortable: true,
      render: (row) => (
        <div>
          <div style={{ fontWeight: 600, color: '#48465b' }}>{row.bankName || '—'}</div>
          <small className="text-muted">{row.branchName || '—'}</small>
        </div>
      )
    },
    {
      title: 'Account Name',
      data: 'accountName',
      sortable: true,
      render: (row) => <span style={{ fontWeight: 600 }}>{row.accountName || '—'}</span>
    },
    {
      title: 'Account Number',
      data: 'accountNumber',
      sortable: true,
      render: (row) => (
        <span className="kt-badge kt-badge--inline kt-badge--brand font-monospace" style={{ fontWeight: 600 }}>
          {row.accountNumber || '—'}
        </span>
      )
    },
    {
      title: 'Routing No',
      data: 'routingNo',
      sortable: true,
      render: (row) => <span className="font-monospace text-muted">{row.routingNo || '—'}</span>
    },
    {
      title: 'Swift Code',
      data: 'swiftCode',
      sortable: true,
      render: (row) => <span className="font-monospace text-muted">{row.swiftCode || '—'}</span>
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
            title="Edit Bank Account"
            onClick={() => openEditModal(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Bank Account"
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
        title="Bank Account Info"
        breadcrumbs={[
          { label: 'Payment Information' },
          { label: 'Bank Account Info' }
        ]}
        actions={
          <div style={{ display: 'flex', gap: 8 }}>
            <button className="btn btn-outline-danger" onClick={handleDownloadPdf} disabled={isPdfLoading}>
              <i className="bi bi-file-earmark-pdf"></i>
              <span>{isPdfLoading ? 'Exporting...' : 'Export PDF'}</span>
            </button>
            <button className="btn btn-brand" onClick={openCreateModal}>
              <i className="bi bi-plus-lg"></i>
              <span>Add Account</span>
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
        title="Company Bank Accounts"
        columns={columns}
        fetchData={bankInfoDetailService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 540 }}>
            <div className="modal-header">
              <h5>{editingItem ? 'Edit Bank Account Info' : 'New Bank Account Info'}</h5>
              <button className="btn btn-sm btn-label-secondary" onClick={() => setIsFormOpen(false)}>×</button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body" style={{ padding: '20px' }}>
                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Bank Name *</label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="e.g. Dutch Bangla Bank"
                      value={formData.bankName}
                      onChange={(e) => setFormData({ ...formData, bankName: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Branch Name</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. Gulshan Branch"
                      value={formData.branchName}
                      onChange={(e) => setFormData({ ...formData, branchName: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Account Name *</label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="e.g. ABC Trading Co."
                      value={formData.accountName}
                      onChange={(e) => setFormData({ ...formData, accountName: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Account Number *</label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="e.g. 104.120.98765"
                      value={formData.accountNumber}
                      onChange={(e) => setFormData({ ...formData, accountNumber: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Routing No</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. 090271234"
                      value={formData.routingNo}
                      onChange={(e) => setFormData({ ...formData, routingNo: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Swift Code</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. DBBLBDDH"
                      value={formData.swiftCode}
                      onChange={(e) => setFormData({ ...formData, swiftCode: e.target.value })}
                    />
                  </div>
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setIsFormOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-brand">
                  {editingItem ? 'Update Account' : 'Save Account'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Modal */}
      <ConfirmModal
        isOpen={isDeleteOpen}
        title="Delete Bank Account Info"
        message={`Are you sure you want to delete bank account ${itemToDelete?.accountNumber || itemToDelete?.id}? This action cannot be undone.`}
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
