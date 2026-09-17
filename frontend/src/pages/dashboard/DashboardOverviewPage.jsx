import React, { useState, useEffect } from 'react';
import api from '../../config/api';

export default function DashboardOverviewPage() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  // Search filters for lists
  const [paidSearch, setPaidSearch] = useState('');
  const [unpaidSearch, setUnpaidSearch] = useState('');
  const [m43Search, setM43Search] = useState('');
  const [submittedSearch, setSubmittedSearch] = useState('');
  const [missingSearch, setMissingSearch] = useState('');

  useEffect(() => {
    loadDashboard();
    const interval = setInterval(loadDashboard, 30000);
    return () => clearInterval(interval);
  }, []);

  const loadDashboard = async () => {
    try {
      const res = await api.get('/debug/dashboard/overview');
      setData(res.data || res);
    } catch (err) {
      console.error('Failed to load dashboard:', err);
    } finally {
      setLoading(false);
    }
  };

  const filteredPaid = (data?.paidList || []).filter(c => (c.name || '').toLowerCase().includes(paidSearch.toLowerCase()));
  const filteredUnpaid = (data?.unpaidList || []).filter(c => (c.name || '').toLowerCase().includes(unpaidSearch.toLowerCase()));
  const filteredM43Amend = (data?.m43AmendmentList || []).filter(c => ((c.company || '') + ' ' + (c.description || '')).toLowerCase().includes(m43Search.toLowerCase()));
  const filteredSubmitted = (data?.returnsSubmittedList || []).filter(c => (c.name || '').toLowerCase().includes(submittedSearch.toLowerCase()));
  const filteredMissing = (data?.returnsMissingList || []).filter(c => (c.name || '').toLowerCase().includes(missingSearch.toLowerCase()));

  return (
    <div className="tax-dash-wrapper">
      {/* Row 1: Bismillah Top Banner */}
      <div className="tax-top-bismillah">
        <img src="/img/bismillahtop.png" alt="বিসমিল্লাহির রাহমানির রাহিম" className="tax-top-bismillah-img" />
      </div>

      {/* Row 2: Red Notice Scrolling Marquee */}
      <div className="tax-notice-bar">
        <div className="tax-notice-badge">
          <i className="bi bi-megaphone-fill"></i> NOTICE
        </div>
        <div className="tax-notice-marquee">
          <marquee behavior="scroll" direction="left" scrollamount="6">
            {(data?.scrollNotices?.filter(n => !n.includes(''))?.length ? data.scrollNotices.filter(n => !n.includes('')) : [
              'প্রিয় গ্রাহক, আপনাদের অবগতির জন্য জানানো যাচ্ছে যে, আমদানীকৃত পণ্যের বিল অব এন্ট্রি হাতে পাওয়ার সাথে সাথে info@barabdonline.xyz এই মেইলে যথা সময়ে পাঠানোর জন্য অনুরোধ করা হলো।',
              'ভ্যাট অফিস থেকে বিভিন্ন প্রতিষ্ঠানে নিয়মিত ইনস্পেকশন চলছে। সবাই প্রয়োজনীয় দলিলাদি সংরক্ষণ করে সচেতন থাকুন।'
            ]).join(' \u00A0\u00A0\u00A0\u00A0|\u00A0\u00A0\u00A0\u00A0 ')}
          </marquee>
        </div>
      </div>

      {/* Row 3: Centered Header Block with 4 Logos and Title */}
      <div className="tax-header-center">
        <div className="tax-header-center-main">
          {/* Left Side Logos: ISO 27001 + BARAL */}
          <div className="tax-header-side tax-header-side--left">
            <div className="tax-header-brand">
              <div className="tax-header-logo-slot">
                <img src="/img/Iso-27001.png" alt="ISO 27001" className="tax-header-side-logo" />
              </div>
              <div className="tax-header-brand-desc">
                <span>Information Security</span>
                <span>Management Systems (ISMS)</span>
              </div>
            </div>

            <div className="tax-header-brand">
              <div className="tax-header-logo-slot">
                <img src="/img/bara_associates_logo.png" alt="BARAL" className="tax-header-side-logo bara-round" />
              </div>
              <div className="tax-header-brand-desc">
                <span>BAKTIER AHMED RONY &amp;</span>
                <span>ASSOCIATES LTD (BARAL)</span>
              </div>
            </div>
          </div>

          {/* Center Text */}
          <div className="tax-header-center-text">
            <span className="tax-header-center-line--1">DASHBOARD</span>
            <span className="tax-header-center-line--2">”প্রযুক্তি ও রাজস্ব খাতে আমরাও অংশীদার”</span>
          </div>

          {/* Right Side Logos: NBR + BASIS */}
          <div className="tax-header-side tax-header-side--right">
            <div className="tax-header-brand">
              <div className="tax-header-logo-slot">
                <img src="/img/gov_logo.png" alt="NBR" className="tax-header-side-logo" />
              </div>
              <div className="tax-header-brand-desc">
                <span>NATIONAL BOARD OF</span>
                <span>REVENUE (NBR)</span>
              </div>
            </div>

            <div className="tax-header-brand tax-header-brand--basis">
              <div className="tax-header-logo-slot">
                <img src="/img/basis2.png" alt="BASIS" className="tax-header-side-logo" />
              </div>
              <div className="tax-header-brand-desc">
                <span>Bangladesh Association of</span>
                <span>Software &amp; Information Services</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Action Row: 4 Center Buttons */}
      <div className="tax-dash-action-row">
        <a href="#purchase-report" className="tax-dash-action-btn">Purchase Report</a>
        <a href="#sale-report" className="tax-dash-action-btn">Sale Report</a>
        <a href="#stock-report" className="tax-dash-action-btn">Stock Report</a>
        <a href="#activity-log" className="tax-dash-action-btn">Activity Log</a>
      </div>

      {/* Main Dashboard Cards Container */}
      <div className="tax-dash">
        {/* Row 1: 3 Big Metric Cards */}
        <div className="row mb-4">
          {/* Card 1: Service charge — Paid */}
          <div className="col-xl-4 col-md-6 mb-4">
            <div className="tax-card tax-card--ops-green">
              <div className="tax-card-header">
                <div className="tax-ops-head-left">
                  <i className="bi bi-check-circle-fill"></i>
                  <h5 className="tax-card-title">Service charge — Paid</h5>
                </div>
                <div className="tax-ops-head-right">
                  <span className="tax-ops-month-chip">{data?.paymentMonth || 'August 2026'}</span>
                  <span className="tax-ops-auto-pill">
                    <span className="tax-ops-live-dot"></span> Auto
                  </span>
                </div>
              </div>
              <div className="tax-card-body">
                <div className="tax-ops-big">{data?.paidCount ?? 0}</div>
                <p className="tax-card-subhead">
                  <span>{data?.paidCount ?? 0}</span> companies paid · <a href="/company">View all</a>
                </p>

                <div className="tax-ops-progress">
                  <div className="tax-ops-progress-meta">
                    <span>Collection rate</span>
                    <strong>{data?.paidPct ?? 0}%</strong>
                  </div>
                  <div className="tax-ops-progress-track">
                    <span className="tax-ops-progress-fill is-green" style={{ width: `${data?.paidPct ?? 0}%` }}></span>
                  </div>
                  <div className="tax-ops-progress-hint">
                    {((data?.paidCount || 0) + (data?.unpaidCount || 0))} active companies this month
                  </div>
                </div>

                <div className="tax-ops-search">
                  <i className="bi bi-search tax-ops-search-icon"></i>
                  <input
                    type="search"
                    className="tax-ops-search-input"
                    placeholder="Search paid companies…"
                    value={paidSearch}
                    onChange={e => setPaidSearch(e.target.value)}
                  />
                </div>

                <div className="tax-scroll-list">
                  <ul className="tax-list">
                    {filteredPaid.map((c, i) => (
                      <li key={i}>
                        <span>
                          <span className="tax-ops-status-dot is-green"></span>
                          {c.name}
                        </span>
                        <span className="tax-badge tax-badge--online">Paid</span>
                      </li>
                    ))}
                    {filteredPaid.length === 0 && (
                      <li className="tax-empty">No paid companies found</li>
                    )}
                  </ul>
                </div>
              </div>
            </div>
          </div>

          {/* Card 2: Service charge — Unpaid */}
          <div className="col-xl-4 col-md-6 mb-4">
            <div className="tax-card tax-card--ops-red">
              <div className="tax-card-header">
                <div className="tax-ops-head-left">
                  <i className="bi bi-exclamation-circle-fill"></i>
                  <h5 className="tax-card-title">Service charge — Unpaid</h5>
                </div>
                <div className="tax-ops-head-right">
                  <span className="tax-ops-month-chip">{data?.paymentMonth || 'August 2026'}</span>
                  <span className="tax-ops-auto-pill">
                    <span className="tax-ops-live-dot"></span> Auto
                  </span>
                </div>
              </div>
              <div className="tax-card-body">
                <div className="tax-ops-big tax-ops-big--red">{data?.unpaidCount ?? 0}</div>
                <p className="tax-card-subhead">
                  <span>{data?.unpaidCount ?? 0}</span> companies unpaid
                </p>

                <div className="tax-ops-progress">
                  <div className="tax-ops-progress-meta">
                    <span>Still outstanding</span>
                    <strong>{data?.unpaidPct ?? 0}%</strong>
                  </div>
                  <div className="tax-ops-progress-track">
                    <span className="tax-ops-progress-fill is-red" style={{ width: `${data?.unpaidPct ?? 0}%` }}></span>
                  </div>
                  <div className="tax-ops-progress-hint">
                    One-click Pay on each row · sorted for follow-up
                  </div>
                </div>

                <div className="tax-ops-search">
                  <i className="bi bi-search tax-ops-search-icon"></i>
                  <input
                    type="search"
                    className="tax-ops-search-input"
                    placeholder="Search unpaid companies…"
                    value={unpaidSearch}
                    onChange={e => setUnpaidSearch(e.target.value)}
                  />
                </div>

                <div className="tax-scroll-list">
                  <ul className="tax-list">
                    {filteredUnpaid.map((c, i) => (
                      <li key={i}>
                        <span>
                          <span className="tax-ops-status-dot is-red"></span>
                          {c.name}
                        </span>
                        <button className="tax-badge tax-badge--warn" style={{ border: 'none', cursor: 'pointer' }}>
                          Pay
                        </button>
                      </li>
                    ))}
                    {filteredUnpaid.length === 0 && (
                      <li className="tax-empty">All active companies paid</li>
                    )}
                  </ul>
                </div>
              </div>
            </div>
          </div>

          {/* Card 3: Mushok 4.3 Amendment */}
          <div className="col-xl-4 col-md-6 mb-4">
            <div className="tax-card tax-card--ops-black">
              <div className="tax-card-header">
                <div className="tax-ops-head-left">
                  <i className="bi bi-file-earmark-diff-fill"></i>
                  <h5 className="tax-card-title">Mushok 4.3 Amendment</h5>
                </div>
                <div className="tax-ops-head-right">
                  <span className="tax-ops-month-chip">5 years</span>
                  <span className="tax-ops-auto-pill">
                    <span className="tax-ops-live-dot"></span> Auto
                  </span>
                </div>
              </div>
              <div className="tax-card-body">
                <div className="tax-ops-big tax-ops-big--black">{data?.m43AmendmentCount ?? 0}</div>
                <p className="tax-card-subhead">
                  Amendments · last 5 years · <a href="#view-amendments">View all</a>
                </p>

                <div className="tax-ops-progress">
                  <div className="tax-ops-progress-meta">
                    <span>Audit trail</span>
                    <strong>Live feed</strong>
                  </div>
                  <div className="tax-ops-progress-hint">
                    Recent amendments with company + date for quick review
                  </div>
                </div>

                <div className="tax-ops-search">
                  <i className="bi bi-search tax-ops-search-icon"></i>
                  <input
                    type="search"
                    className="tax-ops-search-input"
                    placeholder="Search amendments…"
                    value={m43Search}
                    onChange={e => setM43Search(e.target.value)}
                  />
                </div>

                <div className="tax-scroll-list">
                  <ul className="tax-list">
                    {filteredM43Amend.map((row, i) => (
                      <li key={i}>
                        <span>
                          <span className="tax-ops-status-dot is-blue"></span>
                          <strong>{row.company}</strong>
                          {row.description && <small className="d-block text-muted">{row.description}</small>}
                        </span>
                        <span className="tax-badge tax-badge--slate">{row.date || '—'}</span>
                      </li>
                    ))}
                    {filteredM43Amend.length === 0 && (
                      <li className="tax-empty">No Mushok 4.3 amendments</li>
                    )}
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Row 2: 4 Metric Cards */}
        <div className="row">
          {/* Card 4: Returns submitted */}
          <div className="col-xl-3 col-lg-6 col-md-6 mb-4">
            <div className="tax-card tax-card--ops-blue">
              <div className="tax-card-header">
                <div className="tax-ops-head-left">
                  <i className="bi bi-file-earmark-check-fill"></i>
                  <h5 className="tax-card-title">Returns submitted</h5>
                </div>
              </div>
              <div className="tax-card-body">
                <div className="tax-ops-big">{data?.returnsSubmittedCount ?? 0}</div>
                <p className="tax-card-subhead">
                  VAT Online 9.1 · <span>{data?.returnsWindowLabel}</span> · <a href="#returns-list">View all</a>
                </p>

                <div className="tax-ops-progress">
                  <div className="tax-ops-progress-meta">
                    <span>Filed this period</span>
                    <strong>{data?.returnsSubmittedPct ?? 0}%</strong>
                  </div>
                  <div className="tax-ops-progress-track">
                    <span className="tax-ops-progress-fill is-blue" style={{ width: `${data?.returnsSubmittedPct ?? 0}%` }}></span>
                  </div>
                </div>

                <div className="tax-ops-search">
                  <i className="bi bi-search tax-ops-search-icon"></i>
                  <input
                    type="search"
                    className="tax-ops-search-input"
                    placeholder="Search submitted…"
                    value={submittedSearch}
                    onChange={e => setSubmittedSearch(e.target.value)}
                  />
                </div>

                <div className="tax-scroll-list">
                  <ul className="tax-list">
                    {filteredSubmitted.map((c, i) => (
                      <li key={i}>
                        <span>
                          <span className="tax-ops-status-dot is-green"></span>
                          {c.name}
                        </span>
                        <span className="tax-badge tax-badge--online">Filed</span>
                      </li>
                    ))}
                    {filteredSubmitted.length === 0 && (
                      <li className="tax-empty">None yet this month</li>
                    )}
                  </ul>
                </div>
              </div>
            </div>
          </div>

          {/* Card 5: Returns not submitted */}
          <div className="col-xl-3 col-lg-6 col-md-6 mb-4">
            <div className="tax-card tax-card--ops-red">
              <div className="tax-card-header">
                <div className="tax-ops-head-left">
                  <i className="bi bi-exclamation-triangle-fill"></i>
                  <h5 className="tax-card-title">Returns not submitted</h5>
                </div>
              </div>
              <div className="tax-card-body">
                <div className="tax-ops-big tax-ops-big--red">{data?.returnsMissingCount ?? 0}</div>
                <p className="tax-card-subhead">
                  Active companies missing VAT Online 9.1 · <a href="#upload-return">Upload</a>
                </p>

                <div className="tax-ops-progress">
                  <div className="tax-ops-progress-meta">
                    <span>Still missing</span>
                    <strong>{data?.returnsMissingPct ?? 0}%</strong>
                  </div>
                  <div className="tax-ops-progress-track">
                    <span className="tax-ops-progress-fill is-red" style={{ width: `${data?.returnsMissingPct ?? 0}%` }}></span>
                  </div>
                </div>

                <div className="tax-ops-search">
                  <i className="bi bi-search tax-ops-search-icon"></i>
                  <input
                    type="search"
                    className="tax-ops-search-input"
                    placeholder="Search missing returns…"
                    value={missingSearch}
                    onChange={e => setMissingSearch(e.target.value)}
                  />
                </div>

                <div className="tax-scroll-list">
                  <ul className="tax-list">
                    {filteredMissing.map((c, i) => (
                      <li key={i}>
                        <span>
                          <span className="tax-ops-status-dot is-red"></span>
                          {c.name}
                        </span>
                        <span className="tax-badge tax-badge--warn">Due</span>
                      </li>
                    ))}
                    {filteredMissing.length === 0 && (
                      <li className="tax-empty">All active companies submitted</li>
                    )}
                  </ul>
                </div>
              </div>
            </div>
          </div>

          {/* Card 6: Purchases */}
          <div className="col-xl-3 col-lg-6 col-md-6 mb-4">
            <div className="tax-card tax-card--ops-green">
              <div className="tax-card-header">
                <i className="bi bi-bag-fill"></i>
                <h5 className="tax-card-title">Purchases</h5>
              </div>
              <div className="tax-card-body">
                <div className="tax-ops-big">{data?.purchaseTotal ?? 0}</div>
                <p className="tax-card-subhead">
                  Total entries · latest 25 by show date · <a href="#purchases">View all</a>
                </p>

                <div className="tax-scroll-list">
                  <ul className="tax-list">
                    {(data?.purchaseList || []).map((row, i) => (
                      <li key={i}>
                        <span>
                          <strong>{row.company}</strong>
                          <small className="d-block text-muted">
                            {row.type} {row.invoice ? `· Inv ${row.invoice}` : ''}
                          </small>
                        </span>
                        <span className="tax-badge">
                          {row.amount || '0.00'}
                          {row.date && <small className="d-block">{row.date}</small>}
                        </span>
                      </li>
                    ))}
                    {(!data?.purchaseList || data?.purchaseList.length === 0) && (
                      <li className="tax-empty">No purchases</li>
                    )}
                  </ul>
                </div>
              </div>
            </div>
          </div>

          {/* Card 7: Mushak 4.3 */}
          <div className="col-xl-3 col-lg-6 col-md-6 mb-4">
            <div className="tax-card tax-card--ops-blue">
              <div className="tax-card-header">
                <i className="bi bi-file-earmark-text-fill"></i>
                <h5 className="tax-card-title">Mushak 4.3</h5>
              </div>
              <div className="tax-card-body">
                <div className="tax-ops-big">{data?.m43Total ?? 0}</div>
                <p className="tax-card-subhead">
                  All declared forms · <a href="#m43-list">View all</a>
                </p>

                <div className="tax-scroll-list">
                  <ul className="tax-list">
                    {(data?.m43List || []).map((row, i) => (
                      <li key={i}>
                        <span>
                          <strong>{row.company}</strong>
                          <small className="d-block text-muted">
                            {row.product || '—'} {row.hs_code ? `· HS ${row.hs_code}` : ''}
                          </small>
                        </span>
                        <span className="tax-badge">{row.price || '0.00'}</span>
                      </li>
                    ))}
                    {(!data?.m43List || data?.m43List.length === 0) && (
                      <li className="tax-empty">No Mushak 4.3</li>
                    )}
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Bottom Footer Banner */}
      <div className="tax-dash-footer">
        POWERED BY: NATIONAL BOARD OF REVENUE (NBR)
      </div>
    </div>
  );
}
