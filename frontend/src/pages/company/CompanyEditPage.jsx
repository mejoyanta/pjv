import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Subheader from '../../layouts/Subheader';
import CompanyForm from '../../components/company/CompanyForm';
import { companyService } from '../../services/companyService';

export default function CompanyEditPage() {
  const { slug } = useParams();
  const navigate = useNavigate();
  const [company, setCompany] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let isMounted = true;
    companyService.getCompany(slug)
      .then((res) => {
        if (isMounted && res && res.data) {
          setCompany(res.data);
        }
      })
      .catch((err) => {
        if (isMounted) setError(err.message);
      })
      .finally(() => {
        if (isMounted) setLoading(false);
      });

    return () => { isMounted = false; };
  }, [slug]);

  if (loading) {
    return (
      <div style={{ padding: 40, textAlign: 'center', color: 'var(--kt-brand)', fontSize: 14 }}>
        <i className="fa fa-spinner fa-spin" style={{ marginRight: 8 }}></i> Loading company details...
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ padding: 30 }}>
        <div style={{ color: 'var(--kt-danger)', marginBottom: 15 }}>{error}</div>
        <button className="btn btn-secondary" onClick={() => navigate('/company')}>Back to List</button>
      </div>
    );
  }

  return (
    <div>
      <Subheader
        title={`Edit Company: ${company?.name || ''}`}
        breadcrumbs={[
          { label: 'Company', link: '/company' },
          { label: 'Edit' }
        ]}
      />
      <CompanyForm initialData={company} isEdit={true} />
    </div>
  );
}
