import React, { useState } from 'react';
import DataTable from '../../components/common/DataTable';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import ConfirmModal from '../../components/common/ConfirmModal';
import { mobileBankingAccountService, mobileBankingTransactionService } from '../../services/paymentInfoService';

export default function MobileBankingListPage() {
  const [activeTab, setActiveTab] = useState('accounts'); // 'accounts' | 'transactions'
  const [reloadTriggerAccs, setReloadTriggerAccs] = useState(0);
  const [reloadTriggerTxns, setReloadTriggerTxns] = useState(0);
  const [alert, setAlert] = useState(null);
  const [isPdfLoading, setIsPdfLoading] = useState(false);

  // Modal states for Accounts
  const [isAccModalOpen, setIsAccModalOpen] = useState(false);
  const [editingAcc, setEditingAcc] = useState(null);
  const [accFormData, setAccFormData] = useState({ accountType: 'bKash', accountNumber: '', accountHolderName: '' });
  const [accToDelete, setAccToDelete] = useState(null);
  const [isAccDeleteOpen, setIsAccDeleteOpen] = useState(false);

  // Modal states for Transactions
  const [isTxnModalOpen, setIsTxnModalOpen] = useState(false);
  const [editingTxn, setEditingTxn] = useState(null);
  const [txnFormData, setTxnFormData] = useState({
    transactionType: 'Send Money',
    transactionDate: '',
    amount: '',
    transactionNo: '',
    note: ''
  });
  const [txnToDelete, setTxnToDelete] = useState(null);
  const [isTxnDeleteOpen, setIsTxnDeleteOpen] = useState(false);

  // Handlers for Accounts
  const openCreateAcc = () => {
    setEditingAcc(null);
    setAccFormData({ accountType: 'bKash', accountNumber: '', accountHolderName: '' });
    setIsAccModalOpen(true);
  };

  const openEditAcc = (item) => {
    setEditingAcc(item);
    setAccFormData({
      accountType: item.accountType || 'bKash',
      accountNumber: item.accountNumber || '',
      accountHolderName: item.accountHolderName || ''
    });
    setIsAccModalOpen(true);
  };

  const handleSaveAcc = async (e) => {
    e.preventDefault();
    try {
      if (editingAcc) {
        await mobileBankingAccountService.update(editingAcc.id, accFormData);
        setAlert({ type: 'success', message: 'Mobile account updated successfully!' });
      } else {
        await mobileBankingAccountService.create(accFormData);
        setAlert({ type: 'success', message: 'Mobile account created successfully!' });
      }
      setIsAccModalOpen(false);
      setReloadTriggerAccs(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    }
  };

  const confirmDeleteAcc = async () => {
    if (!accToDelete) return;
    try {
      await mobileBankingAccountService.delete(accToDelete.id);
      setAlert({ type: 'success', message: 'Mobile account deleted successfully!' });
      setReloadTriggerAccs(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setIsAccDeleteOpen(false);
      setAccToDelete(null);
    }
  };

  // Handlers for Transactions
  const openCreateTxn = () => {
    setEditingTxn(null);
    setTxnFormData({
      transactionType: 'Payment',
      transactionDate: new Date().toISOString().split('T')[0],
      amount: '',
      transactionNo: '',
      note: ''
    });
    setIsTxnModalOpen(true);
  };

  const openEditTxn = (item) => {
    setEditingTxn(item);
    setTxnFormData({
      transactionType: item.transactionType || '',
      transactionDate: item.transactionDate ? String(item.transactionDate).substring(0, 10) : '',
      amount: item.amount || '',
      transactionNo: item.transactionNo || '',
      note: item.note || ''
    });
    setIsTxnModalOpen(true);
  };

  const handleSaveTxn = async (e) => {
    e.preventDefault();
    try {
      if (editingTxn) {
        await mobileBankingTransactionService.update(editingTxn.id, txnFormData);
        setAlert({ type: 'success', message: 'Transaction updated successfully!' });
      } else {
        await mobileBankingTransactionService.create(txnFormData);
        setAlert({ type: 'success', message: 'Transaction recorded successfully!' });
      }
      setIsTxnModalOpen(false);
      setReloadTriggerTxns(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    }
  };

  const confirmDeleteTxn = async () => {
    if (!txnToDelete) return;
    try {
      await mobileBankingTransactionService.delete(txnToDelete.id);
      setAlert({ type: 'success', message: 'Transaction deleted successfully!' });
      setReloadTriggerTxns(prev => prev + 1);
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setIsTxnDeleteOpen(false);
      setTxnToDelete(null);
    }
  };

  const handleDownloadPdf = async () => {
    setIsPdfLoading(true);
    try {
      if (activeTab === 'accounts') {
        await mobileBankingAccountService.downloadPdf();
      } else {
        await mobileBankingTransactionService.downloadPdf();
      }
    } catch (err) {
      setAlert({ type: 'danger', message: 'Failed to download PDF report' });
    } finally {
      setIsPdfLoading(false);
    }
  };

  const accColumns = [
    {
      title: '#',
      sortable: false,
      width: '60px',
      align: 'center',
      render: (row, idx) => idx
    },
    {
      title: 'MFS Provider',
      data: 'accountType',
      sortable: true,
      render: (row) => {
        const type = (row.accountType || '').toLowerCase();
        let badgeClass = 'kt-badge--brand';
        if (type.includes('bkash')) badgeClass = 'kt-badge--danger';
        else if (type.includes('nagad')) badgeClass = 'kt-badge--warning';
        else if (type.includes('rocket')) badgeClass = 'kt-badge--primary';
        return (
          <span className={`kt-badge kt-badge--inline ${badgeClass}`} style={{ fontWeight: 600 }}>
            {row.accountType || 'MFS'}
          </span>
        );
      }
    },
    {
      title: 'Account Number',
      data: 'accountNumber',
      sortable: true,
      render: (row) => <span className="font-monospace" style={{ fontWeight: 600 }}>{row.accountNumber || '—'}</span>
    },
    {
      title: 'Account Holder',
      data: 'accountHolderName',
      sortable: true,
      render: (row) => <span style={{ fontWeight: 500, color: '#48465b' }}>{row.accountHolderName || '—'}</span>
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
            title="Edit Account"
            onClick={() => openEditAcc(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Account"
            onClick={() => {
              setAccToDelete(row);
              setIsAccDeleteOpen(true);
            }}
          >
            <i className="bi bi-trash"></i>
          </button>
        </div>
      )
    }
  ];

  const txnColumns = [
    {
      title: '#',
      sortable: false,
      width: '60px',
      align: 'center',
      render: (row, idx) => idx
    },
    {
      title: 'Trx ID / No',
      data: 'transactionNo',
      sortable: true,
      render: (row) => (
        <span className="kt-badge kt-badge--inline kt-badge--dark font-monospace" style={{ fontWeight: 600 }}>
          {row.transactionNo || '—'}
        </span>
      )
    },
    {
      title: 'Transaction Type',
      data: 'transactionType',
      sortable: true,
      render: (row) => (
        <span className="kt-badge kt-badge--inline kt-badge--primary" style={{ fontWeight: 600 }}>
          {row.transactionType || 'Transfer'}
        </span>
      )
    },
    {
      title: 'Date',
      data: 'transactionDate',
      sortable: true,
      render: (row) => row.transactionDate ? String(row.transactionDate).substring(0, 10) : '—'
    },
    {
      title: 'Amount (BDT)',
      data: 'amount',
      sortable: true,
      align: 'right',
      render: (row) => (
        <span style={{ fontWeight: 700, color: '#0abb87' }}>
          {Number(row.amount || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
        </span>
      )
    },
    {
      title: 'Note',
      data: 'note',
      sortable: false,
      render: (row) => <span className="text-truncate" style={{ maxWidth: 200, display: 'inline-block' }}>{row.note || '—'}</span>
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
            title="Edit Transaction"
            onClick={() => openEditTxn(row)}
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            className="btn btn-sm btn-label-danger btn-circle btn-icon"
            title="Delete Transaction"
            onClick={() => {
              setTxnToDelete(row);
              setIsTxnDeleteOpen(true);
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
        title="Mobile Banking"
        breadcrumbs={[
          { label: 'Payment Information' },
          { label: 'Mobile Banking' }
        ]}
        actions={
          <div style={{ display: 'flex', gap: 8 }}>
            <button className="btn btn-outline-danger" onClick={handleDownloadPdf} disabled={isPdfLoading}>
              <i className="bi bi-file-earmark-pdf"></i>
              <span>{isPdfLoading ? 'Exporting...' : `Export ${activeTab === 'accounts' ? 'Accounts' : 'Transactions'} PDF`}</span>
            </button>
            <button
              className="btn btn-brand"
              onClick={activeTab === 'accounts' ? openCreateAcc : openCreateTxn}
            >
              <i className="bi bi-plus-lg"></i>
              <span>{activeTab === 'accounts' ? 'Add Account' : 'Add Transaction'}</span>
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
          className={`btn ${activeTab === 'accounts' ? 'btn-brand' : 'btn-outline-secondary'}`}
          onClick={() => setActiveTab('accounts')}
          style={{ padding: '8px 24px', fontWeight: 600 }}
        >
          <i className="bi bi-phone" style={{ marginRight: 6 }}></i>
          Mobile Accounts
        </button>
        <button
          className={`btn ${activeTab === 'transactions' ? 'btn-brand' : 'btn-outline-secondary'}`}
          onClick={() => setActiveTab('transactions')}
          style={{ padding: '8px 24px', fontWeight: 600 }}
        >
          <i className="bi bi-arrow-left-right" style={{ marginRight: 6 }}></i>
          Mobile Transactions
        </button>
      </div>

      {activeTab === 'accounts' ? (
        <DataTable
          title="Mobile Banking Accounts"
          columns={accColumns}
          fetchData={mobileBankingAccountService.load}
          reloadTrigger={reloadTriggerAccs}
        />
      ) : (
        <DataTable
          title="Mobile Banking Transactions"
          columns={txnColumns}
          fetchData={mobileBankingTransactionService.load}
          reloadTrigger={reloadTriggerTxns}
        />
      )}

      {/* Account Modal */}
      {isAccModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 480 }}>
            <div className="modal-header">
              <h5>{editingAcc ? 'Edit Mobile Account' : 'Add Mobile Account'}</h5>
              <button className="btn btn-sm btn-label-secondary" onClick={() => setIsAccModalOpen(false)}>×</button>
            </div>
            <form onSubmit={handleSaveAcc}>
              <div className="modal-body" style={{ padding: '20px' }}>
                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>MFS Provider *</label>
                  <select
                    className="form-control"
                    value={accFormData.accountType}
                    onChange={(e) => setAccFormData({ ...accFormData, accountType: e.target.value })}
                  >
                    <option value="bKash">bKash</option>
                    <option value="Nagad">Nagad</option>
                    <option value="Rocket">Rocket</option>
                    <option value="Upay">Upay</option>
                    <option value="Other">Other</option>
                  </select>
                </div>
                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Account Number *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    placeholder="e.g. 01700000000"
                    value={accFormData.accountNumber}
                    onChange={(e) => setAccFormData({ ...accFormData, accountNumber: e.target.value })}
                  />
                </div>
                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Account Holder Name</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="e.g. Company Finance"
                    value={accFormData.accountHolderName}
                    onChange={(e) => setAccFormData({ ...accFormData, accountHolderName: e.target.value })}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setIsAccModalOpen(false)}>Cancel</button>
                <button type="submit" className="btn btn-brand">{editingAcc ? 'Update Account' : 'Save Account'}</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Transaction Modal */}
      {isTxnModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: 500 }}>
            <div className="modal-header">
              <h5>{editingTxn ? 'Edit Mobile Transaction' : 'Record Mobile Transaction'}</h5>
              <button className="btn btn-sm btn-label-secondary" onClick={() => setIsTxnModalOpen(false)}>×</button>
            </div>
            <form onSubmit={handleSaveTxn}>
              <div className="modal-body" style={{ padding: '20px' }}>
                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Trx ID / No *</label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="e.g. 9J82KL09"
                      value={txnFormData.transactionNo}
                      onChange={(e) => setTxnFormData({ ...txnFormData, transactionNo: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Transaction Type</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. Payment, Cash Out"
                      value={txnFormData.transactionType}
                      onChange={(e) => setTxnFormData({ ...txnFormData, transactionType: e.target.value })}
                    />
                  </div>
                </div>
                <div className="row" style={{ display: 'flex', gap: 12, marginBottom: 15 }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Date</label>
                    <input
                      type="date"
                      className="form-control"
                      value={txnFormData.transactionDate}
                      onChange={(e) => setTxnFormData({ ...txnFormData, transactionDate: e.target.value })}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Amount (BDT) *</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-control"
                      required
                      placeholder="0.00"
                      value={txnFormData.amount}
                      onChange={(e) => setTxnFormData({ ...txnFormData, amount: e.target.value })}
                    />
                  </div>
                </div>
                <div className="form-group" style={{ marginBottom: 15 }}>
                  <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Note / Reference</label>
                  <textarea
                    className="form-control"
                    rows="2"
                    placeholder="Reference, beneficiary details..."
                    value={txnFormData.note}
                    onChange={(e) => setTxnFormData({ ...txnFormData, note: e.target.value })}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setIsTxnModalOpen(false)}>Cancel</button>
                <button type="submit" className="btn btn-brand">{editingTxn ? 'Update Transaction' : 'Save Transaction'}</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Modals */}
      <ConfirmModal
        isOpen={isAccDeleteOpen}
        title="Delete Mobile Account"
        message={`Are you sure you want to delete mobile account "${accToDelete?.accountNumber}"? This action cannot be undone.`}
        confirmText="Yes, Delete"
        confirmType="danger"
        onConfirm={confirmDeleteAcc}
        onClose={() => {
          setIsAccDeleteOpen(false);
          setAccToDelete(null);
        }}
      />

      <ConfirmModal
        isOpen={isTxnDeleteOpen}
        title="Delete Mobile Transaction"
        message={`Are you sure you want to delete mobile transaction "${txnToDelete?.transactionNo}"? This action cannot be undone.`}
        confirmText="Yes, Delete"
        confirmType="danger"
        onConfirm={confirmDeleteTxn}
        onClose={() => {
          setIsTxnDeleteOpen(false);
          setTxnToDelete(null);
        }}
      />
    </div>
  );
}
