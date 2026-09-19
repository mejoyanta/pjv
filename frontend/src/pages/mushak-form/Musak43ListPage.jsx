import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { mushak43Service } from '../../services/mushakService';

export default function Musak43ListPage() {
  const navigate = useNavigate();

  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalRecords, setTotalRecords] = useState(0);
  const [filteredRecords, setFilteredRecords] = useState(0);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [sortColumn, setSortColumn] = useState('id');
  const [sortDir, setSortDir] = useState('desc');

  // Filter States
  const [companies, setCompanies] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState('');
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');
  const [search, setSearch] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');

  // Selection & UI States
  const [selectedIds, setSelectedIds] = useState([]);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);
  const [reloadTrigger, setReloadTrigger] = useState(0);

  // Modals
  const [viewItem, setViewItem] = useState(null);
  const [isViewOpen, setIsViewOpen] = useState(false);
  const [itemToDelete, setItemToDelete] = useState(null);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);

  useEffect(() => {
    mushak43Service.getFormData()
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

    mushak43Service.load(params)
      .then(res => {
        setData(res.data || []);
        setTotalRecords(res.recordsTotal || 0);
        setFilteredRecords(res.recordsFiltered || 0);
      })
      .catch(err => {
        console.error('Failed to load Mushak 4.3:', err);
        setAlert({ type: 'danger', message: 'Failed to load Mushak 4.3 data.' });
      })
      .finally(() => setLoading(false));
  }, [page, pageSize, debouncedSearch, selectedCompany, fromDate, toDate, reloadTrigger]);

  const handleSelectAll = (e) => {
    if (e.target.checked) {
      setSelectedIds(data.map(item => item.id));
    } else {
      setSelectedIds([]);
    }
  };

  const handleSelectRow = (id) => {
    if (selectedIds.includes(id)) {
      setSelectedIds(selectedIds.filter(item => item !== id));
    } else {
      setSelectedIds([...selectedIds, id]);
    }
  };

  const handlePdfDownload = async (id) => {
    try {
      setIsPdfLoading(true);
      await mushak43Service.downloadPdf(id);
    } catch (err) {
      setAlert({ type: 'danger', message: 'Failed to download PDF.' });
    } finally {
      setIsPdfLoading(false);
    }
  };

  const totalPages = Math.ceil(filteredRecords / pageSize);

  return (
    <div className="kt-content kt-grid__item kt-grid__item--fluid" id="kt_content">
      <Subheader
        title="4.3 Mushak Forms"
        breadcrumbs={[
          { title: 'Mushak Form', url: '/mushak-form/musak-4-3' },
          { title: '4.3 Mushak Forms' }
        ]}
      />

      {alert && (
        <div className="mb-3">
          <Alert type={alert.type} message={alert.message} onClose={() => setAlert(null)} />
        </div>
      )}

      <div className="kt-portlet kt-portlet--mobile">
        <div className="kt-portlet__head kt-portlet__head--lg">
          <div className="kt-portlet__head-label">
            <span className="kt-portlet__head-icon">
              <i className="kt-font-brand flaticon2-line-chart"></i>
            </span>
            <h3 className="kt-portlet__head-title">Mushak 4.3 (Price Declaration)</h3>
          </div>
          <div className="kt-portlet__head-toolbar">
            <div className="kt-portlet__head-wrapper">
              <button
                type="button"
                className="btn btn-brand btn-elevate btn-icon-sm"
                onClick={() => setReloadTrigger(prev => prev + 1)}
              >
                <i className="la la-refresh"></i> Reload
              </button>
            </div>
          </div>
        </div>

        <div className="kt-portlet__body">
          {/* Filters Row */}
          <div className="kt-form kt-form--label-right mb-4">
            <div className="row align-items-center">
              <div className="col-md-3">
                <label className="form-label text-muted small">Company</label>
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

              <div className="col-md-3">
                <label className="form-label text-muted small">From Date</label>
                <input
                  type="date"
                  className="form-control"
                  value={fromDate}
                  onChange={(e) => { setFromDate(e.target.value); setPage(0); }}
                />
              </div>

              <div className="col-md-3">
                <label className="form-label text-muted small">To Date</label>
                <input
                  type="date"
                  className="form-control"
                  value={toDate}
                  onChange={(e) => { setToDate(e.target.value); setPage(0); }}
                />
              </div>

              <div className="col-md-3">
                <label className="form-label text-muted small">Search</label>
                <div className="kt-input-icon kt-input-icon--left">
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Search HS code, details..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                  />
                  <span className="kt-input-icon__icon kt-input-icon__icon--left">
                    <span><i className="la la-search"></i></span>
                  </span>
                </div>
              </div>
            </div>
          </div>

          {/* Table */}
          <div className="table-responsive" style={{ minHeight: '300px' }}>
            <table className="table table-striped table-bordered table-hover">
              <thead className="thead-light">
                <tr>
                  <th style={{ width: '30px' }} className="text-center">
                    <input
                      type="checkbox"
                      checked={data.length > 0 && selectedIds.length === data.length}
                      onChange={handleSelectAll}
                    />
                  </th>
                  <th>SL</th>
                  <th>Submission ID</th>
                  <th>Submission Date</th>
                  <th>BOE/Invoice Date</th>
                  <th>BOE No</th>
                  <th>Company</th>
                  <th>Branch</th>
                  <th>Product Details</th>
                  <th>HS Code</th>
                  <th>Purchase Price</th>
                  <th>Qty Cost</th>
                  <th>Additional Cost</th>
                  <th>Profit</th>
                  <th>Sale Price</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td colSpan="16" className="text-center py-5 text-muted">
                      <div className="spinner-border text-primary" role="status">
                        <span className="sr-only">Loading...</span>
                      </div>
                      <div className="mt-2">Loading Mushak 4.3 data...</div>
                    </td>
                  </tr>
                ) : data.length === 0 ? (
                  <tr>
                    <td colSpan="16" className="text-center py-5 text-muted">
                      No records found.
                    </td>
                  </tr>
                ) : (
                  data.map((item, idx) => (
                    <tr key={item.id}>
                      <td className="text-center">
                        <input
                          type="checkbox"
                          checked={selectedIds.includes(item.id)}
                          onChange={() => handleSelectRow(item.id)}
                        />
                      </td>
                      <td>{page * pageSize + idx + 1}</td>
                      <td><span className="badge badge-info">{item.submissionId || 'N/A'}</span></td>
                      <td>{item.submissionDate || 'N/A'}</td>
                      <td>{item.boeDate || 'N/A'}</td>
                      <td>{item.billOfEntry || 'N/A'}</td>
                      <td>{item.companyName || '—'}</td>
                      <td>{item.branchName || 'N/A'}</td>
                      <td>{item.productServiceDetails || 'N/A'}</td>
                      <td><code>{item.hsCode || 'N/A'}</code></td>
                      <td className="text-right">{Number(item.purchasePrice || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                      <td className="text-right">{Number(item.qtyCost || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                      <td className="text-right">{Number(item.additionalCost || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                      <td className="text-right">{Number(item.profit || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                      <td className="text-right font-weight-bold">{Number(item.salePrice || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                      <td className="text-center text-nowrap">
                        <button
                          type="button"
                          className="btn btn-sm btn-clean btn-icon btn-icon-md text-info"
                          title="View Details"
                          onClick={() => { setViewItem(item); setIsViewOpen(true); }}
                        >
                          <i className="la la-eye"></i>
                        </button>
                        <button
                          type="button"
                          className="btn btn-sm btn-clean btn-icon btn-icon-md text-danger"
                          title="Download PDF"
                          onClick={() => handlePdfDownload(item.id)}
                          disabled={isPdfLoading}
                        >
                          <i className="la la-file-pdf-o"></i>
                        </button>
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
              Showing {filteredRecords > 0 ? page * pageSize + 1 : 0} to {Math.min((page + 1) * pageSize, filteredRecords)} of {filteredRecords.toLocaleString()} entries
              {totalRecords !== filteredRecords && ` (filtered from ${totalRecords.toLocaleString()} total)`}
            </div>
            <div className="d-flex align-items-center gap-2">
              <select
                className="form-control form-control-sm mr-2"
                style={{ width: '80px' }}
                value={pageSize}
                onChange={(e) => { setPageSize(Number(e.target.value)); setPage(0); }}
              >
                <option value="10">10</option>
                <option value="25">25</option>
                <option value="50">50</option>
                <option value="100">100</option>
              </select>
              <ul className="pagination pagination-sm mb-0">
                <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
                  <button className="page-link" onClick={() => setPage(p => Math.max(0, p - 1))}>Previous</button>
                </li>
                {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                  const pNum = Math.max(0, Math.min(totalPages - 5, page - 2)) + i;
                  if (pNum >= totalPages) return null;
                  return (
                    <li key={pNum} className={`page-item ${page === pNum ? 'active' : ''}`}>
                      <button className="page-link" onClick={() => setPage(pNum)}>{pNum + 1}</button>
                    </li>
                  );
                })}
                <li className={`page-item ${page >= totalPages - 1 ? 'disabled' : ''}`}>
                  <button className="page-link" onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}>Next</button>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      {/* View Modal */}
      {isViewOpen && viewItem && (
        <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-lg">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title font-weight-bold">Mushak 4.3 Details - {viewItem.submissionId || viewItem.id}</h5>
                <button type="button" className="close" onClick={() => setIsViewOpen(false)}>&times;</button>
              </div>
              <div className="modal-body">
                <div className="row mb-2">
                  <div className="col-md-6"><strong>Company:</strong> {viewItem.companyName}</div>
                  <div className="col-md-6"><strong>Branch:</strong> {viewItem.branchName}</div>
                </div>
                <div className="row mb-2">
                  <div className="col-md-6"><strong>Submission ID:</strong> {viewItem.submissionId || 'N/A'}</div>
                  <div className="col-md-6"><strong>Submission Date:</strong> {viewItem.submissionDate || 'N/A'}</div>
                </div>
                <div className="row mb-2">
                  <div className="col-md-6"><strong>BOE / Invoice No:</strong> {viewItem.billOfEntry || 'N/A'}</div>
                  <div className="col-md-6"><strong>BOE Date:</strong> {viewItem.boeDate || 'N/A'}</div>
                </div>
                <div className="row mb-2">
                  <div className="col-md-6"><strong>Product Details:</strong> {viewItem.productServiceDetails || 'N/A'}</div>
                  <div className="col-md-6"><strong>HS Code:</strong> {viewItem.hsCode || 'N/A'}</div>
                </div>
                <hr />
                <div className="row mb-2">
                  <div className="col-md-4"><strong>Purchase Price:</strong> {Number(viewItem.purchasePrice || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</div>
                  <div className="col-md-4"><strong>Qty Cost:</strong> {Number(viewItem.qtyCost || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</div>
                  <div className="col-md-4"><strong>Additional Cost:</strong> {Number(viewItem.additionalCost || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</div>
                </div>
                <div className="row mb-2">
                  <div className="col-md-4"><strong>Profit:</strong> {Number(viewItem.profit || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</div>
                  <div className="col-md-4"><strong>Sale Price:</strong> <span className="text-primary font-weight-bold">{Number(viewItem.salePrice || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</span></div>
                  <div className="col-md-4"><strong>Wholesale Price:</strong> {Number(viewItem.wholesalePrice || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</div>
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setIsViewOpen(false)}>Close</button>
                <button type="button" className="btn btn-danger" onClick={() => handlePdfDownload(viewItem.id)}>
                  <i className="la la-file-pdf-o"></i> Download PDF
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
