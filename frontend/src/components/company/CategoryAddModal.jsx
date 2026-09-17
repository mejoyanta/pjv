import React, { useState } from 'react';
import { companyService } from '../../services/companyService';

export default function CategoryAddModal({ isOpen, onClose, onCategoryCreated }) {
  const [name, setName] = useState('');
  const [type, setType] = useState('OTHERS');
  const [description, setDescription] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!name.trim()) {
      setError('Category name is required');
      return;
    }

    setSubmitting(true);
    setError(null);

    try {
      const res = await companyService.createCategory({ name, type, description });
      if (res && res.data) {
        onCategoryCreated(res.data);
        onClose();
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content" style={{ maxWidth: 500 }}>
        <div className="modal-header">
          <h4 className="modal-title">Add Company Category</h4>
          <button
            onClick={onClose}
            style={{ background: 'none', border: 'none', fontSize: 20, cursor: 'pointer', color: '#959cb6' }}
          >
            &times;
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            {error && (
              <div style={{ color: 'var(--kt-danger)', marginBottom: 15, fontSize: 12 }}>{error}</div>
            )}

            <div className="form-group">
              <label className="form-label">Category Name *</label>
              <input
                type="text"
                className="form-control"
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="e.g. Manufacturer, IT Service..."
              />
            </div>

            <div className="form-group">
              <label className="form-label">Category Type</label>
              <select
                className="form-control"
                value={type}
                onChange={(e) => setType(e.target.value)}
              >
                <option value="MANUFACTURER">Manufacturer</option>
                <option value="IMPORTER">Importer</option>
                <option value="TRADERS">Traders</option>
                <option value="SERVICE">Service</option>
                <option value="OTHERS">Others</option>
              </select>
            </div>

            <div className="form-group" style={{ marginBottom: 0 }}>
              <label className="form-label">Description</label>
              <textarea
                className="form-control"
                rows="3"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Brief description of the category..."
              />
            </div>
          </div>

          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={submitting}>
              Cancel
            </button>
            <button type="submit" className="btn btn-brand" disabled={submitting}>
              {submitting ? 'Creating...' : 'Save Category'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
