import React, { useState } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { companyService } from '../../services/companyService';

export default function CompanyArchivePage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);

  // Restore Modal State
  const [companyToRestore, setCompanyToRestore] = useState(null);
  const [isRestoreOpen, setIsRestoreOpen] = useState(false);

  // Force Delete Modal State
  const [companyToForceDelete, setCompanyToForceDelete] = useState(null);
  const [isForceDeleteOpen, setIsForceDeleteOpen] = useState(false);

  const confirmRestore = async () => {
    if (!companyToRestore) return;
    try {
      await companyService.restoreCompany(companyToRestore.id);
      setAlert({ type: 'success', message: 'Company restored successfully!' });
      setReloadTrigger((prev) => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setIsRestoreOpen(false);
      setCompanyToRestore(null);
    }
  };

  const confirmForceDelete = async () => {
    if (!companyToForceDelete) return;
    try {
      await companyService.forceDeleteCompany(companyToForceDelete.id);
      setAlert({ type: 'success', message: 'Company permanently deleted!' });
      setReloadTrigger((prev) => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setIsForceDeleteOpen(false);
      setCompanyToForceDelete(null);
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
      title: 'Company Name',
      data: 'name',
      sortable: true,
      render: (row) => (
        <div>
          <span style={{ fontWeight: 600, color: '#48465b', display: 'block' }}>{row.name}</span>
          <span style={{ fontSize: 11.5, color: '#959cb6' }}>ID: {row.username}</span>
        </div>
      )
    },
    {
      title: 'BIN',
      data: 'bin',
      sortable: true,
      render: (row) => row.bin || 'N/A'
    },
    {
      title: 'Email',
      data: 'email',
      sortable: true,
      render: (row) => row.email || 'N/A'
    },
    {
      title: 'Category',
      data: 'categoryName',
      sortable: false,
      render: (row) => (
        <span className="badge badge-dark">{row.categoryName || 'General'}</span>
      )
    },
    {
      title: 'Deleted At',
      data: 'deletedAt',
      sortable: true,
      render: (row) => (row.deletedAt ? new Date(row.deletedAt).toLocaleDateString() : '—')
    },
    {
      title: 'Actions',
      sortable: false,
      align: 'center',
      width: '140px',
      render: (row) => (
        <div style={{ display: 'flex', gap: 6, justifyContent: 'center' }}>
          {/* Restore Action */}
          <button
            className="btn btn-sm btn-label-success btn-circle btn-icon"
            title="Restore Company"
            onClick={() => {
              setCompanyToRestore(row);
              setIsRestoreOpen(true);
            }}
          >
            <i className="bi bi-arrow-counterclockwise"></i>
          </button>

          {/* Force Delete Action */}
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Permanent Delete"
            onClick={() => {
              setCompanyToForceDelete(row);
              setIsForceDeleteOpen(true);
            }}
          >
            <i className="bi bi-trash3-fill"></i>
          </button>
        </div>
      )
    }
  ];

  return (
    <div>
      <Subheader
        title="Company Archive"
        breadcrumbs={[{ label: 'Company', link: '/company' }, { label: 'Archive' }]}
      />

      {alert && (
        <Alert
          type={alert.type}
          message={alert.message}
          onClose={() => setAlert(null)}
        />
      )}

      <DataTable
        title="Archived Companies"
        columns={columns}
        fetchData={companyService.loadArchive}
        reloadTrigger={reloadTrigger}
      />

      {/* Restore Confirmation */}
      <ConfirmModal
        isOpen={isRestoreOpen}
        title="Restore Company"
        message={`Are you sure you want to restore "${companyToRestore?.name}" back to the active list?`}
        confirmText="Restore"
        confirmBtnClass="btn-success"
        onConfirm={confirmRestore}
        onCancel={() => setIsRestoreOpen(false)}
      />

      {/* Force Delete Confirmation */}
      <ConfirmModal
        isOpen={isForceDeleteOpen}
        title="Permanent Delete"
        message={`WARNING: Are you sure you want to PERMANENTLY delete "${companyToForceDelete?.name}"? This action cannot be undone.`}
        confirmText="Delete Forever"
        confirmBtnClass="btn-danger"
        onConfirm={confirmForceDelete}
        onCancel={() => setIsForceDeleteOpen(false)}
      />
    </div>
  );
}
