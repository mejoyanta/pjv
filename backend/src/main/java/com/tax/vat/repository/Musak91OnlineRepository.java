package com.tax.vat.repository;

import com.tax.vat.entity.Musak91Online;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Musak91OnlineRepository extends JpaRepository<Musak91Online, Long>, JpaSpecificationExecutor<Musak91Online> {

    Optional<Musak91Online> findBySlug(String slug);

    long countByDeletedAtIsNull();

    @Query(value = "SELECT m FROM Musak91Online m " +
            "LEFT JOIN FETCH m.company c " +
            "LEFT JOIN FETCH m.companyBranch cb " +
            "WHERE m.deletedAt IS NULL " +
            "AND (:companyId IS NULL OR m.companyId = :companyId) " +
            "AND (:month IS NULL OR :month = '' OR m.month = :month) " +
            "AND (:year IS NULL OR :year = '' OR m.year = :year) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(m.companyBin) LIKE LOWER(CONCAT('%', :search, '%')))",
            countQuery = "SELECT COUNT(m) FROM Musak91Online m " +
            "LEFT JOIN m.company c " +
            "WHERE m.deletedAt IS NULL " +
            "AND (:companyId IS NULL OR m.companyId = :companyId) " +
            "AND (:month IS NULL OR :month = '' OR m.month = :month) " +
            "AND (:year IS NULL OR :year = '' OR m.year = :year) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(m.companyBin) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Musak91Online> findFiltered(
            @Param("companyId") Long companyId,
            @Param("month") String month,
            @Param("year") String year,
            @Param("search") String search,
            Pageable pageable
    );
}
