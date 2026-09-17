import React from 'react';
import Subheader from '../../layouts/Subheader';
import UserForm from '../../components/user/UserForm';

export default function UserCreatePage() {
  return (
    <div>
      <Subheader
        title="Create New User"
        breadcrumbs={[
          { label: 'Users', link: '/users' },
          { label: 'Create' }
        ]}
      />
      <UserForm isEdit={false} />
    </div>
  );
}
