import React, { useState, useEffect, useRef } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { purchaseService } from '../../services/purchaseService';

export default function PurchaseListPage() {
  const navigate = useNavigate();

  // Data & Pagination State
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
  const [isBatchDeleteOpen, setIsBatchDeleteOpen] = useState(false);

  // Load Companies for Filter
  useEffect(() => {
    purchaseService.getFormData()
      .then(res => {
        if (res?.data?.companies) {
          setCompanies(res.data.companies);
        }
      })
      .catch(err => console.error('Failed to load companies for filter:', err));
  }, []);

  // Search Debounce (400ms)
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search);
      setPage(0);
    }, 400);
    return () => clearTimeout(timer);
  }, [search]);

  const drawRef = useRef(1);

  // Fetch Purchases
  useEffect(() => {
    let isMounted = true;
    setLoading(true);

    const currentDraw = drawRef.current++;
    const params = {
      draw: currentDraw,
      start: page * pageSize,
      length: pageSize,
      searchValue: debouncedSearch,
      companyId: selectedCompany || undefined,
      fromDate: fromDate || undefined,
      toDate: toDate || undefined,
      sortDirection: sortDir
    };

    purchaseService.load(params)
      .then(res => {
        if (isMounted && res) {
          setData(res.data || []);
          setTotalRecords(res.recordsTotal || 0);
          setFilteredRecords(res.recordsFiltered || 0);
          setSelectedIds([]); // reset selection on page change
        }
      })
      .catch(err => {
        if (isMounted) {
          console.error('Error loading purchases:', err);
          setAlert({ type: 'danger', message: 'Failed to load purchase records from server.' });
        }
      })
      .finally(() => {
        if (isMounted) setLoading(false);
      });

    return () => {
      isMounted = false;
    };
  }, [page, pageSize, sortDir, debouncedSearch, selectedCompany, fromDate, toDate, reloadTrigger]);

  // Checkbox Handlers
  const handleSelectAll = (e) => {
    if (e.target.checked) {
      const allPageIds = data.map(item => item.id);
      setSelectedIds(allPageIds);
    } else {
      setSelectedIds([]);
    }
  };

  const handleSelectRow = (id) => {
    setSelectedIds(prev =>
      prev.includes(id) ? prev.filter(item => item !== id) : [...prev, id]
    );
  };

  // Delete Single
  const confirmDelete = async () => {
    if (!itemToDelete) return;
    try {
      await purchaseService.delete(itemToDelete.id);
      setAlert({ type: 'success', message: 'Purchase record deleted successfully!' });
      setReloadTrigger(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message || 'Failed to delete purchase record.' });
    } finally {
      setIsDeleteOpen(false);
      setItemToDelete(null);
    }
  };

  // Multiple Delete
  const confirmBatchDelete = async () => {
    if (selectedIds.length === 0) return;
    try {
      const res = await purchaseService.multipleDelete(selectedIds);
      setAlert({ type: 'success', message: res.data?.message || `Successfully deleted ${selectedIds.length} purchase records!` });
      setSelectedIds([]);
      setReloadTrigger(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message || 'Batch delete failed.' });
    } finally {
      setIsBatchDeleteOpen(false);
    }
  };

  // PDF Export
  const handleDownloadPdf = async () => {
    setIsPdfLoading(true);
    try {
      await purchaseService.downloadPdf(selectedCompany, debouncedSearch);
      setAlert({ type: 'success', message: 'PDF report generated and downloaded.' });
    } catch (err) {
      setAlert({ type: 'danger', message: 'Failed to export PDF report.' });
    } finally {
      setIsPdfLoading(false);
    }
  };

  // Formatting helper
  const fmtNum = (val) => {
    if (val === null || val === undefined || isNaN(val)) return '0.00';
    return Number(val).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  };

  const totalPages = Math.ceil(filteredRecords / pageSize) || 1;
  const startItem = filteredRecords === 0 ? 0 : page * pageSize + 1;
  const endItem = Math.min((page + 1) * pageSize, filteredRecords);

  return (
    <div className="musak-page-shell">
      {/* Subheader */}
      <Subheader
        title="Purchase"
        breadcrumbs={[
          { label: 'Stock Management' },
          { label: 'Product Purchase' },
          { label: 'List' }
        ]}
        actions={
          <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', alignItems: 'center' }}>
            {selectedIds.length > 0 && (
              <button
                className="btn btn-danger"
                onClick={() => setIsBatchDeleteOpen(true)}
              >
                <i className="bi bi-trash me-1"></i>
                <span>Delete Selected ({selectedIds.length})</span>
              </button>
            )}

            <button
              className="btn btn-outline-danger"
              onClick={handleDownloadPdf}
              disabled={isPdfLoading}
            >
              <i className="bi bi-file-earmark-pdf me-1"></i>
              <span>{isPdfLoading ? 'Exporting...' : 'Export PDF'}</span>
            </button>

            <Link to="/purchase/create/foreign" className="btn btn-brand">
              <i className="bi bi-plus-lg me-1"></i>
              <span>Import Purchase</span>
            </Link>

            <Link to="/purchase/create/local" className="btn btn-outline-brand">
              <i className="bi bi-plus-circle me-1"></i>
              <span>Local Purchase</span>
            </Link>
          </div>
        }
      />

      {/* Alert */}
      {alert && (
        <Alert
          type={alert.type}
          message={alert.message}
          onClose={() => setAlert(null)}
        />
      )}

      {/* Filter Portlet */}
      <div className="kt-portlet mb-4">
        <div className="kt-portlet__head py-2">
          <div className="kt-portlet__head-label">
            <span className="kt-portlet__head-icon text-brand">
              <i className="bi bi-funnel-fill"></i>
            </span>
            <h3 className="kt-portlet__head-title" style={{ fontSize: '15px', fontWeight: 600 }}>
              Filter Data
            </h3>
          </div>
        </div>
        <div className="kt-portlet__body py-3">
          <div className="row g-3 align-items-end">
            <div className="col-12 col-md-3">
              <label className="form-label fw-bold small">Select Company</label>
              <select
                className="form-select form-select-sm"
                value={selectedCompany}
                onChange={(e) => {
                  setSelectedCompany(e.target.value);
                  setPage(0);
                }}
              >
                <option value="">All Companies</option>
                {companies.map(c => (
                  <option key={c.id} value={c.id}>{c.name}</option>
                ))}
              </select>
            </div>

            <div className="col-12 col-md-2">
              <label className="form-label fw-bold small">From Date</label>
              <input
                type="date"
                className="form-control form-control-sm"
                value={fromDate}
                onChange={(e) => {
                  setFromDate(e.target.value);
                  setPage(0);
                }}
              />
            </div>

            <div className="col-12 col-md-2">
              <label className="form-label fw-bold small">To Date</label>
              <input
                type="date"
                className="form-control form-control-sm"
                value={toDate}
                onChange={(e) => {
                  setToDate(e.target.value);
                  setPage(0);
                }}
              />
            </div>

            <div className="col-12 col-md-3">
              <label className="form-label fw-bold small">Search</label>
              <div className="input-group input-group-sm">
                <input
                  type="text"
                  className="form-control"
                  placeholder="BOE, Product, Supplier, BIN..."
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                />
                {search && (
                  <button
                    className="btn btn-outline-secondary"
                    type="button"
                    onClick={() => setSearch('')}
                  >
                    <i className="bi bi-x"></i>
                  </button>
                )}
              </div>
            </div>

            <div className="col-12 col-md-2 d-flex gap-2">
              <button
                className="btn btn-sm btn-brand flex-fill"
                onClick={() => setReloadTrigger(prev => prev + 1)}
              >
                <i className="bi bi-arrow-clockwise me-1"></i> Filter
              </button>
              <button
                className="btn btn-sm btn-light border"
                onClick={() => {
                  setSelectedCompany('');
                  setFromDate('');
                  setToDate('');
                  setSearch('');
                  setPage(0);
                }}
                title="Reset Filters"
              >
                Reset
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Main Table Portlet */}
      <div className="kt-portlet">
        <div className="kt-portlet__head">
          <div className="kt-portlet__head-label">
            <span className="kt-portlet__head-icon">
              <i className="bi bi-table text-brand"></i>
            </span>
            <h3 className="kt-portlet__head-title" style={{ fontWeight: 600 }}>
              Purchase List
            </h3>
          </div>
          <div className="kt-portlet__head-toolbar">
            <div className="d-flex align-items-center gap-2">
              <span className="text-muted small">Show:</span>
              <select
                className="form-select form-select-sm"
                style={{ width: '80px' }}
                value={pageSize}
                onChange={(e) => {
                  setPageSize(Number(e.target.value));
                  setPage(0);
                }}
              >
                <option value={10}>10</option>
                <option value={25}>25</option>
                <option value={50}>50</option>
                <option value={100}>100</option>
              </select>
            </div>
          </div>
        </div>

        <div className="kt-portlet__body p-0">
          {/* Scrollable Table matching Laravel p-s-filter-table.blade.php exact 22 columns */}
          <div style={{ overflowX: 'auto', width: '100%', WebkitOverflowScrolling: 'touch' }}>
            <table
              className="table table-hover table-bordered table-striped align-middle mb-0"
              style={{ minWidth: '2400px', fontSize: '12px' }}
            >
              <thead className="table-light text-nowrap text-center" style={{ fontSize: '11px', fontWeight: 700 }}>
                <tr>
                  <th style={{ width: '40px' }}>
                    <input
                      type="checkbox"
                      className="form-check-input"
                      checked={data.length > 0 && selectedIds.length === data.length}
                      onChange={handleSelectAll}
                    />
                  </th>
                  <th style={{ width: '90px' }}>Actions</th>
                  <th style={{ width: '60px' }}>SL NO</th>
                  <th style={{ width: '130px' }}>Date and Time</th>
                  <th style={{ width: '180px' }}>Company</th>
                  <th style={{ width: '150px' }}>Branch</th>
                  <th style={{ width: '130px' }}>BIN No</th>
                  <th style={{ width: '120px' }}>TIN No</th>
                  <th style={{ width: '100px' }}>HSCODE</th>
                  <th style={{ width: '220px' }}>Product/Service Name</th>
                  <th style={{ width: '120px' }}>BOE/Invoice No</th>
                  <th style={{ width: '110px' }}>BOE/Invoice Date</th>
                  <th style={{ width: '180px' }}>Supplier Name</th>
                  <th style={{ width: '180px' }}>SUPPLIER ADDRESS</th>
                  <th style={{ width: '130px' }}>SUPPLY BIN/NID</th>
                  <th style={{ width: '120px' }}>Assessable Price</th>
                  <th style={{ width: '90px' }}>VAT Amount</th>
                  <th style={{ width: '90px' }}>SD Amount</th>
                  <th style={{ width: '100px' }}>AT Amount</th>
                  <th style={{ width: '140px' }}>Market Price (APPROX)</th>
                  <th style={{ width: '100px' }}>Wholesale Rate</th>
                  <th style={{ width: '100px' }}>Retailer Rate</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td colSpan={22} className="text-center py-5">
                      <div className="spinner-border text-brand" role="status"></div>
                      <div className="mt-2 text-muted fw-semibold">Loading purchase records...</div>
                    </td>
                  </tr>
                ) : data.length === 0 ? (
                  <tr>
                    <td colSpan={22} className="text-center py-5 text-muted">
                      <i className="bi bi-inbox fs-2 d-block mb-2"></i>
                      No purchase records found matching criteria.
                    </td>
                  </tr>
                ) : (
                  data.map((row, idx) => {
                    const isSelected = selectedIds.includes(row.id);
                    const serialNumber = page * pageSize + idx + 1;
                    return (
                      <tr key={row.id} className={isSelected ? 'table-active' : ''}>
                        {/* 1. Checkbox */}
                        <td className="text-center">
                          <input
                            type="checkbox"
                            className="form-check-input"
                            checked={isSelected}
                            onChange={() => handleSelectRow(row.id)}
                          />
                        </td>

                        {/* 2. Actions */}
                        <td className="text-center text-nowrap">
                          <div className="d-flex justify-content-center gap-1">
                            <button
                              className="btn btn-sm btn-icon btn-light text-primary border-0"
                              title="View Purchase"
                              onClick={() => {
                                setViewItem(row);
                                setIsViewOpen(true);
                              }}
                            >
                              <i className="bi bi-eye"></i>
                            </button>
                            <button
                              className="btn btn-sm btn-icon btn-light text-info border-0"
                              title="Edit Purchase"
                              onClick={() => navigate(`/purchase/${row.id}/edit`)}
                            >
                              <i className="bi bi-pencil"></i>
                            </button>
                            <button
                              className="btn btn-sm btn-icon btn-light text-danger border-0"
                              title="Delete Purchase"
                              onClick={() => {
                                setItemToDelete(row);
                                setIsDeleteOpen(true);
                              }}
                            >
                              <i className="bi bi-trash"></i>
                            </button>
                          </div>
                        </td>

                        {/* 3. SL NO */}
                        <td className="text-center text-muted fw-semibold">{serialNumber}</td>

                        {/* 4. Date and Time */}
                        <td className="text-nowrap">{row.date || '—'}</td>

                        {/* 5. Company */}
                        <td className="fw-semibold text-truncate" style={{ maxWidth: '180px' }} title={row.companyName}>
                          {row.companyName || '—'}
                        </td>

                        {/* 6. Branch */}
                        <td className="text-truncate" style={{ maxWidth: '150px' }} title={row.branchName}>
                          {row.branchName || '—'}
                        </td>

                        {/* 7. BIN No */}
                        <td className="font-monospace text-nowrap">{row.bin || '—'}</td>

                        {/* 8. TIN No */}
                        <td className="font-monospace text-nowrap">{row.tin || '—'}</td>

                        {/* 9. HSCODE */}
                        <td className="font-monospace text-nowrap">{row.hsCode || '—'}</td>

                        {/* 10. Product/Service Name */}
                        <td
                          style={{
                            maxWidth: '220px',
                            whiteSpace: 'normal',
                            wordBreak: 'break-word',
                            lineHeight: '1.25'
                          }}
                          title={row.productDescription}
                        >
                          {row.productDescription || '—'}
                        </td>

                        {/* 11. BOE/Invoice No */}
                        <td className="font-monospace text-nowrap fw-semibold">{row.billOfEntry || '—'}</td>

                        {/* 12. BOE/Invoice Date */}
                        <td className="text-nowrap">{row.boeDate || '—'}</td>

                        {/* 13. Supplier Name */}
                        <td className="text-truncate" style={{ maxWidth: '180px' }} title={row.supplierName}>
                          {row.supplierName || '—'}
                        </td>

                        {/* 14. SUPPLIER ADDRESS */}
                        <td className="text-truncate" style={{ maxWidth: '180px' }} title={row.supplierAddress}>
                          {row.supplierAddress || '—'}
                        </td>

                        {/* 15. SUPPLY BIN/NID */}
                        <td className="font-monospace text-nowrap">{row.supplyBinNid || '—'}</td>

                        {/* 16. Assessable Price */}
                        <td className="text-end font-monospace">{fmtNum(row.assessable)}</td>

                        {/* 17. VAT Amount */}
                        <td className="text-end font-monospace">{fmtNum(row.vatAmount)}</td>

                        {/* 18. SD Amount */}
                        <td className="text-end font-monospace">{fmtNum(row.sdAmount)}</td>

                        {/* 19. AT Amount */}
                        <td className="text-end font-monospace">{fmtNum(row.atAmount)}</td>

                        {/* 20. Market Price (APPROX) */}
                        <td className="text-end font-monospace fw-bold text-success">
                          {fmtNum(row.totalPurchaseAmount)}
                        </td>

                        {/* 21. Wholesale Rate */}
                        <td className="text-end font-monospace">{fmtNum(row.wholesaleRate)}</td>

                        {/* 22. Retailer Rate */}
                        <td className="text-end font-monospace">{fmtNum(row.retailerRate)}</td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>

          {/* Pagination Footer */}
          <div className="d-flex flex-wrap justify-content-between align-items-center p-3 border-top gap-2">
            <div className="text-muted small">
              Showing <b>{startItem}</b> to <b>{endItem}</b> of <b>{filteredRecords.toLocaleString()}</b> entries
              {filteredRecords !== totalRecords && (
                <span> (filtered from {totalRecords.toLocaleString()} total)</span>
              )}
            </div>

            <div className="d-flex align-items-center gap-1">
              <button
                className="btn btn-sm btn-light border"
                disabled={page === 0 || loading}
                onClick={() => setPage(0)}
                title="First Page"
              >
                <i className="bi bi-chevron-double-left"></i>
              </button>
              <button
                className="btn btn-sm btn-light border"
                disabled={page === 0 || loading}
                onClick={() => setPage(prev => Math.max(0, prev - 1))}
              >
                <i className="bi bi-chevron-left"></i> Prev
              </button>

              <span className="px-3 small fw-bold">
                Page {page + 1} of {totalPages}
              </span>

              <button
                className="btn btn-sm btn-light border"
                disabled={page >= totalPages - 1 || loading}
                onClick={() => setPage(prev => Math.min(totalPages - 1, prev + 1))}
              >
                Next <i className="bi bi-chevron-right"></i>
              </button>
              <button
                className="btn btn-sm btn-light border"
                disabled={page >= totalPages - 1 || loading}
                onClick={() => setPage(totalPages - 1)}
                title="Last Page"
              >
                <i className="bi bi-chevron-double-right"></i>
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* VIEW MODAL (matching Laravel view.blade.php) */}
      {isViewOpen && viewItem && (
        <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)', zIndex: 1060 }} tabIndex="-1">
          <div className="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
            <div className="modal-content">
              <div className="modal-header bg-light">
                <h5 className="modal-title fs-6 fw-bold">
                  {viewItem.companyName} (BIN: {viewItem.bin || '—'}) || {viewItem.slug || viewItem.billOfEntry}
                </h5>
                <button
                  type="button"
                  className="btn-close"
                  onClick={() => {
                    setIsViewOpen(false);
                    setViewItem(null);
                  }}
                ></button>
              </div>
              <div className="modal-body p-4">
                <div className="row g-3">
                  <div className="col-12 col-md-6 border-bottom pb-2">
                    <span className="text-muted small d-block">Purchase Type</span>
                    <span className="fw-semibold text-uppercase text-brand">
                      {viewItem.purchaseType === 'foreign' ? 'Import' : 'Local'}
                    </span>
                  </div>

                  <div className="col-12 col-md-6 border-bottom pb-2">
                    <span className="text-muted small d-block">BOE / Invoice No</span>
                    <span className="fw-bold font-monospace">{viewItem.billOfEntry || '—'}</span>
                  </div>

                  <div className="col-12 col-md-6 border-bottom pb-2">
                    <span className="text-muted small d-block">Date</span>
                    <span className="fw-semibold">{viewItem.date || '—'}</span>
                  </div>

                  <div className="col-12 col-md-6 border-bottom pb-2">
                    <span className="text-muted small d-block">BOE Date</span>
                    <span className="fw-semibold">{viewItem.boeDate || '—'}</span>
                  </div>

                  <div className="col-12 col-md-6 border-bottom pb-2">
                    <span className="text-muted small d-block">HS Code</span>
                    <span className="fw-semibold font-monospace">{viewItem.hsCode || '—'}</span>
                  </div>

                  <div className="col-12 col-md-6 border-bottom pb-2">
                    <span className="text-muted small d-block">Product / Service Name</span>
                    <span className="fw-semibold">{viewItem.productDescription || '—'}</span>
                  </div>

                  <div className="col-12 col-md-6 border-bottom pb-2">
                    <span className="text-muted small d-block">Supplier Name</span>
                    <span className="fw-semibold">{viewItem.supplierName || '—'}</span>
                  </div>

                  <div className="col-12 col-md-6 border-bottom pb-2">
                    <span className="text-muted small d-block">Supplier Address</span>
                    <span className="fw-semibold">{viewItem.supplierAddress || '—'}</span>
                  </div>

                  <div className="col-12 col-md-6 border-bottom pb-2">
                    <span className="text-muted small d-block">Supplier BIN / NID</span>
                    <span className="font-monospace">{viewItem.supplyBinNid || '—'}</span>
                  </div>

                  <div className="col-12 col-md-6 border-bottom pb-2">
                    <span className="text-muted small d-block">Assessable Amount</span>
                    <span className="fw-bold font-monospace">{fmtNum(viewItem.assessable)}</span>
                  </div>

                  <div className="col-12 col-md-4 border-bottom pb-2">
                    <span className="text-muted small d-block">VAT Amount</span>
                    <span className="font-monospace">{fmtNum(viewItem.vatAmount)}</span>
                  </div>

                  <div className="col-12 col-md-4 border-bottom pb-2">
                    <span className="text-muted small d-block">SD Amount</span>
                    <span className="font-monospace">{fmtNum(viewItem.sdAmount)}</span>
                  </div>

                  <div className="col-12 col-md-4 border-bottom pb-2">
                    <span className="text-muted small d-block">AT Amount</span>
                    <span className="font-monospace">{fmtNum(viewItem.atAmount)}</span>
                  </div>

                  <div className="col-12 col-md-4 border-bottom pb-2">
                    <span className="text-muted small d-block">Wholesale Rate</span>
                    <span className="font-monospace">{fmtNum(viewItem.wholesaleRate)}</span>
                  </div>

                  <div className="col-12 col-md-4 border-bottom pb-2">
                    <span className="text-muted small d-block">Retailer Rate</span>
                    <span className="font-monospace">{fmtNum(viewItem.retailerRate)}</span>
                  </div>

                  <div className="col-12 col-md-4 border-bottom pb-2">
                    <span className="text-muted small d-block">Market Price (APPROX)</span>
                    <span className="fw-bold text-success font-monospace fs-6">
                      BDT {fmtNum(viewItem.totalPurchaseAmount)}
                    </span>
                  </div>
                </div>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => {
                    setIsViewOpen(false);
                    setViewItem(null);
                  }}
                >
                  Close
                </button>
                <button
                  type="button"
                  className="btn btn-brand"
                  onClick={() => {
                    const id = viewItem.id;
                    setIsViewOpen(false);
                    navigate(`/purchase/${id}/edit`);
                  }}
                >
                  <i className="bi bi-pencil me-1"></i> Edit
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Single Delete Confirm */}
      <ConfirmModal
        isOpen={isDeleteOpen}
        title="Confirm Delete"
        message={`Are you sure you want to delete purchase record #${itemToDelete?.billOfEntry || itemToDelete?.id}? This action cannot be undone.`}
        onConfirm={confirmDelete}
        onCancel={() => {
          setIsDeleteOpen(false);
          setItemToDelete(null);
        }}
      />

      {/* Batch Delete Confirm */}
      <ConfirmModal
        isOpen={isBatchDeleteOpen}
        title="Confirm Batch Delete"
        message={`Are you sure you want to delete all ${selectedIds.length} selected purchase records?`}
        onConfirm={confirmBatchDelete}
        onCancel={() => setIsBatchDeleteOpen(false)}
      />
    </div>
  );
}
