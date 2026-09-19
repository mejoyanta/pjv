package com.tax.vat.repository;

import com.tax.vat.entity.Musak621;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface Musak621Repository extends JpaRepository<Musak621, Long>, JpaSpecificationExecutor<Musak621> {

    Optional<Musak621> findBySlug(String slug);

    long countByDeletedAtIsNull();

    @Query(value = "SELECT m FROM Musak621 m " +
            "LEFT JOIN FETCH m.company c " +
            "LEFT JOIN FETCH m.companyBranch cb " +
            "LEFT JOIN FETCH m.product p " +
            "LEFT JOIN FETCH m.purchase pu " +
            "LEFT JOIN FETCH m.sale s " +
            "LEFT JOIN FETCH m.musak63 m63 " +
            "WHERE m.deletedAt IS NULL " +
            "AND (:companyId IS NULL OR m.companyId = :companyId) " +
            "AND (:productId IS NULL OR m.productId = :productId) " +
            "AND (:fromDate IS NULL OR (m.saleDate >= :fromDate OR m.purchaseDate >= :fromDate)) " +
            "AND (:toDate IS NULL OR (m.saleDate <= :toDate OR m.purchaseDate <= :toDate)) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(s.buyerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(pu.billOfEntry) LIKE LOWER(CONCAT('%', :search, '%')))",
            countQuery = "SELECT COUNT(m) FROM Musak621 m " +
            "LEFT JOIN m.company c " +
            "LEFT JOIN m.sale s " +
            "LEFT JOIN m.purchase pu " +
            "WHERE m.deletedAt IS NULL " +
            "AND (:companyId IS NULL OR m.companyId = :companyId) " +
            "AND (:productId IS NULL OR m.productId = :productId) " +
            "AND (:fromDate IS NULL OR (m.saleDate >= :fromDate OR m.purchaseDate >= :fromDate)) " +
            "AND (:toDate IS NULL OR (m.saleDate <= :toDate OR m.purchaseDate <= :toDate)) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(s.buyerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(pu.billOfEntry) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Musak621> findFiltered(
            @Param("companyId") Long companyId,
            @Param("productId") Long productId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("search") String search,
            Pageable pageable
    );
}
