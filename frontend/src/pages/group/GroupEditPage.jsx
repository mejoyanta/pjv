import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Subheader from '../../layouts/Subheader';
import GroupForm from '../../components/group/GroupForm';
import { groupService } from '../../services/groupService';

export default function GroupEditPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [group, setGroup] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let isMounted = true;
    groupService.getGroup(id)
      .then((res) => {
        if (isMounted && res?.data) {
          setGroup(res.data);
        }
      })
      .catch((err) => {
        if (isMounted) setError(err.message);
      })
      .finally(() => {
        if (isMounted) setLoading(false);
      });

    return () => { isMounted = false; };
  }, [id]);

  if (loading) {
    return (
      <div style={{ padding: 40, textAlign: 'center', color: 'var(--kt-brand)' }}>
        <i className="fa fa-spinner fa-spin" style={{ marginRight: 8 }}></i> Loading group...
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ padding: 30 }}>
        <div style={{ color: 'var(--kt-danger)', marginBottom: 15 }}>{error}</div>
        <button className="btn btn-secondary" onClick={() => navigate('/groups')}>Back to Groups</button>
      </div>
    );
  }

  return (
    <div>
      <Subheader
        title={`Edit Group: ${group?.name || ''}`}
        breadcrumbs={[
          { label: 'Groups', link: '/groups' },
          { label: 'Edit' }
        ]}
      />
      <GroupForm initialData={group} isEdit={true} />
    </div>
  );
}
