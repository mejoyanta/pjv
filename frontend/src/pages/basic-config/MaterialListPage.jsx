import React, { useState, useEffect } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { materialService } from '../../services/basicConfigService';
import { companyService } from '../../services/companyService';

export default function MaterialListPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);
  const [companies, setCompanies] = useState([]);

  // Form Modal state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    hsCode: '',
    vat: 0,
    sd: 0,
    cd: 0,
    rd: 0,
    at: 0,
    ait: 0,
    vatType: 'exclude',
    description: '',
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
      hsCode: '',
      vat: 15,
      sd: 0,
      cd: 0,
      rd: 0,
      at: 0,
      ait: 0,
      vatType: 'exclude',
      description: '',
      companyId: companies.length > 0 ? companies[0].id : ''
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      name: item.name || '',
      hsCode: item.hsCode || '',
      vat: item.vat != null ? item.vat : 0,
      sd: item.sd != null ? item.sd : 0,
      cd: item.cd != null ? item.cd : 0,
      rd: item.rd != null ? item.rd : 0,
      at: item.at != null ? item.at : 0,
      ait: item.ait != null ? item.ait : 0,
      vatType: item.vatType || 'exclude',
      description: item.description || '',
      companyId: item.companyId || ''
    });
    setIsFormOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await materialService.update(editingItem.id, formData);
        setAlert({ type: 'success', message: 'Material updated successfully!' });
      } else {
        await materialService.create(formData);
        setAlert({ type: 'success', message: 'Material created successfully!' });
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
      await materialService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Material deleted successfully!' });
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
      await materialService.downloadPdf();
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
      title: 'HS Code',
      data: 'hsCode',
      sortable: true,
      render: (row) => (
        <span className="kt-badge kt-badge--inline kt-badge--brand" style={{ fontWeight: 600 }}>
          {row.hsCode || '—'}
        </span>
      )
    },
    {
      title: 'Material Name',
      data: 'name',
      sortable: true,
      render: (row) => <span style={{ fontWeight: 600, color: '#48465b' }}>{row.name}</span>
    },
    {
      title: 'VAT %',
      data: 'vat',
      sortable: true,
      align: 'center',
      render: (row) => `${row.vat || 0}%`
    },
    {
      title: 'SD %',
      data: 'sd',
      sortable: true,
      align: 'center',
      render: (row) => `${row.sd || 0}%`
    },
    {
      title: 'CD %',
      data: 'cd',
      sortable: true,
      align: 'center',
      render: (row) => `${row.cd || 0}%`
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
            title="Edit Material"
            onClick={() => openEditModal(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Material"
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
        title="Material"
        breadcrumbs={[
          { label: 'Basic Configuration' },
          { label: 'Material' }
        ]}
        actions={
          <div style={{ display: 'flex', gap: 8 }}>
            <button className="btn btn-outline-danger" onClick={handleDownloadPdf} disabled={isPdfLoading}>
              <i className="bi bi-file-earmark-pdf"></i>
              <span>{isPdfLoading ? 'Exporting...' : 'Export PDF'}</span>
            </button>
            <button className="btn btn-brand" onClick={openCreateModal}>
              <i className="bi bi-plus-lg"></i>
              <span>Add Material</span>
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
        title="Material Records"
        columns={columns}
        fetchData={materialService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 650 }}>
            <div className="modal-header">
              <h5>{editingItem ? 'Edit Material' : 'Add New Material'}</h5>
              <button className="btn btn-sm btn-label-secondary" onClick={() => setIsFormOpen(false)}>×</button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body" style={{ padding: '20px' }}>
                <div className="row" style={{ display: 'flex', gap: 15, marginBottom: 15 }}>
                  <div style={{ flex: 2 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Material Name *</label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="e.g. Cotton Yarn"
                      value={formData.name}
                      onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>HS Code</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. 5205.11.00"
                      value={formData.hsCode}
                      onChange={(e) => setFormData({ ...formData, hsCode: e.target.value })}
                    />
                  </div>
                </div>

                <div className="row" style={{ display: 'flex', gap: 10, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>VAT %</label>
                    <input
                      type="number"
                      step="any"
                      className="form-control"
                      value={formData.vat}
                      onChange={(e) => setFormData({ ...formData, vat: parseFloat(e.target.value) || 0 })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>SD %</label>
                    <input
                      type="number"
                      step="any"
                      className="form-control"
                      value={formData.sd}
                      onChange={(e) => setFormData({ ...formData, sd: parseFloat(e.target.value) || 0 })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>CD %</label>
                    <input
                      type="number"
                      step="any"
                      className="form-control"
                      value={formData.cd}
                      onChange={(e) => setFormData({ ...formData, cd: parseFloat(e.target.value) || 0 })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>RD %</label>
                    <input
                      type="number"
                      step="any"
                      className="form-control"
                      value={formData.rd}
                      onChange={(e) => setFormData({ ...formData, rd: parseFloat(e.target.value) || 0 })}
                    />
                  </div>
                </div>

                <div className="row" style={{ display: 'flex', gap: 15, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>VAT Type</label>
                    <select
                      className="form-control"
                      value={formData.vatType}
                      onChange={(e) => setFormData({ ...formData, vatType: e.target.value })}
                    >
                      <option value="exclude">Exclude</option>
                      <option value="include">Include</option>
                      <option value="fixed">Fixed</option>
                      <option value="exempted">Exempted</option>
                    </select>
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Company</label>
                    <select
                      className="form-control"
                      value={formData.companyId}
                      onChange={(e) => setFormData({ ...formData, companyId: e.target.value })}
                    >
                      <option value="">-- Select Company (Optional) --</option>
                      {companies.map(c => (
                        <option key={c.id} value={c.id}>{c.name}</option>
                      ))}
                    </select>
                  </div>
                </div>

                <div className="form-group">
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Description</label>
                  <textarea
                    className="form-control"
                    rows="2"
                    placeholder="Optional description"
                    value={formData.description}
                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  />
                </div>
              </div>
              <div className="modal-footer" style={{ display: 'flex', justifyContent: 'flex-end', gap: 10 }}>
                <button type="button" className="btn btn-secondary" onClick={() => setIsFormOpen(false)}>Cancel</button>
                <button type="submit" className="btn btn-brand">{editingItem ? 'Update Material' : 'Create Material'}</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* View Modal */}
      {isViewOpen && viewItem && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 550 }}>
            <div className="modal-header">
              <h5>Material Details</h5>
              <button className="btn btn-sm btn-label-secondary" onClick={() => setIsViewOpen(false)}>×</button>
            </div>
            <div className="modal-body" style={{ padding: 20 }}>
              <table className="table table-bordered">
                <tbody>
                  <tr><th style={{ width: '35%' }}>Material Name</th><td>{viewItem.name}</td></tr>
                  <tr><th>HS Code</th><td>{viewItem.hsCode || '—'}</td></tr>
                  <tr><th>VAT %</th><td>{viewItem.vat || 0}% ({viewItem.vatType})</td></tr>
                  <tr><th>SD %</th><td>{viewItem.sd || 0}%</td></tr>
                  <tr><th>CD %</th><td>{viewItem.cd || 0}%</td></tr>
                  <tr><th>RD %</th><td>{viewItem.rd || 0}%</td></tr>
                  <tr><th>Company</th><td>{viewItem.company ? viewItem.company.name : '—'}</td></tr>
                  <tr><th>Description</th><td>{viewItem.description || '—'}</td></tr>
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
        message={`Are you sure you want to delete material "${itemToDelete?.name}"?`}
        onConfirm={confirmDelete}
        onCancel={() => setIsDeleteOpen(false)}
      />
    </div>
  );
}
