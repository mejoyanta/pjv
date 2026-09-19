package com.tax.vat.repository;

import com.tax.vat.entity.Musak43;
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
public interface Musak43Repository extends JpaRepository<Musak43, Long>, JpaSpecificationExecutor<Musak43> {

    Optional<Musak43> findBySlug(String slug);

    long countByDeletedAtIsNull();

    @Query(value = "SELECT m FROM Musak43 m " +
            "LEFT JOIN FETCH m.company c " +
            "LEFT JOIN FETCH m.companyBranch cb " +
            "LEFT JOIN FETCH m.product p " +
            "LEFT JOIN FETCH m.purchase pu " +
            "WHERE m.deletedAt IS NULL " +
            "AND (:companyId IS NULL OR m.companyId = :companyId) " +
            "AND (:productId IS NULL OR m.productId = :productId) " +
            "AND (:fromDate IS NULL OR m.date >= :fromDate) " +
            "AND (:toDate IS NULL OR m.date <= :toDate) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(m.productServiceDetails) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(m.hsCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))",
            countQuery = "SELECT COUNT(m) FROM Musak43 m " +
            "LEFT JOIN m.company c " +
            "WHERE m.deletedAt IS NULL " +
            "AND (:companyId IS NULL OR m.companyId = :companyId) " +
            "AND (:productId IS NULL OR m.productId = :productId) " +
            "AND (:fromDate IS NULL OR m.date >= :fromDate) " +
            "AND (:toDate IS NULL OR m.date <= :toDate) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(m.productServiceDetails) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(m.hsCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Musak43> findFiltered(
            @Param("companyId") Long companyId,
            @Param("productId") Long productId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("search") String search,
            Pageable pageable
    );
}
