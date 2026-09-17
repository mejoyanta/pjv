package com.tax.vat.repository;

import com.tax.vat.entity.DocumentRegister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRegisterRepository extends JpaRepository<DocumentRegister, Long>, JpaSpecificationExecutor<DocumentRegister> {
    Optional<DocumentRegister> findBySlug(String slug);

    @Query("SELECT d FROM DocumentRegister d WHERE d.deletedAt IS NULL AND (:companyId IS NULL OR d.companyId = :companyId)")
    Page<DocumentRegister> findActiveDocuments(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT d FROM DocumentRegister d WHERE d.deletedAt IS NULL AND (:companyId IS NULL OR d.companyId = :companyId) AND (LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(d.token) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(d.ref) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<DocumentRegister> searchActiveDocuments(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);
}
