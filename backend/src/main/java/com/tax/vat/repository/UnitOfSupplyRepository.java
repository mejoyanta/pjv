package com.tax.vat.repository;

import com.tax.vat.entity.UnitOfSupply;
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
public interface UnitOfSupplyRepository extends JpaRepository<UnitOfSupply, Long>, JpaSpecificationExecutor<UnitOfSupply> {
    Optional<UnitOfSupply> findBySlug(String slug);

    @Query("SELECT u FROM UnitOfSupply u WHERE u.deletedAt IS NULL")
    Page<UnitOfSupply> findActiveUnits(Pageable pageable);

    @Query("SELECT u FROM UnitOfSupply u WHERE u.deletedAt IS NULL AND (LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<UnitOfSupply> searchActiveUnits(@Param("search") String search, Pageable pageable);

    @Query("SELECT u FROM UnitOfSupply u WHERE u.deletedAt IS NULL ORDER BY u.id DESC")
    List<UnitOfSupply> findAllActiveUnits();
}
