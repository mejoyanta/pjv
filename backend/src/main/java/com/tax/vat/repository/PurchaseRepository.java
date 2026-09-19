package com.tax.vat.repository;

import com.tax.vat.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long>, JpaSpecificationExecutor<Purchase> {

    Optional<Purchase> findBySlug(String slug);

    @Query(value = "SELECT p FROM Purchase p " +
            "LEFT JOIN FETCH p.company c " +
            "LEFT JOIN FETCH p.companyBranch cb " +
            "LEFT JOIN FETCH p.product pr " +
            "LEFT JOIN FETCH p.supplier s " +
            "WHERE p.deletedAt IS NULL " +
            "AND (:companyId IS NULL OR p.companyId = :companyId) " +
            "AND (:supplierId IS NULL OR p.supplierId = :supplierId) " +
            "AND (:purchaseType IS NULL OR p.purchaseType = :purchaseType) " +
            "AND (:fromDate IS NULL OR p.purchaseShowDate >= :fromDate OR (p.purchaseShowDate IS NULL AND p.date >= :fromDate)) " +
            "AND (:toDate IS NULL OR p.purchaseShowDate <= :toDate OR (p.purchaseShowDate IS NULL AND p.date <= :toDate))",
            countQuery = "SELECT COUNT(p) FROM Purchase p WHERE p.deletedAt IS NULL " +
            "AND (:companyId IS NULL OR p.companyId = :companyId) " +
            "AND (:supplierId IS NULL OR p.supplierId = :supplierId) " +
            "AND (:purchaseType IS NULL OR p.purchaseType = :purchaseType) " +
            "AND (:fromDate IS NULL OR p.purchaseShowDate >= :fromDate OR (p.purchaseShowDate IS NULL AND p.date >= :fromDate)) " +
            "AND (:toDate IS NULL OR p.purchaseShowDate <= :toDate OR (p.purchaseShowDate IS NULL AND p.date <= :toDate))")
    Page<Purchase> findPurchases(
            @Param("companyId") Long companyId,
            @Param("supplierId") Long supplierId,
            @Param("purchaseType") String purchaseType,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    @Query(value = "SELECT p FROM Purchase p " +
            "LEFT JOIN FETCH p.company c " +
            "LEFT JOIN FETCH p.companyBranch cb " +
            "LEFT JOIN FETCH p.product pr " +
            "LEFT JOIN FETCH p.supplier s " +
            "WHERE p.deletedAt IS NULL " +
            "AND (:companyId IS NULL OR p.companyId = :companyId) " +
            "AND (:supplierId IS NULL OR p.supplierId = :supplierId) " +
            "AND (:purchaseType IS NULL OR p.purchaseType = :purchaseType) " +
            "AND (:fromDate IS NULL OR p.purchaseShowDate >= :fromDate OR (p.purchaseShowDate IS NULL AND p.date >= :fromDate)) " +
            "AND (:toDate IS NULL OR p.purchaseShowDate <= :toDate OR (p.purchaseShowDate IS NULL AND p.date <= :toDate)) " +
            "AND (" +
            "   LOWER(p.billOfEntry) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(p.seller) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(p.chassisOrDescription) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(pr.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(pr.hsCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(p.lcNo) LIKE LOWER(CONCAT('%', :search, '%')) " +
            ")",
            countQuery = "SELECT COUNT(p) FROM Purchase p " +
            "LEFT JOIN p.company c " +
            "LEFT JOIN p.product pr " +
            "LEFT JOIN p.supplier s " +
            "WHERE p.deletedAt IS NULL " +
            "AND (:companyId IS NULL OR p.companyId = :companyId) " +
            "AND (:supplierId IS NULL OR p.supplierId = :supplierId) " +
            "AND (:purchaseType IS NULL OR p.purchaseType = :purchaseType) " +
            "AND (:fromDate IS NULL OR p.purchaseShowDate >= :fromDate OR (p.purchaseShowDate IS NULL AND p.date >= :fromDate)) " +
            "AND (:toDate IS NULL OR p.purchaseShowDate <= :toDate OR (p.purchaseShowDate IS NULL AND p.date <= :toDate)) " +
            "AND (" +
            "   LOWER(p.billOfEntry) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(p.seller) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(p.chassisOrDescription) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(pr.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(pr.hsCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(p.lcNo) LIKE LOWER(CONCAT('%', :search, '%')) " +
            ")")
    Page<Purchase> searchPurchases(
            @Param("companyId") Long companyId,
            @Param("supplierId") Long supplierId,
            @Param("purchaseType") String purchaseType,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT COUNT(p) FROM Purchase p WHERE p.deletedAt IS NULL AND (:companyId IS NULL OR p.companyId = :companyId)")
    long countActive(@Param("companyId") Long companyId);

    @Query("SELECT p FROM Purchase p WHERE p.deletedAt IS NULL AND p.billOfEntry = :billOfEntry")
    List<Purchase> findByBillOfEntry(@Param("billOfEntry") String billOfEntry);
}
