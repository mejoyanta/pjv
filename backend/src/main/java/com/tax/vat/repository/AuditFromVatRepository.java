package com.tax.vat.repository;

import com.tax.vat.entity.AuditFromVat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuditFromVatRepository extends JpaRepository<AuditFromVat, Long>, JpaSpecificationExecutor<AuditFromVat> {
    Optional<AuditFromVat> findBySlug(String slug);

    @Query("SELECT a FROM AuditFromVat a WHERE a.deletedAt IS NULL AND (:companyId IS NULL OR a.companyId = :companyId)")
    Page<AuditFromVat> findActiveAudits(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT a FROM AuditFromVat a WHERE a.deletedAt IS NULL AND (:companyId IS NULL OR a.companyId = :companyId) AND (LOWER(a.companyName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.companyBin) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.ownerName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<AuditFromVat> searchActiveAudits(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);
}
