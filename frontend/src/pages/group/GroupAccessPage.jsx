import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import { groupService } from '../../services/groupService';

export default function GroupAccessPage() {
  const { slug } = useParams();
  const navigate = useNavigate();
  const [group, setGroup] = useState(null);
  const [selectedPermissions, setSelectedPermissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [alert, setAlert] = useState(null);

  // Grouped system permission definitions
  const permissionModules = [
    {
      module: 'Company Information',
      permissions: [
        { key: 'company_list', label: 'View Company List' },
        { key: 'company_create', label: 'Create New Company' },
        { key: 'company_update', label: 'Edit & Update Company' },
        { key: 'company_delete', label: 'Move Company to Archive' },
        { key: 'company_archive', label: 'View Archived Companies' },
        { key: 'company_restore', label: 'Restore Company' },
        { key: 'company_force_delete', label: 'Permanent Delete Company' },
        { key: 'company_download', label: 'Download Company PDF' },
        { key: 'company_documents', label: 'Manage Company Documents' }
      ]
    },
    {
      module: 'User Management',
      permissions: [
        { key: 'user_list', label: 'View Users List' },
        { key: 'user_create', label: 'Create User' },
        { key: 'user_update', label: 'Edit User' },
        { key: 'user_delete', label: 'Archive User' },
        { key: 'user_restore', label: 'Restore User' },
        { key: 'user_force_delete', label: 'Permanent Delete User' },
        { key: 'user_login', label: 'Login as User (Impersonate)' },
        { key: 'username_manage', label: 'Manage / Sync Company Usernames' }
      ]
    },
    {
      module: 'Group & Access Control',
      permissions: [
        { key: 'group_list', label: 'View Groups List' },
        { key: 'group_create', label: 'Create Group' },
        { key: 'group_update', label: 'Edit Group' },
        { key: 'group_delete', label: 'Archive Group' },
        { key: 'group_restore', label: 'Restore Group' },
        { key: 'group_force_delete', label: 'Permanent Delete Group' },
        { key: 'group_access', label: 'Manage Permission Matrix' }
      ]
    }
  ];

  useEffect(() => {
    let isMounted = true;
    Promise.all([
      groupService.getGroupBySlug(slug),
      groupService.getGroupAccess(slug)
    ])
      .then(([groupRes, accessRes]) => {
        if (isMounted) {
          if (groupRes?.data) setGroup(groupRes.data);
          if (accessRes?.data) setSelectedPermissions(accessRes.data);
        }
      })
      .catch((err) => {
        if (isMounted) setAlert({ type: 'danger', message: err.message });
      })
      .finally(() => {
        if (isMounted) setLoading(false);
      });

    return () => { isMounted = false; };
  }, [slug]);

  const handleToggle = (key) => {
    setSelectedPermissions((prev) =>
      prev.includes(key) ? prev.filter((k) => k !== key) : [...prev, key]
    );
  };

  const handleSelectAllModule = (modulePermissions, selectAll) => {
    const keys = modulePermissions.map((p) => p.key);
    if (selectAll) {
      setSelectedPermissions((prev) => Array.from(new Set([...prev, ...keys])));
    } else {
      setSelectedPermissions((prev) => prev.filter((k) => !keys.includes(k)));
    }
  };

  const handleSave = async () => {
    setSaving(true);
    setAlert(null);
    try {
      await groupService.saveGroupAccess(slug, selectedPermissions);
      setAlert({ type: 'success', message: 'Group permissions updated successfully!' });
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div style={{ padding: 40, textAlign: 'center', color: 'var(--kt-brand)' }}>
        <i className="fa fa-spinner fa-spin" style={{ marginRight: 8 }}></i> Loading permissions...
      </div>
    );
  }

  return (
    <div>
      <Subheader
        title={`Permission Access Control: ${group?.name || slug}`}
        breadcrumbs={[
          { label: 'Groups', link: '/groups' },
          { label: 'Access Matrix' }
        ]}
        actions={
          <div style={{ display: 'flex', gap: 10 }}>
            <button className="btn btn-secondary" onClick={() => navigate('/groups')}>
              Back
            </button>
            <button className="btn btn-brand" onClick={handleSave} disabled={saving}>
              {saving ? 'Saving...' : 'Save Permissions'}
            </button>
          </div>
        }
      />

      {alert && <Alert type={alert.type} message={alert.message} onClose={() => setAlert(null)} />}

      <div className="row">
        {permissionModules.map((mod, mIdx) => {
          const allSelected = mod.permissions.every((p) => selectedPermissions.includes(p.key));

          return (
            <div className="col-12" key={mIdx}>
              <div className="kt-portlet">
                <div className="kt-portlet__head" style={{ backgroundColor: '#fafbfc' }}>
                  <div className="kt-portlet__head-title">
                    <i className="bi bi-shield-lock" style={{ color: 'var(--kt-brand)' }}></i>
                    <span>{mod.module}</span>
                  </div>
                  <div>
                    <button
                      type="button"
                      className="btn btn-sm btn-secondary"
                      onClick={() => handleSelectAllModule(mod.permissions, !allSelected)}
                    >
                      {allSelected ? 'Deselect All' : 'Select All'}
                    </button>
                  </div>
                </div>

                <div className="kt-portlet__body">
                  <div className="row">
                    {mod.permissions.map((perm) => {
                      const isChecked = selectedPermissions.includes(perm.key);
                      return (
                        <div className="col-md-4" key={perm.key} style={{ marginBottom: 14 }}>
                          <label
                            style={{
                              display: 'flex',
                              alignItems: 'center',
                              gap: 10,
                              cursor: 'pointer',
                              padding: '8px 12px',
                              borderRadius: 4,
                              backgroundColor: isChecked ? 'rgba(93, 120, 255, 0.06)' : '#fff',
                              border: isChecked ? '1px solid #7c90ff' : '1px solid #e2e5ec',
                              transition: 'all 0.15s ease'
                            }}
                          >
                            <input
                              type="checkbox"
                              checked={isChecked}
                              onChange={() => handleToggle(perm.key)}
                              style={{ width: 16, height: 16, accentColor: 'var(--kt-brand)' }}
                            />
                            <span style={{ fontSize: 13, color: isChecked ? 'var(--kt-brand)' : '#48465b', fontWeight: isChecked ? 600 : 400 }}>
                              {perm.label}
                            </span>
                          </label>
                        </div>
                      );
                    })}
                  </div>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 10, marginBottom: 40 }}>
        <button className="btn btn-secondary" onClick={() => navigate('/groups')} disabled={saving}>
          Cancel
        </button>
        <button className="btn btn-brand" onClick={handleSave} disabled={saving}>
          {saving ? 'Saving...' : 'Save Permissions'}
        </button>
      </div>
    </div>
  );
}
