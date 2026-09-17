import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { companyService } from '../../services/companyService';
import CategoryAddModal from './CategoryAddModal';

export default function CompanyForm({ initialData = null, isEdit = false }) {
  const navigate = useNavigate();
  const [categories, setCategories] = useState([]);
  const [isCategoryModalOpen, setIsCategoryModalOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  // Form State
  const [formData, setFormData] = useState({
    name: '',
    nameOfEntry: '',
    categoryId: '',
    companyLevel: 'SMALL',
    email: '',
    username: '',
    password: '',
    bin: '',
    tin: '',
    phone: '',
    altPhone: '',
    trustCode: '',
    effectiveDate: '',
    subscriptionExpireDate: '',
    status: 'ACTIVE',

    // Contact Person
    contactPersonName: '',
    contactPersonPhone: '',
    contactPersonDesignation: '',

    // Owner
    ownerName: '',
    ownerPhone: '',
    ownerAddress: '',
    ownershipType: '',
    ownerFatherName: '',
    ownerMotherName: '',
    ownerDob: '',
    ownerAge: '',

    // Emergency Contact
    emergencyContactName: '',
    emergencyContactNid: '',
    emergencyContactPassport: '',
    emergencyContactPhone: '',
    emergencyContactRelation: '',

    // Addresses
    address: '',
    presentAddress: '',
    permanentAddress: '',
    vatOfficeAddress: '',
    divisionAddress: '',
    circleAddress: '',

    // Charges
    serviceCharge: '',
    rentVatCharge: '',
    consultancyCharge: '',

    // Colors
    padHeadingColor: '#5d78ff',
    debitInvoiceHeadingColor: '#34495e',
    creditInvoiceHeadingColor: '#e74c3c',
    commercialInvoiceHeadingColor: '#2ecc71',
    proformaInvoiceHeadingColor: '#9b59b6',
    billOfLadingInvoiceHeadingColor: '#1abc9c',
    packageListHeadingColor: '#f39c12',
    purchaseOrderHeadingColor: '#2980b9',

    // Flags
    isSales: false,
    isNewDashboard: false,
    companyIsGold: false,
    companyIsTobacco: false,
    allow63PriceType: false,
    allowRevenueShare: false,
    bkashPayBillEnabled: false,
    bkashPayBillReferenceId: '',
    stockUpdateDate: '',
    notes: '',

    // Edit Reason
    usernameChangeReason: ''
  });

  // File states
  const [logoFile, setLogoFile] = useState(null);
  const [companySealFile, setCompanySealFile] = useState(null);
  const [companyPadFile, setCompanyPadFile] = useState(null);

  useEffect(() => {
    loadCategories();
  }, []);

  useEffect(() => {
    if (initialData) {
      setFormData((prev) => ({
        ...prev,
        ...initialData,
        categoryId: initialData.categoryId || '',
        status: initialData.status || 'ACTIVE',
        password: '' // Don't pre-fill password for security
      }));
    }
  }, [initialData]);

  const loadCategories = async () => {
    try {
      const res = await companyService.getCategories();
      if (res && res.data) {
        setCategories(res.data);
      }
    } catch (err) {
      console.error('Error loading categories:', err);
    }
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleCategoryCreated = (newCat) => {
    setCategories((prev) => [...prev, newCat]);
    setFormData((prev) => ({ ...prev, categoryId: newCat.id }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);

    const payload = new FormData();
    Object.keys(formData).forEach((key) => {
      if (formData[key] !== null && formData[key] !== undefined) {
        payload.append(key, formData[key]);
      }
    });

    if (logoFile) payload.append('logoFile', logoFile);
    if (companySealFile) payload.append('companySealFile', companySealFile);
    if (companyPadFile) payload.append('companyPadFile', companyPadFile);

    try {
      if (isEdit) {
        await companyService.updateCompany(initialData.slug, payload);
      } else {
        await companyService.createCompany(payload);
      }
      navigate('/company');
    } catch (err) {
      setError(err.message);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <form onSubmit={handleSubmit}>
        {error && (
          <div style={{
            padding: '14px 20px', backgroundColor: 'rgba(253, 57, 122, 0.1)',
            borderLeft: '4px solid var(--kt-danger)', color: '#c9164e',
            borderRadius: 4, marginBottom: 20, fontSize: 13, fontWeight: 500
          }}>
            <i className="bi bi-exclamation-octagon-fill" style={{ marginRight: 8 }}></i>
            {error}
          </div>
        )}

        {/* Section 1: Company General Information */}
        <div className="kt-portlet">
          <div className="kt-portlet__head">
            <div className="kt-portlet__head-title">
              <i className="bi bi-building" style={{ color: 'var(--kt-brand)' }}></i>
              <span>1. Company General Information</span>
            </div>
          </div>
          <div className="kt-portlet__body">
            <div className="row">
              <div className="col-md-4 form-group">
                <label className="form-label">Company Name *</label>
                <input
                  type="text"
                  name="name"
                  className="form-control"
                  required
                  placeholder="e.g. Acme Corporation"
                  value={formData.name}
                  onChange={handleChange}
                />
              </div>

              <div className="col-md-4 form-group">
                <label className="form-label">Name of Entry</label>
                <input
                  type="text"
                  name="nameOfEntry"
                  className="form-control"
                  placeholder="Name of entry..."
                  value={formData.nameOfEntry}
                  onChange={handleChange}
                />
              </div>

              <div className="col-md-4 form-group">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <label className="form-label">Category *</label>
                  <button
                    type="button"
                    onClick={() => setIsCategoryModalOpen(true)}
                    style={{ background: 'none', border: 'none', color: 'var(--kt-brand)', fontSize: 11.5, cursor: 'pointer', fontWeight: 600 }}
                  >
                    + Add Category
                  </button>
                </div>
                <select
                  name="categoryId"
                  className="form-control"
                  required
                  value={formData.categoryId}
                  onChange={handleChange}
                >
                  <option value="">Select Category...</option>
                  {categories.map((c) => (
                    <option key={c.id} value={c.id}>{c.name}</option>
                  ))}
                </select>
              </div>

              <div className="col-md-4 form-group">
                <label className="form-label">Company Level</label>
                <select
                  name="companyLevel"
                  className="form-control"
                  value={formData.companyLevel}
                  onChange={handleChange}
                >
                  <option value="SMALL">Small</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="LARGE">Large</option>
                </select>
              </div>

              <div className="col-md-4 form-group">
                <label className="form-label">Email *</label>
                <input
                  type="email"
                  name="email"
                  className="form-control"
                  required
                  placeholder="company@domain.com"
                  value={formData.email}
                  onChange={handleChange}
                />
              </div>

              <div className="col-md-4 form-group">
                <label className="form-label">Company ID / Username * (Prefix: BARA49)</label>
                <input
                  type="text"
                  name="username"
                  className="form-control"
                  required
                  placeholder="e.g. BARA49-DHAKA"
                  value={formData.username}
                  onChange={handleChange}
                />
                <span className="form-text">Must start with BARA49 (e.g. BARA49-CITY or BARA49CITY)</span>
              </div>

              {isEdit && initialData?.username !== formData.username && (
                <div className="col-12 form-group" style={{ backgroundColor: '#fff9e6', padding: 15, borderRadius: 4, border: '1px solid #ffeeba' }}>
                  <label className="form-label" style={{ color: '#856404' }}>Reason for changing Company ID *</label>
                  <input
                    type="text"
                    name="usernameChangeReason"
                    className="form-control"
                    required
                    placeholder="Enter reason for changing the company username/ID..."
                    value={formData.usernameChangeReason}
                    onChange={handleChange}
                  />
                </div>
              )}

              {!isEdit && (
                <div className="col-md-4 form-group">
                  <label className="form-label">Password</label>
                  <input
                    type="password"
                    name="password"
                    className="form-control"
                    placeholder="Company account password"
                    value={formData.password}
                    onChange={handleChange}
                  />
                </div>
              )}

              <div className="col-md-4 form-group">
                <label className="form-label">BIN * (Business Identification Number)</label>
                <input
                  type="text"
                  name="bin"
                  className="form-control"
                  required
                  placeholder="e.g. 000123456-0101"
                  value={formData.bin}
                  onChange={handleChange}
                />
              </div>

              <div className="col-md-4 form-group">
                <label className="form-label">TIN</label>
                <input
                  type="text"
                  name="tin"
                  className="form-control"
                  placeholder="Tax Identification Number"
                  value={formData.tin}
                  onChange={handleChange}
                />
              </div>

              <div className="col-md-4 form-group">
                <label className="form-label">Phone</label>
                <input
                  type="text"
                  name="phone"
                  className="form-control"
                  placeholder="Phone number"
                  value={formData.phone}
                  onChange={handleChange}
                />
              </div>

              <div className="col-md-4 form-group">
                <label className="form-label">Alternate Phone</label>
                <input
                  type="text"
                  name="altPhone"
                  className="form-control"
                  placeholder="Alternate phone"
                  value={formData.altPhone}
                  onChange={handleChange}
                />
              </div>

              <div className="col-md-4 form-group">
                <label className="form-label">Trust Code</label>
                <input
                  type="text"
                  name="trustCode"
                  className="form-control"
                  placeholder="Trust Code"
                  value={formData.trustCode}
                  onChange={handleChange}
                />
              </div>

              <div className="col-md-4 form-group">
                <label className="form-label">Effective Date</label>
                <input
                  type="date"
                  name="effectiveDate"
                  className="form-control"
                  value={formData.effectiveDate || ''}
                  onChange={handleChange}
                />
              </div>

              <div className="col-md-4 form-group">
                <label className="form-label">Subscription Expire Date</label>
                <input
                  type="date"
                  name="subscriptionExpireDate"
                  className="form-control"
                  value={formData.subscriptionExpireDate || ''}
                  onChange={handleChange}
                />
              </div>

              <div className="col-md-4 form-group">
                <label className="form-label">Status</label>
                <select
                  name="status"
                  className="form-control"
                  value={formData.status}
                  onChange={handleChange}
                >
                  <option value="ACTIVE">Active</option>
                  <option value="INACTIVE">Inactive</option>
                </select>
              </div>
            </div>
          </div>
        </div>

        {/* Section 2: Contact Person */}
        <div className="kt-portlet">
          <div className="kt-portlet__head">
            <div className="kt-portlet__head-title">
              <i className="bi bi-person-badge" style={{ color: 'var(--kt-brand)' }}></i>
              <span>2. Contact Person Details</span>
            </div>
          </div>
          <div className="kt-portlet__body">
            <div className="row">
              <div className="col-md-4 form-group">
                <label className="form-label">Contact Person Name</label>
                <input
                  type="text"
                  name="contactPersonName"
                  className="form-control"
                  placeholder="Full name"
                  value={formData.contactPersonName}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-4 form-group">
                <label className="form-label">Contact Person Phone</label>
                <input
                  type="text"
                  name="contactPersonPhone"
                  className="form-control"
                  placeholder="Phone"
                  value={formData.contactPersonPhone}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-4 form-group">
                <label className="form-label">Designation</label>
                <input
                  type="text"
                  name="contactPersonDesignation"
                  className="form-control"
                  placeholder="e.g. Director, Manager"
                  value={formData.contactPersonDesignation}
                  onChange={handleChange}
                />
              </div>
            </div>
          </div>
        </div>

        {/* Section 3: Owner Details */}
        <div className="kt-portlet">
          <div className="kt-portlet__head">
            <div className="kt-portlet__head-title">
              <i className="bi bi-person" style={{ color: 'var(--kt-brand)' }}></i>
              <span>3. Owner Information</span>
            </div>
          </div>
          <div className="kt-portlet__body">
            <div className="row">
              <div className="col-md-4 form-group">
                <label className="form-label">Owner Name</label>
                <input
                  type="text"
                  name="ownerName"
                  className="form-control"
                  placeholder="Owner full name"
                  value={formData.ownerName}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-4 form-group">
                <label className="form-label">Owner Phone</label>
                <input
                  type="text"
                  name="ownerPhone"
                  className="form-control"
                  placeholder="Owner phone"
                  value={formData.ownerPhone}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-4 form-group">
                <label className="form-label">Ownership Type</label>
                <input
                  type="text"
                  name="ownershipType"
                  className="form-control"
                  placeholder="e.g. Proprietorship, Limited"
                  value={formData.ownershipType}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-4 form-group">
                <label className="form-label">Father's Name</label>
                <input
                  type="text"
                  name="ownerFatherName"
                  className="form-control"
                  value={formData.ownerFatherName}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-4 form-group">
                <label className="form-label">Mother's Name</label>
                <input
                  type="text"
                  name="ownerMotherName"
                  className="form-control"
                  value={formData.ownerMotherName}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-4 form-group">
                <label className="form-label">Owner Date of Birth</label>
                <input
                  type="date"
                  name="ownerDob"
                  className="form-control"
                  value={formData.ownerDob || ''}
                  onChange={handleChange}
                />
              </div>
              <div className="col-12 form-group">
                <label className="form-label">Owner Address</label>
                <textarea
                  name="ownerAddress"
                  className="form-control"
                  rows="2"
                  value={formData.ownerAddress}
                  onChange={handleChange}
                />
              </div>
            </div>
          </div>
        </div>

        {/* Section 4: Emergency Contact */}
        <div className="kt-portlet">
          <div className="kt-portlet__head">
            <div className="kt-portlet__head-title">
              <i className="bi bi-telephone-plus" style={{ color: 'var(--kt-brand)' }}></i>
              <span>4. Emergency Contact</span>
            </div>
          </div>
          <div className="kt-portlet__body">
            <div className="row">
              <div className="col-md-4 form-group">
                <label className="form-label">Emergency Contact Name</label>
                <input
                  type="text"
                  name="emergencyContactName"
                  className="form-control"
                  value={formData.emergencyContactName}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-4 form-group">
                <label className="form-label">Phone</label>
                <input
                  type="text"
                  name="emergencyContactPhone"
                  className="form-control"
                  value={formData.emergencyContactPhone}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-4 form-group">
                <label className="form-label">Relation</label>
                <input
                  type="text"
                  name="emergencyContactRelation"
                  className="form-control"
                  placeholder="e.g. Brother, Partner"
                  value={formData.emergencyContactRelation}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-6 form-group">
                <label className="form-label">NID</label>
                <input
                  type="text"
                  name="emergencyContactNid"
                  className="form-control"
                  value={formData.emergencyContactNid}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-6 form-group">
                <label className="form-label">Passport</label>
                <input
                  type="text"
                  name="emergencyContactPassport"
                  className="form-control"
                  value={formData.emergencyContactPassport}
                  onChange={handleChange}
                />
              </div>
            </div>
          </div>
        </div>

        {/* Section 5: Addresses */}
        <div className="kt-portlet">
          <div className="kt-portlet__head">
            <div className="kt-portlet__head-title">
              <i className="bi bi-geo-alt" style={{ color: 'var(--kt-brand)' }}></i>
              <span>5. Company & VAT Addresses</span>
            </div>
          </div>
          <div className="kt-portlet__body">
            <div className="row">
              <div className="col-md-6 form-group">
                <label className="form-label">Company Address</label>
                <textarea
                  name="address"
                  className="form-control"
                  rows="2"
                  value={formData.address}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-6 form-group">
                <label className="form-label">VAT Office Address</label>
                <textarea
                  name="vatOfficeAddress"
                  className="form-control"
                  rows="2"
                  value={formData.vatOfficeAddress}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-6 form-group">
                <label className="form-label">Division Address</label>
                <input
                  type="text"
                  name="divisionAddress"
                  className="form-control"
                  value={formData.divisionAddress}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-6 form-group">
                <label className="form-label">Circle Address</label>
                <input
                  type="text"
                  name="circleAddress"
                  className="form-control"
                  value={formData.circleAddress}
                  onChange={handleChange}
                />
              </div>
            </div>
          </div>
        </div>

        {/* Section 6: Charges */}
        <div className="kt-portlet">
          <div className="kt-portlet__head">
            <div className="kt-portlet__head-title">
              <i className="bi bi-cash-stack" style={{ color: 'var(--kt-brand)' }}></i>
              <span>6. Charges</span>
            </div>
          </div>
          <div className="kt-portlet__body">
            <div className="row">
              <div className="col-md-4 form-group">
                <label className="form-label">Service Charge (৳)</label>
                <input
                  type="number"
                  step="0.01"
                  name="serviceCharge"
                  className="form-control"
                  value={formData.serviceCharge}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-4 form-group">
                <label className="form-label">Rent VAT Charge (৳)</label>
                <input
                  type="number"
                  step="0.01"
                  name="rentVatCharge"
                  className="form-control"
                  value={formData.rentVatCharge}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-4 form-group">
                <label className="form-label">Consultancy Charge (৳)</label>
                <input
                  type="number"
                  step="0.01"
                  name="consultancyCharge"
                  className="form-control"
                  value={formData.consultancyCharge}
                  onChange={handleChange}
                />
              </div>
            </div>
          </div>
        </div>

        {/* Section 7: Heading Colors */}
        <div className="kt-portlet">
          <div className="kt-portlet__head">
            <div className="kt-portlet__head-title">
              <i className="bi bi-palette" style={{ color: 'var(--kt-brand)' }}></i>
              <span>7. Invoice & Pad Heading Colors</span>
            </div>
          </div>
          <div className="kt-portlet__body">
            <div className="row">
              <div className="col-md-3 form-group">
                <label className="form-label">Pad Heading Color</label>
                <input
                  type="color"
                  name="padHeadingColor"
                  className="form-control"
                  style={{ height: 42, padding: 4 }}
                  value={formData.padHeadingColor}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-3 form-group">
                <label className="form-label">Debit Invoice Color</label>
                <input
                  type="color"
                  name="debitInvoiceHeadingColor"
                  className="form-control"
                  style={{ height: 42, padding: 4 }}
                  value={formData.debitInvoiceHeadingColor}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-3 form-group">
                <label className="form-label">Credit Invoice Color</label>
                <input
                  type="color"
                  name="creditInvoiceHeadingColor"
                  className="form-control"
                  style={{ height: 42, padding: 4 }}
                  value={formData.creditInvoiceHeadingColor}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-3 form-group">
                <label className="form-label">Commercial Invoice</label>
                <input
                  type="color"
                  name="commercialInvoiceHeadingColor"
                  className="form-control"
                  style={{ height: 42, padding: 4 }}
                  value={formData.commercialInvoiceHeadingColor}
                  onChange={handleChange}
                />
              </div>
            </div>
          </div>
        </div>

        {/* Section 8: File Attachments */}
        <div className="kt-portlet">
          <div className="kt-portlet__head">
            <div className="kt-portlet__head-title">
              <i className="bi bi-cloud-upload" style={{ color: 'var(--kt-brand)' }}></i>
              <span>8. Logo, Seal & Pad Upload</span>
            </div>
          </div>
          <div className="kt-portlet__body">
            <div className="row">
              <div className="col-md-4 form-group">
                <label className="form-label">Company Logo</label>
                <input
                  type="file"
                  className="form-control"
                  accept="image/*"
                  onChange={(e) => setLogoFile(e.target.files[0])}
                />
                {initialData?.logo && (
                  <span className="form-text" style={{ color: 'var(--kt-brand)' }}>Existing logo attached</span>
                )}
              </div>
              <div className="col-md-4 form-group">
                <label className="form-label">Company Seal</label>
                <input
                  type="file"
                  className="form-control"
                  accept="image/*"
                  onChange={(e) => setCompanySealFile(e.target.files[0])}
                />
                {initialData?.companySeal && (
                  <span className="form-text" style={{ color: 'var(--kt-brand)' }}>Existing seal attached</span>
                )}
              </div>
              <div className="col-md-4 form-group">
                <label className="form-label">Company Pad Attachment</label>
                <input
                  type="file"
                  className="form-control"
                  accept="image/*,application/pdf"
                  onChange={(e) => setCompanyPadFile(e.target.files[0])}
                />
                {initialData?.companyPad && (
                  <span className="form-text" style={{ color: 'var(--kt-brand)' }}>Existing pad attached</span>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* Bottom Actions */}
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12, marginBottom: 40 }}>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => navigate('/company')}
            disabled={submitting}
          >
            Cancel
          </button>
          <button type="submit" className="btn btn-brand" disabled={submitting}>
            {submitting ? 'Saving...' : isEdit ? 'Update Company' : 'Save Company'}
          </button>
        </div>
      </form>

      {/* Category Modal */}
      <CategoryAddModal
        isOpen={isCategoryModalOpen}
        onClose={() => setIsCategoryModalOpen(false)}
        onCategoryCreated={handleCategoryCreated}
      />
    </div>
  );
}
