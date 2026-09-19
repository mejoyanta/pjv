import React, { useState, useEffect } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { materialService } from '../../services/basicConfigService';

export default function MaterialListPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);

  // Form options from backend
  const [companies, setCompanies] = useState([]);
  const [branches, setBranches] = useState([]);
  const [categories, setCategories] = useState([]);
  const [units, setUnits] = useState([]);

  // Form Modal state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    companyId: '',
    companyBranchId: '',
    categoryId: '',
    supplymentUnitId: '',
    name: '',
    hsCode: '',
    vatType: 'exclude',
    vat: 15,
    sd: 0,
    at: 0,
    cd: 0,
    rd: 0,
    ait: 0,
    tti: 0,
    exd: 0,
    purchaseType: 'both',
    description: ''
  });

  // View Modal state
  const [viewItem, setViewItem] = useState(null);
  const [isViewOpen, setIsViewOpen] = useState(false);

  // Delete Confirm state
  const [itemToDelete, setItemToDelete] = useState(null);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);

  useEffect(() => {
    materialService.getFormData()
      .then(res => {
        if (res?.data?.data) {
          const d = res.data.data;
          setCompanies(d.companies || []);
          setBranches(d.branches || []);
          setCategories(d.categories || []);
          setUnits(d.units || []);
        } else if (res?.data) {
          setCompanies(res.data.companies || []);
          setBranches(res.data.branches || []);
          setCategories(res.data.categories || []);
          setUnits(res.data.units || []);
        }
      })
      .catch(console.error);
  }, []);

  const openCreateModal = () => {
    setEditingItem(null);
    setFormData({
      companyId: companies.length > 0 ? companies[0].id : '',
      companyBranchId: '',
      categoryId: '',
      supplymentUnitId: '',
      name: '',
      hsCode: '',
      vatType: 'exclude',
      vat: 15,
      sd: 0,
      at: 0,
      cd: 0,
      rd: 0,
      ait: 0,
      tti: 0,
      exd: 0,
      purchaseType: 'both',
      description: ''
    });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      companyId: item.companyId || '',
      companyBranchId: item.companyBranchId || '',
      categoryId: item.categoryId || '',
      supplymentUnitId: item.supplymentUnitId || '',
      name: item.name || '',
      hsCode: item.hsCode || '',
      vatType: item.vatType || 'exclude',
      vat: item.vat != null ? item.vat : 0,
      sd: item.sd != null ? item.sd : 0,
      at: item.at != null ? item.at : 0,
      cd: item.cd != null ? item.cd : 0,
      rd: item.rd != null ? item.rd : 0,
      ait: item.ait != null ? item.ait : 0,
      tti: item.tti != null ? item.tti : 0,
      exd: item.exd != null ? item.exd : 0,
      purchaseType: item.purchaseType || 'both',
      description: item.description || ''
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
      setAlert({ type: 'danger', message: err.message || 'Failed to save material.' });
    }
  };

  const confirmDelete = async () => {
    if (!itemToDelete) return;
    try {
      await materialService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Material deleted successfully!' });
      setReloadTrigger(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message || 'Failed to delete material.' });
    } finally {
      setIsDeleteOpen(false);
      setItemToDelete(null);
    }
  };

  const handleDownloadPdf = async () => {
    setIsPdfLoading(true);
    try {
      await materialService.downloadPdf();
      setAlert({ type: 'success', message: 'Material PDF exported successfully.' });
    } catch (err) {
      setAlert({ type: 'danger', message: 'Failed to download PDF report' });
    } finally {
      setIsPdfLoading(false);
    }
  };

  const filteredBranches = formData.companyId
    ? branches.filter(b => String(b.companyId) === String(formData.companyId))
    : branches;

  // Exact Laravel 14 table columns
  const columns = [
    {
      title: '#',
      sortable: false,
      width: '45px',
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
      title: 'Company',
      sortable: false,
      render: (row) => (row.company ? row.company.name : '—')
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
      title: 'AT %',
      data: 'at',
      sortable: true,
      align: 'center',
      render: (row) => `${row.at || 0}%`
    },
    {
      title: 'CD %',
      data: 'cd',
      sortable: true,
      align: 'center',
      render: (row) => `${row.cd || 0}%`
    },
    {
      title: 'RD %',
      data: 'rd',
      sortable: true,
      align: 'center',
      render: (row) => `${row.rd || 0}%`
    },
    {
      title: 'AIT %',
      data: 'ait',
      sortable: true,
      align: 'center',
      render: (row) => `${row.ait || 0}%`
    },
    {
      title: 'TTI %',
      data: 'tti',
      sortable: true,
      align: 'center',
      render: (row) => `${row.tti || 0}%`
    },
    {
      title: 'VAT Type',
      data: 'vatType',
      sortable: true,
      align: 'center',
      render: (row) => (
        <span className={`badge ${row.vatType === 'fixed' ? 'badge-info' : 'badge-secondary'}`}>
          {row.vatType || 'exclude'}
        </span>
      )
    },
    {
      title: 'Unit',
      sortable: false,
      render: (row) => (row.unit ? row.unit.name : '—')
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

      {/* Create / Edit Modal (All Laravel Fields) */}
      {isFormOpen && (
        <div className="modal-overlay" style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', zIndex: 1050, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 15 }}>
          <div className="modal-content" style={{ maxWidth: 850, maxHeight: '92vh', overflowY: 'auto', background: '#fff', borderRadius: 8, boxShadow: '0 5px 20px rgba(0,0,0,0.3)' }}>
            <div className="modal-header" style={{ padding: '15px 20px', borderBottom: '1px solid #ebedf2', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h5 style={{ margin: 0, fontWeight: 600 }}>{editingItem ? 'Edit Material' : 'Create New Material'}</h5>
              <button type="button" className="btn btn-sm btn-label-secondary" onClick={() => setIsFormOpen(false)} style={{ border: 'none', background: 'none', fontSize: 20 }}>×</button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body" style={{ padding: '20px' }}>
                <div className="row">
                  {/* Company */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Company <span className="text-danger">*</span></label>
                    <select
                      className="form-control"
                      value={formData.companyId}
                      onChange={(e) => setFormData({ ...formData, companyId: e.target.value, companyBranchId: '' })}
                      required
                    >
                      <option value="">Select Company Name</option>
                      {companies.map(c => (
                        <option key={c.id} value={c.id}>{c.name}</option>
                      ))}
                    </select>
                  </div>

                  {/* Branch */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Company Branch</label>
                    <select
                      className="form-control"
                      value={formData.companyBranchId}
                      onChange={(e) => setFormData({ ...formData, companyBranchId: e.target.value })}
                    >
                      <option value="">Company Branch</option>
                      {filteredBranches.map(b => (
                        <option key={b.id} value={b.id}>{b.name}</option>
                      ))}
                    </select>
                  </div>

                  {/* HS Code */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>HS Code</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Material HS Code"
                      value={formData.hsCode}
                      onChange={(e) => setFormData({ ...formData, hsCode: e.target.value })}
                    />
                  </div>

                  {/* Category */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Category</label>
                    <select
                      className="form-control"
                      value={formData.categoryId}
                      onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
                    >
                      <option value="">Select Category</option>
                      {categories.map(cat => (
                        <option key={cat.id} value={cat.id}>{cat.name}</option>
                      ))}
                    </select>
                  </div>

                  {/* Material Name */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Name <span className="text-danger">*</span></label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="Material Name"
                      value={formData.name}
                      onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    />
                  </div>

                  {/* Unit */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Unit</label>
                    <select
                      className="form-control"
                      value={formData.supplymentUnitId}
                      onChange={(e) => setFormData({ ...formData, supplymentUnitId: e.target.value })}
                    >
                      <option value="">Units</option>
                      {units.map(u => (
                        <option key={u.id} value={u.id}>{u.name}</option>
                      ))}
                    </select>
                  </div>

                  {/* VAT */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>VAT % <span className="text-danger">*</span></label>
                    <input
                      type="number"
                      step="any"
                      min="0"
                      className="form-control"
                      required
                      value={formData.vat}
                      onChange={(e) => setFormData({ ...formData, vat: parseFloat(e.target.value) || 0 })}
                    />
                  </div>

                  {/* VAT Type */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>VAT Type <span className="text-danger">*</span></label>
                    <select
                      className="form-control"
                      required
                      value={formData.vatType}
                      onChange={(e) => setFormData({ ...formData, vatType: e.target.value })}
                    >
                      <option value="exclude">Exclude</option>
                      <option value="fixed">Fixed</option>
                    </select>
                  </div>

                  {/* SD */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>SD %</label>
                    <input
                      type="number"
                      step="any"
                      min="0"
                      className="form-control"
                      value={formData.sd}
                      onChange={(e) => setFormData({ ...formData, sd: parseFloat(e.target.value) || 0 })}
                    />
                  </div>

                  {/* AT */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>AT %</label>
                    <input
                      type="number"
                      step="any"
                      min="0"
                      className="form-control"
                      value={formData.at}
                      onChange={(e) => setFormData({ ...formData, at: parseFloat(e.target.value) || 0 })}
                    />
                  </div>

                  {/* CD */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>CD %</label>
                    <input
                      type="number"
                      step="any"
                      min="0"
                      className="form-control"
                      value={formData.cd}
                      onChange={(e) => setFormData({ ...formData, cd: parseFloat(e.target.value) || 0 })}
                    />
                  </div>

                  {/* RD */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>RD %</label>
                    <input
                      type="number"
                      step="any"
                      min="0"
                      className="form-control"
                      value={formData.rd}
                      onChange={(e) => setFormData({ ...formData, rd: parseFloat(e.target.value) || 0 })}
                    />
                  </div>

                  {/* AIT */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>AIT %</label>
                    <input
                      type="number"
                      step="any"
                      min="0"
                      className="form-control"
                      value={formData.ait}
                      onChange={(e) => setFormData({ ...formData, ait: parseFloat(e.target.value) || 0 })}
                    />
                  </div>

                  {/* TTI */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>TTI %</label>
                    <input
                      type="number"
                      step="any"
                      min="0"
                      className="form-control"
                      value={formData.tti}
                      onChange={(e) => setFormData({ ...formData, tti: parseFloat(e.target.value) || 0 })}
                    />
                  </div>

                  {/* EXD */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>EXD %</label>
                    <input
                      type="number"
                      step="any"
                      min="0"
                      className="form-control"
                      value={formData.exd}
                      onChange={(e) => setFormData({ ...formData, exd: parseFloat(e.target.value) || 0 })}
                    />
                  </div>

                  {/* Purchase Type */}
                  <div className="col-12 col-md-4 mb-3">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Purchase Type <span className="text-danger">*</span></label>
                    <select
                      className="form-control"
                      required
                      value={formData.purchaseType}
                      onChange={(e) => setFormData({ ...formData, purchaseType: e.target.value })}
                    >
                      <option value="both">Both</option>
                      <option value="import">Import</option>
                      <option value="local">Local</option>
                    </select>
                  </div>

                  {/* Description */}
                  <div className="col-12 mb-2">
                    <label style={{ fontWeight: 600, marginBottom: 5 }}>Description</label>
                    <textarea
                      className="form-control"
                      rows="2"
                      placeholder="Material Description"
                      value={formData.description}
                      onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                    />
                  </div>
                </div>
              </div>
              <div className="modal-footer" style={{ padding: '15px 20px', borderTop: '1px solid #ebedf2', display: 'flex', justifyContent: 'flex-end', gap: 10 }}>
                <button type="button" className="btn btn-secondary" onClick={() => setIsFormOpen(false)}>Cancel</button>
                <button type="submit" className="btn btn-brand">{editingItem ? 'Update Material' : 'Submit'}</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* View Modal */}
      {isViewOpen && viewItem && (
        <div className="modal-overlay" style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', zIndex: 1050, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 15 }}>
          <div className="modal-content" style={{ maxWidth: 600, background: '#fff', borderRadius: 8, boxShadow: '0 5px 20px rgba(0,0,0,0.3)' }}>
            <div className="modal-header" style={{ padding: '15px 20px', borderBottom: '1px solid #ebedf2', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h5 style={{ margin: 0, fontWeight: 600 }}>Material Details</h5>
              <button type="button" className="btn btn-sm btn-label-secondary" onClick={() => setIsViewOpen(false)} style={{ border: 'none', background: 'none', fontSize: 20 }}>×</button>
            </div>
            <div className="modal-body" style={{ padding: 20 }}>
              <table className="table table-bordered">
                <tbody>
                  <tr><th style={{ width: '35%' }}>Material Name</th><td>{viewItem.name}</td></tr>
                  <tr><th>HS Code</th><td>{viewItem.hsCode || '—'}</td></tr>
                  <tr><th>Company</th><td>{viewItem.company ? viewItem.company.name : '—'}</td></tr>
                  <tr><th>VAT % / Type</th><td>{viewItem.vat || 0}% ({viewItem.vatType})</td></tr>
                  <tr><th>SD / CD / RD</th><td>SD: {viewItem.sd || 0}% | CD: {viewItem.cd || 0}% | RD: {viewItem.rd || 0}%</td></tr>
                  <tr><th>AT / AIT / TTI</th><td>AT: {viewItem.at || 0}% | AIT: {viewItem.ait || 0}% | TTI: {viewItem.tti || 0}%</td></tr>
                  <tr><th>Purchase Type</th><td>{viewItem.purchaseType || 'both'}</td></tr>
                  <tr><th>Unit</th><td>{viewItem.unit ? viewItem.unit.name : '—'}</td></tr>
                  <tr><th>Description</th><td>{viewItem.description || '—'}</td></tr>
                  <tr><th>Created Date</th><td>{viewItem.createdAt ? new Date(viewItem.createdAt).toLocaleString() : '—'}</td></tr>
                </tbody>
              </table>
            </div>
            <div className="modal-footer" style={{ padding: '15px 20px', borderTop: '1px solid #ebedf2', display: 'flex', justifyContent: 'flex-end' }}>
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
