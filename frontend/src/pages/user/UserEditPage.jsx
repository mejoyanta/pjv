import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Subheader from '../../layouts/Subheader';
import UserForm from '../../components/user/UserForm';
import { userService } from '../../services/userService';

export default function UserEditPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let isMounted = true;
    userService.getUser(id)
      .then((res) => {
        if (isMounted && res?.data) {
          setUser(res.data);
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
        <i className="fa fa-spinner fa-spin" style={{ marginRight: 8 }}></i> Loading user data...
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ padding: 30 }}>
        <div style={{ color: 'var(--kt-danger)', marginBottom: 15 }}>{error}</div>
        <button className="btn btn-secondary" onClick={() => navigate('/users')}>Back to Users</button>
      </div>
    );
  }

  return (
    <div>
      <Subheader
        title={`Edit User: ${user?.name || ''}`}
        breadcrumbs={[
          { label: 'Users', link: '/users' },
          { label: 'Edit' }
        ]}
      />
      <UserForm initialData={user} isEdit={true} />
    </div>
  );
}
