import React, { useState } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { bankTransactionsService } from '../../services/paymentInfoService';

export default function BankTransactionsListPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);

  // Form Modal state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    bankAccount: '',
    transactionType: '',
    transactionDate: '',
    amount: '',
    note: ''
  });

  // Delete Confirm state
  const [itemToDelete, setItemToDelete] = useState(null);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);

  const openCreateModal = () => {
    setEditingItem(null);
    setFormData({
      bankAccount: '',
      transactionType: 'Deposit',
      transactionDate: new Date().toISOString().split('T')[0],
      amount: '',
      note: ''
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      bankAccount: item.bankAccount || '',
      transactionType: item.transactionType || '',
      transactionDate: item.transactionDate ? String(item.transactionDate).substring(0, 10) : '',
      amount: item.amount || '',
      note: item.note || ''
    });
    setIsFormOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await bankTransactionsService.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'Bank Transaction updated successfully!' });
      } else {
        await bankTransactionsService.create(formData);
        setAlert({ type: 'success', message: 'Bank Transaction recorded successfully!' });
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
      await bankTransactionsService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Bank Transaction deleted successfully!' });
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
      await bankTransactionsService.downloadPdf();
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
      title: 'Bank Account',
      data: 'bankAccount',
      sortable: true,
      render: (row) => (
        <span style={{ fontWeight: 600, color: '#48465b' }}>
          {row.bankAccount || '—'}
        </span>
      )
    },
    {
      title: 'Type',
      data: 'transactionType',
      sortable: true,
      render: (row) => {
        const isCredit = (row.transactionType || '').toLowerCase().includes('deposit') || (row.transactionType || '').toLowerCase().includes('credit');
        return (
          <span className={`kt-badge kt-badge--inline ${isCredit ? 'kt-badge--success' : 'kt-badge--danger'}`} style={{ fontWeight: 600 }}>
            {row.transactionType || 'Transaction'}
          </span>
        );
      }
    },
    {
      title: 'Date',
      data: 'transactionDate',
      sortable: true,
      render: (row) => row.transactionDate ? String(row.transactionDate).substring(0, 10) : '—'
    },
    {
      title: 'Amount (BDT)',
      data: 'amount',
      sortable: true,
      align: 'right',
      render: (row) => (
        <span style={{ fontWeight: 700, color: '#111' }}>
          {Number(row.amount || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
        </span>
      )
    },
    {
      title: 'Note / Details',
      data: 'note',
      sortable: false,
      render: (row) => <span className="text-truncate" style={{ maxWidth: 220, display: 'inline-block' }}>{row.note || '—'}</span>
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
            title="Edit Transaction"
            onClick={() => openEditModal(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Transaction"
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
        title="Bank Transactions"
        breadcrumbs={[
          { label: 'Payment Information' },
          { label: 'Bank Transactions' }
        ]}
        actions={
          <div style={{ display: 'flex', gap: 8 }}>
            <button className="btn btn-outline-danger" onClick={handleDownloadPdf} disabled={isPdfLoading}>
              <i className="bi bi-file-earmark-pdf"></i>
              <span>{isPdfLoading ? 'Exporting...' : 'Export PDF'}</span>
            </button>
            <button className="btn btn-brand" onClick={openCreateModal}>
              <i className="bi bi-plus-lg"></i>
              <span>Add Transaction</span>
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
        title="Bank Transaction Records"
        columns={columns}
        fetchData={bankTransactionsService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 500 }}>
            <div className="modal-header">
              <h5>{editingItem ? 'Edit Transaction' : 'Record Bank Transaction'}</h5>
              <button className="btn btn-sm btn-label-secondary" onClick={() => setIsFormOpen(false)}>×</button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body" style={{ padding: '20px' }}>
                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Bank Account *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    placeholder="e.g. Sonali Bank - A/C 0123456789"
                    value={formData.bankAccount}
                    onChange={(e) => setFormData({ ...formData, bankAccount: e.target.value })}
                  />
                </div>

                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Transaction Type</label>
                    <select
                      className="form-control"
                      value={formData.transactionType}
                      onChange={(e) => setFormData({ ...formData, transactionType: e.target.value })}
                    >
                      <option value="Deposit">Deposit</option>
                      <option value="Withdrawal">Withdrawal</option>
                      <option value="Transfer">Transfer</option>
                      <option value="Bank Charge">Bank Charge</option>
                    </select>
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Transaction Date</label>
                    <input
                      type="date"
                      className="form-control"
                      value={formData.transactionDate}
                      onChange={(e) => setFormData({ ...formData, transactionDate: e.target.value })}
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
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Note / Reference</label>
                  <textarea
                    className="form-control"
                    rows="2"
                    placeholder="Transaction reference or cheque no..."
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
                  {editingItem ? 'Update Transaction' : 'Save Transaction'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Modal */}
      <ConfirmModal
        isOpen={isDeleteOpen}
        title="Delete Transaction"
        message={`Are you sure you want to delete this bank transaction? This action cannot be undone.`}
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
