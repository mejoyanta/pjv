import React, { useState, useEffect } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { dvcService } from '../../services/companyInfoService';
import { companyService } from '../../services/companyService';

export default function DvcListPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [companies, setCompanies] = useState([]);

  // Form Modal state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    companyId: '',
    year: '',
    activationDate: '',
    dvcNo: '',
    totalIncome: '',
    totalExpense: '',
    totalProfit: '',
    fileUrl: ''
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
    const currentYear = new Date().getFullYear().toString();
    setFormData({
      companyId: companies.length > 0 ? companies[0].id : '',
      year: currentYear,
      activationDate: new Date().toISOString().split('T')[0],
      dvcNo: '',
      totalIncome: '',
      totalExpense: '',
      totalProfit: '',
      fileUrl: ''
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      companyId: item.companyId || '',
      year: item.year || '',
      activationDate: item.activationDate || '',
      dvcNo: item.dvcNo || '',
      totalIncome: item.totalIncome != null ? item.totalIncome : '',
      totalExpense: item.totalExpense != null ? item.totalExpense : '',
      totalProfit: item.totalProfit != null ? item.totalProfit : '',
      fileUrl: item.fileUrl || ''
    });
    setIsFormOpen(true);
  };

  // Auto calculate profit
  const handleIncomeExpenseChange = (income, expense) => {
    const inc = parseFloat(income) || 0;
    const exp = parseFloat(expense) || 0;
    setFormData(prev => ({
      ...prev,
      totalIncome: income,
      totalExpense: expense,
      totalProfit: (inc - exp).toFixed(2)
    }));
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await dvcService.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'DVC File updated successfully!' });
      } else {
        await dvcService.create(formData);
        setAlert({ type: 'success', message: 'DVC File created successfully!' });
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
      await dvcService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'DVC File deleted successfully!' });
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
      title: 'DVC Number',
      data: 'dvcNo',
      sortable: true,
      render: (row) => (
        <div>
          <span style={{ fontWeight: 600, color: '#48465b', display: 'block' }}>{row.dvcNo || '—'}</span>
          <span style={{ fontSize: 11.5, color: '#959cb6' }}>Year: {row.year}</span>
        </div>
      )
    },
    {
      title: 'Activation Date',
      data: 'activationDate',
      sortable: true,
      render: (row) => row.activationDate || '—'
    },
    {
      title: 'Total Income',
      data: 'totalIncome',
      sortable: true,
      align: 'right',
      render: (row) => row.totalIncome != null ? `৳ ${Number(row.totalIncome).toLocaleString()}` : '—'
    },
    {
      title: 'Total Expense',
      data: 'totalExpense',
      sortable: true,
      align: 'right',
      render: (row) => row.totalExpense != null ? `৳ ${Number(row.totalExpense).toLocaleString()}` : '—'
    },
    {
      title: 'Total Profit',
      data: 'totalProfit',
      sortable: true,
      align: 'right',
      render: (row) => row.totalProfit != null ? (
        <span style={{ fontWeight: 600, color: row.totalProfit >= 0 ? '#1dc9b7' : '#fd397a' }}>
          ৳ {Number(row.totalProfit).toLocaleString()}
        </span>
      ) : '—'
    },
    {
      title: 'File',
      sortable: false,
      align: 'center',
      render: (row) => row.fileUrl ? (
        <a
          href={row.fileUrl.startsWith('http') ? row.fileUrl : `http://localhost:8081/${row.fileUrl}`}
          target="_blank"
          rel="noopener noreferrer"
          className="btn btn-sm btn-label-brand"
        >
          <i className="bi bi-download" style={{ marginRight: 4 }}></i> File
        </a>
      ) : <span style={{ color: '#a2a3b7' }}>No File</span>
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
            title="Edit DVC"
            onClick={() => openEditModal(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete DVC"
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
        title="DVC Files"
        breadcrumbs={[
          { label: 'Company Information', link: '/company' },
          { label: 'DVC Files' }
        ]}
        actions={
          <button className="btn btn-brand" onClick={openCreateModal}>
            <i className="bi bi-cloud-upload"></i>
            <span>Upload DVC File</span>
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
        title="DVC File Records"
        columns={columns}
        fetchData={dvcService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 650 }}>
            <div className="modal-header">
              <h4 className="modal-title">{editingItem ? 'Edit DVC Record' : 'Add DVC Record'}</h4>
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

                <div className="row">
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">DVC Number <span className="text-danger">*</span></label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="e.g. DVC-2026-99"
                      value={formData.dvcNo}
                      onChange={(e) => setFormData({ ...formData, dvcNo: e.target.value })}
                    />
                  </div>
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Year <span className="text-danger">*</span></label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="e.g. 2026"
                      value={formData.year}
                      onChange={(e) => setFormData({ ...formData, year: e.target.value })}
                    />
                  </div>
                </div>

                <div className="form-group mb-3">
                  <label className="form-label">Activation Date</label>
                  <input
                    type="date"
                    className="form-control"
                    value={formData.activationDate}
                    onChange={(e) => setFormData({ ...formData, activationDate: e.target.value })}
                  />
                </div>

                <div className="row">
                  <div className="col-md-4 form-group mb-3">
                    <label className="form-label">Total Income (৳)</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-control"
                      placeholder="0.00"
                      value={formData.totalIncome}
                      onChange={(e) => handleIncomeExpenseChange(e.target.value, formData.totalExpense)}
                    />
                  </div>
                  <div className="col-md-4 form-group mb-3">
                    <label className="form-label">Total Expense (৳)</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-control"
                      placeholder="0.00"
                      value={formData.totalExpense}
                      onChange={(e) => handleIncomeExpenseChange(formData.totalIncome, e.target.value)}
                    />
                  </div>
                  <div className="col-md-4 form-group mb-3">
                    <label className="form-label">Total Profit (৳)</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-control"
                      placeholder="0.00"
                      value={formData.totalProfit}
                      onChange={(e) => setFormData({ ...formData, totalProfit: e.target.value })}
                    />
                  </div>
                </div>

                <div className="form-group mb-3">
                  <label className="form-label">File URL or Path</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="e.g. uploads/dvc/doc1.pdf"
                    value={formData.fileUrl}
                    onChange={(e) => setFormData({ ...formData, fileUrl: e.target.value })}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setIsFormOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-brand">
                  {editingItem ? 'Save Changes' : 'Create DVC'}
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
              <h4 className="modal-title">DVC Details</h4>
              <button
                onClick={() => setIsViewOpen(false)}
                style={{ background: 'none', border: 'none', fontSize: 22, cursor: 'pointer', color: '#959cb6' }}
              >
                &times;
              </button>
            </div>
            <div className="modal-body" style={{ padding: '20px 25px' }}>
              <div className="mb-3">
                <strong style={{ color: '#646c9a' }}>DVC No:</strong>
                <div style={{ fontSize: 16, fontWeight: 600 }}>{viewItem.dvcNo || '—'}</div>
              </div>
              <div className="row mb-3">
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Year:</strong>
                  <div>{viewItem.year || '—'}</div>
                </div>
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Activation Date:</strong>
                  <div>{viewItem.activationDate || '—'}</div>
                </div>
              </div>
              <div className="row mb-3">
                <div className="col-4">
                  <strong style={{ color: '#646c9a' }}>Income:</strong>
                  <div>{viewItem.totalIncome != null ? `৳ ${Number(viewItem.totalIncome).toLocaleString()}` : '—'}</div>
                </div>
                <div className="col-4">
                  <strong style={{ color: '#646c9a' }}>Expense:</strong>
                  <div>{viewItem.totalExpense != null ? `৳ ${Number(viewItem.totalExpense).toLocaleString()}` : '—'}</div>
                </div>
                <div className="col-4">
                  <strong style={{ color: '#646c9a' }}>Profit:</strong>
                  <div style={{ fontWeight: 600, color: viewItem.totalProfit >= 0 ? '#1dc9b7' : '#fd397a' }}>
                    {viewItem.totalProfit != null ? `৳ ${Number(viewItem.totalProfit).toLocaleString()}` : '—'}
                  </div>
                </div>
              </div>
              {viewItem.fileUrl && (
                <div className="mb-3">
                  <strong style={{ color: '#646c9a' }}>Attachment:</strong>
                  <div>
                    <a
                      href={viewItem.fileUrl.startsWith('http') ? viewItem.fileUrl : `http://localhost:8081/${viewItem.fileUrl}`}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="btn btn-sm btn-brand mt-2"
                    >
                      <i className="bi bi-cloud-download"></i> View / Download
                    </a>
                  </div>
                </div>
              )}
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
        title="Delete DVC Record"
        message={`Are you sure you want to delete DVC "${itemToDelete?.dvcNo || ''}"?`}
        confirmText="Delete"
        confirmBtnClass="btn-danger"
        onConfirm={confirmDelete}
        onCancel={() => setIsDeleteOpen(false)}
      />
    </div>
  );
}
