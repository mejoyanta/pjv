package com.tax.vat.repository;

import com.tax.vat.entity.CompanyCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyCategoryRepository extends JpaRepository<CompanyCategory, Long> {
    Optional<CompanyCategory> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
