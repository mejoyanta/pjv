import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import MasterLayout from './layouts/MasterLayout';

// Auth Page
import LoginPage from './pages/auth/LoginPage';

// Company Pages
import CompanyListPage from './pages/company/CompanyListPage';
import CompanyArchivePage from './pages/company/CompanyArchivePage';
import CompanyCreatePage from './pages/company/CompanyCreatePage';
import CompanyEditPage from './pages/company/CompanyEditPage';
import CompanyDocumentsPage from './pages/company/CompanyDocumentsPage';

// Company Information Child Menu Pages
import DocumentRegisterListPage from './pages/company-info/DocumentRegisterListPage';
import CompanyReportSummaryListPage from './pages/company-info/CompanyReportSummaryListPage';
import DvcListPage from './pages/company-info/DvcListPage';
import AuditReportListPage from './pages/company-info/AuditReportListPage';
import AnalyzeReportListPage from './pages/company-info/AnalyzeReportListPage';
import LegalManagementListPage from './pages/company-info/LegalManagementListPage';
import TaskManagementListPage from './pages/company-info/TaskManagementListPage';
import CompanyNocListPage from './pages/company-info/CompanyNocListPage';

// Basic Configuration Child Menu Pages
import CurrencyListPage from './pages/basic-config/CurrencyListPage';
import UnitListPage from './pages/basic-config/UnitListPage';
import PortListPage from './pages/basic-config/PortListPage';
import CpcItemNoListPage from './pages/basic-config/CpcItemNoListPage';
import DesignationDepartmentListPage from './pages/basic-config/DesignationDepartmentListPage';
import AdditionalPriceListPage from './pages/basic-config/AdditionalPriceListPage';
import MaterialListPage from './pages/basic-config/MaterialListPage';
import ProductListPage from './pages/basic-config/ProductListPage';
import BarcodeGeneratorPage from './pages/basic-config/BarcodeGeneratorPage';
import SupplierListPage from './pages/basic-config/SupplierListPage';
import PrioritySupplierListPage from './pages/basic-config/PrioritySupplierListPage';
import CustomerListPage from './pages/basic-config/CustomerListPage';
import PriorityCustomerListPage from './pages/basic-config/PriorityCustomerListPage';

// Stock Management Pages
import PurchaseListPage from './pages/stock-management/PurchaseListPage';
import PurchaseCreatePage from './pages/stock-management/PurchaseCreatePage';

// Payment Information Child Menu Pages
import PaymentListPage from './pages/payment-info/PaymentListPage';
import CreditInvoiceListPage from './pages/payment-info/CreditInvoiceListPage';
import DebitInvoiceListPage from './pages/payment-info/DebitInvoiceListPage';
import BankTreasuryListPage from './pages/payment-info/BankTreasuryListPage';
import BankTransactionsListPage from './pages/payment-info/BankTransactionsListPage';
import BankInfoDetailPage from './pages/payment-info/BankInfoDetailPage';
import BankBranchListPage from './pages/payment-info/BankBranchListPage';
import MobileBankingListPage from './pages/payment-info/MobileBankingListPage';
import RentVatListPage from './pages/payment-info/RentVatListPage';
import AdjustmentDecreaseListPage from './pages/payment-info/AdjustmentDecreaseListPage';
import PackageListPage from './pages/payment-info/PackageListPage';
import SubscriptionListPage from './pages/payment-info/SubscriptionListPage';

// User Pages
import UserListPage from './pages/user/UserListPage';
import UserArchivePage from './pages/user/UserArchivePage';
import UserCreatePage from './pages/user/UserCreatePage';
import UserEditPage from './pages/user/UserEditPage';

// Group Pages
import GroupListPage from './pages/group/GroupListPage';
import GroupArchivePage from './pages/group/GroupArchivePage';
import GroupCreatePage from './pages/group/GroupCreatePage';
import GroupEditPage from './pages/group/GroupEditPage';
import GroupAccessPage from './pages/group/GroupAccessPage';

// Dashboard Page
import DashboardOverviewPage from './pages/dashboard/DashboardOverviewPage';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public Login Route */}
          <Route path="/login" element={<LoginPage />} />

          {/* Protected Dashboard Layout */}
          <Route element={<MasterLayout />}>
            <Route path="/" element={<DashboardOverviewPage />} />
            <Route path="/dashboard/overview" element={<DashboardOverviewPage />} />

            {/* Company Routes */}
            <Route path="/company" element={<CompanyListPage />} />
            <Route path="/company/archive" element={<CompanyArchivePage />} />
            <Route path="/company/create" element={<CompanyCreatePage />} />
            <Route path="/company/:slug" element={<CompanyEditPage />} />
            <Route path="/company/:slug/documents" element={<CompanyDocumentsPage />} />

            {/* Company Information Child Menu Routes */}
            <Route path="/document-register" element={<DocumentRegisterListPage />} />
            <Route path="/company-report-summary" element={<CompanyReportSummaryListPage />} />
            <Route path="/dvc-files" element={<DvcListPage />} />
            <Route path="/audit-report" element={<AuditReportListPage />} />
            <Route path="/analyze-report" element={<AnalyzeReportListPage />} />
            <Route path="/legal-management" element={<LegalManagementListPage />} />
            <Route path="/task-management" element={<TaskManagementListPage />} />
            <Route path="/company-noc" element={<CompanyNocListPage />} />

            {/* Basic Configuration Child Menu Routes */}
            <Route path="/currency" element={<CurrencyListPage />} />
            <Route path="/unit" element={<UnitListPage />} />
            <Route path="/port" element={<PortListPage />} />
            <Route path="/cpc-item-no" element={<CpcItemNoListPage />} />
            <Route path="/designation-department" element={<DesignationDepartmentListPage />} />
            <Route path="/price-additional" element={<AdditionalPriceListPage />} />
            <Route path="/material" element={<MaterialListPage />} />
            <Route path="/product" element={<ProductListPage />} />
            <Route path="/barcode" element={<BarcodeGeneratorPage />} />
            <Route path="/supplier" element={<SupplierListPage />} />
            <Route path="/priority-supplier" element={<PrioritySupplierListPage />} />
            <Route path="/customer" element={<CustomerListPage />} />
            <Route path="/priority-customer" element={<PriorityCustomerListPage />} />

            {/* Stock Management Routes */}
            <Route path="/purchase" element={<PurchaseListPage />} />
            <Route path="/purchase/create" element={<PurchaseCreatePage />} />
            <Route path="/purchase/create/:type" element={<PurchaseCreatePage />} />
            <Route path="/purchase/:id/edit" element={<PurchaseCreatePage />} />

            {/* Payment Information Child Menu Routes */}
            <Route path="/payments" element={<PaymentListPage />} />
            <Route path="/credit-invoice" element={<CreditInvoiceListPage />} />
            <Route path="/debit-invoice" element={<DebitInvoiceListPage />} />
            <Route path="/bank-treasury" element={<BankTreasuryListPage />} />
            <Route path="/bank-transactions" element={<BankTransactionsListPage />} />
            <Route path="/bank-info-details" element={<BankInfoDetailPage />} />
            <Route path="/bank-branch" element={<BankBranchListPage />} />
            <Route path="/mobile-banking" element={<MobileBankingListPage />} />
            <Route path="/rent-vat" element={<RentVatListPage />} />
            <Route path="/adjustment-decrease" element={<AdjustmentDecreaseListPage />} />
            <Route path="/packages" element={<PackageListPage />} />
            <Route path="/subscription" element={<SubscriptionListPage />} />

            {/* User Routes */}
            <Route path="/users" element={<UserListPage />} />
            <Route path="/users/archive" element={<UserArchivePage />} />
            <Route path="/users/create" element={<UserCreatePage />} />
            <Route path="/users/:id" element={<UserEditPage />} />

            {/* Group Routes */}
            <Route path="/groups" element={<GroupListPage />} />
            <Route path="/groups/archive" element={<GroupArchivePage />} />
            <Route path="/groups/create" element={<GroupCreatePage />} />
            <Route path="/groups/:id" element={<GroupEditPage />} />
            <Route path="/groups/:slug/access" element={<GroupAccessPage />} />

            {/* Fallback */}
            <Route path="*" element={<Navigate to="/company" replace />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
