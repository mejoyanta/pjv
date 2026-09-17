package com.tax.vat.repository;

import com.tax.vat.entity.CpcItemNo;
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
public interface CpcItemNoRepository extends JpaRepository<CpcItemNo, Long>, JpaSpecificationExecutor<CpcItemNo> {
    Optional<CpcItemNo> findBySlug(String slug);

    @Query("SELECT c FROM CpcItemNo c WHERE c.deletedAt IS NULL")
    Page<CpcItemNo> findActiveCpcItems(Pageable pageable);

    @Query("SELECT c FROM CpcItemNo c WHERE c.deletedAt IS NULL AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CpcItemNo> searchActiveCpcItems(@Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM CpcItemNo c WHERE c.deletedAt IS NULL ORDER BY c.id DESC")
    List<CpcItemNo> findAllActiveCpcItems();
}
