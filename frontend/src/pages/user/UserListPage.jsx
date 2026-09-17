import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import StatsCard from '../../components/common/StatsCard';
import ConfirmModal from '../../components/common/ConfirmModal';
import UserViewModal from '../../components/user/UserViewModal';
import { userService } from '../../services/userService';

export default function UserListPage() {
  const navigate = useNavigate();
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [stats, setStats] = useState({ total: 0, online: 0, active: 0 });
  const [alert, setAlert] = useState(null);

  // View state
  const [selectedUser, setSelectedUser] = useState(null);
  const [isViewOpen, setIsViewOpen] = useState(false);

  // Delete state
  const [userToDelete, setUserToDelete] = useState(null);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);

  useEffect(() => {
    userService.getStats()
      .then((res) => {
        if (res?.data) setStats(res.data);
      })
      .catch((err) => console.error('Error loading stats:', err));
  }, [reloadTrigger]);

  const handleView = async (id) => {
    try {
      const res = await userService.getUser(id);
      if (res?.data) {
        setSelectedUser(res.data);
        setIsViewOpen(true);
      }
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    }
  };

  const confirmDelete = async () => {
    if (!userToDelete) return;
    try {
      await userService.deleteUser(userToDelete.id);
      setAlert({ type: 'success', message: 'User moved to archive successfully!' });
      setReloadTrigger((prev) => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setIsDeleteOpen(false);
      setUserToDelete(null);
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
      render: (row) => {
        const initials = row.name ? row.name.substring(0, 2).toUpperCase() : '?';
        return (
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <div className="kt-badge-avatar" style={{ width: 34, height: 34, fontSize: 12 }}>
              {initials}
            </div>
            <div>
              <span style={{ fontWeight: 600, color: '#48465b', display: 'block' }}>{row.name}</span>
              <span style={{ fontSize: 11.5, color: '#959cb6' }}>{row.username}</span>
            </div>
          </div>
        );
      }
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
      title: 'Role / Group',
      data: 'groupName',
      sortable: true,
      render: (row) => (
        <span className="badge badge-info">{row.groupName || 'User'}</span>
      )
    },
    {
      title: 'Designation',
      data: 'designationName',
      sortable: false,
      render: (row) => row.designationName || '—'
    },
    {
      title: 'Online',
      data: 'isOnline',
      sortable: true,
      align: 'center',
      render: (row) => (
        <span className={`badge ${row.isOnline ? 'badge-success' : 'badge-dark'}`}>
          {row.isOnline ? 'Online' : 'Offline'}
        </span>
      )
    },
    {
      title: 'Status',
      data: 'status',
      sortable: true,
      align: 'center',
      render: (row) => (
        <span className={`badge ${row.status === 'ACTIVE' || row.status === 'active' ? 'badge-success' : 'badge-danger'}`}>
          {row.status}
        </span>
      )
    },
    {
      title: 'Actions',
      sortable: false,
      align: 'center',
      width: '130px',
      render: (row) => (
        <div style={{ display: 'flex', gap: 6, justifyContent: 'center' }}>
          <button
            className="btn btn-sm btn-label-info btn-circle btn-icon"
            title="View User Details"
            onClick={() => handleView(row.id)}
          >
            <i className="bi bi-eye"></i>
          </button>

          <button
            className="btn btn-sm btn-label-brand btn-circle btn-icon"
            title="Edit User"
            onClick={() => navigate(`/users/${row.id}`)}
          >
            <i className="bi bi-pencil"></i>
          </button>

          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Archive User"
            onClick={() => {
              setUserToDelete(row);
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
        title="User Accounts"
        breadcrumbs={[{ label: 'Users', link: '/users' }, { label: 'List' }]}
        actions={
          <Link to="/users/create" className="btn btn-brand">
            <i className="bi bi-person-plus"></i> Create User
          </Link>
        }
      />

      {alert && <Alert type={alert.type} message={alert.message} onClose={() => setAlert(null)} />}

      {/* Stats Cards: Total, Online, Active */}
      <div className="stats-grid">
        <StatsCard
          icon="bi bi-people-fill"
          label="Total Users"
          value={stats.total}
          color="var(--kt-brand)"
          bg="rgba(93, 120, 255, 0.1)"
        />
        <StatsCard
          icon="bi bi-broadcast"
          label="Online Now"
          value={stats.online}
          color="var(--kt-success)"
          bg="rgba(10, 187, 135, 0.1)"
        />
        <StatsCard
          icon="bi bi-person-check-fill"
          label="Active Status"
          value={stats.active}
          color="#00bcd4"
          bg="rgba(0, 188, 212, 0.1)"
        />
      </div>

      <DataTable
        title="System Users"
        columns={columns}
        fetchData={(params) => userService.loadUsers(params)}
        reloadTrigger={reloadTrigger}
      />

      {/* View Modal */}
      <UserViewModal
        isOpen={isViewOpen}
        user={selectedUser}
        onClose={() => setIsViewOpen(false)}
      />

      {/* Delete Confirmation */}
      <ConfirmModal
        isOpen={isDeleteOpen}
        title="Archive User"
        message={`Are you sure you want to move user "${userToDelete?.name}" to archive?`}
        confirmText="Archive"
        confirmBtnClass="btn-danger"
        onConfirm={confirmDelete}
        onCancel={() => setIsDeleteOpen(false)}
      />
    </div>
  );
}
