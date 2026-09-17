import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { groupService } from '../../services/groupService';

export default function GroupForm({ initialData = null, isEdit = false }) {
  const navigate = useNavigate();
  const [groups, setGroups] = useState([]);
  const [formData, setFormData] = useState({
    name: '',
    parentGroupId: '',
    description: '',
    isAdmin: false
  });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    groupService.getActiveGroups()
      .then((res) => {
        if (res?.data) {
          setGroups(res.data.filter((g) => !initialData || g.id !== initialData.id));
        }
      })
      .catch((err) => console.error('Error loading groups:', err));
  }, [initialData]);

  useEffect(() => {
    if (initialData) {
      setFormData({
        name: initialData.name || '',
        parentGroupId: initialData.parentGroupId || '',
        description: initialData.description || '',
        isAdmin: initialData.isAdmin || false
      });
    }
  }, [initialData]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      const payload = {
        ...formData,
        parentGroupId: formData.parentGroupId ? Number(formData.parentGroupId) : null
      };

      if (isEdit) {
        await groupService.updateGroup(initialData.id, payload);
      } else {
        await groupService.createGroup(payload);
      }
      navigate('/groups');
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="kt-portlet">
      <div className="kt-portlet__head">
        <div className="kt-portlet__head-title">
          <i className="bi bi-diagram-3" style={{ color: 'var(--kt-brand)' }}></i>
          <span>{isEdit ? 'Update User Group' : 'Create New User Group'}</span>
        </div>
      </div>

      <div className="kt-portlet__body">
        {error && (
          <div style={{
            padding: '12px 18px', backgroundColor: 'rgba(253, 57, 122, 0.1)',
            borderLeft: '4px solid var(--kt-danger)', color: '#c9164e',
            borderRadius: 4, marginBottom: 20, fontSize: 13, fontWeight: 500
          }}>
            <i className="bi bi-exclamation-octagon-fill" style={{ marginRight: 8 }}></i>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="row">
            <div className="col-md-6 form-group">
              <label className="form-label">Group Name *</label>
              <input
                type="text"
                name="name"
                className="form-control"
                required
                placeholder="e.g. Accounts Manager, Tax Officer"
                value={formData.name}
                onChange={handleChange}
              />
            </div>

            <div className="col-md-6 form-group">
              <label className="form-label">Parent Group (Sub-group of)</label>
              <select
                name="parentGroupId"
                className="form-control"
                value={formData.parentGroupId}
                onChange={handleChange}
              >
                <option value="">None (Top-level Group)</option>
                {groups.map((g) => (
                  <option key={g.id} value={g.id}>{g.name}</option>
                ))}
              </select>
            </div>

            <div className="col-12 form-group">
              <label className="form-label">Description</label>
              <textarea
                name="description"
                className="form-control"
                rows="3"
                placeholder="Describe the responsibility and role of this group..."
                value={formData.description}
                onChange={handleChange}
              />
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 10, marginTop: 20 }}>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => navigate('/groups')}
              disabled={submitting}
            >
              Cancel
            </button>
            <button type="submit" className="btn btn-brand" disabled={submitting}>
              {submitting ? 'Saving...' : isEdit ? 'Update Group' : 'Save Group'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
