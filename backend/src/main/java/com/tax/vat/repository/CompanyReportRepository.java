package com.tax.vat.repository;

import com.tax.vat.entity.CompanyReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyReportRepository extends JpaRepository<CompanyReport, Long>, JpaSpecificationExecutor<CompanyReport> {
    Optional<CompanyReport> findBySlug(String slug);

    @Query("SELECT r FROM CompanyReport r WHERE r.deletedAt IS NULL AND (:companyId IS NULL OR r.companyId = :companyId)")
    Page<CompanyReport> findActiveReports(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT r FROM CompanyReport r WHERE r.deletedAt IS NULL AND (:companyId IS NULL OR r.companyId = :companyId) AND (LOWER(r.month) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.year) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CompanyReport> searchActiveReports(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);
}
