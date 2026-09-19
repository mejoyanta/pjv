package com.tax.vat.repository;

import com.tax.vat.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    @Query("SELECT pc FROM ProductCategory pc WHERE pc.deletedAt IS NULL AND (:companyId IS NULL OR pc.companyId IS NULL OR pc.companyId = :companyId) ORDER BY pc.name ASC")
    List<ProductCategory> findActiveCategories(@Param("companyId") Long companyId);
}
