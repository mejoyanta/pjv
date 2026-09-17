import React, { useState } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import { subscriptionService } from '../../services/paymentInfoService';

export default function SubscriptionListPage() {
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);

  // Renew Modal state
  const [isRenewOpen, setIsRenewOpen] = useState(false);
  const [companyToRenew, setCompanyToRenew] = useState(null);
  const [renewData, setRenewData] = useState({ months: 12, packageType: 'Annual Pro' });

  const openRenewModal = (company) => {
    setCompanyToRenew(company);
    setRenewData({
      months: 12,
      packageType: company.packageType || 'Standard'
    });
    setIsRenewOpen(true);
  };

  const handleRenew = async (e) => {
    e.preventDefault();
    if (!companyToRenew) return;
    try {
      await subscriptionService.renew(companyToRenew.id, renewData);
      setAlert({ type: 'success', message: `Subscription renewed successfully for ${companyToRenew.name}!` });
      setIsRenewOpen(false);
      setCompanyToRenew(null);
      setReloadTrigger(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    }
  };

  const handleDownloadPdf = async () => {
    setIsPdfLoading(true);
    try {
      await subscriptionService.downloadPdf();
    } catch (err) {
      setAlert({ type: 'danger', message: 'Failed to download PDF report' });
    } finally {
      setIsPdfLoading(false);
    }
  };

  const columns = [
    {
      title: '#',
      sortable: false,
      width: '60px',
      align: 'center',
      render: (row, idx) => idx
    },
    {
      title: 'Company Name',
      data: 'name',
      sortable: true,
      render: (row) => (
        <div>
          <span style={{ fontWeight: 600, color: '#48465b' }}>{row.name}</span>
          <br />
          <small className="text-muted">{row.email || '—'}</small>
        </div>
      )
    },
    {
      title: 'Contact Phone',
      data: 'phone',
      sortable: true,
      render: (row) => row.phone || '—'
    },
    {
      title: 'Package Plan',
      data: 'packageType',
      sortable: true,
      render: (row) => (
        <span className="kt-badge kt-badge--inline kt-badge--brand" style={{ fontWeight: 600 }}>
          {row.packageType || 'Standard'}
        </span>
      )
    },
    {
      title: 'Expiry Date',
      data: 'expireDate',
      sortable: true,
      render: (row) => row.expireDate || '—'
    },
    {
      title: 'Days Left',
      data: 'remainingDays',
      sortable: true,
      align: 'center',
      render: (row) => {
        const days = row.remainingDays;
        if (days === null || days === undefined) return <span className="text-muted">—</span>;
        let badgeClass = 'kt-badge--success';
        if (days <= 0) badgeClass = 'kt-badge--danger';
        else if (days <= 30) badgeClass = 'kt-badge--warning';
        return (
          <span className={`kt-badge kt-badge--inline ${badgeClass}`} style={{ fontWeight: 600 }}>
            {days <= 0 ? 'Expired' : `${days} Days`}
          </span>
        );
      }
    },
    {
      title: 'Status',
      data: 'status',
      sortable: true,
      render: (row) => {
        const isActive = (row.status || '').toLowerCase() === 'active';
        return (
          <span className={`kt-badge kt-badge--inline ${isActive ? 'kt-badge--success' : 'kt-badge--danger'}`} style={{ fontWeight: 600 }}>
            {row.status || 'Active'}
          </span>
        );
      }
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
            title="Renew Subscription"
            onClick={() => openRenewModal(row)}
          >
            <i className="bi bi-arrow-repeat"></i>
          </button>
        </div>
      )
    }
  ];

  return (
    <div>
      <Subheader
        title="Subscription"
        breadcrumbs={[
          { label: 'Payment Information' },
          { label: 'Subscription' }
        ]}
        actions={
          <div style={{ display: 'flex', gap: 8 }}>
            <button className="btn btn-outline-danger" onClick={handleDownloadPdf} disabled={isPdfLoading}>
              <i className="bi bi-file-earmark-pdf"></i>
              <span>{isPdfLoading ? 'Exporting...' : 'Export PDF'}</span>
            </button>
          </div>
        }
      />

      {alert && (
        <Alert
          type={alert.type}
          message={alert.message}
          onClose={() => setAlert(null)}
        />
      )}

      <DataTable
        title="Company Subscriptions"
        columns={columns}
        fetchData={subscriptionService.load}
        reloadTrigger={reloadTrigger}
      />

      {/* Renew Modal */}
      {isRenewOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 480 }}>
            <div className="modal-header">
              <h5>Renew Subscription: {companyToRenew?.name}</h5>
              <button className="btn btn-sm btn-label-secondary" onClick={() => setIsRenewOpen(false)}>×</button>
            </div>
            <form onSubmit={handleRenew}>
              <div className="modal-body" style={{ padding: '20px' }}>
                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Package Plan</label>
                  <input
                    type="text"
                    className="form-control"
                    value={renewData.packageType}
                    onChange={(e) => setRenewData({ ...renewData, packageType: e.target.value })}
                  />
                </div>

                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Extension Duration (Months) *</label>
                  <select
                    className="form-control"
                    value={renewData.months}
                    onChange={(e) => setRenewData({ ...renewData, months: parseInt(e.target.value) || 12 })}
                  >
                    <option value={1}>1 Month Extension</option>
                    <option value={3}>3 Months Extension</option>
                    <option value={6}>6 Months Extension</option>
                    <option value={12}>12 Months (1 Year)</option>
                    <option value={24}>24 Months (2 Years)</option>
                  </select>
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setIsRenewOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-brand">
                  Confirm Renewal
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
