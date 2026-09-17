package com.tax.vat.repository;

import com.tax.vat.entity.AnalyzeReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalyzeReportRepository extends JpaRepository<AnalyzeReport, Long>, JpaSpecificationExecutor<AnalyzeReport> {
    Optional<AnalyzeReport> findBySlug(String slug);

    @Query("SELECT r FROM AnalyzeReport r WHERE r.deletedAt IS NULL AND (:companyId IS NULL OR r.companyId = :companyId)")
    Page<AnalyzeReport> findActiveReports(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT r FROM AnalyzeReport r WHERE r.deletedAt IS NULL AND (:companyId IS NULL OR r.companyId = :companyId) AND (LOWER(r.refNo) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.subject) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<AnalyzeReport> searchActiveReports(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);
}
