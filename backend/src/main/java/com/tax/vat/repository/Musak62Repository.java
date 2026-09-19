package com.tax.vat.repository;

import com.tax.vat.entity.Musak62;
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
public interface Musak62Repository extends JpaRepository<Musak62, Long>, JpaSpecificationExecutor<Musak62> {

    Optional<Musak62> findBySlug(String slug);

    long countByDeletedAtIsNull();

    @Query(value = "SELECT m FROM Musak62 m " +
            "LEFT JOIN FETCH m.company c " +
            "LEFT JOIN FETCH m.companyBranch cb " +
            "LEFT JOIN FETCH m.product p " +
            "LEFT JOIN FETCH m.purchase pu " +
            "LEFT JOIN FETCH m.sale s " +
            "LEFT JOIN FETCH m.musak63 m63 " +
            "LEFT JOIN FETCH m63.sale m63s " +
            "WHERE m.deletedAt IS NULL " +
            "AND (:companyId IS NULL OR m.companyId = :companyId) " +
            "AND (:productId IS NULL OR m.productId = :productId) " +
            "AND (:fromDate IS NULL OR (m.saleDate >= :fromDate OR m.purchaseDate >= :fromDate)) " +
            "AND (:toDate IS NULL OR (m.saleDate <= :toDate OR m.purchaseDate <= :toDate)) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(pu.billOfEntry) LIKE LOWER(CONCAT('%', :search, '%')))",
            countQuery = "SELECT COUNT(m) FROM Musak62 m " +
            "LEFT JOIN m.company c " +
            "LEFT JOIN m.product p " +
            "LEFT JOIN m.purchase pu " +
            "WHERE m.deletedAt IS NULL " +
            "AND (:companyId IS NULL OR m.companyId = :companyId) " +
            "AND (:productId IS NULL OR m.productId = :productId) " +
            "AND (:fromDate IS NULL OR (m.saleDate >= :fromDate OR m.purchaseDate >= :fromDate)) " +
            "AND (:toDate IS NULL OR (m.saleDate <= :toDate OR m.purchaseDate <= :toDate)) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(pu.billOfEntry) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Musak62> findFiltered(
            @Param("companyId") Long companyId,
            @Param("productId") Long productId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("search") String search,
            Pageable pageable
    );
}
