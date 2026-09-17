package com.tax.vat.repository;

import com.tax.vat.entity.NocCertificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NocCertificateRepository extends JpaRepository<NocCertificate, Long>, JpaSpecificationExecutor<NocCertificate> {
    Optional<NocCertificate> findBySlug(String slug);

    @Query("SELECT n FROM NocCertificate n WHERE n.deletedAt IS NULL AND (:companyId IS NULL OR n.companyId = :companyId)")
    Page<NocCertificate> findActiveNocs(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT n FROM NocCertificate n WHERE n.deletedAt IS NULL AND (:companyId IS NULL OR n.companyId = :companyId) AND LOWER(n.nocNo) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<NocCertificate> searchActiveNocs(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);
}
