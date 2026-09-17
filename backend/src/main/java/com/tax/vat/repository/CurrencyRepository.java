package com.tax.vat.repository;

import com.tax.vat.entity.Currency;
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
public interface CurrencyRepository extends JpaRepository<Currency, Long>, JpaSpecificationExecutor<Currency> {
    Optional<Currency> findBySlug(String slug);

    @Query("SELECT c FROM Currency c WHERE c.deletedAt IS NULL")
    Page<Currency> findActiveCurrencies(Pageable pageable);

    @Query("SELECT c FROM Currency c WHERE c.deletedAt IS NULL AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.code) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Currency> searchActiveCurrencies(@Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM Currency c WHERE c.deletedAt IS NULL ORDER BY c.id DESC")
    List<Currency> findAllActiveCurrencies();
}
