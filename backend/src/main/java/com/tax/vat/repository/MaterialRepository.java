package com.tax.vat.repository;

import com.tax.vat.entity.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long>, JpaSpecificationExecutor<Material> {
    Optional<Material> findBySlug(String slug);

    @Query("SELECT m FROM Material m WHERE m.deletedAt IS NULL AND (:companyId IS NULL OR m.companyId = :companyId)")
    Page<Material> findActiveMaterials(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT m FROM Material m WHERE m.deletedAt IS NULL AND (:companyId IS NULL OR m.companyId = :companyId) AND (LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.hsCode) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Material> searchActiveMaterials(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT m FROM Material m WHERE m.deletedAt IS NULL AND (:companyId IS NULL OR m.companyId = :companyId) ORDER BY m.id DESC")
    List<Material> findAllActiveMaterials(@Param("companyId") Long companyId);
}
