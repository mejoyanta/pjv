import React, { useState } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { groupService } from '../../services/groupService';

export default function GroupArchivePage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);

  const [groupToRestore, setGroupToRestore] = useState(null);
  const [isRestoreOpen, setIsRestoreOpen] = useState(false);

  const [groupToForceDelete, setGroupToForceDelete] = useState(null);
  const [isForceDeleteOpen, setIsForceDeleteOpen] = useState(false);

  const confirmRestore = async () => {
    if (!groupToRestore) return;
    try {
      await groupService.restoreGroup(groupToRestore.id);
      setAlert({ type: 'success', message: 'Group restored successfully!' });
      setReloadTrigger((prev) => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setIsRestoreOpen(false);
      setGroupToRestore(null);
    }
  };

  const confirmForceDelete = async () => {
    if (!groupToForceDelete) return;
    try {
      await groupService.forceDeleteGroup(groupToForceDelete.id);
      setAlert({ type: 'success', message: 'Group permanently deleted!' });
      setReloadTrigger((prev) => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setIsForceDeleteOpen(false);
      setGroupToForceDelete(null);
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
      title: 'Mother Admin',
      data: 'motherAdmin',
      sortable: false,
      render: (row) => row.motherAdmin || '—'
    },
    {
      title: 'Admin',
      data: 'admin',
      sortable: false,
      render: (row) => row.admin || '—'
    },
    {
      title: 'Group Name',
      data: 'name',
      sortable: true,
      render: (row) => (
        <span style={{ fontWeight: 600, color: '#48465b' }}>{row.name}</span>
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
            className="btn btn-sm btn-label-success btn-circle btn-icon"
            title="Restore Group"
            onClick={() => {
              setGroupToRestore(row);
              setIsRestoreOpen(true);
            }}
          >
            <i className="bi bi-arrow-counterclockwise"></i>
          </button>

          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Permanently Delete"
            onClick={() => {
              setGroupToForceDelete(row);
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
        title="Group Archive"
        breadcrumbs={[{ label: 'Groups', link: '/groups' }, { label: 'Archive' }]}
      />

      {alert && <Alert type={alert.type} message={alert.message} onClose={() => setAlert(null)} />}

      <DataTable
        title="Archived Groups"
        columns={columns}
        fetchData={(params) => groupService.loadArchive(params)}
        reloadTrigger={reloadTrigger}
      />

      <ConfirmModal
        isOpen={isRestoreOpen}
        title="Restore Group"
        message={`Are you sure you want to restore group "${groupToRestore?.name}"?`}
        confirmText="Restore"
        confirmBtnClass="btn-success"
        onConfirm={confirmRestore}
        onCancel={() => setIsRestoreOpen(false)}
      />

      <ConfirmModal
        isOpen={isForceDeleteOpen}
        title="Permanently Delete Group"
        message={`Are you sure you want to PERMANENTLY delete "${groupToForceDelete?.name}"?`}
        confirmText="Delete Forever"
        confirmBtnClass="btn-danger"
        onConfirm={confirmForceDelete}
        onCancel={() => setIsForceDeleteOpen(false)}
      />
    </div>
  );
}
