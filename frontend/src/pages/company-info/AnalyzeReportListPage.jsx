import React, { useState, useEffect } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { analyzeReportService } from '../../services/companyInfoService';
import { companyService } from '../../services/companyService';

export default function AnalyzeReportListPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [companies, setCompanies] = useState([]);

  // Form Modal state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    companyId: '',
    refNo: '',
    subject: '',
    date: '',
    receiveDate: '',
    analysedDate: '',
    deliveryDate: ''
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
    const today = new Date().toISOString().split('T')[0];
    setFormData({
      companyId: companies.length > 0 ? companies[0].id : '',
      refNo: '',
      subject: '',
      date: today,
      receiveDate: today,
      analysedDate: today,
      deliveryDate: today
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      companyId: item.companyId || '',
      refNo: item.refNo || '',
      subject: item.subject || '',
      date: item.date || '',
      receiveDate: item.receiveDate || '',
      analysedDate: item.analysedDate || '',
      deliveryDate: item.deliveryDate || ''
    });
    setIsFormOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await analyzeReportService.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'Analyze Report updated successfully!' });
      } else {
        await analyzeReportService.create(formData);
        setAlert({ type: 'success', message: 'Analyze Report created successfully!' });
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
      await analyzeReportService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Analyze Report deleted successfully!' });
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
      title: 'Reference No',
      data: 'refNo',
      sortable: true,
      render: (row) => (
        <div>
          <span style={{ fontWeight: 600, color: '#48465b', display: 'block' }}>{row.refNo || '—'}</span>
          <span style={{ fontSize: 11.5, color: '#959cb6' }}>Slug: {row.slug}</span>
        </div>
      )
    },
    {
      title: 'Subject',
      data: 'subject',
      sortable: true,
      render: (row) => (
        <span style={{ fontWeight: 500, color: '#595d6e' }}>{row.subject || '—'}</span>
      )
    },
    {
      title: 'Date',
      data: 'date',
      sortable: true,
      render: (row) => row.date || '—'
    },
    {
      title: 'Receive Date',
      data: 'receiveDate',
      sortable: true,
      render: (row) => row.receiveDate || '—'
    },
    {
      title: 'Analyze Date',
      data: 'analysedDate',
      sortable: true,
      render: (row) => row.analysedDate || '—'
    },
    {
      title: 'Delivery Date',
      data: 'deliveryDate',
      sortable: true,
      render: (row) => row.deliveryDate || '—'
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
            title="Edit Analyze Report"
            onClick={() => openEditModal(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Analyze Report"
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
        title="Analyze Report"
        breadcrumbs={[
          { label: 'Company Information', link: '/company' },
          { label: 'Analyze Report' }
        ]}
        actions={
          <button className="btn btn-brand" onClick={openCreateModal}>
            <i className="bi bi-plus-lg"></i>
            <span>Add Analyze Report</span>
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
        title="Analyze Report Records"
        columns={columns}
        fetchData={analyzeReportService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 650 }}>
            <div className="modal-header">
              <h4 className="modal-title">{editingItem ? 'Edit Analyze Report' : 'Add Analyze Report'}</h4>
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
                  <label className="form-label">Reference Number <span className="text-danger">*</span></label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    placeholder="Enter reference number"
                    value={formData.refNo}
                    onChange={(e) => setFormData({ ...formData, refNo: e.target.value })}
                  />
                </div>

                <div className="form-group mb-3">
                  <label className="form-label">Subject</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Enter subject or report title"
                    value={formData.subject}
                    onChange={(e) => setFormData({ ...formData, subject: e.target.value })}
                  />
                </div>

                <div className="row">
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Report Date</label>
                    <input
                      type="date"
                      className="form-control"
                      value={formData.date}
                      onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                    />
                  </div>
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Receive Date</label>
                    <input
                      type="date"
                      className="form-control"
                      value={formData.receiveDate}
                      onChange={(e) => setFormData({ ...formData, receiveDate: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Analyze Date</label>
                    <input
                      type="date"
                      className="form-control"
                      value={formData.analysedDate}
                      onChange={(e) => setFormData({ ...formData, analysedDate: e.target.value })}
                    />
                  </div>
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Delivery Date</label>
                    <input
                      type="date"
                      className="form-control"
                      value={formData.deliveryDate}
                      onChange={(e) => setFormData({ ...formData, deliveryDate: e.target.value })}
                    />
                  </div>
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setIsFormOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-brand">
                  {editingItem ? 'Save Changes' : 'Create Analyze Report'}
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
              <h4 className="modal-title">Analyze Report Details</h4>
              <button
                onClick={() => setIsViewOpen(false)}
                style={{ background: 'none', border: 'none', fontSize: 22, cursor: 'pointer', color: '#959cb6' }}
              >
                &times;
              </button>
            </div>
            <div className="modal-body" style={{ padding: '20px 25px' }}>
              <div className="mb-3">
                <strong style={{ color: '#646c9a' }}>Ref No:</strong>
                <div style={{ fontSize: 16, fontWeight: 600 }}>{viewItem.refNo || '—'}</div>
              </div>
              <div className="mb-3">
                <strong style={{ color: '#646c9a' }}>Subject:</strong>
                <div style={{ fontSize: 14 }}>{viewItem.subject || '—'}</div>
              </div>
              <div className="row mb-3">
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Date:</strong>
                  <div>{viewItem.date || '—'}</div>
                </div>
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Receive Date:</strong>
                  <div>{viewItem.receiveDate || '—'}</div>
                </div>
              </div>
              <div className="row mb-3">
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Analyze Date:</strong>
                  <div>{viewItem.analysedDate || '—'}</div>
                </div>
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Delivery Date:</strong>
                  <div>{viewItem.deliveryDate || '—'}</div>
                </div>
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
        title="Delete Analyze Report"
        message={`Are you sure you want to delete Analyze Report "${itemToDelete?.refNo || ''}"?`}
        confirmText="Delete"
        confirmBtnClass="btn-danger"
        onConfirm={confirmDelete}
        onCancel={() => setIsDeleteOpen(false)}
      />
    </div>
  );
}
