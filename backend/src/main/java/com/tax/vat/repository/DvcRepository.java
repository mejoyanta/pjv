package com.tax.vat.repository;

import com.tax.vat.entity.Dvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DvcRepository extends JpaRepository<Dvc, Long>, JpaSpecificationExecutor<Dvc> {
    Optional<Dvc> findBySlug(String slug);

    @Query("SELECT d FROM Dvc d WHERE d.deletedAt IS NULL AND (:companyId IS NULL OR d.companyId = :companyId)")
    Page<Dvc> findActiveDvcs(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT d FROM Dvc d WHERE d.deletedAt IS NULL AND (:companyId IS NULL OR d.companyId = :companyId) AND (LOWER(d.dvcNo) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(d.year) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Dvc> searchActiveDvcs(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);
}
