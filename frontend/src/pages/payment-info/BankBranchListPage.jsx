import React, { useState } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { bankService, bankBranchService } from '../../services/paymentInfoService';

export default function BankBranchListPage() {
  const [activeTab, setActiveTab] = useState('banks'); // 'banks' | 'branches'
  const [reloadTriggerBanks, setReloadTriggerBanks] = useState(0);
  const [reloadTriggerBranches, setReloadTriggerBranches] = useState(0);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);

  // Modal states for Banks
  const [isBankModalOpen, setIsBankModalOpen] = useState(false);
  const [editingBank, setEditingBank] = useState(null);
  const [bankFormData, setBankFormData] = useState({ name: '', routingNo: '', swiftCode: '' });
  const [bankToDelete, setBankToDelete] = useState(null);
  const [isBankDeleteOpen, setIsBankDeleteOpen] = useState(false);

  // Modal states for Branches
  const [isBranchModalOpen, setIsBranchModalOpen] = useState(false);
  const [editingBranch, setEditingBranch] = useState(null);
  const [branchFormData, setBranchFormData] = useState({ bankName: '', name: '', routingNo: '', district: '' });
  const [branchToDelete, setBranchToDelete] = useState(null);
  const [isBranchDeleteOpen, setIsBranchDeleteOpen] = useState(false);

  // Handlers for Banks
  const openCreateBank = () => {
    setEditingBank(null);
    setBankFormData({ name: '', routingNo: '', swiftCode: '' });
    setIsBankModalOpen(true);
  };

  const openEditBank = (item) => {
    setEditingBank(item);
    setBankFormData({
      name: item.name || '',
      routingNo: item.routingNo || '',
      swiftCode: item.swiftCode || ''
    });
    setIsBankModalOpen(true);
  };

  const handleSaveBank = async (e) => {
    e.preventDefault();
    try {
      if (editingBank) {
        await bankService.update(editingBank.id, bankFormData);
        setAlert({ type: 'success', message: 'Bank updated successfully!' });
      } else {
        await bankService.create(bankFormData);
        setAlert({ type: 'success', message: 'Bank created successfully!' });
      }
      setIsBankModalOpen(false);
      setReloadTriggerBanks(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    }
  };

  const confirmDeleteBank = async () => {
    if (!bankToDelete) return;
    try {
      await bankService.delete(bankToDelete.id);
      setAlert({ type: 'success', message: 'Bank deleted successfully!' });
      setReloadTriggerBanks(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setIsBankDeleteOpen(false);
      setBankToDelete(null);
    }
  };

  // Handlers for Branches
  const openCreateBranch = () => {
    setEditingBranch(null);
    setBranchFormData({ bankName: '', name: '', routingNo: '', district: '' });
    setIsBranchModalOpen(true);
  };

  const openEditBranch = (item) => {
    setEditingBranch(item);
    setBranchFormData({
      bankName: item.bankName || '',
      name: item.name || '',
      routingNo: item.routingNo || '',
      district: item.district || ''
    });
    setIsBranchModalOpen(true);
  };

  const handleSaveBranch = async (e) => {
    e.preventDefault();
    try {
      if (editingBranch) {
        await bankBranchService.update(editingBranch.id, branchFormData);
        setAlert({ type: 'success', message: 'Branch updated successfully!' });
      } else {
        await bankBranchService.create(branchFormData);
        setAlert({ type: 'success', message: 'Branch created successfully!' });
      }
      setIsBranchModalOpen(false);
      setReloadTriggerBranches(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    }
  };

  const confirmDeleteBranch = async () => {
    if (!branchToDelete) return;
    try {
      await bankBranchService.delete(branchToDelete.id);
      setAlert({ type: 'success', message: 'Branch deleted successfully!' });
      setReloadTriggerBranches(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setIsBranchDeleteOpen(false);
      setBranchToDelete(null);
    }
  };

  const handleDownloadPdf = async () => {
    setIsPdfLoading(true);
    try {
      if (activeTab === 'banks') {
        await bankService.downloadPdf();
      } else {
        await bankBranchService.downloadPdf();
      }
    } catch (err) {
      setAlert({ type: 'danger', message: 'Failed to download PDF report' });
    } finally {
      setIsPdfLoading(false);
    }
  };

  const bankColumns = [
    {
      title: '#',
      sortable: false,
      width: '60px',
      align: 'center',
      render: (row, idx) => idx
    },
    {
      title: 'Bank Name',
      data: 'name',
      sortable: true,
      render: (row) => <span style={{ fontWeight: 600, color: '#48465b' }}>{row.name}</span>
    },
    {
      title: 'Routing No',
      data: 'routingNo',
      sortable: true,
      render: (row) => <span className="font-monospace text-muted">{row.routingNo || '—'}</span>
    },
    {
      title: 'Swift Code',
      data: 'swiftCode',
      sortable: true,
      render: (row) => (
        <span className="kt-badge kt-badge--inline kt-badge--brand font-monospace" style={{ fontWeight: 600 }}>
          {row.swiftCode || '—'}
        </span>
      )
    },
    {
      title: 'Actions',
      sortable: false,
      align: 'center',
      width: '120px',
      render: (row) => (
        <div style={{ display: 'flex', gap: 6, justifyContent: 'center' }}>
          <button
            className="btn btn-sm btn-label-brand btn-circle btn-icon"
            title="Edit Bank"
            onClick={() => openEditBank(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Bank"
            onClick={() => {
              setBankToDelete(row);
              setIsBankDeleteOpen(true);
            }}
          >
            <i className="bi bi-trash"></i>
          </button>
        </div>
      )
    }
  ];

  const branchColumns = [
    {
      title: '#',
      sortable: false,
      width: '60px',
      align: 'center',
      render: (row, idx) => idx
    },
    {
      title: 'Bank Name',
      data: 'bankName',
      sortable: true,
      render: (row) => <span style={{ fontWeight: 600, color: '#48465b' }}>{row.bankName || '—'}</span>
    },
    {
      title: 'Branch Name',
      data: 'name',
      sortable: true,
      render: (row) => <span style={{ fontWeight: 500 }}>{row.name}</span>
    },
    {
      title: 'Routing No',
      data: 'routingNo',
      sortable: true,
      render: (row) => (
        <span className="kt-badge kt-badge--inline kt-badge--primary font-monospace" style={{ fontWeight: 600 }}>
          {row.routingNo || '—'}
        </span>
      )
    },
    {
      title: 'District',
      data: 'district',
      sortable: true,
      render: (row) => row.district || '—'
    },
    {
      title: 'Actions',
      sortable: false,
      align: 'center',
      width: '120px',
      render: (row) => (
        <div style={{ display: 'flex', gap: 6, justifyContent: 'center' }}>
          <button
            className="btn btn-sm btn-label-brand btn-circle btn-icon"
            title="Edit Branch"
            onClick={() => openEditBranch(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Branch"
            onClick={() => {
              setBranchToDelete(row);
              setIsBranchDeleteOpen(true);
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
        title="Bank & Branch"
        breadcrumbs={[
          { label: 'Payment Information' },
          { label: 'Bank & Branch' }
        ]}
        actions={
          <div style={{ display: 'flex', gap: 8 }}>
            <button className="btn btn-outline-danger" onClick={handleDownloadPdf} disabled={isPdfLoading}>
              <i className="bi bi-file-earmark-pdf"></i>
              <span>{isPdfLoading ? 'Exporting...' : `Export ${activeTab === 'banks' ? 'Banks' : 'Branches'} PDF`}</span>
            </button>
            <button
              className="btn btn-brand"
              onClick={activeTab === 'banks' ? openCreateBank : openCreateBranch}
            >
              <i className="bi bi-plus-lg"></i>
              <span>{activeTab === 'banks' ? 'Add Bank' : 'Add Branch'}</span>
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

      {/* Tabs Switcher */}
      <div style={{ display: 'flex', gap: 10, marginBottom: 20 }}>
        <button
          className={`btn ${activeTab === 'banks' ? 'btn-brand' : 'btn-outline-secondary'}`}
          onClick={() => setActiveTab('banks')}
          style={{ padding: '8px 24px', fontWeight: 600 }}
        >
          <i className="bi bi-bank" style={{ marginRight: 6 }}></i>
          Banks
        </button>
        <button
          className={`btn ${activeTab === 'branches' ? 'btn-brand' : 'btn-outline-secondary'}`}
          onClick={() => setActiveTab('branches')}
          style={{ padding: '8px 24px', fontWeight: 600 }}
        >
          <i className="bi bi-geo-alt" style={{ marginRight: 6 }}></i>
          Bank Branches
        </button>
      </div>

      {/* Conditional DataTable */}
      {activeTab === 'banks' ? (
        <DataTable
          title="Bank Directory"
          columns={bankColumns}
          fetchData={bankService.load}
          reloadTrigger={reloadTriggerBanks}
        />
      ) : (
        <DataTable
          title="Branch Network"
          columns={branchColumns}
          fetchData={bankBranchService.load}
          reloadTrigger={reloadTriggerBranches}
        />
      )}

      {/* Bank Modal */}
      {isBankModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 500 }}>
            <div className="modal-header">
              <h5>{editingBank ? 'Edit Bank' : 'Add New Bank'}</h5>
              <button className="btn btn-sm btn-label-secondary" onClick={() => setIsBankModalOpen(false)}>×</button>
            </div>
            <form onSubmit={handleSaveBank}>
              <div className="modal-body" style={{ padding: '20px' }}>
                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Bank Name *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    placeholder="e.g. BRAC Bank Limited"
                    value={bankFormData.name}
                    onChange={(e) => setBankFormData({ ...bankFormData, name: e.target.value })}
                  />
                </div>
                <div className="row" style={{ display: 'flex', gap: 12 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Routing No</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. 060271234"
                      value={bankFormData.routingNo}
                      onChange={(e) => setBankFormData({ ...bankFormData, routingNo: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Swift Code</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. BRAKBDDH"
                      value={bankFormData.swiftCode}
                      onChange={(e) => setBankFormData({ ...bankFormData, swiftCode: e.target.value })}
                    />
                  </div>
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setIsBankModalOpen(false)}>Cancel</button>
                <button type="submit" className="btn btn-brand">{editingBank ? 'Update Bank' : 'Save Bank'}</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Branch Modal */}
      {isBranchModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 500 }}>
            <div className="modal-header">
              <h5>{editingBranch ? 'Edit Branch' : 'Add New Branch'}</h5>
              <button className="btn btn-sm btn-label-secondary" onClick={() => setIsBranchModalOpen(false)}>×</button>
            </div>
            <form onSubmit={handleSaveBranch}>
              <div className="modal-body" style={{ padding: '20px' }}>
                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Bank Name *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    placeholder="e.g. BRAC Bank Limited"
                    value={branchFormData.bankName}
                    onChange={(e) => setBranchFormData({ ...branchFormData, bankName: e.target.value })}
                  />
                </div>
                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Branch Name *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    placeholder="e.g. Gulshan Branch"
                    value={branchFormData.name}
                    onChange={(e) => setBranchFormData({ ...branchFormData, name: e.target.value })}
                  />
                </div>
                <div className="row" style={{ display: 'flex', gap: 12 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Routing No</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. 060271234"
                      value={branchFormData.routingNo}
                      onChange={(e) => setBranchFormData({ ...branchFormData, routingNo: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>District</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. Dhaka"
                      value={branchFormData.district}
                      onChange={(e) => setBranchFormData({ ...branchFormData, district: e.target.value })}
                    />
                  </div>
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setIsBranchModalOpen(false)}>Cancel</button>
                <button type="submit" className="btn btn-brand">{editingBranch ? 'Update Branch' : 'Save Branch'}</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Modals */}
      <ConfirmModal
        isOpen={isBankDeleteOpen}
        title="Delete Bank"
        message={`Are you sure you want to delete bank "${bankToDelete?.name}"? This action cannot be undone.`}
        confirmText="Yes, Delete"
        confirmType="danger"
        onConfirm={confirmDeleteBank}
        onClose={() => {
          setIsBankDeleteOpen(false);
          setBankToDelete(null);
        }}
      />

      <ConfirmModal
        isOpen={isBranchDeleteOpen}
        title="Delete Branch"
        message={`Are you sure you want to delete branch "${branchToDelete?.name}"? This action cannot be undone.`}
        confirmText="Yes, Delete"
        confirmType="danger"
        onConfirm={confirmDeleteBranch}
        onClose={() => {
          setIsBranchDeleteOpen(false);
          setBranchToDelete(null);
        }}
      />
    </div>
  );
}
