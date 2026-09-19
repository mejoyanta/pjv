package com.tax.vat.repository;

import com.tax.vat.entity.Musak91;
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
public interface Musak91Repository extends JpaRepository<Musak91, Long>, JpaSpecificationExecutor<Musak91> {

    Optional<Musak91> findBySlug(String slug);

    @Query(value = "SELECT m FROM Musak91 m " +
            "LEFT JOIN FETCH m.company c " +
            "WHERE (:companyId IS NULL OR m.companyId = :companyId) " +
            "AND (:fromDate IS NULL OR m.date >= :fromDate) " +
            "AND (:toDate IS NULL OR m.date <= :toDate) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(c.bin) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(m.returnType) LIKE LOWER(CONCAT('%', :search, '%')))",
            countQuery = "SELECT COUNT(m) FROM Musak91 m " +
            "LEFT JOIN m.company c " +
            "WHERE (:companyId IS NULL OR m.companyId = :companyId) " +
            "AND (:fromDate IS NULL OR m.date >= :fromDate) " +
            "AND (:toDate IS NULL OR m.date <= :toDate) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(c.bin) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(m.returnType) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Musak91> findFiltered(
            @Param("companyId") Long companyId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("search") String search,
            Pageable pageable
    );
}
