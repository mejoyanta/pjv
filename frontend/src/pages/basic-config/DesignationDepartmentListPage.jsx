import React, { useState, useEffect } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { designationService, departmentService } from '../../services/basicConfigService';
import { companyService } from '../../services/companyService';

export default function DesignationDepartmentListPage() {
  const [activeTab, setActiveTab] = useState('designations'); // 'designations' | 'departments'
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);
  const [companies, setCompanies] = useState([]);

  // Modals state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({ name: '', companyId: '' });

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
    setFormData({ name: '', companyId: companies.length > 0 ? companies[0].id : '' });
    setIsFormOpen(true);
  };

  const openEditModal = (item) => {
    setEditingItem(item);
    setFormData({
      name: item.name || '',
      companyId: item.companyId || (companies.length > 0 ? companies[0].id : '')
    });
    setIsFormOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (activeTab === 'designations') {
        if (editingItem) {
          await designationService.update(editingItem.id, formData);
          setAlert({ type: 'success', message: 'Designation updated successfully!' });
        } else {
          await designationService.create(formData);
          setAlert({ type: 'success', message: 'Designation created successfully!' });
        }
      } else {
        if (editingItem) {
          await departmentService.update(editingItem.id, formData);
          setAlert({ type: 'success', message: 'Department updated successfully!' });
        } else {
          await departmentService.create(formData);
          setAlert({ type: 'success', message: 'Department created successfully!' });
        }
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
      if (activeTab === 'designations') {
        await designationService.delete(itemToDelete.id);
        setAlert({ type: 'success', message: 'Designation deleted successfully!' });
      } else {
        await departmentService.delete(itemToDelete.id);
        setAlert({ type: 'success', message: 'Department deleted successfully!' });
      }
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
      if (activeTab === 'designations') {
        await designationService.downloadPdf();
      } else {
        await departmentService.downloadPdf();
      }
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
      title: activeTab === 'designations' ? 'Designation Name' : 'Department Name',
      data: 'name',
      sortable: true,
      render: (row) => <span style={{ fontWeight: 600, color: '#48465b' }}>{row.name}</span>
    },
    {
      title: 'Company',
      sortable: false,
      render: (row) => row.company ? row.company.name : '—'
    },
    {
      title: 'Created Date',
      data: 'createdAt',
      sortable: true,
      render: (row) => row.createdAt ? new Date(row.createdAt).toLocaleDateString() : '—'
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
            title={`Edit ${activeTab === 'designations' ? 'Designation' : 'Department'}`}
            onClick={() => openEditModal(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title={`Delete ${activeTab === 'designations' ? 'Designation' : 'Department'}`}
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
        title="Designation & Department"
        breadcrumbs={[
          { label: 'Basic Configuration' },
          { label: 'Designation & Department' }
        ]}
        actions={
          <div style={{ display: 'flex', gap: 8 }}>
            <button className="btn btn-outline-danger" onClick={handleDownloadPdf} disabled={isPdfLoading}>
              <i className="bi bi-file-earmark-pdf"></i>
              <span>{isPdfLoading ? 'Exporting...' : 'Export PDF'}</span>
            </button>
            <button className="btn btn-brand" onClick={openCreateModal}>
              <i className="bi bi-plus-lg"></i>
              <span>Add {activeTab === 'designations' ? 'Designation' : 'Department'}</span>
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

      {/* Tabs navigation */}
      <div style={{ marginBottom: 15, display: 'flex', gap: 10 }}>
        <button
          className={`btn ${activeTab === 'designations' ? 'btn-brand' : 'btn-secondary'}`}
          onClick={() => { setActiveTab('designations'); setReloadTrigger(prev => prev + 1); }}
        >
          <i className="bi bi-person-badge"></i> Designations
        </button>
        <button
          className={`btn ${activeTab === 'departments' ? 'btn-brand' : 'btn-secondary'}`}
          onClick={() => { setActiveTab('departments'); setReloadTrigger(prev => prev + 1); }}
        >
          <i className="bi bi-building"></i> Departments
        </button>
      </div>

      <DataTable
        key={activeTab}
        title={activeTab === 'designations' ? 'Designation Records' : 'Department Records'}
        columns={columns}
        fetchData={activeTab === 'designations' ? designationService.load : departmentService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Create / Edit Modal */}
      {isFormOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 500 }}>
            <div className="modal-header">
              <h5>{editingItem ? `Edit ${activeTab === 'designations' ? 'Designation' : 'Department'}` : `Add New ${activeTab === 'designations' ? 'Designation' : 'Department'}`}</h5>
              <button className="btn btn-sm btn-label-secondary" onClick={() => setIsFormOpen(false)}>×</button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body" style={{ padding: '20px' }}>
                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Name *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    placeholder={activeTab === 'designations' ? 'e.g. Manager, Accountant' : 'e.g. Finance, Operations'}
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  />
                </div>
                <div className="form-group" style={{ marginBottom: 15 }}>
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
              <div className="modal-footer" style={{ display: 'flex', justifyContent: 'flex-end', gap: 10 }}>
                <button type="button" className="btn btn-secondary" onClick={() => setIsFormOpen(false)}>Cancel</button>
                <button type="submit" className="btn btn-brand">{editingItem ? 'Update' : 'Create'}</button>
              </div>
            </form>
          </div>
        </div>
      )}

      <ConfirmModal
        isOpen={isDeleteOpen}
        title="Confirm Deletion"
        message={`Are you sure you want to delete ${activeTab === 'designations' ? 'designation' : 'department'} "${itemToDelete?.name}"?`}
        onConfirm={confirmDelete}
        onCancel={() => setIsDeleteOpen(false)}
      />
    </div>
  );
}
