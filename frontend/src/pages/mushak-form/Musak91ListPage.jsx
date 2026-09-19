import React, { useState, useEffect } from 'react';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import { mushak91Service } from '../../services/mushakService';

export default function Musak91ListPage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalRecords, setTotalRecords] = useState(0);
  const [filteredRecords, setFilteredRecords] = useState(0);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  // Filters
  const [companies, setCompanies] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState('');
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');
  const [search, setSearch] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');

  // Selection & UI
  const [selectedIds, setSelectedIds] = useState([]);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);
  const [reloadTrigger, setReloadTrigger] = useState(0);

  // View modal
  const [viewItem, setViewItem] = useState(null);
  const [isViewOpen, setIsViewOpen] = useState(false);

  useEffect(() => {
    mushak91Service.getFormData()
      .then(res => {
        if (res?.data?.companies) {
          setCompanies(res.data.companies);
        }
      })
      .catch(err => console.error('Failed to load companies:', err));
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search);
      setPage(0);
    }, 400);
    return () => clearTimeout(timer);
  }, [search]);

  useEffect(() => {
    setLoading(true);
    const params = {
      draw: 1,
      start: page * pageSize,
      length: pageSize,
      searchValue: debouncedSearch,
    };
    if (selectedCompany) params.companyId = selectedCompany;
    if (fromDate) params.fromDate = fromDate;
    if (toDate) params.toDate = toDate;

    mushak91Service.load(params)
      .then(res => {
        const d = res.data;
        setData(d.data || []);
        setTotalRecords(d.recordsTotal || 0);
        setFilteredRecords(d.recordsFiltered || 0);
      })
      .catch(err => {
        console.error('Error loading Mushak 9.1:', err);
        setAlert({ type: 'danger', message: 'Failed to load Mushak 9.1 records.' });
      })
      .finally(() => setLoading(false));
  }, [page, pageSize, debouncedSearch, selectedCompany, fromDate, toDate, reloadTrigger]);

  const handleSelectAll = (e) => {
    if (e.target.checked) {
      setSelectedIds(data.map(d => d.id));
    } else {
      setSelectedIds([]);
    }
  };

  const handleSelectRow = (id) => {
    setSelectedIds(prev =>
      prev.includes(id) ? prev.filter(i => i !== id) : [...prev, id]
    );
  };

  const handleDownloadPdf = async (id) => {
    try {
      setIsPdfLoading(true);
      await mushak91Service.downloadPdf(id);
    } catch (err) {
      console.error('PDF download error:', err);
      setAlert({ type: 'danger', message: 'Failed to download PDF.' });
    } finally {
      setIsPdfLoading(false);
    }
  };

  const handleView = async (id) => {
    try {
      const res = await mushak91Service.get(id);
      if (res?.data?.data) {
        setViewItem(res.data.data);
      } else {
        setViewItem(res.data);
      }
      setIsViewOpen(true);
    } catch (err) {
      console.error('Error fetching details:', err);
      setAlert({ type: 'danger', message: 'Failed to load record details.' });
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this Mushak 9.1 record?')) return;
    try {
      await mushak91Service.delete(id);
      setAlert({ type: 'success', message: 'Record deleted successfully.' });
      setReloadTrigger(prev => prev + 1);
    } catch (err) {
      console.error('Delete error:', err);
      setAlert({ type: 'danger', message: 'Failed to delete record.' });
    }
  };

  const totalPages = Math.ceil(filteredRecords / pageSize);

  return (
    <div className="kt-content kt-grid__item kt-grid__item--fluid">
      <Subheader
        title="Mushak 9.1 Forms"
        breadcrumbs={[
          { label: 'Mushak Form', url: '/mushak-form/musak-9-1' },
          { label: 'Mushak 9.1', url: '/mushak-form/musak-9-1' },
          { label: 'List' }
        ]}
      />

      {alert && (
        <div className="container-fluid px-4 pt-3">
          <Alert type={alert.type} message={alert.message} onClose={() => setAlert(null)} />
        </div>
      )}

      <div className="container-fluid px-4 py-3">
        {/* Filter Card */}
        <div className="card shadow-sm mb-4 border-0">
          <div className="card-header bg-white py-3">
            <h5 className="card-title mb-0 text-primary font-weight-bold">
              <i className="fa fa-filter mr-2"></i>Filter Data
            </h5>
          </div>
          <div className="card-body">
            <div className="row align-items-end">
              <div className="col-md-4 mb-3">
                <label className="font-weight-bold">Company</label>
                <select
                  className="form-control"
                  value={selectedCompany}
                  onChange={(e) => { setSelectedCompany(e.target.value); setPage(0); }}
                >
                  <option value="">All Companies</option>
                  {companies.map(c => (
                    <option key={c.id} value={c.id}>{c.name}</option>
                  ))}
                </select>
              </div>
              <div className="col-md-3 mb-3">
                <label className="font-weight-bold">From Date</label>
                <input
                  type="date"
                  className="form-control"
                  value={fromDate}
                  onChange={(e) => { setFromDate(e.target.value); setPage(0); }}
                />
              </div>
              <div className="col-md-3 mb-3">
                <label className="font-weight-bold">To Date</label>
                <input
                  type="date"
                  className="form-control"
                  value={toDate}
                  onChange={(e) => { setToDate(e.target.value); setPage(0); }}
                />
              </div>
              <div className="col-md-2 mb-3">
                <button
                  className="btn btn-outline-secondary btn-block"
                  onClick={() => { setSelectedCompany(''); setFromDate(''); setToDate(''); setSearch(''); }}
                >
                  Reset
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Data Table Card */}
        <div className="card shadow-sm border-0">
          <div className="card-header bg-white py-3 d-flex justify-content-between align-items-center">
            <div>
              <h5 className="card-title mb-0 font-weight-bold text-dark">Mushak 9.1 VAT Returns</h5>
              <small className="text-muted">Total: {totalRecords.toLocaleString()} | Filtered: {filteredRecords.toLocaleString()}</small>
            </div>
            <div className="d-flex align-items-center gap-2">
              <button
                className="btn btn-sm btn-outline-primary mr-2"
                onClick={() => setReloadTrigger(prev => prev + 1)}
                title="Refresh"
              >
                <i className="fa fa-sync-alt mr-1"></i> Refresh
              </button>
            </div>
          </div>

          <div className="card-body p-3">
            {/* Search & PageSize controls */}
            <div className="d-flex justify-content-between align-items-center mb-3 flex-wrap">
              <div className="d-flex align-items-center mb-2">
                <span className="mr-2 text-muted">Show</span>
                <select
                  className="custom-select custom-select-sm form-control form-control-sm mr-2"
                  style={{ width: '80px' }}
                  value={pageSize}
                  onChange={(e) => { setPageSize(Number(e.target.value)); setPage(0); }}
                >
                  <option value={10}>10</option>
                  <option value={25}>25</option>
                  <option value={50}>50</option>
                  <option value={100}>100</option>
                </select>
                <span className="text-muted">entries</span>
              </div>
              <div className="mb-2" style={{ width: '280px' }}>
                <input
                  type="text"
                  className="form-control form-control-sm"
                  placeholder="Search period, company, BIN..."
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                />
              </div>
            </div>

            {/* Table */}
            <div className="table-responsive">
              <table className="table table-bordered table-hover table-striped mb-0 text-nowrap">
                <thead className="thead-light">
                  <tr>
                    <th style={{ width: '40px' }} className="text-center">
                      <input
                        type="checkbox"
                        checked={data.length > 0 && selectedIds.length === data.length}
                        onChange={handleSelectAll}
                      />
                    </th>
                    <th style={{ width: '60px' }} className="text-center">S/N</th>
                    <th>Period</th>
                    <th>Company</th>
                    <th>BIN</th>
                    <th>Period From</th>
                    <th>Period To</th>
                    <th>Due Date</th>
                    <th style={{ width: '150px' }} className="text-center">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {loading ? (
                    <tr>
                      <td colSpan="9" className="text-center py-4">
                        <div className="spinner-border text-primary spinner-border-sm mr-2" role="status"></div>
                        Loading Mushak 9.1 records...
                      </td>
                    </tr>
                  ) : data.length === 0 ? (
                    <tr>
                      <td colSpan="9" className="text-center py-4 text-muted">
                        No records found.
                      </td>
                    </tr>
                  ) : (
                    data.map((row) => (
                      <tr key={row.id}>
                        <td className="text-center">
                          <input
                            type="checkbox"
                            checked={selectedIds.includes(row.id)}
                            onChange={() => handleSelectRow(row.id)}
                          />
                        </td>
                        <td className="text-center font-weight-bold">{row.sn}</td>
                        <td>
                          <span className="badge badge-light-primary px-2 py-1 font-weight-bold">
                            {row.period || '—'}
                          </span>
                        </td>
                        <td className="font-weight-bold text-dark">{row.company || '—'}</td>
                        <td><code>{row.bin || '—'}</code></td>
                        <td>{row.periodFrom || '—'}</td>
                        <td>{row.periodTo || '—'}</td>
                        <td>
                          {row.dueDate ? (
                            <span className="text-danger font-weight-bold">{row.dueDate}</span>
                          ) : '—'}
                        </td>
                        <td className="text-center">
                          <div className="btn-group btn-group-sm">
                            <button
                              className="btn btn-outline-info"
                              onClick={() => handleView(row.id)}
                              title="View Details"
                            >
                              <i className="fa fa-eye"></i>
                            </button>
                            <button
                              className="btn btn-outline-primary"
                              onClick={() => handleDownloadPdf(row.id)}
                              disabled={isPdfLoading}
                              title="Download 9.1 PDF"
                            >
                              <i className="fa fa-file-pdf"></i>
                            </button>
                            <button
                              className="btn btn-outline-danger"
                              onClick={() => handleDelete(row.id)}
                              title="Delete Record"
                            >
                              <i className="fa fa-trash"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            <div className="d-flex justify-content-between align-items-center mt-3 flex-wrap">
              <div className="text-muted small">
                Showing {filteredRecords > 0 ? page * pageSize + 1 : 0} to{' '}
                {Math.min((page + 1) * pageSize, filteredRecords)} of {filteredRecords} entries
              </div>
              <div className="btn-group btn-group-sm">
                <button
                  className="btn btn-outline-secondary"
                  disabled={page === 0 || loading}
                  onClick={() => setPage(p => Math.max(0, p - 1))}
                >
                  Previous
                </button>
                <button className="btn btn-primary disabled" disabled>
                  {page + 1} / {Math.max(1, totalPages)}
                </button>
                <button
                  className="btn btn-outline-secondary"
                  disabled={page >= totalPages - 1 || loading}
                  onClick={() => setPage(p => p + 1)}
                >
                  Next
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* View Modal */}
      {isViewOpen && viewItem && (
        <div className="modal fade show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-lg modal-dialog-scrollable">
            <div className="modal-content border-0 shadow-lg">
              <div className="modal-header bg-primary text-white">
                <h5 className="modal-title font-weight-bold">
                  <i className="fa fa-file-invoice mr-2"></i>
                  Mushak 9.1 Details - {viewItem.slug || viewItem.id}
                </h5>
                <button type="button" className="close text-white" onClick={() => setIsViewOpen(false)}>
                  <span>&times;</span>
                </button>
              </div>
              <div className="modal-body p-4">
                <div className="row mb-3">
                  <div className="col-md-6">
                    <h6 className="text-primary font-weight-bold mb-2">Basic Info</h6>
                    <table className="table table-sm table-borderless">
                      <tbody>
                        <tr><td className="text-muted" style={{ width: '140px' }}>Date:</td><td className="font-weight-bold">{viewItem.date || '—'}</td></tr>
                        <tr><td className="text-muted">Submission Date:</td><td className="font-weight-bold">{viewItem.submisionDate || '—'}</td></tr>
                        <tr><td className="text-muted">Return Type:</td><td><span className="badge badge-info">{viewItem.returnType || 'Main Return (Sec 64)'}</span></td></tr>
                        <tr><td className="text-muted">Any Activity:</td><td>{viewItem.taxPeriodActivity ? 'Yes' : 'No (Zero/Nil Return)'}</td></tr>
                        <tr><td className="text-muted">Refund Desired:</td><td>{viewItem.getRefund ? 'Yes' : 'No'}</td></tr>
                      </tbody>
                    </table>
                  </div>
                  <div className="col-md-6">
                    <h6 className="text-primary font-weight-bold mb-2">Company Information</h6>
                    <table className="table table-sm table-borderless">
                      <tbody>
                        <tr><td className="text-muted" style={{ width: '140px' }}>Company:</td><td className="font-weight-bold">{viewItem.company?.name || '—'}</td></tr>
                        <tr><td className="text-muted">BIN:</td><td><code>{viewItem.company?.bin || '—'}</code></td></tr>
                        <tr><td className="text-muted">Address:</td><td>{viewItem.company?.address || '—'}</td></tr>
                        <tr><td className="text-muted">Economic Activity:</td><td>{viewItem.company?.economicActivity || '—'}</td></tr>
                      </tbody>
                    </table>
                  </div>
                </div>

                <hr />

                <h6 className="text-primary font-weight-bold mb-2">Declaration / Authorized Signatory</h6>
                <div className="row">
                  <div className="col-md-6">
                    <table className="table table-sm table-borderless">
                      <tbody>
                        <tr><td className="text-muted" style={{ width: '140px' }}>Name:</td><td className="font-weight-bold">{viewItem.declarationName || '—'}</td></tr>
                        <tr><td className="text-muted">Designation:</td><td>{viewItem.declarationDesignation || '—'}</td></tr>
                      </tbody>
                    </table>
                  </div>
                  <div className="col-md-6">
                    <table className="table table-sm table-borderless">
                      <tbody>
                        <tr><td className="text-muted" style={{ width: '140px' }}>Phone:</td><td>{viewItem.declarationPhone || '—'}</td></tr>
                        <tr><td className="text-muted">Email:</td><td>{viewItem.declarationEmail || '—'}</td></tr>
                        <tr><td className="text-muted">NID / Passport:</td><td>{viewItem.declarationNidPassport || '—'}</td></tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
              <div className="modal-footer bg-light">
                <button
                  type="button"
                  className="btn btn-primary mr-auto"
                  onClick={() => handleDownloadPdf(viewItem.id)}
                  disabled={isPdfLoading}
                >
                  <i className="fa fa-file-pdf mr-1"></i> Download PDF
                </button>
                <button type="button" className="btn btn-secondary" onClick={() => setIsViewOpen(false)}>
                  Close
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
