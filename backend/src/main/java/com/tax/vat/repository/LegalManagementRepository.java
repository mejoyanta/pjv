package com.tax.vat.repository;

import com.tax.vat.entity.LegalManagementCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LegalManagementRepository extends JpaRepository<LegalManagementCase, Long>, JpaSpecificationExecutor<LegalManagementCase> {
    Optional<LegalManagementCase> findBySlug(String slug);

    @Query("SELECT c FROM LegalManagementCase c WHERE c.deletedAt IS NULL AND (:companyId IS NULL OR c.companyId = :companyId)")
    Page<LegalManagementCase> findActiveCases(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT c FROM LegalManagementCase c WHERE c.deletedAt IS NULL AND (:companyId IS NULL OR c.companyId = :companyId) AND (LOWER(c.caseNo) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.clientName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.courtName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<LegalManagementCase> searchActiveCases(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);
}
