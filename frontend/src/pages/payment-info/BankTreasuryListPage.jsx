import React, { useState } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { bankTreasuryService } from '../../services/paymentInfoService';

export default function BankTreasuryListPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);

  // Form Modal state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    bankCode: '',
    bankName: '',
    branchName: '',
    treasuryDate: '',
    amount: '',
    type: '',
    accountCode: '',
    note: ''
  });

  // Delete Confirm state
  const [itemToDelete, setItemToDelete] = useState(null);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);

  const openCreateModal = () => {
    setEditingItem(null);
    setFormData({
      bankCode: '',
      bankName: 'Sonali Bank Ltd.',
      branchName: 'Principal Branch',
      treasuryDate: new Date().toISOString().split('T')[0],
      amount: '',
      type: 'Treasury Challan',
      accountCode: '1-1133-0010-0311',
      note: ''
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      bankCode: item.bankCode || '',
      bankName: item.bankName || '',
      branchName: item.branchName || '',
      treasuryDate: item.treasuryDate ? String(item.treasuryDate).substring(0, 10) : '',
      amount: item.amount || '',
      type: item.type || '',
      accountCode: item.accountCode || '',
      note: item.note || ''
    });
    setIsFormOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await bankTreasuryService.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'Bank Treasury updated successfully!' });
      } else {
        await bankTreasuryService.create(formData);
        setAlert({ type: 'success', message: 'Bank Treasury created successfully!' });
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
      await bankTreasuryService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Bank Treasury deleted successfully!' });
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
      await bankTreasuryService.downloadPdf();
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
      title: 'Type',
      data: 'type',
      sortable: true,
      render: (row) => (
        <span className="kt-badge kt-badge--inline kt-badge--primary" style={{ fontWeight: 600 }}>
          {row.type || 'Challan'}
        </span>
      )
    },
    {
      title: 'Account Code',
      data: 'accountCode',
      sortable: true,
      render: (row) => <span className="font-monospace text-dark">{row.accountCode || '—'}</span>
    },
    {
      title: 'Treasury Date',
      data: 'treasuryDate',
      sortable: true,
      render: (row) => row.treasuryDate ? String(row.treasuryDate).substring(0, 10) : '—'
    },
    {
      title: 'Amount (BDT)',
      data: 'amount',
      sortable: true,
      align: 'right',
      render: (row) => (
        <span style={{ fontWeight: 700, color: '#0abb87' }}>
          {Number(row.amount || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
        </span>
      )
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
            title="Edit Bank Treasury"
            onClick={() => openEditModal(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Bank Treasury"
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
        title="Bank Treasury"
        breadcrumbs={[
          { label: 'Payment Information' },
          { label: 'Bank Treasury' }
        ]}
        actions={
          <div style={{ display: 'flex', gap: 8 }}>
            <button className="btn btn-outline-danger" onClick={handleDownloadPdf} disabled={isPdfLoading}>
              <i className="bi bi-file-earmark-pdf"></i>
              <span>{isPdfLoading ? 'Exporting...' : 'Export PDF'}</span>
            </button>
            <button className="btn btn-brand" onClick={openCreateModal}>
              <i className="bi bi-plus-lg"></i>
              <span>Add Treasury Record</span>
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
        title="Bank Treasury Records"
        columns={columns}
        fetchData={bankTreasuryService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 540 }}>
            <div className="modal-header">
              <h5>{editingItem ? 'Edit Bank Treasury' : 'Add Bank Treasury'}</h5>
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
                      placeholder="e.g. Sonali Bank Ltd."
                      value={formData.bankName}
                      onChange={(e) => setFormData({ ...formData, bankName: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Branch Name</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. Principal Branch"
                      value={formData.branchName}
                      onChange={(e) => setFormData({ ...formData, branchName: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Bank Code</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. 010"
                      value={formData.bankCode}
                      onChange={(e) => setFormData({ ...formData, bankCode: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Account Code</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. 1-1133-0010-0311"
                      value={formData.accountCode}
                      onChange={(e) => setFormData({ ...formData, accountCode: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Treasury Date</label>
                    <input
                      type="date"
                      className="form-control"
                      value={formData.treasuryDate}
                      onChange={(e) => setFormData({ ...formData, treasuryDate: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Type</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. Treasury Challan"
                      value={formData.type}
                      onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                    />
                  </div>
                </div>

                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Amount (BDT) *</label>
                  <input
                    type="number"
                    step="0.01"
                    className="form-control"
                    required
                    placeholder="0.00"
                    value={formData.amount}
                    onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                  />
                </div>

                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Note / Remarks</label>
                  <textarea
                    className="form-control"
                    rows="2"
                    placeholder="Treasury challan details..."
                    value={formData.note}
                    onChange={(e) => setFormData({ ...formData, note: e.target.value })}
                  />
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setIsFormOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-brand">
                  {editingItem ? 'Update Treasury' : 'Save Treasury'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Modal */}
      <ConfirmModal
        isOpen={isDeleteOpen}
        title="Delete Treasury Record"
        message={`Are you sure you want to delete treasury record for ${itemToDelete?.bankName || itemToDelete?.id}? This action cannot be undone.`}
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
