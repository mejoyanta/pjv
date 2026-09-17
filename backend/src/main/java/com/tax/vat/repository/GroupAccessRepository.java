package com.tax.vat.repository;

import com.tax.vat.entity.GroupAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupAccessRepository extends JpaRepository<GroupAccess, Long> {
    Optional<GroupAccess> findByGroupId(Long groupId);
    void deleteByGroupId(Long groupId);
}
