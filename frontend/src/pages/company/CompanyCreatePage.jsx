import React from 'react';
import Subheader from '../../layouts/Subheader';
import CompanyForm from '../../components/company/CompanyForm';

export default function CompanyCreatePage() {
  return (
    <div>
      <Subheader
        title="Create New Company"
        breadcrumbs={[
          { label: 'Company', link: '/company' },
          { label: 'Create' }
        ]}
      />
      <CompanyForm isEdit={false} />
    </div>
  );
}
