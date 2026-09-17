import React, { useState, useEffect } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { legalManagementService } from '../../services/companyInfoService';
import { companyService } from '../../services/companyService';

export default function LegalManagementListPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [companies, setCompanies] = useState([]);

  // Form Modal state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    companyId: '',
    caseNo: '',
    courtName: '',
    clientName: '',
    filingLawyer: '',
    representativeName: '',
    respondentName: '',
    nextHearingDate: '',
    barIdNo: '',
    fileNo: '',
    fileDate: '',
    description: '',
    commentRemarks: ''
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
      caseNo: '',
      courtName: '',
      clientName: '',
      filingLawyer: '',
      representativeName: '',
      respondentName: '',
      nextHearingDate: today,
      barIdNo: '',
      fileNo: '',
      fileDate: today,
      description: '',
      commentRemarks: ''
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      companyId: item.companyId || '',
      caseNo: item.caseNo || '',
      courtName: item.courtName || '',
      clientName: item.clientName || '',
      filingLawyer: item.filingLawyer || '',
      representativeName: item.representativeName || '',
      respondentName: item.respondentName || '',
      nextHearingDate: item.nextHearingDate || '',
      barIdNo: item.barIdNo || '',
      fileNo: item.fileNo || '',
      fileDate: item.fileDate || '',
      description: item.description || '',
      commentRemarks: item.commentRemarks || ''
    });
    setIsFormOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await legalManagementService.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'Legal Case updated successfully!' });
      } else {
        await legalManagementService.create(formData);
        setAlert({ type: 'success', message: 'Legal Case created successfully!' });
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
      await legalManagementService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Legal Case deleted successfully!' });
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
      title: 'Case Number',
      data: 'caseNo',
      sortable: true,
      render: (row) => (
        <div>
          <span style={{ fontWeight: 600, color: '#48465b', display: 'block' }}>{row.caseNo || '—'}</span>
          <span style={{ fontSize: 11.5, color: '#959cb6' }}>Slug: {row.slug}</span>
        </div>
      )
    },
    {
      title: 'Court Name',
      data: 'courtName',
      sortable: true,
      render: (row) => row.courtName || '—'
    },
    {
      title: 'Client Name',
      data: 'clientName',
      sortable: true,
      render: (row) => row.clientName || '—'
    },
    {
      title: 'Filing Lawyer',
      data: 'filingLawyer',
      sortable: true,
      render: (row) => row.filingLawyer || '—'
    },
    {
      title: 'Next Hearing',
      data: 'nextHearingDate',
      sortable: true,
      render: (row) => (
        <span className="badge badge-warning" style={{ fontWeight: 600 }}>
          <i className="bi bi-calendar-event" style={{ marginRight: 4 }}></i>
          {row.nextHearingDate || '—'}
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
            className="btn btn-sm btn-label-info btn-circle btn-icon"
            title="View Case"
            onClick={() => {
              setViewItem(row);
              setIsViewOpen(true);
            }}
          >
            <i className="bi bi-eye"></i>
          </button>
          <button
            className="btn btn-sm btn-label-brand btn-circle btn-icon"
            title="Edit Case"
            onClick={() => openEditModal(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Case"
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
        title="Legal Management"
        breadcrumbs={[
          { label: 'Company Information', link: '/company' },
          { label: 'Legal Management' }
        ]}
        actions={
          <button className="btn btn-brand" onClick={openCreateModal}>
            <i className="bi bi-plus-lg"></i>
            <span>Add Legal Case</span>
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
        title="Legal Management Cases"
        columns={columns}
        fetchData={legalManagementService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 750 }}>
            <div className="modal-header">
              <h4 className="modal-title">{editingItem ? 'Edit Legal Case' : 'Add Legal Case'}</h4>
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
                    <label className="form-label">Case Number <span className="text-danger">*</span></label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="e.g. CASE-1029/2026"
                      value={formData.caseNo}
                      onChange={(e) => setFormData({ ...formData, caseNo: e.target.value })}
                    />
                  </div>
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Court Name <span className="text-danger">*</span></label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="e.g. High Court Division"
                      value={formData.courtName}
                      onChange={(e) => setFormData({ ...formData, courtName: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Client Name</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Client or petitioner name"
                      value={formData.clientName}
                      onChange={(e) => setFormData({ ...formData, clientName: e.target.value })}
                    />
                  </div>
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Filing Lawyer</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Lawyer name"
                      value={formData.filingLawyer}
                      onChange={(e) => setFormData({ ...formData, filingLawyer: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Representative Name</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Representative"
                      value={formData.representativeName}
                      onChange={(e) => setFormData({ ...formData, representativeName: e.target.value })}
                    />
                  </div>
                  <div className="col-md-6 form-group mb-3">
                    <label className="form-label">Respondent Name</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Respondent"
                      value={formData.respondentName}
                      onChange={(e) => setFormData({ ...formData, respondentName: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-4 form-group mb-3">
                    <label className="form-label">Next Hearing Date</label>
                    <input
                      type="date"
                      className="form-control"
                      value={formData.nextHearingDate}
                      onChange={(e) => setFormData({ ...formData, nextHearingDate: e.target.value })}
                    />
                  </div>
                  <div className="col-md-4 form-group mb-3">
                    <label className="form-label">File Number</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="File no"
                      value={formData.fileNo}
                      onChange={(e) => setFormData({ ...formData, fileNo: e.target.value })}
                    />
                  </div>
                  <div className="col-md-4 form-group mb-3">
                    <label className="form-label">Bar ID No</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Bar ID"
                      value={formData.barIdNo}
                      onChange={(e) => setFormData({ ...formData, barIdNo: e.target.value })}
                    />
                  </div>
                </div>

                <div className="form-group mb-3">
                  <label className="form-label">Case Description</label>
                  <textarea
                    className="form-control"
                    rows={2}
                    placeholder="Case background, subject, details..."
                    value={formData.description}
                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  />
                </div>

                <div className="form-group mb-3">
                  <label className="form-label">Comment / Remarks</label>
                  <textarea
                    className="form-control"
                    rows={2}
                    placeholder="Latest hearing updates or remarks..."
                    value={formData.commentRemarks}
                    onChange={(e) => setFormData({ ...formData, commentRemarks: e.target.value })}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setIsFormOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-brand">
                  {editingItem ? 'Save Changes' : 'Create Case'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* View Modal */}
      {isViewOpen && viewItem && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 600 }}>
            <div className="modal-header">
              <h4 className="modal-title">Case Information</h4>
              <button
                onClick={() => setIsViewOpen(false)}
                style={{ background: 'none', border: 'none', fontSize: 22, cursor: 'pointer', color: '#959cb6' }}
              >
                &times;
              </button>
            </div>
            <div className="modal-body" style={{ padding: '20px 25px' }}>
              <div className="mb-3">
                <strong style={{ color: '#646c9a' }}>Case No:</strong>
                <div style={{ fontSize: 17, fontWeight: 600, color: '#48465b' }}>{viewItem.caseNo || '—'}</div>
              </div>
              <div className="row mb-3">
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Court:</strong>
                  <div>{viewItem.courtName || '—'}</div>
                </div>
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Next Hearing Date:</strong>
                  <div><span className="badge badge-warning">{viewItem.nextHearingDate || '—'}</span></div>
                </div>
              </div>
              <div className="row mb-3">
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Client:</strong>
                  <div>{viewItem.clientName || '—'}</div>
                </div>
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Filing Lawyer:</strong>
                  <div>{viewItem.filingLawyer || '—'}</div>
                </div>
              </div>
              <div className="row mb-3">
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Representative:</strong>
                  <div>{viewItem.representativeName || '—'}</div>
                </div>
                <div className="col-6">
                  <strong style={{ color: '#646c9a' }}>Respondent:</strong>
                  <div>{viewItem.respondentName || '—'}</div>
                </div>
              </div>
              {viewItem.description && (
                <div className="mb-3">
                  <strong style={{ color: '#646c9a' }}>Description:</strong>
                  <p style={{ marginTop: 4 }}>{viewItem.description}</p>
                </div>
              )}
              {viewItem.commentRemarks && (
                <div className="mb-3">
                  <strong style={{ color: '#646c9a' }}>Remarks:</strong>
                  <p style={{ marginTop: 4 }}>{viewItem.commentRemarks}</p>
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
        title="Delete Legal Case"
        message={`Are you sure you want to delete Case "${itemToDelete?.caseNo || ''}"?`}
        confirmText="Delete"
        confirmBtnClass="btn-danger"
        onConfirm={confirmDelete}
        onCancel={() => setIsDeleteOpen(false)}
      />
    </div>
  );
}
