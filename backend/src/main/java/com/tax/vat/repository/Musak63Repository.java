package com.tax.vat.repository;

import com.tax.vat.entity.Musak63;
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
public interface Musak63Repository extends JpaRepository<Musak63, Long>, JpaSpecificationExecutor<Musak63> {

    Optional<Musak63> findBySlug(String slug);

    long countByDeletedAtIsNull();

    @Query(value = "SELECT m FROM Musak63 m " +
            "LEFT JOIN FETCH m.company c " +
            "LEFT JOIN FETCH m.companyBranch cb " +
            "LEFT JOIN FETCH m.sale s " +
            "WHERE m.deletedAt IS NULL " +
            "AND (:companyId IS NULL OR m.companyId = :companyId) " +
            "AND (:fromDate IS NULL OR m.date >= :fromDate) " +
            "AND (:toDate IS NULL OR m.date <= :toDate) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(s.buyerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     CAST(m.viNo AS string) LIKE CONCAT('%', :search, '%'))",
            countQuery = "SELECT COUNT(m) FROM Musak63 m " +
            "LEFT JOIN m.company c " +
            "LEFT JOIN m.sale s " +
            "WHERE m.deletedAt IS NULL " +
            "AND (:companyId IS NULL OR m.companyId = :companyId) " +
            "AND (:fromDate IS NULL OR m.date >= :fromDate) " +
            "AND (:toDate IS NULL OR m.date <= :toDate) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(s.buyerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     CAST(m.viNo AS string) LIKE CONCAT('%', :search, '%'))")
    Page<Musak63> findFiltered(
            @Param("companyId") Long companyId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("search") String search,
            Pageable pageable
    );

    @Query(value = "SELECT m FROM Musak63 m " +
            "LEFT JOIN FETCH m.company c " +
            "LEFT JOIN FETCH m.sale s " +
            "WHERE m.deletedAt IS NULL " +
            "AND (m.amendment IS NULL OR m.amendment = false) " +
            "AND (m.isShow IS NULL OR m.isShow = false) " +
            "AND m.parent63Id IS NULL " +
            "AND (s.totalSaleAmount >= 200000) " +
            "AND (:companyId IS NULL OR m.companyId = :companyId) " +
            "AND (:fromDate IS NULL OR m.date >= :fromDate) " +
            "AND (:toDate IS NULL OR m.date <= :toDate) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(s.buyerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     CAST(m.viNo AS string) LIKE CONCAT('%', :search, '%'))",
            countQuery = "SELECT COUNT(m) FROM Musak63 m " +
            "LEFT JOIN m.company c " +
            "LEFT JOIN m.sale s " +
            "WHERE m.deletedAt IS NULL " +
            "AND (m.amendment IS NULL OR m.amendment = false) " +
            "AND (m.isShow IS NULL OR m.isShow = false) " +
            "AND m.parent63Id IS NULL " +
            "AND (s.totalSaleAmount >= 200000) " +
            "AND (:companyId IS NULL OR m.companyId = :companyId) " +
            "AND (:fromDate IS NULL OR m.date >= :fromDate) " +
            "AND (:toDate IS NULL OR m.date <= :toDate) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(s.buyerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     CAST(m.viNo AS string) LIKE CONCAT('%', :search, '%'))")
    Page<Musak63> findFiltered610(
            @Param("companyId") Long companyId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("search") String search,
            Pageable pageable
    );
}
