import React, { useState } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { rentVatService } from '../../services/paymentInfoService';

export default function RentVatListPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);

  // Form Modal state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    invoiceNo: '',
    rentMonth: '',
    rentAmount: '',
    vatAmount: '',
    payableAmount: '',
    status: 'Pending',
    note: ''
  });

  // Delete Confirm state
  const [itemToDelete, setItemToDelete] = useState(null);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);

  const openCreateModal = () => {
    setEditingItem(null);
    setFormData({
      invoiceNo: '',
      rentMonth: new Date().toISOString().substring(0, 7),
      rentAmount: '',
      vatAmount: '',
      payableAmount: '',
      status: 'Paid',
      note: ''
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      invoiceNo: item.invoiceNo || '',
      rentMonth: item.rentMonth || '',
      rentAmount: item.rentAmount || '',
      vatAmount: item.vatAmount || '',
      payableAmount: item.payableAmount || '',
      status: item.status || 'Paid',
      note: item.note || ''
    });
    setIsFormOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await rentVatService.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'Increasing Adjustment updated successfully!' });
      } else {
        await rentVatService.create(formData);
        setAlert({ type: 'success', message: 'Increasing Adjustment created successfully!' });
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
      await rentVatService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Record deleted successfully!' });
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
      await rentVatService.downloadPdf();
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
      title: 'Invoice / Ref No',
      data: 'invoiceNo',
      sortable: true,
      render: (row) => (
        <span className="kt-badge kt-badge--inline kt-badge--primary font-monospace" style={{ fontWeight: 600 }}>
          {row.invoiceNo || '—'}
        </span>
      )
    },
    {
      title: 'Adjustment Month',
      data: 'rentMonth',
      sortable: true,
      render: (row) => <span style={{ fontWeight: 600, color: '#48465b' }}>{row.rentMonth || '—'}</span>
    },
    {
      title: 'Base Amount',
      data: 'rentAmount',
      sortable: true,
      align: 'right',
      render: (row) => Number(row.rentAmount || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })
    },
    {
      title: 'VAT Amount',
      data: 'vatAmount',
      sortable: true,
      align: 'right',
      render: (row) => (
        <span style={{ fontWeight: 600, color: '#5867dd' }}>
          {Number(row.vatAmount || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
        </span>
      )
    },
    {
      title: 'Payable Amount',
      data: 'payableAmount',
      sortable: true,
      align: 'right',
      render: (row) => (
        <span style={{ fontWeight: 700, color: '#0abb87' }}>
          {Number(row.payableAmount || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
        </span>
      )
    },
    {
      title: 'Status',
      data: 'status',
      sortable: true,
      render: (row) => {
        const isPaid = (row.status || '').toLowerCase() === 'paid';
        return (
          <span className={`kt-badge kt-badge--inline ${isPaid ? 'kt-badge--success' : 'kt-badge--warning'}`} style={{ fontWeight: 600 }}>
            {row.status || 'Active'}
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
            title="Edit Adjustment"
            onClick={() => openEditModal(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Record"
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
        title="Any Other Increasing Adjustments"
        breadcrumbs={[
          { label: 'Payment Information' },
          { label: 'Any Other Increasing Adjustments' }
        ]}
        actions={
          <div style={{ display: 'flex', gap: 8 }}>
            <button className="btn btn-outline-danger" onClick={handleDownloadPdf} disabled={isPdfLoading}>
              <i className="bi bi-file-earmark-pdf"></i>
              <span>{isPdfLoading ? 'Exporting...' : 'Export PDF'}</span>
            </button>
            <button className="btn btn-brand" onClick={openCreateModal}>
              <i className="bi bi-plus-lg"></i>
              <span>Add Increasing Adjustment</span>
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
        title="Increasing Adjustment Records"
        columns={columns}
        fetchData={rentVatService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 540 }}>
            <div className="modal-header">
              <h5>{editingItem ? 'Edit Increasing Adjustment' : 'New Increasing Adjustment'}</h5>
              <button className="btn btn-sm btn-label-secondary" onClick={() => setIsFormOpen(false)}>×</button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body" style={{ padding: '20px' }}>
                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Invoice / Ref No *</label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="e.g. INC-ADJ-001"
                      value={formData.invoiceNo}
                      onChange={(e) => setFormData({ ...formData, invoiceNo: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Adjustment Month</label>
                    <input
                      type="month"
                      className="form-control"
                      value={formData.rentMonth}
                      onChange={(e) => setFormData({ ...formData, rentMonth: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Base Amount</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-control"
                      placeholder="0.00"
                      value={formData.rentAmount}
                      onChange={(e) => setFormData({ ...formData, rentAmount: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>VAT Amount *</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-control"
                      required
                      placeholder="0.00"
                      value={formData.vatAmount}
                      onChange={(e) => setFormData({ ...formData, vatAmount: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Payable Amount</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-control"
                      placeholder="0.00"
                      value={formData.payableAmount}
                      onChange={(e) => setFormData({ ...formData, payableAmount: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Status</label>
                    <select
                      className="form-control"
                      value={formData.status}
                      onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                    >
                      <option value="Paid">Paid</option>
                      <option value="Pending">Pending</option>
                      <option value="Approved">Approved</option>
                    </select>
                  </div>
                </div>

                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Note / Remarks</label>
                  <textarea
                    className="form-control"
                    rows="2"
                    placeholder="Adjustment details..."
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
                  {editingItem ? 'Update Record' : 'Save Record'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Modal */}
      <ConfirmModal
        isOpen={isDeleteOpen}
        title="Delete Increasing Adjustment"
        message={`Are you sure you want to delete adjustment ${itemToDelete?.invoiceNo || itemToDelete?.id}? This action cannot be undone.`}
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
