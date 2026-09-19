package com.tax.vat.repository;

import com.tax.vat.entity.Sale;
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
public interface SaleRepository extends JpaRepository<Sale, Long>, JpaSpecificationExecutor<Sale> {

    Optional<Sale> findBySlug(String slug);

    long countByDeletedAtIsNull();

    @Query(value = "SELECT s FROM Sale s " +
            "LEFT JOIN FETCH s.company c " +
            "LEFT JOIN FETCH s.companyBranch cb " +
            "WHERE s.deletedAt IS NULL " +
            "AND (:companyId IS NULL OR s.companyId = :companyId) " +
            "AND (:fromDate IS NULL OR s.date >= :fromDate) " +
            "AND (:toDate IS NULL OR s.date <= :toDate) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(s.buyerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     CAST(s.id AS string) LIKE CONCAT('%', :search, '%'))",
            countQuery = "SELECT COUNT(s) FROM Sale s " +
            "LEFT JOIN s.company c " +
            "WHERE s.deletedAt IS NULL " +
            "AND (:companyId IS NULL OR s.companyId = :companyId) " +
            "AND (:fromDate IS NULL OR s.date >= :fromDate) " +
            "AND (:toDate IS NULL OR s.date <= :toDate) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(s.buyerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     CAST(s.id AS string) LIKE CONCAT('%', :search, '%'))")
    Page<Sale> findFiltered(
            @Param("companyId") Long companyId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("search") String search,
            Pageable pageable
    );
}
