package com.tax.vat.repository;

import com.tax.vat.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long>, JpaSpecificationExecutor<Supplier> {

    @Query("SELECT s FROM Supplier s WHERE (:companyId IS NULL OR s.companyId = :companyId)")
    Page<Supplier> findSuppliers(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT s FROM Supplier s WHERE (:companyId IS NULL OR s.companyId = :companyId) AND (LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(s.binTin) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(s.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(s.address) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Supplier> searchSuppliers(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT s FROM Supplier s WHERE (:companyId IS NULL OR s.companyId = :companyId) ORDER BY s.id DESC")
    List<Supplier> findAllByCompany(@Param("companyId") Long companyId);
}
