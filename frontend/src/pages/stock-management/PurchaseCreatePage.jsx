import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import { purchaseService } from '../../services/purchaseService';

export default function PurchaseCreatePage() {
  const { type = 'foreign', id } = useParams();
  const navigate = useNavigate();
  const isEdit = Boolean(id);

  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [alert, setAlert] = useState(null);

  // Dropdown options from backend
  const [companies, setCompanies] = useState([]);
  const [branches, setBranches] = useState([]);
  const [products, setProducts] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [ports, setPorts] = useState([]);
  const [units, setUnits] = useState([]);

  // Form State
  const [formData, setFormData] = useState({
    purchaseType: type === 'local' ? 'local' : 'foreign',
    companyId: '',
    companyBranchId: '',
    supplierId: '',
    seller: '',
    sellerAddress: '',
    sellerPhone: '',
    countryRegion: '',
    portId: '',
    boeOfficeNo: '',
    cpcItemNo: '',
    billOfEntry: '',
    date: new Date().toISOString().split('T')[0],
    assessmentDate: new Date().toISOString().split('T')[0],
    purchaseShowDate: new Date().toISOString().split('T')[0],
    productId: '',
    description: '',
    quantity: 1,
    unitOfSupplyId: '',
    weight: '',
    assessable: 0,
    cd: 0,
    cdPercent: 0,
    rd: 0,
    rdPercent: 0,
    sd: 0,
    sdPercent: 0,
    basePrice: 0,
    vat: 15,
    vatAmount: 0,
    at: 0,
    atPercent: 0,
    totalAmount: 0,
    sellPrice: 0,
    isDraft: false
  });

  // Load initial form data
  useEffect(() => {
    setLoading(true);
    purchaseService.getFormData()
      .then(res => {
        if (res?.data) {
          setCompanies(res.data.companies || []);
          setBranches(res.data.branches || []);
          setProducts(res.data.products || []);
          setSuppliers(res.data.suppliers || []);
          setPorts(res.data.ports || []);
          setUnits(res.data.units || []);
        }
      })
      .catch(err => {
        console.error('Failed to load form options:', err);
        setAlert({ type: 'danger', message: 'Failed to load dropdown options.' });
      })
      .finally(() => setLoading(false));
  }, []);

  // When companyId changes, reload company-specific products and suppliers
  const handleCompanyChange = (companyId) => {
    setFormData(prev => ({
      ...prev,
      companyId,
      companyBranchId: '',
      supplierId: '',
      productId: ''
    }));

    if (companyId) {
      purchaseService.getFormData(companyId)
        .then(res => {
          if (res?.data) {
            setBranches(res.data.branches?.filter(b => String(b.companyId) === String(companyId)) || []);
            setProducts(res.data.products || []);
            setSuppliers(res.data.suppliers || []);
          }
        })
        .catch(err => console.error('Failed to filter options by company:', err));
    }
  };

  // If in Edit mode, load existing purchase record
  useEffect(() => {
    if (isEdit) {
      setLoading(true);
      purchaseService.get(id)
        .then(res => {
          const item = res.data?.data || res.data;
          if (item) {
            setFormData({
              purchaseType: item.purchaseType || 'foreign',
              companyId: item.companyId || '',
              companyBranchId: item.companyBranchId || '',
              supplierId: item.supplierId || '',
              seller: item.seller || '',
              sellerAddress: item.sellerAddress || '',
              sellerPhone: item.sellerPhone || '',
              countryRegion: item.countryRegion || '',
              portId: item.portId || '',
              boeOfficeNo: item.boeOfficeNo || '',
              cpcItemNo: item.cpcItemNo || '',
              billOfEntry: item.billOfEntry || '',
              date: item.date ? String(item.date).substring(0, 10) : '',
              assessmentDate: item.assessmentDate ? String(item.assessmentDate).substring(0, 10) : '',
              purchaseShowDate: item.purchaseShowDate ? String(item.purchaseShowDate).substring(0, 10) : '',
              productId: item.productId || '',
              description: item.description || '',
              quantity: item.quantity || 1,
              unitOfSupplyId: item.unitOfSupplyId || '',
              weight: item.weight || '',
              assessable: item.assessable || 0,
              cd: item.cd || 0,
              cdPercent: 0,
              rd: item.rd || 0,
              rdPercent: 0,
              sd: item.sd || 0,
              sdPercent: item.sd || 0,
              basePrice: item.basePrice || 0,
              vat: item.vat || 15,
              vatAmount: item.vatAmount || 0,
              at: item.at || 0,
              atPercent: 0,
              totalAmount: item.totalAmount || 0,
              sellPrice: item.sellPrice || 0,
              isDraft: Boolean(item.isDraft)
            });
          }
        })
        .catch(err => {
          console.error('Failed to load purchase item for edit:', err);
          setAlert({ type: 'danger', message: 'Failed to load purchase record details.' });
        })
        .finally(() => setLoading(false));
    }
  }, [id, isEdit]);

  // Handle Port Selection (auto-fills custom house code)
  const handlePortChange = (portId) => {
    const selected = ports.find(p => String(p.id) === String(portId));
    setFormData(prev => ({
      ...prev,
      portId,
      boeOfficeNo: selected?.code || ''
    }));
  };

  // Handle Supplier Selection (auto-fills seller details)
  const handleSupplierChange = (supplierId) => {
    const selected = suppliers.find(s => String(s.id) === String(supplierId));
    setFormData(prev => ({
      ...prev,
      supplierId,
      seller: selected?.name || prev.seller,
      sellerAddress: selected?.address || prev.sellerAddress,
      sellerPhone: selected?.phone || prev.sellerPhone
    }));
  };

  // Handle Product Selection (auto-fills description, HS code, taxes)
  const handleProductChange = (productId) => {
    const p = products.find(prod => String(prod.id) === String(productId));
    if (p) {
      const vatPct = p.vat != null ? Number(p.vat) : 15;
      const sdPct = p.sd != null ? Number(p.sd) : 0;
      const cdPct = p.cd != null ? Number(p.cd) : 0;
      const rdPct = p.rd != null ? Number(p.rd) : 0;
      const atPct = p.at != null ? Number(p.at) : 0;

      setFormData(prev => {
        const next = {
          ...prev,
          productId,
          description: p.name || prev.description,
          vat: vatPct,
          sdPercent: sdPct,
          cdPercent: cdPct,
          rdPercent: rdPct,
          atPercent: atPct
        };
        return recalculate(next);
      });
    } else {
      setFormData(prev => ({ ...prev, productId }));
    }
  };

  // Calculate taxes, base price, and total amount
  const recalculate = (curr) => {
    const isForeign = curr.purchaseType === 'foreign';
    const assessable = Number(curr.assessable) || 0;
    const qty = Number(curr.quantity) || 1;

    let cdAmount = curr.cd;
    let rdAmount = curr.rd;
    let sdAmount = curr.sd;
    let basePrice = Number(curr.basePrice) || 0;

    if (isForeign) {
      const cdPct = Number(curr.cdPercent) || 0;
      const rdPct = Number(curr.rdPercent) || 0;
      const sdPct = Number(curr.sdPercent) || 0;

      cdAmount = cdPct > 0 ? (assessable * cdPct) / 100 : Number(curr.cd) || 0;
      rdAmount = rdPct > 0 ? (assessable * rdPct) / 100 : Number(curr.rd) || 0;
      sdAmount = sdPct > 0 ? ((assessable + cdAmount + rdAmount) * sdPct) / 100 : Number(curr.sd) || 0;

      basePrice = assessable + cdAmount + rdAmount + sdAmount;
    }

    const vatPct = Number(curr.vat) || 0;
    const vatAmount = (basePrice * vatPct) / 100;

    const atPct = Number(curr.atPercent) || 0;
    const atAmount = atPct > 0 ? (basePrice * atPct) / 100 : Number(curr.at) || 0;

    const totalAmount = basePrice + vatAmount + atAmount;

    return {
      ...curr,
      cd: Number(cdAmount.toFixed(2)),
      rd: Number(rdAmount.toFixed(2)),
      sd: Number(sdAmount.toFixed(2)),
      basePrice: Number(basePrice.toFixed(2)),
      vatAmount: Number(vatAmount.toFixed(2)),
      at: Number(atAmount.toFixed(2)),
      totalAmount: Number(totalAmount.toFixed(2))
    };
  };

  const handleInputChange = (field, value) => {
    setFormData(prev => {
      const updated = { ...prev, [field]: value };
      if (['assessable', 'quantity', 'basePrice', 'vat', 'sdPercent', 'cdPercent', 'rdPercent', 'atPercent', 'cd', 'rd', 'sd', 'at'].includes(field)) {
        return recalculate(updated);
      }
      return updated;
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.companyId) {
      setAlert({ type: 'danger', message: 'Please select a company.' });
      return;
    }
    if (!formData.billOfEntry?.trim()) {
      setAlert({ type: 'danger', message: 'Please enter BOE / Invoice No.' });
      return;
    }
    if (!formData.productId) {
      setAlert({ type: 'danger', message: 'Please select a product.' });
      return;
    }

    setSubmitting(true);
    setAlert(null);

    try {
      if (isEdit) {
        await purchaseService.update(id, formData);
        setAlert({ type: 'success', message: 'Purchase record updated successfully!' });
      } else {
        await purchaseService.create(formData);
        setAlert({ type: 'success', message: 'Purchase record created successfully!' });
      }
      setTimeout(() => navigate('/purchase'), 1000);
    } catch (err) {
      console.error('Error saving purchase:', err);
      setAlert({ type: 'danger', message: err.message || 'Failed to save purchase record.' });
    } finally {
      setSubmitting(false);
    }
  };

  const isForeign = formData.purchaseType === 'foreign';

  return (
    <div className="musak-page-shell">
      {/* Subheader */}
      <Subheader
        title={isEdit ? 'Edit Purchase' : isForeign ? 'Import Purchase' : 'Local Purchase'}
        breadcrumbs={[
          { label: 'Stock Management' },
          { label: 'Product Purchase' },
          { label: isEdit ? 'Edit' : 'Create' }
        ]}
        actions={
          <Link to="/purchase" className="btn btn-outline-secondary">
            <i className="bi bi-arrow-left me-1"></i>
            <span>Back to List</span>
          </Link>
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

      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-brand" role="status"></div>
          <div className="mt-2 text-muted">Loading purchase details...</div>
        </div>
      ) : (
        <form onSubmit={handleSubmit}>
          {/* 1. Header & General Information */}
          <div className="kt-portlet mb-4">
            <div className="kt-portlet__head">
              <div className="kt-portlet__head-label">
                <span className="kt-portlet__head-icon text-brand">
                  <i className="bi bi-file-earmark-text"></i>
                </span>
                <h3 className="kt-portlet__head-title" style={{ fontWeight: 600 }}>
                  General Information
                </h3>
              </div>
              <div className="kt-portlet__head-toolbar">
                <span className="badge bg-brand fs-7 text-uppercase px-3 py-2">
                  {formData.purchaseType === 'foreign' ? 'Import Purchase' : 'Local Purchase'}
                </span>
              </div>
            </div>

            <div className="kt-portlet__body">
              <div className="row g-3">
                {/* Purchase Type */}
                <div className="col-12 col-md-3">
                  <label className="form-label fw-bold small">Purchase Type</label>
                  <select
                    className="form-select form-select-sm"
                    value={formData.purchaseType}
                    onChange={(e) => handleInputChange('purchaseType', e.target.value)}
                  >
                    <option value="foreign">Import (Foreign)</option>
                    <option value="local">Local</option>
                  </select>
                </div>

                {/* Select Company */}
                <div className="col-12 col-md-3">
                  <label className="form-label fw-bold small">Select Company <span className="text-danger">*</span></label>
                  <select
                    className="form-select form-select-sm"
                    value={formData.companyId}
                    onChange={(e) => handleCompanyChange(e.target.value)}
                    required
                  >
                    <option value="">Select Company</option>
                    {companies.map(c => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>

                {/* Company Branch */}
                <div className="col-12 col-md-3">
                  <label className="form-label fw-bold small">Company Branch</label>
                  <select
                    className="form-select form-select-sm"
                    value={formData.companyBranchId}
                    onChange={(e) => handleInputChange('companyBranchId', e.target.value)}
                  >
                    <option value="">Select Branch</option>
                    {branches.map(b => (
                      <option key={b.id} value={b.id}>{b.name}</option>
                    ))}
                  </select>
                </div>

                {/* Select Supplier */}
                <div className="col-12 col-md-3">
                  <label className="form-label fw-bold small">Select Supplier</label>
                  <select
                    className="form-select form-select-sm"
                    value={formData.supplierId}
                    onChange={(e) => handleSupplierChange(e.target.value)}
                  >
                    <option value="">Select Supplier</option>
                    {suppliers.map(s => (
                      <option key={s.id} value={s.id}>{s.name}</option>
                    ))}
                  </select>
                </div>

                {/* Supplier Name */}
                <div className="col-12 col-md-3">
                  <label className="form-label fw-bold small">Supplier Name</label>
                  <input
                    type="text"
                    className="form-control form-control-sm"
                    placeholder="Supplier / Seller Name"
                    value={formData.seller}
                    onChange={(e) => handleInputChange('seller', e.target.value)}
                  />
                </div>

                {/* Supplier Address */}
                <div className="col-12 col-md-3">
                  <label className="form-label fw-bold small">Supplier Address</label>
                  <input
                    type="text"
                    className="form-control form-control-sm"
                    placeholder="Supplier Address"
                    value={formData.sellerAddress}
                    onChange={(e) => handleInputChange('sellerAddress', e.target.value)}
                  />
                </div>

                {/* Country / Region */}
                <div className="col-12 col-md-3">
                  <label className="form-label fw-bold small">Country / Region</label>
                  <input
                    type="text"
                    className="form-control form-control-sm"
                    placeholder="Ex: Japan, China..."
                    value={formData.countryRegion}
                    onChange={(e) => handleInputChange('countryRegion', e.target.value)}
                  />
                </div>

                {/* Custom House / Port (Foreign) */}
                {isForeign && (
                  <div className="col-12 col-md-3">
                    <label className="form-label fw-bold small">Custom House / Port</label>
                    <select
                      className="form-select form-select-sm"
                      value={formData.portId}
                      onChange={(e) => handlePortChange(e.target.value)}
                    >
                      <option value="">Select Custom House</option>
                      {ports.map(p => (
                        <option key={p.id} value={p.id}>{p.name} ({p.code})</option>
                      ))}
                    </select>
                  </div>
                )}

                {/* Custom House Code (Foreign) */}
                {isForeign && (
                  <div className="col-12 col-md-3">
                    <label className="form-label fw-bold small">Custom House Code</label>
                    <input
                      type="text"
                      className="form-control form-control-sm bg-light"
                      placeholder="Auto-filled"
                      value={formData.boeOfficeNo}
                      readOnly
                    />
                  </div>
                )}

                {/* BOE / Invoice No */}
                <div className="col-12 col-md-3">
                  <label className="form-label fw-bold small">
                    {isForeign ? 'BOE No' : 'Invoice / Challan No'} <span className="text-danger">*</span>
                  </label>
                  <input
                    type="text"
                    className="form-control form-control-sm"
                    placeholder={isForeign ? 'Ex: C 12859' : 'Ex: INV-2026-001'}
                    value={formData.billOfEntry}
                    onChange={(e) => handleInputChange('billOfEntry', e.target.value)}
                    required
                  />
                </div>

                {/* BOE / Invoice Date */}
                <div className="col-12 col-md-3">
                  <label className="form-label fw-bold small">
                    {isForeign ? 'BOE Date' : 'Invoice Date'} <span className="text-danger">*</span>
                  </label>
                  <input
                    type="date"
                    className="form-control form-control-sm"
                    value={formData.date}
                    onChange={(e) => handleInputChange('date', e.target.value)}
                    required
                  />
                </div>

                {/* Purchase Show Date */}
                <div className="col-12 col-md-3">
                  <label className="form-label fw-bold small">Purchase Show Date</label>
                  <input
                    type="date"
                    className="form-control form-control-sm"
                    value={formData.purchaseShowDate}
                    onChange={(e) => handleInputChange('purchaseShowDate', e.target.value)}
                  />
                </div>
              </div>
            </div>
          </div>

          {/* 2. Product & Item Details */}
          <div className="kt-portlet mb-4">
            <div className="kt-portlet__head">
              <div className="kt-portlet__head-label">
                <span className="kt-portlet__head-icon text-brand">
                  <i className="bi bi-box-seam"></i>
                </span>
                <h3 className="kt-portlet__head-title" style={{ fontWeight: 600 }}>
                  Product & Pricing Details
                </h3>
              </div>
            </div>

            <div className="kt-portlet__body">
              <div className="row g-3">
                {/* Select Product */}
                <div className="col-12 col-md-4">
                  <label className="form-label fw-bold small">Select Product <span className="text-danger">*</span></label>
                  <select
                    className="form-select form-select-sm"
                    value={formData.productId}
                    onChange={(e) => handleProductChange(e.target.value)}
                    required
                  >
                    <option value="">Select Product / Service</option>
                    {products.map(p => (
                      <option key={p.id} value={p.id}>
                        {p.hsCode ? `[${p.hsCode}] ` : ''}{p.name}
                      </option>
                    ))}
                  </select>
                </div>

                {/* Description */}
                <div className="col-12 col-md-8">
                  <label className="form-label fw-bold small">Product Description / Notes</label>
                  <input
                    type="text"
                    className="form-control form-control-sm"
                    placeholder="Product / Service description"
                    value={formData.description}
                    onChange={(e) => handleInputChange('description', e.target.value)}
                  />
                </div>

                {/* Quantity */}
                <div className="col-12 col-md-2">
                  <label className="form-label fw-bold small">Quantity <span className="text-danger">*</span></label>
                  <input
                    type="number"
                    step="any"
                    min="0.01"
                    className="form-control form-control-sm"
                    value={formData.quantity}
                    onChange={(e) => handleInputChange('quantity', e.target.value)}
                    required
                  />
                </div>

                {/* Unit */}
                <div className="col-12 col-md-2">
                  <label className="form-label fw-bold small">Unit</label>
                  <select
                    className="form-select form-select-sm"
                    value={formData.unitOfSupplyId}
                    onChange={(e) => handleInputChange('unitOfSupplyId', e.target.value)}
                  >
                    <option value="">Select Unit</option>
                    {units.map(u => (
                      <option key={u.id} value={u.id}>{u.name}</option>
                    ))}
                  </select>
                </div>

                {/* Assessable Amount (Foreign) */}
                {isForeign && (
                  <div className="col-12 col-md-4">
                    <label className="form-label fw-bold small">Assessable Amount (BDT) <span className="text-danger">*</span></label>
                    <input
                      type="number"
                      step="any"
                      min="0"
                      className="form-control form-control-sm"
                      value={formData.assessable}
                      onChange={(e) => handleInputChange('assessable', e.target.value)}
                    />
                  </div>
                )}

                {/* CD % and Amount (Foreign) */}
                {isForeign && (
                  <>
                    <div className="col-12 col-md-2">
                      <label className="form-label fw-bold small">CD (%)</label>
                      <input
                        type="number"
                        step="any"
                        min="0"
                        className="form-control form-control-sm"
                        value={formData.cdPercent}
                        onChange={(e) => handleInputChange('cdPercent', e.target.value)}
                      />
                    </div>
                    <div className="col-12 col-md-2">
                      <label className="form-label fw-bold small">CD Amount</label>
                      <input
                        type="number"
                        step="any"
                        min="0"
                        className="form-control form-control-sm bg-light"
                        value={formData.cd}
                        readOnly
                      />
                    </div>
                  </>
                )}

                {/* RD % and Amount (Foreign) */}
                {isForeign && (
                  <>
                    <div className="col-12 col-md-2">
                      <label className="form-label fw-bold small">RD (%)</label>
                      <input
                        type="number"
                        step="any"
                        min="0"
                        className="form-control form-control-sm"
                        value={formData.rdPercent}
                        onChange={(e) => handleInputChange('rdPercent', e.target.value)}
                      />
                    </div>
                    <div className="col-12 col-md-2">
                      <label className="form-label fw-bold small">RD Amount</label>
                      <input
                        type="number"
                        step="any"
                        min="0"
                        className="form-control form-control-sm bg-light"
                        value={formData.rd}
                        readOnly
                      />
                    </div>
                  </>
                )}

                {/* SD % and Amount */}
                <div className="col-12 col-md-2">
                  <label className="form-label fw-bold small">SD (%)</label>
                  <input
                    type="number"
                    step="any"
                    min="0"
                    className="form-control form-control-sm"
                    value={formData.sdPercent}
                    onChange={(e) => handleInputChange('sdPercent', e.target.value)}
                  />
                </div>
                <div className="col-12 col-md-2">
                  <label className="form-label fw-bold small">SD Amount</label>
                  <input
                    type="number"
                    step="any"
                    min="0"
                    className="form-control form-control-sm bg-light"
                    value={formData.sd}
                    readOnly
                  />
                </div>

                {/* Base Value / Price */}
                <div className="col-12 col-md-4">
                  <label className="form-label fw-bold small">Base Value (BDT) <span className="text-danger">*</span></label>
                  <input
                    type="number"
                    step="any"
                    min="0"
                    className={`form-control form-control-sm ${isForeign ? 'bg-light' : ''}`}
                    value={formData.basePrice}
                    onChange={(e) => handleInputChange('basePrice', e.target.value)}
                    readOnly={isForeign}
                    required
                  />
                </div>

                {/* VAT % and Amount */}
                <div className="col-12 col-md-2">
                  <label className="form-label fw-bold small">VAT (%)</label>
                  <input
                    type="number"
                    step="any"
                    min="0"
                    className="form-control form-control-sm"
                    value={formData.vat}
                    onChange={(e) => handleInputChange('vat', e.target.value)}
                  />
                </div>
                <div className="col-12 col-md-2">
                  <label className="form-label fw-bold small">VAT Amount</label>
                  <input
                    type="number"
                    step="any"
                    min="0"
                    className="form-control form-control-sm bg-light"
                    value={formData.vatAmount}
                    readOnly
                  />
                </div>

                {/* AT % and Amount */}
                <div className="col-12 col-md-2">
                  <label className="form-label fw-bold small">AT (%)</label>
                  <input
                    type="number"
                    step="any"
                    min="0"
                    className="form-control form-control-sm"
                    value={formData.atPercent}
                    onChange={(e) => handleInputChange('atPercent', e.target.value)}
                  />
                </div>
                <div className="col-12 col-md-2">
                  <label className="form-label fw-bold small">AT Amount</label>
                  <input
                    type="number"
                    step="any"
                    min="0"
                    className="form-control form-control-sm bg-light"
                    value={formData.at}
                    readOnly
                  />
                </div>

                {/* Total Grand Amount */}
                <div className="col-12 col-md-4">
                  <label className="form-label fw-bold small text-success">Total Amount (BDT)</label>
                  <input
                    type="number"
                    step="any"
                    className="form-control form-control-sm bg-light fw-bold text-success font-monospace"
                    value={formData.totalAmount}
                    readOnly
                  />
                </div>

                {/* Sell Price */}
                <div className="col-12 col-md-4">
                  <label className="form-label fw-bold small">Target Sell Price (BDT)</label>
                  <input
                    type="number"
                    step="any"
                    min="0"
                    className="form-control form-control-sm"
                    value={formData.sellPrice}
                    onChange={(e) => handleInputChange('sellPrice', e.target.value)}
                  />
                </div>
              </div>
            </div>

            {/* Footer Buttons */}
            <div className="kt-portlet__foot d-flex justify-content-end gap-2 p-3 border-top">
              <Link to="/purchase" className="btn btn-secondary">
                Cancel
              </Link>
              <button
                type="submit"
                className="btn btn-brand"
                disabled={submitting}
              >
                {submitting ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-1" role="status"></span>
                    Saving...
                  </>
                ) : (
                  <>
                    <i className="bi bi-check-lg me-1"></i>
                    {isEdit ? 'Update Purchase' : 'Save Purchase'}
                  </>
                )}
              </button>
            </div>
          </div>
        </form>
      )}
    </div>
  );
}
