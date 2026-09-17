package com.tax.vat.repository;

import com.tax.vat.entity.Product;
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
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findBySlug(String slug);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND (:companyId IS NULL OR p.companyId = :companyId)")
    Page<Product> findActiveProducts(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND (:companyId IS NULL OR p.companyId = :companyId) AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.hsCode) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> searchActiveProducts(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND (:companyId IS NULL OR p.companyId = :companyId) ORDER BY p.id DESC")
    List<Product> findAllActiveProducts(@Param("companyId") Long companyId);
}
