import React, { useState } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { paymentService } from '../../services/paymentInfoService';

export default function PaymentListPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);

  // Form Modal state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    paymentType: '',
    paymentCategory: '',
    billNo: '',
    paymentDate: '',
    paymentAmount: '',
    note: ''
  });

  // Delete Confirm state
  const [itemToDelete, setItemToDelete] = useState(null);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);

  const openCreateModal = () => {
    setEditingItem(null);
    setFormData({
      paymentType: 'Regular',
      paymentCategory: 'Vendor Payment',
      billNo: '',
      paymentDate: new Date().toISOString().split('T')[0],
      paymentAmount: '',
      note: ''
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      paymentType: item.paymentType || '',
      paymentCategory: item.paymentCategory || '',
      billNo: item.billNo || '',
      paymentDate: item.paymentDate ? String(item.paymentDate).substring(0, 10) : '',
      paymentAmount: item.paymentAmount || '',
      note: item.note || ''
    });
    setIsFormOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await paymentService.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'Payment updated successfully!' });
      } else {
        await paymentService.create(formData);
        setAlert({ type: 'success', message: 'Payment recorded successfully!' });
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
      await paymentService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Payment deleted successfully!' });
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
      await paymentService.downloadPdf();
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
      title: 'Payment Type',
      data: 'paymentType',
      sortable: true,
      render: (row) => (
        <span className="kt-badge kt-badge--inline kt-badge--primary" style={{ fontWeight: 600 }}>
          {row.paymentType || 'Standard'}
        </span>
      )
    },
    {
      title: 'Category',
      data: 'paymentCategory',
      sortable: true,
      render: (row) => <span style={{ fontWeight: 600, color: '#48465b' }}>{row.paymentCategory || '—'}</span>
    },
    {
      title: 'Bill No',
      data: 'billNo',
      sortable: true,
      render: (row) => <span className="text-muted font-monospace">{row.billNo || '—'}</span>
    },
    {
      title: 'Payment Date',
      data: 'paymentDate',
      sortable: true,
      render: (row) => row.paymentDate ? String(row.paymentDate).substring(0, 10) : '—'
    },
    {
      title: 'Amount (BDT)',
      data: 'paymentAmount',
      sortable: true,
      align: 'right',
      render: (row) => (
        <span style={{ fontWeight: 700, color: '#0abb87' }}>
          {row.paymentAmount ? Number(row.paymentAmount).toLocaleString('en-US', { minimumFractionDigits: 2 }) : '0.00'}
        </span>
      )
    },
    {
      title: 'Note / Details',
      data: 'note',
      sortable: false,
      render: (row) => <span className="text-truncate" style={{ maxWidth: 200, display: 'inline-block' }}>{row.note || '—'}</span>
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
            title="Edit Payment"
            onClick={() => openEditModal(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Payment"
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
        title="Payments"
        breadcrumbs={[
          { label: 'Payment Information' },
          { label: 'Payments' }
        ]}
        actions={
          <div style={{ display: 'flex', gap: 8 }}>
            <button className="btn btn-outline-danger" onClick={handleDownloadPdf} disabled={isPdfLoading}>
              <i className="bi bi-file-earmark-pdf"></i>
              <span>{isPdfLoading ? 'Exporting...' : 'Export PDF'}</span>
            </button>
            <button className="btn btn-brand" onClick={openCreateModal}>
              <i className="bi bi-plus-lg"></i>
              <span>New Payment</span>
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
        title="Payment Records"
        columns={columns}
        fetchData={paymentService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 540 }}>
            <div className="modal-header">
              <h5>{editingItem ? 'Edit Payment' : 'New Payment'}</h5>
              <button className="btn btn-sm btn-label-secondary" onClick={() => setIsFormOpen(false)}>×</button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body" style={{ padding: '20px' }}>
                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Payment Type *</label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="e.g. Regular, Advance"
                      value={formData.paymentType}
                      onChange={(e) => setFormData({ ...formData, paymentType: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Category</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. Vendor, Tax, Utility"
                      value={formData.paymentCategory}
                      onChange={(e) => setFormData({ ...formData, paymentCategory: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Bill / Voucher No</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. BILL-2026-001"
                      value={formData.billNo}
                      onChange={(e) => setFormData({ ...formData, billNo: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Payment Date</label>
                    <input
                      type="date"
                      className="form-control"
                      value={formData.paymentDate}
                      onChange={(e) => setFormData({ ...formData, paymentDate: e.target.value })}
                    />
                  </div>
                </div>

                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Payment Amount (BDT) *</label>
                  <input
                    type="number"
                    step="0.01"
                    className="form-control"
                    required
                    placeholder="0.00"
                    value={formData.paymentAmount}
                    onChange={(e) => setFormData({ ...formData, paymentAmount: e.target.value })}
                  />
                </div>

                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Note / Remarks</label>
                  <textarea
                    className="form-control"
                    rows="3"
                    placeholder="Payment description or reference..."
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
                  {editingItem ? 'Update Payment' : 'Save Payment'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Modal */}
      <ConfirmModal
        isOpen={isDeleteOpen}
        title="Delete Payment Record"
        message={`Are you sure you want to delete payment ${itemToDelete?.billNo || itemToDelete?.id}? This action cannot be undone.`}
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
