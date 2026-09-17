package com.tax.vat.repository;

import com.tax.vat.entity.TaskManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskManagementRepository extends JpaRepository<TaskManagement, Long>, JpaSpecificationExecutor<TaskManagement> {
    Optional<TaskManagement> findBySlug(String slug);

    @Query("SELECT t FROM TaskManagement t WHERE t.deletedAt IS NULL AND (:companyId IS NULL OR t.companyId = :companyId)")
    Page<TaskManagement> findActiveTasks(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT t FROM TaskManagement t WHERE t.deletedAt IS NULL AND (:companyId IS NULL OR t.companyId = :companyId) AND (LOWER(t.workFor) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.userName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<TaskManagement> searchActiveTasks(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);
}
