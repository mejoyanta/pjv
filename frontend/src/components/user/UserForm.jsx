import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { userService } from '../../services/userService';
import { groupService } from '../../services/groupService';
import { companyService } from '../../services/companyService';

export default function UserForm({ initialData = null, isEdit = false }) {
  const navigate = useNavigate();
  const [groups, setGroups] = useState([]);
  const [companies, setCompanies] = useState([]);
  const [designations, setDesignations] = useState([]);
  const [departments, setDepartments] = useState([]);

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    username: '',
    password: '',
    groupId: '',
    companyId: '',
    companyBranchId: '',
    designationId: '',
    departmentId: '',
    contact: '',
    nid: '',
    status: 'ACTIVE'
  });

  const [pictureFile, setPictureFile] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadLookups();
  }, []);

  useEffect(() => {
    if (initialData) {
      setFormData({
        name: initialData.name || '',
        email: initialData.email || '',
        username: initialData.username || '',
        password: '',
        groupId: initialData.groupId || '',
        companyId: initialData.companyId || '',
        companyBranchId: initialData.companyBranchId || '',
        designationId: initialData.designationId || '',
        departmentId: initialData.departmentId || '',
        contact: initialData.contact || '',
        nid: initialData.nid || '',
        status: initialData.status || 'ACTIVE'
      });
    }
  }, [initialData]);

  const loadLookups = async () => {
    try {
      const [groupRes, desRes, depRes] = await Promise.all([
        groupService.getActiveGroups(),
        userService.getDesignations(),
        userService.getDepartments()
      ]);

      if (groupRes?.data) setGroups(groupRes.data);
      if (desRes?.data) setDesignations(desRes.data);
      if (depRes?.data) setDepartments(depRes.data);
    } catch (err) {
      console.error('Error loading user lookups:', err);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);

    const payload = new FormData();
    Object.keys(formData).forEach((key) => {
      if (formData[key] !== null && formData[key] !== undefined && formData[key] !== '') {
        payload.append(key, formData[key]);
      }
    });

    if (pictureFile) {
      payload.append('pictureFile', pictureFile);
    }

    try {
      if (isEdit) {
        await userService.updateUser(initialData.id, payload);
      } else {
        await userService.createUser(payload);
      }
      navigate('/users');
    } catch (err) {
      setError(err.message);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="kt-portlet">
      <div className="kt-portlet__head">
        <div className="kt-portlet__head-title">
          <i className="bi bi-person-gear" style={{ color: 'var(--kt-brand)' }}></i>
          <span>{isEdit ? 'Update User Details' : 'Create New User Account'}</span>
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
              <label className="form-label">Full Name *</label>
              <input
                type="text"
                name="name"
                className="form-control"
                required
                placeholder="e.g. John Doe"
                value={formData.name}
                onChange={handleChange}
              />
            </div>

            <div className="col-md-6 form-group">
              <label className="form-label">Email Address *</label>
              <input
                type="email"
                name="email"
                className="form-control"
                required
                placeholder="john@example.com"
                value={formData.email}
                onChange={handleChange}
              />
            </div>

            <div className="col-md-6 form-group">
              <label className="form-label">Username *</label>
              <input
                type="text"
                name="username"
                className="form-control"
                required
                placeholder="Unique username"
                value={formData.username}
                onChange={handleChange}
              />
            </div>

            <div className="col-md-6 form-group">
              <label className="form-label">{isEdit ? 'Password (leave blank to keep current)' : 'Password *'}</label>
              <input
                type="password"
                name="password"
                className="form-control"
                required={!isEdit}
                placeholder="Min 8 chars with uppercase, lowercase, digit & symbol"
                value={formData.password}
                onChange={handleChange}
              />
              <span className="form-text">Must have at least 1 uppercase, 1 lowercase, 1 digit, and 1 special symbol.</span>
            </div>

            <div className="col-md-6 form-group">
              <label className="form-label">Group / Access Role *</label>
              <select
                name="groupId"
                className="form-control"
                required
                value={formData.groupId}
                onChange={handleChange}
              >
                <option value="">Select Group...</option>
                {groups.map((g) => (
                  <option key={g.id} value={g.id}>{g.name}</option>
                ))}
              </select>
            </div>

            <div className="col-md-6 form-group">
              <label className="form-label">Status</label>
              <select
                name="status"
                className="form-control"
                value={formData.status}
                onChange={handleChange}
              >
                <option value="ACTIVE">Active</option>
                <option value="SUSPENDED">Suspended</option>
                <option value="ARCHIVED">Archived</option>
              </select>
            </div>

            <div className="col-md-6 form-group">
              <label className="form-label">Designation</label>
              <select
                name="designationId"
                className="form-control"
                value={formData.designationId}
                onChange={handleChange}
              >
                <option value="">Select Designation...</option>
                {designations.map((d) => (
                  <option key={d.id} value={d.id}>{d.name}</option>
                ))}
              </select>
            </div>

            <div className="col-md-6 form-group">
              <label className="form-label">Department</label>
              <select
                name="departmentId"
                className="form-control"
                value={formData.departmentId}
                onChange={handleChange}
              >
                <option value="">Select Department...</option>
                {departments.map((d) => (
                  <option key={d.id} value={d.id}>{d.name}</option>
                ))}
              </select>
            </div>

            <div className="col-md-6 form-group">
              <label className="form-label">Contact Number</label>
              <input
                type="text"
                name="contact"
                className="form-control"
                placeholder="e.g. 01711000000"
                value={formData.contact}
                onChange={handleChange}
              />
            </div>

            <div className="col-md-6 form-group">
              <label className="form-label">NID * (10, 13, or 17 digits)</label>
              <input
                type="text"
                name="nid"
                className="form-control"
                required
                placeholder="e.g. 1980123456789"
                value={formData.nid}
                onChange={handleChange}
              />
            </div>

            <div className="col-md-6 form-group">
              <label className="form-label">Profile Picture</label>
              <input
                type="file"
                className="form-control"
                accept="image/*"
                onChange={(e) => setPictureFile(e.target.files[0])}
              />
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 10, marginTop: 25 }}>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => navigate('/users')}
              disabled={submitting}
            >
              Cancel
            </button>
            <button type="submit" className="btn btn-brand" disabled={submitting}>
              {submitting ? 'Saving...' : isEdit ? 'Update User' : 'Create User'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
