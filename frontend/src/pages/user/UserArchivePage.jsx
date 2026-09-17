import React, { useState } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { userService } from '../../services/userService';

export default function UserArchivePage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);

  const [userToRestore, setUserToRestore] = useState(null);
  const [isRestoreOpen, setIsRestoreOpen] = useState(false);

  const [userToForceDelete, setUserToForceDelete] = useState(null);
  const [isForceDeleteOpen, setIsForceDeleteOpen] = useState(false);

  const confirmRestore = async () => {
    if (!userToRestore) return;
    try {
      await userService.restoreUser(userToRestore.id);
      setAlert({ type: 'success', message: 'User restored successfully!' });
      setReloadTrigger((prev) => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setIsRestoreOpen(false);
      setUserToRestore(null);
    }
  };

  const confirmForceDelete = async () => {
    if (!userToForceDelete) return;
    try {
      await userService.forceDeleteUser(userToForceDelete.id);
      setAlert({ type: 'success', message: 'User permanently deleted!' });
      setReloadTrigger((prev) => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setIsForceDeleteOpen(false);
      setUserToForceDelete(null);
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
      title: 'User',
      data: 'name',
      sortable: true,
      render: (row) => (
        <div>
          <span style={{ fontWeight: 600, color: '#48465b', display: 'block' }}>{row.name}</span>
          <span style={{ fontSize: 11.5, color: '#959cb6' }}>{row.username}</span>
        </div>
      )
    },
    {
      title: 'Email',
      data: 'email',
      sortable: true,
      render: (row) => row.email || '—'
    },
    {
      title: 'Company',
      data: 'companyName',
      sortable: false,
      render: (row) => row.companyName || '—'
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
      width: '120px',
      render: (row) => (
        <div style={{ display: 'flex', gap: 6, justifyContent: 'center' }}>
          <button
            className="btn btn-sm btn-label-success btn-circle btn-icon"
            title="Restore User"
            onClick={() => {
              setUserToRestore(row);
              setIsRestoreOpen(true);
            }}
          >
            <i className="bi bi-arrow-counterclockwise"></i>
          </button>

          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Permanently Delete"
            onClick={() => {
              setUserToForceDelete(row);
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
        title="User Archive"
        breadcrumbs={[{ label: 'Users', link: '/users' }, { label: 'Archive' }]}
      />

      {alert && <Alert type={alert.type} message={alert.message} onClose={() => setAlert(null)} />}

      <DataTable
        title="Archived Users"
        columns={columns}
        fetchData={(params) => userService.loadArchive(params)}
        reloadTrigger={reloadTrigger}
      />

      {/* Restore Confirmation */}
      <ConfirmModal
        isOpen={isRestoreOpen}
        title="Restore User"
        message={`Are you sure you want to restore user "${userToRestore?.name}"?`}
        confirmText="Restore"
        confirmBtnClass="btn-success"
        onConfirm={confirmRestore}
        onCancel={() => setIsRestoreOpen(false)}
      />

      {/* Force Delete Confirmation */}
      <ConfirmModal
        isOpen={isForceDeleteOpen}
        title="Permanently Delete User"
        message={`Are you sure you want to PERMANENTLY delete "${userToForceDelete?.name}"?`}
        confirmText="Delete Forever"
        confirmBtnClass="btn-danger"
        onConfirm={confirmForceDelete}
        onCancel={() => setIsForceDeleteOpen(false)}
      />
    </div>
  );
}
