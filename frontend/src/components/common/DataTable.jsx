import React, { useState, useEffect, useRef } from 'react';

/**
 * Enterprise Server-Side DataTable for 100k - 500k+ Records.
 * Features:
 * - 400ms search debounce (avoids flooding backend)
 * - Server-side pagination & sorting
 * - Total / Filtered record counts
 */
export default function DataTable({
  columns,
  fetchData,
  defaultSortColumn = 0,
  defaultSortDir = 'desc',
  title = 'Data Table',
  actionButton = null,
  reloadTrigger = 0
}) {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalRecords, setTotalRecords] = useState(0);
  const [filteredRecords, setFilteredRecords] = useState(0);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [sortColumn, setSortColumn] = useState(defaultSortColumn);
  const [sortDir, setSortDir] = useState(defaultSortDir);

  // Debounce search input by 400ms
  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedSearch(search);
      setPage(0); // Reset to first page on search
    }, 400);

    return () => clearTimeout(handler);
  }, [search]);

  const drawRef = useRef(1);

  // Load data when page, size, sort, debouncedSearch, or reloadTrigger changes
  useEffect(() => {
    let isMounted = true;
    setLoading(true);
    setError(null);

    const currentDraw = drawRef.current++;
    const params = {
      draw: currentDraw,
      start: page * pageSize,
      length: pageSize,
      searchValue: debouncedSearch,
      sortColumn: sortColumn,
      sortDirection: sortDir
    };

    fetchData(params)
      .then((res) => {
        if (isMounted && res) {
          setData(res.data || []);
          setTotalRecords(res.recordsTotal || 0);
          setFilteredRecords(res.recordsFiltered || 0);
        }
      })
      .catch((err) => {
        console.error('DataTable load error:', err);
        if (isMounted) {
          setError(err.message || 'Failed to load data from server');
        }
      })
      .finally(() => {
        if (isMounted) setLoading(false);
      });

    return () => {
      isMounted = false;
    };
  }, [page, pageSize, sortColumn, sortDir, debouncedSearch, reloadTrigger]);

  const handleSort = (colIndex) => {
    if (columns[colIndex]?.sortable === false) return;
    if (sortColumn === colIndex) {
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
    } else {
      setSortColumn(colIndex);
      setSortDir('asc');
    }
  };

  const totalPages = Math.ceil(filteredRecords / pageSize) || 1;
  const startItem = filteredRecords === 0 ? 0 : page * pageSize + 1;
  const endItem = Math.min((page + 1) * pageSize, filteredRecords);

  return (
    <div className="kt-portlet">
      <div className="kt-portlet__head">
        <div className="kt-portlet__head-title">
          <span>{title}</span>
          <small>Total {totalRecords.toLocaleString()} records</small>
        </div>
        {actionButton && <div>{actionButton}</div>}
      </div>

      <div className="kt-portlet__body">
        {error && (
          <div style={{
            padding: '12px 18px',
            backgroundColor: 'rgba(253, 57, 122, 0.1)',
            borderLeft: '4px solid var(--kt-danger)',
            color: '#c9164e',
            borderRadius: 4,
            marginBottom: 20,
            fontSize: 13,
            fontWeight: 500
          }}>
            <i className="bi bi-exclamation-triangle-fill" style={{ marginRight: 8 }}></i>
            <strong>Server Error:</strong> {error}
          </div>
        )}

        {/* Top Controls: Page Size + Search */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20, flexWrap: 'wrap', gap: 15 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <span style={{ fontSize: 13, color: '#646c9a' }}>Show</span>
            <select
              className="form-control"
              style={{ width: 80, height: 34, padding: '4px 8px' }}
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
            <span style={{ fontSize: 13, color: '#646c9a' }}>entries</span>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <span style={{ fontSize: 13, color: '#646c9a' }}>Search:</span>
            <input
              type="text"
              className="form-control"
              style={{ width: 240, height: 34 }}
              placeholder="Type to search..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
        </div>

        {/* Table View */}
        <div className="table-responsive" style={{ position: 'relative', minHeight: 180 }}>
          {loading && (
            <div style={{
              position: 'absolute', top: 0, left: 0, right: 0, bottom: 0,
              background: 'rgba(255, 255, 255, 0.7)', display: 'flex',
              alignItems: 'center', justifyContent: 'center', zIndex: 10,
              fontWeight: 500, color: 'var(--kt-brand)'
            }}>
              <i className="fa fa-spinner fa-spin" style={{ marginRight: 8 }}></i> Loading records...
            </div>
          )}

          <table className="table">
            <thead>
              <tr>
                {columns.map((col, idx) => (
                  <th
                    key={idx}
                    onClick={() => handleSort(idx)}
                    style={{
                      cursor: col.sortable !== false ? 'pointer' : 'default',
                      whiteSpace: 'nowrap',
                      textAlign: col.align || 'left',
                      width: col.width || 'auto'
                    }}
                  >
                    {col.title}
                    {col.sortable !== false && (
                      <span style={{ marginLeft: 6, fontSize: 10, color: sortColumn === idx ? 'var(--kt-brand)' : '#ccc' }}>
                        {sortColumn === idx ? (sortDir === 'asc' ? '▲' : '▼') : '⇅'}
                      </span>
                    )}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {data.length === 0 && !loading ? (
                <tr>
                  <td colSpan={columns.length} style={{ textAlign: 'center', padding: 30, color: '#a2a3b7' }}>
                    No matching records found
                  </td>
                </tr>
              ) : (
                data.map((row, rowIdx) => (
                  <tr key={row.id || rowIdx}>
                    {columns.map((col, colIdx) => (
                      <td key={colIdx} style={{ textAlign: col.align || 'left' }}>
                        {col.render ? col.render(row, page * pageSize + rowIdx + 1) : row[col.data]}
                      </td>
                    ))}
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Bottom Pagination & Counts */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 20, flexWrap: 'wrap', gap: 15 }}>
          <div style={{ fontSize: 13, color: '#646c9a' }}>
            Showing {startItem} to {endItem} of {filteredRecords.toLocaleString()} entries
            {filteredRecords !== totalRecords && ` (filtered from ${totalRecords.toLocaleString()} total)`}
          </div>

          <div style={{ display: 'flex', gap: 4 }}>
            <button
              className="btn btn-secondary btn-sm"
              disabled={page === 0}
              onClick={() => setPage((p) => Math.max(0, p - 1))}
            >
              Previous
            </button>
            <span style={{ padding: '4px 10px', fontSize: 13, fontWeight: 600, display: 'flex', alignItems: 'center' }}>
              Page {page + 1} of {totalPages}
            </span>
            <button
              className="btn btn-secondary btn-sm"
              disabled={page + 1 >= totalPages}
              onClick={() => setPage((p) => p + 1)}
            >
              Next
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
