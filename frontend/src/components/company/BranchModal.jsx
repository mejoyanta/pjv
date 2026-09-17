import React, { useState, useEffect } from 'react';
import { branchService } from '../../services/branchService';

export default function BranchModal({ isOpen, company, onClose }) {
  const [branches, setBranches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isCreating, setIsCreating] = useState(false);

  // Form State
  const [branchName, setBranchName] = useState('');
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');
  const [address, setAddress] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (isOpen && company?.id) {
      loadBranches();
    }
  }, [isOpen, company]);

  const loadBranches = async () => {
    setLoading(true);
    try {
      const res = await branchService.getBranches(company.id);
      if (res?.data) {
        setBranches(res.data);
      }
    } catch (err) {
      console.error('Error loading branches:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateBranch = async (e) => {
    e.preventDefault();
    if (!branchName.trim()) return;

    setSaving(true);
    setError(null);

    try {
      await branchService.createBranch({
        companyId: company.id,
        name: branchName,
        phone,
        email,
        address,
        isMain: false
      });
      setBranchName('');
      setPhone('');
      setEmail('');
      setAddress('');
      setIsCreating(false);
      loadBranches();
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  if (!isOpen || !company) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content" style={{ maxWidth: 700 }}>
        <div className="modal-header">
          <h4 className="modal-title">Branches &mdash; {company.name}</h4>
          <button
            onClick={onClose}
            style={{ background: 'none', border: 'none', fontSize: 22, cursor: 'pointer', color: '#959cb6' }}
          >
            &times;
          </button>
        </div>

        <div className="modal-body">
          {error && (
            <div style={{ color: 'var(--kt-danger)', marginBottom: 15, fontSize: 13 }}>{error}</div>
          )}

          {!isCreating ? (
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 15 }}>
                <span style={{ fontSize: 13, fontWeight: 600 }}>Registered Branches ({branches.length})</span>
                <button
                  className="btn btn-brand btn-sm"
                  onClick={() => setIsCreating(true)}
                >
                  <i className="bi bi-plus-lg"></i> + Add New Branch
                </button>
              </div>

              {loading ? (
                <div style={{ textAlign: 'center', padding: 20 }}>Loading branches...</div>
              ) : branches.length === 0 ? (
                <div style={{ textAlign: 'center', padding: 25, color: '#959cb6' }}>
                  No extra branches registered yet.
                </div>
              ) : (
                <div className="table-responsive">
                  <table className="table">
                    <thead>
                      <tr>
                        <th>Branch Name</th>
                        <th>Phone</th>
                        <th>Email</th>
                        <th>Address</th>
                        <th>Type</th>
                      </tr>
                    </thead>
                    <tbody>
                      {branches.map((b) => (
                        <tr key={b.id}>
                          <td style={{ fontWeight: 600, color: '#48465b' }}>{b.name}</td>
                          <td>{b.phone || '—'}</td>
                          <td>{b.email || '—'}</td>
                          <td style={{ fontSize: 12 }}>{b.address || '—'}</td>
                          <td>
                            {b.isMain ? (
                              <span className="badge badge-success">Main Branch</span>
                            ) : (
                              <span className="badge badge-info">Sub Branch</span>
                            )}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          ) : (
            <form onSubmit={handleCreateBranch}>
              <h5 style={{ fontSize: 13, fontWeight: 700, color: 'var(--kt-brand)', marginBottom: 15 }}>
                Create New Company Branch
              </h5>
              <div className="row">
                <div className="col-md-6 form-group">
                  <label className="form-label">Branch Name *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    placeholder="e.g. Chittagong Branch"
                    value={branchName}
                    onChange={(e) => setBranchName(e.target.value)}
                  />
                </div>
                <div className="col-md-6 form-group">
                  <label className="form-label">Phone</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Branch phone number"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                  />
                </div>
                <div className="col-md-6 form-group">
                  <label className="form-label">Email</label>
                  <input
                    type="email"
                    className="form-control"
                    placeholder="branch@company.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                  />
                </div>
                <div className="col-md-6 form-group">
                  <label className="form-label">Address</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Physical branch address"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                  />
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 10, marginTop: 15 }}>
                <button
                  type="button"
                  className="btn btn-secondary btn-sm"
                  onClick={() => setIsCreating(false)}
                >
                  Cancel
                </button>
                <button type="submit" className="btn btn-brand btn-sm" disabled={saving}>
                  {saving ? 'Creating...' : 'Save Branch'}
                </button>
              </div>
            </form>
          )}
        </div>

        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose}>Close</button>
        </div>
      </div>
    </div>
  );
}
