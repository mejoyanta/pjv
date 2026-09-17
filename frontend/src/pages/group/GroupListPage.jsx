import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import GroupViewModal from '../../components/group/GroupViewModal';
import { groupService } from '../../services/groupService';

export default function GroupListPage() {
  const navigate = useNavigate();
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);

  const [selectedGroup, setSelectedGroup] = useState(null);
  const [isViewOpen, setIsViewOpen] = useState(false);

  const [groupToDelete, setGroupToDelete] = useState(null);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);

  const handleView = async (slug) => {
    try {
      const res = await groupService.getGroupBySlug(slug);
      if (res?.data) {
        setSelectedGroup(res.data);
        setIsViewOpen(true);
      }
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    }
  };

  const confirmDelete = async () => {
    if (!groupToDelete) return;
    try {
      await groupService.deleteGroup(groupToDelete.id);
      setAlert({ type: 'success', message: 'Group moved to archive successfully!' });
      setReloadTrigger((prev) => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setIsDeleteOpen(false);
      setGroupToDelete(null);
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
      title: 'Assigned Users',
      data: 'usersCount',
      sortable: false,
      align: 'center',
      render: (row) => (
        <span className="badge badge-info">{row.usersCount || 0}</span>
      )
    },
    {
      title: 'Actions',
      sortable: false,
      align: 'center',
      width: '150px',
      render: (row) => (
        <div style={{ display: 'flex', gap: 6, justifyContent: 'center' }}>
          <button
            className="btn btn-sm btn-label-info btn-circle btn-icon"
            title="View Group Details"
            onClick={() => handleView(row.slug)}
          >
            <i className="bi bi-eye"></i>
          </button>

          <button
            className="btn btn-sm btn-label-brand btn-circle btn-icon"
            title="Edit Group"
            onClick={() => navigate(`/groups/${row.id}`)}
          >
            <i className="bi bi-pencil"></i>
          </button>

          <button
            className="btn btn-sm btn-label-warning btn-circle btn-icon"
            title="Access Permissions"
            onClick={() => navigate(`/groups/${row.slug}/access`)}
          >
            <i className="bi bi-shield-lock"></i>
          </button>

          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Archive Group"
            onClick={() => {
              setGroupToDelete(row);
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
        title="User Groups"
        breadcrumbs={[{ label: 'Groups', link: '/groups' }, { label: 'List' }]}
        actions={
          <Link to="/groups/create" className="btn btn-brand">
            <i className="bi bi-plus-lg"></i> Create Group
          </Link>
        }
      />

      {alert && <Alert type={alert.type} message={alert.message} onClose={() => setAlert(null)} />}

      <DataTable
        title="User Groups"
        columns={columns}
        fetchData={(params) => groupService.loadGroups(params)}
        reloadTrigger={reloadTrigger}
      />

      {/* View Modal */}
      <GroupViewModal
        isOpen={isViewOpen}
        group={selectedGroup}
        onClose={() => setIsViewOpen(false)}
      />

      {/* Delete Confirmation */}
      <ConfirmModal
        isOpen={isDeleteOpen}
        title="Archive Group"
        message={`Are you sure you want to move "${groupToDelete?.name}" to archive?`}
        confirmText="Archive"
        confirmBtnClass="btn-danger"
        onConfirm={confirmDelete}
        onCancel={() => setIsDeleteOpen(false)}
      />
    </div>
  );
}
