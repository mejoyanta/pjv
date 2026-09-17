import React from 'react';
import Subheader from '../../layouts/Subheader';
import GroupForm from '../../components/group/GroupForm';

export default function GroupCreatePage() {
  return (
    <div>
      <Subheader
        title="Create Group"
        breadcrumbs={[
          { label: 'Groups', link: '/groups' },
          { label: 'Create' }
        ]}
      />
      <GroupForm isEdit={false} />
    </div>
  );
}
