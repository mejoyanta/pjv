import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import CompanyViewModal from '../../components/company/CompanyViewModal';
import BranchModal from '../../components/company/BranchModal';
import { companyService } from '../../services/companyService';

export default function CompanyListPage() {
  const navigate = useNavigate();
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [alert, setAlert] = useState(null);

  // View Modal state
  const [selectedCompany, setSelectedCompany] = useState(null);
  const [isViewOpen, setIsViewOpen] = useState(false);

  // Branch Modal state
  const [selectedCompanyForBranches, setSelectedCompanyForBranches] = useState(null);
  const [isBranchOpen, setIsBranchOpen] = useState(false);

  // Delete Confirm state
  const [companyToDelete, setCompanyToDelete] = useState(null);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);

  const handleView = async (slug) => {
    try {
      const res = await companyService.getCompany(slug);
      if (res && res.data) {
        setSelectedCompany(res.data);
        setIsViewOpen(true);
      }
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    }
  };

  const confirmDelete = async () => {
    if (!companyToDelete) return;
    try {
      await companyService.deleteCompany(companyToDelete.id);
      setAlert({ type: 'success', message: 'Company moved to archive successfully!' });
      setReloadTrigger((prev) => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setIsDeleteOpen(false);
      setCompanyToDelete(null);
    }
  };

  const columns = [
    {
      title: '#',
      sortable: false,
      width: '50px',
      align: 'center',
      render: (row, idx) => idx
    },
    {
      title: 'Company Name',
      data: 'name',
      sortable: true,
      render: (row) => (
        <div>
          <span style={{ fontWeight: 600, color: '#48465b', display: 'block' }}>{row.name}</span>
          <span style={{ fontSize: 11.5, color: '#959cb6' }}>ID: {row.username}</span>
        </div>
      )
    },
    {
      title: 'BIN',
      data: 'bin',
      sortable: true,
      render: (row) => row.bin || 'N/A'
    },
    {
      title: 'Email',
      data: 'email',
      sortable: true,
      render: (row) => row.email || 'N/A'
    },
    {
      title: 'Phone',
      data: 'phone',
      sortable: true,
      render: (row) => row.phone || 'N/A'
    },
    {
      title: 'Category',
      data: 'categoryName',
      sortable: false,
      render: (row) => (
        <span className="badge badge-info">{row.categoryName || 'General'}</span>
      )
    },
    {
      title: 'Expire Date',
      data: 'subscriptionExpireDate',
      sortable: true,
      render: (row) => row.subscriptionExpireDate || '—'
    },
    {
      title: 'Status',
      data: 'status',
      sortable: true,
      align: 'center',
      render: (row) => (
        <span className={`badge ${row.status === 'ACTIVE' || row.status === 'active' ? 'badge-success' : 'badge-danger'}`}>
          {row.status}
        </span>
      )
    },
    {
      title: 'Actions',
      sortable: false,
      align: 'center',
      width: '160px',
      render: (row) => (
        <div style={{ display: 'flex', gap: 6, justifyContent: 'center' }}>
          {/* View Details */}
          <button
            className="btn btn-sm btn-label-info btn-circle btn-icon"
            title="View Details"
            onClick={() => handleView(row.slug)}
          >
            <i className="bi bi-eye"></i>
          </button>

          {/* Edit Details */}
          <button
            className="btn btn-sm btn-label-brand btn-circle btn-icon"
            title="Edit Company"
            onClick={() => navigate(`/company/${row.slug}`)}
          >
            <i className="bi bi-pencil"></i>
          </button>

          {/* Branches */}
          <button
            className="btn btn-sm btn-label-success btn-circle btn-icon"
            title="Manage Branches"
            onClick={() => {
              setSelectedCompanyForBranches(row);
              setIsBranchOpen(true);
            }}
          >
            <i className="bi bi-diagram-2"></i>
          </button>

          {/* Documents */}
          <button
            className="btn btn-sm btn-label-warning btn-circle btn-icon"
            title="Manage Documents"
            onClick={() => navigate(`/company/${row.slug}/documents`)}
          >
            <i className="bi bi-files"></i>
          </button>

          {/* Delete Action */}
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Move to Archive"
            onClick={() => {
              setCompanyToDelete(row);
              setIsDeleteOpen(true);
            }}
          >
            <i className="bi bi-trash"></i>
          </button>
        </div>
      )
    }
  ];

  return (
    <div>
      <Subheader
        title="Company List"
        breadcrumbs={[{ label: 'Company', link: '/company' }, { label: 'List' }]}
        actions={
          <div style={{ display: 'flex', gap: 10 }}>
            <button
              className="btn btn-secondary"
              onClick={() => companyService.downloadPdf('')}
            >
              <i className="bi bi-file-earmark-pdf"></i>
              <span>Download PDF</span>
            </button>
            <Link to="/company/create" className="btn btn-brand">
              <i className="bi bi-plus-lg"></i>
              <span>Create Company</span>
            </Link>
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
        title="Active Companies"
        columns={columns}
        fetchData={companyService.loadCompanies}
        reloadTrigger={reloadTrigger}
      />

      {/* View Modal */}
      <CompanyViewModal
        isOpen={isViewOpen}
        company={selectedCompany}
        onClose={() => setIsViewOpen(false)}
      />

      {/* Branch Modal */}
      <BranchModal
        isOpen={isBranchOpen}
        company={selectedCompanyForBranches}
        onClose={() => setIsBranchOpen(false)}
      />

      {/* Delete Confirmation */}
      <ConfirmModal
        isOpen={isDeleteOpen}
        title="Archive Company"
        message={`Are you sure you want to move "${companyToDelete?.name}" to archive?`}
        confirmText="Archive"
        confirmBtnClass="btn-danger"
        onConfirm={confirmDelete}
        onCancel={() => setIsDeleteOpen(false)}
      />
    </div>
  );
}
