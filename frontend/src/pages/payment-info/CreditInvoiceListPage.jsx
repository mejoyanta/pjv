import React, { useState } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { creditInvoiceService } from '../../services/paymentInfoService';

export default function CreditInvoiceListPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);

  // Form Modal state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    challanNo: '',
    challanDate: '',
    vehicleType: '',
    vehicleNo: '',
    totalVat: '',
    totalAmount: '',
    returnVatAmount: '',
    returnAmount: '',
    note: ''
  });

  // Delete Confirm state
  const [itemToDelete, setItemToDelete] = useState(null);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);

  const openCreateModal = () => {
    setEditingItem(null);
    setFormData({
      challanNo: '',
      challanDate: new Date().toISOString().split('T')[0],
      vehicleType: 'Covered Van',
      vehicleNo: '',
      totalVat: '',
      totalAmount: '',
      returnVatAmount: '',
      returnAmount: '',
      note: ''
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      challanNo: item.challanNo || '',
      challanDate: item.challanDate ? String(item.challanDate).substring(0, 10) : '',
      vehicleType: item.vehicleType || '',
      vehicleNo: item.vehicleNo || '',
      totalVat: item.totalVat || '',
      totalAmount: item.totalAmount || '',
      returnVatAmount: item.returnVatAmount || '',
      returnAmount: item.returnAmount || '',
      note: item.note || ''
    });
    setIsFormOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await creditInvoiceService.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'Credit Invoice updated successfully!' });
      } else {
        await creditInvoiceService.create(formData);
        setAlert({ type: 'success', message: 'Credit Invoice created successfully!' });
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
      await creditInvoiceService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Credit Invoice deleted successfully!' });
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
      await creditInvoiceService.downloadPdf();
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
      title: 'Challan No',
      data: 'challanNo',
      sortable: true,
      render: (row) => (
        <span className="kt-badge kt-badge--inline kt-badge--success" style={{ fontWeight: 600 }}>
          {row.challanNo || '—'}
        </span>
      )
    },
    {
      title: 'Challan Date',
      data: 'challanDate',
      sortable: true,
      render: (row) => row.challanDate ? String(row.challanDate).substring(0, 10) : '—'
    },
    {
      title: 'Vehicle',
      data: 'vehicleNo',
      sortable: false,
      render: (row) => row.vehicleNo ? `${row.vehicleType || ''} (${row.vehicleNo})` : (row.vehicleType || '—')
    },
    {
      title: 'Total Amount',
      data: 'totalAmount',
      sortable: true,
      align: 'right',
      render: (row) => Number(row.totalAmount || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })
    },
    {
      title: 'Total VAT',
      data: 'totalVat',
      sortable: true,
      align: 'right',
      render: (row) => (
        <span style={{ fontWeight: 600, color: '#5867dd' }}>
          {Number(row.totalVat || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
        </span>
      )
    },
    {
      title: 'Return Amount',
      data: 'returnAmount',
      sortable: true,
      align: 'right',
      render: (row) => (
        <span style={{ fontWeight: 600, color: '#fd3995' }}>
          {Number(row.returnAmount || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
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
            title="Edit Credit Invoice"
            onClick={() => openEditModal(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Credit Invoice"
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
        title="Credit Invoice"
        breadcrumbs={[
          { label: 'Payment Information' },
          { label: 'Credit Invoice' }
        ]}
        actions={
          <div style={{ display: 'flex', gap: 8 }}>
            <button className="btn btn-outline-danger" onClick={handleDownloadPdf} disabled={isPdfLoading}>
              <i className="bi bi-file-earmark-pdf"></i>
              <span>{isPdfLoading ? 'Exporting...' : 'Export PDF'}</span>
            </button>
            <button className="btn btn-brand" onClick={openCreateModal}>
              <i className="bi bi-plus-lg"></i>
              <span>Add Credit Invoice</span>
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
        title="Credit Invoice Records"
        columns={columns}
        fetchData={creditInvoiceService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 600 }}>
            <div className="modal-header">
              <h5>{editingItem ? 'Edit Credit Invoice' : 'New Credit Invoice'}</h5>
              <button className="btn btn-sm btn-label-secondary" onClick={() => setIsFormOpen(false)}>×</button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body" style={{ padding: '20px' }}>
                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Challan No *</label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="e.g. CR-INV-001"
                      value={formData.challanNo}
                      onChange={(e) => setFormData({ ...formData, challanNo: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Challan Date</label>
                    <input
                      type="date"
                      className="form-control"
                      value={formData.challanDate}
                      onChange={(e) => setFormData({ ...formData, challanDate: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Vehicle Type</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. Truck, Van"
                      value={formData.vehicleType}
                      onChange={(e) => setFormData({ ...formData, vehicleType: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Vehicle No</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. DHA-11-2233"
                      value={formData.vehicleNo}
                      onChange={(e) => setFormData({ ...formData, vehicleNo: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Total Amount</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-control"
                      placeholder="0.00"
                      value={formData.totalAmount}
                      onChange={(e) => setFormData({ ...formData, totalAmount: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Total VAT</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-control"
                      placeholder="0.00"
                      value={formData.totalVat}
                      onChange={(e) => setFormData({ ...formData, totalVat: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Return Amount</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-control"
                      placeholder="0.00"
                      value={formData.returnAmount}
                      onChange={(e) => setFormData({ ...formData, returnAmount: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Return VAT</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-control"
                      placeholder="0.00"
                      value={formData.returnVatAmount}
                      onChange={(e) => setFormData({ ...formData, returnVatAmount: e.target.value })}
                    />
                  </div>
                </div>

                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Note / Remarks</label>
                  <textarea
                    className="form-control"
                    rows="2"
                    placeholder="Invoice details or reason for credit note..."
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
                  {editingItem ? 'Update Invoice' : 'Save Invoice'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Modal */}
      <ConfirmModal
        isOpen={isDeleteOpen}
        title="Delete Credit Invoice"
        message={`Are you sure you want to delete credit invoice ${itemToDelete?.challanNo || itemToDelete?.id}? This action cannot be undone.`}
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
