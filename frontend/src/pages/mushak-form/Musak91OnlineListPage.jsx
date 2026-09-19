import React, { useState, useEffect } from 'react';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import { mushak91OnlineService } from '../../services/mushakService';

const MONTHS = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December'
];

export default function Musak91OnlineListPage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalRecords, setTotalRecords] = useState(0);
  const [filteredRecords, setFilteredRecords] = useState(0);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  // Filters
  const [companies, setCompanies] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState('');
  const [selectedMonth, setSelectedMonth] = useState('');
  const [selectedYear, setSelectedYear] = useState('');
  const [search, setSearch] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');

  // Selection & UI
  const [selectedIds, setSelectedIds] = useState([]);
  const [alert, setAlert] = useState(null);
  const [reloadTrigger, setReloadTrigger] = useState(0);

  // View modal
  const [viewItem, setViewItem] = useState(null);
  const [isViewOpen, setIsViewOpen] = useState(false);

  // Years for filter
  const currentYear = new Date().getFullYear();
  const years = Array.from({ length: 15 }, (_, i) => currentYear - i);

  useEffect(() => {
    mushak91OnlineService.getFormData()
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
    if (selectedMonth) params.month = selectedMonth;
    if (selectedYear) params.year = selectedYear;

    mushak91OnlineService.load(params)
      .then(res => {
        const d = res.data;
        setData(d.data || []);
        setTotalRecords(d.recordsTotal || 0);
        setFilteredRecords(d.recordsFiltered || 0);
      })
      .catch(err => {
        console.error('Error loading Online 9.1 documents:', err);
        setAlert({ type: 'danger', message: 'Failed to load Online 9.1 records.' });
      })
      .finally(() => setLoading(false));
  }, [page, pageSize, debouncedSearch, selectedCompany, selectedMonth, selectedYear, reloadTrigger]);

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

  const handleView = async (id) => {
    try {
      const res = await mushak91OnlineService.get(id);
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
    if (!window.confirm('Are you sure you want to delete this Online 9.1 document?')) return;
    try {
      await mushak91OnlineService.delete(id);
      setAlert({ type: 'success', message: 'Document record deleted successfully.' });
      setReloadTrigger(prev => prev + 1);
    } catch (err) {
      console.error('Delete error:', err);
      setAlert({ type: 'danger', message: 'Failed to delete document.' });
    }
  };

  const totalPages = Math.ceil(filteredRecords / pageSize);

  return (
    <div className="kt-content kt-grid__item kt-grid__item--fluid">
      <Subheader
        title="Online 9.1 Documents"
        breadcrumbs={[
          { label: 'Mushak Form', url: '/mushak-form/musak-9-1' },
          { label: 'Online 9.1 Documents', url: '/mushak-form/musak-9-1-online' },
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
                <label className="font-weight-bold">Month</label>
                <select
                  className="form-control"
                  value={selectedMonth}
                  onChange={(e) => { setSelectedMonth(e.target.value); setPage(0); }}
                >
                  <option value="">All Months</option>
                  {MONTHS.map(m => (
                    <option key={m} value={m}>{m}</option>
                  ))}
                </select>
              </div>
              <div className="col-md-3 mb-3">
                <label className="font-weight-bold">Year</label>
                <select
                  className="form-control"
                  value={selectedYear}
                  onChange={(e) => { setSelectedYear(e.target.value); setPage(0); }}
                >
                  <option value="">All Years</option>
                  {years.map(y => (
                    <option key={y} value={y}>{y}</option>
                  ))}
                </select>
              </div>
              <div className="col-md-2 mb-3">
                <button
                  className="btn btn-outline-secondary btn-block"
                  onClick={() => { setSelectedCompany(''); setSelectedMonth(''); setSelectedYear(''); setSearch(''); }}
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
              <h5 className="card-title mb-0 font-weight-bold text-dark">Online 9.1 Documents</h5>
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
                    <th>Uploaded Date</th>
                    <th>Company</th>
                    <th>BIN</th>
                    <th>Document</th>
                    <th style={{ width: '120px' }} className="text-center">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {loading ? (
                    <tr>
                      <td colSpan="8" className="text-center py-4">
                        <div className="spinner-border text-primary spinner-border-sm mr-2" role="status"></div>
                        Loading Online 9.1 records...
                      </td>
                    </tr>
                  ) : data.length === 0 ? (
                    <tr>
                      <td colSpan="8" className="text-center py-4 text-muted">
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
                        <td>{row.uploadedDate || '—'}</td>
                        <td className="font-weight-bold text-dark">{row.company || '—'}</td>
                        <td><code>{row.bin || '—'}</code></td>
                        <td>
                          {row.file ? (
                            <span className="badge badge-success px-2 py-1">
                              <i className="fa fa-file-alt mr-1"></i> Available
                            </span>
                          ) : (
                            <span className="badge badge-secondary px-2 py-1">No file</span>
                          )}
                        </td>
                        <td className="text-center">
                          <div className="btn-group btn-group-sm">
                            <button
                              className="btn btn-outline-info"
                              onClick={() => handleView(row.id)}
                              title="View Document Details"
                            >
                              <i className="fa fa-eye"></i>
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
          <div className="modal-dialog modal-md modal-dialog-scrollable">
            <div className="modal-content border-0 shadow-lg">
              <div className="modal-header bg-primary text-white">
                <h5 className="modal-title font-weight-bold">
                  <i className="fa fa-file-invoice mr-2"></i>
                  Online 9.1 Document Details
                </h5>
                <button type="button" className="close text-white" onClick={() => setIsViewOpen(false)}>
                  <span>&times;</span>
                </button>
              </div>
              <div className="modal-body p-4">
                <table className="table table-sm table-bordered">
                  <tbody>
                    <tr>
                      <th className="bg-light" style={{ width: '40%' }}>Period</th>
                      <td>{viewItem.month} {viewItem.year}</td>
                    </tr>
                    <tr>
                      <th className="bg-light">Date</th>
                      <td>{viewItem.date || '—'}</td>
                    </tr>
                    <tr>
                      <th className="bg-light">Company</th>
                      <td className="font-weight-bold">{viewItem.company?.name || '—'}</td>
                    </tr>
                    <tr>
                      <th className="bg-light">BIN</th>
                      <td><code>{viewItem.companyBin || viewItem.company?.bin || '—'}</code></td>
                    </tr>
                    <tr>
                      <th className="bg-light">File Path</th>
                      <td style={{ wordBreak: 'break-all' }}>
                        {viewItem.file || 'No document attached'}
                      </td>
                    </tr>
                    <tr>
                      <th className="bg-light">Created At</th>
                      <td>{viewItem.createdAt || '—'}</td>
                    </tr>
                  </tbody>
                </table>

                {viewItem.file && (
                  <div className="mt-3 p-3 bg-light rounded text-center">
                    <p className="mb-2 text-muted small">Attached Document File:</p>
                    <a
                      href={`/uploads/${viewItem.file}`}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="btn btn-sm btn-outline-primary"
                    >
                      <i className="fa fa-external-link-alt mr-1"></i> Open Document in New Tab
                    </a>
                  </div>
                )}
              </div>
              <div className="modal-footer bg-light">
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
