package com.tax.vat.repository;

import com.tax.vat.entity.PrioritySupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrioritySupplierRepository extends JpaRepository<PrioritySupplier, Long>, JpaSpecificationExecutor<PrioritySupplier> {
    Optional<PrioritySupplier> findBySlug(String slug);

    @Query("SELECT p FROM PrioritySupplier p WHERE p.deletedAt IS NULL AND (:companyId IS NULL OR p.companyId = :companyId)")
    Page<PrioritySupplier> findActivePrioritySuppliers(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT p FROM PrioritySupplier p WHERE p.deletedAt IS NULL AND (:companyId IS NULL OR p.companyId = :companyId) AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.binTin) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.mobileNo) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<PrioritySupplier> searchActivePrioritySuppliers(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM PrioritySupplier p WHERE p.deletedAt IS NULL AND (:companyId IS NULL OR p.companyId = :companyId) ORDER BY p.id DESC")
    List<PrioritySupplier> findAllActivePrioritySuppliers(@Param("companyId") Long companyId);
}
