package com.tax.vat.repository;

import com.tax.vat.entity.AdditionalPrice;
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
public interface AdditionalPriceRepository extends JpaRepository<AdditionalPrice, Long>, JpaSpecificationExecutor<AdditionalPrice> {
    Optional<AdditionalPrice> findBySlug(String slug);

    @Query("SELECT a FROM AdditionalPrice a WHERE a.deletedAt IS NULL")
    Page<AdditionalPrice> findActivePrices(Pageable pageable);

    @Query("SELECT a FROM AdditionalPrice a WHERE a.deletedAt IS NULL AND (LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<AdditionalPrice> searchActivePrices(@Param("search") String search, Pageable pageable);

    @Query("SELECT a FROM AdditionalPrice a WHERE a.deletedAt IS NULL ORDER BY a.id DESC")
    List<AdditionalPrice> findAllActivePrices();
}
